package com.abanoub.unit.inventory.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abanoub.unit.inventory.R;
import com.abanoub.unit.inventory.data.ProductContract.ProductEntry;

public class ProductAdapter extends CursorAdapter{

    public ProductAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item , viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        /* get column number for each column in database */
        int column_name = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int column_quantity = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int column_price = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int column_image = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

        /* get row/s data from database */
        String name = cursor.getString(column_name);
        int quantity = cursor.getInt(column_quantity);
        double price = cursor.getDouble(column_price);
        byte[] image = cursor.getBlob(column_image);

        /* get product name and add it to textView */
        TextView text_name = view.findViewById(R.id.name_item);
        text_name.setText(name);

        /* get product quantity and add it to textView */
        TextView text_quantity = view.findViewById(R.id.quantity_item);
        text_quantity.setText(String.valueOf(quantity));

        /* get product price and add it to textView */
        TextView text_price = view.findViewById(R.id.price_item);
        text_price.setText(String.valueOf(price));

        /* get product image and add it to imageView */
        ImageView imageView = view.findViewById(R.id.image_item);
        imageView.setImageBitmap(getImage(image));
    }

    /** convert blob data type (byte array) to bitmap image */
    private Bitmap getImage(byte[] image){
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
