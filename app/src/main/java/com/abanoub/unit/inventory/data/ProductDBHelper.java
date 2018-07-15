package com.abanoub.unit.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.abanoub.unit.inventory.data.ProductContract.ProductEntry;

public class ProductDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    /* product create table database statement */
    private static final String PRODUCT_TABLE_STATEMENT =
            "CREATE TABLE " + ProductEntry.TABLE_NAME + " ( "
            + ProductEntry.COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
            + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL DEFAULT 0.0, "
            + ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT, "
            + ProductEntry.COLUMN_PRODUCT_IMAGE + " BLOB);";

    /* product drop table database statement */
    private static final String PRODUCT_TABLE_DROP_STATEMENT =
            "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME + ";";


    private static final String SALES_TABLE_STATEMENT =
            "CREATE TABLE " + ProductEntry.SALES_TABLE_NAME + " ( "
            + ProductEntry.COLUMN_SALES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ProductEntry.COLUMN_SALES_PRODUCT_NAME + " TEXT NOT NULL, "
            + ProductEntry.COLUMN_SALES_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + ProductEntry.COLUMN_SALES_PRICE + " REAL NOT NULL DEFAULT 0.0, "
            + ProductEntry.COLUMN_SALES_CUSTOMER + " TEXT);";

    private static final String SALES_DROP_TABLE =
            "DROP TABLE IF EXISTS " + ProductEntry.SALES_TABLE_NAME + ";";




    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /* when database created execute creating table statement */
        sqLiteDatabase.execSQL(PRODUCT_TABLE_STATEMENT);
        sqLiteDatabase.execSQL(SALES_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /* when table updated execute this statement */
        sqLiteDatabase.execSQL(PRODUCT_TABLE_DROP_STATEMENT);
        onCreate(sqLiteDatabase);

        sqLiteDatabase.execSQL(SALES_DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
