package Validator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import APIquery.*;
import Ontology.*;
import Report.ReportEntry;
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
	volatile ArrayList<ReportEntry> dataEntries = new ArrayList<ReportEntry>(); 
	ArrayList<String> allWikiEntities = new ArrayList<String>(); // All entities currently in wiki
	
	// Generates validation report
	public ArrayList<ReportEntry> getValidationReport(String loc, String output) throws Exception {
		String sub = loc.substring(loc.lastIndexOf("/") + 1, loc.length());

		// Create API query object
		APIQuery api_query = new APIQuery(); 
		api_query.login();
		api_query.listAllPages(Constants.WIKI_ALL_PAGES, allWikiEntities);
		
		if(sub.contains("csv")) {	
            BufferedReader br = null;
            br = new BufferedReader((new InputStreamReader(new FileInputStream(loc), "ISO-8859-1")));   

			String first_line = br.readLine();
			readColHeaders(first_line); // Read the column headers and initial validation setup
			
			String current = "";
			
			// Validate each row of the csv
			while((current = br.readLine()) != null) {
				Category temp = new Category();
				temp.setType(c.getType());
				CSVRow row = new CSVRow(current, allWikiEntities, temp, ontology, allProps, generalWarnings, dataEntries);
				row.validate();
			}
			
			br.close();
						
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
			
			String property = currentarr[i];
			
			// If property doesn't exist in the ontology
			if(!ontology.propertyExists(property)) {
				generalWarnings.add("- Undefined Property Error: Property <b><i>" + currentarr[i] + "</i></b> does not exist within the wiki and will be ignored! <br />");
			}
		}
	}
	
}