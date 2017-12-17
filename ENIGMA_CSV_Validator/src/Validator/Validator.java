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
		
		if(sub.contains("csv")) {			
			Scanner sc = new Scanner(new File(loc));

			Category c=new Category();
			
			int count=0;
			
			Ontology ontology = null;
			
			ArrayList<String> allProps = new ArrayList<String>();
			ArrayList<String> generalWarnings = new ArrayList<String>(); // General warnings not specific to a given csv entry
			
			while(sc.hasNextLine()) {
				String current = sc.nextLine();	
				current = current.replace("; ", ";");
				current = current.replace("_", " ");

				if(count == 0 && !current.equals("")) {
					String currentarr[]=current.split(",");
					
					String type = currentarr[0].split(" ")[0] + " (E)";
					c.setType(type);	
					
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
					else if(c.getType().contains("Project")) {
						ontology = new Ontology(Constants.PROJECT_ONTOLOGY);
					}
					else if(c.getType().contains("WorkingGroup")) {
						ontology = new Ontology(Constants.WORKING_GROUP_ONTOLOGY);
					}
					
					for(int i=1; i<currentarr.length; i++) {
						allProps.add(currentarr[i]);
						
						// Format property for ontology query
						String property = currentarr[i];
						property = property.split(" ")[0];
						property = Character.toLowerCase(property.charAt(0)) + property.substring(1);
						
						// If property doesn't exist in the ontology
						if(!ontology.propertyExists(property)) {
							generalWarnings.add("- WARNING: Undefined Property Error – Property " + currentarr[i] + " does not exist within the wiki and will be ignored! <br />");
						}
					}
				}
				else if(count > 0 && !current.equals("")) {
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
					ArrayList<String> notes = new ArrayList<String>(); // list of notes
					
					String currentarr[] = current.split(",");
					
					HashMap<String,List<String>> hmap=new HashMap<>();
					
					c.setName(currentarr[0]);
					
					String entity = c.getName();
					entity = entity.replaceAll(" ", "+");
					
					// Create API query object 
					if(api_query.doesExist(entity, c.getType())) {
						sbans.append("<strong>" + hyperlink(c.getName()) +" "+c.getType()+" already exists. Your csv data will overwrite any existing values with the wiki. </strong><br /><br />");
					}
					else {
						sbans.append("<strong>" + c.getName() +" "+c.getType()+" does not exist. A new wiki page will be created. </strong><br /><br />");
					}
					
					int warnings_index = sbans.length(); // Index to append warnings
					
					for(int i=1; i<currentarr.length; i++) {
						if(currentarr.length >= i) {							
							String arr[]=currentarr[i].split(";");
							
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
										if(ontology.isObjectProp(property)) {
											notes.add("- NOTE: Property " + allProps.get(i-1) + " received value '" + value + "' a page for this value doesn't exist, so one will be created <br />");
											valid_values.add(value);
										}
										else {
											warnings.add("- WARNING: Incompatible Type Error – Property " + allProps.get(i-1) + " received value '" + value + "' but expects a value of type " + ontology.getDataRange(property) + ". Value will not be added! <br />");
										}
									}
									else {										
										/*** CHECKING FOR WARNINGS ***/
										// check for shortened value error
										if(value.length() == 1 && !isInteger(value)) {
											notes.add("- NOTE: Property " + allProps.get(i-1) + " has shortened value of '" + value + "', was this intended? <br />");
											valid_values.add(value);
										}
										// check for abbreviations error
										else if(value.length() == 2 && value.contains(".")) {
											warnings.add("- WARNING: Abbreviations Error – Property " + allProps.get(i-1) + " value '" + value + "' will not be added! <br />");
										}
										else {
											// Hyperlink existing objects
											if(ontology.isObjectProp(property)) {
												valid_values.add(hyperlink(value));
											}
											else{
												valid_values.add(value);
											}
										}
									}
								}
							}
							
							/*** CHECKING FOR EMPTY STRING NOTE ***/
							if(values.contains("")) {
								notes.add("- NOTE: Empty value found for property " + allProps.get(i-1) + " and will not be added! <br />");
							}
							
							if(!valid_values.isEmpty()) {
								sbans.append("- Property " + allProps.get(i-1) + " will be added with value(s): <br />");
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
						String warnings_str = "";
						warnings_str += "<font color=red> ******************************************************************* <br />";
						
						warnings_str += "<strong> ALERT: " + warnings.size() + " warning(s) found </strong><br />";
						
						for(String warning : warnings) {
							warnings_str += warning + "<br />";
						}
						
						warnings_str += "******************************************************************* </font><br /><br />";
						sbans.insert(warnings_index, warnings_str);
					}
					/*** ADDING NOTES TO REPORT ***/
					if(!notes.isEmpty()) {
						sbans.append("<font color=#636363> ******************************************************************* <br />");
						
						sbans.append("<strong> ALERT: " + notes.size() + " note(s) found </strong><br />");
						
						for(String note : notes) {
							sbans.append(note + "<br />");
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
	private boolean isInteger(String input) {
	    try {
	        Integer.parseInt(input);
	        return true;
	    }
	    catch(NumberFormatException e) {
	        return false;
	    }
	}
	
	private String hyperlink(String value) {
		String value_asLink = value.replace(" ", "_");
		return "<a href=" + Constants.WIKI_INDEX+value_asLink + ">" + value + "</a>";
	}
	
}