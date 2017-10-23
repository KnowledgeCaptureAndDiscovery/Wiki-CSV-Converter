package Validator;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

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
	
	public static String WIKI="https://wiki.org/Enigma#";
	public static String HAS_PROPERTY="https://wiki.org/EnigmaProperty#";
	
	// Set of int properties
	public final static Set<String> INT_PROPERTIES = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(
			"HasNumberOfParticipants (L)", 
			"HasNumberOfFemale (L)",
			"HasNumberOfMale (L)",
			"JoinedEnigmaInYear (L)"
			)
		)
	);
	
	// Set of boolean properties
	public final static Set<String> BOOLEAN_PROPERTIES = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(
			"IsCaseControl (L)", 
			"IsFamilyBased (L)",
			"IsPopulationBased (L)",
			"IncludesFemale (L)",
			"IncludesMale (L)",
			"SexDeterminedGenetically (L)",
			"IsNoLongerActive (L)"
			)
		)
	);
	
	// Generates validation report
	public String getValidationReport(String loc, String output) throws Exception {
		StringBuilder sbans = new StringBuilder();
		String sub = loc.substring(loc.lastIndexOf("/") + 1, loc.length());
		
		if(sub.contains("project") || sub.contains("workinggroup") || sub.contains("wiki")) {			
			Scanner sc = new Scanner(new File(loc));
			Category c = new Category();
			
			int count = 0;
			
			HashMap<String,ArrayList<String>> hmap = new HashMap<>();
			String prop = null; // property
			ArrayList<String> warnings = new ArrayList<String>(); // list of warnings
			
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
					sbans.append("<strong>" + c.getName() +" "+c.getType()+" Does not Exist </strong><br /><br />");
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
						}

						if(hmap.containsKey(prop)) {
							boolean type_error = false;
							
							// Check if property is of type int
							if(INT_PROPERTIES.contains(prop)) {
								// If value is not an int add a warning
								if(!isInteger(currentarr[1])) {
									warnings.add("- WARNING: Property " + prop + " received value '" + currentarr[1] + "'  but expects a value of type 'int' <br />");
									sbans.append("<br />");
									type_error = true;
								}
							}
							// Check if property is of type boolean
							else if(BOOLEAN_PROPERTIES.contains(prop)) {
								// If value is not a boolean add a warning
								if(!isBoolean(currentarr[1])) {
									warnings.add("- WARNING: Property " + prop + " received value '" + currentarr[1] + "'  but expects a value of type 'boolean' <br />");
									sbans.append("<br />");
									type_error = true;
								}
							}
							
							// Add property if there is no type error
							if(!type_error) {
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
								sbans.append("- Property " + prop + " has been added with values: <br />");
								currentAl.add(currentarr[1]);
								
								for(String value : currentAl) {
									boolean type_error = false;
									
									// Check if property is of type int
									if(INT_PROPERTIES.contains(prop)) {
										// If value is not an int add a warning
										if(!isInteger(value)) {
											warnings.add("- WARNING: Property " + prop + " received value '" + value + "'  but expects a value of type 'int' <br />");
											sbans.append("<br />");
											type_error = true;
										}
									}
									// Check if property is of type boolean
									else if(BOOLEAN_PROPERTIES.contains(prop)) {
										// If value is not a boolean add a warning
										if(!isBoolean(value)) {
											warnings.add("- WARNING: Property " + prop + " received value '" + value + "'  but expects a value of type 'boolean' <br />");
											sbans.append("<br />");
											type_error = true;
										}
									}
									
									// Add property if there is no type error
									if(!type_error) {
										sbans.append("&emsp; - " + value + "<br />");
	
										/*** CHECKING FOR WARNINGS ***/
										// check for shortened value error
										if(value.length() == 1) {
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
			if(!warnings.isEmpty()) {
				sbans.append("******************************************************************* <br />");
				
				sbans.append("<strong> ALERT: " + warnings.size() + " warning(s) found </strong><br />");
				
				for(String warning : warnings) {
					sbans.append(warning + "<br />");
				}
				
				sbans.append("******************************************************************* <br /><br />");
			}
			
			sc.close();
			
			return sbans.toString();
			
		}	
		else if(sub.contains("person") || sub.contains("institute") || sub.contains("cohort")) {			
			Scanner sc = new Scanner(new File(loc));
			
			Category c=new Category();
			
			int count=0;
			
			ArrayList<String> allProps=new ArrayList<>();
			
			while(sc.hasNextLine()) {
				String current = sc.nextLine();	

				if(count == 0) {
					String currentarr[]=current.split(",");
					c.setType(currentarr[0]);
					
					for(int i=1; i<currentarr.length; i++) {
						allProps.add(currentarr[i]);
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
					
					String currentarr[] = current.split(",");

					HashMap<String,List<String>> hmap=new HashMap<>();
					
					c.setName(currentarr[0]);
					
					// prints individual name and individual type
					sbans.append("<strong>" + c.getName() + " " + c.getType() + " Does not Exist </strong><br /><br />");
					
					for(int i=1; i<currentarr.length; i++) {
						if(currentarr.length >= i) {
							String arr[]=currentarr[i].split("; ");
							
							List<String> values=Arrays.asList(arr); // gets potential list of values for a property
							
							// add property values to report if they are not empty
							if(!values.contains("") || values.size() > 1) {
								sbans.append("- Property " + allProps.get(i-1) + " has been added with values: <br />");
	
								for(int k=0; k<values.size(); k++) {
									values.set(k, values.get(k).replace('$', ','));
								}
															
								for(String value : values) {
									// Check if property is of type int
									if(INT_PROPERTIES.contains(allProps.get(i-1))) {
										// If value is not an int add a warning
										if(!isInteger(value)) {
											warnings.add("- WARNING: Property " + allProps.get(i-1) + " received value '" + value + "'  but expects a value of type 'int' <br />");
											sbans.append("<br />");
										}
										else {
											sbans.append("&emsp; - " + value + "<br /><br />");
							
											/*** CHECKING FOR WARNINGS ***/
											// check for shortened value error
											if(value.length() == 1) {
												warnings.add("- WARNING: Property " + allProps.get(i-1) + " has value of '" + value + "', was this intended? <br />");
											}
											// check for abbreviations error
											else if(value.length() == 2 && value.contains(".")) {
												warnings.add("- WARNING: Property " + allProps.get(i-1) + " has value of '" + value + "', please avoid abbreviations <br />");
											}
										}
									}
									// Check if property is of type boolean
									else if(BOOLEAN_PROPERTIES.contains(allProps.get(i-1))) {
										// If value is not a boolean add a warning
										if(!isBoolean(value)) {
											warnings.add("- WARNING: Property " + allProps.get(i-1) + " received value '" + value + "'  but expects a value of type 'boolean' <br />");
											sbans.append("<br />");
										}
										else {
											sbans.append("&emsp; - " + value + "<br /><br />");
							
											/*** CHECKING FOR WARNINGS ***/
											// check for shortened value error
											if(value.length() == 1) {
												warnings.add("- WARNING: Property " + allProps.get(i-1) + " has value of '" + value + "', was this intended? <br />");
											}
											// check for abbreviations error
											else if(value.length() == 2 && value.contains(".")) {
												warnings.add("- WARNING: Property " + allProps.get(i-1) + " has value of '" + value + "', please avoid abbreviations <br />");
											}
										}
									}
									else {
										sbans.append("&emsp; - " + value + "<br /><br />");
						
										/*** CHECKING FOR WARNINGS ***/
										// check for shortened value error
										if(value.length() == 1) {
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
							
							hmap.put(allProps.get(i-1), values);
						}
					}
					
					/*** ADDING WARNINGS TO REPORT ***/
					if(!warnings.isEmpty()) {
						sbans.append("******************************************************************* <br />");
						
						sbans.append("<strong> ALERT: " + warnings.size() + " warning(s) found </strong><br />");
						
						for(String warning : warnings) {
							sbans.append(warning + "<br />");
						}
						
						sbans.append("******************************************************************* <br /><br />");
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
	
	// Checks if input is an integer
	public boolean isBoolean(String input) {
		if(input.toLowerCase().equals("true") || input.toLowerCase().equals("false")) {
			return true;
		}
		return false;
	}
	
}