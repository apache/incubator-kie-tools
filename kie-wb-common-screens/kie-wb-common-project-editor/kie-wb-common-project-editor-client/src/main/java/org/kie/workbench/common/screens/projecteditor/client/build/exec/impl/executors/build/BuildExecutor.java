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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.build;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.dialog.BuildDialog;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.AbstractExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.ContextValidator;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources.CONSTANTS;

public class BuildExecutor extends AbstractExecutor {

    public BuildExecutor(final Caller<BuildService> buildServiceCaller,
                         final Event<BuildResults> buildResultsEvent,
                         final Event<NotificationEvent> notificationEvent,
                         final BuildDialog buildDialog,
                         final ContextValidator validator) {
        super(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, validator);
    }

    @Override
    protected void start(final BuildExecutionContext context) {

        showBuildMessage();

        buildServiceCaller.call((RemoteCallback<BuildResults>) result -> {

            if (result.getErrorMessages().isEmpty()) {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildSuccessful(), NotificationEvent.NotificationType.SUCCESS));
            } else {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildFailed(), NotificationEvent.NotificationType.ERROR));
            }

            buildResultsEvent.fire(result);
            finish();
        }, (ErrorCallback<Message>) (message, throwable) -> {
            finish();
            return false;
        }).build(context.getModule());
    }
}
