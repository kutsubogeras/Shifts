package com.kutsubogeras.shifts.data;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBControler {
	/*
	 * This class creates, the Data Base of application. 
	 * Moreover, it has been the methods that used to manipulate the DB. 
	 * 
	 */
	   //-------------- Variables Strings for tables and columns names -----------------
	   //create DataBase name 
	   private static final String DB_NAME    = "ShiftsDB";    //name DB
	   private static final int    DB_VERSION = 1;             //version DB
	  
	   //table names	  
	   private static final String EMPLOYEE   = "Employee";    //Εργαζόμενος
	   private static final String SHIFT      = "Shift";       //Βάρδια
	   private static final String PAID_LEAVE = "PaidLeave";   //Άδεια	   
	   private static final String CREDENTIALS = "Credentials";   //user-pass

	   //arguments of table EMPLOYEE
	   private static final String USER_CODE  = "user_code";   //κωδικός χρήστη
	   private static final String NAME       = "name";
	   private static final String LAST_NAME  = "last_name";
	   private static final String ADDRESS    = "address";
	   private static final String PHONE      = "phone";
	   private static final String MOBILE     = "mobile";
	   private static final String PROFESSION = "profession";  //επάγγελμα
	   private static final String COMPANY    = "company";     //εταιρεία
	   private static final String DEPARTMENT = "department";  //τομέας εργασίας 	   
	   private static final String GRADE      = "grade";       //βαθμός εργαζόμενου	   
	   private static final String PHOTO      = "photo";       //φωτογραφία εργαζόμενου
	   private static final String USER       = "is_user";     //είναι ο ίδιος ο χρήστης

	   //arguments of table SHIFT (ΒΑΡΔΙΕΣ)
	   private static final String EMPLOYEE_ID= "worked_id";   //κωδικός εργαζόμενου
	   private static final String DATE       = "shift_date";  //ημερομηνία
	   private static final String WEEKDAY    = "weekday";     //ημέρα εβδομάδος
	   private static final String TIME       = "work_time";   //ώρα εργασίας(βάρδια)
	   private static final String SUNDAY     = "sunday";      //ημέρα Κυριακή
	   private static final String HOLIDAY    = "holiday";     //επίσιμη αργία 
	   private static final String HOL_NAME   = "holiday_name";//ονομασία αργίας 
	   private static final String COMMENTS   = "comments";    //σχόλια
	   private static final String BUILDING   = "building";    //κτήριο	   
	   private static final String EXTRA_TIME = "extra_time";  //έξτρα χρόνος
	   
	   //arguments of table PAID_LEAVE(ΑΔΕΙΕΣ)
	   private  static final String WORKED_ID = "worked_id";   //ο κωδικός εργαζομένου
	   private  static final String START_DATE= "start_date";  //ημερομηνία έναρξης
	   private  static final String END_DATE  = "end_date";    //ημερομηνία λήξης
	   private  static final String DURATION  = "duration";    //διάρκεια άδειας
	   private  static final String CATEGORY  = "category";    //κατηγορία αδείας
	   private  static final String YEAR      = "year";        //έτος αδείας
	   private  static final String SUMDAYS   = "sum_days";    //ημέρες αδείας που δικαιούτε/έτος 
	   private  static final String LEAVECOMM = "comments";    //σχόλια

	   //arguments of table CREDENTIALS
	   private  static final String USER_NAME = "user_name";
	   private  static final String PASSWORD  = "password";

	   //------------------------ SQL Statements for CREATE TABLES ------------------------------	   
	   //δημιουργία του table: Employee
	   private static final String CREATE_TABLE_EMPLOYEE = 
		    "create table if not exists Employee (_id integer primary key autoincrement, "+
		    "user_code integer not null, name text not null, last_name text not null, "+
		    "address text not null, phone text not null, mobile text, "+
		    "profession text not null, company text not null, department text not null, "+
		    "grade text, photo text, is_user boolean default 0);";
	  
	   //δημιουργία του table: Shift
	   private static final String CREATE_TABLE_SHIFT = 
			"create table if not exists Shift (_id integer primary key autoincrement, "+
			"worked_id integer not null, shift_date date not null unique, weekday text not null, "+
			"work_time text not null, sunday boolean, holiday boolean, holiday_name text, " +
			"comments text, building text, extra_time integer default 0);";
	  
	   //δημιουργία του table: Paid_Leave
	   private static final String CREATE_TABLE_PAID_LEAVE = 
		    "create table if not exists PaidLeave (_id integer primary key autoincrement, "+
	        "worked_id integer not null, start_date date not null, end_date date not null, "+
	        "duration integer not null, category text not null, year integer not null, "+
	        "sum_days integer not null, comments text);";

	   //δημιουργία του table: Credentials
	   private static final String CREATE_TABLE_CREDENTIALS =
			"create table if not exists Credentials (_id integer primary key autoincrement, "+
			"user_name text not null, password text not null);";

	   //------------------------- class variables---------------------------
	   //Μεταβλητές Κλάσης
	   private final Context  context;
	   private SQLiteDatabase sqlDB;
	   private DataBaseHelper DBhelper;
	   
	   
	   //Constractor
	   public DBControler(Context con){
		     this.context = con;
		     DBhelper = new DataBaseHelper(context); //καλεί την inner class που υπάρχει στο τέλος
	   }
	   //------------------------- OPEN DB ---------------------------------
	   public void openDB(){
		   try{
			   sqlDB = SQLiteDatabase.openDatabase(
					   sqlDB.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		      }catch(SQLException e){
				  e.printStackTrace();
				  Log.e("DB_OPEN", "ERROR_WITH_OPEN_DB");
		   }
	   }
	   //=================== CLASS METHODS =====================
	   //------------------- OPEN DataBase ---------------------------
	   //anoigei ti vasi dedomenon
	   public DBControler open() throws SQLException{
		     sqlDB = DBhelper.getWritableDatabase();
		     return this;
	   }//end method
	   
	   //------------------- CLOSE DataBase --------------------------------
	   //kleinei ti vasi
	   public void closeDB(){
		     //DBhelper.close();
		     sqlDB.close();
	   }//end method
	   //==============INNER CLASS CREATE DADABASE========================
	   /*
	    *εσωτερική κλάση που δημιουργεί την βάση και τους πίνακες
	    *αν δεν υπάρχουν. 
	    *Καλείται στο σώμα της constractor μεθόδου της κλάσης: DBControler
	    */
	    private static class DataBaseHelper extends SQLiteOpenHelper{
		   
		   //Constractor
		   DataBaseHelper(Context cont){
			     super(cont, DB_NAME, null, DB_VERSION); //δημιουργεί τη ΒΔ
	       }
           //methods of class
		   @Override
		   public void onCreate(SQLiteDatabase db) {			
			     //Δημιουργία των πινάκων της βάσης δεδομένων
			     try{
				     db.execSQL(CREATE_TABLE_EMPLOYEE);
			        }catch(SQLException e){
					  e.printStackTrace();
					  Log.e("TABLE_EMPLOYEE", "ERROR_CREATE_TABLE");
			        }	
			     try{
			         db.execSQL(CREATE_TABLE_SHIFT);
			        }catch(SQLException e){
				      e.printStackTrace();
				      Log.e("TABLE_SHIFT", "ERROR_CREATE_TABLE");
			        }			
			     try{
			         db.execSQL(CREATE_TABLE_PAID_LEAVE);
			        }catch(SQLException e){
					  e.printStackTrace();
					  Log.e("TABLE_PAID_LEAVE", "ERROR_CREATE_TABLE");
			        }
			     try{
				     db.execSQL(CREATE_TABLE_CREDENTIALS);
			        }catch(SQLException e){
				      e.printStackTrace();
				      Log.e("TABLE_CREDENTIALS", "ERROR_CREATE_TABLE");
			        }
		   }//end method

		   @Override
		   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			     //αν έχει αλλάξει η version της βάσης
			     Log.w("DBControler", "Η Βάση Δεδομένων άλλαξε από έκδοση: "+oldVersion+
				       " σε έκδοση: "+newVersion+" Όλα τα δεδομένα Διαγράφηκαν");			
			     //διαγράφει τους υπάρχοντες πίνακες
			     db.execSQL("DROP TABLE IF EXIST Employee");			
			     db.execSQL("DROP TABLE IF EXIST Shift");			
			     db.execSQL("DROP TABLE IF EXIST PaidLeave");
			     db.execSQL("DROP TABLE IF EXIST Credentials");
			     onCreate(db); //ξαναδημιουργεί τη βάση
		   }//end method
		
	   }//end inner class

	   //===================== INSERT INTO TABLE VALUES =======================
	   //----------------------EMPLOYEE--------------------------------
	   //Eισάγει τιμές στον πίνακα Employee
	   public long InsertIntoEmployeeValues( 
			       int id, String name, String last_name, String address,
			       String phone, String mobile, String job, String company,
			       String department, String grade, String photo, int isUser){		   
		  ContentValues tableValues = new ContentValues();		  
		  tableValues.put(USER_CODE , id);         //κωδικός
		  tableValues.put(NAME      , name);;
		  tableValues.put(LAST_NAME , last_name);
		  tableValues.put(ADDRESS   , address);
		  tableValues.put(PHONE     , phone);
		  tableValues.put(MOBILE    , mobile);
		  tableValues.put(PROFESSION, job);         //επάγγελμα
		  tableValues.put(COMPANY   , company);     //εταιρεία
		  tableValues.put(DEPARTMENT, department);  //τομέας εργασίας 		  
		  tableValues.put(GRADE     , grade);       //βαθμός εργαζόμενου
		  tableValues.put(PHOTO		, photo);
		  tableValues.put(USER      , isUser);  //ελέγχει αν πρόκειται για καταχώρηση του χρήστη
		  return   sqlDB.insert(EMPLOYEE, null, tableValues); //table name, nullColumnHack
	   }//end method
	   //------------------------SHIFT----------------------------------
	   //Eισάγει τιμές στον πίνακα Shift
	   public long InsertIntoShiftValues(
			        int workId, long date, String weekday, String time, boolean sunday,
			        boolean holiday, String hol_name, String comments, String building,
					int extra_time){
           
		  ContentValues tableValues = new ContentValues();
		  tableValues.put(EMPLOYEE_ID, workId );		  
		  tableValues.put(DATE       , date );		  		 
		  tableValues.put(WEEKDAY    , weekday );
		  tableValues.put(TIME       , time );
		  tableValues.put(SUNDAY     , sunday );
		  tableValues.put(HOLIDAY    , holiday );
		  tableValues.put(HOL_NAME   , hol_name );
		  tableValues.put(COMMENTS   , comments );
		  tableValues.put(BUILDING   , building );
		  tableValues.put(EXTRA_TIME , extra_time );	 
		  return   sqlDB.insert(SHIFT, null, tableValues);
	   }//end method	  
	   //---------------------PAID_LEAVE----------------------------------- 
	   //Εισάγει τιμές στον πίνακα Paid Leave
	   public long InsertIntoPaidLeaveValues( 
			       int workedId, long startDate, long endDate, int duration, 
                   String category, int year, int sumDays, String comments ){  
		   
		  ContentValues tableValues = new ContentValues();
		  tableValues.put(WORKED_ID  , workedId);
		  tableValues.put(START_DATE , startDate);
		  tableValues.put(END_DATE   , endDate);
		  tableValues.put(DURATION   , duration);
		  tableValues.put(CATEGORY   , category);
		  tableValues.put(YEAR       , year);
		  tableValues.put(SUMDAYS    , sumDays);
		  tableValues.put(LEAVECOMM  , comments);		  
		  return   sqlDB.insert(PAID_LEAVE, null, tableValues);
	   }//end method	   
	   //---------------------PAID_LEAVE-----------------------------------
	   //Εισάγει τιμές στον πίνακα Paid Leave
	   public long InsertIntoCredentialsValues(String userName, String pass){

		  ContentValues tableValues = new ContentValues();
		  tableValues.put(USER_NAME , userName);
		  tableValues.put(PASSWORD , pass);
		  return   sqlDB.insert(CREDENTIALS, null, tableValues);
	   }//end method

	   public long DeleteCredentials(){
		  return   sqlDB.delete(CREDENTIALS, null, null);
	   }
	   //====================== SELECT STATEMENTS ==============================
	   //------------------- SELECT COUNT(*) FROM TABLE ------------------------
	   //επιστρέφει το πλήθος γραμμών του πίνακα που παίρνει ως παράμετρο
	   public long getSumRowsFromTable(String tableName){ 
		   long count  = 0;
		   String sql  = "SELECT _id FROM "+tableName+"";
		   String sql_2= "SELECT count(*) FROM "+tableName+"";
		   //SQLiteStatement statement = sqlDB.compileStatement(sql);
		   //long count = statement.simpleQueryForLong();
		   Cursor c = sqlDB.rawQuery(sql , null);
		   if(c != null)
		      count = c.getCount();
		   return count;	    
	   }
	   //------------SELECT COUNT(*)FROM TABLE WHERE ID------------------
	   //επιστρέφει το πλήθος γραμμών του πίνακα(συνήθως 1) που σχετίζονται με τον κωδικό 
	   //κάποιου εργαζόμενου(στην περίπτωση που υπάρχει ήδη ένας επιστρέφει 1 αλλιως 0)
	   public long getSumRowsFromTable(String tableName, int workedId){ 
		   long count = 0;
		   String sql = "SELECT _id FROM "+tableName+" WHERE user_code="+workedId;		  
		   Cursor c   = sqlDB.rawQuery(sql , null);
		   if(c != null)
		      count = c.getCount();
		   return count;	  
	   }
	   //---------SELECT COUNT(*)FROM TABLE SHIFT WHERE ID---------------
	   //επιστρέφει το πλήθος γραμμών του πίνακα "Shift" που σχετίζονται
	   //με τον κωδικό εργαζόμενου
	   public long getSumRowsFromShiftTable(int workedId){ 
		   long count  = 0;
		   String sql = "SELECT _id FROM Shift WHERE worked_id="+workedId;		  
		   Cursor c   = sqlDB.rawQuery(sql , null);
		   if(c != null)
		      count = c.getCount();
		   return count;	  
	   }
	  //-------------SELECT FROM TABLE WHERE DATE-------------------
	   //επιστρέφει το πλήθος γραμμών του πίνακα(συνήθως 1) που σχετίζονται με τον κωδικό 
	   //κάποιου εργαζόμενου(στην περίπτωση που υπάρχει ήδη ένας επιστρέφει 1 αλλιως 0)
	   public long selectDateValueFromTable(String tableName, long dateMilisec){ 
		   long count = 0;
		   String sql = "SELECT shift_date FROM "+tableName+" WHERE shift_date="+dateMilisec;		  
		   Cursor c   = sqlDB.rawQuery(sql , null);
		   if(c != null)
		      count = c.getCount();
		   return count;	  
	   }
	  //-------------SELECT FROM TABLE PaidLeave WHERE DATE-----------------
	   //επιστρέφει τη γραμμή του πίνακα(συνήθως 1) που σχετίζονται 
	   //με τη συγκεκριμένη ημερομηνία. Επιστρέφει 1 αν υπάρχει αλλιώς μηδέν(0)
	   public int selectStartDateValueFromOfWork(long dateMilisec){ 
		   int count = 0;
		   String sql = "SELECT * FROM PaidLeave WHERE start_date="+dateMilisec;		  
		   Cursor c   = sqlDB.rawQuery(sql , null);
		   if(c != null)
		      count = c.getCount();
		   return count;	  
	   }	
	   //------------- SELECT VALUE FROM TABLE --------------------------
	   //επιστρέφει την τιμή μιας στήλης του πίνακα απο κάθε γραμμή με τον κωδικό του χρήστη 
	   public String getKeyValueFromTable(String tableName, String columnName, int id)
			                              throws Exception{
		   String sql = "SELECT "+columnName+" from "+tableName+" where _id="+id+"";
		   Cursor c   = sqlDB.rawQuery(sql , null);
		   if(c != null)
			  c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
		   return c.getString(0);//επιστρέφει το 1ο id που βρείκε
	   }
	   //------------- SELECT VALUE FROM TABLE -------------------------
	   //επιστρέφει την τιμή του κωδικού χρήστη 
	   public int getIdValueFromTable(String tableName) throws Exception{
		   int userId = 0;
		   String sql = "SELECT user_code from "+tableName+" where _id=1";
		   Cursor c   = sqlDB.rawQuery(sql , null);
		   if(c != null){
			  c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
			  userId = c.getInt(0);
		   }
		   return  userId;  //επιστρέφει το 1ο id που βρείκε
	   }
       //------------- SELECT * FROM TABLE ----------------------------
	   //επιστρέφει σε μια μεταβλητή τύπου  Cursor όλες τις γραμμές και στήλες 
	   //ενός πίνακα με το συγκεκριμένο id χρήστη 
	   public Cursor getAllValuesFromTable(String tableName, int user) throws Exception{
		   String sql = "SELECT * from "+tableName+" where is_user="+user+"";
		   Cursor c   = sqlDB.rawQuery(sql , null);
		   if(c != null)
			  c.moveToFirst();
		   return c;
	   }
	   //-------------- SELECT COUNT ALL SHIFT FROM TABLE ---------------
	/**
	 * επιστρέφει έναν ακέραιο (το πλήθος γραμμών του πίνακα shift)
	 * που μετρήθηκαν μεταξύ των 2 Ημερομηνιών
	 * Ο τελεστής BETWEEN  δεν περιλαμβάνει την ισότητα.
	 * Μεταφράζεται σε: >  από τιμή και < από τιμή*/

	   public long getCountFromShiftTablebetweenDates(int userCode, long firstDate, long lastDate) 
			                                    throws Exception{
		   String sql = "SELECT shift_date FROM Shift WHERE (worked_id="+userCode+
				        " AND shift_date>="+firstDate+" AND shift_date<="+lastDate+")";
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   return c.getCount();
	   } 
	   //------------- SELECT ALL SHIFTS BETWEEN DATES ------------------
	   /*
	    * Επιστρέφει όλες τις βάρδιες μεταξύ των ημερομηνιών
	    * Ο τελεστής BETWEEN  δεν περιλαμβάνει την ισότητα. Μεταφράζεται σε: >  από τιμή και < από τιμή
	    */
	   public Cursor getAllShiftsBetweenDates(int userCode, long firstDate, long lastDate) 
			                                    throws Exception{
		   String sql = "SELECT * FROM Shift WHERE (worked_id="+userCode+
				        " AND shift_date>="+firstDate+" AND shift_date<="+lastDate+")";
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   return c;
	   } 
	   //----------- SELECT ALL SHIFTS AFTER DATE ----------------------
	   /*
	    * Επιστρέφει όλες τις βάρδιες μεταξύ των ημερομηνιών
	    * Ο τελεστής BETWEEN  δεν περιλαμβάνει την ισότητα.
	    * Μεταφράζεται σε: >  από τιμή και < από τιμή
	    */
	   public Cursor getAllShiftsAfterDate(int userCode, long theDate) throws Exception{
		   String sql = "SELECT * FROM Shift WHERE (worked_id="+userCode+
				        " AND shift_date>="+theDate+")";
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   return c;
	   } 
	   //------------ SELECT COUNT ALL SHIFT FROM TABLE ----------------------
	   //επιστρέφει έναν ακέραιο (το πλήθος γραμμών του πίνακα shift) όπου η τιμή μιας 
	   //στήλης είναι ίση με τη τιμή της παραμέτρου της βάρδιας: shift_value
	   public long getCountFromTableWithShiftValue(int userCode, String shift_value) 
			                                       throws Exception{
		   String sql = "SELECT shift_date FROM Shift WHERE (worked_id="+userCode+
				        " AND work_time='"+shift_value+"')";
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   return c.getCount();
	   }//end method 
	//------------------- SELECT USER NAME ------------------------
	public String getUserName() throws Exception{
		String sql = "SELECT user_name FROM Credentials WHERE _id=1";
		Cursor c   = sqlDB.rawQuery(sql , null);
		return c.getString(0);
	}//end method
	   //--------- SELECT COUNT SHIFT FROM TABLE BETWEEN DATES -----------
	   //Eπιστρέφει έναν ακέραιο (το πλήθος γραμμών του πίνακα shift) όπου η τιμή μιας 
	   //στήλης είναι ίση με τη τιμή της παραμέτρου της βάρδιας: shift_value
	   //μεταξύ δύο ημερομηνιών. Αθροίζει ένα τύπο βάρδιας(πρωι,απόγευμα,βραδυ,ρεπο,αργια)
	   //για κάποιο χρον. διάστημα
	   public Cursor getCountFromTableWithShiftValueBetweenDates(int userCode,
			         String shiftStartTime, long firstDate, long lastDate) throws Exception {
		   //πριν και μετά την ώρα έναρξης της βάρδιας μπορεί να υπάρχει όποιοσδήποτε χαρακτήρας
		   String shiftLikeValue = shiftStartTime+"%"; 
		   String sql ="SELECT shift_date, weekday, work_time FROM Shift WHERE " +
				   "(worked_id="+userCode+" AND work_time LIKE '"+shiftLikeValue+"' AND ("+
				   "shift_date>="+firstDate+" AND shift_date<="+lastDate+"));";
		   //αν δεν δουλέψει βάζω την String τιμή σε ('shiftLikeValue')
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   return c;
	   }   
	   //------------- SELECT COUNT WORKED SUNDAYS BETWEEN DATES ------------------
	   //Eπιστρέφει όλες τις Κυριακές που εργάστηκε (Ημερομηνία, Ημέρα, Βάρδια) 
	   public Cursor getCountSundaysFromShiftBetweenDates(int userCode, long firstDate,
										  long lastDate) throws Exception{
		   String sql = "SELECT shift_date, weekday, work_time, holiday_name FROM Shift " +
				   "WHERE (worked_id="+userCode+ " AND sunday=1 AND " +
				   "(shift_date>="+firstDate+" AND shift_date<="+lastDate+") "+
				   "AND work_time NOT IN "+ "(SELECT work_time FROM Shift WHERE "+
				   "(work_time='ΑΡΓΙΑ' OR work_time='ΡΕΠΟ' " +
				   "OR work_time='ΑΔΕΙΑ' OR work_time='ΑΣΘΕΝΗΣ')))";
		   Cursor c = sqlDB.rawQuery(sql , null);
		   return c;
	   }
	 //------- SELECT COUNT WORKED HOLIDAYS BETWEEN DATES --------------------
	   //Eπιστρέφει όλες τις Αργίες  που εργάστηκε (Ημερομηνία, Ημέρα, Βάρδια) 
	   public Cursor getCountHolidaysFromShiftBetweenDates(int userCode, long firstDate,
														   long lastDate)
			                   throws Exception{
		   String sql = "SELECT shift_date, weekday, work_time, holiday_name FROM Shift " +
				   "WHERE (worked_id="+userCode+ " AND holiday=1 " +
				   "AND (shift_date>="+firstDate+" AND shift_date<="+lastDate+") "+
				   "AND work_time NOT IN "+ "(SELECT work_time FROM Shift " +
				   "WHERE (work_time='ΑΡΓΙΑ' OR work_time='ΡΕΠΟ' " +
				   "OR work_time='ΑΔΕΙΑ' OR work_time='ΑΣΘΕΝΗΣ')))";
		   Cursor c = sqlDB.rawQuery(sql , null);
		   return c;
	   }   
	   //------------- SELECT shift FROM TABLE -------------------------------
	   //Επιστρέφει τις καταχωρημένες τιμές βάρδιας για μια συγκεκριμένη ημερομηνία
	   public Cursor getShiftValuesFromShiftTable(int userId, long date) 
			                                     throws Exception{
		   String sql = "SELECT * from Shift where (worked_id="+userId+" AND shift_date="+date+")";
		   Cursor c = sqlDB.rawQuery(sql , null);
		   if(c != null)
			 c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
		   return c;
	   }//end method
	   //---------- SELECT LAST shift FROM TABLE --------------------------------
	   //Επιστρέφει τις καταχωρημένες τιμές βάρδιας για την τελευταία ημερομηνία
	   public Cursor getLastShiftFromTable() throws Exception{
		   String sql = "SELECT * FROM Shift WHERE shift_date=(SELECT MAX(shift_date) FROM Shift)";
		   Cursor c = sqlDB.rawQuery(sql , null);
		   if(c != null)
			 c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
		   return c;
	   }  
	  //----------- SELECT ID of LAST shift FROM TABLE -------------------------
	   //Επιστρέφει τις καταχωρημένες τιμές βάρδιας για την τελευταία ημερομηνία
	   public int getShiftIDFromMaxDate() throws Exception{
		   String sql= "SELECT _id FROM Shift WHERE shift_date=(SELECT MAX(shift_date) FROM Shift)";
		   Cursor c = sqlDB.rawQuery(sql , null);
		   if(c != null)
			 c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
		   return c.getInt(0);
	   }  
	   //----------- SELECT DATE of LAST shift FROM TABLE ---------------------
	   //Επιστρέφει την τελευταία (μεγαλύτερη) ημερομηνία 
	   //για την οποία υπάρχει καταχωρημένη βάρδια
	   public long getLastRecordOfShifts() throws Exception{
		   String sql = "SELECT MAX(shift_date) FROM Shift";
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   if(c != null)
			 c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
		   return c.getLong(0);
	   }  
	 //------- SELECT DATE of FIRST shift FROM TABLE ---------------------------
	   //Επιστρέφει την πρώτη (μικρότερη) ημερομηνία 
	   //για την οποία υπάρχει καταχωρημένη βάρδια
	   public long getFirstRecordOfShifts() throws Exception{
		   String sql = "SELECT MIN(shift_date) FROM Shift";
		   Cursor c = sqlDB.rawQuery(sql , null);
		   if(c != null)
			 c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
		   return c.getLong(0);
	   }  
	   //-------------- SELECT SUM(extra_time) FROM  shift TABLE ---------------
	   //Επιστρέφει το άθροισμα όλων των εξτρα χρόνων που έχει εργαστεί ο εργαζόμενος
	   public long getShiftSumExtraTime() throws Exception{
		   String sql = "SELECT SUM(extra_time) FROM Shift";
		   Cursor c = sqlDB.rawQuery(sql , null);
		   if(c != null)
			 c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
		   return c.getInt(0);
	   }  
	   //-------------- SELECT LAST shift FROM TABLE ---------------------------
	   //Επιστρέφει τις καταχωρημένες τιμές βάρδιας για την τελευταία ημερομηνία
	   public Cursor getShiftRowValues(long shiftId) throws Exception{
		   String sql = "SELECT * FROM Shift WHERE _id="+shiftId;
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   if(c != null)
			 c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
		   return c;
	   } 
	   //-------------- SELECT YEAR OF PAIDLEAVE -------------------------------
	   //επιστρέφει το ακέραιο πλήθος καταχωρημένων εγγραφών αδειών
	   //που βρέθηκαν στη ΒΔ για το τρέχον έτος
	   public int getTotalOfWorksFromCurrentYear(int curYear){
		   int sumRows = 0;
		   String sql = "SELECT _id FROM PaidLeave WHERE year="+curYear;
		   Cursor c = sqlDB.rawQuery(sql , null);
		   if(c != null)
			  sumRows = c.getCount();
		   return sumRows;
	   }
	   //---------------- SELECT YEAR OF PAIDLEAVE -------------------------------
	   //επιστρέφει ένα cursor με τις εγγραφές αδειών
	   //που βρέθηκαν στη ΒΔ για το επιλεγμένο έτος
	   public Cursor getOfWorksValuesFromCurrentYear(int userCode, int curYear){		   
		   String sql = "SELECT * FROM PaidLeave WHERE year="+curYear;
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   return c;
	   }
	   //----------- SELECT SUM_DAYS PERMISSION OF PAIDLEAVE ---------------------
	   //επιστρέφει το ακέραιο πλήθος ημερών αδειών που δικαιούτε
	   //για το τρέχον έτος
	   public int getOfWorksSumDaysPermission(int curYear){
		   int sumDays = 0;
		   String sql = "SELECT sum_days FROM PaidLeave WHERE year="+curYear;
		   Cursor c = sqlDB.rawQuery(sql , null);
		   if(c != null){
			  c.moveToFirst();
			  sumDays = c.getInt(0);
		   }
		   return sumDays;
	   }
	   //------------- SELECT SUM(DURATION) OF PAIDLEAVE ----------------------------
	   //επιστρέφει το πλήθος ημερών αδείας που έχει πάρει για το τρέχον έτος
	   //μιας συγκεκριμένης κατηγορίας (π.χ. ΚΑΝΟΝΙΚΗ)
	   public int getSumDaysOfWorkHaveTaken(int curYear, String ofWorkCateg){
		   int sumDays = 0;
		   String sql = "SELECT SUM(duration) FROM PaidLeave WHERE (year="+curYear+
				        " AND category='"+ofWorkCateg+"');";
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   if(c != null){
			  c.moveToFirst();
			  sumDays = c.getInt(0);
		   }
		   return sumDays;
	   }//end method
	   //----------------- SELECT SUM ALL OFWORKS FROM TABLE ------------------------
	   //επιστρέφει το πλήθος ημερών αδείας που έχει πάρει για το τρέχον έτος
	   //μιας συγκεκριμένης κατηγορίας (π.χ. ΚΑΝΟΝΙΚΗ)
	   public int getSumDaysOfWorkHaveTaken(int userCode, int curYear, String ofWorkCateg)
			                               throws Exception{
		   int sumDays = 0;
		   String sql = "SELECT SUM(duration) FROM PaidLeave WHERE (" +
		   		        "worked_id="+userCode+
				        " AND year="+curYear+
				        " AND category='"+ofWorkCateg+"')";
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   if(c != null){
			  c.moveToFirst();
			  sumDays = c.getInt(0);
		   }
		   return sumDays;
	   }//end method 
	   //----------------- SELECT SUM ALL OFWORKS FROM TABLE ------------------------
	  /*
	   * επιστρέφει το πλήθος ημερών αδείας που δικαιούτε να πάρει 
	   * για το συγκεκριμένο έτος
	   */
	   public int getSumDaysOfWorkPermited(int userCode, int curYear) throws Exception{
		   int sumDays = 0;
		   String sql = "SELECT sum_days FROM PaidLeave WHERE (" +
		   		        "worked_id="+userCode+
				        " AND year="+curYear+")";
		   Cursor c = sqlDB.rawQuery(sql , null);
		   if(c != null){
			  c.moveToFirst();
			  sumDays = c.getInt(0);
		   }
		   return sumDays;
	   }//end method 
	   
	   //------------- SELECT ofWork FROM TABLE -------------------------------------
	   //Επιστρέφει τις καταχωρημένες τιμές βάρδιας για μια συγκεκριμένη ημερομηνία
	   public Cursor getOfWorkValuesFromTable(int userId, long date) throws Exception{
		   String sql = "SELECT * from PaidLeave where (worked_id="+userId+
				   " AND start_date="+date+")";
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   if(c != null)
			 c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
		   return c;
	   }//end method
	   
	   //---------- SELECT START DATE FROM OFWORKS ----------------------------------
	   //Επιστρέφει τις ημερομηνίες έναρξης όλων των καταχωρημένων αδειών 
	   public Cursor getAllSratDatesFromOfWorks(int userId) throws Exception{
		   String sql = "SELECT start_date from PaidLeave where (worked_id="+userId+")";
		   Cursor c = sqlDB.rawQuery(sql , null);
		   if(c != null)
			 c.moveToFirst();   //μετακινεί το δείκτη στο 1ο αποτέλεσμα
		   return c;
	   }//end method
	   
	   /*
	   //------------- SELECT Working Sundays FROM TABLE Shift -------------------------
	   //επιστρέφει το πλήθος γραμμών που βρέθηκαν με βάρδια ημέρα Κυριακή (εκτός ΡΕΠΟ, ΑΡΓΙΑ)  
	   public long getCountWorkingSundayFromTable(int userCode) throws Exception{
		   long   sumShifts = 0;
		   String weekDay         = "ΚΥΡΙΑΚΗ";
		   String shift_morning   = "ΠΡΩΙ%";
		   String shift_afternoon = "ΑΠΟΓ%";
		   String shift_night     = "ΒΡΑΔ%";
		   
		   String sql = "SELECT * FROM Shift WHERE (worked_id="+userCode+" AND weekday="+weekDay+
                        " AND (work_time LIKE "+shift_morning+" OR work_time LIKE "+shift_afternoon+
                        " OR work_time LIKE "+shift_night+"))";		   
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   if(c != null)
			  sumShifts = c.getCount(); //αθροίζει όλες τις εργάσιμες Κυριακές
		   return sumShifts ;   
	   }  
	   //--------------- SELECT Working Sundays FROM TABLE Shift ------------------------
	   //επιστρέφει τις γραμμές που βρέθηκαν με βάρδια (εκτός ΡΕΠΟ, ΑΡΓΙΑ) για τις Επίσημες Αργίες 
	   public Cursor getWorkingHolidaysFromTable(int userCode) throws Exception{
		   int holidays = 1; //1=true αν είναι αργία
		   String shift_morning   = "ΠΡΩΙ%";
		   String shift_afternoon = "ΑΠΟΓ%";
		   String shift_night     = "ΒΡΑΔ%";
		   
		   String sql = "SELECT * FROM Shift WHERE (worked_id="+userCode+" AND holiday="+holidays+
                        " AND work_time IN ("+shift_morning+","+shift_afternoon+","+shift_night+"))";		   
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   if(c != null)
			  c.moveToFirst(); //αθροίζει όλες τις εργάσιμες Κυριακές
		   return c ;   
	   }  
	   //------------- SELECT Working Sundays FROM TABLE Shift Between Dates -----------------
	   //Eπιστρέφει τις γραμμές του πίνακα Shift με βάρδια όχι ρεπό μια συγκεκριμένη ημέρα της εβομάδος
	   //δηλαδή (ΔΕΥΤΕΡΑ,ΤΡΙΤΗ,ΤΕΤΑΡΤΗ,...)για χρον. διάστημα μεταξύ δύο συγκεκριμένων ημερομηνιών.
	   //Η παράμετρος : weekday παίρνει τιμές μια από τις ημέρες της εβδομαδος 	   
	   public Cursor getWorkingWeekdayFromTableBetweenDates(
			            int userCode, String weekDay, String firstDate, String lastDate)
			            throws Exception{		   
		   String shift_morning   = "ΠΡΩΙ%";
		   String shift_afternoon = "ΑΠΟΓ%";
		   String shift_night     = "ΒΡΑΔ%";
		   String shift_timeOff   = "Ρ%";
		   String shift_holiday   = "ΑΡΓΙΑ";		   
		   String sql = "SELECT * FROM Shift WHERE (worked_id="+userCode+" AND weekday="+weekDay+
                        " AND work_time IN ("+shift_morning+","+shift_afternoon+","+shift_night+")"+
                        " AND date BETWEEN "+firstDate+" AND "+lastDate+")";
		   //enallaktika
		   String sql2= "SELECT * FROM Shift WHERE (worked_id="+userCode+" AND weekday="+weekDay+
                        " AND work_time NOT IN ("+shift_timeOff+","+shift_holiday+")"+
                        " AND date BETWEEN "+firstDate+" AND "+lastDate+")";
		   //enallaktika
		   String sql3= "SELECT * FROM Shift WHERE (worked_id="+userCode+" AND weekday="+weekDay+
                        " AND work_time NOT IN (SELECT work_time FROM Shift WHERE work_time IN ("+
				        shift_timeOff+","+shift_holiday+")"+
                        " AND date BETWEEN "+firstDate+" AND "+lastDate+")";
		   Cursor c   = sqlDB.rawQuery(sql , null);		   
		   if(c != null)
			  c.moveToFirst();
		   return c ;   
	   }
	   */	  
	    //-------------------------UPDATE------------------------------------
	    //================= UPDATE ALL EMPLOYEE VALUES ======================
	   /*
	    * Μπορούν να αλλάξουν όλες οι καταχωρημένες τιμές εκτός αυτής 
	    * που καθορίζει αν είναι χρήστης ή όχι: isUser=1
	    */
	   public boolean updateEmployeeValues(int    id      , int    userCode, String name,
			                               String lastName, String address , String phone,
			                               String mobile  , String job     , String company,
			                               String depart  , String grade   , String photo ){
		   ContentValues args = new ContentValues();
		   args.put(USER_CODE        , userCode);
		   args.put(NAME      , name);
		   args.put(LAST_NAME , lastName);
		   args.put(ADDRESS   , address);
		   args.put(PHONE     , phone);
		   args.put(MOBILE    , mobile);
		   args.put(PROFESSION, job);
		   args.put(COMPANY   , company);
		   args.put(DEPARTMENT, depart);
		   args.put(GRADE     , grade);
		   args.put(PHOTO     , photo);
		   //επιστρέφει true αν το πλήθος γραμμών που ενημερώθηκαν είναι >0
		   return 	sqlDB.update(EMPLOYEE, args, "_id="+id, null) > 0;   
	   }
	   //============= UPDATE A COLUMN VALUE IN TABLE =============================
	   //update a type of String value in a column of table
	   public boolean updateUserPhoto(String pathPhoto, int id){
		   ContentValues args = new ContentValues();
		   args.put(PHOTO, pathPhoto);
		   return 	sqlDB.update(EMPLOYEE, args, "_id="+id, null) > 0;
	   }
	   //============ UPDATE A COLUMN VALUE IN  TABLE ==============================
	   //update a type of Integer value in a column of table
	   public boolean updateTableIntegerValue(String tableName, String keyName,
											  int keyValue, int id){
		   ContentValues args = new ContentValues();
		   args.put(keyName, keyValue);
		   return 	sqlDB.update(tableName, args, USER_CODE+"="+id, null) > 0;
	   }
	   //=============UPDATE A COLUMN VALUE IN TABLE ================================
	   //update a type of String value in a column of table
       public boolean updateTableStringValue(String tableName, String keyName,
											 String keyValue, int id){
		   ContentValues args = new ContentValues();
		   args.put(keyName, keyValue);
		   return 	sqlDB.update(tableName, args, USER_CODE+"="+id, null) > 0;
	   }	
       //============= UPDATE A COLUMN VALUE IN TABLE ==================================
	   //update a type of String value in a column of table
       public boolean updateShiftValue(long date, String shiftValue, String coments,
									   int extraTime){
    	   ContentValues args = new ContentValues();
		   args.put(TIME      , shiftValue);
		   args.put(COMMENTS  , coments);
		   args.put(EXTRA_TIME, extraTime);
		   return  sqlDB.update(SHIFT, args, DATE+"="+date, null) > 0;   
	   }	
       //============== UPDATE OF WORK VALUES =================================
       /*
        * Ενημερώνει τις τιμές μιας Αδειας
        */
       public boolean updateOfWorkValues(int id, long startDate, long endDate, 
    		                             int dur, String categ, int year,
    		                             int sumDays, String  com){
    	   ContentValues args = new ContentValues();
		   args.put(START_DATE, startDate);  //ημερομηνία έναρξης
		   args.put(END_DATE  , endDate);    //ημερομηνία λήξης
		   args.put(DURATION  , dur);    //διάρκεια άδειας
		   args.put(CATEGORY  , categ);    //κατηγορία αδείας
		   args.put(YEAR      , year);        //έτος αδείας
		   args.put(SUMDAYS   , sumDays);    //ημέρες αδείας που δικαιούτε/έτος 
		   args.put(LEAVECOMM , com);		  
		   return  sqlDB.update(PAID_LEAVE, args, "_id="+id, null) > 0;  
       }//end method
       //===================== UPDATE SHIFT CODE =================================
       /*
        * Αλλάζει την τιμή ξένου κλειδιού που εδώ είναι ο κωδικός εργαζόμενου
        * σε όλες τις εγγραφές βαρδιών. Καλείται αν ο χρήστης αλλάξει τον κωδικό του
        */
       public boolean updateShiftForeingKey(int oldCode, int newCode){
    	   ContentValues args = new ContentValues();
		   args.put(EMPLOYEE_ID, newCode);		  
		   return  sqlDB.update(SHIFT, args, EMPLOYEE_ID+"="+oldCode, null) > 0;  
       }//end method
       //===================== UPDATE OF WORK CODE =================================
       /*
        *Αλλάζει την τιμή ξένου κλειδιού που εδώ είναι ο κωδικός εργαζόμενου
        * σε όλες τις εγγραφές Αδειών. Καλείται επίσης όταν ο χρήστης αλλάξει τον κωδικό του
        */
       public boolean updateOfWorkForeingKey(int oldCode, int newCode){
    	   ContentValues args = new ContentValues();
		   args.put(WORKED_ID, newCode);  //ημερομηνία έναρξης		  		  
		   return  sqlDB.update(PAID_LEAVE, args, WORKED_ID+"="+oldCode, null) > 0;  
       }//end method
       //===================== UPDATE SHIFT HOLIDAY NAME ============================
       /*
        *Αλλάζει την τιμή ξένου κλειδιού που εδώ είναι ο κωδικός εργαζόμενου
        * σε όλες τις εγγραφές Αδειών. Καλείται επίσης όταν ο χρήστης αλλάξει τον κωδικό του
        */
       public boolean updateShiftTableStringValue(String columnName, String colValue, long date){
    	   ContentValues args = new ContentValues();
		   args.put(columnName, colValue);  //ημερομηνία έναρξης		  		  
		   return  sqlDB.update(SHIFT, args, DATE+"="+date, null) > 0;  
       }//end method
	   //================== UPDATE CREDENTIALS ===================================
	   //update a type of Integer value in a column of table
	    public boolean updateCredentialsValues(String passValue, String userName){
		    ContentValues args = new ContentValues();
			args.put(PASSWORD, passValue);
		    return 	sqlDB.update(CREDENTIALS, args, USER_NAME+"='"+userName+"'", null) > 0;
	   }
	   //-----------------------DELETE-------------------------------------------
       //==================== DELETE ALL ROWS FROM TABLE =========================
       public int deleteAllRowsFromTable(String tableName){
    	   return sqlDB.delete(tableName, null, null);
    	   //execSQL("DELETE * FROM TABLE "+tableName);   	   
       }//end method
      //-------------------------------------------------------------------------
      //Οι παρακάτω μπορούν να κληθούν μεμονομένα για δημιουργία ή διαγραφή Πινάκων
	   //====================== DROP TABLE ======================================
	   //Διαγράφει ένα Πίνακα
	   public void DropTable(String tableName){		    
		   sqlDB.execSQL("DROP TABLE IF EXISTS "+tableName);
	   } //end method	   
	  
	   //=================== CREATE TABLE EMPLOYEE ==============================
	   //Δημιουργεί τον πίνακα Employee
	   public boolean CreateTableEmployee(){		    
		   try{
			   sqlDB.execSQL(CREATE_TABLE_EMPLOYEE);
			  }catch(SQLException e){
				      e.printStackTrace();
				      Log.e("TABLE_EMPLOYEE", "ERROR_CREATE_TABLE");
			  }
		   return true;
	   } //end method 
	   
	   //--------------------- CREATE TABLE SHIFT -------------------------------
	   //Δημιουργεί τον πίνακα Shift
	   public boolean CreateTableShift(){		    
		   try{
			   sqlDB.execSQL(CREATE_TABLE_SHIFT);
			  }catch(SQLException e){
				    e.printStackTrace();
				    Log.e("TABLE_SHIFT", "ERROR_CREATE_TABLE");
			  }
		   return true;
	   } //end method 
	   
	   //--------------------- CREATE TABLE PAID LEAVE -------------------------
	   //Δημιουργεί τον πίνακα Paid Leave
	   public boolean CreateTablePaidLeave(){		    
		   try{
			   sqlDB.execSQL(CREATE_TABLE_PAID_LEAVE);
			  }catch(SQLException e){
				     e.printStackTrace();
				     Log.e("TABLE_PAID_LEAVE", "ERROR_CREATE_TABLE");
			  }
		   return true;
	   } //end method 
	   
	   //============= INSERT IN TABLE COLUMN STRING TYPE =====================
	   //Προσθέτει μια στήλη με δεδομένα τύπου String σε ένα πίνακα
	   public boolean AddStringTypeColumnInTable(String tableName, String keyName){		    
		   try{
			    sqlDB.execSQL("alter table "+tableName+" add "+keyName+" text not null");
			   }catch(SQLException e){
				      e.printStackTrace();
				      Log.e("TABLE_:"+tableName, "ERROR_CREATE_TABLE_COLUMN :"+keyName);
			   }
		   Log.i("TABLE_:"+tableName, "OK_CREATE_TABLE_COLUMN :"+keyName);
		   return true;
	   } //end method
	  
	   //--------------- INSERT IN TABLE COLUMN INTEGER TYPE -------------------
	   //Προσθέτει μια στήλη με δεδομένα τύπου Integer σε ένα πίνακα 
	   public boolean AddIntegerTypeColumnInTable(String tableName, String keyName){		    
		   try{
			    sqlDB.execSQL("alter table "+tableName+" add "+keyName+" integer not null");
			   }catch(SQLException e){
				      e.printStackTrace();
				      Log.e("TABLE_:"+tableName, "ERROR_CREATE_TABLE_COLUMN :"+keyName);
			   }
		   Log.i("TABLE_:"+tableName, "OK_CREATE_TABLE_COLUMN :"+keyName);
		   return true;
	   } //end method 		   


}//end class
