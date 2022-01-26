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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import io.crysknife.client.BeanManager;
import io.crysknife.client.ManagedInstance;
import io.crysknife.client.SyncBeanDef;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Widget;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
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

    static final String CONTAINER_TAG = "div";
    static final String ROW_CLASS = "row";

    private BeanManager beanManager;
    private ManagedInstance<LayoutDragComponent> instance;
    private FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    private List<FieldLayoutComponent> layoutComponents = new ArrayList<>();
    private Map<String, Class<? extends LayoutDragComponent>> componentsCache = new HashMap<>();
    private FormRenderingContext renderingContext;

    @Inject
    public FormGeneratorDriver(BeanManager beanManager, ManagedInstance<LayoutDragComponent> instance, FormsElementWrapperWidgetUtil wrapperWidgetUtil) {
        this.beanManager = beanManager;
        this.instance = instance;
        this.wrapperWidgetUtil = wrapperWidgetUtil;
    }

    public FormRenderingContext getRenderingContext() {
        return renderingContext;
    }

    public void setRenderingContext(FormRenderingContext renderingContext) {
        this.renderingContext = renderingContext;
    }

    @Override
    public HTMLElement createContainer() {
        HTMLElement container = (HTMLElement)DomGlobal.document.createElement(CONTAINER_TAG);
        container.className = (ColumnSize.MD_12.getCssName());
        return container;
    }

    @Override
    public HTMLElement createRow(LayoutRow layoutRow) {
        HTMLElement row = (HTMLElement)DomGlobal.document.createElement(CONTAINER_TAG);
        row.className = (ROW_CLASS);
        return row;
    }

    @Override
    public HTMLElement createColumn(LayoutColumn layoutColumn) {
        HTMLElement column = (HTMLElement)DomGlobal.document.createElement(CONTAINER_TAG);
        String colSize = ColumnSizeBuilder.buildColumnSize(new Integer(layoutColumn.getSpan()));
        column.className = (colSize);
        return column;
    }

    @Override
    public IsWidget createComponent(HTMLElement column, LayoutComponent layoutComponent) {
        final LayoutDragComponent dragComponent = lookupComponent(layoutComponent);
        if (dragComponent != null) {
            Widget columnWidget = getWidget(column);
            RenderingContext componentContext = new RenderingContext(layoutComponent, columnWidget);
            return dragComponent.getShowWidget(componentContext);
        }
        return null;
    }

    private LayoutDragComponent lookupComponent(LayoutComponent layoutComponent) {
        Class<? extends LayoutDragComponent> clazz = componentsCache.get(layoutComponent.getDragTypeName());
        if (clazz == null) {
            SyncBeanDef dragTypeDef = beanManager.lookupBeans(layoutComponent.getDragTypeName()).iterator().next();
            componentsCache.put(layoutComponent.getDragTypeName(), dragTypeDef.getBeanClass());
            clazz = dragTypeDef.getBeanClass();
        }
        LayoutDragComponent dragComponent = instance.select(clazz).get();
        if (dragComponent instanceof FieldLayoutComponent) {
            FieldLayoutComponent fieldComponent = (FieldLayoutComponent) dragComponent;

            FieldDefinition field = getFieldForLayoutComponent(layoutComponent);

            fieldComponent.init(renderingContext, field);

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

    private Widget getWidget(HTMLElement element) {
        return wrapperWidgetUtil.getWidget(this, element);
    }

    public void clear() {
        layoutComponents.clear();
        instance.destroyAll();
        wrapperWidgetUtil.clear(this);
        componentsCache.clear();
        renderingContext = null;
    }

    FieldDefinition getFieldForLayoutComponent(LayoutComponent layoutComponent) {
        FieldDefinition field = renderingContext.getRootForm().getFieldById(layoutComponent.getProperties().get(
                FieldLayoutComponent.FIELD_ID));
        return field;
    }
}
