package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import dbo.WeatherZoneDao;
import model.WeatherZone;

import org.apache.poi.xwpf.usermodel.XWPFDocument;   
import org.apache.poi.xwpf.usermodel.XWPFParagraph;   
import org.apache.poi.xwpf.usermodel.XWPFRun;   

public class WeatherZoneOutputBuilder {

	public void buildOutputFile(String productType, String zones, String keywords) {
	
		getData(productType, zones, keywords);
		
	}
	
	private void getData(String productType, String zones, String keywords){
		int count = 0;
		WeatherZoneDao dao = new WeatherZoneDao();
		List<WeatherZone>weatherZones = dao.getFilteredData(productType, zones, keywords);
		StringBuilder buff = new StringBuilder();
		String fileContent = "";

		try{
		    PrintWriter writer = new PrintWriter("./output/out.txt", "UTF-8");
		    writer.println("Test " + productType + " Forecasts");
		    writer.println("\n");
		    
		    
		    for(WeatherZone weatherZone : weatherZones){
		    	count++;
		    	writer.println(weatherZone.getProductType());
		    	String header = weatherZone.getHeader();
		    	header = header.replace("| ",  "\n");
		    	
		    	writer.println(header.trim());
 		    	writer.println(weatherZone.getStationTimestamp());
 		    	writer.println(weatherZone.getZones().replace("|", ""));
 		    	writer.println(weatherZone.getStationTimestamp());
 		    	writer.println(weatherZone.getForecast() + "$$");
 		    	writer.println("");
		    }
		    writer.println("\n");
		    writer.println("# of " + zones + " found: " + count);
		    
		    writer.close();
		
		    
		    /*
		    
			buff.append("Test " + productType + " Forecasts");
			buff.append("\n");
			buff.append("\n");
			buff.append("\n");
			
			for ( WeatherZone weatherZone : weatherZones){
				System.out.println(weatherZone.toString());
				buff.append(weatherZone.getProductType());
				buff.append("\n");
		
				buff.append(weatherZone.getHeader());
				buff.append("\n");
				
				buff.append(weatherZone.getZones());
				buff.append("\n");
				 
	
				buff.append(weatherZone.getStationTimestamp());
				buff.append("\n");
	
				buff.append(weatherZone.getForecast());
				buff.append("\n");
				buff.append("\n");
			}
			
			fileContent = buff.toString();
	
			    writer.println("The first line");
			    writer.println("The second line");
			    writer.close();
			} catch (IOException e) {
			   // do something
			}
		*/
		
		/*
		XWPFDocument document = new XWPFDocument();   
	    XWPFParagraph tmpParagraph = document.createParagraph();   
 
	    XWPFRun tmpRun = tmpParagraph.createRun();
	    
	    if (fileContent.contains("\n")) {
            String[] lines = fileContent.split("\n");
            tmpRun.setText(lines[0], 0); // set first line into XWPFRun
            for(int i=1;i<lines.length;i++){
                // add break and insert new text
            	tmpRun.addBreak();
            	tmpRun.setText(lines[i]);
            }
        } else {
        	tmpRun.setText(fileContent, 0);
        }
	  
	        
	    tmpRun.setText(fileContent);   
	    tmpRun.setFontSize(11);
	    tmpRun.setFontFamily("Courier New");
	      
	    File outFile = new File("./output/out.doc");
	    
	    FileOutputStream fos;
		try {
			fos = new FileOutputStream(outFile);
			document.write(fos);   
			fos.close();   
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
*/
		} catch (IOException e) {
			   // do something
			}
	
		}
		  
}
