/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.notification;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;

public final class CommandNotification
        extends AbstractNotification<Command<?, CanvasViolation>> {

    private final Command<?, CanvasViolation> source;

    CommandNotification(final Type type,
                        final NotificationContext context,
                        final Command<?, CanvasViolation> source,
                        final String message) {
        super(context,
              type,
              message);
        this.source = source;
    }

    @Override
    public Optional<Command<?, CanvasViolation>> getSource() {
        return Optional.of(source);
    }

    public static class Builder {

        public static CommandNotification build(final ClientTranslationService translationService,
                                                final NotificationContext context,
                                                final Command<?, CanvasViolation> command,
                                                final CommandResult<CanvasViolation> result) {
            final Notification.Type type = getNotificationType(result);
            final String message = Type.ERROR.equals(type) || Type.WARNING.equals(type) ?
                    getErrorMessage(translationService,
                                    result) :
                    getSuccessMessage(translationService,
                                      result);
            return new CommandNotification(type,
                                           context,
                                           command,
                                           message);
        }

        public static CommandNotification build(final NotificationContext context,
                                                final Command<?, CanvasViolation> command,
                                                final Notification.Type type,
                                                final String message) {
            return new CommandNotification(type,
                                           context,
                                           command,
                                           message);
        }

        private static String getSuccessMessage(final ClientTranslationService translationService,
                                                final CommandResult<CanvasViolation> result) {
            return translationService.getValue(CoreTranslationMessages.COMMAND_SUCCESS);
        }

        private static String getErrorMessage(final ClientTranslationService translationService,
                                              final CommandResult<CanvasViolation> result) {
            return NotificationMessageUtils.getCanvasValidationsErrorMessage(translationService,
                                                                             CoreTranslationMessages.COMMAND_FAILED,
                                                                             result.getViolations());
        }

        private static Notification.Type getNotificationType(final CommandResult<CanvasViolation> result) {
            switch (result.getType()) {
                case ERROR:
                    return Type.ERROR;
                case WARNING:
                    return Type.WARNING;
            }
            return Type.INFO;
        }
    }
}
