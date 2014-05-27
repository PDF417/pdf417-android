# Release notes

## 3.0.0
- support for obtaining multiple scan results from single camera frame
- API change:
    - `onScanningDone` method in BaseBarcodeActivity now receives list of scanning results instead of single scanning result
    - this list can have zero, one or more scan results
    - if multiple barcode recognizers are turned on and `shouldAllowMultipleScanResultsOnSingleImage` method of `Pdf417MobiSettings` returns true, list can have multiple scan data, otherwise it will have at most one element (similar behaviour as before)
- new key has been added for retrieving list of recognised objects via intent: `BaseBarcodeActivity.EXTRAS_RESULT_LIST`
    - you can obtain the list with following snippet:

            ArrayList<Pdf417MobiScanData> dataList = getIntent().getExtras().getParcelableArrayList(BaseBarcodeActivity.EXTRAS_RESULT_LIST);

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
- fixed 'invalid license key' bug when resumin application from background

## 0.8.0
- Initial release
- Scanning support for pdf417 and QR code