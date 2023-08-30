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

import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
public class AssigneeRow {

    private long id;

    private String name;

    private String customName;

    // Field which is incremented for each row.
    // Required to implement equals function which needs a unique field
    private static long lastId = 0;

    public AssigneeRow() {
        this(null, null);
    }

    public AssigneeRow(final String name,
                       final String customName) {
        this.id = lastId++;
        this.name = name;
        this.customName = customName;
    }

    public AssigneeRow(final Assignee assignee) {
        this(assignee.getName(), assignee.getCustomName());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(final String customName) {
        this.customName = customName;
    }

    public boolean isEmpty() {
        if (name != null && name.length() > 0) {
            return false;
        } else if (customName != null && customName.length() > 0) {
            return false;
        } else {
            return true;
        }
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
        AssigneeRow other = (AssigneeRow) obj;
        return (id == other.id);
    }

    @Override
    public int hashCode() {
        return ~~(int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "AssigneeRow [name=" + name + ",customName=" + customName + "]";
    }
}
