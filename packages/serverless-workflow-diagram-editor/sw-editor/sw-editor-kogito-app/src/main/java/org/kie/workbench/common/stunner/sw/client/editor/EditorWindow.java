/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.sw.client.editor;

import elemental2.core.Global;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

// TODO: This is temporal until tooling integration?
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class EditorWindow {

    @JsOverlay
    public static void updateContent(String raw) {
        // Prettify the json content.
        Object parsed = Global.JSON.parse(raw);
        String pretty = Global.JSON.stringify(parsed, (key, value) -> {
            if (null == value) {
                return Global.undefined;
            }
            return value;
        }, 2);
        // Change editor text (area).
        onContentChanged(pretty);
    }

    public static native void onContentChanged(String raw);
}
