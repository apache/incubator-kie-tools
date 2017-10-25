/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.asset.management.client.editors.repository.structure;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.common.services.project.context.ProjectContext;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mvp.PlaceRequest;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class RepositoryStructureTitle {

    private final ProjectContext workbenchContext;
    private final Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceRequest placeRequest;

    @Inject
    public RepositoryStructureTitle(final ProjectContext workbenchContext,
                                    final Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent) {
        this.workbenchContext = workbenchContext;
        this.changeTitleWidgetEvent = changeTitleWidgetEvent;
    }

    public void init(final PlaceRequest placeRequest) {
        checkNotNull("placeRequest.",
                     placeRequest);
        this.placeRequest = placeRequest;
    }

    public void updateEditorTitle(final RepositoryStructureModel model,
                                  final boolean initialized) {

        checkNotNull("Please set placeRequest.",
                     placeRequest);

        if (workbenchContext.getActiveRepository() == null) {
            changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                   Constants.INSTANCE.RepositoryNotSelected()));
        } else if (!initialized) {
            changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                   Constants.INSTANCE.UnInitializedStructure(getRepositoryLabel())));
        } else if (model.isMultiModule()) {
            changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                   Constants.INSTANCE.RepositoryStructureWithName(getRepositoryLabel() + "→ "
                                                                                                                          + model.getPOM().getGav().getArtifactId() + ":"
                                                                                                                          + model.getPOM().getGav().getGroupId() + ":"
                                                                                                                          + model.getPOM().getGav().getVersion())));
        } else if (model.isSingleProject()) {
            changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                   Constants.INSTANCE.RepositoryStructureWithName(getRepositoryLabel() + "→ " + model.getOrphanProjects().get(0).getProjectName())));
        } else {
            changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                   Constants.INSTANCE.UnmanagedRepository(getRepositoryLabel())));
        }
    }

    private String getRepositoryLabel() {
        return workbenchContext.getActiveRepository() != null ? (workbenchContext.getActiveRepository().getAlias() + " (" + workbenchContext.getActiveBranch() + ") ") : "";
    }
}
