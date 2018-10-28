package com.kutsubogeras.shifts.insert;

import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.R.drawable;
import com.kutsubogeras.shifts.R.id;
import com.kutsubogeras.shifts.R.layout;
import com.kutsubogeras.shifts.R.menu;
import com.kutsubogeras.shifts.data.DBControler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

/*
 * 
 * 
 * 
 */
public class EmployeeInsertion extends Activity {
	
    //Variables	
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
	private Button workButton;
	private Button backButton;
	private Button resetButton;
	private DBControler dataBase;      //Μεταβλητή της ΒΔ για εισαγωγή τιμών
	private Context myContext;
	private static final String addUserSuccessMessage = "Επιτυχής Προσθήκη Χρήστη !!!";
	private final  int RESULT_EXIT = 1; // τιμή σταθεράς για τερματισμό της activity
	private final  int ADD_WORKED_OK_FLAG = 5; // τιμή σταθεράς για τερματισμό της activity
	private Camera myCamera;
	private static final int FILE_SELECT_CODE = 12;
	private String path = null;
	private Uri uri = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employee_insertion);
		//δημιουργία αντικειμένων editText
		textCode      = (EditText)findViewById(R.id.editTextCode);
		textName      = (EditText)findViewById(R.id.editTextName);
		textLastName  = (EditText)findViewById(R.id.editTextLastName);
		textAddress   = (EditText)findViewById(R.id.editTextAdrress);
		textPhone     = (EditText)findViewById(R.id.editTextPhone);
		textMobile    = (EditText)findViewById(R.id.editTextMobile);
		textJob       = (EditText)findViewById(R.id.editTextJob);
		textCompany   = (EditText)findViewById(R.id.editTextCompany);
		textDepartment= (EditText)findViewById(R.id.editTextDepartment);
		textGrade     = (EditText)findViewById(R.id.editTextGrade);
		textPhoto     = (TextView)findViewById(R.id.textView_Photo);
		workButton    = (Button)findViewById(R.id.Button_Worked_OK);
		backButton    = (Button)findViewById(R.id.Button_Worked_Back);
		resetButton   = (Button)findViewById(R.id.Button_Worked_Reset);
		//θέτει Listener στο TextView 
		this.setComponensListeners();
	}//end method

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.employee_insertion, menu);
		menu.clear();
		menu.add(0, R.id.item1_emplo_insert, 0, "Βοήθεια")
            .setIcon(android.R.drawable.ic_menu_help);		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
		     case R.id.item1_emplo_insert: 
		    	 this.showingHelpMessage();
		    	 return true;		    
		     default:
		         return super.onOptionsItemSelected(item);
		}
	}//end method
	
	@Override
	public void onPause(){
		super.onPause();
		if (myCamera != null) {
		    myCamera.release();
		    myCamera = null;
		}
	}
	//--------------------------- ON ACTIVITY RESULT -------------------------------
    //όταν τερματίσει η κληθήσα activity 	    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);			
			//Log.i("RESULT_CODE", ": "+resultCode);
	        if ((requestCode == FILE_SELECT_CODE) && (resultCode == -1) ) {
	            // Get the Uri of the selected file 
	            uri = data.getData();
	            Log.i("ΕΠΙΛΕΓΜΕΝΟ ΑΡΧΕΙΟ", "File Uri: " + uri.toString());
	            // Get the path
	            path = this.getAbsolutePath(uri); //επιστρέφει τη διαδρομή και το όνομα αρχείου
	            Log.i("ΔΙΑΔΡΟΜΗ", "File Path: " + path);
	            textPhoto.setText(path);	            
	        }	        
    }//end method  
	//--------------------GET ABSOLUTE PATH OF IMAGE ----------------------------
  	private String getAbsolutePath(Uri uri) {
          String[] projection = { MediaColumns.DATA };
          Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
          if (cursor != null) {
              int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
              cursor.moveToFirst();
              return cursor.getString(column_index);
          } else
              return null;
      }//end method
  	//-----------------------------------------------------------------------
	/*
	 * Θέτει Listeners στα διάφορα συστατικά της φόρμας
	 */
	private void setComponensListeners(){
		textPhoto.setOnClickListener(new View.OnClickListener() {       		
			  public void onClick(View v) {
				  //εμφανίζει dialog με τις επιλογές επιλογής φωτογραφίας
				  displayPhotoChoicesDialog();	
				  //showFileChooser();
			  }
	    });
	    //θέτει Listener στο button
        workButton.setOnClickListener(new View.OnClickListener() {       		
			  public void onClick(View v) {       		     
			       //δημιουργεί ένα Intent για την εισαγωγή εργαζόμενου	
				  if(checkEmployeeValues()){ //αν είναι συμπληρωμένα όλα τα υποχρ. πεδία
					  setResult(ADD_WORKED_OK_FLAG);
					  finish();				         
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
	//---------------------------------------------------------------------
	//εισάγει τις τιμές στη ΒΔ
	private boolean checkEmployeeValues(){
		
		dataBase = new DBControler(this); //αρχικοποίηση με το this της κλάσης DBControler		
		String errorUserCodeMessage    = "Δεν Συμπληρώσατε: Κωδικό Χρήστη";		
		String errorUserCodeZeroMessage= "Ο Κωδικός Χρήστη είναι Μηδέν(0). Επιλέξτε ένα Θετικό Αριθμό.";
		String errorUserNameMessage    = "Δεν Συμπληρώσατε: Όνομα Χρήστη";
		String errorUserLastMessage    = "Δεν Συμπληρώσατε: Επώνυμο Χρήστη";
		String errorUserAddressMessage = "Δεν Συμπληρώσατε: Διεύθυνση Χρήστη";
		String errorUserPhoneMessage   = "Δεν Συμπληρώσατε: Τηλέφωνο Χρήστη";
		String errorUserJobMessage     = "Δεν Συμπληρώσατε: Επάγγελμα Χρήστη";
		String errorUserCompanyMessage = "Δεν Συμπληρώσατε: Εταιρεία Χρήστη";
		String errorUserDepartmMessage = "Δεν Συμπληρώσατε: Τμήμα Εργασίας";
		String successUserInsertMessage= "Επιτυχής Καταχώρηση Εργαζομένου";
		String errorUserExistsMessage  = "Υπάρχει ήδη Εργαζόμενος με αυτό τον Κωδικό." +
				                         "/nΠαρακαλώ Επιλέξτε Νέο Κωδικό";
		long   sumRows    = 0;		
		int    userCode   = 0;
		int    isUser     = 0;
		String tableEmplo = "Employee";
		String myCode     = textCode.getText().toString();
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
		
		if(!myCode.equals("") ){ //αν δεν είναι κενό 
		   userCode = Integer.parseInt(myCode);	//μετατροπή σε ακέραιο του string myCode
		   Log.i("ΒΗΜΑ_10", "ΟΚ ΜΕΤΑΤΡΟΠΗ ΣΕ ΑΚΕΡΑΙΟ");	
		   if(userCode == 0){ //αν είναι μηδέν ο κωδικός
			  Toast tost = Toast.makeText(this, errorUserCodeZeroMessage, Toast.LENGTH_SHORT); 
	          tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() *5);
	          tost.show();
	          return false; 
		   }		   
		}
		else{	
		   Toast tost = Toast.makeText(this, errorUserCodeMessage, Toast.LENGTH_SHORT); 
           tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() *5);
           tost.show();
           return false;		   
		}		
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
		//εισάγει τιμές στον εργαζόμενο
		dataBase.open();    //ανοίγει τη βάση δεδομένων
		sumRows = dataBase.getSumRowsFromTable(tableEmplo);
		dataBase.closeDB();   //κλείνει τη βάση
		Log.i("QUERRY-1", "ΟΚ CHECK CODE");	
		
		if(sumRows == 0){   //αν δεν υπάρχει άλλος εργαζόμενος στη ΒΔ. Είναι κενός ο πίνακας
		  isUser = 1;       //θέτει ως χρήστη τον εργαζόμενο αφού είναι ο 1ος που θα εισαχθεί
		  dataBase.open();  //ανοίγει τη βάση δεδομένων
		  dataBase.InsertIntoEmployeeValues( userCode, myName, myLastName, myAddress,
				                             myPhone, myMobile, myJob, myCompany,
				                             myDepartm, myGrade, myPhoto, isUser);
		  dataBase.closeDB();  //κλείνει τη βάση
		  Log.i("QUERRY-2", "ΟΚ INSERT EMPLOYEE");
		  Toast tost = Toast.makeText(this, successUserInsertMessage, Toast.LENGTH_SHORT); 
          tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
          tost.show();
		}
		else{                    //αν ο πίνακας δεν ειναι κενός. Υπάρχει και άλλος καταχωρημένος
			dataBase.open();     //ανοίγει τη βάση δεδομένων
			sumRows = dataBase.getSumRowsFromTable(tableEmplo, userCode);
			dataBase.closeDB();  //κλείνει τη βάση
			if(sumRows ==0){     //αν δεν υπάρχει ήδη καταχωρημένος χρήστης με τον ίδιο κωδικό
			   isUser = 0;       //θέτει τον εργαζόμενο ως μη χρήστη της συσκευής
			   dataBase.open();  //ανοίγει τη βάση δεδομένων
			   dataBase.InsertIntoEmployeeValues( userCode, myName, myLastName, myAddress,
						                          myPhone, myMobile, myJob, myCompany,
						                          myDepartm, myGrade, myPhoto, isUser);
			   dataBase.closeDB();  //κλείνει τη βάση
			   Toast tost = Toast.makeText(this, successUserInsertMessage, Toast.LENGTH_SHORT); 
		       tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 5);
		       tost.show();
			}
			else{                // αν υπάρχει ήδη καταχωρημένος ο εργαζόμενος
				this.warningFailureEmployeeInsertion(errorUserExistsMessage); //εμφανίζει μήνυμα αποτυχίας
                return false;
		    }//end else
		}//end else
		return true;
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
	//-----------------------------------------------------------------------------
	//Εμφανίζει Dialog με μήνυμα Αποτυχίας Εισαγωγής εργαζομένου
	private void warningFailureEmployeeInsertion(String errorMessage){				
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
			          }
			    });					   		       
			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message
	 } //end method
	//-----------------------------------------------------------------------------
	 /*
	    * Εμφανίζει Dialog με τις διαθέσιμες επιλογές Φωτογραφίας
	    * Ο χρήστης μπορεί να επιλέξει ένα υπάρχον αρχείο φωτογραφίας
	    * ή να χρησιμοποιήσει την κάμερα για να τραβήξει μια φωτογραφία
	    */
	   private void displayPhotoChoicesDialog(){	
	    	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	    	        //String notSelectionMessage=;
	  				String[] userChoices ={"Επιλογή Αρχείου",
	  						               "Χρήση Κάμερας"};
	  				// Θέτει εικόνα των ρυθμίσεων στο Dialog
	  			    alertDialog.setIcon(R.drawable.info_icon);	
	  			    // Dialog Τίτλος
	  			    alertDialog.setTitle("Επιλογή Φωτογραφίας");		 
	  			    // Εμφάνιση Επιλογών του Dialog	στο χρήστη	  			    
	  			    alertDialog.setSingleChoiceItems(userChoices, -1, null);
	  			    alertDialog.setInverseBackgroundForced(false);
	  			    alertDialog.setCancelable(false);
	  			    //Αν πατηθεί το ΟΚ button κλείνει το Dialog
	  			    alertDialog.setPositiveButton("ΟΚ", new DialogInterface.OnClickListener() {
	  			          public void onClick(DialogInterface dialog, int choice) {  			        	   
	  		                   int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
	  		                   if(selectedPosition == -1){ //αν δεν έχει γίνει καμία επιλογή
	  		                	  dialog.cancel();
	  		                	  Toast tost = Toast.makeText(getApplicationContext(), 
	  		                			 "Δεν Επιλέξατε Τρόπο Εισαγωγής", Toast.LENGTH_SHORT); 
	  		             	      tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 4);
	  		             	      tost.show();
	  		                   }
	  		                   if(selectedPosition == 0){  //αν επιλέξει φόρτωση αρχείου φωτογραφίας
	  		                	  dialog.dismiss();	  		                	  
	  		                	  showFileChooser(); //κλήση file chooser intent
	  		                   }
	  		                   if(selectedPosition == 1){ //αν επιλέξει χρήση κάμερας
	  		                	  dialog.dismiss();
	  		                	  //openFrontFacingCamera();	  		                	 
	  					          //δημιουργεί ένα Intent για την εισαγωγή φωτογραφίας				       
	  							  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	  							  startActivityForResult(intent, 0);	  							 
	  		                   }	  		                  
	  			          }
	  			    });					   		       
	  			    alertDialog.show(); // Εμφάνιση του  Alert dialog Message		  			   
	  	} //end method
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
	              this.startActivityForResult(
	                    Intent.createChooser(intent, fileChooserTitle),
	            		FILE_SELECT_CODE);
	         }catch (android.content.ActivityNotFoundException ex) {
	            // Potentially direct the user to the Market with a Dialog
	            Toast.makeText(this, "Παρακαλώ εγκαταστήστε ένα \'File Manager\'.",
	            		Toast.LENGTH_SHORT).show();
	         }
		   }//end if
	   }//end method
	   //---------------------------------------------------------------------------
	   /*
	    * Ανοίγει την Μπροστινή Κάμερα για λήψη φωτογραφίας
	    */
	   private void openFrontFacingCamera() {
		    int numberOfCameras = 0;
		    CameraInfo info = new CameraInfo();
		    numberOfCameras = Camera.getNumberOfCameras();
		    Log.i("NUMBER_OF_CAMERAS", " : "+numberOfCameras);
		    for (int camIndex = 0; camIndex < numberOfCameras; camIndex++) {
		        Camera.getCameraInfo(camIndex, info);
		        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
		            try {
		                myCamera = Camera.open(camIndex);
		                Log.i("CAMERA_OPEN", " : "+camIndex);
		                break;
		                } catch (RuntimeException e) {
		                  Log.e("ERROR_OPEN_CAMERA", "Η Κάμερα δεν Άνοιξε : " + e.getLocalizedMessage());
		                }
		        }//end if
		    }//end for		    
		}
	   //---------------------------- ΒΟΗΘΕΙΑ ---------------------------
	   //Εμφανίζει Dialog με Πληροφορίες Συμπλήρωσης της φόρμας  
	   //για συγκεκριμένη ημερομηνία
	   public void showingHelpMessage(){	
	 		    	  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	 		  		  Spanned defaultMessage = Html.fromHtml("Για Επιστροφή Πατήστε \'ΟΚ\'"); 
	 		  		  Spanned helpMes = Html.fromHtml("Συμπληρώστε οπωσδήποτε τα πεδία της φόρμας με αστέρι(*) " +
	 		  		  		  "και όποια από τα υπόλοιπα θέλετε να εμφανίζονται οι πληροφορίες.<br>" +
	 		  				  "<font color=\"#FF0000\"><b>1. </b></font>" +
	 		  				  "Για να συμπληρώστε ένα πεδίο απλώς αγγίξτε το και πληκτρολογήστε κείμενο ή αριθμούς<br>"+
	 		  				  "<font color=\"#FF0000\"><b>2. </b></font>" +
	 		  				  "Για να επιλέξετε μια φωτογραφία σας που θα εμφανίζεται " +
	 		  				  "στην προβολή στοιχείων εργαζομένου, απλώς αγγίξτε το πεδίο " +
	 		  				  "<font color=\"#D2691E\">\'Αναζήτηση\'</font> " +
	 		  				  "δίπλα στην ετικέτα \'Φωτογραφία\'.<br>" +
	 		  				  "<font color=\"#FF0000\"><b>3. </b></font>" +
	 		  				  "Στη συνέχεια στo αναδυόμενο παράθυρο με τίτλο: " +
	 		  				  "<font color=\"#0000FF\">\'Επιλογή Φωτογραφίας\'</font> επιλέξτε: " +
	 		  				  "<font color=\"#D2691E\">\'Επιλογή αρχείου\'</font> και πατήστε " +
	 		  				  "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>. " +
	 		  				  "Μπορείτε επίσης να ανοίξετε την μποστινή κάμερα του κινητού (αν υπάρχει) " +
	 		  				  "επιλέγοντας: <font color=\"#D2691E\">\'Χρήση Κάμερας\'</font>. " +
	 		  				  "Με αυτή την επιλογή δεν μπορείτε να φορτώσετε μια φωτογραφία. " +
	 		  				  "Απλώς φωτογραφίζετε τον εαυτό σας.<br> " +
	 		  				  "<font color=\"#FF0000\"><b>4. </b></font>" +
	 		  				  "Ακολούθως χρησιμοποιείστε ένα από τα προγράμματα:  " +
	 		  				  "<font color=\"#0000FF\">\'Συλλογή\'</font> ή " +
	 		  				  "<font color=\"#0000FF\">\'Φωτογραφίες\'</font> " +
	 		  				  "για να επιλέξτε μια φωτογραφία σας.<br>" +
	 		  				  "<font color=\"#FF0000\"><b>5. </b></font></b></font>" +
	 		  				  "Αν όλα εκτελεστούν σωστά θα δείτε τη διαδρομή και το όνομα του αρχείου φωτογραφίας " +
	 		  				  "στο πλήκτρο που προηγουμένως έφερε την επωνυμία: " +
	 		  				  "<font color=\"#0000FF\">\'Αναζήτηση\'</font>. " +
	 		  				  "Αν δεν φορτώνεται η διαδρομή αρχείου τερματίστε την εφαρμογή " +
	 		  				  "και αφού περιορίσετε τις διαστάσεις της φωτογραφίας με την εντολή " +
	 		  				  "<font color=\"#0000FF\">\'Crop\'</font> γύρω από το πρόσωπο " +
	 		  				  "του εικονιζομένου προσπαθείστε ξανά.<br>" +
	 		  				  "<font color=\"#FF0000\"><b>6. </b></font>" +
	 		  				  "Αφού συμπληρώσετε όλα τα επιθυμητά πεδία πατήστε " +
	 		  				  "<font color=\"#008B00\"><b>\'ΟΚ\'</b></font>.<br>" +
	 		  				  "<font color=\"#FF0000\"><b>7. </b></font>" +
	 		  				  "Αν θέλετε να διαγράψετε όλες τις τιμές πατήστε: " +
	 		  				  "<font color=\"#FF1493\">\'Διαγραφή\'</font>.<br><br>" + defaultMessage);	  				
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
