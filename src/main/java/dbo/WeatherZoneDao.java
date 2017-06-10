package dbo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.WeatherZone;
import utils.DBUtils;

public class WeatherZoneDao {

	public void writeWeatherZoneData(HashMap<String, ArrayList<WeatherZone>> weatherZoneData) {

		Connection conn = DBUtils.getConnection();

		for (HashMap.Entry<String, ArrayList<WeatherZone>> entry : weatherZoneData.entrySet()) {
			String fileName = entry.getKey();
			ArrayList<WeatherZone> weatherZoneList = entry.getValue();
			for (WeatherZone weatherZone : weatherZoneList) {
				try {

					String sql = "{call spWriteWeatherZoneData(?,?,?,?,?,?) }";
					CallableStatement cs = conn.prepareCall(sql);

					cs.setString(1, weatherZone.getProduct());
					cs.setString(2, weatherZone.getHeader());
 					cs.setString(3, weatherZone.getZones());
					cs.setString(4, weatherZone.getStationTimestamp());
					cs.setString(5, weatherZone.getForecast());
					cs.setString(6, fileName);
					cs.execute();

				} catch (Exception e) {
					System.err.println(" Exception writing weather data! ");
					System.err.println(e.getMessage());
				}
			}
		}
	}

	public List<WeatherZone> getSampleData() {
		Connection conn = DBUtils.getConnection();
		List<WeatherZone> weatherZoneList = new ArrayList<WeatherZone>();

		try {
			String query = "select distinct * from weatherZone limit  5";

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				WeatherZone weatherZone = new WeatherZone();
				weatherZone.setProduct(rs.getString("product"));
				weatherZone.setHeader(rs.getString("header"));
				weatherZone.setZones(rs.getString("zones"));
				weatherZone.setStationTimestamp(rs.getString("station_timestamp"));
				weatherZone.setForecast(rs.getString("forecast"));
				weatherZone.setDateCreated(rs.getDate("date_created"));
				weatherZone.setFileId(rs.getString("file_id"));

				weatherZoneList.add(weatherZone);
			}
			st.close();
		} catch (Exception e) {
			System.err.println("exception retreiving sample data... ");
			System.err.println(e.getMessage());
		}
		return weatherZoneList;
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