package com.kutsubogeras.shifts.queries;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.kutsubogeras.shifts.MainActivity;
import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.MainActivity.showQueriesFragment;
import com.kutsubogeras.shifts.R.drawable;
import com.kutsubogeras.shifts.R.id;
import com.kutsubogeras.shifts.R.layout;
import com.kutsubogeras.shifts.R.menu;
import com.kutsubogeras.shifts.data.DBControler;
import com.kutsubogeras.shifts.settings.SettingsShifts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.TextView;
/**
 * 
 * @author Thanasis
 * Η κλάση αυτή υπολογίζει τα extra repo για τους τραπεζο-υπαλλήλους
 * που εργάζονται πέραν τοων 7 ωρών και 35 min ην ημέρα (όπως συνήθως συμβαίνει)
 * για όσους εργάζονται σε βάρδιες 8-ώρου
 * Υπολογίζονται για όλο το έτος
 */
/**
 * @author Thanasis
 *
 */
public class DisplayExtraRepo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_extra_repo);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container_display_extra_repo, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_extra_repo, menu);
		menu.clear();
		menu.add(0, R.id.item1_display_extra_repo, 0, "Βοήθεια")
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
	     case R.id.item1_display_extra_repo: 
	    	 this.showingHelpMessage();
	    	 return true;	     		
	     default:
	         return super.onOptionsItemSelected(item);
	}
	}
    //======================= fragment =============================
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		  //Μεταβλητές Κλάσης
		   private TextView queryTitle;
           private TextView mustTakeRepo;
	       private TextView foundRepo;
	       private TextView extraRepo;
	       private TextView takeExtra;	       
	       private TextView restExtra;
	       private String   title;
	       private ArrayList<Integer> backgrounds = new ArrayList<Integer>();
	       
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				                 Bundle savedInstanceState) {
			 View rootView = inflater.inflate(R.layout.fragment_display_extra_repo,
					                          container, false);
			 queryTitle  = (TextView)rootView.findViewById(R.id.textView_ExtraRepo_Title);
			 mustTakeRepo= (TextView)rootView.findViewById(R.id.textView_ExtraRepo_SumRepo);
			 foundRepo   = (TextView)rootView.findViewById(R.id.textView_ExtraRepo_SumTake);
			 extraRepo   = (TextView)rootView.findViewById(R.id.textView_ExtraRepo_Extra);
			 takeExtra   = (TextView)rootView.findViewById(R.id.textView_ExtraRepo_ExtraTake);
			 restExtra   = (TextView)rootView.findViewById(R.id.textView_ExtraRepo_ExtraRest);
			 title       = getActivity().getIntent().getStringExtra(
   		                                 MainActivity.showQueriesFragment.QUERY_TITLE);
			 queryTitle.setText(title);
			 this.setBackroundIcons();
			 this.checkPreferentBackround(rootView);
			 this.findResults();
			 return rootView;
		}//end method		
	   
	   //------------------------------------------------------------------
       //Αναζητά για σχετικά αποτελέσματα στη ΒΔ
       private void findResults(){
    	   
    	   DBControler db = new DBControler(getActivity().getApplicationContext());
    	   //Cursor c;    	   
    	   int  userCode  = 0;    // ο κωδικός εργαζομένου
    	   long sumRows   = 0;    // το πλήθος γραμμών του πίνακα
    	   long sumResults= 0;    // το πλήθος ΡΕΠΟ
    	   long sumExtraTime = 0; // ο συνολικός έξτρα χρόνος που εργάστηκε
    	   String shiftValue = "ΡΕΠΟ";
    	   int deserveRepo= 0;    //ρεπό που δικαιούται ο εργαζόμενος
    	   int rest_extra = 0;    //τα υπόλοιπα που δεν πήρε
    	   int take_extra = 0;    //αυτά που πήρε έξτρα
    	   float deserveΕxtra = 0;//το πλήθος έξτρα ρεπό που διακαιούται
    	   final int DEFAULT_TIME = 445; // ο χρόνος εργασίας του υπαλλήλου Τραπέζης
    	   db.open();
    	   try{
    	       userCode = db.getIdValueFromTable("Employee");
    	      }catch(Exception e){
    		   db.closeDB();
    		   e.printStackTrace();
    		   Log.e("ERROR_USER_CODE", "CODE_NOT_FUND");
    	      }    	   
    	   try{
    		   sumRows    = db.getSumRowsFromShiftTable(userCode);   //επιστρέφει το πήθος εγγραφών του πίνακα: Shift
    		   sumResults = db.getCountFromTableWithShiftValue(userCode, shiftValue); //το πλήθος των βαρδιών ΡΕΠΟ που μετρήθηκαν
    	      }catch(Exception e){
    	    	  db.closeDB();
    			  e.printStackTrace();
    			  Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
    		  } 
    	   try{
    		   sumExtraTime = db.getShiftSumExtraTime();
    		  }
    	      catch(Exception e){
    		      db.closeDB();
 			      e.printStackTrace();
 			      Log.e("ERROR_SUM_EXTRA_TIME", "RESULTS_NOT_FUND");
    	      }
    	   db.closeDB();
    	   //υπολογίζονται τα ΡΕΠΟ και EXTRA ΡΕΠΟ
    	      Log.i("SUM_EXTRA_TIME", ": "+ sumExtraTime);
    	   deserveRepo = (int)(sumRows / 7) * 2; //το πλήθος ρεπό που δικαιούτε  	  
    	   deserveΕxtra= (float)sumExtraTime / DEFAULT_TIME;    	//διαιρείται ο συνολικός εξτρα χρόνος με το πλήθος λεπτών μιας εργάσιμης ημέρας
    	      Log.i("EXTRA_REPO", ": "+deserveΕxtra);
    	   float extra_repo = (float) Math.round(deserveΕxtra * 100) / 100;
    	      Log.i("EXTRA_REPO", ": "+extra_repo);
    	   //DecimalFormat formatter = new DecimalFormat("##.##");  
    	   //String extra= formatter.format(deserveΕxtra);
    	   take_extra  = (int) sumResults - deserveRepo;  //πήρε εξτρα ότι παραπάνω πήρε πέραν αυτών που δικαιούτε
    	   take_extra  = (take_extra >0)? take_extra : 0;
    	   rest_extra  = (int) deserveΕxtra - take_extra;
    	   rest_extra  = (rest_extra < 0)? 0 : rest_extra;
    	   //ενημερώνει τα αντίστοιχα πεδία συγκεντρωτικών αποτελεσμάτων
    	   mustTakeRepo.setText(""+deserveRepo);
    	   foundRepo.setText(""+sumResults); 
    	   extraRepo.setText(""+extra_repo);
    	   takeExtra.setText(""+take_extra);
    	   restExtra.setText(""+rest_extra);
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
	
  }//end inner class fragment
	//-------------------------------------------------------------
	//---------------------------- ΒΟΗΘΕΙΑ ---------------------------
	//Εμφανίζει Dialog με Πληροφορίες Συμπλήρωσης της φόρμας  
	//για συγκεκριμένη ημερομηνία
	public void showingHelpMessage(){	
		    	  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		  		  String defaultMessage ="Για Επιστροφή Πατήστε: \'ΟΚ\'"; 
		  		  Spanned helpMes =  Html.fromHtml("<font color=\"red\"><b>"+"ΠΡΟΣΟΧΗ !!!"+"</b></font><br>"
		  		  		+ "Αυτή η λειτουργία αφορά "+"<font color=\"#FF0000\"><b><u>"+"μόνο"+"</b></u></font><br>"
		  		  		+ "τους <font color=\"#0000FF\"><b>Εργαζόμενους σε Τράπεζες</b></font> των οποίων το ωράριο "
		  		  		+ "ενίοτε μπορεί να υπερβαίνει το προκαθορισμένο (από την Ο.Τ.Ο.Ε.).<br>"
		  		  		+ "Δεδομένου ότι εργάζονται σε βάρδιες συνήθως η χρονική διάρκεια της εργασίας τους "
		  		  		+ "είναι 8 ώρες (ή και περισσότερες).<br>"
		  		  		+ "Το ωράριο του τραπεζουπαλλήλου είναι 445 λεπτά (κατά μ.ο./ημέρα).<br>"
		  		  		+ "1. Η εφαρμογή υπολλογίζει τα Έξτρα Ρεπό ως εξής:<br>"
		  		  		+ "Αρχικά ο επιπλέον εργάσιμος χρόνος που προκύπτει από οποιαδήποτε βάρδια αθροίζεται.<br>"
		  		  		+ "Στη συνέχεια επιμερίζεται σύμφωνα με το ωράριο του<br>" +
		  		  		"Τραπεζικού Υπαλλήλου.<br><br>" + defaultMessage);	  
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
 //------------------------------------------------------------------------------------
}//end class
