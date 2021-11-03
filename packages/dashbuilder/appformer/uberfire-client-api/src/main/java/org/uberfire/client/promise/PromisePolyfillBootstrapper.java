/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.promise;

import com.google.gwt.core.client.ScriptInjector;

class PromisePolyfillBootstrapper {

    static void ensurePromiseApiIsAvailable() {
        if (!isPromiseApiAvailable()) {
            ScriptInjector.fromString(PromisePolyfillClientBundle.INSTANCE.promisePolyfill().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
        }
    }

    /**
     * Checks to see if Promise is already present.
     * @return true is Promise is loaded, false otherwise.
     */
    private static native boolean isPromiseApiAvailable() /*-{
        return (typeof $wnd['Promise'] !== 'undefined');
    }-*/;
}
