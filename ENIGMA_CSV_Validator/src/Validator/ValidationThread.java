package Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import APIquery.APIQuery;
import Data.DataEntry;
import Ontology.Ontology;
import Utilities.Constants;

public class ValidationThread extends Thread {
	private String current;
	private APIQuery api_query;
	private Category c; 
	private Ontology ontology;
	private ArrayList<String> allProps;
	private ArrayList<String> generalWarnings;
	private ArrayList<DataEntry> dataEntries;
	
	public ValidationThread(String line, 
			APIQuery apiQuery, 
			Category cat, 
			Ontology ont,
			ArrayList<String> all_props, 
			ArrayList<String> general_warnings,
			ArrayList<DataEntry> dataEntries) {
		current = line;
		api_query = apiQuery;
		c = cat;
		ontology = ont;
		allProps = all_props;
		generalWarnings = general_warnings;
		this.dataEntries = dataEntries;
	}
	
	public void run() {
		current = current.replace("; ", ";");
		current = current.replace("_", " ");

		if(!current.equals("")) {			
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
			
			DataEntry dataEntry = new DataEntry(currentarr[0], c.getType());
			c.setName(currentarr[0]);
			
			String entity = c.getName();
			entity = entity.replaceAll(" ", "+");
			
			// Create API query object 
			if(api_query.doesExist(entity, c.getType())) {
				dataEntry.setHeader("Entry already exists. Your csv data will overwrite any existing values with the wiki");
				dataEntry.setName(hyperlink(c.getName()));
			}
			else {
				dataEntry.setHeader("Entry does not exist. A new wiki page will be created.");
			}
										
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
										
						ExecutorService executor = Executors.newCachedThreadPool();
						
						// Create separate thread for each cell value
						for(String value : values) {
							executor.execute(new ValueThread(value, ontology, api_query, allProps.get(i-1), notes, warnings, valid_values));
						}
						
						executor.shutdown();
						while(!executor.isTerminated()) {
							Thread.yield();
						}	
						
					}
					
					/*** CHECKING FOR EMPTY STRING NOTE ***/
					if(values.contains("")) {
						notes.add("- Empty value found for property <b><i>" + allProps.get(i-1) + "</i></b> and will not be added! <br />");
					}
					
					if(!valid_values.isEmpty()) {
						dataEntry.addPropAndValues(allProps.get(i-1), valid_values);
					}
					hmap.put(allProps.get(i-1), values);
				}
			}
			
			/*** ADDING WARNINGS TO REPORT ***/
			warnings.addAll(generalWarnings);
			if(!warnings.isEmpty()) {
				dataEntry.setWarningsHeader("<font color=red><b>ALERT: " + warnings.size() + " warning(s) found</b></font>");
				
				String warnings_str = "";
				warnings_str += "<font color=red>";
								
				for(String warning : warnings) {
					warnings_str += warning + "<br />";
				}
				
				warnings_str += "</font>";
				
				dataEntry.addWarnings(warnings_str);
			}
			/*** ADDING NOTES TO REPORT ***/
			if(!notes.isEmpty()) {
				dataEntry.setNotesHeader("<i>*" + notes.size() + " note(s) found</i>");
				
				String notes_str = "";
				for(String note : notes) {
					notes_str += note + "<br />";
				}
				
				dataEntry.addNotes(notes_str);
			}
			dataEntries.add(dataEntry);
		}
	}
	
	private String hyperlink(String value) {
		String value_asLink = value.replace(" ", "_");
		return "<a href=" + Constants.WIKI_INDEX+value_asLink + ">" + value + "</a>";
	}	
	
}
