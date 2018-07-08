package com.abanoub.unit.inventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * class contract to save names of table/s and columns
 */
public final class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.abanoub.unit.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "products";

    public static final class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "products";

        public static final String COLUMN_PRODUCT_ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier";
        public static final String COLUMN_PRODUCT_IMAGE = "image";


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

    }
}
