/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.definition.property.service;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.forms.model.GenericServiceTaskEditorFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(startElement = "genericServiceTaskInfo")
public class GenericServiceTaskExecutionSet implements BPMNPropertySet {

    @Property
    @FormField(
            type = GenericServiceTaskEditorFieldType.class
    )
    private GenericServiceTaskInfo genericServiceTaskInfo;

    public GenericServiceTaskExecutionSet() {
        this(new GenericServiceTaskInfo());
    }

    public GenericServiceTaskExecutionSet(final @MapsTo("genericServiceTaskInfo") GenericServiceTaskInfo genericServiceTaskInfo) {
        this.genericServiceTaskInfo = genericServiceTaskInfo;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(genericServiceTaskInfo));
    }

    public GenericServiceTaskInfo getGenericServiceTaskInfo() {
        return genericServiceTaskInfo;
    }

    public void setGenericServiceTaskInfo(GenericServiceTaskInfo genericServiceTaskInfo) {
        this.genericServiceTaskInfo = genericServiceTaskInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GenericServiceTaskExecutionSet) {
            GenericServiceTaskExecutionSet other = (GenericServiceTaskExecutionSet) o;
            return Objects.equals(genericServiceTaskInfo, other.genericServiceTaskInfo);
        }
        return false;
    }
}
