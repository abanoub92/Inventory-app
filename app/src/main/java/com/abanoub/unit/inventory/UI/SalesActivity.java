package com.abanoub.unit.inventory.UI;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.abanoub.unit.inventory.R;
import com.abanoub.unit.inventory.adapter.SalesAdapter;
import com.abanoub.unit.inventory.data.ProductContract.ProductEntry;

public class SalesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_INDEX = 0;

    /** adapter contain the data for sales list */
    private CursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        // create a reference with xml listView
        ListView salesList = findViewById(R.id.sales_list);

        // create an object from custom cursor adapter
        adapter = new SalesAdapter(this, null);

        // setting the adapter to sales list
        salesList.setAdapter(adapter);

        getLoaderManager().initLoader(LOADER_INDEX, null, this);

        // adding the view well display in case of sales list is empty
        View view = findViewById(R.id.empty_view);
        salesList.setEmptyView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_product:
                // move to make new sales operation activity
                Intent intent = new Intent(SalesActivity.this, SaleProductActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left );
                return true;
            case R.id.delete_all:
                // alert dialog if the user want to delete all data
                showDeleteDialog();
                return true;
            case R.id.homeAsUp:
                // back to the home activity for this activity
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {ProductEntry.COLUMN_SALES_ID, ProductEntry.COLUMN_SALES_PRODUCT_NAME,
                ProductEntry.COLUMN_SALES_QUANTITY, ProductEntry.COLUMN_SALES_PRICE, ProductEntry.COLUMN_SALES_CUSTOMER};
        return new CursorLoader(this, ProductEntry.CONTENT_SALES_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    /** insert dummy data to test the data */
    private void insert(){
        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_SALES_PRODUCT_NAME, "aaa");
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 26.7);
        values.put(ProductEntry.COLUMN_SALES_CUSTOMER, "mr.max");

        getContentResolver().insert(ProductEntry.CONTENT_SALES_URI, values);
    }

    /* check the user rally want to delete all data */
    private void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_sales_msg);
        builder.setPositiveButton(R.string.dialog_delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteSale();
            }
        });

        builder.setNegativeButton(R.string.dialog_delete_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteSale(){
        getContentResolver().delete(ProductEntry.CONTENT_SALES_URI, null, null);
    }
}
