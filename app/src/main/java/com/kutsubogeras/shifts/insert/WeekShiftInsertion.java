package com.kutsubogeras.shifts.insert;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.R.drawable;
import com.kutsubogeras.shifts.R.id;
import com.kutsubogeras.shifts.R.layout;
import com.kutsubogeras.shifts.R.menu;
import com.kutsubogeras.shifts.data.DBControler;
import com.kutsubogeras.shifts.help.DiaryView;
import com.kutsubogeras.shifts.settings.SettingsShifts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/*
 * Αυτή η κλάση δημιουργεί την φόρμα εισαγωγής βαρδιών για μια εβδομάδα
 * και αφού ελέγξει τις τιμές που εισάγει ο χρήστης τις καταχωρεί στη βάση δεδομένων
 */
public class WeekShiftInsertion extends Activity {
	//variables
	private FrameLayout frameLayout;
	private EditText date1;
	private EditText date2;
	private EditText date3;
	private EditText date4;
	private EditText date5;
	private EditText date6;
	private EditText date7;
	private EditText weekDay1;
	private EditText weekDay2;
	private EditText weekDay3;
	private EditText weekDay4;
	private EditText weekDay5;
	private EditText weekDay6;
	private EditText weekDay7;
	private Spinner shift1;
	private Spinner shift2;
	private Spinner shift3;
	private Spinner shift4;
	private Spinner shift5;
	private Spinner shift6;
	private Spinner shift7;
	private CheckBox sunday1;
	private CheckBox sunday2;
	private CheckBox sunday3;
	private CheckBox sunday4;
	private CheckBox sunday5;
	private CheckBox sunday6;
	private CheckBox sunday7;
	private CheckBox holiday1;
	private CheckBox holiday2;
	private CheckBox holiday3;
	private CheckBox holiday4;
	private CheckBox holiday5;
	private CheckBox holiday6;
	private CheckBox holiday7;
	private Button   buttonAddDates;
	private Button   buttonOK;
	private Button   buttonReset;
	//date variables
	private final int START_DATE_FLAG = 10;  //flag  για αναγνώρηση της καλούσας activity(μεθόδου)
	private Calendar myCalendar;
	//private Calendar curDate;
	//private Calendar nextDate;
	private String selectedStartDate = null;
	private static final String addDateReciveMessage= "Επιλεγμένη Ημερομηνία : ";
	private static final String SelectedShiftMessage= "Η Επιλογή σας είναι: ";
	private static final String errorShiftInsertMessage= "Δεν Έχετε Καταχωρήσει κάποιον Εργαζόμενο";
	private static final String successShiftInsertMessage= "Επιτυχής Καταχώρηση για : ";
	private ArrayList<Calendar> shiftDates;          // ημερομηνίες τύπου Calendar
	private ArrayList<String>   shiftPrintableDates; // κρατά τις εκτυπώσιμη μορφή των ημερομηνιών
	private ArrayList<Integer>  shiftWeekDays;       // κρατά τις ακέραιες τιμές των ημερών της εβδομάδος
	private ArrayList<Integer>  extraTimes;
	private ArrayList<String>   ShiftValues      = new ArrayList<String>();   // κρατά τις τιμές των βαρδιών
	private ArrayList<Calendar> officialHolidays = new ArrayList<Calendar>(); // επίσιμες αργίες τύπου Calendar
	private ArrayList<String>   holidaysNames    = new ArrayList<String>();   // οι Ονομασίες των επίσιμων αργιών
	private ArrayList<String>   holidaysValues   = new ArrayList<String>();   // οι τιμές των αργιών που θα μπουν στα σχόλια
	private ArrayAdapter<String> dataAdapter1, dataAdapter2, dataAdapter3, dataAdapter4,
	                             dataAdapter5, dataAdapter6, dataAdapter7;
	private TimePicker  timePicker;
	private ArrayList<Integer> backgrounds = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_week_shift_insertion);
		
		// αρχικοποίηση μεταβλητών
		this.setShiftValuesInArrayTable(ShiftValues); // θέτει τις τιμές βαρδιών στον πίνακα
		this.initalizeComponets(); // αρχικοποίηση συστατικών στοιχείων φόρμας
		this.setBackroundIcons();
		this.checkPreferentBackround(frameLayout);
		this.setInSpinnersShiftValues(); // ανάθεση των τιμών των βαρδιών στα Spinners
		this.createComponensListeners(); // δημιουργία Listeners για Buttons,EditTexts,Spinners
		//this.setOfficialHolidays(); // θέτει τιμές στον πίνακαμε τις επίσημες αργίες
      /*
		// test διαγαρφής όλων των εγγραφών του πίνακα
		//=============== DELETE ROWS ===============
		mydb = new DBControler(this);
		mydb.open();
		int totaldeletedRows = mydb.deleteAllRowsFromTable("Shift");
		mydb.closeDB();
		Toast tost = Toast.makeText(this, "Διαγράφηκαν Συνολικά: " + totaldeletedRows,
				     Toast.LENGTH_LONG);
		tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
		tost.show();
	  */
	}
	//--------------------------- ON ACTIVITY RESULT ----------------------------------
		 //όταν τερματίσει η κληθήσα activity 	    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		   super.onActivityResult(requestCode, resultCode, data);		  		
		  	 if(resultCode == START_DATE_FLAG) {
		  		selectedStartDate = data.getStringExtra(DiaryView.SELECTED_DAY);		  		
		  		getDateValuesFromStringDate(selectedStartDate);
		  		
		  		Toast tost = Toast.makeText(this, addDateReciveMessage+selectedStartDate, Toast.LENGTH_LONG); 
			    tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
			    tost.show();
		  	 }		  	 	
	}//end method 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.week_shift_insertion, menu);
		menu.clear();
		menu.add(0, R.id.item1_one_week_shift_insertion, 0, "Βοήθεια")
            .setIcon(android.R.drawable.ic_menu_help);		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
		     case R.id.item1_one_week_shift_insertion: 
		    	 this.showingHelpMessage();
		    	 return true;		    
		     default:
		         return super.onOptionsItemSelected(item);
		}//end switch
	}	
	//αρχικοποιεί όλα τα Componets της φόρμας
    private void initalizeComponets(){
    	
    	frameLayout = (FrameLayout)findViewById(R.id.container_query_dates_manager);
    	date1    = (EditText)findViewById(R.id.editTextShiftDate_1);
    	date2    = (EditText)findViewById(R.id.editTextShiftDate_2);
    	date3    = (EditText)findViewById(R.id.editTextShiftDate_3);
    	date4    = (EditText)findViewById(R.id.editTextShiftDate_4);
    	date5    = (EditText)findViewById(R.id.editTextShiftDate_5);
    	date6    = (EditText)findViewById(R.id.editTextShiftDate_6);
    	date7    = (EditText)findViewById(R.id.editTextShiftDate_7);
    	weekDay1 = (EditText)findViewById(R.id.editTextShiftWeekDay_1);
    	weekDay2 = (EditText)findViewById(R.id.editTextShiftWeekDay_2);
    	weekDay3 = (EditText)findViewById(R.id.editTextShiftWeekDay_3);
    	weekDay4 = (EditText)findViewById(R.id.editTextShiftWeekDay_4);
    	weekDay5 = (EditText)findViewById(R.id.editTextShiftWeekDay_5);
    	weekDay6 = (EditText)findViewById(R.id.editTextShiftWeekDay_6);
    	weekDay7 = (EditText)findViewById(R.id.editTextShiftWeekDay_7);
    	shift1   = (Spinner)findViewById(R.id.SpinnerShiftValue_1);
    	shift2   = (Spinner)findViewById(R.id.SpinnerShiftValue_2);
    	shift3   = (Spinner)findViewById(R.id.SpinnerShiftValue_3);
    	shift4   = (Spinner)findViewById(R.id.SpinnerShiftValue_4);
    	shift5   = (Spinner)findViewById(R.id.SpinnerShiftValue_5);
    	shift6   = (Spinner)findViewById(R.id.SpinnerShiftValue_6);
    	shift7   = (Spinner)findViewById(R.id.SpinnerShiftValue_8);
    	sunday1  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_1);
    	sunday2  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_2);
    	sunday3  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_3);
    	sunday4  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_4);
    	sunday5  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_5);
    	sunday6  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_6);
    	sunday7  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_7);
    	holiday1 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_1);
    	holiday2 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_2);
    	holiday3 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_3);
    	holiday4 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_4);
    	holiday5 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_5);
    	holiday6 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_6);
    	holiday7 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_7);
    	buttonOK = (Button)findViewById(R.id.ButtonEnterShifts_OK);   
    	buttonAddDates = (Button)findViewById(R.id.buttonWeekShiftAddDate);  
    	buttonReset    = (Button)findViewById(R.id.buttonWeekShiftReset);  
    	
    }//end method
  //----------------------- SET BACKROUND ICONS VALUE --------
    //θέτει εικόνες wallpaper στη λίστα
	private void setBackroundIcons(){
		
		backgrounds.add(R.drawable.wallparer_blue_720_1280);
		backgrounds.add(R.drawable.wallpaper_light_blue_720_1280);
		backgrounds.add(R.drawable.wallpaper_blue_nice);
		backgrounds.add(R.drawable.wallpaper_blue_nice_2);
		backgrounds.add(R.drawable.wallpaper_moon);
		backgrounds.add(R.drawable.wallpaper_galaxy);		
		backgrounds.add(R.drawable.wallpaper_natureu_sun);
		backgrounds.add(R.drawable.wallpaper_natureu_2);
		backgrounds.add(R.drawable.wallpaper_natureu_3);
		backgrounds.add(R.drawable.wallpaper_natureu_4);
		backgrounds.add(R.drawable.wallpaper_rain);
		backgrounds.add(R.drawable.wallpaper_rain_2);
		backgrounds.add(R.drawable.wallpaper_black_720_1280);		
	}
	//-------------------------------BACK PREFERENCE------------------------------
	private  void checkPreferentBackround(FrameLayout layout){
		   //έλεγχος προτίμησης και ανάθεσης του επιλεγμένου του backround
		   SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);;
		   String backPrefer = sharedPref.getString(SettingsShifts.KEY_SELECTED_BACKGROUND, "");
		   if(!backPrefer.equals("")){//αν δεν είναι κενή η προτίμηση background
			   int index = Integer.parseInt(backPrefer);
		       Log.i("BACKROUND", ""+backPrefer);
		       layout.setBackgroundResource(backgrounds.get(index));
		   }
	}		  
    //------------------------- LISTENERS --------------------------------
    private void createComponensListeners(){
    	
    	date1.setOnClickListener(new View.OnClickListener() {       		
		       public void onClick(View v) {       		     
		       		 //δημιουργεί ένα Intent για την επιλογή Ημερομηνίας Έναρξης
		       		 Intent shiftIntent = new Intent(WeekShiftInsertion.this , DiaryView.class);
		       		 shiftIntent.setFlags(START_DATE_FLAG);
		       		 startActivityForResult(shiftIntent, START_DATE_FLAG);
		       }
		});    	
    	//------------------------------SPINNERS SHIFTS------------------------------------
	      //Spinner για την 1η βάρδια
    	shift1.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	         if(position >0){
	    	        	String shiftValue = shift1.getItemAtPosition(position).toString();
	    	        	selectedItemView.setBackgroundColor(Color.CYAN);
		    	        if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	           showDialogWithEditText(shift1, dataAdapter1);			    	          
		    	        else{
		    	             Toast tost = Toast.makeText(parentView.getContext(), 
		    	            		       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
		  			         tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
		  			         tost.show();
		    	        }		  		          
	    	          }
	    	    }
	    	    @Override
	    	    public void onNothingSelected(AdapterView<?> parentView) {
	    	        // your code here    	    	
	    	    }
	    });	
    	 //Spinner για την 2η βάρδια
    	shift2.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift2.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift2, dataAdapter2);			    	          
		    	      else{
		    	           Toast tost = Toast.makeText(parentView.getContext(), 
		    	            		       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
		  			       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
		  			       tost.show();
		    	      }		
	    	        }
	    	    }
	    	    @Override
	    	    public void onNothingSelected(AdapterView<?> parentView) {
	    	        // your code here    	    	
	    	    }
	    });	
    	 //Spinner για την 3η βάρδια
    	shift3.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift3.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift3, dataAdapter3);			    	          
		    	      else{
		    	          Toast tost = Toast.makeText(parentView.getContext(), 
		    	            		       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
		  			      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
		  			      tost.show();
		    	      }		
	    	        }
	    	    }
	    	    @Override
	    	    public void onNothingSelected(AdapterView<?> parentView) {
	    	        // your code here    	    	
	    	    }
	    });	
    	 //Spinner για την 4η βάρδια
    	shift4.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift4.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift4, dataAdapter4);			    	          
		    	      else{
		    	          Toast tost = Toast.makeText(parentView.getContext(), 
		    	            		       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
		  			      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
		  			      tost.show();
		    	      }		
	    	        }
	    	    }
	    	    @Override
	    	    public void onNothingSelected(AdapterView<?> parentView) {
	    	        // your code here    	    	
	    	    }
	    });	
    	 //Spinner για την 5η βάρδια
    	shift5.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift5.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift5, dataAdapter5);			    	          
		    	      else{
		    	          Toast tost = Toast.makeText(parentView.getContext(), 
		    	            		       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
		  			      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
		  			      tost.show();
		    	      }		
	    	        }
	    	    }
	    	    @Override
	    	    public void onNothingSelected(AdapterView<?> parentView) {
	    	        // your code here    	    	
	    	    }
	    });	
    	 //Spinner για την 6η βάρδια
    	shift6.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift6.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift6, dataAdapter6);			    	          
		    	      else{
		    	          Toast tost = Toast.makeText(parentView.getContext(), 
		    	            		       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
		  			      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
		  			      tost.show();
		    	      }		          
	    	        }
	    	    }
	    	    @Override
	    	    public void onNothingSelected(AdapterView<?> parentView) {
	    	        // your code here    	    	
	    	    }
	    });	
    	 //Spinner για την 7η βάρδια
    	shift7.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift7.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift7, dataAdapter7);			    	          
		    	      else{
		    	          Toast tost = Toast.makeText(parentView.getContext(), 
		    	            		       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
		  			      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
		  			      tost.show();
		    	      }		
	    	        }
	    	    }
	    	    @Override
	    	    public void onNothingSelected(AdapterView<?> parentView) {
	    	        // your code here    	    	
	    	    }
	    });	
    	//---------------------------BUTTON ADD DATES LISTENER ---------------------
    	buttonAddDates.setOnClickListener(new View.OnClickListener() {       		
		       public void onClick(View v) { 
		    	   //δημιουργεί ένα Intent για την εισαγωγή εργαζόμενου
  		           Intent shiftIntent = new Intent(WeekShiftInsertion.this , DiaryView.class);
  		           shiftIntent.setFlags(START_DATE_FLAG);
  		           startActivityForResult(shiftIntent, START_DATE_FLAG);//ελέγχει αν έχουν συμπληρωθεί τα πεδία της φόρμας		    	   
		       }
	    }); 
    	//---------------------------BUTTON OK LISTENER ---------------------
    	buttonOK.setOnClickListener(new View.OnClickListener() {       		
		       public void onClick(View v) { 
		    	   //ελέγχει αν έχουν συμπληρωθεί τα πεδία της φόρμας      		     
		    	   if(checkFormValues()){
		    		  insertValuesInDataBase(); //εισάγει τις τιμες στη ΒΔ
		       		  //finish();
		    	   }
		       }
	    }); 
    	//---------------------------BUTTON LISTENER ---------------------
    	buttonReset.setOnClickListener(new View.OnClickListener() {       		
		       public void onClick(View v) { 
		    	   resetFormValues(); //διαγράφει όλες τις τιμές σε όλα τα πεδία
		       }
	    }); 
    	
    }
    //-------------------------------------------------------------------
    /*
     * Αυτή η μέθοδος ελέγχει αν έχουν συμπληρωθεί όλα τα απιτούμενα
     * πεδία της φόρμας 
     */
    private boolean checkFormValues(){
    	//Τοπικές μεταβλητές
    	String errorFirstDateMessage  = "Δεν Καθορίσατε Ημερομηνίες και Ημέρες";
    	String errorShiftMessage_1 = "Δεν Συμπληρώσατε Βάρδια για την 1η Ημερομηνία";
    	String errorShiftMessage_2 = "Δεν Συμπληρώσατε Βάρδια για την 2η Ημερομηνία";
    	String errorShiftMessage_3 = "Δεν Συμπληρώσατε Βάρδια για την 3η Ημερομηνία";
    	String errorShiftMessage_4 = "Δεν Συμπληρώσατε Βάρδια για την 4η Ημερομηνία";
    	String errorShiftMessage_5 = "Δεν Συμπληρώσατε Βάρδια για την 5η Ημερομηνία";
    	String errorShiftMessage_6 = "Δεν Συμπληρώσατε Βάρδια για την 6η Ημερομηνία";
    	String errorShiftMessage_7 = "Δεν Συμπληρώσατε Βάρδια για την 7η Ημερομηνία";
    	
    	if(date1.getText().toString().equals("Πάτησε Εδώ")){
    	   Toast tost = Toast.makeText(this, errorFirstDateMessage, Toast.LENGTH_SHORT); 
 	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
 	       tost.show();
 	       return false;
    	}
    	if(shift1.getSelectedItem().toString().equals("Επέλεξε")){
     	   Toast tost = Toast.makeText(this, errorShiftMessage_1, Toast.LENGTH_SHORT); 
  	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
  	       tost.show();
  	       return false;
     	}
    	if(shift2.getSelectedItem().toString().equals("Επέλεξε")){
      	   Toast tost = Toast.makeText(this, errorShiftMessage_2, Toast.LENGTH_SHORT); 
   	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
   	       tost.show();
   	       return false;
      	}
    	if(shift3.getSelectedItem().toString().equals("Επέλεξε")){
      	   Toast tost = Toast.makeText(this, errorShiftMessage_3, Toast.LENGTH_SHORT); 
   	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
   	       tost.show();
   	       return false;
      	}
    	if(shift4.getSelectedItem().toString().equals("Επέλεξε")){
      	   Toast tost = Toast.makeText(this, errorShiftMessage_4, Toast.LENGTH_SHORT); 
   	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
   	       tost.show();
   	       return false;
      	}
     	if(shift5.getSelectedItem().toString().equals("Επέλεξε")){
       	   Toast tost = Toast.makeText(this, errorShiftMessage_5, Toast.LENGTH_SHORT); 
    	   tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
    	   tost.show();
    	   return false;
       	}
     	if(shift6.getSelectedItem().toString().equals("Επέλεξε")){
       	   Toast tost = Toast.makeText(this, errorShiftMessage_6, Toast.LENGTH_SHORT); 
    	   tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
    	   tost.show();
    	   return false;
       	}
     	if(shift7.getSelectedItem().toString().equals("Επέλεξε")){
           Toast tost = Toast.makeText(this, errorShiftMessage_7, Toast.LENGTH_SHORT); 
     	   tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
     	   tost.show();
     	   return false;
     	}
    	return true;
    }//end method
    //---------------------------------------------------------------------
    /*
     * Αυτή η μέθοδος εισάγει στη βάση δεδομένων τις τιμές των πεδίων
     */
    private boolean insertValuesInDataBase(){
    	
    	DBControler db = new DBControler(this);
    	long sumRows = 0; 
    	int userId   = 0;
    	long dateRows= 0;
    	long insertedRow =0;    	
    	ArrayList<String> weekDays   = new ArrayList<String>();   //έχει τις ημέρες της εβδομάδος
    	ArrayList<String> shiftTimes = new ArrayList<String>();   //έχει τα ωράρια των βαρδιών
    	ArrayList<Boolean> sundays   = new ArrayList<Boolean>();  //έχει τις true - false των Κυριακών
    	ArrayList<Boolean> holidays  = new ArrayList<Boolean>();  //έχει τις true - false των Αργιών
    	extraTimes = new ArrayList<Integer>();  //έχει τους εξτρα χρόνους(πέραν του 7,25 ωρών)
    	//ημέρες εβδομάδος
    	weekDays.add(weekDay1.getText().toString());
    	weekDays.add(weekDay2.getText().toString());
    	weekDays.add(weekDay3.getText().toString());
    	weekDays.add(weekDay4.getText().toString());
    	weekDays.add(weekDay5.getText().toString());
    	weekDays.add(weekDay6.getText().toString());
    	weekDays.add(weekDay7.getText().toString());
    	//ωράρια βαρδιών
    	shiftTimes.add(shift1.getSelectedItem().toString());
    	shiftTimes.add(shift2.getSelectedItem().toString());
    	shiftTimes.add(shift3.getSelectedItem().toString());
    	shiftTimes.add(shift4.getSelectedItem().toString());
    	shiftTimes.add(shift5.getSelectedItem().toString());
    	shiftTimes.add(shift6.getSelectedItem().toString());
    	shiftTimes.add(shift7.getSelectedItem().toString());    	
    	//Κυριακές
    	sundays.add(sunday1.isChecked());
    	sundays.add(sunday2.isChecked());
    	sundays.add(sunday3.isChecked());
    	sundays.add(sunday4.isChecked());
    	sundays.add(sunday5.isChecked());
    	sundays.add(sunday6.isChecked());
    	sundays.add(sunday7.isChecked());
    	//Αργίες
    	holidays.add(holiday1.isChecked());
    	holidays.add(holiday2.isChecked());
    	holidays.add(holiday3.isChecked());
    	holidays.add(holiday4.isChecked());
    	holidays.add(holiday5.isChecked());
    	holidays.add(holiday6.isChecked());
    	holidays.add(holiday7.isChecked());
    	//Υπολογισμός εχτρα τιμε
    	this.calgulateExtraTime(shiftTimes);  //υπολογίζει τον εξτρα χρόνο
    	
    	db.open();  
    	sumRows = db.getSumRowsFromTable("Employee");  //επιστρέφει το πλήθος εγγραφών του πίνακα Εργαζόμενος
    	db.closeDB();
    	if(sumRows > 0){     //αν βρέθηκε καταχωρημένος εργαζόμενος στη ΒΔ
    	   db.open(); 
    	   try{
    	       userId = db.getIdValueFromTable("Employee");
    	      }catch(Exception e){
    		        e.printStackTrace();
    		        Log.e("SQL_ERROR", "ERROR ΜΕ ΤΟ USER_CODE");
    		        db.closeDB();
    	      }    	   
    	   //εισάγει επαναληπτικά τα στοιχεία βαρδιών για 7 διαδοχικές ημερομηνίες
    	   for(int i=0; i<shiftDates.size(); i++){
    	      dateRows = db.selectDateValueFromTable("Shift", shiftDates.get(i).getTimeInMillis());
    	      if(dateRows ==0){
    		     insertedRow = db.InsertIntoShiftValues(userId,
    		    		          shiftDates.get(i).getTimeInMillis(),    		    		  
    		    		          weekDays.get(i),
    		    		          shiftTimes.get(i),
    				              sundays.get(i),
    				              holidays.get(i),
    				              holidaysValues.get(i), //έχει το όνομα της αργίας ή null
    				              null,                  //τα σχόλια
    				              null,                  //το κτήριο (δεν προβλέπεται από τις φόρμες εισαγωγής)
    				              extraTimes.get(i));
    	         Log.i("SUCCESS_INSERT", ""+insertedRow);
    	      }//end if   	   
    	      else{
    		     dateRows = 0;
    		     db.closeDB();
    		     Log.e("ERROR_INSERTION", "LINE "+i);
    		     return warningFailureShifDateInsertion(shiftDates.get(i).getTimeInMillis());
    	      }//end else
    	   }//end for
    	   
    	   db.closeDB();
    	   Toast tost = Toast.makeText(this, successShiftInsertMessage+insertedRow+" Βάρδιες",
    			   Toast.LENGTH_LONG); 
   	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
   	       tost.show();
   	       finish();
    	}//end if
    	else      //αν δεν υπάρχει καταχωρημένος εργαζόμενος στη ΒΔ
    		 warningFailureShiftInsertion(errorShiftInsertMessage); //εμφανίζει μήνυμα αποτυχίας    	
    	return true;
    }//end method
    
    //-----------------------------------------------------------------------
    /*
     * Υπολογίζει τον εξτρα χρόνο για κάθε βαρδια
     */
    private void calgulateExtraTime(ArrayList<String> extra){
    	
    	String[] houres;
    	int defaultTime = 445;  //το σύνολο εργάσιμων λεπτών ανά ημέρα για τραπεζουπάλληλο (ακριβώς 444 λεπτά)
    	int difference = 0;
    	int extraMin = 0;
    	for(int i=0; i<extra.size();i++){
    		String curTime = extra.get(i);
    		if(curTime.contains("-")){
    		  houres = curTime.split("-");
    		  String[] startHourMin = houres[0].split(":");
    		  int startHour = Integer.parseInt(startHourMin[0]);
    		  int startMin  = Integer.parseInt(startHourMin[1]);
    		  String[] finishHourMin = houres[1].split(":");
    		  int finishHour = Integer.parseInt(finishHourMin[0]);
    		  int finishMin  = Integer.parseInt(finishHourMin[1]);
    		  finishHour = (finishHour < 12) ? (finishHour + 24) : finishHour;
    		  difference = ((finishHour *60)+finishMin)-((startHour *60)+startMin);
    		  extraMin = (difference > defaultTime) ? (difference - defaultTime) : 0;
    		  extraTimes.add(extraMin);
    		}//end if
    		else{
    			extraMin = 0;
    			extraTimes.add(extraMin);
    		}//end else
    	}//end for
    }//end method
    //------------------------------------------------------------------------------------------
    /*
     * Η μέθοδος θέτει σε όλα τα Spinners ένα ArrayAdapter που παίρνει ως παραμέτρους
     * μεταξύ άλλων τον ιδιάιτερο τύπο φόρμας , τον τρόπο επιλογής και ένα
     * ArrayList με στοιχεία τύπου String με τιμές όλες τις πιθανές παραλλαγές βαρδιών
     */
    private void setInSpinnersShiftValues(){
    	dataAdapter1 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
    	/* simple_list_item_checked */
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice ); 
        dataAdapter2 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice );
        dataAdapter3 = new ArrayAdapter<String>( this, 
                           android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_list_item_single_choice ); 
        dataAdapter4 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter4.setDropDownViewResource(android.R.layout.simple_list_item_single_choice );
        dataAdapter5 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter5.setDropDownViewResource(android.R.layout.simple_list_item_single_choice );
        dataAdapter6 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter6.setDropDownViewResource(android.R.layout.simple_list_item_single_choice );
        dataAdapter7 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter7.setDropDownViewResource(android.R.layout.simple_list_item_single_choice ); 
        shift1.setAdapter(dataAdapter1);       
        shift1.setSelection(0);
        shift2.setAdapter(dataAdapter2);
        shift2.setSelection(0);    
        shift3.setAdapter(dataAdapter3);       
        shift3.setSelection(0);
        shift4.setAdapter(dataAdapter4);       
        shift4.setSelection(0);
        shift5.setAdapter(dataAdapter5);       
        shift5.setSelection(0);
        shift6.setAdapter(dataAdapter6);       
        shift6.setSelection(0);
        shift7.setAdapter(dataAdapter7);       
        shift7.setSelection(0);            
    }//end method
    //---------------------------------------------------------------------
    /*
     * Η μέθοδος θέτει τιμές βαρδιών στo πίνακα τιμών κάθε spinner
     */
    private void setShiftValuesInArrayTable(ArrayList<String> shiftData){
    	shiftData.add("Επέλεξε");
    	shiftData.add("06:30-14:30");
    	shiftData.add("06:30-13:55");
    	shiftData.add("07:00-15:00");
    	shiftData.add("07:00-16:00");
    	shiftData.add("07:15-14:40");
    	shiftData.add("07:15-15:15");
    	shiftData.add("07:30-15:30");
    	shiftData.add("07:45-14:45");
    	shiftData.add("07:45-15:15");
    	shiftData.add("08:00-16:00");
    	shiftData.add("09:00-17:00");
    	shiftData.add("09:35-17:00");
    	shiftData.add("14:30-22:30");
    	shiftData.add("14:30-22:00");
    	shiftData.add("14:45-23:15");
    	shiftData.add("15:00-22:00"); 
    	shiftData.add("15:00-23:00"); 
    	shiftData.add("15:15-23:15");
    	shiftData.add("22:30-06:00"); 
    	shiftData.add("22:30-06:30");    	
    	shiftData.add("23:15-07:15");      	
    	shiftData.add("ΡΕΠΟ");
    	shiftData.add("ΑΡΓΙΑ");
    	shiftData.add("ΑΔΕΙΑ");
    	shiftData.add("ΑΣΘΕΝΗΣ");
    	shiftData.add("ΑΛΛΟ"); 
    }//end method
    
    //--------------------------------------------------------------------
    /*
     *Αυτή η Μέθοδος καλείται στην OnActivityResault() μέθοδο της Activity
     *αι θέτει τιμές σε όλα τα πεδία της φόρμας μετά την επιλογή της 1ης ημερομηνίας
     *από το χρήστη
     */
    private void getDateValuesFromStringDate(String date){
    	
    	this.resetSundaysAndHolidaysCheckedValues();  //κάνει reset τις τσεκαρισμένες τιμές στα πεδία των Κυριακών
    	this.cancelSpinnerSelectedValues();           //κάνει reset τις τιμές βαρδιών αν είχαν επιλεγεί
    	
    	String dateValues[] = date.split("-");        //τεμαχίζει την ημερομηνία με βάση το χαρακτήρα (-)
    	int day   = 0;
    	int month = 0;
    	int year  = 0;    	
    	for(int i=0; i<dateValues.length; i++){
    		String curDateValue = dateValues[i];
    		switch(i){
    		case 0:
    		      try{
    	             day = Integer.parseInt(curDateValue);
    		         }catch(Exception e){
    			            e.printStackTrace();
    			            Log.e("CONVERT_DAY_ERROR", "Δεν έγινε μετατροπή της Ημέρας");
    		         }
    		      break;
    		case 1:
    		      try{
     	              month = Integer.parseInt(curDateValue);
     		         }catch(Exception e){
     			            e.printStackTrace();
     			            Log.e("CONVERT_MONTH_ERROR", "Δεν έγινε μετατροπή του Μήνα");
     		         }
    		      break;
    		case 2:
    		      try{
     	              year = Integer.parseInt(curDateValue);
     		         }catch(Exception e){
     			            e.printStackTrace();
     			            Log.e("CONVERT_YEAR_ERROR", "Δεν έγινε μετατροπή του Έτους");
     		         }
    		      break;
    		}//end switch
    	}//end for
    	myCalendar = Calendar.getInstance();    //Στιγμιότυπο της τρέχουσας ημερομηνίας
    	myCalendar.clear();                     //καθαρίζει τις τιμές της τρέχουσας ημερομηνίας
    	myCalendar.set(year, (month -1), day);	//θέτει νέα ημερομηνία   
    	
    	this.setOfficialHolidays();             // θέτει τιμές στον πίνακαμε τις επίσημες αργίες 	
    	this.findDatesAndWeekDays(myCalendar);  //υπολογίζει τις επόμενες ημερομηνίες 
    	this.setDatesAndWeekDays();             //θέτει τιμές στα πεδία των επόμενων ημερομηνιών 
    }//end method
    //--------------------------------------------------------------------
    /*
     * Ανάθεση τιμών στον πίνακα με τις Επίσιμες Αργίες
     */
    private void setOfficialHolidays(){
    	SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        int userYear = myCalendar.get(Calendar.YEAR);  //το έτος της ημερομηνίας που επιλέχθηκε από το χρήστη  	
    	
        Calendar holiday = Calendar.getInstance();
	    holiday.clear();
	    holiday.set(userYear, 11, 25);  // 25-12 Χριστούγεννα
	    officialHolidays.add(holiday);
	    holidaysNames.add("ΧΡΙΣΤΟΥΓΕΝΝΑ");
	    
	    holiday = Calendar.getInstance();
	    holiday.clear();
	    holiday.set(userYear, 11, 26);  // 26-12 Της Παναγίας
	    officialHolidays.add(holiday);
	    holidaysNames.add("2Η ΤΩΝ ΧΡΙΣΤΟΥΓΕΝΝΩΝ");
	    
	    //δημιουργεί την ημερομηνία κριτήριο : 18/12/(έτος επιλογής του χρήστη)
    	Calendar date_18_12 = Calendar.getInstance();
    	date_18_12.clear();
    	date_18_12.set(userYear, 11, 18);
	    Log.i("START_DATE : "+myCalendar.getTimeInMillis(), "DATE_25_12 : "+date_18_12.getTimeInMillis());
	    //αν η ημερομηνία έναρξης της εβδομάδος του χρήστη είναι μεγαλύτερη της 18/12   
    	if(myCalendar.getTimeInMillis() > date_18_12.getTimeInMillis()){ //π.χ. 19/12 +14 = 1/1/ νέο Έτος
    	   userYear +=1;  //προσθέτει ένα στο Έτος     		
    	   Log.i("NEXT_YEAR", ""+userYear);
    	}
    	holiday = Calendar.getInstance();
    	holiday.clear();
    	holiday.set(userYear, 0, 1);    // 1-1   Πρωτοχρονιά
 	    officialHolidays.add(holiday);
 	    holidaysNames.add("ΠΡΩΤΟΧΡΟΝΙΑ");
 	    
 	    holiday = Calendar.getInstance();
 	    holiday.clear();
 	    holiday.set(userYear, 0, 6);    // 6-1   των Φώτων
	    officialHolidays.add(holiday);
	    holidaysNames.add("ΤΩΝ ΘΕΟΦΑΝΕΙΩΝ");
	    //---------------------------------------------
	    holiday = Calendar.getInstance();
	    holiday.clear();
	    holiday.set(userYear, 2, 25);   // 25-3  25η Μαρτίου
	    officialHolidays.add(holiday);
	    holidaysNames.add("ΤΟΥ ΕΥΑΓΓΕΛΙΣΜΟΥ");
	    
	    holiday = Calendar.getInstance();
	    holiday.clear();
	    holiday.set(userYear, 4, 1);    // 1-5   1η ΜαΙου
	    officialHolidays.add(holiday);
	    holidaysNames.add("ΠΡΩΤΟΜΑΓΙΑ");
	    
	    holiday = Calendar.getInstance();
	    holiday.clear();
	    holiday.set(userYear, 7, 15);   // 15-8  15 Αυγούστου
	    officialHolidays.add(holiday);
	    holidaysNames.add("ΚΟΙΜΗΣΗ ΘΕΟΤΟΚΟΥ");
	    
	    holiday = Calendar.getInstance();
	    holiday.clear();
	    holiday.set(userYear, 9, 28);   // 28-10 28η Οκτωβρίου
	    officialHolidays.add(holiday);	 
	    holidaysNames.add("28 ΟΚΤΩΒΡΙΟΥ");
	    
	    //Προσθήκη ΚΙνητών Αργιών (Πάσχα, Καθαρά Δευτέρα, Αγίου Πνεύματος,
	    //Μεγάλη Παρασκευή, Δευτέρα του Πάσχα)
	    Calendar Easter_holiday = Calendar.getInstance();
	    Easter_holiday.clear();
	    //επιστρέφει την ημερομηνία του Πασχα
	    Easter_holiday = this.findEasterHoliday(userYear);  //Κυριακή του Πάσχα
	    officialHolidays.add(Easter_holiday);
	    holidaysNames.add("ΑΓΙΟΝ ΠΑΣΧΑ");
	    holiday = Calendar.getInstance();
	    holiday.clear();	    
	    holiday.set(Easter_holiday.get(Calendar.YEAR), 
	    		    Easter_holiday.get(Calendar.MONTH),
	    		    Easter_holiday.get(Calendar.DAY_OF_MONTH));
	    holiday.add(Calendar.DAY_OF_MONTH, 1);         //Δευτέρα του Πάσχα
	    officialHolidays.add(holiday);
	    holidaysNames.add("ΔΕΥΤ. ΔΙΑΚΑΙΝΗΣΙΜΟΥ");
	    
	    holiday = Calendar.getInstance();
	    holiday.clear();
	    holiday.set(Easter_holiday.get(Calendar.YEAR), 
	    		    Easter_holiday.get(Calendar.MONTH),
	    		    Easter_holiday.get(Calendar.DAY_OF_MONTH)); 
	    holiday.add(Calendar.DAY_OF_MONTH, -2);        //Μεγάλη Παρασκευή
	    officialHolidays.add(holiday);
	    holidaysNames.add("ΜΕΓ. ΠΑΡΑΣΚΕΥΗ");
	    
	    holiday = Calendar.getInstance();
	    holiday.clear();
	    holiday.set(Easter_holiday.get(Calendar.YEAR), 
	    		    Easter_holiday.get(Calendar.MONTH),
	    		    Easter_holiday.get(Calendar.DAY_OF_MONTH)); 
	    holiday.add(Calendar.DAY_OF_MONTH, -48);       //Καθαρή Δευτέρα
	    officialHolidays.add(holiday);
	    holidaysNames.add("ΚΑΘ. ΔΕΥΤΕΡΑ");
	    
	    holiday = Calendar.getInstance();
	    holiday.clear();
	    holiday.set(Easter_holiday.get(Calendar.YEAR), 
	    		    Easter_holiday.get(Calendar.MONTH),
	    		    Easter_holiday.get(Calendar.DAY_OF_MONTH)); 
	    holiday.add(Calendar.DAY_OF_MONTH, 50);        //Αγίου πνεύματος
	    officialHolidays.add(holiday);
	    holidaysNames.add("ΑΓ. ΠΝΕΥΜΑΤΟΣ");
	    //test values
	    /*
	    Log.i("ΑΡΓΙΕΣ", "====== HOLIDAYS ======");
	    for(int i=0; i<officialHolidays.size(); i++){
	    	Log.i("ARGIA_IN_MILIC:"+i, ""+officialHolidays.get(i).getTimeInMillis());
	    	Log.i("ARGIA_ΗΜΕΡΟΜΗΝΙΑ :"+i, df.format(officialHolidays.get(i).getTime()));
	    }
	   */
    }//end method
  //---------------------- ΑΛΓΟΡΙΘΜΟΣ ΥΠΟΛΟΓΙΣΜΟΥ ΗΜΕΡΟΜΗΝΙΑΣ ΠΑΣΧΑ ---------------
    /*
     * Υπολογίζει την Ημερομηνία Του Πάσχα με βάση το τρέχων Γρηγοριανό Ημερολόγιο
     * αφού είναι μετακινούμενη αργία.
     * (Εξακολουθεί να υπολογίζεται από την εκκλησία με βάση το Ιουλιανό Ημερολόγιο
     * που είναι το προγενέστερο του Γρηγοριανού). Επίσης από αυτή υπολογίζονται η Καθαρά Δευτέρα
     *  του Αγίου Πνέυματος (40 ημέρες πρίν και μετά αντίστοιχα) καθώς επίσης η Μεγάλη Παρασκευή
     *  και η Δευτέρα του Πάσχα
     */
    private Calendar findEasterHoliday(int year){   
    	
        	 String title    ="Λανθασμένη Επιλογή";
        	 String errorMes ="Το Έτος επιλογής σας πρέπει να βρίσκεται μεταξύ των τιμών : 1583 και 4099";
        	 Calendar EasterDay = Calendar.getInstance();
        	 int day    =0;
        	 int month  =0;
        	 int calYear=0; 
        	 int er     =0;
        	 int Gr     =0;
        	 int Jul    =0;
        	 int Ioul   =0;
        	 int Lat    =0;
        	 int poin   =0;
        	 
        	 if(year <1583 || year >4099)
        	    showingDialogWithMessage(title, errorMes, R.drawable.error_icon);
        	 else{
        		 if(year >1600){
        			calYear = (int) Math.floor(year / 100);
        			er      = 10 + calYear - 16 - (int)Math.floor((calYear - 16) / 4);
        		 }
        	     Gr   = year % 19;
        	     Ioul = (19 * Gr + 15) % 30;
        	     Jul  = (year + (int)Math.floor(year / 4) + Ioul) % 7;
        	     Lat  = Ioul  - Jul;
        	     poin = Lat   + er;    	     
        	     day  = 1 + (poin + 27 +(int)Math.floor((poin + 6) / 40)) % 31;
        	     month= 3 + (int)Math.floor((poin + 26) / 30) - 1;    	     
        	 }
        	 EasterDay.clear();
        	 EasterDay.set(year, month, day);
        	 return EasterDay;     	  
 	}//end method    
    
    //--------------------------------------------------------------------
    /*
     * Η μέθοδος δεν καλείται πουθενά στην κλάση. Υλοποιήθηκε για την περίπτωση
     * που θέλαμε να αποθηκεύσουμε την βάρδια σε 2 πεδία  : Ώρα έναρξης και ώρα λήξης
     * ως ακεραίους(milisec) προκειμένου να μπορούν να συγκριθούν μεταξύ τους αναζητώντας
     * μικρότερες τιμές ή μεγαλύτερες από κάποιες άλλες ή και ίσες
     *Αυτή η Μέθοδος τροποποιεί το String ώρας: π.χ. 10:30 σε τύπου long
     *ακέραιο σε milisec.Η ακριβή ώρα προκύπτει διαιρώντας το συνολικό αριθμό
     *με το 3.600.000 και κρατώντας το πηλίκο ενώ τα λεπτά διαιρώντας το υπόλοιπο
     *με το 60.000
     */
    private long getTimeValuesFromStringTime(String time){ 
    	
    	long timeMilisec = 0;
    	int year  = 0;
    	int month = 0;
    	int day   = 0;
    	int hourOfDay= 0;
    	int minute   = 0;
    	String timeValues[] = time.split(":");        //τεμαχίζει την ημερομηνία με βάση το χαρακτήρα (-)
    	
    	for(int i=0; i<timeValues.length; i++){
    		String curTimeValue = timeValues[i];
    		switch(i){
    		case 0:
    		      try{
    		    	  hourOfDay = Integer.parseInt(curTimeValue);
    		         }catch(Exception e){
    			            e.printStackTrace();
    			            Log.e("CONVERT_TIME_ERROR", "Δεν έγινε μετατροπή της Ώρας");
    		         }
    		      break;
    		case 1:
    		      try{
     	              minute = Integer.parseInt(curTimeValue);
     		         }catch(Exception e){
     			            e.printStackTrace();
     			            Log.e("CONVERT_MINUTE_ERROR", "Δεν έγινε μετατροπή των Λεπτών");
     		         }
    		      break;    		
    		}//end switch
    	}//end for
    	Calendar date = Calendar.getInstance();         //Στιγμιότυπο της τρέχουσας ημερομηνίας
    	date.clear();                                   //καθαρίζει τις τιμές της τρέχουσας ημερομηνίας
    	date.set(year, month, day, hourOfDay, minute);	//θέτει νέα ώρα
    	timeMilisec = date.getTimeInMillis();
    	return timeMilisec;   //επιστρέφει την ώρα σε milisec(1 ώρα: 60*60*1000 = 3.600.000 msec)
    }//end method
     
    //----------------------------------------------------------------------
    /*
     *Ορίζει τις επόμενες 6 ημερομηνίες και ημέρες της εβδομάδος 
     *που θα μπούν στα αντίστοιχα επόμενα 6 πεδία της φόρμας
     *Καλείται στο τέλος της προηγούμενης μεθόδου : getDateValuesFromStringDate()
     */
    private void findDatesAndWeekDays(Calendar date){
    	
    	Calendar nextDate   = Calendar.getInstance();
    	SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    	shiftDates          = new ArrayList<Calendar>();
    	shiftPrintableDates = new ArrayList<String>();    	
    	shiftWeekDays       = new ArrayList<Integer>();
    	nextDate.clear();
    	nextDate.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
    	shiftDates.add(nextDate);
    	shiftPrintableDates.add(df.format(shiftDates.get(0).getTime()));
    	shiftWeekDays.add(shiftDates.get(0).get(Calendar.DAY_OF_WEEK));
    	//Log.i("START DATE ", shiftPrintableDates.get(0));
    	//Log.i("START WEEKDAY ", ""+shiftWeekDays.get(0));
    	
    	for(int i=0; i<6; i++){
    	   Calendar curDate = Calendar.getInstance();
    	   curDate.clear();
    	   curDate.set(nextDate.get(Calendar.YEAR), nextDate.get(Calendar.MONTH), nextDate.get(Calendar.DAY_OF_MONTH)); 	   
    	   curDate.add(Calendar.DATE, 1);   //αυξάνει την ημέρα της ημερομηνίας κατά 1
    	   nextDate = Calendar.getInstance();
    	   nextDate.clear();
    	   nextDate.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), curDate.get(Calendar.DAY_OF_MONTH));
    	   shiftDates.add(nextDate);    //προσθέτει την ημερομηνία 
    	   shiftPrintableDates.add(df.format(nextDate.getTime()));
           shiftWeekDays.add(nextDate.get(Calendar.DAY_OF_WEEK)); //κρατά την τιμή της ημέρας της εβδομάδος    	  
           //Log.i("NEXT DATE : "+i, ""+shiftPrintableDates.get(i));
           //Log.i("NEXT DATE : "+(i+1), df.format(shiftDates.get(i+1).getTime()));
           //Log.i("NEXT WEEKDAY : "+(i+1), ""+shiftDates.get(i+1).get(Calendar.DAY_OF_WEEK));
           //textview.setText(formattedDate);
         }//end for
    	
    	//print test 
    	Log.i("ΗΜΕΡΟΜΗΝΙΕΣ", "========== DATES =========");
    	Log.i("SHIFT_NEXT_DATES ", "SIZE :"+shiftDates.size());
    	for(int i=0; i<shiftDates.size(); i++){
    		Log.i("DATE "+(i+1), df.format(shiftDates.get(i).getTime()));
    	}//end for
    }//end method
    //---------------------------------------------------------------------------
    /*
     *Θέτει τιμές αντίστοιχα επόμενα 6 πεδία της φόρμας με ημερομηνίες και ημέρες
     *Καλείται στο τέλος της  : getDateValuesFromStringDate()
     */
    private void setDatesAndWeekDays(){
    	SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    	//ανάθεση ημερομηνιών
    	for(int i=0; i<shiftPrintableDates.size(); i++){
    		if(i==0)
    		   date1.setText(shiftPrintableDates.get(i));
    		if(i==1)
     		   date2.setText(shiftPrintableDates.get(i));   
    		if(i==2)
     		   date3.setText(shiftPrintableDates.get(i));   
    		if(i==3)
     		   date4.setText(shiftPrintableDates.get(i));   
    		if(i==4)
     		   date5.setText(shiftPrintableDates.get(i));   
    		if(i==5)
     		   date6.setText(shiftPrintableDates.get(i));   
    		if(i==6)
      		   date7.setText(shiftPrintableDates.get(i));   
        }//end for    	
    	//Ανάθεση ημερών εβδομάδος και Eπιλογή  Κυριακών (checked) 
    	for(int j=0; j<shiftWeekDays.size(); j++){
    		int weekDay = shiftWeekDays.get(j);    		
    		if(j==0){
    			weekDay1.setText(getDayOfWeek(weekDay));
    			if(weekDay == 1)             //αν είναι Κυριακή
    			   sunday1.setChecked(true);    			
    		}
    		if(j==1){
    			weekDay2.setText(getDayOfWeek(weekDay));
			    if(weekDay == 1)
 			       sunday2.setChecked(true);			    
 		    }
    		if(j==2){
    			weekDay3.setText(getDayOfWeek(weekDay));
			    if(weekDay == 1)
 			       sunday3.setChecked(true);			    
 		    }
    		if(j==3){
    			weekDay4.setText(getDayOfWeek(weekDay));
			    if(weekDay == 1)
 			       sunday4.setChecked(true);			    
 		    } 
    		if(j==4){
    			weekDay5.setText(getDayOfWeek(weekDay)); 
			    if(weekDay == 1)
 			       sunday5.setChecked(true);			    
 		    } 
    		if(j==5){
    			weekDay6.setText(getDayOfWeek(weekDay)); 
			    if(weekDay == 1)
 			       sunday6.setChecked(true);			    
 		    }  
    		if(j==6){
    			weekDay7.setText(getDayOfWeek(weekDay)); 
			    if(weekDay == 1)
 			       sunday7.setChecked(true);			    
 		    }    	
        }//end for 
    	//Τσεκάρισμα Αργιών
    	for (int i=0; i<shiftDates.size();i++){
    		
    		if(i==0 && isAnOfficialHoliday(shiftDates.get(i))) //αν είναι επίσημη αργία
    			   holiday1.setChecked(true);    		
    		if(i==1 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday2.setChecked(true);
    		if(i==2 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday3.setChecked(true);
    		if(i==3 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday4.setChecked(true);
    		if(i==4 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday5.setChecked(true);
    		if(i==5 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday6.setChecked(true);
    		if(i==6 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday7.setChecked(true);    		
    	}
    	
    }//end method
    //----------------------------------------------------------------------
    /*
     * Ελέγχει αν πρόκειται γαι Επίσημη Αργία
     * Καλείται στην προηγούμενη: setDatesAndWeekDays()
     */
    private boolean isAnOfficialHoliday(Calendar curDate){
    	//Log.i("HOLIDAYS_CHECK ", "========= CHECK ========");
    	boolean isHoliday = false;
    	String holidayName = null;
    	for(int j=0; j<officialHolidays.size(); j++){
    		//Log.i("HOLIDAY ", " 1. ="+curDate.getTimeInMillis()+"  "+(j+1)+". ="+officialHolidays.get(j).getTimeInMillis());
    		if(curDate.getTimeInMillis() == officialHolidays.get(j).getTimeInMillis()){
    		   isHoliday   = true;
     		   holidayName = holidaysNames.get(j); //παίρνει το όνομα της Αργίας
     		   break;
     		}     		
    	}//end for
    	holidaysValues.add(holidayName);  //η holidayName έχει το όνομα της αργίας ή τιμή null
    	return isHoliday;
    }//end method
    
    //----------------------------------------------------------------------
    /*
     * Επιστρέφει το όνομα της ημέρας της εβδομάδος
     * Καλείται στην προηγούμενη: setDatesAndWeekDays()
     */
    private String getDayOfWeek(int weekDay){
    	String dayName = null;  //το όνομα της ημέρας της εβδομάδος
    	 switch (weekDay){
         case 1:    //αν έχει τιμή 1
        	 dayName ="ΚΥΡΙΑΚΗ"; 
             break;    	               
         case 2:   //αν έχει τιμή 2
        	 dayName ="ΔΕΥΤΕΡΑ"; 
             break;     
         case 3:   //αν έχει τιμή 3
        	 dayName ="ΤΡΙΤΗ";
             break;    
         case 4:
        	 dayName ="ΤΕΤΑΡΤΗ";
             break;    
         case 5:
        	 dayName ="ΠΕΜΠΤΗ";
             break;     
         case 6:
        	 dayName ="ΠΑΡΑΣΚΕΥΗ";
             break;    
         case 7:
        	 dayName ="ΣΑΒΒΑΤΟ";
             break;           
         }//end switch
        return dayName;
    }//end method
    //----------------------------------------------------------------------
    /*
     * Κάνει Reset όλα τα τσεκαρισμένα πεδία των Κυριακών και των Αργιών
     * Καλείται στο σώμα της getDateValuesFromStringDate() στην αρχή
     */
    private void resetSundaysAndHolidaysCheckedValues(){
    	sunday1.setChecked(false);
    	sunday2.setChecked(false);
    	sunday3.setChecked(false);
    	sunday4.setChecked(false);
    	sunday5.setChecked(false);
    	sunday6.setChecked(false);
    	sunday7.setChecked(false);
    	holiday1.setChecked(false);
    	holiday2.setChecked(false);
    	holiday3.setChecked(false);
    	holiday4.setChecked(false);
    	holiday5.setChecked(false);
    	holiday6.setChecked(false);
    	holiday7.setChecked(false);
    }//end method
    //----------------------------------------------------------------------
    /*
     * Θέτει σε όλα τα Spinner ην αρχική επιλογή 
     * δηλαδή το μήνυμα προτροπής: <Επέλεξε>
     */
    private void cancelSpinnerSelectedValues(){
    	shift1.setSelection(0);
    	shift2.setSelection(0);
    	shift3.setSelection(0);
    	shift4.setSelection(0);
    	shift5.setSelection(0);
    	shift6.setSelection(0);
    	shift7.setSelection(0);
    }//end method
  //----------------------------------------------------------------------
    /*
     * Διαγράφει όλες τις τιμές στα πεδία Ημερομηνιών και Ημερών Εβδομάδος
     */
    private void resetDatesAndWeekdaysValues(){
    	
    	//date1.setBackgroundColor(Color.WHITE);
    	date1.setText("Πάτησε Εδώ");   //dates
    	date2.setText("");
    	date3.setText("");
    	date4.setText("");
    	date5.setText("");
    	date6.setText("");
    	date7.setText("");
    	weekDay1.setText(""); //weekdays
    	weekDay2.setText("");
    	weekDay3.setText("");
    	weekDay4.setText("");
    	weekDay5.setText("");
    	weekDay6.setText("");
    	weekDay7.setText("");
    }//end method
  //----------------------------------------------------------------------
    /*
     * Διαγράφει όλες τις τιμές στα πεδία της φόρμας
     */
    private void resetFormValues(){
    	resetDatesAndWeekdaysValues();  //διαγραφή Ημερομηνιών και Ημερών
    	cancelSpinnerSelectedValues();  //διαγραφή επιλεγμένων Βαρδιών
    	resetSundaysAndHolidaysCheckedValues(); //διαγραφή Επιλεγμένων Κυριακών - Αργιών
    }//end method
    //-------------------------------------------------------------------------
	 /*
	  * Η μέθοδος αυτή δημιουργεί ένα Dialog με 2 TextView στα οποία μπορεί  
	  * ο χρήστης να εισάγει ή να αλλάξει τιμή αγγίζοντάς τα. Οι τιμές που
	  * εισάγονται είναι Ώρα ¨εναρξηε και Λ¨ηξης της Νέας Βάρδιας που δεν
	  * υπάρχει στις διαθέσιμες τιμές του Spinner
	  */
	 private void showDialogWithEditText(final Spinner spinner, final ArrayAdapter<String> adapter){
		    
		    final String hourStartMes  = "Ώρα Έναρξης";
	    	final String hourFinishMes = "Ώρα Λήξης";		    	
	    	LayoutInflater factory = LayoutInflater.from(this);		    	
			View timeEntryView = factory.inflate(R.layout.fragment_dialog_time_items, null);
			final TextView txtStart  = (TextView) timeEntryView.findViewById(R.id.TextView_Time_start_Dialog);
			final TextView txtFinish = (TextView) timeEntryView.findViewById(R.id.TextView_Time_Finish_Dialog);
			txtStart.setOnClickListener(new View.OnClickListener() {       		
			       public void onClick(View v) { 
			    	   showTimeDialog(hourStartMes, txtStart);
			       }
		    }); 
			txtFinish.setOnClickListener(new View.OnClickListener() {       		
			       public void onClick(View v) { 
			    	   showTimeDialog(hourFinishMes, txtFinish);
			       }
		    }); 				
			AlertDialog.Builder myDialog = new  AlertDialog.Builder(this);		    	
		    myDialog.setIcon(R.drawable.clock)
	     	       .setTitle("Επιλογή Ωραρίου ")  
	     	       .setMessage("Για να ορίσετε την ώρα αγγίξτε το αντίστοιχο πλαίσιο ") 	       
                   .setView(timeEntryView)
		           .setPositiveButton("OK",  new DialogInterface.OnClickListener() {				
			          public void onClick(DialogInterface dialog, int which) {
			        	  //αν ο χρήστης συμπλήρωσε και τις 2 ώρες (Εναρξη, Λήξης)
			        	  if(checkUserChoices(txtStart.getText().toString(), txtFinish.getText().toString())){  
			        	    String newTime =  txtStart.getText().toString() +"-"+ txtFinish.getText().toString();
				            adapter.add(newTime);
				            int lastPos = adapter.getCount();
				            spinner.setSelection(lastPos-1);
			        	  }
			        	  else
			        		spinner.setSelection(0);
			          }
		           })   	           
	              .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {				
		              public void onClick(DialogInterface dialog, int which) {
		        	      spinner.setSelection(0);
		              }
		          })
	              .show();		           
	  }//end merthod
	   
	 //--------------------- CREATE TIME_PICKER_DIALOG -------------------------------
	 /*
	  * Η μέθοδος αυτή δημιουργεί ένα dialog με TimePicker που επιστρέφει
	  * στο προηγούμενο Dialog σε αντίστοιχο EditText (ή TextView) 
	  * την ώρα που επέλεξε ο χρήστης
	  */
	  private void showTimeDialog(String mes, final TextView v){
	    	
	    	timePicker = new TimePicker(this); //δημιουργεί αντικείμενο DatePicker        
	    	timePicker.setIs24HourView(true);  //θέτει 24ωρη ώρα
	    	//create Dialog
	    	AlertDialog.Builder builder = new  AlertDialog.Builder(this);		    	
	     	builder.setIcon(R.drawable.clock)
	     	       .setTitle(mes)  
	     	       .setView(timePicker)
	   	           .setPositiveButton("OK",  new DialogInterface.OnClickListener() {				
					    public void onClick(DialogInterface dialog, int which) {
						    // TODO Auto-generated method stub
					    	int h = timePicker.getCurrentHour();
					    	int m = timePicker.getCurrentMinute();
					    	String pickerHour = h < 10 ? "0"+h : ""+h;
					    	String pickerMin  = m < 10 ? "0"+m : ""+m;
					    	String selectedHour  = pickerHour+":"+pickerMin;
					    	v.setText(selectedHour); 
					    }
				    })   	           
	   	           .setNegativeButton("Cancel", null)
	   	           .show();      	
	  } //τελος μεθόδου 
	  //---------------------------------------------------------------
	  /*
	   * Η μέθοδος αυτή ελέγχει αν συμπλήρψσε ο χρήστης και τα 2 πεδία με
	   * τις ώρες έναρξης και λήξης βάρδιας
	   */
	  private boolean checkUserChoices(String startHour, String finishHour){
		    String hourMess_1 = "Δεν Έπιλέξατε Ώρα Έναρξης Βάρδιας";
		    String hourMess_2 = "Δεν Έπιλέξατε Ώρα Λήξης Βάρδιας";
		    if(startHour.equals("")){
		    	Toast t = Toast.makeText(getApplicationContext(), hourMess_1, Toast.LENGTH_SHORT);
		    	t.setGravity(Gravity.BOTTOM, t.getXOffset() / 2, t.getYOffset() * 2);
		    	t.show();
		    	return false;
		    }
		    if(finishHour.equals("")){
		    	Toast t = Toast.makeText(getApplicationContext(), hourMess_2, Toast.LENGTH_SHORT);
		    	t.setGravity(Gravity.BOTTOM, t.getXOffset() / 2, t.getYOffset() * 2);
		    	t.show();
		    	return false;
		    }			    
	        return true;
	  }//end method 
    //--------------------------------------------------------------------------- 
    //Εμφανίζει Dialog με μήνυμα Αποτυχίας Εισαγωγής Βαρδιών
    public void warningFailureShiftInsertion(String errorMessage){				
  				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
  				String defaultMessage = "\nΓια Επιστροφή Πατήστε \'ΟΚ\'"; 		 
  			    // Dialog Τίτλος
  			    alertDialog.setTitle("Αποτυχία Εισαγωγής");		 
  			    // Μήνυμα του Dialog
  			    alertDialog.setMessage(errorMessage + defaultMessage);		 
  			    // Θέτει εικόνα των ρυθμίσεων στο Dialog
  			    alertDialog.setIcon(R.drawable.warning);	
  			    alertDialog.setCancelable(false);
  			    //Αν πατηθεί το ΟΚ button κλείνει το Dialog
  			    alertDialog.setPositiveButton("ΟΚ", new DialogInterface.OnClickListener() {
  			          public void onClick(DialogInterface dialog, int choice) {            	    
  					       dialog.cancel(); //κλείνει το dialog 
  					       finish();        //κλείνει τη φόρμα αι επιστρέφει στην αρχική
  			          }
  			    });					   		       
  			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
  	} //end method
    //--------------------------------------------------------------------------
    //Εμφανίζει Dialog με μήνυμα Αποτυχίας Εισαγωγής μιας Βάρδιας 
    //για συγκεκριμένη ημερομηνία
    public boolean warningFailureShifDateInsertion(long dateMilisec){	
    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
  				String defaultMessage = "\nΓια Επιστροφή Πατήστε \'ΟΚ\'"; 
  				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
  				Calendar date = Calendar.getInstance();
  				date.clear();
  				date.setTimeInMillis(dateMilisec);
  				String printDate = df.format(date.getTime());
  			    // Dialog Τίτλος
  			    alertDialog.setTitle("Αποτυχία Εισαγωγής Βάρδιας : "+ printDate);		 
  			    // Μήνυμα του Dialog
  			    alertDialog.setMessage("Υπάρχει ήδη Βάρδια για : "+ printDate + defaultMessage);		 
  			    // Θέτει εικόνα των ρυθμίσεων στο Dialog
  			    alertDialog.setIcon(R.drawable.error_icon);	
  			    alertDialog.setCancelable(false);
  			    //Αν πατηθεί το ΟΚ button κλείνει το Dialog
  			    alertDialog.setPositiveButton("ΟΚ", new DialogInterface.OnClickListener() {
  			          public void onClick(DialogInterface dialog, int choice) {            	    
  					       dialog.cancel(); //κλείνει το dialog  					      	       
  			          }
  			    });					   		       
  			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
  			    return false;
  	} //end method
    //----------------------------------------------------------------------
    //-----------------------------------------------------------------
	/*Εμφανίζει Dialog το οποίο παίρνει ως παραμέτρους :
	 * το τίτλο και το μήνυμα προβολής στο χρήστη
	 */
	public void showingDialogWithMessage(String title, String errorMessage, int iconResId){				
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			String defaultMessage = "\nΓια Επιστροφή Πατήστε \'ΟΚ\'"; 		 
		    // Dialog Τίτλος
		    alertDialog.setTitle(title);		 
		    // Μήνυμα του Dialog
		    alertDialog.setMessage(errorMessage + defaultMessage);		 
		    // Θέτει εικόνα των ρυθμίσεων στο Dialog
		    alertDialog.setIcon(iconResId);	
		    alertDialog.setCancelable(false);
		    //Αν πατηθεί το ΟΚ button κλείνει το Dialog
		    alertDialog.setPositiveButton("ΟΚ", new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int choice) {            	    
				       dialog.cancel(); //κλείνει το dialog   					      
		          }
		    });					   		       
		    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
    } //end method	
	//---------------------------- ΒΟΗΘΕΙΑ ---------------------------
		/*Εμφανίζει Dialog με Πληροφορίες Συμπλήρωσης της φόρμας  
		 *για συγκεκριμένη ημερομηνία
		 */
		public void showingHelpMessage(){	
			 	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			 	    Spanned defaultMessage = Html.fromHtml("Για Επιστροφή Πατήστε \'ΟΚ\'"); 
			 		Spanned helpMes  = Html.fromHtml( 
			 		   "Για να συμπληρώσετε τις τιμές της φόρμας αρχικά πατήστε το πλήκτρο : " +
			 		   "<font color=\"blue\">\'Ανάθεση\'</font> " +
			 		   "ή αγγίξτε το επάνω αριστερά με την ένδειξη : " +
			 		   "<font color=\"#D2691E\">\'Πατήστε Εδώ\'</font>.<br>" +
			 		   "<font color=\"red\"><b>1. </b></font>" +
			 		   "Στη φόρμα Ημερολογίου που ανοίγει επιλέγετε την Ημερομηνία έναρξης " +
			 		   "του προγράμματος διάρκειας 7 ημερών και πατήστε : " +
			 		   "<font color=\"#008B00\"><b>\'OK\'</b></font>.<br>"+
			 		   "<font color=\"red\"><b>2. </b></font>" +
			 		   "Οταν κλείσει η φόρμα Ημερολογίου και επιστρέψεται στην Εισαγωγή Βαρδιών " +
			 		   "οι <font color=\"blue\">Ημερομηνίες</font>, οι " +
			 		   "<font color=\"blue\">Ημέρες</font> Εβδομάδος και τα \'Check\' πεδία: " +
			 		   "<font color=\"blue\">\'Κυριακή\'</font> και " +
			 		   "<font color=\"blue\">\'Αργία\'</font> " +
			 		   "συμπληρώνονται και τσεκάρονται αυτόματα. Η εφαρμογή εντοπίζει και τις κινητές αργίες.<br>" +
			 		   "<font color=\"red\"><b>3. </b></font>" +
			 		   "Επιλέξτε το ωράριο βάρδιας για κάθε Ημερομηνία αγγίζοντας " +
			 		   "το πλήκτρο με την ένδειξη : " +
			 		   "<font color=\"#D2691E\">\'Επέλεξε\'</font><br>" +
			 		   "<font color=\"red\"><b>4. </b></font>" +
			 		   "Αν το ωράριο δεν υπάρχει στις διαθέσιμες τιμές Βάρδιας, " +
			 		   "πατήστε την ένδειξη " +
			 		   "<font color=\"#D2691E\"><b>\'ΑΛΛΟ\'</b></font> " +
			 		   "και στη φόρμα που ανοίγει " +
			 		   "επιλέξτε το νέο Ωράριο και πατήστε " +
			 		   "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>.<br> " +
			 		   "<font color=\"red\"><b>5. </b></font>" +
			 		   "Δεν χρειάζεται να πληκτρολογήσετε το ίδιο ωράριο περισσότερες από μία φορές " +
			 		   "αφού μετά την εισαγωγή του θα εμφανίζεται σε όλα τα υπόλοιπα πεδία βαρδιών.<br>" +
			 		   "<font color=\"red\"><b>6. </b></font>" +
			 		   "Αφού συμπληρώσετε όλα τα πεδία πατήστε : " +
			 		   "<font color=\"#008B00\"><b>\'OK\'</b></font>.<br>" +
			 		   "<font color=\"red\"><b>7. </b></font>" +
			 		   "Αν θέλετε να διαγράψετε όλες τις τιμές πατήστε: " +
			 		   "<font color=\"#FF00FF\">\'Διαγραφή\'</font>.<br><br>" + defaultMessage);
			 		 Spanned buttonName  = Html.fromHtml("<font color=\"#008B00\"><b>OK</b></font>");
			 		 // Dialog Τίτλος
			 		 alertDialog.setTitle("Οδηγίες");		 
			 		 // Μήνυμα του Dialog
			 		 alertDialog.setMessage(helpMes);		 
			 		 // Θέτει εικόνα των ρυθμίσεων στο Dialog
			 		 alertDialog.setIcon(R.drawable.info_icon);	
			 		 alertDialog.setCancelable(false);
			 		 //Αν πατηθεί το ΟΚ button κλείνει το Dialog
			 		 alertDialog.setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
			 		 public void onClick(DialogInterface dialog, int choice) {            	    
			 		     dialog.cancel(); //κλείνει το dialog  					      	       
			 		 }
			 		});					   		       
			 		alertDialog.show(); // Εμφάνιση του  Alert dialog Message	  			    
		  }//end method	
}//end class
