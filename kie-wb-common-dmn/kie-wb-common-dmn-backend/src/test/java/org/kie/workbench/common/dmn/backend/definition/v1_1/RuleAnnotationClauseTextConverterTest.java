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
import org.kie.dmn.model.api.RuleAnnotation;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleAnnotationClauseTextConverterTest {

    @Mock
    private RuleAnnotation ruleAnnotation;

    @Mock
    private RuleAnnotationClauseText ruleAnnotationClauseText;

    @Test
    public void testWbFromDMN() {

        final String text = "text";
        when(ruleAnnotation.getText()).thenReturn(text);
        final RuleAnnotationClauseText converted = RuleAnnotationClauseTextConverter.wbFromDMN(ruleAnnotation);

        assertEquals(text, converted.getText().getValue());
    }

    @Test
    public void testWbFromDMNWhenIsNull() {

        final RuleAnnotationClauseText converted = RuleAnnotationClauseTextConverter.wbFromDMN(null);

        assertNull(converted);
    }

    @Test
    public void testDmnFromWB(){

        final String text = "text";
        final Text textObject = new Text(text);
        when(ruleAnnotationClauseText.getText()).thenReturn(textObject);

        final RuleAnnotation converted = RuleAnnotationClauseTextConverter.dmnFromWB(ruleAnnotationClauseText);

        assertEquals(text, converted.getText());
    }
}