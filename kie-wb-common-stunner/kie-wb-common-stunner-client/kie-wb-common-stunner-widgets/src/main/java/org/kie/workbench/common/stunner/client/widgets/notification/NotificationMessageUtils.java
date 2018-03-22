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

import java.util.Collection;

import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;

public class NotificationMessageUtils {

    public static final String NEW_LINE = "\n";
    public static final String DOT = ".";
    public static final String COLON = ": ";
    public static final String OPEN_BRA = "[";
    public static final String CLOSE_BRA = "] ";
    public static final String OPEN_COMMENT = "'";
    public static final String CLOSE_COMMENT = "' ";

    public static String getDiagramValidationsErrorMessage(final ClientTranslationService translationService,
                                                           final String key,
                                                           final Collection<DiagramElementViolation<RuleViolation>> result) {
        final String message = translationService.getValue(key) + DOT + NEW_LINE
                + translationService.getValue(CoreTranslationMessages.REASON) + COLON + NEW_LINE
                + getValidationMessages(translationService,
                                        result);
        return message;
    }

    public static String getCanvasValidationsErrorMessage(final ClientTranslationService translationService,
                                                          final String key,
                                                          final Iterable<CanvasViolation> result) {
        final String message = translationService.getValue(key) + DOT + NEW_LINE
                + translationService.getValue(CoreTranslationMessages.REASON) + COLON + NEW_LINE
                + getValidationMessages(translationService,
                                        result);
        return message;
    }

    public static String getRuleValidationMessage(final ClientTranslationService translationService,
                                                  final RuleViolation violation) {
        return getViolationTypeMessage(violation) + translationService.getViolationMessage(violation);
    }

    public static String getBeanValidationMessage(final ClientTranslationService translationService,
                                                  final ModelBeanViolation violation) {
        return getViolationTypeMessage(violation) +
                OPEN_COMMENT + violation.getPropertyPath() + CLOSE_COMMENT
                + violation.getMessage();
    }

    private static String getViolationTypeMessage(final Violation violation) {
        return "(" + violation.getViolationType() + ") ";
    }

    private static String getValidationMessages(final ClientTranslationService translationService,
                                                final Collection<DiagramElementViolation<RuleViolation>> violations) {
        final StringBuilder message = new StringBuilder();
        violations.forEach(v -> message.append(getElementValidationMessage(translationService,
                                                                           v)));
        return message.toString();
    }

    private static String getElementValidationMessage(final ClientTranslationService translationService,
                                                      final DiagramElementViolation<RuleViolation> elementViolation) {
        final String uuid = elementViolation.getUUID();
        // Bean & graph structure resulting messages.
        final Collection<ModelBeanViolation> modelViolations = elementViolation.getModelViolations();
        final Collection<RuleViolation> graphViolations = elementViolation.getGraphViolations();
        final boolean skip = modelViolations.isEmpty() && graphViolations.isEmpty();
        if (!skip) {
            final StringBuilder message = new StringBuilder()
                    .append(OPEN_BRA)
                    .append(translationService.getValue(CoreTranslationMessages.ELEMENT_UUID))
                    .append(COLON)
                    .append(uuid)
                    .append(CLOSE_BRA).append(NEW_LINE);
            modelViolations
                    .forEach(v -> message.append(getBeanValidationMessage(translationService,
                                                                          v)).append(NEW_LINE));
            graphViolations
                    .forEach(v -> message.append(getRuleValidationMessage(translationService,
                                                                          v)).append(NEW_LINE));
            return message.toString();
        }
        return "";
    }

    private static String getValidationMessages(final ClientTranslationService translationService,
                                                final Iterable<CanvasViolation> violations) {
        final StringBuilder message = new StringBuilder();
        final int[] i = {1};
        violations
                .forEach(v -> message
                        .append(OPEN_BRA).append(i[0]++).append(CLOSE_BRA)
                        .append(getRuleValidationMessage(translationService,
                                                         v))
                        .append(NEW_LINE));
        return message.toString();
    }
}
