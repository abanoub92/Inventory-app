package com.abanoub.unit.inventory.UI;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.abanoub.unit.inventory.R;
import com.abanoub.unit.inventory.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private byte[] imageData;

    private static final int LOADER_INDEX = 0;

    /*code for intent find image. it's a simple code not the real code for image*/
    private static final int PICK_IMAGE = 100;

    /*image uri (database path) in android device*/
    private Uri imageUri;

    /*uri for the comes product list which can make edit on it*/
    private Uri dataUri;

    private ImageView mProductImage;
    private EditText mProductName;
    private EditText mProductQuantity;
    private EditText mProductPrice;
    private EditText mProductSupplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mProductImage = findViewById(R.id.product_image);
        mProductName = findViewById(R.id.product_name);
        mProductQuantity = findViewById(R.id.product_quantity);
        mProductPrice = findViewById(R.id.product_price);
        mProductSupplier = findViewById(R.id.product_supplier);

        Intent intent = getIntent();
        dataUri = intent.getData();

        if (dataUri == null){
            setTitle(R.string.action_menu_add);
        }else {
            setTitle(R.string.action_menu_edit);
            getLoaderManager().initLoader(LOADER_INDEX, null, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.agree:
                saveData();
                return true;
            case R.id.attach_image:
                openGallery();
                return true;
            case R.id.delete:
                deleteData();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            mProductImage.setImageURI(imageUri);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {ProductEntry.COLUMN_PRODUCT_ID, ProductEntry.COLUMN_PRODUCT_NAME,
        ProductEntry.COLUMN_PRODUCT_QUANTITY, ProductEntry.COLUMN_PRODUCT_PRICE, ProductEntry.COLUMN_PRODUCT_SUPPLIER, ProductEntry.COLUMN_PRODUCT_IMAGE};

        return new CursorLoader(this, ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()){
            int column_name = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int column_quantity = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int column_price = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int column_supplier = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int column_image = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

            String name = cursor.getString(column_name);
            int quantity = cursor.getInt(column_quantity);
            double price = cursor.getDouble(column_price);
            String supplier = cursor.getString(column_supplier);
            byte[] image = cursor.getBlob(column_image);

            mProductName.setText(name);
            mProductQuantity.setText(String.valueOf(quantity));
            mProductPrice.setText(String.valueOf(price));
            mProductSupplier.setText(supplier);
            mProductImage.setImageBitmap(getImage(image));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mProductQuantity.setText("");
        mProductPrice.setText("");
        mProductSupplier.setText("");
        mProductImage.setImageBitmap(null);
    }

    private Bitmap getImage(byte[] image){
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private void saveData(){
        ContentValues values = new ContentValues();

        String name = mProductName.getText().toString();
        int quantity = Integer.parseInt(mProductQuantity.getText().toString());
        double price = Double.parseDouble(mProductPrice.getText().toString());
        String supplier = mProductSupplier.getText().toString();

        String path = getRealPathFromURI_API19(this, imageUri);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        setImageDataFromBitmap(bitmap);

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageData);

        if (dataUri == null){
            getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        }else {
            String selection = ProductEntry.COLUMN_PRODUCT_ID + " =?";
            String[] selectionArgs = {String.valueOf(ContentUris.parseId(dataUri))};
            getContentResolver().update(dataUri, values, selection, selectionArgs);
        }

        finish();
    }

    // Bitmap to byte[] to imageData
    public void setImageDataFromBitmap(Bitmap image) {
        if (image != null) {
            //bitmap to byte[]
            imageData = bitmapToByte(image);
        } else {
            imageData = null;
        }
    }


    // Bitmap to byte[]
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

    //get real path for file inside android device
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    private void deleteData(){
        String selection = ProductEntry.COLUMN_PRODUCT_ID + " =?";
        String[] selectionArgs = {String.valueOf(ContentUris.parseId(dataUri))};
        getContentResolver().delete(dataUri, selection, selectionArgs);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
