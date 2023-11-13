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

package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessVariables;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.commons.Pair;

public class VariablesProvider
        extends AbstractProcessFilteredNodeProvider {

    @Inject
    public VariablesProvider(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public Predicate<Node> getFilter() {
        //not used in this implementation.
        return node -> true;
    }

    @Override
    public Function<Node, Pair<Object, String>> getMapper() {
        //not used in this implementation.
        return null;
    }

    @Override
    protected Collection<Pair<Object, String>> findElements(Predicate<Node> filter,
                                                            Function<Node, Pair<Object, String>> mapper) {
        Collection<Pair<Object, String>> result = new ArrayList<>();
        String elementUUID = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getMetadata().getCanvasRootUUID();
        Node node;
        if (elementUUID != null) {
            node = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getGraph().getNode(elementUUID);
            Object oDefinition = ((View) node.getContent()).getDefinition();
            if (oDefinition instanceof BPMNDiagram) {
                BPMNDiagram bpmnDiagram = (BPMNDiagram) oDefinition;

                BaseProcessVariables processVars = bpmnDiagram.getProcessData().getProcessVariables();
                addPropertyVariableToResult(result, processVars.getValue());


                Iterable<Node> nodes = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getGraph().nodes();

                StreamSupport.stream(nodes.spliterator(), false)
                        .filter(this::isBPMNDefinition)
                        .map(elm -> (Node<View<BPMNDefinition>, Edge>) elm)
                        .forEach(elm -> processNode(elm, result));

                CaseFileVariables caseVars = bpmnDiagram.getCaseManagementSet().getCaseFileVariables();
                addCaseFileVariableToResult(result, caseVars.getValue());
            }
        }

        return result;
    }

    private void processNode(Node<View<BPMNDefinition>, Edge> elm, Collection<Pair<Object, String>> result) {
            if(elm.getContent().getDefinition() instanceof DataObject) {
                DataObject dataObject = (DataObject)elm.getContent().getDefinition();
                String name = dataObject.getName().getValue();
                result.add(new Pair(name, name));
            }
    }

    protected boolean isBPMNDefinition(Node node) {
        return node.getContent() instanceof View &&
                ((View) node.getContent()).getDefinition() instanceof BPMNDefinition;
    }

    private void addPropertyVariableToResult(Collection<Pair<Object, String>> result, String element) {
        if (element.length() > 0) {
            element = StringUtils.preFilterVariablesTwoSemicolonForGenerics(element);
            List<String> list = Arrays.asList(element.split(","));
            list.forEach(s1 -> {
                String value = s1.split(":")[0];
                result.add(new Pair<>(value, value));
            });
        }
    }

    private void addCaseFileVariableToResult(Collection<Pair<Object, String>> result, String element) {
        if (element.length() > 0) {
            List<String> list = Arrays.asList(element.split(","));
            list.forEach(s1 -> {
                String value = CaseFileVariables.CASE_FILE_PREFIX + s1.split(":")[0];
                result.add(new Pair<>(value, value));
            });
        }
    }
}
