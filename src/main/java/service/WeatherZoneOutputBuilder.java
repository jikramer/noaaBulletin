package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;

import dbo.WeatherZoneDao;
import model.WeatherZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherZoneOutputBuilder {

	Logger slf4jLogger = LoggerFactory.getLogger("WeatherZoneOutputBuilder");

	public static final String EMPTY_SPACE = "";
	public static final List<String> blueList = new ArrayList<String>();
	public static final List<String> greenList = new ArrayList<String>();
	public static final List<String> magentaList = new ArrayList<String>();

	static {
		blueList.add("snow");
		blueList.add("freezing rain");
		blueList.add("sleet");
		blueList.add("freezing drizzle");
		blueList.add("hail");
		blueList.add("ice");
		blueList.add("ice pellets");
		greenList.add("thunderstorm");
		greenList.add("rain");
		greenList.add("drizzle");
		greenList.add("flood");
		greenList.add("flooding");
		magentaList.add("black ice");
	}

	public void buildOutputFile(String station, String zones, String keywords, String fileNameOut, HashMap<String, String> additionalZones, String fileName, String truncateDay) {

		slf4jLogger.info("in buildOutputFile");

		if (keywords == null)
			keywords = EMPTY_SPACE;
 
		additionalZones = new HashMap<String, String>();
		if (fileNameOut == null || fileNameOut == EMPTY_SPACE)
			fileNameOut = "I_am_an_unnamed_output_file";
 		writeWordFile(station, zones, keywords, fileNameOut, additionalZones, fileName, truncateDay);
	}

	private void writeWordFile(String station, String zones, String keywords, String fileNameOut, HashMap <String, String> additionalZones, String fileName, String truncateDay) {

		int count = 0;
		WeatherZoneDao dao = new WeatherZoneDao();
 		slf4jLogger.info("calling the dbo: ");
 		List<WeatherZone> weatherZones = dao.getFilteredData(station, zones, keywords, additionalZones, fileName);
 		slf4jLogger.info("back from dbo with [" + weatherZones.size() + "] weatherZones...");
 		additionalZones = new HashMap<String, String>();

 		try {
 			FileOutputStream out = new FileOutputStream(new File("c:/dev/weatherMark/output/" + fileNameOut + ".docx"));
			XWPFDocument doc = new XWPFDocument();
			XWPFParagraph paragraph = doc.createParagraph();
			XWPFRun run = paragraph.createRun();
			run.setFontSize(10);
			run.setFontFamily("Courier New");
 			run.addBreak();
			run.addBreak();

			slf4jLogger.info("writing word file: [" + fileNameOut + "]");

			for (WeatherZone weatherZone : weatherZones) {
				count++;
 				slf4jLogger.info("count: " + count);

				// station
				run.setText(weatherZone.getStation().replace("-", EMPTY_SPACE));
				run.addBreak();

				// header
				String header = weatherZone.getHeader();

				String[] aHeader = header.split("\\|");
				for (int i = 0; i < aHeader.length; i++) {
					run.setText(aHeader[i].replace("-", EMPTY_SPACE).trim());
					if (i < aHeader.length - 1)
						run.addBreak();
				}

				// 1st timestamp
				run.setText(weatherZone.getStationTimestamp().replace("-", EMPTY_SPACE).trim());
				run.addBreak();

				// zone codes
				if(weatherZone.getZoneCodes() != null){
					run.setText(weatherZone.getZoneCodes().replace("|", EMPTY_SPACE).replace("-", EMPTY_SPACE).trim());
					run.addBreak();
				}
				
				// zones
				if(weatherZone.getZones() != null){
					doWeatherZone(weatherZone, zones, paragraph, run);
				}		
				
				// highlighted timestamp
 				run = paragraph.createRun();
				run.setFontSize(10);
				run.setFontFamily("Courier New");
				run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW);
				run.setText(weatherZone.getStationTimestamp().trim());
				run.addBreak();

				// set format back to default
				run = paragraph.createRun();
				run.setFontSize(10);
				run.setFontFamily("Courier New");

				// forecast
				String forecast = weatherZone.getForecast();

				//truncate as of day
				if(truncateDay != null && truncateDay != EMPTY_SPACE)
					forecast = truncateByDay(forecast, truncateDay);

				run = processForecast(run, paragraph, forecast, keywords);

				run.setText("$$");

				run.addBreak();
				run.addBreak();
			}
			 
			run.setText("# of " + zones + " found: " + count);
 			doc.write(out);
			out.close();

 		} catch (IOException e) {
			slf4jLogger.info("IOException : " + e.getMessage());
		} catch (Exception e) {
			slf4jLogger.info("Exception : " + e.toString());
		}

	}
 
		
	private XWPFRun doWeatherZone(WeatherZone weatherZone, String zones, XWPFParagraph paragraph, XWPFRun run ){
		String weatherZoneText = weatherZone.getZones();
		slf4jLogger.info("weatherZoneText: " + weatherZoneText);
		
		////////////
		if (weatherZoneText.equals(zones.toUpperCase() + "- | ")) {
			// colorize
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setFontFamily("Courier New");
			run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW);

			run.setText(weatherZoneText.replace("|", EMPTY_SPACE).trim());
			run.addBreak();
		} else if (weatherZoneText.indexOf(zones.toUpperCase() + "-") > -1) {
			int startIndex = weatherZoneText.indexOf(zones.toUpperCase() + "-");
			int endIndex = (zones.toUpperCase() + "-").length() + startIndex;

			String preKey = weatherZoneText.substring(0, weatherZoneText.indexOf(zones.toUpperCase() + "-"));
			String postKey = weatherZoneText.substring(endIndex, weatherZoneText.length());
			String keyword = weatherZoneText.substring(startIndex, endIndex);

			// do pre
			if (preKey.indexOf("|") > -1) {
				String[] aPreKey = preKey.split("\\|");
				for (int i = 0; i < aPreKey.length; i++) {
					run.setText(aPreKey[i].trim());
					if (i < aPreKey.length - 1)
						run.addBreak();
				}
			} else
				run.setText(preKey.trim());

			// colorize
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setFontFamily("Courier New");
			run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW);

			if (keyword.indexOf("|") > -1) {
				String[] aKeyword = keyword.split("\\|");
				for (int i = 0; i < aKeyword.length; i++) {
					run.setText(aKeyword[i].trim());
					run.addBreak();
				}
			} else
				run.setText(keyword.trim());

			// set back to default
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setFontFamily("Courier New");

			if (postKey.indexOf("|") > -1) {
				String[] aPostKey = postKey.split("\\|");
				for (int i = 0; i < aPostKey.length; i++) {
					run.setText(aPostKey[i].trim());
					if (i < aPostKey.length - 1)
						run.addBreak();
				}
			} else
				run.setText(postKey.trim());
 		}
		return run;
 	}
		
		
	String truncateByDay(String forecast, String dayToTruncFrom) {
 		String rc = EMPTY_SPACE;
 		try{
			switch (dayToTruncFrom.toUpperCase()) {
 				case "FRIDAY":
					if(forecast.contains("SATURDAY"))
						rc = forecast.substring(0, forecast.indexOf("SATURDAY"));
					else
						rc = forecast;
					break;
		
				case "SATURDAY":
					if(forecast.contains("SUNDAY"))
						rc = forecast.substring(0, forecast.indexOf("SUNDAY"));
					else
						rc = forecast;
					break;
					
				case "SUNDAY":
					if(forecast.contains("MONDAY"))
						rc = forecast.substring(0, forecast.indexOf("MONDAY"));
					else
						rc = forecast;
		 			break;
					
				case "MONDAY":
					if(forecast.contains("TUESDAY"))
						rc = forecast.substring(0, forecast.indexOf("TUESDAY"));
					else
						rc = forecast;
					break;
					
				case "TUESDAY":
					if(forecast.contains("WEDNESDAY"))
						rc = forecast.substring(0, forecast.indexOf("WEDNESDAY"));
					else
						rc = forecast;
 					break;
					
				case "WEDNESDAY":
					if(forecast.contains("THURSDAY"))
						rc = forecast.substring(0, forecast.indexOf("THURSDAY"));
					else
						rc = forecast;
 					break;
				
				default:
					rc = forecast;
					break;
			}
		}catch(Exception e){
			slf4jLogger.info("Exception truncating by day: " + e.getMessage());
		}
  		return rc;
 	}

	private XWPFRun processForecast(XWPFRun run, XWPFParagraph paragraph, String forecast, String keywords) {
 		String[] aForecast = forecast.split("\\n");
		for (int i = 0; i < aForecast.length; i++) {
	 		run = colorizeRow(run, paragraph, aForecast[i], keywords.toUpperCase(), STHighlightColor.CYAN);
 	 	}
		return run;
	}

	private XWPFRun colorizeRow(XWPFRun run, XWPFParagraph paragraph, String forecastRow, String keywords, STHighlightColor.Enum color) {
 		slf4jLogger.info("forecast row: " + forecastRow);

 		try{
			//line contains a keyword to highlight.  go for it.
			if (keywords != null && keywords != EMPTY_SPACE && forecastRow.contains(keywords.toUpperCase())) {
	
				int startIndex = forecastRow.indexOf(keywords);
				int endIndex = keywords.length() + startIndex;
	
				String preKey = forecastRow.substring(0, forecastRow.indexOf(keywords));
				String postKey = forecastRow.substring(endIndex, forecastRow.length());
				String keyword = forecastRow.substring(startIndex, endIndex);
				
				// do pre
				run.setText(preKey);
	
				// colorize
				run = paragraph.createRun();
				run.setFontSize(10);
				run.setFontFamily("Courier New");
				run.getCTR().addNewRPr().addNewHighlight().setVal(color);
				run.setText(keyword);
	
				// set post keyword highlight rest of row back to default text
				run = paragraph.createRun();
				run.setFontSize(10);
				run.setFontFamily("Courier New");
	
				run.setText(postKey);
 			} 
			//nothing to highlight in this row
			else {
				run.setText(forecastRow);
			}
 			run.addBreak();
		}catch(Exception e){
			slf4jLogger.info("e: " + e.toString());
 		}
		return run;
	}

 
	public static void main(String[] args) {
		WeatherZoneOutputBuilder builder = new WeatherZoneOutputBuilder();

		HashMap testMap = new HashMap() {
		};
		testMap.put("0", "WESTERN UNION");
		testMap.put("1", "BRONX");

		builder.writeWordFile("KOKX", "EASTERN UNION", "HIGHS IN THE MID 30S", "testOut", testMap,
				"Baxter-Hutzel-srrs-op.txt", "MONDAY");

	}
}
