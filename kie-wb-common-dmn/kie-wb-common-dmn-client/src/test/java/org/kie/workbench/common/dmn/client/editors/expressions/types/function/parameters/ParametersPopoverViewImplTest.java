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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ParametersPopoverViewImplTest {

    /**/
    private static final String PARAMETER1_NAME = "parameter1";

    private static final String PARAMETER2_NAME = "parameter2";

    private static final String PARAMETER_NAME_NEW = "new name";

    private static final QName PARAMETER_TYPE_REF_NEW = new QName(QName.NULL_NS_URI,
                                                                  BuiltInType.DATE.getName());

    @Mock
    private Div parametersContainer;

    @Mock
    private Div addParameter;

    @Mock
    private ManagedInstance<ParameterView> parameterViews;

    @Mock
    private ParameterView parameterView1;

    @Mock
    private ParameterView parameterView2;

    @Mock
    private ParametersPopoverView.Presenter presenter;

    @Mock
    private HTMLElement element;

    @Mock
    private Div popoverElement;

    @Mock
    private Div popoverContentElement;

    @Mock
    private JQueryProducer.JQuery<Popover> jQueryProducer;

    @Mock
    private Popover popover;

    @Mock
    private Event event;

    private ParametersPopoverViewImpl view;

    @Before
    public void setup() {
        this.view = spy(new ParametersPopoverViewImpl(parametersContainer,
                                                      addParameter,
                                                      parameterViews,
                                                      popoverElement,
                                                      popoverContentElement,
                                                      jQueryProducer));
        when(parameterViews.get()).thenReturn(parameterView1, parameterView2);
        when(parameterView1.getElement()).thenReturn(element);
        when(parameterView2.getElement()).thenReturn(element);

        doReturn(element).when(view).getElement();
        when(jQueryProducer.wrap(element)).thenReturn(popover);

        this.view.init(presenter);
    }

    @Test
    public void testSetParameters() {
        final InformationItem parameter1 = new InformationItem();
        final InformationItem parameter2 = new InformationItem();
        parameter1.getName().setValue(PARAMETER1_NAME);
        parameter2.getName().setValue(PARAMETER2_NAME);
        final List<InformationItem> parameters = Arrays.asList(parameter1, parameter2);

        view.setParameters(parameters);

        verify(parameterView1).setName(eq(PARAMETER1_NAME));
        verifyRemoveClickHandler(parameter1, parameterView1);
        verifyParameterNameChangeHandler(parameter1, parameterView1);
        verifyParameterTypeRefChangeHandler(parameter1, parameterView1);

        verify(parameterView2).setName(eq(PARAMETER2_NAME));
        verifyRemoveClickHandler(parameter2, parameterView2);
        verifyParameterNameChangeHandler(parameter2, parameterView2);
        verifyParameterTypeRefChangeHandler(parameter2, parameterView2);

        verify(parametersContainer, times(2)).appendChild(element);
    }

    private void verifyRemoveClickHandler(final InformationItem parameter,
                                          final ParameterView view) {
        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);

        verify(view).addRemoveClickHandler(commandArgumentCaptor.capture());

        commandArgumentCaptor.getValue().execute();

        verify(presenter).removeParameter(parameter);
    }

    @SuppressWarnings("unchecked")
    private void verifyParameterNameChangeHandler(final InformationItem parameter,
                                                  final ParameterView view) {
        final ArgumentCaptor<ParameterizedCommand> commandArgumentCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);

        verify(view).addParameterNameChangeHandler(commandArgumentCaptor.capture());

        commandArgumentCaptor.getValue().execute(PARAMETER_NAME_NEW);

        verify(presenter).updateParameterName(parameter, PARAMETER_NAME_NEW);
    }

    @SuppressWarnings("unchecked")
    private void verifyParameterTypeRefChangeHandler(final InformationItem parameter,
                                                     final ParameterView view) {
        final ArgumentCaptor<ParameterizedCommand> commandArgumentCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);

        verify(view).addParameterTypeRefChangeHandler(commandArgumentCaptor.capture());

        commandArgumentCaptor.getValue().execute(PARAMETER_TYPE_REF_NEW);

        verify(presenter).updateParameterTypeRef(parameter, PARAMETER_TYPE_REF_NEW);
    }

    @Test
    public void testFocusParameter() {
        final InformationItem parameter1 = new InformationItem();
        final InformationItem parameter2 = new InformationItem();

        view.setParameters(Arrays.asList(parameter1, parameter2));

        view.focusParameter(0);

        verify(parameterView1).focus();

        view.focusParameter(1);

        verify(parameterView2).focus();

        reset(parameterView1, parameterView2);
        view.focusParameter(-1);

        verify(parameterView1, never()).focus();
        verify(parameterView2, never()).focus();

        reset(parameterView1, parameterView2);
        view.focusParameter(2);

        verify(parameterView1, never()).focus();
        verify(parameterView2, never()).focus();
    }

    @Test
    public void testShow() {
        view.show(Optional.empty());

        verify(popover).show();
    }

    @Test
    public void testHideBeforeShown() {
        view.hide();

        verify(popover, never()).hide();
        verify(popover, never()).destroy();
    }

    @Test
    public void testHideAfterShown() {
        view.show(Optional.empty());
        view.hide();

        verify(popover).hide();
        verify(popover).destroy();
    }

    @Test
    public void testOnClickAddParameter() {
        view.onClickAddParameter(event);

        verify(presenter).addParameter();
    }
}
