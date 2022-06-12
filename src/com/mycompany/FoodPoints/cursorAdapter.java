package com.mycompany.FoodPoints;
import android.widget.*;
import android.content.*;
import android.database.*;
import android.view.*;
import android.graphics.*;
import android.provider.*;
import android.view.View.*;
import android.widget.AdapterView.*;
import android.view.ContextMenu.*;
import android.graphics.drawable.*;

public class cursorAdapter extends CursorAdapter
{
    private final Context mCtx;
 
    private TextView text1;
	private TextView text2;

	private int col1=0;
	private int col2=0;	
	
    public cursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
		mCtx=context;
    }

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//  LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.result, parent, false);
		return retView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		text1 = (TextView) view.findViewById(R.id.word);
		text2 = (TextView) view.findViewById(R.id.points);
		if (col1==0){col1=text1.getCurrentTextColor();}
		if (col2==0){col2=text2.getCurrentTextColor();}
		String string1=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_WORD));
		String string2=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
		int int2=cursor.getInt(cursor.getColumnIndex(FoodPointsDatabase.KEY_POINTS_STRING));
		String in=cursor.getString(cursor.getColumnIndex(FoodPointsDatabase.KEY_DAILY));
		boolean bool=Boolean.valueOf(in);
		if (bool){string2 += "             In dagelijkse lijst";}
		if (int2==0){
			text1.setTextColor(Color.GREEN);
			text1.setText(string1);
			text2.setTextColor(Color.GREEN);
			text2.setText(string2);
		}
		else{
			text1.setTextColor(col1);
			text1.setText(string1);
			text2.setTextColor(col2);
			text2.setText(string2);
		}
	}
	
	public void toast(String msg){
        Toast.makeText(mCtx, msg, Toast.LENGTH_LONG).show();
    }
}
