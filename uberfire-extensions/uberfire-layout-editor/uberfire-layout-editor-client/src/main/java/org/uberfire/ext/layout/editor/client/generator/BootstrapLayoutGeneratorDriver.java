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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.infra.ColumnSizeBuilder;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * The layout generator driver used in the {@link BootstrapLayoutGenerator}
 */
@Default
@Dependent
public class BootstrapLayoutGeneratorDriver implements LayoutGeneratorDriver {

    @Inject
    private LayoutDragComponentHelper dragTypeHelper;

    @Override
    public HTMLElement createContainer() {
        Div div = (Div) Window.getDocument().createElement("div");
        return div;
    }

    @Override
    public HTMLElement createRow(LayoutRow layoutRow) {
        Div div = (Div) Window.getDocument().createElement("div");
        div.setClassName("row");
        return div;
    }

    @Override
    public HTMLElement createColumn(LayoutColumn layoutColumn) {
        Div div = (Div) Window.getDocument().createElement("div");
        String colSize = ColumnSizeBuilder.buildColumnSize(new Integer(layoutColumn.getSpan()));
        div.setClassName(colSize);
        return div;
    }

    @Override
    public IsWidget createComponent(HTMLElement column, LayoutComponent layoutComponent) {
        final LayoutDragComponent dragComponent = lookupComponent(layoutComponent);
        if (dragComponent != null) {
            Widget columnWidget = ElementWrapperWidget.getWidget(column);
            RenderingContext componentContext = new RenderingContext(layoutComponent, columnWidget);
            return dragComponent.getShowWidget(componentContext);
        }
        return null;
    }

    protected LayoutDragComponent lookupComponent(LayoutComponent layoutComponent) {
        return dragTypeHelper.lookupDragTypeBean(layoutComponent.getDragTypeName());
    }
}
