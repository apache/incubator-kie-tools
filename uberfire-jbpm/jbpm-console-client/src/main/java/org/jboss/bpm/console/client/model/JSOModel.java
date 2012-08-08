package org.jboss.bpm.console.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import org.drools.guvnor.client.util.SimpleDateFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Java overlay of a JavaScriptObject.
 * Borrowed from Matt Raible:
 * http://raibledesigns.com/rd/entry/json_parsing_with_javascript_overlay
 */
public class JSOModel extends JavaScriptObject {

    // Overlay types always have protected, zero-arg constructors
    protected JSOModel() {
    }

    /**
     * Create an empty instance.
     *
     * @return new Object
     */
    public static native JSOModel create() /*-{
        return new Object();
    }-*/;

    /**
     * Convert a JSON encoded string into a JSOModel instance.
     * <p/>
     * Expects a JSON string structured like '{"foo":"bar","number":123}'
     *
     * @return a populated JSOModel object
     */
    public static native JSOModel fromJson(String jsonString) /*-{
        return eval('(' + jsonString + ')');
    }-*/;

    /**
     * Convert a JSON encoded string into an array of JSOModel instance.
     * <p/>
     * Expects a JSON string structured like '[{"foo":"bar","number":123}, {...}]'
     *
     * @return a populated JsArray
     */
    public static native JsArray<JSOModel> arrayFromJson(String jsonString) /*-{
        return eval('(' + jsonString + ')');
    }-*/;

    public final native boolean hasKey(String key) /*-{
        return this[key] != undefined;
    }-*/;

    public final native JsArrayString keys() /*-{
        var a = new Array();
        for (var p in this) {
            a.push(p);
        }
        return a;
    }-*/;

    @Deprecated
    public final Set<String> keySet() {
        JsArrayString array = keys();
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < array.length(); i++) {
            set.add(array.get(i));
        }
        return set;
    }

    public final native String get(String key) /*-{
        return "" + this[key];
    }-*/;

    public final native String get(String key, String defaultValue) /*-{
        return this[key] ? ("" + this[key]) : defaultValue;
    }-*/;

    public final native void set(String key, String value) /*-{
        this[key] = value;
    }-*/;

    public final int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public final boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public final native JSOModel getObject(String key) /*-{
        return this[key];
    }-*/;

    public final native JsArray<JSOModel> getArray(String key) /*-{
        return this[key] ? this[key] : new Array();
    }-*/;

    public final long getLong(String key) {
        return Long.valueOf(get(key));
    }

    public final Date getDate(String key) {
        Date result = null;
        String value = get(key);
        if (!isNull(value)) {
            SimpleDateFormat df = new SimpleDateFormat();
            result = df.parse(value);
        }

        return result;
    }

    public final Date getDate(String key, Date fallback) {
        Date date = getDate(key);
        return date != null ? date : fallback;
    }

    private final boolean isNull(String val) {
        return (val != null && "null".equals(val) || "undefined".equals(val));
    }
}

