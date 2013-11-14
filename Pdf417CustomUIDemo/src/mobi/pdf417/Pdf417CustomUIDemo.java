package mobi.pdf417;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;
import net.photopay.barcode.BarcodeDetailedData;
import net.photopay.barcode.BarcodeElement;
import net.photopay.barcode.ElementType;
import net.photopay.base.BaseBarcodeActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Pdf417CustomUIDemo extends Activity {

	public static final String TAG = "MainActivity";

	public static final String LICENSE = "1c61089106f282473fbe6a5238ec585f8ca0c29512b2dea3b7c17b8030c9813dc965ca8e70c8557347177515349e6e";

	private static final int MY_REQUEST_CODE = 1337;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnDefaultUINoDialog: {
			// create intent for scan activity
			Intent intent = new Intent(this, Pdf417ScanActivity.class);
			// add license that allows removing of dialog in default UI
			intent.putExtra(BaseBarcodeActivity.EXTRAS_LICENSE_KEY, LICENSE);
			// create settings object
			Pdf417MobiSettings sett = new Pdf417MobiSettings();
			// set this to true to enable PDF417 scanning
			sett.setPdf417Enabled(true);
			// set this to true to enable QR code scanning
			sett.setQrCodeEnabled(true);
			// set this to true to prevent showing dialog after successful scan
			sett.setDontShowDialog(true);
			// put settings as intent extra
			intent.putExtra(BaseBarcodeActivity.EXTRAS_SETTINGS, sett);
			startActivityForResult(intent, MY_REQUEST_CODE);
			break;
		}
		case R.id.btnDefaultUINoLogo: {
			// create intent for scan activity
			Intent intent = new Intent(this, Pdf417ScanActivity.class);
			// add license that allows removing of logo in default UI
			intent.putExtra(BaseBarcodeActivity.EXTRAS_LICENSE_KEY, LICENSE);
			// create settings object
			Pdf417MobiSettings sett = new Pdf417MobiSettings();
			// set this to true to enable PDF417 scanning
			sett.setPdf417Enabled(true);
			// set this to true to enable QR code scanning
			sett.setQrCodeEnabled(true);
			// if license permits this, remove Pdf417.mobi logo overlay on scan activity
			// if license forbids this, this option has no effect
			sett.setRemoveOverlayEnabled(true);
			// put settings as intent extra
			intent.putExtra(BaseBarcodeActivity.EXTRAS_SETTINGS, sett);
			startActivityForResult(intent, MY_REQUEST_CODE);
			break;
		}
		case R.id.btnCustomUI: {
			// create intent for custom scan activity
			Intent intent = new Intent(this, CustomScanActivity.class);
			// add license that allows creating custom camera overlay
			intent.putExtra(BaseBarcodeActivity.EXTRAS_LICENSE_KEY, LICENSE);
			// create settings object
			Pdf417MobiSettings sett = new Pdf417MobiSettings();
			// set this to true to enable PDF417 scanning
			sett.setPdf417Enabled(true);
			// set this to true to enable QR code scanning
			sett.setQrCodeEnabled(true);
			// put settings as intent extra
			intent.putExtra(BaseBarcodeActivity.EXTRAS_SETTINGS, sett);
			startActivityForResult(intent, MY_REQUEST_CODE);
			break;
		}
		}
	}

	/**
	 * this method is same as in Pdf417MobiDemo project
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_REQUEST_CODE && resultCode == BaseBarcodeActivity.RESULT_OK) {
			// read scan result
			Pdf417MobiScanData scanData = data.getParcelableExtra(BaseBarcodeActivity.EXTRAS_RESULT);

			// read scanned barcode type (PDF417 or QR code)
			String barcodeType = scanData.getBarcodeType();
			// read the data contained in barcode
			String barcodeData = scanData.getBarcodeData();
			// read raw barcode data
			BarcodeDetailedData rawData = scanData.getBarcodeRawData();

			if (rawData != null) {
				// the following is the explanation on how to use raw data
				/**
				 * BarcodeDetailedData is a structure that contains vector of
				 * barcode elements. Each barcode element contains byte array
				 * with raw data for this element and the type of that byte
				 * array. The type can be either ElementType.TEXT_DATA or
				 * ElementType.BYTE_DATA. TEXT_DATA means that bytes in that
				 * element represent text and can be converted to string.
				 * BYTE_DATA meand that bytes in that element don't represent
				 * anything in particular and you should decide of to use them.
				 * 
				 * Of course, both BYTE_DATA and TEXT_DATA fields can be
				 * converted to string (this is actually done in library for
				 * string result obtained in barcodeData variable).
				 */
				// get list of barcode elements
				List<BarcodeElement> elems = rawData.getElements();
				// log the amount of elements
				Log.i(TAG, "Number of barcode elements is " + elems.size());
				// now iterate over elements
				for (int i = 0; i < elems.size(); ++i) {
					BarcodeElement elem = elems.get(i);
					// get the barcode element type
					ElementType elemType = elem.getElementType();
					// get raw bytes of the element
					byte[] rawBytes = elem.getElementBytes();

					// do with that data whatever you want
					// for example print it
					Log.i(TAG, "Element #" + i + " is of type: " + elemType.name());
					StringBuilder sb = new StringBuilder("{");
					for (int j = 0; j < rawBytes.length; ++j) {
						sb.append((int) rawBytes[j] & 0x0FF);
						if (j != rawBytes.length - 1) {
							sb.append(", ");
						}
					}
					sb.append("}");
					Log.i(TAG, sb.toString());
				}
			}

			// if barcode contains URL, create intent for browser
			// else, contain intent for message
			boolean barcodeDataIsUrl = false;

			try {
				@SuppressWarnings("unused")
				URL url = new URL(barcodeData);
				barcodeDataIsUrl = true;
			} catch (MalformedURLException exc) {
				barcodeDataIsUrl = false;
			}

			if (barcodeDataIsUrl) {
				// create intent for browser
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(barcodeData));
				startActivity(intent);
			} else {
				// ask user what to do with data
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, barcodeType + ": " + barcodeData);
				startActivity(Intent.createChooser(intent, getString(R.string.UseWith)));
			}
		}
	}
}
