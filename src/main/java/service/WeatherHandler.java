package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dbo.WeatherDao;
import model.Weather;
import utils.FileUtil;


public class WeatherHandler {

	public static final String EMPTY_STRING = "";	
	public static final String END_OF_SECTION_DELIMITER = "$$";
	public static final String NEW_LINE_CHAR = "\n";
	public static final String END_OF_LINE_DELIMITER = "-";
	public static final int START_LINE = 10;	
	public static final int MIN_STATION_ID_LENGTH = 10;
	public static final int MAX_STATION_ID_LENGTH = 20;
	private BufferedReader br;	
	int lineNumberOfFile = 1; // assumption data of interest does not start until line 10 for standard files
	String line = EMPTY_STRING;
	
	
  	public void doWeatherDataLoad(){
		HashMap<String, ArrayList<Weather>> weatherData = doDataRead();
		WeatherDao weatherDao = new WeatherDao();
		weatherDao.writeWeatherData(weatherData);
	}
 
	 
	private HashMap<String, ArrayList<Weather>> doDataRead(){
		HashMap<String, ArrayList<Weather>> weatherDataMap = new HashMap<String, ArrayList<Weather>> (); 
		
	 	File[] inputFiles = FileUtil.getInputFiles();
		for(File file : inputFiles){
			
			ArrayList<Weather> weatherDataList = parseFile(file);
			weatherDataMap.put(file.getName(), weatherDataList);
			resyncForNextFile();
		}
		return weatherDataMap;
	}


	private ArrayList<Weather> parseFile(File file){
		
		ArrayList<Weather> weatherList = new ArrayList<Weather>();
		StringBuilder rawDataBuffer = new StringBuilder();
		
		try {
			br = new BufferedReader(new FileReader(file));
			line = br.readLine();
		    
			initializeBufferedReader();
	 	    
		    while (line != null ) {
		    	
		    	if (line.equals(END_OF_SECTION_DELIMITER))
		    		line = br.readLine();
		    	
		    	boolean emptyLineTestResult = processEmptyLineForEOF();
		    	if(emptyLineTestResult)
		    		return weatherList; 
		    				
		    	boolean specialResultTestResult = processForSpecialReport();
		    	if( specialResultTestResult	)
		    		continue;
		 
		    	Weather weather = processHeader();
		       
		    	line = br.readLine();
		        
		        while(!line.contains(END_OF_SECTION_DELIMITER)){
			
			        rawDataBuffer.append(line);
			        rawDataBuffer.append(NEW_LINE_CHAR);
			    	line = br.readLine();
		        }

		        System.out.println("raw data: " + rawDataBuffer.toString());
		        weather.setRawData(rawDataBuffer.toString());
		        weatherList.add(weather);
		        rawDataBuffer = new StringBuilder();
		    }
		    
		}catch(Exception e){
			System.out.println("Exception reading.. " + e.getMessage());
		} 
		return weatherList;
	}

	
 	
 	private void initializeBufferedReader(){
	
 		while(lineNumberOfFile < START_LINE){
	    	try {
				br.readLine();
			} catch (IOException e) { 
				e.printStackTrace();
			}
	    	lineNumberOfFile++;  
	    }
	 
 	}
 	
 	private Weather processHeader(){
 		Weather weather = new Weather();
 		
 		try{
	 		if(line.equals(EMPTY_STRING))
	 			line = br.readLine();
	    	System.out.println("station id: " + line);
	    	scrubLine();
 	    	weather.setStationId(line);
	        
	        line = br.readLine();
	        System.out.println("station location: " + line);
	        scrubLine();
	        weather.setStationLocation(line);
	        
	        line = br.readLine();
	        System.out.println("station timestamp: " + line);
	        weather.setStationTimestamp(line);
	
	        //TODO handle this..
	        weather.setSpecialInfo(EMPTY_STRING);
		} catch (IOException e) {
			e.printStackTrace();
		}
 		
 		return weather;
 	}

 	private boolean processEmptyLineForEOF(){
		if(line == EMPTY_STRING){
			try {
				line = br.readLine();
				if (line == null || line == EMPTY_STRING){  //2 empty lines in a row done
					System.out.println("2 empty lines in a row i think we're done");
					return true;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 		System.out.println("special report or some other thang...  skipping");
		return false;//continue;;
 	}
 	
 	
 	private boolean processForSpecialReport(){
			try{
				if (line.length() > MAX_STATION_ID_LENGTH || line.length() < MIN_STATION_ID_LENGTH){
					System.out.println("line length > 20 maybe a special report get to next section");
					while(!line.contains(END_OF_SECTION_DELIMITER)){	// get to next section
						line = br.readLine();
					}
					return true;
				}
	
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return false; //line ! empty, good to go
 	}

 	private void scrubLine(){
    	if(line.endsWith(END_OF_LINE_DELIMITER))
    		line = line.replace(END_OF_LINE_DELIMITER, EMPTY_STRING);
 	}
	private void resyncForNextFile(){

		lineNumberOfFile = 1;
		line = EMPTY_STRING;		
	}

	/**
	 * stub for testing db connection, direct queries
	 */
	public static void main(String args[]){
		WeatherHandler weatherHandler = new WeatherHandler();
		weatherHandler.doWeatherDataLoad();
		
	}

}
