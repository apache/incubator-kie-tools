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

import java.util.Objects;

import javax.validation.constraints.Pattern;

import org.jboss.errai.common.client.api.annotations.MapsTo;
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
public class SignalRef implements BPMNProperty {

    protected static final String STATIC_EXP = "[a-zA-Z0-9._:\\s]+";
    protected static final String OR_EXP = "|";
    protected static final String EXP_SEPARATOR = "[\\-]";
    protected static final String MVEL_EXP = "[#]{1}[{]{1}[a-zA-Z0-9_]+[}]{1}";
    protected static final String MVEL_COMPLEX_EXP = "[#]{1}[{]{1}[a-zA-Z0-9_]+[.]+[a-zA-Z0-9_]+[}]{1}";
    protected static final String MVEL_COMPLEX_PARAM_EXP = "[#]{1}[{]{1}[a-zA-Z0-9_]+[.]*[a-zA-Z0-9_]+[(]{1}[a-zA-Z0-9._\",\\s]+[)]{1}[}]{1}";

    @Value
    @FieldValue
    @Pattern(regexp = STATIC_EXP + EXP_SEPARATOR + MVEL_COMPLEX_PARAM_EXP +
            OR_EXP +
            STATIC_EXP + EXP_SEPARATOR + MVEL_COMPLEX_EXP +
            OR_EXP +
            STATIC_EXP + EXP_SEPARATOR + MVEL_EXP +
            OR_EXP +
            STATIC_EXP + EXP_SEPARATOR + STATIC_EXP +
            OR_EXP +
            MVEL_COMPLEX_PARAM_EXP +
            OR_EXP +
            MVEL_COMPLEX_EXP +
            OR_EXP +
            MVEL_EXP +
            OR_EXP +
            STATIC_EXP +
            OR_EXP +
            "^$",
            message = "Invalid characters. Hover over the \"i\" icon for more information")
    private String value;

    public SignalRef() {
        this("");
    }

    public SignalRef(final @MapsTo("value") String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SignalRef) {
            SignalRef other = (SignalRef) o;
            return Objects.equals(value, other.value);
        }
        return false;
    }
}
