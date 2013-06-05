# Android pdf417.mobi integration instructions

The package contains two Android projects: 

 - Pdf417MobiSdk which is the library project which you include in your applications
 - Pdf417MobiDemo demo application which demonstrates the usage of Pdf417MobiSdk
 
 Pdf417.mobi is supported on Android SDK version 8 (Android 2.2) or later.
 
 The project contains one Activity called ScanActivity that is responsible for camera control and barcode recognition

## How to integrate Pdf417MobiSdk into your project

1. Pdf417MobiSdk is an Android Library project with classes, resources and everything required to function properly. 
Simply place the project into your workspace and reference it from your application project. 

	![Referencing Pdf417MobiSdk](libraryref.png)
 	
2. Edit your `AndroidManifest.xml`. You should include camera, camera autofocus and OpenGL 2.0 features:

   		<uses-permission android:name="android.permission.CAMERA" />

   		<uses-feature android:name="android.hardware.camera" />
    	<uses-feature android:name="android.hardware.camera.autofocus" />
    	<uses-feature android:glEsVersion="0x00020000" />
	
	Also, add ScanActivity entry:
	
		<activity android:name="mobi.pdf417.activity.ScanActivity" android:label="@string/app_name" android:screenOrientation="portrait">
			<intent-filter>
				<action android:name="mobi.pdf417.activity.ScanActivity" />
				<category android:name="android.intent.category.DEFAULT" />			
			</intent-filter>
		</activity>
		
3. If you are using ProGuard, add the following line to your `proguard-project.txt`
 
 		-keep class net.photopay.** { *; }

		-keepclassmembers class net.photopay.** {
    		*;
		}
				
        -keep class mobi.pdf417.** { *; }
        
        -keepclassmembers class mobi.pdf417.** { 
            *; 
        }

		-dontwarn android.support.v4.**
 
4. You can start scanning process by starting `ScanActivity` activity with Intent initialized in the following way:
    
		// Intent for ScanActivity
		Intent intent = new Intent(this, ScanActivity.class);
		
		/** In order for library to work, a valid license key needs to be provided.
		  * Library is free for non-commercial and personal use.
		  * For pricing and licensing options, see http://pdf417.mobi
		  */
		intent.putExtra(BaseCameraActivity.EXTRAS_LICENSE_KEY, "4b2b088801ead5183cef6d5038b45003494ce5ca36a1e19fd145557926109e0865a1e794438f6c12a0");
		
        /** If you want sound to be played after the scanning process ends, 
         *  put here the resource ID of your sound file. 
         */
        intent.putExtra(ScanActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);
				
		// Starting Activity
		startActivityForResult(intent, MY_REQUEST_CODE);
		

	`ScanActivity` will return the result to your activity via intent passed to your `onActivityResult` method after user click `Use` button in dialog shown after successful scan.

	License key is bound to package name of application which integrates the library. Demo license key works for package name `mobi.pdf417`. To integrate library properly into your application, obtain a license from [PDF417.mobi web]. 
###NOTE
    - [PDF417.mobi web] is currently under construction. To obtain license key, contact us at <pdf417@photopay.net>. 
	
5. Obtaining the scanned data is done in the `onActivityResult` method. If the recognition returned some results, result code returned will be `BaseBarcodeActivity.RESULT_OK`. Optionally, if user tapped the `Copy` button in dialog, result code returned will be `BaseBarcodeActivity.RESULT_OK_DATA_COPIED` to indicate that barcode data is copied into clipboard. For example, your implementation of this method could look like this:

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			
            if(requestCode==MY_REQUEST_CODE && resultCode==BaseBarcodeActivity.RESULT_OK) {
                // read scanned barcode type (PDF417 or QR code)
                String barcodeType = data.getStringExtra(BaseBarcodeActivity.EXTRAS_BARCODE_TYPE);
                // read the data contained in barcode
                String barcodeData = data.getStringExtra(BaseBarcodeActivity.EXTRAS_RESULT);
                
                // ask user what to do with data
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, barcodeType + ": " + barcodeData);
                startActivity(Intent.createChooser(intent, getString(R.string.UseWith)));
            }
		}

## Translation and localization

- Adding new language

	Pdf417.mobi can easily be translated to other languages. The `res` folder in Pdf417MobiSdk library project has folder `values` which contains `strings.xml` - this file contains english strings. In order to make e.g. croatian translation, create a folder `values-hr` in your project and put the copy od `strings.xml` inside it. Then, open that file and change the english version strings into croatian version. 

- Modifying other resources.

	You can also modify other resources, such as colors and camera overlay layouts. To change a color, simply open res/valies/colors.xml and change the values of colors. Changing camera overlay layout is not recommended in this version as it might break the library. We have plans to better the support for creating totaly customized camera overlays for paying customers.

## Pdf417MobiDemo application

In the package is the working demo application in which you can experiment with integration details.

## Additional info

If you have problems running the demo, try multiple refreshes, clean builds, close/open projects and Android Tools -> Fix Project Properties. In many situations this helps.

Also, feel free to contact us at <pdf417@photopay.net>.

[javadoc]: Javadoc/index.html
[PDF417.mobi web]: http://pdf417.mobi