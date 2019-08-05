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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.junit.Test;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.model.v1_2.TDecisionRule;
import org.kie.dmn.model.v1_2.TLiteralExpression;
import org.kie.dmn.model.v1_2.TUnaryTests;

import static org.junit.Assert.assertEquals;

public class DecisionRulePropertyConverterTest {

    private static final String ID = "uuid";

    private static final String DESCRIPTION = "description";

    @Test
    public void testWbFromDMN() {
        final UnaryTests inputEntry = new TUnaryTests();
        final LiteralExpression outputEntry = new TLiteralExpression();
        final org.kie.dmn.model.api.DecisionRule dmn = new TDecisionRule();
        dmn.setId(ID);
        dmn.setDescription(DESCRIPTION);
        dmn.getInputEntry().add(inputEntry);
        dmn.getOutputEntry().add(outputEntry);

        final org.kie.workbench.common.dmn.api.definition.model.DecisionRule wb = DecisionRulePropertyConverter.wbFromDMN(dmn);

        assertEquals(ID, wb.getId().getValue());
        assertEquals(DESCRIPTION, wb.getDescription().getValue());
        assertEquals(wb, wb.getInputEntry().get(0).getParent());
        assertEquals(wb, wb.getOutputEntry().get(0).getParent());
    }

    @Test
    public void testDmnFromWb() {
        final org.kie.workbench.common.dmn.api.definition.model.UnaryTests inputEntry = new org.kie.workbench.common.dmn.api.definition.model.UnaryTests();
        final org.kie.workbench.common.dmn.api.definition.model.LiteralExpression outputEntry = new org.kie.workbench.common.dmn.api.definition.model.LiteralExpression();
        final org.kie.workbench.common.dmn.api.definition.model.DecisionRule wb = new org.kie.workbench.common.dmn.api.definition.model.DecisionRule();
        wb.getId().setValue(ID);
        wb.getDescription().setValue(DESCRIPTION);
        wb.getInputEntry().add(inputEntry);
        wb.getOutputEntry().add(outputEntry);

        final org.kie.dmn.model.api.DecisionRule dmn = DecisionRulePropertyConverter.dmnFromWB(wb);

        assertEquals(ID, dmn.getId());
        assertEquals(DESCRIPTION, dmn.getDescription());
        assertEquals(dmn, dmn.getInputEntry().get(0).getParent());
        assertEquals(dmn, dmn.getOutputEntry().get(0).getParent());
    }
}
