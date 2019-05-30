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

package org.kie.workbench.common.stunner.client.widgets.marshaller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.view.client.ListDataProvider;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.mvp.Command;

public class MarshallingResponsePopup {

    public interface View extends UberElemental<MarshallingResponsePopup> {

        void setTitle(String title);

        void setInlineNotification(String notificationMessage, InlineNotification.InlineNotificationType notificationType);

        void setOkActionLabel(String okActionLabel);

        void setOkActionEnabled(boolean enabled);

        void show(Command command);

        void copyToClipboard(String text);

        ListDataProvider<Row> getMessagesTableProvider();
    }

    public static class Row {

        private String level;
        private String message;

        public Row(String level, String message) {
            this.level = level;
            this.message = message;
        }

        public String getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }
    }

    private View view;

    private ClientTranslationService translationService;

    @Inject
    public MarshallingResponsePopup(View view, ClientTranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
    }

    @PostConstruct
    void init() {
        view.init(this);
    }

    public void show(String title,
                     String notificationMessage, InlineNotification.InlineNotificationType notificationType,
                     List<MarshallingMessage> messages,
                     String okCommandLabel, Command okCommand) {
        view.setTitle(title);
        view.setInlineNotification(notificationMessage, notificationType);
        final List<Row> rows = Optional.ofNullable(messages).orElse(Collections.emptyList()).stream().map(this::buildRow).collect(Collectors.toList());
        view.getMessagesTableProvider().getList().clear();
        view.getMessagesTableProvider().getList().addAll(rows);
        view.getMessagesTableProvider().flush();
        view.setOkActionLabel(okCommandLabel);
        if (okCommand == null) {
            view.setOkActionEnabled(false);
            view.show(() -> {
            });
        } else {
            view.setOkActionEnabled(true);
            view.show(okCommand);
        }
    }

    public void show(String title,
                     String notificationMessage, InlineNotification.InlineNotificationType notificationType,
                     List<MarshallingMessage> messages,
                     String okCommandLabel) {
        show(title, notificationMessage, notificationType, messages, okCommandLabel, null);
    }

    void onCopyToClipboard() {
        String clipboardMessage = view.getMessagesTableProvider().getList().stream()
                .map(this::buildClipboardMessage)
                .collect(Collectors.joining("\n"));
        view.copyToClipboard(clipboardMessage);
    }

    private Row buildRow(MarshallingMessage message) {
        return new Row(displayableValue(message.getViolationType()), displayableValue(message));
    }

    private String buildClipboardMessage(Row row) {
        return row.getLevel() + ", " + row.getMessage();
    }

    String displayableValue(MarshallingMessage message) {
        if (StringUtils.nonEmpty(message.getMessageKey())) {
            final List<?> arguments = Optional.ofNullable(message.getMessageArguments()).orElse(Collections.emptyList());
            String translation = translationService.getValue(message.getMessageKey(), arguments);
            if (!erraiDefaultValue(message.getMessageKey()).equals(translation)) {
                return translation;
            }
        }
        return message.getMessage();
    }

    String displayableValue(Violation.Type type) {
        if (type != null) {
            switch (type) {
                case ERROR:
                    return translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_ErrorMessageLabel);
                case WARNING:
                    return translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_WarningMessageLabel);
                case INFO:
                    return translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_InfoMessageLabel);
            }
        }
        return translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_UnknownMessageLabel);
    }

    private static String erraiDefaultValue(String key) {
        return "!!!" + key + "!!!";
    }
}
