package com.abanoub.unit.inventory.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ProductProvider extends ContentProvider {

    /** Tag for the log messages */
    private static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private ProductDBHelper dbHelper;

    /** URI matcher code for the content URI for the products table */
    private static final int PRODUCT = 10;

    /** URI matcher code for the content URI for a single product in the products table */
    private static final int PRODUCT_ID = 11;

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
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
