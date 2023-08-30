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


package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldValue;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.I18nMode;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNProperty;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Value;

@Portable
@Bindable
@Property
@FieldDefinition(i18nMode = I18nMode.OVERRIDE_I18N_KEY)
public class Executable implements BPMNProperty {

    @Value
    @FieldValue
    private Boolean value;

    public Executable() {
        this(true);
    }

    public Executable(final Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(final Boolean value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return (null != value) ? value.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Executable) {
            Executable other = (Executable) o;
            return (null != value) ? value.equals(other.value) : null == other.value;
        }
        return false;
    }
}
