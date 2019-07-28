package com.ibs.panreader;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;


public class PanReader {
	
	static Logger log = Logger.getLogger(PanReader.class.getName());
	  
	
	public static void main(String[] args) 	
	{
		
		
		try {
			log.info("***********************execution starts***************");
			Properties prop = new Properties();
			prop.load(PanReader.class.getClassLoader().getResourceAsStream("app.properties"));
		    String form16Directory = System.getenv("FORM16_DIR");
		    if(form16Directory == null || form16Directory.length() == 0) {
		    	form16Directory =prop.getProperty("BASE_DIR");
		    }
		    String csvFilePath = System.getenv("CSV_FILE_PATH");
		    if(csvFilePath == null || csvFilePath.length() == 0) {
		    	csvFilePath =prop.getProperty("CSV_FILE_PATH");
		    }
		    File[] files = new File(form16Directory).listFiles();
		    log.info("***********************Base directry scan starts***************");
		    Util.showFiles(files);
		    log.info("***********************Base directry scan end***************");
		    Util.makeCSV(csvFilePath);
		    log.info("***********************Successfully  file created***************");
		}catch(IOException e) {
			
			  log.error("***********************Error ***************"+ e.getMessage().toString());
		}
		
	}

}
