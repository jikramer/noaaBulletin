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



@RestController
public class RestWebController {
     
    List<WeatherZone> weatherZoneList = new ArrayList<WeatherZone>();
     
    @RequestMapping(value = "/getallweather", method = RequestMethod.GET)
    public String getWeather(){
    	WeatherZoneHandler handler = new WeatherZoneHandler();
    	weatherZoneList = handler.getSampleWeather();

    	Gson gson = new Gson();
    	
    	//initially return single element
    	String jsonWeather = gson.toJson(weatherZoneList.get(0));
    	System.out.println("jsonified single row: " + jsonWeather);
    	return jsonWeather;
    }
     
    @RequestMapping(value="/postfilename", method=RequestMethod.POST )	
    public void postCustomer(@RequestBody String param){
    	WeatherZoneHandler handler = new WeatherZoneHandler();
    	System.out.println("incoming: " + param);
    	Gson gson = new Gson();
    	    	
    	WeatherZone weatherZone = gson.fromJson(param, WeatherZone.class);
    	
     	handler.doWeatherZoneDataLoad(weatherZone.getProductType());
     	
        
    }
}