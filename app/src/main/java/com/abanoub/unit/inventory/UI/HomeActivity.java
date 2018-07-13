package com.abanoub.unit.inventory.UI;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;

import com.abanoub.unit.inventory.R;
import com.abanoub.unit.inventory.adapter.ProductAdapter;
import com.abanoub.unit.inventory.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // image data contain image of product but in byte shape
    private byte[] imageData;

    // cursor adapter which hold and contain the product data
    private CursorAdapter cursorAdapter;

    private static final int LOADER_INDEX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // create a reference to floating button xml and view class
        FloatingActionButton actionButton = findViewById(R.id.action_floating_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SalesActivity.class);
                startActivity(intent);

                // add some transitions when activity is starting
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //make an initial loader to connect to product data
        getLoaderManager().initLoader(LOADER_INDEX, null, this);

        // create grid view reference between view group xml and main activity class
        GridView gridView = findViewById(R.id.product_list);

        // cursor adapter it's contains the product data for display it in grid view
        cursorAdapter = new ProductAdapter(this, null);

        // add the adapter to grid view to display the data
        gridView.setAdapter(cursorAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                // moving to edit product activity to modify a product
                Intent intent = new Intent(HomeActivity.this, EditActivity.class);

                // getting the uri for this product data which can modify it
                Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                // add the uri data to intent to move the uri to edit product
                intent.setData(uri);

                // go to edit product activity and pause home activity
                startActivity(intent);

                // add some transitions when activity is starting
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //create a reference between xml and java class to empty grid view
        //this view well show if the grid view is empty
        View empty_list = findViewById(R.id.empty_view);
        gridView.setEmptyView(empty_list);
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
                moveToEditProduct();
                return true;
            case R.id.delete_all:
                showDeleteAllDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /** move to add product activity to to add new data */
    private void moveToEditProduct() {
        Intent intent = new Intent(HomeActivity.this, EditActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {ProductEntry.COLUMN_PRODUCT_ID, ProductEntry.COLUMN_PRODUCT_NAME,
                        ProductEntry.COLUMN_PRODUCT_QUANTITY, ProductEntry.COLUMN_PRODUCT_PRICE,
                        ProductEntry.COLUMN_PRODUCT_SUPPLIER, ProductEntry.COLUMN_PRODUCT_IMAGE};
        return new CursorLoader(this, ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    /** delete and clear all products data */
    private void deleteAll(){
        getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
    }


    /** insert dummy data to test the data */
    private void insert(){
        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "product");
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 15);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 4.99);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, "mr.max");

        String path = "/storage/emulated/0/Pictures/Screenshots/Screenshot_2018-05-28-01-58-39.png";
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        setImageDataFromBitmap(bitmap);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageData);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }


    /** Convert Bitmap to byte[] to imageData */
    public void setImageDataFromBitmap(Bitmap image) {
        if (image != null) {
            //bitmap to byte[]
            imageData = bitmapToByte(image);
        } else {
            imageData = null;
        }
    }


    /** Convert Bitmap to byte[] array */
    public byte[] bitmapToByte(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //bitmap to byte[] stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] x = stream.toByteArray();
            //close stream to save memory
            stream.close();
            return x;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /** when user click delete all the dialog well appear to user
      * to warn him whether delete all or cancel */
    private void showDeleteAllDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_all_msg);
        builder.setPositiveButton(R.string.dialog_delete_all_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAll();
            }
        });

        builder.setNegativeButton(R.string.dialog_delete_all_cancel, new DialogInterface.OnClickListener() {
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
}
