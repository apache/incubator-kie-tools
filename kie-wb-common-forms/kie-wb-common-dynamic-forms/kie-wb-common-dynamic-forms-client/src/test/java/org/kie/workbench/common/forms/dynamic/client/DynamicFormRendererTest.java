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

package org.kie.workbench.common.forms.dynamic.client;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper;
import org.kie.workbench.common.forms.dynamic.client.init.FormHandlerGeneratorManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget;
import org.kie.workbench.common.forms.dynamic.client.test.TestDynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.forms.dynamic.test.model.Employee;
import org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DynamicFormRendererTest extends TestCase {

    private FieldLayoutComponent component;

    private FieldRenderer fieldRenderer;

    @Mock
    private FormField formField;

    @GwtMock
    private SubFormWidget widget;

    @Mock
    private FieldChangeHandler changeHandler;

    @Mock
    private FormHandler formHandler;

    @Mock
    private MapModelBindingHelper helper;

    @Mock
    private DynamicFormModelGenerator dynamicFormModelGenerator;

    private DynamicFormRenderer.DynamicFormRendererView view;

    private DynamicFormRenderer renderer;

    private Employee employee = new Employee();

    @Before
    public void initTest() {
        component = mock(FieldLayoutComponent.class);
        view = mock(DynamicFormRenderer.DynamicFormRendererView.class);
        fieldRenderer = mock(FieldRenderer.class);

        when(view.getFieldLayoutComponentForField(any(FieldDefinition.class))).thenReturn(component);

        when(component.getFieldRenderer()).thenReturn(fieldRenderer);

        when(formField.getWidget()).thenReturn(widget);

        when(fieldRenderer.getFormField()).thenReturn(formField);

        FormHandlerGeneratorManager generatorManager = new FormHandlerGeneratorManager(context -> formHandler,
                                                                                       context -> formHandler);

        when(dynamicFormModelGenerator.getContextForModel(any())).thenReturn(TestFormGenerator.getContextForEmployee(employee));

        renderer = new TestDynamicFormRenderer(view,
                                               generatorManager,
                                               dynamicFormModelGenerator);
        renderer.init();
        verify(view).setPresenter(renderer);
        renderer.asWidget();
        verify(view).asWidget();
    }

    @Test
    public void testBaseBinding() {
        doBind(1);

        unBind();
    }

    @Test
    public void testBindingAddingFieldChangeHandler() {
        doBind(1);

        renderer.addFieldChangeHandler(changeHandler);

        renderer.addFieldChangeHandler("name",
                                       changeHandler);

        renderer.addFieldChangeHandler("address",
                                       changeHandler);

        verify(formHandler).addFieldChangeHandler(any());
        verify(formHandler,
               times(2)).addFieldChangeHandler(anyString(),
                                               any());

        unBind();
    }

    @Test
    public void testRenderMultipleTimes() {
        doBind(1);

        doBind(2);

        verify(formHandler).clear();
        verify(view).clear();
    }

    @Test
    public void testFlush() {
        doBind(1);

        renderer.flush();

        verify(formHandler).maybeFlush();
    }

    @Test
    public void testFlushWithoutInitializing() {
        renderer.flush();

        verify(formHandler, never()).maybeFlush();
    }

    @Test
    public void testGetModel() {
        doBind(1);

        renderer.getModel();

        verify(formHandler).getModel();
    }

    @Test
    public void testGetModelWithoutInitializing() {
        renderer.getModel();

        verify(formHandler, never()).getModel();
    }

    protected void doBind(int times) {
        Command callback = mock(Command.class);
        renderer.renderDefaultForm(employee,
                                   callback);

        verify(callback, times(1)).execute();
        verify(view, times(times)).render(any());
        verify(view, times(times)).bind();
        verify(formHandler, times(times)).setUp(any(Employee.class));
    }

    protected void unBind() {
        renderer.isValid();
        renderer.unBind();
        verify(formHandler).clear();
        verify(view).clear();
    }
}
