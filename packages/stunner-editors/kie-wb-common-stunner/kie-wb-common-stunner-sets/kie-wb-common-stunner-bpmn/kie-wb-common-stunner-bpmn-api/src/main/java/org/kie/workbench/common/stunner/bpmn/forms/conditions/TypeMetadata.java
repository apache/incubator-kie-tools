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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class TypeMetadata {

    private String type;

    private List<FieldMetadata> fieldMetadata = new ArrayList<>();

    public TypeMetadata(final @MapsTo("type") String type,
                        final @MapsTo("fieldMetadata") List<FieldMetadata> fieldMetadata) {
        this.type = type;
        this.fieldMetadata = fieldMetadata;
    }

    public TypeMetadata(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FieldMetadata> getFieldMetadata() {
        return fieldMetadata;
    }

    public void setFieldMetadata(List<FieldMetadata> fieldMetadata) {
        this.fieldMetadata = fieldMetadata;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(type),
                                         Objects.hashCode(fieldMetadata));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TypeMetadata) {
            TypeMetadata other = (TypeMetadata) o;
            return Objects.equals(type, other.type) &&
                    Objects.equals(fieldMetadata, other.fieldMetadata);
        }
        return false;
    }
}
