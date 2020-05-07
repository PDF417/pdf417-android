#!/bin/bash

pushd `dirname $0` > /dev/null
SCRIPTPATH=`pwd -P`
popd > /dev/null

$SCRIPTPATH/script/size_report.sh "PDF417.mobi" $SCRIPTPATH/../Pdf417MobiSample pdf417MobiSample $SCRIPTPATH/sdk_size_report.md
