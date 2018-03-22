/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.notification;

import java.util.Collections;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.core.validation.impl.ModelBeanViolationImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.client.widgets.notification.NotificationMessageUtils.CLOSE_BRA;
import static org.kie.workbench.common.stunner.client.widgets.notification.NotificationMessageUtils.CLOSE_COMMENT;
import static org.kie.workbench.common.stunner.client.widgets.notification.NotificationMessageUtils.COLON;
import static org.kie.workbench.common.stunner.client.widgets.notification.NotificationMessageUtils.OPEN_BRA;
import static org.kie.workbench.common.stunner.client.widgets.notification.NotificationMessageUtils.OPEN_COMMENT;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationMessageUtilsTest {

    public static final String HTML_NEW_LINE = "<br>";
    public static final String HTML_OPEN_COMMENT = "&#39;";
    public static final String HTML_CLOSE_COMMENT = "&#39; ";

    @Mock
    ClientTranslationService translationService;

    @Before
    public void setup() {
        when(translationService.getValue(eq(CoreTranslationMessages.REASON))).thenReturn("R");
        when(translationService.getValue(eq(CoreTranslationMessages.ELEMENT_UUID))).thenReturn("E");
        when(translationService.getValue(eq(CoreTranslationMessages.REASON))).thenReturn("R");
        when(translationService.getValue(eq(CoreTranslationMessages.REASON))).thenReturn("R");
    }

    @Test
    public void testBeanValidationMessage() {
        final ConstraintViolation<?> rootViolation = mock(ConstraintViolation.class);
        final Path propertyPath = mock(Path.class);
        when(propertyPath.toString()).thenReturn("path1");
        when(rootViolation.getPropertyPath()).thenReturn(propertyPath);
        when(rootViolation.getMessage()).thenReturn("message1");
        final ModelBeanViolation violation = ModelBeanViolationImpl.Builder.build(rootViolation);
        final String message = NotificationMessageUtils.getBeanValidationMessage(translationService,
                                                                                 violation);
        assertEquals("(WARNING) " + OPEN_COMMENT + "path1" + CLOSE_COMMENT + "message1",
                     message);
    }

    @Test
    public void testRuleValidationMessage() {
        final RuleViolation ruleViolation = mock(RuleViolation.class);
        when(ruleViolation.getViolationType()).thenReturn(Violation.Type.WARNING);
        when(translationService.getViolationMessage(eq(ruleViolation))).thenReturn("rv1");
        final String message = NotificationMessageUtils.getRuleValidationMessage(translationService,
                                                                                 ruleViolation);
        assertEquals("(WARNING) " + "rv1",
                     message);
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
        String message = NotificationMessageUtils.getCanvasValidationsErrorMessage(translationService,
                                                                                   "aKey",
                                                                                   violations);
        message = new SafeHtmlBuilder().appendEscapedLines(message).toSafeHtml().asString();
        assertEquals("aValue." + HTML_NEW_LINE + "R" + COLON + HTML_NEW_LINE +
                             OPEN_BRA + "1" + CLOSE_BRA + "(ERROR) "
                             + "cv1" + HTML_NEW_LINE,
                     message);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDiagramValidationMessage() {
        final ConstraintViolation<?> rootViolation = mock(ConstraintViolation.class);
        final Path propertyPath = mock(Path.class);
        when(propertyPath.toString()).thenReturn("path1");
        when(rootViolation.getPropertyPath()).thenReturn(propertyPath);
        when(rootViolation.getMessage()).thenReturn("message1");
        final ModelBeanViolation beanViolation = ModelBeanViolationImpl.Builder.build(rootViolation);
        final RuleViolation ruleViolation = mock(RuleViolation.class);
        final DiagramElementViolation<RuleViolation> diagramViolation = mock(DiagramElementViolation.class);
        when(diagramViolation.getUUID()).thenReturn("uuid1");
        when(diagramViolation.getModelViolations()).thenReturn(Collections.singletonList(beanViolation));
        when(diagramViolation.getGraphViolations()).thenReturn(Collections.singletonList(ruleViolation));
        when(ruleViolation.getViolationType()).thenReturn(Violation.Type.WARNING);
        when(translationService.getViolationMessage(eq(ruleViolation))).thenReturn("rv1");
        when(translationService.getValue(eq("aKey"))).thenReturn("aValue");
        String message = NotificationMessageUtils.getDiagramValidationsErrorMessage(translationService,
                                                                                    "aKey",
                                                                                    Collections.singleton(diagramViolation));
        message = new SafeHtmlBuilder().appendEscapedLines(message).toSafeHtml().asString();
        assertEquals("aValue." + HTML_NEW_LINE + "R" + COLON + HTML_NEW_LINE +
                             OPEN_BRA + "E" + COLON + "uuid1" + CLOSE_BRA + HTML_NEW_LINE +
                             "(WARNING) " + HTML_OPEN_COMMENT + "path1" + HTML_CLOSE_COMMENT +
                             "message1" + HTML_NEW_LINE +
                             "(WARNING) rv1" + HTML_NEW_LINE,
                     message);
    }
}
