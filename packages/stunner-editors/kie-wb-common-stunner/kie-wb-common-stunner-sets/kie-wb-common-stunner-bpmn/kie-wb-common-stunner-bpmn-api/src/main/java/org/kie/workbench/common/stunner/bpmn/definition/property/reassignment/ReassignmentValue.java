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


package org.kie.workbench.common.stunner.bpmn.definition.property.reassignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.forms.validation.reassignment.ValidReassignmentValue;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@ValidReassignmentValue
public class ReassignmentValue {

    private String type;

    private String duration;

    private List<String> users, groups;

    public ReassignmentValue() {
        this.groups = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    public ReassignmentValue(@MapsTo("type") String type,
                             @MapsTo("duration") String duration,
                             @MapsTo("users") List<String> users,
                             @MapsTo("groups") List<String> groups) {
        this.type = type;
        this.duration = duration;
        this.groups = groups;
        this.users = users;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String toCDATAFormat() {
        return "[users:" + users.stream().collect(Collectors.joining(",")) +
                "|groups:" + groups.stream().collect(Collectors.joining(",")) + "]" +
                "@[" + duration + "]";
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(duration),
                                         Objects.hashCode(groups),
                                         Objects.hashCode(users),
                                         Objects.hashCode(type));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ReassignmentValue) {
            ReassignmentValue other = (ReassignmentValue) o;
            return Objects.equals(duration, other.duration) &&
                    Objects.equals(type, other.type) &&
                    Objects.equals(groups, other.groups) &&
                    Objects.equals(users, other.users);
        }
        return false;
    }

    public String toString() {
        return "{\"type\":\"" + getType() + "\"," +
                "\"period\":\"" + getDuration() + "\"," +
                "\"users\":[" + getUsers().stream().map(u -> "\"" + u + "\"").collect(Collectors.joining(",")) + "]," +
                "\"groups\":[" + getGroups().stream().map(u -> "\"" + u + "\"").collect(Collectors.joining(",")) + "]}";
    }
}
