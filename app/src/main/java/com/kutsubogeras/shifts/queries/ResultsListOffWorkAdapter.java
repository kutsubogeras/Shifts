package com.kutsubogeras.shifts.queries;

import java.util.ArrayList;

import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.ShiftOfUser;
import com.kutsubogeras.shifts.R.drawable;
import com.kutsubogeras.shifts.ofwork.OffWork;

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
public class ResultsListOffWorkAdapter extends ArrayAdapter{
	
    public ResultsListOffWorkAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}
	//Metavlites
	private Context myContext;	 
    private LayoutInflater myLayoutInflater; 
    private int layoutId;
	private ArrayList<OffWork> userOffWorks = new ArrayList<OffWork>(); 	
	private ArrayList<Integer> rowPointers;  // ο πίνακας με τις εικόνες αρίθμησης των γραμμών
	private int listSize = 0;
	
	//consractor
	public ResultsListOffWorkAdapter(Context context, int resourceId, ArrayList<OffWork> offworks) {
           super( context, resourceId, offworks );
		   myContext  = context; 
		   layoutId   = resourceId;
		   userOffWorks = offworks;
		   listSize   = offworks.size()-1; // 
		   myLayoutInflater = (LayoutInflater) myContext.getSystemService(
        		                               Context.LAYOUT_INFLATER_SERVICE);
		   setDrawableRowPointers(); //θέτει σε πίνακα τις εικόνες αρίθμησης των γραμμών
           //myLayoutInflater = LayoutInflater.from( context); 		   
    }//end constactor   
	
	
	//------------------------------------------------------------------------------------	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userOffWorks.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return userOffWorks.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}	
	
	@Override
    public View getView ( int position, View convertView, ViewGroup parent ) {         
		  
		  OffWork curOffWork = (OffWork)getItem(position); //η shift: είναι τύπου  ShiftOfUser        
		  ResultsItemListOffWorks viewCache; //η κλάση PoiItemOfList δημιουργεί γραμμές με EditText για τη λίστα
          if (convertView == null ) {
              convertView = (LinearLayout) myLayoutInflater.inflate(layoutId, null );
        	  viewCache   = new ResultsItemListOffWorks(convertView);  //Object Of PoiItemOfList Class
              convertView.setTag(viewCache);            
          }
          else {
              convertView = (LinearLayout) convertView;        	   
              viewCache   = (ResultsItemListOffWorks) convertView.getTag();        	   
          }          
          //δημιουργεί ένα ImageView για την αρίθμηση γραμμής
          ImageView imaNumber = viewCache.getRowNumberImage(layoutId);
          if(position >= rowPointers.size())
        	 imaNumber.setImageResource(R.drawable.num_star);
          else  
             imaNumber.setImageResource(rowPointers.get(position)); 
          
          //δημιουργεί ένα editText με την Ημερομηνία έναρξης
          TextView txtDateFrom = viewCache.getStartDate(layoutId);
          txtDateFrom.setText(curOffWork.getStartDate());
          
          //δημιουργεί ένα editText με την Ημερομηνία λήξης
          TextView txtDateTo = viewCache.getEndDate(layoutId);
          txtDateTo.setText(curOffWork.getEndDate());          
         
          //δημιουργεί ένα editText με το πλήθος ημερών αδείας
          TextView txtTotal = viewCache.getTotalDays(layoutId);
          txtTotal.setText(""+curOffWork.getDuration()); 
          
          //δημιουργεί ένα editText με το πλήθος ημερών αδείας
          TextView txtType = viewCache.getOffWorkType(layoutId);
          txtType.setText(curOffWork.getOfWorkCategory());
          
          //δημιουργεί εναλλαγές στο background color κάθε γραμμής της λίστας 
          if((position % 2) == 1)
        	  convertView.setBackgroundResource(R.drawable.list_odd_row_background);       	 
          else
        	  convertView.setBackgroundResource(R.drawable.list_even_row_background);       	     
          
	   return convertView;
    }//end getView()
	 
	public void addItem(OffWork item, int pos) {
		userOffWorks.add(pos, item);
	}	
	public void removeItem(int pos) { 
		userOffWorks.remove(pos);
	}	
	public void removeAllItems() { 
		userOffWorks.clear();
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
	  	  
	  }//end method

}//end class
