/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.i18n;

import java.util.Collections;
import java.util.Optional;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.core.validation.impl.ModelBeanViolationImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.ELEMENT;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.REASON;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.VALIDATION_PROPERTY;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
    public void testBeanValidationMessage() {
        final ConstraintViolation<?> rootViolation = mock(ConstraintViolation.class);
        final Path propertyPath = mock(Path.class);
        when(propertyPath.toString()).thenReturn("path1");
        when(rootViolation.getPropertyPath()).thenReturn(propertyPath);
        when(rootViolation.getMessage()).thenReturn("message1");
        final ModelBeanViolation violation = ModelBeanViolationImpl.Builder.build(rootViolation, "uuid");
        final String message = CoreTranslationMessages.getBeanValidationMessage(translationService, violation);
        assertEquals(VALIDATION_BEAN_MESSAGE, message);
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

    @Test
    @SuppressWarnings("unchecked")
    public void testDiagramValidationMessage() {
        final ConstraintViolation<?> rootViolation = mock(ConstraintViolation.class);
        final Path propertyPath = mock(Path.class);
        when(propertyPath.toString()).thenReturn("path1");
        when(rootViolation.getPropertyPath()).thenReturn(propertyPath);
        when(rootViolation.getMessage()).thenReturn("message1");
        final ModelBeanViolation beanViolation = ModelBeanViolationImpl.Builder.build(rootViolation, "uuid");
        final RuleViolation ruleViolation = mock(RuleViolation.class);
        final DiagramElementViolation<RuleViolation> diagramViolation = mock(DiagramElementViolation.class);
        final DomainViolation domainViolation = mock(DomainViolation.class);
        when(diagramViolation.getUUID()).thenReturn("uuid1");
        when(diagramViolation.getModelViolations()).thenReturn(Collections.singletonList(beanViolation));
        when(diagramViolation.getGraphViolations()).thenReturn(Collections.singletonList(ruleViolation));
        when(diagramViolation.getDomainViolations()).thenReturn(Collections.singletonList(domainViolation));
        when(ruleViolation.getViolationType()).thenReturn(Violation.Type.WARNING);
        when(translationService.getViolationMessage(eq(ruleViolation))).thenReturn("rv1");
        when(translationService.getValue(eq("aKey"))).thenReturn("aValue");
        when(translationService.getElementName("uuid1")).thenReturn(Optional.of("name"));

        String message = CoreTranslationMessages.getDiagramValidationsErrorMessage(translationService,
                                                                                   Collections.singleton(diagramViolation)).get();
        message = new SafeHtmlBuilder().appendEscapedLines(message).toSafeHtml().asString();
        assertEquals(VALIDATION_ELEMENT_MESSAGE, message);
    }
}
