/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.handlers;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.wizard.NewGuidedDecisionTableWizardHelper;
import org.drools.workbench.screens.guided.dtable.client.wizard.table.NewGuidedDecisionTableWizard;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewGuidedDecisionTableHandlerTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private GuidedDecisionTableEditorService service;
    private Caller<GuidedDecisionTableEditorService> serviceCaller;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private EventSourceMock<NotificationEvent> mockNotificationEvent;

    @Mock
    private AsyncPackageDataModelOracleFactory oracleFactory;

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private SyncBeanDef<NewGuidedDecisionTableWizard> wizardBeanDef;

    @Mock
    private NewGuidedDecisionTableWizard wizardBean;

    @Mock
    private EventSourceMock<NewResourceSuccessEvent> newResourceSuccessEventMock;

    @GwtMock
    private GuidedDecisionTableOptions options;

    @Captor
    private ArgumentCaptor<Path> pathCaptor;

    @Captor
    private ArgumentCaptor<String> fileNameCaptor;

    private NewGuidedDecisionTableHandler handler;
    private NewGuidedDecisionTableWizardHelper helper;
    private GuidedDTableResourceType resourceType = new GuidedDTableResourceType(new Decision());

    @Before
    public void setup() {
        serviceCaller = new CallerMock<>(service);
        helper = new NewGuidedDecisionTableWizardHelper(serviceCaller,
                                                        oracleFactory,
                                                        beanManager);
        final NewGuidedDecisionTableHandler wrapped = new NewGuidedDecisionTableHandler(placeManager,
                                                                                        serviceCaller,
                                                                                        resourceType,
                                                                                        options,
                                                                                        busyIndicatorView,
                                                                                        helper) {

            {
                this.notificationEvent = mockNotificationEvent;
                this.newResourceSuccessEvent = newResourceSuccessEventMock;
            }
        };
        handler = spy(wrapped);

        when(beanManager.lookupBean(eq(NewGuidedDecisionTableWizard.class))).thenReturn(wizardBeanDef);
        when(wizardBeanDef.getInstance()).thenReturn(wizardBean);

        when(service.create(any(Path.class),
                            any(String.class),
                            any(GuidedDecisionTable52.class),
                            any(String.class))).<Path>thenAnswer((invocation) -> {
            final Path path = ((Path) invocation.getArguments()[0]);
            final String fileName = ((String) invocation.getArguments()[1]);
            final Path newPath = PathFactory.newPath(fileName,
                                                     path.toURI() + "/" + fileName);
            return newPath;
        });
    }

    @Test
    public void testCreate_WithWizard() {
        final String fileName = "fileName";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://project/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);
        when(options.isUsingWizard()).thenReturn(true);
        when(options.getTableFormat()).thenReturn(TableFormat.EXTENDED_ENTRY);
        when(options.getHitPolicy()).thenReturn(GuidedDecisionTable52.HitPolicy.FIRST_HIT);

        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(wizardBean,
               times(1)).setContent(pathCaptor.capture(),
                                    fileNameCaptor.capture(),
                                    eq(TableFormat.EXTENDED_ENTRY),
                                    eq(GuidedDecisionTable52.HitPolicy.FIRST_HIT),
                                    any(),
                                    any(NewGuidedDecisionTableWizard.GuidedDecisionTableWizardHandler.class));
    }

    @Test
    public void testCreate_WithoutWizard() {
        final String fileName = "fileName";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://project/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);
        when(options.isUsingWizard()).thenReturn(false);

        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
        verify(newResourcePresenter,
               times(1)).complete();
        verify(mockNotificationEvent,
               times(1)).fire(any(NotificationEvent.class));
        verify(newResourceSuccessEventMock,
               times(1)).fire(any(NewResourceSuccessEvent.class));
        verify(placeManager,
               times(1)).goTo(pathCaptor.capture());

        assertEquals("default://project/src/main/resources/fileName.gdst",
                     pathCaptor.getValue().toURI());

        verify(service,
               times(1)).create(eq(resourcesPath),
                                eq(fileName + "." + resourceType.getSuffix()),
                                any(GuidedDecisionTable52.class),
                                any(String.class));
    }

    @Test
    public void testResolvedHitPolicy() throws
            Exception {

        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://project/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);
        when(options.isUsingWizard()).thenReturn(false);
        when(options.getHitPolicy()).thenReturn(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT);

        handler.create(pkg,
                       "fileName",
                       newResourcePresenter);

        final ArgumentCaptor<GuidedDecisionTable52> dtableArgumentCaptor = ArgumentCaptor.forClass(GuidedDecisionTable52.class);

        verify(service,
               times(1)).create(eq(resourcesPath),
                                eq("fileName." + resourceType.getSuffix()),
                                dtableArgumentCaptor.capture(),
                                any(String.class));

        final GuidedDecisionTable52 model = dtableArgumentCaptor.getValue();

        assertEquals(1,
                     model.getMetadataCols()
                             .size());
        final MetadataCol52 metadataCol52 = model.getMetadataCols().get(0);
        assertEquals(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT_METADATA_NAME,
                     metadataCol52.getMetadata());
    }
}
