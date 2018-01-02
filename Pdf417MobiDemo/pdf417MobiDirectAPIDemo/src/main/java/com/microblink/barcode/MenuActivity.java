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
        // Don't enable recognizers and barcode types which you don't actually use because this will
        // significantly decrease the scanning speed.

        // create new BarcodeRecognizer
        mBarcodeRecognizer = new BarcodeRecognizer();
        // enable scanning of PDF417 2D barcpde
        mBarcodeRecognizer.setScanPDF417(true);
        // enable scanning of QR code
        mBarcodeRecognizer.setScanQRCode(true);

        // create bundle BarcodeRecognizer within RecognizerBundle
        mRecognizerBundle = new RecognizerBundle(mBarcodeRecognizer);
    }

    /**
     * Handler for "Scan Image" button
     */
    public void onScanImageClick(View v) {
        Intent intent = new Intent(this, ScanImageActivity.class);
        // save RecognizerBundle into Intent
        mRecognizerBundle.saveToIntent(intent);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    /**
     * Handler for "Camera 1 Activity" and "Camera 2 Activity" buttons
     */
    public void onCameraScanClick(View view) {
        Class<?> targetActivity = null;
        switch (view.getId()) {
            case R.id.btn_camera1:
                targetActivity = Camera1Activity.class;
                break;
            case R.id.btn_camera2:
                if (Build.VERSION.SDK_INT >= 21) {
                    targetActivity = Camera2Activity.class;
                } else {
                    Toast.makeText(this, "Camera2 API requires Android 5.0 or newer. Camera1 direct API will be used", Toast.LENGTH_SHORT).show();
                    targetActivity = Camera1Activity.class;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown button clicked!");
        }

        Intent intent = new Intent(this, targetActivity);
        // save RecognizerBundle into Intent
        mRecognizerBundle.saveToIntent(intent);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    public void showResults() {
        // after calling mRecognizerBundle.loadFromIntent, results are stored within mBarcodeRecognizer

        BarcodeRecognizer.Result result = mBarcodeRecognizer.getResult();

        StringBuilder sb = new StringBuilder(result.getBarcodeFormat().name());

        if (result.isUncertain()) {
            sb.append("\nThis scan data is uncertain!\n\nString data:\n");
        }
        sb.append(result.getStringData());

        byte[] rawDataBuffer = result.getRawData();
        sb.append("\n");
        sb.append("Raw data:\n");
        sb.append("{");
        for (int i = 0; i < rawDataBuffer.length; ++i) {
            sb.append((int) rawDataBuffer[i] & 0x0FF);
            if (i != rawDataBuffer.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("}\n\n\n");

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
        if (requestCode == MY_REQUEST_CODE && resultCode == BaseScanActivity.RESULT_OK && data != null) {
            // First, obtain recognition result
            // method loadFromIntent will update bundled recognizers with results that have arrived
            mRecognizerBundle.loadFromIntent(data);
            showResults();
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

