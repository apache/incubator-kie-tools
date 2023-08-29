/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.widget;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationType;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationType.NOT_COMPLETED_NOTIFY;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationType.NOT_STARTED_NOTIFY;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.ISO_DATE_TIME;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.PERIOD;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.REPEATABLE;
import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.isEmpty;
import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.nonEmpty;

@Dependent
public class NotificationEditorWidget implements IsWidget,
                                                 NotificationEditorWidgetView.Presenter {

    private static final String EXPIRATION_PREFIX = "notification.expiration.";

    private static final String EXPIRATION_POSTFIX = ".label";

    private NotificationEditorWidgetView view;

    private ClientTranslationService translationService;

    private DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm");

    @Inject
    public NotificationEditorWidget(NotificationEditorWidgetView view,
                                    ClientTranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getNameHeader() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_LABEL);
    }

    @Override
    public String getFromLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_FROM);
    }

    @Override
    public String getExpirationLabel(Expiration type) {
        switch (type) {
            case EXPRESSION:
                return translationService.getValue(EXPIRATION_PREFIX + "expression" + EXPIRATION_POSTFIX);
            case DATETIME:
                return translationService.getValue(EXPIRATION_PREFIX + "datetime" + EXPIRATION_POSTFIX);
            case TIME_PERIOD:
                return translationService.getValue(EXPIRATION_PREFIX + "time.period" + EXPIRATION_POSTFIX);
            default:
                throw new IllegalArgumentException(type.toString() + " is not supported.");
        }
    }

    @Override
    public void createOrEdit(NotificationWidgetView parent, NotificationRow row) {
        view.createOrEdit(parent, row);
    }

    @Override
    public void ok(String emails) {
        String incorrectValue = getFirstInvalidEmail(emails);
        if (incorrectValue.isEmpty()) {
            view.ok();
        } else {
            view.setValidationFailed(incorrectValue);
        }
    }

    @Override
    public String clearEmails(String emails) {
        String result = emails.replaceAll("\\s", "");
        if (result.startsWith(",")) {
            result = result.substring(1);
        }

        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private String getFirstInvalidEmail(String emailsString) {
        String[] emails = clearEmails(emailsString).split(",");

        for (String email : emails) {
            if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                return email;
            }
        }

        return "";
    }

    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    @Override
    public void setRepeatNotificationVisibility(boolean isVisible) {
        if (isVisible) {
            view.showRepeatNotificationDiv();
        } else {
            view.hideRepeatNotificationDiv();
        }
    }

    @Override
    public void setRepeatNotificationInvisibility(boolean isInvisible) {
        setRepeatNotificationVisibility(!isInvisible);
    }

    @Override
    public void setNotificationPanelDivVisibility(boolean isVisible) {
        if (isVisible) {
            view.showRepeatNotificationPanel();
        } else {
            view.hideRepeatNotificationPanel();
        }
    }

    @Override
    public void addUsers(List<String> users) {
        if (nonEmpty(users)) {
            users.forEach(u -> view.addUserToLiveSearch(u));
            view.addUsersToSelect(users);
        }
    }

    @Override
    public void addGroups(List<String> groups) {
        if (nonEmpty(groups)) {
            groups.forEach(g -> view.addGroupToLiveSearch(g));
            view.addGroupsToSelect(groups);
        }
    }

    @Override
    public void addFrom(String from) {
        if (nonEmpty(from)) {
            view.addFrom(from);
        }
    }

    @Override
    public void addReplyTo(String replyTo) {
        if (nonEmpty(replyTo)) {
            view.addReplyTo(replyTo);
        }
    }

    @Override
    public Expiration parseExpiration(String expirationAt, Expiration expiration) {
        if (isEmpty(expirationAt)) {
            return Expiration.TIME_PERIOD;
        }

        if (expiration != null) {
            return expiration;
        }

        return new ExpirationTypeOracle().guess(expirationAt);
    }

    @Override
    public void setExpiration(Expiration expiration, NotificationRow row) {
        switch (expiration) {
            case EXPRESSION:
                view.setExpressionTextValue(row.getExpiresAt());
                break;
            case DATETIME:
                view.setExpirationDateTime(row);
                break;
            case TIME_PERIOD:
                view.setExpirationTimePeriod(row.getExpiresAt());
                break;
        }
    }

    @Override
    public String getRepeatCount(String repeatable) {
        MatchResult matcher = RegExp.compile(REPEATABLE).exec(repeatable);
        return matcher.getGroup(1);
    }

    @Override
    public void setExpirationDateTime(String expiresAt) {
        MatchResult result = RegExp.compile(REPEATABLE + "/" + ISO_DATE_TIME + "/" + PERIOD).exec(expiresAt);
        if (result != null) {
            String timeZone = result.getGroup(3);
            view.enableRepeatNotification(parseDate(result.getGroup(2)),
                                          clearTimeZone(timeZone),
                                          expiresAt.split("/")[2],
                                          expiresAt.split("/")[0]);
        } else {
            result = RegExp.compile(ISO_DATE_TIME).exec(expiresAt);
            if (result != null) {
                view.disableRepeatNotification(parseDate(result.getGroup(1)),
                                               result.getGroup(2));
            }
        }
    }

    @Override
    public Date parseDate(String date) {
        return dateTimeFormat.parse(date);
    }

    @Override
    public String clearTimeZone(String value) {
        return value.equals("00Z") ? "0" : value;
    }

    @Override
    public NotificationType getNotificationType(boolean isNotStarted) {
        return isNotStarted ? NOT_STARTED_NOTIFY : NOT_COMPLETED_NOTIFY;
    }

    @Override
    public boolean isRepeatable(String repeatable) {
        MatchResult matcher = RegExp.compile(REPEATABLE).exec(repeatable);
        return matcher != null;
    }

    @Override
    public String minuteOrMonth(MatchResult match) {
        String t = match.getGroup(1);
        return ((match.getGroup(3).equals("M") && !t.isEmpty()) ? "m" : match.getGroup(3));
    }
}
