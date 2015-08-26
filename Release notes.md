# Release notes

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