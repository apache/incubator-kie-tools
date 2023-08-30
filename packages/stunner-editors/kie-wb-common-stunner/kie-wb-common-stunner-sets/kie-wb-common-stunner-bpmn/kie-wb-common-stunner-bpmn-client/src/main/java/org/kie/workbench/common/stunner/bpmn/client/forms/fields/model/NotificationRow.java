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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ValidNotificationValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.kie.workbench.common.stunner.core.util.HashUtil;

/**
 * Class which is bound to rows in the NotificationEditor
 */
@Bindable
@ValidNotificationValue
public class NotificationRow {

    // Field which is incremented for each row.
    // Required to implement equals function which needs a unique field
    private static long lastId = 0;
    private long id;
    private String body = "";

    private String expiresAt = "";

    private String from = "";

    private List<String> groups = new ArrayList<>();

    private String replyTo = "";

    private String subject = "";

    private List<String> users = new ArrayList<>();

    private String emails = "";

    private NotificationType type = NotificationType.NOT_COMPLETED_NOTIFY;

    private Expiration expiration;

    public NotificationRow() {
        this.id = lastId++;
    }

    public NotificationRow(NotificationValue notification) {
        this.id = lastId++;
        this.setType(NotificationType.get(notification.getType()));
        this.setExpiresAt(notification.getExpiresAt());
        this.setGroups(new ArrayList<>(notification.getGroups()));
        this.setUsers(new ArrayList<>(notification.getUsers()));
        this.setEmails(notification.getEmails());
        this.setBody(notification.getBody());
        this.setSubject(notification.getSubject());
        this.setFrom(notification.getFrom());
        this.setReplyTo(notification.getReplyTo());
    }

    public NotificationValue toNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setType(getType().getAlias());
        value.setExpiresAt(getExpiresAt());
        value.setGroups(new ArrayList<>(getGroups()));
        value.setUsers(new ArrayList<>(getUsers()));
        value.setEmails(getEmails());
        value.setBody(getBody());
        value.setSubject(getSubject());
        value.setFrom(getFrom());
        value.setReplyTo(getReplyTo());
        return value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Expiration getExpiration() {
        return expiration;
    }

    public void setExpiration(Expiration expiration) {
        this.expiration = expiration;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(id));
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NotificationRow other = (NotificationRow) obj;
        return (id == other.getId());
    }

    public NotificationRow clone() {
        NotificationRow clone = new NotificationRow();
        clone.setId(getId());
        clone.setExpiresAt(getExpiresAt());
        clone.setType(getType());
        clone.setGroups(getGroups());
        clone.setUsers(getUsers());
        clone.setEmails(getEmails());
        clone.setFrom(getFrom());
        clone.setReplyTo(getReplyTo());
        clone.setSubject(getSubject());
        clone.setBody(getBody());
        clone.setExpiration(getExpiration());
        return clone;
    }

    @Override
    public String toString() {
        return "NotificationRow{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", expiresAt='" + expiresAt + '\'' +
                ", Expiration='" + getExpiration() + '\'' +
                ", from='" + from + '\'' +
                ", groups=" + String.join(",", groups) +
                ", replyTo='" + replyTo + '\'' +
                ", subject='" + subject + '\'' +
                ", users=" + String.join(",", users) +
                ", emails=" + String.join(",", emails) +
                ", type=" + type +
                '}';
    }
}
