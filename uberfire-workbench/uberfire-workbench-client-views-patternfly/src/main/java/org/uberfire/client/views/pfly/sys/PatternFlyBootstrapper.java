/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.sys;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import org.gwtbootstrap3.client.GwtBootstrap3ClientBundle;
import org.uberfire.client.views.pfly.monaco.MonacoEditorInitializer;

import static org.uberfire.client.views.pfly.sys.MomentUtils.setMomentLocale;

/**
 * Utilities for ensuring the PatternFly/BS3 system is working early enough that the app can start correctly.
 */
public class PatternFlyBootstrapper {

    private static final String MONACO_EDITOR_BASE_PATH = "monaco-editor/dev";

    private static boolean isPrettifyLoaded = false;

    private static boolean isBootstrapSelectLoaded = false;

    private static boolean isBootstrapDateRangePickerLoaded = false;

    private static boolean isMomentLoaded = false;

    private static boolean isMomentTimeZoneLoaded = false;

    private static boolean isPatternFlyLoaded = false;

    private static boolean isD3Loaded = false;

    private static boolean isJQueryUILoaded = false;

    private static boolean isMonacoEditorLoaderLoaded = false;

    /**
     * Uses GWT's ScriptInjector to put jQuery in the page if it isn't already. All Errai IOC beans that rely on
     * GWTBootstrap 3 widgets should call this before creating their first such widget.
     */
    public static void ensurejQueryIsAvailable() {
        if (!isjQueryLoaded()) {
            ScriptInjector.fromString(GwtBootstrap3ClientBundle.INSTANCE.jQuery().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
        }
    }

    public static void ensurePrettifyIsAvailable() {
        if (!isPrettifyLoaded) {
            ScriptInjector.fromString(PatternFlyClientBundle.INSTANCE.prettify().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
            isPrettifyLoaded = true;
        }
    }

    public static void ensureBootstrapSelectIsAvailable() {
        if (!isBootstrapSelectLoaded) {
            ScriptInjector.fromString(PatternFlyClientBundle.INSTANCE.bootstrapSelect().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
            isBootstrapSelectLoaded = true;
        }
    }

    public static void ensurePatternFlyIsAvailable() {
        ensurejQueryIsAvailable();
        ensureBootstrapSelectIsAvailable();
        if (!isPatternFlyLoaded) {
            ScriptInjector.fromString(PatternFlyClientBundle.INSTANCE.patternFly().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
            isPatternFlyLoaded = true;
        }
    }

    public static void ensureMomentIsAvailable() {
        if (!isMomentLoaded) {
            ScriptInjector.fromString(PatternFlyClientBundle.INSTANCE.moment().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
            isMomentLoaded = true;
        }
        setMomentLocale();
    }

    public static void ensureMomentTimeZoneIsAvailable() {
        if (!isMomentTimeZoneLoaded) {
            ScriptInjector.fromString(PatternFlyClientBundle.INSTANCE.momentTimeZone().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
            isMomentTimeZoneLoaded = true;
        }
    }

    public static void ensureBootstrapDateRangePickerIsAvailable() {
        ensureMomentIsAvailable();
        if (!isBootstrapDateRangePickerLoaded) {
            ScriptInjector.fromString(PatternFlyClientBundle.INSTANCE.bootstrapDateRangePicker().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
            isBootstrapDateRangePickerLoaded = true;
        }
    }

    public static void ensureD3IsAvailable() {
        if (!isD3Loaded) {
            ScriptInjector.fromString(PatternFlyClientBundle.INSTANCE.d3().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
            isD3Loaded = true;
        }
    }

    public static void ensureJQueryUIIsAvailable() {
        if (!isJQueryUILoaded) {
            ScriptInjector.fromString(PatternFlyClientBundle.INSTANCE.jQueryUI().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
            isJQueryUILoaded = true;
        }
    }

    public static void ensureMonacoEditorLoaderIsAvailable() {
        if (!isMonacoEditorLoaderLoaded) {

            final String monacoAbsolutePath = GWT.getModuleBaseURL() + MONACO_EDITOR_BASE_PATH;
            final String amdLoaderScript = PatternFlyClientBundle.INSTANCE.monacoAMDLoader().getText();

            ScriptInjector.fromString(enclosureByMonacoAMDLoaderNamespace(monacoAbsolutePath, amdLoaderScript))
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();

            isMonacoEditorLoaderLoaded = true;
        }
    }

    /**
     * The global scope already has other libraries occupying namespaces that Monaco cannot override.
     * Thus, this method encloses Monaco loader functions into the '__MONACO_AMD_LOADER__' namespace, and
     * the {@link MonacoEditorInitializer} uses '__MONACO_AMD_LOADER__' to correctly initialize Monaco modules.
     */
    private static String enclosureByMonacoAMDLoaderNamespace(final String baseUrlPath,
                                                              final String script) {

        return "(new function() { this.require = { baseUrl: '" + baseUrlPath + "' }; "
                + script
                + " window.__MONACO_AMD_LOADER__ = _amdLoaderGlobal });";
    }

    /**
     * Checks to see if jQuery is already present.
     *
     * @return true is jQuery is loaded, false otherwise.
     */
    private static native boolean isjQueryLoaded() /*-{
        return (typeof $wnd['jQuery'] !== 'undefined');
    }-*/;
}
