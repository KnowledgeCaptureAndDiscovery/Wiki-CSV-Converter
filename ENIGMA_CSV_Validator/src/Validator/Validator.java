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
	
	public String getValidationReport(String loc, String output) throws Exception {
		StringBuilder sbans = new StringBuilder();
		String sub = loc.substring(loc.lastIndexOf("/") + 1, loc.length());
		
		if(sub.contains("project") || sub.contains("workinggroup")) {			
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
					sbans.append(c.getName() +" "+c.getType()+" Does not Exist"+"\n\n");
				}
				else if(count>=4) {
					// if blank line continue and set property to null
					if(current.equals(",")) {	
						sbans.append("\n");
						prop=null;
						continue;
					}
					else {
						if(prop==null) {
							prop=currentarr[0];
						}

						if(hmap.containsKey(prop)) {
							ArrayList<String> currentAl = hmap.get(prop);
							currentAl.add(currentarr[1]);
							
							sbans.append(currentarr[1]+"\n");
							
							hmap.put(prop, currentAl);
							
							/*** CHECKING FOR WARNINGS ***/
							// check for shortened value error
							if(currentarr[1].length() == 1) {
								warnings.add("- WARNING: Property " + prop + " has value of '" + currentarr[1] + "', was this intended? \n");
							}
							// check for abbreviations error
							else if(currentarr[1].length() == 2 && currentarr[1].contains(".")) {
								warnings.add("- WARNING: Property " + prop + " has value of '" + currentarr[1] + "', please avoid abbreviations \n");
							}
						}
						else {
							ArrayList<String> currentAl=new ArrayList<>();
							
							if(currentarr.length > 1) {
								sbans.append("Property " + prop + " has been added with values: \n");
								currentAl.add(currentarr[1]);
								
								for(String value : currentAl) {
									sbans.append(value + "\n");

									/*** CHECKING FOR WARNINGS ***/
									// check for shortened value error
									if(value.length() == 1) {
										warnings.add("- WARNING: Property " + prop + " has value of '" + value + "', was this intended? \n");
									}
									// check for abbreviations error
									else if(currentarr[1].length() == 2 && currentarr[1].contains(".")) {
										warnings.add("- WARNING: Property " + prop + " has value of '" + currentarr[1] + "', please avoid abbreviations \n");
									}
								}
			
								hmap.put(prop, currentAl);
							}
							/*** CHECKING FOR EMPTY STRING WARNING ***/
							else {
								warnings.add("- WARNING: Property " + currentarr[0] + " contains an empty value and will not be added \n");
							}
						}
					}
				}
				count++;
			}
			
			/*** ADDING WARNINGS TO REPORT ***/
			if(!warnings.isEmpty()) {
				sbans.append("******************************************************************* \n");
				
				sbans.append("ALERT: " + warnings.size() + " warning(s) found \n");
				
				for(String warning : warnings) {
					sbans.append(warning + "\n");
				}
				
				sbans.append("******************************************************************* \n\n");
			}
			
			File f = new File(output + "contentinfo.txt");
			FileWriter fw = new FileWriter(f);
			fw.write(sbans.toString());
			fw.close();
			sc.close();
			
			return output + "contentinfo.txt"; 
			
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
					sbans.append(c.getName() + " " + c.getType() + " Does not Exist" + "\n\n");
					
					for(int i=1; i<currentarr.length; i++) {
						if(currentarr.length >= i) {
							String arr[]=currentarr[i].split("; ");
							
							List<String> values=Arrays.asList(arr); // gets potential list of values for a property
							
							// add property values to report if they are not empty
							if(!values.contains("") || values.size() > 1) {
								sbans.append("Property " + allProps.get(i-1) + " has been added with values: \n");
	
								for(int k=0; k<values.size(); k++) {
									values.set(k, values.get(k).replace('$', ','));
								}
															
								for(String value : values) {
									sbans.append(value + "\n\n");
					
									/*** CHECKING FOR WARNINGS ***/
									// check for shortened value error
									if(value.length() == 1) {
										warnings.add("- WARNING: Property " + allProps.get(i-1) + " has value of '" + value + "', was this intended? \n");
									}
									// check for abbreviations error
									else if(value.length() == 2 && value.contains(".")) {
										warnings.add("- WARNING: Property " + allProps.get(i-1) + " has value of '" + value + "', please avoid abbreviations \n");
									}
								}
							}
							
							/*** CHECKING FOR EMPTY STRING WARNING ***/
							if(values.contains("")) {
								// check for empty value error
								warnings.add("- WARNING: Property " + allProps.get(i-1) + " contains an empty value and will not be added \n");
							}
							
							hmap.put(allProps.get(i-1), values);
						}
					}
					
					/*** ADDING WARNINGS TO REPORT ***/
					if(!warnings.isEmpty()) {
						sbans.append("******************************************************************* \n");
						
						sbans.append("ALERT: " + warnings.size() + " warning(s) found \n");
						
						for(String warning : warnings) {
							sbans.append(warning + "\n");
						}
						
						sbans.append("******************************************************************* \n\n");
					}
					
					sbans.append("------------------------------------------------------------------------------------------------\n\n\n\n");
					
				}
				count++;
			}
			
			File f = new File(output + "contentinfo.txt");
			FileWriter fw = new FileWriter(f);
			fw.write(sbans.toString());
			fw.close();
			sc.close();
			
			return output + "contentinfo.txt"; 
		}
		
		return "ERROR";	
	}
	
	public String getValidationReport2(String loc, String output) throws Exception {
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
						else {
							ArrayList<String> currentAl=new ArrayList<>();
							
							if(currentarr.length > 1) {
								sbans.append("- Property " + prop + " has been added with values: <br />");
								currentAl.add(currentarr[1]);
								
								for(String value : currentAl) {
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
}