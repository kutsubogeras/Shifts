package com.kutsubogeras.shifts.queries;
/*
 *
 * 
 */

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kutsubogeras.shifts.R;
/*
 * Η κλάση αυτή δημιουργεί αντικείμενα τύπου EditText για κάθε γραμμή της λίστας
 * που προβάλει τις τελευταίες βάρδιες του εργαζόμενου */

public class QueryItemOfList {
	
	 //Μεταβλητές
	 private View   baseView;	
	 private LinearLayout firstLineLayout;	 
	 private ImageView  imaRowNumber;
	 private TextView  queryName;	 
	 private ImageView imaLink; 
	 
     //constractor
     public QueryItemOfList (View baseView) {
           this.baseView = baseView;
           baseView.setMinimumHeight(100);           
     }
//---------------------------------------------------------------------------------
     public View getViewBase () {    	
           return baseView;           
     }
     // Η 1η γραμμή LinearLayout με τα 3 editText (Ημερομηνία-Ημέρα-Βάρδια)
     public LinearLayout getFirstRowLayout (int resource) {
         if (firstLineLayout == null ) {
        	 firstLineLayout = (LinearLayout) baseView.findViewById(R.id.LinearLayout_Row2_Dialog);
        	 firstLineLayout.setPadding(2, 2, 2, 2);
         }
         return firstLineLayout;
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
     //Δημιουργεί ένα editText για την Ημερομηνία
     public TextView getQueryName(int resource) {
         if (queryName == null ) {
        	 queryName = (TextView) baseView.findViewById(R.id.TextView_Time_Finish_Dialog);        	  
         }
         return queryName;
     }    
     //Δημιουργεί ένα ImageView για τo link γραμμής
     public ImageView getRowLinkNumberImage(int resource) {
         if (imaLink == null ) {
        	 imaLink = (ImageView) baseView.findViewById(R.id.imageView_QueryLink_QueryList);
        	 imaLink.setMinimumHeight(25);
        	 imaLink.setMinimumWidth(25);
         }
         return imaLink;
     }
     //=============================================================================
}//end class	