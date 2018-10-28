package com.kutsubogeras.shifts.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.kutsubogeras.shifts.MainActivity;
import com.kutsubogeras.shifts.R;

import java.util.List;
import java.util.Locale;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * 
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsShifts extends PreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	//μεταβλητή για έλεγχο επιλεγμένης γλώσσας από άλλη activity
	public static final String USER_NAME  = "name_text";
	public static final String KEY_SELECTED_LANGUGE  = "selected_language";
	public static final String KEY_SELECTED_BACKGROUND = "selected_background";
	public static final String KEY_SELECTED_RINGTONE = "notifications_new_message_ringtone";
	public static final String KEY_SELECTED_VIBRATE = "notifications_new_message_vibrate";
	public static final String KEY_SELECTED_PASSWORD  = "pref_user_password";
	public static final String KEY_PASSWORD  = "user_password";
	public static final int SELECTED_LANGUGE_FLAG = 3;
	public static final int SELECTED_PASSWORD_FLAG = 2;
	private static String keyLangugeValue = "";
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private static Context context;
	private Button button_ok;
    
    
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		  //τυπώνεται η android sdk version της συσκευής όπου τρέχει η εφαρμογή
		  //καθώς επίσης και η ακέραια τιμή της ορόσημο version HONEYCOMP
        Log.i("SDK", "CUR_DEVICE_SDK_VERSION :"+Build.VERSION.SDK_INT+
        		  " HONEYCOMB :"+Build.VERSION_CODES.HONEYCOMB);
        context = this.getApplicationContext();         
		this.setupSimplePreferencesScreen(); //καλεί την επόμενο μέθοδο
	}
    //-----------------------------------------------------------------------
	
	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		
		PreferenceCategory header;
		
		if (!isSimplePreferences(this)) {
			return;
		}
		setContentView(R.layout.fragment_settings_shifts_button);
        button_ok = (Button)findViewById(R.id.button_settings_ok);
        
        button_ok.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mainInt = new Intent(SettingsShifts.this , MainActivity.class);				
			    mainInt.putExtra(KEY_SELECTED_LANGUGE, keyLangugeValue);
			    setResult(SELECTED_LANGUGE_FLAG );
			    finish();
			}
		});
        
		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.\		
		
		// Add language preferences.	
		this.addPreferencesFromResource(R.xml.pref_general2);

		// Add 'notifications' preferences, and a corresponding header.
		header = new PreferenceCategory(this);
		header.setTitle(R.string.pref_title_header_notifications);
		this.getPreferenceScreen().addPreference(header);
		this.addPreferencesFromResource(R.xml.pref_notification2);

        // Add preferences, Password and a corresponding header.
		header = new PreferenceCategory(this);
		header.setTitle(R.string.pref_title_header_password);
		this.getPreferenceScreen().addPreference(header);
		this.addPreferencesFromResource(R.xml.pref_password);

		// Add 'backround' preferences, and a corresponding header.
		header = new PreferenceCategory(this);
		header.setTitle(R.string.pref_title_header_background);
		this.getPreferenceScreen().addPreference(header);
		this.addPreferencesFromResource(R.xml.pref_background);
		
		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference(USER_NAME));
		bindPreferenceSummaryToValue(findPreference(KEY_SELECTED_LANGUGE));
		bindPreferenceSummaryToValue(findPreference(KEY_SELECTED_RINGTONE));
		bindPreferenceSummaryToValue(findPreference(KEY_SELECTED_BACKGROUND));
	}	
	
	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & 
				Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 * 
	 * Ελέγχει αν η τρέχουσα έκδοση του Ανδροιδ που είναι εγκατεστημένη
	 * στη συσκευή που θα τρέξει η ερφαρμογή είναι μικρότερη της 11 = HONEYCOMB = v 3.0
	 * Η Build.VERSION.SDK_INT επιστρέφει ένα ακέραιο που προσδιορίζει την 
	 * τρέχουσα έκδοση του ανδροιδ της συσκευής
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}
    //-------------------------------------------------------
	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers2, target);
		}
	}
	//======================================================================================
	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange( preference,
				PreferenceManager.getDefaultSharedPreferences(preference.getContext())
						.getString(preference.getKey(), ""));
	}
    //--------------------------------------------------------------
	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = 
			                                        new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object keyValue) {
			//έλεγχος της επιλεγμένης γλωσσας του λειτουργικού Android
			String systemLanguage = Locale.getDefault().getLanguage().toString();
			String stringValue = keyValue.toString();
			Log.i("PREFERENCE_NAME", stringValue);  
			String message_en = "To complete your choice you must change the system's language of course";
            String message_el = "Για να ολοκληρωθεί η μετατροπή θα πρέπει επίσης" +
            		            " να αλλάξετε και την γλώσσα του android";

			if(preference.getKey().equals("name_text")){
				preference.getEditor().putString(USER_NAME, keyValue.toString());
				preference.getEditor().apply();
			}
			if(preference.getKey().equals(KEY_PASSWORD)){
				preference.getEditor().putString(KEY_PASSWORD, keyValue.toString());
				preference.getEditor().apply();
			}
            //προβάλεται μήνυμα ανάλογα με τη γλώσσα του λειτουργικού συστήματος
            if(systemLanguage.equals("en") && stringValue.equals("el")){ //σύστημα Αγγλικά επιλογή Ελληνικά
            	Toast tost = Toast.makeText(context, message_en, Toast.LENGTH_LONG); 
          	    tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 4);
          	    tost.show();
            }
            else if(systemLanguage.equals("el") && stringValue.equals("en")){//σύστημα Ελληνικά επιλογή Αγγλικά
            	Toast tost = Toast.makeText(context, message_el, Toast.LENGTH_LONG); 
          	    tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset() * 4);
          	    tost.show();
            }
            //αν πρόκειται για τιμή από λίστα τιμών
			if(preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				keyLangugeValue = stringValue; //κρατά την τιμή της επιλεγμένης γλώσσας
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);
				
				// Set the summary to reflect the new value.
				preference.setSummary(index >= 0 ? listPreference.getEntries()[index]: null);

			}else if (preference instanceof RingtonePreference) {//αν είναι ringtone επιλογή
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);

				}else {
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}
			}
			else if (preference instanceof CheckBoxPreference ){
					Boolean boolVal = (Boolean)keyValue;
				    String checValue = (boolVal==true)? "true": "false";
				    preference.setSummary(checValue);
			}
			else { //αν πρόκειται για κάποια άλλη προτίμηση του χρήστη
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};


    //====================== GENERAL PREFERENCE FRAGMENT ========================
	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general2);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("name_text"));      // όνομα χρήστη
			bindPreferenceSummaryToValue(findPreference("language_list"));  // γλώσσα προτίμησης			
		}
	}
	
	//======================= NOTIFICATION PREFERENCE FRAGMENT ========================
	/**
	 * This fragment shows notification preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class NotificationPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_notification2);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		}
	}
	//=============================== PASSWORD FRAGMENT ========================
	/**
	 * This fragment shows data and sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class PasswordFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_password);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("choice_using_pass"));
			bindPreferenceSummaryToValue(findPreference("user_pass"));
		}
	}
	//=============================== PREFERENCE FRAGMENT ========================
	/**
	 * This fragment shows data and sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class DataSyncPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_background);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("selected_background"));
		}
	}
	//=========================================================================
}
