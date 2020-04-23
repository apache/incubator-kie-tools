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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator.SUM;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)

public class DecisionTableTest {

    private static final String TABLE_ID = "TABLE-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String OUTPUT_LABEL = "OUTPUT-LABEL";
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

    @Test
    public void testCopy() {
        final DecisionTable source = new DecisionTable(new Id(TABLE_ID),
                                                       new Description(DESCRIPTION),
                                                       BuiltInType.BOOLEAN.asQName(),
                                                       new ArrayList<>(),
                                                       new ArrayList<>(),
                                                       new ArrayList<>(),
                                                       HitPolicy.UNIQUE,
                                                       SUM,
                                                       DecisionTableOrientation.RULE_AS_ROW,
                                                       OUTPUT_LABEL);

        final DecisionTable target = source.copy();

        assertNotNull(target);
        assertNotEquals(TABLE_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertTrue(target.getInput().isEmpty());
        assertTrue(target.getOutput().isEmpty());
        assertTrue(target.getRule().isEmpty());
        assertEquals(HitPolicy.UNIQUE, target.getHitPolicy());
        assertEquals(SUM, target.getAggregation());
        assertEquals(DecisionTableOrientation.RULE_AS_ROW, target.getPreferredOrientation());
        assertEquals(OUTPUT_LABEL, target.getOutputLabel());
    }
}
