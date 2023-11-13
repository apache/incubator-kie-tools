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


package org.kie.workbench.common.stunner.core.i18n;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.ELEMENT;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.REASON;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.VALIDATION_PROPERTY;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CoreTranslationMessagesTest {

    public static final String HTML_NEW_LINE = "<br>";
    public static final String HTML_OPEN_COMMENT = "&#39;";
    public static final String HTML_CLOSE_COMMENT = "&#39; ";
    public static final String VALIDATION_BEAN_MESSAGE = "P";
    public static final String VALIDATION_ELEMENT_MESSAGE = "E";

    @Mock
    StunnerTranslationService translationService;

    @Before
    public void setup() {
        when(translationService.getValue(eq(REASON))).thenReturn("R");
        when(translationService.getValue(eq(ELEMENT))).thenReturn("E");
        when(translationService.getValue(eq(ELEMENT), anyString(), anyString())).thenReturn(VALIDATION_ELEMENT_MESSAGE);
        when(translationService.getValue(eq(REASON))).thenReturn("R");
        when(translationService.getValue(eq(REASON))).thenReturn("R");
        when(translationService.getValue(eq(VALIDATION_PROPERTY), anyString(), anyString())).thenReturn(VALIDATION_BEAN_MESSAGE);
    }

    @Test
    public void testRuleValidationMessage() {
        final RuleViolation ruleViolation = mock(RuleViolation.class);
        when(ruleViolation.getViolationType()).thenReturn(Violation.Type.WARNING);
        when(translationService.getViolationMessage(eq(ruleViolation))).thenReturn("rv1");
        final String message = CoreTranslationMessages.getRuleValidationMessage(translationService,
                                                                                ruleViolation);
        assertEquals("rv1", message);
    }

}
