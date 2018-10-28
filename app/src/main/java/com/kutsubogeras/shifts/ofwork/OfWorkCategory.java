package com.kutsubogeras.shifts.ofwork;

public class OfWorkCategory {
	//μεταβλητές Κλάσης
	private String category;
	private int totalDays;
    private int daysTake;
    //Μέθοδοι
    //set methods
    public void setCategoryValue(String category){
    	this.category = category;
    }
    public void setDaysTake(int days){
    	this.daysTake = days;
    }
    public void setTotalDays(int days){
    	this.totalDays = days;
    }
    //get methods
    public String getOfWorkCategory(){
    	return category;
    }
    public int getDaysTake(){
    	return daysTake;
    }
    public int getTotalDays(){
    	return totalDays;
    }
}
