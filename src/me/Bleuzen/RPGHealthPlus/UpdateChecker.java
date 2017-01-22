package me.Bleuzen.RPGHealthPlus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class UpdateChecker {
	
	// resource ID on spigotmc.org
	private final String resource = "6176";

	boolean updateAvailable() {
		Main.getInstance().getLogger().log(Level.INFO, "Checking for updates ...");
		try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + resource).getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			con.disconnect();
            
			String rev = version.substring(version.lastIndexOf(" ") + 1);
			
			if(rev.equals(Main.runningVersion)) {
				int onlineVersion = toVersionNumber(version.substring(0, version.indexOf(" ")));
				int localVersion = toVersionNumber(Main.getInstance().getDescription().getVersion());
				
				if(onlineVersion > localVersion) {
					return true;
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return false;
	}
	
	private static int toVersionNumber(String in) {
		return Integer.parseInt(in.replaceAll("[^0-9]", ""));
	}
	
}
