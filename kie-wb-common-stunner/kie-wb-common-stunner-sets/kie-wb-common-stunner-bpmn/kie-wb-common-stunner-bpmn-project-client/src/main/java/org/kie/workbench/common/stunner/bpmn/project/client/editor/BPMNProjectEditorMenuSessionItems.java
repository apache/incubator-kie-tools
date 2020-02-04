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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;

@Dependent
@Typed(BPMNProjectEditorMenuSessionItems.class)
public class BPMNProjectEditorMenuSessionItems extends AbstractProcessProjectEditorMenuSessionItems<BPMNProjectDiagramEditorMenuItemsBuilder> {

    @Inject
    public BPMNProjectEditorMenuSessionItems(final BPMNProjectDiagramEditorMenuItemsBuilder itemsBuilder,
                                             final @BPMN BPMNEditorSessionCommands sessionCommands) {
        super(itemsBuilder, sessionCommands);
    }

    @Override
    protected String getEditorGenerateProcessFormPropertyKey() {
        return BPMNClientConstants.EditorGenerateProcessForm;
    }

    @Override
    protected String getEditorGenerateAllFormsPropertyKey() {
        return BPMNClientConstants.EditorGenerateAllForms;
    }

    @Override
    protected String getEditorGenerateSelectionFormsPropertyKey() {
        return BPMNClientConstants.EditorGenerateSelectionForms;
    }

    @Override
    protected String getEditorFormGenerationTitlePropertyKey() {
        return BPMNClientConstants.EditorFormGenerationTitle;
    }
}
