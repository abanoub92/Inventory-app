
package com.abanoub.unit.inventory.UI;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.abanoub.unit.inventory.R;
import com.abanoub.unit.inventory.data.ProductContract.ProductEntry;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class SaleProductActivity extends AppCompatActivity {

    /** Spinner hold and contain the list of products names */
    private Spinner spinner;

    /** EditText field to enter the customer's name */
    private EditText customer;

    /** TextView field to display whole product's quantity */
    private TextView quantityText;

    /** TextView field to display product's price */
    private TextView priceText;

    /** TextView field to display the user chosen quantity */
    private TextView quantityTotalText;

    /** TextView field to display the total of user quantities */
    private TextView priceTotalText;

    /** it's a counter count the quantity of user wants */
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_product);

        // Create reference with views that we will need to read user input from
        spinner = findViewById(R.id.customer_product);
        customer = findViewById(R.id.customer_name);
        quantityText = findViewById(R.id.customer_quantity);
        priceText = findViewById(R.id.customer_price);
        quantityTotalText = findViewById(R.id.customer_total_quantity);
        priceTotalText = findViewById(R.id.customer_total_price);

        // Button responsible of increase the required quantity for user
        // and display the price depend on numbers of quantities
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

        // Button responsible of decrease the required quantity for user
        // and display the price depend on numbers of quantities
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

        // Method of fill the spinner with all products names
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
                // create new sale operation to new user
                saveSaleOpe();
                return true;
            case R.id.homeAsUp:
                // back to home activity for this activity
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** fill spinner of all products names to make it easier for user to sale it */
    @SuppressLint("Recycle")
    private void fillSpinner() {
        // array list for contains the products names
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor;

        // requires columns from database
        String[] projection = {ProductEntry.COLUMN_PRODUCT_NAME};

        // Query statement for getting the products names data from database
        cursor = getContentResolver().query(ProductEntry.CONTENT_URI, projection, null, null, null, null);

        // check if the cursor is null or not
        assert cursor != null;

        // getting the data one by one and add it to list
        while (cursor.moveToNext()) {
            int column_name = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            String name = cursor.getString(column_name);
            list.add(name);
        }

        // Create adapter for spinner. The list options are from the String it will use
        // the spinner will use the default layout
        ArrayAdapter arrayList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        // Specify dropdown layout style - simple list view with 1 item per line
        arrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(arrayList);

        // get the chosen value from spinner to add it to content values
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor1;

                //getting the selected item and cast it to string
                String id = (String) adapterView.getItemAtPosition(i);

                // create where clause for sql statement
                String selection = ProductEntry.COLUMN_PRODUCT_NAME + " =?";

                // value of where clause
                String[] selectionArgs = {id};

                // required columns for completing the task
                String[] projection2 = {ProductEntry.COLUMN_PRODUCT_QUANTITY, ProductEntry.COLUMN_PRODUCT_PRICE};

                // create the query to get products quantities and prices for specific product
                cursor1 = getContentResolver().query(ProductEntry.CONTENT_URI, projection2, selection, selectionArgs, null);

                // get the rows in this price and quantity columns one by one if there is more than one row
                if (cursor1.moveToNext()){
                    int column_quantity = cursor1.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
                    int column_price = cursor1.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

                    int quantity = cursor1.getInt(column_quantity);
                    double price = cursor1.getDouble(column_price);

                    quantityText.setText(String.valueOf(quantity));
                    priceText.setText(NumberFormat.getCurrencyInstance().format(price));
                }

                // when the user change selected item in spinner back two of textViews to 0
                quantityTotalText.setText("0");
                priceTotalText.setText("0.0");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // when the user choose nothing the selector well by in the first one
                adapterView.setSelection(0);
            }
        });

    }


    private void saveSaleOpe(){
        ContentValues values = new ContentValues();

        // getting the values from input fields to insert it to database
        String productName = (String) spinner.getSelectedItem();
        String quantity = quantityTotalText.getText().toString();
        String price = priceTotalText.getText().toString();
        String customerName = customer.getText().toString();

        int quantityInt = Integer.parseInt(quantity);
        double priceDouble = Double.parseDouble(price);

        values.put(ProductEntry.COLUMN_SALES_PRODUCT_NAME, productName);
        values.put(ProductEntry.COLUMN_SALES_QUANTITY, quantityInt);
        values.put(ProductEntry.COLUMN_SALES_PRICE, priceDouble);
        values.put(ProductEntry.COLUMN_SALES_CUSTOMER, customerName);

        // insert statement
        getContentResolver().insert(ProductEntry.CONTENT_SALES_URI, values);

        // update the product's quantity after every sale operation
        updateProduct();

        // close this activity
        finish();
    }


    /* Method responsible of updating the quantity of the product
     * with every time the user make a new bill */
    private void updateProduct(){
        ContentValues values = new ContentValues();

        String quantity = quantityTotalText.getText().toString();
        int quantityInt = Integer.parseInt(quantity);
        int totalQnt = Integer.parseInt(quantityText.getText().toString());

        int total = totalQnt - quantityInt;

        String selection = ProductEntry.COLUMN_PRODUCT_NAME + " =?";
        String[] selectionArgs = {String.valueOf(spinner.getSelectedItem())};

        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, total);

        getContentResolver().update(ProductEntry.CONTENT_URI, values, selection, selectionArgs);
    }


    @Override
    public void finish() {
        super.finish();
        // when the activity is close apply this animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
