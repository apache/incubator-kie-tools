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
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DynamicFormRendererTest extends TestCase {

    private FieldLayoutComponent component;

    private FieldRenderer fieldRenderer;

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
        when(fieldRenderer.getInputWidget()).thenReturn(widget);

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
        doBind();

        unBind();
    }

    @Test
    public void testBindingAddingFieldChangeHandler() {
        doBind();

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

    protected void doBind() {
        Command callback = mock(Command.class);
        renderer.renderDefaultForm(employee,
                                   callback);

        verify(callback).execute();
        verify(view).render(any());
        verify(view).bind();
        verify(formHandler).setUp(any(Employee.class));
    }

    protected void unBind() {
        renderer.isValid();
        renderer.unBind();
        verify(formHandler).clear();
        verify(view).clear();
    }
}
