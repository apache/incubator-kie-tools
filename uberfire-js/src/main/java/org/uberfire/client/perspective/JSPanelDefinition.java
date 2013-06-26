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

    public final native Integer getMinWidth() /*-{
        return this.min_width;
    }-*/;

    public final native Integer getHeight() /*-{
        return this.height;
    }-*/;

    public final native String getPosition() /*-{
        return this.position;
    }-*/;

    public final native JsArray<JSPartDefinition> getParts() /*-{
        return this.parts;
    }-*/;

    public final native JsArray<JSPanelDefinition> getChildren() /*-{
        return this.panels;
    }-*/;

}
