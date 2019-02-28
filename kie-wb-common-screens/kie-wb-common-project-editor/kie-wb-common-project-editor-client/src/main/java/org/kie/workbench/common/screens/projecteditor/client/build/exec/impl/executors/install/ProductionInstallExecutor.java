/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.install;

import java.util.Set;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.dialog.BuildDialog;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.validators.DefaultContextValidator;
import org.uberfire.workbench.events.NotificationEvent;

public class ProductionInstallExecutor extends AbstractInstallExecutor {

    private final ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    public ProductionInstallExecutor(final Caller<BuildService> buildServiceCaller,
                                     final Event<BuildResults> buildResultsEvent,
                                     final Event<NotificationEvent> notificationEvent,
                                     final BuildDialog buildDialog,
                                     final ConflictingRepositoriesPopup conflictingRepositoriesPopup) {

        super(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, new DefaultContextValidator());
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
    }

    @Override
    DeploymentMode getPreferredDeploymentMode() {
        return DeploymentMode.VALIDATED;
    }

    @Override
    protected boolean handleBuildError(BuildExecutionContext context, Message message, Throwable throwable) {
        if (throwable instanceof GAVAlreadyExistsException) {
            final Set<MavenRepositoryMetadata> repositories = ((GAVAlreadyExistsException) throwable).getRepositories();

            buildDialog.hideBusyIndicator();

            conflictingRepositoriesPopup.setContent(context.getModule().getPom().getGav(),
                                                    repositories,
                                                    () -> finish(),
                                                    () -> finishGavConflict(context));
            conflictingRepositoriesPopup.show();

            return false;
        }
        return super.handleBuildError(context, message, throwable);
    }

    private void finishGavConflict(final BuildExecutionContext context) {
        conflictingRepositoriesPopup.hide();
        buildAndInstall(context, DeploymentMode.FORCED);
    }
}
