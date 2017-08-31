package com.udacity.stockhawk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.stockhawk.data.Contract.Quote;


class DbHelper extends SQLiteOpenHelper {


    private static final String NAME = "StockHawk.db";
    private static final int VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String OPEN_BRACKET = " (";
    private static final String INTEGER_PRIMARY_KEY_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT, ";
    private static final String TEXT_NOT_NULL = " TEXT NOT NULL, ";
    private static final String REAL_NOT_NULL = " REAL NOT NULL, ";
    private static final String UNIQUE = "UNIQUE (";
    private static final String ON_CONFLICT_REPLACE = ") ON CONFLICT REPLACE);";
    private static final String DROP_TABLE_IF_EXISTS = " DROP TABLE IF EXISTS ";

    DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String builder = CREATE_TABLE + Quote.TABLE_NAME + OPEN_BRACKET
                + Quote._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT
                + Quote.COLUMN_SYMBOL + TEXT_NOT_NULL
                + Quote.COLUMN_PRICE + REAL_NOT_NULL
                + Quote.COLUMN_ABSOLUTE_CHANGE + REAL_NOT_NULL
                + Quote.COLUMN_PERCENTAGE_CHANGE + REAL_NOT_NULL
                + Quote.COLUMN_HISTORY + TEXT_NOT_NULL
                + UNIQUE + Quote.COLUMN_SYMBOL + ON_CONFLICT_REPLACE;

        db.execSQL(builder);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DROP_TABLE_IF_EXISTS + Quote.TABLE_NAME);

        onCreate(db);
    }

}
