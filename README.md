# Android pdf417.mobi integration instructions

The package contains two Android projects: 

 - Pdf417MobiSdk which is the library project which you include in your applications
 - Pdf417MobiDemo demo application which demonstrates the usage of Pdf417MobiSdk
 
 Pdf417.mobi is supported on Android SDK version 8 (Android 2.2) or later.
 
 The project contains one Activity called ScanActivity that is responsible for camera control and barcode recognition

## Quick start to get and run Android demo in Eclipse

1. Go to https://github.com/PDF417/android and copy GitHub code URL as shown on picture:

	![Copy GitHub URL](img/01-get-github-url.png)

2. Paste this URL to Eclipse Git Repository view:

	![Paste git URL to Eclipse](img/02-paste-github-url.png)

3. Pass through "Clone Git Repository" by clicking Next, Next and Finish:

	![Clone Git Repo step 1](img/03-github-step1.png)
	![Clone Git Repo step 2](img/04-github-step2.png)
	![Clone Git Repo step 3](img/05-github-step3.png)
	
   Cloned project shows as on this picture:

	![Project cloned](img/06-project-cloned.png)

4. Right click with your mouse on Package Explorer in Eclipse and then on "Import...":

	![Importing](img/07-importing-project.png)

5. Select "Android -> Existing Android Code Into Workspace" and then "Next":

	![Import step 1](img/08-importing-step1.png)

6. Click on "Browse..." and then search for "android" folder in your Eclipse workspace. Click OK.

	![Import step 2](img/09-importing-step2.png)

7. Just click on Finish to import the three projects:

	![Import step 3](img/10-importing-step3.png)

8. Three projects should appear:

	![Project imported](img/11-import-finished.png)

9. Plugin your Android phone to USB, right click on "Pdf417MobiDemo", then "Run As" and "Android Application" and voila, the app is built, installed and run on your phone:

	![Run PDF417 demo](img/12-install.png)

## How to integrate Pdf417MobiSdk into your project

1. Pdf417MobiSdk is an Android Library project with classes, resources and everything required to function properly. 
Simply place the project into your workspace and reference it from your application project. 

	![Referencing Pdf417MobiSdk](img/libraryref.png)
 	
2. Edit your `AndroidManifest.xml`. You should include camera, camera autofocus and OpenGL 2.0 features:

   		<uses-permission android:name="android.permission.CAMERA" />

   		<uses-feature android:name="android.hardware.camera" />
    	<uses-feature android:name="android.hardware.camera.autofocus" />
    	<uses-feature android:glEsVersion="0x00020000" />
	
	Also, add Pdf417ScanActivity entry:
	
		<activity android:name="mobi.pdf417.activity.Pdf417ScanActivity" android:label="@string/app_name" android:screenOrientation="portrait">
			<intent-filter>
				<action android:name="mobi.pdf417.activity.Pdf417ScanActivity" />
				<category android:name="android.intent.category.DEFAULT" />			
			</intent-filter>
		</activity>
		
3. If you are using ProGuard, add the following lines to your `proguard-project.txt`
 
 		-keep class net.photopay.** { *; }

		-keepclassmembers class net.photopay.** {
    		*;
		}
				
        -keep class mobi.pdf417.** { *; }
        
        -keepclassmembers class mobi.pdf417.** { 
            *; 
        }
        
        -keepattributes InnerClasses

        -keep class **.R
        -keep class **.R$* {
            <fields>;
        }
        
        -dontwarn android.hardware.**

		-dontwarn android.support.v4.**
 
4. You can start scanning process by starting `Pdf417ScanActivity` activity with Intent initialized in the following way:
    
		// Intent for ScanActivity
		Intent intent = new Intent(this, Pdf417ScanActivity.class);
		
        /** If you want sound to be played after the scanning process ends, 
         *  put here the resource ID of your sound file. 
         */
        intent.putExtra(Pdf417ScanActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);
				
		// Starting Activity
		startActivityForResult(intent, MY_REQUEST_CODE);
		

	`Pdf417ScanActivity` will return the result to your activity via intent passed to your `onActivityResult` method after user click `Use` button in dialog shown after successful scan. 
	
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

	You can also modify other resources, such as colors and camera overlay layouts. To change a color, simply open res/values/colors.xml and change the values of colors. Changing camera overlay layout is explained in demo application called `Pdf417CustomUIDemo`. In order to be able to change camera overlay, you must buy a license.
	
	License key is bound to package name of application which integrates the library. Demo license key works for package name `mobi.pdf417`. To integrate library properly into your application, obtain a license from [PDF417.mobi web]. 

## Pdf417MobiDemo application

In the package is the working demo application in which you can experiment with integration details.

## Additional info

If you have problems running the demo, try multiple refreshes, clean builds, close/open projects and Android Tools -> Fix Project Properties. In many situations this helps.

Also, feel free to contact us at <pdf417@photopay.net>.

[javadoc]: Javadoc/index.html
[PDF417.mobi web]: http://pdf417.mobi