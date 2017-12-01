package Utilities;

import java.io.InputStream;

// Global constants to be used 

public class Constants {
	
	// WIKI Location
	public static final String WIKI = "http://organicdatacuration.org/enigma_sandbox";
	public static final String WIKI_INDEX = "http://organicdatacuration.org/enigma_sandbox/index.php/";
	
	
	// Ontology NameSpace
	public static final String ONTOLOGY_NS = "https://w3id.org/enigma#";
	
	// Ontology Files
	public static final InputStream COHORT_ONTOLOGY = Constants.class.getResourceAsStream("/CohortOntology.owl");	
	public static final InputStream ORGANIZATION_ONTOLOGY = Constants.class.getResourceAsStream("/OrganizationOntology.owl");
	public static final InputStream PERSON_ONTOLOGY = Constants.class.getResourceAsStream("/PersonOntology.owl");
	public static final InputStream PROJECT_ONTOLOGY = Constants.class.getResourceAsStream("/ProjectOntology.owl");
	public static final InputStream SCANNER_ONTOLOGY = Constants.class.getResourceAsStream("/ScannerOntology.owl");
	public static final InputStream WORKING_GROUP_ONTOLOGY = Constants.class.getResourceAsStream("/WorkingGroupOntology.owl");
	
}
