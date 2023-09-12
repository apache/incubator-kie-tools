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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface StunnerBPMNConstants {

    @TranslationKey(defaultValue = "")
    String ASSIGNEE_NEW = "assignee.newLabel";

    @TranslationKey(defaultValue = "")
    String ASSIGNEE_LABEL = "assignee.label";

    @TranslationKey(defaultValue = "")
    String ASSIGNEE_WITH_DUPLICATES = "assignee.duplicates";

    @TranslationKey(defaultValue = "")
    String ASSIGNEE_CANNOT_BE_EMPTY = "assignee.cannotBeEmpty";

    @TranslationKey(defaultValue = "")
    String ASSIGNEE_SEARCH_ERROR = "assignee.searchError";

    @TranslationKey(defaultValue = "Reassignment")
    String REASSIGNMENT_LABEL = "reassignment.label";

    @TranslationKey(defaultValue = "Reassignments")
    String REASSIGNMENTS_LABEL = "reassignments.label";

    @TranslationKey(defaultValue = "To users")
    String REASSIGNMENT_TO_USERS = "reassignment.toUsers";

    @TranslationKey(defaultValue = "To groups")
    String REASSIGNMENT_TO_GROUPS = "reassignment.toGroups";

    @TranslationKey(defaultValue = "Type")
    String REASSIGNMENT_TYPE = "reassignment.type";

    @TranslationKey(defaultValue = "Expires at")
    String REASSIGNMENT_EXPIRESAT = "reassignment.expiresAt";

    @TranslationKey(defaultValue = "Delete")
    String REASSIGNMENT_DELETE = "reassignment.delete";

    @TranslationKey(defaultValue = "Notification")
    String NOTIFICATION_LABEL = "notification.label";

    @TranslationKey(defaultValue = "Notifications")
    String NOTIFICATIONS_LABEL = "notifications.label";

    @TranslationKey(defaultValue = "To: user(s)")
    String NOTIFICATION_TO_USERS = "notification.toUsers";

    @TranslationKey(defaultValue = "To: email(s)")
    String NOTIFICATION_TO_EMAILS = "notification.toEmails";

    @TranslationKey(defaultValue = "To: group(s)")
    String NOTIFICATION_TO_GROUPS = "notification.toGroups";

    @TranslationKey(defaultValue = "From")
    String NOTIFICATION_FROM = "notification.from";

    @TranslationKey(defaultValue = "Body")
    String NOTIFICATION_BODY = "notification.body";

    @TranslationKey(defaultValue = "Subject")
    String NOTIFICATION_SUBJECT = "notification.subject";

    @TranslationKey(defaultValue = "Reply to")
    String NOTIFICATION_REPLY_TO = "notification.replyTo";

    @TranslationKey(defaultValue = "Type")
    String NOTIFICATION_TYPE = "notification.type";

    @TranslationKey(defaultValue = "Expires at")
    String NOTIFICATION_EXPIRES_AT = "notification.expiresAt";

    @TranslationKey(defaultValue = "Delete")
    String NOTIFICATION_DELETE = "notification.delete";

    @TranslationKey(defaultValue = "Expression")
    String NOTIFICATION_EXPIRATION_EXPRESSION_LABEL = "notification.expiration.expression.label";

    @TranslationKey(defaultValue = "Date/time")
    String NOTIFICATION_EXPIRATION_DATETIME_LABEL = "notification.expiration.datetime.label";

    @TranslationKey(defaultValue = "Time period")
    String NOTIFICATION_EXPIRATION_TIME_PERIOD_LABEL = "notification.expiration.time.period.label";

    @TranslationKey(defaultValue = "Edit")
    String EDIT = "combobox.edit";

    @TranslationKey(defaultValue = "")
    String CORRELATION_HIDE_ERRORS = "correlation.hideErrors";

    @TranslationKey(defaultValue = "")
    String CORRELATION_SHOW_ERRORS = "correlation.showErrors";

    @TranslationKey(defaultValue = "")
    String CORRELATION_ID_EMPTY_ERROR = "correlation.id.emptyErrorMessage";

    @TranslationKey(defaultValue = "")
    String CORRELATION_NAME_EMPTY_ERROR = "correlation.name.emptyErrorMessage";

    @TranslationKey(defaultValue = "")
    String CORRELATION_NAME_DIVERGING_ERROR = "correlation.name.divergingErrorMessage";

    @TranslationKey(defaultValue = "")
    String CORRELATION_PROPERTY_ID_EMPTY_ERROR = "correlation.propertyId.emptyErrorMessage";

    @TranslationKey(defaultValue = "")
    String CORRELATION_PROPERTY_ID_DUPLICATE_ERROR = "correlation.propertyId.duplicateErrorMessage";

    @TranslationKey(defaultValue = "")
    String CORRELATION_PROPERTY_NAME_EMPTY_ERROR = "correlation.propertyName.emptyErrorMessage";

    @TranslationKey(defaultValue = "")
    String CORRELATION_PROPERTY_TYPE_EMPTY_ERROR = "correlation.propertyType.emptyErrorMessage";
}
