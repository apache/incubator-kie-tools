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

import org.assertj.core.api.Assertions;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.dialog.BuildDialog;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentPopup;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.ARTIFACT;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.GROUP;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.SNAPSHOT;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.VERSION;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractExecutorTest<RUNNER extends AbstractExecutor> {

    @Mock
    protected Repository repository;

    @Mock
    protected KieModule module;

    @Mock
    protected Path pomPath;

    @Mock
    protected DeploymentPopup deploymentPopup;

    @Mock
    protected SpecManagementService specManagementServiceMock;

    protected Caller<SpecManagementService> specManagementService;

    @Mock
    protected BuildService buildServiceMock;

    protected Caller<BuildService> buildService;

    @Mock
    protected EventSourceMock<BuildResults> buildResultsEvent;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    protected ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    @Mock
    protected BuildDialog buildDialog;

    protected BuildExecutionContext context;

    protected RUNNER runner;

    public void setup() {
        specManagementService = new CallerMock<>(specManagementServiceMock);

        buildService = spy(new CallerMock<>(buildServiceMock));

        final POM pom = new POM(new GAV(GROUP, ARTIFACT, VERSION));

        when(buildServiceMock.build(any(KieModule.class))).thenReturn(new BuildResults());
        when(buildServiceMock.buildAndDeploy(any(KieModule.class), any(DeploymentMode.class))).thenReturn(new BuildResults());

        when(repository.getAlias()).thenReturn("repository");

        when(module.getModuleName()).thenReturn("module");
        when(module.getPomXMLPath()).thenReturn(pomPath);
        when(module.getPom()).thenReturn(pom);
        when(module.getRootPath()).thenReturn(mock(Path.class));
        when(pomPath.getFileName()).thenReturn("pom.xml");
    }

    protected void verifyNotification(final String message, final NotificationEvent.NotificationType type) {
        verify(notificationEvent).fire(argThat(new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches(final Object argument) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();

                return notification.equals(message) && type.equals(event.getType());
            }
        }));
    }

    @Test
    public void testValidation() {
        Assertions.assertThatThrownBy(() -> {
            if (context.getModule().getPom().getGav().isSnapshot()) {
                runner.run(getDefaultContext());
            } else {
                runner.run(getSnapshotContext());
            }
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConcurrency() {
        when(buildDialog.isBuilding()).thenReturn(true);

        runner.run(context);

        verify(runner, never()).start(eq(context));
    }

    protected BuildExecutionContext getDefaultContext() {
        module.getPom().getGav().setVersion(VERSION);

        return new BuildExecutionContext(ARTIFACT + "_" + VERSION, ARTIFACT, module);
    }

    protected BuildExecutionContext getSnapshotContext() {
        module.getPom().getGav().setVersion(VERSION + SNAPSHOT);

        context = new BuildExecutionContext(ARTIFACT + "_" + VERSION + SNAPSHOT, ARTIFACT, module);

        return context;
    }
}
