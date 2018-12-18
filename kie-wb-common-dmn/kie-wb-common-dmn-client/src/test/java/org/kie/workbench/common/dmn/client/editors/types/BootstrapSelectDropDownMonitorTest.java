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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.CallbackFunction;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JQuerySelectPicker.class})
public class BootstrapSelectDropDownMonitorTest {

    private static final Optional<String> TITLE = Optional.of("title");

    @Mock
    private JQuerySelectPicker jQuerySelectPicker;

    @Mock
    private JQuerySelectPickerEvent event;

    @Mock
    private ParameterizedCommand<Optional<String>> showCommand;

    @Mock
    private Command hideCommand;

    @Captor
    private ArgumentCaptor<CallbackFunction> shownCallbackHandlerCaptor;

    @Captor
    private ArgumentCaptor<CallbackFunction> hiddenCallbackHandlerCaptor;

    private BootstrapSelectDropDownMonitor monitor;

    @Before
    public void setup() {
        mockStatic(JQuerySelectPicker.class);
        when(JQuerySelectPicker.$(anyString())).thenReturn(jQuerySelectPicker);
        when(jQuerySelectPicker.on(anyString(), any(CallbackFunction.class))).thenReturn(jQuerySelectPicker);

        this.monitor = new BootstrapSelectDropDownMonitor(showCommand, hideCommand);
    }

    @Test
    public void testShow() {
        monitor.show(TITLE);

        verify(showCommand).execute(eq(TITLE));

        verify(jQuerySelectPicker).on(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_SHOWN_EVENT),
                                      any(CallbackFunction.class));
        verify(jQuerySelectPicker).on(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_HIDDEN_EVENT),
                                      any(CallbackFunction.class));

        assertThat(monitor.isSelectDropDownShown).isFalse();
    }

    @Test
    public void testShowEventSetup() {
        monitor.show(TITLE);

        verify(jQuerySelectPicker).on(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_SHOWN_EVENT),
                                      shownCallbackHandlerCaptor.capture());
        verify(jQuerySelectPicker).on(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_HIDDEN_EVENT),
                                      hiddenCallbackHandlerCaptor.capture());

        //Mock Bootstrap Select firing event showing drop-down
        shownCallbackHandlerCaptor.getValue().call(event);

        assertThat(monitor.isSelectDropDownShown).isTrue();

        //Mock Bootstrap Select firing event hiding drop-down
        hiddenCallbackHandlerCaptor.getValue().call(event);

        assertThat(monitor.isSelectDropDownShown).isFalse();
    }

    @Test
    public void testHideWhenDropDownIsShown() {
        monitor.show(TITLE);

        //Mock Bootstrap Select firing event showing drop-down
        verify(jQuerySelectPicker).on(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_SHOWN_EVENT),
                                      shownCallbackHandlerCaptor.capture());
        shownCallbackHandlerCaptor.getValue().call(event);

        reset(jQuerySelectPicker);

        monitor.hide();

        verify(jQuerySelectPicker).off(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_SHOWN_EVENT));

        verify(jQuerySelectPicker).on(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_HIDDEN_EVENT),
                                      hiddenCallbackHandlerCaptor.capture());

        verify(hideCommand, never()).execute();

        //Mock Bootstrap Select firing event hiding drop-down
        hiddenCallbackHandlerCaptor.getValue().call(event);

        verify(jQuerySelectPicker).off(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_HIDDEN_EVENT));

        verify(hideCommand).execute();

        verifyNoMoreInteractions(jQuerySelectPicker);
    }

    @Test
    public void testHideWhenDropDownIsNotShown() {
        monitor.show(TITLE);

        reset(jQuerySelectPicker);

        monitor.hide();

        verify(jQuerySelectPicker).off(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_SHOWN_EVENT));
        verify(jQuerySelectPicker).off(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_HIDDEN_EVENT));

        verify(hideCommand).execute();

        verifyNoMoreInteractions(jQuerySelectPicker);
    }
}
