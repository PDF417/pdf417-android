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
import com.microblink.recognizers.BaseRecognitionResult;
import com.microblink.recognizers.RecognitionResults;
import com.microblink.recognizers.blinkbarcode.BarcodeType;
import com.microblink.recognizers.blinkbarcode.bardecoder.BarDecoderRecognizerSettings;
import com.microblink.recognizers.blinkbarcode.bardecoder.BarDecoderScanResult;
import com.microblink.recognizers.blinkbarcode.pdf417.Pdf417RecognizerSettings;
import com.microblink.recognizers.blinkbarcode.pdf417.Pdf417ScanResult;
import com.microblink.recognizers.blinkbarcode.zxing.ZXingRecognizerSettings;
import com.microblink.recognizers.blinkbarcode.zxing.ZXingScanResult;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.recognizers.settings.RecognizerSettings;
import com.microblink.results.barcode.BarcodeDetailedData;

import java.util.ArrayList;
import java.util.List;


public class MenuActivity extends Activity {

    // obtain your licence key at http://microblink.com/login or
    // contact us at http://help.microblink.com
    private static final String LICENSE_KEY = "BKEBQ4LY-V4GNRCKE-2CGDYLRI-H4HHHUWZ-7EFI7ZOJ-MKQERFLE-5F3FR7XY-MXORWT6N";

    private static final int MY_REQUEST_CODE = 1337;
    private static final String TAG = "DirectApiDemo";

    private static final int PERMISSION_REQUEST_CODE = 0x123;

    /**
     * Recognition settings instance, same recognition settings are used for all examples.
     */
    private RecognitionSettings mRecognitionSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buildRecognitionSettings();

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

    private void buildRecognitionSettings() {
        // Don't enable recognizers and barcode types which you don't actually use because this will
        // significantly decrease the scanning speed.

        // Pdf417RecognizerSettings define the settings for scanning plain PDF417 barcodes.
        Pdf417RecognizerSettings pdf417RecognizerSettings = new Pdf417RecognizerSettings();
        // Set this to true to scan barcodes which don't have quiet zone (white area) around it
        // Use only if necessary because it drastically slows down the recognition process
        pdf417RecognizerSettings.setNullQuietZoneAllowed(true);
        // Set this to true to scan even barcode not compliant with standards
        // For example, malformed PDF417 barcodes which were incorrectly encoded
        // Use only if necessary because it slows down the recognition process
//        pdf417RecognizerSettings.setUncertainScanning(true);

        // BarDecoderRecognizerSettings define settings for scanning 1D barcodes with algorithms
        // implemented by Microblink team.
        BarDecoderRecognizerSettings oneDimensionalRecognizerSettings = new BarDecoderRecognizerSettings();
        // set this to true to enable scanning of Code 39 1D barcodes
        oneDimensionalRecognizerSettings.setScanCode39(true);
        // set this to true to enable scanning of Code 128 1D barcodes
        oneDimensionalRecognizerSettings.setScanCode128(true);
        // set this to true to use heavier algorithms for scanning 1D barcodes
        // those algorithms are slower, but can scan lower resolution barcodes
//        oneDimensionalRecognizerSettings.setTryHarder(true);

        // ZXingRecognizerSettings define settings for scanning barcodes with ZXing library
        // We use modified version of ZXing library to support scanning of barcodes for which
        // we still haven't implemented our own algorithms.
        ZXingRecognizerSettings zXingRecognizerSettings = new ZXingRecognizerSettings();
        // set this to true to enable scanning of QR codes
        zXingRecognizerSettings.setScanQRCode(true);
        zXingRecognizerSettings.setScanITFCode(true);

        // finally, when you have defined settings for each recognizer you want to use,
        // you should put them into array held by global settings object

        mRecognitionSettings = new RecognitionSettings();
        // add settings objects to recognizer settings array
        // Pdf417Recognizer, BarDecoderRecognizer and ZXingRecognizer
        //  will be used in the recognition process
        mRecognitionSettings.setRecognizerSettingsArray(new RecognizerSettings[]{pdf417RecognizerSettings, oneDimensionalRecognizerSettings, zXingRecognizerSettings});

        // additionally, there are generic settings that are used by all recognizers or the
        // whole recognition process

        // by default, this option is true, which means that it is possible to obtain multiple
        // recognition results from the same image.
        // if you want to obtain one result from the first successful recognizer
        // (when first barcode is found, no matter which type) set this option to false
//        recognitionSettings.setAllowMultipleScanResultsOnSingleImage(false);

    }

    /**
     * Handler for "Scan Image" button
     */
    public void onScanImageClick(View v) {
        Intent intent = new Intent(this, ScanImageActivity.class);
        // send license key over intent to scan activity
        intent.putExtra(BaseScanActivity.EXTRAS_LICENSE_KEY, LICENSE_KEY);
        // send settings over intent to scan activity
        intent.putExtra(BaseScanActivity.EXTRAS_RECOGNITION_SETTINGS, mRecognitionSettings);
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
        // send license key over intent to scan activity
        intent.putExtra(BaseScanActivity.EXTRAS_LICENSE_KEY, LICENSE_KEY);
        // send settings over intent to scan activity
        intent.putExtra(BaseScanActivity.EXTRAS_RECOGNITION_SETTINGS, mRecognitionSettings);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    public void showResults(RecognitionResults results) {
        // Get scan results array. If scan was successful, array will contain at least one element.
        // Multiple element may be in array if multiple scan results from single image were allowed in settings.
        BaseRecognitionResult[] resultArray = results.getRecognitionResults();

        // Each recognition result corresponds to active recognizer. As stated earlier, there are 3 types of
        // recognizers available (PDF417, Bardecoder and ZXing), so there are 3 types of results
        // available.

        StringBuilder sb = new StringBuilder();

        for (BaseRecognitionResult res : resultArray) {
            if (res instanceof Pdf417ScanResult) { // check if scan result is result of Pdf417 recognizer
                Pdf417ScanResult result = (Pdf417ScanResult) res;
                // getStringData getter will return the string version of barcode contents
                String barcodeData = result.getStringData();
                // isUncertain getter will tell you if scanned barcode contains some uncertainties
                boolean uncertainData = result.isUncertain();
                // getRawData getter will return the raw data information object of barcode contents
                BarcodeDetailedData rawData = result.getRawData();

                // add data to string builder
                sb.append("PDF417");
                if (uncertainData) {
                    sb.append("\nThis scan data is uncertain!\n\n");
                }
                sb.append(" string data:\n");
                sb.append(barcodeData);
                if (rawData != null) {
                    sb.append("\n\n");
                    sb.append("PDF417 raw data:\n");
                    sb.append(rawData.toString());

                    // BarcodeDetailedData contains information about barcode's binary layout, if you
                    // are only interested in raw bytes, you can obtain them with getAllData getter
                    byte[] rawDataBuffer = rawData.getAllData();
                    if (rawDataBuffer != null) {
                        sb.append("\n");
                        sb.append("PDF417 raw data merged:\n");
                        sb.append("{");
                        for (int i = 0; i < rawDataBuffer.length; ++i) {
                            sb.append((int) rawDataBuffer[i] & 0x0FF);
                            if (i != rawDataBuffer.length - 1) {
                                sb.append(", ");
                            }
                        }
                    }
                    sb.append("}\n\n\n");
                }
            } else if (res instanceof BarDecoderScanResult) { // check if scan result is result of BarDecoder recognizer
                BarDecoderScanResult result = (BarDecoderScanResult) res;
                // with getBarcodeType you can obtain barcode type enum that tells you the type of decoded barcode
                BarcodeType type = result.getBarcodeType();
                // as with PDF417, getStringData will return the string contents of barcode
                String barcodeData = result.getStringData();
                sb.append(type.name());
                sb.append(" string data:\n");
                sb.append(barcodeData);
                sb.append("\n\n\n");

            } else if (res instanceof ZXingScanResult) { // check if scan result is result of ZXing recognizer
                ZXingScanResult result = (ZXingScanResult) res;
                // with getBarcodeType you can obtain barcode type enum that tells you the type of decoded barcode
                BarcodeType type = result.getBarcodeType();
                // as with PDF417, getStringData will return the string contents of barcode
                String barcodeData = result.getStringData();
                sb.append(type.name());
                sb.append(" string data:\n");
                sb.append(barcodeData);
                sb.append("\n\n\n");
            }
        }

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
        if (requestCode == MY_REQUEST_CODE && resultCode == BaseScanActivity.RESULT_OK) {
            // First, obtain recognition result
            RecognitionResults results = data.getParcelableExtra(BaseScanActivity.EXTRAS_RECOGNITION_RESULTS);
            showResults(results);
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

