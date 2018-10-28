package com.kutsubogeras.shifts.help;


import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.R.id;
import com.kutsubogeras.shifts.R.layout;
import com.kutsubogeras.shifts.R.menu;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Build;

public class DeveloperInfo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_developer_info);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container_developer, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.developer_info, menu);
		return true;
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
		//μεταβλητές κλάσης
        private TextView txtInfo;
        private TextView txtInfoFace;
        private ImageView facebook_ima;
        private String information;
        private String informationFace;
        private String uml_twitter  ="https://twitter.com/KutsubogerasT";
        private String uml_facebook ="https://www.facebook.com/kutsubothan";
        
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_developer_info,
					container, false);
			txtInfo = (TextView)rootView.findViewById(R.id.textView_DeveloperInfo_Info);
			txtInfoFace = (TextView)rootView.findViewById(R.id.textView_DeveloperInfo_facebook);
			facebook_ima= (ImageView)rootView.findViewById(R.id.ImageView_facebook);
			setInformation(); //εμφανίζει πληροφορίες για τον προγραμματιστή
			return rootView;
		}
		private void setInformation(){
			information = " Αυτή η Εφαρμογή Δημιουργήθηκε από τον : Κουτσουμπόγερα Θανάση.\n"
					+ "Για οποιοδήποτε πρόβλημα ή αίτημά σας μπορείται να με βρείτε στα παρακάτω e-mails:\n"
					+ "1. kutsubogeras@yahoo.gr \n\n"
					+ "2. kutsuthanos@gmail.com \n\n";
			informationFace = "Μπορείτε επίσης να με βρείτε στη σελίδα μου στο facebook: ";
			txtInfo.setText(information);
			txtInfoFace.setText(informationFace);
			facebook_ima.setOnClickListener(new View.OnClickListener() {       		
		             public void onClick(View v) {       		     
		       		      //δημιουργεί ένα Intent για την επιλογή Ημερομηνίας Έναρξης
		            	 Intent browser = new Intent(getActivity() , DisplayWebInfo.class);
		       		     browser.setData(Uri.parse(uml_facebook));
		       		     startActivityForResult(browser,0); 		            	 
		             }
		       });
		}
	}

}
