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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import com.google.gwt.dom.client.Style.HasCssName;
import elemental2.dom.HTMLElement;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static java.util.Collections.emptyList;
import static org.gwtbootstrap3.client.ui.constants.IconType.DOWNLOAD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseCardComponentTest<C extends BaseCardComponent<R, V>, V extends BaseCardComponent.ContentView, R extends BaseIncludedModelActiveRecord> {

    @Mock
    protected EventSourceMock<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    protected C card;

    protected V cardView;

    @Before
    public void setup() {
        cardView = getCardView();
        card = spy(getCard(cardView));
    }

    protected abstract V getCardView();

    protected abstract C getCard(final V cardView);

    protected abstract Class<R> getActiveRecordClass();

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
        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
        final String path = "/bla/bla/bla/111111111111111222222222222222333333333333333444444444444444/file.dmn";

        when(includedModel.getNamespace()).thenReturn(path);
        doReturn(includedModel).when(card).getIncludedModel();

        card.refreshView();

        verify(cardView).setPath("...111111222222222222222333333333333333444444444444444/file.dmn");
    }

    @Test
    public void testGetIcon() {
        final IconType expected = DOWNLOAD;
        final HasCssName actual = card.getIcon();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTitle() {
        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
        final String expectedTitle = "file";

        when(includedModel.getName()).thenReturn(expectedTitle);
        doReturn(includedModel).when(card).getIncludedModel();

        final String actualTitle = card.getTitle();

        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    public void testGetUUID() {
        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
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
    public void testOnTitleChangedWhenIncludedModelIsValid() {
        doTestOnTitleChangedWhenIncludedModelIsValid("newName", "newName");
    }

    @Test
    public void testOnTitleChangedWhenIncludedModelIsValidWithWhitespace() {
        doTestOnTitleChangedWhenIncludedModelIsValid("   newName   ", "newName");
    }

    private void doTestOnTitleChangedWhenIncludedModelIsValid(final String newName,
                                                              final String expectedNewName) {
        final DMNCardsGridComponent grid = mock(DMNCardsGridComponent.class);
        final DMNIncludedModelActiveRecord includedModel = spy(new DMNIncludedModelActiveRecord(null));

        doReturn(true).when(includedModel).isValid();
        doReturn(emptyList()).when(includedModel).update();
        doReturn(includedModel).when(card).getIncludedModel();
        doReturn(grid).when(card).getGrid();

        final boolean titleChanged = card.onTitleChanged().apply(newName);

        assertEquals(expectedNewName, includedModel.getName());
        assertTrue(titleChanged);
        verify(includedModel).update();
        verify(grid).refresh();
        verify(card).refreshDecisionComponents();
    }

    @Test
    public void testOnTitleChangedWhenIncludedModelIsNotValid() {
        final DMNCardsGridComponent grid = mock(DMNCardsGridComponent.class);
        final DMNIncludedModelActiveRecord includedModel = spy(new DMNIncludedModelActiveRecord(null));
        final String newName = "newName";
        final String oldName = "oldName";

        includedModel.setName(oldName);
        doReturn(false).when(includedModel).isValid();
        doReturn(includedModel).when(card).getIncludedModel();
        doReturn(grid).when(card).getGrid();

        final boolean titleChanged = card.onTitleChanged().apply(newName);

        assertEquals(oldName, includedModel.getName());
        assertFalse(titleChanged);
        verify(includedModel, never()).update();
        verify(grid, never()).refresh();
        verify(card, never()).refreshDecisionComponents();
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
        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);

        doReturn(includedModel).when(card).getIncludedModel();
        doReturn(grid).when(card).getGrid();

        card.remove();

        verify(includedModel).destroy();
        verify(grid).refresh();
        verify(refreshDecisionComponentsEvent).fire(any(RefreshDecisionComponents.class));
    }

    @Test
    public void testGetSubTitleWhenPathIsNotEmpty() {
        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
        final String expected = "/src/path/kie/dmn";

        doReturn(includedModel).when(card).getIncludedModel();
        when(includedModel.getPath()).thenReturn(expected);

        final String actual = card.getSubTitle();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetSubTitleWhenPathIsEmpty() {
        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
        final String expected = "://namespace";

        doReturn(includedModel).when(card).getIncludedModel();
        when(includedModel.getPath()).thenReturn("");
        when(includedModel.getNamespace()).thenReturn(expected);

        final String actual = card.getSubTitle();

        assertEquals(expected, actual);
    }
}
