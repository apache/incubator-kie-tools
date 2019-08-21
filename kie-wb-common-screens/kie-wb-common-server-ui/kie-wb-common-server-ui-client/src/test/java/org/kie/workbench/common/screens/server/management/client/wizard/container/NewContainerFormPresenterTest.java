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

package org.kie.workbench.common.screens.server.management.client.wizard.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieServerMode;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.kie.workbench.common.screens.server.management.client.util.ContentChangeHandler;
import org.kie.workbench.common.screens.server.management.client.widget.artifact.ArtifactListWidgetPresenter;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.server.management.client.wizard.container.NewContainerFormPresenter.SNAPSHOT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewContainerFormPresenterTest {

    private final Collection<ContentChangeHandler> contentChangeHandlers = new ArrayList<ContentChangeHandler>();
    private final Answer contentHandlerAnswer = new Answer() {
        @Override
        public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
            fireContentHandlers();
            return null;
        }
    };
    Caller<M2RepoService> m2RepoServiceCaller;
    @Mock
    Logger logger;
    @Mock
    ManagedInstance<ArtifactListWidgetPresenter> presenterProvider;
    @Mock
    M2RepoService m2RepoService;
    Caller<SpecManagementService> specManagementServiceCaller;
    @Mock
    SpecManagementService specManagementService;
    @Spy
    Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent = new EventSourceMock<WizardPageStatusChangeEvent>();
    @Mock
    ArtifactListWidgetPresenter artifactListWidgetPresenter;
    @Mock
    NewContainerFormPresenter.View view;
    NewContainerFormPresenter presenter;

    @Before
    public void init() {
        contentChangeHandlers.clear();
        m2RepoServiceCaller = new CallerMock<M2RepoService>(m2RepoService);
        specManagementServiceCaller = new CallerMock<SpecManagementService>(specManagementService);
        doNothing().when(wizardPageStatusChangeEvent).fire(any(WizardPageStatusChangeEvent.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final ContentChangeHandler handler = (ContentChangeHandler) invocation.getArguments()[0];
                contentChangeHandlers.add(handler);
                return null;
            }
        }).when(view).addContentChangeHandler(any(ContentChangeHandler.class));

        doAnswer(contentHandlerAnswer).when(view).setArtifactId(anyString());
        doAnswer(contentHandlerAnswer).when(view).setGroupId(anyString());
        doAnswer(contentHandlerAnswer).when(view).setVersion(anyString());

        presenter = spy(new NewContainerFormPresenter(
                logger, view,
                presenterProvider, m2RepoServiceCaller,
                specManagementServiceCaller,
                wizardPageStatusChangeEvent));
        doReturn(artifactListWidgetPresenter).when(presenterProvider).get();
    }

    private void fireContentHandlers() {
        for (final ContentChangeHandler handler : contentChangeHandlers) {
            handler.onContentChange();
        }
    }

    @Test
    public void testInit() {
        presenter.init();
        final ContentChangeHandler contentChangeHandler = mock(ContentChangeHandler.class);

        presenter.addContentChangeHandler(contentChangeHandler);

        view.setVersion("1.0");
        view.setArtifactId("artifact");
        view.setGroupId("group");

        verify(view).init(presenter);
        verify(wizardPageStatusChangeEvent, times(3)).fire(any(WizardPageStatusChangeEvent.class));
        verify(contentChangeHandler, times(3)).onContentChange();
    }

    @Test
    public void testClear() {
        presenter.clear();

        verify(view).clear();
        assertEquals(NewContainerFormPresenter.Mode.OPTIONAL, presenter.getMode());
        assertNull(presenter.getServerTemplate());
    }

    @Test
    public void testIsEmpty() {
        when(view.getContainerName()).thenReturn(" ");
        when(view.getGroupId()).thenReturn(" ");
        when(view.getArtifactId()).thenReturn(" ");
        when(view.getVersion()).thenReturn(" ");

        assertTrue(presenter.isEmpty());
    }

    @Test
    public void testIsValid() {
        when(view.getContainerName()).thenReturn(" ").thenReturn("containerName").thenReturn("");
        when(view.getGroupId()).thenReturn(" ").thenReturn("groupId").thenReturn("");
        when(view.getArtifactId()).thenReturn(" ").thenReturn("artifactId").thenReturn("");
        when(view.getVersion()).thenReturn(" ").thenReturn("1.0").thenReturn("");

        assertTrue(presenter.isValid());

        verify(view).noErrors();

        presenter.setServerTemplate(new ServerTemplate());

        assertTrue(presenter.isValid());

        verify(view).noErrorOnContainerName();
        verify(view).noErrorOnGroupId();
        verify(view).noErrorOnArtifactId();
        verify(view).noErrorOnVersion();

        assertFalse(presenter.isValid());

        verify(view).errorOnContainerName();
        verify(view).errorOnGroupId();
        verify(view).errorOnArtifactId();
        verify(view).errorOnVersion();
    }

    @Test
    public void testIsValidWithTemplateProductionMode() {
        when(view.getContainerName()).thenReturn("containerName");
        when(view.getGroupId()).thenReturn("groupId");
        when(view.getArtifactId()).thenReturn("artifactId");
        when(view.getVersion()).thenReturn("1.0");

        ServerTemplate serverTemplate = new ServerTemplate();
        serverTemplate.setMode(KieServerMode.PRODUCTION);

        presenter.setServerTemplate(serverTemplate);

        assertTrue(presenter.isValid());

        verify(view).noErrorOnContainerName();
        verify(view).noErrorOnGroupId();
        verify(view).noErrorOnArtifactId();
        verify(view).noErrorOnVersion();

        when(view.getVersion()).thenReturn("1.0" + SNAPSHOT);

        assertFalse(presenter.isValid());

        verify(view, times(2)).noErrorOnContainerName();
        verify(view, times(2)).noErrorOnGroupId();
        verify(view, times(2)).noErrorOnArtifactId();

        verify(view).errorOnVersion();
        verify(view).errorProductionModeSupportsDoesntSnapshots();
    }

    @Test
    public void testIsValidWithTemplateDevelopmentModeSNAPSHOT() {
        testIsValidDevelopmentMode("1.0" + SNAPSHOT);
    }

    @Test
    public void testIsValidWithTemplateDevelopmentModeNonSNAPSHOT() {
        testIsValidDevelopmentMode("1.0");
    }

    private void testIsValidDevelopmentMode(String version) {
        when(view.getContainerName()).thenReturn("containerName");
        when(view.getGroupId()).thenReturn("groupId");
        when(view.getArtifactId()).thenReturn("artifactId");
        when(view.getVersion()).thenReturn(version);

        ServerTemplate serverTemplate = new ServerTemplate();
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);

        presenter.setServerTemplate(serverTemplate);

        assertTrue(presenter.isValid());

        verify(view).noErrorOnContainerName();
        verify(view).noErrorOnGroupId();
        verify(view).noErrorOnArtifactId();
        verify(view).noErrorOnVersion();

        assertTrue(presenter.isValid());

        verify(view, times(2)).noErrorOnContainerName();
        verify(view, times(2)).noErrorOnGroupId();
        verify(view, times(2)).noErrorOnArtifactId();

        verify(view, never()).errorOnVersion();
        verify(view, never()).errorProductionModeSupportsDoesntSnapshots();
    }

    @Test
    public void testIsFieldValid() {
        when(view.getContainerName()).thenReturn(" ");
        when(view.getGroupId()).thenReturn(" ");
        when(view.getArtifactId()).thenReturn(" ");
        when(view.getVersion()).thenReturn(" ");

        assertTrue(presenter.isContainerNameValid());
        assertTrue(presenter.isGroupIdValid());
        assertTrue(presenter.isArtifactIdValid());
        assertTrue(presenter.isVersionValid());

        presenter.setServerTemplate(new ServerTemplate());

        assertFalse(presenter.isContainerNameValid());
        assertFalse(presenter.isGroupIdValid());
        assertFalse(presenter.isArtifactIdValid());
        assertFalse(presenter.isVersionValid());
    }

    @Test
    public void testOnDependencyPathSelectedEvent() {
        final String path = "org:kie:1.0";
        final GAV gav = new GAV(path);
        when(m2RepoService.loadGAVFromJar(path)).thenReturn(gav);
        when(view.getContainerName()).thenReturn("containerName");
        when(view.getContainerAlias()).thenReturn("containerAlias");
        when(view.getGroupId()).thenReturn(gav.getGroupId());
        when(view.getArtifactId()).thenReturn(gav.getArtifactId());
        when(view.getVersion()).thenReturn(gav.getVersion());

        presenter.asWidget();

        presenter.onDependencyPathSelectedEvent(new DependencyPathSelectedEvent(artifactListWidgetPresenter, path));

        verify(m2RepoService).loadGAVFromJar(path);
        verify(view).setGroupId(gav.getGroupId());
        verify(view).setArtifactId(gav.getArtifactId());
        verify(view).setVersion(gav.getVersion());
        verify(wizardPageStatusChangeEvent).fire(any(WizardPageStatusChangeEvent.class));

        final ContainerSpec containerSpec = presenter.buildContainerSpec("templateId", Collections.<Capability, ContainerConfig>emptyMap());

        assertEquals(new ReleaseId(gav.getGroupId(), gav.getArtifactId(), gav.getVersion()), containerSpec.getReleasedId());
        assertEquals(KieContainerStatus.STOPPED, containerSpec.getStatus());
        assertEquals("containerAlias", containerSpec.getContainerName());
        assertEquals("containerName", containerSpec.getId());
    }

    @Test
    public void testOnDependencyPathSelectedEventWithDefaultContainerNameWhenContainerNameIsNotEmpty() {
        final String path = "org:kie:1.0";
        final String templateId = "templateId";
        final GAV gav = new GAV(path);
        final ServerTemplate serverTemplate = mock(ServerTemplate.class);

        when(serverTemplate.getId()).thenReturn(templateId);
        when(m2RepoService.loadGAVFromJar(path)).thenReturn(gav);
        when(specManagementService.validContainerId(templateId, path)).thenReturn(path);
        when(view.getContainerName()).thenReturn("custom");

        presenter.setServerTemplate(serverTemplate);
        presenter.asWidget();

        presenter.onDependencyPathSelectedEvent(new DependencyPathSelectedEvent(artifactListWidgetPresenter, path));

        verify(view, never()).setContainerName(path);
    }

    @Test
    public void testOnDependencyPathSelectedEventWithDefaultContainerNameWhenContainerNameIsEmpty() {
        final String path = "org:kie:1.0";
        final String templateId = "templateId";
        final GAV gav = new GAV(path);
        final ServerTemplate serverTemplate = mock(ServerTemplate.class);

        when(serverTemplate.getId()).thenReturn(templateId);
        when(m2RepoService.loadGAVFromJar(path)).thenReturn(gav);
        when(specManagementService.validContainerId(templateId, path)).thenReturn(path);
        when(view.getContainerName()).thenReturn("");

        presenter.setServerTemplate(serverTemplate);
        presenter.asWidget();

        presenter.onDependencyPathSelectedEvent(new DependencyPathSelectedEvent(artifactListWidgetPresenter, path));

        verify(view).setContainerName(path);
    }

    @Test
    public void testOnDependencyPathSelectedEventWithDefaultContainerNameWhenServerTemplateIsNull() {
        final String path = "org:kie:1.0";
        final GAV gav = new GAV(path);

        when(m2RepoService.loadGAVFromJar(path)).thenReturn(gav);

        when(view.getContainerName()).thenReturn("");

        presenter.setServerTemplate(null);
        presenter.asWidget();

        presenter.onDependencyPathSelectedEvent(new DependencyPathSelectedEvent(artifactListWidgetPresenter, path));

        verify(view).setContainerName(path);
        verify(specManagementService, never()).validContainerId(anyString(), anyString());
    }

    @Test
    public void testNewContainerStarted() {
        final String path = "org:kie:1.0";
        final GAV gav = new GAV(path);
        when(m2RepoService.loadGAVFromJar(path)).thenReturn(gav);
        when(view.getContainerName()).thenReturn("containerName");
        when(view.getContainerAlias()).thenReturn("containerAlias");
        when(view.getGroupId()).thenReturn(gav.getGroupId());
        when(view.getArtifactId()).thenReturn(gav.getArtifactId());
        when(view.getVersion()).thenReturn(gav.getVersion());
        when(view.isStartContainer()).thenReturn(true);

        presenter.asWidget();

        final ContainerSpec containerSpec = presenter.buildContainerSpec("templateId", Collections.<Capability, ContainerConfig>emptyMap());

        assertEquals(new ReleaseId(gav.getGroupId(), gav.getArtifactId(), gav.getVersion()), containerSpec.getReleasedId());
        assertEquals(KieContainerStatus.STARTED, containerSpec.getStatus());
        assertEquals("containerAlias", containerSpec.getContainerName());
        assertEquals("containerName", containerSpec.getId());
    }

    @Test
    public void testIsArtifactSupportedByServerWhenVersionIsEmptyAndServerTemplateIsNull() {
        when(view.getVersion()).thenReturn("");
        ServerTemplate serverTemplate = null;
        presenter.setServerTemplate(serverTemplate);
        assertTrue(presenter.isArtifactSupportedByServer());
    }

    @Test
    public void testIsArtifactSupportedByServerWhenVersionIsNotEmptyAndServerTemplateIsNull() {
        when(view.getVersion()).thenReturn("1.0.0");
        ServerTemplate serverTemplate = null;
        presenter.setServerTemplate(serverTemplate);
        assertTrue(presenter.isArtifactSupportedByServer());
    }

    @Test
    public void testIsArtifactSupportedByServerWhenVersionIsSnapshotAndServerTemplateIsNull() {
        when(view.getVersion()).thenReturn("1.0.0-SNAPSHOT");
        ServerTemplate serverTemplate = null;
        presenter.setServerTemplate(serverTemplate);
        assertTrue(presenter.isArtifactSupportedByServer());
    }

    @Test
    public void testIsArtifactSupportedByServerWhenVersionIsEmptyAndServerTemplateModeIsProduction() {
        when(view.getVersion()).thenReturn("");
        ServerTemplate serverTemplate = new ServerTemplate();
        serverTemplate.setMode(KieServerMode.PRODUCTION);
        presenter.setServerTemplate(serverTemplate);
        assertTrue(presenter.isArtifactSupportedByServer());
    }

    @Test
    public void testIsArtifactSupportedByServerWhenVersionIsEmptyAndServerTemplateModeIsDevelopment() {
        when(view.getVersion()).thenReturn("");
        ServerTemplate serverTemplate = new ServerTemplate();
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);
        presenter.setServerTemplate(serverTemplate);
        assertTrue(presenter.isArtifactSupportedByServer());
    }

    @Test
    public void testIsArtifactSupportedByServerWhenVersionIsNotEmptyAndServerTemplateModeIsProduction() {
        when(view.getVersion()).thenReturn("1.0.0");
        ServerTemplate serverTemplate = new ServerTemplate();
        serverTemplate.setMode(KieServerMode.PRODUCTION);
        presenter.setServerTemplate(serverTemplate);
        assertTrue(presenter.isArtifactSupportedByServer());
    }

    @Test
    public void testIsArtifactSupportedByServerWhenVersionIsNotEmptyAndServerTemplateModeIsDevelopment() {
        when(view.getVersion()).thenReturn("1.0.0");
        ServerTemplate serverTemplate = new ServerTemplate();
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);
        presenter.setServerTemplate(serverTemplate);
        assertTrue(presenter.isArtifactSupportedByServer());
    }

    @Test
    public void testIsArtifactSupportedByServerWhenVersionIsSnapshoptAndServerTemplateModeIsProduction() {
        when(view.getVersion()).thenReturn("1.0.0-SNAPSHOT");
        ServerTemplate serverTemplate = new ServerTemplate();
        serverTemplate.setMode(KieServerMode.PRODUCTION);
        presenter.setServerTemplate(serverTemplate);
        assertFalse(presenter.isArtifactSupportedByServer());
    }

    @Test
    public void testIsArtifactSupportedByServerWhenVersionIsNotSnapshotAndServerTemplateModeIsDevelopment() {
        when(view.getVersion()).thenReturn("1.0.0-SNAPSHOT");
        ServerTemplate serverTemplate = new ServerTemplate();
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);
        presenter.setServerTemplate(serverTemplate);
        assertTrue(presenter.isArtifactSupportedByServer());
    }
}
