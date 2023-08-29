/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.infra.ColumnSizeBuilder;

import static org.kie.workbench.common.forms.dynamic.client.rendering.FormGeneratorDriver.CONTAINER_TAG;
import static org.kie.workbench.common.forms.dynamic.client.rendering.FormGeneratorDriver.ROW_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormGeneratorDriverTest {

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private ManagedInstance<LayoutDragComponent> instance;

    @Mock
    private FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    @Mock
    private Document document;

    @Mock
    private FormRenderingContext context;

    private FormGeneratorDriver driver;

    @Before
    public void init() {
        when(beanManager.lookupBeans(Mockito.<String>any())).thenAnswer(invocationOnMock -> {
            SyncBeanDef beanDef = mock(SyncBeanDef.class);
            when(beanDef.getBeanClass()).thenReturn(FieldLayoutComponent.class);
            return Collections.singletonList(beanDef);
        });

        when(instance.select(any(Class.class))).thenAnswer(invocationOnMock -> {
            ManagedInstance<FieldLayoutComponent> nestedInstance = mock(ManagedInstance.class);
            FieldLayoutComponent component = mock(FieldLayoutComponent.class);
            when(nestedInstance.get()).thenReturn(component);
            return nestedInstance;
        });

        when(document.createElement(Mockito.<String>any())).thenAnswer(invocationOnMock -> mock(HTMLElement.class));

        driver = new FormGeneratorDriver(beanManager, instance, wrapperWidgetUtil, document) {
            @Override
            FieldDefinition getFieldForLayoutComponent(LayoutComponent layoutComponent) {
                return new TextBoxFieldDefinition();
            }

            @Override
            public FieldLayoutComponent getFieldLayoutComponentForField(FieldDefinition field) {
                return driver.getLayoutFields().get(0);
            }
        };

        driver.setRenderingContext(context);
    }

    @Test
    public void testCreateContainers() {
        HTMLElement container = driver.createContainer();

        verify(document).createElement(eq(CONTAINER_TAG));
        Assert.assertNotNull(container);
        verify(container).setClassName(ColumnSize.MD_12.getCssName());

        HTMLElement row = driver.createRow(new LayoutRow());
        verify(document, times(2)).createElement(eq(CONTAINER_TAG));
        Assert.assertNotNull(row);
        verify(row).setClassName(eq(ROW_CLASS));

        LayoutColumn layoutColumn = new LayoutColumn("12");
        HTMLElement column = driver.createColumn(layoutColumn);
        Assert.assertNotNull(column);
        verify(column).setClassName(ColumnSizeBuilder.buildColumnSize(12));
    }

    @Test
    public void testCreateComponent() {
        HTMLElement column = mock(HTMLElement.class);

        LayoutComponent layoutComponent = new LayoutComponent(FieldLayoutComponent.class.getName());

        driver.createComponent(column, layoutComponent);

        verify(beanManager).lookupBeans(Mockito.<String>any());
        verify(instance).select(eq(FieldLayoutComponent.class));
        verify(wrapperWidgetUtil).getWidget(same(driver), any(HTMLElement.class));

        Assertions.assertThat(driver.getLayoutFields())
                .hasSize(1);

        FieldLayoutComponent fieldLayoutComponent = driver.getLayoutFields().get(0);

        verify(fieldLayoutComponent).init(eq(context), any());
        verify(fieldLayoutComponent).getShowWidget(any());

        // checking a second try
        layoutComponent = new LayoutComponent(FieldLayoutComponent.class.getName());

        driver.createComponent(column, layoutComponent);

        verify(beanManager, times(1)).lookupBeans(Mockito.<String>any());
        verify(instance, times(2)).select(eq(FieldLayoutComponent.class));
        verify(wrapperWidgetUtil, times(2)).getWidget(same(driver), any(HTMLElement.class));

        Assertions.assertThat(driver.getLayoutFields())
                .hasSize(2);

        fieldLayoutComponent = driver.getLayoutFields().get(1);

        verify(fieldLayoutComponent).init(eq(context), any());
        verify(fieldLayoutComponent).getShowWidget(any());
    }

    @Test
    public void testClear() {
        driver.clear();

        verify(instance).destroyAll();
        verify(wrapperWidgetUtil).clear(same(driver));
    }
}
