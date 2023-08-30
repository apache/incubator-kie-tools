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

package org.kie.workbench.common.stunner.bpmn.definition.property.event.signal;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "signalRef")
public class ScopedSignalEventExecutionSet implements BPMNPropertySet {

    @Property
    @FormField(type = ComboBoxFieldType.class)
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ProcessSignalRefProvider"
    )
    @Valid
    private SignalRef signalRef;

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            afterElement = "signalRef"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.SignalScopeProvider")
    private SignalScope signalScope;

    public ScopedSignalEventExecutionSet() {
        this(new SignalRef(),
             new SignalScope());
    }

    public ScopedSignalEventExecutionSet(final @MapsTo("signalRef") SignalRef signalRef,
                                         final @MapsTo("signalScope") SignalScope signalScope) {
        this.signalRef = signalRef;
        this.signalScope = signalScope;
    }

    public SignalRef getSignalRef() {
        return signalRef;
    }

    public void setSignalRef(final SignalRef signalRef) {
        this.signalRef = signalRef;
    }

    public SignalScope getSignalScope() {
        return signalScope;
    }

    public void setSignalScope(SignalScope signalScope) {
        this.signalScope = signalScope;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(signalRef.hashCode(),
                                         signalScope.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ScopedSignalEventExecutionSet) {
            ScopedSignalEventExecutionSet other = (ScopedSignalEventExecutionSet) o;
            return (other.signalRef == null ? signalRef == null : other.signalRef.equals(signalRef)) &&
                    (other.signalScope == null ? signalScope == null : other.signalScope.equals(signalScope));
        }
        return false;
    }
}
