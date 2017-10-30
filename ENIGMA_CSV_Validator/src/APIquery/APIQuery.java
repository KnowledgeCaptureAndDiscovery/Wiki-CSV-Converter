package APIquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// Class to query the API 

public class APIQuery {
	
	public static String WIKI="http://organicdatacuration.org/enigma_sandbox";
	private String cookieprefix;
	private String token;
	private String sessionid;
	
	public static void main(String[] args) {
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		APIQuery api_query = new APIQuery();
		api_query.login();
	}
	
	public APIQuery() {
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
	}
	
	public void login() {
		try {
			getLoginParams();
            
            String link = WIKI + "/api.php?action=login&lgname=ryanespiritu&lgpassword=bluebaby1&lgtoken="+token+"&format=json";
			System.out.println(link);
			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty(cookieprefix, sessionid);
			con.setRequestMethod("POST");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
	        JSONParser parser = new JSONParser();
	        Object obj = parser.parse(response.toString());
	        JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);
            
            System.out.println(doesExist("AMC"));

		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	// Gets login token and sessionID and stores them locally
	public void getLoginParams() {
		try {
			String link = WIKI + "/api.php?action=login&lgname=ryanespiritu&lgpassword=bluebaby1&format=json";
			System.out.println(link);
			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			con.setRequestMethod("POST");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
	        JSONParser parser = new JSONParser();
	        Object obj = parser.parse(response.toString());
	        JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);

	        JSONObject login = (JSONObject) jsonObject.get("login");
	        sessionid = (String) login.get("sessionid");
	        cookieprefix = (String) login.get("cookieprefix");
            token = (String) login.get("token");
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	// Checks if an entity exists in the wiki
	public boolean doesExist(String entity) {
		try {
			String link = WIKI + "/api.php?&format=json&action=query&list=search&srwhat=title&srsearch="+entity;
			System.out.println(link);
			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
	        JSONParser parser = new JSONParser();
	        Object obj = parser.parse(response.toString());
	        JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);
            
            JSONObject query = (JSONObject) jsonObject.get("query");
            JSONArray search = (JSONArray) query.get("search");
            
            for(int i = 0; i < search.size(); i++) {
            	JSONObject result = (JSONObject) search.get(i);
            	String title = (String) result.get("title");
            	if(title.equals(entity)) {
        			in.close();
            		return true;
            	}
            }
            
			in.close();
            return false;		
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
