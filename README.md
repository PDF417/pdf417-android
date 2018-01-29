# _PDF417.mobi_ SDK for Android

[![Build Status](https://travis-ci.org/PDF417/pdf417-android.svg?branch=master)](https://travis-ci.org/PDF417/pdf417-android)

_PDF417.mobi_ SDK for Android enables you to perform scans of various barcodes in your app. You can integrate the SDK into your app simply by following the instructions below and your app will be able to scan and process data from the following barcode standards:

* [PDF417 barcode](https://en.wikipedia.org/wiki/PDF417)
* [QR code](https://en.wikipedia.org/wiki/QR_code)
* [Code 128](https://en.wikipedia.org/wiki/Code_128)
* [Code 39](https://en.wikipedia.org/wiki/Code_39)
* [EAN 13](https://en.wikipedia.org/wiki/International_Article_Number_(EAN))
* [EAN 8](https://en.wikipedia.org/wiki/EAN-8)
* [UPC A](https://en.wikipedia.org/wiki/Universal_Product_Code)
* [UPC E](https://en.wikipedia.org/wiki/Universal_Product_Code)
* [ITF](https://en.wikipedia.org/wiki/Interleaved_2_of_5)
* [Data Matrix](https://en.wikipedia.org/wiki/Data_Matrix)
* [Aztec](https://en.wikipedia.org/wiki/Aztec_Code)

Using _PDF417.mobi_ in your app requires a valid license. You can obtain a trial license by registering to [Microblink dashboard](https://microblink.com/login). After registering, you will be able to generate a license for your app. License is bound to [package name](http://tools.android.com/tech-docs/new-build-system/applicationid-vs-packagename) of your app, so please make sure you enter the correct package name when asked.

For more information on how to integrate _PDF417.mobi_ SDK into your app read the instructions below. Make sure you read the latest [Release notes](Release\ notes.md) for most recent changes and improvements.

# Table of contents

* [Android _PDF417.mobi_ integration instructions](#intro)
* [Quick Start](#quickStart)
    * [Quick start with demo app](#quickDemo)
    * [Integrating _PDF417.mobi_ into your project using Maven](#mavenIntegration)
    * [Android studio integration instructions](#quickIntegration)
        * [Import Javadoc to Android Studio](#androidStudio_importAAR_javadoc)
    * [Eclipse integration instructions](#eclipseIntegration)
    * [Performing your first scan](#quickScan)
* [Advanced _PDF417.mobi_ integration instructions](#advancedIntegration)
    * [Checking if _PDF417.mobi_ is supported](#supportCheck)
    * [UI customizations of built-in activities and fragments](#uiCustomizations)
        * [Using built-in scan activity for performing the scan](#runBuiltinActivity)
        * [Using `RecognizerRunnerFragment` within your activity](#recognizerRunnerFragment)
        * [Built-in activities and overlays](#builtInUIComponents)
        * [Changing appearance of built-in activities and scanning overlays](#changeBuiltInUIComponents)
        * [Translation and localization](#translation)
    * [Embedding `RecognizerRunnerView` into custom scan activity](#recognizerRunnerView)
        * [Scan activity's orientation](#scanOrientation)
    * [Using Direct API for recognition of Android Bitmaps and custom camera frames](#directAPI)
        * [Understanding DirectAPI's state machine](#directAPIStateMachine)
        * [Using DirectAPI while RecognizerRunnerView is active](#directAPIWithRecognizer)
    * [Handling processing events with `RecognizerRunner` and `RecognizerRunnerView`](#processingEvents)
        * [Note about `setMetadataCallbacks` method](#processingEventsImportantNote)
* [`RecognizerBundle` and available recognizers](#availableRecognizers)
    * [The `Recognizer` concept](#recognizerConcept)
    * [`RecognizerBundle`](#recognizerBundle)
        * [Passing `Recognizer` objects between activities](#intentOptimization)
    * [List of available recognizers](#recognizerList)
        * [Frame Grabber Recognizer](#frameGrabberRecognizer)
        * [Success Frame Grabber Recognizer](#successFrameGrabberRecognizer)
        * [PDF417 recognizer](#pdf417Recognizer)
        * [Barcode recognizer](#barcodeRecognizer)
* [Embedding _PDF417.mobi_ inside another SDK](#embedAAR)
    * [_PDF417.mobi_ licensing model](#licensingModel)
        * [Application licenses](#appLicence)
        * [Library licenses](#libLicence)
    * [Ensuring the final app gets all resources required by _PDF417.mobi_](#sdkIntegrationIntoApp)
* [Processor architecture considerations](#archConsider)
    * [Reducing the final size of your app](#reduceSize)
        * [Consequences of removing processor architecture](#archConsequences)
    * [Combining _PDF417.mobi_ with other native libraries](#combineNativeLibraries)
* [Troubleshooting](#troubleshoot)
    * [Integration problems](#integrationTroubleshoot)
    * [SDK problems](#sdkTroubleshoot)
    * [Frequently asked questions and known problems](#faq)
* [Additional info](#info)

# <a name="intro"></a> Android _PDF417.mobi_ integration instructions

The package contains Android Archive (AAR) that contains everything you need to use the _PDF417.mobi_ library. Besides AAR, package also contains a demo project that contains following modules:

- _Pdf417MobiSample_ shows how to use simple Intent-based API to scan a single barcode.
- _Pdf417MobiCustomUISample_ demonstrates advanced SDK integration within a custom scan activity and shows how `RecognizerRunnerFragment` can be used to embed default UI into your activity.
- _Pdf417MobiDirectAPISample_ demonstrates how to perform scanning of [Android Bitmaps](https://developer.android.com/reference/android/graphics/Bitmap.html)
 
Source code of all demo apps is given to you to show you how to perform integration of _PDF417.mobi_ SDK into your app. This source code and all of the resources are at your disposal. You can use these demo apps as a basis for creating your own app, or you can copy/paste code and/or resources from demo apps into your app and use them as you wish without even asking us for permission.

_PDF417.mobi_ is supported in Android SDK version 16 (Android 4.1) or later.

The library contains one activity: `BarcodeScanActivity`. It is responsible for camera control and recognition. You can also create your own scanning UI - you just need to embed `RecognizerRunnerView` into your activity and pass activity's lifecycle events to it and it will control the camera and recognition process. For more information, see [Embedding `RecognizerRunnerView` into custom scan activity](#recognizerRunnerView).

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
    implementation('com.microblink:pdf417.mobi:7.0.0@aar') {
    	transitive = true
    }
}
```

#### Import Javadoc to Android Studio

Android studio 3.0 should automatically import javadoc from maven dependency. If that doesn't happen, you can do that manually by following these steps:

1. In Android Studio project sidebar, ensure [project view is enabled](https://developer.android.com/sdk/installing/studio-androidview.html)
2. Expand `External Libraries` entry (usually this is the last entry in project view)
3. Locate `pdf417.mobi-7.0.0` entry, right click on it and select `Library Properties...`
4. A `Library Properties` pop-up window will appear
5. Click the second `+` button in bottom left corner of the window (the one that contains `+` with little globe)
6. Window for definining documentation URL will appear
7. Enter following address: `https://pdf417.github.io/pdf417-android/`
8. Click `OK`

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
		  <version>7.0.0</version>
		  <type>aar</type>
  	</dependency>
</dependencies>
```

## <a name="quickIntegration"></a> Android studio integration instructions

1. In Android Studio menu, click _File_, select _New_ and then select _Module_.
2. In new window, select _Import .JAR or .AAR Package_, and click _Next_.
3. In _File name_ field, enter the path to _LibPdf417Mobi.aar_ and click _Finish_.
4. In your app's `build.gradle`, add dependency to `LibPdf417Mobi` and appcompat-v7:

    ```
    dependencies {
        implementation project(':LibPdf417Mobi')
        implementation "com.android.support:appcompat-v7:27.0.2"
    }
    ```
	
### <a name="androidStudio_importAAR_javadoc"></a> Import Javadoc to Android Studio

1. In Android Studio project sidebar, ensure [project view is enabled](https://developer.android.com/sdk/installing/studio-androidview.html)
2. Expand `External Libraries` entry (usually this is the last entry in project view)
3. Locate `LibPdf417Mobi-unspecified` entry, right click on it and select `Library Properties...`
4. A `Library Properties` pop-up window will appear
5. Click the `+` button in bottom left corner of the window
6. Window for choosing JAR file will appear
7. Find and select `LibPdf417Mobi-javadoc.jar` file which is located in root folder of the SDK distribution
8. Click `OK`
	
## <a name="eclipseIntegration"></a> Eclipse integration instructions

We do not provide Eclipse integration demo apps. We encourage you to use Android Studio. We also do not test integrating _PDF417.mobi_ with Eclipse. If you are having problems with _PDF417.mobi_, make sure you have tried integrating it with Android Studio prior to contacting us.

However, if you still want to use Eclipse, you will need to convert AAR archive to Eclipse library project format. You can do this by doing the following:

1. In Eclipse, create a new _Android library project_ in your workspace.
2. Clear the `src` and `res` folders.
3. Unzip the `LibPdf417Mobi.aar` file. You can rename it to zip and then unzip it using any tool.
4. Copy the `classes.jar` to `libs` folder of your Eclipse library project. If `libs` folder does not exist, create it.
5. Copy the contents of `jni` folder to `libs` folder of your Eclipse library project.
6. Replace the `res` folder on library project with the `res` folder of the `LibPdf417Mobi.aar` file.

You’ve already created the project that contains almost everything you need. Now let’s see how to configure your project to reference this library project.

1. In the project you want to use the library (henceforth, "target project") add the library project as a dependency
2. Open the `AndroidManifest.xml` file inside `LibPdf417Mobi.aar` file and make sure to copy all permissions, features and activities to the `AndroidManifest.xml` file of the target project.
3. Copy the contents of `assets` folder from `LibPdf417Mobi.aar` into `assets` folder of target project. If `assets` folder in target project does not exist, create it.
4. Clean and Rebuild your target project
5. Add appcompat-v7 library to your workspace and reference it by target project (modern ADT plugin for Eclipse does this automatically for all new android projects).

## <a name="quickScan"></a> Performing your first scan
1. Before starting a recognition process, you need to obtain a license from [Microblink dashboard](https://microblink.com/login). After registering, you will be able to generate a trial license for your app. License is bound to [package name](http://tools.android.com/tech-docs/new-build-system/applicationid-vs-packagename) of your app, so please make sure you enter the correct package name when asked. 

    After creating a license, you will have the option to download the license as a file that you must place within your application's _assets_ folder. You must ensure that license key is set before instantiating any other classes from the SDK, otherwise you will get an exception at runtime. Therefore, we recommend that you extend [Android Application class](https://developer.android.com/reference/android/app/Application.html) and set the license in its [onCreate callback](https://developer.android.com/reference/android/app/Application.html#onCreate()) in the following way:

    ```java
    public class MyApplication extends Application {
        @Override
        public void onCreate() {
            MicroblinkSDK.setLicenseFile("path/to/license/file/within/assets/dir", this);
        }
    }
    ```

2. In your main activity, create recognizer objects that will perform image recognition, configure them and store them into [RecognizerBundle object](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/RecognizerBundle.html). You can see more information about available recognizers and about `RecognizerBundle` in chapter [RecognizerBundle and available recognizers](#availableRecognizers). For example, to scan PDF417 2D barcode, you can configure your recognizer object in the following way:

    ```java
    public class MyActivity extends Activity {
        private Pdf417Recognizer mRecognizer;
        private RecognizerBundle mRecognizerBundle;
        
        @Override
        protected void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            
            // setup views, as you would normally do in onCreate callback
            
            // create Pdf417Recognizer
            mRecognizer = new Pdf417Recognizer();
            
            // bundle recognizers into RecognizerBundle
            mRecognizerBundle = new RecognizerBundle(mRecognizer);
        }
    }
    ```

3. You can start recognition process by starting `BarcodeScanActivity` activity by creating `BarcodeUISettings` and calling [`ActivityRunner.startActivityForResult`](https://pdf417.github.io/pdf417-android/com/microblink/uisettings/ActivityRunner.html#startActivityForResult-android.app.Activity-int-com.microblink.uisettings.UISettings-) method:
	
	```java
	// method within MyActivity from previous step
	public void startScanning() {
        // Settings for BarcodeScanActivity Activity
        BarcodeUISettings settings = new BarcodeUISettings(mRecognizerBundle);
        
        // tweak settings as you wish
        
        // Start activity
        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, settings);
	}
	```
	
4. After `BarcodeScanActivity` activity finishes the scan, it will return to the calling activity or fragment and will call its method `onActivityResult`. You can obtain the scanning results in that method.

	```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == BarcodeScanActivity.RESULT_OK && data != null) {
                // load the data into all recognizers bundled within your RecognizerBundle
                
                mRecognizerBundle.loadFromIntent(data);
                
                // now every recognizer object that was bundled within RecognizerBundle
                // has been updated with results obtained during scanning session
                
                // you can get the result by invoking getResult on recognizer
                Pdf417Recognizer.Result result = mRecognizer.getResult();
                if (result.getResultState() == Recognizer.Result.State.Valid) {
                    // result is valid, you can use it however you wish
                }
            }
        }
    }
	```
	
	For more information about available recognizers and `RecognizerBundle`, see [RecognizerBundle and available recognizers](#availableRecognizers).

# <a name="advancedIntegration"></a> Advanced _PDF417.mobi_ integration instructions
This section covers more advanced details of _PDF417.mobi_ integration.

1. [First part](#supportCheck) will discuss the methods for checking whether _PDF417.mobi_ is supported on current device. 
2. [Second part](#uiCustomizations) will cover the possible customizations when using UI provided by the SDK.
3. [Third part](#recognizerRunnerView) will describe how to embed `RecognizerRunnerView` into your activity with the goal of creating a custom UI for scanning, while still using camera management capabilites of the SDK.
4. [Fourth part](#directAPI) will describe how to use the `RecognizerRunner` singleton (Direct API) for recognition directly from android bitmaps without the need of camera or to recognize camera frames that are obtained by custom camera management.
5. [Fifth part](#processingEvents) will describe how to subscribe to and handle processing events when using either `RecognizerRunnerView` or `RecognizerRunner`.


## <a name="supportCheck"></a> Checking if _PDF417.mobi_ is supported

### _PDF417.mobi_ requirements
Even before settings the license key, you should check if _PDF417.mobi_ is supported on current device. This is required because the _PDF417.mobi_ is a native library that needs to be loaded by the JVM and it is possible that it doesn't support CPU architecture of the current device. Attempt of calling any methods from the SDK that rely on native code, such as license check, on a device with unsupported CPU architecture will cause a crash of your app.

_PDF417.mobi_ requires Android 4.1 as the minimum android version. For best performance and compatibility, we recommend Android 5.0 or newer.

Camera video preview resolution also matters. In order to perform successful scans, camera preview resolution cannot be too low. Minimum camera preview resolution in order to perform a scan is 480p. It must be noted that camera preview resolution is not the same as the video record resolution, although on most devices those are the same. However, there are some devices that allow recording of HD video (720p resolution), but do not allow high enough camera preview resolution (for example, [Sony Xperia Go](http://www.gsmarena.com/sony_xperia_go-4782.php) supports video record resolution at 720p, but camera preview resolution is only 320p - _PDF417.mobi_ does not work on that device).

_PDF417.mobi_ is native application, written in C++ and available for multiple platforms. Because of this, _PDF417.mobi_ cannot work on devices that have obscure hardware architectures. We have compiled _PDF417.mobi_ native code only for most popular Android [ABIs](https://en.wikipedia.org/wiki/Application_binary_interface). See [Processor architecture considerations](#archConsider) for more information about native libraries in _PDF417.mobi_ and instructions how to disable certain architectures in order to reduce the size of final app.

### Checking for _PDF417.mobi_ support in your app
To check whether the _PDF417.mobi_ is supported on the device, you can do it in the following way:
	
```java
// check if PDF417.mobi is supported on the device
RecognizerCompatibilityStatus status = RecognizerCompatibility.getRecognizerCompatibilityStatus(this);
if (status == RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED) {
    Toast.makeText(this, "PDF417.mobi is supported!", Toast.LENGTH_LONG).show();
} else if (status == RecognizerCompatibilityStatus.NO_CAMERA) {
    Toast.makeText(this, "PDF417.mobi is supported only via Direct API!", Toast.LENGTH_LONG).show();
} else {
	Toast.makeText(this, "PDF417.mobi is not supported! Reason: " + status.name(), Toast.LENGTH_LONG).show();
}
```

However, some recognizers require camera with autofocus. If you try to start recognition with those recognizers on a device that does not have a camera with autofocus, you will get an error. To prevent that, you can check whether certain recognizer requires autofocus by calling its [requiresAutofocus](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/Recognizer.html#requiresAutofocus--) method.

If you already have an array of recognizers, you can easily filter out all recognizers that require autofocus from array using the following code snippet:

```java
Recognizer[] recArray = ...;
if(!RecognizerCompatibility.cameraHasAutofocus(CameraType.CAMERA_BACKFACE, this)) {
	recArray = RecognizerUtils.filterOutRecognizersThatRequireAutofocus(recArray);
}
```

This utility method basically iterates over the given array of recognizers and throws out each recognizer that returns `true` from its [requiresAutofocus](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/Recognizer.html#requiresAutofocus--) method.
## <a name="uiCustomizations"></a> UI customizations of built-in activities and fragments

This section will discuss supported appearance and behaviour customizations of built-in activities and will show how to use [`RecognizerRunnerFragment`](https://pdf417.github.io/pdf417-android/com/microblink/fragment/RecognizerRunnerFragment.html) with provided built-in [scanning overlays](https://pdf417.github.io/pdf417-android/com/microblink/fragment/overlay/ScanningOverlay.html) to get the built-in UI experience within any part of your app.

### <a name="runBuiltinActivity"></a> Using built-in scan activity for performing the scan

As shown in [first scan example](#quickScan), you need to create a settings object that is associated with the activity you wish to use. Attempt to start built-in activity directly via custom-crafted `Intent` will result with either crashing the app or with undefined behaviour of the scanning procedure.

List of available built-in scan activities in _PDF417.mobi_ are listed in section [Built-in activities and fragments](#builtInUIComponents).

### <a name="recognizerRunnerFragment"></a> Using `RecognizerRunnerFragment` within your activity

If you want to integrate UI provided by our built-in activity somewhere within your activity, you can do so by using [`RecognizerRunnerFragment`](https://pdf417.github.io/pdf417-android/com/microblink/fragment/RecognizerRunnerFragment.html). Any activity that will host the `RecognizerRunnerFragment` must implement [`ScanningOverlayBinder`](https://pdf417.github.io/pdf417-android/com/microblink/fragment/RecognizerRunnerFragment.ScanningOverlayBinder.html) interface. Attempt of adding `RecognizerRunnerFragment` to activity that does not implement the aforementioned interface will result in a `ClassCastException`. This design is in accordance with the [recommendation for communication between fragments](https://developer.android.com/training/basics/fragments/communicating.html).

The `ScanningOverlayBinder` is responsible for returning `non-null` implementation of [`ScanningOverlay`](https://pdf417.github.io/pdf417-android/com/microblink/fragment/overlay/ScanningOverlay.html) - class that will manage UI on top of `RecognizerRunnerFragment`. It is not recommended to create your own implementation of `ScanningOverlay` as effort to do so might be equal or even greater to creating your custom UI implementation [in the recommended way](#recognizerRunnerView). 

Here is the minimum example for activity that hosts the `RecognizerRunnerFragment`:

```java
public class MyActivity extends Activity implements RecognizerRunnerFragment.ScanningOverlayBinder, ScanResultListener {
    private Pdf417Recognizer mRecognizer;
    private RecognizerBundle mRecognizerBundle;
    private BarcodeOverlayController mScanOverlay = createOverlay();
    private RecognizerRunnerFragment mRecognizerRunnerFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
        setContentView(R.layout.activity_my_activity);
        
        if (null == savedInstanceState) {
            // create fragment transaction to replace R.id.recognizer_runner_view_container with RecognizerRunnerFragment
            mRecognizerRunnerFragment = new RecognizerRunnerFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.recognizer_runner_view_container, mRecognizerRunnerFragment);
            fragmentTransaction.commit();
        } else {
            // obtain reference to fragment restored by Android within super.onCreate() call
            mRecognizerRunnerFragment = (RecognizerRunnerFragment) getFragmentManager().findFragmentById(R.id.recognizer_runner_view_container);
        }
    }
    
    @Override
    @NonNull
    public ScanningOverlay getScanningOverlay() {
        return mScanningOverlay;
    }
    
    @Override
    public void onScanningDone(@NonNull RecognitionSuccessType successType) {
        // pause scanning to prevent new results while fragment is being removed
        mRecognizerRunnerFragment.getRecognizerRunnerView().pauseScanning();
        
        // now you can remove the RecognizerRunnerFragment with new fragment transaction
        // and use result within mRecognizer safely without the need for making a copy of it
        
        // if not paused, as soon as this method ends, RecognizerRunnerFragments continues
        // scanning. Note that this can happen even if you created fragment transaction for
        // removal of RecognizerRunnerFragment - in the time between end of this method
        // and beginning of execution of the transaction. So to ensure result within mRecognizer
        // does not get mutated, ensure calling pauseScanning() as shown above.
    }
    
    private BarcodeOverlayController createOverlay() {
        // create Pdf417Recognizer
        mRecognizer = new Pdf417Recognizer();
        
        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = new RecognizerBundle(mRecognizer);
        
        // Settings for BarcodeOverlayController overlay
        BarcodeUISettings settings = new BarcodeUISettings(mRecognizerBundle);
        
        return new BarcodeOverlayController(settings, this);
    }
}
```

Also please refer to demo apps provided with the SDK for more detailed example and make sure your host activity's orientation is set to `nosensor` or has configuration changing enabled (i.e. is not restarted when configuration change happens). For more information, check [this section](#scanOrientation).

### <a name="builtInUIComponents"></a> Built-in activities and overlays

Within _PDF417.mobi_ SDK there are several built-in activities and scanning overlays that you can use to perform scanning.
#### `BarcodeScanActivity` and `BarcodeOverlayController`

[`BarcodeOverlayController`](https://pdf417.github.io/pdf417-android/com/microblink/fragment/overlay/BarcodeOverlayController.html) is overlay for [`RecognizerRunnerFragment`](https://pdf417.github.io/pdf417-android/com/microblink/fragment/RecognizerRunnerFragment.html) best suited for performing scanning of various barcodes. [`BarcodeScanActivity`](https://pdf417.github.io/pdf417-android/com/microblink/activity/BarcodeScanActivity.html) contains `RecognizerRunnerFragment` with [`BarcodeOverlayController`](https://pdf417.github.io/pdf417-android/com/microblink/fragment/overlay/BarcodeOverlayController.html), which can be used out of the box to perform scanning using the default UI.
### <a name="changeBuiltInUIComponents"></a> Changing appearance of built-in activities and scanning overlays

Built-in activities and overlays use resources from the `res` folder within `LibPdf417Mobi.aar` to display its contents. If you need a fully customised UI, we recommend creating completely custom scanning procedure (either activity or fragment), as described [here](#recognizerRunnerView). However, if you just want to slightly change the appearance of built-in activity or overlay, you can do that by overriding appropriate resource values, however this is **strictly not recommended**, as it can have unknown effects on the appearance of the UI component. If you think that some part of our built-in UI component should be configurable in a way that it currently is not, please [let us know](https://help.microblink.com) and we will consider adding that configurability into appropriate settings object.

### <a name="translation"></a> Translation and localization

Strings used within built-in activities and overlays can be localized to any language. If you are using `RecognizerRunnerView` ([see this chapter for more information](#recognizerRunnerView)) in your custom scan activity or fragment, you should handle localization as in any other Android app. `RecognizerRunnerView` does not use strings nor drawables, it only uses assets from `assets/microblink` folder. Those assets must not be touched as they are required for recognition to work correctly.

However, if you use our built-in activities or overlays, they will use resources packed within `LibPdf417Mobi.aar` to display strings and images on top of the camera view. We have already prepared strings for several languages which you can use out of the box. You can also [modify those strings](#stringChanging), or you can [add your own language](#addLanguage).

To use a language, you have to enable it from the code:
		
* To use a certain language, you should call method `LanguageUtils.setLanguageAndCountry(language, country, context)`. For example, you can set language to Croatian like this:
	
	```java
	// define PDF417.mobi language
	LanguageUtils.setLanguageAndCountry("hr", "", this);
	```

#### <a name="addLanguage"></a> Adding new language

_PDF417.mobi_ can easily be translated to other languages. The `res` folder in `LibPdf417Mobi.aar` archive has folder `values` which contains `strings.xml` - this file contains english strings. In order to make e.g. croatian translation, create a folder `values-hr` in your project and put the copy of `strings.xml` inside it (you might need to extract `LibPdf417Mobi.aar` archive to access those files). Then, open that file and translate the strings from English into Croatian.

#### <a name="stringChanging"></a> Changing strings in the existing language
	
To modify an existing string, the best approach would be to:

1. Choose a language you want to modify. For example Croatian ('hr').
2. Find `strings.xml` in folder `res/values-hr` of the `LibPdf417Mobi.aar` archive
3. Choose a string key which you want to change. For example: ```<string name="MBBack">Back</string>```
4. In your project create a file `strings.xml` in the folder `res/values-hr`, if it doesn't already exist
5. Create an entry in the file with the value for the string which you want. For example: ```<string name="MBBack">Natrag</string>```
6. Repeat for all the string you wish to change

## <a name="recognizerRunnerView"></a> Embedding `RecognizerRunnerView` into custom scan activity
This section discusses how to embed [RecognizerRunnerView](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerRunnerView.html) into your scan activity and perform scan.

1. First make sure that `RecognizerRunnerView` is a member field in your activity. This is required because you will need to pass all activity's lifecycle events to `RecognizerRunnerView`.
2. It is recommended to keep your scan activity in one orientation, such as `portrait` or `landscape`. Setting `sensor` as scan activity's orientation will trigger full restart of activity whenever device orientation changes. This will provide very poor user experience because both camera and _PDF417.mobi_ native library will have to be restarted every time. There are measures against this behaviour that are discussed [later](#scanOrientation).
3. In your activity's `onCreate` method, create a new `RecognizerRunnerView`, set [RecognizerBundle](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/RecognizerBundle.html) containing recognizers that will be used by the view, define [CameraEventsListener](https://pdf417.github.io/pdf417-android/com/microblink/view/CameraEventsListener.html) that will handle mandatory camera events, define [ScanResultListener](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/ScanResultListener.html) that will receive call when recognition has been completed and then call its `create` method. After that, add your views that should be layouted on top of camera view.
4. Override your activity's `onStart`, `onResume`, `onPause`, `onStop` and `onDestroy` methods and call `RecognizerRunnerView's` lifecycle methods `start`, `resume`, `pause`, `stop` and `destroy`. This will ensure correct camera and native resource management. If you plan to manage `RecognizerRunnerView's` lifecycle independently of host's lifecycle, make sure the order of calls to lifecycle methods is the same as is with activities (i.e. you should not call `resume` method if `create` and `start` were not called first).

Here is the minimum example of integration of `RecognizerRunnerView` as the only view in your activity:

```java
public class MyScanActivity extends Activity implements ScanResultListener, CameraEventsListener {
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 69;
    private RecognizerRunnerView mRecognizerRunnerView;
    private Pdf417Recognizer mRecognizer;
    private RecognizerBundle mRecognizerBundle;
    	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // create Pdf417Recognizer
        mRecognizer = new Pdf417Recognizer();
        
        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = new RecognizerBundle(mRecognizer);				
        // create RecognizerRunnerView
        mRecognizerRunnerView = new RecognizerRunnerView(this);

        // associate RecognizerBundle with RecognizerRunnerView
        mRecognizerRunnerView.setRecognizerBundle(mRecognizerBundle);
				
        // scan result listener will be notified when scanning is complete
        mRecognizerRunnerView.setScanResultListener(this);
        // camera events listener will be notified about camera lifecycle and errors
        mRecognizerRunnerView.setCameraEventsListener(this);
		   
        mRecognizerRunnerView.create();
        setContentView(mRecognizerRunnerView);
    }
	
    @Override
    protected void onStart() {
        super.onStart();
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.start();
    }
	
    @Override
    protected void onResume() {
        super.onResume();
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.destroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.changeConfiguration(newConfig);
    }
		
    @Override
    public void onScanningDone(@NonNull RecognitionSuccessType successType) {
        // this method is from ScanResultListener and will be called when scanning completes
        // you can obtain scanning result by calling getResult on each
        // recognizer that you bundled into RecognizerBundle.
        // for example:
        
        Pdf417Recognizer.Result result = mRecognizer.getResult();
        if (result.getResultState() == Recognizer.Result.State.Valid) {
            // result is valid, you can use it however you wish
        }
        
        // Note that mRecognizer is stateful object and that as soon as
        // scanning either resumes or its state is reset
        // the result object within mRecognizer will be changed. If you
        // need to create a immutable copy of the result, you can do that
        // by calling clone() on it, for example:

        Pdf417Recognizer.Result immutableCopy = result.clone();
    	
        // After this method ends, scanning will be resumed and recognition
        // state will be retained. If you want to prevent that, then
        // you should call:
        mRecognizerRunnerView.resetRecognitionState();
        // Note that reseting recognition state will clear internal result
        // objects of all recognizers that are bundled in RecognizerBundle
        // associated with RecognizerRunnerView.

        // If you want to pause scanning to prevent receiving recognition
        // results or mutating result, you should call:
        mRecognizerRunnerView.pauseScanning();
        // if scanning is paused at the end of this method, it is guaranteed
        // that result within mRecognizer will not be mutated, therefore you
        // can avoid creating a copy as described above
        
        // After scanning is paused, you will have to resume it with:
        mRecognizerRunnerView.resumeScanning(true);
        // boolean in resumeScanning method indicates whether recognition
        // state should be automatically reset when resuming scanning - this
        // includes clearing result of mRecognizer
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
    	 * Called in Android 6.0 and newer if camera permission is not given
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

For that matter, we recommend setting your scan activity to either `portrait` or `landscape` mode and handle device orientation changes manually. To help you with this, `RecognizerRunnerView` supports adding child views to it that will be rotated regardless of activity's `screenOrientation`. You add a view you wish to be rotated (such as view that contains buttons, status messages, etc.) to `RecognizerRunnerView` with [addChildView](#{javadocUrl}(com/microblink/view/CameraViewGroup.html#addChildView-android.view.View-boolean-)) method. The second parameter of the method is a boolean that defines whether the view you are adding will be rotated with device. To define allowed orientations, implement [OrientationAllowedListener](https://pdf417.github.io/pdf417-android/com/microblink/view/OrientationAllowedListener.html) interface and add it to `RecognizerRunnerView` with method `setOrientationAllowedListener`. **This is the recommended way of rotating camera overlay.**

However, if you really want to set `screenOrientation` property to `sensor` or similar and want Android to handle orientation changes of your scan activity, then we recommend to set `configChanges` property of your activity to `orientation|screenSize`. This will tell Android not to restart your activity when device orientation changes. Instead, activity's `onConfigurationChanged` method will be called so that activity can be notified of the configuration change. In your implementation of this method, you should call `changeConfiguration` method of `RecognizerView` so it can adapt its camera surface and child views to new configuration.
## <a name="directAPI"></a> Using Direct API for recognition of Android Bitmaps and custom camera frames

This section will describe how to use direct API to recognize android Bitmaps without the need for camera. You can use direct API anywhere from your application, not just from activities.

1. First, you need to obtain reference to [RecognizerRunner singleton](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html) using [getSingletonInstance](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html#getSingletonInstance--).
2. Second, you need to [initialize the recognizer runner](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html#initialize-android.content.Context-com.microblink.entities.recognizers.RecognizerBundle-com.microblink.directApi.DirectApiErrorListener-).
3. After initialization, you can use singleton to [process Android bitmaps](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html#recognizeBitmap-android.graphics.Bitmap-com.microblink.hardware.orientation.Orientation-com.microblink.geometry.Rectangle-com.microblink.view.recognition.ScanResultListener-) or [images](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html#recognizeImage-com.microblink.image.Image-com.microblink.view.recognition.ScanResultListener-) that are [built from custom camera frames](https://pdf417.github.io/pdf417-android/com/microblink/image/ImageBuilder.html#buildImageFromCamera1NV21Frame-byte:A-int-int-com.microblink.hardware.orientation.Orientation-com.microblink.geometry.Rectangle-). Currently, it is not possible to process multiple images in parallel.
4. Do not forget to [terminate](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html#terminate--) the recognizer runner singleton after usage (it is a shared resource).

Here is the minimum example of usage of direct API for recognizing android Bitmap:

```java
public class DirectAPIActivity extends Activity implements ScanResultListener {
    private RecognizerRunner mRecognizerRunner;
    private Pdf417Recognizer mRecognizer;
    private RecognizerBundle mRecognizerBundle;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize your activity here
        // create Pdf417Recognizer
        mRecognizer = new Pdf417Recognizer();
        
        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = new RecognizerBundle(mRecognizer);
        
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
        mRecognizerRunner.recognize(bitmap, Orientation.ORIENTATION_LANDSCAPE_RIGHT, this);
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        mRecognizerRunner.terminate();
    }

    @Override
    public void onScanningDone(@NonNull RecognitionSuccessType successType) {
        // this method is from ScanResultListener and will be called 
        // when scanning completes
        // you can obtain scanning result by calling getResult on each
        // recognizer that you bundled into RecognizerBundle.
        // for example:
        
        Pdf417Recognizer.Result result = mRecognizer.getResult();
        if (result.getResultState() == Recognizer.Result.State.Valid) {
            // result is valid, you can use it however you wish
        }
    }
    
}
```

### <a name="directAPIStateMachine"></a> Understanding DirectAPI's state machine

DirectAPI's `RecognizerRunner` singleton is actually a state machine which can be in one of 3 states: `OFFLINE`, `READY` and `WORKING`. 

- When you obtain the reference to `RecognizerRunner` singleton, it will be in `OFFLINE` state. 
- You can initialize `RecognizerRunner` by calling [initialize](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html#initialize-android.content.Context-com.microblink.entities.recognizers.RecognizerBundle-com.microblink.directApi.DirectApiErrorListener-) method. If you call `initialize` method while `RecognizerRunner` is not in `OFFLINE` state, you will get `IllegalStateException`.
- After successful initialization, `RecognizerRunner` will move to `READY` state. Now you can call any of the `recognize*` methods.
- When starting recognition with any of the `recognize*` methods, `RecognizerRunner` will move to `WORKING` state. If you attempt to call these methods while `RecognizerRunner` is not in `READY` state, you will get `IllegalStateException`
- Recognition is performed on background thread so it is safe to call all `RecognizerRunner's` methods from UI thread
- When recognition is finished, `RecognizerRunner` first moves back to `READY` state and then calls the [onScanningDone](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/ScanResultListener.html#onScanningDone-RecognitionSuccessType-) method of the provided [`ScanResultListener`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/ScanResultListener.html). 
- Please note that `ScanResultListener`'s [`onScanningDone`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/ScanResultListener.html#onScanningDone-RecognitionSuccessType-) method will be called on background processing thread, so make sure you do not perform UI operations in this calback. Also note that until the `onScanningDone` method completes, `RecognizerRunner` will not perform recognition of another image, even if any of the `recognize*` methods have been called just after transitioning to `READY` state. This is to ensure that results of the recognizers bundled within `RecognizerBundle` associated with `RecognizerRunner` are not modified while possibly being used within `onScanningDone` method.
- By calling [`terminate`](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html#terminate--) method, `RecognizerRunner` singleton will release all its internal resources. Note that even after calling `terminate` you might receive `onScanningDone` event if there was work in progress when `terminate` was called.
- `terminate` method can be called from any `RecognizerRunner` singleton's state
- You can observe `RecognizerRunner` singleton's state with method [`getCurrentState`](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html#getCurrentState--)

### <a name="directAPIWithRecognizer"></a> Using DirectAPI while RecognizerRunnerView is active
Both [RecognizerRunnerView](#recognizerRunnerView) and `RecognizerRunner` use the same internal singleton that manages native code. This singleton handles initialization and termination of native library and propagating recognizers to native library. It is possible to use `RecognizerRunnerView` and `RecognizerRunner` together, as internal singleton will make sure correct synchronization and correct recognition settings are used. If you run into problems while using `RecognizerRunner` in combination with `RecognizerRunnerView`, [let us know](http://help.microblink.com)!

## <a name="processingEvents"></a> Handling processing events with `RecognizerRunner` and `RecognizerRunnerView`

This section will describe how you can subscribe to and handle processing events when using [RecognizerRunner](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html) or [RecognizerRunnerView](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerRunnerView.html). Processing events, also known as _Metadata callbacks_ are purely intended for giving processing feedback on UI or to capture some debug information during development of your app using _PDF417.mobi_ SDK. For that reason, built-in activities and fragments do not support subscribing and handling of those events from third parties - they handle those events internally. If you need to handle those events by yourself, you need to use either [RecognizerRunnerView](#recognizerRunnerView) or [RecognizerRunner](#directAPI).

Callbacks for all events are bundled together into the [MetadataCallbacks](https://pdf417.github.io/pdf417-android/com/microblink/metadata/MetadataCallbacks.html) object. Both [RecognizerRunner](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html#setMetadataCallbacks-com.microblink.metadata.MetadataCallbacks-) and [RecognizerRunnerView](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerRunnerView.html#setMetadataCallbacks-com.microblink.metadata.MetadataCallbacks-) have methods which allow you to set all your callbacks.

We suggest that you check for more information about available callbacks and events to which you can handle in the [javadoc for MetadataCallbacks class](https://pdf417.github.io/pdf417-android/com/microblink/metadata/MetadataCallbacks.html).

### <a name="processingEventsImportantNote"></a> Note about `setMetadataCallbacks` method

Please note that both those methods need to pass information about available callbacks to the native code and for efficiency reasons this is done at the time `setMetadataCallbacks` method is called and **not every time** when change occurs within the `MetadataCallbacks` object. This means that if you, for example, set `QuadDetectionCallback` to `MetadataCallbacks` **after** you already called `setMetadataCallbacks` method, the `QuadDetectionCallback` will not be registered with the native code and you will not receive its events.

Similarly, if you, for example, remove the `QuadDetectionCallback` from `MetadataCallbacks` object **after** you already called `setMetadataCallbacks` method, your app will crash with `NullPointerException` when our processing code attempts to invoke the method on removed callback (which is now set to `null`). We **deliberately** do not perform `null` check here because of two reasons:

- it is inefficient
- having `null` callback, while still being registered to native code is illegal state of your program and it should therefore crash

**Remember**, each time you make some changes to `MetadataCallbacks` object, you need to apply those changes to to your `RecognizerRunner` or `RecognizerRunnerView` by calling its `setMetadataCallbacks` method.

# <a name="availableRecognizers"></a> `RecognizerBundle` and available recognizers

[RecognizerBundle](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/RecognizerBundle.html) is an object which wraps the [Recognizers](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/Recognizer.html) and defines settings about how recognition should be performed. Besides that, `RecognizerBundle` makes it possible to transfer `Recognizer` objects between different activities, which is required when using built-in activities to perform scanning, as described in [first scan section](#quickScan), but is also handy when you need to pass `Recognizer` objects between your activities.

This section will first describe [what is a `Recognizer`](#recognizerConcept) and how it should be used to perform recognition of the images, videos and camera stream. Next, [we will describe how `RecognizerBundle`](#recognizerBundle) can be used to tweak the recognition procedure and to transfer `Recognizer` objects between activities. Finally, we will give a [list of all available `Recognizer` objects](#recognizerList) and give a brief description of each `Recognizer`, its purpose and recommendations how it should be used to get best performance and user experience.

## <a name="recognizerConcept"></a> The `Recognizer` concept

The [Recognizer](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/Recognizer.html) is the basic unit of processing within the _PDF417.mobi_ SDK. Its main purpose is to process the image and extract meaningful information from it. As you will see [later](#recognizerList), the _PDF417.mobi_ SDK has lots of different `Recognizer` objects that have various purposes.

Each `Recognizer` has a `Result` object, which contains the data that was extracted from the image. The `Result` object is a member of corresponding `Recognizer` object its lifetime is bound to the lifetime of its parent `Recognizer` object. If you need your `Result` object to outlive its parent `Recognizer` object, you must make a copy of it by calling its method [`clone()`](https://pdf417.github.io/pdf417-android/com/microblink/entities/Entity.Result.html#clone--).

Every `Recognizer` is a stateful object, that can be in two states: _idle state_ and _working state_. While in _idle state_, you can tweak `Recognizer` object's properties via its getters and setters. After you bundle it into a `RecognizerBundle` and use either [RecognizerRunner](https://pdf417.github.io/pdf417-android/com/microblink/directApi/RecognizerRunner.html) or [RecognizerRunnerView](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerRunnerView.html) to _run_ the processing with all `Recognizer` objects bundled within `RecognizerBundle`, it will change to _working state_ where the `Recognizer` object is being used for processing. While being in _working state_, you cannot tweak `Recognizer` object's properties. If you need to, you have to create a copy of the `Recognizer` object by calling its [`clone()`](https://pdf417.github.io/pdf417-android/com/microblink/entities/Entity.html#clone--), then tweak that copy, bundle it into a new `RecognizerBundle` and use [`reconfigureRecognizers`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerRunnerView.html#reconfigureRecognizers-com.microblink.entities.recognizers.RecognizerBundle-) to ensure new bundle gets used on processing thread.

While `Recognizer` object works, it changes its internal state and its result. The `Recognizer` object's `Result` always starts in [Empty state](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/Recognizer.Result.State.html#Empty). When corresponding `Recognizer` object performs the recognition of given image, its `Result` can either stay in `Empty` state (in case `Recognizer` failed to perform recognition), move to [Uncertain state](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/Recognizer.Result.State.html#Uncertain) (in case `Recognizer` performed the recognition, but not all mandatory information was extracted) or move to [Valid state](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/Recognizer.Result.State.html#Valid) (in case `Recognizer` performed recognition and all mandatory information was successfully extracted from the image).

As soon as one `Recognizer` object's `Result` within `RecognizerBundle` given to `RecognizerRunner` or `RecognizerRunnerView` changes to `Valid` state, the [`onScanningDone`](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/ScanResultListener.html#onScanningDone-RecognitionSuccessType-) callback will be invoked on same thread that performs the background processing and you will have the opportunity to inspect each of your `Recognizer` objects' `Results` to see which one has moved to `Valid` state.

As already stated in [section about `RecognizerRunnerView`](#recognizerRunnerView), as soon as `onScanningDone` method ends, the `RecognizerRunnerView` will continue processing new camera frames with same `Recognizer` objects, unless [paused](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerRunnerView.html#pauseScanning--). Continuation of processing or [resetting recognition](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerRunnerView.html#resetRecognitionState--) will modify or reset all `Recognizer` objects's `Results`. When using built-in activities, as soon as `onScanningDone` is invoked, built-in activity pauses the `RecognizerRunnerView` and starts finishing the activity, while saving the `RecognizerBundle` with active `Recognizer` objects into `Intent` so they can be transferred back to the calling activities.


## <a name="recognizerBundle"></a> `RecognizerBundle`

The [RecognizerBundle](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/RecognizerBundle.html) is wrapper around [Recognizers](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/Recognizer.html) objects that can be used to transfer `Recognizer` objects between activities and to give `Recognizer` objects to `RecognizerRunner` or `RecognizerRunnerView` for processing.

The `RecognizerBundle` is always [constructed with array](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/RecognizerBundle.html#RecognizerBundle-com.microblink.entities.recognizers.Recognizer:A-) of `Recognizer` objects that need to be prepared for recognition (i.e. their properties must be tweaked already). The _varargs_ constructor makes it easier to pass `Recognizer` objects to it, without the need of creating a temporary array.

The `RecognizerBundle` manages a chain of `Recognizer` objects within the recognition process. When a new image arrives, it is processed by the first `Recognizer` in chain, then by the second and so on, iterating until a `Recognizer` object's `Result` changes its state to `Valid` or all of the `Recognizer` objects in chain were invoked (none getting a `Valid` result state). If you want to invoke all `Recognizers` in the chain, regardless of whether some `Recognizer` object's `Result` in chain has changed its state to `Valid` or not, you can [allow returning of multiple results on a single image](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/RecognizerBundle.html#setAllowMultipleScanResultsOnSingleImage-boolean-).

You cannot change the order of the `Recognizer` objects within the chain - no matter the order in which you give `Recognizer` objects to `RecognizerBundle`, they are internally ordered in a way that provides best possible performance and accuracy. Also, in order for _PDF417.mobi_ SDK to be able to order `Recognizer` objects in recognition chain in a best way possible, it is not allowed to have multiple instances of `Recognizer` objects of the same type within the chain. Attempting to do so will crash your application.

### <a name="intentOptimization"></a> Passing `Recognizer` objects between activities

Besides managing the chain of `Recognizer` objects, `RecognizerBundle` also manages transferring bundled `Recognizer` objects between different activities within your app. Although each `Recognizer` object, and each its `Result` object implements [Parcelable interface](https://developer.android.com/reference/android/os/Parcelable.html), it is not so straight forward to put those objects into [Intent](https://developer.android.com/reference/android/content/Intent.html) and pass them around between your activities and services for two main reasons:

- `Result` object is tied to its `Recognizer` object, which manages lifetime of the native `Result` object.
- `Result` object often contains large data blocks, such as images, which cannot be transferred via `Intent` because of [Android's Intent transaction data limit](https://developer.android.com/reference/android/os/TransactionTooLargeException.html).

Although the first problem can be easily worked around by making a [copy](https://pdf417.github.io/pdf417-android/com/microblink/entities/Entity.Result.html#clone--) of the `Result` and transfer it independently, the second problem is much tougher to cope with. This is where, `RecognizerBundle's` methods [saveToIntent](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/RecognizerBundle.html#saveToIntent-android.content.Intent-) and [loadFromIntent](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/RecognizerBundle.html#loadFromIntent-android.content.Intent-) come to help, as they ensure the safe passing of `Recognizer` objects bundled within `RecognizerBundle` between activities according to policy defined with method [`setIntentDataTransferMode`](https://pdf417.github.io/pdf417-android/com/microblink/MicroblinkSDK.html#setIntentDataTransferMode-com.microblink.intent.IntentDataTransferMode-):

- if set to [`STANDARD`](https://pdf417.github.io/pdf417-android/com/microblink/intent/IntentDataTransferMode.html#STANDARD), the `Recognizer` objects will be passed via `Intent` using normal _Intent transaction mechanism_, which is limited by [Android's Intent transaction data limit](https://developer.android.com/reference/android/os/TransactionTooLargeException.html). This is same as manually putting `Recognizer` objects into `Intent` and is OK as long as you do not use `Recognizer` objects that produce images or other large objects in their `Results`.
- if set to [`OPTIMISED`](https://pdf417.github.io/pdf417-android/com/microblink/intent/IntentDataTransferMode.html#OPTIMISED), the `Recognizer` objects will be passed via internal singleton object and no serialization will take place. This means that there is no limit to the size of data that is being passed. This is also the fastest transfer method, but it has a serious drawback - if Android kills your app to save memory for other apps and then later restarts it and redelivers `Intent` that should contain `Recognizer` objects, the internal singleton that should contain saved `Recognizer` objects will be empty and data that was being sent will be lost. You can easily provoke that condition by choosing _No background processes_ under _Limit background processes_ in your device's _Developer options_, and then switch from your app to another app and then back to your app.
- if set to [`PERSISTED_OPTIMISED`](https://pdf417.github.io/pdf417-android/com/microblink/intent/IntentDataTransferMode.html#PERSISTED_OPTIMISED), the `Recognizer` objects will be passed via internal singleton object (just like in `OPTIMISED` mode) and will additionaly be serialized into a file in your application's private folder. In case Android restarts your app and internal singleton is empty after re-delivery of the `Intent`, the data will be loaded from file and nothing will be lost. The files will be automatically cleaned up when data reading takes place. Just like `OPTIMISED`, this mode does not have limit to the size of data that is being passed and does not have a drawback that `OPTIMISED` mode has, but some users might be concerned about files to which data is being written. 
    - These files **will** contain end-user's private data, such as image of the object that was scanned and the extracted data. Also these files **may** remain saved in your application's private folder until the next successful reading of data from the file. 
    - If your app gets restarted multiple times, only after first restart will reading succeed and will delete the file after reading. If multiple restarts take place, you must implement [`onSaveInstanceState`](https://developer.android.com/reference/android/app/Activity.html#onSaveInstanceState(android.os.Bundle)) and save bundle back to file by calling its [`saveState`](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/RecognizerBundle.html#saveState--) method. Also, after saving state, you should ensure that you [clear saved state](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/RecognizerBundle.html#clearSavedState--) in your [`onResume`](https://developer.android.com/reference/android/app/Activity.html#onResume()), as [`onCreate`](https://developer.android.com/reference/android/app/Activity.html#onCreate(android.os.Bundle)) may not be called if activity is not restarted, while `onSaveInstanceState` may be called as soon as your activity goes to background (before `onStop`), even though activity may not be killed at later time. 
    - If saving data to file in private storage is a concern to you, you should use either `OPTIMISED` mode to transfer large data and image between activities or create your own mechanism for data transfer. Note that your application's private folder is only accessible by your application and your application alone, unless the end-user's device is rooted.

## <a name="recognizerList"></a> List of available recognizers

This section will give a list of all `Recognizer` objects that are available within _PDF417.mobi_ SDK, their purpose and recommendations how they should be used to get best performance and user experience.

### <a name="frameGrabberRecognizer"></a> Frame Grabber Recognizer

The [`FrameGrabberRecognizer`](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/framegrabber/FrameGrabberRecognizer.html) is the simplest recognizer in _PDF417.mobi_ SDK, as it does not perform any processing on the given image, instead it just returns that image back to its [`FrameCallback`](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/framegrabber/FrameCallback.html). Its [Result](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/framegrabber/FrameGrabberRecognizer.Result.html) never changes state from [Empty](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/Recognizer.Result.State.html#Empty).

This recognizer is best for easy capturing of camera frames with [`RecognizerRunnerView`](#recognizerRunnerView). Note that [`Image`](https://pdf417.github.io/pdf417-android/com/microblink/image/Image.html) sent to [`onFrameAvailable`](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/framegrabber/FrameCallback.html#onFrameAvailable-com.microblink.image.Image-boolean-double-) are temporary and their internal buffers all valid only until the `onFrameAvailable` method is executing - as soon as method ends, all internal buffers of `Image` object are disposed. If you need to store `Image` object for later use, you must create a copy of it by calling [`clone`](https://pdf417.github.io/pdf417-android/com/microblink/image/Image.html#clone--).

Also note that [`FrameCallback`](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/framegrabber/FrameCallback.html) interface extends [Parcelable interface](https://developer.android.com/reference/android/os/Parcelable.html), which means that when implementing `FrameCallback` interface, you must also implement `Parcelable` interface. 

This is especially important if you plan to transfer `FrameGrabberRecognizer` between activities - in that case, keep in mind that the instance of your object may not be the same as the instance on which `onFrameAvailable` method gets called - the instance that receives `onFrameAvailable` calls is the one that is created within activity that is performing the scan.

### <a name="successFrameGrabberRecognizer"></a> Success Frame Grabber Recognizer

The [`SuccessFrameGrabberRecognizer`](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/successframe/SuccessFrameGrabberRecognizer.html) is a special `Recognizer` that wraps some other `Recognizer` and impersonates it while processing the image. However, when the `Recognizer` being impersonated changes its `Result` into `Valid` state, the `SuccessFrameGrabberRecognizer` captures the image and saves it into its own `Result` object.

Since `SuccessFrameGrabberRecognizer` impersonates its slave `Recognizer` object, it is not possible to give both concrete `Recognizer` object and `SuccessFrameGrabberRecognizer` that wraps it to same `RecognizerBundle` - doing so will have the same result as if you have given two instances of same `Recognizer` type to the `RecognizerBundle` - it will crash your application.

This recognizer is best for use cases when you need to capture the exact image that was being processed by some other `Recognizer` object at the time its `Result` became `Valid`. When that happens, `SuccessFrameGrabber's` [`Result`](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/successframe/SuccessFrameGrabberRecognizer.Result.html) will also become `Valid` and will contain described image. That image can then be retreived with [`getSuccessFrame()`](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/successframe/SuccessFrameGrabberRecognizer.Result.html#getSuccessFrame--) method.

### <a name="pdf417Recognizer"></a> PDF417 recognizer

The [`Pdf417Recognizer`](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/blinkbarcode/pdf417/Pdf417Recognizer.html) is recognizer specialised for scanning [PDF417 2D barcodes](https://en.wikipedia.org/wiki/PDF417). This recognizer can recognize only PDF417 2D barcodes - for recognition of other barcodes, please refer to [BarcodeRecognizer](#barcodeRecognizer).

This recognizer can be used in any context, but it works best with the [`BarcodeScanActivity`](https://pdf417.github.io/pdf417-android/com/microblink/activity/BarcodeScanActivity.html), which has UI best suited for barcode scanning.

### <a name="barcodeRecognizer"></a> Barcode recognizer

The [`BarcodeRecognizer`](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/blinkbarcode/barcode/BarcodeRecognizer.html) is recognizer specialised for scanning various types of barcodes. This recognizer should be your first choice when scanning barcodes as it supports lots of barcode symbologies, including the [PDF417 2D barcodes](https://en.wikipedia.org/wiki/PDF417), thus making [PDF417 recognizer](#pdf417Recognizer) possibly redundant, which was kept only for its simplicity.

As you can see from [javadoc](https://pdf417.github.io/pdf417-android/com/microblink/entities/recognizers/blinkbarcode/barcode/BarcodeRecognizer.html), you can enable multiple barcode symbologies within this recognizer, however keep in mind that enabling more barcode symbologies affect scanning performance - the more barcode symbologies are enabled, the slower the overall recognition performance. Also, keep in mind that some simple barcode symbologies that lack proper redundancy, such as [Code 39](https://en.wikipedia.org/wiki/Code_39), can be recognized within more complex barcodes, especially 2D barcodes, like [PDF417](https://en.wikipedia.org/wiki/PDF417).

This recognizer can be used in any context, but it works best with the [`BarcodeScanActivity`](https://pdf417.github.io/pdf417-android/com/microblink/activity/BarcodeScanActivity.html), which has UI best suited for barcode scanning.
# <a name="embedAAR"></a> Embedding _PDF417.mobi_ inside another SDK

When creating your own SDK which depends on _PDF417.mobi_, you should consider following cases:

- [_PDF417.mobi_ licensing model](#licensingModel)
- [ensuring final app gets all classes and resources that are required by _PDF417.mobi_](#sdkIntegrationIntoApp)

## <a name="licensingModel"></a> _PDF417.mobi_ licensing model

_PDF417.mobi_ supports two types of licenses: 

- application licenses
- library licenses.

### <a name="appLicence"></a> Application licenses

Application licenses are bound to application's [package name](http://tools.android.com/tech-docs/new-build-system/applicationid-vs-packagename). This means that each app must have its own license in order to be able to use _PDF417.mobi_. This model is appropriate when integrating _PDF417.mobi_ directly into app, however if you are creating SDK that depends on _PDF417.mobi_, you would need separate _PDF417.mobi_ license for each of your clients using your SDK. This is not practical, so you should contact us at [help.microblink.com](http://help.microblink.com) and we can provide you a library license.

### <a name="libLicence"></a> Library licenses

Library license keys are bound to licensee name. You will provide your licensee name with your inquiry for library license. Unlike application licenses, library licenses must be set together with licensee name:

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        MicroblinkSDK.setLicenseFile("path/to/license/file/within/assets/dir", "licensee", this);
    }
}
```
	
## <a name="sdkIntegrationIntoApp"></a> Ensuring the final app gets all resources required by _PDF417.mobi_

At the time of writing this documentation, [Android does not have support for combining multiple AAR libraries into single fat AAR](https://stackoverflow.com/questions/20700581/android-studio-how-to-package-single-aar-from-multiple-library-projects/20715155#20715155). The problem is that resource merging is done while building application, not while building AAR, so application must be aware of all its dependencies. **There is no official Android way of "hiding" third party AAR within your AAR.**

This problem is usually solved with transitive Maven dependencies, i.e. when publishing your AAR to Maven you specify dependencies of your AAR so they are automatically referenced by app using your AAR. Besides this, there are also several other approaches you can try:

- you can ask your clients to reference _PDF417.mobi_ in their app when integrating your SDK
- since the problem lies in resource merging part you can try avoiding this step by ensuring your library will not use any component from _PDF417.mobi_ that uses resources (i.e. built-in activities, fragments and views, except `RecognizerRunnerView`). You can perform [custom UI integration](#recognizerRunnerView) while taking care that all resources (strings, layouts, images, ...) used are solely from your AAR, not from _PDF417.mobi_. Then, in your AAR you should not reference `LibPdf417Mobi.aar` as gradle dependency, instead you should unzip it and copy its assets to your AAR’s assets folder, its `classes.jar` to your AAR’s lib folder (which should be referenced by gradle as jar dependency) and contents of its jni folder to your AAR’s src/main/jniLibs folder.
- Another approach is to use [3rd party unofficial gradle script](https://github.com/adwiv/android-fat-aar) that aim to combine multiple AARs into single fat AAR. Use this script at your own risk and report issues to [its developers](https://github.com/adwiv/android-fat-aar/issues) - we do not offer support for using that script.
- There is also a [3rd party unofficial gradle plugin](https://github.com/Vigi0303/fat-aar-plugin) which aims to do the same, but is more up to date with latest updates to Android gradle plugin. Use this plugin at your own risk and report all issues with using to [its developers](https://github.com/Vigi0303/fat-aar-plugin/issues) - we do not offer support for using that plugin.

# <a name="archConsider"></a> Processor architecture considerations

_PDF417.mobi_ is distributed with both ARMv7, ARM64, x86 and x86_64 native library binaries.

ARMv7 architecture gives the ability to take advantage of hardware accelerated floating point operations and SIMD processing with [NEON](http://www.arm.com/products/processors/technologies/neon.php). This gives _PDF417.mobi_ a huge performance boost on devices that have ARMv7 processors. Most new devices (all since 2012.) have ARMv7 processor so it makes little sense not to take advantage of performance boosts that those processors can give. Also note that some devices with ARMv7 processors do not support NEON instruction sets, most popular being those based on [NVIDIA Tegra 2](https://en.wikipedia.org/wiki/Tegra#Tegra_2). Since these devices are old by today's standard, _PDF417.mobi_ does not support them. For the same reason, _PDF417.mobi_ does not support devices with ARMv5 (`armeabi`) architecture.

ARM64 is the new processor architecture that most new devices use. ARM64 processors are very powerful and also have the possibility to take advantage of new NEON64 SIMD instruction set to quickly process multiple pixels with single instruction.

x86 architecture gives the ability to obtain native speed on x86 android devices, like [Asus Zenfone 4](http://www.gsmarena.com/asus_zenfone_4-5951.php). Without that, _PDF417.mobi_ will not work on such devices, or it will be run on top of ARM emulator that is shipped with device - this will give a huge performance penalty.

x86_64 architecture gives better performance than x86 on devices that use 64-bit Intel Atom processor.

However, there are some issues to be considered:

- ARMv7 build of native library cannot be run on devices that do not have ARMv7 compatible processor (list of those old devices can be found [here](http://www.getawesomeinstantly.com/list-of-armv5-armv6-and-armv5-devices/))
- ARMv7 processors does not understand x86 instruction set
- x86 processors do not understand neither ARM64 nor ARMv7 instruction sets
- however, some x86 android devices ship with the builtin [ARM emulator](http://commonsware.com/blog/2013/11/21/libhoudini-what-it-means-for-developers.html) - such devices are able to run ARM binaries but with performance penalty. There is also a risk that builtin ARM emulator will not understand some specific ARM instruction and will crash.
- ARM64 processors understand ARMv7 instruction set, but ARMv7 processors do not understand ARM64 instructions. 
    - <a name="64bitNotice"></a> **NOTE:** as of year 2018, some android devices that ship with ARM64 processor do not have full compatibility with ARMv7. This is mostly due to incorrect configuration of Android's 32-bit subsystem by the vendor, however Google [has announced](https://android-developers.googleblog.com/2017/12/improving-app-security-and-performance.html) that as od August 2019 all apps on PlayStore that contain native code will need to have native support for 64-bit processors (this includes ARM64 and x86_64) - this is in anticipation of future Android devices that will support 64-bit code **only**, i.e. that will have ARM64 processors that do not understand ARMv7 instruction set.
- if ARM64 processor executes ARMv7 code, it does not take advantage of modern NEON64 SIMD operations and does not take advantage of 64-bit registers it has - it runs in emulation mode
- x86_64 processors understand x86 instruction set, but x86 processors do not understand x86_64 instruction set
- if x86_64 processor executes x86 code, it does not take advantage of 64-bit registers and use two instructions instead of one for 64-bit operations

`LibPdf417Mobi.aar` archive contains ARMv7, ARM64, x86 and x86_64 builds of native library. By default, when you integrate _PDF417.mobi_ into your app, your app will contain native builds for all processor architectures. Thus, _PDF417.mobi_ will work on ARMv7, ARM64, x86 and x86_64 devices and will use ARMv7 features on ARMv7 devices and ARM64 features on ARM64 devices. However, the size of your application will be rather large.

## <a name="reduceSize"></a> Reducing the final size of your app

If your final app is too large because of _PDF417.mobi_, you can decide to create multiple flavors of your app - one flavor for each architecture. With gradle and Android studio this is very easy - just add the following code to `build.gradle` file of your app:

```
android {
  ...
  splits {
    abi {
      enable true
      reset()
      include 'x86', 'armeabi-v7a', 'arm64-v8a', 'x86_64'
      universalApk true
    }
  }
}
```

With that build instructions, gradle will build four different APK files for your app. Each APK will contain only native library for one processor architecture and one APK will contain all architectures. In order for Google Play to accept multiple APKs of the same app, you need to ensure that each APK has different version code. This can easily be done by defining a version code prefix that is dependent on architecture and adding real version code number to it in following gradle script:

```
// map for the version code
def abiVersionCodes = ['armeabi-v7a':1, 'arm64-v8a':2, 'x86':3, 'x86_64':4]

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

For more information about creating APK splits with gradle, check [this article from Google](https://developer.android.com/studio/build/configure-apk-splits.html#configure-abi-split).

After generating multiple APK's, you need to upload them to Google Play. For tutorial and rules about uploading multiple APK's to Google Play, please read the [official Google article about multiple APKs](https://developer.android.com/google/play/publishing/multiple-apks.html).

### Removing processor architecture support in gradle without using APK splits

If you will not be distributing your app via Google Play or for some other reasons you want to have single APK of smaller size, you can completely remove support for certaing CPU architecture from your APK. **This is not recommended due to [consequences](#archConsequences)**.

To remove certain CPU arhitecture, add following statement to your `android` block inside `build.gradle`:

```
android {
	...
	packagingOptions {
		exclude 'lib/<ABI>/libPdf417Mobi.so'
	}
}
```

where `<ABI>` represents the CPU architecture you want to remove:

- to remove ARMv7 support, use `exclude 'lib/armeabi-v7a/libPdf417Mobi.so'`
- to remove x86 support, use `exclude 'lib/x86/libPdf417Mobi.so'`
- to remove ARM64 support, use `exclude 'lib/arm64-v8a/libPdf417Mobi.so'`
    - **NOTE**: this is **not recommended**. See [this notice](#64bitNotice).
- to remove x86_64 support, use `exclude 'lib/x86_64/libPdf417Mobi.so'`

You can also remove multiple processor architectures by specifying `exclude` directive multiple times. Just bear in mind that removing processor architecture will have side effects on performance and stability of your app. Please read [this](#archConsequences) for more information.

### Removing processor architecture support in Eclipse

This section assumes that you have set up and prepared your Eclipse project from `LibPdf417Mobi.aar` as described in chapter [Eclipse integration instructions](#eclipseIntegration).

If you are using Eclipse, removing processor architecture support gets really complicated. Eclipse does not support APK splits and you will either need to remove support for some processors or create several different library projects from `LibPdf417Mobi.aar` - each one for specific processor architecture. 

Native libraryies in eclipse library project are located in subfolder `libs`:

- `libs/armeabi-v7a` contains native libraries for ARMv7 processor arhitecture
- `libs/x86` contains native libraries for x86 processor architecture
- `libs/arm64-v8a` contains native libraries for ARM64 processor architecture
- `libs/x86_64` contains native libraries for x86_64 processor architecture

To remove a support for processor architecture, you should simply delete appropriate folder inside Eclipse library project:

- to remove ARMv7 support, delete folder `libs/armeabi-v7a`
- to remove x86 support, delete folder `libs/x86`
- to remove ARM64 support, delete folder `libs/arm64-v8a`
    - **NOTE**: this is **not recommended**. See [this notice](#64bitNotice).
- to remove x86_64 support, delete folder `libs/x86_64`

### <a name="archConsequences"></a> Consequences of removing processor architecture

However, removing a processor architecture has some consequences:

- by removing ARMv7 support _PDF417.mobi_ will not work on devices that have ARMv7 processors. 
- by removing ARM64 support, _PDF417.mobi_ will not use ARM64 features on ARM64 device
    - also, some future devices may ship with ARM64 processors that will not support ARMv7 instruction set. Please see [this note](#64bitNotice) for more information.
- by removing x86 support, _PDF417.mobi_ will not work on devices that have x86 processor, except in situations when devices have ARM emulator - in that case, _PDF417.mobi_ will work, but will be slow and possibly unstable
- by removing x86_64 support, _PDF417.mobi_ will not use 64-bit optimizations on x86_64 processor, but if x86 support is not removed, _PDF417.mobi_ should work

Our recommendation is to include all architectures into your app - it will work on all devices and will provide best user experience. However, if you really need to reduce the size of your app, we recommend releasing separate version of your app for each processor architecture. It is easiest to do that with [APK splits](#reduceSize).


## <a name="combineNativeLibraries"></a> Combining _PDF417.mobi_ with other native libraries

If you are combining _PDF417.mobi_ library with some other libraries that contain native code into your application, make sure you match the architectures of all native libraries. For example, if third party library has got only ARMv7 and x86 versions, you must use exactly ARMv7 and x86 versions of _PDF417.mobi_ with that library, but not ARM64. Using these architectures will crash your app in initialization step because JVM will try to load all its native dependencies in same preferred architecture and will fail with `UnsatisfiedLinkError`.
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
* licence that is causing problems
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
	* information about device that you are using - we need exact model name of the device. You can obtain that information with any app like [this one](https://play.google.com/store/apps/details?id=ru.andr7e.deviceinfohw)
	* please stress out that you are reporting problem related to Android version of _PDF417.mobi_ SDK


## <a name="faq"></a> Frequently asked questions and known problems
Here is a list of frequently asked questions and solutions for them and also a list of known problems in the SDK and how to work around them.

#### <a name="featureNotSupportedByLicenseKey"></a> In demo everything worked, but after switching to production license I get `InvalidLicenseKeyException` as soon as I construct specific `Recognizer` object

Each license key contains information about which features are allowed to use and which are not. This exception indicates that your production license does not allow using of specific `Recognizer` object. You should contact [support](http://help.microblink.com) to check if provided licence is OK and that it really contains all features that you have purchased.

#### <a name="invalidLicenseKey"></a> I get `InvalidLicenseKeyException` with trial license key

Whenever you construct any `Recognizer` object or any other object that derives from [`Entity`](https://pdf417.github.io/pdf417-android/com/microblink/entities/Entity.html), a check whether license allows using that object will be performed. If license is not set prior constructing that object, you will get `InvalidLicenseKeyException`. We recommend setting license as early as possible in your app, ideally in `onCreate` callback of your [Application singleton](https://developer.android.com/reference/android/app/Application.html).

#### <a name="missingResources"></a> When my app starts, I get exception telling me that some resource/class cannot be found or I get `ClassNotFoundException`

This usually happens when you perform integration into [Eclipse project](#eclipseIntegration) and you forget to add resources or native libraries into the project. You must alway take care that same versions of both resources, assets, java library and native libraries are used in combination. Combining different versions of resources, assets, java and native libraries will trigger crash in SDK. This problem can also occur when you have performed improper integration of _PDF417.mobi_ SDK into your SDK. Please read how to [embed _PDF417.mobi_ inside another SDK](#embedAAR).

#### <a name="unsatisfiedLinkError"></a> When my app starts, I get `UnsatisfiedLinkError`

This error happens when JVM fails to load some native method from native library. If performing integration into [Eclipse project](#eclipseIntegration) make sure you have the same version of all native libraries and java wrapper. If performing integration [into Android studio](quickIntegration) and this error happens, make sure that you have correctly combined _PDF417.mobi_ SDK with [third party SDKs that contain native code](#combineNativeLibraries). If this error also happens in our integration demo apps, then it may indicate a bug in the SDK that is manifested on specific device. Please report that to our [support team](http://help.microblink.com).

#### <a name="lateMetadata1"></a> I've added my callback to `MetadataCallbacks` object, but it is not being called

Make sure that after adding your callback to `MetadataCallbacks` you have applied changes to `RecognizerRunnerView` or `RecognizerRunner` as described in [this section](#processingEventsImportantNote).

#### <a name="lateMetadata2"></a> I've removed my callback to `MetadataCallbacks` object, and now app is crashing with `NullPointerException`

Make sure that after removing your callback from `MetadataCallbacks` you have applied changes to `RecognizerRunnerView` or `RecognizerRunner` as described in [this section](#processingEventsImportantNote).

#### <a name="statefulRecognizer"></a> In my `onScanningDone` callback I have the result inside my `Recognizer`, but when scanning activity finishes, the result is gone

This usually happens when using `RecognizerRunnerView` and forgetting to pause the `RecognizerRunnerView` in your `onScanningDone` callback. Then, as soon as `onScanningDone` happens, the result is mutated or reset by additional processing that `Recognizer` performs in the time between end of your `onScanningDone` callback and actual finishing of the scanning activity. For more information about statefulness of the `Recognizer` objects, check [this section](#recognizerConcept).

#### <a name="transactionTooLarge"></a> I am using built-in activity to perform scanning and after scanning finishes, my app crashes with `IllegalStateException` stating `Data cannot be saved to intent because its size exceeds intent limit`.

This usually happens when you use `Recognizer` that produces image or similar large object inside its `Result` and that object exceeds the Android intent transaction limit. You should enable different intent data transfer mode. For more information about this, [check this section](#intentOptimization). Also, instead of using built-in activity, you can use [`RecognizerRunnerFragment` with built-in scanning overlay](#recognizerRunnerFragment).

#### <a name="transactionTooLarge2"></a> After scanning finishes, my app freezes

This usually happens when you attempt to transfer standalone `Result` that contains images or similar large objects via Intent and the size of the object exceeds Android intent transaction limit. Depending on the device, you will get either [TransactionTooLargeException](https://developer.android.com/reference/android/os/TransactionTooLargeException.html), a simple message `BINDER TRANSACTION FAILED` in log and your app will freeze or you app will get into restart loop. We recommend that you use `RecognizerBundle` and its API for sending `Recognizer` objects via Intent in a more safe manner ([check this section](#intentOptimization) for more information). However, if you really need to transfer standalone `Result` object (e.g. `Result` object obtained by cloning `Result` object owned by specific `Recognizer` object), you need to do that using global variables or singletons within your application. Sending large objects via Intent is not supported by Android.
# <a name="info"></a> Additional info
Complete API reference can be found in [Javadoc](https://pdf417.github.io/pdf417-android/index.html). 

For any other questions, feel free to contact us at [help.microblink.com](http://help.microblink.com).


