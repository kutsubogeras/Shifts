package com.kutsubogeras.shifts.queries;


import java.util.ArrayList;

import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.ShiftListAdapter;
import com.kutsubogeras.shifts.ShiftOfUser;
import com.kutsubogeras.shifts.R.id;
import com.kutsubogeras.shifts.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;



public class DisplayLastShifts extends Fragment {	
		
		public static final String ARG_SECTION_NUMBER = "section_number";
        private ArrayList<ShiftOfUser> listOfShifts;        
        
        //constractor
		public DisplayLastShifts() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			View rootView = inflater.inflate(R.layout.fragment_display_last_shifts, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label3);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));			
				
		//----------------------LIST VIEW------------------------
		//δημιουργία ListView και adapter για χειρισμό της λίστας	
		//lv = this.getListView();
		ListView lv =(ListView)rootView.findViewById(R.id.myListView);		
		ShiftListAdapter adapter = new ShiftListAdapter(getActivity(), 
				                      R.layout.activity_item_of_list_shifts , listOfShifts );
		lv.setAdapter(adapter); 
        lv.setTextFilterEnabled(true);
        lv.setSoundEffectsEnabled(true);
        // lv.setClickable(true);
        // Listener 
        lv.setOnItemClickListener(new OnItemClickListener (){
        	@Override
            public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {        		
        		String infoMess = "You are clicking on item list :"+(pos+1);                
        		Toast tost = Toast.makeText(getActivity(), infoMess, Toast.LENGTH_SHORT); 
                tost.setGravity(Gravity.BOTTOM, tost.getXOffset() / 2, tost.getYOffset()*2 );    
                tost.show();                                
                //callNewActivity(pos); //����� ��� activity ��� ��������� ������� ��� POI              
            }//end method
       });
       return rootView;
	}//end onCreate
	
}//end class
