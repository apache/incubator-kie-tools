/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.layout.editor.client.generator;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Window;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.infra.ColumnSizeBuilder;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;

/**
 * A bootstrap based layout generator
 */
@Default
@Dependent
public class BootstrapLayoutGenerator extends AbstractLayoutGenerator {

    @Inject
    private LayoutDragComponentHelper dragTypeHelper;

    @Override
    protected HTMLElement createContainer(LayoutTemplate layoutTemplate) {
        Div div = (Div) Window.getDocument().createElement("div");
        div.setId("mainContainer");
        return div;
    }

    @Override
    protected HTMLElement createRow(LayoutRow layoutRow) {
        Div div = (Div) Window.getDocument().createElement("div");
        div.setClassName("row");
        return div;
    }

    @Override
    protected HTMLElement createColumn(LayoutColumn layoutColumn) {
        Div div = (Div) Window.getDocument().createElement("div");
        String colSize = ColumnSizeBuilder.buildColumnSize(new Integer(layoutColumn.getSpan()));
        div.setClassName(colSize);
        return div;
    }

    @Override
    public LayoutDragComponent lookupLayoutDragComponent(LayoutComponent layoutComponent) {
        return dragTypeHelper.lookupDragTypeBean(layoutComponent.getDragTypeName());
    }
}
