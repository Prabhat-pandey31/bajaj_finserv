package com.prabhat.sol;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <240350120100> <\"C:\\Users\\DELL\\Desktop\\example.json\">");
            System.exit(1);
        }

        String prnNumber = args[0].toLowerCase();
        String filePath = args[1];

        String destinationValue = findDestinationValue(filePath);
        if (destinationValue == null) {
            System.out.println("No 'destination' key found in the JSON file.");
            System.exit(1);
        }

        String randomString = generateRandomString(8);
        String concatenatedString = prnNumber + destinationValue + randomString;

        String md5Hash = generateMD5Hash(concatenatedString);
        System.out.println(md5Hash + ";" + randomString);
    }

    private static String findDestinationValue(String filePath) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            return searchForDestination(jsonObject);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String searchForDestination(JSONObject jsonObject) {
        Set<Entry<String, Object>> entrySet = jsonObject.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            if (entry.getKey().equals("\"C:\\Users\\DELL\\Desktop\\example.json\"")) {
                return entry.getValue().toString();
            } else if (entry.getValue() instanceof JSONObject) {
                String result = searchForDestination((JSONObject) entry.getValue());
                if (result != null) return result;
            } else if (entry.getValue() instanceof Iterable) {
                for (Object item : (Iterable<?>) entry.getValue()) {
                    if (item instanceof JSONObject) {
                        String result = searchForDestination((JSONObject) item);
                        if (result != null) return result;
                    }
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
