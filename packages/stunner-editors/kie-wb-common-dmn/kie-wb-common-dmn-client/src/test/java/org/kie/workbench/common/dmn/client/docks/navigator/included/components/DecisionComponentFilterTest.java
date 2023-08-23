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

package org.kie.workbench.common.dmn.client.docks.navigator.included.components;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionComponentFilterTest {

    private DecisionComponentFilter filter;

    @Before
    public void setup() {
        filter = new DecisionComponentFilter();
    }

    @Test
    public void testGetDrgElementWhenDrgElementIsNotPresent() {
        assertFalse(filter.getDrgElement().isPresent());
    }

    @Test
    public void testGetDrgElement() {
        final String decision = "Decision";

        filter.setDrgElement(decision);

        assertTrue(filter.getDrgElement().isPresent());
        assertEquals(decision, filter.getDrgElement().get());
    }

    @Test
    public void testGetTermWhenTermIsNotPresent() {
        assertFalse(filter.getTerm().isPresent());
    }

    @Test
    public void testGetTerm() {
        final String term = "term";

        filter.setTerm(term);

        assertTrue(filter.getTerm().isPresent());
        assertEquals(term, filter.getTerm().get());
    }

    @Test
    public void testQueryWithoutFilters() {

        final DecisionComponentsItem item1 = item("Can Drive?", new Decision());
        final DecisionComponentsItem item2 = item("Is Allowed?", new Decision());
        final DecisionComponentsItem item3 = item("Age", new InputData());
        final DecisionComponentsItem item4 = item("Name", new InputData());
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = asList(item1, item2, item3, item4);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testQueryFilteredByTerm() {

        final DecisionComponentsItem item1 = item("Can Drive?", new Decision());
        final DecisionComponentsItem item2 = item("Is Allowed?", new Decision());
        final DecisionComponentsItem item3 = item("Age", new InputData());
        final DecisionComponentsItem item4 = item("Name", new InputData());
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        filter.setTerm("name");

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = singletonList(item4);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testQueryFilteredByTermMultipleResults() {

        final DecisionComponentsItem item1 = item("Can Drive?", new Decision());
        final DecisionComponentsItem item2 = item("Is Allowed?", new Decision());
        final DecisionComponentsItem item3 = item("Age", new InputData());
        final DecisionComponentsItem item4 = item("Name", new InputData());
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        filter.setTerm("?");

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = asList(item1, item2);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testQueryFilteredByDrgElement() {

        final DecisionComponentsItem item1 = item("Can Drive?", new Decision());
        final DecisionComponentsItem item2 = item("Is Allowed?", new Decision());
        final DecisionComponentsItem item3 = item("Age", new InputData());
        final DecisionComponentsItem item4 = item("Name", new InputData());
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        filter.setDrgElement("Decision");

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = asList(item1, item2);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testQueryFilteredByDrgElementNoResult() {

        final DecisionComponentsItem item1 = item("Can Drive?", new Decision());
        final DecisionComponentsItem item2 = item("Is Allowed?", new Decision());
        final DecisionComponentsItem item3 = item("Age", new InputData());
        final DecisionComponentsItem item4 = item("Name", new InputData());
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        filter.setDrgElement("KnowledgeRequirement");

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = emptyList();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testQueryFilteredByDrgElementAndTerm() {

        final DecisionComponentsItem item1 = item("Can Drive?", new Decision());
        final DecisionComponentsItem item2 = item("Is Allowed?", new Decision());
        final DecisionComponentsItem item3 = item("Age", new InputData());
        final DecisionComponentsItem item4 = item("Name", new InputData());
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        filter.setTerm("is");
        filter.setDrgElement("Decision");

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = singletonList(item2);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testQueryFilteredByDrgElementAndTermMultipleResults() {

        final DecisionComponentsItem item1 = item("Can Drive?", new Decision());
        final DecisionComponentsItem item2 = item("Is Allowed?", new Decision());
        final DecisionComponentsItem item3 = item("Age", new InputData());
        final DecisionComponentsItem item4 = item("Name", new InputData());
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        filter.setTerm("e");
        filter.setDrgElement("InputData");

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = asList(item3, item4);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testQueryFilteredByDrgElementAndTermNoResult() {

        final DecisionComponentsItem item1 = item("Can Drive?", new Decision());
        final DecisionComponentsItem item2 = item("Is Allowed?", new Decision());
        final DecisionComponentsItem item3 = item("Age", new InputData());
        final DecisionComponentsItem item4 = item("Name", new InputData());
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        filter.setTerm("?");
        filter.setDrgElement("InputData");

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = emptyList();

        assertEquals(expectedResult, actualResult);
    }

    private DecisionComponentsItem item(final String drgElementName,
                                        final DRGElement drgElement) {

        final DecisionComponentsItem item = mock(DecisionComponentsItem.class);
        final Name name = mock(Name.class);
        final DecisionComponent decisionComponent = new DecisionComponent("file.dmn", drgElement, true);

        when(name.getValue()).thenReturn(drgElementName);
        when(item.getDecisionComponent()).thenReturn(decisionComponent);
        drgElement.setName(name);

        return item;
    }
}
