package com.kutsubogeras.shifts.queries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.kutsubogeras.shifts.MainActivity;
import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.ShiftOfUser;
import com.kutsubogeras.shifts.R.id;
import com.kutsubogeras.shifts.R.layout;
import com.kutsubogeras.shifts.R.menu;
import com.kutsubogeras.shifts.R.string;
import com.kutsubogeras.shifts.data.DBControler;
import com.kutsubogeras.shifts.ofwork.OffWork;
import com.kutsubogeras.shifts.queries.NavigationDrawerFragment.NavigationDrawerCallbacks;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class YearOfWorks extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_year_of_works);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.year_of_works, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private TextView queryTitle;
		private Spinner spinYear;
		private String title;		
        private ListView lv;
        private Button but_OK;
	    private Button but_Back;
	    private Button but_Reset;
        private int selectedYear = 0;
        private ArrayAdapter<String> spinnerAdapter;        
        private ArrayList<String> yearValues = new ArrayList<String>();  //οι τιμές επιλογής ετους
        private ResultsListOffWorkAdapter adapter;
        private ArrayList<OffWork> userOffWorks;
		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_year_of_works,
					container, false);
			queryTitle = (TextView) rootView.findViewById(R.id.textView_OfWorkYear_Title);
			spinYear = (Spinner) rootView.findViewById(R.id.Spinner_OfWorkYear_Year);
			lv = (ListView)rootView.findViewById(R.id.listView_OfWorkList);
			but_OK = (Button) rootView.findViewById(R.id.button_OfWorkYear_OK);
		    but_Back = (Button) rootView.findViewById(R.id.button_OfWorkYear_Back);
		    but_Reset = (Button) rootView.findViewById(R.id.button_OfWorkYear_Reset);
			title = getActivity().getIntent().getStringExtra(
  		                         MainActivity.showQueriesFragment.QUERY_TITLE);
            queryTitle.setText(title);
			this.setValuesInSpinnerYear(yearValues);
			this.createSpinnerAdapter();			
			lv.setTextFilterEnabled(true);
		    lv.setSoundEffectsEnabled(true);
		    lv.setClickable(true);	
		    this.createListeners();
		    return rootView;
		}
		//---------------------- CHECK DATES ------------------------------
	    /*
	     * Ελέγχει αν έχει επιλεγεί κάποιο Έτος Αδείας
	     */
       private boolean chechYearSelectedValue(){
    	   String title = "Λοίπουν Στοιχεία";
    	   String SelectedYearMessage = "Δεν έχετε Επιλέξει Έτος Αδείας";	
    	   String yearValue = spinYear.getSelectedItem().toString();    	   
    	   if(yearValue.equals("Επέλεξε") ){
    		  lv.setAdapter(null);
    		  this.showingMessageDialog(title, SelectedYearMessage);
			  return false;
    	   }//end if
    	   return true;    	   
       }//end method
       //--------------------------------------------------------------
       private void createListeners(){
    	 //Spinner για την επιλογή Έτους
	    	spinYear.setOnItemSelectedListener(new OnItemSelectedListener() {
		    	    @Override
		    	    public void onItemSelected(AdapterView<?> parentView, 
		    	    		       View selectedItemView, int position, long id) {	
		    	    	  //παίρνει την τιμή του έτους από το spinner
		    	    	  String message = "Έπιλεγμένο Έτος : ";
		    	    	  String title = "Λοίπουν Στοιχεία";
		    	    	  String SelectedYearMessage = "Δεν έχετε Επιλέξει Έτος Αδείας";
		    	    	  if(position > 0){ //αν έχει επιλέξει κάποιο έτος εκτός της προτροπής
		    	             String year = spinYear.getItemAtPosition(position).toString();
		    	             selectedYear = Integer.parseInt(year);
		    	             selectedItemView.setBackgroundColor(Color.CYAN);
		    	             selectedItemView.setSoundEffectsEnabled(true);
		    	             //findResults();
		    	             //προβάλει το έτος που επιλέγει σε Toast
		    	             Toast tost = Toast.makeText(getActivity(), message + year, Toast.LENGTH_SHORT); 
			                 tost.setGravity(Gravity.BOTTOM, tost.getXOffset()/2, tost.getYOffset()*2);
			                 tost.show();
		    	          }		    	    	  
		    	    }//end method
		    	    @Override
		    	    public void onNothingSelected(AdapterView<?> parentView) {
		    	        // your code here    	    	
		    	    }
	    	});	
	    	but_OK.setOnClickListener(new View.OnClickListener() {       		
	             public void onClick(View v) { 
	            	 if(chechYearSelectedValue())
	       		       //δημιουργεί και εμφανίζει τα αποτελέσματα
	            	   findResults();
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
       } 
       //------------------ SPINNER ADAPTER -------------------------
       /*
        * Δημιουργεί ένα adapter για χειρισμό των τιμών του έτους που εμαφνίζοντα
        * στο πεδίο spinner επιλογής Έτους Αδείας
        */
       private void createSpinnerAdapter(){	    	   
    	   spinnerAdapter = new ArrayAdapter<String>( getActivity(), 
	                              android.R.layout.simple_spinner_dropdown_item, yearValues);
	       spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice ); 
	       spinYear.setAdapter(spinnerAdapter);       
	       spinYear.setSelection(0);
	       
       }//end method
	   //----------------------SET YEAR VALUES -----------------------------
	    /*
	     * Περνά τιμές Έτους στο ArrayList που χρησιμοποιεί ο SpinnerAdapter
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
		   //---------------------- FIND RESULTS -----------------------------
	       //Αναζητά για σχετικά αποτελέσματα στη ΒΔ
	       private void findResults(){
	    	   String title   = "Αποτυχία Αναζήτησης";		 
 			   String message = "Δεν Βρέθηκαν : \'Άδειες\' για αυτό το Έτος.";		
	    	   DBControler db = new DBControler(getActivity());
	    	   userOffWorks   = new ArrayList<OffWork>();
	    	   lv.setAdapter(null);
	    	   if(adapter != null)
	    	      adapter.clear();
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
	    	   try{
	    	       c = db.getOfWorksValuesFromCurrentYear(userCode, selectedYear);    	        
	    	       sumResults = c.getCount();
	    	       getCursorValues(c);  //κλήση της επόμενης μεθόδου
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
	    		  }    	  
	    	   db.closeDB();
	    	   if(sumResults == 0){     //αν δεν βρέθηκαν βάρδιες για αυτό το διάστημα
	    		  showingMessageDialog(title, message); //εμφανίζει μήνυμα
	    	   }
	    	   else{
	    		  adapter = new ResultsListOffWorkAdapter(getActivity(), 
	                               R.layout.activity_item_list_offworks_year , userOffWorks );
	              lv.setAdapter(adapter); //δημιουργεί και αναθέτει στη λίστα ένα αντάπτορα
	    	   }
	       }//end method
	       //-------------------------------------------------------      
	       /* Διαγράφει όλες τις τιμές στα πεδία της φόρμας αλλά 
	        * και τα περιεχόμενα της λίστας
	        */       
	       private void resetValues(){
	    	     spinYear.setSelection(0);
	    	     lv.setAdapter(null);
	       }//end method
	       //----------------------------------------------------------------
	       /*
	        * Αυτή η μέθοδος παίρνει τις τιμές : Ημερομηνίας, Ημέρας, Βάρδιας 
	        * από τη μεταβλητή Cursor και δημιουργεί ένα αντικείμενο τύπου ShiftOfUser 
	        * όπου και φορτώνονται. Τέλος φορτώνει τα αντικείμενα σε ένα ArrayList.
	        */
	       private void getCursorValues(Cursor c){
	    	   if(c!= null && c.moveToFirst()) {
	    		 SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
	    		 do{
	    		    OffWork curOffWork = new OffWork();    		    
	    		    curOffWork.setStartDate(df.format(c.getLong(2))); //ημερομηνία έναρξης
	    		    curOffWork.setEndDate(df.format(c.getLong(3))); //ημερομηνία λήξης
	    		    curOffWork.setDuration(c.getInt(4)); //διάρκεια
	    		    curOffWork.setCategoryValue(c.getString(5)); //κατηγορία
	    		    curOffWork.setYearValue(c.getInt(6));  //το έτος αδείας
	    		    curOffWork.setTotalDaysOfWork(c.getInt(7));  //Ημέρες κανονικής που δικαιούται	    
	    		    curOffWork.setComents(c.getString(8));    		   //σχόλια
	    		    userOffWorks.add(curOffWork);
	    		   }while(c.moveToNext());
	    	   }//end if
	       }//end method
	       //------------------------- SHOW MESSAGE -------------------------
		   //Εμφανίζει Dialog με μήνυμα του οποίου τον τίτλο και το περιεχόμενο
	       //περνούμε ως παραμέτρους κλήσης ανάλογα με την περίπτωση
		   private boolean showingMessageDialog(String title, String message){	
		    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		  				String defaultMessage = "\nΓια Επιστροφή Πατήστε \'ΟΚ\'"; 	  				
		  			    // Dialog Τίτλος
		  			    alertDialog.setTitle(title);		 
		  			    // Μήνυμα του Dialog
		  			    alertDialog.setMessage(message + defaultMessage);		 
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
	       
		  @Override
		  public void onAttach(Activity activity) {
			  super.onAttach(activity);
			  ((YearOfWorks) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		  }
	}//end fragment

}//end class
