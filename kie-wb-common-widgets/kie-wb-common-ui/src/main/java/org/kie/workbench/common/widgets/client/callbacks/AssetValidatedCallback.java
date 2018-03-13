/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.callbacks;

import java.util.List;

import javax.enterprise.event.Event;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public class AssetValidatedCallback implements RemoteCallback<List<ValidationMessage>> {

    private final Command validationFinishedCommand;

    private final Event<NotificationEvent> notificationEvent;
    private ValidationPopup validationPopup;

    public AssetValidatedCallback(final Command validationFinishedCommand,
                                  final Event<NotificationEvent> notificationEvent,
                                  final ValidationPopup validationPopup) {
        this.validationFinishedCommand = validationFinishedCommand;
        this.notificationEvent = notificationEvent;
        this.validationPopup = validationPopup;
    }

    @Override
    public void callback(final List<ValidationMessage> validationMessages) {
        if (validationMessages == null || validationMessages.isEmpty()) {
            notifyValidationSuccess();
        } else {
            validationPopup.showMessages(validationMessages);
        }
        if (validationFinishedCommand != null) {
            validationFinishedCommand.execute();
        }
    }

    private void notifyValidationSuccess() {
        // the null check is due to tests that are not able to inject Event instance
        if (notificationEvent != null) {
            notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                                                         NotificationEvent.NotificationType.SUCCESS));
        }
    }
}
