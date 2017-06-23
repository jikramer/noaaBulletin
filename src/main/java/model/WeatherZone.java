package model;

import java.util.Date;

public class WeatherZone {
	
	private String productType;
	private String header;
	private String zoneCodes;
	private String zones;
	private String specialInfo;
	private String stationTimestamp;
	private String stationLocation;
	private String forecast;
	private Date dateCreated;
	private String filename;
	private String keywords;
	
	public String getProductType() {
		return productType;
	}
	public void setProductType(String product) {
		this.productType = product;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getZones() {
		return zones;
	}
	public void setZones(String zones) {
		this.zones = zones;
	}
	public String getSpecialInfo() {
		return specialInfo;
	}
	public void setSpecialInfo(String specialInfo) {
		this.specialInfo = specialInfo;
	}
	public String getStationLocation() {
		return stationLocation;
	}
	public void setStationLocation(String stationLocation) {
		this.stationLocation = stationLocation;
	}
	public String getStationTimestamp() {
		return stationTimestamp;
	}
	public void setStationTimestamp(String stationTimestamp) {
		this.stationTimestamp = stationTimestamp;
	}
	public String getForecast() {
		return forecast;
	}
	public void setForecast(String forecast) {
		this.forecast = forecast;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	/**
	 * @return the fileId
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param fileId the fileId to set
	 */
	public void setFilename(String fileId) {
		this.filename = fileId;
	}
	/**
	 * @return the zone_codes
	 */
	public String getZoneCodes() {
		return zoneCodes;
	}
	/**
	 * @param zone_codes the zone_codes to set
	 */
	public void setZoneCodes(String zoneCodes) {
		this.zoneCodes = zoneCodes;
	}

	public String toString(){
		StringBuilder buff = new StringBuilder();
		buff.append("productType: " );
		buff.append(productType );
		buff.append("\n");
		
		buff.append("header: " );
		buff.append(header);
		buff.append("\n");
		
		buff.append("zoneCodes: " );
		buff.append(zoneCodes);
		buff.append("\n");
		
		buff.append("zones: " );
		buff.append(zones);
		buff.append("\n");
		
		buff.append("specialInfo: " );
		buff.append(specialInfo);
		buff.append("\n");
		
		buff.append("stationTimestamp: " );
		buff.append(stationTimestamp);
		buff.append("\n");
		
		buff.append("stationLocation: " );
		buff.append(stationLocation);
		buff.append("\n");
		

		buff.append("forecast: " );
		buff.append(forecast);
		buff.append("\n");
		
		buff.append("dateCreated: " );
		buff.append(dateCreated);
		buff.append("\n");
		
		buff.append("filename: " );
		buff.append(filename);
		buff.append("\n");
		
		return buff.toString();
	}
	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}
	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
}
 