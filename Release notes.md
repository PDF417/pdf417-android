# Release notes

## 7.0.0
- new API, which is not backward compatible. Please check [README](README.md) and updated demo applications for more information, but the gist of it is:
    - `RecognizerView` has been renamed to `RecognizerRunnerView` and `Recognizer` singleton to `RecognizerRunner`
    - `Pdf417ScanActivity` has been renamed to `BarcodeScanActivity`
    - previously internal `Recognizer` objects are not internal anymore - instead of having opaque `RecognizerSettings` and `RecognizerResult` objects, you now have stateful `Recognizer` object that contains its `Result` within and mutates it while performing recognition. For more information, see [README](README.md) and updated demo applications
    - added `RecognizerRunnerFragment` with support for various scanning overlays in a manner similar to iOS API. This now allows you to use built-in UI, which was previously strictly available for `Pdf417ScanActivity`, in form of fragment anywhere within your activity. Full details are given in [README](README.md) and in updated demo applications.
- new licence format, which is not backward compatible. Full details are given in [README](README.md) and in updated applications, but the gist of it is:
    - licence can now be provided with either file, byte array or base64-encoded bytes

## 6.4.0
- `LibPDF417` is now fully ProGuard-compatible, i.e. you no longer need to exclude `com.microblink.**` classes in your ProGuard configuration
- removed support for Android 2.3 and Android 4.0 - minimum required Android version is now Android 4.1 (API level 16)
	- devices with Android 4.0 and earlier take [less than 2% of market share](https://developer.android.com/about/dashboards/index.html#Platform) and is very costly to support them
- removed support for `armeabi` devices and ARMv7 devices without NEON SIMD support
    - support for ARMv7 devices with NEON support still remains
- prefixed custom attributes to avoid name collisions with attributes from other libraries:
    - `CameraViewGroup`: renamed animateRotation to `mb_animateRotation`, animationDuration to `mb_animationDuration`, rotatable to `mb_rotatable`
    - `BaseCameraView`:  renamed initialOrientation to `mb_initialOrientation`, aspectMode to `mb_aspectMode`

## 6.3.0
- fixed `UnsatisfiedLinkError` that happened on pre-Lollipop devices and was introduced in v6.2.1
- optimised native binary size
	- 23% size reduction for `arm64-v8a` ABI
	- 18% size reduction for `armeabi` ABI
	- 30% size reduction for `armeabi-v7a` ABI
	- 22% size reduction for `x86` and `x86_64` ABIs
- removed `mips` and `mips64` native binaries for two reasons
	- code size optimisations did not work for those ABIs
	- no known Android device uses those ABIs

## 6.2.1
- fixed rotated camera preview on Panasonic A2 tablet

## 6.2.0
- fixed autofocus issue on US version of Samsung devices that received Android N update
- added support for scanning barcodes on SIM cards

## 6.1.0

- removed `RecognizerView` method `setInitialScanningPaused`. For achieving the same functionality, method `pauseScanning` should be used.
- added support for cancelling ongoing DirectAPI recognition call
- added support for Android 7 multi-window mode
- fixed autofocus bug on Huawei Honor 8
- fixed black camera on Motorola Moto Z
- made camera focusing more stable on some devices
    - _stable_ means less "jumpy" when searching for focused image

## 6.0.1
- fixed camera management on LG X Cam
- fixed crash on some devices when using `ImageListener`

## 6.0.0

- removed support for parsing US Driver's license barcodes
    - if you need USDL parsing feature, please upgrade to our [BlinkID product](https://github.com/BlinkID/blinkid-android)
    - you are still able to scan PDF417 barcodes using the [PDF417 recognizer](https://github.com/PDF417/pdf417-android#pdf417Recognizer), but it will only raw barcode contents, not the parsed license data
- _LibRecognizer.aar_ renamed to _LibPdf417Mobi.aar_
- _libBlinkBarcode.so_ renamed to _libPdf417Mobi.so_
- workaround for camera bug on some samsung devices
- migrated to libc++ native runtime and used clang from NDKr13b for building the native code
    - this enabled c++14 features which will help us yield much better performance in the future

## 5.5.2
- added support for Alberta (Canada) DL
- enabled reading of longer ITF barcodes

## 5.5.1
- improved autofocus support on SGS6 and SGS7
- fixed memory leak in RecognitionProcessCallback (https://github.com/PDF417/pdf417-android/issues/17)

## 5.5.0
- small camera fixes
- support for North Carolina driver licenses
- default value for `RecognitionSettings` option `allowMultipleScanResultsOnSingleImage` is now `true`, which means that it is by default possible to obtain multiple recognition results from the same image, produced by different recognizers 

## 5.4.1
- fixed autofocus bug on LG G4
- fixed blinking torch problem on SGS6, Nexus 5X, LG G4 and similar new devices that use camera2 API

## 5.4.0
- FailedDetectionMetadata, PointsDetectionMetadata and QuadDetectionMetadata have been replaced with DetectionMetadata which now holds a DetectorResult
    - DetectorResult is more flexible as it allows more different detection types to be added in future
- fixed several possible crashes in camera management
- fixed autofocus bug on LG devices when metering areas or non-default zoom level were set

## 5.3.0
- reconfigureRecognizers method now throws an error if phone does not have autofocus and at least one of new recognizers require it
- raw resources are now packed as assets
- fixed bug with `isScanningPaused` which sometimes returned bogus value and caused scanning to work even if initial scanning was set to be paused
- support for scanning custom camera frames via DirectAPI
- fixed bug on some devices causing it to never start scanning if device was not shaken
- improved Frame Quality Estimation on low-end devices (fixed regression introduced in v4.5.0)

## 5.2.0
- support detecting on activity flip event
- added RecognizerCompatibility to javadoc
- fixed NPE in BarcodeDetailedData

## 5.1.0
- improved performance of conversion of `Image` object into `Bitmap`
- fixed crash that could be caused by quickly restarting camera activity
- fixed bug in camera layout in certain aspect ratios of camera view
- fixed bug in handling `setMeteringAreas`
- `setMeteringAreas` now receives boolean indicating whether set areas should be rotated with device
- added support for specifying camera aspect mode from XML

## 5.0.0
- new API which is easier to understand, but is not backward compatible. Please check [README](README.md) and updated demo applications for more information.
- added support for Android 6.0 and it's runtime camera permissions
	- if using provided [_Pdf417ScanActivity_](https://pdf417.github.io/pdf417-android/com/microblink/activity/Pdf417ScanActivity.html), the logic behind asking user to give camera permission is handled internally
	- if integrating using custom UI, you are required to ask user to give you permission to use camera. To make this easier, we have provided a [_CameraPermissionManager_](https://pdf417.github.io/pdf417-android/com/microblink/util/CameraPermissionManager.html) class which does all heavylifting code about managing states when asking user for camera permission. Refer to demo apps to see how it is used.
- PDF417.mobi SDK now depends on appcompat-v7 library
- completely rewritten JNI bridge between native code and Java which resulted in big performance improvement
- fixed camera orientation bug on Nexus 5X
- Bitmap processed by DirectAPI is not recycled anymore after use
	- this gives you ability to reuse existing Bitmap
- fixed bug that caused rare irrational stop of scanning while keeping camera active
- in [RecognizerView](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html), there is now method [setInitialScanningPaused](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#setInitialScanningPaused(boolean)) which allows defining whether initial start of camera will prevent automatic start of scanning
	- this is useful if you want to display onboarding help over camera
- [pauseScanning](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#pauseScanning()) and [resumeScanning](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#resumeScanning(boolean)) methods are now counted, i.e. if you call `pauseScanning` twice, you also need to call `resumeScanning` twice to actually resume scanning
	- this is very useful if you have multiple onboarding views where each wants to pause scanning while it is being displayed and wants to resume scanning after it is dismissed
- **IMPORTANT** [onScanningDone](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/ScanResultListener.html#onScanningDone(com.microblink.recognizers.RecognitionResults)) does not automatically pause scanning anymore. Scanning is resumed automatically immediately after `onScanningDone` method ends.
	- if you want to prevent such behaviour, you need to call `pauseScanning` in your implementation of `onScanningDone`
	- automatic resume does not reset internal scanning state. If you need that, call [resetRecognitionState](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#resetRecognitionState()) in your implementation of `onScanningDone`.
- fixed ZXing recognizer not obeying scanning region when recognising 1D barcodes in portrait orientation


## 4.7.0
- fixed autofocus issue on devices that do not support continuous autofocus
- support for defining camera video resolution preset
	- to define video resolution preset via Intent, use `Pdf417ScanActivity.EXTRAS_CAMERA_VIDEO_PRESET`
	- to define video resolution preset on `RecognizerView`, use method `setVideoResolutionPreset`

## 4.6.0
- improved USDL barcode parsing
	- better handling of FullName, FullAddress, Height and Weight of cardholder
- fixed race condition causing memory leak or rare crashes
- fixed `NullPointerException` in `BaseCameraView.dispatchTouchEvent`
- fixed bug that caused returning scan result from old video frame
- fixed `NullPointerException` in camera2 management
- fixed rare race condition in gesture recognizer
- fixed segmentation fault on recognizer reconfiguration operation
- fixed freeze when camera was being quickly turned on and off
- ensured `RecognizerView` lifecycle methods are called on UI thread
- ensure `onCameraPreviewStarted` is not called if camera is immediately closed after start before the call should have taken place
- ensure `onScanningDone` is not called after `RecognizerView` has been paused, even if it had result ready just before pausing
- added support for using _PDF417.mobi_ as camera capture API. To do that, implement following:
	- when using `RecognizerView` do not call `setRecognitionSettings` or call it with `null` or empty array
	- implement `ImageListener` interface and set the listener with `setImageListener`
	- as a reminder - you can process video frames obtained that way using DirectAPI method `recognizeImageWithSettings`
- added `Pdf417MobiDirectAPIDemo ` demo app that demonstrates how to perform scanning of [Android Bitmaps](https://developer.android.com/reference/android/graphics/Bitmap.html)
- all demo apps now use Maven integration method because it is much easier than importing AAR manually

## 4.5.2
- fixed segfault in USDL parser
- fixed parsing of some Canadian driver's licenses

## 4.5.1
- fixed NullPointerException when RecognizerSettings array element was `null`

## 4.5.0
- utilize Camera2 API on devices that support it to achieve much better frame grabbing performance
- new algorithm for determining which video frame is of good enough quality to be processed - it uses much less memory than last one and works in separate thread so it does not impact image recognition thread
	- this results in much better and faster scanning performance
- support for [defining camera metering areas](https://pdf417.github.io/pdf417-android/com/microblink/view/BaseCameraView.html#setMeteringAreas(com.microblink.geometry.Rectangle[])) that will camera use to perform focus, exposure and white balance measurement.
- bitmaps can now be processed while [RecognizerView](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html) is active using method [recognizeBitmap](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#recognizeBitmap(android.graphics.Bitmap, com.microblink.hardware.orientation.Orientation, com.microblink.view.recognition.ScanResultListener))
- removed method `resumeScanningWithoutStateReset` - method [resumeScanning](https://pdf417.github.io/pdf417-android/com/microblink/view/recognition/RecognizerView.html#resumeScanning(boolean)) now receives `boolean` indicating whether internal state should be reset
- by default, null quiet zone is now set to `true` in US Driver's License recognizer
- fixed rare crash when scanning PDF417 barcodes
- added support for returning location of detected PDF417 barcode with [getPositionOnImage](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/barcode/pdf417/Pdf417ScanResult.html#getPositionOnImage())
- in [ZXingRecognizer](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/barcode/zxing/ZXingRecognizerSettings.html) added [option](https://pdf417.github.io/pdf417-android/com/microblink/recognizers/barcode/zxing/ZXingRecognizerSettings.html#setSlowThoroughScan(boolean)) to use slower, but more thorough scan algorithm

## 4.4.3
- fixed extraction of street address and customer name from some US Driver Licenses
- fixed crash when scanning specific PDF417 barcodes

## 4.4.2
- fixed camera orientation bug on Samsung GT-S5570I (regression from version 3.3.0)

## 4.4.1
- fixed crash in Direct API when recognizer was terminated in the middle of recognition process

## 4.4.0
- added support for arm64, x86_64, mips and mip64 processor architectures

## 4.3.3
- fixed bug in ITF barcode decoder

## 4.3.2
- fixed camera orientation detection when RecognizerView is not initialized with Activity context

## 4.3.1
- fixed decoding error of certain PDF417 barcodes

## 4.3.0
- support for defining camera aspect mode via intent
- removed autoscale setting from USDL and PDF417 recognizers - autoscale is now always used in those recognizers

## 4.2.0
- ability to control camera zoom level
- fixed NPE when null SuccessCallback was given

## 4.1.1
- improved parsing of some problematic US Driver's Licence barcodes

## 4.1.0
- added new format of licence key
- support for magnetic stripe standard in USDL barcodes

## 4.0.1
- added support for Mississippi driver's licenses

## 4.0.0
- completely rewritten API for defining settings and obtaining results which is easier to use; see README for details
- optimized library size - native library is now 2 MB per platform smaller than before
- `Pdf417MobiView` renamed to `RecognizerView`
- added support for Nvidia Tegra 2 devices, whilst preserving NEON acceleration on other ARMv7 devices
- Android studio and gradle are now recommended - demo apps are now only provided for Android studio, Eclipse is supported only via documentation, see README for details
- added direct API for direct recognition of Bitmap images, without the need for camera

## 3.5.0
- Fixed very obscure issue with decoding pdf417 barcodes
- fixed crash with missing AutofocusFail string
- AutofocusListener now has callbacks onAutofocusStarted and onAutofocusStopped that allow you to draw focus animation on your custom UI

## 3.4.2
- fixed camera orientation bug for Samsung Galaxy Ace GT-S5830M

## 3.4.1
- support for tablets that have inverse landscape natural display orientation (currently only Sprint Optik 2 supported)

## 3.4.0
- support for aspect fill camera mode, check README for instructions

## 3.3.1
- fixed race condition at camera initialization on some Android devices (HTC One M8)

## 3.3.0
- added support for embedding Pdf417MobiView into custom activity 
- Pdf417MobiView is now just a camera view that can be embedded anywhere, you just need to pass activity's lifecycle events to it, see README for more details

## 3.2.0
- fixed crash on Samsung Galaxy Tab 10.1 and other Tegra 2 SoC (i.e. removed NEON support) - NOTE: this fix is temporary, soon we will drop support for Tegra 2 and other ARMv7-based SoCs that do not support NEON
- removed progress bar callback and simplified creation of custom UI

## 3.1.0
- added API for parsing US Driver's License data from PDF417 barcodes
- PDF417 scanning improvements
- bugfixes

## 3.0.1
- fixed focusing issue on HTC One V

## 3.0.0
- support for obtaining multiple scan results from single camera frame
- API change:
    - `onScanningDone` method in BaseBarcodeActivity now receives list of scanning results instead of single scanning result
    - this list can have zero, one or more scan results
    - if multiple barcode recognizers are turned on and `shouldAllowMultipleScanResultsOnSingleImage` method of `Pdf417MobiSettings` returns true, list can have multiple scan data, otherwise it will have at most one element (similar behaviour as before)
- new key has been added for retrieving list of recognised objects via intent: `BaseBarcodeActivity.EXTRAS_RESULT_LIST`
    - you can obtain the list with following snippet:

            ArrayList<Pdf417MobiScanData> dataList = getIntent().getExtras().getParcelableArrayList(BaseBarcodeActivity.EXTRAS_RESULT_LIST);
            
- by default, taking screenshots of camera activity is now allowed (until now it was disabled by default and could be overriden with custom implementation of `onConfigureWindow`)
- added support for x86 devices

## 2.6.2
- support for entering premium license key that can be used with multiple application package names

## 2.6.1
- added support for changing camera activity's background color

## 2.6.0
- new format of license key
- fixed race condition in focus management
- improved support for Code128 and Code39 barcodes

## 2.5.3
- fixed camera orientation bug on Samsung Galaxy Ace GT-S5830i

## 2.5.2
- fixed ProGuard compile warnings

## 2.5.1
- various bugfixes and performance improvements, especially when scanning uncertain PDF417 barcodes
- non UTF-8 string JNI transfer support:
	- if string is not UTF-8, instead of showing "Invalid UTF-8 string", part of the string that can be decoded will be decoded as ASCII bytes

## 2.5.0
- added support for setting scanning region

## 2.4.0
- initial support for Data Matrix and Aztec 2D barcodes

## 2.3.1
- fixed bug which caused camera on Nexus 7 not being detected
- fixed wrong detection location drawing when front facing camera is being used

## 2.3.0
- various bugfixes
- added support for scanning inverted barcodes

## 2.2.0
- very important bugfix release
- this version does not use R class for referencing resources from within binary jar - this means that from now on PDF417.mobi library can be repacked into another android library project; this also means that PhoneGap integration will now be easier and AppCelerator Titanium intergration has been made possible

## 2.1.0
- support for having title bar and status bar in camera activity
- support for custom activity window configuration (added overridable method `onConfigureWindow` to `BaseCameraActivity`)
- support for library license keys (key that supports multiple application package names)
- Added option to scan barcodes which don't have quiet zone around them
- Improved scanning algorithm
- fixed crash on Android 4.4 ART when trying to show content of barcode that cannot be read as UTF8 string

## 2.0.1
- support for having camera activity in landscape mode - extended support to Android 2.1 devices (fixing Samsung Galaxy Mini issues)

## 2.0.0
- new simpler API
- better support for custom user interface
- ability to scan multiple barcodes without closing camera activity (from custom UI only)
- added torch control button to default PDF417.mobi demo app
- PDF417CustomUIDemo contains example of controlling camera torch and example of scanning multiple barcodes without closing camera activity
- documentation updates
- support for scanning damaged and non-standard PDF417 barcodes - if barcode checksum is not correct, returned result is marked as "uncertain"

## 1.6.0
- autofocus and flash features are now optional (play store app now supports much more devices)

## 1.3.1
- better and faster barcode detection
- faster detection and decoding performance
- additional bugfixes

## 1.3.0
- adding support for front facing cameras
- fixed memory leaks

## 1.2.1
- fixed race condition in detection display

## 1.2.0
- support for micro PDF417 standard

## 1.1.0
- support for reading raw barcode data
- support for reading damaged and non-standard PDF417 barcodes

## 1.0.1
- support for case insensitive package matching when checking for license key
- added release notes

## 1.0.0
- added API for enabling 1D barcode scanning
- scan popup can be now removed even when using free non-commercial license

## 0.9.0
- support for reading PDF417 with error correction 0 and 1
- better support for Sony Xperia S
- fixed 'invalid license key' bug when resuming application from background

## 0.8.0
- Initial release
- Scanning support for pdf417 and QR code