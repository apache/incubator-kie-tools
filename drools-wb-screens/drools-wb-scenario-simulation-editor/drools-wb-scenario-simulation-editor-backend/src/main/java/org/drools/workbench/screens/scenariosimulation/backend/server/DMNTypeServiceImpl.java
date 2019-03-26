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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;

import org.drools.workbench.screens.scenariosimulation.backend.server.util.DMNSimulationUtils;
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

        ErrorHolder errorHolder = new ErrorHolder();

        for (InputDataNode input : dmnModel.getInputs()) {
            DMNType type = input.getType();

            checkTypeSupport(type, errorHolder, input.getName());

            visibleFacts.put(input.getName(), createFactModelTree(input.getName(), input.getName(), type, hiddenFacts, FactModelTree.Type.INPUT));
        }
        for (DecisionNode decision : dmnModel.getDecisions()) {
            DMNType type = decision.getResultType();

            checkTypeSupport(type, errorHolder, decision.getName());

            visibleFacts.put(decision.getName(), createFactModelTree(decision.getName(), decision.getName(), type, hiddenFacts, FactModelTree.Type.DECISION));
        }
        FactModelTuple factModelTuple = new FactModelTuple(visibleFacts, hiddenFacts);

        errorHolder.getTopLevelCollection().forEach(factModelTuple::addTopLevelCollectionError);
        errorHolder.getMultipleNestedCollection().forEach(factModelTuple::addMultipleNestedCollectionError);
        errorHolder.getMultipleNestedObject().forEach(factModelTuple::addMultipleNestedObjectError);

        return factModelTuple;
    }

    public DMNModel getDMNModel(Path path, String dmnPath) {
        return DMNSimulationUtils.extractDMNModel(getDMNRuntime(path), dmnPath);
    }

    public DMNRuntime getDMNRuntime(Path path) {
        KieContainer kieContainer = getKieContainer(path);
        return DMNSimulationUtils.extractDMNRuntime(kieContainer);
    }

    protected FactModelTree createFactModelTree(String name, String path, DMNType type, SortedMap<String, FactModelTree> hiddenFacts, FactModelTree.Type fmType) {
        return createFactModelTree(name, path, type, hiddenFacts, fmType, false);
    }

    protected FactModelTree createFactModelTree(String name, String path, DMNType type, SortedMap<String, FactModelTree> hiddenFacts, FactModelTree.Type fmType, boolean collectionRecursion) {
        // a simple type
        if (!type.isComposite()) {
            // if is not a collection or a recursion just retur a simple fact
            if (!type.isCollection() || collectionRecursion) {
                return createSimpleFact(name, type.getName(), fmType);
            }
            // otherwise create the generics and return the simple type
            else {
                Map<String, String> simpleFields = new HashMap<>();
                Map<String, List<String>> genericTypeInfoMap = new HashMap<>();
                String genericKey = populateGeneric(simpleFields, genericTypeInfoMap, path, type.getName(), type.getName());

                FactModelTree fact = createSimpleFact(name, type.getName(), fmType);
                hiddenFacts.put(genericKey, fact);
                return fact;
            }
        }

        Map<String, String> simpleFields = new HashMap<>();
        Map<String, List<String>> genericTypeInfoMap = new HashMap<>();
        FactModelTree factModelTree = new FactModelTree(name, "", simpleFields, genericTypeInfoMap, fmType);
        for (Map.Entry<String, DMNType> entry : type.getFields().entrySet()) {

            String expandableId = path + "." + entry.getKey();

            // if it is a collection, just generate the generic and add as hidden fact a simple or composite fact model tree
            if (entry.getValue().isCollection()) {
                String genericKey = populateGeneric(simpleFields, genericTypeInfoMap, path, entry.getValue().getName(), entry.getKey());

                FactModelTree fact = createFactModelTree(entry.getKey(), expandableId, entry.getValue(), hiddenFacts, FactModelTree.Type.UNDEFINED, true);
                hiddenFacts.put(genericKey, fact);
            }
            // a simple type is just name -> type
            else if (!entry.getValue().isComposite()) {
                simpleFields.put(entry.getKey(), entry.getValue().getName());
            }
            // a complex type needs the expandable property and then in the hidden map, its fact model tree
            else {
                factModelTree.addExpandableProperty(entry.getKey(), expandableId);
                hiddenFacts.put(expandableId, createFactModelTree(entry.getKey(), expandableId, entry.getValue(), hiddenFacts, FactModelTree.Type.UNDEFINED));
            }
        }
        return factModelTree;
    }

    private FactModelTree createSimpleFact(String name, String type, FactModelTree.Type fmType) {
        Map<String, String> simpleFields = new HashMap<>();
        Map<String, List<String>> genericTypeInfoMap = new HashMap<>();
        FactModelTree simpleFactModelTree = new FactModelTree(name, "", simpleFields, genericTypeInfoMap, fmType);
        simpleFields.put("value", type);
        simpleFactModelTree.setSimple(true);
        return simpleFactModelTree;
    }

    private String populateGeneric(Map<String, String> simpleFields,
                                   Map<String, List<String>> genericTypeInfoMap,
                                   String path,
                                   String type,
                                   String name) {
        String genericKey = path + "." + type;
        genericTypeInfoMap.put(name, Collections.singletonList(genericKey));
        simpleFields.put(name, List.class.getCanonicalName());

        return genericKey;
    }

    protected void checkTypeSupport(DMNType type, ErrorHolder errorHolder, String path) {
        if (type.isCollection()) {
            errorHolder.getTopLevelCollection().add(path);
        }
        visitType(type, false, errorHolder, path);
    }

    private void visitType(DMNType type,
                           boolean alreadyInCollection,
                           ErrorHolder errorHolder,
                           String path) {
        if (type.isComposite()) {
            for (Map.Entry<String, DMNType> entry : type.getFields().entrySet()) {
                String name = entry.getKey();
                DMNType nestedType = entry.getValue();
                String nestedPath = path + "." + name;
                if (alreadyInCollection && nestedType.isCollection()) {
                    errorHolder.getMultipleNestedCollection().add(nestedPath);
                }
                if (alreadyInCollection && nestedType.isComposite()) {
                    errorHolder.getMultipleNestedObject().add(nestedPath);
                }
                visitType(nestedType,
                          alreadyInCollection || nestedType.isCollection(),
                          errorHolder,
                          nestedPath);
            }
        }
    }

    static class ErrorHolder {

        List<String> topLevelCollection = new ArrayList<>();
        List<String> multipleNestedObject = new ArrayList<>();
        List<String> multipleNestedCollection = new ArrayList<>();

        public List<String> getTopLevelCollection() {
            return topLevelCollection;
        }

        public List<String> getMultipleNestedObject() {
            return multipleNestedObject;
        }

        public List<String> getMultipleNestedCollection() {
            return multipleNestedCollection;
        }
    }
}