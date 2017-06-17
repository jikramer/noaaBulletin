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
	String currentProduct = EMPTY_STRING;
	String currentHeader = EMPTY_STRING;
	String productType = EMPTY_STRING;

	public void doWeatherZoneDataLoad(String productType) {
		this.productType = productType.toUpperCase();
		HashMap<String, ArrayList<WeatherZone>> weatherZoneData = doDataRead();
		WeatherZoneDao weatherZoneDao = new WeatherZoneDao();
		weatherZoneDao.writeWeatherZoneData(weatherZoneData);
	}

	private HashMap<String, ArrayList<WeatherZone>> doDataRead() {
		HashMap<String, ArrayList<WeatherZone>> weatherZoneDataMap = new HashMap<String, ArrayList<WeatherZone>>();

		File[] inputFiles = FileUtil.getInputFiles();
		for (File file : inputFiles) {

			ArrayList<WeatherZone> weatherZoneDataList = parseFile(file);
			weatherZoneDataMap.put(file.getName(), weatherZoneDataList);
		}
		return weatherZoneDataMap;
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
					System.out.println("forecast line: " + line);
					rawDataBuffer.append(line);
					rawDataBuffer.append(NEW_LINE_CHAR);
					line = br.readLine();
				}

				weatherZone.setForecast(rawDataBuffer.toString());
				weatherZoneList.add(weatherZone);
				rawDataBuffer = new StringBuilder();
			}

		} catch (Exception e) {
			System.out.println("Exception reading.. " + e.getMessage());
		}
		return weatherZoneList;
	}

	private void initializeBufferedReader() {

		try {
			line = br.readLine();

			while (!line.contains(productType)) {
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private WeatherZone processHeader() {
		boolean hasProduct = false;
		WeatherZone weatherZone = new WeatherZone();

		try {
			if (line.equals(EMPTY_STRING))
				line = br.readLine();

			if (line.contains(productType)) {
				hasProduct = true;
				System.out.println("product updated to... : " + line);
				currentProduct = line;
				weatherZone.setProductType(currentProduct);

				// since we have a product get the header
				StringBuilder headerBuff = new StringBuilder();
				line = br.readLine();
				headerBuff.append(line);
				headerBuff.append(SPACE_PIPE_SPACE);

				while (true) {
					line = br.readLine();
					if (line.startsWith(TRIPLE_DOT))
						break;

					if (line.length() >= 3 && line.substring(0, 3).matches("[0-9]{3}")) {
						weatherZone.setStationTimestamp(line);
						System.out.println("station timestamp: " + line);
						break;
					}

					headerBuff.append(line);
					headerBuff.append(SPACE_PIPE_SPACE);
				}
				currentHeader = headerBuff.toString();
				System.out.println("Header updated to...: " + currentHeader);
				weatherZone.setHeader(currentHeader);
			}

			if (weatherZone.getProductType() == null)
				weatherZone.setProductType(currentProduct);

			if (weatherZone.getHeader() == null)
				weatherZone.setHeader(currentHeader);

			// get the zones, omit the zone codes
			if (hasProduct)
				line = br.readLine();
			skipZoneCodes();

			// get the text zones
			StringBuilder zoneBuff = new StringBuilder();
			zoneBuff.append(line);
			zoneBuff.append(SPACE_PIPE_SPACE);

			if (zoneBuff.toString().contains("PUBLIC INFORMATION") || zoneBuff.toString().contains("SPOTTER")
					|| weatherZone.getHeader().contains("PUBLIC INFORMATION")
					|| weatherZone.getHeader().contains("SPOTTER")) {
				zoneBuff = new StringBuilder();
				zoneBuff.append(weatherZone.getZoneCodes());
			}

			while (true) {
				line = br.readLine();
				System.out.println("line: " + line);
				if (line.length() >= 3 && line.substring(0, 3).matches("[0-9]{3}")) {
					weatherZone.setStationTimestamp(line);
					System.out.println("station timestamp: " + line);
					break;
				}

				zoneBuff.append(line);
				zoneBuff.append(SPACE_PIPE_SPACE);
			}

			if (zoneBuff.toString().contains("PUBLIC INFORMATION") || zoneBuff.toString().contains("SPOTTER")
					|| weatherZone.getHeader().contains("PUBLIC INFORMATION")
					|| weatherZone.getHeader().contains("SPOTTER")) {
				weatherZone.setZones(weatherZone.getZoneCodes());
			} else {
				weatherZone.setZones(zoneBuff.toString());
				System.out.println("zones: " + zoneBuff.toString());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(weatherZone.toString());	
		return weatherZone;
	}

	public void skipZoneCodes() {
		try {
			line = br.readLine();
			if (isZoneCode(line)) {
				System.out.println("zone code: " + line);
				skipZoneCodes();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isZoneCode(String s) {
		String n = ".*[0-9].*";
		String a = ".*[A-Z].*";
		return s.matches(n) && s.matches(a);
	}

	private boolean processEmptyLineForEOF() {
		if (line.equals(EMPTY_STRING)) {
			try {
				line = br.readLine();
				if (line == null || line.equals(EMPTY_STRING)) { // 2 empty
																	// lines in
																	// a row
																	// done
					System.out.println("2 empty lines in a row i think we're done");
					return true;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("not 2 empty lines in a row");
		return false;// continue;;
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
