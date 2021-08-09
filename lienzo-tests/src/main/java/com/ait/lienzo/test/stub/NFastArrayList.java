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

package com.ait.lienzo.test.stub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.ait.lienzo.test.annotation.StubClass;
import com.ait.lienzo.test.util.LienzoMockitoLogger;

/**
 * In-memory array list implementation stub for class <code>com.ait.tooling.nativetools.client.collection.NFastArrayList</code>.
 * <p>
 * Results easier creating this stub class for this wrapper of FastArrayListJSO than creating concrete stubs for NFastArrayListJSO and
 * its super classes.
 *
 * @author Roger Martinez
 * @since 1.0
 */
@StubClass("com.ait.lienzo.tools.client.collection.NFastArrayList")
public class NFastArrayList<M> implements Iterable<M> {

    private final ArrayList<M> list = new ArrayList<M>();

    public NFastArrayList() {
        LienzoMockitoLogger.log("NFastArrayList", "Creating custom Lienzo overlay type.");
    }

    @SuppressWarnings("unchecked")
    public NFastArrayList(final M value, final M... values) {
        this();

        add(value);

        if ((null != values) && (values.length > 0)) {
            for (int i = 0; i < values.length; i++) {
                add(values[i]);
            }
        }
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public int getLength() {
        return size();
    }

    public <M> M get(final int index) {
        return (M) list.get(index);
    }

    public NFastArrayList<M> add(final M value) {
        list.add(value);

        return this;
    }

    public NFastArrayList<M> set(final int i, final M value) {
        list.set(i, value);

        return this;
    }

    public boolean contains(final M value) {
        return list.contains(value);
    }

    public void clear() {
        list.clear();
    }

    public NFastArrayList<M> remove(final M value) {
        list.remove(value);

        return this;
    }

    public NFastArrayList<M> unshift(final M value) {
        doUnShift(value);

        return this;
    }

    public NFastArrayList<M> moveUp(final M value) {
        if (!list.isEmpty()) {
            final int i = list.indexOf(value);

            list.set(i + 1, value);
        }
        return this;
    }

    public NFastArrayList<M> moveDown(final M value) {
        if (!list.isEmpty()) {
            final int i = list.indexOf(value);

            list.set(i > 0 ? i - 1 : 0, value);
        }
        return this;
    }

    public NFastArrayList<M> moveToTop(final M value) {
        if ((size() < 2) || (!contains(value))) {
            return this;
        }
        remove(value);

        add(value);

        return this;
    }

    public NFastArrayList<M> moveToBottom(final M value) {
        if ((size() < 2) || (!contains(value))) {
            return this;
        }
        remove(value);

        unshift(value);

        return this;
    }

    public M pop() {
        M result = null;

        if (!list.isEmpty()) {
            final int i = list.size() - 1;

            result = list.get(i);

            list.remove(i);
        }
        return result;
    }

    public M shift() {
        return doShift();
    }

    public NFastArrayList<M> splice(final int beg, final int removed, final M value) {
        return this;
    }

    public NFastArrayList<M> reverse() {
        Collections.reverse(list);
        return this;
    }

    public int push(final M... values) {
        add(values[0]);

        for (int i = 1; i < values.length; i++) {
            add(values[i]);
        }
        return size();
    }

    public NFastArrayList<M> copy() {
        final NFastArrayList<M> result = new NFastArrayList<>();

        result.list.addAll(this.list);

        return result;
    }

    public NFastArrayList<M> concat(final NFastArrayList<M> value) {
        final NFastArrayList<M> result = copy();

        if (null != value) {
            result.list.addAll(value.list);

            return copy();
        }
        return result;
    }

    public NFastArrayList<M> slice(final int beg) {
        return copy();
    }

    public NFastArrayList<M> slice(final int beg, final int end) {
        return copy();
    }

    public List<M> toList() {
        final int size = size();

        final ArrayList<M> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(get(i));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public Iterator<M> iterator() {
        return toList().iterator();
    }

    private M doShift() {
        final M t = list.get(0);

        list.remove(0);

        return t;
    }

    public List<M> asList() {
        return list;
    }

    private void doUnShift(final M value) {
        list.add(0, value);
    }
}
