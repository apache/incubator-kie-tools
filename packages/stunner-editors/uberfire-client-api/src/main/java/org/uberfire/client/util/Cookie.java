/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.util;

import javax.enterprise.context.Dependent;

import elemental2.dom.DomGlobal;

@Dependent
public class Cookie {

    public String get() {
        return DomGlobal.document.cookie;
    }

    public String get(final String name) {
        String cookie = "; " + get();
        String[] parts = cookie.split("; " + name + "=");
        if (parts.length == 2) {
            return parts[1].split(";")[0];
        }
        return "";
    }

    public void set(final String name,
                    final String value) {
        DomGlobal.document.cookie = name + "=" + value;
    }

    public void set(final String name,
                    final String value,
                    final int maxAge) {
        DomGlobal.document.cookie = name + "=" + value + ";max-age=" + maxAge;
    }

    public void clear(final String name) {
        set(name, "");
    }
}
