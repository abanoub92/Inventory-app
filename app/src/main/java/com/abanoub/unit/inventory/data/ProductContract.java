package com.abanoub.unit.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * class contract to save names of table/s and columns
 */
public final class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.abanoub.unit.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "products";
    public static final String PATH_SALES = "sales";

    public static final class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "products";

        public static final String COLUMN_PRODUCT_ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier";
        public static final String COLUMN_PRODUCT_IMAGE = "image";


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);


        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;


        public static final Uri CONTENT_SALES_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SALES);


        public static final String SALES_TABLE_NAME = "sales";
        public static final String COLUMN_SALES_ID = BaseColumns._ID;
        public static final String COLUMN_SALES_PRODUCT_NAME = "name";
        public static final String COLUMN_SALES_QUANTITY = "quantity";
        public static final String COLUMN_SALES_PRICE = "price";

    }
}
