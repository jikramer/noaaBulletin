package model;

import java.util.Date;


public class Weather {

	private String stationId;
	private String specialInfo;
	private String stationLocation;
	private String stationTimestamp;
	private String rawData;
	private Date dateCreated;
	
	
	public String getStationId() {
		return stationId;
	}
	public void setStationId(String stationId) {
		this.stationId = stationId;
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
	public String getRawData() {
		return rawData;
	}
	public void setRawData(String rawData) {
		this.rawData = rawData;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	/**
	 * @return the specialInfo
	 */
	public String getSpecialInfo() {
		return specialInfo;
	}
	/**
	 * @param specialInfo the specialInfo to set
	 */
	public void setSpecialInfo(String specialInfo) {
		this.specialInfo = specialInfo;
	}
	private String fileId;
}
