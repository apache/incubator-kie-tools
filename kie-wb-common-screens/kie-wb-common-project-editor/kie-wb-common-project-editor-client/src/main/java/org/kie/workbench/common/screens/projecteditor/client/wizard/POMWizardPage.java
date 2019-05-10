/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.projecteditor.client.wizard;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.client.ArtifactIdChangeHandler;
import org.guvnor.common.services.project.client.GroupIdChangeHandler;
import org.guvnor.common.services.project.client.NameChangeHandler;
import org.guvnor.common.services.project.client.POMEditorPanel;
import org.guvnor.common.services.project.client.VersionChangeHandler;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.utils.NewWorkspaceProjectUtils;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

public class POMWizardPage
        implements WizardPage {

    private POMEditorPanel pomEditor;
    private POMWizardPageView view;
    private Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;
    private Caller<ProjectScreenService> projectScreenService;
    private Caller<ValidationService> validationService;
    private boolean userModifiedArtifactId = false;

    public POMWizardPage() {
    }

    @Inject
    public POMWizardPage(final POMEditorPanel pomEditor,
                         final POMWizardPageView view,
                         final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent,
                         final Caller<ProjectScreenService> projectScreenService,
                         final Caller<ValidationService> validationService) {
        this.pomEditor = pomEditor;
        this.view = view;
        this.wizardPageStatusChangeEvent = wizardPageStatusChangeEvent;
        this.projectScreenService = projectScreenService;
        this.validationService = validationService;

        // changes are passed on from the pom editor through its view onto the underlying gav editor
        addChangeHandlers();
    }

    private void addChangeHandlers() {
        this.pomEditor.addNameChangeHandler(new NameChangeHandler() {
            @Override
            public void onChange(String newName) {
                validateName(pomEditor.getPom().getName());
                if (pomEditor.getPom().getGav().getArtifactId() == null || pomEditor.getPom().getGav().getArtifactId().isEmpty()) {
                    userModifiedArtifactId = false;
                }

                // TODO Move to PomBuilder
                final String sanitizedProjectName = NewWorkspaceProjectUtils.sanitizeProjectName(pomEditor.getPom().getName());
                if (!userModifiedArtifactId) {
                    pomEditor.setArtifactID(sanitizedProjectName);
                    validateArtifactId(sanitizedProjectName);
                }

                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent(POMWizardPage.this);
                POMWizardPage.this.wizardPageStatusChangeEvent.fire(event);
            }
        });
        this.pomEditor.addGroupIdChangeHandler(new GroupIdChangeHandler() {
            @Override
            public void onChange(String newGroupId) {
                validateGroupId(pomEditor.getPom().getGav().getGroupId());
                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent(POMWizardPage.this);
                POMWizardPage.this.wizardPageStatusChangeEvent.fire(event);
            }
        });
        this.pomEditor.addArtifactIdChangeHandler(new ArtifactIdChangeHandler() {
            @Override
            public void onChange(String newArtifactId) {
                userModifiedArtifactId = true;
                validateArtifactId(pomEditor.getPom().getGav().getArtifactId());
                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent(POMWizardPage.this);
                POMWizardPage.this.wizardPageStatusChangeEvent.fire(event);
            }
        });
        this.pomEditor.addVersionChangeHandler(new VersionChangeHandler() {
            @Override
            public void onChange(String newVersion) {
                validateVersion(pomEditor.getPom().getGav().getVersion());
                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent(POMWizardPage.this);
                POMWizardPage.this.wizardPageStatusChangeEvent.fire(event);
            }
        });
    }

    public void setPom(final POM pom) {
        this.pomEditor.setPOM(pom,
                              false);

        validateName(pom.getName());
        validateArtifactId(pom.getGav().getArtifactId());
        validateGroupId(pom.getGav().getGroupId());
        validateVersion(pom.getGav().getVersion());
    }

    public POM getPom() {
        return pomEditor.getPom();
    }

    void validateName(final String projectName) {
        validationService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(final Boolean response) {
                pomEditor.setValidName(Boolean.TRUE.equals(response));
            }
        }).isProjectNameValid(projectName);
    }

    void validateGroupId(final String groupId) {
        validationService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(final Boolean result) {
                pomEditor.setValidGroupID(Boolean.TRUE.equals(result));
            }
        }).validateGroupId(groupId);
    }

    void validateArtifactId(final String artifactId) {
        validationService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(final Boolean result) {
                pomEditor.setValidArtifactID(Boolean.TRUE.equals(result));
            }
        }).validateArtifactId(artifactId);
    }

    void validateVersion(final String version) {
        validationService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(final Boolean result) {
                pomEditor.setValidVersion(Boolean.TRUE.equals(result));
            }
        }).validateGAVVersion(version);
    }

    @Override
    public String getTitle() {
        return ProjectEditorResources.CONSTANTS.NewProjectWizard();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        validationService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(final Boolean result) {
                callback.callback(Boolean.TRUE.equals(result));
            }
        }).validate(pomEditor.getPom());
    }

    @Override
    public void initialise() {
        userModifiedArtifactId = false;
    }

    @Override
    public void prepareView() {
    }

    @Override
    public Widget asWidget() {
        return pomEditor.asWidget();
    }
}
