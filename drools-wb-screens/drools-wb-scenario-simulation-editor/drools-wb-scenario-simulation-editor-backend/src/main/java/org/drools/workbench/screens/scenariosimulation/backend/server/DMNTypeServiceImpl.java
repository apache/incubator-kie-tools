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

package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;

import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class DMNTypeServiceImpl
        extends AbstractKieContainerService
        implements DMNTypeService {

    @Override
    public FactModelTuple retrieveType(Path path, String dmnPath) {
        DMNModel dmnModel = getDMNModel(path, dmnPath);
        SortedMap<String, FactModelTree> visibleFacts = new TreeMap<>();
        SortedMap<String, FactModelTree> hiddenFacts = new TreeMap<>();
        for (InputDataNode input : dmnModel.getInputs()) {
            DMNType type = input.getType();
            visibleFacts.put(input.getName(), createFactModelTree(input.getName(), input.getName(), type, hiddenFacts, FactModelTree.Type.INPUT));
        }
        for (DecisionNode decision : dmnModel.getDecisions()) {
            DMNType type = decision.getResultType();
            visibleFacts.put(decision.getName(), createFactModelTree(decision.getName(), decision.getName(), type, hiddenFacts, FactModelTree.Type.DECISION));
        }
        return new FactModelTuple(visibleFacts, hiddenFacts);
    }

    public DMNModel getDMNModel(Path path, String dmnPath) {
        return getDMNRuntime(path).getModels().stream()
                .filter(model -> dmnPath.endsWith(model.getResource().getSourcePath()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Impossible to find DMN model"));
    }

    public DMNRuntime getDMNRuntime(Path path) {
        KieContainer kieContainer = getKieContainer(path);
        return kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
    }

    private FactModelTree createFactModelTree(String name, String path, DMNType type, SortedMap<String, FactModelTree> hiddenFacts, FactModelTree.Type fmType) {
        Map<String, String> simpleFields = new HashMap<>();
        if (!type.isComposite()) {
            simpleFields.put("value", type.getName());
            FactModelTree simpleFactModelTree = new FactModelTree(name, "", simpleFields, fmType);
            simpleFactModelTree.setSimple(true);
            return simpleFactModelTree;
        }
        FactModelTree factModelTree = new FactModelTree(name, "", simpleFields, fmType);
        for (Map.Entry<String, DMNType> entry : type.getFields().entrySet()) {
            if (!entry.getValue().isComposite()) {
                simpleFields.put(entry.getKey(), entry.getValue().getName());
            } else {
                String expandableId = path + "." + entry.getKey();
                factModelTree.addExpandableProperty(entry.getKey(), expandableId);
                hiddenFacts.put(expandableId, createFactModelTree(entry.getKey(), expandableId, entry.getValue(), hiddenFacts, FactModelTree.Type.UNDEFINED));
            }
        }
        return factModelTree;
    }
}
