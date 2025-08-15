#!/bin/sh
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

DOWNLOADS="
jquery-3.7.1.min.js=https://code.jquery.com/jquery-3.7.1.min.js
jquery-migrate-3.4.1.min.js=https://code.jquery.com/jquery-migrate-3.4.1.min.js
"

FILES="
jquery-3.7.1.min.js
jquery-migrate-3.4.1.min.js
../../kie-wb-common-stunner/kie-wb-common-stunner-client/kie-wb-common-stunner-lienzo/src/main/resources/org/kie/workbench/common/stunner/client/lienzo/resources/js/bootstrap.min.js.noproc
../../kie-wb-common-stunner/kie-wb-common-stunner-client/kie-wb-common-stunner-lienzo/src/main/resources/org/kie/workbench/common/stunner/client/lienzo/resources/js/gwtbootstrap3.js.noproc
../../kie-wb-common-stunner/kie-wb-common-stunner-client/kie-wb-common-stunner-lienzo/src/main/resources/org/kie/workbench/common/stunner/client/lienzo/resources/js/bootstrap-select.min.js.noproc
../../kie-wb-common-stunner/kie-wb-common-stunner-client/kie-wb-common-stunner-lienzo/src/main/resources/org/kie/workbench/common/stunner/client/lienzo/resources/js/patternfly.min.js.noproc
../../third_party/gwtbootstrap3/extras/src/main/java/org/gwtbootstrap3/extras/notify/client/resource/js/bootstrap-notify-3.1.3.min.cache
../../uberfire-extensions/uberfire-commons-editor/uberfire-commons-editor-client/src/main/resources/org/uberfire/ext/editor/commons/client/file/exports/js/canvas2svg.js.back
../../uberfire-extensions/uberfire-commons-editor/uberfire-commons-editor-client/src/main/resources/org/uberfire/ext/editor/commons/client/file/exports/js/FileSaver.min.js.back
target/sw-editor-kogito-app/org.kie.workbench.common.stunner.sw.KogitoSWEditor/org.kie.workbench.common.stunner.sw.KogitoSWEditor.js
"


OUTPUT_FILE="merged.js"

DEST_DIR="target/sw-editor-kogito-app/org.kie.workbench.common.stunner.sw.KogitoSWEditor"
DEST_FILE="$DEST_DIR/org.kie.workbench.common.stunner.sw.KogitoSWEditor.js"

[ -f "$OUTPUT_FILE" ] && rm "$OUTPUT_FILE"


> "$OUTPUT_FILE"

for entry in $DOWNLOADS; do
    name="${entry%%=*}"
    url="${entry#*=}"
    echo "Downloading $name ..."
    curl -L -o "$name" "$url"
done

for file in $FILES; do
    if [ -f "$file" ]; then
        cat "$file" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
    else
        echo "File not found: $file" >&2
    fi
done

mkdir -p "$DEST_DIR"

cp "$OUTPUT_FILE" "$DEST_FILE"

rm "$OUTPUT_FILE"

for entry in $DOWNLOADS; do
    name="${entry%%=*}"
    rm -f "$name"
done

echo "Merged file copied to $DEST_FILE and temporary file removed."
