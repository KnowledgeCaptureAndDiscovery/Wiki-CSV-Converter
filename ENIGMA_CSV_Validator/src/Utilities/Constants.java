package Utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Global constants to be used 

public class Constants {
	
	// WIKI Location
	public static final String WIKI = "http://organicdatacuration.org/enigma_new";
	public static final String WIKI_INDEX = "http://organicdatacuration.org/enigma_new/index.php/";
	public static final String WIKI_ALL_PAGES = WIKI + "/api.php?&format=json&action=query&list=allpages&aplimit=100";
	
	
	// Ontology NameSpaces
	public static final List<String> ONT_NAMESPACES = 
		    Collections.unmodifiableList(Arrays.asList("https://w3id.org/enigma#",
		    									       "http://purl.org/dc/terms/",
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
	public static final String ROLES_ONTOLOGY = Constants.class.getResource("/RolesOntology.owl").getPath();

	
	// Mapping of Inverse Object Properties
	public static final Map<String, String> INVERSE_PROPERTIES = initInverseMap();
    private static Map<String, String> initInverseMap() {
        Map<String, String> map = new HashMap<>();
        map.put("contributor", "contributor");
        map.put("author", "author");
        map.put("isProjectLeadOf", "hasProjectLead");
        map.put("isAffiliatedWith", "hasAffiliate");
        map.put("isMemberOfWorkingGroup", "hasWorkingGroupMember");
        map.put("isChairOfWorkingGroup", "hasWorkingGroupChair");
        map.put("isInvestigatorOf", "hasInvestigator");
        map.put("isPrincipalInvestigatorOf", "hasPrincipalInvestigator");

        return Collections.unmodifiableMap(map);
    }   
    
 // Mapping of Inverse Object Properties
 	public static final Map<String, String> ROLES_PROPERTIES = initRolesMap();
    private static Map<String, String> initRolesMap() {
    	Map<String, String> map = new HashMap<>();
        map.put("isDataAnalystOf", "hasDataAnalyst");
        map.put("isImagingDataAnalystOf", "hasImagingDataAnalyst");
        map.put("isGeneticDataAnalystOf", "hasGeneticDataAnalyst");
        map.put("isDataCollectorOf", "hasDataCollector");
        map.put("isImagingDataCollectorOf", "hasImagingDataCollector");
        map.put("isGeneticDataCollectorOf", "hasGeneticDataCollector");
        map.put("isProjectDeveloperOf", "hasProjectDeveloper");
        map.put("isDesignerOf", "hasDesigner");
        map.put("isFirstDraftContributorOf", "firstDraftContributor");
        map.put("isReviewerOf", "hasReviewer");

        map.put("hasDataAnalyst", "hasDataAnalyst");
        map.put("hasImagingDataAnalyst", "hasImagingDataAnalyst");
        map.put("hasGeneticDataAnalyst", "hasGeneticDataAnalyst");
        map.put("hasDataCollector", "hasDataCollector");
        map.put("hasImagingDataCollector", "hasImagingDataCollector");
        map.put("hasGeneticDataCollector", "hasGeneticDataCollector");
        map.put("hasProjectDeveloper", "hasProjectDeveloper");
        map.put("hasDesigner", "hasDesigner");
        map.put("hasFirstDraftContributor", "firstDraftContributor");
        map.put("hasReviewer", "hasReviewer");
        
        return Collections.unmodifiableMap(map);
    }
    	
}
