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

package org.kie.workbench.common.dmn.client.docks.navigator.drds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

public class DMNDiagramsSessionState {

    private DMNDiagramElement currentDMNDiagramElement = null;

    private final Map<String, Diagram> diagramsByDiagramId = new HashMap<>();

    private final Map<String, DMNDiagramElement> dmnDiagramsByDiagramId = new HashMap<>();

    private final DMNDiagramUtils dmnDiagramUtils;

    @Inject
    public DMNDiagramsSessionState(final DMNDiagramUtils dmnDiagramUtils) {
        this.dmnDiagramUtils = dmnDiagramUtils;
    }

    Map<String, Diagram> getDiagramsByDiagramId() {
        return diagramsByDiagramId;
    }

    Map<String, DMNDiagramElement> getDMNDiagramsByDiagramId() {
        return dmnDiagramsByDiagramId;
    }

    Diagram getDiagram(final String dmnDiagramElementId) {
        return diagramsByDiagramId.get(dmnDiagramElementId);
    }

    DMNDiagramElement getDMNDiagramElement(final String dmnDiagramElementId) {
        return dmnDiagramsByDiagramId.get(dmnDiagramElementId);
    }

    DMNDiagramTuple getDiagramTuple(final String dmnDiagramElementId) {
        final Diagram diagram = getDiagram(dmnDiagramElementId);
        final DMNDiagramElement dmnDiagramElement = getDMNDiagramElement(dmnDiagramElementId);
        return new DMNDiagramTuple(diagram,
                                   dmnDiagramElement);
    }

    List<DMNDiagramTuple> getDMNDiagrams() {
        return dmnDiagramsByDiagramId
                .values()
                .stream()
                .map(dmnDiagramElement -> getDiagramTuple(dmnDiagramElement.getId().getValue()))
                .collect(Collectors.toList());
    }

    void setCurrentDMNDiagramElement(final DMNDiagramElement currentDMNDiagramElement) {
        this.currentDMNDiagramElement = currentDMNDiagramElement;
    }

    Optional<DMNDiagramElement> getCurrentDMNDiagramElement() {
        final Optional<DMNDiagramElement> currentDMNDiagramElement = Optional.ofNullable(this.currentDMNDiagramElement);

        if (currentDMNDiagramElement.isPresent()) {
            return currentDMNDiagramElement;
        }

        return Optional.ofNullable(getDRGDiagramElement());
    }

    Optional<Diagram> getCurrentDiagram() {
        final String currentDiagramId = getCurrentDMNDiagramElement().map(e -> e.getId().getValue()).orElse("");
        return Optional.ofNullable(diagramsByDiagramId.get(currentDiagramId));
    }

    public Diagram getDRGDiagram() {
        return getDRGDiagramTuple().getStunnerDiagram();
    }

    DMNDiagramElement getDRGDiagramElement() {
        return getDRGDiagramTuple().getDMNDiagram();
    }

    DMNDiagramTuple getDRGDiagramTuple() {
        return getDMNDiagrams()
                .stream()
                .filter(t -> DRGDiagramUtils.isDRG(t.getDMNDiagram()))
                .findFirst()
                .orElse(null);
    }

    void clear() {
        diagramsByDiagramId.clear();
        dmnDiagramsByDiagramId.clear();
    }

    List<DRGElement> getModelDRGElements() {
        return getDMNDiagrams()
                .stream()
                .flatMap(diagram -> dmnDiagramUtils.getDRGElements(diagram.getStunnerDiagram()).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    List<Import> getModelImports() {
        final Diagram drgDiagram = getDRGDiagram();
        return dmnDiagramUtils.getDefinitions(drgDiagram).getImport();
    }
}
