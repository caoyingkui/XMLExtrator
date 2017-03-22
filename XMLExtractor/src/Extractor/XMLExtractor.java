package Extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.Doc;
import javax.print.attribute.standard.RequestingUserName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.events.EntityReference;

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class XMLExtractor {
	
	String XMLFileName; // absolute file path 
	File XMLFile;       // file object
	DocumentBuilderFactory factory ;
	DocumentBuilder builder;
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("start\n");
		//XMLExtractor extractor = new XMLExtractor("/home/oliver/Downloads/PostLinks.xml");
		BufferedWriter writer = new BufferedWriter(new FileWriter("/home/oliver/Desktop/result.txt"));
		BufferedReader reader = new BufferedReader(new FileReader("/home/oliver/Downloads/PostLinks.xml"));
		String line;
		
		Pattern postPattern = Pattern.compile("PostId=\"([0-9]+)\"");//.[0-9]+.");
		
		Pattern relatePattern = Pattern.compile("RelatedPostId=\"([0-9]+)\"");
		
		Matcher matcher ;
		String postID;
		String relateID;
		int count = 0;
		while((line = reader.readLine()) != null)
		{
			if(line.contains("LinkTypeId=\"3\""))
			{
				if(count % 1000 == 0)
				{
					if(writer != null)
						writer.close();
					writer = new BufferedWriter(new FileWriter("/home/oliver/Desktop/result/" + count / 1000 + ".txt"));
				}
				count ++;
				System.out.println(line);
				matcher = postPattern.matcher(line);
				matcher.find();
				postID = matcher.group(1);
				//postID = matcher.group(1);
				matcher = relatePattern.matcher(line);
				matcher.find();
				relateID = matcher.group(1);
				
				writer.write(postID + "  " + relateID + "\n");
			}
		}
		
		writer.close();
		reader.close();
		System.out.println("end");;
	}

	public XMLExtractor(String fileAbsolutePath)
	{
		XMLFileName = fileAbsolutePath;
		XMLFile = new File(XMLFileName);
	}

	public Map<Integer , String> GetAttributes(String rowName , String attributeName)
	{
		Map<Integer , String> attributes = new HashMap<Integer , String>();
		try{
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			
			Document doc = (Document) builder.parse(XMLFile);
			
			NodeList nodeList  = doc.getElementsByTagName(rowName);
			if(nodeList != null)
			{
				int length = nodeList.getLength();
				String attribute;
				Element element;
				for(int i = 0 ; i < length ; i ++)
				{
					element = (Element) nodeList.item(i);
					attribute = element.getAttribute(attributeName);
					attributes.put(i , attribute);
				}
			}
			return attributes;
			
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public Map<String , String> GetAttributes(String rowName , String attributeName , String filterAttribute , String filterValue)
	{
		Map<String , String> attributes = new HashMap<String , String>();
		
		try{
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			
			Document document = builder.parse(XMLFile);
			
			NodeList nodeList = document.getElementsByTagName(rowName);
			if(nodeList != null)
			{
				int length = nodeList.getLength();
				int count = 0;
				Element element;
				String temp;
				String postID;
				String relatedPostID;
				for(int i = 0 ; i < length ; i++)
				{
					if(i% 100 == 0)
					{
						System.out.println("I am still alive!");
					}
					element = (Element)nodeList.item(i);
					temp = element.getAttribute(filterAttribute);
					if(temp.compareTo(filterValue) == 0)
					{
						postID = element.getAttribute("PostID");
						relatedPostID = element.getAttribute("RelatedPostID");
						attributes.put(postID , relatedPostID);
					}
				}
				return attributes;
			}
			else
			{
				return null;
			}
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
}




















