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

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.VariableDeclaration;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class DeclarationWriter {

    private final String parentId;
    private final DataInput target;
    private final ItemDefinition typeDef;
    private final VariableDeclaration decl;
    private final String varId;

    public DeclarationWriter(
            String parentId,
            VariableDeclaration decl) {
        this.varId = decl.getIdentifier();
        this.parentId = parentId;
        this.decl = decl;

        // first we declare the type that we will use for the input
        this.typeDef = typedefInput(decl);

        // then we declare the input that will provide
        // the value that we assign to `source`
        // e.g. myTarget
        this.target = readInputFrom(decl.getIdentifier(), typeDef);
    }

    private ItemDefinition typedefInput(VariableDeclaration decl) {
        ItemDefinition typeDef = bpmn2.createItemDefinition();
        typeDef.setId(Ids.dataInputItem(parentId, decl.getIdentifier()));
        typeDef.setStructureRef(decl.getType());
        return typeDef;
    }

    private DataInput readInputFrom(String targetName, ItemDefinition typeDef) {
        DataInput dataInput = bpmn2.createDataInput();
        // the id is an encoding of the node id + the name of the input
        dataInput.setId(Ids.dataInput(parentId, decl.getIdentifier()));
        dataInput.setName(targetName);
        dataInput.setItemSubjectRef(typeDef);
        CustomAttribute.dtype.of(dataInput).set(typeDef.getStructureRef());
        return dataInput;
    }

    public DataInput getDataInput() {
        return target;
    }

    public ItemDefinition getItemDefinition() {
        return typeDef;
    }

    public String getVarId() {
        return varId;
    }

    public String getParentId() {
        return parentId;
    }
}
