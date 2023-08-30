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

package org.kie.workbench.common.dmn.client.editors.drd;

import java.util.Optional;

import javax.enterprise.event.Event;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLAnchorElement;
import org.gwtbootstrap3.client.ui.html.Span;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DRDNameChangerViewTest {

    private DRDNameChangerView drdNameChangerView;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private Event<DMNDiagramSelected> selectedEvent;

    @Mock
    private DivElement viewMode;

    @Mock
    private DivElement editMode;

    @Mock
    private HTMLAnchorElement returnToDRG;

    @Mock
    private Span drdName;

    @Mock
    private InputElement drdNameInput;

    @Mock
    private SessionPresenter.View sessionPresenterView;

    @Mock
    private DMNDiagramSelected dmnDiagramSelected;

    @Mock
    private DMNDiagramElement dmnDiagramElement;

    @Mock
    private Style viewStyle;

    @Mock
    private Style editStyle;

    @Mock
    private ClickEvent clickEvent;

    @Mock
    private KeyDownEvent keyDownEvent;

    @Mock
    private BlurEvent blurEvent;

    @Before
    public void setUp() {
        when(editMode.getStyle()).thenReturn(editStyle);
        when(viewMode.getStyle()).thenReturn(viewStyle);
        when(dmnDiagramsSession.getCurrentDMNDiagramElement()).thenReturn(Optional.empty());

        drdNameChangerView = spy(new DRDNameChangerView(dmnDiagramsSession, selectedEvent, viewMode, editMode, returnToDRG, drdName, drdNameInput));
        drdNameChangerView.setSessionPresenterView(sessionPresenterView);
    }

    @Test
    public void testOnSettingCurrentDMNDiagramWhenIsGlobal() {
        when(dmnDiagramsSession.isGlobalGraphSelected()).thenReturn(true);

        drdNameChangerView.onSettingCurrentDMNDiagramElement(dmnDiagramSelected);

        verify(dmnDiagramsSession, times(2)).isGlobalGraphSelected();
        verify(drdNameChangerView, times(1)).hideDRDNameChanger();
        // One call is on setup method
        verify(drdNameChangerView, times(1)).showDRDNameChanger();
        verify(drdName, times(0)).setText(anyString());

        verify(viewStyle, never()).setDisplay(any());
        verify(editStyle, never()).setDisplay(any());
    }

    @Test
    public void testOnSettingCurrentDMNDiagramWhenIsDRD() {
        when(dmnDiagramsSession.isGlobalGraphSelected()).thenReturn(false);
        when(dmnDiagramSelected.getDiagramElement()).thenReturn(dmnDiagramElement);
        when(dmnDiagramElement.getName()).thenReturn(new Name());

        drdNameChangerView.onSettingCurrentDMNDiagramElement(dmnDiagramSelected);

        verify(dmnDiagramsSession, times(2)).isGlobalGraphSelected();
        verify(drdNameChangerView, times(0)).hideDRDNameChanger();
        // One call is on setup method
        verify(drdNameChangerView, times(2)).showDRDNameChanger();
        verify(drdName, times(1)).setText("");
        verify(viewStyle, times(1)).setDisplay(Style.Display.BLOCK);
        verify(editStyle, times(1)).setDisplay(Style.Display.NONE);
    }

    @Test
    public void testOnClickReturnToDRG() {
        drdNameChangerView.onClickReturnToDRG(clickEvent);

        verify(selectedEvent, times(1)).fire(any(DMNDiagramSelected.class));
    }

    @Test
    public void testEnableEdit() {
        final String spanText = "SPAN TEXT";
        when(drdName.getText()).thenReturn(spanText);

        drdNameChangerView.enableEdit(clickEvent);

        verify(drdNameInput, times(1)).setValue(spanText);
        verify(drdNameInput, times(1)).focus();
        verify(viewStyle, times(1)).setDisplay(Style.Display.NONE);
        verify(editStyle, times(1)).setDisplay(Style.Display.BLOCK);
    }

    @Test
    public void testOnInputTextKeyPress() {
        final NativeEvent nativeEvent = mock(NativeEvent.class);
        when(keyDownEvent.getNativeEvent()).thenReturn(nativeEvent);
        when(nativeEvent.getKeyCode()).thenReturn(13);

        drdNameChangerView.onInputTextKeyPress(keyDownEvent);

        verify(dmnDiagramsSession, times(1)).getCurrentDMNDiagramElement();
    }

    @Test
    public void testOnInputTextBlur() {
        drdNameChangerView.onInputTextBlur(blurEvent);

        verify(dmnDiagramsSession, times(1)).getCurrentDMNDiagramElement();
    }

    @Test
    public void testSaveForTheCurrentDiagramChangedValue() {
        String originalValue = "original";
        String nameValue = "changed";

        testSaveForTheCurrentDiagram(originalValue, nameValue);
    }

    @Test
    public void testSaveForTheCurrentDiagramSameValue() {
        String originalValue = "original";

        testSaveForTheCurrentDiagram(originalValue, originalValue);
    }

    private void testSaveForTheCurrentDiagram(String originalValue, String nameValue) {
        Name nameMock = mock(Name.class);
        boolean changed = !originalValue.equals(nameValue);

        when(dmnDiagramsSession.getCurrentDMNDiagramElement()).thenReturn(Optional.of(dmnDiagramElement));
        when(dmnDiagramElement.getName()).thenReturn(nameMock);
        when(drdNameInput.getValue()).thenReturn(nameValue);
        when(nameMock.getValue()).thenReturn(originalValue);

        drdNameChangerView.saveForTheCurrentDiagram();

        verify(nameMock, times(changed ? 1 : 0)).setValue(nameValue);
        verify(selectedEvent, times(changed ? 1 : 0)).fire(any(DMNDiagramSelected.class));
        verify(viewStyle, times(changed ? 0 : 1)).setDisplay(changed ? any() : Style.Display.BLOCK);
        verify(editStyle, times(changed ? 0 : 1)).setDisplay(changed ? any() : Style.Display.NONE);
    }
}
