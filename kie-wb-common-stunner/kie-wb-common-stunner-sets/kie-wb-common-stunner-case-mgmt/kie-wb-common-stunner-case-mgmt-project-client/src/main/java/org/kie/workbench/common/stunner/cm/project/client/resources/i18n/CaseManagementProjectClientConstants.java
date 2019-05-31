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

package org.kie.workbench.common.stunner.cm.project.client.resources.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface CaseManagementProjectClientConstants {

    @TranslationKey(defaultValue = "")
    String CaseManagementDiagramResourceTypeShortName = "CaseManagementDiagramResourceType.shortName";

    @TranslationKey(defaultValue = "")
    String CaseManagementDiagramResourceTypeDescription = "CaseManagementDiagramResourceType.description";

    @TranslationKey(defaultValue = "")
    String CaseManagementDiagramResourceTypeDownload = "CaseManagementDiagramResourceType.download";

    @TranslationKey(defaultValue = "Form Generation")
    String CaseManagementEditorFormGenerationTitle = "cm.editor.forms.title";

    @TranslationKey(defaultValue = "Generate process form")
    String CaseManagementEditorGenerateProcessForm = "cm.editor.forms.generateProcessForm";

    @TranslationKey(defaultValue = "Generate all forms")
    String CaseManagementEditorGenerateAllForms = "cm.editor.forms.generateAllForms";

    @TranslationKey(defaultValue = "Generate forms for selection")
    String CaseManagementEditorGenerateSelectionForms = "cm.editor.forms.generateSelectionForms";

    @TranslationKey(defaultValue = "Case View")
    String CaseManagementEditorCaseViewTooltip = "cm.editor.case.view.tooltip";

    @TranslationKey(defaultValue = "Process View")
    String CaseManagementEditorProcessViewTooltip = "cm.editor.process.view.tooltip";
}
