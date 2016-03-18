# _PDF417.mobi_ SDK for Android

[![Build Status](https://travis-ci.org/PDF417/pdf417-android.png)](https://travis-ci.org/PDF417/pdf417-android)

_PDF417.mobi_ SDK for Android is SDK that enables you to perform scans of various barcodes in your app. You can simply integrate the SDK into your app by following the instructions below and your app will be able to benefit the scanning feature for following barcode standards:

* [PDF417 barcode](https://en.wikipedia.org/wiki/PDF417)
* [QR code](https://en.wikipedia.org/wiki/QR_code)
* [Code 128](https://en.wikipedia.org/wiki/Code_128)
* [Code 38](https://en.wikipedia.org/wiki/Code_39)
* [EAN 13](https://en.wikipedia.org/wiki/International_Article_Number_(EAN))
* [EAN 8](https://en.wikipedia.org/wiki/EAN-8)
* [UPC A](https://en.wikipedia.org/wiki/Universal_Product_Code)
* [UPC E](https://en.wikipedia.org/wiki/Universal_Product_Code)
* [ITF](https://en.wikipedia.org/wiki/Interleaved_2_of_5)
* [Data Matrix](https://en.wikipedia.org/wiki/Data_Matrix)
* [Aztec](https://en.wikipedia.org/wiki/Aztec_Code)

Additionaly, _PDF417.mobi_ supports scanning and parsing barcodes from [United States' Driver's License](https://en.wikipedia.org/wiki/Driver%27s_license_in_the_United_States).

Using _PDF417.mobi_ in your app requires a valid license key. You can obtain a trial license key by registering to [Microblink dashboard](https://microblink.com/login). After registering, you will be able to generate a license key for your app. License key is bound to [package name](http://tools.android.com/tech-docs/new-build-system/applicationid-vs-packagename) of your app, so please make sure you enter the correct package name when asked.

See below for more information about how to integrate _PDF417.mobi_ SDK into your app and also check latest [Release notes](Release notes.md).

# Table of contents

* [Android _PDF417.mobi_ integration instructions](#intro)
* [Quick Start](#quickStart)
  * [Quick start with demo app](#quickDemo)
  * [Integrating _PDF417.mobi_ into your project using Maven](#mavenIntegration)
  * [Android studio integration instructions](#quickIntegration)
  * [Eclipse integration instructions](#eclipseIntegration)
  * [Performing your first scan](#quickScan)
* [Advanced _PDF417.mobi_ integration instructions](#advancedIntegration)
  * [Checking if _PDF417.mobi_ is supported](#supportCheck)
  * [Customization of `Pdf417ScanActivity` activity](#scanActivityCustomization)
  * [Embedding `RecognizerView` into custom scan activity](#recognizerView)
  * [`RecognizerView` reference](#recognizerViewReference)
* [Using direct API for recognition of Android Bitmaps](#directAPI)
  * [Understanding DirectAPI's state machine](#directAPIStateMachine)
  * [Using DirectAPI while RecognizerView is active](#directAPIWithRecognizer)
  * [Obtaining various metadata with _MetadataListener_](#metadataListener)
  * [Using ImageListener to obtain images that are being processed](#imageListener)
* [Recognition settings and results](#recognitionSettingsAndResults)
  * [[Recognition settings](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/settings/RecognitionSettings.html)](#recognitionSettings)
  * [Scanning PDF417 barcodes](#pdf417Recognizer)
  * [Scanning US Driver's licence barcodes](#usdl)
  * [Scanning one dimensional barcodes with _PDF417.mobi_'s implementation](#custom1DBarDecoder)
  * [Scanning barcodes with ZXing implementation](#zxing)
* [Translation and localization](#translation)
* [Embedding _PDF417.mobi_ inside another SDK](#embedAAR)
  * [_PDF417.mobi_ licensing model](#licensingModel)
  * [Ensuring the final app gets all resources required by _PDF417.mobi_](#sdkIntegrationIntoApp)
* [Processor architecture considerations](#archConsider)
  * [Reducing the final size of your app](#reduceSize)
  * [Combining _PDF417.mobi_ with other native libraries](#combineNativeLibraries)
* [Troubleshooting](#troubleshoot)
  * [Integration problems](#integrationTroubleshoot)
  * [SDK problems](#sdkTroubleshoot)
* [Additional info](#info)

# <a name="intro"></a> Android _PDF417.mobi_ integration instructions

The package contains Android Archive (AAR) that contains everything you need to use _PDF417.mobi_ library. Besides AAR, package also contains a demo project that contains following modules:

- _Pdf417MobiDemo_ shows how to use simple Intent-based API to scan single barcode.
- _Pdf417MobiDemoCustomUI_ demonstrates advanced integration within custom scan activity.
- _Pdf417MobiDirectAPIDemo_ demonstrates how to perform scanning of [Android Bitmaps](https://developer.android.com/reference/android/graphics/Bitmap.html)
 
_PDF417.mobi_ is supported on Android SDK version 10 (Android 2.3.3) or later.

The library contains one activity: `Pdf417ScanActivity`. It is responsible for camera control and recognition. You can also create your own scanning UI - you just need to embed `RecognizerView` into your activity and pass activity's lifecycle events to it and it will control the camera and recognition process. For more information, see [Embedding `RecognizerView` into custom scan activity](#recognizerView).

# <a name="quickStart"></a> Quick Start

## <a name="quickDemo"></a> Quick start with demo app

1. Open Android Studio.
2. In Quick Start dialog choose _Import project (Eclipse ADT, Gradle, etc.)_.
3. In File dialog select _Pdf417MobiDemo_ folder.
4. Wait for project to load. If Android studio asks you to reload project on startup, select `Yes`.

## <a name="mavenIntegration"></a> Integrating _PDF417.mobi_ into your project using Maven

Maven repository for _PDF417.mobi_ SDK is: [http://maven.microblink.com](http://maven.microblink.com). If you do not want to perform integration via Maven, simply skip to [Android Studio integration instructions](#quickIntegration) or [Eclipse integration instructions](#eclipseIntegration).

### Using gradle or Android Studio

In your `build.gradle` you first need to add _PDF417.mobi_ maven repository to repositories list:

```
repositories {
	maven { url 'http://maven.microblink.com' }
}
```

After that, you just need to add _PDF417.mobi_ as a dependency to your application (make sure, `transitive` is set to true):

```
dependencies {
    compile('com.microblink:pdf417.mobi:5.4.0') {
    	transitive = true
    }
}
```

If you plan to use ProGuard, add following lines to your `proguard-rules.pro`:
	
```
-keep class com.microblink.** { *; }
-keepclassmembers class com.microblink.** { *; }
-dontwarn android.hardware.**
-dontwarn android.support.v4.**
```

### Using android-maven-plugin

[Android Maven Plugin](https://simpligility.github.io/android-maven-plugin/) v4.0.0 or newer is required.

Open your `pom.xml` file and add these directives as appropriate:

```xml
<repositories>
   	<repository>
       	<id>MicroblinkRepo</id>
       	<url>http://maven.microblink.com</url>
   	</repository>
</repositories>

<dependencies>
	<dependency>
		  <groupId>com.microblink</groupId>
		  <artifactId>pdf417.mobi</artifactId>
		  <version>5.4.0</version>
		  <type>aar</type>
  	</dependency>
</dependencies>
```

## <a name="quickIntegration"></a> Android studio integration instructions

1. In Android Studio menu, click _File_, select _New_ and then select _Module_.
2. In new window, select _Import .JAR or .AAR Package_, and click _Next_.
3. In _File name_ field, enter the path to _LibRecognizer.aar_ and click _Finish_.
4. In your app's `build.gradle`, add dependency to `LibRecognizer` and appcompat-v7:

	```
	dependencies {
   		compile project(':LibRecognizer')
 		compile "com.android.support:appcompat-v7:23.1.1"
	}
	```
5. If you plan to use ProGuard, add following lines to your `proguard-rules.pro`:
	
	```
	-keep class com.microblink.** { *; }
	-keepclassmembers class com.microblink.** { *; }
	-dontwarn android.hardware.**
	-dontwarn android.support.v4.**
	```
	
## <a name="eclipseIntegration"></a> Eclipse integration instructions

We do not provide Eclipse integration demo apps. We encourage you to use Android Studio. We also do not test integrating _PDF417.mobi_ with Eclipse. If you are having problems with _PDF417.mobi_, make sure you have tried integrating it with Android Studio prior contacting us.

However, if you still want to use Eclipse, you will need to convert AAR archive to Eclipse library project format. You can do this by doing the following:

1. In Eclipse, create a new _Android library project_ in your workspace.
2. Clear the `src` and `res` folders.
3. Unzip the `LibRecognizer.aar` file. You can rename it to zip and then unzip it using any tool.
4. Copy the `classes.jar` to `libs` folder of your Eclipse library project. If `libs` folder does not exist, create it.
5. Copy the contents of `jni` folder to `libs` folder of your Eclipse library project.
6. Replace the `res` folder on library project with the `res` folder of the `LibRecognizer.aar` file.

You’ve already created the project that contains almost everything you need. Now let’s see how to configure your project to reference this library project.

1. In the project you want to use the library (henceforth, "target project") add the library project as a dependency
2. Open the `AndroidManifest.xml` file inside `LibRecognizer.aar` file and make sure to copy all permissions, features and activities to the `AndroidManifest.xml` file of the target project.
3. Copy the contents of `assets` folder from `LibRecognizer.aar` into `assets` folder of target project. If `assets` folder in target project does not exist, create it.
4. Clean and Rebuild your target project
5. If you plan to use ProGuard, add same statements as in [Android studio guide](#quickIntegration) to your ProGuard configuration file.
6. Add appcompat-v7 library to your workspace and reference it by target project (modern ADT plugin for Eclipse does this automatically for all new android projects).

## <a name="quickScan"></a> Performing your first scan
1. You can start recognition process by starting `Pdf417ScanActivity` activity with Intent initialized in the following way:
	
	```java
	// Intent for Pdf417ScanActivity Activity
	Intent intent = new Intent(this, Pdf417ScanActivity.class);
	
	// set your licence key
	// obtain your licence key at http://microblink.com/login or
	// contact us at http://help.microblink.com
	intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, "Add your licence key here");

	RecognitionSettings settings = new RecognitionSettings();
	// setup array of recognition settings (described in chapter "Recognition 
	// settings and results")
	settings.setRecognizerSettingsArray(setupSettingsArray());
	intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_SETTINGS, settings);

	// Starting Activity
	startActivityForResult(intent, MY_REQUEST_CODE);
	```
2. After `Pdf417ScanActivity` activity finishes the scan, it will return to the calling activity and will call method `onActivityResult`. You can obtain the scanning results in that method.

	```java
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == MY_REQUEST_CODE) {
			if (resultCode == Pdf417ScanActivity.RESULT_OK && data != null) {
				// perform processing of the data here
				
				// for example, obtain parcelable recognition result
				Bundle extras = data.getExtras();
				RecognitionResults result = data.getParcelableExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_RESULTS);

				// get array of recognition results
				BaseRecognitionResult[] resultArray = result.getRecognitionResults();				
				// Each element in resultArray inherits BaseRecognitionResult class and
				// represents the scan result of one of activated recognizers that have
				// been set up. More information about this can be found in 
				// "Recognition settings and results" chapter
						
				// Or, you can pass the intent to another activity
				data.setComponent(new ComponentName(this, ResultActivity.class));
				startActivity(data);
			}
		}
	}
	```
	
	For more information about defining recognition settings and obtaining scan results see [Recognition settings and results](#recognitionSettingsAndResults).

# <a name="advancedIntegration"></a> Advanced _PDF417.mobi_ integration instructions
This section will cover more advanced details in _PDF417.mobi_ integration. First part will discuss the methods for checking whether _PDF417.mobi_ is supported on current device. Second part will cover the possible customization of builtin `Pdf417ScanActivity` activity, third part will describe how to embed `RecognizerView` into your activity and fourth part will describe how to use direct API to recognize directly android bitmaps without the need of camera.

## <a name="supportCheck"></a> Checking if _PDF417.mobi_ is supported

### _PDF417.mobi_ requirements
Even before starting the scan activity, you should check if _PDF417.mobi_ is supported on current device. In order to be supported, device needs to have camera. 

Android 2.3 is the minimum android version on which _PDF417.mobi_ is supported.

Camera video preview resolution also matters. In order to perform successful scans, camera preview resolution cannot be too low. _PDF417.mobi_ requires minimum 320p camera preview resolution in order to perform scan. It must be noted that camera preview resolution is not the same as the video record resolution, although on most devices those are the same. However, there are some devices that allow recording of HD video (720p resolution), but do not allow high enough camera preview resolution (for example, [Sony Xperia Go](http://www.gsmarena.com/sony_xperia_go-4782.php) supports video record resolution at 720p, but camera preview resolution is only 320p - _PDF417.mobi_ does not work on that device).

_PDF417.mobi_ is native application, written in C++ and available for multiple platforms. Because of this, _PDF417.mobi_ cannot work on devices that have obscure hardware architectures. We have compiled _PDF417.mobi_ native code only for most popular Android [ABIs](https://en.wikipedia.org/wiki/Application_binary_interface). See [Processor architecture considerations](#archConsider) for more information about native libraries in _PDF417.mobi_ and instructions how to disable certain architectures in order to reduce the size of final app.

### Checking for _PDF417.mobi_ support in your app
To check whether the _PDF417.mobi_ is supported on the device, you can do it in the following way:
	
```java
// check if PDF417.mobi is supported on the device
RecognizerCompatibilityStatus status = RecognizerCompatibility.getRecognizerCompatibilityStatus(this);
if(status == RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED) {
	Toast.makeText(this, "PDF417.mobi is supported!", Toast.LENGTH_LONG).show();
} else {
	Toast.makeText(this, "PDF417.mobi is not supported! Reason: " + status.name(), Toast.LENGTH_LONG).show();
}
```

However, some recognizers require camera with autofocus. If you try to start recognition with those recognizers on a device that does not have camera with autofocus, you will get an error. To prevent that, when you prepare the array with recognition settings (see [Recognition settings and results](#recognitionSettingsAndResults) for settings reference), you can easily filter out all settings that require autofocus from array using the following code snippet:

```java
// setup array of recognition settings (described in chapter "Recognition 
// settings and results")
RecognizerSettings[] settArray = setupSettingsArray();
if(!RecognizerCompatibility.cameraHasAutofocus(CameraType.CAMERA_BACKFACE, this)) {
	setarr = RecognizerSettingsUtils.filterOutRecognizersThatRequireAutofocus(setarr);
}
```

## <a name="scanActivityCustomization"></a> Customization of `Pdf417ScanActivity` activity

### `Pdf417ScanActivity` intent extras

This section will discuss possible parameters that can be sent over `Intent` for `Pdf417ScanActivity` activity that can customize default behaviour. There are several intent extras that can be sent to `Pdf417ScanActivity` actitivy:

* **`Pdf417ScanActivity.EXTRAS_CAMERA_TYPE`** - with this extra you can define which camera on device will be used. To set the extra to intent, use the following code snippet:
	
	```java
	intent.putExtra(Pdf417ScanActivity.EXTRAS_CAMERA_TYPE, (Parcelable)CameraType.CAMERA_FRONTFACE);
	```
	
* **`Pdf417ScanActivity.EXTRAS_CAMERA_ASPECT_MODE`** - with this extra you can define which [camera aspect mode](https://pdf417.github.io/pdf417-android/com/microblink/view/CameraAspectMode.html) will be used. If set to `ASPECT_FIT` (default), then camera preview will be letterboxed inside available view space. If set to `ASPECT_FILL`, camera preview will be zoomed and cropped to use the entire view space. To set the extra to intent, use the following code snippet:

	```java
	intent.putExtra(Pdf417ScanActivity.EXTRAS_CAMERA_ASPECT_MODE, (Parcelable)CameraAspectMode.ASPECT_FIT);
	```
	
* **`Pdf417ScanActivity.EXTRAS_RECOGNITION_SETTINGS`** - with this extra you can define settings that affect whole recognition process. This includes both array of recognizer settings and global recognition settings. More information about recognition settings can be found in chapter [Recognition settings and results](#recognitionSettingsAndResults). To set the extra to intent, use the following code snippet:
	
	```java
	RecognitionSettings recognitionSettings = new RecognitionSettings();
	// define additional settings; e.g set timeout to 10 seconds
	recognitionSettings.setNumMsBeforeTimeout(10000);
	// setup recognizer settings array
	recognitionSettings.setRecognizerSettingsArray(setupSettingsArray());
	intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_SETTINGS, recognitionSettings);
	```
		
* **`Pdf417ScanActivity.EXTRAS_RECOGNITION_RESULTS`** - you can use this extra in `onActivityResult` method of calling activity to obtain recognition results. For more information about recognition settings and result, see [Recognition settings and results](#recognitionSettingsAndResults). You can use the following snippet to obtain scan results:

	```java
	RecognitionResults results = data.getParcelableExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_RESULTS);
	```
	
* **`Pdf417ScanActivity.EXTRAS_OPTIMIZE_CAMERA_FOR_NEAR_SCANNING`** - with this extra you can give a hint to _PDF417.mobi_ to optimize camera parameters for near object scanning. When camera parameters are optimized for near object scanning, macro focus mode will be preferred over autofocus mode. Thus, camera will have easier time focusing on to near objects, but might have harder time focusing on far objects. If you expect that most of your scans will be performed by holding the device very near the object, turn on that parameter. By default, this parameter is set to false.
	
* **`Pdf417ScanActivity.EXTRAS_BEEP_RESOURCE`** - with this extra you can set the resource ID of the sound to be played when scan completes. You can use following snippet to set this extra:

	```java
	intent.putExtra(Pdf417ScanActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);
    ```
* **`Pdf417ScanActivity.EXTRAS_SPLASH_SCREEN_LAYOUT_RESOURCE`** - with this extra you can set the resource ID of the layout that will be used as camera splash screen while camera is being initialized. You can use following snippet to set this extra:

	```java
	intent.putExtra(Pdf417ScanActivity. EXTRAS_SPLASH_SCREEN_LAYOUT_RESOURCE, R.layout.camera_splash);
    ```
	
* **`Pdf417ScanActivity.EXTRAS_SHOW_FOCUS_RECTANGLE`** - with this extra you can enable showing of rectangle that displays area camera uses to measure focus and brightness when automatically adjusting its parameters. You can enable showing of this rectangle with following code snippet:

	```java
	intent.putExtra(Pdf417ScanActivity.EXTRAS_SHOW_FOCUS_RECTANGLE, true);
	```
	
* **`Pdf417ScanActivity.EXTRAS_ALLOW_PINCH_TO_ZOOM`** - with this extra you can set whether pinch to zoom will be allowed on camera activity. Default is `false`. To enable pinch to zoom gesture on camera activity, use the following code snippet:

	```java
	intent.putExtra(Pdf417ScanActivity.EXTRAS_ALLOW_PINCH_TO_ZOOM, true);
	```
* **`Pdf417ScanActivity.EXTRAS_CAMERA_VIDEO_PRESET`** - with this extra you can set the video resolution preset that will be used when choosing camera resolution for scanning. For more information, see [javadoc](https://pdf417.github.io/pdf417-android/com/microblink/hardware/camera/VideoResolutionPreset.html). For example, to use 720p video resolution preset, use the following code snippet:

	```java
	intent.putExtra(Pdf417ScanActivity.EXTRAS_CAMERA_VIDEO_PRESET, (Parcelable)VideoResolutionPreset.VIDEO_RESOLUTION_720p);
	```

* **`Pdf417ScanActivity.EXTRAS_LICENSE_KEY`** - with this extra you can set the license key for _PDF417.mobi_. You can obtain your licence key from [Microblink website](http://microblink.com/login) or you can contact us at [http://help.microblink.com](http://help.microblink.com). Once you obtain a license key, you can set it with following snippet:

	```java
	// set the license key
	intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, "Enter_License_Key_Here");
	```
	
	Licence key is bound to package name of your application. For example, if you have licence key that is bound to `mobi.pdf417.demo` app package, you cannot use the same key in other applications. However, if you purchase Premium licence, you will get licence key that can be used in multiple applications. This licence key will then not be bound to package name of the app. Instead, it will be bound to the licencee string that needs to be provided to the library together with the licence key. To provide licencee string, use the `EXTRAS_LICENSEE` intent extra like this:

	```java
	// set the license key
	intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, "Enter_License_Key_Here");
	intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSEE, "Enter_Licensee_Here");
	```

* **`Pdf417ScanActivity.EXTRAS_IMAGE_LISTENER`** - with this extra you can set your implementation of [ImageListener interface](https://pdf417.github.io/pdf417-android/com/microblink/image/ImageListener.html) that will obtain images that are being processed. Make sure that your [ImageListener](https://pdf417.github.io/pdf417-android/com/microblink/image/ImageListener.html) implementation correctly implements [Parcelable](https://developer.android.com/reference/android/os/Parcelable.html) interface with static [CREATOR](https://developer.android.com/reference/android/os/Parcelable.Creator.html) field. Without this, you might encounter a runtime error. For more information and example, see [Using ImageListener to obtain images that are being processed](#imageListener).

* **`Pdf417ScanActivity.EXTRAS_SHOW_DIALOG_AFTER_SCAN`** - with this extra you can prevent showing of dialog after each barcode scan. By default, each time scanner finds and decodes a barcode, a dialog with barcode's contents will be shown. To prevent this, use the following snippet:
	
	```java
	// disable showing of dialog after scan
	intent.putExtra(Pdf417ScanActivity.EXTRAS_SHOW_DIALOG_AFTER_SCAN, false);
	```

### Customizing `Pdf417ScanActivity` appearance

Besides possibility to put various intent extras for customizing `Pdf417ScanActivity` behaviour, you can also change strings it displays. The procedure for changing strings in `Pdf417ScanActivity` activity are explained in [Translation and localization](#stringChanging) section.

#### Modifying other resources.

Generally, you can also change other resources that `Pdf417ScanActivity` uses, but you are encouraged to create your own custom scan activity instead (see [Embedding `RecognizerView` into custom scan activity](#recognizerView)).

#### Changing viewfinder appearance

To change the colour of viewfinder in `Pdf417ScanActivity`, change or override the colours defined in `res/values/colors.xml` (colours `default_frame` and `recognized_frame`).

## <a name="recognizerView"></a> Embedding `RecognizerView` into custom scan activity
This section will discuss how to embed [RecognizerView](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html) into your scan activity and perform scan.

1. First make sure that `RecognizerView` is a member field in your activity. This is required because you will need to pass all activity's lifecycle events to `RecognizerView`.
2. It is recommended to keep your scan activity in one orientation, such as `portrait` or `landscape`. Setting `sensor` as scan activity's orientation will trigger full restart of activity whenever device orientation changes. This will provide very poor user experience because both camera and _PDF417.mobi_ native library will have to be restarted every time. There are measures for this behaviour and will be discussed [later](#scanOrientation).
3. In your activity's `onCreate` method, create a new `RecognizerView`, define its [settings and listeners](#recognizerViewReference) and then call its `create` method. After that, add your views that should be layouted on top of camera view.
4. Override your activity's `onStart`, `onResume`, `onPause`, `onStop` and `onDestroy` methods and call `RecognizerView's` lifecycle methods `start`, `resume`, `pause`, `stop` and `destroy`. This will ensure correct camera and native resource management. If you plan to manage `RecognizerView's` lifecycle independently of host activity's lifecycle, make sure the order of calls to lifecycle methods is the same as is with activities (i.e. you should not call `resume` method if `create` and `start` were not called first).

Here is the minimum example of integration of `RecognizerView` as the only view in your activity:

```java
public class MyScanActivity extends Activity implements ScanResultListener, CameraEventsListener {
	private static final int PERMISSION_CAMERA_REQUEST_CODE = 69;
	private RecognizerView mRecognizerView;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {				
		// create RecognizerView
		mRecognizerView = new RecognizerView(this);
		   
		RecognitionSettings settings = new RecognitionSettings();
		// setup array of recognition settings (described in chapter "Recognition 
		// settings and results")
		RecognizerSettings[] settArray = setupSettingsArray();
		if(!RecognizerCompatibility.cameraHasAutofocus(CameraType.CAMERA_BACKFACE, this)) {
			settArray = RecognizerSettingsUtils.filterOutRecognizersThatRequireAutofocus(settArray);
		}
		settings.setRecognizerSettingsArray(settArray);
		mRecognizerView.setRecognitionSettings(settings);
		
		try {
		    // set license key
		    mRecognizerView.setLicenseKey(this, "your license key");
		} catch (InvalidLicenceKeyException exc) {
		    finish();
		    return;
		}
		
		// scan result listener will be notified when scan result gets available
		mRecognizerView.setScanResultListener(this);
		// camera events listener will be notified about camera lifecycle and errors
		mRecognizerView.setCameraEventsListener(this);
		
		// set camera aspect mode
		// ASPECT_FIT will fit the camera preview inside the view
		// ASPECT_FILL will zoom and crop the camera preview, but will use the
		// entire view surface
		mRecognizerView.setAspectMode(CameraAspectMode.ASPECT_FILL);
		   
		mRecognizerView.create();
		
		setContentView(mRecognizerView);
	}
	
	@Override
	protected void onStart() {
	   super.onStart();
	   // you need to pass all activity's lifecycle methods to RecognizerView
	   mRecognizerView.start();
	}
	
	@Override
	protected void onResume() {
	   	super.onResume();
	   	// you need to pass all activity's lifecycle methods to RecognizerView
       mRecognizerView.resume();
	}

	@Override
	protected void onPause() {
	   	super.onPause();
	   	// you need to pass all activity's lifecycle methods to RecognizerView
		mRecognizerView.pause();
	}

	@Override
	protected void onStop() {
	   super.onStop();
	   // you need to pass all activity's lifecycle methods to RecognizerView
	   mRecognizerView.stop();
	}
	
	@Override
	protected void onDestroy() {
	   super.onDestroy();
	   // you need to pass all activity's lifecycle methods to RecognizerView
	   mRecognizerView.destroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	   super.onConfigurationChanged(newConfig);
	   // you need to pass all activity's lifecycle methods to RecognizerView
	   mRecognizerView.changeConfiguration(newConfig);
	}
		
    @Override
    public void onScanningDone(RecognitionResults results) {
    	// this method is from ScanResultListener and will be called when scanning completes
    	// RecognitionResults may contain multiple results in array returned
    	// by method getRecognitionResults().
    	// This depends on settings in RecognitionSettings object that was
    	// given to RecognizerView.
    	// For more information, see chapter "Recognition settings and results")
    	
    	// After this method ends, scanning will be resumed and recognition
    	// state will be retained. If you want to prevent that, then
    	// you should call:
    	// mRecognizerView.resetRecognitionState();

		// If you want to pause scanning to prevent receiving recognition
		// results, you should call:
		// mRecognizerView.pauseScanning();
		// After scanning is paused, you will have to resume it with:
		// mRecognizerView.resumeScanning(true);
		// boolean in resumeScanning method indicates whether recognition
		// state should be automatically reset when resuming scanning
    }
    
    @Override
    public void onCameraPreviewStarted() {
        // this method is from CameraEventsListener and will be called when camera preview starts
    }
    
    @Override
    public void onCameraPreviewStopped() {
        // this method is from CameraEventsListener and will be called when camera preview stops
    }

    @Override
    public void onError(Throwable exc) {
        /** 
         * This method is from CameraEventsListener and will be called when 
         * opening of camera resulted in exception or recognition process
         * encountered an error. The error details will be given in exc
         * parameter.
         */
    }
    
    @Override
    @TargetApi(23)
    public void onCameraPermissionDenied() {
    	/**
    	 * Called on Android 6.0 and newer if camera permission is not given
    	 * by user. You should request permission from user to access camera.
    	 */
    	 requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST_CODE);
    	 /**
    	  * Please note that user might have not given permission to use 
    	  * camera. In that case, you have to explain to user that without
    	  * camera permissions scanning will not work.
    	  * For more information about requesting permissions at runtime, check
    	  * this article:
    	  * https://developer.android.com/training/permissions/requesting.html
    	  */
    }
    
    @Override
    public void onAutofocusFailed() {
	    /**
	     * This method is from CameraEventsListener will be called when camera focusing has failed. 
	     * Camera manager usually tries different focusing strategies and this method is called when all 
	     * those strategies fail to indicate that either object on which camera is being focused is too 
	     * close or ambient light conditions are poor.
	     */
    }
    
    @Override
    public void onAutofocusStarted(Rect[] areas) {
	    /**
	     * This method is from CameraEventsListener and will be called when camera focusing has started.
	     * You can utilize this method to draw focusing animation on UI.
	     * Areas parameter is array of rectangles where focus is being measured. 
	     * It can be null on devices that do not support fine-grained camera control.
	     */
    }

    @Override
    public void onAutofocusStopped(Rect[] areas) {
	    /**
	     * This method is from CameraEventsListener and will be called when camera focusing has stopped.
	     * You can utilize this method to remove focusing animation on UI.
	     * Areas parameter is array of rectangles where focus is being measured. 
	     * It can be null on devices that do not support fine-grained camera control.
	     */
    }
}
```

### <a name="scanOrientation"></a> Scan activity's orientation

If activity's `screenOrientation` property in `AndroidManifest.xml` is set to `sensor`, `fullSensor` or similar, activity will be restarted every time device changes orientation from portrait to landscape and vice versa. While restarting activity, its `onPause`, `onStop` and `onDestroy` methods will be called and then new activity will be created anew. This is a potential problem for scan activity because in its lifecycle it controls both camera and native library - restarting the activity will trigger both restart of the camera and native library. This is a problem because changing orientation from landscape to portrait and vice versa will be very slow, thus degrading a user experience. **We do not recommend such setting.**

For that matter, we recommend setting your scan activity to either `portrait` or `landscape` mode and handle device orientation changes manually. To help you with this, `RecognizerView` supports adding child views to it that will be rotated regardless of activity's `screenOrientation`. You add a view you wish to be rotated (such as view that contains buttons, status messages, etc.) to `RecognizerView` with `addChildView` method. The second parameter of the method is a boolean that defines whether the view you are adding will be rotated with device. To define allowed orientations, implement [OrientationAllowedListener](https://pdf417.github.io/pdf417-android/com/microblink/view/OrientationAllowedListener.html) interface and add it to `RecognizerView` with method `setOrientationAllowedListener`. **This is the recommended way of rotating camera overlay.**

However, if you really want to set `screenOrientation` property to `sensor` or similar and want Android to handle orientation changes of your scan activity, then we recommend to set `configChanges` property of your activity to `orientation|screenSize`. This will tell Android not to restart your activity when device orientation changes. Instead, activity's `onConfigurationChanged` method will be called so that activity can be notified of the configuration change. In your implementation of this method, you should call `changeConfiguration` method of `RecognizerView` so it can adapt its camera surface and child views to new configuration. Note that on Android versions older than 4.0 changing of configuration will require restart of camera, which can be slow.

## <a name="recognizerViewReference"></a> `RecognizerView` reference
The complete reference of `RecognizerView` is available in [Javadoc](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html). The usage example is provided in `pdf417MobiDemoCustomUI` demo app provided with SDK. This section just gives a quick overview of `RecognizerView's` most important methods.

##### <a name="recognizerView_create"></a> [`create()`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#create--)
This method should be called in activity's `onCreate` method. It will initialize `RecognizerView's` internal fields and will initialize camera control thread. This method must be called after all other settings are already defined, such as listeners and recognition settings. After calling this method, you can add child views to `RecognizerView` with method `addChildView(View, boolean)`.

##### <a name="recognizerView_start"></a> [`start()`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#start--)
This method should be called in activity's `onStart` method. It will initialize background processing thread and start native library initialization on that thread.

##### <a name="recognizerView_resume"></a> [`resume()`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#resume--)
This method should be called in activity's `onResume` method. It will trigger background initialization of camera. After camera is loaded, it will start camera frame recognition, except if scanning loop is paused.

##### <a name="recognizerView_pause"></a> [`pause()`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#pause--)
This method should be called in activity's `onPause` method. It will stop the camera, but will keep native library loaded.

##### <a name="recognizerView_stop"></a> [`stop()`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#stop--)
This method should be called in activity's `onStop` method. It will deinitialize native library, terminate background processing thread and free all resources that are no longer necessary.

##### <a name="recognizerView_destroy"></a> [`destroy()`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#destroy--)
This method should be called in activity's `onDestroy` method. It will free all resources allocated in `create()` and will terminate camera control thread.

##### <a name="recognizerView_changeConfiguration"></a> [`changeConfiguration(Configuration)`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#changeConfiguration-android.content.res.Configuration-)
This method should be called in activity's `onConfigurationChanged` method. It will adapt camera surface to new configuration without the restart of the activity. See [Scan activity's orientation](#scanOrientation) for more information.

##### <a name="recognizerView_setCameraType"></a> [`setCameraType(CameraType)`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#setCameraType-com.microblink.hardware.camera.CameraType-)
With this method you can define which camera on device will be used. Default camera used is back facing camera.

##### <a name="recognizerView_setAspectMode"></a> [`setAspectMode(CameraAspectMode)`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#setAspectMode-com.microblink.view.CameraAspectMode-)
Define the [aspect mode of camera](https://pdf417.github.io/pdf417-android/com/microblink/view/CameraAspectMode.html). If set to `ASPECT_FIT` (default), then camera preview will be letterboxed inside available view space. If set to `ASPECT_FILL`, camera preview will be zoomed and cropped to use the entire view space.

##### <a name="recognizerView_setVideoResolutionPreset"></a> [`setVideoResolutionPreset(VideoResolutionPreset)`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#setVideoResolutionPreset-com.microblink.hardware.camera.VideoResolutionPreset-)
Define the [video resolution preset](https://pdf417.github.io/pdf417-android/com/microblink/hardware/camera/VideoResolutionPreset.html) that will be used when choosing camera resolution for scanning.

##### <a name="recognizerView_setRecognitionSettings"></a> [`setRecognitionSettings(RecognitionSettings)`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#setRecognitionSettings-com.microblink.recognizers.settings.RecognitionSettings-)
With this method you can set recognition settings that contains information what will be scanned and how will scan be performed. For more information about recognition settings and results see [Recognition settings and results](#recognitionSettingsAndResults). This method must be called before `create()`.

##### <a name="recognizerView_reconfigureRecognizers1"></a> [`reconfigureRecognizers(RecognitionSettings)`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#reconfigureRecognizers-com.microblink.recognizers.settings.RecognitionSettings-)
With this method you can reconfigure the recognition process while recognizer is active. Unlike `setRecognitionSettings`, this method must be called while recognizer is active (i.e. after `resume` was called). For more information about recognition settings see [Recognition settings and results](#recognitionSettingsAndResults).

##### <a name="recognizerView_setOrientationAllowedListener"></a> [`setOrientationAllowedListener(OrientationAllowedListener)`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#setOrientationAllowedListener-com.microblink.view.OrientationAllowedListener-)
With this method you can set a [OrientationAllowedListener](https://pdf417.github.io/pdf417-android/com/microblink/view/OrientationAllowedListener.html) which will be asked if current orientation is allowed. If orientation is allowed, it will be used to rotate rotatable views to it and it will be passed to native library so that recognizers can be aware of the new orientation. If you do not set this listener, recognition will be performed only in orientation defined by current activity's orientation.

##### <a name="recognizerView_setScanResultListener"></a> [`setScanResultListener(ScanResultListener)`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#setScanResultListener-com.microblink.view.recognition.ScanResultListener-)
With this method you can set a [ScanResultListener](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/ScanResultListener.html) which will be notified when recognition completes. After recognition completes, `RecognizerView` will pause its scanning loop and to continue the scanning you will have to call `resumeScanning` method. In this method you can obtain data from scanning results. For more information see [Recognition settings and results](#recognitionSettingsAndResults).

##### <a name="recognizerView_setCameraEventsListener"></a> [`setCameraEventsListener(CameraEventsListener)`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#setCameraEventsListener-com.microblink.view.CameraEventsListener-)
With this method you can set a [CameraEventsListener](https://pdf417.github.io/pdf417-android/com/microblink/view/CameraEventsListener.html) which will be notified when various camera events occur, such as when camera preview has started, autofocus has failed or there has been an error while using the camera or performing the recognition.

##### <a name="recognizerView_pauseScanning"></a> [`pauseScanning()`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#pauseScanning--)
This method pauses the scanning loop, but keeps both camera and native library initialized. Pause and resume scanning methods count the number of calls, so if you called `pauseScanning()` twice, you will have to call `resumeScanning` twice to actually resume scanning.

##### <a name="recognizerView_resumeScanning"></a> [`resumeScanning(boolean)`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#resumeScanning-boolean-)
With this method you can resume the paused scanning loop. If called with `true` parameter, implicitly calls `resetRecognitionState()`. If called with `false`, old recognition state will not be reset, so it could be reused for boosting recognition result. This may not be always a desired behaviour.  Pause and resume scanning methods count the number of calls, so if you called `pauseScanning()` twice, you will have to call `resumeScanning` twice to actually resume scanning loop.


##### <a name="recognizerView_setInitialScanningPaused"></a> [`setInitialScanningPaused()`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#setInitialScanningPaused-boolean-)
This method lets you set up RecognizerView to not automatically resume scanning first time [resume](#recognizerView_resume) is called. An example use case of when you might want this is if you want to display onboarding help when opening camera first time and want to prevent scanning in background while onboarding is displayed over camera preview.

##### <a name="recognizerView_resetRecognitionState"></a> [`resetRecognitionState()`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#resetRecognitionState--)
With this method you can reset internal recognition state. State is usually kept to improve recognition quality over time, but without resetting recognition state sometimes you might get poorer results (for example if you scan one object and then another without resetting state you might end up with result that contains properties from both scanned objects).

##### <a name="recognizerView_addChildView"></a> [`addChildView(View, boolean)`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#addChildView-android.view.View-boolean-)
With this method you can add your own view on top of `RecognizerView`. `RecognizerView` will ensure that your view will be layouted exactly above camera preview surface (which can be letterboxed if aspect ratio of camera preview size does not match the aspect ratio of `RecognizerView` and camera aspect mode is set to `ASPECT_FIT`). Boolean parameter defines whether your view should be rotated with device orientation changes. The rotation is independent of host activity's orientation changes and allowed orientations will be determined from [OrientationAllowedListener](https://pdf417.github.io/pdf417-android/com/microblink/view/OrientationAllowedListener.html). See also [Scan activity's orientation](#scanOrientation) for more information why you should rotate your views independently of activity.

##### <a name="recognizerView_isCameraFocused"></a> [`isCameraFocused()`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#isCameraFocused--) 
This method returns `true` if camera thinks it has focused on object. Note that camera has to be active for this method to work. If camera is not active, returns `false`.

##### <a name="recognizerView_focusCamera"></a> [`focusCamera()`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#focusCamera--) 
This method requests camera to perform autofocus. If camera does not support autofocus feature, method does nothing. Note that camera has to be active for this method to work.

##### <a name="recognizerView_isCameraTorchSupported"></a> [`isCameraTorchSupported()`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#isCameraTorchSupported--)
This method returns `true` if camera supports torch flash mode. Note that camera has to be active for this method to work. If camera is not active, returns `false`.

##### <a name="recognizerView_setTorchState"></a> [`setTorchState(boolean, SuccessCallback)`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#setTorchState-boolean-com.microblink.hardware.SuccessCallback-) 
If torch flash mode is supported on camera, this method can be used to enable/disable torch flash mode. After operation is performed, [SuccessCallback](https://pdf417.github.io/pdf417-android/com/microblink/hardware/SuccessCallback.html) will be called with boolean indicating whether operation has succeeded or not. Note that camera has to be active for this method to work and that callback might be called on background non-UI thread.

##### <a name="recognizerView_setScanningRegion"></a> [`setScanningRegion(Rectangle, boolean)`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#setScanningRegion-com.microblink.geometry.Rectangle-boolean-)
You can use this method to define the scanning region and define whether this scanning region will be rotated with device if [OrientationAllowedListener](https://pdf417.github.io/pdf417-android/com/microblink/view/OrientationAllowedListener.html) determines that orientation is allowed. This is useful if you have your own camera overlay on top of `RecognizerView` that is set as rotatable view - you can thus synchronize the rotation of the view with the rotation of the scanning region native code will scan.

Scanning region is defined as [Rectangle](https://pdf417.github.io/pdf417-android/com/microblink/geometry/Rectangle.html). First parameter of rectangle is x-coordinate represented as percentage of view width, second parameter is y-coordinate represented as percentage of view height, third parameter is region width represented as percentage of view width and fourth parameter is region height represented as percentage of view height.

View width and height are defined in current context, i.e. they depend on screen orientation. If you allow your ROI view to be rotated, then in portrait view width will be smaller than height, whilst in landscape orientation width will be larger than height. This complies with view designer preview. If you choose not to rotate your ROI view, then your ROI view will be laid out either in portrait or landscape, depending on setting for your scan activity in `AndroidManifest.xml`

Note that scanning region only reflects to native code - it does not have any impact on user interface. You are required to create a matching user interface that will visualize the same scanning region you set here.

##### <a name="recognizerView_setMeteringAreas"/></a> [`setMeteringAreas(Rectangle[],boolean)`](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#setMeteringAreas-com.microblink.geometry.Rectangle:A-boolean-)
This method can only be called when camera is active. You can use this method to define regions which camera will use to perform meterings for focus, white balance and exposure corrections. On devices that do not support metering areas, this will be ignored. Some devices support multiple metering areas and some support only one. If device supports only one metering area, only the first rectangle from array will be used.

Each region is defined as [Rectangle](https://pdf417.github.io/pdf417-android/com/microblink/geometry/Rectangle.html). First parameter of rectangle is x-coordinate represented as percentage of view width, second parameter is y-coordinate represented as percentage of view height, third parameter is region width represented as percentage of view width and fourth parameter is region height represented as percentage of view height.

View width and height are defined in current context, i.e. they depend on current device orientation. If you have custom [OrientationAllowedListener](https://pdf417.github.io/pdf417-android/com/microblink/view/OrientationAllowedListener.html), then device orientation will be the last orientation that you have allowed in your listener. If you don't have it set, orientation will be the orientation of activity as defined in `AndroidManifest.xml`. In portrait orientation view width will be smaller than height, whilst in landscape orientation width will be larger than height. This complies with view designer preview.

Second boolean parameter indicates whether or not metering areas should be automatically updated when device orientation changes.

##### <a name="recognizerView_setMetadataListener"></a> [`setMetadadaListener(MetadataListener, MetadataSettings)`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#setMetadataListener-com.microblink.metadata.MetadataListener-com.microblink.metadata.MetadataSettings-)
You can use this method to define [metadata listener](https://pdf417.github.io/pdf417-android/com/microblink/metadata/MetadataListener.html) that will obtain various metadata
from the current recognition process. Which metadata will be available depends on [metadata settings](https://pdf417.github.io/pdf417-android/com/microblink/metadata/MetadataSettings.html). For more information and examples, check demo applications and section [Obtaining various metadata with _MetadataListener_](#metadataListener).

##### <a name="recognizerView_setLicenseKey1"></a> [`setLicenseKey(String licenseKey)`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#setLicenseKey-java.lang.String-)
This method sets the license key that will unlock all features of the native library. You can obtain your license key from [Microblink website](http://microblink.com/login).

##### <a name="recognizerView_setLicenseKey2"></a> [`setLicenseKey(String licenseKey, String licensee)`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#setLicenseKey-java.lang.String-java.lang.String-)
Use this method to set a license key that is bound to a licensee, not the application package name. You will use this method when you obtain a license key that allows you to use _PDF417.mobi_ SDK in multiple applications. You can obtain your license key from [Microblink website](http://microblink.com/login).

# <a name="directAPI"></a> Using direct API for recognition of Android Bitmaps

This section will describe how to use direct API to recognize android Bitmaps without the need for camera. You can use direct API anywhere from your application, not just from activities.

1. First, you need to obtain reference to [Recognizer singleton](https://pdf417.github.io/pdf417-android/com/microblink/directApi/Recognizer.html) using [getSingletonInstance](https://pdf417.github.io/pdf417-android/com/microblink/directApi/Recognizer.html#getSingletonInstance--).
2. Second, you need to [initialize the recognizer](https://pdf417.github.io/pdf417-android/com/microblink/directApi/Recognizer.html#initialize-android.content.Context-com.microblink.recognizers.settings.RecognitionSettings-com.microblink.directApi.DirectApiErrorListener-).
3. After initialization, you can use singleton to [process images](https://pdf417.github.io/pdf417-android/com/microblink/directApi/Recognizer.html#recognizeBitmap-android.graphics.Bitmap-com.microblink.hardware.orientation.Orientation-com.microblink.view.recognition.ScanResultListener-). You cannot process multiple images in parallel.
4. Do not forget to [terminate](https://pdf417.github.io/pdf417-android/com/microblink/directApi/Recognizer.html#terminate--) the recognizer after usage (it is a shared resource).

Here is the minimum example of usage of direct API for recognizing android Bitmap:

```java
public class DirectAPIActivity extends Activity implements ScanResultListener {
	private Recognizer mRecognizer;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// initialize your activity here
	}
	
	@Override
	protected void onStart() {
	   super.onStart();
	   try {
		   mRecognizer = Recognizer.getSingletonInstance();
		} catch (FeatureNotSupportedException e) {
			Toast.makeText(this, "Feature not supported! Reason: " + e.getReason().getDescription(), Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	   try {
	       // set license key
	       mRecognizer.setLicenseKey(this, "your license key");
	   } catch (InvalidLicenceKeyException exc) {
	       finish();
	       return;
	   }
		RecognitionSettings settings = new RecognitionSettings();
		// setupSettingsArray method is described in chapter "Recognition 
		// settings and results")
		settings.setRecognizerSettingsArray(setupSettingsArray());
		mRecognizer.initialize(this, settings, new DirectApiErrorListener() {
			@Override
			public void onRecognizerError(Throwable t) {
				Toast.makeText(DirectAPIActivity.this, "There was an error in initialization of Recognizer: " + t.getMessage(), Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}
	
	@Override
	protected void onResume() {
	   super.onResume();
		// start recognition
		Bitmap bitmap = BitmapFactory.decodeFile("/path/to/some/file.jpg");
		mRecognizer.recognize(bitmap, Orientation.ORIENTATION_LANDSCAPE_RIGHT, this);
	}

	@Override
	protected void onStop() {
	   super.onStop();
	   mRecognizer.terminate();
	}

    @Override
    public void onScanningDone(RecognitionResults results) {
    	// this method is from ScanResultListener and will be called 
    	// when scanning completes
    	// RecognitionResults may contain multiple results in array returned
    	// by method getRecognitionResults().
    	// This depends on settings in RecognitionSettings object that was
    	// given to RecognizerView.
    	// For more information, see chapter "Recognition settings and results")
    	    	
    	finish(); // in this example, just finish the activity
    }
    
}
```

## <a name="directAPIStateMachine"></a> Understanding DirectAPI's state machine

DirectAPI's Recognizer singleton is actually a state machine which can be in one of 4 states: `OFFLINE`, `UNLOCKED`, `READY` and `WORKING`. 

- When you obtain the reference to Recognizer singleton, it will be in `OFFLINE` state. 
- First you need to unlock the Recognizer by providing a valid licence key using [`setLicenseKey`](https://pdf417.github.io/pdf417-android/com/microblink/directApi/Recognizer.html#setLicenseKey-android.content.Context-java.lang.String-) method. If you attempt to call `setLicenseKey` while Recognizer is not in `OFFLINE` state, you will get `IllegalStateException`.
- After successful unlocking, Recognizer singleton will move to `UNLOCKED` state.
- Once in `UNLOCKED` state, you can initialize Recognizer by calling [`initialize`](https://pdf417.github.io/pdf417-android/com/microblink/directApi/Recognizer.html#initialize-android.content.Context-com.microblink.recognizers.settings.RecognitionSettings-com.microblink.directApi.DirectApiErrorListener-) method. If you call `initialize` method while Recognizer is not in `UNLOCKED` state, you will get `IllegalStateException`.
- After successful initialization, Recognizer will move to `READY` state. Now you can call any of the `recognize*` methods.
- When starting recognition with any of the `recognize*` methods, Recognizer will move to `WORKING` state. If you attempt to call these methods while Recognizer is not in `READY` state, you will get `IllegalStateException`
- Recognition is performed on background thread so it is safe to call all Recognizer's method from UI thread
- When recognition is finished, Recognizer first moves back to `READY` state and then returns the result via provided [`ScanResultListener`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/ScanResultListener.html). 
- Please note that `ScanResultListener`'s [`onScanningDone`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/ScanResultListener.html#onScanningDone-com.microblink.recognizers.RecognitionResults-) method will be called on background processing thread, so make sure you do not perform UI operations in this calback.
- By calling [`terminate`](https://pdf417.github.io/pdf417-android/com/microblink/directApi/Recognizer.html#terminate--) method, Recognizer singleton will release all its internal resources and will request processing thread to terminate. Note that even after calling `terminate` you might receive `onScanningDone` event if there was work in progress when `terminate` was called.
- `terminate` method can be called from any Recognizer singleton's state
- You can observe Recognizer singleton's state with method [`getCurrentState`](https://pdf417.github.io/pdf417-android/com/microblink/directApi/Recognizer.html#getCurrentState--)

## <a name="directAPIWithRecognizer"></a> Using DirectAPI while RecognizerView is active
Both [RecognizerView](#recognizerView) and DirectAPI recognizer use the same internal singleton that manages native code. This singleton handles initialization and termination of native library and propagating recognition settings to native library. It is possible to use RecognizerView and DirectAPI together, as internal singleton will make sure correct synchronization and correct recognition settings are used. If you run into problems while using DirectAPI in combination with RecognizerView, [let us know](http://help.microblink.com)!

## <a name="metadataListener"></a> Obtaining various metadata with _MetadataListener_

This section will give an example how to use [Metadata listener](https://pdf417.github.io/pdf417-android/com/microblink/metadata/MetadataListener.html) to obtain various metadata, such as object detection location, images that are being processed and much more. Which metadata will be obtainable is configured with [Metadata settings](https://pdf417.github.io/pdf417-android/com/microblink/metadata/MetadataSettings.html). You must set both _MetadataSettings_ and your implementation of _MetadataListener_ before calling [create](#recognizerView_create) method of [RecognizerView](#recognizerView). Setting them after causes undefined behaviour.

The following code snippet shows how to configure _MetadataSettings_ to obtain detection location, video frame that was used to perform and dewarped image of the document being scanned (**NOTE:** the availability of metadata depends on currently active recognisers and their settings. Not all recognisers can produce all types of metadata. Check [Recognition settings and results](#recognitionSettingsAndResults) article for more information about recognisers and their settings):

```java
// this snippet should be in onCreate method of your scanning activity

MetadataSettings ms = new MetadataSettings();
// enable receiving of detection location
ms.setDetectionMetadataAllowed(true);

// ImageMetadataSettings contains settings for defining which images will be returned
MetadataSettings.ImageMetadataSettings ims = new MetadataSettings.ImageMetadataSettings();
// enable returning of dewarped images, if they are available
ims.setDewarpedImageEnabled(true);
// enable returning of image that was used to obtain valid scanning result
ims.setSuccessfulScanFrameEnabled(true)

// set ImageMetadataSettings to MetadataSettings object
ms.setImageMetadataSettings(ims);

// this line must be called before mRecognizerView.create()
mRecognizerView.setMetadataListener(myMetadataListener, ms);
```

The following snippet shows one possible implementation of _MetadataListener_:

```java
public class MyMetadataListener implements MetadataListener {

	/**
	 * Called when metadata is available.
	 */
    @Override
    public void onMetadataAvailable(Metadata metadata) {
    	// detection location will be available as DetectionMetadata
        if (metadata instanceof DetectionMetadata) {
        	// DetectionMetadata contains DetectorResult which is null if object detection
        	// has failed and non-null otherwise
        	// Let's assume that we have a QuadViewManager which can display animated frame
        	// around detected object (for reference, please check javadoc and demo apps)
            DetectorResult dr = ((DetectionMetadata) metadata).getDetectionResult();
            if (dr == null) {
            	// animate frame to default location if detection has failed
                mQuadViewManager.animateQuadToDefaultPosition();
            } else if (dr instanceof QuadDetectorResult) {
            	// otherwise, animate frame to detected location
                mQuadViewManager.animateQuadToDetectionPosition((QuadDetectorResult) dr);
            }
        // images will be available inside ImageMetadata
        } else if (metadata instanceof ImageMetadata) {
        	// obtain image
        	// Please note that Image's internal buffers are valid only
        	// until this method ends. If you want to save image for later,
        	// obtained a cloned image with image.clone().
            Image image = ((ImageMetadata) metadata).getImage();
            // to convert the image to Bitmap, call image.convertToBitmap()
        }
    }
}
```

Here are javadoc links to all classes that appeared in previous code snippet:

- [Metadata](https://pdf417.github.io/pdf417-android/com/microblink/metadata/Metadata.html)
- [DetectionMetadata](https://pdf417.github.io/pdf417-android/com/microblink/metadata/DetectionMetadata.html)
- [DetectorResult](https://pdf417.github.io/pdf417-android/com/microblink/detectors/DetectorResult.html)
- [QuadViewManager](https://pdf417.github.io/pdf417-android/com/microblink/view/viewfinder/quadview/QuadViewManager.html)
- [QuadDetectorResult](https://pdf417.github.io/pdf417-android/com/microblink/detectors/quad/QuadDetectorResult.html)
- [ImageMetadata](https://pdf417.github.io/pdf417-android/com/microblink/metadata/ImageMetadata.html)
- [Image](https://pdf417.github.io/pdf417-android/com/microblink/image/Image.html)

## <a name="imageListener"></a> Using ImageListener to obtain images that are being processed

There are two ways of obtaining images that are being processed:

- if _Pdf417ScanActivity_ is being used to perform scanning, then you need to implement [ImageListener interface](https://pdf417.github.io/pdf417-android/com/microblink/image/ImageListener.html) and send your implementation via Intent to _Pdf417ScanActivity_. Note that while this seems easier, this actually introduces a large performance penalty because _ImageListener_ will receive all images, including ones you do not actually need. If you need more control over which images will be received and which not, see point below.
- if [RecognizerView](#recognizerView) is directly embedded into your scanning activity, then you should initialise it with [Metadata settings](https://pdf417.github.io/pdf417-android/com/microblink/metadata/MetadataSettings.html) and your implementation of [Metadata listener interface](https://pdf417.github.io/pdf417-android/com/microblink/metadata/MetadataListener.html). The _MetadataSettings_ will define which metadata will be reported to _MetadataListener_. The metadata can contain various data, such as images, object detection location etc. To see documentation and example how to use _MetadataListener_ to obtain images and other metadata, see section [Obtaining various metadata with _MetadataListener_](#metadataListener).

This section will give an example how to implement [ImageListener interface](https://pdf417.github.io/pdf417-android/com/microblink/image/ImageListener.html) that will obtain images that are being processed. `ImageListener` has only one method that needs to be implemented: `onImageAvailable(Image)`. This method is called whenever library has available image for current processing step. [Image](https://pdf417.github.io/pdf417-android/com/microblink/image/Image.html) is class that contains all information about available image, including buffer with image pixels. Image can be in several format and of several types. [ImageFormat](https://pdf417.github.io/pdf417-android/com/microblink/image/ImageFormat.html) defines the pixel format of the image, while [ImageType](https://pdf417.github.io/pdf417-android/com/microblink/image/ImageType.html) defines the type of the image. `ImageListener` interface extends android's [Parcelable interface](https://developer.android.com/reference/android/os/Parcelable.html) so it is possible to send implementations via [intents](https://developer.android.com/reference/android/content/Intent.html).

Here is the example implementation of [ImageListener interface](https://pdf417.github.io/pdf417-android/com/microblink/image/ImageListener.html). This implementation will save all images into folder `myImages` on device's external storage:

```java
public class MyImageListener implements ImageListener {

   /**
    * Called when library has image available.
    */
    @Override
    public void onImageAvailable(Image image) {
        // we will save images to 'myImages' folder on external storage
        // image filenames will be 'imageType - currentTimestamp.jpg'
        String output = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myImages";
        File f = new File(output);
        if(!f.exists()) {
            f.mkdirs();
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String dateString = dateFormat.format(new Date());
        String filename = null;
        switch(image.getImageFormat()) {
            case ALPHA_8: {
                filename = output + "/alpha_8 - " + image.getImageName() + " - " + dateString + ".jpg";
                break;
            }
            case BGRA_8888: {
                filename = output + "/bgra - " + image.getImageName() + " - " + dateString + ".jpg";
                break;
            }
            case YUV_NV21: {
                filename = output + "/yuv - " + image.getImageName()+ " - " + dateString + ".jpg";
                break;
            }
        }
        Bitmap b = image.convertToBitmap();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            boolean success = b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if(!success) {
                Log.e(this, "Failed to compress bitmap!");
                if(fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ignored) {
                    } finally {
                        fos = null;
                    }
                    new File(filename).delete();
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(this, e, "Failed to save image");
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * ImageListener interface extends Parcelable interface, so we also need to implement
     * that interface. The implementation of Parcelable interface is below this line.
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Creator<MyImageListener> CREATOR = new Creator<MyImageListener>() {
        @Override
        public MyImageListener createFromParcel(Parcel source) {
            return new MyImageListener();
        }

        @Override
        public MyImageListener[] newArray(int size) {
            return new MyImageListener[size];
        }
    };
}
```

Note that [ImageListener](https://pdf417.github.io/pdf417-android/com/microblink/image/ImageListener.html) can only be given to _Pdf417ScanActivity_ via Intent, while to [RecognizerView](#recognizerView), you need to give [Metadata listener](https://pdf417.github.io/pdf417-android/com/microblink/metadata/MetadataListener.html) and [Metadata settings](https://pdf417.github.io/pdf417-android/com/microblink/metadata/MetadataSettings.html) that defines which metadata should be obtained. When you give _ImageListener_ to _Pdf417ScanActivity_ via Intent, it internally registers a _MetadataListener_ that enables obtaining of all available image types and invokes _ImageListener_ given via Intent with the result. For more information and examples how to use _MetadataListener_ for obtaining images, refer to demo applications.

# <a name="recognitionSettingsAndResults"></a> Recognition settings and results

This chapter will discuss various recognition settings used to configure different recognizers and scan results generated by them.

## <a name="recognitionSettings"></a> [Recognition settings](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/settings/RecognitionSettings.html)

Recognition settings define what will be scanned and how will the recognition process be performed. Here is the list of methods that are most relevant:

##### [`setAllowMultipleScanResultsOnSingleImage(boolean)`](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/settings/RecognitionSettings.html#setAllowMultipleScanResultsOnSingleImage-boolean-)
Sets whether or not outputting of multiple scan results from same image is allowed. If that is `true`, it is possible to return multiple recognition results produced by different recognizers from same image. However, single recognizer can still produce only a single result from single image. By default, this option is `false`, i.e. the array of `BaseRecognitionResults` will contain at most 1 element. The upside of setting that option to `false` is the speed - if you enable lots of recognizers, as soon as the first recognizer succeeds in scanning, recognition chain will be terminated and other recognizers will not get a chance to analyze the image. The downside is that you are then unable to obtain multiple results from different recognizers from single image.

##### [`setNumMsBeforeTimeout(int)`](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/settings/RecognitionSettings.html#setNumMsBeforeTimeout-int-)
Sets the number of miliseconds _PDF417.mobi_ will attempt to perform the scan it exits with timeout error. On timeout returned array of `BaseRecognitionResults` inside [RecognitionResults](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/RecognitionResults.html) might be null, empty or may contain only elements that are not valid ([`isValid`](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/BaseRecognitionResult.html#isValid--) returns `false`) or are empty ([`isEmpty`](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/BaseRecognitionResult.html#isEmpty--) returns `true`).

##### [`setFrameQualityEstimationMode(FrameQualityEstimationMode)`](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/settings/RecognitionSettings.html#setFrameQualityEstimationMode-com.microblink.recognizers.settings.RecognitionSettings.FrameQualityEstimationMode-)
Sets the mode of the frame quality estimation. Frame quality estimation is the process of estimating the quality of video frame so only best quality frames can be chosen for processing so no time is wasted on processing frames that are of too poor quality to contain any meaningful information. It is **not** used when performing recognition of [Android bitmaps](https://developer.android.com/reference/android/graphics/Bitmap.html) using [Direct API](#directAPI). You can choose 3 different frame quality estimation modes: automatic, always on and always off.

- In **automatic** mode (default), frame quality estimation will be used if device contains multiple processor cores or if on single core device at least one active recognizer requires frame quality estimation.
- In **always on** mode, frame quality estimation will be used always, regardless of device or active recognizers.
- In **always off** mode, frame quality estimation will be always disabled, regardless of device or active recognizers. This is not recommended setting because it can significantly decrease quality of the scanning process.

##### [`setRecognizerSettingsArray(RecognizerSettings[])`](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/settings/RecognitionSettings.html#setRecognizerSettingsArray-com.microblink.recognizers.settings.RecognizerSettings:A-)
Sets the array of [RecognizerSettings](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/settings/RecognizerSettings.html) that will define which recognizers should be activated and how should the be set up. The list of available _RecognizerSettings_ and their specifics are given below.

## <a name="pdf417Recognizer"></a> Scanning PDF417 barcodes

This section discusses the settings for setting up PDF417 recognizer and explains how to obtain results from PDF417 recognizer.

### Setting up PDF417 recognizer

To activate PDF417 recognizer, you need to create a [Pdf417RecognizerSettings](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/blinkbarcode/pdf417/Pdf417RecognizerSettings.html) and add it to `RecognizerSettings` array. You can do this using following code snippet:

```java
private RecognizerSettings[] setupSettingsArray() {
	Pdf417RecognizerSettings sett = new Pdf417RecognizerSettings();
	// disable scanning of white barcodes on black background
	sett.setInverseScanning(false);
	// allow scanning of barcodes that have invalid checksum
	sett.setUncertainScanning(true);
	// disable scanning of barcodes that do not have quiet zone
	// as defined by the standard
	sett.setNullQuietZoneAllowed(false);

	// now add sett to recognizer settings array that is used to configure
	// recognition
	return new RecognizerSettings[] { sett };
}
```

As can be seen from example, you can tweak PDF417 recognition parameters with methods of `Pdf417RecognizerSettings`.

##### `setUncertainScanning(boolean)`
By setting this to `true`, you will enable scanning of non-standard elements, but there is no guarantee that all data will be read. This option is used when multiple rows are missing (e.g. not whole barcode is printed). Default is `false`.

##### `setNullQuietZoneAllowed(boolean)`
By setting this to `true`, you will allow scanning barcodes which don't have quiet zone surrounding it (e.g. text concatenated with barcode). This option can significantly increase recognition time. Default is `false`.

##### `setInverseScanning(boolean)`
By setting this to `true`, you will enable scanning of barcodes with inverse intensity values (i.e. white barcodes on dark background). This option can significantly increase recognition time. Default is `false`.

### Obtaining results from PDF417 recognizer
PDF417 recognizer produces [Pdf417ScanResult](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/blinkbarcode/pdf417/Pdf417ScanResult.html). You can use `instanceof` operator to check if element in results array is instance of `Pdf417ScanResult` class. See the following snippet for an example:

```java
@Override
public void onScanningDone(RecognitionResults results) {
	BaseRecognitionResult[] dataArray = results.getRecognitionResults();
	for(BaseRecognitionResult baseResult : dataArray) {
		if(baseResult instanceof Pdf417ScanResult) {
			Pdf417ScanResult result = (Pdf417ScanResult) baseResult;
			
	        // getStringData getter will return the string version of barcode contents
			String barcodeData = result.getStringData();
			// isUncertain getter will tell you if scanned barcode is uncertain
			boolean uncertainData = result.isUncertain();
			// getRawData getter will return the raw data information object of barcode contents
			BarcodeDetailedData rawData = result.getRawData();
			// BarcodeDetailedData contains information about barcode's binary layout, if you
			// are only interested in raw bytes, you can obtain them with getAllData getter
			byte[] rawDataBuffer = rawData.getAllData();
		}
	}
}
```

As you can see from the example, obtaining data is rather simple. You just need to call several methods of the `Pdf417ScanResult` object:

##### `String getStringData()`
This method will return the string representation of barcode contents. Note that PDF417 barcode can contain binary data so sometimes it makes little sense to obtain only string representation of barcode data.

##### `boolean isUncertain()`
This method will return the boolean indicating if scanned barcode is uncertain. This can return `true` only if scanning of uncertain barcodes is allowed, as explained earlier.

##### `BarcodeDetailedData getRawData()`
This method will return the object that contains information about barcode's binary layout. You can see information about that object in [javadoc](https://pdf417.github.io/pdf417-android/com/microblink/results/barcode/BarcodeDetailedData.html). However, if you only need to access byte array containing, you can call method `getAllData` of `BarcodeDetailedData` object.

##### `Quadrilateral getPositionOnImage()`
Returns the position of barcode on image. Note that returned coordinates are in image's coordinate system which is not related to view coordinate system used for UI.

## <a name="usdl"></a> Scanning US Driver's licence barcodes

This section discusses the settings for setting up USDL recognizer and explains how to obtain results from it.

### Setting up USDL recognizer
To activate USDL recognizer, you need to create [USDLRecognizerSettings](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/blinkbarcode/usdl/USDLRecognizerSettings.html) and add it to `RecognizerSettings` array. You can do this using following code snippet:

```java
private RecognizerSettings[] setupSettingsArray() {
	USDLRecognizerSettings sett = new USDLRecognizerSettings();
	// disallow scanning of barcodes that have invalid checksum
	sett.setUncertainScanning(false);
	// disable scanning of barcodes that do not have quiet zone
	// as defined by the standard
	sett.setNullQuietZoneAllowed(false);
       
	// now add sett to recognizer settings array that is used to configure
	// recognition
	return new RecognizerSettings[] { sett };
}
```

As can be seen from example, you can tweak USDL recognition parameters with methods of `USDLRecognizerSettings`.

##### `setUncertainScanning(boolean)`
By setting this to `true`, you will enable scanning of non-standard elements, but there is no guarantee that all data will be read. This option is used when multiple rows are missing (e.g. not whole barcode is printed). Default is `false`.

##### `setNullQuietZoneAllowed(boolean)`
By setting this to `true`, you will allow scanning barcodes which don't have quiet zone surrounding it (e.g. text concatenated with barcode). This option can significantly increase recognition time. Default is `true`.

##### `setScan1DBarcodes(boolean)`
Some driver's licenses contain 1D Code39 and Code128 barcodes alongside PDF417 barcode. These barcodes usually contain only reduntant information and are therefore not read by default. However, if you feel that some information is missing, you can enable scanning of those barcodes by setting this to `true`.

### Obtaining results from USDL recognizer

USDL recognizer produces [USDLScanResult](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/blinkbarcode/usdl/USDLScanResult.html). You can use `instanceof` operator to check if element in results array is instance of `USDLScanResult`. See the following snippet for an example:

```java
@Override
public void onScanningDone(RecognitionResults results) {
	BaseRecognitionResult[] dataArray = results.getRecognitionResults();
	for(BaseRecognitionResult baseResult : dataArray) {
		if(baseResult instanceof USDLScanResult) {
			USDLScanResult result = (USDLScanResult) baseResult;
			
	        // getStringData getter will return the string version of barcode contents (not parsed)
			String barcodeData = result.getStringData();
			// isUncertain getter will tell you if scanned barcode is uncertain
			boolean uncertainData = result.isUncertain();
			// getRawData getter will return the raw data information object of barcode contents
			BarcodeDetailedData rawData = result.getRawData();
			// BarcodeDetailedData contains information about barcode's binary layout, if you
			// are only interested in raw bytes, you can obtain them with getAllData getter
			byte[] rawDataBuffer = rawData.getAllData();
			
			// if you need specific parsed driver's licence element, you can
			// use getField method
			// for example, to obtain AAMVA version, you should use:
			String aamvaVersion = result.getField(USDLScanResult.kAamvaVersionNumber);
		}
	}
}
```

##### `String getStringData()`
This method will return the string representation of barcode contents (not parsed). Note that PDF417 barcode can contain binary data so sometimes it makes little sense to obtain only string representation of barcode data.

##### `boolean isUncertain()`
This method will return the boolean indicating if scanned barcode is uncertain. This can return `true` only if scanning of uncertain barcodes is allowed, as explained earlier.

##### `BarcodeDetailedData getRawData()`
This method will return the object that contains information about barcode's binary layout. You can see information about that object in [javadoc](https://pdf417.github.io/pdf417-android/com/microblink/results/barcode/BarcodeDetailedData.html). However, if you only need to access byte array containing, you can call method `getAllData` of `BarcodeDetailedData` object.

##### `getField(String)`
This method will return a parsed US Driver's licence element. The method requires a key that defines which element should be returned and returns either a string representation of that element or `null` if that element does not exist in barcode. To see a list of available keys, refer to [Keys for obtaining US Driver's license data](DriversLicenseKeys.md)

## <a name="custom1DBarDecoder"></a> Scanning one dimensional barcodes with _PDF417.mobi_'s implementation

This section discusses the settings for setting up 1D barcode recognizer that uses _PDF417.mobi_'s implementation of scanning algorithms and explains how to obtain results from that recognizer. Henceforth, the 1D barcode recognizer that uses _PDF417.mobi_'s implementation of scanning algorithms will be refered as "Bardecoder recognizer".

### Setting up Bardecoder recognizer

To activate Bardecoder recognizer, you need to create a [BarDecoderRecognizerSettings](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/blinkbarcode/bardecoder/BarDecoderRecognizerSettings.html) and add it to `RecognizerSettings` array. You can do this using following code snippet:

```java
private RecognizerSettings[] setupSettingsArray() {
	BarDecoderRecognizerSettings sett = new BarDecoderRecognizerSettings();
	// activate scanning of Code39 barcodes
	sett.setScanCode39(true);
	// activate scanning of Code128 barcodes
	sett.setScanCode128(true);
	// disable scanning of white barcodes on black background
	sett.setInverseScanning(false);
	// disable slower algorithm for low resolution barcodes
	sett.setTryHarder(false);

	// now add sett to recognizer settings array that is used to configure
	// recognition
	return new RecognizerSettings[] { sett };
}
```

As can be seen from example, you can tweak Bardecoder recognition parameters with methods of `BarDecoderRecognizerSettings`.

##### `setScanCode128(boolean)`
Method activates or deactivates the scanning of Code128 1D barcodes. Default (initial) value is `false`.

##### `setScanCode39(boolean)`
Method activates or deactivates the scanning of Code39 1D barcodes. Default (initial) value is `false`.

##### `setInverseScanning(boolean)`
By setting this to `true`, you will enable scanning of barcodes with inverse intensity values (i.e. white barcodes on dark background). This option can significantly increase recognition time. Default is `false`.

##### `setTryHarder(boolean)`
By setting this to `true`, you will enabled scanning of lower resolution barcodes at cost of additional processing time. This option can significantly increase recognition time. Default is `false`.

### Obtaining results from Bardecoder recognizer

Bardecoder recognizer produces [BarDecoderScanResult](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/barcode/blinkbardecoder/BarDecoderScanResult.html). You can use `instanceof` operator to check if element in results array is instance of `BarDecoderScanResult` class. See the following snippet for example:

```java
@Override
public void onScanningDone(RecognitionResults results) {
	BaseRecognitionResult[] dataArray = results.getRecognitionResults();
	for(BaseRecognitionResult baseResult : dataArray) {
		if(baseResult instanceof BarDecoderScanResult) {
			BarDecoderScanResult result = (BarDecoderScanResult) baseResult;
			
			// getBarcodeType getter will return a BarcodeType enum that will define
			// the type of the barcode scanned
			BarcodeType barType = result.getBarcodeType();
	        // getStringData getter will return the string version of barcode contents
			String barcodeData = result.getStringData();
			// getRawData getter will return the raw data information object of barcode contents
			BarcodeDetailedData rawData = result.getRawData();
			// BarcodeDetailedData contains information about barcode's binary layout, if you
			// are only interested in raw bytes, you can obtain them with getAllData getter
			byte[] rawDataBuffer = rawData.getAllData();
		}
	}
}
```

As you can see from the example, obtaining data is rather simple. You just need to call several methods of the `BarDecoderScanResult` object:

##### `String getStringData()`
This method will return the string representation of barcode contents. 

##### `BarcodeDetailedData getRawData()`
This method will return the object that contains information about barcode's binary layout. You can see information about that object in [javadoc](https://pdf417.github.io/pdf417-android/com/microblink/results/barcode/BarcodeDetailedData.html). However, if you only need to access byte array containing, you can call method `getAllData` of `BarcodeDetailedData` object.

##### `String getExtendedStringData()`
This method will return the string representation of extended barcode contents. This is available only if barcode that supports extended encoding mode was scanned (e.g. code39).

##### `BarcodeDetailedData getExtendedRawData()`
This method will return the object that contains information about barcode's binary layout when decoded in extended mode. You can see information about that object in [javadoc](https://pdf417.github.io/pdf417-android/com/microblink/results/barcode/BarcodeDetailedData.html). However, if you only need to access byte array containing, you can call method `getAllData` of `BarcodeDetailedData` object. This is available only if barcode that supports extended encoding mode was scanned (e.g. code39).

##### `getBarcodeType()`
This method will return a [BarcodeType](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/blinkbarcode/BarcodeType.html) enum that defines the type of barcode scanned.

## <a name="zxing"></a> Scanning barcodes with ZXing implementation

This section discusses the settings for setting up barcode recognizer that use ZXing's implementation of scanning algorithms and explains how to obtain results from it. _PDF417.mobi_ uses ZXing's [c++ port](https://github.com/zxing/zxing/tree/00f634024ceeee591f54e6984ea7dd666fab22ae/cpp) to support barcodes for which we still do not have our own scanning algorithms. Also, since ZXing's c++ port is not maintained anymore, we also provide updates and bugfixes to it inside our codebase.

### Setting up ZXing recognizer

To activate ZXing recognizer, you need to create [ZXingRecognizerSettings](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/blinkbarcode/zxing/ZXingRecognizerSettings.html) and add it to `RecognizerSettings` array. You can do this using the following code snippet:

```java
private RecognizerSettings[] setupSettingsArray() {
	ZXingRecognizerSettings sett=  new ZXingRecognizerSettings();
	// disable scanning of white barcodes on black background
	sett.setInverseScanning(false);
	// activate scanning of QR codes
	sett.setScanQRCode(true);

	// now add sett to recognizer settings array that is used to configure
	// recognition
	return new RecognizerSettings[] { sett };
}
```

As can be seen from example, you can tweak ZXing recognition parameters with methods of `ZXingRecognizerSettings`. Note that some barcodes, such as Code 39 are available for scanning with [_PDF417.mobi_'s implementation](#custom1DBarDecoder). You can choose to use only one implementation or both (just put both settings objects into `RecognizerSettings` array). Using both implementations increases the chance of correct barcode recognition, but requires more processing time. Of course, we recommend using the _PDF417.mobi_'s implementation for supported barcodes.

##### `setScanAztecCode(boolean)`
Method activates or deactivates the scanning of Aztec 2D barcodes. Default (initial) value is `false`.

##### `setScanCode128(boolean)`
Method activates or deactivates the scanning of Code128 1D barcodes. Default (initial) value is `false`.

##### `setScanCode39(boolean)`
Method activates or deactivates the scanning of Code39 1D barcodes. Default (initial) value is `false`.

##### `setScanDataMatrixCode(boolean)`
Method activates or deactivates the scanning of Data Matrix 2D barcodes. Default (initial) value is `false`.

##### `setScanEAN13Code(boolean)`
Method activates or deactivates the scanning of EAN 13 1D barcodes. Default (initial) value is `false`.

##### `setScanEAN8Code(boolean)`
Method activates or deactivates the scanning of EAN 8 1D barcodes. Default (initial) value is `false`.

##### `shouldScanITFCode(boolean)`
Method activates or deactivates the scanning of ITF 1D barcodes. Default (initial) value is `false`.

##### `setScanQRCode(boolean)`
Method activates or deactivates the scanning of QR 2D barcodes. Default (initial) value is `false`.

##### `setScanUPCACode(boolean)`
Method activates or deactivates the scanning of UPC A 1D barcodes. Default (initial) value is `false`.

##### `setScanUPCECode(boolean)`
Method activates or deactivates the scanning of UPC E 1D barcodes. Default (initial) value is `false`.

##### `setInverseScanning(boolean)`
By setting this to `true`, you will enable scanning of barcodes with inverse intensity values (i.e. white barcodes on dark background). This option can significantly increase recognition time. Default is `false`.

##### `setSlowThoroughScan(boolean)`
Use this method to enable slower, but more thorough scan procedure when scanning barcodes. By default, this option is turned on.

### Obtaining results from ZXing recognizer

ZXing recognizer produces [ZXingScanResult](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/blinkbarcode/zxing/ZXingScanResult.html). You can use `instanceof` operator to check if element in results array is instance of `ZXingScanResult` class. See the following snippet for example:

```java
@Override
public void onScanningDone(RecognitionResults results) {
	BaseRecognitionResult[] dataArray = results.getRecognitionResults();
	for(BaseRecognitionResult baseResult : dataArray) {
		if(baseResult instanceof ZXingScanResult) {
			ZXingScanResult result = (ZXingScanResult) baseResult;
			
			// getBarcodeType getter will return a BarcodeType enum that will define
			// the type of the barcode scanned
			BarcodeType barType = result.getBarcodeType();
	        // getStringData getter will return the string version of barcode contents
			String barcodeData = result.getStringData();
		}
	}
}
```

As you can see from the example, obtaining data is rather simple. You just need to call several methods of the `ZXingScanResult` object:

##### `String getStringData()`
This method will return the string representation of barcode contents. 

##### `getBarcodeType()`
This method will return a [BarcodeType](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/blinkbarcode/BarcodeType.html) enum that defines the type of barcode scanned.

# <a name="translation"></a> Translation and localization

`PDF417.mobi` can be localized to any language. If you are using `RecognizerView` in your custom scan activity, you should handle localization as in any other Android app - `RecognizerView` does not use strings nor drawables, it only uses assets from `assets/microblink` folder. Those assets must not be touched as they are required for recognition to work correctly.

However, if you use our builtin `Pdf417ScanActivity` activity, it will use resources packed with library project to display strings and images on top of camera view. We have already prepared string in several languages which you can use out of the box. You can also [modify those strings](#stringChanging), or you can [add your own language](#addLanguage).

To use a language, you have to enable it from the code:

* To enable usage of predefined language you should call method `LanguageUtils.setLanguage(language, context)`. For example, you can set language like this:

	```java
	// define PDF417.mobi language
	LanguageUtils.setLanguage(Language.Croatian, this);
	```
		
* To enable usage of language that is not available in predefined language enum (for example, if you added your own language), you should call method `LanguageUtils.setLanguageAndCountry(language, country, context)`. For example, you can set language like this:
	
	```java
	// define PDF417.mobi language
	LanguageUtils.setLanguageAndCountry("hr", "", this);
	```

### <a name="addLanguage"></a> Adding new language

_PDF417.mobi_ can easily be translated to other languages. The `res` folder in `LibRecognizer.aar` archive has folder `values` which contains `strings.xml` - this file contains english strings. In order to make e.g. croatian translation, create a folder `values-hr` in your project and put the copy of `strings.xml` inside it (you might need to extract `LibRecognizer.aar` archive to get access to those files). Then, open that file and change the english version strings into croatian version. 

### <a name="stringChanging"></a> Changing strings in the existing language
	
To modify an existing string, the best approach would be to:

1. choose a language which you want to modify. For example Croatia ('hr').
2. find strings.xml in `LibRecognizer.aar` archive folder `res/values-hr`
3. choose a string key which you want to change. For example, ```<string name="PhotoPayHelp">Help</string>```
4. in your project create a file `strings.xml` in the folder `res/values-hr`, if it doesn't already exist
5. create an entry in the file with the value for the string which you want. For example ```<string name="PhotoPayHelp">Pomoć</string>```
6. repeat for all the string you wish to change

# <a name="embedAAR"></a> Embedding _PDF417.mobi_ inside another SDK

When creating your own SDK which depends on _PDF417.mobi_, you should consider following cases:

- [_PDF417.mobi_ licensing model](#licensingModel)
- [ensuring final app gets all classes and resources that are required by _PDF417.mobi_](#sdkIntegrationIntoApp)

## <a name="licensingModel"></a> _PDF417.mobi_ licensing model

_PDF417.mobi_ supports two types of licenses: 

- application licenses
- library licenses.

### <a name="appLicence"></a> Application licenses

Application license keys are bound to application's [package name](http://tools.android.com/tech-docs/new-build-system/applicationid-vs-packagename). This means that each app must have its own license key in order to be able to use _PDF417.mobi_. This model is appropriate when integrating _PDF417.mobi_ directly into app, however if you are creating SDK that depends on _PDF417.mobi_, you would need separate _PDF417.mobi_ license key for each of your clients using your SDK. This is not practical, so you should contact us at [help.microblink.com](http://help.microblink.com) and we can provide you a library license key.

### <a name="libLicence"></a> Library licenses

Library license keys are bound to licensee name. You will provide your licensee name with your inquiry for library license key. Unlike application license keys, library license keys must be set together with licensee name:

- when using _Pdf417ScanActivity_, you should provide licensee name with extra `Pdf417ScanActivity.EXTRAS_LICENSEE`, for example:

	```java
	// set the license key
	intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, "Enter_License_Key_Here");
	intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSEE, "Enter_Licensee_Here");
	```
	
- when using [RecognizerView](#recognizerView), you should use [method that accepts both license key and licensee](#recognizerView_setLicenseKey2), for example:

	```java
	mRecognizerView.setLicenseKey("Enter_License_Key_Here", "Enter_Licensee_Here");
	```
	
## <a name="sdkIntegrationIntoApp"></a> Ensuring the final app gets all resources required by _PDF417.mobi_

At the time of writing this documentation, [Android does not have support for combining multiple AAR libraries into single fat AAR](https://stackoverflow.com/questions/20700581/android-studio-how-to-package-single-aar-from-multiple-library-projects/20715155#20715155). The problem is that resource merging is done while building application, not while building AAR, so application must be aware of all its dependencies. **There is no official Android way of "hiding" third party AAR within your AAR.**

This problem is usually solved with transitive Maven dependencies, i.e. when publishing your AAR to Maven you specify dependencies of your AAR so they are automatically referenced by app using your AAR. Besides this, there are also several other approaches you can try:

- you can ask your clients to reference _PDF417.mobi_ in their app when integrating your SDK
- since the problem lies in resource merging part you can try avoiding this step by ensuring your library will not use any component from _PDF417.mobi_ that uses resources (i.e. _Pdf417ScanActivity_). You can perform [custom UI integration](#recognizerView) while taking care that all resources (strings, layouts, images, ...) used are solely from your AAR, not from _PDF417.mobi_. Then, in your AAR you should not reference `LibRecognizer.aar` as gradle dependency, instead you should unzip it and copy its assets to your AAR’s assets folder, its classes.jar to your AAR’s lib folder (which should be referenced by gradle as jar dependency) and contents of its jni folder to your AAR’s src/main/jniLibs folder.
- Another approach is to use [3rd party unofficial gradle script](https://github.com/adwiv/android-fat-aar) that aim to combine multiple AARs into single fat AAR. Use this script at your own risk.

# <a name="archConsider"></a> Processor architecture considerations

_PDF417.mobi_ is distributed with native library binaries for all processor architectures supported by Android.

ARMv7 architecture gives the ability to take advantage of hardware accelerated floating point operations and SIMD processing with [NEON](http://www.arm.com/products/processors/technologies/neon.php). This gives _PDF417.mobi_ a huge performance boost on devices that have ARMv7 processors. Most new devices (all since 2012.) have ARMv7 processor so it makes little sense not to take advantage of performance boosts that those processors can give. 

ARM64 is the new processor architecture that some new high end devices use. ARM64 processors are very powerful and also have the possibility to take advantage of new NEON64 SIMD instruction set to quickly process multiple pixels with single instruction.

x86 architecture gives the ability to obtain native speed on x86 android devices, like [Prestigio 5430](http://www.gsmarena.com/prestigio_multiphone_5430_duo-5721.php). Without that, _PDF417.mobi_ will not work on such devices, or it will be run on top of ARM emulator that is shipped with device - this will give a huge performance penalty.

x86_64 architecture gives better performance than x86 on devices that use 64-bit Intel Atom processor.

Mips and Mips64 architectures are used for devices that use mips-compatible processor.

However, there are some issues to be considered:

- ARMv7 processors understand ARMv6 instruction set, but ARMv6 processors do not understand ARMv7 instructions.
- if ARMv7 processor executes ARMv6 code, it does not take advantage of hardware floating point acceleration and does not use SIMD operations
- ARMv7 build of native library cannot be run on devices that do not have ARMv7 compatible processor (list of those old devices can be found [here](http://www.getawesomeinstantly.com/list-of-armv5-armv6-and-armv5-devices/))
- neither ARMv6 nor ARMv7 processors understand x86 instruction set
- x86 processors do not understand neither ARMv6 nor ARMv7 instruction sets
- however, some x86 android devices ship with the builtin [ARM emulator](http://commonsware.com/blog/2013/11/21/libhoudini-what-it-means-for-developers.html) - such devices are able to run ARM binaries (both ARMv6 and ARMv7) but with performance penalty. There is also a risk that builtin ARM emulator will not understand some specific ARM instruction and will crash.
- ARM64 processors understand both ARMv6 and ARMv7 instruction sets, but neither ARMv6 nor ARMv7 processors do not understand ARM64 instructions
- if ARM64 processor executes ARMv6 code, it does not take advantage of hardware floating point acceleration and does not use SIMD operations
- if ARM64 processor executes ARMv7 code, it does not take advantage of modern NEON64 SIMD operations and does not take advantage of 64-bit registers it has - it runs in emulation mode
- x86_64 processors understand x86 instruction set, but x86 processors do not understand x86_64 instruction set
- if x86_64 processor executes x86 code, it does not take advantage of 64-bit registers and use two instructions instead of one for 64-bit operations
- MIPS processors understand only MIPS instruction set, while MIPS64 processors understand both MIPS and MIPS64 instruction sets

`LibRecognizer.aar` archive contains builds of native library for all available architectures. By default, when you integrate _PDF417.mobi_ into your app, your app will contain native builds for all processor architectures. Thus, _PDF417.mobi_ will work on all devices and will use specific processor features where it can, e.g. ARMv7 features on ARMv7 devices and ARM64 features on ARM64 devices. However, the size of your application will be rather large.

## <a name="reduceSize"></a> Reducing the final size of your app

If your final app is too large because of _PDF417.mobi_, you can decide to create multiple flavors of your app - one flavor for each architecture. With gradle and Android studio this is very easy - just add the following code to `build.gradle` file of your app:

```
android {
  ...
  splits {
    abi {
      enable true
      reset()
      include 'x86', 'armeabi-v7a', 'armeabi', 'arm64-v8a', 'mips', 'mips64', 'x86_64'
      universalApk true
    }
  }
}
```

With that build instructions, gradle will build four different APK files for your app. Each APK will contain only native library for one processor architecture and one APK will contain all architectures. In order for Google Play to accept multiple APKs of the same app, you need to ensure that each APK has different version code. This can easily be done by defining a version code prefix that is dependent on architecture and adding real version code number to it in following gradle script:

```
// map for the version code
def abiVersionCodes = ['armeabi':1, 'armeabi-v7a':2, 'arm64-v8a':3, 'mips':4, 'mips64':5, 'x86':6, 'x86_64':7]

import com.android.build.OutputFile

android.applicationVariants.all { variant ->
    // assign different version code for each output
    variant.outputs.each { output ->
        def filter = output.getFilter(OutputFile.ABI)
        if(filter != null) {
            output.versionCodeOverride = abiVersionCodes.get(output.getFilter(OutputFile.ABI)) * 1000000 + android.defaultConfig.versionCode
        }
    }
}
```

For more information about creating APK splits with gradle, check [this article from Google](https://sites.google.com/a/android.com/tools/tech-docs/new-build-system/user-guide/apk-splits#TOC-ABIs-Splits).

After generating multiple APK's, you need to upload them to Google Play. For tutorial and rules about uploading multiple APK's to Google Play, please read the [official Google article about multiple APKs](https://developer.android.com/google/play/publishing/multiple-apks.html).

However, if you are using Eclipse, things get really complicated. Eclipse does not support build flavors and you will either need to remove support for some processors or create three different library projects from `LibRecognizer.aar` - each one for specific processor architecture. In the next section, we will discuss how to remove processor architecture support from Eclipse library project.

### Removing processor architecture support in Eclipse

This section assumes that you have set up and prepared your Eclipse project from `LibRecognizer.aar` as described in chapter [Eclipse integration instructions](#eclipseIntegration).

Native libraryies in eclipse library project are located in subfolder `libs`:

- `libs/armeabi` contains native libraries for ARMv6 processor architecture
- `libs/armeabi-v7a` contains native libraries for ARMv7 processor arhitecture
- `libs/x86` contains native libraries for x86 processor architecture
- `libs/arm64-v8a` contains native libraries for ARM64 processor architecture
- `libs/x86_64` contains native libraries for x86_64 processor architecture
- `libs/mips` contains native libraries for MIPS processor architecture
- `libs/mips64` contains native libraries for MIPS64 processor architecture

To remove a support for processor architecture, you should simply delete appropriate folder inside Eclipse library project:

- to remove ARMv6 support, delete folder `libs/armeabi`
- to remove ARMv7 support, delete folder `libs/armeabi-v7a`
- to remove x86 support, delete folder `libs/x86`
- to remove ARM64 support, delete folder `libs/arm64-v8a`
- to remove x86_64 support, delete folder `libs/x86_64`
- to remove MIPS support, delete folder `libs/mips`
- to remove MIPS64 support, delete folder `libs/mips64`

### Consequences of removing processor architecture

However, removing a processor architecture has some consequences:

- by removing ARMv6 support _PDF417.mobi_ will not work on devices that have ARMv6 processors. 
- by removing ARMv7 support, _PDF417.mobi_ will work on both devices that have ARMv6, ARM64 or ARMv7 processor. However, on ARMv7 and ARM64 processors, hardware floating point and SIMD acceleration will not be used, thus making _PDF417.mobi_ much slower. Our internal tests have shown that running ARMv7 version of _PDF417.mobi_ on ARMv7 device is more than 50% faster than running ARMv6 version on same device.
- by removing ARM64 support, _PDF417.mobi_ will not use ARM64 features on ARM64 device
- by removing x86 support, _PDF417.mobi_ will not work on devices that have x86 processor, except in situations when devices have ARM emulator - in that case, _PDF417.mobi_ will work, but will be slow
- by removing x86_64 support, _PDF417.mobi_ will not use 64-bit optimizations on x86_64 processor, but if x86 support is not removed, _PDF417.mobi_ should work
- by removing MIPS support, _PDF417.mobi_ will not work on MIPS processors
- by removing MIPS64 support, _PDF417.mobi_ will not utilize MIPS64 optimizations on MIPS64 processor, but if MIPS support is not removed, _PDF417.mobi_ should work

Our recommendation is to include all architectures into your app - it will work on all devices and will provide best user experience. However, if you really need to reduce the size of your app, we recommend releasing separate version of your app for each processor architecture.

## <a name="combineNativeLibraries"></a> Combining _PDF417.mobi_ with other native libraries

If you are combining _PDF417.mobi_ library with some other libraries that contain native code into your application, make sure you match the architectures of all native libraries. For example, if third party library has got only ARMv6 and x86 versions, you must use exactly ARMv6 and x86 versions of _PDF417.mobi_ with that library, but not ARMv7, ARM64 or some else. Using these architectures will crash your app in initialization step because JVM will try to load all its native dependencies in same preferred architecture - for example if device preferres ARMv7 native libraries so it will see that there is a _PDF417.mobi_ ARMv7 native library and will load it. After that, it will try to load ARMv7 version of your third party library which does not exist - therefore app will crash with `UnsatisfiedLinkError`.

# <a name="troubleshoot"></a> Troubleshooting

## <a name="integrationTroubleshoot"></a> Integration problems

In case of problems with integration of the SDK, first make sure that you have tried integrating it into Android Studio by following [integration instructions](#quickIntegration). Althought we do provide [Eclipse ADT integration](#eclipseIntegration) integration instructions, we officialy do not support Eclipse ADT anymore. Also, for any other IDEs unfortunately you are on your own.

If you have followed [Android Studio integration instructions](#quickIntegration) and are still having integration problems, please contact us at [help.microblink.com](http://help.microblink.com).

## <a name="sdkTroubleshoot"></a> SDK problems

In case of problems with using the SDK, you should do as follows:

### Licencing problems

If you are getting "invalid licence key" error or having other licence-related problems (e.g. some feature is not enabled that should be or there is a watermark on top of camera), first check the ADB logcat. All licence-related problems are logged to error log so it is easy to determine what went wrong.

When you have determine what is the licence-relate problem or you simply do not understand the log, you should contact us [help.microblink.com](http://help.microblink.com). When contacting us, please make sure you provide following information:

* exact package name of your app (from your `AndroidManifest.xml` and/or your `build.gradle` file)
* licence key that is causing problems
* please stress out that you are reporting problem related to Android version of _PDF417.mobi_ SDK
* if unsure about the problem, you should also provide excerpt from ADB logcat containing licence error

### Other problems

If you are having problems with scanning certain items, undesired behaviour on specific device(s), crashes inside _PDF417.mobi_ or anything unmentioned, please do as follows:

* enable logging to get the ability to see what is library doing. To enable logging, put this line in your application:

	```java
	com.microblink.util.Log.setLogLevel(com.microblink.util.Log.LogLevel.LOG_VERBOSE);
	```

	After this line, library will display as much information about its work as possible. Please save the entire log of scanning session to a file that you will send to us. It is important to send the entire log, not just the part where crash occured, because crashes are sometimes caused by unexpected behaviour in the early stage of the library initialization.
	
* Contact us at [help.microblink.com](http://help.microblink.com) describing your problem and provide following information:
	* log file obtained in previous step
	* high resolution scan/photo of the item that you are trying to scan
	* information about device that you are using - we need exact model name of the device. You can obtain that information with [this app](https://play.google.com/store/apps/details?id=com.jphilli85.deviceinfo&hl=en)
	* please stress out that you are reporting problem related to Android version of _PDF417.mobi_ SDK


# <a name="info"></a> Additional info
Complete API reference can be found in [Javadoc](https://pdf417.github.io/pdf417-android/index.html). 

For any other questions, feel free to contact us at [help.microblink.com](http://help.microblink.com).


