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


package org.kie.workbench.common.stunner.bpmn.definition.property.dataio;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssignmentsEditorFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;

@Portable
@Bindable
@FormDefinition(
        startElement = "assignmentsinfo"
)
public class DataIOSet implements BPMNPropertySet {

    @Property
    @FormField(
            type = AssignmentsEditorFieldType.class
    )
    @Valid
    private AssignmentsInfo assignmentsinfo;

    public DataIOSet() {
        this(new AssignmentsInfo());
    }

    public DataIOSet(final @MapsTo("assignmentsinfo") AssignmentsInfo assignmentsinfo) {
        this.assignmentsinfo = assignmentsinfo;
    }

    public DataIOSet(final String assignmentsinfo) {
        this.assignmentsinfo = new AssignmentsInfo(assignmentsinfo);
    }

    public AssignmentsInfo getAssignmentsinfo() {
        return assignmentsinfo;
    }

    public void setAssignmentsinfo(final AssignmentsInfo assignmentsinfo) {
        this.assignmentsinfo = assignmentsinfo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssignmentsinfo());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataIOSet) {
            DataIOSet other = (DataIOSet) o;
            return assignmentsinfo.equals(other.assignmentsinfo);
        }
        return false;
    }
}
