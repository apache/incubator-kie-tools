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


package org.kie.workbench.common.stunner.bpmn.definition.property.variables;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.MetaDataAttributes;
import org.kie.workbench.common.stunner.bpmn.forms.model.MetaDataEditorFieldType;
import org.kie.workbench.common.stunner.bpmn.forms.model.VariablesEditorFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "metaDataAttributes")
public class RootProcessAdvancedData implements BaseRootProcessAdvancedData {

    @Property
    @FormField(
            type = MetaDataEditorFieldType.class
    )
    @Valid
    private MetaDataAttributes metaDataAttributes;

    @Property
    @FormField(
            afterElement = "metaDataAttributes",
            type = VariablesEditorFieldType.class
    )
    @Valid
    private GlobalVariables globalVariables;

    public RootProcessAdvancedData() {
        this(new GlobalVariables(), new MetaDataAttributes());
    }

    public RootProcessAdvancedData(final @MapsTo("globalVariables") GlobalVariables globalVariables, final @MapsTo("metaDataAttributes") MetaDataAttributes metaDataAttributes) {
        this.globalVariables = globalVariables;
        this.metaDataAttributes = metaDataAttributes;
    }

    public RootProcessAdvancedData(final String globalVariables, final String metaDataAttributes) {
        this.globalVariables = new GlobalVariables(globalVariables);
        this.metaDataAttributes = new MetaDataAttributes(metaDataAttributes);
    }

    @Override
    public GlobalVariables getGlobalVariables() {
        return globalVariables;
    }

    public void setGlobalVariables(final GlobalVariables globalVariables) {
        this.globalVariables = globalVariables;
    }

    @Override
    public MetaDataAttributes getMetaDataAttributes() {
        return metaDataAttributes;
    }

    public void setMetaDataAttributes(MetaDataAttributes metadataAttributes) {
        this.metaDataAttributes = metadataAttributes;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(globalVariables),
                                         Objects.hashCode(metaDataAttributes));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RootProcessAdvancedData) {
            RootProcessAdvancedData other = (RootProcessAdvancedData) o;
            return Objects.equals(globalVariables, other.globalVariables) &&
                    Objects.equals(metaDataAttributes, other.metaDataAttributes);
        }
        return false;
    }
}