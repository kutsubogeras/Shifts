/**
 * 
 */
package com.kutsubogeras.shifts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Thanasis
 *
 */
public class ShiftOfUser /*implements Parcelable*/{
	
	//Μεταβλητές Κλάσης
	private long    curDateMilisec = 0; 
	private String  curDate;    // ημερομηνία	
	private String  curWeekDay; // ημέρα
	private String  curShift;   // βάρδια
	private int     isSunday  = 0;   // Κυριακή ή όχι
	private int     isHoliday = 0;  // Αργία ή όχι
	private String  holidayName;// Όνομα Αργίας
	private String  coments;    // Σχόλια
	private int     extraTime = 0;
	/*
	//Parcelable Constractor
	public static final Parcelable.Creator<ShiftOfUser> CREATOR = new Parcelable.Creator<ShiftOfUser>() {
		        
		        public ShiftOfUser createFromParcel(Parcel src) {
		            return new ShiftOfUser(src); //επιστρέφει τη μέθοδο που δημιουργεί τον costractor
		        }
		        public ShiftOfUser[] newArray(int size) {
		            return new ShiftOfUser[size];
		        }		        
	    }; 
	   
	//Constractor
	public ShiftOfUser(String date, String week_day, String cur_shift) {			
		curDate    = date;
		curWeekDay = week_day;	
		curShift   = cur_shift;		
	}
	
//------------------Percelable method-------------------------
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
    //----μέθοδος δημιουργίας του Parcelable Constractor---------
	private ShiftOfUser(Parcel src ) {
		readFromParcel(src);
	}
	
	public void readFromParcel(Parcel src) {	    	
	    	curDate    = src.readString();
	    	curWeekDay = src.readString();
	    	curShift   = src.readString();
	}
	    
	public void writeToParcel(Parcel dest, int flags) {		
		    dest.writeString(curDate);
	        dest.writeString(curWeekDay);
	        dest.writeString(curShift);	        
	}
	*/
    //----------------------Method Class------------------
	//-------------GET METHODS-------------------------
	public long getDateInMilisec(){
		return curDateMilisec;		   
	}
	public String getDate() {
		return curDate;
	}
	public String getWeekDay() {
		return curWeekDay;
	}
	public String getShift() {
		return curShift;
	}
	public int getShiftSunday() {
		return isSunday;
	}
	public int getShiftHoliday() {
		return isHoliday;
	}
	public String getShiftHolidayName() {
		return holidayName;
	}
	public String getComents() {
		return coments;
	}
	public int getExtraTime() {
		return extraTime;
	}
	//---------------SET METHODS-----------------------------
	public void setDateInMilisec(long date){
		   this.curDateMilisec = date;		   
	}
	public void setDate(String date){
		   this.curDate = date;		   
	}	
	public void setWeekDay(String day) {
		   this.curWeekDay = day;
	}
	public void setShift(String shift){		
   		   this.curShift = shift;   	    
	}//end method	
	public void setShiftSunday(int isSunday){		
		   this.isSunday = isSunday;   	    
	}//end method	
	public void setShiftHoliday(int isHoliday){		
		   this.isHoliday = isHoliday;   	    
	}//end method
	public void setShiftHolidayName(String holiday){		
		   this.holidayName = holiday;   	    
	}//end method
	public void setComents(String shiftComents){		
		   this.coments = shiftComents;   	    
	}//end method
	public void setExtraTime(int time){		
		   this.extraTime = time;   	    
	}//end method
}
