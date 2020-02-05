/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included.imports.persistence;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.IsInformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class DMNIncludedModelHandler implements DRGElementHandler {

    private final DMNGraphUtils dmnGraphUtils;

    private final DefaultCanvasCommandFactory canvasCommandFactory;

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    private final DefinitionUtils definitionUtils;

    @Inject
    public DMNIncludedModelHandler(final DMNGraphUtils dmnGraphUtils,
                                   final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                   final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                   final DefinitionUtils definitionUtils) {
        this.dmnGraphUtils = dmnGraphUtils;
        this.canvasCommandFactory = canvasCommandFactory;
        this.sessionCommandManager = sessionCommandManager;
        this.definitionUtils = definitionUtils;
    }

    @Override
    public void update(final String oldModelName,
                       final String newModelName) {

        updateDRGElementsVariableAndId(oldModelName, newModelName);
        findDRGElementsByOldName(oldModelName)
                .forEach(drgElement -> {
                    final String oldName = drgElement.getName().getValue();
                    final String newName = oldName.replaceAll("^" + oldModelName, newModelName);

                    updateDRGElementName(drgElement, newName);
                });
    }

    @Override
    public void destroy(final String oldModelName) {
        findDRGElementsByOldName(oldModelName).forEach(this::deleteDRGElement);
    }

    private List<DRGElement> findDRGElementsByOldName(final String oldModelName) {
        return dmnGraphUtils
                .getDRGElements()
                .stream()
                .filter(drgElement -> drgElementNameStartsWith(oldModelName, drgElement))
                .filter(DRGElement::isAllowOnlyVisualChange)
                .collect(Collectors.toList());
    }

    private void updateDRGElementsVariableAndId(final String oldModelName,
                                                final String newModelName) {
        dmnGraphUtils
                .getDRGElements()
                .forEach(drgElement -> {

                    final Id oldId = drgElement.getId();

                    if (oldId.getValue().startsWith(oldModelName)) {
                        final String newId = oldId.getValue().replaceAll("^" + oldModelName, newModelName);
                        drgElement.setId(new Id(newId));
                    }

                    if (drgElement instanceof HasVariable) {

                        final HasVariable hasVariable = (HasVariable) drgElement;
                        final IsInformationItem variable = hasVariable.getVariable();
                        final QName typeRef = variable.getTypeRef();
                        final String oldType = typeRef.getLocalPart();

                        if (oldType.startsWith(oldModelName)) {
                            final String newType = oldType.replaceAll("^" + oldModelName, newModelName);
                            variable.setTypeRef(new QName(typeRef.getNamespaceURI(), newType, typeRef.getPrefix()));
                        }
                    }
                });
    }

    private boolean drgElementNameStartsWith(final String oldModelName,
                                             final DRGElement drgElement) {
        final String value = drgElement.getName().getValue();
        return value.startsWith(oldModelName + ".");
    }

    void deleteDRGElement(final DRGElement drgElement) {
        sessionCommandManager.execute(getCanvasHandler(), buildDeleteCommand(drgElement));
    }

    CompositeCommand<AbstractCanvasHandler, CanvasViolation> buildDeleteCommand(final DRGElement drgElement) {

        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();
        final Node node = getNode(drgElement);

        commandBuilder.addCommand(canvasCommandFactory.deleteNode(node));

        return commandBuilder.build();
    }

    void updateDRGElementName(final DRGElement drgElement,
                              final String newName) {
        sessionCommandManager.execute(getCanvasHandler(), buildUpdateCommand(drgElement, newName));
    }

    CompositeCommand<AbstractCanvasHandler, CanvasViolation> buildUpdateCommand(final DRGElement drgElement,
                                                                                final String newName) {

        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();
        final Node node = getNode(drgElement);

        if (node.getContent() instanceof Definition) {

            final Definition definition = (Definition) node.getContent();
            final String nameId = definitionUtils.getNameIdentifier(definition.getDefinition());

            if (nameId != null) {
                commandBuilder.addCommand(canvasCommandFactory.updatePropertyValue(node, nameId, newName));
            }
        }

        return commandBuilder.build();
    }

    Node getNode(final Object definition) {
        final Graph<?, Node> graph = getCanvasHandler().getDiagram().getGraph();
        return StreamSupport
                .stream(graph.nodes().spliterator(), false)
                .filter((Node node) -> node.getContent() instanceof Definition)
                .filter(node -> Objects.equals(definition, ((Definition) node.getContent()).getDefinition()))
                .findFirst()
                .orElse(null);
    }

    private AbstractCanvasHandler getCanvasHandler() {
        return (AbstractCanvasHandler) dmnGraphUtils.getCanvasHandler();
    }
}
