package Data;

import java.util.ArrayList;
import java.util.HashMap;

// Class to encapsulate a row of data from spreadsheet 

public class DataEntry {
	
	private String name;
	private String type;
	private HashMap<String, ArrayList<String>> propValues;
	private ArrayList<String> notes;
	private ArrayList<String> warnings;
	
	// Constructor, intializes hashmap for properties and values, initializes data type and name
	public DataEntry(String name, String type) {
		this.name = name;
		this.type = type;
		propValues = new HashMap<String, ArrayList<String>>();
	}
	
	// Gets name of DataEntry
	public String getName() {
		return name;
	}
	
	// Sets name of DataEntry
	public void setName(String name) {
		this.name = name;
	}
	
	// Gets name of DataEntry
	public String getType() {
		return type;
	}
	
	// Adds property to hashmap if it does not yet exist
	public void addProp(String property) {
		if(!propValues.containsKey(property)) {
			ArrayList<String> values = new ArrayList<String>();
			propValues.put(property, values);
		}
	}
	
	// Adds value to hashmap using the specified property
	public void addValue(String property, String value) {
		if(propValues.containsKey(property)) {
			ArrayList<String> updatedValues = propValues.get(property);
			updatedValues.add(value);
			propValues.put(property, updatedValues);
		}
	}
	
	// Add the notes corresponding to the data entry
	public void addNotes(ArrayList<String> notes) {
		this.notes = notes;
	}
	
	// Add the warnings corresponding to the data entry
	public void addWarnings(ArrayList<String> warnings) {
		this.warnings = warnings;
	}
	
	// Gets the notes of the data entry
	public ArrayList<String> getNotes() {
		return notes;
	}
	
	// Gets the warnings of the data entry 
	public ArrayList<String> getWarnings() {
		return warnings;
	}
}
