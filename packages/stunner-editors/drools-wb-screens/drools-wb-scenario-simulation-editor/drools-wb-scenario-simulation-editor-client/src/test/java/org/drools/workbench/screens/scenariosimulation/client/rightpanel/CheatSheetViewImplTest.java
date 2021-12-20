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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CheatSheetViewImplTest {

    private CheatSheetViewImpl cheatSheetView;

    @Mock
    private CheatSheetPresenter cheatSheetPresenterMock;


    @Mock
    private DivElement ruleCheatSheetMock;

    @Mock
    private DivElement dmnCheatSheetMock;

    @Mock
    private Style ruleCheatSheetStyleMock;

    @Mock
    private Style dmnCheatSheetStyleMock;

    @Before
    public void setup() {
        this.cheatSheetView = spy(new CheatSheetViewImpl() {
            {
                this.ruleCheatSheet = ruleCheatSheetMock;
                this.dmnCheatSheet = dmnCheatSheetMock;
            }
        });
        cheatSheetView.init(cheatSheetPresenterMock);
        when(ruleCheatSheetMock.getStyle()).thenReturn(ruleCheatSheetStyleMock);
        when(dmnCheatSheetMock.getStyle()).thenReturn(dmnCheatSheetStyleMock);
    }

    @Test
    public void setRuleCheatSheetContent() {
        cheatSheetView.setRuleCheatSheetContent();
        verify(ruleCheatSheetStyleMock, times(1)).setDisplay(Style.Display.BLOCK);
        verify(dmnCheatSheetStyleMock, times(1)).setDisplay(Style.Display.NONE);
    }

    @Test
    public void setDMNCheatSheetContent() {
        cheatSheetView.setDMNCheatSheetContent();
        verify(ruleCheatSheetStyleMock, times(1)).setDisplay(Style.Display.NONE);
        verify(dmnCheatSheetStyleMock, times(1)).setDisplay(Style.Display.BLOCK);
    }


    @Test
    public void reset() {
        cheatSheetView.reset();
        verify(ruleCheatSheetStyleMock, times(1)).setDisplay(Style.Display.NONE);
        verify(dmnCheatSheetStyleMock, times(1)).setDisplay(Style.Display.NONE);
    }
}