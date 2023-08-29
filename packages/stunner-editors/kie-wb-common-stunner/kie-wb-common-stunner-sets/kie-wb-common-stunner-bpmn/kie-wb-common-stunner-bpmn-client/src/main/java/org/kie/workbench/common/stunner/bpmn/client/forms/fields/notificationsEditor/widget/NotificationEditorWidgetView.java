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

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationType;

public interface NotificationEditorWidgetView extends IsWidget {

    interface Presenter {

        String getNameHeader();

        String getFromLabel();

        String getExpirationLabel(Expiration type);

        void createOrEdit(NotificationWidgetView parent, NotificationRow row);

        void ok(String emails);

        String clearEmails(String emailsString);

        void setRepeatNotificationVisibility(boolean invisible);

        void setRepeatNotificationInvisibility(boolean invisible);

        void setNotificationPanelDivVisibility(boolean isVisible);

        void addUsers(List<String> users);

        void addGroups(List<String> groups);

        void addFrom(String string);

        void addReplyTo(String replyTo);

        Expiration parseExpiration(String expirationAt, Expiration expiration);

        void setExpiration(Expiration expiration, NotificationRow row);

        String getRepeatCount(String repeatable);

        void setExpirationDateTime(String expiresAt);

        Date parseDate(String date);

        String clearTimeZone(String value);

        NotificationType getNotificationType(boolean isNotStarted);

        boolean isRepeatable(String repeatable);

        String minuteOrMonth(MatchResult match);
    }

    void hideRepeatNotificationDiv();

    void showRepeatNotificationDiv();

    void showRepeatNotificationPanel();

    void hideRepeatNotificationPanel();

    void init(final NotificationEditorWidgetView.Presenter presenter);

    void createOrEdit(NotificationWidgetView parent, NotificationRow row);

    void addFrom(String from);

    void addReplyTo(String replyTo);

    void addUserToLiveSearch(String user);

    void addUsersToSelect(List<String> user);

    void addGroupToLiveSearch(String group);

    void addGroupsToSelect(List<String> groups);

    void setExpressionTextValue(String value);

    void setExpirationDateTime(NotificationRow row);

    void enableRepeatNotification(Date dateTime, String timeZone, String period, String repeatCount);

    void disableRepeatNotification(Date dateTime, String timeZone);

    void setReadOnly(boolean readOnly);

    void setExpirationTimePeriod(String iso);

    void ok();

    void setValidationFailed(String incorrectValue);
}
