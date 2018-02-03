package Validator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import APIquery.*;
import Data.DataEntry;
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
	volatile Ontology ontology;
	Category c = new Category();
	volatile ArrayList<String> allProps = new ArrayList<String>();
	volatile ArrayList<String> generalWarnings = new ArrayList<String>(); // General warnings not specific to a given csv entry
	volatile ArrayList<DataEntry> dataEntries = new ArrayList<DataEntry>(); 
	
	// Generates validation report
	public ArrayList<DataEntry> getValidationReport(String loc, String output) throws Exception {
		String sub = loc.substring(loc.lastIndexOf("/") + 1, loc.length());

		// Create API query object
		APIQuery api_query = new APIQuery(); 
		api_query.login();
		
		if(sub.contains("csv")) {	
            BufferedReader br = null;
            br = new BufferedReader((new InputStreamReader(new FileInputStream(loc), "ISO-8859-1")));   

			String first_line = br.readLine();
			readColHeaders(first_line); // Read the column headers and initial validation setup

			ExecutorService executor = Executors.newCachedThreadPool();
			
			String current = "";
			while((current = br.readLine()) != null) {
				Category temp = new Category();
				temp.setType(c.getType());
				executor.execute(new ValidationThread(current, api_query, temp, ontology, allProps, generalWarnings, dataEntries));
			}
			
			br.close();
			
			executor.shutdown();
			while(!executor.isTerminated()) {
				Thread.yield();
			}	
			
			return dataEntries;
		}	
		return null;	
	}
	
	private void readColHeaders(String current) {	
		current = current.replace("; ", ";");
		current = current.replace("_", " ");

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
				generalWarnings.add("- Undefined Property Error: Property <b><i>" + currentarr[i] + "</i></b> does not exist within the wiki and will be ignored! <br />");
			}
		}
	}
	
}