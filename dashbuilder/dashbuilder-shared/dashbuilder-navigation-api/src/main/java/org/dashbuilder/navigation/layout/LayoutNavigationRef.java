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
package org.dashbuilder.navigation.layout;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class LayoutNavigationRef {

    LayoutNavigationRefType type;
    String name;

    public LayoutNavigationRef() {
    }

    public LayoutNavigationRef(LayoutNavigationRefType type, String name) {
        this.type = type;
        this.name = name;
    }

    public LayoutNavigationRefType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "LayoutNavigationRef{" +
                "type=" + type +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LayoutNavigationRef that = (LayoutNavigationRef) o;
        return type == that.type && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }
}
