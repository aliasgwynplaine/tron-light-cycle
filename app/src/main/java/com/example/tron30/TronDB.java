package com.example.tron30;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.LinkedHashMap;

public class TronDB {
    private final String DATABASE_NAME = "TronDB";
    public static final String SCORE_TABLE = "score_table";
    private final int DATABASE_VERSION = 1;

    private DBHelper shittyhelper;
    private final Context shittycontext;
    private SQLiteDatabase shittydb;

    public TronDB open() throws SQLException {
        shittyhelper = new DBHelper(shittycontext);
        shittydb = shittyhelper.getWritableDatabase();
        return this;
    }

    public void close() { shittyhelper.close(); }

    public LinkedHashMap<String, String> getScores() {
        Log.d("shittylog", "fetching scores...");
        String[] columns = new String[] {"user", "score"};
        Cursor c = shittydb.query(
                SCORE_TABLE,
                columns,
                null,
                null,
                null,
                null,
                "score DESC"
        );

        int usridx = c.getColumnIndex("user");
        int scridx = c.getColumnIndex("score");

        LinkedHashMap<String, String> scorehm = new LinkedHashMap<String, String>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            scorehm.put(c.getString(usridx), c.getString(scridx));
        }

        c.close();
        return scorehm;
    }

    public void insertScore(String user, int score) {
        ContentValues cv = new ContentValues();
        cv.put("user", user);
        cv.put("score", score);
        shittydb.insert(SCORE_TABLE, null, cv);
    }

    public TronDB(Context context) {
        shittycontext = context;
    }

    public class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("shittylog", "onCreate of DB!");
            String createquery = "CREATE TABLE IF NOT EXISTS \""+ SCORE_TABLE + "\" "+
                    "(\"id\" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    "\"user\" TEXT NOT NULL, "+
                    "\"score\" INTEGER NOT NULL);";
            db.execSQL(createquery);
            ContentValues cv = new ContentValues();
            cv.put("user", "RINZLER");
            cv.put("score", 9999999);
            db.insert(SCORE_TABLE, null, cv);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /* empty: should not */
        }
    }
}