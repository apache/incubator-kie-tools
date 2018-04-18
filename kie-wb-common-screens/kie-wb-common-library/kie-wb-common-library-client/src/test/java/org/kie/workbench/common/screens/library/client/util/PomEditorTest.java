/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.util;

import java.util.Collections;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.defaulteditor.client.editor.KieTextEditorView;
import org.kie.workbench.common.screens.projecteditor.model.InvalidPomException;
import org.kie.workbench.common.screens.projecteditor.service.PomEditorService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PomEditorTest {

    @Mock
    private KieTextEditorView view;

    @Mock
    private PomEditorService service;

    @Mock
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    @Mock
    private Path pomPath;

    @Mock
    private PlaceRequest placeRequest;

    @Mock
    private BusyIndicatorView mockBusyIndicatorView;

    @Mock
    private VersionRecordManager mockVersionRecordManager;

    @Mock
    private OverviewWidgetPresenter overviewWidgetPresenter;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private TranslationService translationService;

    private PomEditor presenter;

    private String pomXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "<modelVersion>4.0.0</modelVersion>\n" +
            "<groupId>groupId</groupId>\n" +
            "<artifactId>artifactId</artifactId>\n" +
            "<version>0.0.1</version>\n" +
            "<name>name</name>\n" +
            "<description>description</description>\n" +
            "</project>";

    private String comment = "comment";

    private GAV gav = new GAV("groupId",
                              "artifactId",
                              "0.0.1");

    @Before
    public void setup() {
        presenter = new PomEditor(view,
                                  new CallerMock<>(service),
                                  conflictingRepositoriesPopup,
                                  notificationEvent,
                                  translationService) {
            {
                busyIndicatorView = mockBusyIndicatorView;
                versionRecordManager = mockVersionRecordManager;
                overviewWidget = overviewWidgetPresenter;
                notification = notificationEvent;
            }
        };
        when(view.getContent()).thenReturn(pomXml);
    }

    @Test
    public void testSaveNonClashingGAV() {
        presenter.save(comment);

        verify(service,
               times(1)).save(any(ObservablePath.class),
                              eq(pomXml),
                              any(Metadata.class),
                              eq(comment),
                              eq(DeploymentMode.VALIDATED));
        verify(view,
               times(1)).showBusyIndicator(eq(CommonConstants.INSTANCE.Saving()));
        verify(view,
               times(1)).hideBusyIndicator();
    }

    @Test
    public void testSaveInvalid() {
        doThrow(new InvalidPomException(10, 10))
                .when(service).save(any(ObservablePath.class),
                                    eq(pomXml),
                                    any(Metadata.class),
                                    eq(comment),
                                    eq(DeploymentMode.VALIDATED));

        presenter.save(comment);

        verify(service,
               times(1)).save(any(ObservablePath.class),
                              eq(pomXml),
                              any(Metadata.class),
                              eq(comment),
                              eq(DeploymentMode.VALIDATED));

        verify(view,
               times(1)).showBusyIndicator(eq(CommonConstants.INSTANCE.Saving()));
        verify(view,
               times(1)).hideBusyIndicator();
        verify(notificationEvent,
               times(1)).fire(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSaveClashingGAV() {
        final GAVAlreadyExistsException gae = new GAVAlreadyExistsException(gav,
                                                                            Collections.<MavenRepositoryMetadata>emptySet());
        doThrow(gae).when(service).save(any(ObservablePath.class),
                                        eq(pomXml),
                                        any(Metadata.class),
                                        eq(comment),
                                        eq(DeploymentMode.VALIDATED));

        presenter.save(comment);

        verify(service,
               times(1)).save(any(ObservablePath.class),
                              eq(pomXml),
                              any(Metadata.class),
                              eq(comment),
                              eq(DeploymentMode.VALIDATED));
        verify(view,
               times(1)).showBusyIndicator(eq(CommonConstants.INSTANCE.Saving()));
        verify(view,
               times(1)).hideBusyIndicator();

        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);

        verify(conflictingRepositoriesPopup,
               times(1)).setContent(eq(gav),
                                    any(Set.class),
                                    commandArgumentCaptor.capture());
        verify(conflictingRepositoriesPopup,
               times(1)).show();

        assertNotNull(commandArgumentCaptor.getValue());

        //Emulate User electing to force save
        commandArgumentCaptor.getValue().execute();

        verify(service,
               times(1)).save(any(ObservablePath.class),
                              eq(pomXml),
                              any(Metadata.class),
                              eq(comment),
                              eq(DeploymentMode.FORCED));
        //We attempted to save the POM twice
        verify(view,
               times(2)).showBusyIndicator(eq(CommonConstants.INSTANCE.Saving()));
        //We hid the BusyPopup 1 x per save attempt
        verify(view,
               times(2)).hideBusyIndicator();
    }
}