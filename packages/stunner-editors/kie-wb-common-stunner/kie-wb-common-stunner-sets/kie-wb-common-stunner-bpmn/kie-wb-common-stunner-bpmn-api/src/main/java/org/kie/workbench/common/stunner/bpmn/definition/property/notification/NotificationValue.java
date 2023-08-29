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


package org.kie.workbench.common.stunner.bpmn.definition.property.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static java.lang.String.join;

@Portable
@Bindable
public class NotificationValue {

    private String body = "";

    private String expiresAt = "";

    private String from = "";

    private List<String> groups;

    private String replyTo = "";

    private String subject = "";

    private List<String> users;

    private String emails = "";

    private String type = "";

    public NotificationValue() {
        this.groups = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    public NotificationValue(@MapsTo("body") String body,
                             @MapsTo("expiresAt") String expiresAt,
                             @MapsTo("subject") String subject,
                             @MapsTo("replyTo") String replyTo,
                             @MapsTo("type") String type,
                             @MapsTo("from") String from,
                             @MapsTo("togroups") List<String> groups,
                             @MapsTo("tousers") List<String> users,
                             @MapsTo("emails") String emails) {
        this.body = body;
        this.expiresAt = expiresAt;
        this.subject = subject;
        this.replyTo = replyTo;
        this.type = type;
        this.from = from;
        this.groups = groups;
        this.users = users;
        this.emails = emails;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String toCDATAFormat() {
        return "[from:" + from +
                "|tousers:" + join(",", users) +
                "|togroups:" + join(",", groups) +
                "|toemails:" + emails +
                "|replyTo:" + replyTo +
                "|subject:" + replaceAsciiSymbols(subject) +
                "|body:" + replaceAsciiSymbols(body) +
                "]@[" + expiresAt + "]";
    }

    private static String replaceAsciiSymbols(String str) {
        if (str == null) {
            return "";
        }

        return str
                .replaceAll("\\|", "&#124;")
                .replaceAll("\\^", "&#94;");
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(body),
                                         Objects.hashCode(expiresAt),
                                         Objects.hashCode(from),
                                         Objects.hashCode(type),
                                         Objects.hashCode(users),
                                         Objects.hashCode(groups),
                                         Objects.hashCode(emails),
                                         Objects.hashCode(replyTo),
                                         Objects.hashCode(subject));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NotificationValue) {
            NotificationValue other = (NotificationValue) o;
            return Objects.equals(body, other.body) &&
                    Objects.equals(expiresAt, other.expiresAt) &&
                    Objects.equals(from, other.from) &&
                    Objects.equals(subject, other.subject) &&
                    Objects.equals(type, other.type) &&
                    Objects.equals(groups, other.groups) &&
                    Objects.equals(emails, other.emails) &&
                    Objects.equals(users, other.users) &&
                    Objects.equals(replyTo, other.replyTo);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Notification{" +
                " type='" + type + '\'' +
                ", from='" + from + '\'' +
                ", replyTo='" + replyTo + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", expiresAt='" + expiresAt + '\'' +
                ", users='" + String.join(", ", users) + '\'' +
                ", groups='" + String.join(", ", groups) + '\'' +
                ", emails='" + emails + '\'' +
                '}';
    }
}
