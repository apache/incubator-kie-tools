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
}
