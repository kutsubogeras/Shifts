package com.kutsubogeras.shifts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
public class ShiftListAdapter extends ArrayAdapter{
	
    public ShiftListAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}
	//Metavlites
	private Context myContext;	 
    private LayoutInflater myLayoutInflater; 
    private int layoutId;
	private ArrayList<ShiftOfUser> userShifts = new ArrayList<ShiftOfUser>(); 	
	private ArrayList<Integer> rowPointers;  // ο πίνακας με τις εικόνες αρίθμησης των γραμμών
	private int listSize = 0;
	private Calendar curDate; 
	private int month =0;
	private int day   =0;
	private int year  =0;
	//consractor
	public ShiftListAdapter(Context context, int resourceId, ArrayList<ShiftOfUser> shifts) {
           super( context, resourceId, shifts );
		   myContext  = context; 
		   layoutId   = resourceId;
		   userShifts = shifts;
		   listSize   = shifts.size()-1; // 
		   myLayoutInflater = (LayoutInflater) myContext.getSystemService(
        		                               Context.LAYOUT_INFLATER_SERVICE);
		   this.setDrawableRowPointers(); //θέτει σε πίνακα τις εικόνες αρίθμησης των γραμμών
		   curDate = Calendar.getInstance();
		   day  = curDate.get(Calendar.DAY_OF_MONTH);
		   month= curDate.get(Calendar.MONTH);
		   year = curDate.get(Calendar.YEAR);
		   curDate.clear();
		   curDate.set(year, month, day);
           //myLayoutInflater = LayoutInflater.from( context); 		   
    }//end constactor   
	
	
	//------------------------------------------------------------------------------------	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userShifts.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return userShifts.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}	
	
	@Override
    public View getView ( int position, View convertView, ViewGroup parent ) {         
		  //SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());          
		  ShiftOfUser shift = (ShiftOfUser)getItem(position); //η shift: είναι τύπου  ShiftOfUser        
          ItemOfList viewCache; //η κλάση PoiItemOfList δημιουργεί γραμμές με EditText για τη λίστα
          if (convertView == null ) {
              convertView = (LinearLayout) myLayoutInflater.inflate(layoutId, null );
        	  viewCache   = new ItemOfList(convertView);  //Object Of PoiItemOfList Class
              convertView.setTag(viewCache);            
          }
          else {
              convertView = (LinearLayout) convertView;        	   
              viewCache   = (ItemOfList) convertView.getTag();        	   
          } 
          
          //δημιουργεί ένα LinearLayout για την 1η γραμμή
          LinearLayout firstLine = viewCache.getFirstRowLayout(layoutId);
          
          //δημιουργεί ένα LinearLayout για την 2η γραμμή
          LinearLayout secLine = viewCache.getSecondRowLayout(layoutId);         
          
          //δημιουργεί ένα ImageView για την αρίθμηση γραμμής
          ImageView imaNumber = viewCache.getRowNumberImage(layoutId);
          if(position < rowPointers.size())
             imaNumber.setImageResource(rowPointers.get(position)); 
          else //αν υπερβεί τις 20 βάρδιες (δεν συμβαίνει ποτέ εδώ)
        	 imaNumber.setImageResource(R.drawable.ima_star); //εμφανίζει ως δείκτη αρίθμησης το αστέρι
            Log.i("SHIFT_TABLE_SIZE", ""+this.getCount());
          //δημιουργεί ένα editText με την ημερομηνία
          EditText txtDate = viewCache.getDateOfShift(layoutId);
          txtDate.setText(shift.getDate());
          
          //δημιουργεί ένα editText με την Ημέρα
          EditText txtWeekDay = viewCache.getWeekDayTxt(layoutId);
          txtWeekDay.setText(shift.getWeekDay());
          
          //δημιουργεί ένα editText με την Βάρδια
          EditText txtShift = viewCache.getShiftTxt(layoutId);
          if(shift.getShift().startsWith("ΡΕ")|| /*αν είναι ΡΕΠΟ*/
        	 shift.getShift().startsWith("ΑΔ")|| /*αν είναι ΑΔΕΙΑ*/
        	 shift.getShift().startsWith("ΑΡ"))  //αν είναι ΑΡΓΙΑ*/
         	 txtShift.setBackgroundResource(R.drawable.textfield_background_green_for_shift_list_value);
          else if(shift.getShift().startsWith("0")) //αν είναι απογευματινή βάρδια
         	 txtShift.setBackgroundResource(R.drawable.textfield_background_blue_for_shift_list_value);          
          else if(shift.getShift().startsWith("1")) //αν είναι απογευματινή βάρδια
        	 txtShift.setBackgroundResource(R.drawable.textfield_background_yellow_for_shift_list_value);          
          else if(shift.getShift().startsWith("2")) //αν είναι βραδινή βάρδια
         	 txtShift.setBackgroundResource(R.drawable.textfield_background_orange_for_shift_list_value);
          else   //σε κάθε άλλη περίπτωση
          	 txtShift.setBackgroundResource(R.drawable.textfield_background_blue_for_shift_list_value);  
          txtShift.setText(shift.getShift());        
          
          //δημιουργεί ένα CheckBox για την Κυριακή
          CheckBox txtSunday = viewCache.getSundayTxt(layoutId);
          if(shift.getShiftSunday()==1)
             txtSunday.setChecked(true);
          else
        	 txtSunday.setChecked(false);
          
          //δημιουργεί ένα CheckBox για την Αργία
          CheckBox txtHoliday = viewCache.getHoliDayTxt(layoutId);
          if(shift.getShiftHoliday()==1)
             txtHoliday.setChecked(true);
          else
        	 txtHoliday.setChecked(false);
          
          //δημιουργεί ένα TextView για ρύθμιση του αριστερού διαστήματος από την Κυριακή
          //TextView txtLeftSpace = viewCache.getLeftSpaceTxt(layoutId);
          
          //δημιουργεί ένα TextView για το όνομα αργίας holidayName
          TextView txtHolName = viewCache.getHolidayNameTxt(layoutId);
          String hol_name = shift.getShiftHolidayName();
          if(hol_name != null && !hol_name.equals("") ){ //αν υπάρχει όνομα Αργίας
        	 //txtLeftSpace.setVisibility(View.GONE);
        	 txtHolName.setText(hol_name);
        	 txtHolName.setVisibility(View.VISIBLE); //αν υπάρχουν σχόλια εμφανίζει τη γραμμή         	 
          }
          else{        	 
        	 //txtLeftSpace.setVisibility(View.VISIBLE);
        	 txtHolName.setVisibility(View.INVISIBLE);        	 
          }
          //δημιουργεί ένα editText με τα Σχόλια
          LinearLayout comensLine = viewCache.getThirdRowLayout(layoutId);
          EditText txtComens = viewCache.getComensTxt(layoutId);
          String com = shift.getComents();
          if(com != null && !com.equals("")){ //αν υπάρχουν σχόλια για τη βάρδια
            txtComens.setText(com);
            comensLine.setVisibility(View.VISIBLE); //αν υπάρχουν σχόλια εμφανίζει τη γραμμή          
          }  
          else
        	  comensLine.setVisibility(View.GONE);
          //δημιουργεί εναλλαγές στο background color κάθε γραμμής της λίστας 
          if((position % 2) == 1)        	  
        	  //convertView.setBackgroundResource(R.drawable.shift_unfocus_ok_button);
        	  convertView.setBackgroundResource(R.drawable.background_for_shift_list_item_odd_lines);       	 
          else
        	  convertView.setBackgroundResource(R.drawable.background_for_shift_list_item_even_lines);           
          if(curDate.getTimeInMillis() == shift.getDateInMilisec() ) //αν είναι η σημερινή Ημερομηνία
        	  convertView.setBackgroundResource(R.drawable.selected_item_background);
	   return convertView;
    }//end getView()
	 
	public void addItem(ShiftOfUser item, int pos) {
		userShifts.add(pos, item);
	}	
	public void removeItem(int pos) { 
		userShifts.remove(pos);
	}	
	public void removeAllItems() { 
		userShifts.clear();
	}		
	 //==============================SET POINTER ICON RESOURCE===================================
	  //καλείται μέσα στο σώμα του δημιουργού της κλάσης
	  private void setDrawableRowPointers(){
	  	   //δημιουργεί ένα ArrayList με Drawable αντικείμενα
	  	  rowPointers = new ArrayList<Integer>();
	  	  rowPointers.add(R.drawable.ima1);
	  	  rowPointers.add(R.drawable.ima2);
	  	  rowPointers.add(R.drawable.ima3);
	  	  rowPointers.add(R.drawable.ima4);
	  	  rowPointers.add(R.drawable.ima5);
	  	  rowPointers.add(R.drawable.ima6);
	  	  rowPointers.add(R.drawable.ima7);
	  	  rowPointers.add(R.drawable.ima8);
	  	  rowPointers.add(R.drawable.ima9);
	  	  rowPointers.add(R.drawable.ima10);
	  	  rowPointers.add(R.drawable.ima11);
	  	  rowPointers.add(R.drawable.ima12);
	  	  rowPointers.add(R.drawable.ima13);
	  	  rowPointers.add(R.drawable.ima14);
	  	  rowPointers.add(R.drawable.ima15);
	  	  rowPointers.add(R.drawable.ima16);
	  	  rowPointers.add(R.drawable.ima17);
	  	  rowPointers.add(R.drawable.ima18);
	  	  rowPointers.add(R.drawable.ima19);
	  	  rowPointers.add(R.drawable.ima20);
	  }//end method

}//end class
