
package com.abanoub.unit.inventory.UI;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.abanoub.unit.inventory.R;
import com.abanoub.unit.inventory.data.ProductContract.ProductEntry;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class SaleProductActivity extends AppCompatActivity {

    private TextView quantityText;
    private TextView priceText;
    private TextView quantityTotalText;
    private TextView priceTotalText;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_product);

        quantityText = findViewById(R.id.customer_quantity);
        priceText = findViewById(R.id.customer_price);
        quantityTotalText = findViewById(R.id.customer_total_quantity);
        priceTotalText = findViewById(R.id.customer_total_price);

        Button addOne = findViewById(R.id.add_quantity);
        addOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                int q = Integer.parseInt(quantityText.getText().toString());
                if (count >= q){
                    count = q;
                }
                quantityTotalText.setText(String.valueOf(count));
                String price = priceText.getText().toString();
                Number lastPrice = null;
                try {
                    lastPrice = NumberFormat.getCurrencyInstance().parse(price);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                assert lastPrice != null;
                double p = lastPrice.doubleValue();
                double totalPrice = count * p;
                priceTotalText.setText(String.valueOf(totalPrice));
            }
        });

        Button backOne = findViewById(R.id.back_quantity);
        backOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count--;
                if (count <= -1){
                    count = 0;
                }
                quantityTotalText.setText(String.valueOf(count));
                String price = priceText.getText().toString();
                Number lastPrice = null;
                try {
                    lastPrice = NumberFormat.getCurrencyInstance().parse(price);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                assert lastPrice != null;
                double p = lastPrice.doubleValue();
                double totalPrice = count * p;
                priceTotalText.setText(String.valueOf(totalPrice));
            }
        });

        fillSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sales, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sale_product:

                return true;
            case R.id.homeAsUp:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** fill spinner of all products names to make it easier for user to sale it */
    @SuppressLint("Recycle")
    private void fillSpinner() {
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor;

        String[] projection = {ProductEntry.COLUMN_PRODUCT_NAME, ProductEntry.COLUMN_PRODUCT_QUANTITY, ProductEntry.COLUMN_PRODUCT_PRICE};
        cursor = getContentResolver().query(ProductEntry.CONTENT_URI, projection, null, null, null, null);
        assert cursor != null;
        while (cursor.moveToNext()) {
            int column_name = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            String name = cursor.getString(column_name);
            list.add(name);
        }

        Spinner spinner = findViewById(R.id.customer_product);
        ArrayAdapter arrayList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor1;
                String id = (String) adapterView.getItemAtPosition(i);
                String selection = ProductEntry.COLUMN_PRODUCT_NAME + " =?";
                String[] selectionArgs = {id};
                String[] projection2 = {ProductEntry.COLUMN_PRODUCT_QUANTITY, ProductEntry.COLUMN_PRODUCT_PRICE};
                cursor1 = getContentResolver().query(ProductEntry.CONTENT_URI, projection2, selection, selectionArgs, null);
                if (cursor1.moveToNext()){
                    int column_quantity = cursor1.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
                    int column_price = cursor1.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

                    int quantity = cursor1.getInt(column_quantity);
                    double price = cursor1.getDouble(column_price);

                    quantityText.setText(String.valueOf(quantity));
                    priceText.setText(NumberFormat.getCurrencyInstance().format(price));
                }

                quantityTotalText.setText("0");
                priceTotalText.setText("0.0");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.setSelection(0);
            }
        });

    }
}
