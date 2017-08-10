package TurtleConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

class Category
{
	String Name;
	String type;
	HashMap<String,ArrayList<String>> hmap;
	public String getName() {
		return Name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setName(String name) {
		Name = name;
	}
	public HashMap<String, ArrayList<String>> getHmap() {
		return hmap;
	}
	public void setHmap(HashMap<String, ArrayList<String>> hmap) {
		this.hmap = hmap;
	}
	
}
public class Converter {
	
public static OntModel wiki;
public static String WIKI="https://wiki.org/Enigma#";
public static String HAS_PROPERTY="https://wiki.org/EnigmaProperty#";
    
public static void main(String[] args) throws Exception {
	
	ConvertFunction();
	
	
	
	
}

public static void ConvertFunction() throws Exception
{
	if(wiki!=null)
	{
		wiki.removeAll();
	}
	wiki=ModelFactory.createOntologyModel();
	
	Scanner sc=new Scanner(new File("/Users/Tirthmehta/Documents/workspace/WIKI/src/TurtleConverter/wiki.csv"));
	Category c=new Category();
	int count=0;
	HashMap<String,ArrayList<String>> hmap=new HashMap<>();
	String prop=null;
	
	while(sc.hasNextLine())
	{
	
		String current=sc.nextLine();
		String currentarr[]=current.split(",");
		if(count==0)
		{
			//setting the Name of the Category Instance
			c.setName(currentarr[1]);
		}
		else if(count==1)
		{
			//setting the Type of the Category Instance
			c.setType(currentarr[1]);
		}
		else if(count>=4)
		{
			if(current.equals(","))
			{	
				prop=null;
				continue;
			}
			else
			{
				if(prop==null)
					prop=currentarr[0];
				if(hmap.containsKey(prop))
				{
					ArrayList<String> currentAl=hmap.get(prop);
					currentAl.add(currentarr[1]);
					hmap.put(prop, currentAl);
				}
				else
				{
					ArrayList<String> currentAl=new ArrayList<>();
					currentAl.add(currentarr[1]);
					hmap.put(prop, currentAl);
				}
			}
		}
		
		count++;
	}
	//setting the hashMap of the Category Instance
	c.setHmap(hmap);

	System.out.println(c.getName());
	System.out.println(c.getType());
	for(String x:hmap.keySet())
	{
		System.out.print("prop "+x+" val ");
		ArrayList<String> check=hmap.get(x);
		for(String y:check)
			System.out.print(y+" ");
		System.out.println();	
	}
	
	
	//export begins from here:
	classIsaClass(c.getType().toUpperCase()+"_CLASS",c.getName().toUpperCase());

	
	//export for properties
	for(String x:hmap.keySet())
	{
		for(String y:hmap.get(x))
		{
			DataProps(HAS_PROPERTY+x,c.getName(),y,XSDDatatype.XSDstring);
		}	
	}
	
	//exporting now
	exportRDFFile("/Users/Tirthmehta/Documents/workspace/WIKI/src/TurtleConverter/Output.ttl", wiki);
	
	
}
private static void exportRDFFile(String outFile, OntModel model){
    OutputStream out;
    try {
        out = new FileOutputStream(outFile);
        model.write(out,"TURTLE");
        out.close();
    } catch (Exception ex) {
        System.out.println("Error while writing the model to file "+ex.getMessage());
    }
}
private static String encode(String name){
    name = name.replace("http://","");
    String prenom = name.substring(0, name.indexOf("/")+1);
    //remove tabs and new lines
    String nom = name.replace(prenom, "");
    if(name.length()>255){
        try {
            nom = MD5(name);
        } catch (Exception ex) {
            System.err.println("Error when encoding in MD5: "+ex.getMessage() );
        }
    }        

    nom = nom.replace("\\n", "");
    nom = nom.replace("\n", "");
    nom = nom.replace("\b", "");
    //quitamos "/" de las posibles urls
    nom = nom.replace("/","_");
    nom = nom.replace("=","_");
    nom = nom.trim();
    //espacios no porque ya se urlencodean
    //nom = nom.replace(" ","_");
    //a to uppercase
    nom = nom.toUpperCase();
    try {
        //urlencodeamos para evitar problemas de espacios y acentos
        nom = new URI(null,nom,null).toASCIIString();//URLEncoder.encode(nom, "UTF-8");
    }
    catch (Exception ex) {
        try {
            System.err.println("Problem encoding the URI:" + nom + " " + ex.getMessage() +". We encode it in MD5");
            nom = MD5(name);
            System.err.println("MD5 encoding: "+nom);
        } catch (Exception ex1) {
            System.err.println("Could not encode in MD5:" + name + " " + ex1.getMessage());
        }
    }
    return prenom+nom;
}
public static String MD5(String text) throws NoSuchAlgorithmException
	{
		MessageDigest md=MessageDigest.getInstance("MD5");
		md.update(text.getBytes());
		byte b[]=md.digest();
		StringBuffer sb=new StringBuffer();
		for(byte b1:b)
		{
			sb.append(Integer.toHexString(b1 & 0xff).toString());
		}
		return sb.toString();
		
	}
private static void classIsaClass(String classpart,String indvpart)
{
	 OntClass c21 = wiki.createClass(WIKI+classpart);
     c21.createIndividual(WIKI+indvpart);
}
private static void DataProps(String dataprop,String resourcepart,String propextracted,XSDDatatype x)
{
	OntProperty propSelec22;
    propSelec22 = wiki.createDatatypeProperty(dataprop);
    Resource orig22 = wiki.getResource(WIKI+encode(resourcepart));
    wiki.add(orig22, propSelec22,propextracted,x);
}
}
