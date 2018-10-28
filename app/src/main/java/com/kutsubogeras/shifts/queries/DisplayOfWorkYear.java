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
import com.kutsubogeras.shifts.ofwork.DisplayOfWorkYearAdapter;
import com.kutsubogeras.shifts.ofwork.OfWorkCategory;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.Build;
import android.preference.PreferenceManager;

public class DisplayOfWorkYear extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_of_work_year);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container_display_of_work_year, 
					new PlaceholderFragment()).commit();
		}
	}//end method

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_of_work_year, menu);
		menu.clear();
		menu.add(0, R.id.item1_of_work_year_help, 0, "Βοήθεια")
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
	        case R.id.item1_of_work_year_help: 
	    	     this.showingHelpMessage();
	    	     return true;		    
	        default:
	             return super.onOptionsItemSelected(item);
	     }//end switch
	}//end method

	//======================== INNER STATIC CLASS FRAGMENT =====================
		/**
		 * A placeholder fragment containing a simple view.
		 */
		public static class PlaceholderFragment extends Fragment {
			  //Μεταβλητές Κλάσης
	           private TextView queryTitle;
		       private Spinner  ofWorkYear;
		       private TextView totalDays;
		       private TextView takeDays;
		       private TextView restOfDays;
		       private ListView lv;
		       private Button   but_OK;
		       private Button   but_Back;
		       private Button   but_Reset;
		       private String   title;
		       private String   selectedYear = null; 
		       private ArrayAdapter<String> spinnerAdapter;
		       private ArrayList<String> yearValues = new ArrayList<String>();  //οι τιμές επιλογής ετους
		       private ArrayList<String> ofWorkCategories = new ArrayList<String>(); //όλες οι κατηγορίες Αδειών
		       private ArrayList<OfWorkCategory> otherOfWorks; //όλες οι κατηγορίες Αδειών		       
		       private ArrayList<Integer> backgrounds = new ArrayList<Integer>();
		       
			public PlaceholderFragment() {
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					                 Bundle savedInstanceState) {
				 View rootView = inflater.inflate(R.layout.fragment_display_of_work_year,
						                          container, false);
				 queryTitle = (TextView)rootView.findViewById(R.id.textView_OfWorkYear_Title);
			     ofWorkYear = (Spinner)rootView.findViewById(R.id.Spinner_OfWorkYear_Year);		    
			     totalDays  = (TextView)rootView.findViewById(R.id.textView_OfWorkYear_SumDays);
			     takeDays   = (TextView)rootView.findViewById(R.id.textView_OfWorkYear_DaysTake);
			     restOfDays = (TextView)rootView.findViewById(R.id.textView_OfWorkYear_Rest);	     
			     but_OK     = (Button)  rootView.findViewById(R.id.button_OfWorkYear_OK);
			     but_Back   = (Button)  rootView.findViewById(R.id.button_OfWorkYear_Back);
			     but_Reset  = (Button)  rootView.findViewById(R.id.button_OfWorkYear_Reset);		     
			     title      = getActivity().getIntent().getStringExtra(
			    		          MainActivity.showQueriesFragment.QUERY_TITLE);
			     queryTitle.setText(title);
			     this.setAllOfWorksCategories(ofWorkCategories); // θέτει σε πίνακα τις κατηγορίες αδειών
			     this.setValuesInSpinnerYear(yearValues);//ενημερώνει το ArrayList με τις τιμές του Έτους
			     this.createSpinnerAdapter();            //δημιουργεί ένα spinner adapter
			     this.createComponensAndListeners();     //δημιουργία Listeners		
			     this.setBackroundIcons();
			     this.checkPreferentBackround(rootView);
			     //δημιουργία Λίστας
			     lv = (ListView)rootView.findViewById(R.id.listView_OfWorkYear_List);			 
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
						 
		    }//end method 
		    
			//------------------ METHODOI --------------------
		    public void createComponensAndListeners(){
			      //Listeners
		    	  //Spinner για την επιλογή Έτους
		    	   ofWorkYear.setOnItemSelectedListener(new OnItemSelectedListener() {
			    	    @Override
			    	    public void onItemSelected(AdapterView<?> parentView, 
			    	    		       View selectedItemView, int position, long id) {	
			    	    	  //παίρνει την τιμή του έτους από το spinner
			    	    	  String message = "Έπιλεγμένο Έτος : ";
			    	    	  if(position > 0){ //αν έχει επιλέξει κάποιο έτος εκτός της προτροπής
			    	             String year = ofWorkYear.getItemAtPosition(position).toString();
			    	             selectedItemView.setBackgroundColor(Color.CYAN);
			    	             selectedItemView.setSoundEffectsEnabled(true);
			    	             //προβάλει το έτος που επιλέγει σε Toast
			    	             Toast tost = Toast.makeText(parentView.getContext(), 
	    	            		       message+"\n"+ year, Toast.LENGTH_SHORT); 
	  			                 tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 2);
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
	       //---------------------- CHECK DATES ------------------------------
		    /*
		     * Ελέγχει αν έχει επιλεγεί κάποιο Έτος Αδείας
		     */
	       private boolean chechYearSelectedValue(){
	    	   String title ="Λοίπουν Στοιχεία";
	    	   String SelectedYearMessage = "Δεν έχετε Επιλέξει Έτος Αδείας";	
	    	   String yearValue = ofWorkYear.getSelectedItem().toString();    	   
	    	   if(yearValue.equals("Επέλεξε") ){
	    		  this.showingMessageDialog(title, SelectedYearMessage);
				  return false;
	    	   }//end if
	    	   return true;    	   
	       }//end method
	       //------------------ SPINNER ADAPTER -------------------------
	       /*
	        * Δημιουργεί ένα adapter για χειρισμό των τιμών του έτους που εμαφνίζοντα
            * στο πεδίο spinner επιλογής Έτους Αδείας
	        */
	       private void createSpinnerAdapter(){	    	   
	    	   spinnerAdapter = new ArrayAdapter<String>( getActivity(), 
		                              android.R.layout.simple_spinner_dropdown_item, yearValues);
		       spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice ); 
		       ofWorkYear.setAdapter(spinnerAdapter);       
		       ofWorkYear.setSelection(0);
		       
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
	       //---------------------------------------------------------------------
	       /*
	        * Η μέθοδος θέτει τιμές Κατηγοριών Αδειών 
	        * στo ArrayList με τα Ονόματα όλων των Αδειών
	        */
	       private void setAllOfWorksCategories(ArrayList<String> spData){
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
		   //---------------------- FIND RESULTS -----------------------------
	       //Αναζητά για σχετικά αποτελέσματα στη ΒΔ
	       private void findResults(){
	    	   //διαγράφει τις τιμές προηγουμένων επιλογών αναζήτησης
	    	    totalDays.setText("");
			    takeDays.setText("");
			    restOfDays.setText("");
	    	    lv.setAdapter(null); //διαγράφει ότι υπάρχει στην λίστα προβολής Ειδικής κατηγορίας Αδειών
	    	    //παίρνει το επιλεγμένο έτος
	    	    String yearValue = ofWorkYear.getSelectedItem().toString();   
	    	    int curYear    = Integer.parseInt(yearValue); //μετατροπή σε ακέραιο του έτους που επιλέγεται
	    	    otherOfWorks   = new ArrayList<OfWorkCategory>(); //δημιουργεί πίνακα για αποθήκευση των ειδικών Αδειών	    	    
	    	    DBControler db = new DBControler(getActivity().getApplicationContext());
	    	    String category;
				int sumRows    = 0;		//το πλήθος γραμμών του πίνακα άδειες
				int sumDaysTook= 0;     //το πλήθος ημερών αδίας που πήρε
				int sumDaysEntitled = 0;//το πλήθος ημερών αδείας που δικαιούτε για αυτό το έτος
				
				db.open();				
				sumRows = db.getTotalOfWorksFromCurrentYear(curYear); //το πλήθος γραμμών με άδειες του τρέχοντος έτους
				if(sumRows > 0){ //αν βρέθηκαν εγγραφές Αδειών για αυτό το τρέχον έτος
				  try{
					  sumDaysEntitled = db.getOfWorksSumDaysPermission(curYear); //επιστρέφει τις ημέρες που δικαιούτε
					 }catch (Exception e){
						db.closeDB(); 
				        e.printStackTrace();
				        Log.e("SQL_ERROR", "ΔΕΝ ΒΡΕΘΗΚΑΝ ΗΜΕΡΕΣ ΑΔΕΙΑΣ ΓΙΑ ΑΥΤΟ ΤΟ ΕΤΟΣ");
				     }	
				  //KANONIKH ADEIA
				  category = ofWorkCategories.get(0); //ΚΑΝΟΝΙΚΗ ΑΔΕΙΑ
				  try{
				      sumDaysTook = db.getSumDaysOfWorkHaveTaken(curYear, category); //το πλήθος αδειών που πείρε		    
				     }catch (Exception e){
					    db.closeDB(); 
			            e.printStackTrace();
			            Log.e("SQL_ERROR", "ΔΕΝ ΒΡΕΘΗΚΑΝ ΑΔΕΙΕΣ");
			         }						 
				  totalDays.setText(""+sumDaysEntitled); //θέτει το άθροισμα αδειών που διακαιούτε στο πεδίο του πίνακα 
				  takeDays.setText(""+ sumDaysTook);     //θέτει το άθροισμα αδειών που πήρε στο πεδίο του πίνακα
				  restOfDays.setText(""+(sumDaysEntitled - sumDaysTook)); //θέτει τη υπόλοιπο αδειών στο πεδίο του πίνακα 
				  
				  //για κάθε άλλη κατηγορία
				  for(int i=1; i<ofWorkCategories.size(); i++){
					  category = ofWorkCategories.get(i); //έχει το όνομα της κατηγορίας αδειας
					  sumDaysTook = 0;
					  OfWorkCategory curCategory = new OfWorkCategory();
					  try{
					     sumDaysTook = db.getSumDaysOfWorkHaveTaken(curYear, category); //το πλήθος αδειών που πείρε		    
					     }catch (Exception e){
						    db.closeDB(); 
				            e.printStackTrace();
				            Log.e("SQL_ERROR", "ΔΕΝ ΒΡΕΘΗΚΑΝ ΑΔΕΙΕΣ");
				         }	
					  if(sumDaysTook > 0){ //αν βρέθηκαν αδειες αυτής της κατηγορίας
						 //Log.i("ΒΡΕΘΗΚΑΝ_ΑΔΕΙΕΣ", ""+sumDaysTook);
					     curCategory.setCategoryValue(category); 
					     curCategory.setDaysTake(sumDaysTook);
					     otherOfWorks.add(curCategory);
					  }//end if
				  }//end for 
				  db.closeDB();
				  //set list adapter
				  if(!otherOfWorks.isEmpty()){ //αν το ArrayList περιέχει και εγγραφές άλλων αδειών
				    //δημιουργεί ένα adapter για προβολή σε λίστα των πολυήμερων ρεπό που βρέθηκαν	 		        
					DisplayOfWorkYearAdapter adapter = new DisplayOfWorkYearAdapter(
	 		    		        getActivity(), R.layout.activity_item_of_ofwork_list,
	 		    		        otherOfWorks );
	                lv.setAdapter(adapter); //δημιουργεί και αναθέτει στη λίστα ένα αντάπτορα
				  }//end if	  				 
				}//end if
				else{  //αν δεν υπάρχουν εγγραφές στη ΒΔ για αυτό το έτος
					db.closeDB();
					ofWorkYear.setSelection(0);
				    showingMessageWhenOfWorksNotExist(yearValue); //εμφανίζει μήνυμα	
				}
	       }//end method
	       
	       //-------------------------------------------------------      
	       /* Διαγράφει όλες τις τιμές στα πεδία της φόρμας αλλά 
	        * και τα περιεχόμενα της λίστας
	        */       
	       private void resetValues(){
	    	     ofWorkYear.setSelection(0);
	    	     totalDays.setText("");
			     takeDays.setText("");
			     restOfDays.setText(""); 
			     lv.setAdapter(null);
	       }//end method
	       //------------------------- SHOW MESSAGE -------------------------
		   //Εμφανίζει Dialog με μήνυμα του οποίου τον τίτλο και το περιεχόμενο
	       //περνούμε ως παραμέτρους κλήσης ανάλογα με την περίπτωση
		   public boolean showingMessageDialog(String title, String message){	
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
	       //------------------------- SHOW MESSAGE -------------------------
		   //Εμφανίζει Dialog με μήνυμα Αποτυχίας Ανεύρεσης Βαρδιών 
		   //με τα συγκεκριμένα χαρακτηριστικά για το αντίστοιχο χρονικό διάστημα
		   public boolean showingMessageWhenOfWorksNotExist(String selYear){	
		    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		  				String defaultMessage = "\nΓια Επιστροφή Πατήστε \'ΟΚ\'"; 	  				
		  			    // Dialog Τίτλος
		  			    alertDialog.setTitle("Αποτυχία Αναζήτησης");		 
		  			    // Μήνυμα του Dialog
		  			    alertDialog.setMessage("Δεν Βρέθηκαν Άδειες για \n"+
		  			    		               "το Έτος : " + selYear + defaultMessage);		 
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
		   
		}//end inner class fragment
		//============================== ΒΟΗΘΕΙΑ ====================================
			//Εμφανίζει Dialog με Πληροφορίες Συμπλήρωσης της φόρμας  
			//για συγκεκριμένη ημερομηνία
			public void showingHelpMessage() {
				
			    	  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			  		  Spanned defaultMessage = Html.fromHtml("Για Επιστροφή Πατήστε: \'ΟΚ\'"); 
			  		  Spanned helpMes = Html.fromHtml("Αρχικά επιλέγετε το Έτος στο πεδίο: " +
			  		  		  "<font color=\"#D2691E\">\'Έτος Αδείας\'</font>.<br>" +
			  				  "<font color=\"#FF0000\"><b>1.</b></font>" +
			  				  " Απλά αγγίξτε το πλαίσιο με την ένδειξη: " +
			  				  "<font color=\"#ff00ff\">\'Επέλεξε\'</font> " +
			  				  "δίπλα από την αντίστοιχη ετικέτα " +
			  				  "και επιλέξτε από την πτυσσόμενη λίστα τιμών το " +
			  				  "<font color=\"#0000FF\"><b>\'Έτος\'</b></font> που θέλετε.<br>" +
			  				  "<font color=\"#FF0000\"><b>2.</b></font>" +
			  				  " Αφού επιλέξετε το Έτος Πατήστε: " +
			  				  "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>.<br>" +
			  				  "Η εφαρμογή στη συνέχεια μετρά τις Κανονικές Άδειες που βρίσκει "
			  				  + "για το συγκεκριμένο Έτος και εμφανίζει τα αποτελέσματα υπολογίζοντας "
			  				  + "και τα Υπόλοιπα Αδειών.<br>" +
			  				  "<font color=\"#FF0000\"><b>3.</b></font>" +
			  				  " Στην πτυσσόμενη λίστα (πιο κάτω) εμφανίζονται οι ημέρες Αδείας που έχετε πάρει "
			  				  + "για κάθε κατηγορία Αδείας για την οποία βρέθηκαν καταχωρημένες εγγραφές.<br>" +
			  				  "Αν δεν βρεθούν Άδειες για αυτό το Έτος εμφανίζεται κατάλληλο μήνυμα.<br><br>" + defaultMessage);	
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
