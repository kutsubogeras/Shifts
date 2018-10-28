package com.kutsubogeras.shifts.queries;

import java.util.ArrayList;

import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.R.color;
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
public class QueryListAdapter extends ArrayAdapter{
	
    public QueryListAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}
	//Metavlites
	private Context myContext;	 
    private LayoutInflater myLayoutInflater; 
    private int layoutId;
	private ArrayList<String> userQueries = new ArrayList<String>(); 	
	private ArrayList<Integer> rowPointers;  // ο πίνακας με τις εικόνες αρίθμησης των γραμμών
	private int listSize = 0;
	
	//consractor
	public QueryListAdapter(Context context, int resourceId, ArrayList<String> queries) {
           super( context, resourceId, queries );
		   myContext  = context; 
		   layoutId   = resourceId;
		   userQueries= queries;
		   listSize   = queries.size()-1; // 
		   myLayoutInflater = (LayoutInflater) myContext.getSystemService(
        		                               Context.LAYOUT_INFLATER_SERVICE);
		   setDrawableRowPointers(); //θέτει σε πίνακα τις εικόνες αρίθμησης των γραμμών
           //myLayoutInflater = LayoutInflater.from( context); 		   
    }//end constactor   
	
	
	//------------------------------------------------------------------------------------	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userQueries.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return userQueries.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}	
	
	@Override
    public View getView ( int position, View convertView, ViewGroup parent ) {         
		  
          String queryName = (String)getItem(position); //η shift: είναι τύπου  ShiftOfUser        
          QueryItemOfList viewCache; //η κλάση PoiItemOfList δημιουργεί γραμμές με EditText για τη λίστα
          if (convertView == null ) {
              convertView = (LinearLayout) myLayoutInflater.inflate(layoutId, null );
        	  viewCache   = new QueryItemOfList(convertView);  //Object Of PoiItemOfList Class
              convertView.setTag(viewCache);            
          }
          else {
              convertView = (LinearLayout) convertView;        	   
              viewCache   = (QueryItemOfList) convertView.getTag();        	   
          }          
          //δημιουργεί ένα ImageView για την αρίθμηση γραμμής
          ImageView imaNumber = viewCache.getRowNumberImage(layoutId);
          imaNumber.setImageResource(rowPointers.get(position)); 
          
            //Log.i("SHIFT_TABLE_SIZE", ""+this.getCount());
          //δημιουργεί ένα editText με το όνομα Ερωτήματος
          TextView txtQuery = viewCache.getQueryName(layoutId);
          txtQuery.setText(queryName);
          
          //δημιουργεί ένα ImageView για τo link γραμμής
          ImageView imaLink = viewCache.getRowLinkNumberImage(layoutId);
          //imaLink.setImageResource(rowPointers.get(position)); 
          
          //δημιουργεί εναλλαγές στο background color κάθε γραμμής της λίστας 
          if((position % 2) == 1)
        	  convertView.setBackgroundColor(myContext.getResources().getColor(R.color.Color_light_gray));        	 
          else
        	  convertView.setBackgroundColor(Color.WHITE);            
          
	   return convertView;
    }//end getView()
	 
	public void addItem(String item, int pos) {
		userQueries.add(pos, item);
	}	
	public void removeItem(int pos) { 
		userQueries.remove(pos);
	}	
	public void removeAllItems() { 
		userQueries.clear();
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
	  }//end method

}//end class
