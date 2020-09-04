/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.monaco.jsinterop;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import elemental2.core.JsObject;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class MonacoStandaloneCodeEditor {

    public JsObject _contentWidgets;

    public native void focus();

    public native void layout(JavaScriptObject dimensions);

    public native void dispose();

    public native String getValue();

    public native void trigger(final String source,
                               final String handlerId);

    public native void setValue(final String value);

    public native void onKeyDown(final CallbackFunction callback);

    public native void onDidBlurEditorWidget(final CallbackFunction callback);

    @JsOverlay
    public final boolean isSuggestWidgetVisible() {
        return MonacoStandaloneCodeEditorHelper.isSuggestWidgetVisible(this);
    }

    @JsFunction
    public interface CallbackFunction {

        void call(final NativeEvent event);
    }
}
