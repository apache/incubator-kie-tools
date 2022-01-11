package com.ait.lienzo.test.stub.overlays;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class NArrayBase<T extends NArrayBase<T>> {

    protected final List<Object> list = new ArrayList<>();

    @SuppressWarnings("unchecked")
    protected static <T extends NArrayBase<T>> T createNArrayBase() {
        return (T) new NArrayBase<T>();
    }

    protected NArrayBase() {
    }

    public void clear() {
        list.clear();
    }

    public String join() {
        return join(",");
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

    public void setSize(final int size) {
    }

    public void splice(final int beg, final int removed) {
    }

    public void reverse() {
        Collections.reverse(list);
    }

    public String join(final String separator) {
        return StringUtils.join(list, separator);
    }

    public T concat(final T value) {
        list.addAll(value.list);

        return value;
    }

    @SuppressWarnings("unchecked")
    public T copy() {
        return isEmpty() ? null : (T) list.get(list.size() - 1);
    }

    public T slice(final int beg) {
        return copy();
    }

    public T slice(final int beg, final int end) {
        return copy();
    }

    protected double doShift() {
        final double t = (double) list.get(0);

        list.remove(0);

        return t;
    }

    protected void doUnShift(final double value) {
        list.add(0, value);
    }
}
