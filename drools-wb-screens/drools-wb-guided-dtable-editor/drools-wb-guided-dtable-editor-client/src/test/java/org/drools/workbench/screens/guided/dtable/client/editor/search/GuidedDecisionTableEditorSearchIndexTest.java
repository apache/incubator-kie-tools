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

package org.drools.workbench.screens.guided.dtable.client.editor.search;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.search.common.HasSearchableElements;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableEditorSearchIndexTest {

    private GuidedDecisionTableEditorSearchIndex searchIndex;

    @Mock
    private GuidedDecisionTableSearchableElement element1;

    @Mock
    private GuidedDecisionTableSearchableElement element2;

    @Mock
    private GuidedDecisionTableSearchableElement element3;

    @Mock
    private GuidedDecisionTableSearchableElement element4;

    @Mock
    private GuidedDecisionTableSearchableElement element5;

    @Before
    public void setup() {

        searchIndex = spy(new GuidedDecisionTableEditorSearchIndex());

        searchIndex.registerSubIndex(new FakeHasSearchableElements(asList(element1, element2)));
        searchIndex.registerSubIndex(new FakeHasSearchableElements(asList(element3, element4, element5)));
    }

    @Test
    public void testGetSearchableElements() {

        final List<GuidedDecisionTableSearchableElement> actualElements = searchIndex.getSearchableElements();
        final List<GuidedDecisionTableSearchableElement> expectedElements = asList(element1, element2, element3, element4, element5);

        assertEquals(expectedElements, actualElements);
    }

    class FakeHasSearchableElements implements HasSearchableElements<GuidedDecisionTableSearchableElement> {

        final List<GuidedDecisionTableSearchableElement> searchableElements;

        FakeHasSearchableElements(final List<GuidedDecisionTableSearchableElement> searchableElements) {
            this.searchableElements = searchableElements;
        }

        @Override
        public List<GuidedDecisionTableSearchableElement> getSearchableElements() {
            return searchableElements;
        }
    }
}
