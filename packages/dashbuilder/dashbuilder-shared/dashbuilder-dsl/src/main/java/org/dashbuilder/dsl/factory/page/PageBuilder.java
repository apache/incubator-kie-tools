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

import org.dashbuilder.dsl.model.Page;
import org.dashbuilder.dsl.model.Row;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate.Style;

public class PageBuilder extends AbstractLayoutBuilder<PageBuilder> {

    LayoutTemplate layoutTemplate;

    private PageBuilder(String name) {
        this.layoutTemplate = new LayoutTemplate(name);
    }

    public static PageBuilder newBuilder(String name) {
        return new PageBuilder(name);
    }

    public PageBuilder name(String name) {
        this.layoutTemplate.setName(name);
        return this;
    }

    public PageBuilder style(Style style) {
        this.layoutTemplate.setStyle(style);
        return this;
    }

    public PageBuilder row(Row row) {
        this.layoutTemplate.addRow(row.getLayoutRow());
        return this;
    }

    public PageBuilder rows(Row... rows) {
        for (Row row : rows) {
            this.row(row);
        }
        return this;
    }

    public Page build() {
        return Page.create(this.layoutTemplate);
    }

    @Override
    protected void addProperty(String key, String value) {
        this.layoutTemplate.addLayoutProperty(key, value);
    }

}