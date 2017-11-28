package org.dashbuilder.renderer.chartjs.lib.options;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Simple container for chart options
 */
public class ChartOption extends JavaScriptObject{

    protected ChartOption(){
        super();
    }

    public static ChartOption get(){
        return JavaScriptObject.createObject().cast();
    }

    public final native void setProperty(String key, Object value)/*-{
        this[key] = value;
    }-*/;

    public final native void clearProperty(String key)/*-{
        this[key] = null;
    }-*/;

    public final native void appendTo(JavaScriptObject parent)/*-{
        for(var key in this) parent[key]=this[key];
    }-*/;

    public final native void setArrayProperty(String key, Object[] value)/*-{
        this[key] = value;
    }-*/;
}
