#! /bin/bash


APP="target/Kogito DMN Runner.app"
CONTENTS=$APP/Contents
MACOS=$CONTENTS/MacOs
RESOURCES=$CONTENTS/Resources
APPLICATIONS=target/Applications

rm -rf target
rm -rf Kogito.dmg

mkdir -p "$CONTENTS"
mkdir "$MACOS"
mkdir "$RESOURCES"

cp ../../build/darwin/dmn_runner "$MACOS"/kogito
cp src/Info.plist "$CONTENTS"
cp src/KogitoLogo.png "$RESOURCES"
ln -s /Applications $APPLICATIONS

hdiutil create /tmp/tmp.dmg -ov -volname Kogito -fs HFS+ -srcfolder "target" 
hdiutil convert /tmp/tmp.dmg -format UDZO -o target/Kogito.dmg
