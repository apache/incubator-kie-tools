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


package org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.forms.model.CorrelationsFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "correlations")
public class CollaborationSet implements BaseCollaborationSet {

    @Property
    @FormField(
            type = CorrelationsFieldType.class
    )
    @Valid
    private Correlations correlations;

    public CollaborationSet() {
        this(new Correlations());
    }

    public CollaborationSet(final @MapsTo("correlations") Correlations correlations) {
        this.correlations = correlations;
    }

    @Override
    public Correlations getCorrelations() {
        return correlations;
    }

    public void setCorrelations(final Correlations correlations) {
        this.correlations = correlations;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(correlations));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CollaborationSet) {
            CollaborationSet other = (CollaborationSet) o;
            return Objects.equals(correlations, other.correlations);
        }
        return false;
    }
}
