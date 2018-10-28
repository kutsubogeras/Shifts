package com.kutsubogeras.shifts.queries;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.kutsubogeras.shifts.MainActivity;
import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.ShiftListAdapter;
import com.kutsubogeras.shifts.ShiftOfUser;
import com.kutsubogeras.shifts.data.DBControler;
import com.kutsubogeras.shifts.settings.SettingsShifts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DisplayIntervalOfShifts extends Activity {
	
	private Fragment shiftsFragment = new DisplayIntervalOfShiftsFragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_interval_of_shifts);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, shiftsFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_interval_of_shifts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		     case R.id.item1_nterval_of_shifts_help: 
		    	 this.showingHelpMessage();
		    	 return true;
		     case R.id.action_settings: 
		         return true;			
		     default:
		         return super.onOptionsItemSelected(item);
		}//end switch
	}//end method

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class DisplayIntervalOfShiftsFragment extends Fragment {
		
		 //Μεταβλητές Κλάσης
           private TextView queryTitle;
	       private TextView startDate;
	       private TextView endDate;
	       private Button   but_OK;
	       private Button   but_Back;
	       private Button   but_Reset;
	       public static final int START_DATE_FLAG = 17;
           public static final int END_DATE_FLAG   = 18;           
	       private static final String addDateReciveMessage = "Επιλεγμένη Ημερομηνία : ";
	       private String selectedDate = "";
	       private Calendar dateStart;
	       private Calendar dateFinish;
	       private String title;
		   private ArrayList<ShiftOfUser> listOfShifts;
		   private ListView lv;
		   private Context context;
		   private ArrayList<Integer> backgrounds = new ArrayList<Integer>();
		   
       //constractor
		public DisplayIntervalOfShiftsFragment() {				
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			View rootView = inflater.inflate(R.layout.fragment_display_interval_of_shifts, container, false);
			
					
			//----------------------LIST VIEW------------------------
			//Δημιουργία αντικειμένου τύπου DBControler class μεθόδου ανάγνωσης των βαρδιών
			queryTitle  = (TextView)rootView.findViewById(R.id.TextView_IntervalShifts_Title);
		    startDate   = (TextView)rootView.findViewById(R.id.textView_IntervalShifts_StartDay);
		    endDate     = (TextView)rootView.findViewById(R.id.textView_IntervalShifts_EndDay);
		    but_OK      = (Button)  rootView.findViewById(R.id.button_IntervalShifts_Button_OK);
		    but_Back    = (Button)  rootView.findViewById(R.id.button_IntervalShifts_Button_Back);
		    but_Reset   = (Button)  rootView.findViewById(R.id.button_IntervalShifts_Button_Reset);		     
		    title       = getActivity().getIntent().getStringExtra(
		    	          MainActivity.showQueriesFragment.QUERY_TITLE);
		    queryTitle.setText(title);
		    context = getActivity();			    
		    this.createComponensAndListeners(); //δημιουργία Listeners 		
			this.setBackroundIcons();
			this.checkPreferentBackround(rootView);
			//δημιουργία ListView και adapter για χειρισμό της λίστας				
			lv = (ListView)rootView.findViewById(R.id.listView_Interval_Shifts);			 
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
				  				addDateReciveMessage+selectedDate, Toast.LENGTH_SHORT); 
					    tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 3);
					    tost.show();
				  	 }	
					 if(resultCode == END_DATE_FLAG) {
						selectedDate = data.getStringExtra(DiaryView.SELECTED_DAY);
						Log.i("END_START", selectedDate );
						endDate.setText(selectedDate);
						dateFinish = getDateValuesFromStringDate(selectedDate);
					  	Toast tost = Toast.makeText(getActivity(), 
					  			addDateReciveMessage+selectedDate, Toast.LENGTH_SHORT); 
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
		       		      //δημιουργεί ένα Intent κλήσης της εφαρμογής Ημερολογίου 
		            	  //για την επιλογή Ημερομηνίας Έναρξης
		       		      Intent shiftIntent = new Intent(getActivity() , DiaryView.class);
		       		      shiftIntent.setFlags(START_DATE_FLAG); //θέτει σημαία αναγνώρισης = 17
		       		      startActivityForResult(shiftIntent, START_DATE_FLAG);
		       		      */
		            	  showDialogWithDatePicker(startDate);
		             }
		       });    	
		       endDate.setOnClickListener(new View.OnClickListener() {       		
		             public void onClick(View v) {   
		            	 /*
		       		      //δημιουργεί ένα Intent για την επιλογή "Έως Ημερομηνία" 
		       		      Intent shiftIntent = new Intent(getActivity() , DiaryView.class);
		       		      shiftIntent.setFlags(END_DATE_FLAG); //θέτει σημαία αναγνώρισης = 18
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
      /*
       * Ελέγχει αν έχουν επιλεγεί οι Ημερομηνίες Έναρξης - Λήξης
       */
      private boolean chechDatesValues(){
   	   String startDateMes = "Δεν επιλέξατε Ημερομηνία : \n\'Από Ημερομηνία\'";
   	   String endDateMes   = "Δεν επιλέξατε Ημερομηνία : \n\'Έως Ημερομηνία\'";
   	   String smallerEndDateMes ="Η τιμή : \'Έως Ημερομηνία\' \nείναι μικρότερη ή ίση της: "+
   	   		                     "\'Από Ημερομηνία\'. \nΠαρακαλώ Επιλέξτε μεγαλύτερη Ημερομηνία";
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
    //---------------------- FIND RESULTS -----------------------------
      //Αναζητά για σχετικά αποτελέσματα στη ΒΔ
      private void findResults(String queryTitle){
    	  
   	   Cursor c; 
   	   DBControler db = new DBControler(getActivity().getApplicationContext());    	      	   
   	   int userCode   = 0;
   	   long totalRows = 0;    	   
   	   listOfShifts   = new ArrayList<ShiftOfUser>(); //ArrayList με αντικείμενα τύπου ShiftOfUser
   	   
   	   db.open();
   	   try{
   	       userCode = db.getIdValueFromTable("Employee"); //Ο κωδικός Χρήστη
   	      }catch(Exception e){
   		      e.printStackTrace();
   		      Log.e("ERROR_USER_CODE", "CODE_NOT_FUND");
   	      }    	  
       try{ //το πλήθος εγγραφών μεταξύ των ημερομηνιών
    	   totalRows = db.getCountFromShiftTablebetweenDates( userCode,  
     	               dateStart.getTimeInMillis(), dateFinish.getTimeInMillis());    	        
     	  }catch(Exception e){
     	  	  db.closeDB();
     	      e.printStackTrace();
     		  Log.e("ERROR_RESULTS", "ΔΕΝ ΒΡΕΘΗΚΑΝ ΕΓΓΡΑΦΕΣ");
     	  }
       if(totalRows >0){ //αν βρέθηκαν εγγραφές
   	      try{
   		      c = db.getAllShiftsBetweenDates(userCode, dateStart.getTimeInMillis(),
   		    		                         dateFinish.getTimeInMillis());    	        
   		      this.getShiftsValuesFromCursor(c); //παίρνει τις τιμές κάθε βάρδιας	       
   		     }catch(Exception e){
   			      db.closeDB();
   			      e.printStackTrace();
   			      Log.e("ERROR_SHIFTS", "RESULTS_NOT_FUND");
   		     }    	      
   	      db.closeDB();
   	      
   	      //δημιουργεί ένα adapter για προβολή σε λίστα των πολυήμερων ρεπό που βρέθηκαν
		  ShiftListAdapter adapter = new ShiftListAdapter(context, 
                                           R.layout.activity_item_of_list_shifts , listOfShifts );
          lv.setAdapter(adapter); 
    	}   	   
    	 else     //αν δεν βρέθηκαν βάρδιες για αυτό το διάστημα
       	   showingMessageWhenShiftNotExist(); //εμφανίζει μήνυμα
      }//end method
      //---------------------GET SHIFT VALUES ----------------------------
      /*
       * Θέτει σε Δυναμικό πίνακα τις τιμές κάθε Βάρδιας
       */
      private void getShiftsValuesFromCursor(Cursor c){
   	   if(c.moveToFirst()){
   	      do{ //ξεκινάει από την 13η πριν την τελευταία
				 ShiftOfUser curShift = new ShiftOfUser();
				 SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
				 Calendar date = Calendar.getInstance();
				 date.clear();		  				
				 curShift.setDateInMilisec(c.getLong(2));      //καταχωρεί την ημερομηνία σε Milisec
				 date.setTimeInMillis(c.getLong(2));           //ημερομηνία
				 String printableDate = df.format(date.getTime());
				 curShift.setDate(printableDate);              //καταχωρεί την ημερομηνία στη μορφή: (d/m/y)
				 curShift.setWeekDay(c.getString(3));          //ημέρα
				 curShift.setShift(c.getString(4));            //βάρδια
				 curShift.setShiftSunday(c.getInt(5));         //κυριακή
				 curShift.setShiftHoliday(c.getInt(6));        //αργία
				 String hn = c.getString(7);                   //Όνομα αργίας
				 hn = (hn != null)? hn : null;
				 curShift.setShiftHolidayName(hn);             
				 curShift.setComents(c.getString(8));          //σχόλια
				 curShift.setExtraTime(c.getInt(10));          //εχτρα χρόνος
				 listOfShifts.add(curShift);	//προσθέτει το στοιχείο βάρδια στον πίνακα
			   }while(c.moveToNext());//end do while	
   	   }//end if
      }//end method
      //----------------------------------------------------
      /* Διαγράφει όλες τις τιμές στα πεδία της φόρμας αλλά 
       * και τα περιεχόμενα της λίστας
       */       
      private void resetValues(){
   	     startDate.setText("Πατήστε εδώ");
		     endDate.setText("Πατήστε εδώ");
		     lv.setAdapter(null);
		     if(listOfShifts != null)
		    	listOfShifts.clear();//διαγράφει από τη λίστα τα ΡΕΠΟ
      }//end method
      //------------------------- SHOW MESSAGE -------------------------
	   //Εμφανίζει Dialog με μήνυμα Αποτυχίας Ανεύρεσης Βαρδιών 
	   //με τα συγκεκριμένα χαρακτηριστικά για το αντίστοιχο χρονικό διάστημα
	   public boolean showingMessageWhenShiftNotExist(){	
	    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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
	   //Εμφανίζει Dialog με μήνυμα ότι δεν συμπληρώθηκε κάποια από τις δύο Ημερομηνίες
	   public boolean showingMessageWhenDatesIsEmpty(String message){	
	    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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
	
	}//end fragment
	//============================== ΒΟΗΘΕΙΑ ====================================
		//Εμφανίζει Dialog με Πληροφορίες Συμπλήρωσης της φόρμας  
		//για συγκεκριμένη ημερομηνία
		public void showingHelpMessage(){	
		    	  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		    	  Spanned defaultMessage = Html.fromHtml("Για Επιστροφή Πατήστε: \'ΟΚ\'"); 
		  		  Spanned helpMes = Html.fromHtml("Αρχικά εισάγετε Ημερομηνία στα πεδία: " +
		  		  		    "<font color=\"#D2691E\">\'Από Ημερομηνία\'</font>,<br> "
		  		  		  + "<font color=\"#D2691E\">\'Έως Ημερομηνία\'</font>.<br>" +
		  				    "<font color=\"#FF0000\"><b>1.</b></font>"
		  				  + " Απλώς αγγίξτε το πλαίσιο δίπλα από την αντίστοιχη ετικέτα.<br>" +
		  				    "<font color=\"#FF0000\"><b>2.</b></font>"
		  				  + " Στη φόρμα με το ημερολόγιο που ανοίγει επιλέξτε την ημερομηνία " +
		  				    "που θέλετε και πατήστε: " +
		  				    "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>.<br>" +
		  				    "<font color=\"#FF0000\"><b>3.</b></font>"
		  				  + " Αφού εισάγετε και τις δύο Ημερομηνίες Πατήστε: " +
		  				    "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>.<br>" +
		  				    "Η εφαρμογή αναζητά όλα τις Βάρδιες μεταξύ των Ημερομηνιών, " +
		  				    "και εφόσον υπάρχουν τις εμφανίζει αναλυτικά σε λίστα με αύξουσα διάταξη.<br>" +
		  				    "Αν δεν βρεθούν Βάρδιες εμφανίζει κατάλληλο μήνυμα.<br><br>" + defaultMessage);	  				
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
