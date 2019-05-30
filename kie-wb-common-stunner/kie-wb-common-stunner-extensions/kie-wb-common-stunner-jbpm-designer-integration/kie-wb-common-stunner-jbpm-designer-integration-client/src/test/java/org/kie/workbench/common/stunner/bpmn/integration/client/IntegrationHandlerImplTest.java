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

package org.kie.workbench.common.stunner.bpmn.integration.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.integration.client.resources.IntegrationClientConstants;
import org.kie.workbench.common.stunner.bpmn.integration.service.IntegrationService;
import org.kie.workbench.common.stunner.bpmn.integration.service.MigrateRequest;
import org.kie.workbench.common.stunner.bpmn.integration.service.MigrateResult;
import org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopup;
import org.kie.workbench.common.stunner.client.widgets.popups.PopupUtil;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingResponse;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.stunner.core.client.util.TestUtils.prepareServiceCallerError;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationHandlerImplTest {

    private static final String BPMN_EXTENSION = ".bpmn";
    private static final String BPMN2_EXTENSION = ".bpmn2";

    private static final String JBPM_NAME = "TestProcess";
    private static final String JBPM_FILE_NAME = JBPM_NAME + BPMN2_EXTENSION;
    private static final String JBPM_FILE_URI = "default:///test/src/main/resources/com/myspace/test/" + JBPM_FILE_NAME;

    private static final String STUNNER_NAME = "StunnerTestProcess";
    private static final String STUNNER_FILE_NAME = STUNNER_NAME + BPMN_EXTENSION;
    private static final String STUNNER_FILE_URI = "default:///test/src/main/resources/com/myspace/test/" + STUNNER_FILE_NAME;

    private static final String MigrateActionConfirmSaveInformationTitle = "MigrateActionConfirmSaveInformationTitle";
    private static final String MigrateActionConfirmSaveMessage = "MigrateActionConfirmSaveMessage";
    private static final String MigrateActionTitle = "MigrateActionTitle";
    private static final String MigrateAction = "MigrateAction";
    private static final String MigrateToStunnerConfirmAction = "MigrateToStunnerConfirmAction";
    private static final String MigrateToStunnerInfoWarningsProducedMessage = "MigrateToStunnerInfoWarningsProducedMessage";
    private static final String MigrateDiagramSuccessfullyMigratedMessage = "MigrateDiagramSuccessfullyMigratedMessage";
    private static final String MigrateToStunnerErrorsProducedMessage = "MigrateToStunnerErrorsProducedMessage";
    private static final String MigrateActionUnexpectedErrorMessage = "MigrateActionUnexpectedErrorMessage";
    private static final String MigrateToJBPMDesignerActionWarning = "MigrateToJBPMDesignerActionWarning";
    private static final String MigrateToJBPMDesignerConfirmAction = "MigrateToJBPMDesignerConfirmAction";
    private static final String MigrateToStunnerNoDiagramHasBeenReturned = "MigrateToStunnerNoDiagramHasBeenReturned";

    private static final String ToStunnerCommitMessage = "ToStunnerCommitMessage";
    private static final String ToJBPMCommitMessage = "ToJBPMCommitMessage";

    private static final String ERROR = "ERROR";

    @Mock
    private IntegrationService integrationService;

    private Caller<IntegrationService> integrationServiceCaller;

    @Mock
    private PlaceManager placeManger;

    @Mock
    private PopupUtil popupUtil;

    @Mock
    private ErrorPopupPresenter errorPopup;

    @Mock
    private MarshallingResponsePopup responsePopup;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    @Mock
    private Path jbpmPath;

    @Mock
    private Path stunnerPath;

    @Mock
    private ProjectDiagram projectDiagram;

    @Mock
    private PlaceRequest place;

    @Mock
    private PlaceRequest newPlace;

    @Captor
    private ArgumentCaptor<Command> saveYesCommandCaptor;

    @Captor
    private ArgumentCaptor<Command> saveNoCommandCaptor;

    @Captor
    private ArgumentCaptor<Command> startMigrationCommandCaptor;

    @Captor
    private ArgumentCaptor<Command> marshalingResponseCaptor;

    private IntegrationHandlerImpl handler;

    private ParameterizedCommand<Consumer<Boolean>> saveSuccessfulCommand = parameter -> parameter.accept(true);

    @Before
    public void setUp() {
        when(translationService.getValue(IntegrationClientConstants.MigrateActionConfirmSaveInformationTitle)).thenReturn(MigrateActionConfirmSaveInformationTitle);
        when(translationService.getValue(IntegrationClientConstants.MigrateActionConfirmSaveMessage)).thenReturn(MigrateActionConfirmSaveMessage);
        when(translationService.getValue(IntegrationClientConstants.MigrateActionTitle)).thenReturn(MigrateActionTitle);
        when(translationService.getValue(IntegrationClientConstants.MigrateAction)).thenReturn(MigrateAction);
        when(translationService.getValue(IntegrationClientConstants.MigrateToStunnerConfirmAction)).thenReturn(MigrateToStunnerConfirmAction);
        when(translationService.getValue(IntegrationClientConstants.MigrateDiagramSuccessfullyMigratedMessage)).thenReturn(MigrateDiagramSuccessfullyMigratedMessage);
        when(translationService.getValue(IntegrationClientConstants.MigrateToStunnerCommitMessage, JBPM_FILE_NAME)).thenReturn(ToStunnerCommitMessage);
        when(translationService.getValue(IntegrationClientConstants.MigrateToJBPMDesignerCommitMessage, STUNNER_FILE_NAME)).thenReturn(ToJBPMCommitMessage);

        when(translationService.getValue(IntegrationClientConstants.MigrateToStunnerInfoWarningsProducedMessage)).thenReturn(MigrateToStunnerInfoWarningsProducedMessage);
        when(translationService.getValue(IntegrationClientConstants.MigrateToStunnerErrorsProducedMessage)).thenReturn(MigrateToStunnerErrorsProducedMessage);
        when(translationService.getValue(IntegrationClientConstants.MigrateActionUnexpectedErrorMessage)).thenReturn(MigrateActionUnexpectedErrorMessage);
        when(translationService.getValue(IntegrationClientConstants.MigrateToJBPMDesignerActionWarning)).thenReturn(MigrateToJBPMDesignerActionWarning);
        when(translationService.getValue(IntegrationClientConstants.MigrateToJBPMDesignerConfirmAction)).thenReturn(MigrateToJBPMDesignerConfirmAction);
        when(translationService.getValue(IntegrationClientConstants.MigrateToStunnerNoDiagramHasBeenReturned)).thenReturn(MigrateToStunnerNoDiagramHasBeenReturned);

        when(jbpmPath.getFileName()).thenReturn(JBPM_FILE_NAME);
        when(jbpmPath.toURI()).thenReturn(JBPM_FILE_URI);
        when(jbpmPath.toString()).thenReturn(JBPM_FILE_URI);
        when(stunnerPath.getFileName()).thenReturn(STUNNER_FILE_NAME);
        when(stunnerPath.toURI()).thenReturn(STUNNER_FILE_URI);
        when(stunnerPath.toString()).thenReturn(STUNNER_FILE_URI);

        integrationServiceCaller = spy(new CallerMock<>(integrationService));
        handler = new IntegrationHandlerImpl(integrationServiceCaller, placeManger, popupUtil, errorPopup, responsePopup, translationService, notification) {
            @Override
            PlaceRequest createTargetPlace(Path path) {
                return newPlace;
            }
        };
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerDirtyWithMessagesSuccessful() {
        testMigrateFromJBPMDesignerToStunnerSuccessful(true, Arrays.asList(mock(MarshallingMessage.class), mock(MarshallingMessage.class)));
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerDirtyWithoutMessagesSuccessful() {
        testMigrateFromJBPMDesignerToStunnerSuccessful(true, new ArrayList<>());
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerNotDirtyWithMessagesSuccessful() {
        testMigrateFromJBPMDesignerToStunnerSuccessful(false, Arrays.asList(mock(MarshallingMessage.class), mock(MarshallingMessage.class)));
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerNotDirtyWithoutMessagesSuccessful() {
        testMigrateFromJBPMDesignerToStunnerSuccessful(false, new ArrayList<>());
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerDirtyWithMarshallingWithErrors() {
        testMigrateFromJBPMDesignerToStunnerMarshallingWithErrors(true);
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerNotDirtyWithMarshallingWithErrors() {
        testMigrateFromJBPMDesignerToStunnerMarshallingWithErrors(false);
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerDirtyWithMessagesUnSuccessful() {
        testMigrateFromJBPMDesignerToStunnerWithServiceError(true, Arrays.asList(mock(MarshallingMessage.class), mock(MarshallingMessage.class)));
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerDirtyWithoutMessagesUnSuccessful() {
        testMigrateFromJBPMDesignerToStunnerWithServiceError(true, new ArrayList<>());
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerNotDirtyWithMessagesUnSuccessful() {
        testMigrateFromJBPMDesignerToStunnerWithServiceError(false, Arrays.asList(mock(MarshallingMessage.class), mock(MarshallingMessage.class)));
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerNotDirtyWithoutMessagesUnSuccessful() {
        testMigrateFromJBPMDesignerToStunnerWithServiceError(false, new ArrayList<>());
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerWithUnexpectedError() {
        prepareServiceCallerError(integrationService, integrationServiceCaller, new Throwable(ERROR));
        handler.migrateFromJBPMDesignerToStunner(jbpmPath, place, false, saveSuccessfulCommand);
        verifyUserWasAskedForStartingToStunnerMigrationAndRespond(true);
        String expectedMessage = MigrateActionUnexpectedErrorMessage + "\n" + ERROR;
        verify(errorPopup).showMessage(expectedMessage);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMigrateFromJBPMDesignerToStunnerWithUnexpectedErrorNoDiagramWasReturned() {
        MarshallingResponse<ProjectDiagram> marshallingResponse = new MarshallingResponse.MarshallingResponseBuilder<ProjectDiagram>()
                .state(MarshallingResponse.State.SUCCESS)
                .messages(Collections.EMPTY_LIST)
                .result(null)
                .build();
        when(integrationService.getDiagramByPath(jbpmPath, MarshallingRequest.Mode.AUTO)).thenReturn(marshallingResponse);
        handler.migrateFromJBPMDesignerToStunner(jbpmPath, place, false, saveSuccessfulCommand);
        verifyUserWasAskedForStartingToStunnerMigrationAndRespond(true);
        verify(errorPopup).showMessage(MigrateToStunnerNoDiagramHasBeenReturned);
    }

    @Test
    public void testMigrateFromStunnerToJBPMDesignerDirtySuccessful() {
        testMigrateFromStunnerToJBPMDesignerSuccessful(true);
    }

    @Test
    public void testMigrateFromStunnerToJBPMDesignerNoDirtySuccessful() {
        testMigrateFromStunnerWithServiceError(false);
    }

    @Test
    public void testMigrateFromStunnerToJBPMDesignerDirtyUnSuccessful() {
        testMigrateFromStunnerWithServiceError(true);
    }

    @Test
    public void testMigrateFromStunnerToJBPMDesignerNoDirtyUnSuccessful() {
        testMigrateFromStunnerToJBPMDesignerSuccessful(false);
    }

    @Test
    public void testMigrateFromStunnerToJBPMDesignerWithUnexpectedError() {
        prepareServiceCallerError(integrationService, integrationServiceCaller, new Throwable(ERROR));
        handler.migrateFromStunnerToJBPMDesigner(stunnerPath, place, false, saveSuccessfulCommand);
        verifyUserWasAskedForStartingToJBPMMigrationAndRespond(true);
        String expectedMessage = MigrateActionUnexpectedErrorMessage + "\n" + ERROR;
        verify(errorPopup).showMessage(expectedMessage);
    }

    private void testMigrateFromStunnerToJBPMDesignerSuccessful(boolean isDirty) {
        MigrateRequest expectedRequest = MigrateRequest.newFromStunnerToJBPMDesigner(stunnerPath, STUNNER_NAME, BPMN2_EXTENSION, ToJBPMCommitMessage);
        Path resultPath = mock(Path.class);
        MigrateResult result = new MigrateResult(resultPath, null, null, null);
        when(integrationService.migrateDiagram(expectedRequest)).thenReturn(result);

        handler.migrateFromStunnerToJBPMDesigner(stunnerPath, place, isDirty, saveSuccessfulCommand);
        verifySavePopupWasShownAndRespond(isDirty, true);
        verifyUserWasAskedForStartingToJBPMMigrationAndRespond(true);
        verifyMigrationFinished();
    }

    private void testMigrateFromStunnerWithServiceError(boolean isDirty) {
        MigrateRequest expectedRequest = MigrateRequest.newFromStunnerToJBPMDesigner(stunnerPath, STUNNER_NAME, BPMN2_EXTENSION, ToJBPMCommitMessage);
        Path resultPath = mock(Path.class);
        String messageKey = "messageKey";
        List<?> messageArguments = new ArrayList<>();
        MigrateResult result = new MigrateResult(resultPath, IntegrationService.ServiceError.JBPM_DESIGNER_PROCESS_ALREADY_EXIST, messageKey, messageArguments);
        when(integrationService.migrateDiagram(expectedRequest)).thenReturn(result);

        handler.migrateFromStunnerToJBPMDesigner(stunnerPath, place, isDirty, saveSuccessfulCommand);

        verifySavePopupWasShownAndRespond(isDirty, true);
        verifyUserWasAskedForStartingToJBPMMigrationAndRespond(true);
        when(translationService.getValue(messageKey, messageArguments.toArray())).thenReturn(ERROR);
        errorPopup.showMessage(ERROR);
    }

    private void testMigrateFromJBPMDesignerToStunnerSuccessful(boolean isDirty, List<MarshallingMessage> messages) {
        prepareMigrateFromJBPMDesignerToStunner(MarshallingResponse.State.SUCCESS, messages);
        MigrateRequest expectedRequest = MigrateRequest.newFromJBPMDesignerToStunner(jbpmPath, JBPM_NAME, BPMN_EXTENSION, ToStunnerCommitMessage, projectDiagram);
        Path resultPath = mock(Path.class);
        MigrateResult result = new MigrateResult(resultPath, null, null, null);
        when(integrationService.migrateDiagram(expectedRequest)).thenReturn(result);

        handler.migrateFromJBPMDesignerToStunner(jbpmPath, place, isDirty, saveSuccessfulCommand);

        //ask user for saving and say yes.
        verifySavePopupWasShownAndRespond(isDirty, true);
        //ask user for starting the migration and say yes
        verifyUserWasAskedForStartingToStunnerMigrationAndRespond(true);
        //show the marshaling messages and ask user for proceeding with the migration
        if (!messages.isEmpty()) {
            verifyMarshallingResponseWasShownAndRespond(messages, true);
        } else {
            verifyMarshallingResponseWasNeverShown();
        }
        verifyMigrationFinished();
    }

    private void testMigrateFromJBPMDesignerToStunnerWithServiceError(boolean isDirty, List<MarshallingMessage> messages) {
        prepareMigrateFromJBPMDesignerToStunner(MarshallingResponse.State.SUCCESS, messages);
        MigrateRequest expectedRequest = MigrateRequest.newFromJBPMDesignerToStunner(jbpmPath, JBPM_NAME, BPMN_EXTENSION, ToStunnerCommitMessage, projectDiagram);
        Path resultPath = mock(Path.class);
        String messageKey = "messageKey";
        List<?> messageArguments = new ArrayList<>();
        MigrateResult result = new MigrateResult(resultPath, IntegrationService.ServiceError.STUNNER_PROCESS_ALREADY_EXIST, messageKey, messageArguments);
        when(integrationService.migrateDiagram(expectedRequest)).thenReturn(result);

        handler.migrateFromJBPMDesignerToStunner(jbpmPath, place, isDirty, saveSuccessfulCommand);

        //ask user for saving and say yes.
        verifySavePopupWasShownAndRespond(isDirty, true);
        //ask user for starting the migration and say yes
        verifyUserWasAskedForStartingToStunnerMigrationAndRespond(true);
        //show the marshaling messages and ask user for proceeding with the migration
        if (!messages.isEmpty()) {
            verifyMarshallingResponseWasShownAndRespond(messages, true);
        } else {
            verifyMarshallingResponseWasNeverShown();
        }

        when(translationService.getValue(messageKey, messageArguments.toArray())).thenReturn(ERROR);
        errorPopup.showMessage(ERROR);
    }

    private void testMigrateFromJBPMDesignerToStunnerMarshallingWithErrors(boolean isDirty) {
        List<MarshallingMessage> messages = Arrays.asList(mock(MarshallingMessage.class), mock(MarshallingMessage.class));
        prepareMigrateFromJBPMDesignerToStunner(MarshallingResponse.State.ERROR, messages);
        MigrateRequest expectedRequest = MigrateRequest.newFromJBPMDesignerToStunner(jbpmPath, JBPM_NAME, BPMN_EXTENSION, ToStunnerCommitMessage, projectDiagram);

        MigrateResult result = new MigrateResult(null, IntegrationService.ServiceError.JBPM_DESIGNER_PROCESS_ALREADY_EXIST, "messageKey", new ArrayList<>());
        when(integrationService.migrateDiagram(expectedRequest)).thenReturn(result);

        handler.migrateFromJBPMDesignerToStunner(jbpmPath, place, isDirty, saveSuccessfulCommand);

        //ask user for saving and say yes.
        verifySavePopupWasShownAndRespond(isDirty, true);
        //ask user for starting the migration and say yes
        verifyUserWasAskedForStartingToStunnerMigrationAndRespond(true);

        verifyMarshallingResponseWithErrorsWereShown(messages);
    }

    @SuppressWarnings("unchecked")
    private void prepareMigrateFromJBPMDesignerToStunner(MarshallingResponse.State state, List<MarshallingMessage> messages) {
        MarshallingResponse<ProjectDiagram> marshallingResponse = new MarshallingResponse.MarshallingResponseBuilder<ProjectDiagram>()
                .state(state)
                .messages(messages)
                .result(projectDiagram)
                .build();
        when(integrationService.getDiagramByPath(jbpmPath, MarshallingRequest.Mode.AUTO)).thenReturn(marshallingResponse);
    }

    private void verifySavePopupWasShownAndRespond(boolean isDirty, boolean sayYes) {
        if (isDirty) {
            //ask user for saving
            verify(popupUtil).showYesNoCancelPopup(eq(MigrateActionConfirmSaveInformationTitle),
                                                   eq(MigrateActionConfirmSaveMessage),
                                                   saveYesCommandCaptor.capture(),
                                                   saveNoCommandCaptor.capture());
            if (sayYes) {
                saveYesCommandCaptor.getValue().execute();
            } else {
                saveNoCommandCaptor.getValue().execute();
            }
        } else {
            verify(popupUtil, never()).showYesNoCancelPopup(anyString(),
                                                            anyString(),
                                                            any(Command.class),
                                                            any(Command.class));
        }
    }

    private void verifyUserWasAskedForStartingToStunnerMigrationAndRespond(boolean sayYes) {
        //ask user for starting with the migration
        verify(popupUtil).showConfirmPopup(eq(MigrateActionTitle),
                                           eq(null),
                                           eq(null),
                                           eq(MigrateAction),
                                           eq(org.uberfire.client.views.pfly.widgets.Button.ButtonStyleType.PRIMARY),
                                           eq(MigrateToStunnerConfirmAction), startMigrationCommandCaptor.capture());
        if (sayYes) {
            startMigrationCommandCaptor.getValue().execute();
        }
    }

    private void verifyUserWasAskedForStartingToJBPMMigrationAndRespond(boolean sayYes) {
        //ask user for starting with the migration
        verify(popupUtil).showConfirmPopup(eq(MigrateActionTitle),
                                           eq(MigrateToJBPMDesignerActionWarning),
                                           eq(InlineNotification.InlineNotificationType.WARNING),
                                           eq(MigrateAction),
                                           eq(org.uberfire.client.views.pfly.widgets.Button.ButtonStyleType.PRIMARY),
                                           eq(MigrateToJBPMDesignerConfirmAction), startMigrationCommandCaptor.capture());
        if (sayYes) {
            startMigrationCommandCaptor.getValue().execute();
        }
    }

    private void verifyMarshallingResponseWasShownAndRespond(List<MarshallingMessage> messages, boolean sayYes) {
        //show the marshaling messages and ask user for proceeding with the migration
        verify(responsePopup).show(eq(MigrateActionTitle),
                                   eq(MigrateToStunnerInfoWarningsProducedMessage),
                                   eq(InlineNotification.InlineNotificationType.INFO),
                                   eq(messages),
                                   eq(MigrateAction),
                                   marshalingResponseCaptor.capture());
        if (sayYes) {
            marshalingResponseCaptor.getValue().execute();
        }
    }

    private void verifyMarshallingResponseWithErrorsWereShown(List<MarshallingMessage> messages) {
        verify(responsePopup).show(eq(MigrateActionTitle),
                                   eq(MigrateToStunnerErrorsProducedMessage),
                                   eq(InlineNotification.InlineNotificationType.DANGER),
                                   eq(messages),
                                   eq(MigrateAction));
    }

    private void verifyMarshallingResponseWasNeverShown() {
        verify(responsePopup, never()).show(anyString(), anyString(), any(InlineNotification.InlineNotificationType.class), anyList(), anyString(), any(Command.class));
    }

    private void verifyMigrationFinished() {
        verify(placeManger).forceClosePlace(place);
        verify(placeManger).goTo(newPlace);
        verify(notification).fire(new NotificationEvent(MigrateDiagramSuccessfullyMigratedMessage, NotificationEvent.NotificationType.SUCCESS));
    }
}
