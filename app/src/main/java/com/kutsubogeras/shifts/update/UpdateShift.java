package com.kutsubogeras.shifts.update;

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
import android.database.Cursor;
import android.graphics.Color;
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

public class UpdateShift extends Activity {
	
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
	private String selectedShiftDate = null;
	private static final String addDateReciveMessage = "Επιλεγμένη Ημερομηνία : ";
	private static final String SelectedShiftMessage = "Η Επιλογή σας είναι: ";
	private static final String errorShiftUpdateMessage  = "Δεν Έχετε Καταχωρήσει κάποιον Εργαζόμενο";
	private static final String successShiftInsertMessage= "Επιτυχής Καταχώρηση για : ";
	private ArrayList<String>   ShiftValues      = new ArrayList<String>();   // κρατά τις τιμές των βαρδιών
	private ArrayList<Calendar> officialHolidays = new ArrayList<Calendar>(); // επίσιμες αργίες τύπου Calendar			
	private ArrayList<String>  holidaysNames     = new ArrayList<String>();   // Ονομασίες των Αργιών
	private String holidayName = null;
	private ArrayAdapter<String> dataAdapter;
	private TimePickerDialog timePickerDialog;
	private TimePicker       timePicker;
	private ArrayList<Integer> backgrounds = new ArrayList<Integer>();
			
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
		this.setShiftValuesInArrayTable(ShiftValues); // θέτει τις τιμές βαρδιών στον πίνακα
		this.initalizeComponets();                    // αρχικοποίηση συστατικών στοιχείων φόρμας
		this.setBackroundIcons();
		this.checkPreferentBackround(frameLayout);
		this.setInSpinnersShiftValues();              // ανάθεση των τιμών των βαρδιών στα Spinners
		this.createComponensListeners();              // δημιουργία Listeners για Buttons,EditTexts,Spinners
		//this.setOfficialHolidays();                   // θέτει τιμές στον πίνακαμε τις επίσημες αργίες
	}//end method
	
    //--------------------------- ON ACTIVITY RESULT -------------------------------
	//όταν τερματίσει η κληθήσα activity 	    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);		  		
				 if(resultCode == START_DATE_FLAG) {
			  		selectedShiftDate = data.getStringExtra(DiaryView.SELECTED_DAY);		  		
			  		this.getDateValuesFromStringDate(selectedShiftDate);
			  		txtDate.setText(selectedShiftDate);
			  		//εμφανίζει μήνυμα με την ημερομηνία που επέλεξε ο χρήστης
			  		Toast tost = Toast.makeText(this, 
			  				addDateReciveMessage+selectedShiftDate, Toast.LENGTH_SHORT); 
				    tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
				    tost.show();				    
				    this.setShiftValuesInTextViews(); //παιρνά τις τιμές της βάρδιας στα πεδία της φόρμας
			  	 }		  	 	
	}//end method 
	//--------------------------- MENU -------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.one_shift_insertion, menu);
		menu.clear();
		menu.add(0, R.id.item1_update_shift_help, 0, "Βοήθεια")
            .setIcon(android.R.drawable.ic_menu_help);	
		return true;
	}
	//---------------------------- MENU ITEMS -------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
	        case R.id.item1_update_shift_help: 
	    	     this.sowingHelpMessage();
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
	 //------------------------- LISTENERS ---------------------------------------------------
	    //Δημιουργεί τα Listeners
	    private void createComponensListeners(){
	    	
	    	txtDate.setOnClickListener(new View.OnClickListener() {       		
			       public void onClick(View v) {
			    	     //διαγράφει τις τιμές της προηγούμενης βάρδιας
			    	     resetShiftValues(); 
			       		 //δημιουργεί ένα Intent για την εισαγωγή εργαζόμενου
			       		 Intent shiftIntent = new Intent(UpdateShift.this , DiaryView.class);
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
			    	   if(checkFormValues())          //ελέγχει αν έχουν συμπληρωθεί τα πεδία της φόρμας 
			    		  warningFailureShiftUpdate();//εμφανίζει dialog προειδοποίησης αλλαγής της Βάρδιας		    	  
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
			    	   resetShiftValues(); 		    		 
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
		}//end method	
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
	    	//this.setOfficialHolidays();             //θέτει τιμές στον πίνακαμε τις επίσημες αργίες
	    	//this.setDatesAndWeekDays();             //θέτει τιμές στα πεδία των επόμενων ημερομηνιών 
	    }//end method   
	    
	    //---------------------------------------------------------------------
	    /*
	     * Αυτή η μέθοδος εισάγει στη βάση δεδομένων τις τιμές των πεδίων
	     */
	    private void setShiftValuesInTextViews(){
	    	
	    	DBControler db = new DBControler(this);
	    	Cursor c = null;	    	
	    	int  userCode  = 0;
	    		    	
	    	db.open();     	 
	    	try{
	    		userCode = db.getIdValueFromTable("Employee");
	    	   }catch(Exception e){
	    		     db.closeDB();
	    		     e.printStackTrace();
	    		     Log.e("SQL_ERROR", "ERROR ΜΕ ΤΟ USER_CODE");	    		     
	    	   } 
	    	try{
	    	    c = db.getShiftValuesFromShiftTable(userCode, myCalendar.getTimeInMillis());
	    	    this.getCursorValues(c); //παίρνει τις τιμές από τον κέρσορα
	    	   }catch(Exception e){
	    		     db.closeDB();
   		             e.printStackTrace();
   		             Log.e("SQL_ERROR", "ERROR ΜΕ THN ΒΑΡΔΙΑ");    		
	    	}	    	
	    	db.closeDB();     
	    }//end method
	    //-------------------------GET CURSOR VALUES -----------------------
	    /*
	     * Επιστρέφει τις τιμές της βάρδιας που βρίσκονται στον Cursor
	     * και τις θέτει στα TextViews της φόρμας. Καλείται στην Προηγούμενη μέθοδο:
	     * setShiftValuesInTextViews()
	     */
	    private void getCursorValues(Cursor c){
	    	String dialogTitle  ="Ανύπαρκτη Ημερομηνία : "+selectedShiftDate;
	    	String errorMessage ="Δεν Βρέθηκε Βάρδια για την Ημερομηνία: "+selectedShiftDate;
	    	   if(c.moveToFirst()) {
	    		 SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
	    		 do{    			 
					Calendar date = Calendar.getInstance();
	  				date.clear();
	    		    date.setTimeInMillis(c.getLong(2));
	    		    String day   = c.getString(3);    		    
	    		    String shift = c.getString(4);
	    		    int sunday   = c.getInt(5);
	    		    int holiday  = c.getInt(6);
	    		    String comen = c.getString(8);
	    		    txtDate.setText(df.format(date.getTime()));
	    		    txtWeekDay.setText(day);
	    		    //επιστρέφει τον αριθμό δείκτη γραμμής της βάρδιας στο spinner
	    		    int rowIndex = this.getRowIndexOfSelectionShiftValue(shift); 
	    		    spinnerShift.setSelection(rowIndex);  
	    		    if(sunday ==1)
	    	    	   checkSunday.setChecked(true);	    //είναι Κυριακή
	    		    if(holiday ==1)
	    	    	   checkHoliday.setChecked(true);       //είναι Αργία
	    	    	txtComens.setText(comen);
	    		   }while(c.moveToNext());
	    	   }//end if
	    	   else{
	    		   this.showingDialogWithMessage(dialogTitle, errorMessage, R.drawable.warning);
	    		   txtDate.setText("Πατήστε εδώ");
	    	   }
	       }//end method
	    //---------------------------------------------------------------------
	    /*
	     * Επιστρέφει τη θέση που βρίσκεται καταχωρημένη η τιμή βάρδιας στον πίνακα
	     * και κατ' επέκταση στο spinner. Καλείται στην προηγούμενη:
	     * getCursorValues(Cursor c)
	     */
	    private int getRowIndexOfSelectionShiftValue(String shiftValue){
	    	int shift = 0;
	    	for(int i=1; i<ShiftValues.size(); i++){
	    		if(ShiftValues.get(i).equals(shiftValue)){
	    			shift = i;
	    		    break;
	    	    }
	    	}
	    	return shift;
	    }//end method
	    //---------------------------- CHECK FORM VALUES -------------------
	    /*
	     * Αυτή η μέθοδος ελέγχει αν έχει επιλέξει  χρήστης Ημερομηνία Βάρδιας
	     * Καλείται στο Listener του button OK
	     */
	    private boolean checkFormValues(){
	    	//Τοπικές μεταβλητές
	    	String  dialogdateTitle ="Λανθασμένη Επιλογή";
	    	String  errorDateMessage="Δεν Επιλέξατε Ημερομηνία Βάρδιας!!!";
	    	String  dialogTitle ="Λανθασμένη Επιλογή";
	    	String  errorMessage="Δεν Επιλέξατε Βάρδια για Αλλαγή Στοιχείων!!!";
	    	if(txtDate.getText().toString().equals("Πατήστε εδώ")){
		       this.showingDialogWithMessage(dialogdateTitle, errorDateMessage, R.drawable.warning);
		  	   return false;
	    	}	
	    	if(spinnerShift.getSelectedItem().toString().equals("Επέλεξε")){
	    	   this.showingDialogWithMessage(dialogTitle, errorMessage, R.drawable.warning);
	  	       return false;
	     	}	    	
	    	return true;
	    }//end method
	    //========================== UPDATE SHIFT ==============================
	    /*
	     * Αυτή η μέθοδος αλλάζει την τιμή ωραρίου μιας Βάρδιας, των σχολίων
	     * και του έξτρα χρόνου (αν προκύπτει)
	     */
	    private void updateShiftsValues(){
	    	//μεταβλητές κλάσης
	    	SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());				
			String  printDate = df.format(myCalendar.getTime());
	    	String  updatedTitle     ="Επιτυχημένη Αλλαγή";
	    	String  updatedMessage   ="Τα στοιχεία της Βάρδιας : "+printDate+" έχουν Αλλάξει";
	    	String  notUpdatedTitle  ="Αποτυχία Αλλαγής";
	    	String  notUpdatedMessage="Δεν Έγινε Μεταβολή Στοιχείων της Βάρδιας : "+printDate;	    	   	
	    	
	    	int     extraTime = 0;//έχει τους εξτρα χρόνους(πέραν του 7,25 ωρών)
	    	boolean updatedRow= false;	    	
	    	String  curShift  = spinnerShift.getSelectedItem().toString();    	
	    	String  curComens = txtComens.getText().toString();	    	
	    	extraTime = this.calgulateExtraTime(curShift);   //υπολογίζει τον εξτρα χρόνο
	    	DBControler db  = new DBControler(this);	    	
	    	db.open();  
	    	updatedRow = db.updateShiftValue(
	    	    		             myCalendar.getTimeInMillis(),
	    	    			         curShift,
	    	    			         curComens,
	    	    			         extraTime);
	    	Log.i("SUCCESS_UPDATE", ""+updatedRow);    	
	    	db.closeDB();	
	    	//this.updateHolidayName_25_3_2014(); //την διαγράφω μετά από την κλήση της
	    	if(updatedRow)  //αν έγινε επιτυχώς το update εμφανίζει μήνυμα και τερματίζει με το ΟΚ
	    	   this.showingDialogUpdateResult(updatedTitle, updatedMessage, R.drawable.info_icon);
	    	else      //αν δεν έγινε update των στοιχείων της βάρδιας εμφανίζει κατάλληλο μήνυμα και τερματίζει
	    	   this.showingDialogUpdateResult(notUpdatedTitle, notUpdatedMessage, R.drawable.error_icon); 	    	
	    }//end method
	    //---------------------------ΔΙΟΡΘΩΣΗ ΕΠΩΝΥΜΙΑΣ ΑΡΓΙΑΣ 25/3---------------------------
	    /*
	     * Διόρθωση Ονόματος Ημερομηνίας της 25/3/2014
	     * Δεν είχε περαστεί γιατί προστέθηκε μετέπειτα το πεδίο ονομασία Αργίας
	     */
	    private void updateHolidayName_25_3_2014(){
	    	
	    	Calendar holiday = Calendar.getInstance();
	    	holiday.clear();
	    	holiday.set(2014, 2, 25);
	    	boolean updatedHolName =false;
            DBControler db  = new DBControler(this);	    	
	    	db.open();  
	    	updatedHolName =db.updateShiftTableStringValue("holiday_name", "ΤΟΥ ΕΥΑΓΓΕΛΙΣΜΟΥ",
	    			holiday.getTimeInMillis());
	    	Log.i("SUCCESS_UPDATE", ""+updatedHolName); 
	    	db.closeDB();
	    }
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
	   
	    
	    //----------------------------------------------------------------------
	    /*
	     * Κάνει Reset όλα τα τσεκαρισμένα πεδία των Κυριακών και των Αργιών
	     * Καλείται στο σώμα της getDateValuesFromStringDate() στην αρχή
	     */
	    private void resetShiftValues(){
	    	txtDate.setText("Πατήστε εδώ");
	    	txtWeekDay.setText("");
	    	spinnerShift.setSelection(0);
	    	checkSunday.setChecked(false);	    	
	    	checkHoliday.setChecked(false);	
	    	txtComens.setText("");
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
		   * τις ώρες έναρξης και λήξης βάρδιας του νέου ωραρίου που καταχωρεί
		   * Καλείται στο ΟΚ button του dialog εισαγωγής ωραρίου: 
		   * showDialogWithEditText
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
		//-----------------------------------------------------------------
		/*Εμφανίζει Dialog το οποίο παίρνει ως παραμέτρους :
		 * το τίτλο και το μήνυμα προβολής στο χρήστη
		 */
		public void showingDialogUpdateResult(String title, String errorMessage, int iconResId){				
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
		  					       finish();
		  			          }
		  			    });					   		       
		  			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
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
	    //--------------------------------------------------------------------------- 
	    /*
	     * Εμφανίζει Dialog με μήνυμα Προειδοποίησης στο Χρήστη ότι πρόκειται
	     * να αλλαχθούν τα στοιχεία της βάρδιας. Αν Πατήσει ΟΚ η βάρδια αλλάζει
	     * αλλιώς ακυρώνεται η αλλαγή
	     */
	    public void warningFailureShiftUpdate(){				
	  				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	  				String defaultMessage = "Πρόκειται να Μεταβάλετε τα στοιχεία της βάρδιας :"
	  						+ "\nΑν Συμφωνείται Πατήστε \'ΟΚ\' Αλλιώς Πατήστε \'ΑΚΥΡΩΣΗ\'"; 		 
	  			    // Dialog Τίτλος
	  			    alertDialog.setTitle("Προσοχή !!!");		 
	  			    // Μήνυμα του Dialog
	  			    alertDialog.setMessage(defaultMessage);		 
	  			    // Θέτει εικόνα των ρυθμίσεων στο Dialog
	  			    alertDialog.setIcon(R.drawable.warning);	
	  			    alertDialog.setCancelable(false);
	  			    //Αν πατηθεί το ΟΚ button κλείνει το Dialog
	  			    alertDialog.setPositiveButton("ΟΚ", new DialogInterface.OnClickListener() {
	  			          public void onClick(DialogInterface dialog, int choice) {            	    
	  					       dialog.cancel(); //κλείνει το dialog 
	  					       updateShiftsValues(); //καλεί τη μέθοδο που θα κάνει update την βάρδια
	  			          }
	  			    });	
	  			    alertDialog.setNegativeButton("ΑΚΥΡΩΣΗ", new DialogInterface.OnClickListener() {
  			          public void onClick(DialogInterface dialog, int choice) {            	    
  					       dialog.cancel(); //κλείνει το dialog   					       
  			          }
  			    });		
	  			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
	  	} //end method	    
	    //------------------------------HELP--------------------------------
	    /*
	     * Εμφανίζει ένα dialog με οδηγίες συμπλήρωσης της φόρμας στο χρήστη
	     * αν ο χρήστης επιλέξει από το μενού : 'Βοήθεια'
	     */
	    private void sowingHelpMessage(){
	    	 String titleHelp = "Οδηγίες !!!";
	    	 Spanned defaultMessage = Html.fromHtml("Για Επιστροφή Πατήστε: \'ΟΚ\'"); 
	    	 Spanned helpMes = Html.fromHtml("Αρχικά επιλέγετε Ημερομηνία Βάρδιας πατώντας " +
	    	 		    "στο πεδίο με την ένδειξη : " +
	    	 		    "<font color=\"#D2691E\">\'Πατήστε Eδώ\'</font>.<br>" +
	  				    "Η εφαρμογή στη συνέχεια αναζητά στη βάση δεδομένων " +
	  				    "και αν υπάρχει καταχωρημένη Βάρδια "+
	  				    "για αυτή την ημερομηνία προβάλει τα στοιχεία της στα αντίστοιχα πεδία."+
	  				    " Αν δεν υπάρχει εμφανίζει κατάλληλο μήνυμα.<br>"+
	  				    "<font color=\"#FF0000\"><b>1.</b></font>" +
	  				    " Μπορείτε να μεταβάλετε μόνο το " +
	  				    "<font color=\"#D2691E\">\'Ωράριο\'</font>" +
	  				    " της Βάρδιας και να προσθέσετε " +
	  				    "<font color=\"#D2691E\">\'Σχόλια\'</font>"+
	  				    " αν κρίνεται αναγκαίο.<br>" +
	  				    "Τα υπόλοιπα Στοιχεία συμπληρώνονται αυτόματα.<br>"+
	  				    "<font color=\"#FF0000\"><b>2.</b></font>" +
	  				    " Για να αλλάξετε το Ωράριο πατήστε το πλαίσιο με " +
	  				    "την ώρα και επιλέξτε ένα νέο Ωράριο.<br>"+
	  				    "<font color=\"#FF0000\"><b>3.</b></font>" +
	  				    " Για Εισαγωγή Σχολίων απλώς αγγίξτε το πλαίσο και πληκτρολογίστε το κείμενο.<br>"+
	  				    "<font color=\"#FF0000\"><b>4.</b></font>" +
	  				    " Τέλος Πατήστε \'ΟΚ\' για να αλλάξετε τη Βάρδια.<br>"+
	  				    "<font color=\"#FF0000\"><b>5.</b></font>" +
	  				    " Η εφαρμογή πριν από οποιαδήποτε μεταβολή ζητά επιβεβαίωση από το χρήστη.<br>"+ 
	  				    "Εδώ επιλέγετε : " +
	  				    "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font> ή " +
	  				    "<font color=\"#FF1493\">\'ΑΚΥΡΩΣΗ\'</font>" +
	  				    " ανάλογα με την περίπτωση.<br><br>"+ defaultMessage);	    	 
	    	 Spanned buttonName = Html.fromHtml("<font color=\"#008B00\"><b>OK</b></font>");
	    	 AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);			 	 
			 //Dialog Τίτλος
			 alertDialog.setTitle(titleHelp);		 
			 //Μήνυμα του Dialog
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
	    }
	//--------------------------------------------------------------------------
}//end class
