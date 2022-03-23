/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.autolayout.elkjs;

import com.google.gwt.core.client.ScriptInjector;

public class ELKWrapper {

    public void injectScript() {
        if (!isInjected()) {
           ScriptInjector.fromString(JsClientBundle.INSTANCE.elk().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();
        }
    }

    //TODO Migrate from JSNI to Native
    private native final boolean isInjected() /*-{
        if (!(typeof $wnd.ELK === "undefined") && !(null===$wnd.ELK)) {
            return true;
        }
        return false;
    }-*/;

}
