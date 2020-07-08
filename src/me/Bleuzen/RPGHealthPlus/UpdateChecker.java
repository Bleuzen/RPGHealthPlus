package me.Bleuzen.RPGHealthPlus;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class UpdateChecker {

    private static final String UPDATER_URL = "https://raw.githubusercontent.com/Bleuzen/metadata/master/rpghealthplus/v1/data.json";

    boolean checkUpdateAvailable() {
        Main.getInstance().getLogger().log(Level.INFO, "Checking for updates ...");

        try {
            JSONObject json = fetchJSON(UPDATER_URL);

            String latestVersion = json.get("version").toString();
//			String downloadUrl = ((JSONObject)json.get("download")).get("url").toString();

            Main.getInstance().getLogger().log(Level.INFO, "Newest version: " + latestVersion);

            String localVersion = Main.getInstance().getDescription().getVersion();

            return compare(localVersion, latestVersion);
        } catch (Exception e) {
            Main.getInstance().getLogger().log(Level.WARNING, "Failed to check for updates.");
            e.printStackTrace();
        }
        return false;
    }

    private JSONObject fetchJSON(String url) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        InputStream inputStream = con.getInputStream();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(
                new InputStreamReader(inputStream, "UTF-8"));
        inputStream.close();
        return jsonObject;
    }

    private String toVersionString(String version) {
        return version.replaceAll("[^0-9.]", "");
    }

    private boolean compare(String local, String online) {
        local = toVersionString(local);
        online = toVersionString(online);

        String[] valsLocal = local.split("\\.");
        String[] valsOnline = online.split("\\.");

        // find the first non-equal number
        int i = 0;
        while (i < valsLocal.length && i < valsOnline.length && valsLocal[i].equals(valsOnline[i])) {
            i++;
        }

        boolean newer;
        if (i < valsLocal.length && i < valsOnline.length) {
            int numLocal = Integer.parseInt(valsLocal[i]);
            int numOnline = Integer.parseInt(valsOnline[i]);
            newer = numOnline > numLocal;
        } else {
            newer = valsOnline.length > valsLocal.length;
        }

        return newer;
    }

}
