package Utilities;

// Global constants to be used 

public class Constants {
	
	// WIKI Location
	public static final String WIKI = "http://organicdatacuration.org/enigma";
	public static final String WIKI_INDEX = "http://organicdatacuration.org/enigma/index.php/";
	
	
	// Ontology NameSpace
	public static final String ONTOLOGY_NS = "https://w3id.org/enigma#";
	
	// Ontology Files	
	public static final String COHORT_ONTOLOGY = Constants.class.getResource("/CohortOntology.owl").getPath();	
	public static final String ORGANIZATION_ONTOLOGY = Constants.class.getResource("/OrganizationOntology.owl").getPath();
	public static final String PERSON_ONTOLOGY = Constants.class.getResource("/PersonOntology.owl").getPath();
	public static final String PROJECT_ONTOLOGY = Constants.class.getResource("/ProjectOntology.owl").getPath();
	public static final String SCANNER_ONTOLOGY = Constants.class.getResource("/ScannerOntology.owl").getPath();
	public static final String WORKING_GROUP_ONTOLOGY = Constants.class.getResource("/WorkingGroupOntology.owl").getPath();
	
}
