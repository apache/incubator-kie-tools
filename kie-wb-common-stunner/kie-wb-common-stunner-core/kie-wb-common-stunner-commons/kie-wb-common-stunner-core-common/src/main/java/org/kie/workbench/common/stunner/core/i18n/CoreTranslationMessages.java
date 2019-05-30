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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;

public class CoreTranslationMessages {

    private static final String SEP = ".";
    private static final String CORE_PREF = "org.kie.workbench.common.stunner.core" + SEP;
    private static final String COMMAND_PREF = CORE_PREF + "command" + SEP;
    private static final String RULE_PREF = CORE_PREF + "rule" + SEP;
    private static final String CLIENT_PREF = CORE_PREF + "client" + SEP;

    public static final String ERROR = CORE_PREF + "error";
    public static final String WARNING = CORE_PREF + "warn";
    public static final String INFO = CORE_PREF + "info";
    public static final String REASON = CORE_PREF + "reason";
    public static final String DELETE = CORE_PREF + "delete";
    public static final String EDIT = CORE_PREF + "edit";
    public static final String PRINT = CORE_PREF + "print";
    public static final String RESET = CORE_PREF + "reset";
    public static final String INCREASE = CORE_PREF + "increase";
    public static final String DECREASE = CORE_PREF + "decrease";
    public static final String FIT = CORE_PREF + "fit";
    public static final String ARE_YOU_SURE = CORE_PREF + "areYouSure";
    public static final String COMMAND_SUCCESS = COMMAND_PREF + "success";
    public static final String COMMAND_FAILED = COMMAND_PREF + "fail";
    public static final String VALIDATION_SUCCESS = RULE_PREF + "success";
    public static final String VALIDATION_PROPERTY = RULE_PREF + "property";
    public static final String ELEMENT = RULE_PREF + "element";
    public static final String VALIDATION_FAILED = RULE_PREF + "fail";
    public static final String MEDIATOR_PREVIEW = CLIENT_PREF + "mediator.zoomArea";
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
    private Function<String, String> nameByIdResolver;

    public static Optional<String> getDiagramValidationsErrorMessage(final StunnerTranslationService translationService,
                                                                     final Collection<DiagramElementViolation<RuleViolation>> result) {
        return getValidationMessages(translationService, result)
                .filter(StringUtils::nonEmpty);
    }

    public static String getRuleValidationMessage(final StunnerTranslationService translationService,
                                                  final RuleViolation violation) {
        return translationService.getViolationMessage(violation);
    }

    public static String getBeanValidationMessage(final StunnerTranslationService translationService,
                                                  final ModelBeanViolation violation) {
        return translationService.getValue(VALIDATION_PROPERTY, violation.getPropertyPath(), violation.getMessage());
    }

    public static String getDomainValidationMessage(final DomainViolation violation) {
        return violation.getMessage();
    }

    private static Optional<String> getValidationMessages(final StunnerTranslationService translationService,
                                                          final Collection<DiagramElementViolation<RuleViolation>> violations) {
        return Optional.of(violations
                                   .stream()
                                   .map(v -> getElementValidationMessage(translationService, v).orElse(""))
                                   .filter(StringUtils::nonEmpty)
                                   .collect(Collectors.joining()));
    }

    public static Optional<String> getElementValidationMessage(final StunnerTranslationService translationService,
                                                               final DiagramElementViolation<RuleViolation> elementViolation) {
        final String uuid = elementViolation.getUUID();
        // Bean & graph structure resulting messages.
        final Collection<ModelBeanViolation> modelViolations = elementViolation.getModelViolations();
        final Collection<RuleViolation> graphViolations = elementViolation.getGraphViolations();
        final Collection<DomainViolation> domainViolations = elementViolation.getDomainViolations();

        if (modelViolations.isEmpty() && graphViolations.isEmpty() && domainViolations.isEmpty()) {
            return Optional.empty();
        }

        final String message =
                Stream.of(modelViolations.stream().map(v -> getBeanValidationMessage(translationService, v)),
                          graphViolations.stream().map(v -> getRuleValidationMessage(translationService, v)),
                          domainViolations.stream().map(CoreTranslationMessages::getDomainValidationMessage))
                        .flatMap(s -> s)
                        .collect(Collectors.joining(NEW_LINE));

        return Optional.of(message)
                .filter(StringUtils::nonEmpty)
                .map(msg -> {
                    final String name = translationService.getElementName(uuid)
                            .filter(StringUtils::nonEmpty)
                            .orElse(uuid);
                    return translationService.getValue(ELEMENT, name, msg);
                });
    }
}