package dbo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.WeatherZone;
import utils.DBUtils;



public class WeatherZoneDao {

	Logger slf4jLogger = LoggerFactory.getLogger("WeatherZoneDao");
	
	public void writeWeatherZoneData(HashMap<String, ArrayList<WeatherZone>> weatherZoneData) {

		Connection conn = DBUtils.getConnection();

		for (HashMap.Entry<String, ArrayList<WeatherZone>> entry : weatherZoneData.entrySet()) {
			String fileName = entry.getKey();
			ArrayList<WeatherZone> weatherZoneList = entry.getValue();
			for (WeatherZone weatherZone : weatherZoneList) {
				try {

					String sql = "{call spWriteWeatherZoneData(?,?,?,?,?,?,?) }";
					CallableStatement cs = conn.prepareCall(sql);

					cs.setString(1, weatherZone.getStation());
					cs.setString(2, weatherZone.getHeader());
 					cs.setString(3, weatherZone.getZoneCodes());
 					cs.setString(4, weatherZone.getZones());
					cs.setString(5, weatherZone.getStationTimestamp());
					cs.setString(6, weatherZone.getForecast());
					cs.setString(7, fileName);
					cs.execute();

				} catch (Exception e) {
					System.err.println(" Exception writing weather data! ");
					System.err.println(e.getMessage());
					slf4jLogger.info("Exception : " + e.getMessage());
				}
			}
		}
	}

	
	public List<WeatherZone> getSampleData() {
		Connection conn = DBUtils.getConnection();
		List<WeatherZone> weatherZoneList = new ArrayList<WeatherZone>();

		try {
			String query = "select distinct * from weatherZone where zone_codes like 'NY%' limit  5";

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				WeatherZone weatherZone = new WeatherZone();
				weatherZone.setStation(rs.getString("station"));
				weatherZone.setHeader(rs.getString("header"));
				weatherZone.setZoneCodes(rs.getString("zone_codes"));
				weatherZone.setZones(rs.getString("zones"));
				weatherZone.setStationTimestamp(rs.getString("station_timestamp"));
				weatherZone.setForecast(rs.getString("forecast"));
				weatherZone.setDateCreated(rs.getDate("date_created"));
				weatherZone.setFilename(rs.getString("file_id"));

				weatherZoneList.add(weatherZone);
			}
			st.close();
		} catch (Exception e) {
			System.err.println("exception retreiving sample data... ");
			System.err.println(e.getMessage());
			slf4jLogger.info("Exception : " + e.getMessage());
		}
		return weatherZoneList;
	}

	public List<WeatherZone> getFilteredData(String station, String zones, String keywords, HashMap additionalZones, String fileName) {
		Connection conn = DBUtils.getConnection();
		List<WeatherZone> weatherZoneList = new ArrayList<WeatherZone>();
 		
		if ( keywords == null)
			keywords = "";
		
		try {

			 
			String query = "select * from weatherZone " 
					+ " where station like '%" + station + "%'" 
					+ " and zones like '%" + zones + "%'"
					+ " and file_id = '" + fileName + "'"
					;
					//+ " and forecast like '%" + keywords + "%'";

	/*		if(additionalZones != null){
				Set<String>keys = (Set)additionalZones.keySet();
	
		 		for(String key: keys){
		 		    query = query +  " or zones like '%" + additionalZones.get(key) + "%'";
		 		}
			}
 */
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next())  {
				WeatherZone weatherZone = new WeatherZone();
				weatherZone.setStation(rs.getString("station"));
				weatherZone.setHeader(rs.getString("header"));
				weatherZone.setZoneCodes(rs.getString("zone_codes"));
				weatherZone.setZones(rs.getString("zones"));
				weatherZone.setStationTimestamp(rs.getString("station_timestamp"));
				weatherZone.setForecast(rs.getString("forecast"));
				weatherZone.setDateCreated(rs.getDate("date_created"));
				weatherZone.setFilename(rs.getString("file_id"));

				weatherZoneList.add(weatherZone);
			}
			st.close();
		} catch (Exception e) {
			System.err.println("exception retreiving sample data... ");
			System.err.println(e.getMessage());
			slf4jLogger.info("Exception : " + e.getMessage());
		}
		return weatherZoneList;
	}


	public void deleteTable() {
		Connection conn = DBUtils.getConnection();

		try {
			String query = "truncate table weatherZone ";

			Statement st = conn.createStatement();
			boolean rs = st.execute(query);
 			st.close();

		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			slf4jLogger.info("Exception : " + e.getMessage());
		}
	}

	
	public void test() {
		Connection conn = DBUtils.getConnection();

		try {
			String query = "SELECT count(*) c FROM weatherZone ";

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				System.out.println("num rows: " + rs.getInt("c"));

			}
			st.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
	}

	/**
	 * stub for testing db connection, direct queries
	 */
	public static void main(String args[]) {
		WeatherZoneDao weatherZoneDao = new WeatherZoneDao();
		weatherZoneDao.test();

	}
}
