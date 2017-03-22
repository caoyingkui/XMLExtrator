package Stackoverflow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Util.XMLUtil;
import Util.XMLUtil.*;

import com.google.common.collect.*;
public class QuestionParser {
	
	private String questionPath = "SOData/questions/posts.xml";
	Map<Integer , Integer> questionInfo = new HashMap<Integer , Integer>();
	
	public static void main(String[] args) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader("SOData/duplicatedQuestion/21.txt"));
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("SOData/questions/modefied.txt"));
		String line;
		String[] numbers;
		int id1;
		int id2;
		while((line = reader.readLine()) != null)
		{
			numbers = line.split("  ");
			id1 = Integer.parseInt(numbers[0]);
			id2 = Integer.parseInt(numbers[1]);
			
			if(id1 < id2)
			{
				System.out.println(id1 + "  " + id2);
				writer.write(id1 + "  " + id2 + "\n");
			}
		}
		reader.close();
		writer.close();
		/*QuestionParser parser = new QuestionParser();
		parser.getQuestionsBeyondThreshold((float)0);*/
	}
	
	public Map<Integer , Integer> extractQuestionScore()
	{
		Map<Integer , Integer> result = new HashMap<Integer , Integer>();
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(questionPath));
			
			String record ;
			Integer postID;
			int score;
			String postType;
			int count = 0;
			while((record = reader.readLine()) != null)
			{
				count ++;
				postType = XMLUtil.getPropertyValue(record, "PostTypeId");
				if(postType.compareTo("1") == 0 )
				{
					postID = Integer.parseInt(XMLUtil.getPropertyValue(record, "Id") );
					score = Integer.parseInt(XMLUtil.getPropertyValue(record, "Score"));
					result.put(postID, score);
				}
				if(count % 1000 == 0)
				{
					System.out.println("extractorQuestionScore:" + count);
				}
			}
			
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return result;
	}

    public Map<Integer , String> getSimilarQuestion()
    {
    	Map<Integer , String> result = new HashMap<Integer , String>();
    	try
    	{
    		BufferedReader reader ;
    		String line;
    		String[] questions;
    		int id1;
    		int id2;
    		for(int i = 1; i < 100 ; i ++)
    		{
    			reader = new BufferedReader(new FileReader("SOData/duplicatedQuestion/"+ i +".txt"));
    			while((line = reader.readLine()) != null)
    			{
    				questions = line.split("  ");
    				id1 = Integer.parseInt(questions[0]);
    				id2 = Integer.parseInt(questions[1]);
    				if(id1 > id2)
    				{
    					int temp = id1;
    					id1 = id2;
    					id2 = temp;
    				}
    				
    				if(result.containsKey(id1))
    				{
    					result.put(id1, result.get(id1) + " , " + id2);
    				}
    				else
    				{
    					result.put(id1 , id2 + "");
    				}
    				
    			}
    		}
    	}catch(Exception e)
    	{
    		System.out.println(e.getMessage() + "asdfasdfa");
    	}
    	
    	return result;
    }

    public boolean beyondThreshold(int id1 , int id2 , float threshold)
    {
    	boolean result = false;
    	
    	if(questionInfo.containsKey(id1) && questionInfo.containsKey(id2))
    	{
    		int score1 = questionInfo.get(id1);
    		int score2 = questionInfo.get(id2);
    		if(score1 > score2)
    		{
    			int temp = score1;
    			score1 = score2;
    			score2 = temp;
    		}
    		
    		if( score1 >0 && ( (score2 - score1) / (float) score2 )  > threshold)
    		{
    			result = true;
    		}
    	}
    	
    	return result;
    }
    
    public HashMultimap<Integer , Integer> getQuestionsBeyondThreshold(float threshold)
    {
    	HashMultimap result = HashMultimap.create();
    	try
    	{
        	questionInfo = extractQuestionScore();
    		BufferedWriter writer = new BufferedWriter(new FileWriter("SOData/result.txt"));
    		Map<Integer , String> simiQuestion = new HashMap<Integer , String>();
    		
    		simiQuestion = getSimilarQuestion();
    		
    		Iterator infoIterator = simiQuestion.entrySet().iterator();
    		Map.Entry<Integer , String> entry;
    		int id1;
    		String[] id2;
    		int count =0;
    		while(infoIterator.hasNext())
    		{
    			count ++;
    			entry = (Map.Entry<Integer, String>)infoIterator.next();
    			id1 = entry.getKey();
    			id2 = entry.getValue().split(" , ");
    			
    			for(int i = 0 ; i < id2.length ; i ++)
    			{
    				if(beyondThreshold(id1 , Integer.parseInt(id2[i]) , threshold))
    				{
    					result.put(id1, Integer.parseInt(id2[i]));
    					writer.write(id1 + "  " + id2[i] + "\n");
    				}
    			}
    			if(count % 1000 == 0)
    				System.out.println(count + "\n");
    		}
    		writer.close();
    	}catch(Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
    	
    	return result;
    	
    }
}





























