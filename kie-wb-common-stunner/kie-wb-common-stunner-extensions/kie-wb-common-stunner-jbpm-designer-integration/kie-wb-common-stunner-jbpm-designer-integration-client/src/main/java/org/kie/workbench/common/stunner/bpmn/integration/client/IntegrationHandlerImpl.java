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

import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.stunner.bpmn.integration.client.resources.IntegrationClientConstants;
import org.kie.workbench.common.stunner.bpmn.integration.service.IntegrationService;
import org.kie.workbench.common.stunner.bpmn.integration.service.MigrateRequest;
import org.kie.workbench.common.stunner.bpmn.integration.service.MigrateResult;
import org.kie.workbench.common.stunner.bpmn.resource.BPMNDefinitionSetResourceType;
import org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopup;
import org.kie.workbench.common.stunner.client.widgets.popups.PopupUtil;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingResponse;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class IntegrationHandlerImpl implements IntegrationHandler {

    static final String BPMN_EXTENSION = "." + BPMNDefinitionSetResourceType.BPMN_EXTENSION;

    /**
     * Note: Define this bpmp2 file extension constant here to avoid dependencies with jBPM designer.
     * The migrate to jBPM designer feature will also be removed when Stunner is fully featured.
     */
    static final String BPMN2_EXTENSION = ".bpmn2";

    private final Caller<IntegrationService> integrationService;
    private final PlaceManager placeManager;
    private final PopupUtil popupUtil;
    private final ErrorPopupPresenter errorPopup;
    private final MarshallingResponsePopup responsePopup;
    private final ClientTranslationService translationService;
    private final Event<NotificationEvent> notification;

    public IntegrationHandlerImpl() {
        //proxying constructor
        this(null, null, null, null, null, null, null);
    }

    @Inject
    public IntegrationHandlerImpl(final Caller<IntegrationService> integrationService,
                                  final PlaceManager placeManger,
                                  final PopupUtil popupUtil,
                                  final ErrorPopupPresenter errorPopup,
                                  final MarshallingResponsePopup responsePopup,
                                  final ClientTranslationService translationService,
                                  final Event<NotificationEvent> notification) {
        this.integrationService = integrationService;
        this.placeManager = placeManger;
        this.popupUtil = popupUtil;
        this.errorPopup = errorPopup;
        this.responsePopup = responsePopup;
        this.translationService = translationService;
        this.notification = notification;
    }

    @Override
    public void migrateFromJBPMDesignerToStunner(Path path, PlaceRequest place, boolean isDirty, ParameterizedCommand<Consumer<Boolean>> saveCommand) {
        checkIfDirtyAndMigrate(isDirty, saveCommand, () -> fromJBPMDesignerToStunner(path, place));
    }

    @Override
    public void migrateFromStunnerToJBPMDesigner(Path path, PlaceRequest place, boolean isDirty, ParameterizedCommand<Consumer<Boolean>> saveCommand) {
        checkIfDirtyAndMigrate(isDirty, saveCommand, () -> fromStunnerToJBPMDesigner(path, place));
    }

    private void checkIfDirtyAndMigrate(boolean isDirty, ParameterizedCommand<Consumer<Boolean>> saveCommand, Command migrate) {
        if (isDirty) {
            popupUtil.showYesNoCancelPopup(translationService.getValue(IntegrationClientConstants.MigrateActionConfirmSaveInformationTitle),
                                           translationService.getValue(IntegrationClientConstants.MigrateActionConfirmSaveMessage),
                                           () -> saveCommand.execute(saveSuccess -> {
                                               if (saveSuccess) {
                                                   migrate.execute();
                                               }
                                           }),
                                           migrate);
        } else {
            migrate.execute();
        }
    }

    private void confirmAndMigrate(String inlineNotificationMessage, InlineNotification.InlineNotificationType inlineNotificationType, String confirmMessage, Command okCommand) {
        popupUtil.showConfirmPopup(translationService.getValue(IntegrationClientConstants.MigrateActionTitle),
                                   inlineNotificationMessage,
                                   inlineNotificationType,
                                   translationService.getValue(IntegrationClientConstants.MigrateAction),
                                   org.uberfire.client.views.pfly.widgets.Button.ButtonStyleType.PRIMARY,
                                   confirmMessage,
                                   okCommand);
    }

    private void fromJBPMDesignerToStunner(Path path, PlaceRequest place) {
        final RemoteCallback<MarshallingResponse<ProjectDiagram>> successCallback = (result) -> onGetDiagramByPathSuccess(result, path, place);
        final ErrorCallback<Message> errorCallback = (message, throwable) -> onUnexpectedError(throwable);
        final Command okCommand = () -> integrationService.call(successCallback, errorCallback).getDiagramByPath(path, MarshallingRequest.Mode.AUTO);
        confirmAndMigrate(null, null, translationService.getValue(IntegrationClientConstants.MigrateToStunnerConfirmAction), okCommand);
    }

    private void onGetDiagramByPathSuccess(MarshallingResponse<ProjectDiagram> response, Path path, PlaceRequest place) {
        if (response.isSuccess()) {
            final ProjectDiagram diagram = response.getResult().orElse(null);
            if (diagram == null) {
                errorPopup.showMessage(translationService.getValue(IntegrationClientConstants.MigrateToStunnerNoDiagramHasBeenReturned));
            } else {
                final Command doMigrate = () -> fromJBPMDesignerToStunner(diagram, path, place);
                if (!response.getMessages().isEmpty()) {
                    showResultSuccessful(response.getMessages(), doMigrate);
                } else {
                    doMigrate.execute();
                }
            }
        } else {
            showResultWithErrors(response.getMessages());
        }
    }

    private void fromJBPMDesignerToStunner(ProjectDiagram projectDiagram, Path path, PlaceRequest place) {
        Pair<String, String> targetNameAndExtension = calculateTargetNameAndExtension(path);
        String commitMessage = translationService.getValue(IntegrationClientConstants.MigrateToStunnerCommitMessage, path.getFileName());
        migrate(MigrateRequest.newFromJBPMDesignerToStunner(path, targetNameAndExtension.getK1(), targetNameAndExtension.getK2(), commitMessage, projectDiagram),
                place);
    }

    private void migrate(MigrateRequest request, PlaceRequest place) {
        final RemoteCallback<MigrateResult> successCallback = (result) -> migrateFinished(result, place);
        final ErrorCallback<Message> errorCallback = (message, throwable) -> onUnexpectedError(throwable);
        integrationService.call(successCallback, errorCallback).migrateDiagram(request);
    }

    private void migrateFinished(MigrateResult result, PlaceRequest place) {
        if (result.hasError()) {
            errorPopup.showMessage(getErrorMessage(result));
        } else {
            notification.fire(new NotificationEvent(translationService.getValue(IntegrationClientConstants.MigrateDiagramSuccessfullyMigratedMessage), NotificationEvent.NotificationType.SUCCESS));
            placeManager.forceClosePlace(place);
            placeManager.goTo(createTargetPlace(result.getPath()));
        }
    }

    private void fromStunnerToJBPMDesigner(Path path, PlaceRequest place) {
        confirmAndMigrate(translationService.getValue(IntegrationClientConstants.MigrateToJBPMDesignerActionWarning),
                          InlineNotification.InlineNotificationType.WARNING,
                          translationService.getValue(IntegrationClientConstants.MigrateToJBPMDesignerConfirmAction),
                          () -> doFromStunnerToJBPMDesigner(path, place));
    }

    private void doFromStunnerToJBPMDesigner(Path path, PlaceRequest place) {
        Pair<String, String> targetNameAndExtension = calculateTargetNameAndExtension(path);
        String commitMessage = translationService.getValue(IntegrationClientConstants.MigrateToJBPMDesignerCommitMessage, path.getFileName());
        migrate(MigrateRequest.newFromStunnerToJBPMDesigner(path, targetNameAndExtension.getK1(), targetNameAndExtension.getK2(), commitMessage),
                place);
    }

    private void showResultSuccessful(List<MarshallingMessage> messages, Command okCommand) {
        responsePopup.show(translationService.getValue(IntegrationClientConstants.MigrateActionTitle),
                           translationService.getValue(IntegrationClientConstants.MigrateToStunnerInfoWarningsProducedMessage),
                           InlineNotification.InlineNotificationType.INFO,
                           messages,
                           translationService.getValue(IntegrationClientConstants.MigrateAction),
                           okCommand);
    }

    private void showResultWithErrors(List<MarshallingMessage> messages) {
        responsePopup.show(translationService.getValue(IntegrationClientConstants.MigrateActionTitle),
                           translationService.getValue(IntegrationClientConstants.MigrateToStunnerErrorsProducedMessage),
                           InlineNotification.InlineNotificationType.DANGER,
                           messages,
                           translationService.getValue(IntegrationClientConstants.MigrateAction));
    }

    private boolean onUnexpectedError(Throwable throwable) {
        String message = translationService.getValue(IntegrationClientConstants.MigrateActionUnexpectedErrorMessage);
        message += "\n" + throwable.getMessage();
        errorPopup.showMessage(message);
        return false;
    }

    private String getErrorMessage(MigrateResult result) {
        if (result.getError() != null) {
            return translationService.getValue(result.getMessageKey(), result.getMessageArguments().toArray());
        }
        return translationService.getValue(IntegrationClientConstants.MigrateErrorGeneric);
    }

    private static Pair<String, String> calculateTargetNameAndExtension(Path sourcePath) {
        String sourceExtension;
        String targetExtension;
        String sourceName = sourcePath.getFileName();

        if (sourceName.endsWith(BPMN2_EXTENSION)) {
            sourceExtension = BPMN2_EXTENSION;
            targetExtension = BPMN_EXTENSION;
        } else {
            sourceExtension = BPMN_EXTENSION;
            targetExtension = BPMN2_EXTENSION;
        }

        String targetName = sourceName.substring(0, sourceName.length() - sourceExtension.length());
        return new Pair<>(targetName, targetExtension);
    }

    /**
     * for testing purposes.
     */
    PlaceRequest createTargetPlace(Path path) {
        return new PathPlaceRequest(path);
    }
}
