package com.ait.lienzo.test.stub.overlays;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.test.annotation.StubClass;
import com.ait.tooling.common.api.json.JSONType;
import com.ait.tooling.nativetools.client.NJSONReplacer;
import com.ait.tooling.nativetools.client.NUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import org.apache.commons.lang3.StringUtils;

@StubClass("com.ait.tooling.nativetools.client.NArrayBaseJSO")
public class NArrayBaseJSO<T extends NArrayBaseJSO<T>> extends JavaScriptObject {

    protected final List<Object> list = new ArrayList<Object>();

    protected static <T extends NArrayBaseJSO<T>> T createNArrayBaseJSO() {
        return (T) new NArrayBaseJSO<T>();
    }

    protected NArrayBaseJSO() {
    }

    public JSONArray toJSONArray() {
        return new JSONArray(this);
    }

    public String toJSONString() {
        return NUtils.JSON.toJSONString(this);
    }

    public String toJSONString(final NJSONReplacer replacer) {
        return NUtils.JSON.toJSONString(this,
                                        replacer);
    }

    public String toJSONString(final String indent) {
        return NUtils.JSON.toJSONString(this,
                                        indent);
    }

    public String toJSONString(final NJSONReplacer replacer,
                               final String indent) {
        return NUtils.JSON.toJSONString(this,
                                        replacer,
                                        indent);
    }

    public String toJSONString(final int indent) {
        return NUtils.JSON.toJSONString(this,
                                        indent);
    }

    public String toJSONString(final NJSONReplacer replacer,
                               final int indent) {
        return NUtils.JSON.toJSONString(this,
                                        replacer,
                                        indent);
    }

    public void clear() {
        list.clear();
    }

    public String join() {
        return join(",");
    }

    public JSONType getNativeTypeOf(final int index) {
        if ((index < 0) || (index >= size())) {
            return JSONType.UNDEFINED;
        }
        return NUtils.Native.getNativeTypeOf(this,
                                             index);
    }

    public boolean isNull(final int index) {
        if ((index < 0) || (index >= size())) {
            return true;
        }
        return list.get(index) == null;
    }

    public boolean isDefined(final int index) {
        if ((index < 0) || (index >= size())) {
            return false;
        }
        return list.get(index) != null;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public void setSize(int size) {

    }

    public void splice(int beg,
                       int removed) {
        // TODO
    }

    public void reverse() {
        Collections.reverse(list);
    }

    public String join(String separator) {
        return StringUtils.join(list,
                                separator);
    }

    public T concat(T value) {
        list.addAll(value.list);
        return value;
    }

    public T copy() {
        return isEmpty() ? null : (T) list.get(list.size() - 1);
    }

    public T slice(int beg) {
        // TOD
        return copy();
    }

    public T slice(int beg,
                   int end) {
        // TOD
        return copy();
    }

    protected double doShift() {
        double t = (double) list.get(0);

        list.remove(0);

        return t;
    }

    protected void doUnShift(final double value) {
        list.add(0,
                 value);
    }
}

