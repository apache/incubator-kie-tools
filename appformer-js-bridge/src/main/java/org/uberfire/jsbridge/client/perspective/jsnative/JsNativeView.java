/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client.perspective.jsnative;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JsNativeView {

    private final JavaScriptObject self;
    private final String partsFieldName;
    private final String panelsFieldName;

    public JsNativeView(final JavaScriptObject self, final String partsFieldName, final String panelsFieldName) {
        this.self = self;
        this.partsFieldName = partsFieldName;
        this.panelsFieldName = panelsFieldName;
    }

    public List<JsNativePart> parts() {

        final List<JsNativePart> parts = new ArrayList<>();

        final JsArray<JavaScriptObject> jsParts = nativeParts(partsFieldName);
        for (int i = 0; i < jsParts.length(); i++) {
            parts.add(new JsNativePart(jsParts.get(i)));
        }

        return parts;
    }

    public List<JsNativePanel> panels() {

        final List<JsNativePanel> panels = new ArrayList<>();

        final JsArray<JavaScriptObject> jsPanels = nativePanels(panelsFieldName);
        for (int i = 0; i < jsPanels.length(); i++) {
            panels.add(new JsNativePanel(jsPanels.get(i)));
        }

        return panels;
    }

    private native JsArray<JavaScriptObject> nativeParts(final String partsField) /*-{
        return this.@org.uberfire.jsbridge.client.perspective.jsnative.JsNativeView::self[partsField];
    }-*/;

    private native JsArray<JavaScriptObject> nativePanels(final String panelsField) /*-{
        return this.@org.uberfire.jsbridge.client.perspective.jsnative.JsNativeView::self[panelsField];
    }-*/;
}
