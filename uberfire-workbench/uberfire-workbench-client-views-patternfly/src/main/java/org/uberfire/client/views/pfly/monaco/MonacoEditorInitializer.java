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

package org.uberfire.client.views.pfly.monaco;

import java.util.function.Consumer;

import com.google.gwt.core.client.JsArrayString;
import org.uberfire.client.views.pfly.monaco.jsinterop.Monaco;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoLoader;

import static com.google.gwt.core.client.JavaScriptObject.createArray;

/**
 * {@link MonacoEditorInitializer} initializes Monaco modules by temporarily switching the functions
 * '__MONACO_AMD_LOADER__.define' and '__MONACO_AMD_LOADER__.require' from Monaco namespace to the global scope.
 * The global scope already has other libraries occupying namespaces that Monaco cannot override, so this class
 * temporarily uses the global scope and gives it back to the legacy libraries, when Monaco finishes its work.
 */
public class MonacoEditorInitializer {

    static final String VS_EDITOR_EDITOR_MAIN_MODULE = "vs/editor/editor.main";

    public void require(final Consumer<Monaco> monacoConsumer) {

        switchAMDLoaderFromDefaultToMonaco();

        require(monacoModule(), monaco -> {
            monacoConsumer.accept(monaco);
            switchAMDLoaderFromMonacoToDefault();
        });
    }

    void require(final JsArrayString modules,
                 final MonacoLoader.CallbackFunction callback) {
        MonacoLoader.require(modules, callback);
    }

    void switchAMDLoaderFromDefaultToMonaco() {
        nativeSwitchAMDLoaderFromDefaultToMonaco();
    }

    void switchAMDLoaderFromMonacoToDefault() {
        nativeSwitchAMDLoaderFromMonacoToDefault();
    }

    JsArrayString monacoModule() {
        final JsArrayString modules = makeJsArrayString();
        modules.push(VS_EDITOR_EDITOR_MAIN_MODULE);
        return modules;
    }

    JsArrayString makeJsArrayString() {
        return (JsArrayString) createArray();
    }

    private native void nativeSwitchAMDLoaderFromDefaultToMonaco() /*-{

        // Store current definition of 'define' and 'require'
        $wnd.__GLOBAL_DEFINE__ = $wnd.define;
        $wnd.__GLOBAL_REQUIRE__ = $wnd.require;

        // Set Monaco AMD Loader definition of 'define' and 'require'
        $wnd.define = $wnd.__MONACO_AMD_LOADER__.define;
        $wnd.require = $wnd.__MONACO_AMD_LOADER__.require;
    }-*/;

    private native void nativeSwitchAMDLoaderFromMonacoToDefault() /*-{
        // Reset the definition of 'define' and 'require'
        $wnd.define = $wnd.__GLOBAL_DEFINE__;
        $wnd.require = $wnd.__GLOBAL_REQUIRE__;
    }-*/;
}
