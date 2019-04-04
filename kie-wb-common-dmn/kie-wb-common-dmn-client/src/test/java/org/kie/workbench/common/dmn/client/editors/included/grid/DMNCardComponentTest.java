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
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.mockito.Mock;

import static java.util.Collections.emptyList;
import static org.gwtbootstrap3.client.ui.constants.IconType.DOWNLOAD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNCardComponentTest {

    @Mock
    private DMNCardComponent.ContentView contentView;

    private DMNCardComponent dmnCard;

    @Before
    public void setup() {
        dmnCard = spy(new DMNCardComponent(contentView));
    }

    @Test
    public void testInit() {
        dmnCard.init();
        verify(contentView).init(dmnCard);
    }

    @Test
    public void testSetup() {

        final DMNCardsGridComponent expectedGrid = mock(DMNCardsGridComponent.class);
        final IncludedModel expectedIncludedModel = mock(IncludedModel.class);

        when(expectedIncludedModel.getNamespace()).thenReturn("://namespace");

        dmnCard.setup(expectedGrid, expectedIncludedModel);

        final DMNCardsGridComponent actualGrid = dmnCard.getGrid();
        final IncludedModel actualIncludedModel = dmnCard.getIncludedModel();

        verify(dmnCard).refreshView();
        assertEquals(expectedGrid, actualGrid);
        assertEquals(expectedIncludedModel, actualIncludedModel);
    }

    @Test
    public void testRefreshView() {

        final IncludedModel includedModel = mock(IncludedModel.class);
        final String path = "/bla/bla/bla/111111111111111222222222222222333333333333333444444444444444/file.dmn";
        final int dataTypesCount = 12;
        final int drgElementsCount = 34;

        when(includedModel.getNamespace()).thenReturn(path);
        when(includedModel.getDataTypesCount()).thenReturn(dataTypesCount);
        when(includedModel.getDrgElementsCount()).thenReturn(drgElementsCount);
        doReturn(includedModel).when(dmnCard).getIncludedModel();

        dmnCard.refreshView();

        verify(contentView).setPath("...111111222222222222222333333333333333444444444444444/file.dmn");
        verify(contentView).setDataTypesCount(dataTypesCount);
        verify(contentView).setDrgElementsCount(drgElementsCount);
    }

    @Test
    public void testGetIcon() {

        final IconType expected = DOWNLOAD;
        final HasCssName actual = dmnCard.getIcon();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTitle() {

        final IncludedModel includedModel = mock(IncludedModel.class);
        final String expectedTitle = "file";

        when(includedModel.getName()).thenReturn(expectedTitle);
        doReturn(includedModel).when(dmnCard).getIncludedModel();

        final String actualTitle = dmnCard.getTitle();

        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    public void testGetUUID() {

        final IncludedModel includedModel = mock(IncludedModel.class);
        final String expectedUUID = "123";

        when(includedModel.getUUID()).thenReturn(expectedUUID);
        doReturn(includedModel).when(dmnCard).getIncludedModel();

        final String actualUUID = dmnCard.getUUID();

        assertEquals(expectedUUID, actualUUID);
    }

    @Test
    public void testGetContent() {

        final HTMLElement expectedContent = mock(HTMLElement.class);

        when(contentView.getElement()).thenReturn(expectedContent);

        final HTMLElement actualContent = dmnCard.getContent();

        assertEquals(expectedContent, actualContent);
    }

    @Test
    public void testOnTitleChangedWhenIncludedModelIsValid() {

        final DMNCardsGridComponent grid = mock(DMNCardsGridComponent.class);
        final IncludedModel includedModel = spy(new IncludedModel(null));
        final String newName = "newName";

        doReturn(true).when(includedModel).isValid();
        doReturn(emptyList()).when(includedModel).update();
        doReturn(includedModel).when(dmnCard).getIncludedModel();
        doReturn(grid).when(dmnCard).getGrid();

        final boolean titleChanged = dmnCard.onTitleChanged().apply(newName);

        assertEquals(newName, includedModel.getName());
        assertTrue(titleChanged);
        verify(includedModel).update();
        verify(grid).refresh();
    }

    @Test
    public void testOnTitleChangedWhenIncludedModelIsNotValid() {

        final DMNCardsGridComponent grid = mock(DMNCardsGridComponent.class);
        final IncludedModel includedModel = spy(new IncludedModel(null));
        final String newName = "newName";
        final String oldName = "oldName";

        includedModel.setName(oldName);
        doReturn(false).when(includedModel).isValid();
        doReturn(includedModel).when(dmnCard).getIncludedModel();
        doReturn(grid).when(dmnCard).getGrid();

        final boolean titleChanged = dmnCard.onTitleChanged().apply(newName);

        assertEquals(oldName, includedModel.getName());
        assertFalse(titleChanged);
        verify(includedModel, never()).update();
        verify(grid, never()).refresh();
    }

    @Test
    public void testTruncateWhenItIsTruncated() {

        final String actualTruncate = dmnCard.truncate("123456", 5);
        final String expectedTruncate = "...23456";

        assertEquals(expectedTruncate, actualTruncate);
    }

    @Test
    public void testTruncateWhenItIsNotTruncated() {

        final String actualTruncate = dmnCard.truncate("12345", 5);
        final String expectedTruncate = "12345";

        assertEquals(expectedTruncate, actualTruncate);
    }

    @Test
    public void testRemove() {

        final DMNCardsGridComponent grid = mock(DMNCardsGridComponent.class);
        final IncludedModel includedModel = mock(IncludedModel.class);

        doReturn(includedModel).when(dmnCard).getIncludedModel();
        doReturn(grid).when(dmnCard).getGrid();

        dmnCard.remove();

        verify(includedModel).destroy();
        verify(grid).refresh();
    }

    @Test
    public void testGetSubTitleWhenPathIsNotEmpty() {

        final IncludedModel includedModel = mock(IncludedModel.class);
        final String expected = "/src/path/kie/dmn";

        doReturn(includedModel).when(dmnCard).getIncludedModel();
        when(includedModel.getPath()).thenReturn(expected);

        final String actual = dmnCard.getSubTitle();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetSubTitleWhenPathIsEmpty() {

        final IncludedModel includedModel = mock(IncludedModel.class);
        final String expected = "://namespace";

        doReturn(includedModel).when(dmnCard).getIncludedModel();
        when(includedModel.getPath()).thenReturn("");
        when(includedModel.getNamespace()).thenReturn(expected);

        final String actual = dmnCard.getSubTitle();

        assertEquals(expected, actual);
    }
}
