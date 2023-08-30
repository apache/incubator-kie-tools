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
import java.util.stream.Collectors;

import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.kie.workbench.common.stunner.core.util.HashUtil;

/**
 * Class which is bound to rows in the ReassignmentEditor
 */
@Bindable
public class ReassignmentRow {

    private long id;

    // Field which is incremented for each row.
    // Required to implement equals function which needs a unique field
    private static long lastId = 0;

    private ReassignmentType type = ReassignmentType.NotCompletedReassign;

    private String duration = "0" + Duration.HOUR.getAlias();

    private List<String> users = new ArrayList<>();

    private List<String> groups = new ArrayList<>();

    public ReassignmentRow() {
        this.id = lastId++;
    }

    public ReassignmentRow(ReassignmentValue reassignment) {
        this.id = lastId++;
        this.setType(ReassignmentType.get(reassignment.getType()));
        this.setDuration(reassignment.getDuration());
        this.setGroups(reassignment.getGroups().stream().collect(Collectors.toList()));
        this.setUsers(reassignment.getUsers().stream().collect(Collectors.toList()));
    }

    public ReassignmentType getType() {
        return type;
    }

    public void setType(ReassignmentType type) {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ReassignmentRow clone() {
        ReassignmentRow clone = new ReassignmentRow();
        clone.setId(getId());
        clone.setDuration(getDuration());
        clone.setType(getType());
        clone.setGroups(getGroups());
        clone.setUsers(getUsers());
        return clone;
    }

    public ReassignmentValue toReassignmentValue() {
        ReassignmentValue value = new ReassignmentValue();
        value.setType(getType().getAlias());
        value.setDuration(getDuration());
        value.setGroups(getGroups());
        value.setUsers(getUsers());
        return value;
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
        ReassignmentRow other = (ReassignmentRow) obj;
        return (id == other.id);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(id));
    }
}
