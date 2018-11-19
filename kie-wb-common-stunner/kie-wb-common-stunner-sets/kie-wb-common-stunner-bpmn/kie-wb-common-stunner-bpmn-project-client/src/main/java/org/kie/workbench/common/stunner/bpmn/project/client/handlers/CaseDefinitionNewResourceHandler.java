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
package org.kie.workbench.common.stunner.bpmn.project.client.handlers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.profile.api.preferences.Profile;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNProjectImageResources;
import org.kie.workbench.common.stunner.bpmn.project.client.type.BPMNDiagramResourceType;
import org.kie.workbench.common.stunner.bpmn.service.BPMNDiagramService;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.project.client.handlers.AbstractProjectDiagramNewResourceHandler;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;


@ApplicationScoped
public class CaseDefinitionNewResourceHandler extends AbstractProjectDiagramNewResourceHandler<BPMNDiagramResourceType> {

    protected static final Image ICON = new Image(BPMNProjectImageResources.INSTANCE.bpmn2Icon());
    protected static final String CASE_DEFINITION = "org.kie.workbench.common.stunner.bpmn.CaseDefinition";
    private final ClientTranslationService translationService;
    private final Caller<BPMNDiagramService> bpmnDiagramService;
    private final WorkspaceProjectContext projectContext;

    CaseDefinitionNewResourceHandler() {
        this(null, null, null, null, null, null, null);
    }

    @Inject
    public CaseDefinitionNewResourceHandler(final DefinitionManager definitionManager,
                                            final ClientProjectDiagramService projectDiagramService,
                                            final BusyIndicatorView indicatorView,
                                            final BPMNDiagramResourceType projectDiagramResourceType,
                                            final ClientTranslationService translationService,
                                            final Caller<BPMNDiagramService> bpmnDiagramService,
                                            final WorkspaceProjectContext projectContext) {
        super(definitionManager, projectDiagramService, indicatorView, projectDiagramResourceType);
        this.translationService = translationService;
        this.bpmnDiagramService = bpmnDiagramService;
        this.projectContext = projectContext;
    }

    @Override
    public String getDescription() {
        return translationService.getDefinitionDescription(CASE_DEFINITION);
    }

    @Override
    public IsWidget getIcon() {
        return ICON;
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return BPMNDefinitionSet.class;
    }

    @Override
    public void createDiagram(Package pkg, String name, NewResourcePresenter presenter, Path path, String setId,
                              String moduleName, Optional<String> projectType) {
        super.createDiagram(pkg, name, presenter, path, setId, moduleName, Optional.of(ProjectType.CASE.name()));
    }

    @Override
    public void acceptContext(Callback<Boolean, Void> callback) {
        projectContext.getActiveWorkspaceProject()
                .map(WorkspaceProject::getRootPath)
                .ifPresent(path -> bpmnDiagramService.call(projectType -> {
                    Optional.ofNullable(projectType)
                            .filter(ProjectType.CASE::equals)
                            .ifPresent(p -> callback.onSuccess(true));
                }).getProjectType(path));
    }
    
    public List<Profile> getProfiles() {
        return Arrays.asList(Profile.FULL);
    }
}