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
package org.kie.workbench.common.services.refactoring;

import org.uberfire.ext.metadata.model.KProperty;

public class KPropertyImpl<T> implements KProperty<T> {

    private final String name;
    private final T value;
    private final boolean isSearchable;
    private final boolean isSortable;

    public KPropertyImpl(final String name,
                         final T value) {
        this(name,
             value,
             true,
             false);
    }

    public KPropertyImpl(final String name,
                         final T value,
                         final boolean isSearchable,
                         final boolean isSortable) {
        this.name = name;
        this.value = value;
        this.isSearchable = isSearchable;
        this.isSortable = isSortable;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public boolean isSearchable() {
        return this.isSearchable;
    }

    @Override
    public boolean isSortable() {
        return this.isSortable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KPropertyImpl)) {
            return false;
        }

        KPropertyImpl<?> kProperty = (KPropertyImpl<?>) o;

        if (isSearchable != kProperty.isSearchable) {
            return false;
        }
        if (isSortable != kProperty.isSortable) {
            return false;
        }
        if (name != null ? !name.equals(kProperty.name) : kProperty.name != null) {
            return false;
        }
        return value != null ? value.equals(kProperty.value) : kProperty.value == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (isSearchable ? 1 : 0);
        result = 31 * result + (isSortable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KPropertyImpl{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", isSearchable=" + isSearchable +
                ", isSortable=" + isSortable +
                '}';
    }
}
