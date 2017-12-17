package APIquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Utilities.Constants;

// Class to query the API 

public class APIQuery {
	
	private String token;
	private HttpClient httpclient;
	
	public static void main(String[] args) {
		APIQuery api_query = new APIQuery();
		api_query.login();
		System.out.println(api_query.doesExist("AMC", "Cohort (E)"));
	}
	
	public APIQuery() {
		httpclient = HttpClients.createDefault();
	}
	
	// Logs in to wiki using token 
	public void login() {
		try {
			getLoginParams();
            
			HttpPost httppost = new HttpPost(Constants.WIKI + "/api.php?action=login&lgname=admin&format=json");

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("lgpassword", "admin123"));
			params.add(new BasicNameValuePair("lgtoken", token));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			//Execute request.
			httpclient.execute(httppost);     
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	// Gets login token and stores it locally
	public void getLoginParams() {
		try {
			HttpPost httppost = new HttpPost(Constants.WIKI + "/api.php?action=query&meta=tokens&type=login&format=json");

			//Execute and get the response.
			HttpResponse http_response = httpclient.execute(httppost);
			HttpEntity entity = http_response.getEntity();

			if (entity != null) {
			    InputStream instream = entity.getContent();
			    try {
			    	BufferedReader in = new BufferedReader(new InputStreamReader(instream));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
										
					JSONParser parser = new JSONParser();
			        Object obj = parser.parse(response.toString());
			        JSONObject jsonObject = (JSONObject) obj;

			        JSONObject query = (JSONObject) jsonObject.get("query");
			        JSONObject tokens = (JSONObject) query.get("tokens");
		            token = (String) tokens.get("logintoken");
			    } finally {
			        instream.close();
			    }
			}	
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	// Checks if parameter exists in the wiki
	public boolean doesExist(String entity) {
		try {
			entity = entity.replace(" ", "+");
			
			HttpGet request = new HttpGet(Constants.WIKI + "/api.php?&format=json&action=query&list=search&srwhat=title&srsearch="+entity);
			
			//Execute and get the response.
			HttpResponse http_response = httpclient.execute(request);
			HttpEntity http_entity = http_response.getEntity();
			
			if (http_entity != null) {
			    InputStream instream = http_entity.getContent();
			    try {
			    	BufferedReader in = new BufferedReader(new InputStreamReader(instream));
					String inputLine;
					StringBuffer response = new StringBuffer();
			
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					
			        JSONParser parser = new JSONParser();
			        Object obj = parser.parse(response.toString());
			        JSONObject jsonObject = (JSONObject) obj;
			        
		            JSONObject query = (JSONObject) jsonObject.get("query");
		            JSONArray search = (JSONArray) query.get("search");
		            
		            for(int i = 0; i < search.size(); i++) {
		            	JSONObject result = (JSONObject) search.get(i);
		            	String title = (String) result.get("title");
		            	
		            	entity = entity.replaceAll("\\+", " ");

		            	if(title.equals(entity)) {
		        			in.close();
		            		return true;
		            	}
		            }
		            
					in.close();
		            return false;
			    } finally {
			        instream.close();
			    }
			}	
			else {
				return false;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}	
	
	// Checks if parameter exists in the wiki with given category
	public boolean doesExist(String entity, String category) {
		try {
			entity = entity.replace(" ", "+");

			HttpGet request = new HttpGet(Constants.WIKI + "/api.php?&format=json&action=query&prop=categories&titles="+entity);
			
			//Execute and get the response.
			HttpResponse http_response = httpclient.execute(request);
			HttpEntity http_entity = http_response.getEntity();
			
			if (http_entity != null) {
			    InputStream instream = http_entity.getContent();
			    try {
			    	BufferedReader in = new BufferedReader(new InputStreamReader(instream));
					String inputLine;
					StringBuffer response = new StringBuffer();
			
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					
			        JSONParser parser = new JSONParser();
			        Object obj = parser.parse(response.toString());
			        JSONObject jsonObject = (JSONObject) obj;
			        
		            JSONObject query = (JSONObject) jsonObject.get("query");
		            JSONObject pages = (JSONObject) query.get("pages");
		            
		            String pages_str = pages.toString();
		            String pageid_str = pages_str.substring(pages_str.indexOf("\"") + 1);
		            pageid_str = pageid_str.substring(0, pageid_str.indexOf("\""));
		            
		            if(pageid_str.equals("-1")) {
		            	return false;
		            }
		            
		            JSONObject pageid = (JSONObject) pages.get(pageid_str);
		            JSONArray categories = (JSONArray) pageid.get("categories");

		            for(int i = 0; i < categories.size(); i++) {
		            	JSONObject result = (JSONObject) categories.get(i);
		            	String c_title = (String) result.get("title");
		            	c_title = c_title.substring(c_title.indexOf(":") + 1);
		            	
		            	if(c_title.equals(category)) {
		        			in.close();
		            		return true;
		            	}
		            }
		            
					in.close();
		            return false;
			    } finally {
			        instream.close();
			    }
			}
			else {
				return false;
			}
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
