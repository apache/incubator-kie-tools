#!/bin/bash


APP="target/Extended Services.app"
CONTENTS=$APP/Contents
MACOS=$CONTENTS/MacOs
RESOURCES=$CONTENTS/Resources
APPLICATIONS=target/Applications

rm -rf target

mkdir -p "$CONTENTS"
mkdir "$MACOS"
mkdir "$RESOURCES"

cp ../../dist/darwin/kie_sandbox_extended_services "$MACOS"/kogito
cp src/Info.plist "$CONTENTS"
cp src/KieLogo.png "$RESOURCES"
ln -s /Applications $APPLICATIONS

hdiutil create /tmp/tmp.dmg -ov -volname Kogito -fs HFS+ -srcfolder "target"
hdiutil convert /tmp/tmp.dmg -format UDZO -o ../../dist/darwin/Kogito.dmg

rm $APPLICATIONS
rm -rf target
