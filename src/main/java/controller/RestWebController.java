package controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import model.WeatherZone;
import service.WeatherZoneHandler;
import service.WeatherZoneOutputBuilder;

@RestController
public class RestWebController {
     
    List<WeatherZone> weatherZoneList = new ArrayList<WeatherZone>();
     
    @RequestMapping(value = "/getsampleweather", method = RequestMethod.POST)
    public String getSampleWeather(@RequestBody String param){
    	System.out.println("incoming 1: " );
    	WeatherZoneHandler handler = new WeatherZoneHandler();
      
    	System.out.println("buildOutputFile request: " + param);
    	Gson gson = new Gson();
    	    	
    	WeatherZone weatherZone = gson.fromJson(param, WeatherZone.class);
    
		weatherZoneList = handler.getSampleData(weatherZone.getStation(), weatherZone.getZones(), weatherZone.getKeyword(),  weatherZone.getadditionalzones(), weatherZone.getFilename());
		
		String result = buildSampleWeatherJSON(weatherZoneList);
    	return result;
    	
     }
     
    @RequestMapping(value="/doParse", method=RequestMethod.POST )	
    public void postSampleRequest(@RequestBody String param){
    	WeatherZoneHandler handler = new WeatherZoneHandler();
    	System.out.println("postSampleRequest incoming: " + param);
    	Gson gson = new Gson();
    	    	
    	WeatherZone weatherZone = gson.fromJson(param, WeatherZone.class);
     	handler.doWeatherZoneDataLoad(weatherZone.getStation());
        
    }
    
    @RequestMapping(value="/buildoutputfile", method=RequestMethod.POST)	
    public void buildOutputFile(@RequestBody String param){
    	WeatherZoneOutputBuilder builder = new WeatherZoneOutputBuilder();
    	
    	System.out.println("buildOutputFile request: " + param);
    	Gson gson = new Gson();
    	    	
    	WeatherZone weatherZone = gson.fromJson(param, WeatherZone.class);
    	builder.buildOutputFile(weatherZone.getStation(), weatherZone.getZones(), weatherZone.getKeyword(), weatherZone.getFileNameOut(), weatherZone.getadditionalzones(), weatherZone.getFilename() );
    }

    @RequestMapping(value="/clearDatabase", method=RequestMethod.POST)	
    public void clearDatabase(){
    	WeatherZoneHandler handler = new WeatherZoneHandler();
    	
    	System.out.println("clear database request: " );
    	    	
    	handler.clearDatabase();

    }

    
    private String buildSampleWeatherJSON(List<WeatherZone> weatherZoneList){

    	Gson gson = new Gson();
     	StringBuilder buff = new StringBuilder();
    	
    	buff.append("[ ");
    	 
    	//TODO - improve verifying size of list big enough to display
    	if(weatherZoneList.size() >= 3){
	    	for(int i = 0; i < 3; i++){
	    		buff.append( gson.toJson(weatherZoneList.get(i)));
	     		if( i < 2)
	    			buff.append(",");
	    	}
	    	buff.append(" ]");
    	}
	  	else if(weatherZoneList.size() == 2){
	    	for(int i = 0; i < 2; i++){
	    		buff.append( gson.toJson(weatherZoneList.get(i)));
	     		if(i<1)
	    			buff.append(",");
	    	}
	    	buff.append(" ]");
    	}
    	
    	else if(weatherZoneList.size() == 1){
     		buff.append( gson.toJson(weatherZoneList.get(0)));
  	    	buff.append(" ]");
    	}
    	System.out.println("jsonified buff: " + buff.toString());
	    return buff.toString();
     }
}