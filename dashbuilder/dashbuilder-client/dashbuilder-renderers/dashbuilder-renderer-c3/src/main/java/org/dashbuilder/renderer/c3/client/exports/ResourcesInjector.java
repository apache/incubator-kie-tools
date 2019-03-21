/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.c3.client.exports;

import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;

import com.google.gwt.core.client.ScriptInjector;

public class ResourcesInjector {

    static boolean c3Injected;
    static boolean d3geoprojectionInjected;


    public static void ensureC3Injected() {
        if (!c3Injected) {
            injectC3Resources();
            c3Injected = true;
        }
    }
    
    public static void ensureD3GeoProjectionInjected() {
        if (!d3geoprojectionInjected) {
            injectD3GeoProjectionResources();
            d3geoprojectionInjected = true;
        }
    }

    private static void injectC3Resources() {
        PatternFlyBootstrapper.ensureD3IsAvailable();
        ScriptInjector.fromString(NativeLibraryResources.INSTANCE.c3js().getText())
                        .setWindow(ScriptInjector.TOP_WINDOW)
                        .inject();
    }
    
    private static void injectD3GeoProjectionResources() {
        PatternFlyBootstrapper.ensureD3IsAvailable();
        ScriptInjector.fromString(NativeLibraryResources.INSTANCE.d3geoprojectionjs().getText())
                        .setWindow(ScriptInjector.TOP_WINDOW)
                        .inject();
    }

}