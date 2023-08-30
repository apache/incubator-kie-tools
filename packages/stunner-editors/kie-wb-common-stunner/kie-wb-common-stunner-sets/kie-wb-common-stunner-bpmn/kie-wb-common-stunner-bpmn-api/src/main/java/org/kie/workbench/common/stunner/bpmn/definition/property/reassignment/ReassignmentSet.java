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

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.ReassignmentsType;
import org.kie.workbench.common.stunner.bpmn.forms.model.ReassignmentsEditorFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Type;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(
        startElement = "reassignmentsInfo"
)
public class ReassignmentSet implements BPMNPropertySet {

    @Type
    public static final PropertyType type = new ReassignmentsType();
    @Property
    @FormField(
            type = ReassignmentsEditorFieldType.class
    )
    @Valid
    private ReassignmentsInfo reassignmentsInfo;

    public ReassignmentSet() {
        this(new ReassignmentsInfo());
    }

    public ReassignmentSet(final @MapsTo("reassignmentsInfo") ReassignmentsInfo reassignmentsInfo) {
        this.reassignmentsInfo = reassignmentsInfo;
    }

    public PropertyType getType() {
        return type;
    }

    public ReassignmentsInfo getReassignmentsInfo() {
        return reassignmentsInfo;
    }

    public void setReassignmentsInfo(final ReassignmentsInfo reassignmentsInfo) {
        this.reassignmentsInfo = reassignmentsInfo;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(reassignmentsInfo));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ReassignmentSet) {
            ReassignmentSet other = (ReassignmentSet) o;
            return Objects.equals(reassignmentsInfo, other.reassignmentsInfo);
        }
        return false;
    }
}