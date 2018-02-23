package Ontology;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.jena.ontology.EnumeratedClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import Utilities.Constants;
import APIquery.APIQuery;;

// Class to encapsulate ontology files

public class Ontology {
	    
	private OntModel model;
    
	// Constructor, instantiates OntModel object with specified file
	public Ontology(String ontFile) {
    	model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		loadModel(ontFile);
	}

	public static void main(String[] args) {
		APIQuery api_query = new APIQuery();
		api_query.login();
		ArrayList<String> allWikiEntities = new ArrayList<String>(); // All entities currently in wiki
		api_query.listAllPages(Constants.WIKI_ALL_PAGES, allWikiEntities);

		Ontology ontology = new Ontology(Constants.COHORT_ONTOLOGY);
		System.out.println(ontology.validType(allWikiEntities, "hasMsaxIQ", "100"));
	}
	
    // Load ontology model from file
    public void loadModel(String file) {
    	InputStream in = FileManager.get().open(file);
        if (in == null) {
        	throw new IllegalArgumentException( "File: " + file + " not found");
        }
          
        model.read(in, "");  
    }

    // Checks if property exists
    public boolean propertyExists(String property_str) {    	
    	OntProperty property = model.getOntProperty(Constants.ONTOLOGY_NS + property_str);
    	
        if(property == null) {
        	return false;
        }
        
        return true;
    }
    
    // Gets data range of property
    public String getDataRange(String property_str) {
    	OntProperty property = model.getOntProperty(Constants.ONTOLOGY_NS + property_str);
    	
    	EnumeratedClass e = null;
		ExtendedIterator<RDFNode> i = null;
					
		// Check if property range is a set
		if(property.getRange().asClass().isEnumeratedClass()) {
			e = property.getRange().asClass().asEnumeratedClass();
			i = e.getOneOf().iterator();
			     
			RDFNode prop = null;
			String prop_values = "{";
			
			while(i.hasNext()) {
				prop = i.next();
			    prop_values += prop.asLiteral().toString() + ", ";
			}
			prop_values = prop_values.substring(0, prop_values.length()-2) + "}";
			return prop_values;
		}
		else {
	    	return property.getRange().getLocalName();
		}
    	
    }
    
    // Checks if property value is consistent with type in ontology
    public boolean validType(ArrayList<String> allWikiEntities, String property_str, String property_val) {
    	OntProperty property = model.getOntProperty(Constants.ONTOLOGY_NS + property_str);
  	
        if(property == null) {
        	return false;
        }
        // Object Property Type Validation
        else if(property.isObjectProperty()) {   
    		if(allWikiEntities.contains(property_val)) {
    			return true;
    		}
    		return false;
        }
        // DataType Property Type Validation
        else if(property.isDatatypeProperty()) {
        	
			EnumeratedClass e = null;
			ExtendedIterator<RDFNode> i = null;
						
			// Check if property range is a set
			if(property.getRange().asClass().isEnumeratedClass()) {
				e = property.getRange().asClass().asEnumeratedClass();
				i = e.getOneOf().iterator();
				     
				RDFNode prop = null;
				String s = null;
				
				while(i.hasNext()) {
					prop = i.next();
				    s = prop.asLiteral().toString();

				    if(property_val.toLowerCase().equals(s.toLowerCase())) {
				    	return true;
				    }
				}
				return false;
			}
			// It is a defined data type
			else {
	        	String prop_type = property.getRange().getLocalName();
	        	
	        	if(prop_type.contains("positiveInteger") || prop_type.contains("Year")) {
	        		if(isPositiveInteger(property_val)) {
	        			return true;
	        		}
	        		return false;
	        	}
	        	else if(prop_type.contains("boolean")) {
	        		if(isBoolean(property_val)) {
	        			return true;
	        		}
	        		return false;
	        	}
	        	else if(prop_type.contains("double")) {
	        		if(isDouble(property_val)) {
	        			return true;
	        		}
	        		return false;
	        	}
	        	else if(prop_type.contains("nonNegativeInteger")) {
	        		if(isNonNegativeInteger(property_val)) {
	        			return true;
	        		}
	        		return false;
	        	}
	        	return true;
			}   
        }   
        
        return false;
    }
    
    // Checks if property is an object property
    public boolean isObjectProp(String property_str) {
    	OntProperty property = model.getOntProperty(Constants.ONTOLOGY_NS + property_str);
    	
    	if(property == null) {
    		return false;
    	}
    	else if(property.isObjectProperty()) {
    		 return true;
    	}
    	return false;
    }
    
    // Checks if input is a positive integer
 	private boolean isPositiveInteger(String input) {
 	    try {
 	        int integer = Integer.parseInt(input);
 	        if(integer > 0) {
 	 	        return true;
 	        }
 	        
 	        return false;
 	    }
 	    catch(NumberFormatException e) {
 	        return false;
 	    }
 	}
 	
 	// Checks if input is a non-negative integer
 	private boolean isNonNegativeInteger(String input) {
 	    try {
 	        int integer = Integer.parseInt(input);
 	        if(integer > -1) {
 	 	        return true;
 	        }
 	        
 	        return false;
 	    }
 	    catch(NumberFormatException e) {
 	        return false;
 	    }
 	}
 	
 	// Checks if input is a double
 	public boolean isDouble(String input) {
 	    try {
 	    	Double.parseDouble(input);
 	        return true;
 	    }
 	    catch(NumberFormatException e) {
 	        return false;
 	    }
 	}
 	
 	// Checks if input is an integer
 	private boolean isBoolean(String input) {
 		if(input.toLowerCase().equals("true") || input.toLowerCase().equals("false")) {
 			return true;
 		}
 		return false;
 	}
    
}
