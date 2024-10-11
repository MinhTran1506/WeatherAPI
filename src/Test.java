import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws IOException, ParseException {
        String city = "Ho Chi Minh";

        city = city.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
                + city + "&count=10&language=en&format=json";


        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");

        StringBuilder resultJson = new StringBuilder();




        if (conn.getResponseCode() == 200) {
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNextLine()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();

        } else {
            System.out.println(conn.getResponseCode());
        }
        System.out.println(resultJson.toString());

        JSONParser parser = new JSONParser();
        JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());

        JSONArray locationData = (JSONArray) resultJsonObj.get("results");
        System.out.println(locationData);
        System.out.println((JSONObject) locationData.get(0));

        JSONObject location = (JSONObject) locationData.get(0);
        double longitude = (double) location.get("longitude");
        double latitude = (double) location.get("latitude");

        System.out.println(longitude + " " + latitude);

        String weatherData = getWeatherData(longitude, latitude);
        System.out.println(weatherData);


    }

    private static String getWeatherData(double longitude, double latitude) throws IOException {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current=temperature_2m,relative_humidity_2m,is_day,rain,wind_speed_10m";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        StringBuilder resultJson = new StringBuilder();
        if (conn.getResponseCode() == 200) {

            Scanner scanner = new Scanner(conn.getInputStream());

            while (scanner.hasNextLine()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            return resultJson.toString();
        } else {
            System.out.println(conn.getResponseCode());
        }

        return null;
    }
}
