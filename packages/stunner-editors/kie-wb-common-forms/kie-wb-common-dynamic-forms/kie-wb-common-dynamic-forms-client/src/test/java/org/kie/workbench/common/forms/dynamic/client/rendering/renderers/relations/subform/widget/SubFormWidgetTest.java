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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SubFormWidgetTest {

    @Mock
    private DynamicFormRenderer formRenderer;

    @Mock
    private FlowPanel formContent;

    @InjectMocks
    @Spy
    private SubFormWidget subFormWidget;

    @Mock
    protected FormRenderingContext context;

    @Before
    public void init() {
        subFormWidget.init();

        verify(formContent).add(formRenderer);
    }

    @Test
    public void testRender() {
        subFormWidget.render(context);

        verify(formRenderer).render(same(context));
    }

    @Test
    public void testGetValue() {
        subFormWidget.getValue();

        verify(formRenderer).getModel();
    }

    @Test
    public void testSetValue() {
        final Object value = new Object();

        subFormWidget.setValue(value);
        verify(formRenderer).bind(same(value));
    }

    @Test
    public void testClear() {
        subFormWidget.clear();

        verify(formRenderer).unBind();
    }

    @Test
    public void testFlush() {
        subFormWidget.flush();

        verify(formRenderer).flush();
    }

    @Test
    public void testAddFieldChangeHandler() {
        FieldChangeHandler handler = mock(FieldChangeHandler.class);

        subFormWidget.addFieldChangeHandler(handler);
        verify(formRenderer).addFieldChangeHandler(same(handler));
    }

    @Test
    public void testSetReadOnly() {
        subFormWidget.setReadOnly(true);
        verify(formRenderer).switchToMode(eq(RenderMode.READ_ONLY_MODE));

        subFormWidget.setReadOnly(false);
        verify(formRenderer).switchToMode(eq(RenderMode.EDIT_MODE));
    }

    @Test
    public void testIsValid() {
        subFormWidget.isValid();

        verify(formRenderer).isValid();
    }
}
