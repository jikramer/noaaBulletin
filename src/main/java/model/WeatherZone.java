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
	
}
