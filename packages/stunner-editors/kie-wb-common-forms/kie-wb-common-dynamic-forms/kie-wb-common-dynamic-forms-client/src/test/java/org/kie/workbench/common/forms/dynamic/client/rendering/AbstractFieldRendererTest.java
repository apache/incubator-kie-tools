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

import java.util.List;

import org.assertj.core.api.Assertions;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayer;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractFieldRendererTest<R extends FieldRenderer, F extends FieldDefinition, G extends FormGroup> {

    protected static final String NAMESPACE = "ns";

    @Mock
    protected ManagedInstance<G> managedInstance;

    @Mock
    protected ManagedInstance<G> formGroupsInstance;

    @Mock
    protected ConfigErrorDisplayer errorDisplayer;

    @Mock
    protected FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    @Mock
    protected FormRenderingContext context;

    protected F fieldDefinition;
    protected R renderer;

    @Before
    public void init() {
        when(context.getRenderMode()).thenReturn(RenderMode.EDIT_MODE);
        when(context.getNamespace()).thenReturn(NAMESPACE);

        renderer = getRendererInstance();
        fieldDefinition = spy(getFieldDefinition());

        renderer.init(context, fieldDefinition);
    }

    @Test
    public void testRender() {
        renderer.renderWidget();

        verify(errorDisplayer, never()).render(anyList());
        verify(wrapperWidgetUtil).getWidget(eq(renderer), Mockito.<HTMLElement>any());
    }

    @Test
    public void testGetName() {
        checkName(renderer.getName());
    }

    protected void checkName(String name) {
        assertEquals(fieldDefinition.getFieldType().getTypeName(), name);
    }

    protected void testRenderWithConfigErrors(String expectedError) {
        renderer.renderWidget();

        ArgumentCaptor<List> errorCaptor = ArgumentCaptor.forClass(List.class);
        verify(errorDisplayer).render((java.util.List<String>) errorCaptor.capture());

        Assertions.assertThat(errorCaptor.getValue())
                .isNotNull()
                .hasSize(1)
                .containsExactly(expectedError);

        verify(wrapperWidgetUtil, never()).getWidget(eq(renderer), Mockito.<HTMLElement>any());
    }

    @Test
    public void testDestroy() {
        renderer.preDestroy();

        verify(wrapperWidgetUtil).clear(eq(renderer));
        verify(formGroupsInstance).destroyAll();
    }

    protected abstract R getRendererInstance();

    protected abstract F getFieldDefinition();
}
