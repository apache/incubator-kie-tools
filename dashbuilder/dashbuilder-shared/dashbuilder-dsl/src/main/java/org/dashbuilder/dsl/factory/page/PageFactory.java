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

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.dsl.model.Column;
import org.dashbuilder.dsl.model.Component;
import org.dashbuilder.dsl.model.Page;
import org.dashbuilder.dsl.model.Row;

import static org.dashbuilder.dsl.factory.component.ComponentFactory.displayer;
import static org.dashbuilder.dsl.factory.component.ComponentFactory.html;

public class PageFactory {

    private PageFactory() {
        // empty
    }

    public static PageBuilder pageBuilder(String name) {
        return PageBuilder.newBuilder(name);
    }

    public static RowBuilder rowBuilder(String height) {
        return RowBuilder.newBuilder(height);
    }

    public static ColumnBuilder columnBuilder() {
        return ColumnBuilder.newBuilder();
    }

    public static ColumnBuilder columnBuilder(String span) {
        return ColumnBuilder.newBuilder(span);
    }

    public static ColumnBuilder columnBuilder(String span, Component... components) {
        return ColumnBuilder.newBuilder(span).components(components);
    }

    public static Page page(String name, Row... rows) {
        return PageBuilder.newBuilder(name).rows(rows).build();
    }

    public static Column column(String span, Component... components) {
        return ColumnBuilder.newBuilder(span).components(components).build();
    }

    public static Column column(Component... components) {
        return ColumnBuilder.newBuilder().components(components).build();
    }

    public static Column column(Component component) {
        return ColumnBuilder.newBuilder().components(component).build();
    }

    public static Column column(DisplayerSettings settings) {
        return ColumnBuilder.newBuilder().components(displayer(settings)).build();
    }

    public static Column column(Row row) {
        return ColumnBuilder.newBuilder().row(row).build();
    }

    public static Column column(Row... rows) {
        return ColumnBuilder.newBuilder().rows(rows).build();
    }

    public static Row row(Column... columns) {
        return RowBuilder.newBuilder().columns(columns).build();
    }

    /**
     * 
     * Creates a column with a single html component
     * @param html
     * @return
     */
    public static Column column(String html) {
        return ColumnBuilder.newBuilder().component(html(html)).build();
    }

    /**
     * 
     * Creates a row with a single column and a single html component
     * @param html
     * @return
     */
    public static Row row(String html) {
        return row(html(html));
    }

    /**
     * 
     * Creates a row with a single column with the provided component
     * @param component
     * @return
     */
    public static Row row(Component component) {
        return row(column(component));
    }

    /**
     * 
     * Creates a row with a single column with a displayer created with the provided settings
     * @param settings
     * @return
     */
    public static Row row(DisplayerSettings settings) {
        return row(column(settings));
    }

}