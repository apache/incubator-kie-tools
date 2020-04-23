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

package org.kie.workbench.common.dmn.api.definition.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
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
}
