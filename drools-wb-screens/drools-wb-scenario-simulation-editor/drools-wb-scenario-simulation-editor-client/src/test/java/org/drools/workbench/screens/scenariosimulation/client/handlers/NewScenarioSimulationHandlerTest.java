/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewScenarioSimulationHandlerTest extends AbstractNewScenarioTest {

    @Mock
    private BusyIndicatorView busyIndicatorViewMock;
    @Mock
    private ScenarioSimulationService scenarioSimulationServiceMock;
    @Mock
    private ScenarioSimulationResourceType resourceTypeMock;
    @Mock
    private EventSourceMock notificationEventMock;
    @Mock
    private EventSourceMock newResourceSuccessEventMock;
    @Mock
    private PlaceManager placeManagerMock;
    @Mock
    private AuthorizationManager authorizationManagerMock;
    @Mock
    private SessionInfo sessionInfoMock;

    @Mock
    private User userMock;

    @Captor
    private ArgumentCaptor<ResourceRef> refArgumentCaptor;

    @Mock
    private TitledAttachmentFileWidget uploadWidgetMock;

    @Mock
    private SourceTypeSelector sourceTypeSelectorMock;

    private NewScenarioSimulationHandler handler;

    private CallerMock<ScenarioSimulationService> scenarioSimulationServiceCallerMock;

    @Before
    public void setup() throws Exception {
        super.setup();
        scenarioSimulationServiceCallerMock = new CallerMock<>(scenarioSimulationServiceMock);
        handler = spy(new NewScenarioSimulationHandler(resourceTypeMock,
                                                       busyIndicatorViewMock,
                                                       notificationEventMock,
                                                       newResourceSuccessEventMock,
                                                       placeManagerMock,
                                                       scenarioSimulationServiceCallerMock,
                                                       authorizationManagerMock,
                                                       sessionInfoMock,
                                                       libraryPlacesMock,
                                                       assetQueryServiceMock) {
            {
                this.uploadWidget = uploadWidgetMock;
                this.sourceTypeSelector = sourceTypeSelectorMock;
            }
        });
        when(sessionInfoMock.getIdentity()).thenReturn(userMock);
    }

    @Test
    public void createValidDMO() {
        createCommon(ScenarioSimulationModel.Type.RULE, true, true);
    }

    @Test
    public void createInvalidDMN() {
        createCommon(ScenarioSimulationModel.Type.DMN, false, false);
    }

    @Test
    public void createValidDMN() {
        createCommon(ScenarioSimulationModel.Type.DMN, true, true);
    }

    @Test
    public void checkCanCreateWhenFeatureDisabled() {
        when(authorizationManagerMock.authorize(any(ResourceRef.class),
                                                eq(ResourceAction.READ),
                                                eq(userMock))).thenReturn(false);
        assertFalse(handler.canCreate());
        assertResourceRef();
    }

    @Test
    public void checkCanCreateWhenFeatureEnabled() {
        when(authorizationManagerMock.authorize(any(ResourceRef.class),
                                                eq(ResourceAction.READ),
                                                eq(userMock))).thenReturn(true);
        assertTrue(handler.canCreate());
        assertResourceRef();
    }

    @Test
    public void checkRightResourceType() throws Exception {
        doReturn(true).when(sourceTypeSelectorMock).validate();
        when(sourceTypeSelectorMock.getSelectedType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        handler.create(new Package(),
                       "newfile.scesim",
                       mock(NewResourcePresenter.class));

        verify(busyIndicatorViewMock).showBusyIndicator("Saving");
        verify(busyIndicatorViewMock).hideBusyIndicator();
        verify(notificationEventMock).fire(any(NotificationEvent.class));
        verify(newResourceSuccessEventMock).fire(any(NewResourcePresenter.class));
        verify(placeManagerMock).goTo(any(Path.class));
    }

    @Test
    public void getCommandMethod() {
        NewResourcePresenter newResourcePresenterMock = mock(NewResourcePresenter.class);
        handler.getCommandMethod(newResourcePresenterMock);
        verify(uploadWidgetMock, times(1)).clearStatus();
        verify(newResourcePresenterMock, times(1)).show(any());
    }

    private void createCommon(ScenarioSimulationModel.Type type, boolean validate, boolean called) {
        doReturn(validate).when(sourceTypeSelectorMock).validate();
        when(sourceTypeSelectorMock.getSelectedType()).thenReturn(type);
        handler.create(mock(Package.class), "BASEFILENAME", mock(NewResourcePresenter.class));
        if (called) {
            verify(busyIndicatorViewMock, times(1)).showBusyIndicator(eq(CommonConstants.INSTANCE.Saving()));
            verify(scenarioSimulationServiceMock, times(1)).create(any(), any(), any(), any(), any(), any());
        }
    }

    private void assertResourceRef() {
        verify(authorizationManagerMock).authorize(refArgumentCaptor.capture(),
                                                   eq(ResourceAction.READ),
                                                   eq(userMock));
        assertEquals(ScenarioSimulationEditorPresenter.IDENTIFIER,
                     refArgumentCaptor.getValue().getIdentifier());
        assertEquals(ActivityResourceType.EDITOR,
                     refArgumentCaptor.getValue().getResourceType());
    }
}