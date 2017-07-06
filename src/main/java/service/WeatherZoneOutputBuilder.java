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
	
	public void buildOutputFile(String station, String zones, String keywords, String fileNameOut, HashMap additionalZones) {
		if (keywords == null) 
			keywords = "";
		if ( additionalZones == null )
			additionalZones = new HashMap();
		if(fileNameOut == null || fileNameOut == "")
			fileNameOut = "testOut";
		writeTxtFile(station, zones, keywords, fileNameOut,  additionalZones);
		writeWordFile(station, zones, keywords, fileNameOut,  additionalZones );
	}

	private void writeTxtFile(String station, String zones, String keywords, String fileNameOut, HashMap additionalZones) {
		int count = 0;
		WeatherZoneDao dao = new WeatherZoneDao();
		List<WeatherZone> weatherZones = dao.getFilteredData(station, zones, keywords, additionalZones);

		try {
			PrintWriter writer = new PrintWriter("./output/" + fileNameOut + ".txt", "UTF-8");
			writer.println("Test " + station + " Forecasts");
			writer.println("\n");

			for (WeatherZone weatherZone : weatherZones) {
				count++;
				writer.println(weatherZone.getStation());
				String header = weatherZone.getHeader();
				header = header.replace("| ", "\n");

				writer.println(header.trim());
				writer.println(weatherZone.getStationTimestamp());
				writer.println(weatherZone.getZoneCodes().replace("|", ""));
				writer.println(weatherZone.getZones().replace("|", ""));
				writer.println(weatherZone.getStationTimestamp());
				writer.println(weatherZone.getForecast() + "$$");
				writer.println("");
			}
			writer.println("\n");
			writer.println("# of " + zones + " found: " + count);

			writer.close();
		} catch (IOException e) {
		}
	}

	private void writeWordFile(String station, String zones, String keywords, String fileNameOut,  HashMap additionalZones) {

		int count = 0;
		WeatherZoneDao dao = new WeatherZoneDao();
		List<WeatherZone> weatherZones = dao.getFilteredData(station, zones, keywords, additionalZones);

		try {
			FileOutputStream out = new FileOutputStream(new File("./output/" + fileNameOut + ".docx"));
			XWPFDocument doc = new XWPFDocument(); 
			XWPFParagraph paragraph = doc.createParagraph();
		 	XWPFRun run=paragraph.createRun();
		 	run.setFontSize(11);
		    run.setFontFamily("Courier New");
		 	run.setText("Test " + station + " Forecasts");
 			run.addBreak();
 		    run.addBreak();
			  
 			for (WeatherZone weatherZone : weatherZones) {
				count++;
				 
 			    run.setText(weatherZone.getStation());
 			    run.addBreak();
 	 			 
 			    String header = weatherZone.getHeader();
				String[] aHeader = header.split("\\|");
				for(int i = 0; i < aHeader.length; i++){
					run.setText(aHeader[i].trim());
					if( i < aHeader.length -1)
						run.addBreak();
				}	

			 
				run.setText(weatherZone.getStationTimestamp());
				run.addBreak();
		 			 
				run.setText(weatherZone.getZoneCodes().replace("|", ""));
	 			run.addBreak();
	 			
	 			String weatherZoneText = weatherZone.getZones();

				Set<String>keys = (Set)additionalZones.keySet();
				List <String> xZones = new ArrayList<String>();
		 		for(String key: keys){
		 			xZones.add((String)additionalZones.get(key));
		 		}

	 			
	 			
				if(weatherZoneText.equals(zones.toUpperCase()+ "-  ")){		
 		 			//colorize
					run = paragraph.createRun();	
				 	run.setFontSize(11);
				    run.setFontFamily("Courier New");
					run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW); 				  

					run.setText(weatherZoneText);
				}else if (weatherZoneText.indexOf(zones.toUpperCase() + "-") > -1){
 					int startIndex = weatherZoneText.indexOf(zones.toUpperCase() + "-");
					int endIndex = (zones.toUpperCase() + "-").length() + startIndex;
					
					String preKey = weatherZoneText.substring(0, weatherZoneText.indexOf(zones.toUpperCase() + "-"));
					String postKey = weatherZoneText.substring(endIndex, weatherZoneText.length());
					String keyword = weatherZoneText.substring(startIndex, endIndex);
					
 					//do pre
					run.setText(preKey);
					
					//colorize
					run = paragraph.createRun();	
				 	run.setFontSize(11);
				    run.setFontFamily("Courier New");
					run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW); 				  
					run.setText(keyword);
					
					//set back to default 
					run = paragraph.createRun();	
				 	run.setFontSize(11);
				    run.setFontFamily("Courier New");
						
					run.setText(postKey);
	 			}
	 			
				for (String z : xZones){
					
					if(weatherZoneText.equals(z.toUpperCase()+ "-  ")){		
	 		 			//colorize
						run = paragraph.createRun();	
					 	run.setFontSize(11);
					    run.setFontFamily("Courier New");
						run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW); 				  

						run.setText(weatherZoneText);
					}else if (weatherZoneText.indexOf(z.toUpperCase() + "-") > -1){
	 					int startIndex = weatherZoneText.indexOf(z.toUpperCase() + "-");
						int endIndex = (z.toUpperCase() + "-").length() + startIndex;
						
						String preKey = weatherZoneText.substring(0, weatherZoneText.indexOf(z.toUpperCase() + "-"));
						String postKey = weatherZoneText.substring(endIndex, weatherZoneText.length());
						String keyword = weatherZoneText.substring(startIndex, endIndex);
						
	 					//do pre
						run.setText(preKey);
						
						//colorize
						run = paragraph.createRun();	
					 	run.setFontSize(11);
					    run.setFontFamily("Courier New");
						run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW); 				  
						run.setText(keyword);
						
						//set back to default 
						run = paragraph.createRun();	
					 	run.setFontSize(11);
					    run.setFontFamily("Courier New");
							
						run.setText(postKey);
		 			}
 					
				}
		 			
	 			
				run.addBreak();

				
				run = paragraph.createRun();	
			 	run.setFontSize(11);
			    run.setFontFamily("Courier New");
				run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW); 				  
 				run.setText(weatherZone.getStationTimestamp());
				run.addBreak();

				//set back to default 
				run = paragraph.createRun();	
			 	run.setFontSize(11);
			    run.setFontFamily("Courier New");

				String forecast = weatherZone.getForecast();
				
				run = processForecast(run, paragraph, forecast, keywords);
				
				run.setText("$$");
 			    
 			    run.addBreak();
 			    run.addBreak();
  	 		}	
 			String zout = zones;	
	
 			if (additionalZones!= null){
	 			Set<String>keys = (Set)additionalZones.keySet();
		 		for(String key: keys){
		 		    zout = zout +  ", "+ additionalZones.get(key) ;
		 		}
	 			}
 			
 	 		run.setText("# of " + zout + " found: " + count);
			
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
		 	run.setFontSize(11);
		    run.setFontFamily("Courier New");
			run.getCTR().addNewRPr().addNewHighlight().setVal(color); 				  
			run.setText(keyword);
			
			//set back to default 
			run = paragraph.createRun();	
		 	run.setFontSize(11);
		    run.setFontFamily("Courier New");
				
			run.setText(postKey);
			
		}else{
			run.setText(forecastRow);
		}
		run.addBreak();
	
		return run;
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

			run.setFontSize(11);
		    run.setFontFamily("Courier New");
			run.setText(preKey);
			
			//colorize
			run = paragraph.createRun();	
		 	run.setFontSize(11);
		    run.setFontFamily("Courier New");
			run.getCTR().addNewRPr().addNewHighlight().setVal(color); 				  
			run.setText(keyword);

			//set back to default 
			run = paragraph.createRun();	
		 	run.setFontSize(11);
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
		
		
		builder.writeWordFile("KOKX", "EASTERN UNION", "HIGHS IN THE MID 30S", "testOut", testMap);
		
	}
}
