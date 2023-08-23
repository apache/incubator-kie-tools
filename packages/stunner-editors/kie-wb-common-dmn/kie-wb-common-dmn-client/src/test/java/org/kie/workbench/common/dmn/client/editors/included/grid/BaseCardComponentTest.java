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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import javax.enterprise.event.Event;

import com.google.gwt.dom.client.Style.HasCssName;
import elemental2.dom.HTMLElement;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.commands.RemoveIncludedModelCommand;
import org.kie.workbench.common.dmn.client.editors.included.commands.RenameIncludedModelCommand;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.gwtbootstrap3.client.ui.constants.IconType.DOWNLOAD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseCardComponentTest<C extends BaseCardComponent<R, V>, V extends BaseCardComponent.ContentView, R extends BaseIncludedModelActiveRecord> {

    @Mock
    protected EventSourceMock<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    @Mock
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    protected SessionManager sessionManager;

    @Mock
    protected ImportRecordEngine recordEngine;

    @Mock
    protected DMNIncludeModelsClient client;

    @Mock
    protected Event<RefreshDataTypesListEvent> refreshDataTypesListEvent;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    protected C card;

    protected V cardView;

    @Before
    public void setup() {
        final ClientSession clientSession = mock(ClientSession.class);
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(sessionManager.getCurrentSession()).thenReturn(clientSession);
        cardView = getCardView();
        card = spy(getCard(cardView));
    }

    protected abstract V getCardView();

    protected abstract C getCard(final V cardView);

    protected abstract Class<R> getActiveRecordClass();

    protected abstract BaseIncludedModelActiveRecord prepareIncludedModelMock();

    protected abstract void doCheckRemoveIncludedModelCommandType(final RemoveIncludedModelCommand command);

    @Test
    public void testInit() {
        card.init();
        verify(cardView).init(card);
    }

    @Test
    public void testSetup() {
        final DMNCardsGridComponent expectedGrid = mock(DMNCardsGridComponent.class);
        final R expectedIncludedModel = mock(getActiveRecordClass());

        when(expectedIncludedModel.getNamespace()).thenReturn("://namespace");

        card.setup(expectedGrid, expectedIncludedModel);

        final DMNCardsGridComponent actualGrid = card.getGrid();
        final BaseIncludedModelActiveRecord actualIncludedModel = card.getIncludedModel();

        verify(card).refreshView();
        assertEquals(expectedGrid, actualGrid);
        assertEquals(expectedIncludedModel, actualIncludedModel);
    }

    @Test
    public void testRefreshView() {
        final DMNCardsGridComponent gridMock = mock(DMNCardsGridComponent.class);
        final BaseIncludedModelActiveRecord includedModel = prepareIncludedModelMock();
        final String path = "/bla/bla/bla/111111111111111222222222222222333333333333333444444444444444/file.dmn";
        final String expectedPathReference = "...111111222222222222222333333333333333444444444444444/file.dmn";

        when(includedModel.getNamespace()).thenReturn(path);
        doReturn(includedModel).when(card).getIncludedModel();

        doReturn(gridMock).when(card).getGrid();
        when(gridMock.presentPathAsLink()).thenReturn(false);

        card.refreshView();

        verify(cardView, times(1)).setPath(expectedPathReference);

        reset(cardView);

        when(gridMock.presentPathAsLink()).thenReturn(true);

        card.refreshView();

        verify(cardView, times(1)).setPath(expectedPathReference);

        reset(cardView);

        when(gridMock.presentPathAsLink()).thenReturn(true);
        when(includedModel.getPath()).thenReturn(path);

        card.refreshView();

        verify(cardView, times(1)).setPathLink(expectedPathReference);
    }

    @Test
    public void testGetIcon() {
        final IconType expected = DOWNLOAD;
        final HasCssName actual = card.getIcon();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTitle() {
        final BaseIncludedModelActiveRecord includedModel = prepareIncludedModelMock();
        final String expectedTitle = "file";

        when(includedModel.getName()).thenReturn(expectedTitle);
        doReturn(includedModel).when(card).getIncludedModel();

        final String actualTitle = card.getTitle();

        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    public void testGetUUID() {
        final BaseIncludedModelActiveRecord includedModel = prepareIncludedModelMock();
        final String expectedUUID = "123";

        when(includedModel.getUUID()).thenReturn(expectedUUID);
        doReturn(includedModel).when(card).getIncludedModel();

        final String actualUUID = card.getUUID();

        assertEquals(expectedUUID, actualUUID);
    }

    @Test
    public void testGetContent() {
        final HTMLElement expectedContent = mock(HTMLElement.class);

        when(cardView.getElement()).thenReturn(expectedContent);

        final HTMLElement actualContent = card.getContent();

        assertEquals(expectedContent, actualContent);
    }

    @Test
    public void testOnTitleChanged() {

        final ArgumentCaptor<RenameIncludedModelCommand> captor = ArgumentCaptor.forClass(RenameIncludedModelCommand.class);
        final String newName = "new name";

        when(sessionCommandManager.execute(any(), any())).thenReturn(CanvasCommandResultBuilder.SUCCESS);

        final boolean result = card.onTitleChanged().apply(newName);

        verify(sessionCommandManager).execute(eq(canvasHandler), captor.capture());

        final RenameIncludedModelCommand renameCommand = captor.getValue();

        assertEquals(card.getIncludedModel(), renameCommand.getIncludedModel());
        assertEquals(card.getGrid(), renameCommand.getGrid());
        assertEquals(refreshDecisionComponentsEvent, renameCommand.getRefreshDecisionComponentsEvent());
        assertEquals(newName, renameCommand.getNewName());
        assertTrue(result);
    }

    @Test
    public void testOnTitleChangedWhenNameIsNotValid() {

        final ArgumentCaptor<RenameIncludedModelCommand> captor = ArgumentCaptor.forClass(RenameIncludedModelCommand.class);
        final String newName = "new name";

        when(sessionCommandManager.execute(any(), any())).thenReturn(CanvasCommandResultBuilder.failed());

        final boolean result = card.onTitleChanged().apply(newName);

        verify(sessionCommandManager).execute(eq(canvasHandler), captor.capture());

        final RenameIncludedModelCommand renameCommand = captor.getValue();

        assertEquals(card.getIncludedModel(), renameCommand.getIncludedModel());
        assertEquals(card.getGrid(), renameCommand.getGrid());
        assertEquals(refreshDecisionComponentsEvent, renameCommand.getRefreshDecisionComponentsEvent());
        assertEquals(newName, renameCommand.getNewName());
        assertFalse(result);
    }

    @Test
    public void testTruncateWhenItIsTruncated() {
        final String actualTruncate = card.truncate("123456", 5);
        final String expectedTruncate = "...23456";

        assertEquals(expectedTruncate, actualTruncate);
    }

    @Test
    public void testTruncateWhenItIsNotTruncated() {
        final String actualTruncate = card.truncate("12345", 5);
        final String expectedTruncate = "12345";

        assertEquals(expectedTruncate, actualTruncate);
    }

    @Test
    public void testRemove() {
        final DMNCardsGridComponent grid = mock(DMNCardsGridComponent.class);
        final BaseIncludedModelActiveRecord includedModel = prepareIncludedModelMock();
        final RemoveIncludedModelCommand command = mock(RemoveIncludedModelCommand.class);

        doReturn(includedModel).when(card).getIncludedModel();
        doReturn(grid).when(card).getGrid();
        doReturn(command).when(card).getRemoveCommand();

        card.remove();

        verify(sessionCommandManager).execute(canvasHandler, command);
    }

    @Test
    public void testGetSubTitleWhenPathIsNotEmpty() {
        final BaseIncludedModelActiveRecord includedModel = prepareIncludedModelMock();
        final String expected = "/src/path/kie/dmn";

        doReturn(includedModel).when(card).getIncludedModel();
        when(includedModel.getPath()).thenReturn(expected);

        final String actual = card.getSubTitle();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetSubTitleWhenPathIsEmpty() {
        final BaseIncludedModelActiveRecord includedModel = prepareIncludedModelMock();
        final String expected = "://namespace";

        doReturn(includedModel).when(card).getIncludedModel();
        when(includedModel.getPath()).thenReturn("");
        when(includedModel.getNamespace()).thenReturn(expected);

        final String actual = card.getSubTitle();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRemoveCommand() {

        final DMNCardsGridComponent expectedGrid = mock(DMNCardsGridComponent.class);
        final R expectedIncludedModel = mock(getActiveRecordClass());

        doNothing().when(card).refreshView();

        card.setup(expectedGrid, expectedIncludedModel);

        final RemoveIncludedModelCommand command = card.getRemoveCommand();

        doCheckRemoveIncludedModelCommandType(command);

        assertEquals(client, command.getClient());
        assertEquals(refreshDecisionComponentsEvent, command.getRefreshDecisionComponentsEvent());
        assertEquals(recordEngine, command.getRecordEngine());
        assertEquals(refreshDataTypesListEvent, command.getRefreshDataTypesListEvent());
        assertEquals(expectedIncludedModel, command.getIncludedModel());
    }

    @Test
    public void testOpenLinkFullPath() {
        final DMNCardsGridComponent gridMock = mock(DMNCardsGridComponent.class);
        final BaseIncludedModelActiveRecord includedModel = prepareIncludedModelMock();
        final String expected = "/src/path/kie/dmn";

        doReturn(gridMock).when(card).getGrid();
        when(gridMock.presentPathAsLink()).thenReturn(true);
        doReturn(includedModel).when(card).getIncludedModel();
        when(includedModel.getPath()).thenReturn(expected);

        card.openPathLink();

        verify(gridMock, times(1)).openPathLink(expected);
    }
}
