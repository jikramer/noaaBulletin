package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import dbo.WeatherZoneDao;
import model.WeatherZone;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun; 

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;

public class WeatherZoneOutputBuilder {

	public void buildOutputFile(String station, String zones, String keywords) {
		writeTxtFile(station, zones, keywords);
		writeWordFile(station, zones, keywords);
	}

	private void writeTxtFile(String station, String zones, String keywords) {
		int count = 0;
		WeatherZoneDao dao = new WeatherZoneDao();
		List<WeatherZone> weatherZones = dao.getFilteredData(station, zones, keywords);

		try {
			PrintWriter writer = new PrintWriter("./output/out.txt", "UTF-8");
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

	private void writeWordFile(String station, String zones, String keywords) {

		int count = 0;
		WeatherZoneDao dao = new WeatherZoneDao();
		List<WeatherZone> weatherZones = dao.getFilteredData(station, zones, keywords);

		try {
			FileOutputStream out = new FileOutputStream(new File("./output/out.docx"));
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
		 			 
				//colorize
				run = paragraph.createRun();	
			 	run.setFontSize(11);
			    run.setFontFamily("Courier New");
				run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW); 				  

				run.setText(weatherZone.getZones().replace("|", ""));
				run.addBreak();

				//set back to default 
				run = paragraph.createRun();	
			 	run.setFontSize(11);
			    run.setFontFamily("Courier New");
 				
				
				run.setText(weatherZone.getStationTimestamp());
				run.addBreak();
		 		
 				
				String forecast = weatherZone.getForecast();
				String[] aForecast = forecast.split("\\n");
				for(int i = 0; i < aForecast.length; i++){
					System.out.println("forcast row: " + aForecast[i]);
				
					if(keywords != null && keywords != "" && aForecast[i].contains(keywords)){
						
						int startIndex = aForecast[i].indexOf(keywords);
						int endIndex = keywords.length() + startIndex;
						
						String preKey = aForecast[i].substring(0, aForecast[i].indexOf(keywords));
						String postKey = aForecast[i].substring(endIndex, aForecast[i].length());
						String keyword = aForecast[i].substring(startIndex, endIndex);

						//do pre
						run.setText(preKey);
						
						//colorize
						run = paragraph.createRun();	
					 	run.setFontSize(11);
					    run.setFontFamily("Courier New");
						run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.MAGENTA); 				  
						run.setText(keyword);
						
						//set back to default 
						run = paragraph.createRun();	
					 	run.setFontSize(11);
					    run.setFontFamily("Courier New");
		 				
						run.setText(postKey);
						
						
					}else{
						run.setText(aForecast[i]);
					}
					run.addBreak();
					
				}	
			 
				run.setText("$$");
 			    
 			    run.addBreak();
 			    run.addBreak();
  	 		}	
 			
			run.setText("# of " + zones + " found: " + count);
			
			doc.write(out);
		    out.close();
		} catch (IOException e) {
 	}
}

	
	
 	
	
	public static void main(String[] args){
		
		WeatherZoneOutputBuilder builder = new WeatherZoneOutputBuilder();
		builder.writeWordFile("KOKX", "EASTERN UNION", "LOWS IN THE UPPER 20S");
		
	}
}
