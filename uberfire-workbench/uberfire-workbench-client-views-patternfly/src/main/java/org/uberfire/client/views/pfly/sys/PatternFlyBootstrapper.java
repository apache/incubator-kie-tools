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

import com.google.gwt.core.client.ScriptInjector;
import org.gwtbootstrap3.client.GwtBootstrap3ClientBundle;

/**
 * Utilities for ensuring the PatternFly/BS3 system is working early enough that the app can start correctly.
 */
public class PatternFlyBootstrapper {

    /**
     * Uses GWT's ScriptInjector to put jQuery in the page if it isn't already. All Errai IOC beans that rely on
     * GWTBootstrap 3 widgets should call this before creating their first such widget.
     */
    public static void ensurejQueryIsAvailable() {
        if ( !isjQueryLoaded() ) {
            ScriptInjector.fromString( GwtBootstrap3ClientBundle.INSTANCE.jQuery().getText() )
                    .setWindow( ScriptInjector.TOP_WINDOW )
                    .inject();
        }
    }

    public static void ensurePrettifyIsAvailable() {
        if ( !isPrettifyLoaded() ) {
            ScriptInjector.fromString( PatternFlyClientBundle.INSTANCE.prettify().getText() )
                    .setWindow( ScriptInjector.TOP_WINDOW )
                    .inject();
        }
    }

    /**
     * Checks to see if jQuery is already present.
     * @return true is jQuery is loaded, false otherwise.
     */
    private static native boolean isjQueryLoaded() /*-{
        return (typeof $wnd['jQuery'] !== 'undefined');
    }-*/;

    /**
     * Checks to see if Prettify is already present.
     * @return true is Prettify is loaded, false otherwise.
     */
    private static native boolean isPrettifyLoaded() /*-{
        return (typeof $wnd['prettyPrint'] !== 'undefined');
    }-*/;
}

