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
public class DecisionRuleTest {

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

        mockStatic(HasTypeRefHelper.class);
        when(HasTypeRefHelper.getFlatHasTypeRefs(outputEntry)).thenReturn(asList(literalExpression1, literalExpression2));

        final List<HasTypeRef> actualHasTypeRefs = decisionRule.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(literalExpression1, literalExpression2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }
}
