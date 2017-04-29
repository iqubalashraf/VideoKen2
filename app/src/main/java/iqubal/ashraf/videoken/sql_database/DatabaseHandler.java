package iqubal.ashraf.videoken.sql_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import iqubal.ashraf.videoken.data_model.DataModel;

/**
 * Created by ashrafiqubal on 28/04/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DataBase Handler";
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "videoken";

    // Table name
    private static final String TABLE_ALL_NOTES = "TABLE_ALL_NOTES";

    //Table Columns names
    private static final String YOUTUBE_ID = "YOUTUBE_ID";
    private static final String VOICE_CLIP_NAME = "VOICE_CLIP_NAME";
    private static final String START_TIME = "START_TIME";
    private static final String DURATION = "DURATION";
    private static final String VOICE_IN_STRING = "VOICE_IN_STRING";
    private static final String TYPE_OF_NOTE = "TYPE_OF_NOTE";
    private List<DataModel> dataModels = new ArrayList<DataModel>();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_ALL_NOTES + "("
                + YOUTUBE_ID + " TEXT," + VOICE_CLIP_NAME + " TEXT," + START_TIME + " TEXT,"
                + DURATION + " TEXT,"+ VOICE_IN_STRING + " TEXT,"+ TYPE_OF_NOTE + " INTEGER" + ")";
        Log.d(TAG,CREATE_NOTE_TABLE);
        db.execSQL(CREATE_NOTE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL_NOTES);
        // Create tables again
        onCreate(db);
    }

    // Adding new Note
    public void addNoteDetailes(String youtube_id,String voice_clip_name, String start_time, String duration, String voice_in_string,int type_of_note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(YOUTUBE_ID,youtube_id);
        values.put(VOICE_CLIP_NAME,voice_clip_name);
        values.put(START_TIME,start_time);
        values.put(DURATION,duration);
        values.put(VOICE_IN_STRING,voice_in_string);
        values.put(TYPE_OF_NOTE,type_of_note);
        // Inserting Row
        db.insert(TABLE_ALL_NOTES,null,values);
        db.close(); // Closing database connection
        Log.d("DataBaseHandler: ","Saved - "+youtube_id+" "+voice_clip_name+" "+start_time+ " "+duration+ " "+voice_in_string+ " "+type_of_note);
    }

    // Getting All Notes
    public List<DataModel> getAllNotes(String youtube_id){
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ALL_NOTES+" WHERE "+YOUTUBE_ID+" = "+"'"+youtube_id+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    Log.d(TAG,cursor.getString(0)+","+cursor.getString(1)+","+cursor.getString(2)+","+cursor.getString(3)+","+cursor.getString(4)+","+cursor.getInt(5));
                    DataModel dataModel = new DataModel();
                    dataModel.setYOUTUBE_ID(cursor.getString(0));
                    dataModel.setVOICE_CLIP_NAME(cursor.getString(1));
                    dataModel.setSTART_TIME(cursor.getString(2));
                    dataModel.setDURATION(cursor.getString(3));
                    dataModel.setVOICE_IN_STRING(cursor.getString(4));
                    dataModel.setNOTE_TYPE(cursor.getInt(5));
                    dataModels.add(dataModel);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){

        }
        return dataModels;
    }
}
