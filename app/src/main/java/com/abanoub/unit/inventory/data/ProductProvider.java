package com.abanoub.unit.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.abanoub.unit.inventory.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    /** Tag for the log messages */
    private static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private ProductDBHelper dbHelper;

    /** URI matcher code for the content URI for the products table */
    private static final int PRODUCT = 10;

    /** URI matcher code for the content URI for a single product in the products table */
    private static final int PRODUCT_ID = 11;

    /** URI matcher code for the content URI for the sales table */
    private static final int SALES = 20;

    /** URI matcher code for the content URI for a single sale operation in the sales table */
    private static final int SALES_ID = 21;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** Static initializer. This is run the first time anything is called from this class */
    static {
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT, PRODUCT);

        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT + "/#", PRODUCT_ID);

        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_SALES, SALES);

        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_SALES + "/#", SALES_ID);
    }


    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        dbHelper = new ProductDBHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String s1) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor;

        final int match = uriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                cursor = sqLiteDatabase.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case PRODUCT_ID:
                selection = ProductEntry.COLUMN_PRODUCT_ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = sqLiteDatabase.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case SALES:
                cursor = sqLiteDatabase.query(ProductEntry.SALES_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case SALES_ID:
                selection = ProductEntry.COLUMN_PRODUCT_ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = sqLiteDatabase.query(ProductEntry.SALES_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            case SALES:
                return ProductEntry.CONTENT_SALES_LIST_TYPE;
            case SALES_ID:
                return ProductEntry.CONTENT_SALES_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return insertProduct(uri, contentValues);

            case SALES:
                return insertProduct(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return deleteProduct(uri, selection, selectionArgs);

            case PRODUCT_ID:
                selection = ProductEntry.COLUMN_PRODUCT_ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteProduct(uri, selection, selectionArgs);

            case SALES:
                return deleteProduct(uri, selection, selectionArgs);

            case SALES_ID:
                selection = ProductEntry.COLUMN_SALES_ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteProduct(uri, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("deleting is not supported for " + uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return updateProduct(uri, contentValues, s, strings);

            case PRODUCT_ID:
                s = ProductEntry.COLUMN_PRODUCT_ID + " =?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, s, strings);

            case SALES:
                return updateProduct(uri, contentValues, s, strings);

            case SALES_ID:
                s = ProductEntry.COLUMN_SALES_ID + " =?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, s, strings);

            default:
                throw new IllegalArgumentException("updating is not supported for " + uri);
        }
    }


    private Uri insertProduct(Uri uri, ContentValues values){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = 0;
        if (uri == ProductEntry.CONTENT_URI) {
            id = database.insert(ProductEntry.TABLE_NAME, null, values);
        }else if (uri == ProductEntry.CONTENT_SALES_URI){
            id = database.insert(ProductEntry.SALES_TABLE_NAME, null, values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }


    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int value = 0;
        if (uri == ProductEntry.CONTENT_URI) {
            value = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        }else if (uri == ProductEntry.CONTENT_SALES_URI){
            value = database.update(ProductEntry.SALES_TABLE_NAME, values, selection, selectionArgs);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return value;
    }


    private int deleteProduct(Uri uri, String selection, String[] selectionArgs){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int value = 0;
        if (uri == ProductEntry.CONTENT_URI) {
            value = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
        }else if (uri == ProductEntry.CONTENT_SALES_URI){
            value = database.delete(ProductEntry.SALES_TABLE_NAME, selection, selectionArgs);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return value;
    }
}
