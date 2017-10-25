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

package org.guvnor.asset.management.client.editors.repository.wizard.pages;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.editors.repository.wizard.CreateRepositoryWizardModel;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.jboss.errai.bus.client.api.base.DefaultErrorCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.callbacks.Callback;

public class RepositoryStructurePage extends RepositoryWizardPage
        implements
        RepositoryStructurePageView.Presenter {

    private RepositoryStructurePageView view;

    private boolean isProjectValid = false;

    private boolean isGroupIdValid = false;

    private boolean isArtifactIdValid = false;

    private boolean isValidVersion = false;

    private Caller<RepositoryStructureService> repositoryStructureService;

    @Inject
    public RepositoryStructurePage(RepositoryStructurePageView view,
                                   Caller<RepositoryStructureService> repositoryStructureService) {
        this.view = view;
        view.init(this);
        this.repositoryStructureService = repositoryStructureService;
    }

    @Override
    public String getTitle() {
        return Constants.INSTANCE.RepositoryStructurePage();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {

        boolean isComplete = structurePageWasVisited &&
                isProjectValid &&
                isGroupIdValid &&
                isArtifactIdValid &&
                isValidVersion;

        callback.callback(isComplete);
    }

    @Override
    public void setModel(CreateRepositoryWizardModel model) {
        super.setModel(model);
        model.setConfigureRepository(view.isConfigureRepository());
        model.setMultiModule(view.isMultiModule());
    }

    @Override
    public void initialise() {
        //no additional processing required
    }

    @Override
    public void prepareView() {
        //no additional processing required
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void setProjectName(String projectName) {
        model.setProjectName(projectName);
        view.setProjectName(projectName);
        view.clearProjectNameErrorMessage();
        isProjectValid = true;
    }

    @Override
    public void onProjectNameChange() {
        String projectName = view.getProjectName();
        projectName = projectName != null ? projectName.trim() : null;
        if (projectName != null && !projectName.equals(view.getProjectName())) {
            view.setProjectName(projectName);
        }
        model.setProjectName(projectName);

        repositoryStructureService.call(new RemoteCallback<Boolean>() {
                                            @Override
                                            public void callback(Boolean isValid) {
                                                if (isValid) {
                                                    view.clearProjectNameErrorMessage();
                                                } else {
                                                    view.setProjectNameErrorMessage(Constants.INSTANCE.InvalidProjectName());
                                                }
                                                if (isValid != isProjectValid) {
                                                    isProjectValid = isValid;
                                                    fireEvent();
                                                }
                                            }
                                        },
                                        new DefaultErrorCallback()).isValidProjectName(projectName);
    }

    @Override
    public void setProjectDescription(String projectDescription) {
        model.setProjectDescription(projectDescription);
        view.setProjectDescription(projectDescription);
    }

    @Override
    public void onProjectDescriptionChange() {
        model.setProjectDescription(view.getProjectDescription().trim());
    }

    @Override
    public void setGroupId(String groupId) {
        model.setGroupId(groupId);
        view.setGroupId(groupId);
        view.clearGroupIdErrorMessage();
        isGroupIdValid = true;
    }

    @Override
    public void onGroupIdChange() {
        String groupId = view.getGroupId();
        groupId = groupId != null ? groupId.trim() : null;
        if (groupId != null && !groupId.equals(view.getGroupId())) {
            view.setGroupId(groupId);
        }
        model.setGroupId(groupId);

        repositoryStructureService.call(new RemoteCallback<Boolean>() {
                                            @Override
                                            public void callback(Boolean isValid) {
                                                if (isValid) {
                                                    view.clearGroupIdErrorMessage();
                                                } else {
                                                    view.setGroupIdErrorMessage(Constants.INSTANCE.InvalidGroupId());
                                                }
                                                if (isValid != isGroupIdValid) {
                                                    isGroupIdValid = isValid;
                                                    fireEvent();
                                                }
                                            }
                                        },
                                        new DefaultErrorCallback()).isValidGroupId(groupId);
    }

    @Override
    public void setArtifactId(String artifactId) {
        model.setArtifactId(artifactId);
        view.setArtifactId(artifactId);
        view.clearArtifactIdErrorMessage();
        isArtifactIdValid = true;
    }

    @Override
    public void onArtifactIdChange() {
        String artifactId = view.getArtifactId();
        artifactId = artifactId != null ? artifactId.trim() : null;
        if (artifactId != null && !artifactId.equals(view.getArtifactId())) {
            view.setArtifactId(artifactId);
        }
        model.setArtifactId(artifactId);

        repositoryStructureService.call(new RemoteCallback<Boolean>() {
                                            @Override
                                            public void callback(Boolean isValid) {
                                                if (isValid) {
                                                    view.clearArtifactIdErrorMessage();
                                                } else {
                                                    view.setArtifactIdErrorMessage(Constants.INSTANCE.InvalidArtifactId());
                                                }
                                                if (isValid != isArtifactIdValid) {
                                                    isArtifactIdValid = isValid;
                                                    fireEvent();
                                                }
                                            }
                                        },
                                        new DefaultErrorCallback()).isValidArtifactId(artifactId);
    }

    @Override
    public void setConfigureRepository(boolean configureRepository) {
        model.setConfigureRepository(configureRepository);
        view.setConfigureRepository(configureRepository);
    }

    @Override
    public void setVersion(String version) {
        model.setVersion(version);
        view.setVersion(version);
        view.clearVersionErrorMessage();
        isValidVersion = true;
    }

    @Override
    public void onVersionChange() {
        String version = view.getVersion();
        version = version != null ? version.trim() : null;
        if (version != null && !version.equals(view.getVersion())) {
            view.setVersion(version);
        }
        model.setVersion(version);

        repositoryStructureService.call(new RemoteCallback<Boolean>() {
                                            @Override
                                            public void callback(Boolean isValid) {
                                                if (isValid) {
                                                    view.clearVersionErrorMessage();
                                                } else {
                                                    view.setVersionErrorMessage(Constants.INSTANCE.InvalidVersion());
                                                }
                                                if (isValid != isValidVersion) {
                                                    isValidVersion = isValid;
                                                    fireEvent();
                                                }
                                            }
                                        },
                                        new DefaultErrorCallback()).isValidVersion(version);
    }

    @Override
    public void onSingleModuleChange() {
        model.setMultiModule(!view.isSingleModule());
    }

    @Override
    public void onMultiModuleChange() {
        model.setMultiModule(view.isMultiModule());
    }

    @Override
    public void onConfigureRepositoryChange() {
        model.setConfigureRepository(view.isConfigureRepository());
    }
}
