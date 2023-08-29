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
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class TypeMetadataQueryResult {

    private Set<TypeMetadata> typeMetadatas;

    private Set<String> missingTypes;

    public TypeMetadataQueryResult(final @MapsTo("typeMetadatas") Set<TypeMetadata> typeMetadatas,
                                   final @MapsTo("missingTypes") Set<String> missingTypes) {
        this.typeMetadatas = typeMetadatas;
        this.missingTypes = missingTypes;
    }

    public Set<String> getMissingTypes() {
        return missingTypes;
    }

    public void setMissingTypes(Set<String> missingTypes) {
        this.missingTypes = missingTypes;
    }

    public Set<TypeMetadata> getTypeMetadatas() {
        return typeMetadatas;
    }

    public void setTypeMetadatas(Set<TypeMetadata> typeMetadatas) {
        this.typeMetadatas = typeMetadatas;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(missingTypes),
                                         Objects.hashCode(typeMetadatas));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TypeMetadataQueryResult) {
            TypeMetadataQueryResult other = (TypeMetadataQueryResult) o;
            return Objects.equals(missingTypes, other.missingTypes) &&
                    Objects.equals(typeMetadatas, other.typeMetadatas);
        }
        return false;
    }
}