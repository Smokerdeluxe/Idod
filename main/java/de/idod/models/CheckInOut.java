package de.gajd.idod.models;

public class CheckInOut {
	
	private String checkInTime;
	private String checkOutTime;
	private String address;
	private String duration;
	private boolean isCheckedIn;

	// Leerer Konstruktor für Firebase	
	public CheckInOut() {}
	
	public CheckInOut(String checkInTime, String checkOutTime, String address, String duration, boolean isCheckedIn) {
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.address = address;
		this.duration = duration;
		this.isCheckedIn = isCheckedIn;
	}

	// Getter und Setter	
	public String getCheckInTime() {
		return checkInTime;
	}
	
	public void setCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}
	
	public String getCheckOutTime() {
		return checkOutTime;
	}
	
	public void setCheckOutTime(String checkOutTime) {
		this.checkOutTime = checkOutTime;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getDuration() {
		return duration;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public boolean isCheckedIn() {
		return isCheckedIn;
	}
	
	public void setCheckedIn(boolean checkedIn) {
		isCheckedIn = checkedIn;
	}
}