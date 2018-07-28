package SimpleTanner;

import org.rspeer.ui.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class FetchHelper {
    static int fetchItemPrice(String urlString, int fallbackPrice) {
        try {
            String data = FetchHelper.sendGET(urlString, 4);
            if (data.startsWith("FAIL")) {
                throw new Exception("FAILED get request");
            }
            // overall price is the first property in the JSON response
            String overallPrice = data.substring(data.indexOf(":") + 1, data.indexOf(","));
            return Integer.valueOf(overallPrice);
        } catch (Exception e) {
            Log.severe(e);
            return fallbackPrice;
        }
    }

    static Image getImage(String url){
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e){
            return null;
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

            return response.toString();
        } else {
            Log.info("GET request failed, retrying...");
            if (retriesLeft >= 1) {
                return FetchHelper.sendGET(getUrl, retriesLeft - 1);
            }
            return null;
        }

    }
}
