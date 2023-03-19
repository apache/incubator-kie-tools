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

import org.dashbuilder.dsl.model.Column;
import org.dashbuilder.dsl.model.Component;
import org.dashbuilder.dsl.model.Page;
import org.dashbuilder.dsl.model.Row;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

import static org.junit.Assert.assertEquals;

public class PageBuilderTest {

    @Test
    public void testPageBuilderProperties() {
        String name = "pageName";
        Page page = PageBuilder.newBuilder(name).property("key", "value").build();
        assertEquals(name, page.getLayoutTemplate().getName());
        assertEquals("value", page.getLayoutTemplate().getLayoutProperties().get("key"));
    }

    @Test
    public void testPageBuilderComponent() {
        LayoutComponent lt = new LayoutComponent();
        Component comp = Component.create(lt);
        Column column = ColumnBuilder.newBuilder().component(comp).build();
        Row row = RowBuilder.newBuilder().column(column).build();
        Page page = PageBuilder.newBuilder("test").row(row).build();
        assertEquals(lt, page.getLayoutTemplate()
                             .getRows().get(0)
                             .getLayoutColumns().get(0)
                             .getLayoutComponents().get(0));
    }

}