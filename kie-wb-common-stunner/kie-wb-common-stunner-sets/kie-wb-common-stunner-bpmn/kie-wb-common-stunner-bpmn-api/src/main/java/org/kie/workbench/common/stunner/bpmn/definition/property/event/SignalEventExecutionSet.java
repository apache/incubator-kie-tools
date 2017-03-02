/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.definition.property.event;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "BPMNProperties"),
        startElement = "signalRef"
)
public class SignalEventExecutionSet implements BPMNPropertySet {

    @Name
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FormField(
            labelKey = "signalRef"
    )
    @Valid
    private SignalRef signalRef;

    public SignalEventExecutionSet() {
        this(new SignalRef());
    }

    public SignalEventExecutionSet(final @MapsTo("signalRef") SignalRef signalRef) {
        this.signalRef = signalRef;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public SignalRef getSignalRef() {
        return signalRef;
    }

    public void setSignalRef(final SignalRef signalRef) {
        this.signalRef = signalRef;
    }
}