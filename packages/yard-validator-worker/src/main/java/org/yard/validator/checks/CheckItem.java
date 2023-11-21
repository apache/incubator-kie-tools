/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.yard.validator.checks;

import org.yard.validator.CustomTreeSet;
import org.yard.validator.key.Key;
import org.yard.validator.key.Location;

public class CheckItem {
    private final int index;
    private final Location location;
    private final Key[] keys;

    public CheckItem(final int index,
                     final Location location,
                     final CustomTreeSet keys) {

        this.index = index;
        this.location = location;
        this.keys = keys.toArray(new Key[keys.size()]);
    }

    public int getIndex() {
        return index;
    }

    public Location getLocation() {
        return location;
    }

    public Key[] getKeys() {
        return keys;
    }

    @Override
    public String toString() {
        return "CheckItem{" +
                "location=" + location +
                '}';
    }
}
