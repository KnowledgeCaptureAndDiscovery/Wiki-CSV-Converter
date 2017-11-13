package Validator;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

import APIquery.*;
import Ontology.*;
import Utilities.*;

class Category {
	
	String Name;
	String type;
	HashMap<String,ArrayList<String>> hmap;
	
	public String getName() {
		return Name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setName(String name) {
		Name = name;
	}
	
	public HashMap<String, ArrayList<String>> getHmap() {
		return hmap;
	}
	
	public void setHmap(HashMap<String, ArrayList<String>> hmap) {
		this.hmap = hmap;
	}
}

public class Validator {
	
	// Generates validation report
	public String getValidationReport(String loc, String output) throws Exception {
		StringBuilder sbans = new StringBuilder();
		String sub = loc.substring(loc.lastIndexOf("/") + 1, loc.length());
		
		// Create API query object
		APIQuery api_query = new APIQuery(); 
		api_query.login();
		
		if(sub.contains("project") || sub.contains("workinggroup")) {			
			Scanner sc = new Scanner(new File(loc));
			Category c = new Category();
			
			int count = 0;
			
			Ontology ontology = null;
			
			HashMap<String,ArrayList<String>> hmap = new HashMap<>();
			String prop = null; // property
			ArrayList<String> warnings = new ArrayList<String>(); // list of warnings
			ArrayList<String> generalWarnings = new ArrayList<String>(); // list of warnings

			while(sc.hasNextLine()) {
				String current = sc.nextLine();
				String currentarr[] = current.split(",");

				if(count==0) {
					//setting the Name of the Category Instance
					c.setName(currentarr[1]);
				}
				else if(count==1) {
					//setting the Type of the Category Instance
					c.setType(currentarr[1]);
					
					// Initialize ontology object with corresponding owl file
					if(c.getType().contains("Project")) {
						ontology = new Ontology(Constants.PROJECT_ONTOLOGY);
					}
					else if(c.getType().contains("WorkingGroup")) {
						ontology = new Ontology(Constants.WORKING_GROUP_ONTOLOGY);
					}
					
					String entity = c.getName();
					entity = entity.replaceAll(" ", "+");
					
					if(api_query.doesExist(entity)) {
						sbans.append("<strong>" + c.getName() +" "+c.getType()+" already exists. Your csv data will overwrite any existing values with the wiki. </strong><br /><br />");
					}
					else {
						sbans.append("<strong>" + c.getName() +" "+c.getType()+" does not exist. A new wiki page will be created. </strong><br /><br />");
					}
				}
				else if(count>=4) {
					// if blank line continue and set property to null
					if(current.equals(",")) {	
						sbans.append("<br />");
						prop=null;
						continue;
					}
					else {
						if(prop==null) {
							prop=currentarr[0];
							
							/*** CHECKING FOR PROPERTY NAMING WARNINGS ***/							
							// Format property for ontology query
							String property = prop;
							property = property.split(" ")[0];
							property = Character.toLowerCase(property.charAt(0)) + property.substring(1);
							
							// If property doesn't exist in the ontology
							if(!ontology.propertyExists(property)) {
								generalWarnings.add("- WARNING: Property " + prop + " does not exist within the wiki, was this addition intended? <br />");
							}
						}

						if(hmap.containsKey(prop)) {
							// Format property for ontology query
							String property = prop;
							property = property.split(" ")[0];
							property = Character.toLowerCase(property.charAt(0)) + property.substring(1);
							
							/*** CHECKING FOR INVALID TYPE OF PROPERTY VALUE ***/
							if(!ontology.validType(api_query, property, currentarr[1])) {									
								warnings.add("- WARNING: Property " + prop + " received value '" + currentarr[1] + "'  but expects a value of type " + ontology.getDataRange(property) + "<br />");
								sbans.append("<br />");
							}
							// Add property if there is no type error
							else {
								ArrayList<String> currentAl = hmap.get(prop);
								currentAl.add(currentarr[1]);
								
								sbans.append("&emsp; - " + currentarr[1]+"<br />");
								
								hmap.put(prop, currentAl);
								
								/*** CHECKING FOR WARNINGS ***/
								// check for shortened value error
								if(currentarr[1].length() == 1) {
									warnings.add("- WARNING: Property " + prop + " has value of '" + currentarr[1] + "', was this intended? <br />");
								}
								// check for abbreviations error
								else if(currentarr[1].length() == 2 && currentarr[1].contains(".")) {
									warnings.add("- WARNING: Property " + prop + " has value of '" + currentarr[1] + "', please avoid abbreviations <br />");
								}
							}
						}
						else {
							ArrayList<String> currentAl=new ArrayList<>();
							
							if(currentarr.length > 1) {
								sbans.append("- Property " + prop + " will be added with values: <br />");
								
								currentAl.add(currentarr[1]);
								
								for(String value : currentAl) {
									// Format property for ontology query
									String property = prop;
									property = property.split(" ")[0];
									property = Character.toLowerCase(property.charAt(0)) + property.substring(1);
									
									/*** CHECKING FOR INVALID TYPE OF PROPERTY VALUE ***/
									if(!ontology.validType(api_query, property, value)) {									
										warnings.add("- WARNING: Property " + prop + " received value '" + value + "'  but expects a value of type " + ontology.getDataRange(property) + "<br />");
										sbans.append("<br />");
									}
									// Add property if there is no type error
									else {
										sbans.append("&emsp; - " + value + "<br />");
	
										/*** CHECKING FOR WARNINGS ***/
										// check for shortened value error
										if(value.length() == 1 && !isInteger(value)) {
											warnings.add("- WARNING: Property " + prop + " has value of '" + value + "', was this intended? <br />");
										}
										// check for abbreviations error
										else if(currentarr[1].length() == 2 && currentarr[1].contains(".")) {
											warnings.add("- WARNING: Property " + prop + " has value of '" + currentarr[1] + "', please avoid abbreviations <br />");
										}
									}
								}
			
								hmap.put(prop, currentAl);
							}
							/*** CHECKING FOR EMPTY STRING WARNING ***/
							else {
								warnings.add("- WARNING: Property " + currentarr[0] + " contains an empty value and will not be added <br />");
							}
						}
					}
				}
				count++;
			}
			
			/*** ADDING WARNINGS TO REPORT ***/
			warnings.addAll(generalWarnings);
			if(!warnings.isEmpty()) {
				sbans.append("<font color=red> ******************************************************************* <br />");
				
				sbans.append("<strong> ALERT: " + warnings.size() + " warning(s) found </strong><br />");
				
				for(String warning : warnings) {
					sbans.append(warning + "<br />");
				}
				
				sbans.append("******************************************************************* </font><br /><br />");
			}
			
			sc.close();
			
			return sbans.toString();
			
		}	
		else if(sub.contains("person") || sub.contains("organization") || sub.contains("cohort")) {			
			Scanner sc = new Scanner(new File(loc));
			
			Category c=new Category();
			
			int count=0;
			
			Ontology ontology = null;
			
			ArrayList<String> allProps = new ArrayList<String>();
			ArrayList<String> generalWarnings = new ArrayList<String>(); // General warnings not specific to a given csv entry
			
			while(sc.hasNextLine()) {
				String current = sc.nextLine();	

				if(count == 0) {
					String currentarr[]=current.split(",");
					
					c.setType(currentarr[0]);	
					
					// Initialize ontology object with corresponding owl file
					if(c.getType().contains("Person")) {
						ontology = new Ontology(Constants.PERSON_ONTOLOGY);
					}
					else if(c.getType().contains("Organization")) {
						ontology = new Ontology(Constants.ORGANIZATION_ONTOLOGY);
					}
					else if(c.getType().contains("Cohort")) {
						ontology = new Ontology(Constants.COHORT_ONTOLOGY);
					}
					
					for(int i=1; i<currentarr.length; i++) {
						allProps.add(currentarr[i]);
						
						// Format property for ontology query
						String property = currentarr[i];
						property = property.split(" ")[0];
						property = Character.toLowerCase(property.charAt(0)) + property.substring(1);
						
						// If property doesn't exist in the ontology
						if(!ontology.propertyExists(property)) {
							generalWarnings.add("- WARNING: Property " + currentarr[i] + " does not exist within the wiki, was this addition intended? <br />");
						}
					}
				}
				else if(count > 0) {
					
					if(current.contains("\"")) {
						String tempcurr = "";
						String doublequote = "";
						
						for(int i=0; i<current.length(); i++) {
							if(current.charAt(i) != '\"') {
								tempcurr += current.charAt(i);	
							}
							else if(current.charAt(i) == '\"') {
								int j=i+1;
							
								while(j<current.length() && current.charAt(j) != '\"') {
									doublequote += current.charAt(j);
									j++;
								}
								doublequote = doublequote.replace(',', '$');
								tempcurr += doublequote;
								doublequote = "";
								
								i=j;
							}
						}
						current=tempcurr;
					}
					
					ArrayList<String> warnings = new ArrayList<String>(); // list of warnings
					ArrayList<String> errors = new ArrayList<String>(); // list of errors
					
					String currentarr[] = current.split(",");

					HashMap<String,List<String>> hmap=new HashMap<>();
					
					c.setName(currentarr[0]);
					
					String entity = c.getName();
					entity = entity.replaceAll(" ", "+");
					
					// Create API query object 
					if(api_query.doesExist(entity)) {
						sbans.append("<strong>" + c.getName() +" "+c.getType()+" already exists. Your csv data will overwrite any existing values with the wiki. </strong><br /><br />");
					}
					else {
						sbans.append("<strong>" + c.getName() +" "+c.getType()+" does not exist. A new wiki page will be created. </strong><br /><br />");
					}
										
					for(int i=1; i<currentarr.length; i++) {
						if(currentarr.length >= i) {
							boolean error = false; // Flag for error
							
							String arr[]=currentarr[i].split("; ");
							
							List<String> values=Arrays.asList(arr); // gets potential list of values for a property
							ArrayList<String> valid_values = new ArrayList<String>();
							
							// add property values to report if they are not empty
							if(!values.contains("") || values.size() > 1) {	
								for(int k=0; k<values.size(); k++) {
									values.set(k, values.get(k).replace('$', ','));
								}
															
								for(String value : values) {
									// Format property for ontology query
									String property = allProps.get(i-1);
									property = property.split(" ")[0];
									property = Character.toLowerCase(property.charAt(0)) + property.substring(1);

									if(!ontology.validType(api_query, property, value)) {									
										errors.add("- ERROR: Property " + allProps.get(i-1) + " received value '" + value + "'  but expects a value of type " + ontology.getDataRange(property) + "<br />");
										error = true;
									}
									else {
										valid_values.add(value);
										
										/*** CHECKING FOR WARNINGS ***/
										// check for shortened value error
										if(value.length() == 1 && !isInteger(value)) {
											warnings.add("- WARNING: Property " + allProps.get(i-1) + " has value of '" + value + "', was this intended? <br />");
										}
										// check for abbreviations error
										else if(value.length() == 2 && value.contains(".")) {
											warnings.add("- WARNING: Property " + allProps.get(i-1) + " has value of '" + value + "', please avoid abbreviations <br />");
										}
									}
								}
							}
							
							/*** CHECKING FOR EMPTY STRING WARNING ***/
							if(values.contains("")) {
								// check for empty value error
								warnings.add("- WARNING: Property " + allProps.get(i-1) + " contains an empty value and will not be added <br />");
							}
							
							if(!error && !valid_values.isEmpty()) {
								sbans.append("- Property " + allProps.get(i-1) + " will be added with values: <br />");
								for(String value : valid_values) {
									sbans.append("&emsp; - " + value + "<br /><br />");
								}
							}
							
							hmap.put(allProps.get(i-1), values);
						}
					}
					
					/*** ADDING WARNINGS TO REPORT ***/
					warnings.addAll(generalWarnings);
					if(!warnings.isEmpty()) {
						sbans.append("<font color=red> ******************************************************************* <br />");
						
						sbans.append("<strong> ALERT: " + warnings.size() + " warning(s) found </strong><br />");
						
						for(String warning : warnings) {
							sbans.append(warning + "<br />");
						}
						
						sbans.append("******************************************************************* </font><br /><br />");
					}
					
					/*** ADDING ERRORS TO REPORT ***/
					if(!errors.isEmpty()) {
						sbans.append("<font color=red> ******************************************************************* <br />");
						
						sbans.append("<strong> ALERT: " + errors.size() + " errors(s) found </strong><br />");
						
						for(String error : errors) {
							sbans.append(error + "<br />");
						}
						
						sbans.append("******************************************************************* </font><br /><br />");
					}
					
					sbans.append("------------------------------------------------------------------------------------------------<br /><br /><br /><br />");
					
				}
				count++;
			}
			sc.close();
			
			return sbans.toString();
		}
		
		return "ERROR";	
	}
	
	// Checks if input is an integer
	public boolean isInteger(String input) {
	    try {
	        Integer.parseInt(input);
	        return true;
	    }
	    catch(NumberFormatException e) {
	        return false;
	    }
	}
	
}