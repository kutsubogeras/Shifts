package com.kutsubogeras.shifts.queries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.kutsubogeras.shifts.MainActivity;
import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.ShiftOfUser;
import com.kutsubogeras.shifts.MainActivity.showQueriesFragment;
import com.kutsubogeras.shifts.R.drawable;
import com.kutsubogeras.shifts.R.id;
import com.kutsubogeras.shifts.R.layout;
import com.kutsubogeras.shifts.R.menu;
import com.kutsubogeras.shifts.data.DBControler;
import com.kutsubogeras.shifts.help.DiaryView;
import com.kutsubogeras.shifts.settings.SettingsShifts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;
/**
 * 
 * @author Thanasis
 * Εμφανίζει τις Ημέρες Αργιών 
 * κατά τις οποίες έχει εργαστεί ο εργαζόμενος
 */
public class ShowWorkedHolidays extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_worked_holidays);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container_show_holidays, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_worked_holidays, menu);
		menu.clear();
		menu.add(0, R.id.item1_show_worked_holiday, 0, "Βοήθεια")
        .setIcon(android.R.drawable.ic_menu_help);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
	     case R.id.item1_show_worked_holiday: 
	    	 this.showingHelpMessage();
	    	 return true;
	     case R.id.action_settings: 
	         return true;			
	     default:
	         return super.onOptionsItemSelected(item);
	}
	}

	//======================== INNER STATIC CLASS FRAGMENT =====================
		/**
		 * A placeholder fragment containing a simple view.
		 */
		public static class PlaceholderFragment extends Fragment {
			
			  //Μεταβλητές Κλάσης			   
	           private TextView queryTitle;
		       private TextView startDate;
		       private TextView endDate;
		       private TextView results;
		       private Button   but_OK;
		       private Button   but_Back;
		       private Button   but_Reset;
		       public  static final int START_DATE_FLAG = 15;
	           public  static final int END_DATE_FLAG   = 16;           
		       private static final String addDateReciveMessage = "Επιλεγμένη Ημερομηνία : ";
		       private String selectedDate = null;
		       private Calendar dateStart;
		       private Calendar dateFinish;
		       private ListView lv;
		       private ArrayList<ShiftOfUser> listOfShifts =null;
		       private String title;		      
		       private Context context;
		       private ArrayList<Integer> backgrounds = new ArrayList<Integer>();
		       
			public PlaceholderFragment() {
			}

			@Override
			public View onCreateView(LayoutInflater inflater,
					                 ViewGroup container,
					                 Bundle savedInstanceState) {
				 View rootView = inflater.inflate(R.layout.fragment_show_worked_holidays,
						                          container, false);
				 queryTitle= (TextView)rootView.findViewById(R.id.textView_ShowHolidays_Title);
			     startDate = (TextView)rootView.findViewById(R.id.textView_ShowHolidays_DateStart);
			     endDate   = (TextView)rootView.findViewById(R.id.textView_ShowHolidays_DateFinish);
			     results   = (TextView)rootView.findViewById(R.id.textView_ShowHolidays_Results);
			     but_OK    = (Button)  rootView.findViewById(R.id.button_ShowHolidays_OK);
			     but_Back  = (Button)  rootView.findViewById(R.id.button_ShowHolidays_Back);
			     but_Reset = (Button)  rootView.findViewById(R.id.button_ShowHolidays_Reset);		     
			     title     = getActivity().getIntent().getStringExtra(
			    		          MainActivity.showQueriesFragment.QUERY_TITLE);
			     queryTitle.setText(title);
			     context = getActivity();
			     this.setBackroundIcons();
			     this.checkPreferentBackround(rootView);
			     this.createComponensAndListeners(); //δημιουργία Listeners
			     
			     lv = (ListView)rootView.findViewById(R.id.listView_ShowHolidays_List);			 
			     lv.setTextFilterEnabled(true);
			     lv.setSoundEffectsEnabled(true);
			     lv.setClickable(true);			        
				 return rootView;
			}
			//--------------------------- ON ACTIVITY RESULT -------------------------------
		    //όταν τερματίσει η κληθήσα activity 	    
		    @Override
		    public void onActivityResult(int requestCode, int resultCode, Intent data) {
					super.onActivityResult(requestCode, resultCode, data);	
					  /*
						 if(resultCode == START_DATE_FLAG) {
							selectedDate = data.getStringExtra(DiaryView.SELECTED_DAY);
							Log.i("DATE_START", selectedDate );
							startDate.setText(selectedDate);
							dateStart = getDateValuesFromStringDate(selectedDate);
					  		Toast tost = Toast.makeText(getActivity(), 
					  				addDateReciveMessage+selectedDate, Toast.LENGTH_LONG); 
						    tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 3);
						    tost.show();
					  	 }	
						 if(resultCode == END_DATE_FLAG) {
							selectedDate = data.getStringExtra(DiaryView.SELECTED_DAY);
							Log.i("END_START", selectedDate );
							endDate.setText(selectedDate);
							dateFinish = getDateValuesFromStringDate(selectedDate);
						  	Toast tost = Toast.makeText(getActivity(), 
						  			addDateReciveMessage+selectedDate, Toast.LENGTH_LONG); 
							tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 3);
							tost.show();
						  }	
						*/	  	 
		    }//end method  
			//------------------ METHODOI --------------------
		    public void createComponensAndListeners(){
			       //Listeners
			       startDate.setOnClickListener(new View.OnClickListener() {       		
			             public void onClick(View v) {   
			            	 /*
			       		      //δημιουργεί ένα Intent για την επιλογή Ημερομηνίας Έναρξης
			       		      Intent shiftIntent = new Intent(getActivity() , DiaryView.class);
			       		      shiftIntent.setFlags(START_DATE_FLAG);
			       		      startActivityForResult(shiftIntent, START_DATE_FLAG);
			       		      */
			            	  showDialogWithDatePicker(startDate);
			             }
			       });    	
			       endDate.setOnClickListener(new View.OnClickListener() {       		
			             public void onClick(View v) {   
			            	 /*
			       		      //δημιουργεί ένα Intent για την επιλογή Ημερομηνίας Έναρξης
			       		      Intent shiftIntent = new Intent(getActivity() , DiaryView.class);
			       		      shiftIntent.setFlags(END_DATE_FLAG);
			       		      startActivityForResult(shiftIntent, END_DATE_FLAG);
			       		      */
			            	  showDialogWithDatePicker(endDate);
			             }
			       }); 
			       but_OK.setOnClickListener(new View.OnClickListener() {       		
			             public void onClick(View v) { 
			            	 if(chechDatesValues())
			       		       //δημιουργεί και εμφανίζει τα αποτελέσματα
			            	   findResults(queryTitle.getText().toString());
			             }
			       });    	  
			       but_Back.setOnClickListener(new View.OnClickListener() {       		
			             public void onClick(View v) {   
			            	 //τερματίζει την activity
			       		      getActivity().finish();
			             }
			       });    	  
			       but_Reset.setOnClickListener(new View.OnClickListener() {       		
			             public void onClick(View v) {       		     
			       		     //κάνει reset τις τιμές ημερομηνιών
			            	 resetValues();		            	 
			             }
			       });   		       
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
			private  void checkPreferentBackround(View layout){
				   //έλεγχος προτίμησης και ανάθεσης του επιλεγμένου του backround
				   SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());;
				   String backPrefer = sharedPref.getString(SettingsShifts.KEY_SELECTED_BACKGROUND, "");
				   if(!backPrefer.equals("")){//αν δεν είναι κενή η προτίμηση background
					   int index = Integer.parseInt(backPrefer);
				       Log.i("BACKROUND", ""+backPrefer);
				       layout.setBackgroundResource(backgrounds.get(index));
				   }
			}		  
		    //----------------------------- CONVERT STRING DATE TO CALENDAR --------------------
	        /*
	         *Αυτή η Μέθοδος καλείται στην OnActivityResault() μέθοδο της Activity
	         *αι θέτει τιμές σε όλα τα πεδία της φόρμας μετά την επιλογή της 1ης ημερομηνίας
	         *από το χρήστη
	         */
	       private Calendar getDateValuesFromStringDate(String date){
	    	
	    	String dateValues[] = date.split("-");   //τεμαχίζει την ημερομηνία με βάση το χαρακτήρα (-)
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
	    	Calendar myCalendar = Calendar.getInstance();    //Στιγμιότυπο της τρέχουσας ημερομηνίας
	    	myCalendar.clear();                     //καθαρίζει τις τιμές της τρέχουσας ημερομηνίας
	    	myCalendar.set(year, (month -1), day);	//θέτει νέα ημερομηνία     	 	
	    	return myCalendar;
	       }//end method 
	     //----------------------------- DIALOG WITH DATEPICKER ------------------------------
	    	 /*
	    	  * δημιουργεί ένα dialog με editText για εισαγωγή προγραμματισμένων εργασιών
	    	  */
	       @SuppressLint("NewApi")
	    	 private void showDialogWithDatePicker(final TextView userDate){ 
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
	    					   	  Calendar myCalendar = Calendar.getInstance();//Στιγμιότυπο ημερομηνίας
	    			    	      myCalendar.clear();  //καθαρίζει τις τιμές της τρέχουσας ημερομηνίας
	    			    	      myCalendar.set(year, month, day);	//θέτει νέα ημερομηνία  
	    			    	      if(userDate.getId() == startDate.getId()){
	    					   		startDate.setText(selectedDate);
	    					   	    dateStart = myCalendar;
	    					   	  }
	    					   	  else if(userDate.getId() == endDate.getId()){
	    					   		endDate.setText(selectedDate);
	    					   	    dateFinish = myCalendar;
	    					   	  }
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
	       //---------------------- CHECK DATES ------------------------------
	       private boolean chechDatesValues(){
	    	   String startDateMes = "Δεν επιλέξατε Ημερομηνία :\n\'Από Ημερομηνία\'";
	    	   String endDateMes   = "Δεν επιλέξατε Ημερομηνία :\n\'Έως Ημερομηνία\'";
	    	   String smallerEndDateMes ="Η τιμή : \'Έως Ημερομηνία\' \nείναι μικρότερη ή ίση της:\n"+
	    	   		                     "\'Από Ημερομηνία\'.\nΠαρακαλώ Επιλέξτε μεγαλύτερη Ημερομηνία";
	    	   if(startDate.getText().toString().equals("Πατήστε εδώ")){
	    		  showingMessageWhenDatesIsEmpty(startDateMes);
				  return false;
	    	   }
	    	   if(endDate.getText().toString().equals("Πατήστε εδώ")){
	    		  showingMessageWhenDatesIsEmpty(endDateMes);
	 			  return false;
	     	   }
	    	   if(dateStart != null && dateFinish != null){
	    		  if(dateStart.getTimeInMillis() >= dateFinish.getTimeInMillis()){
	    			 showingMessageWhenDatesIsEmpty(smallerEndDateMes); 
	     			 return false;
	    		  }
	    	   }
	    	   return true;    	   
	       }//end method
	       
		   //------------------------FIND RESULTS ---------------------
	       //Αναζητά για σχετικά αποτελέσματα στη ΒΔ
	       private void findResults(String queryTitle){
	    	   
	    	   DBControler db = new DBControler(context);
	    	   listOfShifts   = new ArrayList<ShiftOfUser>();
	    	   Cursor c;
	    	   int sumResults = 0;
	    	   int userCode   = 0;
	    	   
	    	   db.open();
	    	   try{
	    	      userCode = db.getIdValueFromTable("Employee");
	    	   }catch(Exception e){
	    		   e.printStackTrace();
	    		   Log.e("ERROR_USER_CODE", "CODE_NOT_FUND");
	    	   }
	    	   if(queryTitle.contains("Sundays") || queryTitle.startsWith("Κυρ") ){  //αν αφορά Κυριακές 
	    		 try{
	    	         c = db.getCountSundaysFromShiftBetweenDates(
	    	        		 userCode, 
	    	        		 dateStart.getTimeInMillis(), 
	    	        		 dateFinish.getTimeInMillis());
	    	         Log.i("WORKED_SUNDAYS", ": "+c.getCount());
	    	         sumResults = c.getCount();
	    	         getCursorValues(c);  //κλήση της επόμενης μεθόδου
	    		    }catch(Exception e){
	    			  e.printStackTrace();
	    			  Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
	    		    } 
	    	   }//end if
	    	   else if(queryTitle.contains("Holidays") || queryTitle.startsWith("Αργ") ){//αν αφορά Αργίες 
	    		   try{
		    	       c = db.getCountHolidaysFromShiftBetweenDates(
		    	    		   userCode, 
		    	    		   dateStart.getTimeInMillis(),
		    	    		   dateFinish.getTimeInMillis());
		    	       Log.i("WORKED_SUNDAYS", ": "+c.getCount());
		    	       sumResults = c.getCount();
		    	       getCursorValues(c);  //κλήση της επόμενης μεθόδου
		    		  }catch(Exception e){
		    			  e.printStackTrace();
		    			  Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
		    		  }  
	    	   }//end else
	    	   db.closeDB();
	    	   results.setText(""+sumResults); //ενημερώνει το αντίστοιχο πεδίο συγκεντρωτικών αποτελεσμάτων
	    	   ResultsListAdapter adapter = new ResultsListAdapter(
	    			                            context, 
	                                            R.layout.activity_item_of_results_list , 
	                                            listOfShifts );
	           lv.setAdapter(adapter); //δημιουργεί και αναθέτει στη λίστα ένα αντάπτορα
	           if(sumResults == 0)     //αν δεν βρέθηκαν βάρδιες για αυτό το διάστημα
	        	  showingMessageWhenShiftNotExist(); //εμφανίζει μήνυμα
	       }//end method
	       //----------------------------------------------------------------
	       /*
	        * Αυτή η μέθοδος παίρνει τις τιμές : Ημερομηνίας, Ημέρας, Βάρδιας 
	        * από τη μεταβλητή Cursor και δημιουργεί ένα αντικείμενο τύπου ShiftOfUser 
	        * όπου και φορτώνονται. Τέλος φορτώνει τα αντικείμενα σε ένα ArrayList.
	        */
	       private void getCursorValues(Cursor c){
	    	   if(c.moveToFirst()) {
	    		 SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
	    		 do{
	    		    ShiftOfUser curShift = new ShiftOfUser();    		    
					Calendar date = Calendar.getInstance();
	  				date.clear();
	    		    date.setTimeInMillis(c.getLong(0)); //Ημερομηνία
	    		    String day    = c.getString(1);    	//Ημέρα	    
	    		    String shift  = c.getString(2);    	//Βάρδια
	    		    String holName= c.getString(3);     //Όνομα Αργίας
	    		    curShift.setDate(df.format(date.getTime()));
	    		    curShift.setWeekDay(day);
	    		    curShift.setShift(shift);
	    		    curShift.setShiftHolidayName(holName);
	    		    listOfShifts.add(curShift);
	    		   }while(c.moveToNext());
	    	   }//end if
	       }//end method
	       //----------------------------------------------------
	       /* Διαγράφει όλες τις τιμές στα πεδία της φόρμας αλλά 
	        * και τα περιεχόμενα της λίστας
	        */       
	       private void resetValues(){
	    	     startDate.setText("Πατήστε εδώ");
			     endDate.setText("Πατήστε εδώ");
			     results.setText("");
			     lv.setAdapter(null);
			     if (listOfShifts != null) //αν δεν είναι καινή
			        listOfShifts.clear();
	       }//end method
	       //------------------------- SHOW MESSAGE DIALOG-------------------------
		   //Εμφανίζει Dialog με μήνυμα Αποτυχίας Ανεύρεσης Βαρδιών 
		   //με τα συγκεκριμένα χαρακτηριστικά για το αντίστοιχο χρονικό διάστημα
		   public boolean showingMessageWhenShiftNotExist(){	
		    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		  				String defaultMessage = "\nΓια Επιστροφή Πατήστε \'ΟΚ\'"; 	  				
		  			    // Dialog Τίτλος
		  			    alertDialog.setTitle("Αποτυχία Αναζήτησης");		 
		  			    // Μήνυμα του Dialog
		  			    alertDialog.setMessage("Δεν Βρέθηκαν : \'"+title+"\'" +
		  			    		" για αυτό το χρονικό διάστημα."+ defaultMessage);		 
		  			    // Θέτει εικόνα των ρυθμίσεων στο Dialog
		  			    alertDialog.setIcon(R.drawable.alert);	
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
		   //------------------------- SHOW MESSAGE DIALOG-------------------------
		   //Εμφανίζει Dialog με μήνυμα Αποτυχίας Ανεύρεσης Βαρδιών 
		   //με τα συγκεκριμένα χαρακτηριστικά για το αντίστοιχο χρονικό διάστημα
		   public boolean showingMessageWhenDatesIsEmpty(String message){	
		    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		  				String defaultMessage = "\nΓια Επιστροφή Πατήστε \'ΟΚ\'"; 	  				
		  			    // Dialog Τίτλος
		  			    alertDialog.setTitle("Λείπουν Στοιχεία");		 
		  			    // Μήνυμα του Dialog
		  			    alertDialog.setMessage(message + defaultMessage);		 
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
		}//end inner class fragment
		//---------------------------- ΒΟΗΘΕΙΑ ---------------------------
			//Εμφανίζει Dialog με Πληροφορίες Συμπλήρωσης της φόρμας  
			//για συγκεκριμένη ημερομηνία
			public void showingHelpMessage(){	
			    	  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			  		  Spanned defaultMessage = Html.fromHtml("Για Επιστροφή Πατήστε: \'ΟΚ\'"); 
			  		  Spanned helpMes = Html.fromHtml("Αρχικά εισάγετε Ημερομηνία στα πεδία: " +
			  		  		  "<font color=\"#D2691E\">\'Από Ημερομηνία\'</font>,<br>" +
			  		  		  "<font color=\"#D2691E\">\'Έως Ημερομηνία\'</font>.<br>" +
			  				  "<font color=\"#FF0000\"><b>1.</b></font>" +
			  				  " Απλώς αγγίξτε το πλαίσιο δίπλα από την αντίστοιχη ετικέτα.<br>" +
			  				  "<font color=\"#FF0000\"><b>2.</b></font>" +
			  				  " Στη φόρμα με το ημερολόγιο που ανοίγει επιλέξτε την ημερομηνία " +
			  				  "που θέλετε και πατήστε: " +
			  				  "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>.<br>" +
			  				  "<font color=\"#FF0000\"><b>3.</b></font>" +
			  				  " Αφού εισάγετε και τις δύο Ημερομηνίες Πατήστε: " +
			  				  "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>.<br>" +
			  				  "Η εφαρμογή μετρά το πλήθος βαρδιών που πληρούν τα κριτήρια " +
			  				  "και το εμφανίζει στο επόμενο πλαίσιο. " +
			  				  "Παράλληλα εμφανίζει μια αύξουσα λίστα με όλες τις βάρδιες που βρέθηκαν.<br>" +
			  				  "Αν δεν βρεθούν Βάρδιες εμφανίζει κατάλληλο μήνυμα.<br><br>" + defaultMessage );	  				
			  		  Spanned buttonName = Html.fromHtml("<font color=\"#008B00\"><b>OK</b></font>");
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
