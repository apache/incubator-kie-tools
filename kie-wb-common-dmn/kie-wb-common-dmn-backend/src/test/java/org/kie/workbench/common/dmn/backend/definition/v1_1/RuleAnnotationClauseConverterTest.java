/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleAnnotationClauseConverterTest {

    @Mock
    private org.kie.dmn.model.api.RuleAnnotationClause ruleAnnotationClause;

    @Mock
    private RuleAnnotationClause dmnRuleAnnotationClause;

    @Test
    public void testWbFromDMN() {

        final String name = "name";
        when(ruleAnnotationClause.getName()).thenReturn(name);

        final RuleAnnotationClause converted = RuleAnnotationClauseConverter.wbFromDMN(ruleAnnotationClause);

        assertEquals(name, converted.getName().getValue());
    }

    @Test
    public void testWbFromDMNWhenIsNull() {

        final RuleAnnotationClause converted = RuleAnnotationClauseConverter.wbFromDMN(null);
        assertNull(converted);
    }

    @Test
    public void testDmnFromWB() {

        final String dmnName = "name";
        final Name name = new Name(dmnName);
        when(dmnRuleAnnotationClause.getName()).thenReturn(name);

        final org.kie.dmn.model.api.RuleAnnotationClause converted = RuleAnnotationClauseConverter.dmnFromWB(dmnRuleAnnotationClause);

        assertEquals(dmnName, converted.getName());
    }

    @Test
    public void testDmnFromWBWhenIsNull() {

        final org.kie.dmn.model.api.RuleAnnotationClause converted = RuleAnnotationClauseConverter.dmnFromWB(null);

        assertNull(converted);
    }
}