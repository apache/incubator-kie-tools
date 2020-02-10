/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.codecompletion;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@ApplicationScoped
public class MonacoFEELVariableSuggestions {

    private final DMNGraphUtils dmnGraphUtils;

    @Inject
    public MonacoFEELVariableSuggestions(final DMNGraphUtils dmnGraphUtils) {
        this.dmnGraphUtils = dmnGraphUtils;
    }

    public List<String> getSuggestions() {

        final List<Object> diagramDefinitions = getDiagramDefinitions();
        final List<String> dataTypesSuggestions = getDataTypesSuggestions(diagramDefinitions);
        final List<String> namedElementSuggestions = getNamedElementSuggestions(diagramDefinitions);

        return Stream
                .of(dataTypesSuggestions, namedElementSuggestions)
                .flatMap(Collection::stream)
                .distinct()
                .filter(name -> !name.isEmpty())
                .sorted()
                .collect(Collectors.toList());
    }

    private List<String> getNamedElementSuggestions(final List<Object> diagramDefinitions) {
        return diagramDefinitions
                .stream()
                .map(this::getNamedSuggestion)
                .collect(Collectors.toList());
    }

    private List<String> getDataTypesSuggestions(final List<Object> diagramDefinitions) {
        return diagramDefinitions
                .stream()
                .flatMap(this::getDataTypesSuggestions)
                .collect(Collectors.toList());
    }

    private String getNamedSuggestion(final Object definition) {
        if (definition instanceof NamedElement) {
            final NamedElement namedElement = (NamedElement) definition;
            return getName(namedElement);
        }
        return "";
    }

    private Stream<? extends String> getDataTypesSuggestions(final Object definition) {
        if (definition instanceof DMNDiagram) {
            final DMNDiagram dmnDiagram = (DMNDiagram) definition;
            return dmnDiagram
                    .getDefinitions()
                    .getItemDefinition()
                    .stream()
                    .map(this::getName);
        }
        return Stream.empty();
    }

    private List<Object> getDiagramDefinitions() {
        return dmnGraphUtils
                .getNodeStream()
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition) c)
                .map(Definition::getDefinition)
                .collect(Collectors.toList());
    }

    private String getName(final NamedElement namedElement) {
        final Name name = namedElement.getName();
        return name.getValue();
    }
}
