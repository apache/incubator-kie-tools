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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.VariableDeclaration;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Ids;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public interface VariableScope {

    Variable declare(String scopeId, String identifier, String type);

    Optional<Variable> lookup(String identifier);

    Variable declare(String scopeId, String identifier, String type, String kpi);

    Collection<Variable> getVariables(String scopeId);

    class Variable {

        String parentScopeId;
        VariableDeclaration declaration;
        ItemDefinition typeDeclaration;
        Property typedIdentifier;
        String kpi;

        Variable(String parentScopeId, String identifier, String type) {
            this.parentScopeId = parentScopeId;
            this.declaration = new VariableDeclaration(identifier, type);

            this.typeDeclaration = bpmn2.createItemDefinition();
            this.typeDeclaration.setId(Ids.item(identifier));
            this.typeDeclaration.setStructureRef(type);

            this.typedIdentifier = bpmn2.createProperty();
            this.typedIdentifier.setId(Ids.typedIdentifier(parentScopeId, identifier));
            this.typedIdentifier.setName(identifier);
            this.typedIdentifier.setItemSubjectRef(typeDeclaration);
        }

        Variable(String parentScopeId, String identifier, String type, String kpi) {
            this(parentScopeId, identifier, type);
            this.kpi = kpi;
        }

        public ItemDefinition getTypeDeclaration() {
            return typeDeclaration;
        }

        public Property getTypedIdentifier() {
            return typedIdentifier;
        }

        public String getParentScopeId() {
            return parentScopeId;
        }
    }
}
