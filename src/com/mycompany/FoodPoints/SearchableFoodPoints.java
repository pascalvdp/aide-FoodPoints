//dagtotaal 26
//weekextra 49

package com.mycompany.FoodPoints;

import android.app.*;
import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.SearchView.*;
import java.io.*;
import android.text.*;
import android.graphics.*;
import android.widget.AdapterView.*;
import android.content.res.*;
import android.view.ContextMenu.*;
import android.view.LayoutInflater.*;
import android.util.*;
import android.widget.ActionMenuView.*;
import android.view.MenuItem.*;
import java.text.*;
import java.util.*;
import android.preference.*;
import android.widget.CalendarView.*;
import android.widget.PopupWindow.*;

public class SearchableFoodPoints extends Activity {

	private SearchView searchView;
    private cursorAdapter cursAdapter;
	private ListView mListView1;
	private ListView mListV1;
	private ListView mListV2;
	private ListView mListViewDate;
	
	private FoodPointsDatabase mDb;
	
	private static final int DIALOG_SAVE = 1;
	private static final int DIALOG_DATE = 2;
	private static final int DIALOG_SAVECHANGES = 3;
	private static final int DIALOG_SAVECHANGES2 = 4;
	private static final int DIALOG_CHOICE = 5;
	private static final int DIALOG_DELETE = 6;
	private static final int DIALOG_EDIT=7;
	private static final int DIALOG_ALL_DATES=8;
	private static final int DIALOG_ALL_DATES_DELETE=9;
	private static final int DIALOG_DAYPOINTSMAX=10;
	private static final int DIALOG_FOODLIST_DELETE=11;
	String strInput1="";
	String strInput2="";
	EditText mInput1;
	EditText mInput2;
	boolean hiddenkeyboard;
	boolean saveChanges=false;
	Cursor cursorGlobal;
	int pos=-1;//positie in cursor bij aanklikken
	boolean clickEnter=false;
	boolean cancel=false;
	MenuItem menuItemCount;
	MenuItem menuItemSearch;
	MenuItem menuItemKeyboard;
	Button buttonBar;
	Button mButtonCount;
	
    //boolean save=false;
	TextView mTextV1;
	Button mButtonDate,mDateButton,mButtonDagTotaal;
	CalendarView calview;
	//indien oncreate wordt toegepast dan wordt dit ook gebruikt
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");//HH:mm:ss
	Calendar cal = Calendar.getInstance();
	Date date=cal.getTime(); //new Date()
	Date date2=date;
	Date date3=date;
	
	int dagTotaalMax;
	int weekExtra;
	
	SimpleCursorAdapter dates;
	//for (int i=0; i<intArray.length; i++)
	SharedPreferences settings;
	
	ProgressDialog progressDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		
		mListV1 = (ListView) findViewById(R.id.list1);
		mListV2 = (ListView) findViewById(R.id.list2);
	
		//toast("onCreate");
		mDb = new FoodPointsDatabase(this,isExternalStorageWritable());
		mDb.open();
		
		handleIntent(getIntent());
		
		mTextV1=(TextView) findViewById(R.id.textv1);
		mButtonDate=(Button) findViewById(R.id.buttondate);
		mDateButton=(Button) findViewById(R.id.datebutton);//button datum vandaag
		mButtonDagTotaal=(Button) findViewById(R.id.buttondagtotaal);
		showlist1(false,false);
		calview=(CalendarView) findViewById(R.id.calendarview);

		calview.setOnDateChangeListener(new OnDateChangeListener(){
				public void onSelectedDayChange(CalendarView view,int year,int month,
												int day){							
					cal.set(year,month,day);
					date =cal.getTime();
		            mButtonDate.setText(dateFormat.format(date));
					showlist2(false,false);
					savePreferences("time",date.getTime());
				}
		});	
	}

	@Override
	protected void onStart(){
		super.onStart();
		//toast("onStart");
		loadSavedPreferences();
		//omzetten naar String en dateFormat enkel datum als men wil weten wanneer
		//er een nieuwe dag is
		String dateTwo=dateFormat.format(date2);
		String dateThree=dateFormat.format(date3);
		if (dateTwo.compareTo(dateThree)<0){//als dateTwo voor dateThree komt
			date=date2=date3;
		    savePreferences("time2",date2.getTime());
			savePreferences("time",date.getTime());
			toast("Een nieuwe dag");
		}
		else{savePreferences("time2",date2.getTime());}
		cal.setTime(date);
		mButtonDate.setText(dateFormat.format(date));
		
		if (dagTotaalMax==-1 || weekExtra==-1){
			showDialog(DIALOG_DAYPOINTSMAX);
		}
		showlist2(false,false);
	}
	
	private void loadSavedPreferences(){
	    date=new Date(settings.getLong("time",date.getTime()));
		date2=new Date(settings.getLong("time2",date2.getTime()));
		dagTotaalMax=settings.getInt("dagTotaalMax",-1);
		weekExtra=settings.getInt("weekExtra",-1);
	}

	private void savePreferences(String key,long value){
		SharedPreferences.Editor editor=settings.edit();
		editor.putLong(key,value);
		editor.commit();
	}
	private void savePreferences(String key,int value){
		SharedPreferences.Editor editor=settings.edit();
		editor.putInt(key,value);
		editor.commit();
	}
	
	public void onClick(View p1){
		switch (p1.getId()) {
			case R.id.button1: 
			    cal.add(Calendar.DATE, -1);
				calview.setDate(cal.getTimeInMillis());
				date =cal.getTime();
				mButtonDate.setText(dateFormat.format(date));
				showlist2(false,false);
				savePreferences("time",date.getTime());
				break;
			case R.id.button2: 
			    cal.add(Calendar.DATE, 1);
				calview.setDate(cal.getTimeInMillis());
				date =cal.getTime();
				mButtonDate.setText(dateFormat.format(date));
				showlist2(false,false);
				savePreferences("time",date.getTime());
				break;
			case R.id.buttondate:		
				if (mListV1.getVisibility()==View.GONE){
					calview.setVisibility(View.GONE);
					mTextV1.setVisibility(View.VISIBLE);
					mListV1.setVisibility(View.VISIBLE);
					mDateButton.setVisibility(View.GONE);
				}
				else{
			     	calview.setVisibility(View.VISIBLE);
					mDateButton.setVisibility(View.VISIBLE);
					mTextV1.setVisibility(View.GONE);
					mListV1.setVisibility(View.GONE);
					long ti=cal.getTimeInMillis();
					cal.add(Calendar.DATE, 1);
					calview.setDate(cal.getTimeInMillis());
					cal.setTimeInMillis(ti);
					calview.setDate(cal.getTimeInMillis());
				}		
				break;
			case R.id.datebutton:
			    cal.add(Calendar.DATE, 1);
				calview.setDate(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				calview.setDate(cal.getTimeInMillis());
				date =cal.getTime();
				mButtonDate.setText(dateFormat.format(date));
				showlist2(false,false);
				savePreferences("time",date.getTime());
				break;
			case R.id.buttondatelist:
				showDialog(DIALOG_ALL_DATES);
				break;
			case R.id.buttondagtotaal:
				showDialog(DIALOG_DAYPOINTSMAX);
				break;
		}
	}
	
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {  //ENKEL DATA DOORGEVEN NAAR ANDERE CLASS
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {} //dit gebruiken we enkel om keyboard te doen verdwijnen	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
	
		LayoutInflater baseInflater = (LayoutInflater)getBaseContext()
		   .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inflate your custom view.
		View myCustomView = baseInflater.inflate(R.layout.buttonbar, null);
		menuItemCount=menu.findItem(R.id.menu_count).setActionView(myCustomView);
		menuItemCount.setVisible(false);
	    buttonBar=(Button)myCustomView.findViewById(R.id.buttonBar);
		buttonBar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					//searchView.setQuery(searchView.getQuery().toString(),false);
				}		
		});
		buttonBar.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1)
				{
					String str="Aantal overeenkomsten";
					Toast tst=Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT);
					tst.setGravity(Gravity.TOP,295,75);
					tst.show();
					return true;
				}
		});
		menuItemSearch=menu.findItem(R.id.search);
		menuItemSearch.setOnActionExpandListener(new OnActionExpandListener(){

				@Override
				public boolean onMenuItemActionExpand(MenuItem p1)
				{
					menuItemCount.setVisible(true);
					menuItemKeyboard.setVisible(true);
					return true;
				}
				@Override
				public boolean onMenuItemActionCollapse(MenuItem p1)
				{
					menuItemCount.setVisible(false);
					menuItemKeyboard.setVisible(false);
					return true;
				}
		});
		menuItemKeyboard=menu.findItem(R.id.menu_keyboard);	
		menuItemKeyboard.setVisible(false);
		
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
			searchView.setMaxWidth(800);
			
			searchView.setOnSuggestionListener(new OnSuggestionListener(){

					@Override
					public boolean onSuggestionSelect(int p1)
					{
						return false;// false;
					}

					@Override
					public boolean onSuggestionClick(int p1)
					{
						pos=p1;
						showDialog(DIALOG_CHOICE);
						return false; //na dit ga je naar Intent.ACTION_VIEW
					}
			}); 
			searchView.setOnQueryTextListener(new OnQueryTextListener(){

					@Override
					public boolean onQueryTextSubmit(String p1)
					{// toetsenbord=begin
						return false;  //false
					}

                    @Override
					public boolean onQueryTextChange(String query)//wordt constant aangepast
					{
						loadingData(query, searchView);
						return true;
					}
			});
		}
        return true;//false=geen menus bovenaan
    }

	private void loadingData(String query, SearchView searchV)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Cursor cursor = managedQuery(FoodPointsProvider.CONTENT_URI, null, null,
										 new String[] {query}, null);

			if (query.compareTo(" ") == 0) {cursor=mDb.getAllWords();}				 
		    cursAdapter=new cursorAdapter(this, cursor);
			searchV.setSuggestionsAdapter(cursAdapter);		
			buttonBar.setText(String.valueOf(cursAdapter.getCount()));	
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
			case R.id.save:
				showDialog(DIALOG_SAVE);
				return true;
            case R.id.menu_keyboard:
				if (searchView.findFocus()!=null){
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); //tonen en verbergen keyboard
					searchView.setQuery(searchView.getQuery().toString(),false);
				}
                return true;
			case R.id.delete:
				showDialog(DIALOG_FOODLIST_DELETE);
				return true;	
            default:
                return false;
        }
    }

	private void showResults(String query) {

        Cursor cursor = managedQuery(FoodPointsProvider.CONTENT_URI, null, null,
									 new String[] {query}, null);							 
		if (cursor == null) {
            mListView1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0));
			mButtonCount.setText("0");
        } else {
			int count=cursor.getCount();
			int height=count * 80;
			if (height>135){height=135;}			
			mListView1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
			mButtonCount.setText(String.valueOf(count));
            // Specify the columns we want to display in the result
            String[] from = new String[] { FoodPointsDatabase.KEY_WORD,
				FoodPointsDatabase.KEY_POINTS_STRING};

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.word,
				R.id.points};

            // Create a simple cursor adapter for the words and apply them to the ListView
		    SimpleCursorAdapter words = new SimpleCursorAdapter(this,
																R.layout.result, cursor, from, to);
           mListView1.setAdapter(words);
			words.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
					private int i=0;
					private int[] col = new int[3]; //0 1 2
					@Override
				    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
						TextView tv = (TextView)view;
						i+=1;
						if (i<3){ col[columnIndex]=tv.getCurrentTextColor();}
						String str=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
						if (str.equals("0")) {
							tv.setTextColor(Color.GREEN);
							tv.setText(cursor.getString(columnIndex));
							return true;
						}
						else{
					        tv.setTextColor(col[columnIndex]);
						    tv.setText(cursor.getString(columnIndex));
							return true;
						}
					}
				});
            // Define the on-click listener for the list items
            mListView1.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					   	Uri uri = Uri.withAppendedPath(FoodPointsProvider.CONTENT_URI,
														String.valueOf(id));
						Cursor cursor = managedQuery(uri, null, null, null, null);
						if (cursor == null) {
							finish();
						} else {
							cursor.moveToFirst();
							strInput1=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_WORD));
							mInput1.setText(strInput1);
							mInput1.setSelection(strInput1.length()); //zet cursor achteraan
							mInput1.requestFocus();
						}
					}
				});
        }
    }
	
	private void showlist1(final boolean itemAdded, boolean itemDeleted){
		int firstVisiblePosition=0;
		if (mListV1!=null && itemDeleted){firstVisiblePosition=mListV1.getFirstVisiblePosition();}
		Cursor cursor = mDb.getWordsDaily();
		if (cursor == null) {
			mListV1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0));
        } else {
			mListV1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            // Specify the columns we want to display in the result
            String[] from = new String[] { FoodPointsDatabase.KEY_WORD,
				FoodPointsDatabase.KEY_POINTS_STRING};

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.word,
				R.id.points};

            // Create a simple cursor adapter for the words and apply them to the ListView
		    SimpleCursorAdapter words = new SimpleCursorAdapter(this,
																R.layout.result, cursor, from, to);
			mListV1.setAdapter(words);
			words.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
					private int i=0;
					private int[] col = new int[3]; //0 1 2
					@Override
				    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
						TextView tv = (TextView)view;
						i+=1;
						if (i<3){ col[columnIndex]=tv.getCurrentTextColor();}
						String str=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
						if (str.equals("0")) {
							tv.setTextColor(Color.GREEN);
							tv.setText(cursor.getString(columnIndex));
							return true;
						}
						else{
					        tv.setTextColor(col[columnIndex]);
						    tv.setText(cursor.getString(columnIndex));
							return true;
						}
					}
			});
			
            // Define the on-click listener for the list items
            mListV1.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					   	Uri uri = Uri.withAppendedPath(FoodPointsProvider.CONTENT_URI,
													   String.valueOf(id));
						Cursor cursor = managedQuery(uri, null, null, null, null);
						String str1=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_WORD));
						String str2=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
						mDb.addFoodToDate(str1,str2,mButtonDate.getText().toString(), cal.getTimeInMillis());
						setFoodToDateList(mButtonDate.getText().toString(), cal.getTimeInMillis());	
						showlist2(true,false);
						toast("Item toegevoegd aan datum lijst");						
					}
			});
			mListV1.setOnItemLongClickListener(new OnItemLongClickListener(){

					@Override
					public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, final long id)
					{
						PopupMenu popup = new PopupMenu(SearchableFoodPoints.this, p2);
						popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

								@Override
								public boolean onMenuItemClick(MenuItem item)
								{
									switch (item.getItemId()) {
										case R.id.item1:
											mDb.changeDaily("false",id);
											loadingData(searchView.getQuery().toString(),searchView);//update adapter
											showlist1(false,true);
											toast("Item verwijderd uit dagelijkse lijst");
											return true;
										default:
											return false;
									}	
								}
						});
						popup.show();
						return true;
					}
			});
			if (itemDeleted){mListV1.setSelection(firstVisiblePosition);}
			if (itemAdded && cursor!=null){
				long itemId=cursAdapter.getItemId(pos);
				do{
					long longId=cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
					if (itemId==longId){
						mListV1.setSelection(cursor.getPosition());
						break;
					}					
				}while (cursor.moveToNext());			
			}
        }
	}
	
	private void showlist2(final boolean itemAdded,boolean itemDeleted){
		int firstVisiblePosition=0;
		if (mListV2!=null){firstVisiblePosition=mListV2.getFirstVisiblePosition();}
		String strDate=mButtonDate.getText().toString();
		strDate=strDate.replaceAll("/","");
		Cursor cursor = mDb.getWordsDate(strDate);
		final String strD=strDate;
	
		Point size=new Point();
		this.getWindowManager().getDefaultDisplay().getSize(size); 
		int screenHeight = size.y;
		int ywaarde=screenHeight-250;			
		mListV2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ywaarde));
		
        // Specify the columns we want to display in the result
        String[] from = new String[] { FoodPointsDatabase.KEY_WORD,
		     FoodPointsDatabase.KEY_POINTS_STRING};

        // Specify the corresponding layout elements where we want the columns to go
        int[] to = new int[] { R.id.word,
			  R.id.points};

        // Create a simple cursor adapter for the words and apply them to the ListView
		SimpleCursorAdapter words = new SimpleCursorAdapter(this,
											  R.layout.result, cursor, from, to);
		mListV2.setAdapter(words);
		words.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
				
					private int i=0;
					private int[] col = new int[3]; //0 1 2
					@Override
				    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
						
						TextView tv = (TextView)view;
						i+=1;
						if (i<3){ col[columnIndex]=tv.getCurrentTextColor();}
						String str=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
						if (str.equals("0")) {
							tv.setTextColor(Color.GREEN);
							tv.setText(cursor.getString(columnIndex));
							return true;
						}
						else{
					        tv.setTextColor(col[columnIndex]);
						    tv.setText(cursor.getString(columnIndex));
							return true;
						}
					}
		});
		mListV2.setOnItemLongClickListener(new OnItemLongClickListener(){

					@Override
					public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, final long id)
					{
						PopupMenu popup = new PopupMenu(SearchableFoodPoints.this, p2);
						popup.getMenuInflater().inflate(R.menu.popup_menu_date, popup.getMenu());
						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

								@Override
								public boolean onMenuItemClick(MenuItem item)
								{
									switch (item.getItemId()) {
										case R.id.item1:
											mDb.deleteFoodDate(id);
											setFoodToDateList(mButtonDate.getText().toString(), cal.getTimeInMillis());	
											showlist2(false,true);
											toast("Item verwijderd uit datum lijst");
											if (mDb.getWordsDate(strD)==null){
												Cursor cursDateList=mDb.getDateList(strD);
												long lo=cursDateList.getLong(cursDateList.getColumnIndex(BaseColumns._ID));
												cursDateList.close();
												mDb.deleteFoodDateList(lo);
											}
											return true;
										default:
											return false;
									}	
								}
							});
						popup.show();
						return true;
					}
		});
		//dagtotaal en weekextra
		String strTot="0";
		String strExtra="0";
		Cursor curs=mDb.getDateList(strDate);
		if (curs==null){//indien geen items dan toch nog strExtra vastleggen
			Calendar calen=Calendar.getInstance();
			calen.setTimeInMillis(cal.getTimeInMillis());
			while(calen.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
				calen.add(Calendar.DATE, -1);
			}
			String[] daysOfWeek = new String[8];//0 tot 7
			for (int n = 1; n <= 7; n++){
				String othDate=dateFormat.format(calen.getTime());
				daysOfWeek[n]=othDate.replaceAll("/","");
				Cursor cur = mDb.getDateList(daysOfWeek[n]);
				if (cur!=null){
					strExtra = cur.getString(cur.getColumnIndex(FoodPointsDatabase.KEY_EXTRA));
					break;
				}
			}	
		}else{
			strTot = curs.getString(curs.getColumnIndex(FoodPointsDatabase.KEY_DAY_TOTAL));
			strExtra = curs.getString(curs.getColumnIndex(FoodPointsDatabase.KEY_EXTRA));
			curs.close();
		}
		String strDagTotaal="Dagtotaal = " + strTot + "/" + dagTotaalMax +
			"    Weekextra = " + strExtra + "/" + weekExtra;
		mButtonDagTotaal.setText(strDagTotaal);
		//setselection en scrollen...
		final int firstPos=firstVisiblePosition;
		if (itemDeleted){mListV2.setSelection(firstPos);}
		mListV2.post(new Runnable(){

				@Override
				public void run()
				{
					if (itemAdded){
						mListV2.setSelection(firstPos);
						int pos=mListV2.getAdapter().getCount()-1;
						mListV2.smoothScrollToPosition(pos);
					}	
				}
		});			
	}

	private void showlistdate(){
		int firstVisiblePosition=0;
		if (mListViewDate!=null){firstVisiblePosition=mListViewDate.getFirstVisiblePosition();}
		
			Cursor cursor = mDb.getAllDates();
		
            // Specify the columns we want to display in the result
		String[] from = new String[] { FoodPointsDatabase.KEY_DATE_LIST,FoodPointsDatabase.KEY_DAY_TOTAL};
		
            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] {R.id.datelist,R.id.wordpoints};

            // Create a simple cursor adapter for the words and apply them to the ListView
		    dates = new SimpleCursorAdapter(this,R.layout.resultdate, cursor, from, to);											
			mListViewDate.setAdapter(dates);
			dates.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

					@Override
				    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
						TextView tvDate = (TextView)view;//.findViewById(R.id.datelist);dit werkt hier niet
						
					 	String strDate=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_DATE_LIST));
						String strDa=strDate.substring(0,2)+ "/" + strDate.substring(2,4)+ "/" + 
							 strDate.substring(4,8)+ " :";
						if(columnIndex==cursor.getColumnIndex(FoodPointsDatabase.KEY_DATE_LIST)){tvDate.setText(strDa);}	
						
						String strTot=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_DAY_TOTAL));
						String strExtra=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_EXTRA));
						String str="Dagtotaal = " + strTot + "/" + dagTotaalMax +
								"    Weekextra = " + strExtra + "/" + weekExtra;
						String strView=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_VIEW_VISIBLE));
						boolean visible=Boolean.valueOf(strView);
						if (visible){
							Cursor curs=mDb.getWordsDate(strDate);
							String wp="";
							do{
								String word=curs.getString(curs.getColumnIndex(FoodPointsDatabase.KEY_WORD));
								String points=curs.getString(curs.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
								wp=wp + "\n" + word + "\n" + points;
							}while (curs.moveToNext());
							
							str=str + wp;
							if(columnIndex==cursor.getColumnIndex(FoodPointsDatabase.KEY_DAY_TOTAL)){tvDate.setText(str);}
						}else{
							
							if(columnIndex==cursor.getColumnIndex(FoodPointsDatabase.KEY_DAY_TOTAL)){tvDate.setText(str);}
						}
						return true;
					}
			});
			mListViewDate.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int intPos, long id)
				{
					Cursor cur=mDb.getViewVisibility(String.valueOf(id));
					String str=cur.getString(cur.getColumnIndex(FoodPointsDatabase.KEY_VIEW_VISIBLE));
					boolean visible=Boolean.valueOf(str);
					if (visible){
						mDb.changeViewVisibility("false", id);
					}else{
						mDb.changeViewVisibility("true", id);
					}
					showlistdate();
				}
			});
			//de longclicklistener zit in de dialog
			
		//setselection
		mListViewDate.setSelection(firstVisiblePosition);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
			
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
			case DIALOG_SAVE:
                View promptsView = layoutInflater.inflate(R.layout.save, null);
		
				mInput1 = (EditText)promptsView.findViewById(R.id.input1);
				mInput2 = (EditText)promptsView.findViewById(R.id.input2);
				
				mListView1 = (ListView)promptsView.findViewById(R.id.listviewdialog);
				mListView1.setFocusable(false);
				
				mButtonCount= (Button)promptsView.findViewById(R.id.count);
				mButtonCount.setOnLongClickListener(new OnLongClickListener(){

						@Override
						public boolean onLongClick(View p1)
						{
							toast("Aantal overeenkomsten");
							return false;
						}
				});
							
				hiddenkeyboard=true;
				if (searchView.findFocus()!=null){hiddenkeyboard=false;}
			    
				String query=searchView.getQuery().toString().trim();
				if (searchView.findFocus()!=null && strInput1.isEmpty() && strInput2.isEmpty()){mInput1.setText(query);}
				if (!strInput1.isEmpty() || !strInput2.isEmpty()){
					mInput1.setText(strInput1);
					mInput2.setText(strInput2);
				}
				strInput1=mInput1.getText().toString().trim();
				strInput2=mInput2.getText().toString();
				if (strInput1.isEmpty()){mInput1.requestFocus();}else{mInput2.requestFocus();}
				showResults(strInput1);
	            builder
					.setView(promptsView)// set save.xml to be the layout file of the alertdialog builder
					.setCancelable(true)
					.setTitle("Nieuw item")
					.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface p1)
						{
						 if (hiddenkeyboard){
						   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						 }
						 else{
						   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						 }
						 if (saveChanges){
							 showDialog(DIALOG_SAVECHANGES);saveChanges=false;
						 }
						}
					})
					.setOnCancelListener(new DialogInterface.OnCancelListener(){
						
						@Override
						public void onCancel(DialogInterface p1)
						{	
							strInput1="";
							strInput2="";
							toast("Niets opgeslagen");
						}
					})
					.setPositiveButton("Save", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							strInput1=mInput1.getText().toString().trim();
							strInput2=mInput2.getText().toString();
							//controle of strInput1 nog niet is opgeslagen
							Cursor cursor = managedQuery(FoodPointsProvider.CONTENT_URI, null, null,
														 new String[] {strInput1}, null);
							
							if (stringIsInCursor(strInput1,cursor)){
								saveChanges=true;hiddenkeyboard=true;
								cursorGlobal=cursor;
							}
							else{
								mDb.addFood(strInput1,strInput2);
								loadingData(strInput1,searchView);//update adapter
								searchView.setQuery(strInput1,false);
								strInput1="";
								strInput2="";
					    		toast("Nieuw item opgeslagen");
							}
						}
					})
					.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {	
						  dialog.cancel();
						}
					});
				final AlertDialog dialog = builder.create();
				dialog.show();
				
				mInput1.setOnKeyListener(new OnKeyListener(){

						@Override
						public boolean onKey(View p1, int keyCode, KeyEvent event)
						{
							if ((event.getAction()==KeyEvent.ACTION_DOWN) && 
								(keyCode==KeyEvent.KEYCODE_ENTER)){
								clickEnter=true;
								mInput2.requestFocus();
								mInput2.setSelection(strInput2.length());								
							}
							return false;//false
						}	
					});
	
				mInput2.setOnFocusChangeListener(new OnFocusChangeListener(){

						@Override
						public void onFocusChange(View p1, final boolean hasFocus)
						{
							mInput2.post(new Runnable() { //wordt later toegepast
									@Override
									public void run() {
										if (hasFocus && clickEnter){         //keyboard wordt verwijderd of verschijnt terug
											InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
											imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
											clickEnter=false;
										}
									}
							});
						}
				});
				if (buttonSaveEnabled()){
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
				else{
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
				//check button positive
			    mInput1.addTextChangedListener(new TextWatcher(){
					    @Override
						public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void afterTextChanged(Editable p1)
						{
							strInput1=mInput1.getText().toString().trim();
							strInput2=mInput2.getText().toString();
							showResults(strInput1);
							if (buttonSaveEnabled()){
								dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
							else{
								dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
						}
				});
				mInput2.addTextChangedListener(new TextWatcher(){
					    @Override
						public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void afterTextChanged(Editable p1)
						{
							strInput1=mInput1.getText().toString().trim();
							strInput2=mInput2.getText().toString();
						    if (strInput2.length()>3){
								strInput2=strInput2.substring(0,3);mInput2.setText(strInput2);
								mInput2.setSelection(strInput2.length()); //zet cursor achteraan
							}
							if (buttonSaveEnabled()){
								dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
							else{
								dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
						}
				});
			break;
			case DIALOG_CHOICE:
				View choiceView = layoutInflater.inflate(R.layout.choice, null);

				Button mButtonDelete = (Button)choiceView.findViewById(R.id.delete);
				Button mButtonEdit = (Button)choiceView.findViewById(R.id.edit);
				Button mButtonDaily = (Button)choiceView.findViewById(R.id.daily);
				Button mButtonDates = (Button)choiceView.findViewById(R.id.date);
				
				Cursor curs=cursAdapter.getCursor();
				curs.moveToPosition(pos);
				String strDaily=curs.getString(curs.getColumnIndex(FoodPointsDatabase.KEY_DAILY));
				boolean boolDaily=Boolean.valueOf(strDaily);	
				if(boolDaily){mButtonDaily.setVisibility(View.GONE);}else{mButtonDaily.setVisibility(View.VISIBLE);}
		
				builder
					.setView(choiceView)
					.setCancelable(true)
					.setTitle(getStringTitleDialog());
				 	
				final AlertDialog dialogChoice = builder.create();
				dialogChoice.show();
							
				mButtonDelete.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							showDialog(DIALOG_DELETE);
							dialogChoice.dismiss();
						}
				});
				mButtonEdit.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							Cursor cur=cursAdapter.getCursor();
							cur.moveToPosition(pos);
							strInput1=cur.getString(cur.getColumnIndex(FoodPointsDatabase.KEY_WORD));
							strInput2=cur.getString(cur.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
							//cur.close();
							showDialog(DIALOG_EDIT);
							dialogChoice.dismiss();
						}
				});
				mButtonDaily.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							mDb.changeDaily("true",cursAdapter.getItemId(pos));//true
							loadingData(searchView.getQuery().toString(),searchView);//update adapter
							showlist1(true,false);
							toast("Item toegevoegd aan dagelijkse lijst");
							dialogChoice.dismiss();
						}
				});
				mButtonDates.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							Cursor cur=cursAdapter.getCursor();
							cur.moveToPosition(pos);
							String str1=cur.getString(cur.getColumnIndex(FoodPointsDatabase.KEY_WORD));
							String str2=cur.getString(cur.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
							mDb.addFoodToDate(str1,str2,mButtonDate.getText().toString(), cal.getTimeInMillis());
							setFoodToDateList(mButtonDate.getText().toString(), cal.getTimeInMillis());		
							loadingData(searchView.getQuery().toString(),searchView);//update adapter
							showlist2(true,false);
							toast("Item toegevoegd aan datum lijst");
							dialogChoice.dismiss();
						}
				});
			break;	
			case DIALOG_DELETE:
				builder
					.setTitle(getStringTitleDialog())
					.setMessage("Verwijderen uit voedsellijst")
					.setCancelable(true)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mDb.deleteFood(cursAdapter.getItemId(pos));
							loadingData(searchView.getQuery().toString(),searchView);//update adapter
							showlist1(false,false);
							toast("item verwijderd");
						}
					})
					.setNegativeButton("Annuleren",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							dialog.cancel();
						}
					})
					.setOnCancelListener(new DialogInterface.OnCancelListener(){

						@Override
						public void onCancel(DialogInterface p1)
						{
							showDialog(DIALOG_CHOICE);
						}
					});	
				AlertDialog dialogDelete = builder.create();
				dialogDelete.show();
			break;
			case DIALOG_EDIT:				
				View editView = layoutInflater.inflate(R.layout.save, null);

				TextView tv=(TextView)editView.findViewById(R.id.textView1); 
				tv.setText("Te bewerken voedsel :");
				
				mInput1 = (EditText)editView.findViewById(R.id.input1);
				mInput2 = (EditText)editView.findViewById(R.id.input2);

				mListView1 = (ListView)editView.findViewById(R.id.listviewdialog);
				mListView1.setFocusable(false);

				mButtonCount = (Button)editView.findViewById(R.id.count);
				mButtonCount.setOnLongClickListener(new OnLongClickListener(){

						@Override
						public boolean onLongClick(View p1)
						{
							toast("Aantal overeenkomsten");
							return false;
						}
				});
				
				mInput1.setText(strInput1);
				mInput2.setText(strInput2);
				mInput1.requestFocus();//zo komt het cursor-teken ook achteraan te staan
				
				showResults(strInput1);
	            builder
					.setView(editView)// set save.xml to be the layout file of the alertdialog builder
					.setCancelable(true)
					.setTitle(getStringTitleDialog())
					.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface p1)
						{
							if (saveChanges){
								getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
								showDialog(DIALOG_SAVECHANGES2);saveChanges=false;
							}
							if(cancel){
								getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
								showDialog(DIALOG_CHOICE);
								cancel=false;
							}
						}
					})
					.setOnCancelListener(new DialogInterface.OnCancelListener(){

						@Override
						public void onCancel(DialogInterface p1)
						{	
							cancel=true;				
							strInput1="";
							strInput2="";
							toast("Niets opgeslagen");
						}
					})
					.setPositiveButton("Save", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							strInput1=mInput1.getText().toString().trim();
							strInput2=mInput2.getText().toString();
							saveChanges=true;
						}
					})
					.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {	
							dialog.cancel();
						}
					});
				final AlertDialog dialogEdit = builder.create();
				dialogEdit.show();
						
				mInput1.setOnKeyListener(new OnKeyListener(){

						@Override
						public boolean onKey(View p1, int keyCode, KeyEvent event)
						{
							if ((event.getAction()==KeyEvent.ACTION_DOWN) && 
								(keyCode==KeyEvent.KEYCODE_ENTER)){
								clickEnter=true;
								mInput2.requestFocus();
								mInput2.setSelection(strInput2.length());								
							}
							return false;//false
						}	
					});

				mInput2.setOnFocusChangeListener(new OnFocusChangeListener(){

						@Override
						public void onFocusChange(View p1, final boolean hasFocus)
						{
							mInput2.post(new Runnable() { //wordt later toegepast
									@Override
									public void run() {
										if (hasFocus && clickEnter){         //keyboard wordt verwijderd of verschijnt terug
											InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
											imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
											clickEnter=false;
										}
									}
								});
						}
				});
				
				if (buttonSaveEnabled()){
					dialogEdit.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
				else{
					dialogEdit.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
				//check button positive
			    mInput1.addTextChangedListener(new TextWatcher(){
					    @Override
						public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void afterTextChanged(Editable p1)
						{
							strInput1=mInput1.getText().toString().trim();
							strInput2=mInput2.getText().toString();
							showResults(strInput1);
							
							Cursor cursor = managedQuery(FoodPointsProvider.CONTENT_URI, null, null,
														 new String[] {strInput1}, null);
					
							if (buttonSaveEnabled() && !stringIsInCursorExceptPos(strInput1,cursor,cursAdapter.getItemId(pos))){
								dialogEdit.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
							else{
								dialogEdit.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
						}
					});
				mInput2.addTextChangedListener(new TextWatcher(){
					    @Override
						public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void afterTextChanged(Editable p1)
						{
							strInput1=mInput1.getText().toString().trim();
							strInput2=mInput2.getText().toString();
						    if (strInput2.length()>3){
								strInput2=strInput2.substring(0,3);mInput2.setText(strInput2);
								mInput2.setSelection(strInput2.length()); //zet cursor achteraan
							}
							
							Cursor cursor = managedQuery(FoodPointsProvider.CONTENT_URI, null, null,
														 new String[] {strInput1}, null);
														 
							if (buttonSaveEnabled()&& !stringIsInCursorExceptPos(strInput1,cursor,cursAdapter.getItemId(pos))){
								dialogEdit.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
							else{
								dialogEdit.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
						}
					});
				break;
			case DIALOG_DATE://dit is nog niet in gebruik
				View viewDate = layoutInflater.inflate(R.layout.date, null);
				builder
					.setView(viewDate)
					.setMessage("testertje")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//do something
						}
					})
					.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							//dialog.cancel();
						}
					});
				AlertDialog dialogDate = builder.create();
				WindowManager.LayoutParams wmlp = dialogDate.getWindow().getAttributes();
				wmlp.gravity=Gravity.LEFT;
				wmlp.x=30;
				//	wmlp.y=100;
				dialogDate.show();
			break;
			case DIALOG_SAVECHANGES:
				View viewChanges = layoutInflater.inflate(R.layout.savechanges, null);
				Cursor cursor=cursorGlobal;
				String string1=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_WORD));
				String string2=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
				final long _id=cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
				cursor.close();cursorGlobal.close();
				TextView tv1 = (TextView)viewChanges.findViewById(R.id.word1);
				tv1.setText(string1);
				TextView tv2 = (TextView)viewChanges.findViewById(R.id.points1);
				tv2.setText(string2);
				TextView tv3 = (TextView)viewChanges.findViewById(R.id.word2);
				tv3.setText(strInput1);
				TextView tv4 = (TextView)viewChanges.findViewById(R.id.points2);
				tv4.setText(strInput2);
				
				builder
					.setView(viewChanges)
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mDb.changeFood(strInput1,strInput2,_id);				
							loadingData(strInput1,searchView);//update adapter
							showlist1(false,false);
							searchView.setQuery(strInput1,false);
							strInput1="";
							strInput2="";
							toast("Item aangepast");
						}
					})
					.setNegativeButton("BACK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							showDialog(DIALOG_SAVE);
						}
					});
				AlertDialog dialogChanges = builder.create();
				dialogChanges.show();
				break;
			case DIALOG_SAVECHANGES2:
				View viewChanges2 = layoutInflater.inflate(R.layout.savechanges, null);
				Cursor cur=cursAdapter.getCursor();
				cur.moveToPosition(pos);
				string1=cur.getString(cur.getColumnIndex(FoodPointsDatabase.KEY_WORD));
				string2=cur.getString(cur.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
			
				tv1 = (TextView)viewChanges2.findViewById(R.id.word1);
				tv1.setText(string1);
				tv2 = (TextView)viewChanges2.findViewById(R.id.points1);
				tv2.setText(string2);
				tv3 = (TextView)viewChanges2.findViewById(R.id.word2);
				tv3.setText(strInput1);
				tv4 = (TextView)viewChanges2.findViewById(R.id.points2);
				tv4.setText(strInput2);

				builder
					.setView(viewChanges2)
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mDb.changeFood(strInput1,strInput2,cursAdapter.getItemId(pos));				
							loadingData(strInput1,searchView);//update adapter
							showlist1(false,false);
							searchView.setQuery(strInput1,false);
							strInput1="";
							strInput2="";
							toast("Item aangepast");
						}
					})
					.setNegativeButton("BACK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							showDialog(DIALOG_EDIT);
						}
					});
				AlertDialog dialogChanges2 = builder.create();
				dialogChanges2.show();
				break;
			case DIALOG_ALL_DATES:
				View viewAllDates = layoutInflater.inflate(R.layout.resultalldates, null);
				TextView tvEmpty = (TextView)viewAllDates.findViewById(R.id.empty);
				Button but = (Button)viewAllDates.findViewById(R.id.buttondatelistsdelete);
				mListViewDate = (ListView)viewAllDates.findViewById(R.id.listviewdatedialog);
				showlistdate();
				if (dates.isEmpty()){
					but.setVisibility(View.GONE);
				}else{
					tvEmpty.setVisibility(View.GONE);
				}
				builder
					.setView(viewAllDates)
					.setCancelable(true);
				final AlertDialog dialogAllDates = builder.create();
				dialogAllDates.show();
				but.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							showDialog(DIALOG_ALL_DATES_DELETE);
							dialogAllDates.dismiss();
						}
				});
				mListViewDate.setOnItemLongClickListener(new OnItemLongClickListener(){

						@Override
						public boolean onItemLongClick(AdapterView<?> p1, final View view, int p3, final long id)
						{
							PopupMenu popup = new PopupMenu(SearchableFoodPoints.this, view);
							popup.getMenuInflater().inflate(R.menu.popup_menu_date, popup.getMenu());
							popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

									@Override
									public boolean onMenuItemClick(MenuItem item)
									{
										switch (item.getItemId()) {
											case R.id.item1:
												TextView tv=(TextView)view.findViewById(R.id.datelist);
												String date1=tv.getText().toString();
												date1=date1.replaceAll(":","");
												String date=date1.replaceAll("/","");
												Cursor curs = mDb.getWordsDate(date);
												if (curs!=null){
													do{
														long lo=curs.getLong(curs.getColumnIndex(BaseColumns._ID));
														mDb.deleteFoodDate(lo);			
													}while (curs.moveToNext());
													curs.close();
												}
												Cursor cursor = mDb.getDateList(date);
												long longDate=cursor.getLong(cursor.getColumnIndex(FoodPointsDatabase.KEY_DATE_LIST_LONG));
												mDb.deleteFoodDateList(id);
												setFoodToDateList(date, longDate);										
												showlistdate();
												if (dates.isEmpty()){
													dialogAllDates.dismiss();
													showDialog(DIALOG_ALL_DATES);
												}
												showlist2(false,true);
												String toas="Alle items verwijderd van " + date1;
												toast(toas);
												return true;
											default:
												return false;
										}	
									}
								});
							popup.show();
							return true;
						}
					});
				break;	
			case DIALOG_ALL_DATES_DELETE:
				builder
					.setTitle("Alle datumlijsten verwijderen?")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mDb.deleteAllDateLists();
							showlist2(false,false);
							showDialog(DIALOG_ALL_DATES);
							toast("Datumlijsten verwijderd");
						}
					})
					.setNegativeButton("BACK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							showDialog(DIALOG_ALL_DATES);
						}
					});
				AlertDialog dialogAllDatesDelete = builder.create();
				dialogAllDatesDelete.show();
				break;
			case DIALOG_FOODLIST_DELETE:
				builder
					.setTitle("Voedsellijst verwijderen?")
					.setOnCancelListener(new DialogInterface.OnCancelListener(){

											 @Override
											 public void onCancel(DialogInterface p1)
											 {
												 toast("Voedsellijst niet verwijderd");
											 }
					})	
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mDb.deleteFoodList();
							loadingData(searchView.getQuery().toString(),searchView);//update adapter
							showlist1(false,false);
							toast("Voedsellijst verwijderd");
						}
					})
					.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							dialog.cancel();
						}
					});
				AlertDialog dialogFoodListDelete = builder.create();
				dialogFoodListDelete.show();
				break;
			case DIALOG_DAYPOINTSMAX:
                View viewDayPointsMax = layoutInflater.inflate(R.layout.daypointsmax, null);
				mInput1 = (EditText)viewDayPointsMax.findViewById(R.id.inputdaypointsmax);
				mInput2 = (EditText)viewDayPointsMax.findViewById(R.id.inputweekextra);	
				if (dagTotaalMax!=-1){
					mInput1.setText(String.valueOf(dagTotaalMax));
					mInput2.setText(String.valueOf(weekExtra));
				}	
				mInput1.requestFocus();//zo komt het cursor-teken ook achteraan te staan
				
				strInput1=mInput1.getText().toString();
				strInput2=mInput2.getText().toString();
	            
				builder
					.setView(viewDayPointsMax)
					.setTitle("tttt") //als we hier niets zetten dan werkt het niet
					.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface p1)
						{
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						}
					})
					.setOnCancelListener(new DialogInterface.OnCancelListener(){

						@Override
						public void onCancel(DialogInterface p1)
						{	
							strInput1="";
							strInput2="";
							toast("Geen wijzigingen");
						}
					})
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							strInput1=mInput1.getText().toString();
							strInput2=mInput2.getText().toString();
							int p1=Integer.parseInt(strInput1);
							int p2=Integer.parseInt(strInput2);
							if (p1==dagTotaalMax && p2==weekExtra){
								strInput1="";
								strInput2="";
								toast("Geen wijzigingen, gegevens zijn hetzelfde");
							}else{
								dagTotaalMax=Integer.parseInt(strInput1);
								weekExtra=Integer.parseInt(strInput2);
								savePreferences("dagTotaalMax",dagTotaalMax);
								savePreferences("weekExtra",weekExtra);
								strInput1="";
								strInput2="";
							
								progressDialog = ProgressDialog.show(SearchableFoodPoints.this, "Eventjes wachten a.u.b.",
  							 	"Aanpassen gegevens, datumlijsten...", true);
								new Thread(new Runnable() {
									@Override
									public void run()
									{
										Cursor cursor = mDb.getAllDates();
										if (cursor!=null){
											do{
												String date=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_DATE_LIST));
												long longDate=cursor.getLong(cursor.getColumnIndex(FoodPointsDatabase.KEY_DATE_LIST_LONG));	
												setFoodToDateList(date, longDate);
											}while (cursor.moveToNext());
										}

										runOnUiThread(new Runnable() {
												@Override
												public void run()
												{
													progressDialog.dismiss();
												}
											});
									}
								}).start();
								showlist2(false,false);
								toast("Gegevens aangepast");
							}
						}
					});
				final AlertDialog dialogDayPointsMax = builder.create();
				dialogDayPointsMax.show();
				
				if (dagTotaalMax==-1){
					dialogDayPointsMax.setCancelable(false);
					dialogDayPointsMax.setTitle("Enkele gegevens... (Kan later nog aangepast worden)");
				}else{
					dialogDayPointsMax.setCancelable(true);
					dialogDayPointsMax.setTitle("Gegevens aanpassen...");
				}	
				
				if (buttonOkEnabled()){
					dialogDayPointsMax.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
				else{
					dialogDayPointsMax.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
				//check button positive
			    mInput1.addTextChangedListener(new TextWatcher(){
					    @Override
						public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void afterTextChanged(Editable p1)
						{
							strInput1=mInput1.getText().toString();
							strInput2=mInput2.getText().toString();
							if (strInput1.length()>3){
								strInput1=strInput1.substring(0,3);mInput1.setText(strInput1);
								mInput1.setSelection(strInput1.length()); //zet cursor achteraan
							}
							if (buttonOkEnabled()){
								dialogDayPointsMax.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
							else{
								dialogDayPointsMax.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
						}
				});
				mInput2.addTextChangedListener(new TextWatcher(){
					    @Override
						public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
						{
						}
                        @Override
						public void afterTextChanged(Editable p1)
						{
							strInput1=mInput1.getText().toString();
							strInput2=mInput2.getText().toString();
						    if (strInput2.length()>3){
								strInput2=strInput2.substring(0,3);mInput2.setText(strInput2);
								mInput2.setSelection(strInput2.length()); //zet cursor achteraan
							}
							if (buttonOkEnabled()){
								dialogDayPointsMax.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
							else{
								dialogDayPointsMax.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
						}
				});
				break;	
		}
		return super.onCreateDialog(id);
	}	
	
	public void toast(String pp){
		Toast.makeText(this,pp,Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onDestroy() {
		toast("onDestroy");
		super.onDestroy();
		if (mDb != null) {
			mDb.close();
		}
	}
	
	public boolean stringIsInCursor(String str,Cursor cursor){
		if (cursor==null){return false;}
		do {
			String string=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_WORD));
			if (string.equals(strInput1)){return true;}
		}	
		while (cursor.moveToNext());
		cursor.close();
		return false;
	}
	
	public boolean stringIsInCursorExceptPos(String str,Cursor cursor,long longId){
		if (cursor==null){return false;}
		do {
			String string=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_WORD));
			long id=cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
			if (string.equals(strInput1) && id!=longId){return true;}
		}	
		while (cursor.moveToNext());
		cursor.close();
		return false;
	}

	public boolean buttonSaveEnabled(){
		boolean numberOk=true;
		if (!strInput2.isEmpty() && strInput2.charAt(0)=='0' && strInput2.length()>1 
			|| !strInput2.isEmpty() && Integer.parseInt(strInput2)>50){numberOk=false;}
		if (strInput1.isEmpty() || strInput2.isEmpty() || !numberOk){
			return false;
		}
		else {return true;}
	}
	
	public boolean buttonOkEnabled(){
		boolean numberOk=true;
		if (!strInput1.isEmpty() && strInput1.charAt(0)=='0' && strInput1.length()>1 
			|| !strInput1.isEmpty() && Integer.parseInt(strInput1)>50){numberOk=false;}
		if (!strInput2.isEmpty() && strInput2.charAt(0)=='0' && strInput2.length()>1 
			|| !strInput2.isEmpty() && Integer.parseInt(strInput2)>100){numberOk=false;}
		if (strInput1.isEmpty() || strInput2.isEmpty() || !numberOk){
			return false;
		}
		else {return true;}
	}
	
	public String getStringTitleDialog(){
		Cursor cur=cursAdapter.getCursor();
		cur.moveToPosition(pos);
		String stringw=cur.getString(cur.getColumnIndex(FoodPointsDatabase.KEY_WORD));
		String stringp=cur.getString(cur.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
		String str="("+stringp+")"+stringw;
		return str;
	}
	
	public void setFoodToDateList(String strDate, long dateLong){
		String dateString=strDate.replaceAll("/","");
		Cursor cursor = mDb.getWordsDate(dateString);
		int intTot=0;
		int intExtra=0;
		if (cursor!=null){
			do{
				int int1=cursor.getInt(cursor.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
				intTot+=int1;			
			}while (cursor.moveToNext());
		}
		Calendar calen=Calendar.getInstance();
		calen.setTimeInMillis(dateLong);
		while(calen.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
			calen.add(Calendar.DATE, -1);
		}
		String[] daysOfWeek = new String[8];//0 tot 7
		for (int n = 1; n <= 7; n++){
			String othDate=dateFormat.format(calen.getTime());
			daysOfWeek[n]=othDate.replaceAll("/","");
			Cursor curs = mDb.getWordsDate(daysOfWeek[n]);
			int intTotaal=0;
			if (curs!=null){
				do{
					int int1=curs.getInt(curs.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
					intTotaal+=int1;			
				}while (curs.moveToNext());
			}
			if (intTotaal>dagTotaalMax){intExtra=intExtra + intTotaal - dagTotaalMax;}
			calen.add(Calendar.DATE, 1);
		}
		for (int n = 1; n <= 7; n++){
			Cursor cursDateList=mDb.getDateList(daysOfWeek[n]);
			if (cursDateList!=null){
				long lo=cursDateList.getLong(cursDateList.getColumnIndex(BaseColumns._ID));
				if(daysOfWeek[n].compareTo(dateString)==0){
			 		mDb.changeDayTotalAndExtra(intTot, intExtra, lo);
				}else{
					mDb.changeDayTotalAndExtra(-1, intExtra, lo);
				}
				cursDateList.close();
			}	
		}
	
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
