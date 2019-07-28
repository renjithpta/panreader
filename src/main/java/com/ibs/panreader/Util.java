package com.ibs.panreader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.opencsv.CSVWriter;


public class Util {
	
	private static Map<String, String> map = new HashMap<String, String>();
	
	public static void showFiles(File[] files) throws IOException {
		
		String pan ="";
		String result ="";
		String fileContent ="";
		
	    for (File file : files) {
	        if (file.isDirectory()) {
	            
	            showFiles(file.listFiles()); // Calls same method again.
	        } else {
	            
	            result=readPdfFile(file.getAbsolutePath());
	            if(result.contains("PART A")) {
	            	
	            	fileContent = extractPanAndAmount(result);
	            	 pan = (fileContent.split(",")[0]).trim();
	            	 if(map.get(pan) == null ||pan.equalsIgnoreCase( (map.get(pan).split(",")[0]).trim())) {
	            	  map.put(pan, fileContent);
	            	 }else {
	            	   map.put(pan, fileContent + " ," +map.get(pan) ); 
	            	 }
	            }
	            if(result.contains("PART B")) {
	            	
	            	fileContent = extractPartB(result);
	            	 pan = (fileContent.split(",")[0]).trim();
	            	 if(map.get(pan) != null) {
	            		 
	            		 map.put(pan, map.get(pan) + "," +fileContent.split(",")[1]);
	            		 
	            	 }else {
	            		 
	            		 map.put(pan, fileContent.split(",")[1]);
	            	 }
	            	
	            }
	            
	        }
	    }
	    
	   
	}
	
	public static String readPdfFile(String fileName) {
		
		  PdfReader reader;
		  String result ="";

	        try {

	            reader = new PdfReader(fileName);

	            // pageNumber = 1
	            result = PdfTextExtractor.getTextFromPage(reader, 1);

	           
	            reader.close();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
           return result;
	}
	
	public static boolean validatePAN(String s1) {
		Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

		Matcher matcher = pattern.matcher(s1);
		if (matcher.matches()) {
		
		return true;
		}
		else
		return false;
		}
	
	public static String extractPanAndAmount(String text) {
		
		String data[] = text.split("\n");
		boolean prevPanLine = false;
		String pan="";
		String amount ="";
		
		
		for(String str : data) {
			
			if(pan.length() == 0 && str.contains("PAN of the Employee")) {
				
				prevPanLine = true;
			}else if(prevPanLine) {
				
				
				str = str.substring(str.lastIndexOf(" "));
				pan = str.trim();
	
				if(validatePAN(pan))prevPanLine = false;
			}
			
			if(amount.length() == 0 && 
					str.contains("Total (Rs.)")) {
				
				str = str.substring(str.indexOf(")")+ 1);
				
				str = str.substring(0, str.indexOf(".00") + 3);
				amount = str.trim();
				
			}
			 
			
		}
		
	   return pan.trim() + "," + amount.trim();
	}
	
	public static String extractPartB(String text) {
		
		
		String data[] = text.split("\n");
		String amount2 ="";
		boolean prevPanLine = false;
		String pan="";
		for(String str : data) {

			if(pan.length() == 0 && str.contains("PAN of the Employee")) {
				prevPanLine= true;
			}else if(prevPanLine ) {
				
				
				str = str.substring(str.lastIndexOf(" "));
				pan = str.trim();
				if(validatePAN(pan))prevPanLine = false;
			}
			
			
			if(amount2.length() == 0 && str.contains("Salary as per provisions contained in section 17(1)")) {
				amount2 = str.substring(str.lastIndexOf(")")+1);
				
			}
		
		}
		
		return pan.trim() + "," + amount2.trim();
	}

	
	public static void makeCSV(String filePath) throws IOException {
		
		 FileWriter csvWriter = new FileWriter(filePath+"PANREPORT.csv");
		 CSVWriter writer = new CSVWriter(csvWriter); 
	     writer.writeNext(new String[] { "Pan Number", "Amount1", "Amount2" }); 
	     List<String[]> data = new ArrayList<String[]>(); 
		 for (Map.Entry<String,String> entry : map.entrySet()) {
			 data.add(entry.getValue().split(","));
		 }
		 writer.writeAll(data); 
		 writer.close(); 
	}
}
