package dbo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.Weather;
import utils.DBUtils;

public class WeatherDao {

	
	public void writeWeatherData(HashMap<String, ArrayList<Weather>> weatherData){

		Connection conn = DBUtils.getConnection();
		
		for (HashMap.Entry<String, ArrayList<Weather>> entry : weatherData.entrySet()) {
		    String fileName = entry.getKey();
		    ArrayList<Weather> weatherList = entry.getValue();
			for (Weather weather : weatherList){
				try{
					
					String sql = "{call spWriteWeatherData(?,?,?,?,?,?) }";
					CallableStatement cs = conn.prepareCall(sql);
		
					cs.setString(1, weather.getStationId());
					cs.setString(2, weather.getSpecialInfo());
					cs.setString(3, weather.getStationLocation());
					cs.setString(4, weather.getStationTimestamp());
					cs.setString(5, weather.getRawData());
					cs.setString(6, fileName);
					cs.execute();
		
			    }
			    catch (Exception e)
			    {
			      System.err.println(" Exception writing weather data! ");
			      System.err.println(e.getMessage());
			    }
			}
		}		
	}
	
	public void test(){
		Connection conn = DBUtils.getConnection();
		
				
		try{
	      String query = "SELECT count(*) c FROM weather ";
 
	      Statement st = conn.createStatement();
	      ResultSet rs = st.executeQuery(query);
	      
	      while (rs.next())
	      {
	        System.out.println("num rows: " + rs.getInt("c"));
	          
	      }
	      st.close();
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception! ");
	      System.err.println(e.getMessage());
	    }
 	} 

	
	/**
	 * stub for testing db connection, direct queries
	 */
	public static void main(String args[]){
		WeatherDao weatherDao = new WeatherDao();
		weatherDao.test();
		
	}
}
