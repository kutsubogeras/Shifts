package com.kutsubogeras.shifts.queries;
/*
 *
 * 
 */

import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.R.id;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
/*
 * Η κλάση αυτή δημιουργεί αντικείμενα τύπου EditText για κάθε γραμμή της λίστας
 * που προβάλει τις τελευταίες βάρδιες του εργαζόμενου */

public class ResultsItemOfList {
	
	 //Μεταβλητές
	 private View      baseView;	
	 private ImageView imaRowNumber;
	 private LinearLayout layoutHoliName;
	 private TextView  txtDame;	 
	 private TextView  txtWeekday;	 
	 private TextView  txtShift;
	 private TextView  txtHolName;
     //constractor
     public ResultsItemOfList (View baseView) {
           this.baseView = baseView;
           baseView.setMinimumHeight(100);           
     }
//---------------------------------------------------------------------------------
     public View getViewBase () {    	
           return baseView;           
     }
         
     //Δημιουργεί ένα ImageView για την αρίθμηση γραμμής
     public ImageView getRowNumberImage(int resource) {
         if (imaRowNumber == null ) {
        	 imaRowNumber = (ImageView) baseView.findViewById(R.id.imageView_ItemResult_RowNumber);
        	 imaRowNumber.setMinimumHeight(25);
        	 imaRowNumber.setMinimumWidth(25);
         }
         return imaRowNumber;
     }
     //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getDateTextView(int resource) {
         if (txtDame == null ) {
        	 txtDame = (TextView) baseView.findViewById(R.id.TextView_Label_TimeFinish);        	  
         }
         return txtDame;
     }    
   //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getWeekdayTextView(int resource) {
         if (txtWeekday == null ) {
        	 txtWeekday = (TextView) baseView.findViewById(R.id.textView_ItemResult_WeekDay);        	  
         }
         return txtWeekday;
     }    
   //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getShiftValueTextView(int resource) {
         if (txtShift == null ) {
        	 txtShift = (TextView) baseView.findViewById(R.id.TextView_Time_Finish_Dialog);        	  
         }
         return txtShift;
     } 
   //Δημιουργεί ένα textView για την Ημερομηνία
     public LinearLayout getHolidayNameLayout(int resource) {
         if (layoutHoliName == null ) {
        	 layoutHoliName = (LinearLayout) baseView.findViewById(R.id.LinearLayout_Item_results_holiday);        	  
         }
         return layoutHoliName;
     }    
   //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getHolidayNameTextView(int resource) {
         if (txtHolName == null ) {
        	 txtHolName = (TextView) baseView.findViewById(R.id.textView_Item_results_HolidayName);        	  
         }
         return txtHolName;
     }    
     //=============================================================================
}//end class	