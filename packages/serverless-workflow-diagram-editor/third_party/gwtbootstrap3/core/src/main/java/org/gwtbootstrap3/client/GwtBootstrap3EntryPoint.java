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

import elemental2.core.Reflect;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLScriptElement;
import io.crysknife.ui.common.client.injectors.ScriptInjector;
import jsinterop.base.Js;

/**
 * Provides script injection for jQuery and boostrap if they aren't already loaded.
 *
 * @author Sven Jacobs
 * @author Steven Jardine
 */
public class GwtBootstrap3EntryPoint {

    /**
     * Check to see if Bootstrap is loaded already.
     *
     * @return true is Bootstrap loaded, false otherwise.
     */
    private boolean isBootstrapLoaded() {
        return Reflect.get(Reflect.get(Reflect.get(DomGlobal.window,"jQuery"),"fn"),"emulateTransitionEnd") != Js.undefined();
    }

    /**
     * Check to see if jQuery is loaded already
     *
     * @return true is jQuery is loaded, false otherwise
     */
    private boolean isjQueryLoaded() {
        return Reflect.get(DomGlobal.window, "jQuery") != Js.undefined();
    }

    /** {@inheritDoc} */
    public void onModuleLoad() {

        DomGlobal.console.log("??? 1: " + Reflect.get(DomGlobal.window, "jQuery"));
        DomGlobal.console.log("??? 2: " + Reflect.get(DomGlobal.window, "jQuery") == Js.undefined());

        DomGlobal.console.log("GwtBootstrap3EntryPoint.onModuleLoad 1: " + isjQueryLoaded());

        if (!isjQueryLoaded()) {
            HTMLScriptElement script = (HTMLScriptElement) DomGlobal.document.createElement("script");
            script.type = "text/javascript";
            script.src = "https://code.jquery.com/jquery-1.12.4.min.js";
            DomGlobal.document.head.appendChild(script);
        }

        if(isBootstrapLoaded()){
            ScriptInjector.fromString(GwtBootstrap3ClientBundle.INSTANCE.bootstrap().getText(), new ScriptInjector.Callback() {
                @Override
                public void accept(HTMLScriptElement script) {
                    DomGlobal.console.log("ZZZz");
                }
            }).setWindow(ScriptInjector.TOP_WINDOW).inject();
        }
    }

}
