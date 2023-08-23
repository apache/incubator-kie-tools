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

package org.kie.workbench.common.dmn.client.editors.search;

import java.util.List;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditor;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNEditorSearchIndexTest {

    @Mock
    private DMNGraphSubIndex graphSubIndex;

    @Mock
    private DMNGridSubIndex gridSubIndex;

    @Mock
    private DMNDataTypesSubIndex dataTypesSubIndex;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNSession dmnSession;

    @Mock
    private ExpressionEditor expressionEditor;

    private List<DMNSearchableElement> expectedElements = asList(mock(DMNSearchableElement.class), mock(DMNSearchableElement.class));

    private DMNEditorSearchIndex searchIndex;

    @Before
    public void setup() {
        searchIndex = spy(new DMNEditorSearchIndex(graphSubIndex, gridSubIndex, dataTypesSubIndex, sessionManager, null));

        when(sessionManager.getCurrentSession()).thenReturn(dmnSession);
        when(dmnSession.getExpressionEditor()).thenReturn(expressionEditor);
    }

    @Test
    public void testInit() {

        final Command noResultsFoundCallback = mock(Command.class);
        final Command searchClosedCallback = mock(Command.class);

        doReturn(noResultsFoundCallback).when(searchIndex).getNoResultsFoundCallback();
        doReturn(searchClosedCallback).when(searchIndex).getSearchClosedCallback();

        searchIndex.init();

        verify(searchIndex).registerSubIndex(graphSubIndex);
        verify(searchIndex).registerSubIndex(gridSubIndex);
        verify(searchIndex).setNoResultsFoundCallback(noResultsFoundCallback);
        verify(searchIndex).setSearchClosedCallback(searchClosedCallback);
    }

    @Test
    public void testGetNoResultsFoundCallbackWhenDataTypesTabIsActive() {

        searchIndex.setIsDataTypesTabActiveSupplier(() -> true);

        searchIndex.getNoResultsFoundCallback().execute();

        verify(dataTypesSubIndex).onNoResultsFound();
    }

    @Test
    public void testGetNoResultsFoundCallbackWhenExpressionEditorIsActive() {

        when(expressionEditor.isActive()).thenReturn(true);

        searchIndex.getNoResultsFoundCallback().execute();

        verify(gridSubIndex).onNoResultsFound();
    }

    @Test
    public void testGetNoResultsFoundCallbackWhenExpressionEditorIsNotActive() {

        when(expressionEditor.isActive()).thenReturn(false);

        searchIndex.getNoResultsFoundCallback().execute();

        verify(graphSubIndex).onNoResultsFound();
    }

    @Test
    public void testSearchClosedCallbackExpressionEditorIsActive() {
        when(expressionEditor.isActive()).thenReturn(true);

        searchIndex.getSearchClosedCallback().execute();

        verify(gridSubIndex).onSearchClosed();
    }

    @Test
    public void testSearchClosedCallbackDataTypeEditorIsActive() {
        searchIndex.setIsDataTypesTabActiveSupplier(() -> true);

        searchIndex.getSearchClosedCallback().execute();

        verify(dataTypesSubIndex).onSearchClosed();
    }

    @Test
    public void testSearchClosedCallbackGraphEditorIsActive() {
        when(expressionEditor.isActive()).thenReturn(false);

        searchIndex.getSearchClosedCallback().execute();

        verify(graphSubIndex).onSearchClosed();
    }

    @Test
    public void testGetSearchableElementsWhenDataTypesTabIsActive() {

        when(expressionEditor.isActive()).thenReturn(true);
        when(dataTypesSubIndex.getSearchableElements()).thenReturn(expectedElements);
        when(gridSubIndex.getSearchableElements()).thenReturn(emptyList());
        when(graphSubIndex.getSearchableElements()).thenReturn(emptyList());
        searchIndex.setIsDataTypesTabActiveSupplier(() -> true);

        final List<DMNSearchableElement> actualElements = searchIndex.getSearchableElements();

        assertEquals(expectedElements, actualElements);
    }

    @Test
    public void testGetSearchableElementsWhenExpressionEditorIsActive() {

        when(expressionEditor.isActive()).thenReturn(true);
        when(gridSubIndex.getSearchableElements()).thenReturn(expectedElements);
        when(graphSubIndex.getSearchableElements()).thenReturn(emptyList());

        final List<DMNSearchableElement> actualElements = searchIndex.getSearchableElements();

        assertEquals(expectedElements, actualElements);
    }

    @Test
    public void testGetSearchableElementsWhenExpressionEditorIsNotActive() {

        when(expressionEditor.isActive()).thenReturn(false);
        when(gridSubIndex.getSearchableElements()).thenReturn(emptyList());
        when(graphSubIndex.getSearchableElements()).thenReturn(expectedElements);

        final List<DMNSearchableElement> actualElements = searchIndex.getSearchableElements();

        assertEquals(expectedElements, actualElements);
    }

    @Test
    public void testGetSearchableElementsWhenSessionIsNull() {

        when(sessionManager.getCurrentSession()).thenReturn(null);
        when(gridSubIndex.getSearchableElements()).thenReturn(emptyList());
        when(graphSubIndex.getSearchableElements()).thenReturn(expectedElements);

        final List<DMNSearchableElement> actualElements = searchIndex.getSearchableElements();

        assertEquals(expectedElements, actualElements);
    }
}
