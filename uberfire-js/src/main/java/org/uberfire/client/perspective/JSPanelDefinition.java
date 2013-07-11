package org.uberfire.client.perspective;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import org.uberfire.workbench.model.PanelType;

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
