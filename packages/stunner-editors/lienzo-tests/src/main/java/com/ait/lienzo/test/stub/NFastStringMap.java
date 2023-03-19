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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.test.annotation.StubClass;
import com.ait.lienzo.test.util.LienzoMockitoLogger;

/**
 * In-memory Map implementation stub stub for class <code>com.ait.tooling.nativetools.client.collection.NFastStringMap</code>.
 * <p>
 * Results easier creating this stub class for this wrapper of NFastStringMapJSO than creating concrete stubs for NFastStringMapJSO and
 * its super classes.
 *
 * @author Roger Martinez
 * @since 1.0
 */
@StubClass("com.ait.lienzo.tools.client.collection.NFastStringMap")
public class NFastStringMap<V> {

    private final Map<String, V> map = new HashMap<>();

    public NFastStringMap() {
        LienzoMockitoLogger.log("NFastStringMap", "Creating custom Lienzo overlay type.");
    }

    public NFastStringMap<V> put(final String key, final V value) {
        map.put(key, value);
        return this;
    }

    public V get(final String key) {
        //return map.get(NUtils.doKeyRepair(key));
        return map.get(key);
    }

    public NFastStringMap<V> remove(final String key) {
        map.remove(key);

        return this;
    }

    public boolean isDefined(final String key) {
        return map.containsKey(key);
    }

    public boolean isNull(final String key) {
        return map.get(key) == null;
    }

    public int size() {
        return map.size();
    }

    public void clear() {
        map.clear();
    }

    public Collection<String> keys() {
        return map.keySet();
    }

    public Collection<V> values() {
        final ArrayList<V> list = new ArrayList<>(map.values());

        return Collections.unmodifiableList(list);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
