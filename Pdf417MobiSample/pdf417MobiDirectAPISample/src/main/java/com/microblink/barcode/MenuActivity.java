package com.microblink.barcode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.microblink.activity.BaseScanActivity;
import com.microblink.barcode.customcamera.Camera1Activity;
import com.microblink.barcode.customcamera.camera2.Camera2Activity;
import com.microblink.barcode.imagescan.ScanImageActivity;
import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.entities.recognizers.blinkbarcode.barcode.BarcodeRecognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MenuActivity extends Activity {

    private static final int MY_REQUEST_CODE = 1337;

    private static final int PERMISSION_REQUEST_CODE = 0x123;

    /**
     * Barcode recognizer that will perform recognition of images
     */
    private BarcodeRecognizer mBarcodeRecognizer;

    /**
     * Recognizer bundle that will wrap the barcode recognizer in order for recognition to be performed
     */
    private RecognizerBundle mRecognizerBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initRecognizer();

        // Request permissions if not granted, we need CAMERA permission and
        // WRITE_EXTERNAL_STORAGE permission because images that are taken by camera
        // will be stored on external storage and used in recognition process
        List<String> requiredPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (requiredPermissions.size() > 0) {
            String[] permArray = new String[requiredPermissions.size()];
            permArray = requiredPermissions.toArray(permArray);
            ActivityCompat.requestPermissions(this, permArray, PERMISSION_REQUEST_CODE);
        }
    }

    private void initRecognizer() {
        // You have to enable recognizers and barcode types you want to support
        // Don't enable what you don't need, it will significantly decrease scanning performance
        mBarcodeRecognizer = new BarcodeRecognizer();
        mBarcodeRecognizer.setScanPDF417(true);
        mBarcodeRecognizer.setScanQRCode(true);

        mRecognizerBundle = new RecognizerBundle(mBarcodeRecognizer);
    }

    public void onScanImageClick(View v) {
        Intent intent = new Intent(this, ScanImageActivity.class);
        mRecognizerBundle.saveToIntent(intent);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    public void onCamera1Click(View view) {
        startCameraActivity(Camera1Activity.class);
    }

    public void onCamera2Click(View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            startCameraActivity(Camera2Activity.class);
        } else {
            Toast.makeText(this, "Camera2 API requires Android 5.0 or newer. Camera1 direct API will be used", Toast.LENGTH_SHORT).show();
            startCameraActivity(Camera1Activity.class);
        }
    }

    private void startCameraActivity(Class targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        mRecognizerBundle.saveToIntent(intent);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    public void showResults(BarcodeRecognizer.Result result) {
        StringBuilder sb = new StringBuilder(result.getBarcodeFormat().name());
        sb.append("\n\n");
        if (result.isUncertain()) {
            sb.append("\nThis scan data is uncertain!\n\nString data:\n");
        }
        sb.append(result.getStringData());

        sb.append("\nRaw data:\n");
        sb.append(Arrays.toString(result.getRawData()));
        sb.append("\n\n\n");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Scan result")
                .setMessage(sb.toString())
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // updates bundled recognizers with results that have arrived
            mRecognizerBundle.loadFromIntent(data);
            // after calling mRecognizerBundle.loadFromIntent, results are stored within mBarcodeRecognizer
            BarcodeRecognizer.Result result = mBarcodeRecognizer.getResult();
            showResults(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("Exiting")
                        .setMessage("Exiting app, permission(s) not granted.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
                return;
            }
        }
    }

}

