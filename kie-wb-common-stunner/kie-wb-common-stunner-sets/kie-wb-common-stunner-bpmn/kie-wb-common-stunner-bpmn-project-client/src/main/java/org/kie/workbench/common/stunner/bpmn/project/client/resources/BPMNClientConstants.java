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

package org.kie.workbench.common.stunner.bpmn.project.client.resources;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface BPMNClientConstants {

    String BPMNDiagramResourceTypeShortName = "BPMNDiagramResourceType.shortName";

    String BPMNDiagramResourceTypeDescription = "BPMNDiagramResourceType.description";

    @TranslationKey(defaultValue = "Form Generation")
    String EditorFormGenerationTitle = "editor.forms.title";

    @TranslationKey(defaultValue = "Generate process form")
    String EditorGenerateProcessForm = "editor.forms.generateProcessForm";

    @TranslationKey(defaultValue = "Generate all forms")
    String EditorGenerateAllForms = "editor.forms.generateAllForms";

    @TranslationKey(defaultValue = "Generate forms for selection")
    String EditorGenerateSelectionForms = "editor.forms.generateSelectionForms";

    @TranslationKey(defaultValue = "Migrate")
    String EditorMigrateActionMenu = "editor.actions.migrateMenu";

    @TranslationKey(defaultValue = "Migrate Diagram")
    String EditorMigrateAction = "editor.actions.migrate";

    @TranslationKey(defaultValue = "Migrate Diagram")
    String EditorMigrateActionTitle = "editor.actions.migrateTitle";

    @TranslationKey(defaultValue = "Warning this action cannot be undone")
    String EditorMigrateActionWarning = "editor.actions.migrateWarning";

    @TranslationKey(defaultValue = "Confirm Migrate")
    String EditorMigrateConfirmAction = "editor.actions.migrateConfirmAction";

    @TranslationKey(defaultValue = "File {0} was migrated from Stunner to jBPM designer")
    String EditorMigrateCommitMessage = "editor.actions.migrateCommitMessage";

    @TranslationKey(defaultValue = "A jBPM designer process already exists for target file {0}")
    String EditorMigrateErrorProcessAlreadyExists = "editor.actions.migrateErrorProcessAlreadyExists";

    @TranslationKey(defaultValue = "An error was produced during migration")
    String EditorMigrateErrorGeneric = "editor.actions.migrateErrorGeneric";
}
