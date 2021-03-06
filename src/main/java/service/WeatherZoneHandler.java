package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dbo.WeatherZoneDao;
import model.WeatherZone;
import utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class WeatherZoneHandler {

	public static final String EMPTY_STRING = "";
	public static final String SPACE = " ";
	public static final String END_OF_SECTION_DELIMITER = "$$";
	public static final String NEW_LINE_CHAR = "\n";
	public static final String END_OF_LINE_DELIMITER = "-";
	public static final int MIN_STATION_ID_LENGTH = 10;
	public static final int MAX_STATION_ID_LENGTH = 20;
	public static final String SPACE_PIPE_SPACE = " | ";
	public static final String TRIPLE_DOT = "...";
	private BufferedReader br; 
	
 	String line = EMPTY_STRING;
	String currentStation = EMPTY_STRING;
	String currentHeader = EMPTY_STRING;
	String station = EMPTY_STRING;
	Logger slf4jLogger = LoggerFactory.getLogger("WeatherZoneHandler");
	
	public void doWeatherZoneDataLoad(String station) {
		this.station = station.toUpperCase();
		HashMap<String, ArrayList<WeatherZone>> weatherZoneData = doDataRead();
		WeatherZoneDao weatherZoneDao = new WeatherZoneDao();
		weatherZoneDao.writeWeatherZoneData(weatherZoneData);
	}

	private HashMap<String, ArrayList<WeatherZone>> doDataRead() {
		HashMap<String, ArrayList<WeatherZone>> weatherZoneDataMap = new HashMap<String, ArrayList<WeatherZone>>();
		try{
			File[] inputFiles = FileUtil.getInputFiles();
			for (File file : inputFiles) {
				ArrayList<WeatherZone> weatherZoneDataList = parseFile(file);
				slf4jLogger.info("WeatherZoneHandler.doDataRead():  " + file.getName());
				weatherZoneDataMap.put(file.getName(), weatherZoneDataList);
			}
		} catch (Exception e) {
			slf4jLogger.info("Exception reading.. " + e.getMessage()  );
		}
		return weatherZoneDataMap;
	}


	public void clearDatabase() {
		WeatherZoneDao weatherZoneDao = new WeatherZoneDao(); 
		weatherZoneDao.deleteTable(); 
	}
	
	
	
	public List<WeatherZone> getSampleData(String station, String zones, String keywords,   HashMap <String, String>additionalZones, String fileName) {
		WeatherZoneDao weatherZoneDao = new WeatherZoneDao(); 
 		List<WeatherZone> weatherZones = weatherZoneDao.getFilteredData(station, zones, keywords, additionalZones, fileName);
		return weatherZones;
	}
	
	
	private ArrayList<WeatherZone> parseFile(File file) {

		ArrayList<WeatherZone> weatherZoneList = new ArrayList<WeatherZone>();
		StringBuilder rawDataBuffer = new StringBuilder();

		try {
			br = new BufferedReader(new FileReader(file));
			line = br.readLine();

			initializeBufferedReader();

			while (line != null) {

				if (line.equals(END_OF_SECTION_DELIMITER))
					line = br.readLine();

				boolean emptyLineTestResult = processEmptyLineForEOF();
				if (emptyLineTestResult)
					return weatherZoneList;

				WeatherZone weatherZone = processHeader();
				line = br.readLine();
				
				while (!line.contains(END_OF_SECTION_DELIMITER)) {
					rawDataBuffer.append(line);
					rawDataBuffer.append(NEW_LINE_CHAR);
					line = br.readLine();
				}

				weatherZone.setForecast(rawDataBuffer.toString());
				weatherZoneList.add(weatherZone);
				rawDataBuffer = new StringBuilder();
			}

		} catch (Exception e) {
			slf4jLogger.info("Exception reading.. " + e.getMessage()  );
		}
		return weatherZoneList;
	}
	//iterate thru lines until we get first line with a station
	private void initializeBufferedReader() {
		try {
			line = br.readLine();

			while (!line.contains(station)) {
				line = br.readLine();
			}
		} catch (IOException e) {
			slf4jLogger.info("IOException: " + e.getMessage()  );
		}
	}

	private WeatherZone processHeader() {
		boolean hasStation = false;
		WeatherZone weatherZone = new WeatherZone();

		try {
			
//			if(isZoneCode(line))
//				weatherZone.setZoneCodes(line);
			
			while (( !isZoneCode(line) &&  !line.contains(station)) || line.equals(EMPTY_STRING) )
				line = br.readLine();
		
			if (line.contains(station)) {
				hasStation = true;
				slf4jLogger.info("station updated to... : " + line);
				currentStation = line;
				weatherZone.setStation(currentStation);
				weatherZone = processHeaderWithStation(weatherZone);
			}

			if (weatherZone.getStation()  == null)
				weatherZone.setStation(currentStation);

			if (weatherZone.getHeader() == null)
				weatherZone.setHeader(currentHeader);

			// get the zones, omit the zone codes
			if (hasStation){
				line = br.readLine();
			}
			
			//jk 8.1.2017 - SHORT TERM FORECAST KPHI
			if(weatherZone.getZoneCodes() == null){	
				String zoneCodes = loadZoneCodes();
				if(zoneCodes.length() > 0){
					//note - setZoneCodes advances the br
					weatherZone.setZoneCodes(zoneCodes);
	 			}
			}
			
			// get the text zones
			StringBuilder zoneBuff = new StringBuilder();
			zoneBuff.append(line);
			zoneBuff.append(SPACE_PIPE_SPACE);

			while (true) {
				line = br.readLine();
				
				if (line.contains("PUBLIC INFORMATION") || line.contains("SPOTTER")
						|| weatherZone.getHeader().contains("PUBLIC INFORMATION")
						|| weatherZone.getHeader().contains("SPOTTER") ) {
					return weatherZone;
				}			
				
 
				if (line.length() >= 3 && line.substring(0, 3).matches("[0-9]{3}")) {
					weatherZone.setStationTimestamp(line);
 					slf4jLogger.info("station timestamp: " + line);
					break;
				}
 				zoneBuff.append(line);
				zoneBuff.append(SPACE_PIPE_SPACE);
			}

			weatherZone.setZones(zoneBuff.toString());
 			slf4jLogger.info("All the Zones: " + zoneBuff.toString());
 
		} catch (IOException e) {
 			slf4jLogger.info("exception: " + e.getMessage());
			
		}
		slf4jLogger.info("this weatherZone: " + weatherZone.toString());
		return weatherZone;
	}

	// When the header has the station it's a new section 
	private WeatherZone processHeaderWithStation(WeatherZone weatherZone){

		try{
			// since we have a station get the header
			StringBuilder headerBuff = new StringBuilder();
			line = br.readLine();
			
			headerBuff.append(line);
			headerBuff.append(SPACE_PIPE_SPACE);
	
			while (true) {
				line = br.readLine();
	
				if (isZoneCode(line) || isZoneCodeContinuation(line)) {
					slf4jLogger.info("calling this a zone code: " + line);
					weatherZone.setZoneCodes(line);
					break;
				}
				
				if (line.startsWith(TRIPLE_DOT)){
					slf4jLogger.info("we got at TRIPLE_DOT: " + line);
					break;
				}
		 
				if (line.length() >= 3 && line.substring(0, 3).matches("[0-9]{3}")) {
					weatherZone.setStationTimestamp(line);
					slf4jLogger.info("station timestamp: " + line);
			
					//'usually' the line after the 1st date for a batch with a new station is the zone code
					line = br.readLine();
					if (isZoneCode(line) || isZoneCodeContinuation(line)) {
						slf4jLogger.info("calling this a zone code too: " + line);
						weatherZone.setZoneCodes(line);
					}	
					break;
				}
				headerBuff.append(line);
				headerBuff.append(SPACE_PIPE_SPACE);
			}
			currentHeader = headerBuff.toString();
			slf4jLogger.info("Header updated to...: " + currentHeader);
			weatherZone.setHeader(currentHeader);

		}catch (Exception e){
			slf4jLogger.info("Exception processing header with station: " + e.getMessage());
		}
		return weatherZone;
	}

	 
	public String loadZoneCodes() {
		StringBuilder buff = new StringBuilder();
		try {
			line = br.readLine();
	 		
			while (line.startsWith(TRIPLE_DOT) || line.endsWith(TRIPLE_DOT))
				line = br.readLine();
			
			if (isZoneCode(line) || isZoneCodeContinuation(line)) {
 				slf4jLogger.info("current Zone Code: " + line);
				buff.append(line);
				loadZoneCodes();
			}
		} catch (IOException e) {
 			slf4jLogger.info("exception: " + e.getMessage());
		}
		return buff.toString();
	}
	

	private boolean isZoneCodeContinuation(String s) {
		if (s.contains(SPACE))
			return false;
 		
		String n = ".*[0-9].*";
		boolean a = s.endsWith("-");
		return s.matches(n) && a;
	}

	private boolean isZoneCode(String s) {
		if (s.contains(SPACE))
			return false;
		String n = ".*[0-9].*";
		String a = ".*[A-Z].*";
		return s.matches(n) && s.matches(a);
	}

	private boolean processEmptyLineForEOF() {
		if (line.equals(EMPTY_STRING)) {
			try {
				line = br.readLine();
				if (line == null || line.equals(EMPTY_STRING)) { 
					slf4jLogger.info("2 empty lines in a row...  Done!!");
					return true;
				}

			} catch (IOException e) {
				slf4jLogger.info("exception: " + e.getMessage());
			}
		}
		slf4jLogger.info("not 2 empty lines in a row...  Continuing!!");
		return false;
	}

	public List<WeatherZone> getSampleWeather() {
 		WeatherZoneDao weatherZoneDao = new WeatherZoneDao();
		List<WeatherZone> weatherZone = weatherZoneDao.getSampleData();
		return weatherZone;
	}

	/**
	 * stub for testing db connection, direct queries
	 */
	public static void main(String args[]) {
		WeatherZoneHandler weatherZoneHandler = new WeatherZoneHandler();
		weatherZoneHandler.doWeatherZoneDataLoad("KOKX");
 	}

}