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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParametersEditorImplTest {

    private static final int ROW_INDEX = 0;

    private static final int COLUMN_INDEX = 1;

    private static final String PARAMETER_NAME = "name";

    @Mock
    private ParametersEditorView view;

    @Mock
    private HasParametersControl control;

    @Mock
    private InformationItem parameter;

    @Captor
    private ArgumentCaptor<Command> commandCaptor;

    @Captor
    private ArgumentCaptor<String> parameterNameCaptor;

    private ParametersEditorView.Presenter presenter;

    private List<InformationItem> parameters = Collections.emptyList();

    @Before
    public void setup() {
        this.presenter = new ParametersEditorImpl(view);
    }

    @Test
    public void testGetElement() {
        presenter.getElement();

        verify(view).getElement();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBindNullControl() {
        presenter.bind(null,
                       ROW_INDEX,
                       COLUMN_INDEX);

        verify(view, never()).setParameters(anyList());
    }

    @Test
    public void testBindNonNullControl() {
        when(control.getParameters()).thenReturn(parameters);

        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);

        verify(view).setParameters(eq(parameters));
    }

    @Test
    public void testShowNullControl() {
        presenter.show();

        verify(view, never()).show();
    }

    @Test
    public void testShowNonNullControl() {
        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);
        presenter.show();

        verify(view).show();
    }

    @Test
    public void testHideNullControl() {
        presenter.hide();

        verify(view, never()).hide();
    }

    @Test
    public void testHideNonNullControl() {
        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);
        presenter.hide();

        verify(view).hide();
    }

    @Test
    public void testAddParameterNullControl() {
        presenter.addParameter();

        verify(control, never()).addParameter(any(Command.class));
    }

    @Test
    public void testAddParameterNonNullControl() {
        when(control.getParameters()).thenReturn(parameters);

        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);
        //Binding sets the parameters, so reset
        reset(view);

        presenter.addParameter();

        verify(control).addParameter(commandCaptor.capture());

        commandCaptor.getValue().execute();

        verify(view).setParameters(eq(parameters));
    }

    @Test
    public void testRemoveParameterNullControl() {
        presenter.removeParameter(parameter);

        verify(control, never()).removeParameter(any(InformationItem.class),
                                                 any(Command.class));
    }

    @Test
    public void testRemoveParameterNonNullControl() {
        when(control.getParameters()).thenReturn(parameters);

        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);
        //Binding sets the parameters, so reset
        reset(view);

        presenter.removeParameter(parameter);

        verify(control).removeParameter(eq(parameter),
                                        commandCaptor.capture());

        commandCaptor.getValue().execute();

        verify(view).setParameters(eq(parameters));
    }

    @Test
    public void testUpdateParameterNameNullControl() {
        presenter.updateParameterName(parameter,
                                      PARAMETER_NAME);

        verify(control, never()).updateParameterName(any(InformationItem.class),
                                                     any(String.class));
    }

    @Test
    public void testUpdateParameterNameNonNullControl() {
        when(control.getParameters()).thenReturn(parameters);

        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);

        presenter.updateParameterName(parameter,
                                      PARAMETER_NAME);

        verify(control).updateParameterName(eq(parameter),
                                            parameterNameCaptor.capture());

        assertThat(parameterNameCaptor.getValue()).isEqualTo(PARAMETER_NAME);
    }
}
