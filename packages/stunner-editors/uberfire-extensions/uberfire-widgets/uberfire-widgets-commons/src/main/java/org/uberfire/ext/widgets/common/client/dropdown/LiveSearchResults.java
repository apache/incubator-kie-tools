/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.dropdown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class LiveSearchResults<TYPE> extends ArrayList<LiveSearchEntry<TYPE>> {

    private int maxEntries = -1;
    private static Comparator<LiveSearchEntry> _byValue = (e1, e2) -> e1.getValue().compareTo(e2.getValue());
    private static Comparator<LiveSearchEntry> _byKey = (e1, e2) -> e1.getKey().toString().compareTo(e2.getKey().toString());

    public LiveSearchResults() {
        super();
    }

    public LiveSearchResults(int maxEntries) {
        super();
        this.maxEntries = maxEntries;
    }

    public LiveSearchResults(Collection<String> keysCollection) {
        super();
        this.addKeys(keysCollection);
    }

    public LiveSearchResults(int maxEntries, Collection<String> itemCollection) {
        this(maxEntries);
        this.addKeys(itemCollection);
    }

    public boolean add(String key) {
        return this.add(key, key);
    }

    public boolean add(String key, String value) {
        if (maxEntries <= 0 || size() < maxEntries) {
            LiveSearchEntry entry = new LiveSearchEntry(key, value);
            return this.add(entry);
        }
        return false;
    }

    @Override
    public boolean add(LiveSearchEntry entry) {
        if (maxEntries <= 0 || size() < maxEntries) {
            return super.add(entry);
        }
        return false;
    }

    public void addKeys(Collection<String> itemCollection) {
        itemCollection.forEach(item -> add(item, item));
    }

    public LiveSearchResults sortByKey() {
        sort(_byKey);
        return this;
    }

    public LiveSearchResults sortByValue() {
        sort(_byValue);
        return this;
    }
}