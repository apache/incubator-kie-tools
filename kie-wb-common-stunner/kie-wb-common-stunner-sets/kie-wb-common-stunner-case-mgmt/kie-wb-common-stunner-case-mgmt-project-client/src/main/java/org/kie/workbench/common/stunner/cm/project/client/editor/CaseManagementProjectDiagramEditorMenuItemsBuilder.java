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
package org.kie.workbench.common.stunner.cm.project.client.editor;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.client.widgets.popups.PopupUtil;
import org.kie.workbench.common.stunner.cm.project.client.resources.i18n.CaseManagementProjectClientConstants;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuItemsBuilder;

@Dependent
@Typed(CaseManagementProjectDiagramEditorMenuItemsBuilder.class)
public class CaseManagementProjectDiagramEditorMenuItemsBuilder extends AbstractDiagramEditorMenuItemsBuilder {

    @SuppressWarnings("unused")
    public CaseManagementProjectDiagramEditorMenuItemsBuilder() {
        //CDI proxy
    }

    @Inject
    public CaseManagementProjectDiagramEditorMenuItemsBuilder(final ClientTranslationService translationService,
                                                              final PopupUtil popupUtil) {
        super(translationService,
              popupUtil);
    }

    @Override
    protected Optional<String> getExportLabelToRawFormatIfSupported() {
        return Optional.of(translationService.getValue(CaseManagementProjectClientConstants.CaseManagementDiagramResourceTypeDownload));
    }
}
