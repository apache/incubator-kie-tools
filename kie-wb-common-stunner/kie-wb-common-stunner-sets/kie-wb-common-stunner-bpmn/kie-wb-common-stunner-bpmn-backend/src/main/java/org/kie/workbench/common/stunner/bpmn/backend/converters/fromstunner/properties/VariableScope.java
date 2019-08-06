/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.VariableDeclaration;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public interface VariableScope {

    Variable declare(String scopeId, String identifier, String type);

    Optional<Variable> lookup(String identifier);

    Collection<Variable> getVariables(String scopeId);

    class Variable {

        String parentScopeId;
        VariableDeclaration declaration;
        ItemDefinition typeDeclaration;
        Property typedIdentifier;

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
