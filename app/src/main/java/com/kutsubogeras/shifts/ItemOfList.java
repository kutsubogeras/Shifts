package com.kutsubogeras.shifts;
/*
 *
 * 
 */

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

public class ItemOfList {
	
	 //Μεταβλητές
	 private View   baseView;	
	 private LinearLayout firstLineLayout;
	 private LinearLayout secLineLayout;
	 private LinearLayout thirdLineLayout;
	 private ImageView imaRowNumber;
	 private EditText  myDate;
	 private EditText  myWeekDay;
	 private EditText  myShift; 
	 //private TextView  leftSpace;
	 private CheckBox  mySunday;
	 private CheckBox  myHoliday;
	 private TextView  myHolidayName;
	 private EditText  myComens; 
	 
     //constractor
     public ItemOfList (View baseView) {
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
     // Η 2η γραμμή LinearLayout με τα 2 checkBox
     public LinearLayout getSecondRowLayout (int resource) {
         if (secLineLayout == null ) {
        	 secLineLayout = (LinearLayout) baseView.findViewById(R.id.LinearLayout_Row2_ShiftList);
        	 secLineLayout.setPadding(2, 2, 2, 2);       	
         }
         return secLineLayout;
     }
     // Η 3η γραμμή LinearLayout για τα συστατικά με τα σχόλια
     public LinearLayout getThirdRowLayout (int resource) {
         if (thirdLineLayout == null ) {
        	 thirdLineLayout = (LinearLayout) baseView.findViewById(R.id.LinearLayout_Row3_ShiftList);
        	 thirdLineLayout.setPadding(2, 2, 2, 2);       	 
         }
         return thirdLineLayout;
     }
   //Δημιουργεί ένα ImageView για την αρίθμηση γραμμής
     public ImageView getRowNumberImage(int resource) {
         if (imaRowNumber == null ) {
        	 imaRowNumber = (ImageView) baseView.findViewById(R.id.ImageView_RowNumberShiftList);
        	 imaRowNumber.setMinimumHeight(25);
        	 imaRowNumber.setMinimumWidth(25);
         }
         return imaRowNumber;
     }
     //Δημιουργεί ένα editText για την Ημερομηνία
     public EditText getDateOfShift(int resource) {
         if (myDate == null ) {
        	 myDate = (EditText) baseView.findViewById(R.id.EditText_Date_ItemOfList);        	 
         }
         return myDate;
     }
     //Δημιουργεί ένα editText για την Ημέρα
     public EditText getWeekDayTxt (int resource) {
         if (myWeekDay == null ) {
        	 myWeekDay = (EditText) baseView.findViewById(R.id.EditText_Weekday_ItemOfList);        	 
        	 myWeekDay.setMinimumHeight(25);
        	 myWeekDay.setMinimumWidth(71); 
        	 myWeekDay.setMaxWidth(75);
        	 myWeekDay.setMaxHeight(25);       
         }
         return myWeekDay;
     }      
     //Δημιουργεί ένα editText για την Βάρδια
     public EditText getShiftTxt (int resource) {
         if (myShift == null ) {
        	 myShift = (EditText) baseView.findViewById(R.id.EditText_Shift_ItemOfList);
        	 myShift.setMinimumHeight(25);
        	 myShift.setMinimumWidth(71); 
        	 myShift.setMaxWidth(75);
        	 myShift.setMaxHeight(25);       
         }
         return myShift;
     } 
     /*
     //Δημιουργεί ένα TextView για το διάστημα αριστερά από το label Κυριακή
     public TextView getLeftSpaceTxt (int resource) {
         if (leftSpace == null ) {
        	 leftSpace = (TextView) baseView.findViewById(R.id.textView_leftSpace);       	        
         }
         return leftSpace;
     } 
     */
     //Δημιουργεί ένα CheckBox για την Κυριακή
     public CheckBox getSundayTxt(int resource) {
         if (mySunday == null ) {
        	 mySunday = (CheckBox) baseView.findViewById(R.id.checkBox_SundayShiftList);
        	 mySunday.setMinimumHeight(25);
        	 mySunday.setMinimumWidth(25);
         }
         return mySunday;
     }
     //Δημιουργεί ένα CheckBox για την Αργία
     public CheckBox getHoliDayTxt (int resource) {
         if (myHoliday == null ) {
        	 myHoliday = (CheckBox) baseView.findViewById(R.id.checkBox_HolidayShiftList);        	 
        	 myHoliday.setMinimumHeight(25);
        	 myHoliday.setMinimumWidth(25);         	      
         }
         return myHoliday;
     } 
   //Δημιουργεί ένα editText για τα Σχόλια
     public TextView getHolidayNameTxt (int resource) {
         if (myHolidayName == null ) {
        	 myHolidayName = (TextView) baseView.findViewById(R.id.TextView_HolidayName_ShiftList);        	 
         }
         return myHolidayName;
     }           
     //Δημιουργεί ένα editText για τα Σχόλια
     public EditText getComensTxt (int resource) {
         if (myComens == null ) {
        	 myComens = (EditText) baseView.findViewById(R.id.EditText_Comens_ShiftList);
        	 myComens.setMinimumHeight(25);
        	 myComens.setMinimumWidth(71); 
        	 myComens.setMaxWidth(75);
        	 myComens.setMaxHeight(25);       
         }
         return myComens;
     }           
     //=============================================================================
}//end class	