package com.microblink.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.microblink.directApi.DirectApiErrorListener;
import com.microblink.directApi.Recognizer;
import com.microblink.hardware.orientation.Orientation;
import com.microblink.recognition.FeatureNotSupportedException;
import com.microblink.recognition.InvalidLicenceKeyException;
import com.microblink.recognizers.BaseRecognitionResult;
import com.microblink.recognizers.RecognitionResults;
import com.microblink.recognizers.blinkbarcode.pdf417.Pdf417RecognizerSettings;
import com.microblink.recognizers.blinkbarcode.pdf417.Pdf417ScanResult;
import com.microblink.recognizers.blinkbarcode.zxing.ZXingRecognizerSettings;
import com.microblink.recognizers.blinkbarcode.zxing.ZXingScanResult;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.recognizers.settings.RecognizerSettings;
import com.microblink.view.recognition.RecognitionType;
import com.microblink.view.recognition.ScanResultListener;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends Activity {

    // obtain your licence key at http://microblink.com/login or
    // contact us at http://help.microblink.com
    private static final String LICINSE_KEY = "LF4HOK6C-2CBLHLKC-2W32Z7CV-Z5Y5Z644-XIDIRD7F-ZFRKASEV-MTUXMWH6-7BSYYAS4";

    private static final String TAG = "DirectApiDemo";

    private Recognizer mRecognizer = null;
    private Button mScanAssetBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScanAssetBtn = (Button)findViewById(R.id.button);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // get the recognizer instance
        try {
            mRecognizer = Recognizer.getSingletonInstance();
        } catch (FeatureNotSupportedException e) {
            Toast.makeText(this, "Feature not supported! Reason: " + e.getReason().getDescription(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // In order for scanning to work, you must enter a valid licence key. Without licence key,
        // scanning will not work. Licence key is bound the the package name of your app, so when
        // obtaining your licence key from Microblink make sure you give us the correct package name
        // of your app. You can obtain your licence key at http://microblink.com/login or contact us
        // at http://help.microblink.com.
        // Licence key also defines which recognizers are enabled and which are not. Since the licence
        // key validation is performed on image processing thread in native code, all enabled recognizers
        // that are disallowed by licence key will be turned off without any error and information
        // about turning them off will be logged to ADB logcat.
        try {
            mRecognizer.setLicenseKey(this, LICINSE_KEY);
        } catch (InvalidLicenceKeyException e) {
            Log.e(TAG, "Failed to set licence key!");
            Toast.makeText(this, "Failed to set licence key!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // prepare settings for PDF417 and ZXing QR code recognition
        Pdf417RecognizerSettings pdf417Sett = new Pdf417RecognizerSettings();
        ZXingRecognizerSettings zxingSett = new ZXingRecognizerSettings();
        zxingSett.setScanQRCode(true);

        // prepare recognition settings
        RecognitionSettings recognitionSettings = new RecognitionSettings();
        // add settings objects to recognizer settings array
        // Pdf417Recognizer and ZXingRecognizer will be used in the recognition process
        recognitionSettings.setRecognizerSettingsArray(
                new RecognizerSettings[]{pdf417Sett, zxingSett});

        // additionally, there are generic settings that are used by all recognizers or the
        // whole recognition process

        // allow returning multiple scan results from single image
        recognitionSettings.setAllowMultipleScanResultsOnSingleImage(true);

        // initialize recognizer singleton
        mRecognizer.initialize(this, recognitionSettings, new DirectApiErrorListener() {
            @Override
            public void onRecognizerError(Throwable throwable) {
                Log.e(TAG, "Failed to initialize recognizer.", throwable);
                Toast.makeText(MainActivity.this, "Failed to initialize recognizer. Reason: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void onScanAssetClick(View v) {
        if(mRecognizer.getCurrentState() != Recognizer.State.READY) {
            Log.e(TAG, "Recognizer not ready!");
            return;
        }
        // load Bitmap from assets
        AssetManager assets = getAssets();
        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assets.open("dual-barcode-sample.png");
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
            Log.e(TAG, "Failed to load image from assets!");
            Toast.makeText(this, "Failed to load image from assets!", Toast.LENGTH_LONG).show();
            return;
        }

        if(bitmap != null) {
            // disable button
            mScanAssetBtn.setEnabled(false);
            // show progress dialog
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setIndeterminate(true);
            pd.setMessage("Performing recognition");
            pd.setCancelable(false);
            pd.show();
            // recognize image
            mRecognizer.recognizeBitmap(bitmap, Orientation.ORIENTATION_LANDSCAPE_RIGHT, new ScanResultListener() {
                @Override
                public void onScanningDone(RecognitionResults results) {
                    BaseRecognitionResult[] resultArray = results.getRecognitionResults();
                    if (resultArray != null && resultArray.length > 0) {
                        StringBuilder totalResult = new StringBuilder();

                        for (BaseRecognitionResult result : resultArray) {
                            if (result instanceof Pdf417ScanResult) { // result has been generated by PDF417 recognizer
                                Pdf417ScanResult pdf417Result = (Pdf417ScanResult) result;
                                totalResult.append("Barcode type: PDF417\nBarcode content:\n");
                                totalResult.append(pdf417Result.getStringData());
                                totalResult.append("\n\n");
                            } else if (result instanceof ZXingScanResult) {
                                ZXingScanResult zxingResult = (ZXingScanResult) result;
                                totalResult.append("Barcode type: ");
                                totalResult.append(zxingResult.getBarcodeType().name());
                                totalResult.append("\nBarcode content:\n");
                                totalResult.append(zxingResult.getStringData());
                                totalResult.append("\n\n");
                            }
                        }

                        // raise dialog with barcode result on UI thread
                        final String scanResult = totalResult.toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScanAssetBtn.setEnabled(true);
                                pd.dismiss();

                                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                                b.setTitle("Scan result").setMessage(scanResult).setCancelable(false).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Nothing scanned!", Toast.LENGTH_SHORT).show();
                        // enable button again
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScanAssetBtn.setEnabled(true);
                                pd.dismiss();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRecognizer.terminate();
        mRecognizer = null;
    }
}
