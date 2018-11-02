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
package org.kie.workbench.common.stunner.cm.backend.marshall.json;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.Bpmn2Marshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;

public class CaseManagementMarshaller extends Bpmn2Marshaller {

    private List<ItemDefinition> _subprocessItemDefs = new LinkedList<>();

    public CaseManagementMarshaller(DefinitionManager definitionManager, OryxManager oryxManager) {
        super(definitionManager, oryxManager);
    }

    @Override
    public void revisitSubProcessItemDefs(Definitions def) {
        _subprocessItemDefs.forEach(i -> def.getRootElements().add(i));
        _subprocessItemDefs.clear();
    }

    @Override
    protected void addSubprocessItemDefs(ItemDefinition itemdef) {
        _subprocessItemDefs.add(itemdef);
    }
}
