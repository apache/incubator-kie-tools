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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.CallbackFunction;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.OPEN_CLASS;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
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

    @Mock
    private Element menuElement;

    @Mock
    private DOMTokenList menuElementClassList;

    @Captor
    private ArgumentCaptor<CallbackFunction> hiddenCallbackHandlerCaptor;

    private BootstrapSelectDropDownMonitor monitor;

    @Before
    public void setup() {
        this.monitor = spy(new BootstrapSelectDropDownMonitor(showCommand, hideCommand));
        when(monitor.kieDataTypeSelect()).thenReturn(jQuerySelectPicker);
        doReturn(menuElement).when(monitor).getMenuElement();
        menuElement.classList = menuElementClassList;
    }

    @Test
    public void testShow() {
        monitor.show(TITLE);

        verify(showCommand).execute(eq(TITLE));
    }

    @Test
    public void testHideWhenDropDownIsShown() {

        doReturn(true).when(monitor).isDropDownVisible();

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

        doReturn(false).when(monitor).isDropDownVisible();

        monitor.hide();

        verify(jQuerySelectPicker).off(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_SHOWN_EVENT));
        verify(jQuerySelectPicker).off(eq(BootstrapSelectDropDownMonitor.BOOTSTRAP_SELECT_HIDDEN_EVENT));

        verify(hideCommand).execute();

        verifyNoMoreInteractions(jQuerySelectPicker);
    }

    @Test
    public void testIsVisible() {
        when(menuElementClassList.contains(OPEN_CLASS)).thenReturn(true);
        final boolean actual = monitor.isDropDownVisible();
        assertTrue(actual);
    }

    @Test
    public void testIsVisibleWhenIsNotVisible() {
        when(menuElementClassList.contains(OPEN_CLASS)).thenReturn(false);
        final boolean actual = monitor.isDropDownVisible();
        assertFalse(actual);
    }
}
