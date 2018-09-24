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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.events.SetColumnValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.models.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RightPanelPresenterTest extends AbstractRightPanelTest {

    @Mock
    private RightPanelView mockRightPanelView;

    @Mock
    private DivElement mockListContainer;

    @Mock
    private ListGroupItemPresenter mockListGroupItemPresenter;
    @Mock
    private EventBus mockEventBus;

    private RightPanelPresenter rightPanelPresenter;
    private RightPanelPresenter rightPanelPresenterSpy;

    @Before
    public void setup() {
        super.setup();
        when(mockRightPanelView.getListContainer()).thenReturn(mockListContainer);
        when(mockListGroupItemPresenter.getDivElement(FACT_NAME, FACT_MODEL_TREE)).thenReturn(mockListContainer);
        this.rightPanelPresenter = new RightPanelPresenter(mockRightPanelView, mockListGroupItemPresenter);
        rightPanelPresenter.factTypeFieldsMap = mockTopLevelMap;
        rightPanelPresenter.eventBus = mockEventBus;
        rightPanelPresenterSpy = spy(rightPanelPresenter);
    }

    @Test
    public void onSetup() {
        rightPanelPresenter.setup();
        verify(mockRightPanelView, times(1)).init(rightPanelPresenter);
    }

    @Test
    public void getTitle() {
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.testTools(), rightPanelPresenter.getTitle());
    }

    @Test
    public void onClearSearch() {
        rightPanelPresenterSpy.onClearSearch();
        verify(mockRightPanelView, times(1)).clearInputSearch();
        verify(mockRightPanelView, times(1)).hideClearButton();
    }

    @Test
    public void onClearNameField() {
        rightPanelPresenterSpy.onClearNameField();
        verify(mockRightPanelView, times(1)).clearNameField();
    }

    @Test
    public void onClearStatus() {
        rightPanelPresenterSpy.onClearStatus();
        verify(rightPanelPresenterSpy, times(1)).onClearSearch();
        verify(rightPanelPresenterSpy, times(1)).onClearNameField();
        verify(rightPanelPresenterSpy, times(1)).clearList();
    }

    @Test
    public void getFactModelTree() {
        rightPanelPresenter.setFactTypeFieldsMap(mockTopLevelMap);
        String factName = getRandomFactModelTree(mockTopLevelMap, 0);
        FactModelTree retrieved = rightPanelPresenter.getFactModelTree(factName);
        assertNotNull(retrieved);
        assertEquals(mockTopLevelMap.get(factName), retrieved);
    }

    @Test
    public void setFactTypeFieldsMap() {
        rightPanelPresenterSpy.setFactTypeFieldsMap(mockTopLevelMap);
        verify(rightPanelPresenterSpy, times(mockTopLevelMap.size())).addListGroupItemView(anyString(), anyObject());
    }

    @Test
    public void onShowClearButton() {
        rightPanelPresenter.onShowClearButton();
        verify(mockRightPanelView, times(1)).showClearButton();
    }

    @Test
    public void setEventBus() {
        rightPanelPresenterSpy.setEventBus(mockEventBus);
        assertEquals(mockEventBus, rightPanelPresenterSpy.eventBus);
    }

    @Test
    public void addListGroupItemView() {
        rightPanelPresenter.addListGroupItemView(FACT_NAME, FACT_MODEL_TREE);
        verify(mockRightPanelView, times(1)).getListContainer();
        verify(mockListGroupItemPresenter, times(1)).getDivElement(eq(FACT_NAME), eq(FACT_MODEL_TREE));
        verify(mockListContainer, times(1)).appendChild(anyObject());
    }

    @Test
    public void onEnableEditorTab() {
        rightPanelPresenter.onEnableEditorTab(COLUMN_INDEX);
        verify(mockListGroupItemPresenter, times(1)).setDisabled(eq(false));
        assertEquals(COLUMN_INDEX, rightPanelPresenter.editingColumnIndex);
        verify(mockRightPanelView, times(1)).enableEditorTab();
    }

    @Test
    public void onDisableEditorTab() {
        rightPanelPresenter.onDisableEditorTab();
        verify(mockListGroupItemPresenter, times(1)).setDisabled(eq(true));
        assertEquals(-1, rightPanelPresenter.editingColumnIndex);
        verify(mockRightPanelView, times(1)).disableEditorTab();
    }

    @Test
    public void onModifyColumn() {
        rightPanelPresenter.editingColumnIndex = COLUMN_INDEX;
        rightPanelPresenter.onModifyColumn(FACT_NAME, VALUE, VALUE_CLASS_NAME);
        verify(mockEventBus, times(1)).fireEvent(isA(SetColumnValueEvent.class));
    }
}