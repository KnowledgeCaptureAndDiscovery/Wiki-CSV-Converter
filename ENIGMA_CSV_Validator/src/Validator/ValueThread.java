package Validator;

import java.util.ArrayList;

import APIquery.APIQuery;
import Ontology.Ontology;
import Utilities.Constants;

public class ValueThread extends Thread {
	private String value;
	private Ontology ontology;
	private APIQuery api_query;
	private String prop;
	private ArrayList<String> notes;
	private ArrayList<String> warnings;
	private ArrayList<String> valid_values;
	
	public ValueThread(String val, 
			Ontology ont, 
			APIQuery apiQuery, 
			String property,
			ArrayList<String> notes_, 
			ArrayList<String> warnings_,
			ArrayList<String> validValues) {
		value = val;
		api_query = apiQuery;
		ontology = ont;
		prop = property;
		notes = notes_;
		warnings = warnings_;
		valid_values = validValues;
	}
	
	public void run() {
		// Format property for ontology query
		String property = prop;
		property = property.split(" ")[0];
		property = Character.toLowerCase(property.charAt(0)) + property.substring(1);
				
		if(!ontology.validType(api_query, property, value)) {		
			if(ontology.isObjectProp(property)) {
				notes.add("- NOTE: Property " + prop + " received value '" + value + "' a page for this value doesn't exist, so one will be created <br />");
				valid_values.add(value);
			}
			else {
				warnings.add("- WARNING: Incompatible Type Error – Property " + prop + " received value '" + value + "' but expects a value of type " + ontology.getDataRange(property) + ". Value will not be added! <br />");
			}
		}
		else {										
			/*** CHECKING FOR WARNINGS ***/
			// check for shortened value error
			if(value.length() == 1 && !isInteger(value)) {
				notes.add("- NOTE: Property " + prop + " has shortened value of '" + value + "', was this intended? <br />");
				valid_values.add(value);
			}
			// check for abbreviations error
			else if(value.length() == 2 && value.contains(".")) {
				warnings.add("- WARNING: Abbreviations Error – Property " + prop + " value '" + value + "' will not be added! <br />");
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
	
	private String hyperlink(String value) {
		String value_asLink = value.replace(" ", "_");
		return "<a href=" + Constants.WIKI_INDEX+value_asLink + ">" + value + "</a>";
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
	
}
