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
		String firstpart = loc.substring(0, loc.lastIndexOf("/")) + "/";
		
		if(sub.contains("project") || sub.contains("workinggroup")) {			
			Scanner sc = new Scanner(new File(loc));
			Category c = new Category();
			
			int count = 0;
			
			HashMap<String,ArrayList<String>> hmap = new HashMap<>();
			String prop = null;
		
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
					if(current.equals(",")) {	
						prop=null;
						continue;
					}
					else {
						if(prop==null) {
							prop=currentarr[0];
						}
						
						if(hmap.containsKey(prop)) {
							ArrayList<String> currentAl=hmap.get(prop);
							currentAl.add(currentarr[1]);
							hmap.put(prop, currentAl);
						}
						else {
							ArrayList<String> currentAl=new ArrayList<>();
							
							if(currentarr.length > 1) {
								sbans.append("Property " + prop + " has been added with values: \n");
								currentAl.add(currentarr[1]);
								
								for(String h:currentAl) {
									sbans.append(h+"\n\n");
								}
			
								hmap.put(prop, currentAl);
							}
						}
					}
				}
				count++;
			}
			
			File f = new File(output + "contentinfo.txt");
			FileWriter fw = new FileWriter(f);
			fw.write(sbans.toString());
			fw.close();
			
			return output + "contentinfo.txt"; 
			
		}	
		else if(sub.contains("person") || sub.contains("institute") || sub.contains("cohort")) {			
			Scanner sc=new Scanner(new File(loc));
			
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
	
					
					String currentarr[] = current.split(",");
										
					HashMap<String,List<String>> hmap=new HashMap<>();
					
					c.setName(currentarr[0]);
					
					sbans.append(c.getName() + " " + c.getType() + " Does not Exist" + "\n\n");
					
					for(int i=1; i<currentarr.length; i++) {
						if(currentarr.length >= i) {
							String arr[]=currentarr[i].split("; ");
							
							List<String> ar=Arrays.asList(arr);
							
							sbans.append("Property " + allProps.get(i-1) + " has been added with values: \n");
							
							for(int k=0; k<ar.size(); k++) {
								ar.set(k, ar.get(k).replace('$', ','));
							}
							
							for(String h : ar) {
								sbans.append(h + "\n\n");
							}
							
							hmap.put(allProps.get(i-1), ar);
						}
					}
					
					sbans.append("------------------------------------------\n\n");
					
				}
				count++;
			}
			
			File f = new File(output + "contentinfo.txt");
			FileWriter fw = new FileWriter(f);
			fw.write(sbans.toString());
			fw.close();
			
			return output + "contentinfo.txt"; 
		}
		
		return "ERROR";
		
		
	}
	
}