package lamerton.troy.tanner;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.ui.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class FetchHelper {
    static int fetchItemPrice(int itemId, int fallbackPrice) {
        try {
            String urlString = "https://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + itemId;
            String data = FetchHelper.sendGET(urlString, 4);

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

    static Image getImage(String url){
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e){
            return null;
        }
    }

    static Font getRunescapeFont() {
        try {
            ClassLoader cLoader = FetchHelper.class.getClassLoader();
            // for some reason, getResourceAsStream(...) throws an exception
            // if we dont create any temp file beforehand
            File tmp = File.createTempFile("getResourceAsStream_uses_temp_files1", ".tmp");
            tmp.deleteOnExit();

            String fontpath = "runescape_uf.ttf";

            return Font.createFont(Font.TRUETYPE_FONT, cLoader.getResourceAsStream(fontpath)).deriveFont(20f);

        } catch (Exception e) {
            Log.info("Failed to load awesome font, using fallback font");
            return new Font("Arial", Font.BOLD, 24).deriveFont(14f);
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
                return FetchHelper.sendGET(getUrl, retriesLeft - 1);
            }
            return null;
        }

    }
}
