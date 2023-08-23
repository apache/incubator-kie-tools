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

package org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes;

import java.util.function.BiConsumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBusinessKnowledgeModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITNamedElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.stunner.core.graph.Node;

import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.uniqueId;

public class NodeEntry {

    private String name;

    private Node node;

    private final String id;

    private final String diagramId;

    private final JSIDMNShape dmnShape;

    private final boolean isIncluded;

    private final JSITDMNElement dmnElement;

    private final BiConsumer<String, HasComponentWidths> componentWidthsConsumer;

    NodeEntry(final String diagramId,
              final JSIDMNShape dmnShape,
              final JSITDMNElement dmnElement,
              final boolean isIncluded,
              final BiConsumer<String, HasComponentWidths> componentWidthsConsumer) {
        this.id = uniqueId();
        this.diagramId = diagramId;
        this.dmnShape = dmnShape;
        this.dmnElement = dmnElement;
        this.isIncluded = isIncluded;
        this.componentWidthsConsumer = componentWidthsConsumer;
    }

    public JSIDMNShape getDmnShape() {
        return dmnShape;
    }

    public JSITDMNElement getDmnElement() {
        return dmnElement;
    }

    public BiConsumer<String, HasComponentWidths> getComponentWidthsConsumer() {
        return componentWidthsConsumer;
    }

    public String getId() {
        return id;
    }

    public boolean isIncluded() {
        return isIncluded;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(final Node node) {
        this.node = node;
    }

    public String getDiagramId() {
        return diagramId;
    }

    public String getName() {
        if (name == null) {
            final String type = dmnElement.getTYPE_NAME();
            switch (type) {
                case JSITBusinessKnowledgeModel.TYPE:
                case JSITDecision.TYPE:
                case JSITDecisionService.TYPE:
                case JSITInputData.TYPE:
                case JSITKnowledgeSource.TYPE:
                    final JSITNamedElement drgElement = Js.uncheckedCast(dmnElement);
                    name = drgElement.getName();
                    break;
                case JSITTextAnnotation.TYPE:
                    final JSITTextAnnotation textAnnotation = Js.uncheckedCast(dmnElement);
                    name = textAnnotation.getText();
                    break;
            }
        }
        return name;
    }
}
