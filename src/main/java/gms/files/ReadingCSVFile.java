package gms.files;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;


public class ReadingCSVFile {
	
	public static String getTokenFromCSV(String fileName, char separator){
		 CSVReader reader = null;
		 
		 String fileContents="";
	        try
	        {
	            //Get the CSVReader instance with specifying the delimiter to be used
	            reader = new CSVReader(new FileReader(fileName),separator);
	            String [] nextLine;
	           
	            //Read one line at a time
	            while ((nextLine = reader.readNext()) != null)
	            {
	                for(String token : nextLine)
	                {	               
	                    fileContents=fileContents+token;	                    
	                }
	            }
	           
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally {
	            try {
	                reader.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return fileContents;
	}
	
	public static String getTokenFromCSV(String fileName){
		 CSVReader reader = null;
		 
		 String fileContents="";
	        try
	        {
	            //Get the CSVReader instance with specifying the delimiter to be used
	            reader = new CSVReader(new FileReader(fileName));
	            String [] nextLine;
	           
	            //Read one line at a time
	            while ((nextLine = reader.readNext()) != null)
	            {
	                for(String token : nextLine)
	                {	               
	                    fileContents=fileContents+token;	                    
	                }
	            }
	           
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally {
	            try {
	                reader.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return fileContents;
	}
	
	public static String getLinesFromCSV(String fileName){
		 CSVReader reader = null;
		 int count=0;
		 
		 String fileContents="";
	        try
	        {
	            //Get the CSVReader instance with specifying the delimiter to be used
	            reader = new CSVReader(new FileReader(fileName));
	            String [] nextLine;
	           
	            //Read one line at a time
	            while ((nextLine = reader.readNext()) != null)
	            {
	                for(String token : nextLine)
	                {	               
	                    fileContents=fileContents+token;	   
	                }
	                count++;
	                System.out.println(fileContents);
	                fileContents="";
	                Thread.sleep(1000);
                   
	            }
	           
	           // System.out.println(count);
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally {
	            try {
	                reader.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return fileContents;
	}

	public static int getCountOfLinesFromCSV(String fileName){
		 CSVReader reader = null;
		 int count=0;
		 
		 String fileContents="";
	        try
	        {
	            //Get the CSVReader instance with specifying the delimiter to be used
	            reader = new CSVReader(new FileReader(fileName));
	            String [] nextLine;
	           
	            //Read one line at a time
	            while ((nextLine = reader.readNext()) != null)count++;       	                       
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally {
	            try {
	                reader.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return count;
	}

}
