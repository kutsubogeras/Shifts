package com.kutsubogeras.shifts.ofwork;
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

public class CategoryOfWorkItemOfList {
	
	 //Μεταβλητές
	 private View      baseView;	
	 private ImageView imaRowNumber;
	 private LinearLayout layoutCategoryTotal;
	 private TextView  txtCategory;	 
	 private TextView  txtDuration;	 
	 private TextView  txtTotalDays;
     //constractor
     public CategoryOfWorkItemOfList (View baseView) {
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
        	 imaRowNumber = (ImageView) baseView.findViewById(
        			                    R.id.imageView_OfWorkCategoryItem_RowNum);        	 
         }
         return imaRowNumber;
     }
     //Δημιουργεί ένα textView για την Ονομασία της κατηγορίας αδείας
     public TextView getCategoryTextView(int resource) {
         if (txtCategory == null ) {
        	 txtCategory = (TextView) baseView.findViewById(
        			                  R.id.textView_OfWorkCategoryItem_Category);        	  
         }
         return txtCategory;
     }    
     //Δημιουργεί ένα textView για την προβολή του πλήθους ημερών αδείας
     //που πήρε για αυτή την κατηγορία
     public TextView getDurationTextView(int resource) {
         if (txtDuration == null ) {
        	 txtDuration = (TextView) baseView.findViewById(
        			                  R.id.textView_OfWorkCategoryItem_DaysTake);        	  
         }
         return txtDuration;
     } 
     //Δημιουργεί ένα LinearLayout για την προβολή του πλήθους ημερών που
     //δικαιούτε για αυτή την κατηγορία αδείας
     public LinearLayout getTotalDaysLayout(int resource) {
         if (layoutCategoryTotal == null ) {
        	 layoutCategoryTotal = (LinearLayout) baseView.findViewById(
        			                              R.id.LinearLayout_OfWorkCategoryItem_Days);        	  
         }
         return layoutCategoryTotal;
     }    
     //Δημιουργεί ένα textView για το πλήθος ημερών αδείας για αυτή την κατηγορία αδειας
     public TextView getTotalDaysTextView(int resource) {
         if (txtTotalDays == null ) {
        	 txtTotalDays = (TextView) baseView.findViewById(
        			                   R.id.textView_OfWorkCategoryItem_TotalDays);        	  
         }
         return txtTotalDays;
     }    
     //=============================================================================
}//end class	