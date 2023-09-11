package org.gwtbootstrap3.client;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2015 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import io.crysknife.ui.common.client.injectors.ScriptInjector;
import io.crysknife.ui.common.client.injectors.StyleInjector;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * Provides script injection for jQuery and boostrap if they aren't already loaded.
 *
 * @author Sven Jacobs
 * @author Steven Jardine
 */
@JsType
public class GwtBootstrap3EntryPoint {

    /**
     * Check to see if Bootstrap is loaded already.
     *
     * @return true is Bootstrap loaded, false otherwise.
     */
    //@JsMethod(namespace = JsPackage.GLOBAL)
    //private static native boolean isBootstrapLoaded();
    //{
    //    return Reflect.get(Reflect.get(Reflect.get(DomGlobal.window,"jQuery"),"fn"),"emulateTransitionEnd") != Js.undefined();
    //}

    /**
     * Check to see if jQuery is loaded already
     *
     * @return true is jQuery is loaded, false otherwise
     */
    private boolean isjQueryLoaded() {
        return Js.global().has("jQuery");
    }

    //TODO
    /** {@inheritDoc} */
    public void onModuleLoad() {
        StyleInjector.fromString(GwtBootstrap3ClientBundle.INSTANCE.bootstrap_css().getText()).inject();


        ScriptInjector.fromString(GwtBootstrap3ClientBundle.INSTANCE.gwtBootstrap3().getText())
                .setWindow(ScriptInjector.TOP_WINDOW)
                .inject();

        ScriptInjector.fromString(GwtBootstrap3ClientBundle.INSTANCE.jQuery().getText())
                .setWindow(ScriptInjector.TOP_WINDOW)
                .inject();

        if (!isjQueryLoaded()) {
        }

        //if (!isBootstrapLoaded()) {
            ScriptInjector.fromString(GwtBootstrap3ClientBundle.INSTANCE.bootstrap().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
        //}
    }

}
