/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.testscenario.client.firedrules;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@WithClassesToStub(Text.class)
@RunWith(GwtMockitoTestRunner.class)
public class ShowFiredRulesButtonTest {

    @Mock
    private FiredRulesTable firedRulesTable;

    @Mock
    private Button hideFiredRulesButton;

    @Captor
    private ArgumentCaptor<ClickHandler> clickCaptor;

    private ShowFiredRulesButton button;

    @Before
    public void setUp() throws Exception {
        button = spy(new ShowFiredRulesButton());
    }

    @Test
    public void testInit() throws Exception {
        button.init(firedRulesTable, hideFiredRulesButton);

        verify(button).setText(TestScenarioConstants.INSTANCE.ShowFiredRules());
        verify(button).setIcon(IconType.ANGLE_RIGHT);
        verify(button).setVisible(true);
        verify(button).addClickHandler(clickCaptor.capture());
        verify(button, never()).showFiredRules();

        clickCaptor.getValue().onClick(null);

        verify(button).showFiredRules();
    }

    @Test
    public void testShowFiredRules() throws Exception {
        button.init(firedRulesTable, hideFiredRulesButton);
        verify(button).setVisible(true);

        button.showFiredRules();

        verify(firedRulesTable).setVisible(true);
        verify(hideFiredRulesButton).setVisible(true);
        verify(button).setVisible(false);
    }
}
