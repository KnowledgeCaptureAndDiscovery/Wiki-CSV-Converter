package Utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Global constants to be used 

public class Constants {
	
	// WIKI Location
	public static final String WIKI = "http://organicdatacuration.org/enigma_new";
	public static final String WIKI_INDEX = "http://organicdatacuration.org/enigma_new/index.php/";
	public static final String WIKI_ALL_PAGES = WIKI + "/api.php?&format=json&action=query&list=allpages&aplimit=100";
	
	
	// Ontology NameSpaces
	public static final List<String> ONT_NAMESPACES = 
		    Collections.unmodifiableList(Arrays.asList("https://w3id.org/enigma#", 
		    										   "http://www.w3.org/2006/vcard/ns#", 
		    										   "http://vivoweb.org/ontology/core#", 
		    										   "http://xmlns.com/foaf/0.1/"));
	
	// Ontology Files	
	public static final String COHORT_ONTOLOGY = Constants.class.getResource("/CohortOntology.owl").getPath();	
	public static final String ORGANIZATION_ONTOLOGY = Constants.class.getResource("/OrganizationOntology.owl").getPath();
	public static final String PERSON_ONTOLOGY = Constants.class.getResource("/PersonOntology.owl").getPath();
	public static final String PROJECT_ONTOLOGY = Constants.class.getResource("/ProjectOntology.owl").getPath();
	public static final String SCANNER_ONTOLOGY = Constants.class.getResource("/ScannerOntology.owl").getPath();
	public static final String WORKING_GROUP_ONTOLOGY = Constants.class.getResource("/WorkingGroupOntology.owl").getPath();
	
}
