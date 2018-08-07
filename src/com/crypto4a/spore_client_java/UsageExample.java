package com.crypto4a.spore_client_java;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

public class UsageExample {

	/**
	 * 
	 * @param args
	 * args[0](String)	-- Spore server adress
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
	
	public static void seedRNG(SporeClient sporeClient) throws IOException, JSONException {
		byte[] challenge = new byte[32];
		new Random().nextBytes(challenge);
		
		String challengeB64 = Base64.getEncoder().encodeToString(challenge);
		String entropy = sporeClient.doEntropyRequest(challengeB64).getString("entropy");
		System.out.println("Received entropy: " + entropy);
		
		SecureRandom rng = new SecureRandom();
		rng.setSeed(Base64.getDecoder().decode(entropy));
		System.out.println("SecureRandom was seeded successfully.");
	}

}
