package com.crypto4a.spore.clientjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public final class SporeClient {
	
	private final String ENDPOINT_INFO = "getInfo";
	private final String ENDPOINT_ENTROPY = "getEntropy";
	private final String ENDPOINT_CERT = "getCertChain";
	
	private URL serverAddress;
	
	public SporeClient(String serverAddress) throws MalformedURLException {
		this.serverAddress = new URL(serverAddress);
	}
	
	public void setServerAddress(String serverAddress) throws MalformedURLException {
		this.serverAddress = new URL(serverAddress);
	}
	
	public JSONObject doInfoRequest() throws IOException, JSONException {
		URL url = new URL(serverAddress, ENDPOINT_INFO);
		String response = performPost(url);
		return new JSONObject(response);
	}
	
	public JSONObject doEntropyRequest(String challenge) throws IOException, JSONException {
		URL url = new URL(serverAddress, ENDPOINT_ENTROPY + "?challenge=" + challenge);
		String response = performPost(url);
		return new JSONObject(response);
	}
	
	public JSONObject doCertificateChainRequest() throws IOException, JSONException {
		URL url = new URL(serverAddress, ENDPOINT_CERT);
		String response = performPost(url);
		return new JSONObject(response);
	}
	
	private String performPost(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		BufferedReader br = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));
		String line;
		StringBuffer content = new StringBuffer();
		while((line = br.readLine())  != null) {
			content.append(line);
		}
		return content.toString();
	}
}
