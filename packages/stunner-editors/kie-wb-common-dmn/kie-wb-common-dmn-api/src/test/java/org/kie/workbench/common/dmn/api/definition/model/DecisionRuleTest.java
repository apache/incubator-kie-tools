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

package org.kie.workbench.common.dmn.api.definition.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DecisionRuleTest {

    private static final String DECISION_RULE_ID = "DECISION_RULE_ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String UUID = "uuid";

    private DecisionRule decisionRule;

    @Before
    public void setup() {
        this.decisionRule = spy(new DecisionRule());
    }

    @Test
    public void testGetHasTypeRefs() {
        final LiteralExpression literalExpression1 = mock(LiteralExpression.class);
        final LiteralExpression literalExpression2 = mock(LiteralExpression.class);
        final List<LiteralExpression> outputEntry = asList(literalExpression1, literalExpression2);

        doReturn(outputEntry).when(decisionRule).getOutputEntry();

        when(literalExpression1.getHasTypeRefs()).thenReturn(asList(literalExpression1));
        when(literalExpression2.getHasTypeRefs()).thenReturn(asList(literalExpression2));

        final List<HasTypeRef> actualHasTypeRefs = decisionRule.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(literalExpression1, literalExpression2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final DecisionRule source = new DecisionRule(new Id(DECISION_RULE_ID), new Description(DESCRIPTION), new ArrayList<>(), new ArrayList<>());

        final DecisionRule target = source.copy();

        assertNotNull(target);
        assertNotEquals(DECISION_RULE_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertTrue(target.getInputEntry().isEmpty());
        assertTrue(target.getOutputEntry().isEmpty());
    }

    @Test
    public void testExactCopy() {
        final DecisionRule source = new DecisionRule(new Id(DECISION_RULE_ID), new Description(DESCRIPTION), new ArrayList<>(), new ArrayList<>());

        final DecisionRule target = source.exactCopy();

        assertNotNull(target);
        assertEquals(DECISION_RULE_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertTrue(target.getInputEntry().isEmpty());
        assertTrue(target.getOutputEntry().isEmpty());
    }

    @Test
    public void testFindDomainObject_FromInputEntry() {

        final DecisionRule decisionRule = new DecisionRule();
        final UnaryTests inputEntry1 = mock(UnaryTests.class);
        final UnaryTests inputEntry2 = mock(UnaryTests.class);
        final UnaryTests inputEntry3 = mock(UnaryTests.class);

        when(inputEntry2.getDomainObjectUUID()).thenReturn(UUID);

        decisionRule.getInputEntry().add(inputEntry1);
        decisionRule.getInputEntry().add(inputEntry2);
        decisionRule.getInputEntry().add(inputEntry3);

        final Optional<DomainObject> actual = decisionRule.findDomainObject(UUID);

        assertTrue(actual.isPresent());
        assertEquals(inputEntry2, actual.get());
    }

    @Test
    public void testFindDomainObject_FromOutputEntry() {

        final DecisionRule decisionRule = new DecisionRule();
        final UnaryTests inputEntry1 = mock(UnaryTests.class);
        final UnaryTests inputEntry2 = mock(UnaryTests.class);
        final UnaryTests inputEntry3 = mock(UnaryTests.class);
        final LiteralExpression outputEntry1 = mock(LiteralExpression.class);
        final LiteralExpression outputEntry2 = mock(LiteralExpression.class);
        final LiteralExpression outputEntry3 = mock(LiteralExpression.class);

        decisionRule.getInputEntry().add(inputEntry1);
        decisionRule.getInputEntry().add(inputEntry2);
        decisionRule.getInputEntry().add(inputEntry3);

        when(outputEntry2.getDomainObjectUUID()).thenReturn(UUID);

        decisionRule.getOutputEntry().add(outputEntry1);
        decisionRule.getOutputEntry().add(outputEntry2);
        decisionRule.getOutputEntry().add(outputEntry3);

        final Optional<DomainObject> actual = decisionRule.findDomainObject(UUID);

        assertTrue(actual.isPresent());
        assertEquals(outputEntry2, actual.get());
    }

    @Test
    public void testFindDomainObject_WhenNoneMatches() {

        final DecisionRule decisionRule = new DecisionRule();
        final UnaryTests inputEntry1 = mock(UnaryTests.class);
        final UnaryTests inputEntry2 = mock(UnaryTests.class);
        final LiteralExpression outputEntry1 = mock(LiteralExpression.class);
        final LiteralExpression outputEntry2 = mock(LiteralExpression.class);

        decisionRule.getInputEntry().add(inputEntry1);
        decisionRule.getInputEntry().add(inputEntry2);
        decisionRule.getOutputEntry().add(outputEntry1);
        decisionRule.getOutputEntry().add(outputEntry2);

        final Optional<DomainObject> actual = decisionRule.findDomainObject(UUID);

        assertFalse(actual.isPresent());
    }

    @Test
    public void testFindDomainObject_FromInput() {

        final DecisionRule decisionRule = new DecisionRule();
        final UnaryTests input1 = mock(UnaryTests.class);
        final UnaryTests input2 = mock(UnaryTests.class);
        final UnaryTests input3 = mock(UnaryTests.class);

        when(input3.getDomainObjectUUID()).thenReturn(UUID);

        decisionRule.getInputEntry().addAll(asList(input1, input2, input3));

        final Optional<DomainObject> actual = decisionRule.findDomainObject(UUID);

        assertTrue(actual.isPresent());
        assertEquals(input3, actual.get());
    }

    @Test
    public void testFindDomainObject_FromOutput() {

        final DecisionRule decisionRule = new DecisionRule();
        final UnaryTests input1 = mock(UnaryTests.class);
        final UnaryTests input2 = mock(UnaryTests.class);
        final UnaryTests input3 = mock(UnaryTests.class);
        final LiteralExpression output1 = mock(LiteralExpression.class);
        final LiteralExpression output2 = mock(LiteralExpression.class);
        final LiteralExpression output3 = mock(LiteralExpression.class);

        decisionRule.getInputEntry().addAll(asList(input1, input2, input3));
        decisionRule.getOutputEntry().addAll(asList(output1, output2, output3));

        when(output3.getDomainObjectUUID()).thenReturn(UUID);

        final Optional<DomainObject> actual = decisionRule.findDomainObject(UUID);

        assertTrue(actual.isPresent());
        assertEquals(output3, actual.get());
    }

    @Test
    public void testFindDomainObject_WhenNothingHasBeenFound() {

        final DecisionRule decisionRule = new DecisionRule();

        final Optional<DomainObject> actual = decisionRule.findDomainObject(UUID);

        assertFalse(actual.isPresent());
    }
}

