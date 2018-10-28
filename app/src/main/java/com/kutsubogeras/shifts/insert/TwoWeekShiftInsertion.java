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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.DatePicker;
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
public class TwoWeekShiftInsertion extends Activity {
	//variables
	private FrameLayout frameLayout;
	private EditText date1, date2, date3, date4, date5, date6, date7, date8;
	private EditText date9, date10, date11, date12, date13, date14;
	private EditText weekDay1, weekDay2, weekDay3, weekDay4, weekDay5;
	private EditText weekDay6, weekDay7, weekDay8, weekDay9, weekDay10;
	private EditText weekDay11, weekDay12, weekDay13, weekDay14;
	private Spinner shift1, shift2, shift3, shift4, shift5, shift6;
	private Spinner shift7, shift8, shift9, shift10, shift11 ;
	private Spinner shift12, shift13, shift14;
	private CheckBox sunday1, sunday2, sunday3, sunday4, sunday5;
	private CheckBox sunday6, sunday7, sunday8, sunday9, sunday10;
	private CheckBox sunday11, sunday12, sunday13, sunday14;
	private CheckBox holiday1, holiday2, holiday3, holiday4, holiday5;
	private CheckBox holiday6, holiday7, holiday8, holiday9, holiday10;
	private CheckBox holiday11, holiday12, holiday13, holiday14;
	private Button   buttonAddDates;
	private Button   buttonOK;
	private Button   buttonReset;
	//date variables
	private final int START_DATE_FLAG = 10;  //flag  για αναγνώρηση της καλούσας activity(μεθόδου)	
	private String  selectedDate = null;
	private Calendar myCalendar;
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
	private ArrayList<String>   holidaysNames    = new ArrayList<String>();   // οι ονομασίες των Αργιών
	private ArrayList<String>   holidaysValues   = new ArrayList<String>();   // Ονομασία της Αργίας που εισάγενται στη στήλη σχόλια του Shift
	private ArrayAdapter<String> dataAdapter1, dataAdapter2, dataAdapter3, dataAdapter4, dataAdapter5, dataAdapter6,
	                             dataAdapter7, dataAdapter8, dataAdapter9, dataAdapter10, dataAdapter11, dataAdapter12,
	                             dataAdapter13, dataAdapter14;
	private TimePicker  timePicker;
	private ArrayList<Integer> backgrounds = new ArrayList<Integer>();
	private String systemLanguage = "";    //η επιλεγμένη γλώσσα του λειτουργικού συστήματος(android)
	private String languagePreference =""; //η προτίμηση του χρήστη στη γλώσσα προβολής δεδομένων στις Ρυθμίσεις
	private static SharedPreferences sharedPref;
	private Context context;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_two_weeks_shift_insertion);
		
		context = this;
		// αρχικοποίηση μεταβλητών
		//έλεγχος της επιλεγμένης γλωσσας του λειτουργικού Android
    	systemLanguage = Locale.getDefault().getLanguage().toString();
    	//-------------- check user languge in settings -----------------
    	sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    	languagePreference = sharedPref.getString(SettingsShifts.KEY_SELECTED_LANGUGE, "");
    	
		this.setShiftValuesInArrayTable(ShiftValues); // θέτει τις τιμές βαρδιών στον πίνακα
		this.setBackroundIcons();
		this.initalizeComponets(); // αρχικοποίηση συστατικών στοιχείων φόρμας
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
		  		selectedDate = data.getStringExtra(DiaryView.SELECTED_DAY);		  		
		  		this.getDateValuesFromStringDate(selectedDate);
		  		Toast tost = Toast.makeText(this, addDateReciveMessage+selectedDate, Toast.LENGTH_LONG); 
			    tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
			    tost.show();
		  	 }		  	 	
	}//end method 
	//========================= Menu ==============================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.two_weeks_shift_insertion, menu);
		menu.clear();
		menu.add(0, R.id.item1_two_weeks_shift_insertion, 0, "Βοήθεια")
            .setIcon(android.R.drawable.ic_menu_help);		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
		     case R.id.item1_two_weeks_shift_insertion: 
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
    	date8    = (EditText)findViewById(R.id.editTextShiftDate_8);
    	date9    = (EditText)findViewById(R.id.editTextShiftDate_9);
    	date10   = (EditText)findViewById(R.id.editTextShiftDate_10);
    	date11   = (EditText)findViewById(R.id.editTextShiftDate_11);
    	date12   = (EditText)findViewById(R.id.editTextShiftDate_12);
    	date13   = (EditText)findViewById(R.id.editTextShiftDate_13);
    	date14   = (EditText)findViewById(R.id.editTextShiftDate_14);
    	weekDay1 = (EditText)findViewById(R.id.editTextShiftWeekDay_1);
    	weekDay2 = (EditText)findViewById(R.id.editTextShiftWeekDay_2);
    	weekDay3 = (EditText)findViewById(R.id.editTextShiftWeekDay_3);
    	weekDay4 = (EditText)findViewById(R.id.editTextShiftWeekDay_4);
    	weekDay5 = (EditText)findViewById(R.id.editTextShiftWeekDay_5);
    	weekDay6 = (EditText)findViewById(R.id.editTextShiftWeekDay_6);
    	weekDay7 = (EditText)findViewById(R.id.editTextShiftWeekDay_7);
    	weekDay8 = (EditText)findViewById(R.id.editTextShiftWeekDay_8);
    	weekDay9 = (EditText)findViewById(R.id.editTextShiftWeekDay_9);
    	weekDay10= (EditText)findViewById(R.id.editTextShiftWeekDay_10);
    	weekDay11= (EditText)findViewById(R.id.editTextShiftWeekDay_11);
    	weekDay12= (EditText)findViewById(R.id.editTextShiftWeekDay_12);
    	weekDay13= (EditText)findViewById(R.id.editTextShiftWeekDay_13);
    	weekDay14= (EditText)findViewById(R.id.editTextShiftWeekDay_14);
    	shift1   = (Spinner)findViewById(R.id.SpinnerShiftValue_1);
    	shift2   = (Spinner)findViewById(R.id.SpinnerShiftValue_2);
    	shift3   = (Spinner)findViewById(R.id.SpinnerShiftValue_3);
    	shift4   = (Spinner)findViewById(R.id.SpinnerShiftValue_4);
    	shift5   = (Spinner)findViewById(R.id.SpinnerShiftValue_5);
    	shift6   = (Spinner)findViewById(R.id.SpinnerShiftValue_6);
    	shift7   = (Spinner)findViewById(R.id.Spinner_ShiftValue_7);
    	shift8   = (Spinner)findViewById(R.id.SpinnerShiftValue_8);
    	shift9   = (Spinner)findViewById(R.id.SpinnerShiftValue_9);
    	shift10  = (Spinner)findViewById(R.id.SpinnerShiftValue_10);
    	shift11  = (Spinner)findViewById(R.id.SpinnerShiftValue_11);
    	shift12  = (Spinner)findViewById(R.id.SpinnerShiftValue_12);
    	shift13  = (Spinner)findViewById(R.id.SpinnerShiftValue_13);
    	shift14  = (Spinner)findViewById(R.id.SpinnerShiftValue_14);
    	sunday1  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_1);
    	sunday2  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_2);
    	sunday3  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_3);
    	sunday4  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_4);
    	sunday5  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_5);
    	sunday6  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_6);
    	sunday7  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_7);
    	sunday8  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_8);
    	sunday9  = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_9);
    	sunday10 = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_10);
    	sunday11 = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_11);
    	sunday12 = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_12);
    	sunday13 = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_13);
    	sunday14 = (CheckBox)findViewById(R.id.CheckBoxShiftSunday_14);
    	holiday1 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_1);
    	holiday2 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_2);
    	holiday3 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_3);
    	holiday4 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_4);
    	holiday5 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_5);
    	holiday6 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_6);
    	holiday7 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_7);
    	holiday8 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_8);
    	holiday9 = (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_9);
    	holiday10= (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_10);
    	holiday11= (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_11);
    	holiday12= (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_12);
    	holiday13= (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_13);
    	holiday14= (CheckBox)findViewById(R.id.CheckBoxShiftHoliday_14);
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
	//-----------------------Show Dialog Calendar Choice----------------------
	   /*
	    * Εμφανίζει Dialog με τις διαθέσιμες επιλογές Εισαγωγής Βαρδιών(1 ή 7 ημέρες)
	    */
	 private void displayDialogWithDateChoice(){
		   
	    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
	    	        String[] userChoices = {"Από Ημερολόγιο",
	  						                "Με Επιλογή Ημερ/νίας"};
	    	        String[] userChoices_en = {"From Month Calendar",
				                               "From Date Picker"};
	    	        final String tostMessage = "Δεν Επιλέξατε Τρόπο Εισαγωγής";
	    	        final String tostMessage_en = "You didn't select eny";
	    	        final String message = (systemLanguage.equals("el") || languagePreference.equals("el"))?
	    	        		                tostMessage : tostMessage_en ;
	    	        // Θέτει εικόνα των ρυθμίσεων στο Dialog
	  			    alertDialog.setIcon(R.drawable.calendar);	
	  			    	 
	  			    // Εμφάνιση Επιλογών του Dialog	στο χρήστη
	  			    if(systemLanguage.equals("el") || languagePreference.equals("el")){ //αν ο χρήστης επέλεξε Ελληνική γλώσσα
	  			       alertDialog.setTitle("Εισαγωγή Ημερομηνίας");
	  			       alertDialog.setSingleChoiceItems(userChoices, -1, null);			  			       
	  			    }
				    else if (systemLanguage.equals("en") || languagePreference.equals("en")){ // αν επέλεξε Αγγλική γλώσσα
				       alertDialog.setTitle("Select Date");
	  			       alertDialog.setSingleChoiceItems(userChoices_en, -1, null);			  			       
				    }
	  			    alertDialog.setInverseBackgroundForced(false);
	  			    alertDialog.setCancelable(false);
	  			    //Αν πατηθεί το ΟΚ button κλείνει το Dialog
	  			    alertDialog.setPositiveButton("ΟΚ", new DialogInterface.OnClickListener() {
	  			          public void onClick(DialogInterface dialog, int choice) {  			        	   
	  		                   int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
	  		                   if(selectedPosition == -1){ //αν δεν έχει γίνει καμία επιλογή
	  		                	 dialog.cancel();		  		                	
	  		                	 Toast tost = Toast.makeText(context, message, Toast.LENGTH_SHORT); 
	  		             	     tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 4);
	  		             	     tost.show();
	  		                   }
	  		                   if(selectedPosition == 0){  //αν επιλέξει βάρδια για 1 ημέρα
	  		                	 dialog.dismiss();
	  		                	 //δημιουργεί ένα Intent για την εισαγωγή Ημερομηνιών
	  		  		             Intent shiftIntent = new Intent(TwoWeekShiftInsertion.this , DiaryView.class);
	  		  		             shiftIntent.setFlags(START_DATE_FLAG);
	  		  		             startActivityForResult(shiftIntent, START_DATE_FLAG);
	  		                   }
	  		                   if(selectedPosition == 1){ //αν επιλέξει βάρδιες για 7 ημέρες
	  		                	 dialog.dismiss();
	  		                	 showDialogWithDatePicker();
	  		                   }	  		                   
	  			          }
	  			    });					   		       
	  			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message		  			   
	  	} //end method
	//----------------------------- DIALOG WITH DATEPICKER ------------------------------
  	 /**
  	  * δημιουργεί ένα dialog με editText για εισαγωγή προγραμματισμένων εργασιών
  	  */
     @SuppressLint("NewApi")
  	 private void showDialogWithDatePicker(){ 
  		    LayoutInflater factory = LayoutInflater.from(context);	
  		    View dialogView = factory.inflate(R.layout.dialog_selection_of_date, null); 
  		    final DatePicker datePic = (DatePicker)dialogView.findViewById(R.id.datePicker_dateSelect);   		    
  			AlertDialog.Builder myDialog = new  AlertDialog.Builder(context);
  			datePic.setCalendarViewShown(false);  			
  		    myDialog.setIcon(R.drawable.calendar);
  	     	myDialog.setTitle("Επιλογή Ημερομηνίας");  
  	     	myDialog.setView(dialogView);  
            myDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
  				   @Override
  				   public void onClick(DialogInterface dialog, int choice) {
  					      //call method: insert into db task
  					      int year  = datePic.getYear();    
  					   	  int month = datePic.getMonth();	
  					   	  int day   = datePic.getDayOfMonth();
  					   	  String sDay = String.valueOf((day <10)? "0"+day : day);
  					   	  String sMonth = String.valueOf((month <9)? "0"+(month+1) : (month+1));
  					   	  selectedDate = sDay+"-"+sMonth+"-"+year;
  					   	  myCalendar = Calendar.getInstance();    //Στιγμιότυπο της τρέχουσας ημερομηνίας
  				    	  myCalendar.clear();                     //καθαρίζει τις τιμές της τρέχουσας ημερομηνίας
  				    	  myCalendar.set(year, month, day);	//θέτει νέα ημερομηνία     	
  				    	  setOfficialHolidays();             //θέτει τιμές στον πίνακαμε τις επίσημες αργίες 	
  				    	  findDatesAndWeekDays(myCalendar);  //υπολογίζει τις επόμενες ημερομηνίες 
  				    	  setDatesAndWeekDays(); 
  					   	  dialog.cancel();							    
  				   }
  			 });
  			 myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
  					@Override
  					public void onClick(DialogInterface dialog, int choice) {				         
  						   dialog.cancel(); 						    	   
  					}
  			 });
             myDialog.show();             
  	   }//end merthod
    //------------------------- LISTENERS --------------------------------
    private void createComponensListeners(){
    	
    	date1.setOnClickListener(new View.OnClickListener() {       		
		       public void onClick(View v) {       		     
		       		 //δημιουργεί ένα Intent για την εισαγωγή εργαζόμενου
		       		 Intent shiftIntent = new Intent(TwoWeekShiftInsertion.this , DiaryView.class);
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
	    	          selectedItemView.setSoundEffectsEnabled(true);
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
    	//Spinner για την 8η βάρδια
    	shift8.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift8.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift8, dataAdapter8);			    	          
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
    	//Spinner για την 9η βάρδια
    	shift9.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift9.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	        showDialogWithEditText(shift9, dataAdapter9);			    	          
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
    	//Spinner για την 10η βάρδια
    	shift10.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift10.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	        showDialogWithEditText(shift10, dataAdapter10);			    	          
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
    	//Spinner για την 11η βάρδια
    	shift11.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift11.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift11, dataAdapter11);			    	          
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
    	//Spinner για την 12η βάρδια
    	shift12.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift12.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift12, dataAdapter12);			    	          
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
    	//Spinner για την 13η βάρδια
    	shift13.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift13.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift13, dataAdapter13);			    	          
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
    	//Spinner για την 14η βάρδια
    	shift14.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	        if(position >0){
	    	          String shiftValue = shift14.getItemAtPosition(position).toString();
	    	          selectedItemView.setBackgroundColor(Color.CYAN);
	    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	         showDialogWithEditText(shift14, dataAdapter14);			    	          
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
		    	   //δημιουργεί ένα dialog για την επιλογή τρόπου ειαγωγής Ημερομηνιών
		    	   displayDialogWithDateChoice();		    	   
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
    	String errorFirstDateMessage = "Δεν Καθορίσατε Ημερομηνίες και Ημέρες";
    	String errorShiftMessage_1   = "Δεν Συμπληρώσατε Βάρδια για την 1η Ημερομηνία";
    	String errorShiftMessage_2   = "Δεν Συμπληρώσατε Βάρδια για την 2η Ημερομηνία";
    	String errorShiftMessage_3   = "Δεν Συμπληρώσατε Βάρδια για την 3η Ημερομηνία";
    	String errorShiftMessage_4   = "Δεν Συμπληρώσατε Βάρδια για την 4η Ημερομηνία";
    	String errorShiftMessage_5   = "Δεν Συμπληρώσατε Βάρδια για την 5η Ημερομηνία";
    	String errorShiftMessage_6   = "Δεν Συμπληρώσατε Βάρδια για την 6η Ημερομηνία";
    	String errorShiftMessage_7   = "Δεν Συμπληρώσατε Βάρδια για την 7η Ημερομηνία";
    	String errorShiftMessage_8   = "Δεν Συμπληρώσατε Βάρδια για την 8η Ημερομηνία";
    	String errorShiftMessage_9   = "Δεν Συμπληρώσατε Βάρδια για την 9η Ημερομηνία";
    	String errorShiftMessage_10  = "Δεν Συμπληρώσατε Βάρδια για την 10η Ημερομηνία";
    	String errorShiftMessage_11  = "Δεν Συμπληρώσατε Βάρδια για την 11η Ημερομηνία";
    	String errorShiftMessage_12  = "Δεν Συμπληρώσατε Βάρδια για την 12η Ημερομηνία";
    	String errorShiftMessage_13  = "Δεν Συμπληρώσατε Βάρδια για την 13η Ημερομηνία";
    	String errorShiftMessage_14  = "Δεν Συμπληρώσατε Βάρδια για την 14η Ημερομηνία";
    	
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
     	if(shift8.getSelectedItem().toString().equals("Επέλεξε")){
      	   Toast tost = Toast.makeText(this, errorShiftMessage_8, Toast.LENGTH_SHORT); 
   	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
   	       tost.show();
   	       return false;
      	}
     	if(shift9.getSelectedItem().toString().equals("Επέλεξε")){
       	   Toast tost = Toast.makeText(this, errorShiftMessage_9, Toast.LENGTH_SHORT); 
    	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
    	       tost.show();
    	       return false;
       	}
     	if(shift10.getSelectedItem().toString().equals("Επέλεξε")){
       	   Toast tost = Toast.makeText(this, errorShiftMessage_10, Toast.LENGTH_SHORT); 
    	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
    	       tost.show();
    	       return false;
       	}
     	if(shift11.getSelectedItem().toString().equals("Επέλεξε")){
       	   Toast tost = Toast.makeText(this, errorShiftMessage_11, Toast.LENGTH_SHORT); 
    	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
    	       tost.show();
    	       return false;
       	}
      	if(shift12.getSelectedItem().toString().equals("Επέλεξε")){
        	   Toast tost = Toast.makeText(this, errorShiftMessage_12, Toast.LENGTH_SHORT); 
     	   tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
     	   tost.show();
     	   return false;
        	}
      	if(shift13.getSelectedItem().toString().equals("Επέλεξε")){
        	   Toast tost = Toast.makeText(this, errorShiftMessage_13, Toast.LENGTH_SHORT); 
     	   tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
     	   tost.show();
     	   return false;
        	}
      	if(shift14.getSelectedItem().toString().equals("Επέλεξε")){
            Toast tost = Toast.makeText(this, errorShiftMessage_14, Toast.LENGTH_SHORT); 
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
    	weekDays.add(weekDay8.getText().toString());
    	weekDays.add(weekDay9.getText().toString());
    	weekDays.add(weekDay10.getText().toString());
    	weekDays.add(weekDay11.getText().toString());
    	weekDays.add(weekDay12.getText().toString());
    	weekDays.add(weekDay13.getText().toString());
    	weekDays.add(weekDay14.getText().toString());
    	//ωράρια βαρδιών
    	shiftTimes.add(shift1.getSelectedItem().toString());
    	shiftTimes.add(shift2.getSelectedItem().toString());
    	shiftTimes.add(shift3.getSelectedItem().toString());
    	shiftTimes.add(shift4.getSelectedItem().toString());
    	shiftTimes.add(shift5.getSelectedItem().toString());
    	shiftTimes.add(shift6.getSelectedItem().toString());
    	shiftTimes.add(shift7.getSelectedItem().toString());
    	shiftTimes.add(shift8.getSelectedItem().toString());
    	shiftTimes.add(shift9.getSelectedItem().toString());
    	shiftTimes.add(shift10.getSelectedItem().toString());
    	shiftTimes.add(shift11.getSelectedItem().toString());
    	shiftTimes.add(shift12.getSelectedItem().toString());
    	shiftTimes.add(shift13.getSelectedItem().toString());
    	shiftTimes.add(shift14.getSelectedItem().toString()); 
    	//Κυριακές
    	sundays.add(sunday1.isChecked());
    	sundays.add(sunday2.isChecked());
    	sundays.add(sunday3.isChecked());
    	sundays.add(sunday4.isChecked());
    	sundays.add(sunday5.isChecked());
    	sundays.add(sunday6.isChecked());
    	sundays.add(sunday7.isChecked());
    	sundays.add(sunday8.isChecked());
    	sundays.add(sunday9.isChecked());
    	sundays.add(sunday10.isChecked());
    	sundays.add(sunday11.isChecked());
    	sundays.add(sunday12.isChecked());
    	sundays.add(sunday13.isChecked());
    	sundays.add(sunday14.isChecked());
    	//Αργίες
    	holidays.add(holiday1.isChecked());
    	holidays.add(holiday2.isChecked());
    	holidays.add(holiday3.isChecked());
    	holidays.add(holiday4.isChecked());
    	holidays.add(holiday5.isChecked());
    	holidays.add(holiday6.isChecked());
    	holidays.add(holiday7.isChecked());
    	holidays.add(holiday8.isChecked());
    	holidays.add(holiday9.isChecked());
    	holidays.add(holiday10.isChecked());
    	holidays.add(holiday11.isChecked());
    	holidays.add(holiday12.isChecked());
    	holidays.add(holiday13.isChecked());
    	holidays.add(holiday14.isChecked());
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
    	   //εισάγει επαναληπτικά τα στοιχεία βαρδιών για 14 διαδοχικές ημερομηνίες
    	   for(int i=0; i<shiftDates.size(); i++){
    	      dateRows = db.selectDateValueFromTable("Shift", shiftDates.get(i).getTimeInMillis());
    	      if(dateRows ==0){ //αν δεν είναι ήδη καταχωρημενη η ημερομηνία
    		     insertedRow = db.InsertIntoShiftValues(userId,
    		    		          shiftDates.get(i).getTimeInMillis(),    		    		  
    		    		          weekDays.get(i),
    		    		          shiftTimes.get(i),
    				              sundays.get(i),
    				              holidays.get(i),
    				              holidaysValues.get(i),
    				              null,
    				              null,
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
    	   Toast tost = Toast.makeText(this, 
    			successShiftInsertMessage+insertedRow+" Βάρδιες", Toast.LENGTH_LONG); 
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
    	final int DEFAULT_TIME = 445;  //το σύνολο εργάσιμων λεπτών ανά ημέρα για τραπεζουπάλληλο (ακριβώς 444 λεπτά)
    	int difference = 0;
    	int extraMin = 0;
    	for(int i=0; i<extra.size(); i++){
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
    		  extraMin = (difference > DEFAULT_TIME) ? (difference - DEFAULT_TIME) : 0;    		  
    		}//end if
    		else
    		  extraMin = 0;    		
    		extraTimes.add(extraMin);
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
        dataAdapter8 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter8.setDropDownViewResource(android.R.layout.simple_list_item_single_choice );
        dataAdapter9 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter9.setDropDownViewResource(android.R.layout.simple_list_item_single_choice ); 
        dataAdapter10 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter10.setDropDownViewResource(android.R.layout.simple_list_item_single_choice );
        dataAdapter11 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter11.setDropDownViewResource(android.R.layout.simple_list_item_single_choice );
        dataAdapter12 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter12.setDropDownViewResource(android.R.layout.simple_list_item_single_choice );
        dataAdapter13 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter13.setDropDownViewResource(android.R.layout.simple_list_item_single_choice ); 
        dataAdapter14 = new ArrayAdapter<String>( this, 
                android.R.layout.simple_spinner_dropdown_item, ShiftValues);
        dataAdapter14.setDropDownViewResource(android.R.layout.simple_list_item_single_choice ); 
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
        shift8.setAdapter(dataAdapter8);       
        shift8.setSelection(0);
        shift9.setAdapter(dataAdapter9);
        shift9.setSelection(0);    
        shift10.setAdapter(dataAdapter10);       
        shift10.setSelection(0);
        shift11.setAdapter(dataAdapter11);       
        shift11.setSelection(0);
        shift12.setAdapter(dataAdapter12);       
        shift12.setSelection(0);
        shift13.setAdapter(dataAdapter13);       
        shift13.setSelection(0);
        shift14.setAdapter(dataAdapter14);       
        shift14.setSelection(0);   
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
    	this.setOfficialHolidays();             //θέτει τιμές στον πίνακαμε τις επίσημες αργίες 	
    	this.findDatesAndWeekDays(myCalendar);  //υπολογίζει τις επόμενες ημερομηνίες 
    	this.setDatesAndWeekDays();             //θέτει τιμές στα πεδία των επόμενων ημερομηνιών 
    }//end method
     
    //--------------------------------------------------------------------
    /*
     * Ανάθεση τιμών στον πίνακα με τις Επίσιμες Αργίες
     * Γίνεται πριν τον έλεγχο αργιών και το τσεκάρισμά τους στη φόρμα
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
    //--------------- ΑΛΓΟΡΙΘΜΟΣ ΥΠΟΛΟΓΙΣΜΟΥ ΗΜΕΡΟΜΗΝΙΑΣ ΠΑΣΧΑ ΚΑΙ ΚΙΝΗΤΩΝ ΑΡΓΙΩΝ ---------------
    /*
     * Υπολογίζει την Ημερομηνία Του Πάσχα με βάση το τρέχων Γρηγοριανό Ημερολόγιο
     * αφού είναι μετακινούμενη αργία.
     * (Εξακολουθεί να υπολογίζεται από την εκκλησία με βάση το Ιουλιανό Ημερολόγιο
     * που είναι το προγενέστερο του Γρηγοριανού). Επίσης από αυτή υπολογίζονται η Καθαρά Δευτέρα
     *  του Αγίου Πνέυματος (40 ημέρες πρίν και μετά αντίστοιχα) καθώς επίσης η Μεγάλη Παρασκευή
     *  και η Δευτέρα του Πάσχα.
     * Ο Αλγόριθμος βασίζεται σε αυτόν του site eortologio.gr 
     * με επιφύλλαξη κάθε νόμιμου δικαιώματος
     */
    private Calendar findEasterHoliday(int year){
    	
    	 String title    ="Λανθασμένη Επιλογή";
    	 String errorMes ="Το Έτος επιλογής σας πρέπει να βρίσκεται"
    	 		+ " μεταξύ των τιμών : 1583 και 4099";
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
    			calYear = (int)Math.floor(year / 100);
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
     *Ορίζει τις επόμενες 13 ημερομηνίες και ημέρες της εβδομάδος 
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
    	
    	for(int i=0; i<13; i++){
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
    		if(i==7)
     		   date8.setText(shiftPrintableDates.get(i));
     		if(i==8)
      		   date9.setText(shiftPrintableDates.get(i));   
     		if(i==9)
      		   date10.setText(shiftPrintableDates.get(i));   
     		if(i==10)
      		   date11.setText(shiftPrintableDates.get(i));   
     		if(i==11)
      		   date12.setText(shiftPrintableDates.get(i));   
     		if(i==12)
      		   date13.setText(shiftPrintableDates.get(i));   
     		if(i==13)
       		   date14.setText(shiftPrintableDates.get(i));   
        }//end for    	
    	//Θέτει τις ημέρες της εβδομάδος και Τσεκάρει τις Κυριακές (checked)
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
    		if(j==7){
    			weekDay8.setText(getDayOfWeek(weekDay));
    			if(weekDay == 1)             //αν είναι Κυριακή
    			   sunday8.setChecked(true);    			
    		}
    		if(j==8){
    			weekDay9.setText(getDayOfWeek(weekDay));
			    if(weekDay == 1)
 			       sunday9.setChecked(true);			    
 		    }
    		if(j==9){
    			weekDay10.setText(getDayOfWeek(weekDay));
			    if(weekDay == 1)
 			       sunday10.setChecked(true);			    
 		    }
    		if(j==10){
    			weekDay11.setText(getDayOfWeek(weekDay));
			    if(weekDay == 1)
 			       sunday11.setChecked(true);			    
 		    } 
    		if(j==11){
    			weekDay12.setText(getDayOfWeek(weekDay)); 
			    if(weekDay == 1)
 			       sunday12.setChecked(true);			    
 		    } 
    		if(j==12){
    			weekDay13.setText(getDayOfWeek(weekDay)); 
			    if(weekDay == 1)
 			       sunday13.setChecked(true);			    
 		    }  
    		if(j==13){
    			weekDay14.setText(getDayOfWeek(weekDay)); 
			    if(weekDay == 1)
 			       sunday14.setChecked(true);			    
 		    }    	
        }//end for 
    	//Τσεκάρισμα των Αργιών (Κινητών και Σταθερών)
    	for (int i=0; i<shiftDates.size(); i++){    		
    		
    		if(i==0 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
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
    	    if(i==7 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
 			       holiday8.setChecked(true);
 		    if(i==8 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday9.setChecked(true);
 		    if(i==9 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday10.setChecked(true);
 		    if(i==10 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday11.setChecked(true);
 		    if(i==11 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday12.setChecked(true);
 		    if(i==12 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday13.setChecked(true);
 		    if(i==13 && isAnOfficialHoliday(shiftDates.get(i)))  //αν είναι επίσημη αργία
	    		   holiday14.setChecked(true); 		    
    	}
    	Log.i("HOLIDAY_VALUES_SIZE", ": "+holidaysValues.size());
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
    		   isHoliday = true;
    		   holidayName = holidaysNames.get(j);    		   
    		   break;
    		}    		
    	}//end for
    	holidaysValues.add(holidayName); //έχει το όνομα της Αργίας ή null
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
    	sunday8.setChecked(false);
    	sunday9.setChecked(false);
    	sunday10.setChecked(false);
    	sunday11.setChecked(false);
    	sunday12.setChecked(false);
    	sunday13.setChecked(false);
    	sunday14.setChecked(false);
    	holiday1.setChecked(false);
    	holiday2.setChecked(false);
    	holiday3.setChecked(false);
    	holiday4.setChecked(false);
    	holiday5.setChecked(false);
    	holiday6.setChecked(false);
    	holiday7.setChecked(false);
    	holiday8.setChecked(false);
    	holiday9.setChecked(false);
    	holiday10.setChecked(false);
    	holiday11.setChecked(false);
    	holiday12.setChecked(false);
    	holiday13.setChecked(false);
    	holiday14.setChecked(false);
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
    	shift8.setSelection(0);
    	shift9.setSelection(0);
    	shift10.setSelection(0);
    	shift11.setSelection(0);
    	shift12.setSelection(0);
    	shift13.setSelection(0);
    	shift14.setSelection(0);
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
    	date8.setText("");   //dates
    	date9.setText("");
    	date10.setText("");
    	date11.setText("");
    	date12.setText("");
    	date13.setText("");
    	date14.setText("");
    	weekDay1.setText(""); //weekdays
    	weekDay2.setText("");
    	weekDay3.setText("");
    	weekDay4.setText("");
    	weekDay5.setText("");
    	weekDay6.setText("");
    	weekDay7.setText("");
    	weekDay8.setText(""); //weekdays
    	weekDay9.setText("");
    	weekDay10.setText("");
    	weekDay11.setText("");
    	weekDay12.setText("");
    	weekDay13.setText("");
    	weekDay14.setText("");
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
  	}//end method
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
		 		   "Για να συμπληρώσετε τις τιμές της φόρμας αρχικά πατήστε το πλήκτρο : \'Ανάθεση\' " +
		 		   "ή αγγίξτε το επάνω αριστερά με την ένδειξη: " +
		 		   "<font color=\"#D2691E\">\'Πατήστε Εδώ\'</font>.<br>" +
		 		   "<font color=\"red\"><b>1.</b></font>" +
		 		   " Στη φόρμα Ημερολογίου που ανοίγει επιλέγετε την Ημερομηνία έναρξης " +
		 		   "του προγράμματος διάρκειας 14 ημερών και πατήστε: " +
		 		   "<font color=\"#008B00\"><b>\'OK\'</b></font>.<br>"+
		 		   "<font color=\"red\"><b>2.</b></font>" +
		 		   " Οταν κλείσει η φόρμα Ημερολογίου και επιστρέψεται στην Εισαγωγή Βαρδιών " +
		 		   "οι <font color=\"blue\">\'Ημερομηνίες\'</font>, οι <font color=\"blue\">\'Ημέρες\'</font>" +
		 		   " Εβδομάδος και τα \'Check\' πεδία: " +
		 		   "<font color=\"blue\">\'Κυριακή\'</font> και " +
		 		   "<font color=\"blue\">\'Αργία\'</font> " +
		 		   "συμπληρώνονται και τσεκάρονται αυτόματα. Η εφαρμογή εντοπίζει και τις κινητές αργίες.<br>" +
		 		   "<font color=\"red\"><b>3.</b></font>" +
		 		   " Επιλέξτε το ωράριο βάρδιας για κάθε Ημερομηνία αγγίζοντας " +
		 		   "το πλήκτρο με την ένδειξη: " +
		 		   "<font color=\"#D2691E\">\'Επέλεξε\'</font><br>" +
		 		   "<font color=\"red\"><b>4.</b></font>" +
		 		   " Αν το ωράριο δεν υπάρχει στις διαθέσιμες τιμές Βάρδιας, " +
		 		   "πατήστε την ένδειξη: " +
		 		   "<font color=\"#D2691E\"><b>\'ΑΛΛΟ\'</b></font> και στη φόρμα που ανοίγει " +
		 		   "επιλέξτε το νέο Ωράριο και πατήστε: " +
		 		   "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>.<br>" +
		 		   "<font color=\"red\"><b>5.</b></font>" +
		 		   " Δεν χρειάζεται να πληκτρολογήσετε το ίδιο ωράριο περισσότερες από μία φορές " +
		 		   "αφού μετά την εισαγωγή του θα εμφανίζεται σε όλα τα υπόλοιπα πεδία βαρδιών.<br>" +
		 		   "<font color=\"red\"><b>6.</b></font>" +
		 		   " Αφού συμπληρώσετε όλα τα πεδία πατήστε : " +
		 		   "<font color=\"#008B00\"><b>\'OK\'</b></font>.<br>" +
		 		   "<font color=\"red\"><b>7.</b></font>" +
		 		   " Αν θέλετε να διαγράψετε όλες τις τιμές πατήστε: " +
		 		   "<font color=\"#FF00FF\">\'Διαγραφή\'</font>.<br><br>"+ defaultMessage);	  
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
