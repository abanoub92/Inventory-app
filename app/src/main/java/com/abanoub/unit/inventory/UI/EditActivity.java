package com.abanoub.unit.inventory.UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.abanoub.unit.inventory.R;
import com.abanoub.unit.inventory.data.ProductContract.ProductEntry;

import java.awt.font.TextAttribute;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = EditActivity.class.getSimpleName();

    /** will be true if the user updates part of the product form */
    private boolean mProductHasChanged = false;

    /** OnTouchListener that listens for any user touches on a View, implying that they are modifying
    * the view, and we change the mProductHasChanged boolean to true.*/
    private View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return true;
        }
    };

    /** hold the product image in byte shape */
    private byte[] imageData;

    private static final int LOADER_INDEX = 0;

    /** code for intent find image. it's a simple code not the real code for image*/
    private static final int PICK_IMAGE = 100;

    /** image uri (database path) in android device*/
    private Uri imageUri;

    /** uri for the comes product list which can make edit on it*/
    private Uri dataUri;

    /** imageView field to enter the product's image */
    private ImageView mProductImage;

    /** EditText field to enter the product's name */
    private EditText mProductName;

    /** EditText field to enter the product's quantity */
    private EditText mProductQuantity;

    /** EditText field to enter the product's price */
    private EditText mProductPrice;

    /** EditText field to enter the product's supplier */
    private EditText mProductSupplier;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // get uri data from grid view to update it
        Intent intent = getIntent();
        dataUri = intent.getData();

        if (dataUri == null){
            // title of the activity well be add product if uri data is null
            setTitle(R.string.action_menu_add);

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }else {
            /* but if there an uri for data the title of the activity
             * well be edit activity and the loader well search for this data
             */
            setTitle(R.string.action_menu_edit);
            getLoaderManager().initLoader(LOADER_INDEX, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mProductImage = findViewById(R.id.product_image);
        mProductName = findViewById(R.id.product_name);
        mProductQuantity = findViewById(R.id.product_quantity);
        mProductPrice = findViewById(R.id.product_price);
        mProductSupplier = findViewById(R.id.product_supplier);


        // this sense if the user make any change on text fields
        mProductName.setOnTouchListener(listener);
        mProductQuantity.setOnTouchListener(listener);
        mProductPrice.setOnTouchListener(listener);
        mProductSupplier.setOnTouchListener(listener);
        mProductImage.setOnTouchListener(listener);
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
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link HomeActivity}.
                if (!mProductHasChanged) {
                    // Navigate back to parent activity (HomeActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditActivity.this);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(listener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    /** open the device gallery to choose product image from it */
    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /** getting the image when the gallery closed */
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

        return new CursorLoader(this, dataUri, projection, null, null, null);
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

    /** convert the image byte[] to bitmap image */
    private Bitmap getImage(byte[] image){
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Perform the insert and update actions (but if there's an uri data)
     * of the product in the database.
     */
    private void saveData(){
        // Defines an object to contain the new values to insert
        ContentValues values = new ContentValues();

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String name = mProductName.getText().toString();
        String quantity = mProductQuantity.getText().toString().trim();
        String price = mProductPrice.getText().toString().trim();
        String supplier = mProductSupplier.getText().toString();

        int quantityInt = Integer.parseInt(quantity);
        if (TextUtils.isEmpty(quantity)){
            quantity = "0";
            quantityInt = Integer.parseInt(quantity);
        }

        double priceDouble = Double.parseDouble(price);
        if (TextUtils.isEmpty(price)){
            price = "0";
            priceDouble = Double.parseDouble(price);
        }

        String path = getRealPathFromURI_API19(this, imageUri);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        setImageDataFromBitmap(bitmap);

        /*
         * Sets the values of each column and inserts the product. The arguments to the "put"
         * method are "column name" and "value"
         */
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityInt);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceDouble);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageData);


        String column_name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (TextUtils.isEmpty(column_name)){
            throw new IllegalArgumentException("Product requires a name");
        }

        int column_quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (TextUtils.isEmpty(Integer.toString(column_quantity))){
            column_quantity = 0;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, column_quantity);

        double column_price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (TextUtils.isEmpty(String.valueOf(column_price))){
            column_price = 0.0;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, column_price);

        String column_supplier = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        if (TextUtils.isEmpty(column_supplier)){
            column_supplier = getString(R.string.blank_supplier);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, column_supplier);


        if (dataUri == null && TextUtils.isEmpty(column_name) && TextUtils.isEmpty(String.valueOf(column_quantity))
                && TextUtils.isEmpty(String.valueOf(column_price)) && TextUtils.isEmpty(column_supplier) && imageData == null){
            return;
        }

        if (dataUri == null){
            // Insert a new product into the provider, returning the content URI for the new product.
            getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        }else {
            // update product into the provider, returning the content if for the modify product.
            String selection = ProductEntry.COLUMN_PRODUCT_ID + " =?";
            long id = ContentUris.parseId(dataUri);
            String[] selectionArgs = {Integer.toString((int) id)};
            getContentResolver().update(dataUri, values, selection, selectionArgs);
        }

        // close this activity when user insert new data, or update an old data
        finish();
    }

    /** convert Bitmap image to byte[] and save it to imageData */
    public void setImageDataFromBitmap(Bitmap image) {
        if (image != null) {
            //bitmap to byte[]
            imageData = bitmapToByte(image);
        } else {
            imageData = null;
        }
    }


    /** convert Bitmap image to byte[] */
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

    /** get real path for file inside android device */
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        if (uri == null){
            return filePath;
        }
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

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteData(){
        String selection = ProductEntry.COLUMN_PRODUCT_ID + " =?";
        String[] selectionArgs = {String.valueOf(ContentUris.parseId(dataUri))};
        getContentResolver().delete(dataUri, selection, selectionArgs);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        // when the activity is finish by any way, well apply this transition
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener onClickListener){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_save_msg);
        builder.setPositiveButton(R.string.dialog_save_discard, onClickListener);
        builder.setNegativeButton(R.string.dialog_save_keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(clickListener);
    }


    private void showDeleteConfirmationDialog(){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_msg);
        builder.setPositiveButton(R.string.dialog_delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Delete" button, so delete the product.
                deleteData();
            }
        });

        builder.setNegativeButton(R.string.dialog_delete_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If this is a new product, hide the "Delete" menu item.
        if (dataUri == null){
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }
}
