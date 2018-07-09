package com.abanoub.unit.inventory.UI;

import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.abanoub.unit.inventory.R;

public class EditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private Uri uri;
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.agree:

                return true;
            case R.id.attach_image:
                openGallery();
                return true;
            case R.id.delete:

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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            uri = data.getData();
            mProductImage.setImageURI(uri);
        }
    }
}
