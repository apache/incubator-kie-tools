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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HasTypeRefHelper.class})
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

        final List<InputClause> inputClauses = asList(mock(InputClause.class), mock(InputClause.class));
        final List<OutputClause> outputClauses = asList(mock(OutputClause.class), mock(OutputClause.class));
        final List<DecisionRule> decisionRules = asList(mock(DecisionRule.class), mock(DecisionRule.class));
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef5 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef6 = mock(HasTypeRef.class);

        doReturn(inputClauses).when(decisionTable).getInput();
        doReturn(outputClauses).when(decisionTable).getOutput();
        doReturn(decisionRules).when(decisionTable).getRule();

        mockStatic(HasTypeRefHelper.class);
        when(HasTypeRefHelper.getFlatHasTypeRefs(inputClauses)).thenReturn(asList(hasTypeRef1, hasTypeRef2));
        when(HasTypeRefHelper.getFlatHasTypeRefs(outputClauses)).thenReturn(asList(hasTypeRef3, hasTypeRef4));
        when(HasTypeRefHelper.getFlatHasTypeRefs(decisionRules)).thenReturn(asList(hasTypeRef5, hasTypeRef6));

        final List<HasTypeRef> actualHasTypeRefs = decisionTable.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(decisionTable, hasTypeRef1, hasTypeRef2, hasTypeRef3, hasTypeRef4, hasTypeRef5, hasTypeRef6);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }
}
