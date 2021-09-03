#! /bin/bash


APP="target/KIE Tooling Extended Services.app"
CONTENTS=$APP/Contents
MACOS=$CONTENTS/MacOs
RESOURCES=$CONTENTS/Resources
APPLICATIONS=target/Applications

rm -rf target
rm -rf Kogito.dmg

mkdir -p "$CONTENTS"
mkdir "$MACOS"
mkdir "$RESOURCES"

cp ../../build/darwin/kie_tooling_extended_services "$MACOS"/kogito
cp src/Info.plist "$CONTENTS"
cp src/KieLogo.png "$RESOURCES"
ln -s /Applications $APPLICATIONS

hdiutil create /tmp/tmp.dmg -ov -volname Kogito -fs HFS+ -srcfolder "target" 
hdiutil convert /tmp/tmp.dmg -format UDZO -o target/Kogito.dmg
