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

package org.uberfire.client.perspective;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JSPanelDefinition extends JavaScriptObject {

    protected JSPanelDefinition() {
    }

    public final native int getWidth() /*-{
        if ((typeof this["width"]) === "number") {
            return this.width;
        }
        return -1;
    }-*/;

    public final native int getMinWidth() /*-{
        if ((typeof this["min_width"]) === "number") {
            return this.min_width;
        }
        return -1;
    }-*/;

    public final native int getHeight() /*-{
        if ((typeof this["height"]) === "number") {
            return this.height;
        }
        return -1;
    }-*/;

    public final native int getMinHeight() /*-{
        if ((typeof this["min_height"]) === "number") {
            return this.min_height;
        }
        return -1;
    }-*/;

    public final native String getPosition() /*-{
        return this.position;
    }-*/;

    public final native String getPanelTypeAsString() /*-{
        return this.panel_type;
    }-*/;

    public final native JsArray<JSPartDefinition> getParts() /*-{
        return this.parts;
    }-*/;

    public final native JsArray<JSPanelDefinition> getChildren() /*-{
        return this.panels;
    }-*/;

    public final native String getContextId()  /*-{
        return this.context_id;
    }-*/;

    public final native String getContextDisplayModeAsString()  /*-{
        return this.context_display_mode;
    }-*/;


}
