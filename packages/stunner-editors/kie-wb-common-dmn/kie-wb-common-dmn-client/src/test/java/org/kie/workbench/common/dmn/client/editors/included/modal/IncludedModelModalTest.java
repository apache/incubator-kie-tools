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

package org.kie.workbench.common.dmn.client.editors.included.modal;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter;
import org.kie.workbench.common.dmn.client.editors.included.commands.AddIncludedModelCommand;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdown;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModal.WIDTH;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelModalTest {

    @Mock
    private IncludedModelModal.View view;

    @Mock
    private DMNAssetsDropdown dropdown;

    @Mock
    private ImportRecordEngine recordEngine;

    @Mock
    private IncludedModelsPagePresenter grid;

    @Mock
    private DMNIncludeModelsClient client;

    @Mock
    private EventSourceMock<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    @Mock
    private EventSourceMock<RefreshDecisionComponents> refreshPMMLComponentsEvent;

    @Mock
    private EventSourceMock<RefreshDataTypesListEvent> refreshDataTypesListEvent;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private SessionManager sessionManager;

    @Captor
    private ArgumentCaptor<RefreshDataTypesListEvent> refreshDataTypesListArgumentCaptor;

    @Captor
    private ArgumentCaptor<BaseIncludedModelActiveRecord> includedModelActiveRecordArgumentCaptor;

    private IncludedModelModalFake modal;

    @Before
    public void setup() {
        modal = spy(new IncludedModelModalFake(view, dropdown, recordEngine));
        modal.init(grid);
    }

    @Test
    public void testSetup() {

        final HTMLElement htmlElement = mock(HTMLElement.class);

        doReturn(htmlElement).when(modal).getInitializedDropdownElement();
        doNothing().when(modal).superSetup();
        doNothing().when(modal).setWidth(WIDTH);

        modal.setup();

        verify(modal).superSetup();
        verify(modal).setWidth(WIDTH);
        verify(view).init(modal);
        verify(view).setupAssetsDropdown(htmlElement);
    }

    @Test
    public void testShow() {
        doNothing().when(modal).superShow();

        modal.show();

        verify(dropdown).loadAssets();
        verify(view).initialize();
        verify(view).disableIncludeButton();
        verify(modal).superShow();
    }

    @Test
    public void testGetInitializedDropdownElement() {

        final Command onValueChanged = mock(Command.class);
        final HTMLElement expectedElement = mock(HTMLElement.class);
        doReturn(onValueChanged).when(modal).getOnValueChanged();
        when(dropdown.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = modal.getInitializedDropdownElement();

        verify(dropdown).initialize();
        verify(dropdown).registerOnChangeHandler(onValueChanged);
        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testInclude() {

        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final AddIncludedModelCommand command = mock(AddIncludedModelCommand.class);
        final KieAssetsDropdownItem dropdownItem = mock(KieAssetsDropdownItem.class);

        when(dropdown.getValue()).thenReturn(Optional.of(dropdownItem));

        doReturn(canvasHandler).when(modal).getCanvasHandler();
        doReturn(command).when(modal).createAddIncludedModelCommand(dropdownItem);

        doNothing().when(modal).hide();

        modal.include();

        verify(sessionCommandManager).execute(canvasHandler, command);
    }

    @Test
    public void testGetCanvasHandler() {

        final ClientSession currentSession = mock(ClientSession.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);

        when(sessionManager.getCurrentSession()).thenReturn(currentSession);
        when(currentSession.getCanvasHandler()).thenReturn(canvasHandler);

        final AbstractCanvasHandler currentCanvasHandler = modal.getCanvasHandler();
        assertEquals(canvasHandler, currentCanvasHandler);
    }

    @Test
    public void testCreateIncludedModel() {
        doTestCreateAddIncludedModelCommand("file", "file");
    }

    @Test
    public void testCreateIncludedModelWithWhitespace() {
        doTestCreateAddIncludedModelCommand("   file   ", "file");
    }

    private void doTestCreateAddIncludedModelCommand(final String name,
                                                     final String expectedName) {

        final KieAssetsDropdownItem value = mock(KieAssetsDropdownItem.class);

        when(view.getModelNameInput()).thenReturn(name);

        final AddIncludedModelCommand command = modal.createAddIncludedModelCommand(value);

        assertEquals(value, command.getValue());
        assertEquals(grid, command.getPresenter().get());
        assertEquals(refreshDecisionComponentsEvent, command.getRefreshDecisionComponentsEvent());
        assertEquals(refreshDataTypesListEvent, command.getRefreshDataTypesListEvent());
        assertEquals(recordEngine, command.getRecordEngine());
        assertEquals(client, command.getClient());
        assertEquals(expectedName, command.getModelName());
    }

    @Test
    public void testHide() {
        doNothing().when(modal).superHide();

        modal.hide();

        verify(modal).superHide();
        verify(dropdown).clear();
    }

    @Test
    public void testOnValueChangedWhenValuesAreValid() {
        doReturn(true).when(modal).isValidValues();

        modal.getOnValueChanged().execute();

        verify(view).enableIncludeButton();
    }

    @Test
    public void testOnValueChangedWhenValuesAreNotValid() {
        doReturn(false).when(modal).isValidValues();

        modal.getOnValueChanged().execute();

        verify(view).disableIncludeButton();
    }

    @Test
    public void testIsValidValuesWhenModelNameIsBlank() {
        when(view.getModelNameInput()).thenReturn("");
        assertFalse(modal.isValidValues());
    }

    @Test
    public void testIsValidValuesWhenDropDownIsNotPresent() {
        when(view.getModelNameInput()).thenReturn("name");
        when(dropdown.getValue()).thenReturn(Optional.empty());
        assertFalse(modal.isValidValues());
    }

    @Test
    public void testIsValidValuesWhenItReturnsTrue() {
        when(view.getModelNameInput()).thenReturn("name");
        when(dropdown.getValue()).thenReturn(Optional.of(mock(KieAssetsDropdownItem.class)));
        assertTrue(modal.isValidValues());
    }

    class IncludedModelModalFake extends IncludedModelModal {

        IncludedModelModalFake(final View view,
                               final DMNAssetsDropdown dropdown,
                               final ImportRecordEngine recordEngine) {
            super(view, dropdown, recordEngine, client, refreshDataTypesListEvent, refreshDecisionComponentsEvent,
                    refreshPMMLComponentsEvent, sessionCommandManager, sessionManager);
        }

        @Override
        protected void setWidth(final String width) {
            // empty.
        }
    }
}
