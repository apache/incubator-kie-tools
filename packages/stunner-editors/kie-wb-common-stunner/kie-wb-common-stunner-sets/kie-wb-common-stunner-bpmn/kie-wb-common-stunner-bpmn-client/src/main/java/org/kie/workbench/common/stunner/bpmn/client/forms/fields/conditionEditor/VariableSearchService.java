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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadata;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadataQuery;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadataQueryResult;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchService;

import static org.kie.workbench.common.stunner.core.client.util.ClientUtils.getSelectedElementUUID;
import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.getParent;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class VariableSearchService implements LiveSearchService<String> {

    private static final String CASE_VARIABLE_PREFIX = "caseFile_";

    private static final String CASE_VARIABLE_LABEL_PREFIX = "VariableSearchService.CaseVariableLabelPrefix";

    private final ConditionEditorMetadataService metadataService;

    private final ClientTranslationService translationService;

    private Map<String, String> options = new HashMap<>();

    private Map<String, TypeMetadata> typesMetadata = new HashMap<>();

    private Map<String, String> optionType = new HashMap<>();

    @Inject
    public VariableSearchService(final ConditionEditorMetadataService metadataService,
                                 final ClientTranslationService translationService) {
        this.metadataService = metadataService;
        this.translationService = translationService;
    }

    public void init(ClientSession session) {
        Diagram diagram = session.getCanvasHandler().getDiagram();
        String canvasRootUUID = diagram.getMetadata().getCanvasRootUUID();
        @SuppressWarnings("unchecked")
        Node<?, ? extends Edge> selectedNode = getSourceNode(diagram, getSelectedElementUUID(session));
        if (selectedNode != null) {
            Map<String, VariableMetadata> collectedVariables = new HashMap<>();
            Set<String> collectedTypes = new HashSet<>();
            Node<?, ? extends Edge> parentNode = getParent(selectedNode).asNode();
            String parentVariables;
            while (parentNode != null) {
                parentVariables = getVariables(parentNode);
                if (!isEmpty(parentVariables)) {
                    addVariables(parentVariables, collectedVariables, collectedTypes);
                }
                if (parentNode.getUUID().equals(canvasRootUUID)) {
                    parentNode = null;
                } else {
                    parentNode = getParent(parentNode).asNode();
                }
            }
            Path path = session.getCanvasHandler().getDiagram().getMetadata().getPath();
            TypeMetadataQuery query = new TypeMetadataQuery(path, collectedTypes);
            metadataService
                    .call(query)
                    .then(result -> {
                        initVariables(collectedVariables.values(),
                                      result);
                        return null;
                    });
        }
    }

    @Override
    public void search(String pattern, int maxResults, LiveSearchCallback<String> callback) {
        LiveSearchResults<String> results = new LiveSearchResults<>(maxResults);
        options.entrySet().stream()
                .filter(entry -> entry.getValue().toLowerCase().contains(pattern.toLowerCase()))
                .forEach(entry -> results.add(entry.getKey(), entry.getValue()));
        callback.afterSearch(results);
    }

    @Override
    public void searchEntry(String key, LiveSearchCallback<String> callback) {
        LiveSearchResults<String> results = new LiveSearchResults<>();
        if (options.containsKey(key)) {
            results.add(key, options.get(key));
        }
        callback.afterSearch(results);
    }

    public String getOptionType(String key) {
        return optionType.get(key);
    }

    public void clear() {
        options.clear();
        typesMetadata.clear();
        optionType.clear();
    }

    protected String getVariables(Node<?, ? extends Edge> node) {
        View view = node.getContent() instanceof View ? (View) node.getContent() : null;
        if (view == null) {
            return null;
        }
        if (view.getDefinition() instanceof EventSubprocess) {
            return ((EventSubprocess) view.getDefinition()).getProcessData().getProcessVariables().getValue();
        }
        if (view.getDefinition() instanceof AdHocSubprocess) {
            return ((AdHocSubprocess) view.getDefinition()).getProcessData().getProcessVariables().getValue();
        }
        if (view.getDefinition() instanceof EmbeddedSubprocess) {
            return ((EmbeddedSubprocess) view.getDefinition()).getProcessData().getProcessVariables().getValue();
        }
        if (view.getDefinition() instanceof MultipleInstanceSubprocess) {
            return ((MultipleInstanceSubprocess) view.getDefinition()).getProcessData().getProcessVariables().getValue();
        }
        if (view.getDefinition() instanceof BPMNDiagramImpl) {
            BPMNDiagramImpl bpmnDiagram = ((BPMNDiagramImpl) view.getDefinition());
            StringBuilder variablesBuilder = new StringBuilder();
            String processVariables = bpmnDiagram.getProcessData().getProcessVariables().getValue();
            if (!isEmpty(processVariables)) {
                variablesBuilder.append(processVariables);
            }
            addCaseFileVariables(variablesBuilder, bpmnDiagram.getCaseManagementSet());
            return variablesBuilder.length() > 0 ? variablesBuilder.toString() : null;
        }
        return null;
    }

    protected void addCaseFileVariables(StringBuilder variablesBuilder, CaseManagementSet caseManagementSet) {
        if (caseManagementSet != null && caseManagementSet.getCaseFileVariables() != null && !isEmpty(caseManagementSet.getCaseFileVariables().getValue())) {
            String caseVariables = caseManagementSet.getCaseFileVariables().getValue();
            String[] caseVariableDefs = caseVariables.split(",");
            boolean isFirst = variablesBuilder.length() == 0;
            for (String caseVariableDefItem : caseVariableDefs) {
                if (!caseVariableDefItem.isEmpty()) {
                    String[] caseVariable = caseVariableDefItem.split(":");
                    if (!isFirst) {
                        variablesBuilder.append(",");
                    }
                    if (caseVariable.length == 1) {
                        variablesBuilder.append(CASE_VARIABLE_PREFIX)
                                .append(caseVariable[0])
                                .append(":");
                    } else {
                        variablesBuilder.append(CASE_VARIABLE_PREFIX)
                                .append(caseVariable[0])
                                .append(":")
                                .append(caseVariable[1]);
                    }
                    isFirst = false;
                }
            }
        }
    }

    private void addVariables(String variables, Map<String, VariableMetadata> collectedVariables, Set<String> collectedTypes) {
        String[] variableDefs = variables.split(",");
        VariableMetadata variableMetadata;
        for (String variableDefItem : variableDefs) {
            if (!variableDefItem.isEmpty()) {
                String[] variableDef = variableDefItem.split(":");
                if (!collectedVariables.containsKey(variableDef[0])) {
                    if (variableDef.length == 1) {
                        variableMetadata = new VariableMetadata(variableDef[0], Object.class.getName());
                    } else {
                        variableMetadata = new VariableMetadata(variableDef[0], unboxDefaultType(variableDef[1]));
                    }
                    collectedVariables.put(variableDef[0], variableMetadata);
                    collectedTypes.add(variableMetadata.getType());
                }
            }
        }
    }

    private void initVariables(Collection<VariableMetadata> variables, TypeMetadataQueryResult result) {
        optionType.clear();
        typesMetadata = result.getTypeMetadatas().stream().collect(Collectors.toMap(TypeMetadata::getType, Function.identity()));
        variables.forEach(variableMetadata -> {
            TypeMetadata typeMetadata = Optional.ofNullable(typesMetadata.get(variableMetadata.getType())).orElse(new TypeMetadata(Object.class.getName()));
            variableMetadata.setTypeMetadata(typeMetadata);
            addVariableOptions(variableMetadata);
        });
    }

    private void addVariableOptions(VariableMetadata variableMetadata) {
        String option = variableMetadata.getName();
        String optionLabel = getVariableLabel(variableMetadata);
        options.put(option, optionLabel);
        optionType.put(option, unboxDefaultType(variableMetadata.getType()));
        TypeMetadata typeMetadata = variableMetadata.getTypeMetadata();
        typeMetadata.getFieldMetadata().stream()
                .filter(fieldMetadata -> fieldMetadata.getAccessor() != null)
                .forEach(fieldMetadata -> {
                    String fieldOption = variableMetadata.getName() + "." + fieldMetadata.getAccessor() + "()";
                    String fieldOptionLabel = optionLabel + "." + fieldMetadata.getName();
                    options.put(fieldOption, fieldOptionLabel);
                    optionType.put(fieldOption, unboxDefaultType(fieldMetadata.getType()));
                });
    }

    static String unboxDefaultType(String type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case "Short":
            case "short":
                return Short.class.getName();
            case "Integer":
            case "int":
                return Integer.class.getName();
            case "Long":
            case "long":
                return Long.class.getName();
            case "Float":
            case "float":
                return Float.class.getName();
            case "Double":
            case "double":
                return Double.class.getName();
            case "Boolean":
            case "boolean":
                return Boolean.class.getName();
            case "Character":
            case "char":
                return Character.class.getName();
            case "String":
                return String.class.getName();
            case "Object":
                return Object.class.getName();
            default:
                return type;
        }
    }

    private Node getSourceNode(Diagram diagram, String edgeUuid) {
        final Iterator<Node> nodes = diagram.getGraph().nodes().iterator();
        Node<?, ? extends Edge> sourceNode;
        while (nodes.hasNext()) {
            sourceNode = nodes.next();
            if (sourceNode.getOutEdges().stream()
                    .anyMatch(edge -> edge.getUUID().equals(edgeUuid))) {
                return sourceNode;
            }
        }
        return null;
    }

    private String getVariableLabel(VariableMetadata variableMetadata) {
        if (variableMetadata.getName().startsWith(CASE_VARIABLE_PREFIX)) {
            return translationService.getValue(CASE_VARIABLE_LABEL_PREFIX) + " " +
                    variableMetadata.getName().substring(CASE_VARIABLE_PREFIX.length());
        } else {
            return variableMetadata.getName();
        }
    }
}