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

package org.kie.workbench.common.stunner.core.i18n;

import java.util.Collection;

import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;

public class CoreTranslationMessages {

    private static final String SEP = ".";
    private static final String CORE_PREF = "org.kie.workbench.common.stunner.core" + SEP;
    private static final String COMMAND_PREF = CORE_PREF + "command" + SEP;
    private static final String RULE_PREF = CORE_PREF + "rule" + SEP;

    public static final String ERROR = CORE_PREF + "error";
    public static final String WARNING = CORE_PREF + "warn";
    public static final String INFO = CORE_PREF + "info";
    public static final String REASON = CORE_PREF + "reason";
    public static final String DELETE = CORE_PREF + "delete";
    public static final String EDIT = CORE_PREF + "edit";
    public static final String PRINT = CORE_PREF + "print";
    public static final String ARE_YOU_SURE = CORE_PREF + "areYouSure";
    public static final String ELEMENT_UUID = CORE_PREF + "element_uuid";
    public static final String COMMAND_SUCCESS = COMMAND_PREF + "success";
    public static final String COMMAND_FAILED = COMMAND_PREF + "fail";
    public static final String VALIDATION_SUCCESS = RULE_PREF + "success";
    public static final String VALIDATION_FAILED = RULE_PREF + "fail";
    public static final String DIAGRAM_LOAD_FAIL_UNSUPPORTED_ELEMENTS = "org.kie.workbench.common.stunner.core.client.diagram.load.fail.unsupported";
    public static final String DIAGRAM_AUTOMATIC_LAYOUT_PERFORMED = "org.kie.workbench.common.stunner.core.client.diagram.automatic.layout.performed";
    public static final String COPY_SELECTION = "org.kie.workbench.common.stunner.core.client.toolbox.CopySelection";
    public static final String CUT_SELECTION = "org.kie.workbench.common.stunner.core.client.toolbox.CutSelection";
    public static final String PASTE_SELECTION = "org.kie.workbench.common.stunner.core.client.toolbox.PasteSelection";
    public static final String VISIT_GRAPH = "org.kie.workbench.common.stunner.core.client.toolbox.VisitGraph";
    public static final String VALIDATE = "org.kie.workbench.common.stunner.core.client.toolbox.Validate";
    public static final String UNDO = "org.kie.workbench.common.stunner.core.client.toolbox.Undo";
    public static final String SWITCH_GRID = "org.kie.workbench.common.stunner.core.client.toolbox.SwitchGrid";
    public static final String REDO = "org.kie.workbench.common.stunner.core.client.toolbox.Redo";
    public static final String SAVE = "org.kie.workbench.common.stunner.core.client.toolbox.Save";
    public static final String EXPORT_PNG = "org.kie.workbench.common.stunner.core.client.toolbox.ExportPNG";
    public static final String EXPORT_PDF = "org.kie.workbench.common.stunner.core.client.toolbox.ExportPDF";
    public static final String EXPORT_JPG = "org.kie.workbench.common.stunner.core.client.toolbox.ExportJPG";
    public static final String EXPORT_SVG = "org.kie.workbench.common.stunner.core.client.toolbox.ExportSVG";
    public static final String EXPORT_BPMN = "org.kie.workbench.common.stunner.core.client.toolbox.ExportBPMN";
    public static final String DELETE_SELECTION = "org.kie.workbench.common.stunner.core.client.toolbox.DeleteSelection";
    public static final String CLEAR_DIAGRAM = "org.kie.workbench.common.stunner.core.client.toolbox.ClearDiagram";
    public static final String CLEAR_SHAPES = "org.kie.workbench.common.stunner.core.client.toolbox.ClearShapes";
    public static final String CONFIRM_CLEAR_DIAGRAM = "org.kie.workbench.common.stunner.core.client.toolbox.ConfirmClearDiagram";
    public static final String PERFORM_AUTOMATIC_LAYOUT = "org.kie.workbench.common.stunner.core.client.toolbox.PerformAutomaticLayout";
    public static final String NEW_LINE = "\n";
    public static final String DOT = ".";
    public static final String COLON = ": ";
    public static final String OPEN_BRA = "[";
    public static final String CLOSE_BRA = "] ";
    public static final String OPEN_COMMENT = "'";
    public static final String CLOSE_COMMENT = "' ";

    public static String getDiagramValidationsErrorMessage(final StunnerTranslationService translationService,
                                                           final String key,
                                                           final Collection<DiagramElementViolation<RuleViolation>> result) {
        final String message = translationService.getValue(key) + DOT + NEW_LINE
                + translationService.getValue(CoreTranslationMessages.REASON) + COLON + NEW_LINE
                + getValidationMessages(translationService,
                                        result);
        return message;
    }

    public static String getCanvasValidationsErrorMessage(final StunnerTranslationService translationService,
                                                          final String key,
                                                          final Iterable<CanvasViolation> result) {
        final String message = translationService.getValue(key) + DOT + NEW_LINE
                + translationService.getValue(CoreTranslationMessages.REASON) + COLON + NEW_LINE
                + getValidationMessages(translationService,
                                        result);
        return message;
    }

    public static String getCanvasCommandValidationsErrorMessage(final StunnerTranslationService translationService,
                                                                 final Iterable<CanvasViolation> result) {
        return getCanvasValidationsErrorMessage(translationService,
                                                CoreTranslationMessages.COMMAND_FAILED,
                                                result);
    }

    public static String getRuleValidationMessage(final StunnerTranslationService translationService,
                                                  final RuleViolation violation) {
        return getViolationTypeMessage(violation) + translationService.getViolationMessage(violation);
    }

    public static String getBeanValidationMessage(final StunnerTranslationService translationService,
                                                  final ModelBeanViolation violation) {
        return getViolationTypeMessage(violation) +
                OPEN_COMMENT + violation.getPropertyPath() + CLOSE_COMMENT
                + violation.getMessage();
    }

    public static String getDomainValidationMessage(final StunnerTranslationService translationService,
                                                    final DomainViolation violation) {
        return getViolationTypeMessage(violation) +
                violation.getMessage();
    }

    private static String getViolationTypeMessage(final Violation violation) {
        return "(" + violation.getViolationType() + ") ";
    }

    private static String getValidationMessages(final StunnerTranslationService translationService,
                                                final Collection<DiagramElementViolation<RuleViolation>> violations) {
        final StringBuilder message = new StringBuilder();
        violations.forEach(v -> message.append(getElementValidationMessage(translationService,
                                                                           v)));
        return message.toString();
    }

    private static String getElementValidationMessage(final StunnerTranslationService translationService,
                                                      final DiagramElementViolation<RuleViolation> elementViolation) {
        final String uuid = elementViolation.getUUID();
        // Bean & graph structure resulting messages.
        final Collection<ModelBeanViolation> modelViolations = elementViolation.getModelViolations();
        final Collection<RuleViolation> graphViolations = elementViolation.getGraphViolations();
        final Collection<DomainViolation> domainViolations = elementViolation.getDomainViolations();
        final boolean skip = modelViolations.isEmpty() && graphViolations.isEmpty() && domainViolations.isEmpty();
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
            domainViolations
                    .forEach(v -> message.append("BPMN ").append(getDomainValidationMessage(translationService,
                                                                                            v)).append(NEW_LINE));
            return message.toString();
        }
        return "";
    }

    private static String getValidationMessages(final StunnerTranslationService translationService,
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