package com.kutsubogeras.shifts.queries;

import java.util.ArrayList;

import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.R.drawable;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/*
 * 
 */
public class ReportOfRepoAdapter extends ArrayAdapter{
	
    public ReportOfRepoAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}
	//Metavlites
	private Context myContext;	 
    private LayoutInflater myLayoutInflater; 
    private int layoutId;
	private ArrayList<ExtendedRepo> multiDaysRepo = new ArrayList<ExtendedRepo>(); 	
	private ArrayList<Integer> rowPointers;  // ο πίνακας με τις εικόνες αρίθμησης των γραμμών
	private int listSize = 0;
	
	//consractor
	public ReportOfRepoAdapter(Context context, int resourceId, ArrayList<ExtendedRepo> multiRepo) {
           super( context, resourceId, multiRepo );
		   myContext    = context; 
		   layoutId     = resourceId;
		   multiDaysRepo= multiRepo;
		   listSize     = multiRepo.size()-1; // 
		   myLayoutInflater = (LayoutInflater) myContext.getSystemService(
        		                               Context.LAYOUT_INFLATER_SERVICE);
		   setDrawableRowPointers(); //θέτει σε πίνακα τις εικόνες αρίθμησης των γραμμών
           //myLayoutInflater = LayoutInflater.from( context); 		   
    }//end constactor   
	
	
	//------------------------------------------------------------------------------------	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return  multiDaysRepo.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return  multiDaysRepo.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}	
	
	@Override
    public View getView ( int position, View convertView, ViewGroup parent ) {         
		  
          ExtendedRepo extended = (ExtendedRepo)getItem(position); //η shift: είναι τύπου  ShiftOfUser        
          ReportRepoItemOfList viewCache; //η κλάση PoiItemOfList δημιουργεί γραμμές με EditText για τη λίστα
          if (convertView == null ) {
              convertView = (LinearLayout) myLayoutInflater.inflate(layoutId, null );
        	  viewCache   = new ReportRepoItemOfList(convertView);  //Object Of PoiItemOfList Class
              convertView.setTag(viewCache);            
          }
          else {
              convertView = (LinearLayout) convertView;        	   
              viewCache   = (ReportRepoItemOfList) convertView.getTag();        	   
          }          
          //δημιουργεί ένα ImageView για την αρίθμηση γραμμής
          ImageView imaNumber = viewCache.getRowNumberImage(layoutId);
          if(position >= rowPointers.size())
         	 imaNumber.setImageResource(R.drawable.num_star);
           else  
             imaNumber.setImageResource(rowPointers.get(position));           
            
          //δημιουργεί ένα editText με την Ημερομηνία Έναρξης του ΡΕΠΟ
          TextView txtStartDate = viewCache.getStartDateTextView(layoutId);
          txtStartDate.setText(extended.getStartDate());
          
          //δημιουργεί ένα editText με την Ημερομηνία Λήξης του ΡΕΠΟ
          TextView txtEndDate = viewCache.getEndDateTextView(layoutId);
          txtEndDate.setText(extended.getEndDate());
          
          //δημιουργεί ένα editText με το την περιγραφή του Πολυήμερου ΡΕΠΟ
          TextView txtDescription = viewCache.getDesciptionRepoTextView(layoutId);
          int multiDays = extended.getTotalDays();
          String description="-ήμερο";
          txtDescription.setText(""+multiDays + description);
          
          //δημιουργεί εναλλαγές στο background color κάθε γραμμής της λίστας 
          if((position % 2) == 1)
        	  convertView.setBackgroundResource(R.drawable.list_odd_row_background);       	 
          else
        	  convertView.setBackgroundResource(R.drawable.list_even_row_background);             
          
	   return convertView;
    }//end getView()
	 
	public void addItem(ExtendedRepo item, int pos) {
		 multiDaysRepo.add(pos, item);
	}	
	public void removeItem(int pos) { 
		 multiDaysRepo.remove(pos);
	}	
	public void removeAllItems() { 
		 multiDaysRepo.clear();
	}		
	//==============================SET POINTER ICON RESOURCE===================================
	  //καλείται μέσα στο σώμα του δημιουργού της κλάσης
	  private void setDrawableRowPointers(){
	  	  //δημιουργεί ένα ArrayList με Drawable αντικείμενα
	  	  rowPointers = new ArrayList<Integer>();
	  	  rowPointers.add(R.drawable.num_1);
	  	  rowPointers.add(R.drawable.num_2);
	  	  rowPointers.add(R.drawable.num_3);
	  	  rowPointers.add(R.drawable.num_4);
	  	  rowPointers.add(R.drawable.num_5);
	  	  rowPointers.add(R.drawable.num_6);
	  	  rowPointers.add(R.drawable.num_7);
	  	  rowPointers.add(R.drawable.num_8);
	  	  rowPointers.add(R.drawable.num_9);
	  	  rowPointers.add(R.drawable.num_10);
	  	  rowPointers.add(R.drawable.num_11);
	  	  rowPointers.add(R.drawable.num_12);
	  	  rowPointers.add(R.drawable.num_13);
	  	  rowPointers.add(R.drawable.num_14);
	  	  rowPointers.add(R.drawable.num_15);
	  	  rowPointers.add(R.drawable.num_16);
	  	  rowPointers.add(R.drawable.num_17);
	  	  rowPointers.add(R.drawable.num_18);
	  	  rowPointers.add(R.drawable.num_19);
	  	  rowPointers.add(R.drawable.num_20);
	  	  rowPointers.add(R.drawable.num_21);
	  	  rowPointers.add(R.drawable.num_22);
	  	  rowPointers.add(R.drawable.num_23);
	  	  rowPointers.add(R.drawable.num_24);
	  	  rowPointers.add(R.drawable.num_25);
	  	  rowPointers.add(R.drawable.num_26);
	  	  rowPointers.add(R.drawable.num_27);
	  	  rowPointers.add(R.drawable.num_28);	
	  	  rowPointers.add(R.drawable.num_29);
	  	  rowPointers.add(R.drawable.num_30);
	  	  rowPointers.add(R.drawable.num_31);
	  	  rowPointers.add(R.drawable.num_32);
	  	  rowPointers.add(R.drawable.num_33);
	  	  rowPointers.add(R.drawable.num_34);
	  	  rowPointers.add(R.drawable.num_35);
	  	  rowPointers.add(R.drawable.num_36);
	  	  rowPointers.add(R.drawable.num_37);
	  	  rowPointers.add(R.drawable.num_38);
	  	  rowPointers.add(R.drawable.num_39);
	  	  rowPointers.add(R.drawable.num_40);
	  	  rowPointers.add(R.drawable.num_41);
	  	  rowPointers.add(R.drawable.num_42);
	  	  rowPointers.add(R.drawable.num_43);
	  	  rowPointers.add(R.drawable.num_44);
	  	  rowPointers.add(R.drawable.num_45);
	  	  rowPointers.add(R.drawable.num_46);
	  	  rowPointers.add(R.drawable.num_47);
	  	  rowPointers.add(R.drawable.num_48);
	  	  rowPointers.add(R.drawable.num_49);
	  	  rowPointers.add(R.drawable.num_50);
	  	  rowPointers.add(R.drawable.num_51);
	  	  rowPointers.add(R.drawable.num_52);
	  	  rowPointers.add(R.drawable.num_53);
	  	  rowPointers.add(R.drawable.num_54);
	  	  rowPointers.add(R.drawable.num_55);
	  	  rowPointers.add(R.drawable.num_56);
	  	  rowPointers.add(R.drawable.num_57);
	  	  rowPointers.add(R.drawable.num_58);	
	  	  rowPointers.add(R.drawable.num_59);
	  	  rowPointers.add(R.drawable.num_60);
	  }//end method

}//end class
