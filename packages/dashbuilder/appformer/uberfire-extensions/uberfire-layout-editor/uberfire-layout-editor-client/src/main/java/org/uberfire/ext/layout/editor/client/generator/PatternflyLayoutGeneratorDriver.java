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

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.infra.ColumnSizeBuilder;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;

/**
 * The layout generator driver used in the {@link PatternflyLayoutGenerator}
 */
@Default
@Dependent
public class PatternflyLayoutGeneratorDriver implements LayoutGeneratorDriver {

    private static final String PF_ROW_CLASSES =
            "pf-v5-l-grid pf-m-all-6-col-on-sm pf-m-all-4-col-on-md pf-m-all-2-col-on-lg pf-m-all-1-col-on-xl";

    @Inject
    private LayoutDragComponentHelper dragTypeHelper;

    @Override
    public HTMLElement createContainer() {
        return createDiv(null);
    }

    @Override
    public HTMLElement createRow(LayoutRow layoutRow) {
        return createDiv(PF_ROW_CLASSES);
    }

    @Override
    public HTMLElement createColumn(LayoutColumn layoutColumn) {
        var span = Integer.parseInt(layoutColumn.getSpan());
        var pfColumnClasses = ColumnSizeBuilder.buildPFGridClasses(span);
        return createDiv(pfColumnClasses);
    }

    @Override
    public IsWidget createComponent(HTMLElement column, LayoutComponent layoutComponent) {
        final var dragComponent = lookupComponent(layoutComponent);
        if (dragComponent != null) {
            var columnWidget = ElementWrapperWidget.getWidget(column);
            var componentContext = new RenderingContext(layoutComponent, columnWidget);
            return dragComponent.getShowWidget(componentContext);
        }
        return null;
    }

    protected LayoutDragComponent lookupComponent(LayoutComponent layoutComponent) {
        return dragTypeHelper.lookupDragTypeBean(layoutComponent.getDragTypeName());
    }

    private HTMLDivElement createDiv(String className) {
        var div = (HTMLDivElement) DomGlobal.document.createElement("div");
        if (className != null) {
            div.className = className;
        }
        return div;
    }
}
