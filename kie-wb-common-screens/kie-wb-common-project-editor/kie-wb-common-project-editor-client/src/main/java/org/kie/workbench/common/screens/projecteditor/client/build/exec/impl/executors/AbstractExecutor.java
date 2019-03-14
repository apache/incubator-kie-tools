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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.dialog.BuildDialog;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources.CONSTANTS;

public abstract class AbstractExecutor implements Executor {

    protected final Caller<BuildService> buildServiceCaller;
    protected final Event<BuildResults> buildResultsEvent;
    protected final Event<NotificationEvent> notificationEvent;
    protected final BuildDialog buildDialog;
    protected final ContextValidator validator;

    public AbstractExecutor(Caller<BuildService> buildServiceCaller, Event<BuildResults> buildResultsEvent, Event<NotificationEvent> notificationEvent, BuildDialog buildDialog, ContextValidator validator) {
        this.buildServiceCaller = buildServiceCaller;
        this.buildResultsEvent = buildResultsEvent;
        this.notificationEvent = notificationEvent;
        this.buildDialog = buildDialog;
        this.validator = validator;
    }

    @Override
    public void run(BuildExecutionContext context) {

        if (buildDialog.isBuilding()) {
            return;
        }

        validator.validate(context);

        buildDialog.startBuild();

        start(context);
    }

    protected void showBuildMessage() {
        buildDialog.showBusyIndicator(CONSTANTS.Building());
    }

    protected void finish() {
        buildDialog.stopBuild();
    }

    protected abstract void start(BuildExecutionContext context);
}
