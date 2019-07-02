package script.java.tanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.ui.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExPriceChecker {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static JsonObject OSBUDDY_SUMMARY_JSON;


    static int getRSBuddyPrice(int itemId, int fallbackPrice) {
        try {
            String urlString = "https://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + itemId;
            String data = ExPriceChecker.sendGET(urlString, 4);

            if (data == null) {
                throw new Exception("FAILED to fetch item price. The RSBuddy API is not responding.");
            }
            // overall price is the first property in the JSON response
            String overallPrice = data.substring(data.indexOf(":") + 1, data.indexOf(","));
            return Integer.valueOf(overallPrice);
        } catch (Exception e) {
            Log.severe(e.getMessage());
            return fallbackPrice;
        }
    }

    private static String sendGET(String getUrl, int retriesLeft) throws IOException {
        URL obj = new URL(getUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            Log.info("SUCCESS : GET request succeeded");
            return response.toString();
        } else {
            Log.info("Retrying GET request...");
            if (retriesLeft >= 1) {
                Time.sleep(200);
                return ExPriceChecker.sendGET(getUrl, retriesLeft - 1);
            }
            return null;
        }

    }

    /**
     * Sets the OSBuddy price summary json.
     */
    private static void setOSBuddySummaryJson() throws IOException {
        final Request request = new Request.Builder()
                .url("https://storage.googleapis.com/osbuddy-exchange/summary.json")
                .get()
                .build();
        final Response response = HTTP_CLIENT.newCall(request).execute();
        if (!response.isSuccessful())
            return;

        if (response.body() == null)
            return;

        final Gson gson = new Gson().newBuilder().create();
        OSBUDDY_SUMMARY_JSON = gson.fromJson(response.body().string(), JsonObject.class);
    }

    /**
     * Gets the price of the item id from the OSBuddy price summary json. The entire summary data is stored upon first
     * retrieval.
     *
     * @param id The id of the item.
     * @return The price of the item; 0 otherwise.
     */
    public static int getOSBuddySellPrice(int id) throws IOException {
        if (OSBUDDY_SUMMARY_JSON == null)
            setOSBuddySummaryJson();

        final JsonObject json_objects = OSBUDDY_SUMMARY_JSON.getAsJsonObject(Integer.toString(id));
        if (json_objects == null)
            return 0;

        return json_objects.get("sell_average").getAsInt();
    }

    public static int getOSBuddyBuyPrice(int id) throws IOException {
        if (OSBUDDY_SUMMARY_JSON == null)
            setOSBuddySummaryJson();

        final JsonObject json_objects = OSBUDDY_SUMMARY_JSON.getAsJsonObject(Integer.toString(id));
        if (json_objects == null)
            return 0;

        return json_objects.get("buy_average").getAsInt();
    }
}