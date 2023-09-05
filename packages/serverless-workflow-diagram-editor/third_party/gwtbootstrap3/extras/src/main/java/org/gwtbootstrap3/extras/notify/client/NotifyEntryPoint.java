package org.gwtbootstrap3.extras.notify.client;

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
import io.crysknife.ui.common.client.injectors.ScriptInjector;

public class NotifyEntryPoint {

    public void onModuleLoad() {
        if (!isNotifyLoaded()) {
            ScriptInjector.fromString(NotifyClientBundle.INSTANCE.notifyJS().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();
        }
    }

    /**
     * Check if notify is already loaded.
     *
     * @return <code>true</code> if notify is loaded, <code>false</code> otherwise
     */
    private boolean isNotifyLoaded() {
        return Reflect.has(DomGlobal.window, "jQuery") && Reflect.has(Reflect.get(DomGlobal.window, "jQuery"), "notify");
    }
}
