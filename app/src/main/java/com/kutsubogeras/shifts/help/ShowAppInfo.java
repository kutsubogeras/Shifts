package com.kutsubogeras.shifts.help;


import java.util.Locale;

import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.R.id;
import com.kutsubogeras.shifts.R.layout;
import com.kutsubogeras.shifts.R.menu;
import com.kutsubogeras.shifts.settings.SettingsShifts;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ShowAppInfo extends Activity {
	  //Μεταβλητές Κλάσης
     private TextView help_mes;
	 
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_appl_info);
		
        help_mes = (TextView)findViewById(R.id.textView_ShowAppl_Info_Mes);
        this.setMessage();		
	}//end method
	//--------------------------------------------------
	/*
	 * Θέτει το μήνυμα στο TextView
	 */
	private void setMessage(){
		    //αν η γλώσσα είναι Ελληνικά
		    Spanned userHelpMes = Html.fromHtml(
    			"<font color=\"#00FF00\"><b>======== ΓΕΝΙΚΑ ΣΤΟΙΧΕΙΑ ======== </b></font><br>"+
    			" Αυτή η εφαρμογή προσφέρει τη δυνατότητα αποθήκευσης, τροποποίησης και προβολής πληροφοριών" +
    	   		" που σχετίζονται με τα ωράρια εργασίας όσων εργάζονται σε βάρδιες.<br>" +
    	   		"Επομένως ο χρήστης αφού εισάγει στην εφαρμογή τα προγράμματα με τις βάρδιες εργασίας του " +
    	   		"θα μπορεί να αντλήσει πληροφορίες και στατιστικά στοιχεία για το χρονικό διάστημα που επιλέγει.<br><br>" +
    	   		
    	   		"<font color=\"#00FF00\"><b>========= ΑΡΧΙΚΗ ΦΟΡΜΑ ========= </b></font><br>"+
    	   		"Η Αρχική Φόρμα της εφαρμογής αποτελείται από 5 επιμέρους τμήματα.<br>"+    	   		
    	   		"Η πλοήγηση μεταξύ αυτών γίνεται τραβώντας την εικόνα με το δάκτυλο δεξιά ή αριστερά.<br>"+
    	   		"<font color=\"#EE5C42\"><b>1. </b></font>"+
    	   		"Στo 1o τμήμα <font color=\"#FFCC00\"><b>\'ΒΑΡΔΙΕΣ\'</b></font> " +
    	   		"εμφανίζονται τρία πλήκτρα (με εικόνα). <br>"+
    	   		"<font color=\"#00FF00\"><b> A. </b></font>"+
    	   		"Πατώντας το αριστερό πλήκτρο με το γράφημα " +
    	   		"<font color=\"#99CC32\">\'Εικόνας Χρήστη\'</font> " +
    	   		"η εφαρμογή εμφανίζει φόρμα \'Εισαγωγής Στοιχείων\' του εργαζομένου.<br>"+
    	   		"<font color=\"#00FF00\"><b> B. </b></font>"+
    	   		"Πατώντας το δεύτερο πλήκτρο με την ένδειξη: " +
    	   		"<font color=\"#99CC32\">\'ΒΑΡΔΙΑ\'</font> " +
    	   		"η εφαρμογή εμφανίζει παράθυρο επιλογής του τρόπου εισαγωγής βαρδιών.<br>"+
    	   		"Υπάρχουν 3 επιλογές: <br>" +
    	   		"\'Εισαγωγή Βάρδιας\' για 1 Ημέρα," + 
    	   		"<br>για 1 Εβδομάδα<br>" +
    	   		"ή για 2 Εβδομάδες.<br>"+
    	   		"Ο χρήστης επιλέγει μία από τις 3 και πατά \'ΟΚ\'. "+
    	   		"Η εφαρμογή στη συνέχεια εμφανίζει κατάλληλη φόρμα.<br>"+
    	   		"<font color=\"#00FF00\"><b> Γ. </b></font>"+
    	   		"Πατώντας το δεξιό πλήκτρο με την ένδειξη: " +
    	   		"<font color=\"#99CC32\">\'ΑΔΕΙΑ\'</font> " +
    	   		"η εφαρμογή εμφανίζει φόρμα για \'Εισαγωγή Αδείας\' του χρήστη.<br>"+
    	   		"<font color=\"#EE5C42\"><b>2. </b></font>"+
    	   		"Στο 2ο τμήμα <font color=\"#FFCC00\"><b>\'ΕΡΓΑΖΟΜΕΝΟΣ\'</b></font>" +
    	   		" εμφανίζονται τα προσωπικά στοιχεία του χρήστη(εφόσον τα έχει εισάγει).<br>"+
    	   		"<font color=\"#EE5C42\"><b>3. </b></font>"+
    	   		"Στο 3ο τμήμα <font color=\"#FFCC00\"><b>\'ΤΡΕΧΟΝ ΠΡΟΓΡΑΜΜΑ\'</b></font>" +
    	   		" εμφανίζεται το Τελευταίο Πρόγραμμα του χρήστη(οι 14 πιο πρόσφατες Βάρδιες).<br>"+
    	   		"<font color=\"#EE5C42\"><b>4. </b></font>"+
    	   		"Στο 4ο τμήμα <font color=\"#FFCC00\"><b>\'ΚΑΝΟΝΙΚΗ ΑΔΕΙΑ\'</b></font>" +
    	   		" εμφανίζεται ανάλυση της Κανονικής Άδειας του Τρέχοντος Έτους.<br>"+
    	   		"<font color=\"#EE5C42\"><b>5. </b></font>"+
    	   		"Στο 5ο τμήμα <font color=\"#FFCC00\"><b>\'ΕΡΩΤΗΣΕΙΣ\'</b></font>" +
    	   		" ο χρήστης μπορεί να επιλέξει μεταξύ 13 διαφορετικών Επιλογών.<br><br>"+
    	   		
    	   		"<font color=\"#FF00FF\"><b>----------- ΠΡΟΣΟΧΗ !!! ----------- </b></font><br>"+
    	   		" Για να μπορεί να λειτουργεί σωστά η εφαρμογή, αρχικώς θα πρέπει να εισάγετε:<br>"+
    	   		" Τα Στοιχεία Εργαζομένου (αριστερό πλήκτρο)<br>"+
    	   		" και στη συνέχεια τις Βάρδιες ή Άδειες (κεντρικό ή δεξιό πλήκτρο αντίστοιχα) "+
    	   		"<font color=\"#FF00FF\"><b>**************************************** </b></font><br><br>"+
    	   		
    	   		"<font color=\"#00FF00\"><b>========= ΕΡΩΤΗΣΕΙΣ ========= </b></font><br>"+
    	   		"Σε αυτό το τμήμα της εφαρμογής ο χρήστης μπορεί να επιλέξει κάποια " +
    	   		"από τις 13 συνολικά ερωτήσεις της βάσης δεδομένων προκειμένου να αντλήσει" +
    	   		" στατιστικά δεδομένα για συγκεκριμένο χρονικό διάστημα και κατηγορία βαρδιών όπως:<br>"+
    	   		"<font color=\"#EE5C42\"><b>1. </b></font>"+
    	   		"Τη βάρδια που εργάστηκε μια συγκεκριμένη ημερομηνία.<br>" +
    	   		"<font color=\"#EE5C42\"><b>2. </b></font>"+
    	   		"Τις Άδειες που πήρε καθώς και τα Υπόλοιπα Αδειών του τρέχοντος ή άλλου έτους.<br>" +
    	   		"<font color=\"#EE5C42\"><b>3. </b></font>"+
    	   		"Το πλήθος Αργιών ή Κυριακών που εργάστηκε για μια συγκεκριμένη χρονική περίοδο.<br>" +    	   		
    	   		"<font color=\"#EE5C42\"><b>4. </b></font>"+
    	   		"Το πλήθος Ημερών που εργάστηκε για μία συγκεκριμένη βάρδια(Πρωϊ, Απόγευμα, Βράδυ).<br>"+
    	   		"<font color=\"#EE5C42\"><b>5. </b></font>"+
    	   		"Το πλήθος των Ρεπό που εκτείνονται πέραν των 2 Ημερών (Πολυήμερα Ρεπό) "+
    	   		"τα οποία πήρε ο εργαζόμενος σε συγκεκριμένο χρονικό διάστημα.<br>"+
    	   		"<font color=\"#EE5C42\"><b>6. </b></font>"+
    	   		"Τα Έξτρα Ρεπό που ίσως προκύπτουν (για Τραπεζικούς Υπαλλήλους). "+
    	   		"Υπολογίζοντας όλες τις καταχωρημένες βάρδιες.<br>"+
    	   		"<font color=\"#EE5C42\"><b>7. </b></font>"+
    	   		"Συγκεντρωτικά Στοιχεία όλων των βαρδιών για συγκεκριμένο χρονικό διάστημα.<br><br>" +
    	   		
    	   		"<font color=\"#00FF00\"><b>======== ΕΠΙΛΟΓΕΣ ΜΕΝΟΥ ======== </b></font><br>"+
    	   		"Από το μενού της αρχικής φόρμας ο χρήστης μπορεί να εκτελέσει τις παρακάτω εργασίες:<br>"+
    	   		"<font color=\"#EE5C42\"><b>1. </b></font>"+
    	   		"Να διαβάσει χρήσιμες πληροφορίες για τον τρόπο λειτουργίας της εφαρμογής πατώντας: \'Βοήθεια\'<br>"+
    	   		"<font color=\"#EE5C42\"><b>2. </b></font>"+
    	   		"Να αλλάξει κάποιες από τις ρυθμίσεις της εφαρμογής (Γλώσσα, Background) πατώντας: \'Ρυθμίσεις\'<br>"+
    	   		"<font color=\"#EE5C42\"><b>3. </b></font>"+
    	   		"Να ενημερωθεί για το πλήθος βαρδιών καθώς επίσης την 1η και τελευταία βάρδια πατώντας : \'Πλήθος Βαρδιών\'<br>" +
    	   		"<font color=\"#EE5C42\"><b>4. </b></font>"+
    	   		"Να αλλάξει κάποια από τα Στοιχεία Εργαζομένου πατώντας: \'Αλλαγή Εργαζομένου\'<br>" +
    	   		"<font color=\"#EE5C42\"><b>5. </b></font>"+
    	   		"Να αλλάξει το ωράριο κάποιας Βάρδιας πατώντας: \'Αλλαγή Βάρδιας\'<br>" +
    	   		"<font color=\"#EE5C42\"><b>6. </b></font>"+
    	   		"Να αλλάξει τα στοιχεία κάποιας Άδειας πατώντας: \'Αλλαγή Άδειας\'<br>" +
    	   		"<font color=\"#EE5C42\"><b>7. </b></font>"+
    	   		"Να δει επιπλέον πληροφορίες για το δημιουργό της εφαρμογής πατώντας: \'Σχετικά\'<br>");
		    //αν η γλώσσα είναι Αγγλικά
		    Spanned userHelpMes_en = Html.fromHtml(
    			"<font color=\"#00FF00\"><b>======== GENERAL INFO ======== </b></font><br>"+
    			" With this application a user has the abillity to save, edit or display data" +
    	   		" about the time of working some workers that work in shifts.<br>" +
    	   		"Επομένως ο χρήστης αφού εισάγει στην εφαρμογή τα προγράμματα με τις βάρδιες εργασίας του " +
    	   		"θα μπορεί να αντλήσει πληροφορίες και στατιστικά στοιχεία για το χρονικό διάστημα που επιλέγει.<br><br>" +
    	   		
    	   		"<font color=\"#00FF00\"><b>========= INITIAL PAGE ========= </b></font><br>"+
    	   		"The initial interface of the application includes 5 parts.<br>"+    	   		
    	   		"You can navigate between different fragments by pulling the image with a finger left or right.<br>"+
    	   		"<font color=\"#EE5C42\"><b>1. </b></font>"+
    	   		"In the first part <font color=\"#FFCC00\"><b>\'SHIFTS\'</b></font> " +
    	   		"are displayed three buttons with an image. <br>"+
    	   		"<font color=\"#00FF00\"><b> A. </b></font>"+
    	   		"Pressing the left button of the user's image" +
    	   		"<font color=\"#99CC32\">\'Εικόνας Χρήστη\'</font> " +
    	   		"η εφαρμογή εμφανίζει φόρμα \'Εισαγωγής Στοιχείων\' του εργαζομένου.<br>"+
    	   		"<font color=\"#00FF00\"><b> B. </b></font>"+
    	   		"Πατώντας το δεύτερο πλήκτρο με την ένδειξη: " +
    	   		"<font color=\"#99CC32\">\'SHIFT\'</font> " +
    	   		"η εφαρμογή εμφανίζει παράθυρο επιλογής του τρόπου εισαγωγής βαρδιών.<br>"+
    	   		"Υπάρχουν 3 επιλογές: <br>" +
    	   		"\'Εισαγωγή Βάρδιας\' για 1 Ημέρα," + 
    	   		"<br>για 1 Εβδομάδα<br>" +
    	   		"ή για 2 Εβδομάδες.<br>"+
    	   		"Ο χρήστης επιλέγει μία από τις 3 και πατά \'OK\'. "+
    	   		"Η εφαρμογή στη συνέχεια εμφανίζει κατάλληλη φόρμα.<br>"+
    	   		"<font color=\"#00FF00\"><b> Γ. </b></font>"+
    	   		"Πατώντας το δεξιό πλήκτρο με την ένδειξη: " +
    	   		"<font color=\"#99CC32\">\'LEAVE\'</font> " +
    	   		"η εφαρμογή εμφανίζει φόρμα για \'Add Leave\' του χρήστη.<br>"+
    	   		"<font color=\"#EE5C42\"><b>2. </b></font>"+
    	   		"In the second part <font color=\"#FFCC00\"><b>\'EMPLOYEE\'</b></font>" +
    	   		" εμφανίζονται τα προσωπικά στοιχεία του χρήστη(εφόσον τα έχει εισάγει).<br>"+
    	   		"<font color=\"#EE5C42\"><b>3. </b></font>"+
    	   		"In the third part <font color=\"#FFCC00\"><b>\'RECENT SHIFTS\'</b></font>" +
    	   		" εμφανίζεται το Τελευταίο Πρόγραμμα του χρήστη(οι 14 πιο πρόσφατες Βάρδιες).<br>"+
    	   		"<font color=\"#EE5C42\"><b>4. </b></font>"+
    	   		"In the fourth part <font color=\"#FFCC00\"><b>\'ANNUAL LEAVE\'</b></font>" +
    	   		" εμφανίζεται ανάλυση της Κανονικής Άδειας του Τρέχοντος Έτους.<br>"+
    	   		"<font color=\"#EE5C42\"><b>5. </b></font>"+
    	   		"In the fifth part <font color=\"#FFCC00\"><b>\'QUESTIONS\'</b></font>" +
    	   		" ο χρήστης μπορεί να επιλέξει μεταξύ 13 διαφορετικών Επιλογών.<br><br>"+
    	   		
    	   		"<font color=\"#FF00FF\"><b>----------- ΑΤΤΕΝΤΙΟΝ !!! ----------- </b></font><br>"+
    	   		" Για να μπορεί να λειτουργεί σωστά η εφαρμογή, αρχικώς θα πρέπει να εισάγετε:<br>"+
    	   		" Τα Στοιχεία Εργαζομένου (αριστερό πλήκτρο)<br>"+
    	   		" και στη συνέχεια τις Βάρδιες ή Άδειες (κεντρικό ή δεξιό πλήκτρο αντίστοιχα) "+
    	   		"<font color=\"#FF00FF\"><b>**************************************** </b></font><br><br>"+
    	   		
    	   		"<font color=\"#00FF00\"><b>========= QUESTIONS ========= </b></font><br>"+
    	   		"Σε αυτό το τμήμα της εφαρμογής ο χρήστης μπορεί να επιλέξει κάποια " +
    	   		"από τις 13 συνολικά ερωτήσεις της βάσης δεδομένων προκειμένου να αντλήσει" +
    	   		" στατιστικά δεδομένα για συγκεκριμένο χρονικό διάστημα και κατηγορία βαρδιών όπως:<br>"+
    	   		"<font color=\"#EE5C42\"><b>1. </b></font>"+
    	   		"Τη βάρδια που εργάστηκε μια συγκεκριμένη ημερομηνία.<br>" +
    	   		"<font color=\"#EE5C42\"><b>2. </b></font>"+
    	   		"Τις Άδειες που πήρε καθώς και τα Υπόλοιπα Αδειών του τρέχοντος ή άλλου έτους.<br>" +
    	   		"<font color=\"#EE5C42\"><b>3. </b></font>"+
    	   		"Το πλήθος Αργιών ή Κυριακών που εργάστηκε για μια συγκεκριμένη χρονική περίοδο.<br>" +    	   		
    	   		"<font color=\"#EE5C42\"><b>4. </b></font>"+
    	   		"Το πλήθος Ημερών που εργάστηκε για μία συγκεκριμένη βάρδια(Morning, Afternoon, Night).<br>"+
    	   		"<font color=\"#EE5C42\"><b>5. </b></font>"+
    	   		"Το πλήθος των Ρεπό που εκτείνονται πέραν των 2 Ημερών (Πολυήμερα Ρεπό) "+
    	   		"τα οποία πήρε ο εργαζόμενος σε συγκεκριμένο χρονικό διάστημα.<br>"+
    	   		"<font color=\"#EE5C42\"><b>6. </b></font>"+
    	   		"Τα Έξτρα Ρεπό που ίσως προκύπτουν (για Τραπεζικούς Υπαλλήλους). "+
    	   		"Υπολογίζοντας όλες τις καταχωρημένες βάρδιες.<br>"+
    	   		"<font color=\"#EE5C42\"><b>7. </b></font>"+
    	   		"Συγκεντρωτικά Στοιχεία όλων των βαρδιών για συγκεκριμένο χρονικό διάστημα.<br><br>" +
    	   		
    	   		"<font color=\"#00FF00\"><b>======== MENOU CHOICES ======== </b></font><br>"+
    	   		"Using the menou in action bar the user can do the follow tasks:<br>"+
    	   		"<font color=\"#EE5C42\"><b>1. </b></font>"+
    	   		"To read usefull information about this application press: \'Help\'<br>"+
    	   		"<font color=\"#EE5C42\"><b>2. </b></font>"+
    	   		"To change some settings such as Language or the Background in this application. Simply press: \'Settings\'<br>"+
    	   		"<font color=\"#EE5C42\"><b>3. </b></font>"+
    	   		"To get information about the total records of shifts, the first and the last shift press : \'Total Shifts\'<br>" +
    	   		"<font color=\"#EE5C42\"><b>4. </b></font>"+
    	   		"To change some of the user data press: \'Change Employee\'<br>" +
    	   		"<font color=\"#EE5C42\"><b>5. </b></font>"+
    	   		"To change the time of a shift press: \'Change Shift\'<br>" +
    	   		"<font color=\"#EE5C42\"><b>6. </b></font>"+
    	   		"To change the data in one leave press: \'Change Leave\'<br>" +
    	   		"<font color=\"#EE5C42\"><b>7. </b></font>"+
    	   		"To read more information about the developer of this application press: \'About\'<br>");
		 
		    //έλεγχος της επιλεγμένης γλωσσας του λειτουργικού Android
			String systemLanguage = Locale.getDefault().getLanguage().toString();
			//-------------- check user languge in settings -----------------
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			String userLanguage = sharedPref.getString(SettingsShifts.KEY_SELECTED_LANGUGE, "");	
			
			if(systemLanguage.equals("el") || userLanguage.equals("el"))
				help_mes.setText(userHelpMes);
			else if(systemLanguage.equals("en")|| userLanguage.equals("en"))
				help_mes.setText(userHelpMes_en);
    	    
    }//end method
	//--------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_shift, menu);		
		return true;
	}//end method

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){		     
		     case R.id.action_settings: 
		         return true;			
		     default:
		         return super.onOptionsItemSelected(item);
		}
	}//end method	 
	
}//end class
