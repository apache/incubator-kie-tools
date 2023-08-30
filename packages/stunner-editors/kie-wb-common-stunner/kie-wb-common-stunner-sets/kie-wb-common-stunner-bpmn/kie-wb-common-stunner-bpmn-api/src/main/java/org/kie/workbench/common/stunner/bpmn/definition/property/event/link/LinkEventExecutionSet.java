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

package org.kie.workbench.common.stunner.bpmn.definition.property.event.link;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "linkRef")
public class LinkEventExecutionSet implements BPMNPropertySet {

    @Property
    @FormField(type = ComboBoxFieldType.class)
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ProcessLinkRefProvider"
    )
    @Valid
    private LinkRef linkRef;

    public LinkEventExecutionSet() {
        this(new LinkRef());
    }

    public LinkEventExecutionSet(final @MapsTo("linkRef") LinkRef linkRef) {
        this.linkRef = linkRef;
    }

    public LinkRef getLinkRef() {
        return linkRef;
    }

    public void setLinkRef(final LinkRef linkRef) {
        this.linkRef = linkRef;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(linkRef.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof LinkEventExecutionSet) {
            LinkEventExecutionSet other = (LinkEventExecutionSet) o;
            return Objects.equals(other.linkRef, linkRef);
        }
        return false;
    }
}
