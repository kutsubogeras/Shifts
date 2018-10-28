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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.Build;
import android.preference.PreferenceManager;

public class UpdateOfWork extends Activity {
	
    //Μεταβλητές κλάσης
	private FrameLayout frameLayout;
	private Spinner  selectOfWork;
	private TextView startDate;
	private TextView endDate;
	private TextView starOfSumDays; //το αστέρι υποχρεωτικής συμπληρωσης πεδίου
	private Spinner  ofWorkDays;
	private Spinner  ofWorkTypes;
	private Spinner  ofWorkYear;
	private Spinner  ofWorkSumDays;
	private EditText ofWorkComens;
	private Button   butOK;
	private Button   butReset;
	private Button   butReturn;
	private LinearLayout sumDaysLayout; //το Layout γραμμή με το πλήθος ημερών αδείας που δικαιούτε/έτος
	private final int OFWORK_START_DATE_FLAG = 13;  //flag for Result
	private final int OFWORK_END_DATE_FLAG   = 14;  //flag for Result
	private String selectedStartDate =null;
	private String selectedEndDate;
	private Calendar startCalendar;
	private Calendar endCalendar;
	private static final String SelectedShiftMessage= "Η Επιλογή σας είναι: ";	
	private ArrayList<String> ofWorkDaysValues = new ArrayList<String>();
	private ArrayList<String> ofWorkTypeValues = new ArrayList<String>();
	private ArrayList<String> ofWorkYearValues = new ArrayList<String>();
	private ArrayList<String> ofWorkStartDates = new ArrayList<String>();
	private ArrayList<String> ofWorkMultiDaysValues = new ArrayList<String>();
	private int ofwork_id =0; //το ID της έγγραφής της Άδειας που θα γίνει update
	private String selectedDate = null; //η ημερομηνία έναρξης που έχει επιλεγεί
	private ArrayAdapter<String> dataFirstDaysAdapter; //άδειες εκτεταμένης διάρκειας
	private ArrayAdapter<String> dataMultiDaysAdapter; //άδειες εκτεταμένης διάρκειας
	private ArrayAdapter<String> dataMultiDaysHasGotAdapter; //άδειες εκτεταμένης διάρκειας που πειρε
	private ArrayAdapter<String> dataDaysAdapter;      //για το πλήθος ημερών αδείας
	private ArrayAdapter<String> dataDaysHasGotAdapter;//για το πλήθος ημερών αδείας που πείρε
	private ArrayAdapter<String> dataTypesAdapter;     //για τις τιμές κατηγορίας αδειών
	private ArrayAdapter<String> dataYearAdapter;      //για το Έτος αδειών
	private String initialCategoryOfWork;              //η αρχική τιμή της κατηγορίας αδείας
	private ArrayList<Integer> backgrounds = new ArrayList<Integer>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_of_work_update);
		
		this.setValuewInSpinnerStarDates(); //βρίσκει τις Ημερομηνίες Έναρξης Αδειών
		this.setValuesInSpinnerDays(ofWorkDaysValues); // θέτει τις τιμές βαρδιών στον πίνακα
		this.setValuesInSpinnerDaysWhenIsSick(ofWorkMultiDaysValues);
		this.setValuesInSpinnerTypes(ofWorkTypeValues); // θέτει τις τιμές βαρδιών στον πίνακα
		this.setValuesInSpinnerYear(ofWorkYearValues); // θέτει τις τιμές βαρδιών στον πίνακα
		this.createClassComponents();   //δημιουργεί τα συστατικά στοιχεία της φόρμας
		this.setBackroundIcons();
		this.checkPreferentBackround(frameLayout);
		this.setInSpinnersOfWorkValues();//δημιουργεί το περιεχόμενο των spinners
	}
	//--------------------------- ON ACTIVITY RESULT ----------------------------------
	//όταν τερματίσει η κληθήσα activity 	    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	   super.onActivityResult(requestCode, resultCode, data);		  		
	  	 if(resultCode == OFWORK_START_DATE_FLAG) {
	  		//this.resetValues(); //διαγράφει τις τιμές πεδίων
	  		selectedStartDate = data.getStringExtra(DiaryView.SELECTED_DAY);
	  		startDate.setText(selectedStartDate);
	  		startCalendar = this.getCalendarDateFromStringDate(selectedStartDate);	  		
	  	 }	
	  	 if(resultCode == OFWORK_END_DATE_FLAG) {
	  		selectedEndDate = data.getStringExtra(DiaryView.SELECTED_DAY);
	  		endDate.setText(selectedEndDate);
	  		endCalendar = this.getCalendarDateFromStringDate(selectedEndDate);	  		
	  	 }		  	 	
    }//end method 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.of_work_update, menu);
		menu.clear();
		menu.add(0, R.id.item1_update_of_work_help, 0, "Βοήθεια")
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
	        case R.id.item1_update_of_work_help: 
	    	     this.showingHelpMessage();
	    	     return true;		    
	        default:
	             return super.onOptionsItemSelected(item);
	     }//end switch
	}
   //----------------- CLASS METHODS ----------------
	/*
	 * Δημιουργεί τα συστατικά στοιχεία της φόρμας 
	 * και καλείται στο σώμα της onCreate() μεθόδου
	 */
	private void createClassComponents(){
		
		frameLayout  = (FrameLayout) findViewById(R.id.FrameLayout_ofWork_update); 
		selectOfWork = (Spinner)findViewById(R.id.Spinner_OfWorkUpdate_selectDate);
		startDate  = (TextView)findViewById(R.id.textView_OfWorkUpdate_StartDate);
		endDate    = (TextView)findViewById(R.id.textView_OfWorkUpdate_EndDate);
		ofWorkDays = (Spinner)findViewById(R.id.spinner_OfWorkUpdate_Days);
		ofWorkTypes= (Spinner)findViewById(R.id.spinner_OfWorkUpdate_OfWorkTypes);
		ofWorkYear = (Spinner)findViewById(R.id.spinner_OfWorkUpdate_OfWorkYear);
		ofWorkSumDays = (Spinner)findViewById(R.id.spinner_OfWorkUpdate_SumDays);
		ofWorkComens  = (EditText)findViewById(R.id.EditText_OfWorkUpdate_Comens);
		starOfSumDays = (TextView)findViewById(R.id.TextView_OfWorkUpdate_sumStar); //το πεδίο με το αστέρι
		butOK      = (Button)findViewById(R.id.button_OfWorkUpdate_ButtonOK);
		butReset   = (Button)findViewById(R.id.button_OfWorkUpdate_ButtonReset);
		butReturn  = (Button)findViewById(R.id.button_OfWorkUpdate_ButtonReturn);
		//LinearLayout με το spinner επιλογής πλήθους ημερών αδείας
		sumDaysLayout = (LinearLayout)findViewById(R.id.LinearLayout_OfWorkUpdate_SumDays); 
		
		//Listeners		
		//Listener για Επιλογή Άδειας με βάση την Ημερομηνία Έναρξης
		selectOfWork.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    	        if(position >0){
    	          selectedDate = selectOfWork.getItemAtPosition(position).toString();
    	          //κρατά την ημερομηνία έναρξης
    	          startCalendar = getCalendarDateFromStringDate(selectedDate);	 
    	          setOfWorkValuesInTextViews(); //περνάει τις τιμές στα υπόλοιπα πεδία της φόρμας
    	          selectedItemView.setBackgroundColor(Color.CYAN);
    	          selectedItemView.setSoundEffectsEnabled(true);
    	          Toast tost = Toast.makeText(parentView.getContext(), SelectedShiftMessage+"\n"+ selectedDate,
    	        		       Toast.LENGTH_SHORT); 
  			      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
  			      tost.show();    	          
    	       }
    	    }
    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	        // your code here    	    	
    	    }
        });		
		
		//Επιλογή Ημερομηνίας Έναρξης
		startDate.setOnClickListener(new View.OnClickListener() {       		
			  public void onClick(View v) {       		     
				//δημιουργεί ένα Intent για την επιλογή Ημερομηνίας Έναρξης
		       		 Intent shiftIntent = new Intent(UpdateOfWork.this , DiaryView.class);
		       		 shiftIntent.setFlags(OFWORK_START_DATE_FLAG);
		       		 startActivityForResult(shiftIntent, OFWORK_START_DATE_FLAG);  
			  }
	    });
		
		//Επιλογή Ημερομηνίας Λήξης
		endDate.setOnClickListener(new View.OnClickListener() {       		
			  public void onClick(View v) {       		     
				//δημιουργεί ένα Intent για την επιλογή Ημερομηνίας Έναρξης
		       		 Intent shiftIntent = new Intent(UpdateOfWork.this , DiaryView.class);
		       		 shiftIntent.setFlags(OFWORK_END_DATE_FLAG);
		       		 startActivityForResult(shiftIntent, OFWORK_END_DATE_FLAG);
			  }
	    });
		
		//Επιλογή Κατηγορίας Άδειας
		ofWorkTypes.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    	        if(position > 0){
    	          String shiftValue = ofWorkTypes.getItemAtPosition(position).toString();
    	          //parentView.setBackgroundColor(Color.CYAN);
    	          selectedItemView.setBackgroundColor(Color.CYAN);
    	          selectedItemView.setSoundEffectsEnabled(true);
    	          if(shiftValue.equals("ΚΑΝΟΝΙΚΗ")){ //αν εχει επιλεγεί τύπος άδειας: ΚΑΝΟΝΙΚΗ   
    	            starOfSumDays.setText("*"); 
			        sumDaysLayout.setVisibility(View.VISIBLE); //εμφανίζει τη γραμμή επιλογής πλήθους ημερών αδείας 
    	          }
    	          else{
    	        	sumDaysLayout.setVisibility(View.GONE);
    	          }
    	          //αν άλλαξε ο χρήστης την αρχική τιμή κατηγορίας άδειας 
			      if(shiftValue != initialCategoryOfWork){     	          
    	            //αν ανοίκει σε μια από τις κατηγορίες αδείας θέτει όρια τιμής αδείας 1-180 ημέρες
    	            if(shiftValue.equals("ΑΝΑΡΩΤΙΚΗ") || shiftValue.equals("ΦΟΙΤΗΤΙΚΗ")      || 
    	               shiftValue.equals("ΤΟΚΕΤΟΥ")   || shiftValue.equals("ΑΝΕΦ ΑΠΟΔΟΧΩΝ")  ||
    	               shiftValue.equals("ΓΟΝΙΚΗ")    || shiftValue.equals("ΥΙΟΘΕΣΙΑΣ ΤΕΚΝΟΥ") ) {
    	        	   ofWorkDays.setAdapter(dataMultiDaysAdapter);
    	      		   ofWorkDays.setSelection(0);     	      		
    	            }
    	            else{  //για οποιαδήποτε άλλη κατηγορία αδείας θέτει όρια τιμής αδείας 1-30 ημέρες
    	        	   ofWorkDays.setAdapter(dataDaysAdapter);
    	        	   ofWorkDays.setSelection(0);     	        	   
    	            }//end else     	          
    	            Toast tost = Toast.makeText(parentView.getContext(), 
    	                       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
  			        tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
  			        tost.show();  			       
			      }//end if
    	       }//end if position >0
    	    }//end method
    	    
    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	        // your code here    	    	
    	    }
        });
		
		//Επιλογή Χρονικής Διάρκειας Άδειας (σε Ημέρες)
		ofWorkDays.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    	        if(position >0){
    	          String shiftValue = ofWorkDays.getItemAtPosition(position).toString();
    	          //parentView.setBackgroundColor(Color.CYAN);
    	          selectedItemView.setBackgroundColor(Color.CYAN);
    	          selectedItemView.setSoundEffectsEnabled(true);
    	          /*
    	          Toast tost = Toast.makeText(parentView.getContext(), 
    	                       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
  			      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
  			      tost.show();
  			      */    	          
    	       }
    	    }
    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	        // your code here    	    	
    	    }
        });		
		
		//Επιλογή Έτους Αδείας
		ofWorkYear.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    	        if(position >0){
    	          String shiftValue = ofWorkYear.getItemAtPosition(position).toString();
    	          selectedItemView.setBackgroundColor(Color.CYAN);
    	          selectedItemView.setSoundEffectsEnabled(true);
    	          /*
    	          Toast tost = Toast.makeText(parentView.getContext(), 
    	                       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
  			      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
  			      tost.show();
  			      */    	             	          
    	       }
    	    }
    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	        // your code here    	    	
    	    }
        });	
		
		//Επιλογή Ημερών που δικαιούτε ανά έτος
		ofWorkSumDays.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    	        if(position >0){
    	          String shiftValue = ofWorkSumDays.getItemAtPosition(position).toString();
    	          selectedItemView.setBackgroundColor(Color.CYAN);
    	          selectedItemView.setSoundEffectsEnabled(true);
    	          /*
    	          Toast tost = Toast.makeText(parentView.getContext(), 
    	                       SelectedShiftMessage+"\n"+ shiftValue, Toast.LENGTH_SHORT); 
  			      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
  			      tost.show(); 
  			      */ 	          
    	       }
    	    }
    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	        // your code here    	    	
    	    }
        });	
		
		//Πληκτρα Φόρμας
		butOK.setOnClickListener(new View.OnClickListener() {       		
			  public void onClick(View v) {       		     
				  if(checkFormValues())	//αν συμπληρώθηκαν σωστά τα στοιχεία στη φόρμα				 
					 warningOfWorkUpdate(); //εμφανίζει dialog με προειδοποίηση αλλαγής της Άδειας					 
				     								  
			  }
	    });
		butReset.setOnClickListener(new View.OnClickListener() {       		
			  public void onClick(View v) {       		     
				  resetValues(); //διαγράφει τις επιλεγμένες τιμές
			  }
	    });
		butReturn.setOnClickListener(new View.OnClickListener() {       		
			  public void onClick(View v) {       		     
			      finish();     //κλείνει τη φόρμα και επιστρέφει στην προηγούμενη χωρίς να κάνει κάτι
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
	//--------------------------------------------------------------------
    /*
     *Αυτή η Μέθοδος καλείται στην OnActivityResault() μέθοδο της Activity
     *αι θέτει τιμές σε όλα τα πεδία της φόρμας μετά την επιλογή της 1ης ημερομηνίας
     *από το χρήστη
     */
    private Calendar getCalendarDateFromStringDate(String date){    	
    	
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
    	Calendar selectedDate = Calendar.getInstance();    //Στιγμιότυπο της τρέχουσας ημερομηνίας
    	selectedDate.clear();                     //καθαρίζει τις τιμές της τρέχουσας ημερομηνίας
    	selectedDate.set(year, (month -1), day);	//θέτει νέα ημερομηνία       	
    	return selectedDate; 
    }//end method
   
  //------------------------------------------------------------------------------------------
    /*
     * Η μέθοδος θέτει σε όλα τα Spinners ένα ArrayAdapter που παίρνει ως παραμέτρους
     * μεταξύ άλλων τον ιδιάιτερο τύπο φόρμας , τον τρόπο επιλογής και ένα
     * ArrayList με στοιχεία τύπου String με τιμές όλες τις πιθανές παραλλαγές βαρδιών
     */
    private void setInSpinnersOfWorkValues(){
    	//adapter με τις Ημερομηνίες έναρξης όλων των αδειών
        dataFirstDaysAdapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_dropdown_item, ofWorkStartDates);        
        dataFirstDaysAdapter.setDropDownViewResource(
        		android.R.layout.simple_list_item_single_choice ); /* simple_list_item_checked */
    	
        //adapter με τις τιμές κατηγοριών των αδειών
        dataTypesAdapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_dropdown_item, ofWorkTypeValues);        
        dataTypesAdapter.setDropDownViewResource(
        		android.R.layout.simple_list_item_single_choice ); /* simple_list_item_checked */
        
        //adapter με τις τιμές έτους αδείας
        dataYearAdapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_dropdown_item, ofWorkYearValues);        
        dataYearAdapter.setDropDownViewResource(
        		android.R.layout.simple_list_item_single_choice ); /* simple_list_item_checked */
        
        //adapter για το πλήθος ημερών αδείας και το πλήθος αδειών κανονικής/έτους
    	dataDaysAdapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_dropdown_item, ofWorkDaysValues);    	
        dataDaysAdapter.setDropDownViewResource(
        		android.R.layout.simple_list_item_single_choice ); /* simple_list_item_checked */
        
        //adapter για το πλήθος ημερών αδείας που πείρε
    	dataDaysHasGotAdapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_dropdown_item, ofWorkDaysValues);    	
        dataDaysHasGotAdapter.setDropDownViewResource(
        		android.R.layout.simple_list_item_single_choice ); /* simple_list_item_checked */
        
        //adapter για το πλήθος ημερών αδείας για κατηγορίες με περισσότερες από 30 ημέρες άδεια
        dataMultiDaysAdapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_dropdown_item, ofWorkMultiDaysValues);    	
        dataMultiDaysAdapter.setDropDownViewResource(
        		android.R.layout.simple_list_item_single_choice ); /* simple_list_item_checked */
        
        //adapter για το πλήθος ημερών αδείας που πείρε για περισσότερες από 30 ημέρες άδεια
        dataMultiDaysHasGotAdapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_dropdown_item, ofWorkMultiDaysValues);    	
        dataMultiDaysHasGotAdapter.setDropDownViewResource(
        		android.R.layout.simple_list_item_single_choice ); /* simple_list_item_checked */
        
        selectOfWork.setAdapter(dataFirstDaysAdapter); //το πεδίο με τις ημερομηνίες έναρξης
        selectOfWork.setSelection(0);  
        ofWorkTypes.setAdapter(dataTypesAdapter);
        ofWorkTypes.setSelection(0);    
        ofWorkYear.setAdapter(dataYearAdapter);       
        ofWorkYear.setSelection(0); 
        ofWorkDays.setAdapter(dataDaysAdapter);       
        ofWorkDays.setSelection(0);        
        ofWorkSumDays.setAdapter(dataDaysAdapter);       
        ofWorkSumDays.setSelection(0); 
    }//end method
   
    //-------------------------- FIND START DATES -----------------------
    /*
     * βρίσκει όλες τις καταχωρημένες άδεις και καταχωρεί τις ημερομηνίες έναρξης
     * κάθε μιας στο spinner αναζήτησης αδειών προκειμένου να είναι δυνατή
     * η επιλογή μιας άδειας από την ημερομηνία έναρξης. 
     * Η οποία όμως μπορεί και να άλλάξει στη συνέχεια
     */
    private void setValuewInSpinnerStarDates(){
    	
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
    	    c = db.getAllSratDatesFromOfWorks(userCode);
    	    this.getStartDatesFromCursor(c); //παίρνει τις τιμές από τον κέρσορα
    	   }catch(Exception e){
    		     db.closeDB();
		             e.printStackTrace();
		             Log.e("SQL_ERROR", "ERROR ΜΕ THN ΒΑΡΔΙΑ");    		
    	}	    	
    	db.closeDB();     	
    }//end method
  //----------------------------------------------------
    /*
     * Επιστρέφει τις τιμές της βάρδιας που βρίσκονται στον Cursor
     * και τις θέτει στα TextViews της φόρμας. Καλείται στην Προηγούμενη μέθοδο:
     * setShiftValuesInTextViews()
     */
    private void getStartDatesFromCursor(Cursor c){
    	
    	String title   ="Λανθασμένη Επιλογή";
    	String message ="Δεν Βρέθηκαν Καταχωρημένες Άδειες";
    	ofWorkStartDates.add("Επιλέξτε Άδεια");
    	if(c.moveToFirst()) {
    		 SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    		 do{    			 
				Calendar start_date = Calendar.getInstance();				
  				start_date.clear();  				
    		    start_date.setTimeInMillis(c.getLong(0));    		   		    
    		    ofWorkStartDates.add(df.format(start_date.getTime()));    		    
    		 }while(c.moveToNext());
    	}//end if
    	else{
    		this.showingDialogWithMessage(title, message, R.drawable.error_icon); 
    	}    		
       }//end method
    //--------------------------SET VALUES-------------------------
    /*
     * Παιρνά τις τιμές της βάρδιας στα πεδία της φόρμας
     */
    private void setOfWorkValuesInTextViews(){
    	
    	String dialogTitle  ="Ανύπαρκτη Ημερομηνία : "+selectedStartDate;
    	String errorMessage ="Δεν Βρέθηκε Άδεια για την Ημερομηνία: "+selectedStartDate;
    	DBControler db = new DBControler(this);
    	Cursor c = null;	    	
    	int userCode = 0;    		    	
    	db.open();     	 
    	try{
    		userCode = db.getIdValueFromTable("Employee");
    	   }catch(Exception e){
    		   db.closeDB();
    		   e.printStackTrace();
    		   Log.e("SQL_ERROR", "ERROR ΜΕ ΤΟ USER_CODE");	    		     
    	   } 
    	if(userCode >0 && startCalendar != null){
    	   try{
    	       c = db.getOfWorkValuesFromTable(userCode, startCalendar.getTimeInMillis());
    	       this.getCursorValues(c); //παίρνει τις τιμές από τον κέρσορα
    	      }catch(Exception e){
    		     db.closeDB();
		         e.printStackTrace();
		         Log.e("SQL_ERROR", "ΔΕΝ ΒΡΕΘΗΚΕ ΑΔΕΙΑ");    		
    	      }
    	}//end if
    	else{
    		 this.showingDialogWithMessage(dialogTitle, errorMessage, R.drawable.warning);
    		 this.resetValues();
    	    }
    	db.closeDB();    	
    }//end method
  //----------------------------------------------------
    /*
     * Επιστρέφει τις τιμές της βάρδιας που βρίσκονται στον Cursor
     * και τις θέτει στα TextViews της φόρμας. Καλείται στην Προηγούμενη μέθοδο:
     * setShiftValuesInTextViews()
     */
    private void getCursorValues(Cursor c){
    	
    	   if(c.moveToFirst()) {
    		 SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    		 do{   
    			ofwork_id = c.getInt(0); //επιστρέφει το ID της εγγραφής της άδειας
				Calendar start_date = Calendar.getInstance();
				Calendar end_date   = Calendar.getInstance();
  				start_date.clear();
  				end_date.clear();
    		    start_date.setTimeInMillis(c.getLong(2));
    		    end_date.setTimeInMillis(c.getLong(3));
    		    endCalendar = end_date;  //θέτει την ημερομηνία στην global μεταβλητή
    		    String duration= String.valueOf(c.getInt(4));  //διάρκεια σε ημέρες
    		    String category= c.getString(5);               //κατηγορία αδείας
    		    String year    = String.valueOf(c.getInt(6));  //το έτος αδείας
    		    String sumDays = String.valueOf(c.getInt(7));  //Ημέρες κανονικής που δικαιούται	    
    		    String comen   = c.getString(8);    		   //σχόλια
    		    initialCategoryOfWork = category;              //κρατά την αρχική τιμή της κατηγορία άδειας σε global var
    		    startDate.setText(df.format(start_date.getTime()));
    		    endDate.setText(df.format(end_date.getTime()));
    		    
    		    //επιστρέφει τον αριθμό δείκτη γραμμής της κατηγορίας άδειας στο spinner
    		    int catIndex = this.getRowIndexOfSelectionValue(ofWorkTypeValues, category); 
    		    ofWorkTypes.setSelection(catIndex);
    		    //επιστρέφει τον αριθμό δείκτη γραμμής του έτους στο spinner
    		    int yearIndex = this.getRowIndexOfSelectionValue(ofWorkYearValues, year); 
    		    ofWorkYear.setSelection(yearIndex); 
    		    
    		    if(category.equals("ΑΝΑΡΩΤΙΚΗ") || category.equals("ΦΟΙΤΗΤΙΚΗ")      || 
    		       category.equals("ΤΟΚΕΤΟΥ")   || category.equals("ΑΝΕΦ ΑΠΟΔΟΧΩΝ")  ||
    		       category.equals("ΓΟΝΙΚΗ")    || category.equals("ΥΙΟΘΕΣΙΑΣ ΤΕΚΝΟΥ") ) {
    		    	
    		       ofWorkDays.setAdapter(dataMultiDaysAdapter);
    		       
     		       //επιστρέφει τον αριθμό δείκτη γραμμής της διάρκειας  άδειας στο spinner
     		       int durIndex = this.getRowIndexOfSelectionValue(ofWorkMultiDaysValues, duration); 
     			   ofWorkDays.setSelection(durIndex);    
     			   sumDaysLayout.setVisibility(View.GONE); //κρύβει τη γραμμή επιλογής πλήθους ημερών αδείας     			        			   
    		    }
    		    else{ //αν πρόκειται για άλλο τύπο άδειας
    		       ofWorkDays.setAdapter(dataDaysHasGotAdapter);    		       
    		       //επιστρέφει τον αριθμό δείκτη γραμμής της διάρκειας  άδειας στο spinner
    		       int durIndex = this.getRowIndexOfSelectionValue(ofWorkDaysValues, duration); 
    			   ofWorkDays.setSelection(durIndex);  
    			   sumDaysLayout.setVisibility(View.GONE); //κρύβει τη γραμμή επιλογής πλήθους ημερών αδείας 
    			   if(category.equals("ΚΑΝΟΝΙΚΗ")){
    			      ofWorkSumDays.setAdapter(dataDaysAdapter);
    			      //επιστρέφει τον αριθμό δείκτη γραμμής των ημερών που δικαιούτε στο spinner
    			      int sumIndex = this.getRowIndexOfSelectionValue(ofWorkDaysValues, sumDays);
    			      ofWorkSumDays.setSelection(sumIndex);
    			      starOfSumDays.setText("*"); 
    			      sumDaysLayout.setVisibility(View.VISIBLE); //εμφανίζει τη γραμμή επιλογής πλήθους ημερών αδείας 
    			   }
    		    }    		    
    	    	ofWorkComens.setText(comen);
    		   }while(c.moveToNext());
    	   }//end if    	   
       }//end method
    //---------------------------------------------------------------------
    /*
     * Επιστρέφει τη θέση που βρίσκεται καταχωρημένη η τιμή που περιλαμβάνεται
     * σε ένα Spinner. Καλείται στην προηγούμενη:
     * getCursorValues(Cursor c)
     */
    private int getRowIndexOfSelectionValue(ArrayList<String> tableData , String selValue){
    	int rowIndex = 0;
    	for(int i=1; i<tableData.size(); i++){
    		if(tableData.get(i).equals(selValue)){
    			rowIndex = i;
    		    break;
    	    }
    	}
    	return rowIndex;
    }//end method
    
    //------------------------ UPDATE -----------------------------
    /*
     * Η μέθοδος ενημερώνει τις αλλαγμένες τιμές της αδείας
     */
    private void  updateOfWorkValues(){
    	
    	SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());				
		String  printDate = df.format(startCalendar.getTime());
    	String  updatedTitle     ="Επιτυχημένη Αλλαγή";
    	String  updatedMessage   ="Τα στοιχεία της Άδειας : "+printDate+" έχουν Αλλάξει";
    	String  notUpdatedTitle  ="Αποτυχία Αλλαγής";
    	String  notUpdatedMessage="Δεν Έγινε Μεταβολή Στοιχείων της Άδειας : "+printDate;
    	boolean updatedRow = false;	
    	int sumDays = 0;
    	if(ofwork_id > 0){  //αν έχει επιλεγεί κάποι άδεια για update
    	  int    duration = Integer.parseInt(ofWorkDays.getSelectedItem().toString()); 
    	  String category = ofWorkTypes.getSelectedItem().toString(); 
    	  int    year     = Integer.parseInt(ofWorkYear.getSelectedItem().toString()); 
    	  String str_sumDays = ofWorkSumDays.getSelectedItem().toString();
      	  if(!str_sumDays.equals("Επέλεξε"))  //αν έχουν επιλεγεί πλήθος ημερών αδείας που δικαιούται/έτος
      	    sumDays = Integer.parseInt(str_sumDays); 
    	  String com     = ofWorkComens.getText().toString();    	
    	  DBControler db = new DBControler(this);    	
    	  db.open();   	
    	  updatedRow = db.updateOfWorkValues(
    			                 ofwork_id,
    			                 startCalendar.getTimeInMillis(),
    			                 endCalendar.getTimeInMillis(),
    			                 duration,
    			                 category,
    			                 year,
    			                 sumDays,
    			                 com);
    	  Log.i("SUCCESS_UPDATE", ""+updatedRow);
    	  db.closeDB();	
    	}//end if
    	if(updatedRow)  //αν έγινε επιτυχώς το update 
    	   this.showingDialogAboutUpdate(updatedTitle, updatedMessage, R.drawable.info_icon);    	
    	else      //αν δεν έγινε update των στοιχείων της βάρδιας
    	   this.showingDialogAboutUpdate(notUpdatedTitle, notUpdatedMessage, R.drawable.error_icon); 
    	  
    }//emd method
    
    //---------------------------------------------------------------------
    /*
     * Η μέθοδος θέτει τιμές Κατηγοριών Αδειών 
     * στo πίνακα τιμών κάθε spinner
     */
    private void setValuesInSpinnerTypes(ArrayList<String> spData){
    	spData.add("Επέλεξε");
    	spData.add("ΚΑΝΟΝΙΚΗ");
    	spData.add("ΧΕΙΜΕΡΙΝΗ");
    	spData.add("ΑΝΑΡΩΤΙΚΗ");
    	spData.add("ΑΙΜΟΔΟΤΙΚΗ");         //1-2 Ημέρες
    	spData.add("ΑΝΘΥΓΙΕΙΝΗΣ ΕΡΓ.");   //7 ημερες
    	spData.add("ΕΚΛΟΓΙΚΗ");      	
    	spData.add("ΣΧΟΛΙΚΗΣ ΠΡΟΟΔΟΥ");   //6 ημέρες
    	spData.add("ΣΥΝΔΙΚΑΛΙΣΤΙΚΗ"); 
    	spData.add("ΑΘΛΗΤΙΚΗ");     	  //10 ημέρες
    	spData.add("ΓΑΜΟΥ");     	      //5 ημέρες
    	spData.add("ΜΑΡΤΥΡΑΣ ΣΕ ΔΙΚΗ"); 
    	spData.add("ΚΑΘΗΚΟΝ ΕΝΟΡΚΟΥ"); 
    	spData.add("ΑΝΑΠΗΡΙΚΗ");          //6 ημέρες
    	spData.add("ΑΝΑΠΗΡΩΝ ΤΕΚΝΩΝ");    //6 ημέρες
    	spData.add("ΜΟΝΟΓΟΝΕΙΚΕΣ ΟΙΚΟΓ.");//6-8 ημέρες
    	spData.add("ΥΙΟΘΕΣΙΑΣ ΤΕΚΝΟΥ");   //90 ημέρες
    	spData.add("ΕΞΑΡΤΩΜΕΝΩΝ ΜΕΛΩΝ");  //6-14 ημέρες
    	spData.add("ΜΕΤΑΓΓΙΣΗ ΑΙΜΑΤΟΣ");  //22 ημέρες
    	spData.add("ΛΟΓΩ ΠΕΝΘΟΥΣ");       //2 ημέρες
    	spData.add("ΑΝΩΤΕΡΑΣ ΒΙΑΣ");      //4 ημέρες
    	spData.add("ΤΙΜΗΤΙΚΗ");
    	spData.add("ΤΟΚΕΤΟΥ");            //134 ημέρες
    	spData.add("ΑΝΕΦ ΑΠΟΔΟΧΩΝ");      //ολες από εδώ και κάτω άνεφ αποδοχών
    	spData.add("ΦΟΙΤΗΤΙΚΗ");
    	spData.add("ΓΟΝΙΚΗ");    	 
    }//end method
    //---------------------------------------------------------------------
    /*
     * Η μέθοδος θέτει τιμές Ημερών στo πίνακα τιμών κάθε spinner
     */
    private void setValuesInSpinnerDays(ArrayList<String> spData){
    	spData.add("Επέλεξε");
    	spData.add("1");
    	spData.add("2");
    	spData.add("3");
    	spData.add("4");
    	spData.add("5");
    	spData.add("6");
    	spData.add("7");
    	spData.add("8");
    	spData.add("9");
    	spData.add("10");
    	spData.add("11");
    	spData.add("12");
    	spData.add("13");
    	spData.add("14");
    	spData.add("15");
    	spData.add("16"); 
    	spData.add("17"); 
    	spData.add("18");
    	spData.add("19"); 
    	spData.add("20");    	
    	spData.add("21");      	
    	spData.add("22");
    	spData.add("23");
    	spData.add("24");
    	spData.add("25");
    	spData.add("26"); 
    	spData.add("27"); 
    	spData.add("28");
    	spData.add("29"); 
    	spData.add("30"); 
    }//end method  
    //---------------------------------------------------------------------
    /*
     * Η μέθοδος θέτει τιμές Ημερών στo πίνακα τιμών κάθε spinner
     * όταν πρόκειται για συγκεκριμένες κατηγορίες αδειών εκτεταμένης διάρκειας
     * όπως αναρωτική ή άνεφ αποδοχών 
     */
    private void setValuesInSpinnerDaysWhenIsSick(ArrayList<String> spData){
    	spData.add("Επέλεξε");
    	spData.add("1");
    	spData.add("2");
    	spData.add("3");
    	spData.add("4");
    	spData.add("5");
    	spData.add("6");
    	spData.add("7");
    	spData.add("8");
    	spData.add("9");
    	spData.add("10");
    	spData.add("11");
    	spData.add("12");
    	spData.add("13");
    	spData.add("14");
    	spData.add("15");
    	spData.add("16"); 
    	spData.add("17"); 
    	spData.add("18");
    	spData.add("19"); 
    	spData.add("20");    	
    	spData.add("21");      	
    	spData.add("22");
    	spData.add("23");
    	spData.add("24");
    	spData.add("25");
    	spData.add("26"); 
    	spData.add("27"); 
    	spData.add("28");
    	spData.add("29"); 
    	spData.add("30"); 
    	spData.add("31");
    	spData.add("32");
    	spData.add("33");
    	spData.add("34");
    	spData.add("35");
    	spData.add("36");
    	spData.add("37");
    	spData.add("38");
    	spData.add("39");
    	spData.add("40");
    	spData.add("41");
    	spData.add("42");
    	spData.add("43");
    	spData.add("44");
    	spData.add("45");
    	spData.add("46"); 
    	spData.add("47"); 
    	spData.add("48");
    	spData.add("49"); 
    	spData.add("50");    	
    	spData.add("51");      	
    	spData.add("52");
    	spData.add("53");
    	spData.add("54");
    	spData.add("55");
    	spData.add("56"); 
    	spData.add("57"); 
    	spData.add("58");
    	spData.add("59"); 
    	spData.add("60"); 
    	spData.add("61");
    	spData.add("62");
    	spData.add("63");
    	spData.add("64");
    	spData.add("65");
    	spData.add("66");
    	spData.add("67");
    	spData.add("68");
    	spData.add("69");
    	spData.add("70");
    	spData.add("71");
    	spData.add("72");
    	spData.add("73");
    	spData.add("74");
    	spData.add("75");
    	spData.add("76"); 
    	spData.add("77"); 
    	spData.add("78");
    	spData.add("79"); 
    	spData.add("80");    	
    	spData.add("81");      	
    	spData.add("82");
    	spData.add("83");
    	spData.add("84");
    	spData.add("85");
    	spData.add("86"); 
    	spData.add("87"); 
    	spData.add("88");
    	spData.add("89"); 
    	spData.add("90"); 
    	spData.add("91");
    	spData.add("92");
    	spData.add("93");
    	spData.add("94");
    	spData.add("95");
    	spData.add("96");
    	spData.add("97");
    	spData.add("98");
    	spData.add("99");
    	spData.add("100");
    	spData.add("101");
    	spData.add("102");
    	spData.add("103");
    	spData.add("104");
    	spData.add("105");
    	spData.add("106"); 
    	spData.add("107"); 
    	spData.add("108");
    	spData.add("109"); 
    	spData.add("110");    	
    	spData.add("111");      	
    	spData.add("112");
    	spData.add("113");
    	spData.add("114");
    	spData.add("115");
    	spData.add("116"); 
    	spData.add("117"); 
    	spData.add("118");
    	spData.add("119"); 
    	spData.add("120"); 
    	spData.add("121");
    	spData.add("122");
    	spData.add("123");
    	spData.add("124");
    	spData.add("125");
    	spData.add("126");
    	spData.add("127"); 
    	spData.add("128"); 
    	spData.add("129");
    	spData.add("130"); 
    	spData.add("131");    	
    	spData.add("132");      	
    	spData.add("133");
    	spData.add("134");
    	spData.add("135");
    	spData.add("136");
    	spData.add("137"); 
    	spData.add("138"); 
    	spData.add("139");
    	spData.add("140"); 
    	spData.add("141");
    	spData.add("142");
    	spData.add("143");
    	spData.add("144");
    	spData.add("145");
    	spData.add("146");
    	spData.add("147"); 
    	spData.add("148"); 
    	spData.add("149");
    	spData.add("150"); 
    	spData.add("151");    	
    	spData.add("152");      	
    	spData.add("153");
    	spData.add("154");
    	spData.add("155");
    	spData.add("156");
    	spData.add("157"); 
    	spData.add("158"); 
    	spData.add("159");
    	spData.add("160"); 
    	spData.add("161"); 
    	spData.add("162");
    	spData.add("163");
    	spData.add("164");
    	spData.add("165");
    	spData.add("166");
    	spData.add("167");
    	spData.add("168"); 
    	spData.add("169"); 
    	spData.add("170");
    	spData.add("171"); 
    	spData.add("172");    	
    	spData.add("173");      	
    	spData.add("174");
    	spData.add("175");
    	spData.add("176");
    	spData.add("177");
    	spData.add("178"); 
    	spData.add("179"); 
    	spData.add("180"); 
    }//end method    
    //---------------------------------------------------------------------
    /*
     * Η μέθοδος θέτει τιμές Etών στo πίνακα τιμών κάθε spinner
     */
    private void setValuesInSpinnerYear(ArrayList<String> spData){
    	spData.add("Επέλεξε");
    	spData.add("2010");
    	spData.add("2011");
    	spData.add("2012");
    	spData.add("2013");
    	spData.add("2014");
    	spData.add("2015");
    	spData.add("2016");
    	spData.add("2017");
    	spData.add("2018");
    	spData.add("2019");
    	spData.add("2020");
    	spData.add("2021");
    	spData.add("2022");
    	spData.add("2023");
    	spData.add("2024");
    	spData.add("2025");
    	spData.add("2026");
    	spData.add("2027");
    	spData.add("2028");
    	spData.add("2029"); 
    	spData.add("2030"); 
    	spData.add("2031");
    	spData.add("2032"); 
    	spData.add("2033");    	
    	spData.add("2034");      	
    	spData.add("2035");
    	spData.add("2036");
    	spData.add("2037");
    	spData.add("2038");
    	spData.add("2039"); 
    	spData.add("2040");    	
    }//end method        
    //-------------------------------------------------------------
    /*
     * Διαγράφει τις επιλεγμένες τιμές
     */
    private void resetValues(){
    	selectOfWork.setSelection(0);
    	selectedDate = null;
    	startDate.setText("Πατήστε εδώ");
		endDate.setText("Πατήστε εδώ");
		ofWorkDays.setSelection(0);
		ofWorkTypes.setSelection(0);
		ofWorkYear.setSelection(0);	
		ofWorkSumDays.setSelection(0); 
		ofWorkComens.setText("");
		selectedStartDate= null;
		selectedEndDate  = null;
		startCalendar    = null;
		endCalendar      = null;
		ofwork_id = 0; //μηδενίζει το ID της εγγραφής
    }//end method 
    //-------------------------------------------------------------------
    /*
     * Αυτή η μέθοδος ελέγχει αν έχουν συμπληρωθεί όλα τα απιτούμενα
     * πεδία της φόρμας 
     */
    private boolean checkFormValues(){
    	//Τοπικές μεταβλητές
    	String errorFirstDateMessage  = "Δεν Καθορίσατε Ημερομηνία Έναρξης";
    	String errorEndDateMessage    = "Δεν Καθορίσατε Ημερομηνία Λήξης"; 
    	String errorOfWorkDaysMessage = "Δεν Καθορίσατε Το πλήθος Ημερών Αδείας";
    	String errorOfWorkTypesMessage= "Δεν Καθορίσατε Την Κατηγορία Άδείας";
    	String errorOfWorkYearMessage = "Δεν Καθορίσατε Το Έτος Άδείας";
    	String errorOfWorkSumDaysMessage  = "Δεν Καθορίσατε Το Πλήθος Ημερών Κανονικής Άδείας που Δικαιούστε";
    	String errorSelectedEndDateMessage= "Η Ημερομηνία Λήξης Αδείας είναι μικρότερη της Έναρξης";
    	
    	if(startDate.getText().toString().equals("Πατήστε εδώ")){
    	   Toast tost = Toast.makeText(this, errorFirstDateMessage, Toast.LENGTH_SHORT); 
 	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
 	       tost.show();
 	       return false;
    	}
    	if(endDate.getText().toString().equals("Πατήστε εδώ")){
     	   Toast tost = Toast.makeText(this, errorEndDateMessage, Toast.LENGTH_SHORT); 
  	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
  	       tost.show();
  	       return false;
     	}
    	if(ofWorkTypes.getSelectedItem().toString().equals("Επέλεξε")){ //τύπος άδειας
      	   Toast tost = Toast.makeText(this, errorOfWorkTypesMessage, Toast.LENGTH_SHORT); 
   	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
   	       tost.show();
   	       return false;
      	}    	
    	if(ofWorkYear.getSelectedItem().toString().equals("Επέλεξε")){ //έτος άδειας
      	   Toast tost = Toast.makeText(this, errorOfWorkYearMessage, Toast.LENGTH_SHORT); 
   	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
   	       tost.show();
   	       return false;
      	}
    	if(ofWorkDays.getSelectedItem().toString().equals("Επέλεξε")){ // ημέρες άδειας
     	   Toast tost = Toast.makeText(this, errorOfWorkDaysMessage, Toast.LENGTH_SHORT); 
  	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
  	       tost.show();
  	       return false;
     	}    	
    	if(ofWorkSumDays.getSelectedItem().toString().equals("Επέλεξε") && 
    	   ofWorkTypes.getSelectedItem().toString().equals("ΚΑΝΟΝΙΚΗ")){ //ημέρες αδείας που δικαιούτε ανά έτος
       	   Toast tost = Toast.makeText(this, errorOfWorkSumDaysMessage, Toast.LENGTH_SHORT); 
    	   tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
    	   tost.show();
    	   return false;
       	}
    	//ελέγχει αν η ημερομηνία λήξης άδειας είναι μεγαλύτερη της ημερομηνίας έναρξης 
    	if(endCalendar.getTimeInMillis() < startCalendar.getTimeInMillis()){  
    	   Toast tost = Toast.makeText(this, errorSelectedEndDateMessage, Toast.LENGTH_SHORT); 
	   	   tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
	   	   tost.show();
    	   return false;
    	}    	
    	return true;
    }//end method
  //-----------------------------------------------------------------
		/*Εμφανίζει Dialog το οποίο παίρνει ως παραμέτρους :
		 * το τίτλο και το μήνυμα προβολής στο χρήστη
		 */
		public void showingDialogAboutUpdate(String title, String errorMessage, int iconResId){		
			
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
					       finish(); //τερματίζει τη δραστηριότητα
			          }
			    });					   		       
			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
	    } //end method	    
        //-----------------------------SHOW MESSAGE DIALOG ---------------------------
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
  	    //-------------------------- UPDDATE DIALOG OR CANCEL------------------------------- 
  	    /*
  	     * Εμφανίζει Dialog με μήνυμα Προειδοποίησης στο Χρήστη ότι πρόκειται
  	     * να αλλαχθούν τα στοιχεία της Άδειας. Αν Πατήσει ΟΚ η Άδεια αλλάζει
  	     * αλλιώς ακυρώνεται η αλλαγή
  	     */
  	    public void warningOfWorkUpdate(){				
  	    	
  	  				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
  	  				String defaultMessage = "Πρόκειται να Μεταβάλετε τα στοιχεία της Άδειας :"+selectedDate
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
  	  					       updateOfWorkValues(); //καλεί τη μέθοδο που θα κάνει update την βάρδια
  	  			          }
  	  			    });	
  	  			    alertDialog.setNegativeButton("ΑΚΥΡΩΣΗ", new DialogInterface.OnClickListener() {
    			          public void onClick(DialogInterface dialog, int choice) {            	    
    					       dialog.cancel(); //κλείνει το dialog   					       
    			          }
    			    });		
  	  			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
  	  	} //end method	
  	    //----------------------------SHOW HELP MESSAGE -----------------------------
  	    /*
  	     * Εμφανίζει μήνυμα βοήθειας στο χρήστη
  	     */
  	    private void showingHelpMessage(){
	    	 String titleHelp = "Οδηγίες !!!";
	    	 Spanned defaultMessage = Html.fromHtml("Για Επιστροφή Πατήστε: \'ΟΚ\'"); 	
	    	 Spanned helpMes = Html.fromHtml( "Αρχικά απλώς αγγίξτε το πεδίο με την ένδειξη " +
	    	 		    "<font color=\"#D2691E\">\'Επιλέξτε Άδεια\'</font>. "
	    	 		  + "Στη συνέχεια από το πτυσσόμενο μενού επιλέγετε με βάση την Ημερομηνία Έναρξης"
	    	 		  + " την άδεια της οποίας θέλετε να μεταβάλετε τα στοιχεία.<br>"
	    	 		  + "Η εφαρμογή αφού συμπληρώσει τα στοιχεία της Άδειας τα προβάλει "
	    	 		  + "στα αντίστοιχα πεδία ως επιλεγμένες τιμές.<br>"
	  				  + "<font color=\"#FF0000\"><b>1.</b></font>" +
	  				    " Για να αλλάξετε μια τιμή σε ένα πεδίο απλώς αγγίξτε την επιλεγμένη τιμή "
	  				  + "και επιλέξτε από το πτυσσόμενο μενού την νέα που θα καταχωρηθεί στο πεδίο.<br>"
	  				  + "<font color=\"#FF0000\"><b>2.</b></font>" +
	  				    " Αφού βεβαιωθείτε ότι έχουν επιλεγεί οι νέες τιμές που θα αντικαταστήσουν "
	  				  + "τις προηγούμενες πατήστε: " +
	  				    "<font color=\"#008B00\"><b>\'OK\'</b></font>.<br>"
	  				  + "<font color=\"#FF0000\"><b>3.</b></font>" +
	  				    " Η εφαρμογή στη συνέχεια ζητά επιβεβαίωση των επιλογών σας.<br>"
	  				  + "<font color=\"#FF0000\"><b>4.</b></font>" +
	  				    " Αν πατήσετε: " +
	  				    "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>" +
	  				    " ή εφαρμογή αντικαθιστά στην εγγραφή Αδείας τις νέες τιμές. "
	  				  + "Διαφορετικά αν πατήσετε: " +
	  				    "<font color=\"#FF1493\">\'Ακύρωση\'</font>" +
	  				    " η διεργασία μεταβολής τερματίζεται "
	  				  + "και η Άδεια μένει ως έχει.<br><br>" + defaultMessage);
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
	    }//end method
  //-----------------------------------------------------------------------------------	    
}//end class 
