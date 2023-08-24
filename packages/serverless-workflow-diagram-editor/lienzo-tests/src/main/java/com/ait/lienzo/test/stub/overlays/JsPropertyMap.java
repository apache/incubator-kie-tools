package com.ait.lienzo.test.stub.overlays;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.test.annotation.StubClass;
import jsinterop.base.Any;
import jsinterop.base.JsForEachCallbackFn;
import org.mockito.Answers;

import static org.mockito.Mockito.mock;

@StubClass("jsinterop.base.JsPropertyMap")
public interface JsPropertyMap<T> {

    Map<String, Object> parameters = new HashMap<>();

    static JsPropertyMap<Object> of() {
        return mock(JsPropertyMap.class, Answers.CALLS_REAL_METHODS);
    }

    static JsPropertyMap<Object> of(String k, Object v) {
        JsPropertyMap<Object> map = of();
        map.set(k, v);
        return map;
    }

    static JsPropertyMap<Object> of(String k1, Object v1, String k2, Object v2) {
        JsPropertyMap<Object> map = of();
        map.set(k1, v1);
        map.set(k2, v2);
        return map;
    }

    static JsPropertyMap<Object> of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        JsPropertyMap<Object> map = of();
        map.set(k1, v1);
        map.set(k2, v2);
        map.set(k3, v3);
        return map;
    }

    default T get(String propertyName) {
        return (T) parameters.get(propertyName);
    }

    default Object nestedGet(String qualifiedName) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    default Any getAsAny(String propertyName) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    default Any nestedGetAsAny(String qualifiedName) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    default boolean has(String propertyName) {
        return parameters.containsKey(propertyName);
    }

    default void delete(String propertyName) {
        parameters.remove(propertyName);
    }

    default void set(String propertyName, T value) {
        parameters.put(propertyName, value);
        Object obj = parameters.get("__JS_OBJECT");
        if (null != obj
                && !"__JS_OBJECT".equals(propertyName)) {
            try {
                obj.getClass().getField(propertyName).set(obj, value);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    default void forEach(JsForEachCallbackFn cb) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }
}
