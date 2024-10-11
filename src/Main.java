import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String city;

            do {
                System.out.println("===================================================");
                System.out.println("Enter City (Type 'No' to Quit): ");
                city = scanner.nextLine();

                if (city.equalsIgnoreCase("No")) {
                    break;
                }

                // Get Location Data
                JSONObject cityLocationData = (JSONObject) getLocationData(city);
                if (cityLocationData != null) {
                    double latitude = (double) cityLocationData.get("latitude");
                    double longitude = (double) cityLocationData.get("longitude");

                    displayWeatherData(latitude, longitude);
                } else {
                    System.out.println("City not found");
                }


            } while(!city.equalsIgnoreCase("No"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static JSONObject getLocationData(String city) {
        city = city.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
        + city + "&count=10&language=en&format=json";

        try {
            // 1.  Fetch the location API base on API URL
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            if (apiConnection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to the API.");
                System.out.println("Response code: " + apiConnection.getResponseCode());
                return null;
            }

            // 2. Read the response and convert to string
            String jsonResponse = readApiResponse(apiConnection);

            // 3. Parse the string into JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(jsonResponse);

            // 4. Retrieve only the location
            JSONArray locationData = (JSONArray) resultJsonObj.get("results");
            if (locationData != null) {
                return (JSONObject) locationData.get(0);
            }

        } catch (Exception e) {
            System.out.println("Cannot find the city");
            e.printStackTrace();
        }

        return null;
    }


    private static String readApiResponse(HttpURLConnection apiConnection) {
        try {
            // Create String builder
            StringBuilder resultJson = new StringBuilder();

            Scanner scanner = new Scanner(apiConnection.getInputStream());

            while (scanner.hasNextLine()) {
                // Read and append the current line of the response
                resultJson.append(scanner.nextLine());
            }
            scanner.close();

            return resultJson.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void displayWeatherData(double latitude, double longitude) {

        try {
            // fetch api
            String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current=temperature_2m,relative_humidity_2m,is_day,rain,wind_speed_10m";
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            // check response status
            if (apiConnection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to the API");
                System.out.println("Response code: " + apiConnection.getResponseCode());
            }

            // Read the response and convert to String
            String jsonResponse = readApiResponse(apiConnection);
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeather = (JSONObject) resultJsonObj.get("current");

            // Store the data into their corresponding data type
            String time = (String) currentWeather.get("time");
            System.out.println("Current Time: " + time);

            double temperature = (double) currentWeather.get("temperature_2m");
            System.out.println("Current Temperature: " + temperature);

            long humidity = (long) currentWeather.get("relative_humidity_2m");
            System.out.println("Relative Humidity: " + humidity);

            long day = (long) currentWeather.get("is_day");
            if (day == 1) {
                System.out.println("Current Day: Day");
            } else {
                System.out.println("Current Day: Night");
            }

            double rain = (double) currentWeather.get("rain");
            System.out.println("Rain (mm): " + rain);

            double wind_speed = (double) currentWeather.get("wind_speed_10m");
            System.out.println("Wind Speed (km/h): " + wind_speed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HttpURLConnection fetchApiResponse (String urlString) {
        try {
            // try to establish connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set the request type to GET
            conn.setRequestMethod("GET");

            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}