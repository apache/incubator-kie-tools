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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import java.util.Objects;

import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadata;
import org.kie.workbench.common.stunner.core.util.HashUtil;

public class VariableMetadata {

    private String name;

    private String type;

    private TypeMetadata typeMetadata;

    public VariableMetadata() {

    }

    public VariableMetadata(String name,
                            String type,
                            TypeMetadata typeMetadata) {
        this.name = name;
        this.type = type;
        this.typeMetadata = typeMetadata;
    }

    public VariableMetadata(String name,
                            String type) {
        this.name = name;
        this.type = type;
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

    public TypeMetadata getTypeMetadata() {
        return typeMetadata;
    }

    public void setTypeMetadata(TypeMetadata typeMetadata) {
        this.typeMetadata = typeMetadata;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(name),
                                         Objects.hashCode(type),
                                         Objects.hashCode(typeMetadata));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof VariableMetadata) {
            VariableMetadata other = (VariableMetadata) o;
            return Objects.equals(name, other.name) &&
                    Objects.equals(type, other.type) &&
                    Objects.equals(typeMetadata, other.typeMetadata);
        }
        return false;
    }
}
