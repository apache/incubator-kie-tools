package com.ait.lienzo.test.stub.overlays;

import java.util.LinkedList;

import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.test.annotation.StubClass;
import jsinterop.annotations.JsOverlay;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/5/19
 */
@StubClass("com.ait.lienzo.client.core.types.PathPartListJSO")
public class PathPartListJSO extends LinkedList<PathPartEntryJSO> {

    @JsOverlay
    public static final PathPartListJSO make() {
        return new PathPartListJSO();
    }

    protected PathPartListJSO() {
    }

    public int push(PathPartEntryJSO... var_args) {
        for (int i = 0; i < var_args.length; i++) {
            add(var_args[i]);
        }
        return size();
    }

    @JsOverlay
    public final PathPartEntryJSO get(final int i) {
        return super.get(i);
    }

    @JsOverlay
    public final int length() {
        return size();
    }

    public void setLength(int length) {

    }
}
