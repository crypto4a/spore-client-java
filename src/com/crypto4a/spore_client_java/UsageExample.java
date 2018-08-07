package com.crypto4a.spore_client_java;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class UsageExample {

	/**
	 * 
	 * @param args
	 *            args[0](String) -- Spore server adress
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, JSONException {
		String serverAddress = args[0];
		SporeClient sporeClient = new SporeClient(serverAddress);

		System.out.println("Performing requests: ");
		performRequests(sporeClient);

		System.out.println("Seeding local radnomness generator: ");
		seedRNG(sporeClient);

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

	public static void testConnection(SporeClient sporeClient) throws IOException, JSONException {

		// Performing a info request is a simple way to validate that the connection is
		// working. An exception is thrown at this point if the server can't be reached
		// or if an error is returned.
		JSONObject response = sporeClient.doInfoRequest();

		// Printing the received information is not necessary.
		System.out.println("Server name: " + response.get("name"));
		System.out.println("Entropy size: " + response.get("entropySize"));
	}

	public static void seedRNG(SporeClient sporeClient) throws IOException, JSONException {
		String entropy = sporeClient.doEntropyRequest(null).getString("entropy");
		System.out.println("Received entropy: " + entropy);

		SecureRandom rng = new SecureRandom();
		rng.setSeed(Base64.getDecoder().decode(entropy));
		System.out.println("SecureRandom was seeded successfully.");
	}

}
