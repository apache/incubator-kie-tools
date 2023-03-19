package com.ait.lienzo.test.stub.overlays;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.core.JsIteratorIterable;

import static org.mockito.Mockito.mock;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/8/19
 */
@StubClass("elemental2.core.JsMap")
public class JsMap<KEY, VALUE> {

    public int size = 0;

    Map<KEY, VALUE> map = new HashMap<>();

    public VALUE get(KEY key) {
        return map.get(key);
    }

    public JsMap<KEY, VALUE> set(KEY key, VALUE value) {
        map.put(key, value);
        this.size = map.size();
        return this;
    }

    public boolean has(KEY key) {
        return map.containsKey(key);
    }

    public JsIteratorIterable<VALUE> values() {
        return mock(JsIteratorIterable.class);
    }

    public Object forEach(elemental2.core.JsMap.ForEachCallbackFn<? super KEY, ? super VALUE> callback) {
        return mock(Object.class);
    }
}
