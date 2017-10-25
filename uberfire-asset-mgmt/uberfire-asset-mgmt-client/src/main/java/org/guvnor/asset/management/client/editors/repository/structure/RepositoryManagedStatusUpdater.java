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
import javax.inject.Inject;

import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

@Dependent
public class RepositoryManagedStatusUpdater {

    private final ProjectWizard wizard;
    private final Caller<RepositoryStructureService> repositoryStructureService;

    private HasBusyIndicator view;
    private ActionHistory history;
    private RepositoryStructureView.Presenter presenter;

    @Inject
    public RepositoryManagedStatusUpdater(final Caller<RepositoryStructureService> repositoryStructureService,
                                          final ProjectWizard wizard) {
        this.repositoryStructureService = repositoryStructureService;
        this.wizard = wizard;
    }

    public void bind(final HasBusyIndicator view,
                     final ActionHistory history,
                     final RepositoryStructureView.Presenter initRepository) {
        this.view = view;
        this.history = history;
        this.presenter = initRepository;
    }

    public void updateNonManaged(final Repository repository,
                                 final String branch) {
        setManagedStatus(repository,
                         branch,
                         false);
    }

    public void initSingleProject(final Repository repository,
                                  final String branch) {
        wizard.initialise(new POM());
        wizard.start(new Callback<Project>() {
                         @Override
                         public void callback(final Project result) {

                             history.setLastAddedModule(result);

                             if (result != null) {
                                 setManagedStatus(repository,
                                                  branch,
                                                  true);
                             }
                         }
                     },
                     false);
    }

    private void setManagedStatus(final Repository repository,
                                  final String branch,
                                  final boolean managed) {
        view.showBusyIndicator(Constants.INSTANCE.CreatingRepositoryStructure());
        repositoryStructureService.call(new RemoteCallback<Repository>() {
                                            @Override
                                            public void callback(Repository repository) {
                                                presenter.loadModel(repository,
                                                                    branch);
                                            }
                                        },
                                        new HasBusyIndicatorDefaultErrorCallback(view)).updateManagedStatus(repository,
                                                                                                            managed);
    }
}
