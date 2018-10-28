package com.kutsubogeras.shifts.queries;
/*
 * Αυτή η κλάση δημιουργεί αντικείμενα για αποθήκευση των πληροφοριών που
 * χρησιμοποιούνται για την κλάση που προβάλει πληροφορίες για τα  πολυήμερα ΡΕΠΟ
 */

public class ExtendedRepo {
	//μεταβλητές κλάσης
	private String startDate;
	private String endDate;
	private int    totalDays;
	
	//Μέθοδοι
	//set Methods
	public void setStartDate(String date){
		this.startDate = date;
	}
	public void setEndDate(String date){
		this.endDate = date;
	}
	public void setTotalDays(int days){
		this.totalDays = days;
	}
	//get methods
	public String getStartDate(){
		return startDate;
	}
	public String getEndDate(){
		return endDate;
	}
	public int getTotalDays(){
		return totalDays;
	}
}//end class
