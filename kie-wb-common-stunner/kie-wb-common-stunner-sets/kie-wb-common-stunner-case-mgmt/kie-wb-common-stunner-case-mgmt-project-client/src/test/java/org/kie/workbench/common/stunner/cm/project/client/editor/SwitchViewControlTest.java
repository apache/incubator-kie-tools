/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.project.client.editor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SwitchViewControlTest {

    @Mock
    private Runnable caseViewSwitchHandler;

    @Mock
    private Runnable processViewSwitchHandler;

    private SwitchViewControl tested;

    @Before
    public void setUp() {
        tested = new SwitchViewControl("COLUMNS", "Case View", caseViewSwitchHandler,
                                       "SITEMAP", "Process View", processViewSwitchHandler);
    }

    @Test
    public void testConfigureButton() {
        final Button button = mock(Button.class);
        final Button otherButton = mock(Button.class);
        final Runnable runnable = mock(Runnable.class);

        tested.configureButton(button,
                               "COLUMNS",
                               true,
                               mock(Tooltip.class),
                               "tooltip",
                               otherButton,
                               runnable);

        verify(button, times(1)).setType(eq(ButtonType.INFO));

        ArgumentCaptor<ClickHandler> clickHandlerArgumentCaptor = ArgumentCaptor.forClass(ClickHandler.class);
        verify(button, times(1)).addClickHandler(clickHandlerArgumentCaptor.capture());

        reset(button);

        clickHandlerArgumentCaptor.getValue().onClick(mock(ClickEvent.class));

        verify(button, times(1)).setType(eq(ButtonType.INFO));
        verify(otherButton, times(1)).setType(eq(ButtonType.LINK));
        verify(runnable, times(1)).run();
    }
}