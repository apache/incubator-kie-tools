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


package org.kie.workbench.common.widgets.client.search.component;

import com.google.gwt.user.client.Random;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.search.common.EditorSearchIndex;
import org.kie.workbench.common.widgets.client.search.common.Searchable;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SearchBarComponentTest {

    @Mock
    private SearchBarComponent.View view;

    @Mock
    private EditorSearchIndex<Searchable> index;

    private SearchBarComponent<Searchable> component;

    @Before
    public void setup() {
        component = spy(new SearchBarComponent<>(view));
        component.init(index);
    }

    @Test
    public void testSetup() {
        component.setup();
        verify(view).init(component);
    }

    @Test
    public void testGetView() {
        assertEquals(view, component.getView());
    }

    @Test
    public void testSearchWhenTermIsEmpty() {

        final String term = "";

        component.search(term);

        verify(index, never()).search(term);
        verify(component).updateViewNumber();
    }

    @Test
    public void testSearchWhenTermIsNotEmpty() {

        final String term = "something";

        component.search(term);

        verify(index).search(term);
        verify(component).updateViewNumber();
    }

    @Test
    public void testSetSearchButtonVisibility() {
        final boolean visible = Random.nextBoolean();

        component.setSearchButtonVisibility(visible);

        verify(view).setSearchButtonVisibility(visible);
    }

    @Test
    public void testNextResult() {

        component.nextResult();

        verify(index).nextResult();
        verify(component).updateViewNumber();
    }

    @Test
    public void testPreviousResult() {

        component.previousResult();

        verify(index).previousResult();
        verify(component).updateViewNumber();
    }

    @Test
    public void testResetIndex() {

        component.closeIndex();

        verify(index).close();
        verify(component).updateViewNumber();
    }

    @Test
    public void testUpdateViewNumber() {

        final int currentResultNumber = 2;
        final int totalOfResultsNumber = 4;

        when(index.getCurrentResultNumber()).thenReturn(currentResultNumber);
        when(index.getTotalOfResultsNumber()).thenReturn(totalOfResultsNumber);

        component.updateViewNumber();

        verify(view).setCurrentResultNumber(currentResultNumber);
        verify(view).setTotalOfResultsNumber(totalOfResultsNumber);
    }
}
