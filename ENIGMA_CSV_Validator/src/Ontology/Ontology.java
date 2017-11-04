package Ontology;

import java.io.InputStream;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import Utilities.Constants;

// Class to encapsulate ontology files

public class Ontology {
	    
	private OntModel model;
    
	// Constructor, instantiates OntModel object with specified file
	public Ontology(String file) {
    	model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		loadModel(file);
	}

	public static void main(String[] args) {
		Ontology ontology = new Ontology(Constants.COHORT_ONTOLOGY);
		ontology.propertyExists("isCaseControl");
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
    public boolean propertyExists(String property) {    	
    	OntProperty obj_property = model.getOntProperty(Constants.ONTOLOGY_NS + property);
        DatatypeProperty data_property = model.getDatatypeProperty(Constants.ONTOLOGY_NS + property);
        
        if(obj_property == null && data_property == null) {
            System.out.println(property);
        	return false;
        }
        
        return true;
    }
    
}
