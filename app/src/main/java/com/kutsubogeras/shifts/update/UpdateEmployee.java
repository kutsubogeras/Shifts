package com.kutsubogeras.shifts.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore.MediaColumns;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.data.DBControler;
import com.kutsubogeras.shifts.settings.SettingsShifts;

import java.util.ArrayList;

/*
 * 
 * 
 * 
 */
public class UpdateEmployee extends Activity {
	
    //Variables
	private FrameLayout frameLayout;
	private static EditText textCode;
	private static EditText textName;
	private static EditText textLastName;
	private static EditText textAddress;
	private static EditText textPhone;
	private static EditText textMobile;
	private static EditText textJob;
	private static EditText textCompany;
	private static EditText textDepartment;
	private static EditText textGrade;
	private static TextView textPhoto;	
	private Button OKButton;
	private Button backButton;
	private Button resetButton;
	private DBControler dataBase;      //Μεταβλητή της ΒΔ για εισαγωγή τιμών
	private int userCode =0;
	private Context myContext;
	private static final String addUserSuccessMessage = "Επιτυχής Προσθήκη Χρήστη !!!";
	private final int RESULT_EXIT = 1; // τιμή σταθεράς για τερματισμό της activity
	private final int ADD_WORKED_OK_FLAG = 5; // τιμή σταθεράς για τερματισμό της activity
	private Camera myCamera;
	private static final int FILE_SELECT_CODE = 12;
	private String path = null;
	private ArrayList<Integer> backgrounds = new ArrayList<Integer>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employee_update);
		//δημιουργία αντικειμένων editText
		frameLayout   = (FrameLayout)findViewById(R.id.FrameLayout_employee_update);
		textCode      = (EditText)findViewById(R.id.editText_EmploUpdate_Code);
		textName      = (EditText)findViewById(R.id.editText_EmploUpdate_Name);
		textLastName  = (EditText)findViewById(R.id.editText_EmploUpdate_LastName);
		textAddress   = (EditText)findViewById(R.id.editText_EmploUpdate_Adrress);
		textPhone     = (EditText)findViewById(R.id.editText_EmploUpdate_Phone);
		textMobile    = (EditText)findViewById(R.id.editText_EmploUpdate_Mobile);
		textJob       = (EditText)findViewById(R.id.editText_EmploUpdate_Job);
		textCompany   = (EditText)findViewById(R.id.editText_EmploUpdate_Company);
		textDepartment= (EditText)findViewById(R.id.editText_EmploUpdate_Department);
		textGrade     = (EditText)findViewById(R.id.editText_EmploUpdate_Grade);
		textPhoto     = (TextView)findViewById(R.id.textView_EmploUpdate_Photo);
		OKButton      = (Button)findViewById(R.id.Button_EmploUpdate_OK);
		backButton    = (Button)findViewById(R.id.Button_EmploUpdate_Back);
		resetButton   = (Button)findViewById(R.id.Button_EmploUpdate_Reset);
		
		this.setBackroundIcons();
		this.checkPreferentBackround(frameLayout);
		//θέτει Listener στο ediText
		textPhoto.setOnClickListener(new View.OnClickListener() {       		
				  public void onClick(View v) {       		     
				       //δημιουργεί ένα Intent για την εισαγωγή φωτογραφίας				       
					   showFileChooser();
				  }
		}); 
		
		this.setEmployeeValues();// θέτει τιμές στα πεδία της φόρμας
		
		//θέτει Listener στο button
        OKButton.setOnClickListener(new View.OnClickListener() {       		
				  public void onClick(View v) {       		     
				       //δημιουργεί ένα Intent για την εισαγωγή εργαζόμενου	
					  if(checkEmployeeValues()){
						  warningEmployeeUpdate();				         
					  }
				  }
		});
        //θέτει Listener στο button
        backButton.setOnClickListener(new View.OnClickListener() {       		
				  public void onClick(View v) {       
						  finish();					  
				  }
		});
        //θέτει Listener στο button
        resetButton.setOnClickListener(new View.OnClickListener() {       		
				  public void onClick(View v) {       		     
				          resetValues();
				  }
		});
	}//end method
	//--------------------------- ON ACTIVITY RESULT -------------------------------
    //όταν τερματίσει η κληθήσα activity 	    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);			
			//Log.i("RESULT_CODE", ": "+resultCode);
	        if ((requestCode == FILE_SELECT_CODE) && (resultCode == -1)) {
	            // Get the Uri of the selected file 
	            Uri uri = data.getData();
	            Log.i("ΕΠΙΛΕΓΜΕΝΟ ΑΡΧΕΙΟ", "File Uri: " + uri.toString());
	            // Get the path	            
	            path = getAbsolutePath(uri);//uri.getPath();
	            Log.i("ΔΙΑΔΡΟΜΗ", "File Path: " + path);
	            textPhoto.setText(path);	            
	        }	        
    }//end method 
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.employee_update, menu);
		menu.clear();
		menu.add(0, R.id.item1_update_emloyee_help, 0, "Βοήθεια")
            .setIcon(android.R.drawable.ic_menu_help);	
		return true;
	}//end method
	//---------------------------- MENU ITEMS -------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
	        case R.id.item1_update_emloyee_help: 
	    	     this.showingHelpMessage();
	    	     return true;		    
	        default:
	             return super.onOptionsItemSelected(item);
	     }//end switch
	}
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
	//--------------------GET ABSOLUTE PATH OF IMAGE ----------------------------
	private String getAbsolutePath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
        String fileName = "Άγνωστη";
        Uri myUri = uri;
         //η managedQuery(contentUri, proj, null, null, null); είναι : deprecated
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null); 
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
             Log.i("URI_PATH", ""+cursor.getString(column_index));
            myUri = Uri.parse(cursor.getString(column_index));
            fileName = myUri.getLastPathSegment().toString();
             Log.i("PHOTO_NAME", ""+fileName);
            return cursor.getString(column_index);	
        } else
            return null;
    }//end method
	//------------------------SET VALUES ---------------------------------
	private void setEmployeeValues(){
		long   sumRows    = 0;		
		int    userId     = 1; //το id εγγραφής εργαζομενου
	    Cursor c = null;		
		String tableEmplo = "Employee";
		String dialogTitle= "Λανθασμένη Επιλογή";
		String erUserNotExistsMes = "Δεν Έχετε Εισάγει Στοιχεία Εργαζομένου στη Βάση Δεδομένων.";
		
		dataBase = new DBControler(this); //αρχικοποίηση με το this της κλάσης DBControler
		//ελέγχει αν ο πίνακας εργαζόμενος έχει έγγραφές
		dataBase.open();         //ανοίγει τη βάση δεδομένων
		sumRows = dataBase.getSumRowsFromTable(tableEmplo);
		if(sumRows == 0){        //αν δεν υπάρχουν εγγραφές
		   dataBase.closeDB();   //κλείνει τη βάση
		   this.showingDialogWithMessage(dialogTitle, erUserNotExistsMes, R.drawable.error_icon);
		}//end if
		else{    //αν υπάρχουν εγγραφές
			try{ //παίρνει τα στοιχεία του χρήστη
			    c = dataBase.getAllValuesFromTable(tableEmplo, userId);
			    this.getCursorValues(c);
			   }catch(Exception e){
				   dataBase.closeDB();   //κλείνει τη βάση
				   Log.e("SQL_ERROR", "ΔΕΝ ΒΡΕΘΗΚΕ ΕΡΓΑΖΟΜΕΝΟΣ");
			   }
		}
	}//end method
	 //-------------------------GET CURSOR VALUES -----------------------
    /*
     * Επιστρέφει τις τιμές του Εργαζόμενου που βρίσκονται στον Cursor
     * και τις θέτει στα TextViews της φόρμας. Καλείται στην Προηγούμενη μέθοδο:
     * setEmployeeValues()     
     */
    private void getCursorValues(Cursor c){
    	String dialogTitle = "Ανύπαρκτος Χρήστης";
    	String erUserNotExistsMes = "Δεν Έχετε Εισάγει Στοιχεία Εργαζομένου στη Βάση Δεδομένων.";
    	if(c.moveToFirst()) {
    		 do{    			 
				int    code     = c.getInt(1);    		    
    		    String name     = c.getString(2);
    		    String lastName = c.getString(3);
    		    String address  = c.getString(4);  
    		    String phone    = c.getString(5);    		    
    		    String mobile   = c.getString(6);
    		    String profes   = c.getString(7);
    		    String company  = c.getString(8); 
    		    String depart   = c.getString(9);
    		    String grade    = c.getString(10); 
    		    String photo    = c.getString(11);
    		    //ανάθεση τιμών στα πεδία
    		    textCode.setText(""+code);
    			textName.setText(name);
    			textLastName.setText(lastName);
    			textAddress.setText(address);
    			textPhone.setText(phone);
    			textMobile.setText(mobile);
    			textJob.setText(profes);
    			textCompany.setText(company);
    			textDepartment.setText(depart);	
    			textGrade.setText(grade);
    			textPhoto.setText(photo);    			
    		   }while(c.moveToNext());
    	   }//end if
    	   else{
    		   this.showingDialogWithMessage(dialogTitle, erUserNotExistsMes, R.drawable.warning);    		   
    	   }
       }//end method
	//-------------------- method of class---------------------------------
	/*
	 * Ελέγχει τις τιμές της φόρμας
	 */	
	private boolean checkEmployeeValues(){		
		String errorUserCodeMessage    = "Δεν Συμπληρώσατε: Κωδικό Χρήστη";
		String errorUserCodeZeroMessage= "Ο Κωδικός Χρήστη είναι Μηδέν(0). Επιλέξτε Θετικό Αριθμό.";
		String errorUserNameMessage    = "Δεν Συμπληρώσατε: Όνομα Χρήστη";
		String errorUserLastMessage    = "Δεν Συμπληρώσατε: Επώνυμο Χρήστη";
		String errorUserAddressMessage = "Δεν Συμπληρώσατε: Διεύθυνση Χρήστη";
		String errorUserPhoneMessage   = "Δεν Συμπληρώσατε: Τηλέφωνο Χρήστη";
		String errorUserJobMessage     = "Δεν Συμπληρώσατε: Επάγγελμα Χρήστη";
		String errorUserCompanyMessage = "Δεν Συμπληρώσατε: Εταιρεία Χρήστη";
		String errorUserDepartmMessage = "Δεν Συμπληρώσατε: Τμήμα Εργασίας";
		String dialogTitle             = "Λανθασμένη Επιλογή";
		String erConertMes             = "Ο Κωδικός Εργαζομένου δεν είναι αποδεκτός."+
				                         "\nΠαρακαλώ Επιλέξτε νέο Κωδικό ";
		String myCode     = textCode.getText().toString();
		String myName     = textName.getText().toString();
		String myLastName = textLastName.getText().toString();
		String myAddress  = textAddress.getText().toString();
		String myPhone    = textPhone.getText().toString();
		String myJob      = textJob.getText().toString();
		String myCompany  = textCompany.getText().toString();
		String myDepartm  = textDepartment.getText().toString();		
		String myPhoto    = textPhoto.getText().toString();		
		myPhoto = myPhoto.equals("")? "/" : myPhoto;		
		
		if(myCode.equals("")){				
		   Toast tost = Toast.makeText(this, errorUserCodeMessage, Toast.LENGTH_SHORT); 
           tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
           tost.show();
           return false;		   
		}
		else{
			try{
			    userCode = Integer.parseInt(myCode); //μετατροπή σε ακέραιο του string myCode
			    if(userCode == 0){ //αν είναι μηδέν ο κωδικός
				  Toast tost = Toast.makeText(this, errorUserCodeZeroMessage, Toast.LENGTH_SHORT); 
			      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
			      tost.show();
			      return false; 
				 }//end if
			   }catch(NumberFormatException nfe){
					Log.e("ERROR_INTEGER_CONVER", "ΔΕΝ ΈΓΙΝΕ Η ΜΕΤΑΤΡΟΠΗ ΣΕ ΑΚΕΡΑΙΟ");
					nfe.printStackTrace();
					this.showingDialogWithMessage(dialogTitle, erConertMes, R.drawable.error_icon);
					return false;
			   }			
		}//end else
		if(myName.equals("")){
		   Toast tost = Toast.makeText(this, errorUserNameMessage, Toast.LENGTH_SHORT); 
	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
	       tost.show();
	       return false;
		}
		if(myLastName.equals("")){
		   Toast tost = Toast.makeText(this, errorUserLastMessage, Toast.LENGTH_SHORT); 
	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
	       tost.show();
	       return false;
		}
		if(myAddress.equals("")){
		   Toast tost = Toast.makeText(this, errorUserAddressMessage, Toast.LENGTH_SHORT); 
	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
	       tost.show();
	       return false;
		}
		if(myPhone.equals("")){
		   Toast tost = Toast.makeText(this, errorUserPhoneMessage, Toast.LENGTH_SHORT); 
	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
	       tost.show();
	       return false;
		}
		if(myJob.equals("")){
		   Toast tost = Toast.makeText(this, errorUserJobMessage, Toast.LENGTH_SHORT); 
	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
	       tost.show();
	       return false;
		}
		if(myCompany.equals("")){
		   Toast tost = Toast.makeText(this, errorUserCompanyMessage, Toast.LENGTH_SHORT); 
	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
	       tost.show();
	       return false;
		}
		if(myDepartm.equals("")){
		   Toast tost = Toast.makeText(this, errorUserDepartmMessage, Toast.LENGTH_SHORT); 
	       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
	       tost.show();
	       return false;
		}
		return true;
	}//end method
	
	//----------------------------UPDATE EMPLOYEE -----------------------------------
	/*
	 * Ενημερώνει τη ΒΔ με τα νέα Στοιχεία Εργαζόμενου
	 */
	private void updateEmployee(){
		
		long   sumRows    = 0;		
		int    userOldCode= 0;
		int    userId     = 1;       //το id εγγραφής του 1ου εργαζομενου
		boolean isUpdated = false;
		boolean isShiftUpdated  = false;
		boolean isOfworkUpdated = false;
		String myName     = textName.getText().toString();
		String myLastName = textLastName.getText().toString();
		String myAddress  = textAddress.getText().toString();
		String myPhone    = textPhone.getText().toString();
		String myMobile   = textMobile.getText().toString();
		String myJob      = textJob.getText().toString();
		String myCompany  = textCompany.getText().toString();
		String myDepartm  = textDepartment.getText().toString();		
		String myGrade    = textGrade.getText().toString();
		String myPhoto    = textPhoto.getText().toString();		
		myPhoto = myPhoto.equals("")? "/" : myPhoto;
		
		String tableEmplo = "Employee";
		String dialogTitle= "Επιτυχής Ενημέρωση";
		String successMes = "Η Αλλαγή Στοιχείων του Εργαζομένου : "+myLastName+" Ολοκληρώθηκε Επιτυχώς";
		String erUserNotExistsMes = "Δεν Έχετε Εισάγει Στοιχεία Εργαζομένου στη Βάση Δεδομένων.";
		String errorUserExistsMes = "Υπάρχει ήδη Εργαζόμενος με αυτό τον Κωδικό." +
                                         "/nΠαρακαλώ Επιλέξτε Νέο Κωδικό";		
		dataBase = new DBControler(this); //αρχικοποίηση με το this της κλάσης DBControler
		//ελέγχει αν ο πίνακας εργαζόμενος έχει έγγραφές
		dataBase.open();    //ανοίγει τη βάση δεδομένων
		sumRows = dataBase.getSumRowsFromTable(tableEmplo);
		if(sumRows == 0){
		   dataBase.closeDB();   //κλείνει τη βάση
		   Toast tost = Toast.makeText(this, erUserNotExistsMes, Toast.LENGTH_SHORT); 
		   tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
		   tost.show();
		}//end if
		else{// αν βρέθηκε καταχωρημένος εργαζόμενος		
		   try{//παίρνει το κωδικό του εργαζόμενου που έχει καταχωρηθεί ήδη
		      userOldCode = dataBase.getIdValueFromTable(tableEmplo);
		      }catch(Exception e){
			     dataBase.closeDB(); //κλείνει τη βάση	
			     Log.e("USER_ID", "ΔΕΝ ΒΡΕΘΗΚΕ ID ΓΙΑ ΤΟΝ ΕΡΓΑΖΟΜΕΝΟ");
		      }	
		   if(userOldCode > 0){
			  isUpdated = dataBase.updateEmployeeValues(userId   , userCode , myName  , myLastName, 
				                                        myAddress, myPhone  , myMobile, myJob,
				                                        myCompany, myDepartm, myGrade , myPhoto);
			  Log.i("EMPLOYEE_UPDATED", ""+isUpdated);
		      dataBase.closeDB();    //κλείνει τη βάση	
		      if(isUpdated && (userCode != userOldCode)){ //αν άλλαξε και ο κωδικός εργαζομένου
		    	  dataBase.open();
		    	  //ενημερώνει τις βάρδιες που σχετίζοναι με αυτό τον κωδικό εργαζομένου
		    	  isShiftUpdated  = dataBase.updateShiftForeingKey(userOldCode, userCode);
		    	  Log.i("SHIFTS_UPDATED", ""+isShiftUpdated);
		    	  //ενημερώνει τιςε άδειες που σχετίζονται με τον κωδικό εργαζομένου
		    	  isOfworkUpdated = dataBase.updateOfWorkForeingKey(userOldCode, userCode);
		    	  Log.i("OFWORK_UPDATED", ""+isOfworkUpdated);
		    	  dataBase.closeDB(); 
		    	  //εμφανίζει μήνυμα επιτυχούς εκτέλεσης εργασιών
		    	  this.showingDialogWithMessage(dialogTitle, successMes, R.drawable.info_icon);		    	 
		      }
		   }//end if
		   else{  //αν υπάρχει καταχωρημένος εργαζόμενος στη ΒΔ. Δεν είναι κενός ο πίνακας
			   dataBase.closeDB();  //κλείνει τη βάση	
		   }		  
		}//end else		
	}//end method
	//------------------------------------------------------------------------
	/*
	 * Διαγράφει τις τιμές σε όλα τα πεδία της φόρμας
	 */
	private void resetValues(){
		textCode.setText("");
		textName.setText("");
		textLastName.setText("");
		textAddress.setText("");
		textPhone.setText("210");
		textMobile.setText("");
		textJob.setText("");
		textCompany.setText("");
		textDepartment.setText("");	
		textGrade.setText("");
		textPhoto.setText("");	
	}//end method
	
	//-------------------------- UPDDATE DIALOG OR CANCEL------------------------------- 
	    /*
	     * Εμφανίζει Dialog με μήνυμα Προειδοποίησης στο Χρήστη ότι πρόκειται
	     * να αλλαχθούν τα στοιχεία της Άδειας. Αν Πατήσει ΟΚ η Άδεια αλλάζει
	     * αλλιώς ακυρώνεται η αλλαγή
	     */
	public void warningEmployeeUpdate(){				
	  				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	  				String defaultMessage = "Πρόκειται να Μεταβάλετε τα στοιχεία του Εργαζόμενου : "+
	  						                 textLastName.getText().toString()+
	  						                 "\nΑν Συμφωνείται Πατήστε \'ΟΚ\' Αλλιώς Πατήστε \'ΑΚΥΡΩΣΗ\'"; 		 
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
	  					       updateEmployee(); //καλεί τη μέθοδο που θα κάνει update την βάρδια
	  					       finish();
	  			          }
	  			    });	
	  			    alertDialog.setNegativeButton("ΑΚΥΡΩΣΗ", new DialogInterface.OnClickListener() {
			          public void onClick(DialogInterface dialog, int choice) {            	    
					       dialog.cancel(); //κλείνει το dialog 
					       finish();
			          }
			    });		
	  			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
	} //end method	
	//--------------------------DIALOG WITH MESSAGE --------------------
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
	//--------------------------DIALOG WITH MESSAGE --------------------
		/*Εμφανίζει Dialog το οποίο παίρνει ως παραμέτρους :
		 * το τίτλο και το μήνυμα προβολής στο χρήστη
		 */
	public void showingDialogWithSpannedMessage(Spanned title, Spanned errorMessage, int iconResId){				
		  				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		  				// Dialog Τίτλος
		  			    alertDialog.setTitle(title);		 
		  			    // Μήνυμα του Dialog
		  			    alertDialog.setMessage(errorMessage);		 
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
	 //----------------------------SHOW HELP MESSAGE -----------------------------
	 /*
	  * Εμφανίζει μήνυμα βοήθειας στο χρήστη
	  */
	private void showingHelpMessage(){
    	 Spanned titleHelp = Html.fromHtml("Οδηγίες !!!");
    	 Spanned defaultMessage =  Html.fromHtml("Για Επιστροφή Πατήστε: \'ΟΚ\'"); 
    	 Spanned helpMes  = Html.fromHtml("<font color=\"red\"><b>"+"Αλλαγή Στοιχείων"+"</b></font><br>"
	  		  		+ "<font color=\"red\"><b>1. </b></font>"
	  		  		+ "Για να αλλάξετε κάποια από τα στοιχεία εργαζομένου "
	  		  		+ "απλά επιλέξτε την υπάρχουσα τιμή σε ένα πεδίο και πληκτρολογήστε μια άλλη.<br>"
	  		  		+ "<font color=\"red\"><b>2. </b></font>"
	  		  		+ "Για να διαγράψετε κάποιoυς χαρακτήρες χρησιμοποιείστε το πλήκτρο " +
	  		  		  "<font color=\"#0000FF\">\'BackSpace\'</font>.<br>"
	  		  		+ "<font color=\"red\"><b>3. </b></font>"
	  		  		+ "Μπορείτε επίσης να διαγράψετε όλες τις τιμές πατώντας: " +
	  		  		  "<font color=\"#FF1493\">\'Διαγραφή\'</font><br>"+defaultMessage);
    	 
        this.showingDialogWithSpannedMessage(titleHelp, helpMes, R.drawable.info_icon);
    }//end method
    //--------------------------FILE CHOOSER ---------------------------
	/*
	 * Δημιουργία ενός File Chooser
	 * */
	 private void showFileChooser() {
		   //αν η έκδοση του android είναι μεγαλύτερη ή ίση με την έκδοση ΚΙΤ-ΚΑΤ
		    Log.i("ANDROID_VERSION :"+android.os.Build.VERSION.SDK_INT , 
				"VERSION_NAME :"+android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1);
		    if(android.os.Build.VERSION.SDK_INT >= 
				  android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
	          Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
	          intent.setType("image/*"); //όλες οι κατηγορίες
	          intent.addCategory(Intent.CATEGORY_OPENABLE);	       
	          String fileChooserTitle = "Επιλογή  Φωτογραφίας";
	          try {
	              this.startActivityForResult( Intent.createChooser(intent, fileChooserTitle),
	                     FILE_SELECT_CODE);
	          }catch (android.content.ActivityNotFoundException ex) {
	            // Potentially direct the user to the Market with a Dialog
	            Toast.makeText(this, "Παρακαλώ εγκαταστήστε ένα \'File Manager\'.",
	            		Toast.LENGTH_SHORT).show();
	          }
		    }//end if
	   }//end method
	//-----------------------------------------------------------------------------
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        //c = Camera.open(1); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}//end method
	//---------------------------------------------------------------------------------
}//end class
