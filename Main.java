package weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class Main {

    private static final String API_KEY = "11340114-0f4b-4805-971b-3227935f46aa";
    private static final String BASE_URL = "https://api.weather.yandex.ru/v2/forecast";

    public static void main(String[] args) {
        double lat = 55.75; 
        double lon = 37.62; 
    

        try {
            String response = getWeatherData(lat, lon);
            System.out.println("Full Response (JSON): " + response);

            
            JSONObject jsonResponse = new JSONObject(response);

            
            if (jsonResponse.has("fact")) {
                JSONObject fact = jsonResponse.getJSONObject("fact");
                int currentTemperature = fact.getInt("temp");
                System.out.println("Current Temperature: " + currentTemperature + "°C");
            } else {
                System.out.println("Error: 'fact' data is missing in the response.");
            }

            
            if (jsonResponse.has("forecasts")) {
                JSONArray forecast = jsonResponse.getJSONArray("forecasts");
                double averageTemp = calculateAverageTemperature(forecast);
                System.out.println("Average Temperature for next 7 days: " + averageTemp + "°C");
            } else {
                System.out.println("Error: 'forecasts' data is missing in the response.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getWeatherData(double lat, double lon) throws Exception {
        String urlString = String.format("%s?lat=%f&lon=%f", BASE_URL, lat, lon);
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Yandex-API-Key", API_KEY);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP GET request failed with response code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private static double calculateAverageTemperature(JSONArray forecast) {
        double totalTemperature = 0;
        int count = 0;

        for (int i = 0; i < forecast.length(); i++) {
            JSONObject dailyForecast = forecast.getJSONObject(i);
            if (dailyForecast.has("parts")) {
                JSONObject parts = dailyForecast.getJSONObject("parts");
                if (parts.has("day")) {
                    JSONObject dayPart = parts.getJSONObject("day");
                    if (dayPart.has("temp_max")) {
                        int dayTemperature = dayPart.getInt("temp_max");
                        totalTemperature += dayTemperature;
                        count++;
                    }
                }
            }
        }

        return count > 0 ? totalTemperature / count : 0; // Avoid division by zero
    }
}
