package com.abanoub.unit.inventory.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.abanoub.unit.inventory.R;
import com.abanoub.unit.inventory.data.ProductContract.ProductEntry;

public class SalesAdapter extends CursorAdapter {

    public SalesAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.sales_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int column_customer = cursor.getColumnIndex(ProductEntry.COLUMN_SALES_CUSTOMER);
        int column_product_name = cursor.getColumnIndex(ProductEntry.COLUMN_SALES_PRODUCT_NAME);
        int column_quantity = cursor.getColumnIndex(ProductEntry.COLUMN_SALES_QUANTITY);
        int column_price = cursor.getColumnIndex(ProductEntry.COLUMN_SALES_PRICE);

        String customer = cursor.getString(column_customer);
        String product_name = cursor.getString(column_product_name);
        int quantity = cursor.getInt(column_quantity);
        double price = cursor.getDouble(column_price);

        TextView textCustomer = view.findViewById(R.id.sales_customer);
        textCustomer.setText(customer);

        TextView textProductName = view.findViewById(R.id.sales_product_name);
        textProductName.setText(product_name);

        TextView textQuantity = view.findViewById(R.id.sales_quantity);
        textQuantity.setText(String.valueOf(quantity));

        TextView textPrice = view.findViewById(R.id.sales_price);
        textPrice.setText(String.valueOf(price));
    }
}
