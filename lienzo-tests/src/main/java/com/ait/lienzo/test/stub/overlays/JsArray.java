/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.test.stub.overlays;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.test.annotation.StubClass;
import org.apache.commons.lang3.StringUtils;

/**
 * In-memory array implementation stub for class <code>com.google.gwt.core.client.JsArray</code>.
 * @author Roger Martinez
 * @since 1.0
 */
@StubClass("elemental2.core.JsArray")
public class JsArray<T> {

    public int length = 0;

    private final List<T> list = new ArrayList<>();

    public JsArray() {
    }

    public JsArray(T... items) {
        for (int i = 0; i < items.length; i++) {
            list.add(items[i]);
        }
    }

    public T get(final int index) {
        return list.get(index);
    }

    public String join() {
        return join(",");
    }

    public String join(final String separator) {
        return StringUtils.join(list, separator);
    }

    public int length() {
        return list.size();
    }

    public void push(final T value) {
        list.add(value);
        this.length = list.size();
    }

    public int push(final T... items) {
        for (int i = 0; i < items.length; i++) {
            list.add(items[i]);
        }
        this.length = list.size();
        return list.size();
    }

    public void set(final int index, final T value) {
        if ((list.size() - 1) < index) {
            setLength(index + 1);
        }
        list.set(index, value);
        this.length = list.size();
    }

    public void setLength(final int newLength) {
        if (list.size() < newLength) {
            for (int i = list.size(); i < newLength; i++) {
                this.push(list.get(i));
            }
        }
        this.length = list.size();
    }

    public T shift() {
        return doShift();
    }

    public void unshift(final T value) {
        doUnShift(value);
    }

    private T doShift() {
        final T t = list.get(0);

        list.remove(0);
        this.length = list.size();
        return t;
    }

    private void doUnShift(final T value) {
        list.add(0, value);
        this.length = list.size();
    }

    public int indexOf(T obj) {
        return list.indexOf(obj);
    }

    public JsArray<T> splice(int index, int howMany, T... args) {
        if (args.length > 0) {
            throw new Error("Not implemented ");
        }

        List<T> result = new ArrayList<>();

        for (int i = 0; i < howMany; i++) {
            result.add(list.remove(index + i));
        }
        T[] results = (T[]) result.toArray();
        return new JsArray<T>(results);
    }

    public int getLength() {
        return list.size();
    }

    public T getAt(int i) {
        return list.get(i);
    }

    public final T[] asArray(T[] array) {
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
