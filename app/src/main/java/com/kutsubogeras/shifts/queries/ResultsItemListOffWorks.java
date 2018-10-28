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

public class ResultsItemListOffWorks {
	
	 //Μεταβλητές
	 private View      baseView;	
	 private ImageView imaRowNumber;
	 private LinearLayout layoutOffWork;
	 private TextView  txtFrom;	 
	 private TextView  txtTo;	 
	 private TextView  txtDays;
	 private TextView  txtType;
     //constractor
     public ResultsItemListOffWorks (View baseView) {
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
        	 imaRowNumber = (ImageView) baseView.findViewById(R.id.imageView_ImaNum);
        	 imaRowNumber.setMinimumHeight(25);
        	 imaRowNumber.setMinimumWidth(25);
         }
         return imaRowNumber;
     }
     //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getStartDate(int resource) {
         if (txtFrom == null ) {
        	 txtFrom = (TextView) baseView.findViewById(R.id.textView_offwork_startDate);        	  
         }
         return txtFrom;
     }    
   //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getEndDate(int resource) {
         if (txtTo == null ) {
        	 txtTo = (TextView) baseView.findViewById(R.id.textView_offwork_endDate);        	  
         }
         return txtTo;
     }    
   //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getTotalDays(int resource) {
         if (txtDays == null ) {
        	 txtDays = (TextView) baseView.findViewById(R.id.textView_offwork_days);        	  
         }
         return txtDays;
     } 
   //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getOffWorkType(int resource) {
         if (txtType == null ) {
        	 txtType = (TextView) baseView.findViewById(R.id.textView_offwork_type);        	  
         }
         return txtType;
     }    
     //=============================================================================
}//end class	