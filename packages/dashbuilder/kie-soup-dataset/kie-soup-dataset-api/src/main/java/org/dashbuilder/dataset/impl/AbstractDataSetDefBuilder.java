/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.impl;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefBuilder;
import org.dashbuilder.dataset.ColumnType;

/**
 * Base class for DataSetDefBuilder implementations.
 */
public abstract class AbstractDataSetDefBuilder<T> implements DataSetDefBuilder<T> {

    protected DataSetDef def = createDataSetDef();

    protected abstract DataSetDef createDataSetDef();

    public T uuid(String uuid) {
        def.setUUID(uuid);
        return (T) this;
    }

    public T name(String name) {
        def.setName(name);
        return (T) this;
    }

    public T pushOn(int pushMaxSize) {
        return (T) this;
    }

    public T pushOff() {
        return (T) this;
    }

    public T cacheOn(int maxRowsInCache) {
        def.setCacheEnabled(true);
        def.setCacheMaxRows(maxRowsInCache);
        return (T) this;
    }

    public T cacheOff() {
        def.setCacheEnabled(false);
        return (T) this;
    }

    public T refreshOn(String refreshTime, boolean refreshAlways) {
        def.setRefreshTime(refreshTime);
        def.setRefreshAlways(refreshAlways);
        return (T) this;
    }

    public T refreshOff() {
        def.setRefreshTime(null);
        return (T) this;
    }

    public T label(String columnId) {
        def.addColumn(columnId, ColumnType.LABEL);
        return (T) this;
    }

    public T text(String columnId) {
        def.addColumn(columnId, ColumnType.TEXT);
        return (T) this;
    }

    public T number(String columnId) {
        def.addColumn(columnId, ColumnType.NUMBER);
        return (T) this;
    }

    public T date(String columnId) {
        def.addColumn(columnId, ColumnType.DATE);
        return (T) this;
    }

    public T column(String columnId, ColumnType type) {
        def.addColumn(columnId, type);
        return (T) this;
    }

    public DataSetDef buildDef() {
        return def;
    }
}
