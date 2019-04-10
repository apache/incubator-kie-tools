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

package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)

public class DecisionTableTest {

    private DecisionTable decisionTable;

    @Before
    public void setup() {
        this.decisionTable = spy(new DecisionTable());
    }

    @Test
    public void testDefaultHitPolicy() {
        assertEquals(HitPolicy.UNIQUE,
                     decisionTable.getHitPolicy());
    }

    @Test
    public void testGetHasTypeRefs() {
        final InputClause inputClauses1 = mock(InputClause.class);
        final InputClause inputClauses2 = mock(InputClause.class);
        final List<InputClause> inputClauses = asList(inputClauses1, inputClauses2);
        final OutputClause outputClauses1 = mock(OutputClause.class);
        final OutputClause outputClauses2 = mock(OutputClause.class);
        final List<OutputClause> outputClauses = asList(outputClauses1, outputClauses2);
        final DecisionRule decisionRules1 = mock(DecisionRule.class);
        final DecisionRule decisionRules2 = mock(DecisionRule.class);
        final List<DecisionRule> decisionRules = asList(decisionRules1, decisionRules2);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef5 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef6 = mock(HasTypeRef.class);

        doReturn(inputClauses).when(decisionTable).getInput();
        doReturn(outputClauses).when(decisionTable).getOutput();
        doReturn(decisionRules).when(decisionTable).getRule();

        when(inputClauses1.getHasTypeRefs()).thenReturn(asList(hasTypeRef1));
        when(inputClauses2.getHasTypeRefs()).thenReturn(asList(hasTypeRef2));
        when(outputClauses1.getHasTypeRefs()).thenReturn(asList(hasTypeRef3));
        when(outputClauses2.getHasTypeRefs()).thenReturn(asList(hasTypeRef4));
        when(decisionRules1.getHasTypeRefs()).thenReturn(asList(hasTypeRef5));
        when(decisionRules2.getHasTypeRefs()).thenReturn(asList(hasTypeRef6));

        final List<HasTypeRef> actualHasTypeRefs = decisionTable.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(decisionTable, hasTypeRef1, hasTypeRef2, hasTypeRef3, hasTypeRef4, hasTypeRef5, hasTypeRef6);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(decisionTable.getRequiredComponentWidthCount(),
                     decisionTable.getComponentWidths().size());
        decisionTable.getComponentWidths().forEach(Assert::assertNull);
    }
}
