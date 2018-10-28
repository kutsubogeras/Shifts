package com.kutsubogeras.shifts;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kutsubogeras.shifts.data.DBControler;
import com.kutsubogeras.shifts.help.DeveloperInfo;
import com.kutsubogeras.shifts.help.ShowAppInfo;
import com.kutsubogeras.shifts.insert.EmployeeInsertion;
import com.kutsubogeras.shifts.insert.OfWorkInsertion;
import com.kutsubogeras.shifts.insert.OneShiftInsertion;
import com.kutsubogeras.shifts.insert.TwoWeekShiftInsertion;
import com.kutsubogeras.shifts.insert.WeekShiftInsertion;
import com.kutsubogeras.shifts.queries.DisplayExtraRepo;
import com.kutsubogeras.shifts.queries.DisplayIntervalOfShifts;
import com.kutsubogeras.shifts.queries.DisplayOfWorkYear;
import com.kutsubogeras.shifts.queries.DisplayShift;
import com.kutsubogeras.shifts.queries.QueryDatesManager;
import com.kutsubogeras.shifts.queries.QueryListAdapter;
import com.kutsubogeras.shifts.queries.ShowMultiDaysRepo;
import com.kutsubogeras.shifts.queries.ShowSumResults;
import com.kutsubogeras.shifts.queries.ShowWorkedHolidays;
import com.kutsubogeras.shifts.queries.YearOfWorks;
import com.kutsubogeras.shifts.settings.SettingsShifts;
import com.kutsubogeras.shifts.update.UpdateEmployee;
import com.kutsubogeras.shifts.update.UpdateOfWork;
import com.kutsubogeras.shifts.update.UpdateShift;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	public SectionsPagerAdapter mSectionsPagerAdapter;
    //The {@link ViewPager} that will host the section contents.
	private DBControler createDB;
	public  ViewPager mViewPager;	
	private Timer chechDBTimer;    // timer για εμφάνιση dialog μετά από ένα χρονικό διάστημα
	private Context myContext;
	private static final String addUserSuccessMessage = "Επιτυχής Προσθήκη Χρήστη !!!";
	private final int ADD_WORKED_OK_FLAG = 5;   //flag  για αναγνώρηση καλούσας activity(μεθόδου)
	private final int RESULT_EXIT = 1;          //flag  για αναγνώρηση καλούσας activity(μεθόδου)
	private final int PREFERENCE_LANGUAGE_FLAG = 3;  //flag  για αναγνώρηση καλούσας activity SettingsShifts
	private Timer   updateShiftsTimer;  //Timer για προβολή μηνύματος με χρον. καθυστέρηση για τα εξαιρεθέντα pois 
	private Handler taskHandler;        //Handler για προβολή μηνύματος με χρον. καθυστέρηση (εναλακτική λύση αντί Timer) 	
	private NotificationManager notificationManager; 
	private final String NOTIFI_MESSAGE = "Εισάγετε το Νέο Πρόγραμμα Βαρδιών";
	private final String NOTIFI_MESSAGE_EN = "Add the new Program";
	private String systemLanguage = "";    //η επιλεγμένη γλώσσα του λειτουργικού συστήματος(android)
	private String languagePreference =""; //η προτίμηση του χρήστη στη γλώσσα προβολής δεδομένων στις Ρυθμίσεις
	private long totalShiftRecords = 0;
	private static ArrayList<Integer> backrounds = new ArrayList<Integer>(); //περιέχει τις εικόνες για backround
	private static SharedPreferences sharedPref;
	private String preferPassword = "";
	private String userPassword = "";
	/*Notification Manager για (Υπενθύμιση) του χρήστη να εισάγει
	 *το νέο πρόγραμμα όταν βρίσκεται στις τελευταίες μέρες
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myContext = this;
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);		
		//ανάθεση τιμών στη λιστα με τις backround εικόνες της εφαρμογής
		this.setBackroundIcons();
		//έλεγχος της επιλεγμένης γλωσσας του λειτουργικού Android
		systemLanguage = Locale.getDefault().getLanguage().toString();
		//-------------- check user languge in settings -----------------
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		languagePreference = sharedPref.getString(ShiftSettings.KEY_SELECTED_LANGUGE, "");

		//password
		preferPassword = ""+sharedPref.getBoolean(ShiftSettings.KEY_SELECTED_PASSWORD, false);
		//Password= (Password=="")? "false" : "true";
		//Toast tos = Toast.makeText(myContext, Password, Toast.LENGTH_LONG);
		//tos.setGravity(Gravity.BOTTOM, tos.getXOffset() / 2, tos.getYOffset() * 5);
		//tos.show();

		//-------------Update Distance Timer-----------------
        chechDBTimer = new Timer();   //δημιουργεί ένα timer
        //καλεί ένα timer νήμα για ενημέρωση της βάσης δεδομένων μετά από 2 sec
        //αν η βάση είναι κενή εμφανίζει μήνυμα στο χρήστη        
        chechDBTimer.schedule(new TimerTask() {			
			@Override
			public void run() {
				TimerMethod();				
			}			
		}, 1500);
        //---------------Notification --------------------
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        updateShiftsTimer   = new Timer();    //Timer για προβολή μηνύματος μετά από 3 sec  
		taskHandler         = new Handler();  //handler αντί timer για προβολή μυνήματος μετά από 3 sec(ή επαναλητικά)	
		
		updateShiftsTimer.schedule(new TimerTask() {			
			@Override
			public void run() {
				updateShiftsMethod();
			}			
		}, 3000); //εμφανίζεται μετά από 3 sec

		this.checkPassword();  //check if exist password
		//deleteCredentials();

	}//end method
	
	//--------------------------- ON ACTIVITY RESULT ----------------------------------
	//όταν τερματίσει η κληθήσα activity 	    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  	 super.onActivityResult(requestCode, resultCode, data);
	  	 
	  	 String preferentGreek = "Επιλέξατε γλώσσα : Ελληνική";
	  	 String preferentEnglish = "Your selection language is : English";
	  	 String preferentLanguageMes = "";
	  	 Log.i("RESULT_CODE", ""+resultCode);
	  	 
	  	 if(resultCode == PREFERENCE_LANGUAGE_FLAG) {
	  		languagePreference = data.getStringExtra(ShiftSettings.KEY_SELECTED_LANGUGE);
	  		if(languagePreference.equals("el"))
	  			preferentLanguageMes = preferentGreek ;
	  		else if(languagePreference.equals("en"))
	  			preferentLanguageMes = preferentEnglish ;
	  		Toast tos = Toast.makeText(myContext, preferentLanguageMes, Toast.LENGTH_LONG); 
		    tos.setGravity(Gravity.BOTTOM, tos.getXOffset() / 2, tos.getYOffset() * 5);
		    tos.show();
	  	 }
	  	 if(resultCode == ADD_WORKED_OK_FLAG) {
	  		Toast tost = Toast.makeText(myContext, addUserSuccessMessage, Toast.LENGTH_LONG); 
		    tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
		    tost.show();
	  	 }
	  	 if(resultCode == RESULT_EXIT ){
	  	    this.terminateApplication();
	  	 }  		
	}//end method 
	
	//--------------------------- ON RESUME ----------------------------------
	@Override
	public void onResume(){
		super.onResume();
		//έλεγχος της επιλεγμένης γλωσσας του λειτουργικού Android
		systemLanguage = Locale.getDefault().getLanguage().toString();
		//-------------- check user languge in settings -----------------
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		languagePreference = sharedPref.getString(ShiftSettings.KEY_SELECTED_LANGUGE, "");
	}
	
	//--------------------------- ON BACK PRESSED ----------------------------------
	@Override
	public void onBackPressed() {
		 this.warningApplicationClose(); //εμφανίζει dialog με προειδοποίηση για κλείσιμο εφαρμογής
	}
	
	//-------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 // Inflate the menu; this adds items to the action bar if it is present.
		 //έλεγχος της επιλεγμένης γλωσσας του λειτουργικού Android
		 String systemLanguage = Locale.getDefault().getLanguage().toString();
		 //έλεγχος της επιλεγμένης γλώσσας από το χρήστη στο menu settings
		 SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		 String userLanguage = sharedPref.getString(ShiftSettings.KEY_SELECTED_LANGUGE, "");
		 
		 getMenuInflater().inflate(R.menu.main, menu);
		 menu.clear();
		 //αν το android είναι στα Ελληνικά ή ο χρήστης επέλεξε Ελληνική γλώσσα στις ρυθμίσεις
		 if(systemLanguage.equals("el") || userLanguage.equals("el")){
		    menu.add(0, R.id.item1_main, 0, "Βοήθεια")
             .setIcon(android.R.drawable.ic_menu_help);			
		    menu.add(0, R.id.item2_main, 1, "Ρυθμίσεις")
             .setIcon(android.R.drawable.ic_menu_help);	         
		    menu.add(0, R.id.item3_main, 2, "Πλήθος Βαρδιών")
             .setIcon(R.drawable.info_icon);
		    menu.add(0, R.id.item4_main, 3, "Αλλαγή Εργαζομένου")
             .setIcon(R.drawable.info_icon);
		    menu.add(0, R.id.item5_main, 4, "Αλλαγή Βάρδιας")
             .setIcon(R.drawable.info_icon);
		    menu.add(0, R.id.item6_main, 5, "Αλλαγή Αδείας")
             .setIcon(R.drawable.info_icon);		  
		    menu.add(0, R.id.item7_main, 6, "Σχετικά")
	         .setIcon(R.drawable.info_icon);
		 }//αν το android είναι στα Αγγλικά ή ο χρήστης επέλεξε Αγγλική γλώσσα στις ρυθμίσεις
		 else if(systemLanguage.equals("en") || userLanguage.equals("en")){
			    menu.add(0, R.id.item1_main, 0, "Help")
	             .setIcon(android.R.drawable.ic_menu_help);			
			    menu.add(0, R.id.item2_main, 1, "Settings")
	             .setIcon(android.R.drawable.ic_menu_help);	         
			    menu.add(0, R.id.item3_main, 2, "Total Records")
	             .setIcon(R.drawable.info_icon);
			    menu.add(0, R.id.item4_main, 3, "Edit of Employee")
	             .setIcon(R.drawable.info_icon);
			    menu.add(0, R.id.item5_main, 4, "Edit of Shift")
	             .setIcon(R.drawable.info_icon);
			    menu.add(0, R.id.item6_main, 5, "Edit of Leave")
	             .setIcon(R.drawable.info_icon);		  
			    menu.add(0, R.id.item7_main, 6, "About")
		         .setIcon(R.drawable.info_icon);
		 }
		return true;
	}//end method

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		     case R.id.item1_main: 
		    	 this.showingHelpMessage();
		    	 return true;		    	
		     case R.id.item2_main: 
		    	 this.showingSettings();
		    	 return true;		    	  
		     case R.id.item3_main:
		    	 this.showingTotalNumberRecords();
		         return true;
		     case R.id.item4_main:
		    	 this.updateEmployeeValues();
		         return true;  
		     case R.id.item5_main:
		    	 this.updateOneShiftsValues();
		         return true;  
		     case R.id.item6_main:
		    	 this.updateOfWorkValues();
		         return true;  
		     case R.id.item7_main:
		    	 this.showingAboutDeveloper();
		         return true;  
		     default:
		         return super.onOptionsItemSelected(item);
		}
	}//end method	 
    //============================= METHOD TIMER=========================================== 
	   /*
	    * Αυτή η μέθοδος καλείται απευθείας απ τον timer και τρέχει στο ίδιο νήμα με αυτόν
	    * Η μέθοδος αυτή καλεί την runOnUiThread που. 
	    */
	 private void TimerMethod() {		
		 this.runOnUiThread(Timer_Tick);
	 }
     //===========================THREAD TIMER UPDATE DATA BASE============================
	 //Αυτή η μέθοδος τρέχει στο ίδιο νήμα με το αρχικό UI και καλείται στη TimerMethod.    
	 private Runnable Timer_Tick = new Runnable() {
		  public void run() {
				//ελέγχει αν έχει δημιουργηθεί η ΒΔ και αν όχι την δημιουργεί		
				createDB = new DBControler(myContext);
				Log.i("CREATE_DB", "Η Βάση Δεδομένων Δημιουργήθηκε");
	  }
	 };
	 //========================= TIMER WITH HANDLER =========================================
	 //εδώ δεν καλείται αυτή η μέθοδος γιατί δεν εμφανίζει την χρονική καθυστέρηση
	 //αλλά προβάλει αμέσως το μήνυμα μετά τη δημιουργία της λίστας
     protected void setTimer( long time ) {        	
            Runnable t = new Runnable() {
       	      public void run()	{
       	    	  createNotificationForUpdateShifts(NOTIFI_MESSAGE);
       	      }
            };
            taskHandler.postAtTime( t, time );
     }//end method     
	 //====================== METHOD UPDATE SHIFTS TIMER ==================================== 
	  //Αυτή η μέθοδος καλείται απευθείας απ τον timer και τρέχει στο ίδιο νήμα με αυτόν
	  //Η μέθοδος αυτή καλεί την runOnUiThread με παράμετρο το αντικείμενο Runnable: UpdateShiftsTimer.  
	  private void updateShiftsMethod() {		
			this.runOnUiThread(UpdateShiftsTimer);		   
	  }
	  //===========================THREAD TIMER============================================
	  /*
	   * Αυτή η μέθοδος τρέχει στο ίδιο νήμα με το αρχικό UI και καλείται στη TimerMethod.
	   * Εμφανίζει Υπενθύμιση ενημέρωσης του 14ήμερου προγράμματος βαρδιών 3 ημέρες πριν
	   * εκπνεύσει το τρέχων πρόγραμμα βαρδιών. Ημέρα Παρασκευή συνήθως
	   */
	   private Runnable UpdateShiftsTimer = new Runnable() {
			public void run() {
				//εμφανίζει υπενθύμιση ενημέρωσης προγράμματος βαρδιών	
				createNotificationForUpdateShifts(NOTIFI_MESSAGE);
	            }
       };

	   // delete user credentials from db
	   private void deleteCredentials(){
		   DBControler db = new DBControler(myContext);
		   db.open();
		   db.DeleteCredentials();
		   db.closeDB();
	   }
	  //=========================CREATE NOTIFICATION=======================================
	   /*
	    * Αρχικά εμφανίζεται με χρονική καθυστέρηση Timer και εμφανίζει το πλήθος βαρδιών
	    * σε κάθε εκκίνηση της εφαρμογής.
	    * Αν πλησιάζει πλησιάζει στην εκπνοή του το 14-ήμερο του προγράμματος τότε
	    * δημιουργεί ένα Notification για ενημέρωση του νέου 14-ήμερου προγράμματος Βαρδιών.
	    * Καλείται στο πιό πάνω νήμα.
	    */
	  @SuppressLint("NewApi")
	  private void createNotificationForUpdateShifts(String message){
		
		  String totalShiftRowsMes = "Βρέθηκαν Συνολικά : ";	
		  //αν έχει επιλεγεί Αγγλική γλώσσα συστήματος ή από τις ρυθμίσεις
		  if (systemLanguage.equals("en") || languagePreference.equals("en")){
			  totalShiftRowsMes = "Found totaly : ";
			  message = "Add the new Program";
		  }
		  
		  long  currentDate = 0, twoDaysBeforeLast = 0, lastDate = 0, sumRows = 0;	
		  int   year, month, day = 0;	  
		  int   userCode = 0;		  
		  Calendar today = Calendar.getInstance();
		  year = today.get(Calendar.YEAR);
		  month= today.get(Calendar.MONTH);
		  day  = today.get(Calendar.DAY_OF_MONTH);
		  today.clear();
		  today.set(year, month, day, 0, 0);     //κρατά μόνο έτος,μήνα,ημέρα και διαγράφει ώρα,λεπτά
		  currentDate = today.getTimeInMillis(); //η τρέχουσα ημερομηνία σε millisec		  
		  DBControler db = new DBControler(this);
		  Cursor c;		  
		  db.open();
		  try{
		      userCode = db.getIdValueFromTable("Employee");
             }catch(Exception e){
	                db.closeDB();
	                e.printStackTrace();
	                Log.e("ERROR_USER_CODE", "CODE_NOT_FUND");
             }    	   
          try{
	          sumRows = db.getSumRowsFromShiftTable(userCode);   //επιστρέφει το πήθος εγγραφών του πίνακα: Shift
	          totalShiftRecords = sumRows;
	         }catch(Exception e){
    	            db.closeDB();
		            e.printStackTrace();
		            Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
	         } 
          db.closeDB();
          //εμφανίζει ένα Dialog μήνυμα με το πλήθος βαρδιών του πίνακα Shift
          this.showTotalNumberOfRecords(totalShiftRowsMes, sumRows);
    /*
     *    //δοκιμή creation Notification 
     *   this.createNotification(message); //Δημιουργεί ένα Notification
     */
          if(sumRows > 0){ //αν βρέθηκαν εγγραφές	
        	 db.open();
		     try{
		    	 c = db.getLastShiftFromTable();
		         lastDate = c.getLong(2); //επιστρέφει την τελευταία ημερομηνία βάρδιας σε millisecond
		        }catch(Exception e){
		        	  db.closeDB();
				      e.printStackTrace();
				      Log.e("ERROR_LAST_DATE", "RESULTS_NOT_FUND");		        	
		        }
		     db.closeDB();		  
		     twoDaysBeforeLast = lastDate - (48*60*60*1000); //αφαιρεί χρόνο = με 2 ημέρες πριν την τελευταία ημερομηνία
		     //Log.i("CURRENT_DATE", ": "+currentDate);
		     //Log.i("2_DATES_BEFORE_LAST", ": "+twoDaysBeforeLast);
		     if(twoDaysBeforeLast <= currentDate){
			   //αν βρισκόμαστε 2 μέρες πριν το τελευταίο πρόγραμμα βαρδιών (Ημέρα Παρασκευή)
				 // και έχει πειλέξει ο χρήστης ενεργοποίηση notification messages
			   //δημιουργία Intent και PendingIntent για κλήση της activity
		       //εισαγωγής προγράμματος 2 εβδομάδων όταν ο χρήστης αγγίξει το Notification

			   boolean noti = sharedPref.getBoolean(ShiftSettings.NOTIFY_MESSAGE, false);
			   if( noti )  //αν έχει επιλέξει να ενημερώνεται με notofication messages
		          this.createNotification(message);   //δημιουργεί και εμφανίζει notification
		     }//end if
		  }//end if
	  }//end method
	
	//========================= CREATE NOTIFICATION ============================
	@SuppressLint("NewApi")
	public void createNotification(String message){
		//content://media/internal/audio/media/22
		   String soundUri = sharedPref.getString(ShiftSettings.KEY_SELECTED_RINGTONE, null);
		   boolean hasVibrate = sharedPref.getBoolean(ShiftSettings.KEY_SELECTED_VIBRATE, false);
		   String  title = "Ενημέρωση Βαρδιών";
		   if (systemLanguage.equals("en") || languagePreference.equals("en"))				  
		       title = "Update Shifts";
		   Intent intent = new Intent(this, TwoWeekShiftInsertion.class);
	       PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
	       
	       //Δημιουργία ενός Notification που εμφανίζεται επάνω στη μπάρα
	       Notification noti = new Notification.Builder(this)
	         .setStyle(new Notification.BigTextStyle().bigText(message))
             .setContentTitle(title)	        
             .setContentText(message)
             //.setSubText("\nΠατήστε Εδώ")
             .setUsesChronometer(true)
             .setWhen(2500)
             //.setDefaults(Notification.DEFAULT_SOUND)
             .setSmallIcon(R.drawable.shift)
             .setContentIntent(pIntent)
             .addAction(R.drawable.add_32_32, "Call", pIntent)
             .build();
	       //noti.sound = Uri.parse("android.resource://com.kutsubogeras.shifts/"+R.raw.seabird_sounds); /*δουλεύει*/
	//     noti.sound = Uri.parse("android.resource://com.kutsubogeras.shifts/raw/seabird_sounds");
		   if (soundUri.equals("content://media/internal/audio/media/22"))
		       noti.sound = Uri.parse("android.resource://com.kutsubogeras" +
					   ".shifts/raw/seabird_sounds");
		   else
			   noti.sound = Uri.parse(soundUri);
	       //noti.sound = Uri.parse("android.resource://com.kutsubogeras.shifts/raw/ultimate_weapon"); /*δουλεύει*/
		if(hasVibrate){
			noti.defaults |= Notification.DEFAULT_VIBRATE; //δημιουργεί μια δόνηση παράλληλα με
		}
		// την ειδοποίηση
           // κρύβει το notification αφού επιλεγεί απ το χρήστη
           noti.flags |= Notification.FLAG_AUTO_CANCEL;
           notificationManager.notify(0, noti);
	}//end method	
	
	
	//--------------------- TOTAL RECORDS MES -----------------------------
	/*
	 * Εμφανίζει Dialog μήνυμα με το πλήθος εγγραφών ή προτρέπει για εισαγωγή στοιχείων 
	 * εργαζομένου τον χρήστη αν είναι κενή η βάση δεδομένων
	 */
	private void showTotalNumberOfRecords(String message, long rows){
		
		String title ="", defaultMes ="", shifts = "";		
		if(systemLanguage.equals("el") || languagePreference.equals("el")){
		   title = "Κενή Βάση Δεδομένων";
		   defaultMes ="Παρακαλώ Εισάγεται πρώτα τα Στοιχεία Εργαζομένου και ακολούθως τις Βάρδιες.\n" +
				"Πατήστε στην αρχική το αριστερό εικονίδειο με την εικόνα χρήστη " +
				"και συμπληρώστε τα πεδία της φόρμας.\n" +
				"Για επιστροφή Πατήστε: \'OK\'";
		   shifts = " Βάρδιες";
		}
		else if(systemLanguage.equals("en") || languagePreference.equals("en")){
		   title = "The Data base is empty !!!";
		   defaultMes ="At first you have to insert User data.\n"+
				    "To Achieve please touch the user icon on the left and "+
				    "complete the fields on the new form.\n" +
					"To return press: \'OK\'";
		   shifts = " Shifts";
		}
		if(rows > 0){ //αν υπάρχουν εγγραφές βαρδιών
		   Toast t = Toast.makeText(this, message + rows + shifts, Toast.LENGTH_SHORT);
           t.setGravity(Gravity.BOTTOM, t.getXOffset() / 2, t.getYOffset() * 2);
		   t.show();
			//showDialogWithPassword(); //ελέγχει αν έχει επιλεγεί χρήση Password για την εφαρμογή
		}
		else{
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);		 
		    // Dialog Τίτλος
		    alertDialog.setTitle(title);		 
		    // Μήνυμα του Dialog
		    alertDialog.setMessage(defaultMes);		 
		    // Θέτει εικόνα των ρυθμίσεων στο Dialog
		    alertDialog.setIcon(R.drawable.warning);	
		    alertDialog.setCancelable(false);
		    //Αν πατηθεί το ΟΚ button τερματίζει την εφαρμογή
		    alertDialog.setPositiveButton("ΟΚ", new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int choice) {            	    
				       dialog.cancel(); //τερματίζει την εφαρμογή
		          }
		    });	         
		    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
			//showDialogWithPassword(); //ελέγχει αν έχει επιλεγεί χρήση Password για την εφαρμογή
		}

	}//end method
	
	//------------------------DIALOG SHOW SUM RECORDS --------------------
	/*
	 * Καλείται στο μενού και εμφανίζει σε Dialog το πλήθος καταχωρημένων 
	 * εγγραφών βαρδιών της εφαρμογής
	 */
	private void showingTotalNumberRecords(){
		
		String title ="", defaultMes ="", infoMess ="";		     
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
		SimpleDateFormat df_en = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Calendar firstDate = Calendar.getInstance();
		firstDate.clear();		  				
		Calendar secDate = Calendar.getInstance();
		secDate.clear();	
		DBControler db = new DBControler(this);
		int userCode = 0;
		long sumRows = 0;
		long first   = 0;
		long last    = 0;		
		db.open();
		try{
		    userCode = db.getIdValueFromTable("Employee");
           }catch(Exception e){
	             db.closeDB();
	             e.printStackTrace();
	             Log.e("ERROR_USER_CODE", "CODE_NOT_FUND");
           }    	   
        try{
	        sumRows = db.getSumRowsFromShiftTable(userCode); //επιστρέφει το πήθος εγγραφών του πίνακα: Shift
	        totalShiftRecords = sumRows;
	       }catch(Exception e){
   	             db.closeDB();
		         e.printStackTrace();
		         Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
	       }
        try{
        	first = db.getFirstRecordOfShifts(); //επιστρέφει το πήθος εγγραφών του πίνακα: Shift	 
        	firstDate.setTimeInMillis(first);        	      
	       }catch(Exception e){
  	             db.closeDB();
		         e.printStackTrace();
		         Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
	       }
        try{
        	last = db.getLastRecordOfShifts(); //επιστρέφει το πήθος εγγραφών του πίνακα: Shift
        	secDate.setTimeInMillis(last);    
	       }catch(Exception e){
  	             db.closeDB();
		         e.printStackTrace();
		         Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
	       }
        db.closeDB(); 
        
        if(systemLanguage.equals("el") || languagePreference.equals("el")){
 		   title = "Πλήθος Βαρδιών";
 		   defaultMes="\nΓια να Επιστρέψετε στην Εφαρμογή Πατήστε: \'ΟΚ\'" ;
 		   infoMess = "Πλήθος Εγγραφών \t: "+sumRows+ " Βάρδιες \n"+
 					  "Πρώτη Εγγραφή \t: "+df.format(firstDate.getTime())+"\n"+ 
 					  "Τελευταία Εγγραφή \t: "+df.format(secDate.getTime()) + defaultMes;
 		}
 		else if(systemLanguage.equals("en") || languagePreference.equals("en")){
 			 title = "Total Shifts";
 			 defaultMes = "\nTo Return please press : \'ΟΚ\'";
 			 infoMess="Found totaly : "+sumRows+ " Shifts \n"+
			          "First Record : "+df_en.format(firstDate.getTime())+"\n"+ 
			          "Last  Record : "+df_en.format(secDate.getTime()) + defaultMes ;
 		}	       
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);		 
	    // Dialog Τίτλος
	    alertDialog.setTitle(title);		 
	    // Μήνυμα του Dialog
	    alertDialog.setMessage(infoMess);		 
	    // Θέτει εικόνα των ρυθμίσεων στο Dialog
	    alertDialog.setIcon(R.drawable.info_icon);	
	    alertDialog.setCancelable(false);
	    //Αν πατηθεί το ΟΚ button τερματίζει την εφαρμογή
	    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int choice) {            	    
			       dialog.cancel(); //τερματίζει την εφαρμογή
	          }
	    });	         
	    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
	}//end method
	//===============================================================================
	/**
	 *
	 * */
	private void showDialogWithPassword(){

		final DBControler db = new DBControler(this);
		Cursor c;
		String succesMes = "Success Password has Updated !!!";
		String failPasMes = "Failure Password has not Updated ???";
		final AlertDialog.Builder succesDialog = this.sowAlertDialog(succesMes);
		final AlertDialog.Builder failDialog = this.sowAlertDialog(failPasMes);
		if(preferPassword.equals("true")) {
			long sumRows =0;
			db.open();
			try {
				sumRows = db.getSumRowsFromTable("Credentials");   //επιστρέφει το πήθος εγγραφών του πίνακα
				db.closeDB();
			} catch (Exception e) {
				db.closeDB();
				e.printStackTrace();
				Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
			}
			if(sumRows == 0) {
				LayoutInflater factory = LayoutInflater.from(myContext);
				View timeEntryView = factory.inflate(R.layout.fragment_dialog_password, null);
				final TextView errorTxt = (TextView) timeEntryView.findViewById(R.id.textView_WrongPass);
				final EditText userName = (EditText) timeEntryView.findViewById(R.id.editText_User);
				final EditText password = (EditText) timeEntryView.findViewById(R.id.editText_Password);
				final AlertDialog.Builder myDialog = new AlertDialog.Builder(myContext);

				myDialog.setIcon(R.drawable.user_icon_64_64)
						.setTitle(R.string.title_pass)
						//.setMessage("Για να ορίσετε την ώρα αγγίξτε το αντίστοιχο πλαίσιο ")
						.setView(timeEntryView)
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
                                    // Ο κλωδικας μεταφέρθηκε πιό κάτω
							}
						});

				final AlertDialog dialog = myDialog.create();
				// Make sure you show the dialog first before overriding the
				// OnClickListener
				dialog.show();
				// Notice that I`m not using DialogInterface.OnClicklistener but the
				// View.OnClickListener
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if (userName.getText().toString().equals("") || password.getText().toString().equals("")) {
									if (userName.getText().toString().equals("")) {
										userName.setBackgroundResource(R.drawable.textfield_with_red_stroke);
										errorTxt.setText(R.string.mes_wrong_user);
										errorTxt.setVisibility(View.VISIBLE);
									}
									if (password.getText().toString().equals("")) {
										password.setBackgroundResource(R.drawable.textfield_with_red_stroke);
										errorTxt.setText(R.string.mes_wrong_pass);
										errorTxt.setVisibility(View.VISIBLE);
									}
								} else {
									//call db and insert user - pass
									try {
										db.open();
										final long rows= db.InsertIntoCredentialsValues(userName.getText().toString().trim(),
												password.getText().toString().trim());
										db.closeDB();
										if(rows >0){
											sowAlertDialog("Succes Insertion");
											dialog.cancel();
										}
									} catch (Exception e) {
										db.closeDB();
										e.printStackTrace();
										Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
									}
								}
							}//onClick
						});
			}
			else{
				LayoutInflater factory = LayoutInflater.from(myContext);
				View timeEntryView = factory.inflate(R.layout.fragment_dialog_confirm_password, null);
				final TextView errorTxt = (TextView) timeEntryView.findViewById(R.id.textView_WrongPass);
				final EditText password = (EditText) timeEntryView.findViewById(R.id.editText_Password);
				AlertDialog.Builder myDialog = new AlertDialog.Builder(myContext);
				myDialog.setIcon(R.drawable.user_icon_64_64)
						.setTitle(R.string.title_pass)
						//.setMessage("Για να ορίσετε την ώρα αγγίξτε το αντίστοιχο πλαίσιο ")
						.setView(timeEntryView)
				        .setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						});
				final AlertDialog dialog2 = myDialog.create();
				// Make sure you show the dialog first before overriding the
				// OnClickListener
				dialog2.show();
				// Notice that I`m not using DialogInterface.OnClicklistener but the
				// View.OnClickListener
				dialog2.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if (password.getText().toString().equals("")) {
									password.setBackgroundResource(R.drawable.textfield_with_red_stroke);
									errorTxt.setText(R.string.mes_wrong_pass);
									errorTxt.setVisibility(View.VISIBLE);
								} else {
									//call db and insert user - pass
									db.open();
									try {
										final String name = db.getUserName();
										final Boolean res = db.updateCredentialsValues(password.getText().toString(), name);
										db.closeDB();
										if (res == true) {
											succesDialog.show();
											dialog2.cancel();
										} else {
											failDialog.show();
										}
									} catch (Exception e) {
										db.closeDB();
										e.printStackTrace();
										Log.e("ERROR_RESULTS", "RESULTS_NOT_FUND");
										failDialog.show();
									}
								}
							}
						});
				}
			}

	}//end merthod

    private void checkPassword(){
		preferPassword = ""+ sharedPref.getBoolean(ShiftSettings.KEY_SELECTED_PASSWORD, false);
		userPassword = sharedPref.getString(ShiftSettings.KEY_PASSWORD, null);
		if(preferPassword.equals("true") && !userPassword.equals(""))
		{
			LayoutInflater factory = LayoutInflater.from(myContext);
			View timeEntryView = factory.inflate(R.layout.fragment_dialog_confirm_password, null);
			final EditText password = (EditText) timeEntryView.findViewById(R.id.TextView_Password);
			final AlertDialog.Builder myDialog = new AlertDialog.Builder(myContext);
			final Context con = myContext;
			myDialog.setIcon(R.drawable.user_icon_64_64)
					.setTitle(R.string.title_pass)
					//.setMessage("Για να ορίσετε την ώρα αγγίξτε το αντίστοιχο πλαίσιο ")
					.setView(timeEntryView)
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Ο κλωδικας μεταφέρθηκε πιό κάτω
						}
					});

			final AlertDialog dialog = myDialog.create();
			// Make sure you show the dialog first before overriding the
			// OnClickListener
			dialog.show();
			// Notice that I`m not using DialogInterface.OnClicklistener but the
			// View.OnClickListener
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (password.getText().toString().equals("")) {
								Toast.makeText(con, "Το Password είναι Κενό",
										Toast.LENGTH_SHORT).show();
							} else if(password.getText().toString().equals(userPassword) ){
									dialog.cancel();
							}
							else{
								Toast.makeText(con, "Το Password είναι Λανθασμένο !?!",
											Toast.LENGTH_SHORT).show();
							}
			            }//onClick
					});
		}
	}

    //============================================================================

	private AlertDialog.Builder sowAlertDialog(String mess){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(myContext);
		// Dialog Τίτλος
		alertDialog.setTitle("Alert");
		// Μήνυμα του Dialog
		alertDialog.setMessage(mess);
		// Θέτει εικόνα των ρυθμίσεων στο Dialog
		alertDialog.setIcon(R.drawable.info_icon);
		alertDialog.setCancelable(false);
		//Αν πατηθεί το ΟΚ button τερματίζει την εφαρμογή
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int choice) {
				dialog.cancel(); //τερματίζει την εφαρμογή
			}
		});
		alertDialog.create(); // Εμφάνιση του  Alert dialog Message
        return alertDialog;
	}
	//==========================WARNING CLOSE=====================================
	//εμφανίζει προειδοποίηση κλεισίματος εφαρμογής
	private void warningApplicationClose(){
			 
			String infoMess = "Θέλετε να Τερματίσετε \nτην Εφαρμογή ?";	
			String title = "Κλείσιμο Εφαρμογής";
			String buttonCancel = "Ακύρωση";
			//αν επέλεξε γλώσσα Αγγλική ο χρήστης 
			if(systemLanguage.equals("en") || languagePreference.equals("en")){
				infoMess = "Do you want to close \nthe Application ?";	
				title = "Terminate of Application";
				buttonCancel= "Cancel";
			}				
		    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);		 
		    // Dialog Τίτλος
		    alertDialog.setTitle(title);		 
		    // Μήνυμα του Dialog
		    alertDialog.setMessage(infoMess);		 
		    // Θέτει εικόνα των ρυθμίσεων στο Dialog
		    alertDialog.setIcon(R.drawable.warning);	
		    alertDialog.setCancelable(false);
		    //Αν πατηθεί το ΟΚ button τερματίζει την εφαρμογή
		    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int choice) {            	    
				       finish(); //τερματίζει την εφαρμογή
		          }
		    });		 
		    // Αν πατηθεί το Cancel button δεν κάνει κάτι απλώς κλείνει το dialog
		    alertDialog.setNegativeButton(buttonCancel, new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int which) {
		               dialog.cancel(); //κλείνει το dialog         
		          }
		    });		 		       
		    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
	} //end method
	
	//----------------------- SET BACKROUND ICONS VALUE --------
	private void setBackroundIcons(){
		backrounds.add(R.drawable.wallpaper_galaxy);
		backrounds.add(R.drawable.wallparer_blue_720_1280);
		backrounds.add(R.drawable.wallpaper_light_blue_720_1280);
		backrounds.add(R.drawable.wallpaper_blue_nice);
		backrounds.add(R.drawable.wallpaper_blue_nice_2);
		backrounds.add(R.drawable.wallpaper_moon);
		backrounds.add(R.drawable.wallpaper_natureu_sun);
		backrounds.add(R.drawable.wallpaper_natureu_2);
		backrounds.add(R.drawable.wallpaper_natureu_3);
		backrounds.add(R.drawable.wallpaper_natureu_4);
		backrounds.add(R.drawable.wallpaper_rain);
		backrounds.add(R.drawable.wallpaper_rain_2);
		backrounds.add(R.drawable.wallpaper_black_720_1280);		
	}
	
	//-------------------------------BACK PREFERENCE------------------------------
	private static void checkPreferentBackround(View v){
		   //έλεγχος προτίμησης και ανάθεσης του επιλεγμένου του backround
		   String backPrefer = sharedPref.getString(ShiftSettings.KEY_SELECTED_BACKGROUND, "");
		   if(!backPrefer.equals("") || backPrefer.equals("180")){
			   int index = Integer.parseInt(backPrefer);
		       Log.i("BACKROUND", ""+backPrefer);
			   if (index==180)
				   index=0;
		       v.setBackgroundResource(MainActivity.backrounds.get(index));
		   }
	}
	//---------------------------------- ΜΕΘΟΔΟΙ ΚΛΑΣΗΣ -------------------------------
	//-------------------------- APPL INFO --------------------
	private void showingHelpMessage(){
		 Intent helpInt = new Intent(MainActivity.this , ShowAppInfo.class);	       		    
	     startActivityForResult(helpInt, RESULT_OK);
	}//end method
	//-------------------------- APPL SETTINGS --------------------
	private void showingSettings(){
//		 Intent setInt = new Intent(MainActivity.this , SettingsShifts.class);
//	     startActivityForResult(setInt, RESULT_OK);
		Intent setInt = new Intent(MainActivity.this , ShiftSettings.class);
		startActivityForResult(setInt, RESULT_OK);
	}//end method
	//-------------------------- UPDATE EMPLOYEE --------------------
	private void updateEmployeeValues(){
		 Intent update = new Intent(MainActivity.this , UpdateEmployee.class);	       		    
		 startActivityForResult(update, RESULT_OK);
	}//end method
	//-------------------------- UPDATE SHIFT --------------------
	private void updateOneShiftsValues(){
		 Intent update = new Intent(MainActivity.this , UpdateShift.class);	       		    
		 startActivityForResult(update, RESULT_OK);
	}//end method
	//-------------------------- UPDATE OFWORK --------------------
	private void updateOfWorkValues(){
		 Intent update = new Intent(MainActivity.this , UpdateOfWork.class);	       		    
		 startActivityForResult(update, RESULT_OK);
	}//end method
	//-------------------------- APPL DEVELOPER INFO --------------------
	private void showingAboutDeveloper(){
		 Intent helpInt = new Intent(MainActivity.this , DeveloperInfo.class);	       		    
		 startActivityForResult(helpInt, RESULT_OK);
	}//end method
	//========================= TERMINATE APPL ===============================
	 //τερματίζει την εφαρμογή
	private void terminateApplication(){			 
			super.setResult(RESULT_EXIT); 
			this.finish();
	}//end method
    //=========================== INNER CLASS  Section Page Adapter ============================	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.			
			Fragment fragment = null;
			Bundle args = new Bundle();
			
			switch (position) {
			case 0:  //SHIFTS				
				fragment = new InitialPageFragment();  //δημιουργεί ένα InitialPageFragment
				args.putInt(InitialPageFragment.ARG_SECTION_NUMBER, position + 1);				
				fragment.setArguments(args);
				return fragment;
			case 1:  //SHOW EMPLOYEE				
				fragment = new showEmployeeFragment();  //δημιουργεί ένα InitialPageFragmentFragment
				args.putInt(showEmployeeFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment;
			case 2:  //SHOW SHIFT				
				fragment = new showShiftsFragment();     //δημιουργεί ένα InitialPageFragment
				args.putInt(showShiftsFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment;
			case 3:  //ADD PAID LEAVE				
				fragment = new showOffWorksFragment(); //δημιουργεί ένα InitialPageFragment
				args.putInt(showOffWorksFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment; 
			case 4:  //QUERRIES				
				fragment = new showQueriesFragment(); //δημιουργεί ένα InitialPageFragment
				args.putInt(showQueriesFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment; 
			default: 
				return	fragment; //fragment is null
		   }//end switch				 
		}//end getItem
		
		@Override
		public int getCount() {
			// Show 4 total pages.
			return 5;
		}
		@Override
		public CharSequence getPageTitle(int position) {
			//Θέτει τον τίτλο σε κάθε Fragment
			Locale loc = Locale.getDefault();
			switch (position) { //switch:διακόπτης
			    case 0:
				   return getString(R.string.title_section1).toUpperCase(loc); //SHIFTS
			    case 1:
				   return getString(R.string.title_section2).toUpperCase(loc); //ADD EMPLOYEE
			    case 2:
				   return getString(R.string.title_section3).toUpperCase(loc); //ADD SHIFT
			    case 3:
				   return getString(R.string.title_section4).toUpperCase(loc); //ADD PAID LEAVE	
			    case 4:
				   return getString(R.string.title_section5).toUpperCase(loc); //ADD PAID LEAVE			   
		    }//end switch
			return null; 
		 }//end method
		
	  }//end PagerAdapter
      //========================= INNER CLASS INITIAL FRAGMENT ==========================
	  /**   
	   * A dummy fragment representing a section of the app, but that simply
	   * displays dummy text.
	   */
	   public static class InitialPageFragment extends Fragment {
		  /**
		   * The fragment argument representing the section number for this
		   * fragment.
		   */
		   public  static final String ARG_SECTION_NUMBER = "section_number";          
           private View myRootView;
           private LayoutInflater myInflater;
           private ViewGroup myContainer;
           private static ImageButton emploButton;
           private static ImageButton shiftButton;
           private static ImageButton ofWorkButton;
           private String languagePreference = "";
           private String systemLanguage = "";
           
           //constractor
		   public InitialPageFragment() {
		   }
          
		   @Override
		   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			      myInflater  = inflater;
			      myContainer = container;
			      myRootView  = myInflater.inflate(R.layout.fragment_initial_page, myContainer, false);
			      TextView dummyTextView = (TextView) myRootView.findViewById(R.id.section_label2);
			      dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			      emploButton = (ImageButton) myRootView.findViewById(R.id.ImageButtonInitialUser);
			      shiftButton = (ImageButton) myRootView.findViewById(R.id.imageButtonInitialShift);
			      ofWorkButton= (ImageButton) myRootView.findViewById(R.id.imageButtonInitialPaidLeave);
			      
			      //έλεγχος backround
			      MainActivity.checkPreferentBackround(myRootView);
			      //έλεγχος της επιλεγμένης γλωσσας του λειτουργικού Android
				  systemLanguage = Locale.getDefault().getLanguage().toString();
				  //έλεγχος της επιλογής γλώσσας του χρήστη από το μενού ρυθμίσεις
			      //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
				  languagePreference = MainActivity.sharedPref.getString(ShiftSettings.KEY_SELECTED_LANGUGE, "");
				    Log.i("SELECTED_LANGUAGE", ""+languagePreference);
				    
			      if(systemLanguage.equals("el") || languagePreference.equals("el")){ //αν ο χρήστης επέλεξε Ελληνική γλώσσα
			    	  shiftButton.setImageResource(R.drawable.shift_button_background);
			    	  ofWorkButton.setImageResource(R.drawable.paid_leave_button_background);
			      }
			      else if (systemLanguage.equals("en") || languagePreference.equals("en")){ // αν επέλεξε Αγγλική γλώσσα
			    	  shiftButton.setImageResource(R.drawable.shift_button_background_en);	
			    	  ofWorkButton.setImageResource(R.drawable.paid_leave_button_background_en);
			      }
			      //create Listeners
				  emploButton.setOnClickListener(new View.OnClickListener() {       		
				       public void onClick(View v) {       		     
				       		 //δημιουργεί ένα Intent για την εισαγωγή εργαζόμενου
				       		 Intent worked = new Intent(getActivity() , EmployeeInsertion.class);
				       		 startActivityForResult(worked, getActivity().RESULT_OK);
				       }
				  });				  
				  shiftButton.setOnClickListener(new View.OnClickListener() {       		
				       public void onClick(View v) {       		     
				       	   //Εμφανίζει ένα Dialog για επιλογή του πλήθους βαρδιών που θα εισαχθούν
				    	   displayShiftInsertionChoicesDialog();
					   }
				  });				  
				  ofWorkButton.setOnClickListener(new View.OnClickListener() {       		
			       	   public void onClick(View v) {       		     
			       		     //δημιουργεί ένα Intent για την εισαγωγή αδειών του εργαζόμενου
			       		     Intent ofWorkIntent = new Intent(getActivity() , OfWorkInsertion.class);
			       		     startActivityForResult(ofWorkIntent, getActivity().RESULT_OK);
					   }
			      });
			      return myRootView;
		   }//end onCreateView
		   /*
		   //-------------------------------BACK PREFERENCE------------------------------
		   private void checkPreferentBackround(){
			   //έλεγχος προτίμησης και ανάθεσης του επιλεγμένου του backround
			   SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			   String backPrefer = sharedPref.getString(SettingsShifts.KEY_SELECTED_BACKROUND, "");
			   if(!backPrefer.equals("")){
				   int index = Integer.parseInt(backPrefer);
			       Log.i("BACKROUND", ""+backPrefer);
			       myRootView.setBackgroundResource(MainActivity.backrounds.get(index));
			   }
		   }
		   */		   
		   //---------------------------------- ΜΕΘΟΔΟΙ ΚΛΑΣΗΣ -------------------------------
		   /*
		    * Εμφανίζει Dialog με τις διαθέσιμες επιλογές Εισαγωγής Βαρδιών(1 ή 7 ημέρες)
		    */
		   private void displayShiftInsertionChoicesDialog(){
			   
		    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		    	        String[] userChoices = {"Βάρδια για 1 Ημέρα",
		  						                "Βάρδιες για 7 Ημέρες",
		  						                "Βάρδιες για 14 Ημέρες"};
		    	        String[] userChoices_en = {"Shift for 1 Day",
					                               "Shifts for 7 Days",
					                               "Shifts for 14 Days"};
		    	        final String tostMessage = "Δεν Επιλέξατε Τρόπο Εισαγωγής";
		    	        final String tostMessage_en = "You didn't select eny";
		    	        final String message = (systemLanguage.equals("el") || languagePreference.equals("el"))?
		    	        		                tostMessage : tostMessage_en ;
		    	        // Θέτει εικόνα των ρυθμίσεων στο Dialog
		  			    alertDialog.setIcon(R.drawable.info_icon);	
		  			    	 
		  			    // Εμφάνιση Επιλογών του Dialog	στο χρήστη
		  			    if(systemLanguage.equals("el") || languagePreference.equals("el")){ //αν ο χρήστης επέλεξε Ελληνική γλώσσα
		  			       // Dialog Τίτλος
		  			       alertDialog.setTitle("Επιλογή Εισαγωγής Βάρδιας");
		  			       alertDialog.setSingleChoiceItems(userChoices, -1, null);			  			       
		  			    }
					    else if (systemLanguage.equals("en") || languagePreference.equals("en")){ // αν επέλεξε Αγγλική γλώσσα
					       alertDialog.setTitle("Select Quantity");
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
		  		                	 Toast tost = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT); 
		  		             	     tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 4);
		  		             	     tost.show();
		  		                   }
		  		                   if(selectedPosition == 0){  //αν επιλέξει βάρδια για 1 ημέρα
		  		                	 dialog.dismiss();
		  					         Intent oneShiftIntent = new Intent(getActivity() , OneShiftInsertion.class);
					       		     startActivityForResult(oneShiftIntent, getActivity().RESULT_OK);
		  		                   }
		  		                   if(selectedPosition == 1){ //αν επιλέξει βάρδιες για 7 ημέρες
		  		                	 dialog.dismiss();
		  					         Intent weekshiftIntent = new Intent(getActivity() , WeekShiftInsertion.class);
					       		     startActivityForResult(weekshiftIntent, getActivity().RESULT_OK);
		  		                   }
		  		                   if(selectedPosition == 2){ //αν επιλέξει βάρδιες για 7 ημέρες
		  		                	 dialog.dismiss();
		  					         Intent twoWeeksIntent = new Intent(getActivity() , TwoWeekShiftInsertion.class);
					       		     startActivityForResult(twoWeeksIntent, getActivity().RESULT_OK);
		  		                   }
		  			          }
		  			    });					   		       
		  			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message		  			   
		  	} //end method
		    //----------------------------------------------------------------------
	   }//end InitialFragment class  	
	
	   //=============================== INNER CLASS ADD EMPLOYEE fragment ======================================
		/**
		 * Αυτή η κλάση δημιουργεί και εμφανίζει τη φόρμα με τις πληροφορίες που σχετίζονται
		 * με τον χρήστη της συσκευής (Current User) αφού τις διαβάσει από τη Βάση Δεδομένων.
		 */
		public  static class showEmployeeFragment extends Fragment {
			/**
			 * The fragment argument representing the section number for this
			 * fragment.
			 */
			public static final String ARG_SECTION_NUMBER = "section_number";			
			private static ImageButton userImage;
			private static ImageView rotateImage;
			private DBControler shiftsDB;  // μεταβλητή για χειρισμό της Βάσης Δεδομένων
			private static EditText textName;
			private static EditText textLastName;
			private static EditText textAddress;
			private static EditText textPhone;
			private static EditText textMobile;
			private static EditText textJob;
			private static EditText textCompany;
			private static EditText textDepartment;
			private static EditText textGrade;		
			private static final int FILE_SELECT_CODE = 12;
			private String path = null; //η διαδρομή αρχείου φωτογραφίας
			private Uri ph_uri = null;
			private Matrix matrix; //για περιστροφή της εικόνας από το χρήστη
			private float rotateDegrees = 0f;
			private Bitmap bitmap = null;
			private Context myContext;
			
			//constractor
			public showEmployeeFragment() {
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
				View rootView = inflater.inflate(R.layout.fragment_show_employee, container, false);
				TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label2);
				dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
				//αρχικοποίηση editText της φόρμας
				myContext     = getActivity().getApplicationContext();
				textName      = (EditText)rootView.findViewById(R.id.editTextName);
				textLastName  = (EditText)rootView.findViewById(R.id.editTextLastName_show);
				textAddress   = (EditText)rootView.findViewById(R.id.editTextAddress_show);
				textPhone     = (EditText)rootView.findViewById(R.id.editTextPhone_show);
				textMobile    = (EditText)rootView.findViewById(R.id.editTextMobile_show);
				textJob       = (EditText)rootView.findViewById(R.id.editTextJob_show);
				textCompany   = (EditText)rootView.findViewById(R.id.editTextCompany_show);
				textDepartment= (EditText)rootView.findViewById(R.id.editTextDepart_show);
				textGrade     = (EditText)rootView.findViewById(R.id.editTextGrade_show);
				rotateImage   = (ImageView) rootView.findViewById(R.id.Image_Rotate);
				
				//έλεγχος backround
			    MainActivity.checkPreferentBackround(rootView);
				this.getEmployeeValues();  //θέτει τις τιμές του πίνακα του χρήστη στα editText
				//αρχικοποίηση Button
				userImage = (ImageButton) rootView.findViewById(R.id.imageButton6);				
				userImage.setOnClickListener(new View.OnClickListener() {       		
			       	   public void onClick(View v) {       		     
			       		    //δημιουργεί ένα Intent με FileCooser
			       		    showFileChooser();			       		          		   
					   }
				});		
				//rotate image listener
				rotateImage.setOnClickListener(new View.OnClickListener() {       		
			       	   public void onClick(View v) {
			       		   //rotateImage.setBackgroundResource(R.drawable.rotate1_pressed);			       		   
			       		   rotateDegrees -=90; //αφαιρεί 90 μοίρες έτσι περιστρέφεται προς τα αριστερά
			       		   rotateUserImage(rotateDegrees); //περιστρέφει την εικόνα
					   }
				});		
				return rootView;
			}//end method
			//--------------------------- ON ACTIVITY RESULT -------------------------------
		    //όταν τερματίσει η κληθήσα activity 	    
		    @Override
		    public void onActivityResult(int requestCode, int resultCode, Intent data) {
					super.onActivityResult(requestCode, resultCode, data);			
					//Log.i("RESULT_CODE", ": "+resultCode);
			        if ((requestCode == FILE_SELECT_CODE) && (resultCode == -1) ) {
			            // Get the Uri of the selected file 
			        	ph_uri = data.getData();
			            Log.i("ΕΠΙΛΕΓΜΕΝΟ ΑΡΧΕΙΟ", "File Uri: " + ph_uri.toString());
			            // Get the path
			            path =  getAbsolutePath(ph_uri);
			            Log.i("ΔΙΑΔΡΟΜΗ", "File Path: " + path);
			            //-------- KLHSH ASYNCHRONIS CLASS -------------
					    new FetchImageTask().execute(path);
						//set in DB
						this.setUserImage();
					    //------------------------------------------------
			        }	        
		    }//end method 		    
		    //--------------------------ROTATE USER IMAGE-------------------------
		    /*
		     * Περιστρέφει την εικόνα του χρήστη
		     */
		    private void rotateUserImage(float degrees){
		    	matrix = new Matrix();
		    	matrix.reset();		    	
		    	matrix.postRotate(degrees);
		    	Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, 
						bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				userImage.setImageBitmap(resizedBitmap);				
		    }//end method
			//-----------------------------------------------------------------
			//παίρνει τις τιμές που σχετίζονται με τον χρήστη απ τη ΒΔ 
			private void getEmployeeValues(){
				Cursor cursor = null;
				long sumRows = 0;
				int  isUser  = 1;  //autoincriement value = 1 για τον user worked
				shiftsDB = new DBControler(myContext);
				shiftsDB.open();				
				sumRows = shiftsDB.getSumRowsFromTable("Employee");
				if(sumRows > 0){ //αν βρέθηκαν εγγραφές εργαζομέμων
				    try{	//παίρνει τις τιμές από τον πίνακα Εργαζόμενος				
				        cursor = shiftsDB.getAllValuesFromTable("Employee", isUser);				    
				       }catch (Exception e){
					      shiftsDB.closeDB(); 
			              e.printStackTrace();
			              Log.e("SQL_ERROR", "ΔΕΝ ΒΡΕΘΗΚΕ ΕΡΓΑΖΟΜΕΝΟΣ");
			           }
				    if(cursor.moveToFirst()){ //μετακινεί τον κερσορα στην 1η θέση
				       do{
					      this.setEmployeeValuesInEditText(cursor); //παίρνει τις τιμές του εργαζόμενου απο τον cursor				   
					     }while(cursor.moveToNext());					
				     }//end if moveToFirst
				    shiftsDB.closeDB();
				    //-------- Κλήση  Asynchronus Class-------------
				    new FetchImageTask().execute(path);

				}//end if
				else{
					shiftsDB.closeDB();
					Toast.makeText(myContext, "Δεν έχετε Καταχωρήσει Στοιχεία Εργαζομένου",
							Toast.LENGTH_SHORT).show();
				}					
			}//end method
			//-----------------------------------------------------------------
			//θέτει τις τιμές του χρήστη στα editTexts 
			private void setEmployeeValuesInEditText(Cursor c){
				if(c.moveToFirst()){
				     //int id = c.getInt(0);
				     //int curUserId = c.getInt(1);
				     textName.setText(""+c.getString(2));     
				     textLastName.setText(""+c.getString(3));  
				     textAddress.setText(""+c.getString(4));  
				     textPhone.setText(""+c.getString(5));  
				     textMobile.setText(""+c.getString(6));  
				     textJob.setText(""+c.getString(7));  
				     textCompany.setText(""+c.getString(8));  
				     textDepartment.setText(""+c.getString(9));  
				     textGrade.setText(""+c.getString(10)); 
				     path = c.getString(11);  //η διαδρομή αρχείου φωτογραφίας
				}
			}//end method
			//--------------------------FILE CHOOSER ---------------------------
			   /*
			    * Δημιουργία ενός File Chooser
			    * */
			   private void showFileChooser() {				   
			       Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
			       intent.setType("image/*"); //όλες οι κατηγορίες
			       intent.addCategory(Intent.CATEGORY_OPENABLE);	       
			       String fileChooserTitle = "Επιλογή Φωτογραφίας για Φόρτωση";
			       try {
			           this.startActivityForResult(Intent.createChooser(intent, fileChooserTitle),
			        		                       FILE_SELECT_CODE);
			       } catch (android.content.ActivityNotFoundException ex) {			          
			           Toast.makeText(myContext, "Παρακαλώ εγκαταστήστε ένα \'File Manager\'.",
			        		   Toast.LENGTH_SHORT).show();
			       }
			   }//end method
			   //--------------------GET ABSOLUTE PATH OF IMAGE ----------------------------
				private String getAbsolutePath(Uri uri) {
			        String[] projection = { MediaColumns.DATA };
			        String fileName = "Άγνωστη";
			         //η managedQuery(contentUri, proj, null, null, null); είναι : deprecated
			        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null); 
			        if (cursor != null) {
			            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			            cursor.moveToFirst();
			            Log.i("URI_PATH", ""+cursor.getString(column_index));
						Uri myUri = Uri.parse(cursor.getString(column_index));
			            fileName = myUri.getLastPathSegment().toString();
			            Log.i("PHOTO_NAME", ""+fileName);
			            return cursor.getString(column_index);		            
			        } else
			            return null;
			    }//end method
				//===========================DIALOG MESSAGE =================================
				/*
				 * Δημιουργεί ένα dialog που βλοβάλει μήνυμα στο χρήστη
				 */
				public void createDialogWithMessage(){
					String defaultMes ="Παρακαλώ Επιλέξτε μια Φωτογραφία Μικρότερου Μεγέθους (σε pixels).\n" +
							"Χρησιμοποιείστε την Εφαρμογή Επεξεργασία Φωτογραφίας " +
							"και με την εντολή: \'Crop\' περιορίστε το μέγεθος της φωτογραφίας " +
							"γύρω από το πρόσωπο του εικονιζομένου. " +
							"Επιλέξτε διαστάσεις: \n" +
							"Πλάτος = 70\n" +
							"Ύψος   = 80\n" +
							"Μέγιστο Μέγεθος Εικόνας: 2.5 MByte\n"+
							"Στη συνέχεια δοκιμάστε να τη φορτώσετε ξανά.";
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(myContext);		 
				    // Dialog Τίτλος
				    alertDialog.setTitle("Σφάλμα Φόρτωσης");		 
				    // Μήνυμα του Dialog
				    alertDialog.setMessage(defaultMes);		 
				    // Θέτει εικόνα των ρυθμίσεων στο Dialog
				    alertDialog.setIcon(R.drawable.warning);	
				    alertDialog.setCancelable(false);
				    //Αν πατηθεί το ΟΚ button τερματίζει την εφαρμογή
				    alertDialog.setPositiveButton("ΟΚ", new DialogInterface.OnClickListener() {
				          public void onClick(DialogInterface dialog, int choice) {            	    
						       dialog.cancel(); //τερματίζει την εφαρμογή
				          }
				    });	         
				    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
				}//end method

                // update Photo
				private void setUserImage(){
					shiftsDB = new DBControler(myContext);
					shiftsDB.open();
					boolean hasUpdatePhoto =false;
					try{
						hasUpdatePhoto= shiftsDB.updateUserPhoto(path, 1);
						shiftsDB.closeDB();
						if (hasUpdatePhoto)
							Toast.makeText(myContext, R.string.message_update_photo,
									Toast.LENGTH_SHORT).show();
					}catch (Exception e){
						shiftsDB.closeDB();
						e.printStackTrace();
						Log.e("SQL_ERROR", "ΔΕΝ ΒΡΕΘΗΚΕ ΦΩΤΟΓΡΑΦΙΑ");
					}
				}
			 //=========================Asychronous Inner Class ===================================
				//κατεβάζει τη Bitmap φωτογραφία απο το internet
				 private class FetchImageTask extends AsyncTask<String, Integer, Bitmap> {
					    
					 @Override
					 protected Bitmap doInBackground(String... arg0) {
					    	
					        final float DEGREE = -90f;
						    String imaPath = arg0[0];
					    	bitmap = null;
					    	Bitmap resizedBitmap = null ;
					    	matrix = new Matrix();
					    	matrix.reset();	
					    	int bytesOfBitmap = 0; //το πλήθος bytew της bitmap εικόνας
					    	final int MAX_BYTES_OF_BITMAP = 2500000; //2.5 MByte
					    	try {
					    		 /* 
					    		 BitmapFactory.Options options = new BitmapFactory.Options();
								 options.inPurgeable = true;
								 options.inSampleSize= 3;								
								 bitmap = BitmapFactory.decodeFile(path , options);	
								*/
								 bitmap = decodeSampledBitmapFromResource(imaPath, 100, 120);
								//διαδρομή φωτο, πλάτος , ύψος φώτο
								 
								 bytesOfBitmap = bitmap.getByteCount();
								 Log.i("BYTES_OF_BITMAP", " = "+bytesOfBitmap);							 
								   
								 //if(bytesOfBitmap <= MAX_BYTES_OF_BITMAP){
								 int bmpWidth  = bitmap.getWidth();  //το πλάτος της εικόνας
							     int bmpHeight = bitmap.getHeight(); //το ύψος της εικόνας
							     Log.i("SIZES_OF_BITMAP", "ΠΛΑΤΟΣ= "+bmpWidth+" ΥΨΟΣ= "+bmpHeight+" PIXELS= "+(bmpWidth * bmpHeight));
							     
							     if(bmpWidth > bmpHeight){
							    	 int degrees = (int)rotateDegrees % (-360);
							    	 Log.i("ROTATE_DEGREES :"+rotateDegrees, "DEGREES: "+degrees);
							    	 switch(degrees){
							    	    case -90:
							    	    	break;  
							    	    case   0:
							    	        matrix.postRotate(DEGREE);
							    	        break;							    	    
							    	    case -180:
							    	    	matrix.postRotate(DEGREE);
							    	        break; 
							    	    case -270:
							    	    	matrix.postRotate(DEGREE);
							    	        break; 							    	     
							    	  }//end switch
							        }//end if							        
							        resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, 
							                            bmpWidth, bmpHeight, matrix, true);
								  //}//end if
							}catch(java.lang.OutOfMemoryError e){
								   e.printStackTrace();
								   Log.e("ERROR_OUT_OF_MEMORY: ", "" + imaPath);
							}catch (Exception e) {
					               e.printStackTrace();
					               Log.e("ERROR_LOAD_IMAGE_FROM:", "" + imaPath);
				            }
					        return resizedBitmap;
					 }//end method

				     @Override
					 protected void onPostExecute(Bitmap image) {
					     if (image != null)  //αν βρέθηκε ο πόρος
						    userImage.setImageBitmap(image);
				     }//end onPostExecute
				 //===================================================================================
				 public Bitmap decodeSampledBitmapFromResource(String pathFile, int reqWidth, int reqHeight) {

					    // First decode with inJustDecodeBounds=true to check dimensions
					    final BitmapFactory.Options options = new BitmapFactory.Options();
					    options.inJustDecodeBounds = true;
					    BitmapFactory.decodeFile(pathFile , options);

					    // Calculate inSampleSize
					    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

					    // Decode bitmap with inSampleSize set
					    options.inJustDecodeBounds = false;
					    return BitmapFactory.decodeFile(pathFile , options);
				 }//end method
				 //================================================================
				 /*
				  * Καλείται στην πιο πάνω μέθοδο
				  */
				 public  int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
				        // Raw height and width of image
				        final int height = options.outHeight;
				        final int width  = options.outWidth;
				        int inSampleSize = 1;

				        if (height > reqHeight || width > reqWidth) {
				           final int halfHeight = height / 2;
				           final int halfWidth  = width / 2;
				           // Calculate the largest inSampleSize value that is a power of 2 and keeps both
				           // height and width larger than the requested height and width.
				           while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				                 inSampleSize *= 2;
				           }//end while
				         }//end if

				         return inSampleSize;
				 }//end method
				//===================================================================== 
			}//end inner asyn class
				 
		}//end inner fragment class
		
	    //=========================== INNER CLASS ADD SHIFT fragment =============================
		/**
		 * Η κλάση αυτή είναι εσωτερική και δημιουργεί ένα fragment για την προβολή 
		 * σε λίστα των 14 τελευταίων πιο πρόσφατων Βαρδιών του χρήστη που είναι
		 * καταχωρημένες στη ΒΔ
		 */
		public static class showShiftsFragment extends Fragment {
			/**
			 * The fragment argument representing the section number for this
			 * fragment.
			 */
			public static final String ARG_SECTION_NUMBER = "section_number";
			private DBControler shiftsDB;  // μεταβλητή για χειρισμό της Βάσης Δεδομένων
			private ArrayList<ShiftOfUser> listOfShifts;
			private ListView lv;
			private Context myContext;
	        //constractor
			public showShiftsFragment() {				
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
				View rootView = inflater.inflate(R.layout.fragment_display_last_shifts, container, false);
				TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label3);
				dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
				
				//έλεγχος backround
			    MainActivity.checkPreferentBackround(rootView);
				myContext = getActivity().getApplicationContext();
				listOfShifts = new ArrayList<ShiftOfUser>(); //ArrayList με αντικείμενα τύπου ShiftOfUser
				//----------------------LIST VIEW------------------------
				//Δημιουργία αντικειμένου τύπου DBControler class μεθόδου ανάγνωσης των βαρδιών
				//αν υπάρχουν βάρδιες τότε θα εμφανίζει τις  14 τελευταίες
				//αλλιώς θα εμφανίζει άλλο Fragment με κατάλληλο μήνυμα
				this.readLastShiftsFromDB();  //διαβάζει τις τελευταίες 14 βάρδιες από τη ΒΔ				
				
				//δημιουργία ListView και adapter για χειρισμό της λίστας				
				lv = (ListView)rootView.findViewById(R.id.myListView);		
				ShiftListAdapter adapter = new ShiftListAdapter(myContext, 
						                        R.layout.activity_item_of_list_shifts , listOfShifts );
				lv.setAdapter(adapter); 
		        lv.setTextFilterEnabled(true);
		        lv.setSoundEffectsEnabled(true);
		        lv.setClickable(true);
		        // Listener 
		        lv.setOnItemClickListener(new OnItemClickListener (){
		        	@Override
		            public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {        		
		        		String infoMess = "Επιλέξατε τη Βάρδια : "+(pos+1);                
		        		Toast tost = Toast.makeText(myContext, infoMess, Toast.LENGTH_SHORT); 
		                tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset()*2 );    
		                tost.show();                                
		                //v.setBackgroundResource(R.drawable.spinner_pressed);
		                //callNewActivity(pos); //        
		            }//end method
		        });		      
				return rootView;
			}
			//-------------------- class methods ---------------------------
			/*
			 * Η μέθοδος διαβάζει τα δεδομένα των 14 τελευταίων γραμμών του πίνακα Shift της βάσης
			 * και δημιουργεί ένα πίνακα με αντικείμενα τύπου : ShiftOfUser τα οποία έχουν
			 * όλες τις τιμές που αποθηκεύονται σε κάθε γραμμή του πίνακα της ΒΔ 
			 */
			private void readLastShiftsFromDB(){
				
				shiftsDB =  new DBControler(myContext);
				int userCode = 0;
				long sumShiftRows = 0;
				long startRows = 0;
				long startDatesMilisec = 0;
				final int DATES_MILISEC = 86400000; //τα milisec μιας ημέρας(24 ωρών) 
				SimpleDateFormat sdf  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
				Cursor c = null;
				
				shiftsDB.open();
				//sumShiftRows = shiftsDB.getSumRowsFromTable("Shift");	//τo πλήθος γραμμών του πίνακα: shift	
				try{  //το ID της γραμμής με την μεγαλύτερη (άρα και πιο πρόσφατη) ημερομηνία
				    sumShiftRows = shiftsDB.getShiftIDFromMaxDate(); 
				}catch(Exception e){
					e.printStackTrace();
					Log.i("ERROR_MAX_DATE_ID", "ΔΕΝ ΒΡΕΘΗΚΑΝ ΒΑΡΔΙΕΣ ΣΤΗ ΒΔ");
				}
				shiftsDB.closeDB();
				
				Log.i("START_ROWS", ": "+startRows);
				if(sumShiftRows >= 14){  //αν υπάρχουν τουλάχιστον 14 βάρδιες στη ΒΔ
					startRows = sumShiftRows - 13; //αφαιρεί 13 από τον άυξοντα αριθμό της τελευταίας γραμμής
					Calendar curDate    = Calendar.getInstance();
					Calendar curNewDate = Calendar.getInstance();
	  				curDate.clear();
	  				curDate.set(curNewDate.get(Calendar.YEAR),
	  						    curNewDate.get(Calendar.MONTH),
	  						    curNewDate.get(Calendar.DAY_OF_MONTH));
	  				//Log.i("CURRENT_DATES_MILISEC", ": "+curDate.getTimeInMillis());	
	  				Log.i("CURRENT_DATES", ": "+sdf.format(curDate.getTime()));
	  				shiftsDB.open();
	  				try{
	  		    	    userCode = shiftsDB.getIdValueFromTable("Employee");
	  		    	   }catch(Exception e){e.printStackTrace();}
	  				try{
	  				   c = shiftsDB.getShiftRowValues(startRows); //παίρνει τις τιμές βάρδιας της γραμμής: startRows
	  				   startDatesMilisec = c.getLong(2);          //η ημερομηνία πριν 13 ημέρες σε milisec
	  				   //Log.i("START_DATES_MILISEC", ": "+startDatesMilisec);
	  				   Log.i("START_DATE", ": "+ sdf.format(startDatesMilisec));	  				  
	  				   }catch(Exception e){
				    	   shiftsDB.closeDB();
				    	   e.printStackTrace();
				    	   Log.e("ERROR_DB_QUERRY :"+startRows, "ΔΕΝ ΒΡΕΘΗΚΕ ΒΑΡΔΙΑ");
				       } 
	  				 //αν καταχωρήθηκε το νέο πρόγραμμα που αρχίζει από Δευτέρα 
	  				 //θα συμπεριλάβει σε αυτό εγγραφές από την τρέχουσα ημέρα (αν υπάρχουν προηγούμενες εγγραφές)
	  				 if(( curDate.getTimeInMillis() < startDatesMilisec ) && sumShiftRows > 14){ 
	  					//προσθέτει στην γραμμή έναρξης της ημέρες διαφοράς από την τρέχουσα
	  					//startRows -= (int)((startDatesMilisec - curDate.getTimeInMillis()) / DATES_MILISEC);
	  				    //Log.i("NEW_START_ROWS", ": "+startRows);	  					
	  					long lastDate = (startDatesMilisec - DATES_MILISEC); //η προηγούμενη ημέρα της 13ης από την τελευταία
	  					try{
	  				       Cursor cur = shiftsDB.getAllShiftsBetweenDates(userCode, curDate.getTimeInMillis(), lastDate);
	  				       this.getShiftsValuesFromCursor(cur);
	  					}catch(Exception e){
	  						shiftsDB.closeDB();
	  	   			        e.printStackTrace();
	  	   			        Log.e("ERROR_SHIFTS", "RESULTS_NOT_FUND");
	  	   		        }    				    
	  				  }//end if	 	  				
	  				  try{
	  				     c = shiftsDB.getAllShiftsAfterDate(userCode, startDatesMilisec);
	  				     this.getShiftsValuesFromCursor(c);
	  				    }catch(Exception e){
	  				    	shiftsDB.closeDB();
  	   			            e.printStackTrace();
  	   			            Log.e("ERROR_SHIFTS", "RESULTS_NOT_FUND");
  	   			        }
	  				 shiftsDB.closeDB();
	  				 /*
					 for(long i= startRows; i<= sumShiftRows; i++){ //ξεκινάει από την 13η πριν την τελευταία
						 ShiftOfUser curShift = new ShiftOfUser();
						 SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
						 Calendar date = Calendar.getInstance();
		  				 date.clear();		  				
						 shiftsDB.open();
						 try{
							 c = shiftsDB.getShiftRowValues(i);     //παίρνει τις τιμές της γραμμής (i) του πίνακα: shift
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
						    }catch(Exception e){
						    	   shiftsDB.closeDB();
						    	   e.printStackTrace();
						    	   Log.e("ERROR_DB_QUERRY :"+i, "ΔΕΝ ΒΡΕΘΗΚΕ ΒΑΡΔΙΑ");
						    }						
						  listOfShifts.add(curShift);	//προσθέτει το στοιχείο βάρδια στον πίνακα
						  shiftsDB.closeDB();
					  }//end for	
					  */				
				 }//end if
				 else{
					 Toast.makeText(myContext, "Δεν Υπάρχουν Καταχωρημένες Βάρδιες", Toast.LENGTH_SHORT).show();
				 }				
			}//end method
			//--------------------------------------------------------------------
			/*
			 * Καταχωρεί σε πίνακα τις τιμές κάθε βάρδιας. Καλείται στην προηγούμενη
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
		}//end static fragment class	
		
	    //============================ inner class Add off work fragment ==========================
		/**
		 * A dummy fragment representing a section of the app, but that simply
		 * displays dummy text.
		 */
		public static class showOffWorksFragment extends Fragment {
			/**
			 * The fragment argument representing the section number for this
			 * fragment.
			 */
			public static final String ARG_SECTION_NUMBER = "section_number";
			private TextView txtYear;
			private TextView txtTotalDays;
			private TextView txtHaveTake;
			private TextView txtRestDays;
			//constractor
			public showOffWorksFragment() {
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
				View rootView = inflater.inflate(R.layout.fragment_display_of_works, container, false);
				//αφορά την ετικέτα αρίθμησης του fragment(invisible)
				TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label_4); 
				dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
				txtYear       = (TextView)rootView.findViewById(R.id.TextView_OfWork_Year);
				txtTotalDays  = (TextView)rootView.findViewById(R.id.textView_OfWork_TotalDays);
				txtHaveTake   = (TextView)rootView.findViewById(R.id.textView_OfWork_DaysHaveTake);
				txtRestDays   = (TextView)rootView.findViewById(R.id.textView_OfWork_Restdays);
				//έλεγχος backround
			    MainActivity.checkPreferentBackround(rootView);
				updateComponetsValues(); //θέτει τιμές στα πεδία της φόρμας
				return rootView;
			}
			//--------------------------- ΜΕΘΟΔΟΙ ΚΛΑΣΗΣ -------------------------
			//παίρνει τις τιμές που σχετίζονται με τον χρήστη απ τη ΒΔ 
			private void updateComponetsValues(){
				Calendar date  = Calendar.getInstance();
				int curYear    = date.get(Calendar.YEAR);
				txtYear.setText(""+curYear);  //θέτει την τιμή του τρέχοντος έτους στο πεδίο της φόρμας				
				String category= "ΚΑΝΟΝΙΚΗ";
				int sumRows    = 0;		//το πλήθος γραμμών του πίνακα άδειες
				int sumDaysTook= 0;     //το πλήθος ημερών αδίας που πήρε
				int sumDaysEntitled = 0;//το πλήθος ημερών αδείας που δικαιούτε για αυτό το έτος				
				DBControler db = new DBControler(getActivity().getApplicationContext());
				
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
				  try{
				      sumDaysTook = db.getSumDaysOfWorkHaveTaken(curYear, category); //το πλήθος αδειών που πείρε		    
				     }catch (Exception e){
					    db.closeDB(); 
			            e.printStackTrace();
			            Log.e("SQL_ERROR", "ΔΕΝ ΒΡΕΘΗΚΑΝ ΑΔΕΙΕΣ");
			         }				    
				  db.closeDB();
				  txtTotalDays.setText(""+sumDaysEntitled);//θέτει το άθροισμα αδειών που διακαιούτε στο πεδίο του πίνακα
				  txtHaveTake.setText(""+sumDaysTook);     //θέτει το άθροισμα αδειών που πήρε στο πεδίο του πίνακα
				  txtRestDays.setText(""+(sumDaysEntitled - sumDaysTook)); //θέτει τη υπόλοιπο αδειών στο πεδίο του πίνακα
				}//end if
				else{
					db.closeDB();
					Toast.makeText(getActivity().getApplicationContext(), 
							"Δεν έχετε Καταχωρήσει Άδειες για το Έτος: "+curYear, Toast.LENGTH_SHORT).show();
				}					
			}//end method
			//-----------------------------------------------------------------	
		}//end inner class
		
	    //================= inner class Add Queries fragment ==================
		/**
		 * A dummy fragment representing a section of the app, but that simply
		 * displays dummy text.
		 */
		public static class showQueriesFragment extends Fragment {
			/**
			* The fragment argument representing the section number for this
			* fragment.
			*/
			public static final String ARG_SECTION_NUMBER = "section_number";
			public static final String QUERY_TITLE = "QueryTitle";
			private ArrayList<String> listOfQueries;
			private ListView lv;
			private Context myContext;
			//constractor
			public showQueriesFragment() {
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
				View rootView = inflater.inflate(R.layout.fragment_display_queries_list, container, false);
				TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label3);
				dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
				
				//έλεγχος backround
			    MainActivity.checkPreferentBackround(rootView);
				myContext = getActivity().getApplicationContext();
				this.setQueryNames(myContext);  //ανάθεση των ονομάτων των ερωτημάτων
				//δημιουργία ListView και adapter για χειρισμό της λίστας				
				lv = (ListView)rootView.findViewById(R.id.QueryListView);		
				QueryListAdapter adapter = new QueryListAdapter(getActivity().getApplicationContext(), 
						                        R.layout.activity_item_of_queries_list , listOfQueries );
				lv.setAdapter(adapter); 
		        lv.setTextFilterEnabled(true);
		        lv.setSoundEffectsEnabled(true);
		        lv.setClickable(true);
		        // Listener 
		        lv.setOnItemClickListener(new OnItemClickListener (){
		        	@Override
		            public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {        		
		        		String infoMess = "Επιλέξατε την Ερώτηση : "+(pos+1);                
		        		Toast tost = Toast.makeText(getActivity(), infoMess, Toast.LENGTH_SHORT); 
		                tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset()*2 );    
		                tost.show();		               
		                v.setBackgroundResource(R.drawable.spinner_pressed);
		                v.findViewById(R.id.TextView_Time_Finish_Dialog).
		                  setBackgroundResource(R.drawable.textfield_with_red_stroke_pressed);
		                callNewActivity(pos);
		                //callNewActivity(pos); //
		            }//end method
		        });
				return rootView;
			}//end method
			 //=================== CALL A  NEW ACTIVITY ===========================
			 /*
			  * καλείται στο σώμα της onItemClick όταν ο χρήστης κάνει κλικ
			  * σε ένα αντικείμενο της λίστας
			  */
			 private void callNewActivity(int position) {	
				 
				   String query = listOfQueries.get(position); //επιστρέφει το όνομα του query  
				   /*
				    * In Java SE 7 and later, you can use a String object in the switch statement's expression.
				    * Πρέπει να αλλάξω τη έκδοση της Java σε 1,7 από 1,6 στις ιδιότητες :
				    * Properties/java Compiles
				    */
				   switch(position){
				      case 0 :  //αν αφορά τα queries 0
					       this.createIntent(query, DisplayShift.class);					  
			               break;
				      case 1 :  //αν αφορά τα queries 1
					       this.createIntent(query, DisplayIntervalOfShifts.class);				  
			               break;
				      case 2 : //αν αφορά τα queries 2-7
				      case 3 :
				      case 4 :
				      case 5 :
				      case 6 :
				      case 7 :  
		        	       this.createIntent(query, QueryDatesManager.class);	       		    
				           break;				           
				      case 8 :  //αν αφορά την Ερώτηση με τα  EXTRA REPO
		        	       this.createIntent(query, DisplayExtraRepo.class);	       		    
					       break;
				      case 9  : //αν αφορά την Ερώτηση με το πλήθος Εργάσιμων Κυριακών ή Αργιών
		        	  case 10 : //αν αφορά την Ερώτηση με το πλήθος Εργάσιμων Κυριακών ή Αργιών
		        	       this.createIntent(query, ShowWorkedHolidays.class);	       		    
					       break;  
		        	  case 11 :  //αν αφορά την Ερώτηση με Άδειες Έτους επιλογής του χρήστη
		        	       this.createIntent(query, YearOfWorks.class);	       		    
					       break;     
				      case 12 :  //αν αφορά την Ερώτηση με τα Υπόλοιπα Αδειών
		        	       this.createIntent(query, DisplayOfWorkYear.class);	       		    
					       break;
				      case 13 :  //αν αφορά την Ερώτηση με τα ΡΕΠΟ εκτατεταμένης διάρκειας
		        	       this.createIntent(query, ShowMultiDaysRepo.class);	       		    
					       break;
				      case 14 :  //αν αφορά την Ερώτηση με τα Συγκεντρωτικά Στοιχεία
		        	       this.createIntent(query, ShowSumResults.class);
		        	       break;
			    }//end switch
			 }//end method
			//-----------------------------------------------------------
			 /*
			  * Δημιουργεί ένα Intent κάθε φορά που καλείται για να κληθεί
			  * μια καινούρια Activity
			  */
			private void createIntent(String queryName, Class cl){
				   Intent queryInt = new Intent(getActivity() , cl);	       		    
				   queryInt.putExtra(QUERY_TITLE, queryName);
				   startActivityForResult(queryInt, RESULT_OK);
			 }//end method
			//----------------------------------------------
			 //θέτει τον τιτλο της activity που θα κληθεί
			private void setQueryNames(Context context){
				//έλεγχος της επιλεγμένης γλωσσας του λειτουργικού Android
				String systemLanguage = Locale.getDefault().getLanguage().toString();
				//Log.i("SYSTEM_LANGUARE", curLanguage);
				//έλεγχος της επιλεγμένης γλώσσας από το χρήστη στο menu settings
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				String userLanguage = sharedPref.getString(SettingsShifts.KEY_SELECTED_LANGUGE, "");
				Log.i("PREFERENCE_LANGUARE", userLanguage);
				//αν το android είναι στα Ελληνικά ή ο χρήστης επέλεξε Ελληνική γλώσσα στις ρυθμίσεις
				if(systemLanguage.equals("el") || userLanguage.equals("el")){					
				   Log.i("SYSTEM_LANGUARE", systemLanguage);
				  listOfQueries = new ArrayList<String>();
				  listOfQueries.add("Βάρδια Ημερομηνίας");  //query title 0
				  listOfQueries.add("Διάστημα Βαρδιών");    //query title 1
				  listOfQueries.add("Πρωϊνές Βάρδιες");     //query title 2
				  listOfQueries.add("Απογευματινές Βάρδιες");
				  listOfQueries.add("Βραδινές Βάρδιες");
				  listOfQueries.add("Ρεπό");
				  listOfQueries.add("Άδειες");
				  listOfQueries.add("Ασθένειες");
			      listOfQueries.add("Εξτρα Ρεπό");            //query title 8
				  listOfQueries.add("Κυριακές που Εργάστηκα");//query title 9
				  listOfQueries.add("Αργίες που Εργάστηκα");  //query title 10
				  listOfQueries.add("Άδειες που Πήρα");       //query title 11
				  listOfQueries.add("Υπόλοιπα Αδειών");       //query title 12
				  listOfQueries.add("Πολυήμερα Ρεπό");        //query title 13
				  listOfQueries.add("Συγκεντρωτικά Στοιχεία");//query title 14
			    }
				//αν το android είναι στα Αγγλικά ή ο χρήστης επέλεξε Αγγλική γλώσσα στις ρυθμίσεις
				else if(systemLanguage.equals("en") || userLanguage.equals("en")){					
				   Log.i("SYSTEM_LANGUARE", systemLanguage);
				  listOfQueries = new ArrayList<String>();
				  listOfQueries.add("One Shift");           //query title 0
				  listOfQueries.add("Interval of Shifts");  //query title 1
				  listOfQueries.add("Shifts Morning");      //query title 2
				  listOfQueries.add("Shifts Afternoon");
				  listOfQueries.add("Shifts Night");
				  listOfQueries.add("Days Off");            //ρεπό
				  listOfQueries.add("Days Annual Leave");
				  listOfQueries.add("Sick Days");           //ασθενεια
				  listOfQueries.add("Extra Days Off");      //query title 8
				  listOfQueries.add("Working Sundays");     //query title 9
				  listOfQueries.add("Working Holidays");    //query title 10
				  listOfQueries.add("OffWorks Have Taken"); //query title 11
				  listOfQueries.add("Rest of Annual Leave");//query title 11
				  listOfQueries.add("Multi Days off");      //query title 12
				  listOfQueries.add("Total Results");       //query title 13					
				}
			}//end method
		}//end inner class
       
}//end class
