package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import dbo.WeatherZoneDao;
import model.WeatherZone;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun; 

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;

public class WeatherZoneOutputBuilder {

	public static final List<String> blueList = new ArrayList<String>();
	public static final List<String> greenList = new ArrayList<String>();
	public static final List<String> magentaList = new ArrayList<String>();
	
	static{
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
	
	public void buildOutputFile(String station, String zones, String keywords, String fileNameOut, HashMap additionalZones, String fileName) {
		if (keywords == null) 
			keywords = "";
		//if ( additionalZones == null )
			additionalZones = new HashMap();
		if(fileNameOut == null || fileNameOut == "")
			fileNameOut = "testOut";
		//writeTxtFile(station, zones, keywords, fileNameOut,  additionalZones);
		writeWordFile(station, zones, keywords, fileNameOut,  additionalZones, fileName );
	}


	private void writeWordFile(String station, String zones, String keywords, String fileNameOut,  HashMap additionalZones, String fileName) {

		int count = 0;
		WeatherZoneDao dao = new WeatherZoneDao();
		List<WeatherZone> weatherZones = dao.getFilteredData(station, zones, keywords, additionalZones, fileName);

		additionalZones = new HashMap();
		try {
			FileOutputStream out = new FileOutputStream(new File("c:/dev/weatherMark/outputFiles/" + fileNameOut + ".docx"));
			XWPFDocument doc = new XWPFDocument(); 
			XWPFParagraph paragraph = doc.createParagraph();
		 	XWPFRun run=paragraph.createRun();
		 	run.setFontSize(10);
		    run.setFontFamily("Courier New");
		// 	run.setText("Test " + station + " Forecasts");
 		//	run.addBreak();
 		 //   run.addBreak();
			  
 			for (WeatherZone weatherZone : weatherZones) {
				count++;
				
				//station
 			    run.setText(weatherZone.getStation().replace("-", ""));
 			    run.addBreak();
 	 			
 			    //header
 			    String header = weatherZone.getHeader();
				String[] aHeader = header.split("\\|");
				for(int i = 0; i < aHeader.length; i++){
					run.setText(aHeader[i].replace("-", "").trim());
					if( i < aHeader.length -1)
						run.addBreak();
				}	

				//1st timestamp
				run.setText(weatherZone.getStationTimestamp().replace("-", "").trim());
				run.addBreak();
		 		
				//zone codes
				run.setText(weatherZone.getZoneCodes().replace("|", "").replace("-", "").trim());
	 			run.addBreak();
	 			
	 			//zones
	 			String weatherZoneText = weatherZone.getZones();
	 			System.out.println("weatherZoneText: " + weatherZoneText );
	 			
	 			//////////// 
				if(weatherZoneText.equals(zones.toUpperCase()+ "- | ")){		
 		 			//colorize
					run = paragraph.createRun();	
				 	run.setFontSize(10);
				    run.setFontFamily("Courier New");
					run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW); 				  

					run.setText(weatherZoneText.replace("|",  ""));
					run.addBreak();
				}else if (weatherZoneText.indexOf(zones.toUpperCase() + "-") > -1){
 					int startIndex = weatherZoneText.indexOf(zones.toUpperCase() + "-");
					int endIndex = (zones.toUpperCase() + "-").length() + startIndex;
					
					String preKey = weatherZoneText.substring(0, weatherZoneText.indexOf(zones.toUpperCase() + "-"));
					String postKey = weatherZoneText.substring(endIndex, weatherZoneText.length());
					String keyword = weatherZoneText.substring(startIndex, endIndex);
					
 					//do pre
					if(preKey.indexOf("|") > -1){
						String[] aPreKey = preKey.split("\\|");
						for ( int i = 0; i < aPreKey.length; i++){
							run.setText(aPreKey[i]);
							if (i < aPreKey.length-1 )
								run.addBreak();
						}
					}else
						run.setText(preKey);
					
					//colorize
					run = paragraph.createRun();	
				 	run.setFontSize(10);
				    run.setFontFamily("Courier New");
					run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW); 				  
					
					if(keyword.indexOf("|") > -1){
						String[] aKeyword = keyword.split("\\|");
						for ( int i = 0; i< aKeyword.length; i++){
							run.setText( aKeyword[i]);
							run.addBreak();
						}
					}else
						run.setText(  keyword);
					
					//set back to default 
					run = paragraph.createRun();	
				 	run.setFontSize(10);
				    run.setFontFamily("Courier New");
					
				 
					if(postKey.indexOf("|") > -1){
						String[] aPostKey = postKey.split("\\|");
						for ( int i = 0; i< aPostKey.length; i++){
							run.setText(aPostKey[i].trim());
							if (i < aPostKey.length-1 )
								run.addBreak();
						}
					}else
						run.setText(postKey.trim());
				
				}
	 			
		 		////////////
				 //run.addBreak();
 				
				run = paragraph.createRun();	
			 	run.setFontSize(10);
			    run.setFontFamily("Courier New");
				run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW); 				  
 				run.setText(weatherZone.getStationTimestamp().trim());
				run.addBreak();

				//set back to default 
				run = paragraph.createRun();	
			 	run.setFontSize(10);
			    run.setFontFamily("Courier New");

				String forecast = weatherZone.getForecast();
				
	 		 	//try to truncate weatherZoneText as of specified day
			 			
			 	String truncateByDay = "TUESDAY";
			 			
			 	if(truncateByDay != "")
			 		forecast = truncateByDay(forecast, truncateByDay); 
			 	//
			 			
				
				
				run = processForecast(run, paragraph, forecast, keywords);
				
				run.setText("$$");
 			    
 			    run.addBreak();
 			    run.addBreak();
  	 		}	
 			/*String zout = zones;	
	
 			if (additionalZones!= null){
	 			Set<String>keys = (Set)additionalZones.keySet();
		 		for(String key: keys){
		 		    zout = zout +  ", "+ additionalZones.get(key) ;
		 		}
	 			}
 			*/
 	 		run.setText("# of " + zones+ " found: " + count);
			
			doc.write(out);
		    out.close();
		} catch (IOException e) {
		}
	}
	
	
	private XWPFRun processForecast(XWPFRun run, XWPFParagraph paragraph, String forecast, String keywords){
		
		String[] aForecast = forecast.split("\\n");
		for(int i = 0; i < aForecast.length; i++){
//			System.out.println("forecast row: " + aForecast[i]);
			 
			run = colorize(run, paragraph, aForecast[i], keywords.toUpperCase(),  STHighlightColor.CYAN);
		
			/*for(String greenListElement : greenList)
				if(run.toString().contains(greenListElement.toUpperCase()))
					run = colorizeExisting(run, paragraph, run.toString(), greenListElement.toUpperCase(),  STHighlightColor.GREEN);	
		
			for(String blueListElement : blueList)
				if(run.toString().contains(blueListElement.toUpperCase()))
					run = colorizeExisting(run, paragraph, run.toString(), blueListElement.toUpperCase(),  STHighlightColor.BLUE);	
		
			for(String magentaListElement : magentaList)
				if(run.toString().contains(magentaListElement.toUpperCase() ))
					run = colorizeExisting(run, paragraph, run.toString(), magentaListElement.toUpperCase(),  STHighlightColor.MAGENTA);
		*/
		}	
		return run;
	}
		
	private XWPFRun colorize(XWPFRun run, XWPFParagraph paragraph, String forecastRow, String keywords,  STHighlightColor.Enum color){
		
		System.out.println("forecast row: " + forecastRow);
		
		if(keywords != null && keywords != "" && forecastRow.contains(keywords.toUpperCase())){
			
			int startIndex = forecastRow.indexOf(keywords);
			int endIndex = keywords.length() + startIndex;
			
			String preKey = forecastRow.substring(0, forecastRow.indexOf(keywords));
			String postKey = forecastRow.substring(endIndex, forecastRow.length());
			String keyword = forecastRow.substring(startIndex, endIndex);
//	
			//do pre
			run.setText(preKey);
			
			//colorize
			run = paragraph.createRun();	
		 	run.setFontSize(10);
		    run.setFontFamily("Courier New");
			run.getCTR().addNewRPr().addNewHighlight().setVal(color); 				  
			run.setText(keyword);
			
			//set back to default 
			run = paragraph.createRun();	
		 	run.setFontSize(10);
		    run.setFontFamily("Courier New");
				
			run.setText(postKey);
			
		}else{
			run.setText(forecastRow);
		}
		run.addBreak();
	
		return run;
	}

	
	private String truncateByDay(String text, String dayToEnd){
		String result = "";
		
		try{
				switch (dayToEnd){
			
				case "FRIDAY":
					result = text.substring(0,text.indexOf(".SATURDAY"));
					break;
				
				case "SATURDAY":
					result = text.substring(0,text.indexOf(".SUNDAY"));
					break;
				
				case "SUNDAY":
					result = text.substring(0,text.indexOf(".MONDAY"));
					break;
				
				case "MONDAY":
					result = text.substring(0,text.indexOf(".TUESDAY"));
					break;
				
				case "TUESDAY":
					result = text.substring(0,text.indexOf(".WEDNESDAY"));
					break;
				
				case "WEDNESDAY":
					result = text.substring(0,text.indexOf(".THURSDAY"));
					break;
				
				default:
					result = text;
					break;
				
				}
			}catch(Exception e){
				System.out.println("Exception truncating by day, returning entire string..." + e.getMessage());
				result = text;
			}	
		
 		
		return result;
		
	}

	
	private XWPFRun colorizeExisting(XWPFRun run, XWPFParagraph paragraph, String forecastRow, String keywords,  STHighlightColor.Enum color){
		List<XWPFRun>runList = paragraph.getRuns();
		forecastRow = run.toString();
		paragraph.removeRun(runList.size() - 1);
 		
		System.out.println("existing forecast row: " + forecastRow);
		
		if(keywords != null && keywords != "" && forecastRow.contains(keywords.toUpperCase())){
			
			int startIndex = forecastRow.indexOf(keywords);
			int endIndex = keywords.length() + startIndex;
			
			String preKey = forecastRow.substring(0, forecastRow.indexOf(keywords));
			String postKey = forecastRow.substring(endIndex, forecastRow.length());
			String keyword = forecastRow.substring(startIndex, endIndex);

			//do pre
			run = paragraph.createRun();	

			run.setFontSize(10);
		    run.setFontFamily("Courier New");
			run.setText(preKey);
			
			//colorize
			run = paragraph.createRun();	
		 	run.setFontSize(10);
		    run.setFontFamily("Courier New");
			run.getCTR().addNewRPr().addNewHighlight().setVal(color); 				  
			run.setText(keyword);

			//set back to default 
			run = paragraph.createRun();	
		 	run.setFontSize(10);
		    run.setFontFamily("Courier New");
			run.setText(postKey);
			
	//	}else{
	//		run.setText(forecastRow);
		}
	
		run.addBreak();
	
		return run;
	}

	
	public static void main(String[] args){
		WeatherZoneOutputBuilder builder = new WeatherZoneOutputBuilder();
		
		HashMap testMap = new HashMap(){};
		testMap.put("0",  "WESTERN UNION");
		testMap.put("1",  "BRONX");
		
		
		builder.writeWordFile("KOKX", "EASTERN UNION", "HIGHS IN THE MID 30S", "testOut", testMap, "Baxter-Hutzel-srrs-op.txt");
		
	}
}
