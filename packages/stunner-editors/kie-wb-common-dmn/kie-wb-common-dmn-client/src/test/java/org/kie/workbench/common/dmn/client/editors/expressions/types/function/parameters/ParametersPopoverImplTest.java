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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ParametersPopoverImplTest {

    private static final int ROW_INDEX = 0;

    private static final int COLUMN_INDEX = 1;

    private static final String PARAMETER_NAME = "name";

    private static final String PARAMETER_NAME_NEW = "new-name";

    private static final QName PARAMETER_TYPE_REF = new QName(QName.NULL_NS_URI,
                                                              BuiltInType.DATE.getName());

    @Mock
    private ParametersPopoverView view;

    @Mock
    private TranslationService translationService;

    @Mock
    private InformationItem parameter;

    @Captor
    private ArgumentCaptor<Command> commandCaptor;

    @Captor
    private ArgumentCaptor<String> parameterNameCaptor;

    @Captor
    private ArgumentCaptor<QName> parameterTYpeRefCaptor;

    private ParametersPopoverView.Presenter presenter;

    private final List<InformationItem> parameters = new ArrayList<>();

    protected class MockHasParametersControl implements HasParametersControl {

        @Override
        public List<InformationItem> getParameters() {
            return parameters;
        }

        @Override
        public void addParameter(final Command onSuccess) {
            parameters.add(new InformationItem());
        }

        @Override
        public void removeParameter(final InformationItem parameter,
                                    final Command onSuccess) {
            parameters.remove(parameter);
        }

        @Override
        public void updateParameterName(final InformationItem parameter,
                                        final String name,
                                        final Command onSuccess) {
        }

        @Override
        public void updateParameterTypeRef(final InformationItem parameter,
                                           final QName typeRef) {
        }
    }

    private HasParametersControl control = spy(new MockHasParametersControl());

    @Before
    public void setup() {
        this.presenter = new ParametersPopoverImpl(view, translationService);

        when(translationService.getTranslation(Mockito.<String>any())).thenAnswer(i -> i.getArguments()[0]);
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

        verify(view, never()).setParameters(Mockito.<List>any());
    }

    @Test
    public void testBindNonNullControl() {
        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);

        verify(view).setParameters(eq(parameters));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowNullControl() {
        presenter.show();

        verify(view, never()).show(Mockito.<Optional>any());
        verify(view, never()).focusParameter(Mockito.anyInt());
    }

    @Test
    public void testShowNonNullControl() {
        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);
        presenter.show();

        verify(view).show(eq(Optional.ofNullable(presenter.getPopoverTitle())));
        verify(view, never()).focusParameter(Mockito.anyInt());
    }

    @Test
    public void testShowNonNullControlWithParameters() {
        parameters.add(new InformationItem());

        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);
        presenter.show();

        verify(view).show(eq(Optional.ofNullable(presenter.getPopoverTitle())));
        verify(view).focusParameter(0);
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

        verify(control, never()).addParameter(Mockito.<Command>any());
        verify(view, never()).focusParameter(Mockito.anyInt());
    }

    @Test
    public void testAddParameterNonNullControl() {
        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);
        //Binding sets the parameters, so reset
        reset(view);

        presenter.addParameter();

        verify(control).addParameter(commandCaptor.capture());

        commandCaptor.getValue().execute();

        verify(view).setParameters(eq(parameters));
        verify(view).focusParameter(0);
    }

    @Test
    public void testRemoveParameterNullControl() {
        presenter.removeParameter(parameter);

        verify(control, never()).removeParameter(Mockito.<InformationItem>any(),
                                                 Mockito.<Command>any());
        verify(view, never()).focusParameter(Mockito.anyInt());
    }

    @Test
    public void testRemoveLastParameterNonNullControl() {
        parameters.add(parameter);

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
        verify(view, never()).focusParameter(Mockito.anyInt());
    }

    @Test
    public void testRemoveParameterNonNullControl() {
        parameters.add(new InformationItem());
        parameters.add(parameter);

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
        verify(view).focusParameter(0);
    }

    @Test
    public void testUpdateParameterNameNullControl() {
        presenter.updateParameterName(parameter,
                                      PARAMETER_NAME);

        verify(control, never()).updateParameterName(Mockito.<InformationItem>any(),
                                                     Mockito.<String>any(),
                                                     Mockito.<Command>any());
    }

    @Test
    public void testUpdateParameterNameNonNullControlWithChange() {
        final Name name = new Name(PARAMETER_NAME);
        when(parameter.getName()).thenReturn(name);
        parameters.add(parameter);

        assertUpdateParameterName(PARAMETER_NAME_NEW);
    }

    @Test
    public void testUpdateParameterNameNonNullControlWithChangeWithWhiteSpace() {
        final Name name = new Name(PARAMETER_NAME);
        when(parameter.getName()).thenReturn(name);
        parameters.add(parameter);

        assertUpdateParameterName("   " + PARAMETER_NAME_NEW + "  ");
    }

    private void assertUpdateParameterName(final String newName) {
        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);

        presenter.updateParameterName(parameter,
                                      newName);

        verify(control).updateParameterName(eq(parameter),
                                            parameterNameCaptor.capture(),
                                            commandCaptor.capture());

        assertThat(parameterNameCaptor.getValue()).isEqualTo(PARAMETER_NAME_NEW);

        commandCaptor.getValue().execute();

        verify(view).updateParameterName(eq(0), eq(PARAMETER_NAME_NEW));
    }

    @Test
    public void testUpdateParameterNameNonNullControlWithNoChange() {
        final Name name = new Name(PARAMETER_NAME);
        when(parameter.getName()).thenReturn(name);

        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);

        presenter.updateParameterName(parameter,
                                      PARAMETER_NAME);

        verify(control, never()).updateParameterName(Mockito.<InformationItem>any(),
                                                     Mockito.<String>any(),
                                                     Mockito.<Command>any());
    }

    @Test
    public void testUpdateParameterNameNonNullControlWithNoChangeWithWhiteSpace() {
        final Name name = new Name(PARAMETER_NAME);
        when(parameter.getName()).thenReturn(name);
        parameters.add(parameter);

        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);

        presenter.updateParameterName(parameter,
                                      "   " + PARAMETER_NAME + "   ");

        verify(view).updateParameterName(eq(0), eq(PARAMETER_NAME));
    }

    @Test
    public void testUpdateParameterTypeRefNullControl() {
        presenter.updateParameterTypeRef(parameter,
                                         PARAMETER_TYPE_REF);

        verify(control, never()).updateParameterTypeRef(Mockito.<InformationItem>any(),
                                                        Mockito.<QName>any());
    }

    @Test
    public void testUpdateParameterTypeRefNonNullControl() {
        presenter.bind(control,
                       ROW_INDEX,
                       COLUMN_INDEX);

        presenter.updateParameterTypeRef(parameter,
                                         PARAMETER_TYPE_REF);

        verify(control).updateParameterTypeRef(eq(parameter),
                                               parameterTYpeRefCaptor.capture());

        assertThat(parameterTYpeRefCaptor.getValue()).isEqualTo(PARAMETER_TYPE_REF);
    }
}
