package com.crypto4a.usage_example;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.crypto4a.spore_client_java.SporeClient;


public class UsageExample {

	/**
	 * 
	 * @param args
	 *            args[0](String) -- Spore server adress
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String serverAddress = args[0];
		SporeClient sporeClient = new SporeClient(serverAddress);

//		System.out.println("Performing requests: ");
//		performRequests(sporeClient);

		System.out.println("Performing use cases: ");
		useCases(sporeClient);

	}

	public static void performRequests(SporeClient sporeClient) throws IOException, JSONException {
		JSONObject response = sporeClient.doInfoRequest();
		System.out.println("getInfo request:");
		System.out.println("\tname: " + response.get("name"));
		System.out.println("\tentropySize: " + response.get("entropySize"));
		System.out.println(response.toString(4));
		System.out.println();

		response = sporeClient.doEntropyRequest("AwesomeChallenge");
		System.out.println("getEntropy request:");
		System.out.println("\tJWT: " + response.get("JWT"));
		System.out.println("\tentorpy: " + response.get("entropy"));
		System.out.println("\tchallenge: " + response.get("challenge"));
		System.out.println("\ttimestamp: " + response.get("timestamp"));
		System.out.println(response.toString(4));
		System.out.println();

		response = sporeClient.doCertificateChainRequest();
		System.out.println("getCertChain request:");
		System.out.println("\tcertificateChain: \n" + response.get("certificateChain"));
		System.out.println("\tJWT: " + response.get("JWT"));
		System.out.println(response.toString(4));
		System.out.println();
	}

	public static void useCases(SporeClient sporeClient) throws Exception {
		
		// First, we perform a getInfo request. This servers two purposes: it allows us to test the
		// connection and provides us with the size in bytes of the randomness that will be sent.
		JSONObject infoResponse = sporeClient.doInfoRequest();
		int entropySize = infoResponse.getInt("entropySize");
		System.out.println("getInfo request successful");
		System.out.println("entropySize: " + String.valueOf(entropySize));
		
		// Now that the we know the server is reachable, we can perform a getEntropy request.
		// We will generate a random challenge for this.
		SecureRandom rbg = new SecureRandom();
		byte[] randomBytes = new byte[16];
		rbg.nextBytes(randomBytes);
		String challenge = Base64.getUrlEncoder().encodeToString(randomBytes);
		JSONObject entropyResponse = sporeClient.doEntropyRequest(challenge);
		String b64entropy = entropyResponse.getString("entropy");
		System.out.println("getEntropy request successful");
		
		
		// We now have good quality entropy. We will use it to seed our RBG. We will mix it with
		// local entropy first. Indeed, if the received entropy was compromised, by mixing it with
		// our local entropy, we end up with the same amount of entropy. However, if we were to
		// simply seed our RNG directly with the received entropy, our RNG would be compromised.
		// Here we will simply XOR the two byte arrays. However, a more complicated and robust hash
		// algorithm could be used.
		byte[] entropy = Base64.getUrlDecoder().decode(b64entropy);
		randomBytes = new byte[entropySize];
		rbg.nextBytes(randomBytes);
		for (int i = 0; i < entropySize; i++) {
			entropy[i] ^= randomBytes[i];
		}
		rbg.setSeed(entropy);
		System.out.println("RBG successfully seeded");
		
		// So far we did not bother to verify the freshness or the authenticity of the received
		// entropy. This is fine since in the worst case scenario (the received entropy is 
		// compromised), we are left with the same level of security as before. This could be the 
		// approach taken by an IoT device with extremely limited resources. However, some
		// applications will required a guaranty that their level of entropy is now 
		// cryptographically secure, and thus need to perform some freshness and/or authenticity
		// verifications. Let's start by validating that the response was indeed for our request.
		String receivedChallenge = entropyResponse.getString("challenge");
		if (challenge.equals(receivedChallenge)) {
			System.out.println("Challenges match");
		} else {
			System.out.println(challenge);
			System.out.println(receivedChallenge);
			throw new Exception("Challenges do not match");
		}
		
		
		// Now, let's make sure the response if fresh. We will use a one minute window here and
		// compare the timestamp to our local time.
		long timestamp = entropyResponse.getLong("timestamp");
		long localTime = System.currentTimeMillis();
		long freshnessWindowSec = 60;
		
		if (Math.abs(timestamp - localTime) <= freshnessWindowSec * 1000) {
			System.out.println("Entropy response is fresh");
		} else {
			throw new Exception("Entropy response is not fresh");
		}
		
		// We now know the entropy was generated for our request and that it is fresh. If we want to
		// go further and authenticate it, we can verify the signature of the JWT and cross 
		// reference its content with what we received. The public key is fetched from the X.509
		// certificate received from a getCertificateChain request. If required, the certificate
		// chain can be analyzed to insure its source is a trusted CA.
		JSONObject certResponse = sporeClient.doCertificateChainRequest();
		byte[] certChain = certResponse.getString("certificateChain").getBytes();
		InputStream is = new ByteArrayInputStream(certChain);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) cf.generateCertificate(is);
		PublicKey publicKey = certificate.getPublicKey();

		String token = entropyResponse.getString("JWT");
		Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
		JWTVerifier verifier = JWT.require(algorithm)
				.withClaim("challenge", challenge)
				.withClaim("timestamp", timestamp)
				.withClaim("entropy", b64entropy)
				.build();
		DecodedJWT jwt = verifier.verify(token);
		System.out.println("Entropy response is authenticated");
		
	}
}


