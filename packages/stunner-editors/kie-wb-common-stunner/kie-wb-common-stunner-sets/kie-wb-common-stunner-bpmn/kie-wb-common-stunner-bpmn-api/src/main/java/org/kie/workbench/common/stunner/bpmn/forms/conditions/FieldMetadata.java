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


package org.kie.workbench.common.stunner.bpmn.forms.conditions;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class FieldMetadata {

    private String name;

    private String type;

    private String accessor;

    private String mutator;

    public FieldMetadata(final @MapsTo("name") String name,
                         final @MapsTo("type") String type,
                         final @MapsTo("accessor") String accessor,
                         final @MapsTo("mutator") String mutator) {
        this.name = name;
        this.type = type;
        this.accessor = accessor;
        this.mutator = mutator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccessor() {
        return accessor;
    }

    public void setAccessor(String accessor) {
        this.accessor = accessor;
    }

    public String getMutator() {
        return mutator;
    }

    public void setMutator(String mutator) {
        this.mutator = mutator;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(name),
                                         Objects.hashCode(type),
                                         Objects.hashCode(accessor),
                                         Objects.hashCode(mutator));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof FieldMetadata) {
            FieldMetadata other = (FieldMetadata) o;
            return Objects.equals(name, other.name) &&
                    Objects.equals(type, other.type) &&
                    Objects.equals(accessor, other.accessor) &&
                    Objects.equals(mutator, other.mutator);
        }
        return false;
    }
}
