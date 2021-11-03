/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.factory.page;

import java.util.HashMap;

import org.dashbuilder.dsl.model.Column;
import org.dashbuilder.dsl.model.Row;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;

public class RowBuilder extends AbstractLayoutBuilder<RowBuilder> {

    private static final String DEFAULT_HEIGHT = "12";

    LayoutRow layoutRow;

    private RowBuilder(String height) {
        this.layoutRow = new LayoutRow(height, new HashMap<>());
    }

    public static RowBuilder newBuilder() {
        return newBuilder(DEFAULT_HEIGHT);
    }

    public static RowBuilder newBuilder(String height) {
        return new RowBuilder(height);
    }

    @Override
    public RowBuilder property(String key, String value) {
        this.layoutRow.getProperties().put(key, value);
        return this;
    }

    public RowBuilder columns(Column... columns) {
        for (Column column : columns) {
            this.column(column);
        }
        return this;
    }

    public RowBuilder column(Column column) {
        this.layoutRow.add(column.getLayoutColumn());
        return this;
    }

    public Row build() {
        return Row.create(this.layoutRow);
    }

    @Override
    protected void addProperty(String key, String value) {
        this.layoutRow.getProperties().put(key, value);
    }

}
