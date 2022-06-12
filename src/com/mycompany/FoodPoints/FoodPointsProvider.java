    package com.mycompany.FoodPoints;

import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.widget.*;
	
    public class FoodPointsProvider extends ContentProvider {
	    String TAG = "FoodPointsProvider";

	    public static String AUTHORITY = "com.example.android.foodpo.FoodPointsProvider";
	    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/foodpoints");
        // MIME types used for searching words or looking up a single definition
	    public static final String WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
	    "/vnd.example.android.foodpo";                            
    	public static final String DEFINITION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
		"/vnd.example.android.foodpo";                            

    	private FoodPointsDatabase mfoodpoints;

		// UriMatcher stuff
		private static final int SEARCH_WORDS = 0;
	    private static final int GET_WORD = 1;
		private static final UriMatcher sURIMatcher = buildUriMatcher();
		
		 //Builds up a UriMatcher for search suggestion and shortcut refresh queries.
        private static UriMatcher buildUriMatcher() {
			UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
			// to get words...
			matcher.addURI(AUTHORITY, "foodpoints", SEARCH_WORDS);
			matcher.addURI(AUTHORITY, "foodpoints/#", GET_WORD);
            return matcher;
		}

		@Override
		public boolean onCreate() {
			mfoodpoints = new FoodPointsDatabase(getContext(),isExternalStorageWritable());
			mfoodpoints.open();
			return true;
		}
        
		@Override
		public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
							String sortOrder) {

			// Use the UriMatcher to see what kind of query we have and format the db query accordingly
			switch (sURIMatcher.match(uri)) {
				case SEARCH_WORDS:
					if (selectionArgs == null) {
						throw new IllegalArgumentException(
							"selectionArgs must be provided for the Uri: " + uri);
					}
					return search(selectionArgs[0]);
				case GET_WORD:
					return getWord(uri);
				default:
					throw new IllegalArgumentException("Unknown Uri: " + uri);
			}
		}

	    private Cursor search(String query) {
			query = query.toLowerCase();
			String[] columns = new String[] {
				BaseColumns._ID,
				FoodPointsDatabase.KEY_WORD,
				FoodPointsDatabase.KEY_POINTS_STRING,
				FoodPointsDatabase.KEY_DAILY};
         	return mfoodpoints.getWordMatches(query, columns);
		}

		private Cursor getWord(Uri uri) {
			String rowId = uri.getLastPathSegment();
			String[] columns = new String[] {
				FoodPointsDatabase.KEY_WORD,
				FoodPointsDatabase.KEY_POINTS_STRING,
				FoodPointsDatabase.KEY_DAILY};
            return mfoodpoints.getWord(rowId, columns);
		}
        // This method is required in order to query the supported types.
		 //It's also useful in our own query() method to determine the type of Uri received.
		@Override
		public String getType(Uri uri) {
			switch (sURIMatcher.match(uri)) {
				case SEARCH_WORDS:
					return WORDS_MIME_TYPE;
				case GET_WORD:
					return DEFINITION_MIME_TYPE;
				default:
					throw new IllegalArgumentException("Unknown URL " + uri);
			}
		}

		@Override
		public Uri insert(Uri uri, ContentValues values) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int delete(Uri uri, String selection, String[] selectionArgs) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
			throw new UnsupportedOperationException();
		}

		public void toast(String msg){
        	Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    	}
		
		/* Checks if external storage is available for read and write */
		public boolean isExternalStorageWritable() {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				return true;
			}
			return false;
		}
	}
