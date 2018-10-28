package com.kutsubogeras.shifts.help;


import java.util.Calendar;

import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.R.id;
import com.kutsubogeras.shifts.R.layout;
import com.kutsubogeras.shifts.R.menu;
import com.kutsubogeras.shifts.insert.OfWorkInsertion;
import com.kutsubogeras.shifts.insert.OneShiftInsertion;
import com.kutsubogeras.shifts.insert.WeekShiftInsertion;
import com.kutsubogeras.shifts.queries.DisplayIntervalOfShifts;
import com.kutsubogeras.shifts.queries.QueryDatesManager;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

/**
 * Δημιουργεί ένα default Calendar View του Android 
 * για επιλογή Ημερομηνίας από το χρήστη. 
 * Μικρότερη Ημερομηνία πρέπει πάντα να μπαίνει γιατί διαφορετικά δεν θα υπάρχει ακρίβεια 
 * αντιστοίχισης της φερόμενης ως επιλεγμένης εβδομάδος όπως και του σημείου που 
 * αγγίζει ο χρήστης σε κάθε ημερομηνία.
 * Εδώ επιλέγεται ως μικρότερη ημερομηνία η 1/1/1964. 
 * Συνεπώς προγενέστερες ημερομηνίες δεν μπορούν να αποτυπωθούν στο Calendar View.
 */


@SuppressLint("NewApi")
public class DiaryView extends Activity {	
    
    private CalendarView myCalendar;
    private Button  okButton;
    private Button  cancelButton;
	private String  selectedDate;
	private int     check_date = 0;            //flag για έλεγχο της καλούσας Activity
	private final int START_DATE = 10;         //flag for Result
	private final int START_DATE_ONE = 11;     //flag for Result
	private final int OFWORK_START_DATE = 13;  //flag for Result
	private final int OFWORK_END_DATE   = 14;  //flag for Result
	private final int QUERY_MANAGER_START_DATE = 15;  //flag for Result
	private final int QUERY_MANAGER_END_DATE   = 16;  //flag for Result
	private final int SHIFT_INTERVAL_START_DATE = 17;  //flag for Result
	private final int SHIFT_INTERVAL_END_DATE   = 18;  //flag for Result
	private ComponentName calling_activity;
	private String name_calling_activity = "";
	private Calendar calendar;
	
	//String για ταυτοποίηση της μεταβλητής Extra: selectedDate στο Intent
    public static final String SELECTED_DAY  = "SelectedDate";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_view);
		
		//ελέγχει το όνομα της καλούσας Activity όπως ορίζεται στο Manifest.xml
		//p.x.(.TwoWeekShiftInsertion)
		calling_activity = this.getCallingActivity();
		name_calling_activity = calling_activity.getShortClassName();
		Log.i("CALLING_ACTIVITY_NAME", " : " + name_calling_activity);
				
		//ελέγχει την σημαία του Intent
		check_date  = getIntent().getFlags();
	    // components
		myCalendar  = (CalendarView)findViewById(R.id.calendarView1);
	    okButton    = (Button)findViewById(R.id.button1_ok);
	    cancelButton= (Button)findViewById(R.id.button2_cancel);
	   
	    //setting Listener in Buttons and Calendar View
	    calendar = Calendar.getInstance();
	    calendar.clear();
	    calendar.set(1964, 0, 1);//μικρότερη ημερομηνία η: 1/1/1964
	    myCalendar.setOnDateChangeListener(DateChangeListener);
	    myCalendar.setSoundEffectsEnabled(true);
	    myCalendar.setShowWeekNumber(true);
	    myCalendar.setMinDate(calendar.getTimeInMillis());// θετει την 1/1/1964
	    myCalendar.setWeekNumberColor(Color.RED); //
	    
	    //------------OK BUTTON LISTENER------------------------------
	    /*
	     * Αν πατηθεί το πλήκτρο ΟΚ επιστρέφει την επιλεγμένη ημερομηνία στην καλούσα Activity
	     * Διαφορετικά εμφανίζει κατάλληλο μήνυμα στο χρήστη
	     */
	    okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				okButton.setTextColor(Color.BLUE);
				String errorMes = "Δεν έχετε επιλέξει Ημερομηνία \nΠαρακαλώ επιλέξτε μια Ημερομηνία.";
				//αν δεν έχει επιλεγεί ημερομηνία εμφανίζει μήνυμα στο χρήστη
				if(selectedDate == null){
					Toast tost = Toast.makeText(getApplicationContext(), errorMes, Toast.LENGTH_SHORT ); 
                    tost.setGravity(Gravity.CENTER_VERTICAL, tost.getXOffset() / 2, tost.getYOffset() / 2);    
                    tost.show(); 					
				}
				else{ //αν έχει επιλεγεί ημερομηνία επιστρέφει την τιμή της στην αντίστοιχη καλούσα Activity
				    Intent enderDate = new Intent(DiaryView.this , WeekShiftInsertion.class);				
		            enderDate.putExtra(SELECTED_DAY, selectedDate);
		            //ελέγχει την τιμή της check_date και αντίστοιχα επιστρέφει πίσω στην καλούσα Activity
		            //την επιλεγμένει τιμή ημερομηνίας
		            switch(check_date){ 
		                case START_DATE : //αν κλήθηκε από την κλάση WeekShiftInsertion
		                     DiaryView.this.setResult(START_DATE , enderDate);
		                     break;
		                case START_DATE_ONE : //αν κλήθηκε από την κλάση OneShiftInsertion
		            	     Intent oneShiftIntent = new Intent(DiaryView.this , OneShiftInsertion.class);				
		            	     oneShiftIntent.putExtra(SELECTED_DAY, selectedDate);   
		            	     DiaryView.this.setResult(START_DATE_ONE , oneShiftIntent);
		                     break;
		                case OFWORK_START_DATE : //αν κλήθηκε από την κλάση OfWorkInsertion startDate
		            	     Intent ofWorkStartIntent = new Intent(DiaryView.this , OfWorkInsertion.class);				
		            	     ofWorkStartIntent.putExtra(SELECTED_DAY, selectedDate);   
		            	     DiaryView.this.setResult(OFWORK_START_DATE , ofWorkStartIntent);
		                     break;
		                case OFWORK_END_DATE : //αν κλήθηκε από την κλάση OfWorkInsertion endDate
		            	     Intent ofWorkEndInt = new Intent(DiaryView.this , OfWorkInsertion.class);				
		            	     ofWorkEndInt.putExtra(SELECTED_DAY, selectedDate);   
		            	     DiaryView.this.setResult(OFWORK_END_DATE , ofWorkEndInt);
		                     break;
		                case QUERY_MANAGER_START_DATE : //αν κλήθηκε από την κλάση QueryDatesManager StartDate
		            	     Intent queryManStartInt = new Intent(DiaryView.this , QueryDatesManager.class);				
		            	     queryManStartInt.putExtra(SELECTED_DAY, selectedDate);   
		            	     DiaryView.this.setResult(QUERY_MANAGER_START_DATE , queryManStartInt);
		                     break;
		                case QUERY_MANAGER_END_DATE : //αν κλήθηκε από την κλάση QueryDatesManager endDate
		            	     Intent queryManEndInt = new Intent(DiaryView.this , QueryDatesManager.class);				
		            	     queryManEndInt.putExtra(SELECTED_DAY, selectedDate);   
		            	     DiaryView.this.setResult(QUERY_MANAGER_END_DATE , queryManEndInt);
		                     break;
		                case SHIFT_INTERVAL_START_DATE : //αν κλήθηκε από την κλάση DisplayIntervalOfShifts StartDate
		            	     Intent shiftIntervalStartInt = new Intent(DiaryView.this , DisplayIntervalOfShifts.class);				
		            	     shiftIntervalStartInt.putExtra(SELECTED_DAY, selectedDate);   
		            	     DiaryView.this.setResult(SHIFT_INTERVAL_START_DATE , shiftIntervalStartInt);
		                     break;
		                case SHIFT_INTERVAL_END_DATE : //αν κλήθηκε από την κλάση DisplayIntervalOfShifts endDate
		            	     Intent shiftIntervalEndInt = new Intent(DiaryView.this , DisplayIntervalOfShifts.class);				
		            	     shiftIntervalEndInt.putExtra(SELECTED_DAY, selectedDate);   
		            	     DiaryView.this.setResult(SHIFT_INTERVAL_END_DATE , shiftIntervalEndInt);
		                     break;
		            }//end switch
				    finish();
			    }//end else				
			}//end onClick method
		});//end onClickListener
	    
	    //---------------CANCEL BUTTON LISTENER------------------------
	    cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				okButton.setTextColor(Color.RED);
				finish();
			}
		});//end onClickListener
	}//end onCreate
	//========================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_diary_view, menu);
		return true;
	}	
    //======================= LISTENER =======================================	
	@SuppressLint("NewApi")
	private CalendarView.OnDateChangeListener DateChangeListener =
    		                   new CalendarView.OnDateChangeListener() {				
				@Override
				public void onSelectedDayChange(CalendarView view, 
						    int year, int month,	int dayOfMonth) {					
					//������� �� ����� ��� ����������� ��� ����
					myCalendar.setFocusedMonthDateColor(Color.BLUE);					
					String myDay  = dayOfMonth < 10 ? "0"+dayOfMonth : ""+dayOfMonth;        
					String myMonth= (month +1) < 10 ? "0"+(month +1) : ""+(month +1);	
					selectedDate = myDay+"-"+myMonth+"-"+year;
					Toast tost=Toast.makeText(getApplicationContext(),
							         "Date: "+selectedDate, Toast.LENGTH_SHORT ); 
	                tost.setGravity(Gravity.CENTER_VERTICAL, tost.getXOffset() / 2,
	                		tost.getYOffset() / 2);    
                    tost.show(); 
				}
    };//end listener
			
}//end class
