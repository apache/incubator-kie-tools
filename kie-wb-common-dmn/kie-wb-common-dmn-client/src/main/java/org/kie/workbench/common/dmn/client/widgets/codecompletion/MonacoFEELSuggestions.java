/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.Candidate;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.Variable;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.kie.dmn.feel.lang.types.BuiltInType.determineTypeFromName;

@ApplicationScoped
public class MonacoFEELSuggestions {

    private final DMNGraphUtils dmnGraphUtils;

    private final FEELLanguageService feelLanguageService;

    @Inject
    public MonacoFEELSuggestions(final DMNGraphUtils dmnGraphUtils,
                                 final FEELLanguageService feelLanguageService) {
        this.dmnGraphUtils = dmnGraphUtils;
        this.feelLanguageService = feelLanguageService;
    }

    public List<Candidate> getCandidates(final String expression,
                                         final Position position) {
        try {
            return feelLanguageService.getCandidates(expression, getNodesVariables(), position);
        } catch (final Exception e) {
            warn("[FEELLanguageService] Error: Candidates could not be processed.");
            return new ArrayList<>();
        }
    }

    private List<Variable> getNodesVariables() {
        return getDiagramDefinitions()
                .stream()
                .map(this::asVariable)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<Variable> asVariable(final Object definition) {
        if (definition instanceof NamedElement) {
            return Optional.of(new Variable(getName(definition), getType(definition)));
        }
        return Optional.empty();
    }

    private String getName(final Object definition) {
        final NamedElement namedElement = (NamedElement) definition;
        final Name name = namedElement.getName();
        return name.getValue();
    }

    private Type getType(final Object definition) {
        try {
            final HasVariable<?> hasVariable = (HasVariable<?>) definition;
            final String localPart = hasVariable.getVariable().getTypeRef().getLocalPart();
            return determineTypeFromName(localPart);
        } catch (final Exception e) {
            return BuiltInType.UNKNOWN;
        }
    }

    private List<Object> getDiagramDefinitions() {
        return dmnGraphUtils
                .getNodeStream()
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition<?>) c)
                .map(Definition::getDefinition)
                .collect(Collectors.toList());
    }

    void warn(final String message) {
        DomGlobal.console.warn(message);
    }
}
