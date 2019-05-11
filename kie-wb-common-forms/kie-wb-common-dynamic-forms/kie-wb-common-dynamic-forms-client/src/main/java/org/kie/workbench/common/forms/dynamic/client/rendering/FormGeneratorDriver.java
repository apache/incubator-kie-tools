/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.generator.LayoutGeneratorDriver;
import org.uberfire.ext.layout.editor.client.infra.ColumnSizeBuilder;

@Dependent
public class FormGeneratorDriver implements LayoutGeneratorDriver {

    private SyncBeanManager beanManager;
    private ManagedInstance<LayoutDragComponent> instance;
    private List<FieldLayoutComponent> layoutComponents = new ArrayList<>();
    private Map<String, Class<? extends LayoutDragComponent>> componentsCache = new HashMap<>();
    private FormRenderingContext renderingContext;

    @Inject
    public FormGeneratorDriver(SyncBeanManager beanManager, ManagedInstance<LayoutDragComponent> instance) {
        this.beanManager = beanManager;
        this.instance = instance;
    }

    public FormRenderingContext getRenderingContext() {
        return renderingContext;
    }

    public void setRenderingContext(FormRenderingContext renderingContext) {
        this.renderingContext = renderingContext;
    }

    @Override
    public HTMLElement createContainer() {
        Div column = (Div) Window.getDocument().createElement("div");
        column.setClassName(ColumnSize.MD_12.getCssName());
        return column;
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
    
    @Override
    public Optional<IsWidget> getComponentPart(HTMLElement column, LayoutComponent layoutComponent, String partId) {
        FieldDefinition field = getFieldForLayoutComponent(layoutComponent);
        FieldLayoutComponent dragComponent = getFieldLayoutComponentForField(field);
        if (dragComponent != null) {
            Widget columnWidget = ElementWrapperWidget.getWidget(column);
            RenderingContext componentContext = new RenderingContext(layoutComponent, columnWidget);
            return dragComponent.getContentPart(partId, componentContext);
        }
        return Optional.empty();
    }

    protected LayoutDragComponent lookupComponent(LayoutComponent layoutComponent) {
        Class<? extends LayoutDragComponent> clazz = componentsCache.get(layoutComponent.getDragTypeName());
        if (clazz == null) {
            SyncBeanDef dragTypeDef = beanManager.lookupBeans(layoutComponent.getDragTypeName()).iterator().next();

            componentsCache.put(layoutComponent.getDragTypeName(),
                                dragTypeDef.getBeanClass());

            clazz = dragTypeDef.getBeanClass();
        }

        LayoutDragComponent dragComponent = instance.select(clazz).get();

        if (dragComponent instanceof FieldLayoutComponent) {
            FieldLayoutComponent fieldComponent = (FieldLayoutComponent) dragComponent;

            FieldDefinition field = getFieldForLayoutComponent(layoutComponent);
            fieldComponent.init(renderingContext,
                                field);

            layoutComponents.add(fieldComponent);
        }

        return dragComponent;
    }

    public List<FieldLayoutComponent> getLayoutFields() {
        return layoutComponents;
    }

    public FieldLayoutComponent getFieldLayoutComponentForField(FieldDefinition field) {
        for (FieldLayoutComponent component : layoutComponents) {
            if (component.getField().equals(field)) {
                return component;
            }
        }
        return null;
    }

    public void clear() {
        layoutComponents.clear();
        instance.destroyAll();
    }
    
    private FieldDefinition getFieldForLayoutComponent(LayoutComponent layoutComponent) {
        FieldDefinition field = renderingContext.getRootForm().getFieldById(layoutComponent.getProperties().get(
                FieldLayoutComponent.FIELD_ID));
        return field;
    }
}
