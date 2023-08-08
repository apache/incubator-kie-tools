package com.ait.lienzo.test.stub.overlays;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.ait.lienzo.test.annotation.StubClass;
import javaemul.internal.annotations.UncheckedCast;
import jsinterop.base.Any;
import jsinterop.base.JsArrayLike;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;

@StubClass("jsinterop.base.Js")
public class Js {

    public static Object undefined() {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static JsArrayLike<Object> arguments() {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static void debugger() {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static String typeof(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static JsPropertyMap<Object> global() {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static <T> JsConstructorFn<T> asConstructorFn(Class<T> clazz) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static Any asAny(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static JsPropertyMap<Object> asPropertyMap(Object obj) {
        JsPropertyMap<Object> map = JsPropertyMap.of();
        map.set("__JS_OBJECT", obj);
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (Modifier.isPrivate(field.getModifiers())
                        || Modifier.isStatic(field.getModifiers())
                        || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                map.set(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static JsArrayLike<Object> asArrayLike(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static Any[] asArray(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static String asString(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static boolean asBoolean(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static double asDouble(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static float asFloat(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static long asLong(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static int asInt(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static short asShort(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static char asChar(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static byte asByte(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static <T> T cast(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    @UncheckedCast
    public static <T> T uncheckedCast(Object obj) {
        return (T) obj;
    }

    public static boolean isTruthy(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static boolean isFalsy(Object obj) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public boolean isTripleEqual(Object o1, Object o2) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public double coerceToDouble(Object d) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    public static int coerceToInt(Object d) {
        throw new UnsupportedOperationException("This stub method is not implemented yet.");
    }

    private Js() {
    }
}
