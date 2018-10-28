package com.kutsubogeras.shifts.insert;

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
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.Build;
import android.preference.PreferenceManager;

public class OneShiftInsertion extends Activity {
	        //variables
	        private FrameLayout frameLayout;
			private EditText txtDate;			
			private EditText txtWeekDay;			
			private Spinner  spinnerShift;			
			private CheckBox checkSunday;		
			private CheckBox checkHoliday;	
			private EditText txtComens;	
			private Button   buttonOK;
			private Button   buttonBack;
			private Button   buttonRESET;
			//date variables
			private final int START_DATE_FLAG = 11;  //flag  για αναγνώρηση της καλούσας activity(μεθόδου)
			private Calendar myCalendar;
			private String selectedStartDate = null;
			private static final String addDateReciveMessage = "Επιλεγμένη Ημερομηνία : ";
			private static final String SelectedShiftMessage = "Η Επιλογή σας είναι: ";
			private static final String errorShiftInsertMessage  = "Δεν Έχετε Καταχωρήσει κάποιον Εργαζόμενο";
			private static final String successShiftInsertMessage= "Επιτυχής Καταχώρηση για : ";
			private ArrayList<String>   ShiftValues      = new ArrayList<String>();   // κρατά τις τιμές των βαρδιών
			private ArrayList<Calendar> officialHolidays = new ArrayList<Calendar>(); // επίσιμες αργίες τύπου Calendar			
			private ArrayList<String>  holidaysNames     = new ArrayList<String>();   // Ονομασίες των Αργιών
			private String holidayName = null;
			private ArrayAdapter<String> dataAdapter;
			private TimePickerDialog timePickerDialog;
			private TimePicker       timePicker;
			private ArrayList<Integer> backgrounds = new ArrayList<Integer>(); //λίστα για τις εικόνες background
			
			
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_shift_insertion);
        /*
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					            .add(R.id.container, new PlaceholderFragment()).commit();
		}
		*/
		this.initalizeComponets();                    // αρχικοποίηση συστατικών στοιχείων φόρμας
		this.setBackroundIcons();                     // θέτει εικόνες για background
		this.checkPreferentBackround(frameLayout);
		this.setShiftValuesInArrayTable(ShiftValues); // θέτει τις τιμές βαρδιών στον πίνακα		
		this.setInSpinnersShiftValues();              // ανάθεση των τιμών των βαρδιών στα Spinners
		this.createComponensListeners();              // δημιουργία Listeners για Buttons,EditTexts,Spinners
		//this.setOfficialHolidays();                 // θέτει τιμές στον πίνακαμε τις επίσημες αργίες
	}//end method
	
    //--------------------------- ON ACTIVITY RESULT -------------------------------
	//όταν τερματίσει η κληθήσα activity 	    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);		  		
				 if(resultCode == START_DATE_FLAG) {
			  		selectedStartDate = data.getStringExtra(DiaryView.SELECTED_DAY);		  		
			  		getDateValuesFromStringDate(selectedStartDate);
			  		Toast tost = Toast.makeText(this, 
			  				addDateReciveMessage+selectedStartDate, Toast.LENGTH_LONG); 
				    tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
				    tost.show();
			  	 }		  	 	
	}//end method 
	//--------------------------- MENU -------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.one_shift_insertion, menu);
		menu.clear();
		menu.add(0, R.id.item1_one_shift_insertion, 0, "Βοήθεια")
            .setIcon(android.R.drawable.ic_menu_help);		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
		     case R.id.item1_one_shift_insertion: 
		    	 this.showingHelpMessage();
		    	 return true;		    
		     default:
		         return super.onOptionsItemSelected(item);
		}//end switch
	}
    //------------------------ FRAGMENT INNER CLASS -------------------------------
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_one_shift_insertion, container, false);
			return rootView;
		}
	} //end inner class
	//============================= CLASS METHODS =============================	
		//αρχικοποιεί όλα τα Componets της φόρμας
	    private void initalizeComponets(){	  
	    	
	    	frameLayout  = (FrameLayout)findViewById(R.id.FrameLayout_one_shift);	
	    	txtDate      = (EditText)findViewById(R.id.editTextOneShiftDate);	    	
	    	txtWeekDay   = (EditText)findViewById(R.id.editTextOneShiftWeekday);	    	
	    	spinnerShift = (Spinner)findViewById(R.id.spinnerOneShiftValues);	    	
	    	checkSunday  = (CheckBox)findViewById(R.id.checkBoxOneShiftSunday);	    	
	    	checkHoliday = (CheckBox)findViewById(R.id.checkBoxOneShiftHoliday);
	    	txtComens    = (EditText)findViewById(R.id.editTextOneShiftComens);	
	    	buttonOK     = (Button)findViewById(R.id.buttonOneShift_OK);  
	    	buttonBack   = (Button)findViewById(R.id.buttonOneShiftBack);    
	    	buttonRESET  = (Button)findViewById(R.id.buttonOneShiftReset);   
	    	
	    }//end method
	 //------------------------- LISTENERS -----------------------------------------
	    //Δημιουργεί τα Listeners
	    private void createComponensListeners(){
	    	//Επιλογή Ημερομηνίας
	    	txtDate.setOnClickListener(new View.OnClickListener() {       		
			       public void onClick(View v) {       		     
			       		 //δημιουργεί ένα Intent για άνοιγμα του ημερολογίου
			       		 Intent shiftIntent = new Intent(OneShiftInsertion.this , DiaryView.class);
			       		 shiftIntent.setFlags(START_DATE_FLAG);
			       		 startActivityForResult(shiftIntent, START_DATE_FLAG);
			       }
			});    	
	    	//------------------------------SPINNERS SHIFTS------------------------------------
		    //Spinner για την επιλογή βάρδιας
	    	spinnerShift.setOnItemSelectedListener(new OnItemSelectedListener() {
		    	    @Override
		    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	        if(position > 0){
		    	          String shiftValue = spinnerShift.getItemAtPosition(position).toString();
		    	          selectedItemView.setBackgroundColor(Color.CYAN);
		    	          selectedItemView.setSoundEffectsEnabled(true);
		    	          if(shiftValue.equals("ΑΛΛΟ"))   //αν ο χρήστης επέλεξε "ΑΛΛΟ" Ωράριο
		    	        	  showDialogWithEditText(spinnerShift);			    	          
		    	          else{
		    	             Toast tost = Toast.makeText(parentView.getContext(), 
		    	            		 SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
		  			         tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
		  			         tost.show();
		    	          }		  			                
		    	       }//end if
		    	    }//end method onItemSelected
		    	    @Override
		    	    public void onNothingSelected(AdapterView<?> parentView) {
		    	        // your code here    	    	
		    	    }
		    });		    	 
	    	//---------------------------BUTTON LISTENER ---------------------
	    	buttonOK.setOnClickListener(new View.OnClickListener() {       		
			       public void onClick(View v) { 
			    	   //ελέγχει αν έχουν συμπληρωθεί τα πεδία της φόρμας      		     
			    	   if(checkFormValues()){
			    		  insertValuesInDataBase(); //εισάγει τις τιμες στη ΒΔ			       		 
			    	   }
			       }
		    });  
	    	//---------------------------BUTTON LISTENER ---------------------
	    	buttonBack.setOnClickListener(new View.OnClickListener() {       		
			       public void onClick(View v) { 
			    	   finish();
			       }
		    });  
	    	//---------------------------BUTTON LISTENER ---------------------
	    	buttonRESET.setOnClickListener(new View.OnClickListener() {       		
			       public void onClick(View v) { 			    	  
			    		   //κάνει reset τις τσεκαρισμένες τιμές στα πεδία των Κυριακών
			    		   resetShiftSundaysHolidaysCheckedValues(); 		    		 
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
	    //----------------------------- CONVERT STRING DATE TO CALENDAR --------------------
	    /*
	     *Αυτή η Μέθοδος καλείται στην OnActivityResault() μέθοδο της Activity
	     *αι θέτει τιμές σε όλα τα πεδία της φόρμας μετά την επιλογή της 1ης ημερομηνίας
	     *από το χρήστη
	     */
	    private void getDateValuesFromStringDate(String date){
	    	
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
	    	myCalendar = Calendar.getInstance();    //Στιγμιότυπο της τρέχουσας ημερομηνίας
	    	myCalendar.clear();                     //καθαρίζει τις τιμές της τρέχουσας ημερομηνίας
	    	myCalendar.set(year, (month -1), day);	//θέτει νέα ημερομηνία
	    	
	    	this.setOfficialHolidays();             //θέτει τιμές στον πίνακαμε τις επίσημες αργίες
	    	this.setDatesAndWeekDays();             //θέτει τιμές στα πεδία των επόμενων ημερομηνιών 
	    }//end method   
	    //-------------------------------------------------------------------
	    /*
	     * Αυτή η μέθοδος ελέγχει αν έχουν συμπληρωθεί όλα τα απιτούμενα
	     * πεδία της φόρμας 
	     */
	    private boolean checkFormValues(){
	    	//Τοπικές μεταβλητές
	    	String errorFirstDateMessage  = "Δεν Καθορίσατε Ημερομηνία και Ημέρα";
	    	String errorShiftMessage_1    = "Δεν Επιλέξατε Βάρδια ";    	
	    	if(txtDate.getText().toString().equals("")){
	    	   Toast tost = Toast.makeText(this, errorFirstDateMessage, Toast.LENGTH_SHORT); 
	 	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
	 	       tost.show();
	 	       return false;
	    	}
	    	if(spinnerShift.getSelectedItem().toString().equals("Επέλεξε")){
	     	   Toast tost = Toast.makeText(this, errorShiftMessage_1, Toast.LENGTH_SHORT); 
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
	    	
	    	DBControler db  = new DBControler(this);
	    	long sumRows    = 0; 
	    	int  userId     = 0;
	    	long dateRows   = 0;
	    	long insertedRow= 0;    	
	    	int extraTime   = 0;//έχει τους εξτρα χρόνους(πέραν του 7,25 ωρών)
	    	//ημέρες εβδομάδος
	    	String curWeekDay = txtWeekDay.getText().toString();	    	
	    	String  curShift  = spinnerShift.getSelectedItem().toString();    	
	    	boolean curSunday = checkSunday.isChecked();	    //είναι Κυριακή	    	
	    	boolean curHoliday= checkHoliday.isChecked();       //είναι Αργία
	    	String  curComens = txtComens.getText().toString();	    	
	    	extraTime = this.calgulateExtraTime(curShift);      //υπολογίζει τον εξτρα χρόνο
	    	
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
	    	      //ελέγχει αν υπάρχει ήδη η βάρδια στη ΒΔ	    	   
	    	      dateRows = db.selectDateValueFromTable("Shift", myCalendar.getTimeInMillis());
	    	      if(dateRows == 0){   //αν δεν η καταχωρημένη βάρδια με αυτή την ημερομηνία
	    	    	  //τότε την εισάγει στη ΒΔ
	    		     insertedRow = db.InsertIntoShiftValues( 
	    		    		 userId, 
	    		    		 myCalendar.getTimeInMillis(),  
	    		    		 curWeekDay, 
	    		    		 curShift, 
	    		    		 curSunday, 
	    		    		 curHoliday, 
	    		    		 holidayName,
	    		    		 curComens, 
	    		    		 null, 
	    		    		 extraTime);
	    	         Log.i("SUCCESS_INSERT", ""+insertedRow);
	    	      }//end if   	   
	    	      else{
	    		     dateRows = 0;
	    		     db.closeDB();
	    		     Log.e("ERROR_INSERTION", "LINE 1");
	    		     resetShiftSundaysHolidaysCheckedValues();
	    		     return warningFailureShifDateInsertion(myCalendar.getTimeInMillis());
	    	      }//end else	    	   
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
	     * Υπολογίζει τον εξτρα χρόνο για τη βαρδια
	     */
	    private int calgulateExtraTime(String shiftValue){
	    	
	    	String[] houres;
	    	int defaultTime = 445;  //το σύνολο εργάσιμων λεπτών ανά ημέρα για τραπεζουπάλληλο (ακριβώς 444 λεπτά)
	    	int difference = 0;
	    	int extraMin = 0;
	    	if(shiftValue.contains("-")){
	    	   houres = shiftValue.split("-");
	    	   String[] startHourMin = houres[0].split(":");
	           int startHour = Integer.parseInt(startHourMin[0]);
	           int startMin  = Integer.parseInt(startHourMin[1]);
	    	   String[] finishHourMin = houres[1].split(":");
	    	   int finishHour = Integer.parseInt(finishHourMin[0]);
	    	   int finishMin  = Integer.parseInt(finishHourMin[1]);
	    	   finishHour = (finishHour < 12) ? (finishHour + 24) : finishHour;
	    	   difference = ((finishHour *60)+finishMin)-((startHour *60)+startMin);
	    	   extraMin   = (difference > defaultTime) ? (difference - defaultTime) : 0;
	    	}//end if
	    	 else
	    		extraMin = 0;
	    	return extraMin;
	    }//end method
	    //------------------------------------------------------------------------------------------
	    /*
	     * Η μέθοδος θέτει σε όλα τα Spinners ένα ArrayAdapter που παίρνει ως παραμέτρους
	     * μεταξύ άλλων τον ιδιάιτερο τύπο φόρμας , τον τρόπο επιλογής και ένα
	     * ArrayList με στοιχεία τύπου String με τιμές όλες τις πιθανές παραλλαγές βαρδιών
	     */
	    private void setInSpinnersShiftValues(){
	    	dataAdapter = new ArrayAdapter<String>(this, 
	                     android.R.layout.simple_spinner_dropdown_item, ShiftValues);
	    	/* simple_list_item_checked */
	        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice ); 
	        spinnerShift.setAdapter(dataAdapter);       
	        spinnerShift.setSelection(0);
	           
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
		    Log.i("START_DATE : "+myCalendar.getTimeInMillis(), 
		    	  "DATE_25_12 : "+date_18_12.getTimeInMillis());
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
	    	
       	 String title ="Λανθασμένη Επιλογή";
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
		//---------------------------------------------------------------------------
	    /*
	     *Θέτει τιμές αντίστοιχα επόμενα 6 πεδία της φόρμας με ημερομηνίες και ημέρες
	     *Καλείται στο τέλος της  : getDateValuesFromStringDate()
	     */
	    private void setDatesAndWeekDays(){
	    	SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
	    	txtDate.setText(df.format(myCalendar.getTime()));  //Θέτει την Ημερομηνία	    	
	    	int weekDayNumber = myCalendar.get(Calendar.DAY_OF_WEEK);
	    	txtWeekDay.setText(getDayOfWeek(weekDayNumber));   //Θέτει την ημέρα της εβδομάδος 
	    	if(weekDayNumber == 1)               //αν είναι Κυριακή 
	    	   checkSunday.setChecked(true);  	 //τσεκάρει το CheckBox      
	    	if(isAnOfficialHoliday(myCalendar))  //αν είναι Eπίσημη Aργία 
	    	   checkHoliday.setChecked(true);    //τσεκάρει το CheckBox    	
	    }//end method
	    //----------------------------------------------------------------------
	    /*
	     * Ελέγχει αν πρόκειται γαι Επίσημη Αργία
	     * Καλείται στην προηγούμενη: setDatesAndWeekDays()
	     */
	    private boolean isAnOfficialHoliday(Calendar curDate){	    	
	    	boolean isHoliday = false;
	    	for(int j=0; j<officialHolidays.size(); j++){
	    		//Log.i("HOLIDAY ", " 1. ="+curDate.getTimeInMillis()+"  "+(j+1)+". ="+officialHolidays.get(j).getTimeInMillis());
	    		if(curDate.getTimeInMillis() == officialHolidays.get(j).getTimeInMillis()){
	    		   isHoliday   = true;
	    		   holidayName = holidaysNames.get(j); //παίρνει το όνομα της αργίας
	    		   break;
	    		}
	    	}//end for
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
	    private void resetShiftSundaysHolidaysCheckedValues(){
	    	txtDate.setText("Πατήστε εδώ");
	    	txtWeekDay.setText("");
	    	spinnerShift.setSelection(0);
	    	checkSunday.setChecked(false);	    	
	    	checkHoliday.setChecked(false);	    	
	    }//end method
	    //-------------------------------------------------------------------------
		 /*
		  * Η μέθοδος αυτή δημιουργεί ένα Dialog με 2 TextView στα οποία μπορεί  
		  * ο χρήστης να εισάγει ή να αλλάξει τιμή αγγίζοντάς τα. Οι τιμές που
		  * εισάγονται είναι Ώρα ¨εναρξηε και Ληξης της Νέας Βάρδιας που δεν
		  * υπάρχει στις διαθέσιμες τιμές του Spinner
		  */
		 private void showDialogWithEditText(final Spinner spinner){
			    
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
				        	  //αν ο χρήστης συμπλήρωσε και τα δύο πεδία της φόρμας
				        	  if(checkUserChoices(txtStart.getText().toString(), txtFinish.getText().toString())){
					            String newTime =  txtStart.getText().toString() +"-"+ txtFinish.getText().toString();				            
					            dataAdapter.add(newTime);
					            int lastPos = dataAdapter.getCount();
					            //int pos = spinner.getLastVisiblePosition();
					            //ShiftValues.set(pos, newTime);
					            //spinner.setAdapter(dataAdapter);
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
						    	v.setText(selectedHour); //θέτει στο πλαίσιο κειμένου του dialog την ώρα
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
		 //------------------------ CREATE_TIME_PICKER_DIALOG -------------------------
		 /*
		  * Μέθοδος που δημιουργεί ένα TimePickerDialog
		  */			
		 public void showTimePickerDialog(){
			 Calendar time = Calendar.getInstance();
				int currentHour = time.get(Calendar.HOUR_OF_DAY);
				int currentMin  = time.get(Calendar.MINUTE);
				//Δημιουργεί ένα DatePickerDialog και θέτει την ημερομηνία σε αυτό 
		        timePickerDialog = new TimePickerDialog(this, dialogTimePickerListener,
		                                                currentHour, currentMin, true);
		        timePickerDialog.show();		
		 }//τελος μεθόδου	
		 
		 //======================== INNER CLASS ΤΙΜΕ PICKER DIALOG LISTENER=========================
		 /*
		  * Κλάση Listener για TimePickerDialog αντικείμενα
		  */
		    private TimePickerDialog.OnTimeSetListener  dialogTimePickerListener = 
		    			                                new TimePickerDialog.OnTimeSetListener() {
				//Ότι υπάρχει στη μέθοδο onDateSet εκτελείται όταν πατηθεί το κουμπί Set	
			   public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
				  /*
				    currentHour = hourOfDay;
				    currentMin  = minuteOfHour;
				    //μορφοποίηση της ώρας
				    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
				    Date date = new Date(0,0,0, currentHour , currentMin);
				    String strDate= timeFormat.format(date);
				  */
				    String showHour, showMin; 
				    showHour= hourOfDay    < 10 ? "0"+hourOfDay    : ""+hourOfDay;
				    showMin = minuteOfHour < 10 ? "0"+minuteOfHour : ""+minuteOfHour;
				    String strCurDate = showHour + ":" + showMin;		    
				  
				    // θέτει την ώρα και τα λεπτά στο TextView Ώρα Αναχώρησης
				    //spinnerShift.getSelectedItem().toString().
			   }
			};	   
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
	    //--------------------------------------------------------------------------
		//---------------------------- ΒΟΗΘΕΙΑ ---------------------------
		/*Εμφανίζει Dialog με Πληροφορίες Συμπλήρωσης της φόρμας  
		*για συγκεκριμένη ημερομηνία
		*/
		public void showingHelpMessage(){	
					 	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
					 	    Spanned defaultMessage = Html.fromHtml("Για Επιστροφή Πατήστε \'ΟΚ\'"); 
					 		Spanned helpMes  = Html.fromHtml( 
					 		   "Για να συμπληρώσετε τις τιμές της φόρμας αρχικά πατήστε το πλήκτρο " +
					 		   "δίπλα στην Ημερομηνία με την ένδειξη: \'Πατήστε Εδώ\'<br>" +
					 		   "<font color=\"red\"><b>1. </b></font>" +
					 		   "Στη φόρμα Ημερολογίου που ανοίγει επιλέγετε την Ημερομηνία Βάρδιας " +
					 		   "και πατήστε: <font color=\"#008B00\"><b>\'OK\'</b></font>.<br>"+
					 		   "<font color=\"red\"><b>2. </b></font>" +
					 		   "Οταν κλείσει η φόρμα Ημερολογίου και επιστρέψετε στην Εισαγωγή Βάρδιας " +
					 		   "η <font color=\"blue\">\'Ημερομηνία\'</font>, " +
					 		   "η <font color=\"blue\">\'Ημέρα\'</font>Εβδομάδος " +
					 		   "και τα <font color=\"#CD6839\">\'Check\'</font> πεδία: " +
					 		   "<font color=\"blue\">\'Κυριακή\'</font> " +
					 		   "και <font color=\"blue\">\'Αργία\'</font> " +
					 		   "συμπληρώνονται και τσεκάρονται αυτόματα. " +
					 		   "Η εφαρμογή εντοπίζει και τις κινητές αργίες.<br>" +
					 		   "<font color=\"red\"><b>3. </b></font>" +
					 		   "Επιλέξτε μόνο το ωράριο βάρδιας για κάθε Ημερομηνία αγγίζοντας " +
					 		   "το πλήκτρο με την ένδειξη : <font color=\"blue\">\'Επέλεξε\'</font><br>" +
					 		   "<font color=\"red\"><b>4. </b></font>" +
					 		   "Αν το ωράριο δεν υπάρχει στις διαθέσιμες τιμές Βάρδιας, " +
					 		   "πατήστε την ένδειξη: <font color=\"blue\"><b>\'ΑΛΛΟ\'</b></font> και στη φόρμα που ανοίγει " +
					 		   "επιλέξτε το νέο Ωράριο και πατήστε : <font color=\"#008B00\"><b>\'ΟΚ\'</b></font>.<br>" +
					 		   "<font color=\"red\"><b>5. </b></font>" +
					 		   "Στο πεδίο <font color=\"#CD6839\">\'Σχόλια\'</font> " +
					 		   "πληκτρολογήστε το κείμενο που θέλετε αν το θεωρείτε απαραίτητο.<br>" +
					 		   "<font color=\"red\"><b>6. </b></font>" +
					 		   "Αφού συμπληρώσετε όλα τα πεδία πατήστε : <font color=\"#008B00\"><b>\'OK\'</b></font>.<br>" +
					 		   "<font color=\"red\"><b>7. </b></font>" +
					 		   "Αν θέλετε να διαγράψετε όλες τις τιμές πατήστε: " +
					 		   "<font color=\"#FF00FF\"><b>\'Διαγραφή\'</b></font>.<br><br>"+ defaultMessage);	  	
					 		 Spanned buttonName  = Html.fromHtml("<font color=\"#008B00\"><b>OK</b></font>");
					 		 // Dialog Τίτλος
					 		 alertDialog.setTitle("Οδηγίες");		 
					 		 // Μήνυμα του Dialog
					 		 alertDialog.setMessage(helpMes );		 
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
