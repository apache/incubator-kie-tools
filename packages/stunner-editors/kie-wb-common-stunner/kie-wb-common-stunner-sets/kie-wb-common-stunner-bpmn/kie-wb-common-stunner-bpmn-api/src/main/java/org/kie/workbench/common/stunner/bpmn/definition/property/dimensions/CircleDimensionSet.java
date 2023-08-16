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

package org.kie.workbench.common.stunner.bpmn.definition.property.dimensions;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition
public class CircleDimensionSet implements BPMNPropertySet {

    @Property
    @FormField(
            type = SliderFieldType.class,
            settings = {
                    @FieldParam(name = "min", value = "25.0"),
                    @FieldParam(name = "max", value = "50.0"),
                    @FieldParam(name = "step", value = "1.0"),
                    @FieldParam(name = "precision", value = "0.0")
            }
    )
    protected Radius radius;

    public CircleDimensionSet() {
        this(new Radius());
    }

    public CircleDimensionSet(final @MapsTo("radius") Radius radius) {
        this.radius = radius;
    }

    public Radius getRadius() {
        return radius;
    }

    public void setRadius(final Radius radius) {
        this.radius = radius;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(radius));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CircleDimensionSet) {
            CircleDimensionSet other = (CircleDimensionSet) o;
            return Objects.equals(radius,
                                  other.radius);
        }
        return false;
    }
}
