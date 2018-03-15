package com.microblink.barcode.imagescan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.microblink.barcode.R;
import com.microblink.directApi.DirectApiErrorListener;
import com.microblink.directApi.RecognizerRunner;
import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.hardware.orientation.Orientation;
import com.microblink.recognition.FeatureNotSupportedException;
import com.microblink.recognition.RecognitionSuccessType;
import com.microblink.view.recognition.ScanResultListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ScanImageActivity extends Activity {

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    public static final int TAKE_PHOTO_REQUEST_CODE = 1;
    public static final int CHOOSE_PHOTO_REQUEST_CODE = 2;

    /** file that will hold the image taken from camera */
    private String mCameraFile = "";

    /**  tag for logcat */
    public static final String TAG = "pdf417mobiDemo";

    private Button mScanButton;

    /** Image view which shows current image that will be scanned. */
    private ImageView mImgView;

    /** RecognizerRunner that will run all recognizers within RecognizerBundle on given image */
    private RecognizerRunner mRecognizerRunner;

    /** Bundle that will contain all recognizers that have arrived via Intent */
    private RecognizerBundle mRecognizerBundle;

    /** Current bitmap for recognition. */
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_image);

        mScanButton = findViewById(R.id.btnScan);
        mImgView = findViewById(R.id.imgImage);

        Intent intent = getIntent();

        mRecognizerBundle = new RecognizerBundle();
        // since mRecognizerBundle does not contain any recognizers, loadFromIntent will create
        // new recognizers from intent data and automatically bundle them inside mRecognizerBundle
        mRecognizerBundle.loadFromIntent(intent);

        loadDefaultBitmapFromAssets();
        if (mBitmap != null) {
            mImgView.setImageBitmap(mBitmap);
        } else {
            Toast.makeText(this, "Failed to load image from assets!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void loadDefaultBitmapFromAssets() {
        AssetManager assets = getAssets();
        InputStream istr = null;
        try {
            istr = assets.open("dual-barcode-sample.png");
            // load inital bitmap from assets
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = BITMAP_CONFIG;
            mBitmap = BitmapFactory.decodeStream(istr, null, options);
        } catch (IOException e) {
            // handle exception
            Log.e(TAG, "Failed to load image from assets!");
            mBitmap = null;
        } finally {
            try {
                if (istr != null) {
                    istr.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            mRecognizerRunner = RecognizerRunner.getSingletonInstance();
        } catch (FeatureNotSupportedException e) {
            Toast.makeText(this, "Feature not supported! Reason: " + e.getReason().getDescription(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mRecognizerRunner.initialize(this, mRecognizerBundle, new DirectApiErrorListener() {
            @Override
            public void onRecognizerError(Throwable t) {
                Log.e(TAG, "Failed to initialize recognizer.", t);
                Toast.makeText(ScanImageActivity.this, "Failed to initialize recognizer. Reason: "
                        + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * If using IntentDataTransferMode.PERSISTED_OPTIMISED transfer mode for RecognitionBundle,
         * then it is backed by temporary file which gets deleted each time loadFromBundle is called.
         * This can cause crash if your activity gets restarted by the Android. To prevent that crash
         * you should save RecognizerBundle's state in your onSaveInstanceState method. This will
         * ensure that bundle is written back to temporary file that will be available for loadFromBundle
         * method if activity gets restarted. However, if no restart occur, you must ensure this
         * temporary file gets deleted. Therefore, you must call clearSavedState in your onResume callback.
         */
        mRecognizerBundle.saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * Clear temporary file created in onSaveInstanceState in case no activity restart happened
         * after call to onSaveInstanceState. If restart happened and temporary file was consumed
         * by loadFromBundle method in onCreate, then this method will do nothing.
         */
        mRecognizerBundle.clearSavedState();
    }

    public void onTakePhotoClick(View view) {
        // Starts built-in camera intent for taking scan images
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = new File(getFilesDir(), "photo.jpg");
        mCameraFile = photoFile.getAbsolutePath();
        Uri photoURI = FileProvider.getUriForFile(this,
                    "com.microblink.barcode.provider",
                    photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST_CODE);
    }

    public void onChoosePhotoClick(View view) {
        Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK);
        choosePhotoIntent.setType("image/*");
        startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO_REQUEST_CODE);
    }

    public void onScanClick(View view) {
        if (mBitmap == null) {
            return;
        }

        mScanButton.setEnabled(false);

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setIndeterminate(true);
        pd.setMessage("Performing recognition");
        pd.setCancelable(false);
        pd.show();

        // recognize image
        mRecognizerRunner.recognizeBitmap(mBitmap, Orientation.ORIENTATION_LANDSCAPE_RIGHT, new ScanResultListener() {
            @Override
            public void onScanningDone(@NonNull RecognitionSuccessType recognitionSuccessType) {
                if (recognitionSuccessType != RecognitionSuccessType.UNSUCCESSFUL) {
                    // return results (if successful or partial)
                    Intent intent = new Intent();
                    mRecognizerBundle.saveToIntent(intent);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(ScanImageActivity.this, "Nothing scanned!", Toast.LENGTH_SHORT).show();
                    // enable button again
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mScanButton.setEnabled(true);
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRecognizerRunner != null) {
            // terminate the native library
            mRecognizerRunner.terminate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                handleBitmapFromCamera();
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CHOOSE_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                handleBitmapFromChooser(data);
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleBitmapFromCamera() {
        // obtain image that was saved to external storage by camera activity
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = BITMAP_CONFIG;
            mBitmap = BitmapFactory.decodeFile(mCameraFile, options);
            //noinspection ResultOfMethodCallIgnored
            new File(mCameraFile).delete();
            mImgView.setImageBitmap(mBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleBitmapFromChooser(Intent data) {
        Uri selectedImage = data.getData();
        try {
            InputStream imageStream = getContentResolver().openInputStream(selectedImage);
            mBitmap = BitmapFactory.decodeStream(imageStream);
            mImgView.setImageBitmap(mBitmap);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
