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

package org.kie.workbench.common.stunner.core.client.i18n;

import java.util.Collections;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessagesTest;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.CLOSE_BRA;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.COLON;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.OPEN_BRA;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientTranslationMessagesTest {

    @Mock
    StunnerTranslationService translationService;

    @Before
    public void setup() {
        when(translationService.getValue(eq(CoreTranslationMessages.REASON))).thenReturn("R");
        when(translationService.getValue(eq(CoreTranslationMessages.ELEMENT))).thenReturn("E");
        when(translationService.getValue(eq(CoreTranslationMessages.REASON))).thenReturn("R");
        when(translationService.getValue(eq(CoreTranslationMessages.REASON))).thenReturn("R");
    }

    @Test
    public void testCanvasValidationMessage() {

        final RuleViolation ruleViolation = mock(RuleViolation.class);
        final CanvasViolation canvasViolation = mock(CanvasViolation.class);
        when(canvasViolation.getViolationType()).thenReturn(Violation.Type.ERROR);
        when(canvasViolation.getRuleViolation()).thenReturn(ruleViolation);
        when(ruleViolation.getViolationType()).thenReturn(Violation.Type.ERROR);
        when(ruleViolation.getViolationType()).thenReturn(Violation.Type.ERROR);
        final Iterable<CanvasViolation> violations = Collections.singletonList(canvasViolation);
        when(translationService.getValue(eq("aKey"))).thenReturn("aValue");
        when(translationService.getViolationMessage(eq(canvasViolation))).thenReturn("cv1");
        String message = ClientTranslationMessages.getCanvasValidationsErrorMessage(translationService,
                                                                                    "aKey",
                                                                                    violations);
        message = new SafeHtmlBuilder().appendEscapedLines(message).toSafeHtml().asString();
        assertEquals("aValue." + CoreTranslationMessagesTest.HTML_NEW_LINE + "R" + COLON +
                             CoreTranslationMessagesTest.HTML_NEW_LINE + OPEN_BRA + "1" + CLOSE_BRA +
                             "cv1" + CoreTranslationMessagesTest.HTML_NEW_LINE,
                     message);
    }
}
