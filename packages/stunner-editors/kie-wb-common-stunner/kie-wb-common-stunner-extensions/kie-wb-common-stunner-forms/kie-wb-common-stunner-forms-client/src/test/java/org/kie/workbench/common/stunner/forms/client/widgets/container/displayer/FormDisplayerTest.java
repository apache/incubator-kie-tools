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


package org.kie.workbench.common.stunner.forms.client.widgets.container.displayer;

import java.util.Arrays;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.BindableProxyProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.Form;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.domainChangeHandlers.DomainObjectFieldChangeHandlerRegistry;
import org.kie.workbench.common.stunner.forms.context.PathAwareFormContext;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyVararg;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FormDisplayerTest {

    private static final String ELEMENT_UID = "UID";

    private static final String FIELD1 = "field1";
    private static final String FIELD2 = "field2";

    @Mock
    private NodeImpl<Definition> node;

    @Mock
    private Definition nodeContent;

    @Mock
    private Path path;

    @Mock
    private FieldChangeHandler fieldChangeHandler;

    @Mock
    private BindableProxyProvider proxyProvider;

    @Mock
    private BindableProxy<Object> proxy;

    private FirstDefinition elementDefinition = new FirstDefinition();

    @Mock
    private FormDisplayerView view;

    @Mock
    private DynamicFormRenderer formRenderer;

    @Mock
    private DynamicFormModelGenerator dynamicFormModelGenerator;

    @Mock
    private Form form;

    @Mock
    private FormField field1;

    @Mock
    private CollapsibleFormGroup containerField1;

    @Mock
    private FormField field2;

    @Mock
    private CollapsibleFormGroup containerField2;

    @Mock
    private StaticModelFormRenderingContext renderingContext;

    @Mock
    private DomainObjectFieldChangeHandlerRegistry registry;

    private int renderedCount = 0;

    private FormDisplayer displayer;

    @Before
    public void init() {

        when(proxyProvider.getBindableProxy()).thenReturn((BindableProxy) proxy);
        when(proxyProvider.getBindableProxy(any(FirstDefinition.class))).thenReturn((BindableProxy) proxy);
        when(proxyProvider.getBindableProxy(any(SecondDefinition.class))).thenReturn((BindableProxy) proxy);

        when(proxy.deepUnwrap()).thenReturn(elementDefinition);

        when(node.getUUID()).thenReturn(ELEMENT_UID);
        when(node.getContent()).thenReturn(nodeContent);
        when(nodeContent.getDefinition()).thenReturn(elementDefinition);

        BindableProxyFactory.addBindableProxy(FirstDefinition.class,
                                              proxyProvider);

        BindableProxyFactory.addBindableProxy(SecondDefinition.class,
                                              proxyProvider);

        when(field1.getFieldName()).thenReturn(FIELD1);
        when(field1.getContainer()).thenReturn(containerField1);

        when(field2.getFieldName()).thenReturn(FIELD2);
        when(field2.getContainer()).thenReturn(containerField2);

        when(form.getFields()).thenReturn(Arrays.asList(field1, field2));

        doAnswer(invocationOnMock -> renderedCount++)
                .when(formRenderer)
                .render(any());

        when(formRenderer.getCurrentForm()).thenReturn(form);
        when(dynamicFormModelGenerator.getContextForModel(any(), anyVararg())).thenReturn(renderingContext);

        displayer = new FormDisplayer(view, formRenderer, dynamicFormModelGenerator, registry);
    }

    @Test
    public void testBasicFunctions() {

        displayer.init();

        verify(view, times(1)).init(displayer);

        displayer.show();
        verify(view).show();

        displayer.hide();
        verify(view).hide();

        displayer.getElement();
        verify(view).getElement();

        assertEquals(formRenderer, displayer.getRenderer());

        displayer.dispose();
        verify(formRenderer).unBind();

        displayer.destroy();
        verify(formRenderer, times(2)).unBind();
    }

    @Test
    public void testRenderElement() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        testRender(1, 1, 1, 0, 1, renderMode);
    }

    @Test
    public void testRenderElementForSecondTime() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        // Rendering form for first time
        testRender(1, 1, 1, 0, 1, renderMode);

        when(formRenderer.isInitialized()).thenReturn(true);
        when(formRenderer.isValid()).thenReturn(true);

        // Rendering for second time, checks are the same but the view.show() that must be called twice
        testRender(2, 2, 2, 1, 2, renderMode);
    }

    @Test
    public void testRenderElementForSecondTimeWithDefinitionChange() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        testRender(1, 1, 1, 0, 1, renderMode);

        SecondDefinition newDefinition = new SecondDefinition();
        when(nodeContent.getDefinition()).thenReturn(newDefinition);
        when(formRenderer.isInitialized()).thenReturn(true);

        testRender(2, 2, 2, 1, 2, renderMode);
    }

    @Test
    public void testRenderElementForSecondTimeWithValidationFailure() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        // Rendering form for first time
        testRender(1, 1, 1, 0, 1, renderMode);

        when(formRenderer.isInitialized()).thenReturn(true);
        when(formRenderer.isValid()).thenReturn(false);

        // Rendering for second time, checks are the same but the view.show() that must be called twice
        testRender(2, 2, 2, 1, 2, renderMode);
    }

    @Test
    public void testRenderAndCollapsableStatus() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        when(formRenderer.getCurrentForm()).thenAnswer(invocationOnMock -> {
            if (renderedCount > 0) {
                return form;
            }
            return null;
        });

        testRender(1, 1, 1, 0, 1, renderMode);

        verify(containerField1).expand();
        verify(containerField2, never()).expand();

        when(formRenderer.isInitialized()).thenReturn(true);
        when(formRenderer.isValid()).thenReturn(true);

        when(containerField1.isExpanded()).thenReturn(false);
        when(containerField2.isExpanded()).thenReturn(true);

        testRender(2, 2, 2, 1, 2, renderMode);

        verify(containerField1, times(1)).expand();
        verify(containerField2, times(1)).expand();
    }

    private void testRender(int renderingTimes, int initializedTimes, int newContextTimes, int boundTimes, int viewTimes, RenderMode renderMode) {
        displayer.render(node.getUUID(), node.getContent().getDefinition(), path, fieldChangeHandler, renderMode);

        verify(formRenderer, times(initializedTimes)).isInitialized();
        verify(formRenderer, times(boundTimes)).unBind();
        verify(dynamicFormModelGenerator, times(newContextTimes)).getContextForModel(elementDefinition);
        verify(formRenderer, times(renderingTimes)).render(any(PathAwareFormContext.class));
        verify(formRenderer, times(renderingTimes)).addFieldChangeHandler(fieldChangeHandler);
        verify(renderingContext, times(renderingTimes)).setRenderMode(renderMode);

        verify(view, times(viewTimes)).show();
    }

    public class FirstDefinition {

    }

    public class SecondDefinition {

    }
}
