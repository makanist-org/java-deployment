package com.example;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class Api_data {

    private static final String API_BASE_URL = "https://seceng-code-screen.core-services.ingress.coreweave.com";
    private static final String API_CLIENT_ID = "interviewee";
    private static final String API_CLIENT_SECRET = "hunter2";
    private static final String AUTH_TOKEN_PATH = API_BASE_URL + "/oauth2/token";
    private static final String OLD_API_PATH = API_BASE_URL + "/old-system/logs";
    private static final String NEW_API_PATH = API_BASE_URL + "/new-system/logs";
    private static List<JSONObject> transformedLogs = null;

    public Api_data(){
    	try {
	        String accessToken = getAccessToken();
	
	        transformedLogs = fetchOldLogs(accessToken).stream()
	                .map(Api_data::transformLogLine)
	                .filter(Objects::nonNull)
	                .collect(Collectors.toList());
	
	        System.out.println("old_data number of records: " + transformedLogs.size());
	        System.out.println(transformedLogs);
	        System.out.println("new_data number of records: " + transformedLogs.size());
	
	        // Process in batches using stream
	        splitIntoBatches(transformedLogs, 100).forEach(batch -> {
	            try {
	                String newAccessToken = getAccessToken(); // refresh token each time
	                postNewLogs(newAccessToken, batch);
	            } catch (Exception e) {
	                System.err.println("Batch post failed: " + e.getMessage());
	            }
	        });
    	} catch (Exception e) {
    		System.out.print("exception occured in API Data: " + e.getMessage());
    		
    	}
    }

    public List<JSONObject> getApiData() {
        if (transformedLogs == null) {
            throw new IllegalStateException("Data has not been fetched yet. Please initialize Api_data first.");
        }
        List<JSONObject> copy = new ArrayList<>(transformedLogs);
    	return copy;
    }
    
    private static JSONObject transformLogLine(String logLine) {
        String[] parts = logLine.split(" ");
        if (parts.length < 4) return null;

        String timestamp = parts[0];
        String username = parts[1];
        int severity;
        try {
            severity = Integer.parseInt(parts[2]);
        } catch (Exception e) {
            severity = 1;
        }

        String message = IntStream.range(3, parts.length)
                .mapToObj(i -> parts[i])
                .collect(Collectors.joining(" "));

        JSONObject entry = new JSONObject();
        entry.put("timestamp", timestamp);
        entry.put("username", username);
        entry.put("severity", severity);
        entry.put("message", message);
        return entry;
    }

    private static String getAccessToken() throws Exception {
        URL url = new URL(AUTH_TOKEN_PATH);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        String urlParams = "grant_type=client_credentials&client_id=" + URLEncoder.encode(API_CLIENT_ID, "UTF-8") +
                "&client_secret=" + URLEncoder.encode(API_CLIENT_SECRET, "UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(urlParams.getBytes("UTF-8"));
        }

        String response = readResponse(conn.getInputStream());
        JSONObject json = new JSONObject(response);
        return json.getString("access_token");
    }

    private static List<String> fetchOldLogs(String accessToken) throws Exception {
        URL url = new URL(OLD_API_PATH);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        String response = readResponse(conn.getInputStream());
        JSONObject json = new JSONObject(response);
        String logs = json.getString("logs");

        return Arrays.stream(logs.split("\\r?\\n")).collect(Collectors.toList());
    }

    private static void postNewLogs(String accessToken, List<JSONObject> batch) throws Exception {
        URL url = new URL(NEW_API_PATH);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONArray jsonArray = new JSONArray(batch);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonArray.toString().getBytes("UTF-8"));
        }

        String response = readResponse(conn.getInputStream());
        System.out.println(response);
    }

    private static String readResponse(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private static List<List<JSONObject>> splitIntoBatches(List<JSONObject> inputList, int batchSize) {
        return IntStream.range(0, (inputList.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> inputList.subList(i * batchSize, Math.min(inputList.size(), (i + 1) * batchSize)))
                .collect(Collectors.toList());
    }
}
