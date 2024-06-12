#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

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
