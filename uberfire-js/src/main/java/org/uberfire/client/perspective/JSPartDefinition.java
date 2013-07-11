package org.uberfire.client.perspective;

import com.google.gwt.core.client.JavaScriptObject;

public class JSPartDefinition extends JavaScriptObject {

    protected JSPartDefinition() {
    }

    public final native String getPlaceName() /*-{
        return this.place;
    }-*/;

    public final native JavaScriptObject getParameters() /*-{
        return this.parameters;
    }-*/;

    public final native String getContextId()  /*-{
        return this.context_id;
    }-*/;

    public final native String getContextDisplayModeAsString()  /*-{
        return this.context_display_mode;
    }-*/;
}
