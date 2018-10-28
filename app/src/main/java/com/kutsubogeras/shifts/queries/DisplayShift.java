package com.kutsubogeras.shifts.queries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.kutsubogeras.shifts.MainActivity;
import com.kutsubogeras.shifts.R;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class DisplayShift extends Activity {
	
   
	private Fragment fragm = new PlaceholderFragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_shift);
        
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(
					             R.id.FrameLayout_container_2, fragm).commit();
		}		
	}//end method
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_shift, menu);
		menu.clear();
		menu.add(0, R.id.item1_display_shift, 0, "Βοήθεια")
	        .setIcon(android.R.drawable.ic_menu_help);
		return true;
	}//end method

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		     case R.id.item1_display_shift: 
		    	 this.showingHelpMessage();
		    	 return true;
		     case R.id.action_settings: 
		         return true;			
		     default:
		         return super.onOptionsItemSelected(item);
		}
	}//end method	 
	
    //======================== INNER STATIC CLASS FRAGMENT =====================
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		  //Μεταβλητές Κλάσης
           private TextView queryTitle;
	       private TextView shiftDate;
	       private TextView weekDay;
	       private TextView shiftValue;
	       private CheckBox sunday;
	       private CheckBox holiday;
	       private TextView holName;
	       private TextView coments;
	       //private Button but_OK;
	       private Button but_Back;
	       private Button but_Reset;
	       public static final int START_DATE_FLAG = 15;
           public static final int END_DATE_FLAG   = 16;           
	       private static final String addDateReciveMessage = "Επιλεγμένη Ημερομηνία : ";
	       private String selectedDate = null;
	       private Calendar dateOfShift;	       
	       private ArrayList<Integer> backgrounds = new ArrayList<Integer>();
	       private Context context;
	       
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				           Bundle savedInstanceState) {
			 View rootView = inflater.inflate(R.layout.fragment_display_shift,
					       container, false);
			 queryTitle = (TextView)rootView.findViewById(R.id.textView_ofWork_Year);
		     shiftDate  = (TextView)rootView.findViewById(R.id.textView_Query1_ShiftDate);
		     weekDay    = (TextView)rootView.findViewById(R.id.textView_Query1_ShiftWeekday);
		     shiftValue = (TextView)rootView.findViewById(R.id.textView_Query1_ShiftValue);
		     sunday     = (CheckBox)rootView.findViewById(R.id.CheckBox_Query1_Sunday);
		     holiday    = (CheckBox)rootView.findViewById(R.id.CheckBox_Query1_Holiday);
		     holName    = (TextView)rootView.findViewById(R.id.textView_Query1_HolidayName);
		     coments    = (TextView)rootView.findViewById(R.id.textView_Query1_Comens);
		     //but_OK     = (Button)rootView.findViewById(R.id.button_Query1_OK);
		     but_Back   = (Button)rootView.findViewById(R.id.button_Query1_Back);
		     but_Reset  = (Button)rootView.findViewById(R.id.button_Query1_Reset);
		     
		     String title = getActivity().getIntent().getStringExtra(
		    		                MainActivity.showQueriesFragment.QUERY_TITLE);
		     queryTitle.setText(title);
		     context = getActivity();
		     this.setBackroundIcons();
		     this.checkPreferentBackround(rootView);
		     createComponensAndListeners(); //δημιουργία Listeners		     
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
						shiftDate.setText(selectedDate);
						dateOfShift = getDateValuesFromStringDate(selectedDate);
				  		Toast tost  = Toast.makeText(getActivity(), 
				  				addDateReciveMessage+selectedDate, Toast.LENGTH_LONG); 
					    tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 3);
					    tost.show();
					    //δημιουργεί και εμφανίζει τα αποτελέσματα
		            	findResults(queryTitle.getText().toString());
				  	 }
				*/	 
	    }//end method  
		//------------------ METHODOI --------------------
	    public void createComponensAndListeners(){
		       //Listeners
	    	   shiftDate.setOnClickListener(new View.OnClickListener() {       		
		             public void onClick(View v) {       		     
		            	  //διαγράφει τις τιμές των πεδίων
		            	  resetValues();
		            	  /*
		            	  //δημιουργεί ένα Intent για την επιλογή Ημερομηνίας Έναρξης
		            	  Intent shiftIntent = new Intent(getActivity() , DiaryView.class);
		       		      shiftIntent.setFlags(START_DATE_FLAG);
		       		      startActivityForResult(shiftIntent, START_DATE_FLAG);
		       		      */
		            	  showDialogWithDatePicker();		            	  
		             }
		       });
	    	   /*
		       but_OK.setOnClickListener(new View.OnClickListener() {       		
		             public void onClick(View v) {       		     
		       		      //δημιουργεί και εμφανίζει τα αποτελέσματα
		            	  findResults(queryTitle.getText().toString());
		             }
		       });
		       */    	  
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
		}//end method
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
    	       Calendar myCalendar = Calendar.getInstance();//Στιγμιότυπο ημερομηνίας
    	       myCalendar.clear();  //καθαρίζει τις τιμές της τρέχουσας ημερομηνίας
    	       myCalendar.set(year, (month -1), day);	//θέτει νέα ημερομηνία     	 	
    	       return myCalendar;
       }//end method 
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
    					   	  dateOfShift = Calendar.getInstance();//Στιγμιότυπο ημερομηνίας
    					   	  dateOfShift.clear();  //καθαρίζει τις τιμές της τρέχουσας ημερομηνίας
    					   	  dateOfShift.set(year, month, day);	//θέτει νέα ημερομηνία  
    			    	      shiftDate.setText(selectedDate);
    			    	      findResults();
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
	   //------------------------------------------------------------------
       //Αναζητά για σχετικά αποτελέσματα στη ΒΔ
       private void findResults(){
    	   
    	   DBControler db = new DBControler(context);
    	   int userCode   = 0;
    	   Cursor c;
    	   db.open();
    	   try{
    	      userCode = db.getIdValueFromTable("Employee");
    	   }catch(Exception e){
    		   e.printStackTrace();
    		   Log.e("ERROR_USER_CODE", "CODE_NOT_FUND");
    	   }
    	   //επιστρέφει τις τιμές μιας ημερομηνίας
    	   try{
    	       c = db.getShiftValuesFromShiftTable(userCode, dateOfShift.getTimeInMillis());
    	       getCursorValues(c);  //κλήση της επόμενης μεθόδου
    		  }catch(Exception e){
    			  db.closeDB();
    			  e.printStackTrace();    			  
    			  Log.e("ERROR_SHIFT_DATE", "ΔΕΝ ΒΡΕΘΗΚΕ ΒΑΡΔΙΑ ΓΙΑ ΑΥΤΗ ΤΗΝ ΗΜΕΡΟΜΗΝΙΑ");
    		  }    	  
    	   db.closeDB();     	   
       }//end method
       //----------------------------------------------------------------
       /*
        * Αυτή η μέθοδος παίρνει τις τιμές : Ημερομηνίας, Ημέρας, Βάρδιας 
        * από τη μεταβλητή Cursor και δημιουργεί ένα αντικείμενο τύπου ShiftOfUser 
        * όπου και φορτώνονται. Τέλος φορτώνει τα αντικείμενα σε ένα ArrayList.
        */
       private void getCursorValues(Cursor c){
    	   String myday  = null;
    	   String myshift= null;
    	   int mysunday  = 0;
    	   int myholiday = 0;
    	   String myholName= null;
    	   String mycomens = null;
    	   
    	   if(c.moveToFirst()) {    		 
    		 do{
    		    myday    = c.getString(3); //Ημέρα Βάρδιας
    		    myshift  = c.getString(4); //η Βάρδια
    		    mysunday = c.getInt(5);    //έχει τιμή 1 ή 0
    		    myholiday= c.getInt(6);    //έχει τιμή 1 ή 0
    		    myholName= c.getString(7);
    		    mycomens = c.getString(8);
    		   }while(c.moveToNext());
    		 //θέτει τις τιμές βάρδιας στα αντίστοιχα πεδία
    		 weekDay.setText(myday);
    		 if(myshift.startsWith("ΡΕ")|| /*αν είναι ΡΕΠΟ*/
    			myshift.startsWith("ΑΔ")|| /*αν είναι ΑΔΕΙΑ*/
    			myshift.startsWith("ΑΡ"))  /*αν είναι ΑΡΓΙΑ*/
    			shiftValue.setBackgroundResource(R.drawable.textfield_background_green_for_shift_list_value);
    	     else if(myshift.startsWith("0")) //αν είναι απογευματινή βάρδια
    	        shiftValue.setBackgroundResource(R.drawable.textfield_background_blue_for_shift_list_value);          
    	     else if(myshift.startsWith("1")) //αν είναι απογευματινή βάρδια
    	        shiftValue.setBackgroundResource(R.drawable.textfield_background_yellow_for_shift_list_value);          
    	     else if(myshift.startsWith("2")) //αν είναι βραδινή βάρδια
    	        shiftValue.setBackgroundResource(R.drawable.textfield_background_orange_for_shift_list_value);
    	     else   //σε κάθε άλλη περίπτωση
    	        shiftValue.setBackgroundResource(R.drawable.textfield_background_blue_for_shift_list_value); 
		     shiftValue.setText(myshift);
		     if(mysunday ==1)
    		    sunday.setChecked(true);
		     if(myholiday ==1)
		        holiday.setChecked(true);
		     holName.setText(myholName);
		     coments.setText(mycomens);
    	   }//end if
    	   else
    		  showingMessageWhenShiftNotExist(selectedDate);
       }//end method       
       //----------------------------------------------------
       /* Διαγράφει όλες τις τιμές στα πεδία της φόρμας αλλά 
        * και τα περιεχόμενα της λίστας
        */       
       private void resetValues(){
    	     shiftDate.setText("Πατήστε Εδώ");
    	     weekDay.setText("");
		     shiftValue.setText("");
		     sunday.setChecked(false);
		     holiday.setChecked(false);
		     holName.setText("");
		     coments.setText("");
		     shiftValue.setBackgroundResource(R.drawable.textfield_background_blue_for_shift_list_value); 
       }//end method
       //--------------------------------------------------------------------------
	   //Εμφανίζει Dialog με μήνυμα Αποτυχίας Εισαγωγής μιας Βάρδιας 
	   //για συγκεκριμένη ημερομηνία
	   public boolean showingMessageWhenShiftNotExist(String date){	
	    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
	  				String defaultMessage = "\nΓια Επιστροφή Πατήστε \'ΟΚ\'"; 	  				
	  			    // Dialog Τίτλος
	  			    alertDialog.setTitle("Ανύπαρκτη Βάρδια : "+ date);		 
	  			    // Μήνυμα του Dialog
	  			    alertDialog.setMessage("Δεν Βρέθηκε Βάρδια για την Ημερομηνία : "+ date + defaultMessage);		 
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
	   
	}//end inner class fragment
    //---------------------------- ΒΟΗΘΕΙΑ ---------------------------
	//Εμφανίζει Dialog με Πληροφορίες Συμπλήρωσης της φόρμας  
	//για συγκεκριμένη ημερομηνία
	public void showingHelpMessage(){	
	    	  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	  		  Spanned defaultMessage = Html.fromHtml("Για Επιστροφή Πατήστε: \'ΟΚ\'"); 
	  		  Spanned helpMes = Html.fromHtml("Αρχικά θα πρέπει να εισάγεται την Ημερομηνία της Βάρδιας.<br>" +
	  				  "<font color=\"#FF0000\"><b>1.</b></font>" +
	  				  " Αγγίξτε το πλαίσιο δίπλα στην ετικέτα \'Ήμερομηνία\'.<br>" +
	  				  "<font color=\"#FF0000\"><b>2.</b></font>" +
	  				  " Στη φόρμα με το ημερολόγιο που ανοίγει επιλέξτε την ημερομηνία " +
	  				  "που θέλετε και πατήστε: " +
	  				  "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>.<br>" +
	  				  "Αν υπάρχει καταχωρημένη Βάρδια για αυτή την Ημερομηνία " +
	  				  "η φόρμα κλείνει και τα πεδία συμπληρώνονται αυτόματα.<br>" +
	  				  "Αν δεν υπάρχει η εφαρμογή εμφανίζει κατάλληλο μήνυμα.<br><br>" + defaultMessage);	  				
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
