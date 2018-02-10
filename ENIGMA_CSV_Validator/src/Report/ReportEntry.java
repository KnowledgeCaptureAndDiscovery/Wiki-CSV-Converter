package Report;

import java.util.ArrayList;
import java.util.HashMap;

// Class to encapsulate a row of data from spreadsheet 

public class ReportEntry {
	
	private String name;
	private String type;
	private HashMap<String, ArrayList<String>> propValues;
	private String notes;
	private String warnings;
	private String header;
	private String warningsHeader;
	private String notesHeader;
	
	// Constructor, intializes hashmap for properties and values, initializes data type and name
	public ReportEntry(String name, String type) {
		this.name = name;
		this.type = type;
		warnings = "";
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
	
	// Gets header of data entry
	public void setHeader(String header) {
		this.header = header;
	}
	
	// Sets header of data entry
	public String getHeader() {
		return header;
	}
	
	// Gets name of DataEntry
	public String getType() {
		return type;
	}
	
	// Adds property with linked values to hashmap
	public void addPropAndValues(String property, ArrayList<String> values) {
		if(!propValues.containsKey(property)) {
			propValues.put(property, values);
		}
	}
	
	// Add the notes corresponding to the data entry
	public void addNotes(String notes) {
		this.notes = notes;
	}
	
	// Add the warnings corresponding to the data entry
	public void addWarnings(String warnings) {
		this.warnings = warnings;
	}
	
	// Gets the notes of the data entry
	public String getNotes() {
		return notes;
	}
	
	// Gets the warnings of the data entry 
	public String getWarnings() {
		return warnings;
	}
	
	// Gets hmap for properties and values
	public HashMap<String, ArrayList<String>> getPropValMap() {
		return propValues;
	}
	
	// Sets warnings header
	public void setWarningsHeader(String warningsHeader) {
		this.warningsHeader = warningsHeader;
	}
	
	// Gets warnings header
	public String getWarningsHeader() {
		return warningsHeader;
	}
	
	// Sets notes header
	public void setNotesHeader(String notesHeader) {
		this.notesHeader = notesHeader;
	}
	
	// Gets notes header
	public String getNotesHeader() {
		return notesHeader;
	}
}
