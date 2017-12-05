/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutInstance;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.generator.BootstrapLayoutGenerator;

@Any
@Dependent
public class FormLayoutGenerator extends BootstrapLayoutGenerator {

    private List<FieldLayoutComponent> layoutComponents = new ArrayList<>();

    private Map<String, Class<? extends LayoutDragComponent>> componentsCache = new HashMap<>();

    private SyncBeanManager beanManager;

    private ManagedInstance<LayoutDragComponent> instance;

    private FormRenderingContext renderingContext;

    @Inject
    public FormLayoutGenerator(SyncBeanManager beanManager,
                               ManagedInstance<LayoutDragComponent> instance) {
        this.beanManager = beanManager;
        this.instance = instance;
    }

    public HTMLElement buildLayout(FormRenderingContext renderingContext) {
        this.renderingContext = renderingContext;
        layoutComponents.clear();
        if (renderingContext == null || renderingContext.getRootForm() == null) {
            return createContainer();
        }
        LayoutInstance layoutInstance = build(renderingContext.getRootForm().getLayoutTemplate());
        return layoutInstance.getElement();
    }

    protected HTMLElement createContainer() {
        Div column = (Div) Window.getDocument().createElement("div");
        column.setClassName(ColumnSize.MD_12.getCssName());
        return column;
    }

    @Override
    public LayoutDragComponent lookupLayoutDragComponent(LayoutComponent layoutComponent) {

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

            FieldDefinition field = renderingContext.getRootForm().getFieldById(layoutComponent.getProperties().get(
                    FieldLayoutComponent.FIELD_ID));
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
}
