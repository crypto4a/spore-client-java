package com.crypto4a.spore_client_java;

import java.io.IOException;

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
		
		JSONObject response = sporeClient.doInfoRequest();
		System.out.println("getInfo request:");
		System.out.println("\tname: " + response.get("name"));
		System.out.println("\tentropySize: " + response.get("entropySize"));
//		System.out.println(response.toString(4));
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

}
