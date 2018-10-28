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

public class ReportRepoItemOfList {
	
	 //Μεταβλητές
	 private View      baseView;	
	 private ImageView imaRowNumber;
	 private TextView  txtStartDate;	 
	 private TextView  txtEndDate;	 
	 private TextView  txtSumDays;
     //constractor
     public ReportRepoItemOfList (View baseView) {
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
        	 imaRowNumber = (ImageView) baseView.findViewById(R.id.imageView_ItemReportRepo_ImaNum);
        	 imaRowNumber.setMinimumHeight(25);
        	 imaRowNumber.setMinimumWidth(25);
         }
         return imaRowNumber;
     }
     //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getStartDateTextView(int resource) {
         if (txtStartDate == null ) {
        	 txtStartDate = (TextView) baseView.findViewById(R.id.textView_ItemReportRepo_StartDate);        	  
         }
         return txtStartDate;
     }    
   //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getEndDateTextView(int resource) {
         if (txtEndDate == null ) {
        	 txtEndDate = (TextView) baseView.findViewById(R.id.textView_ItemReportRepo_EndDate);        	  
         }
         return txtEndDate;
     }    
   //Δημιουργεί ένα textView για την Ημερομηνία
     public TextView getDesciptionRepoTextView(int resource) {
         if (txtSumDays == null ) {
        	 txtSumDays = (TextView) baseView.findViewById(R.id.textView_ItemReportRepo_Descript);        	  
         }
         return txtSumDays;
     } 
  
     //=============================================================================
}//end class	