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

package org.kie.workbench.common.stunner.forms.client.widgets.container.displayer;

import java.util.Collections;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.BindableProxyProvider;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFilterProviderManager;
import org.kie.workbench.common.stunner.forms.context.PathAwareFormContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormDisplayerTest {

    private static final String ELEMENT_UID = "UID";

    @Mock
    private NodeImpl node;

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
    private ManagedInstance<StunnerFilterProviderManager> managedInstanceFilterProviderManager;

    @Mock
    private StunnerFilterProviderManager filterProviderManager;

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
        when(managedInstanceFilterProviderManager.get()).thenReturn(filterProviderManager);
        when(filterProviderManager.getFilterForDefinition(any(), any(), any())).thenReturn(Collections.emptyList());

        displayer = new FormDisplayer(view, formRenderer, dynamicFormModelGenerator, managedInstanceFilterProviderManager);

        verify(view, times(1)).init(displayer);
    }

    @Test
    public void testBasicFunctions() {
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
        testRender(1, 1, 1, 0, 1);
    }

    @Test
    public void testRenderElementForSecondTime() {

        // Rendering form for first time
        testRender(1, 1, 1, 0, 1);

        when(formRenderer.isInitialized()).thenReturn(true);
        when(formRenderer.isValid()).thenReturn(true);

        // Rendering for second time, checks are the same but the view.show() that must be called twice
        testRender(2, 2, 2, 1, 2);
    }

    @Test
    public void testRenderElementForSecondTimeWithDefinitionChange() {

        testRender(1, 1, 1, 0, 1);

        SecondDefinition newDefinition = new SecondDefinition();
        when(nodeContent.getDefinition()).thenReturn(newDefinition);
        when(formRenderer.isInitialized()).thenReturn(true);

        testRender(2, 2, 2, 1, 2);
    }

    @Test
    public void testRenderElementForSecondTimeWithValidationFailure() {

        // Rendering form for first time
        testRender(1, 1, 1, 0, 1);

        when(formRenderer.isInitialized()).thenReturn(true);
        when(formRenderer.isValid()).thenReturn(false);

        // Rendering for second time, checks are the same but the view.show() that must be called twice
        testRender(2, 2, 2, 1, 2);
    }

    private void testRender(int renderingTimes, int initializedTimes, int newContextTimes, int boundTimes, int viewTimes) {
        displayer.render(node, path, fieldChangeHandler);

        verify(formRenderer, times(initializedTimes)).isInitialized();
        verify(formRenderer, times(boundTimes)).unBind();
        verify(dynamicFormModelGenerator, times(newContextTimes)).getContextForModel(elementDefinition);
        verify(filterProviderManager, times(newContextTimes)).getFilterForDefinition(any(), any(), any());
        verify(formRenderer, times(renderingTimes)).render(any(PathAwareFormContext.class));
        verify(formRenderer, times(renderingTimes)).addFieldChangeHandler(fieldChangeHandler);

        verify(view, times(viewTimes)).show();
    }

    public class FirstDefinition {

    }

    public class SecondDefinition {

    }
}
