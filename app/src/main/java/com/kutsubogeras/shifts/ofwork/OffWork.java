package com.kutsubogeras.shifts.ofwork;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OffWork {
	//μεταβλητές Κλάσης
	private String dateFrom;
	private String dateTo;
	private String category;
	private int duration;
	private int year;
	private int totalPaidleave; //ημέρες που διακαιούτε
	private String comments;
    //Μέθοδοι
    //set methods
	public void setStartDate(int dayMilisec){
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		this.dateFrom = df.format(dayMilisec);
    }
	public void setEndDate(int dayMilisec){
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		this.dateTo = df.format(dayMilisec);
    }	
	public void setStartDate(String date){
		this.dateFrom = date;
    }
	public void setEndDate(String date){
		this.dateTo = date;
    }	
    public void setCategoryValue(String category){
    	this.category = category;
    }
    public void setDuration(int days){
    	this.duration = days;
    }
    public void setYearValue(int year){
    	this.year = year;
    }
    public void setTotalDaysOfWork(int days){
    	this.totalPaidleave = days;
    }
    public void setComents(String comments){
    	this.comments = comments;
    }
    //get methods
    public String getOfWorkCategory(){
    	return category;
    }
    public int getDuration(){
    	return duration;
    }
    public String getStartDate(){
    	return dateFrom;
    }
    public String getEndDate(){
    	return dateTo;
    }
    public int getYearValue(){
    	return year;
    }
    public int getTotalDaysOfWork(){
    	return totalPaidleave;
    }
    public String getComents(){
    	return comments;
    }
}//end class
