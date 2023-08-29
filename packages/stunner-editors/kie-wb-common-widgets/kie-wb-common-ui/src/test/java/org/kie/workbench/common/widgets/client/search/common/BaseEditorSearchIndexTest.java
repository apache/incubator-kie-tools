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


package org.kie.workbench.common.widgets.client.search.common;

import java.util.List;
import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mvp.Command;

import static java.util.Arrays.asList;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class BaseEditorSearchIndexTest {

    @Mock
    private HasSearchableElements<FakeSearchable> hasSearchableElements1;

    @Mock
    private HasSearchableElements<FakeSearchable> hasSearchableElements2;

    @Mock
    private Command noResultsFoundCallback;

    @Mock
    private Command searchPerformedCallback;

    @Mock
    private Command searchClosedCallback;

    private Supplier<Integer> currentAssetHashcodeSupplier;

    private FakeEditorSearchIndex index;

    private List<FakeSearchable> searchableElements;

    private FakeSearchable searchable1;

    private FakeSearchable searchable2;

    private FakeSearchable searchable3;

    private FakeSearchable searchable4;

    @Before
    public void setup() {

        searchable1 = spy(new FakeSearchable("Element 1"));
        searchable2 = spy(new FakeSearchable("Element 2"));
        searchable3 = spy(new FakeSearchable("Element 3"));
        searchable4 = spy(new FakeSearchable("Element 4"));
        searchableElements = asList(searchable1, searchable2, searchable3, searchable4);
        currentAssetHashcodeSupplier = () -> 123;

        index = new FakeEditorSearchIndex(searchableElements);
        index.setNoResultsFoundCallback(noResultsFoundCallback);
        index.setSearchPerformedCallback(searchPerformedCallback);
        index.setSearchClosedCallback(searchClosedCallback);
        index.setCurrentAssetHashcodeSupplier(currentAssetHashcodeSupplier);
    }

    @Test
    public void testSearch() {

        index.search("Element");

        verify(searchable1).onFound();
        verify(searchable2, never()).onFound();
        verify(searchable3, never()).onFound();
        verify(searchable4, never()).onFound();
        verify(noResultsFoundCallback, never()).execute();

        verify(searchPerformedCallback).execute();
    }

    @Test
    public void testSearchWhenAnyElementIsFound() {

        index.search("Something");

        verify(searchable1, never()).onFound();
        verify(searchable2, never()).onFound();
        verify(searchable3, never()).onFound();
        verify(searchable4, never()).onFound();
        verify(noResultsFoundCallback).execute();
        verify(searchPerformedCallback).execute();
    }

    @Test
    public void testSearchWhenSearchIsTriggeredTwiceButUserHasUpdatedAsset() {

        index.search("Element");
        index.setCurrentAssetHashcodeSupplier(() -> currentAssetHashcodeSupplier.get() + 1);
        index.search("Element");

        verify(searchable1, Mockito.times(2)).onFound();
        verify(searchable2, never()).onFound();
        verify(searchable3, never()).onFound();
        verify(searchable4, never()).onFound();
        verify(noResultsFoundCallback, never()).execute();
    }

    @Test
    public void testSearchWhenNextElementIsHighlighted() {

        times(2, () -> index.search("Element"));

        // The first element is highlighted by the first search
        verify(searchable1).onFound();

        // The next element is highlighted by the second search
        verify(searchable2).onFound();
        verify(searchable3, never()).onFound();
        verify(searchable4, never()).onFound();
        verify(noResultsFoundCallback, never()).execute();
    }

    @Test
    public void testSearchWhenAllListWasHighlighted() {

        times(5, () -> index.search("Element"));

        // The next element to the last element of the list is the first one
        verify(searchable1, Mockito.times(2)).onFound();
        verify(searchable2).onFound();
        verify(searchable3).onFound();
        verify(searchable4).onFound();
        verify(noResultsFoundCallback, never()).execute();
    }

    @Test
    public void testIsDirtyWhenItsTheFirstSearch() {
        assertFalse(index.isDirty());
    }

    @Test
    public void testIsDirtyWhenItsTheSecondSearch() {
        index.search("element");
        assertFalse(index.isDirty());
    }

    @Test
    public void testIsDirtyWhenItsTheSecondSearchAndUserHasUpdatedTheAsset() {
        index.search("element");

        // The user has updated the asset.
        index.setCurrentAssetHashcodeSupplier(() -> currentAssetHashcodeSupplier.get() + 1);

        assertTrue(index.isDirty());
    }

    @Test
    public void testGetSubIndexes() {

        index.registerSubIndex(hasSearchableElements1);
        index.registerSubIndex(hasSearchableElements2);

        final List<HasSearchableElements<FakeSearchable>> actualSubIndexes = index.getSubIndexes();
        final List<HasSearchableElements<FakeSearchable>> expectedSubIndexes = asList(hasSearchableElements1,
                                                                                      hasSearchableElements2);

        assertEquals(expectedSubIndexes, actualSubIndexes);
    }

    @Test
    public void testNextResult() {

        index.search("Element");

        times(4, () -> index.nextResult());

        final InOrder inOrder = Mockito.inOrder(searchable1, searchable2, searchable3, searchable4);

        inOrder.verify(searchable1).onFound();
        inOrder.verify(searchable2).onFound();
        inOrder.verify(searchable3).onFound();
        inOrder.verify(searchable4).onFound();
        inOrder.verify(searchable1).onFound();
        inOrder.verifyNoMoreInteractions();
        verify(noResultsFoundCallback, never()).execute();

        // 1 for search + 4 for each "nextResult()" call
        verify(searchPerformedCallback, Mockito.times(5)).execute();
    }

    @Test
    public void testPreviousResult() {

        index.search("Element");

        times(4, () -> index.previousResult());

        final InOrder inOrder = Mockito.inOrder(searchable1, searchable4, searchable3, searchable2);

        inOrder.verify(searchable1).onFound();
        inOrder.verify(searchable4).onFound();
        inOrder.verify(searchable3).onFound();
        inOrder.verify(searchable2).onFound();
        inOrder.verify(searchable1).onFound();
        inOrder.verifyNoMoreInteractions();
        verify(noResultsFoundCallback, never()).execute();

        // 1 for search + 4 for each "previsousResult()" call
        verify(searchPerformedCallback, Mockito.times(5)).execute();
    }

    @Test
    public void testNextResultWhenSearchHasNoResult() {
        index.nextResult();
        verify(noResultsFoundCallback).execute();
        verify(searchPerformedCallback).execute();
    }

    @Test
    public void testPreviousResultWhenSearchHasNoResult() {
        index.previousResult();
        verify(noResultsFoundCallback).execute();
        verify(searchPerformedCallback).execute();
    }

    @Test
    public void testGetCurrentResultNumber() {
        index.search("Element 2");
        assertEquals(1, index.getCurrentResultNumber());
    }

    @Test
    public void testGetTotalOfResultsNumber() {
        index.search("Element");
        assertEquals(4, index.getTotalOfResultsNumber());
    }

    @Test
    public void testClose() {

        index.search("Element");

        index.close();

        assertEquals(0, index.getTotalOfResultsNumber());
        assertEquals(0, index.getCurrentResultNumber());
        assertEquals("", index.getCurrentTerm());
        assertEquals(0, index.getResults().size());

        // 1 for search, 1 for reset
        verify(searchPerformedCallback, Mockito.times(2)).execute();
        verify(searchClosedCallback).execute();
    }

    private void times(final int times,
                       final Command command) {
        range(0, times).forEach(i -> command.execute());
    }

    class FakeSearchable implements Searchable {

        private final String text;

        FakeSearchable(final String text) {
            this.text = text;
        }

        @Override
        public boolean matches(final String text) {
            return this.text.contains(text);
        }

        @Override
        public Command onFound() {
            return () -> {/* Nothing. */};
        }
    }

    class FakeEditorSearchIndex extends BaseEditorSearchIndex<FakeSearchable> {

        private final List<FakeSearchable> searchableElements;

        FakeEditorSearchIndex(final List<FakeSearchable> searchableElements) {
            this.searchableElements = searchableElements;
        }

        @Override
        protected List<FakeSearchable> getSearchableElements() {
            return searchableElements;
        }
    }
}
