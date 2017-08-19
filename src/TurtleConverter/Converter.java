package TurtleConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
	
	ConvertFunction(args[0],args[1]);
	
}


public static void ConvertFunction(String loc,String output) throws Exception
{
	StringBuilder sbans=new StringBuilder();
	String sub=loc.substring(loc.lastIndexOf("/")+1,loc.length());
	String firstpart=loc.substring(0,loc.lastIndexOf("/"))+"/";
	if(sub.contains("project")||sub.contains("workinggroup"))
	{
	if(wiki!=null)
	{
		wiki.removeAll();
	}
	wiki=ModelFactory.createOntologyModel();
	
	Scanner sc=new Scanner(new File(loc));
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
			sbans.append(c.getName() +" "+c.getType()+" Does not Exist"+"\n\n");
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
					if(currentarr.length>1){
						sbans.append("Property "+prop+" has been added with values: \n");
						currentAl.add(currentarr[1]);
						
						for(String h:currentAl)
							sbans.append(h+"\n\n");
						
						hmap.put(prop, currentAl);
					}
				}
			}
		}
		
		count++;
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
//	File f=new File(output+"contentinfo.txt");
//	FileWriter fw=new FileWriter(f);
//	fw.write(sbans.toString());
//	fw.close();
	
	
	//exporting now
	exportRDFFile(output+c.getName()+".ttl", wiki);
	}
	
	
	else if(sub.contains("person")||sub.contains("institute")||sub.contains("cohort"))
	{
		
		if(wiki!=null)
		{
			wiki.removeAll();
		}
		wiki=ModelFactory.createOntologyModel();
		
		Scanner sc=new Scanner(new File(loc));
		Category c=new Category();
		int count=0;
		ArrayList<String> allProps=new ArrayList<>();
		
		while(sc.hasNextLine())
		{
			String current=sc.nextLine();			
			if(count==0)
			{
				String currentarr[]=current.split(",");
				c.setType(currentarr[0]);
				for(int i=1;i<currentarr.length;i++)
					allProps.add(currentarr[i]);
			}
			else if(count>0)
			{
				
			if(current.contains("\""))
			{
				String tempcurr="";
				String doublequote="";
				for(int i=0;i<current.length();i++)
				{
					if(current.charAt(i)!='\"')
					{
						tempcurr+=current.charAt(i);
						
					}
					else if(current.charAt(i)=='\"')
					{
						int j=i+1;
					
						while(j<current.length() && current.charAt(j)!='\"')
						{
							doublequote+=current.charAt(j);
							j++;
						}
						doublequote=doublequote.replace(',', '$');

						tempcurr+=doublequote;
						doublequote="";
						i=j;
					}
				}
				current=tempcurr;
			}

			
				if(wiki!=null)
				{
					wiki.removeAll();
				}
				String currentarr[]=current.split(",");
				wiki=ModelFactory.createOntologyModel();
				
				HashMap<String,List<String>> hmap=new HashMap<>();
				c.setName(currentarr[0]);
				sbans.append(c.getName() +" "+c.getType()+ " Does not Exist"+"\n\n");
				for(int i=1;i<currentarr.length;i++)
				{
					if(currentarr.length>=i)
					{
						String arr[]=currentarr[i].split("; ");
						List<String> ar=Arrays.asList(arr);
						sbans.append("Property "+allProps.get(i-1)+" has been added with values: \n");
						for(int k=0;k<ar.size();k++)
							ar.set(k, ar.get(k).replace('$', ','));
						for(String h:ar)
							sbans.append(h+"\n\n");
						hmap.put(allProps.get(i-1),ar);
					}
				}
				
				//export begins from here:
				classIsaClass(c.getType()+"_CLASS",c.getName().toUpperCase());

				//export for properties
				for(String x:hmap.keySet())
				{
					for(String y:hmap.get(x))
					{
						DataProps(HAS_PROPERTY+x,c.getName(),y,XSDDatatype.XSDstring);
					}	
				}
				sbans.append("------------------------------------------\n\n");
				
				//exporting now
				exportRDFFile(output+c.getName()+".ttl", wiki);
				
				
			}
			
			
			count++;
		}
//		File f=new File(output+"contentinfo.txt");
//		FileWriter fw=new FileWriter(f);
//		fw.write(sbans.toString());
//		fw.close();
		



		
	}
	

	
	
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
	String x="",y="";
	for(int i=0;i<classpart.length();i++)
		if(classpart.charAt(i)!=' ')
			x+=classpart.charAt(i);
	for(int i=0;i<indvpart.length();i++)
		if(indvpart.charAt(i)!=' ')
			y+=indvpart.charAt(i);
	
	 OntClass c21 = wiki.createClass(WIKI+x);
     c21.createIndividual(WIKI+y);
}
private static void DataProps(String dataprop,String resourcepart,String propextracted,XSDDatatype x)
{
	String xyx="";
	for(int i=0;i<resourcepart.length();i++)
		if(resourcepart.charAt(i)!=' ')
			xyx+=resourcepart.charAt(i);
	
	OntProperty propSelec22;
    propSelec22 = wiki.createDatatypeProperty(dataprop);
    Resource orig22 = wiki.getResource(WIKI+encode(xyx));
    wiki.add(orig22, propSelec22,propextracted,x);
}
}
