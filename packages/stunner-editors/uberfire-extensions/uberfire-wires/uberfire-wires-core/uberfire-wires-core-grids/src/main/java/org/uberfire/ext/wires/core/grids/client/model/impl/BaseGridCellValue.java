/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.model.impl;

import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;

/**
 * Base implementation of a grid cell value holder to avoid boiler-plate for more specific implementations.
 * @param <T> The Type of the value
 */
public class BaseGridCellValue<T> implements GridCellValue<T> {

    protected T value;
    private String placeHolder = null;

    public BaseGridCellValue(final T value) {
        this.value = value;
    }

    public BaseGridCellValue(T value, String placeHolder) {
        this(value);
        this.placeHolder = placeHolder;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getPlaceHolder() {
        return placeHolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseGridCellValue that = (BaseGridCellValue) o;

        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
