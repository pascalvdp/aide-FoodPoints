package com.mycompany.FoodPoints;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.provider.*;
import android.util.*;
import java.util.*;
import android.text.*;
import android.widget.*;
import java.text.*;
import android.os.*;

public class FoodPointsDatabase {
	
    private static final String TAG = "FoodPointsDatabase";
	
    //The columns we'll include in the FoodPoints table
    public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_POINTS_STRING = SearchManager.SUGGEST_COLUMN_TEXT_2;
	public static final String KEY_DAILY = "Daily";
	
	public static final String KEY_DATE = "Date";
	public static final String KEY_DATE_LONG = "DateLong";
	
	public static final String KEY_DATE_LIST = "DateList";
	public static final String KEY_DATE_LIST_LONG = "DateListLong";
	public static final String KEY_VIEW_VISIBLE = "ViewVisible";
	public static final String KEY_DAY_TOTAL = "DayTotal";
	public static final String KEY_EXTRA = "Extra";

    private static final String FTS_VIRTUAL_TABLE = "FTSFoodPoints";
	private static final String FTS_VIRTUAL_TABLE_DATE = "FTSFoodPointsDate";
	private static final String FTS_VIRTUAL_TABLE_DATE_LIST = "FTSFoodPointsDateList";
    private static final int DATABASE_VERSION = 1;
	
    private FoodOpenHelper mDbHelper;
	private SQLiteDatabase mDb;
    private static final HashMap<String,String> mColumnMap = buildColumnMap();
	private static final HashMap<String,String> mColumnMapDateList = buildColumnMapDateList();
    
    private static HashMap<String,String> buildColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(KEY_WORD, KEY_WORD);
        map.put(KEY_POINTS_STRING, KEY_POINTS_STRING);
		map.put(KEY_DAILY, KEY_DAILY);
		map.put(KEY_DATE, KEY_DATE);//
		map.put(KEY_DATE_LONG, KEY_DATE_LONG);//
        map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
        return map;
    }
	
	private static HashMap<String,String> buildColumnMapDateList() {//voorlopig 2 mappen??????
        HashMap<String,String> map = new HashMap<String,String>();
		map.put(KEY_DATE_LIST, KEY_DATE_LIST);
		map.put(KEY_DATE_LIST_LONG, KEY_DATE_LIST_LONG);
		map.put(KEY_VIEW_VISIBLE, KEY_VIEW_VISIBLE);
		map.put(KEY_DAY_TOTAL, KEY_DAY_TOTAL);
		map.put(KEY_EXTRA, KEY_EXTRA);
        map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
        return map;
    }
	
    public Cursor getWord(String rowId, String[] columns) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[] {rowId};
		String sortOrder= KEY_WORD+" ASC";
        return query(selection, selectionArgs, columns, sortOrder);
        // SELECT <columns> FROM <table> WHERE rowid = <rowId>
    }
     
    public Cursor getWordMatches(String query, String[] columns) {
		String str=String.valueOf('"');// "-teken verwijderen
		query = query.replaceAll(str," ");
		
		String selection = KEY_WORD + " MATCH ?";
		query=query.trim();
		String[] strings = TextUtils.split(query, " ");
		
		String q="";
		for (int i = 0; i < strings.length; i++) {
			if (!strings[i].isEmpty()){
			    strings[i]=strings[i]+"*"; // "*" werkt enkel rechtse kant
			    q+=strings[i];
			}
		}
        String[] selectionArgs = new String[] {q};//query+"*"
		String sortOrder= KEY_WORD+" ASC";
        return query(selection, selectionArgs, columns, sortOrder);
        // SELECT <columns> FROM <table> WHERE <KEY_WORD> MATCH 'query*' //rowid=_id
    }
	
    private Cursor query(String selection, String[] selectionArgs, String[] columns, String sortOrder) {
        //contentprovider moet geen kolomnamen kennen
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);
        builder.setProjectionMap(mColumnMap);
        Cursor cursor = builder.query(mDbHelper.getReadableDatabase(),             
									  columns, selection, selectionArgs, null, null, sortOrder); //DESC  ASC
		if (cursor == null) {                                                     
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
	
	private Cursor queryDate(String selection, String[] selectionArgs, String[] columns, String sortOrder) {
        //contentprovider moet geen kolomnamen kennen
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE_DATE);
        builder.setProjectionMap(mColumnMap);
        Cursor cursor = builder.query(mDbHelper.getReadableDatabase(),             
									  columns, selection, selectionArgs, null, null, sortOrder); //DESC  ASC
		if (cursor == null) {                                                     
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
	
	private Cursor queryDateList(String selection, String[] selectionArgs, String[] columns, String sortOrder) {
        //contentprovider moet geen kolomnamen kennen
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE_DATE_LIST);
        builder.setProjectionMap(mColumnMapDateList);
        Cursor cursor = builder.query(mDbHelper.getReadableDatabase(),             
									  columns, selection, selectionArgs, null, null, sortOrder);
		if (cursor == null) {                                                     
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
	
	public Cursor getAllWords(){
		String[] columns = new String[] {
			BaseColumns._ID,
			FoodPointsDatabase.KEY_WORD,
			FoodPointsDatabase.KEY_POINTS_STRING,
			FoodPointsDatabase.KEY_DAILY};
		String sortOrder= KEY_WORD+" ASC";	
     	return query(null, null, columns, sortOrder);
	}
	
	public Cursor getWordsDaily(){
		String selection=KEY_DAILY + " MATCH ?";
		String[] selectionArgs = new String[] {"true"};
		String[] columns = new String[] {
			BaseColumns._ID,
			FoodPointsDatabase.KEY_WORD,
			FoodPointsDatabase.KEY_POINTS_STRING,
			FoodPointsDatabase.KEY_DAILY};
		String sortOrder= KEY_WORD+" ASC";	
		return query(selection,selectionArgs,columns, sortOrder);
	}
	
	public Cursor getWordsDate(String dateString){
		String selection=KEY_DATE + " MATCH ?";
		String[] selectionArgs = new String[] {dateString};
		String[] columns = new String[] {
			BaseColumns._ID,
			FoodPointsDatabase.KEY_WORD,
			FoodPointsDatabase.KEY_POINTS_STRING,
			FoodPointsDatabase.KEY_DATE};
		String sortOrder= null;	
		return queryDate(selection,selectionArgs,columns, sortOrder);
	}
	
	public Cursor getAllDates(){
		String[] columns = new String[] {
			BaseColumns._ID,
			FoodPointsDatabase.KEY_DATE_LIST,
			FoodPointsDatabase.KEY_DATE_LIST_LONG,
			FoodPointsDatabase.KEY_VIEW_VISIBLE,
			FoodPointsDatabase.KEY_DAY_TOTAL,
			FoodPointsDatabase.KEY_EXTRA };
		String sortOrder= KEY_DATE_LIST_LONG+" DESC";	
		return queryDateList(null,null,columns, sortOrder);
	}
	
	public Cursor getDateList(String dateString){
		String selection=KEY_DATE_LIST + " MATCH ?";
		String[] selectionArgs = new String[] {dateString};
		String[] columns = new String[] {
			BaseColumns._ID,
			FoodPointsDatabase.KEY_DATE_LIST,
			FoodPointsDatabase.KEY_DATE_LIST_LONG,
			FoodPointsDatabase.KEY_DAY_TOTAL,
			FoodPointsDatabase.KEY_EXTRA};
		String sortOrder= null;	
		return queryDateList(selection,selectionArgs,columns, sortOrder);
	}
	
	public Cursor getViewVisibility(String rowId){
		String selection = "rowid = ?";
        String[] selectionArgs = new String[] {rowId};
		String[] columns = new String[] {
			BaseColumns._ID,
			FoodPointsDatabase.KEY_VIEW_VISIBLE};
		String sortOrder= null;
		return queryDateList(selection,selectionArgs,columns, sortOrder);
	}	
	
	//een UNIQUE key gebruiken we als alle rows verschillend moeten zijn
	//rowid is automatisch een unieke identificatie
	private static final String FTS_TABLE_CREATE =
	"CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
	" USING fts3 (" +
	KEY_WORD + ", " +
	KEY_POINTS_STRING + " INT " + ", " +
	KEY_DAILY + "); "; //DATETIME TEXT INTEGER INT REAL
	
	private static final String FTS_TABLE_CREATE_DATE =
	"CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE_DATE +
	" USING fts3 (" +
	KEY_WORD + ", " +
	KEY_POINTS_STRING + " INT " + ", " +
	KEY_DATE + " TEXT "+ ", " + KEY_DATE_LONG + " LONG " + ");";
	
	private static final String FTS_TABLE_CREATE_DATE_LIST =
	"CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE_DATE_LIST +
	" USING fts3 (" +
	KEY_DATE_LIST + " TEXT " + ", " + KEY_DATE_LIST_LONG + " LONG " + ", " +
	KEY_VIEW_VISIBLE + ", " + KEY_DAY_TOTAL + " INT " + ", " + 
	KEY_EXTRA + " INT " + ");";
	
	private final Context mCtx;
	private boolean isExternalStorage;
	
//	public static final String DATABASE_NAME = "FoodPoints.db"; //.db moet niet
	
     //This creates/opens the database.
    private static class FoodOpenHelper extends SQLiteOpenHelper {
		
		FoodOpenHelper(Context context,final String DATABASE_NAME) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
			Log.w(TAG,FTS_TABLE_CREATE);
			Log.w(TAG,FTS_TABLE_CREATE_DATE);
			Log.w(TAG,FTS_TABLE_CREATE_DATE_LIST);
			db.execSQL(FTS_TABLE_CREATE);
			db.execSQL(FTS_TABLE_CREATE_DATE);
			db.execSQL(FTS_TABLE_CREATE_DATE_LIST);
        }
		
		@Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				  + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE_DATE);
			db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE_DATE_LIST);
            onCreate(db);
        }
    }		
	
	public FoodPointsDatabase(Context ctx, boolean bool) {
		this.isExternalStorage=bool;
		this.mCtx = ctx;
	}
		
    public FoodPointsDatabase open() throws SQLException {
		String DATABASE_NAME = "FoodPoints.db"; //.db moet niet
		if (isExternalStorage){ DATABASE_NAME= "/sdcard/AppProjects/FoodPoints.db";}
		mDbHelper = new FoodOpenHelper(mCtx,DATABASE_NAME);
			      mDb = mDbHelper.getWritableDatabase();
			      return this;
	}
		
	public void close() {
		if (mDbHelper != null) {
				mDbHelper.close();
		}
	}

	public void addFood(String word, String points) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_WORD, word);
            initialValues.put(KEY_POINTS_STRING, points);
			initialValues.put(KEY_DAILY, "false");
            mDb.insert(FTS_VIRTUAL_TABLE, null, initialValues);
    }
	
	public void changeFood(String word, String points, long id) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_WORD, word);
		initialValues.put(KEY_POINTS_STRING, points);
		mDb.update(FTS_VIRTUAL_TABLE, initialValues,"rowid = "+ id,null);	
//mDb.delete(		
    }
	
	public void deleteFood(long id) {
		mDb.delete(FTS_VIRTUAL_TABLE, "rowid = "+ id,null);		
    }
	
	public void deleteFoodDate(long id) {
		mDb.delete(FTS_VIRTUAL_TABLE_DATE, "rowid = "+ id,null);		
    }
	
	public void deleteFoodDateList(long id) {
		mDb.delete(FTS_VIRTUAL_TABLE_DATE_LIST, "rowid = "+ id,null);
    }
	
	public void changeDaily(String daily,long id){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DAILY, daily);
		mDb.update(FTS_VIRTUAL_TABLE, initialValues,"rowid = "+ id,null);
	}
	
	public void changeViewVisibility(String visibility,long id){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_VIEW_VISIBLE, visibility);
		mDb.update(FTS_VIRTUAL_TABLE_DATE_LIST, initialValues,"rowid = "+ id,null);
	}
	
	public boolean deleteAllCustomers() { //nog niet in gebruik
		    int doneDelete = 0;
			doneDelete = mDb.delete(FTS_VIRTUAL_TABLE, null , null);
			Log.w(TAG, Integer.toString(doneDelete));
			return doneDelete > 0;		
	}
	
	public long addFoodToDate(String word, String points, String dateString, long dateLong) {
		dateString=dateString.replaceAll("/","");
		long id=-1;
		if (getWordsDate(dateString)==null){
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_DATE_LIST,dateString);
			initialValues.put(KEY_DATE_LIST_LONG, dateLong);
			initialValues.put(KEY_VIEW_VISIBLE, "false");
			initialValues.put(KEY_DAY_TOTAL, 0);
			initialValues.put(KEY_EXTRA, 0);
			id = mDb.insert(FTS_VIRTUAL_TABLE_DATE_LIST, null, initialValues);
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_WORD, word);
		initialValues.put(KEY_POINTS_STRING, points);
		initialValues.put(KEY_DATE_LONG, dateLong);
		initialValues.put(KEY_DATE,dateString);
		mDb.insert(FTS_VIRTUAL_TABLE_DATE, null, initialValues);
		return id;
    }
	
	public void changeDayTotalAndExtra(int dayTotal, int extra, long id){
		ContentValues initialValues = new ContentValues();
		if (dayTotal!=-1){initialValues.put(KEY_DAY_TOTAL, dayTotal);}
		initialValues.put(KEY_EXTRA, extra);
		mDb.update(FTS_VIRTUAL_TABLE_DATE_LIST, initialValues,"rowid = "+ id,null);
	}
	
	public boolean deleteAllDateLists() {
		int doneDelete = 0;
		doneDelete = mDb.delete(FTS_VIRTUAL_TABLE_DATE, null , null);
		mDb.delete(FTS_VIRTUAL_TABLE_DATE_LIST, null , null);//2de lijst
		return doneDelete > 0;		
	}
	
	public boolean deleteFoodList() {
		int doneDelete = 0;
		doneDelete = mDb.delete(FTS_VIRTUAL_TABLE, null , null);
		return doneDelete > 0;		
	}
	
	public void toast(String msg){
        Toast.makeText(this.mCtx, msg, Toast.LENGTH_SHORT).show();
    }

}
