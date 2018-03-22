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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants;
import org.kie.workbench.common.stunner.bpmn.project.service.BPMNDiagramEditorService;
import org.kie.workbench.common.stunner.bpmn.project.service.MigrationResult;
import org.kie.workbench.common.stunner.bpmn.resource.BPMNDefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@ApplicationScoped
public class BPMNDiagramEditorHelper {

    static final String BPMN_EXTENSION = "." + BPMNDefinitionSetResourceType.BPMN_EXTENSION;

    /**
     * Note: Define this bpmp2 file extension constant here to avoid dependencies with jBPM designer.
     * The migrate to jBPM designer feature will also be removed when Stunner is fully featured.
     */
    static final String BPMN2_EXTENSION = ".bpmn2";

    private final PlaceManager placeManager;

    private final Caller<BPMNDiagramEditorService> editorService;

    private final ClientTranslationService translationService;

    private final ErrorPopupPresenter errorPopupPresenter;

    @Inject
    public BPMNDiagramEditorHelper(final PlaceManager placeManager,
                                   final Caller<BPMNDiagramEditorService> editorService,
                                   final ClientTranslationService translationService,
                                   final ErrorPopupPresenter errorPopupPresenter) {
        this.placeManager = placeManager;
        this.editorService = editorService;
        this.translationService = translationService;
        this.errorPopupPresenter = errorPopupPresenter;
    }

    public void onMigrateDiagram(@Observes final BPMNMigrateDiagramEvent event) {
        placeManager.forceClosePlace(event.getSourcePlace());
        migrateDiagram(event.getSourcePath());
    }

    private PlaceManager getPlaceManager() {
        return placeManager;
    }

    private void migrateDiagram(final Path path) {
        String currentExtension;
        String newExtension;
        String currentName = path.getFileName();

        if (currentName.endsWith(BPMN2_EXTENSION)) {
            currentExtension = BPMN2_EXTENSION;
            newExtension = BPMN_EXTENSION;
        } else {
            currentExtension = BPMN_EXTENSION;
            newExtension = BPMN2_EXTENSION;
        }

        String newName = currentName.substring(0,
                                               currentName.length() - currentExtension.length());

        String commitMessage = translationService.getValue(BPMNClientConstants.EditorMigrateCommitMessage,
                                                           path.getFileName());

        editorService.call((RemoteCallback<MigrationResult>) result -> {
            if (result.hasError()) {
                errorPopupPresenter.showMessage(getErrorMessage(result));
            } else {
                BPMNDiagramEditorHelper.this.getPlaceManager().goTo(createTargetPlace(result.getPath()));
            }
        }).migrateDiagram(path,
                          newName,
                          newExtension,
                          commitMessage);
    }

    private String getErrorMessage(MigrationResult result) {
        if (BPMNDiagramEditorService.ServiceError.MIGRATION_ERROR_PROCESS_ALREADY_EXIST == result.getError()) {
            return translationService.getValue(BPMNClientConstants.EditorMigrateErrorProcessAlreadyExists,
                                               result.getPath().getFileName());
        } else {
            return translationService.getValue(BPMNClientConstants.EditorMigrateErrorGeneric);
        }
    }

    /**
     * for testing purposes.
     */
    PlaceRequest createTargetPlace(Path path) {
        return new PathPlaceRequest(path);
    }
}
