/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client.perspective.jsnative;

import com.google.gwt.core.client.JavaScriptObject;
import org.uberfire.workbench.model.ContextDisplayMode;

public class JsNativeContextDisplay {

    private final JavaScriptObject self;
    private final String displayInfoFieldName;

    public JsNativeContextDisplay(final JavaScriptObject self,
                                  final String displayInfoFieldName) {
        this.self = self;
        this.displayInfoFieldName = displayInfoFieldName;
    }

    public ContextDisplayMode mode() {
        return ContextDisplayMode.valueOf(contextDisplayModeString(displayInfoFieldName));
    }

    public String contextId() {
        return contextId(displayInfoFieldName);
    }

    private native String contextId(final String displayInfoField)   /*-{
        var contextDisplay = this.@org.uberfire.jsbridge.client.perspective.jsnative.JsNativeContextDisplay::self[displayInfoField];
        return contextDisplay && contextDisplay["contextId"];
    }-*/;

    private native String contextDisplayModeString(final String displayInfoField)   /*-{
        var contextDisplay = this.@org.uberfire.jsbridge.client.perspective.jsnative.JsNativeContextDisplay::self[displayInfoField];
        return contextDisplay && contextDisplay["contextDisplayMode"];
    }-*/;
}
