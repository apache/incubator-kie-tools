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


package org.kie.workbench.common.stunner.core.client.canvas;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DeleteNodeConfirmation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.HasStringName;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.stunner.core.client.canvas.resources.StunnerClientCommonConstants.DeleteNodeConfirmationImpl_ConfirmationDescription;
import static org.kie.workbench.common.stunner.core.client.canvas.resources.StunnerClientCommonConstants.DeleteNodeConfirmationImpl_Question;
import static org.kie.workbench.common.stunner.core.client.canvas.resources.StunnerClientCommonConstants.DeleteNodeConfirmationImpl_Title;
import static org.kie.workbench.common.stunner.core.graph.util.NodeDefinitionHelper.getContentDefinitionId;

@Default
@Dependent
public class DeleteNodeConfirmationImpl implements DeleteNodeConfirmation {

    private SessionManager sessionManager;
    private DefinitionUtils definitionUtils;
    private ClientTranslationService translationService;
    private ConfirmationDialog confirmationDialog;
    private ManagedInstance<GraphsProvider> graphsProviderInstances;
    private GraphsProvider graphsProvider;

    static final String QUOTE = "\"";
    static final String SEPARATOR = ", ";

    protected DeleteNodeConfirmationImpl() {
        // CDI proxy
    }

    @Inject
    public DeleteNodeConfirmationImpl(final @Any ManagedInstance<GraphsProvider> graphsProvider,
                                      final ConfirmationDialog confirmationDialog,
                                      final ClientTranslationService translationService,
                                      final DefinitionUtils definitionUtils,
                                      final SessionManager sessionManager) {
        this.graphsProviderInstances = graphsProvider;
        this.confirmationDialog = confirmationDialog;
        this.translationService = translationService;
        this.definitionUtils = definitionUtils;
        this.sessionManager = sessionManager;
    }

    @PostConstruct
    public void init() {
        final Diagram diagram = sessionManager.getCurrentSession()
                .getCanvasHandler()
                .getDiagram();
        final Annotation qualifier = definitionUtils.getQualifier(diagram.getMetadata().getDefinitionSetId());
        graphsProvider = InstanceUtils.lookup(graphsProviderInstances,
                                              GraphsProvider.class,
                                              qualifier);
    }

    @PreDestroy
    public void destroy() {
        graphsProviderInstances.destroyAll();
    }

    @Override
    public boolean requiresDeletionConfirmation(final Collection<Element> elements) {
        return !Objects.isNull(getGraphsProvider())
                && getGraphsProvider().isGlobalGraphSelected()
                && !getGraphsProvider().getNonGlobalGraphs().isEmpty()
                && isReferredInAnotherGraph(elements);
    }

    @Override
    public void confirmDeletion(final Command onDeletionAccepted,
                                final Command onDeletionRejected,
                                final Collection<Element> elements) {

        final String referredElementsName = getReferredElementsName(elements);
        confirmationDialog.show(translationService.getValue(DeleteNodeConfirmationImpl_Title),
                                translationService.getValue(DeleteNodeConfirmationImpl_ConfirmationDescription, referredElementsName),
                                translationService.getValue(DeleteNodeConfirmationImpl_Question),
                                onDeletionAccepted,
                                onDeletionRejected);
    }

    GraphsProvider getGraphsProvider() {
        return graphsProvider;
    }

    String getReferredElementsName(final Collection<Element> elements) {
        final StringBuilder builder = new StringBuilder();
        for (final Element element : elements) {
            if (isReferredInAnotherGraph(element)) {
                final Optional<String> name = getElementName(element);
                name.ifPresent(value -> {
                    if (builder.length() > 0) {
                        builder.append(SEPARATOR);
                    }
                    builder.append(QUOTE);
                    builder.append(value);
                    builder.append(QUOTE);
                });
            }
        }
        return builder.toString();
    }

    Optional<String> getElementName(final Element element) {
        final Object content = element.getContent();
        if (content instanceof Definition) {
            final Object definition = ((Definition) content).getDefinition();
            if (definition instanceof HasStringName) {
                final String name = ((HasStringName) definition).getStringName();
                if (StringUtils.isEmpty(name)) {
                    return Optional.empty();
                }
                return Optional.of(name);
            }
        }
        return Optional.empty();
    }

    boolean isReferredInAnotherGraph(final Collection<Element> elements) {
        return elements.stream().anyMatch(this::isReferredInAnotherGraph);
    }

    boolean isReferredInAnotherGraph(final Element element) {
        if (element.getContent() instanceof Definition) {
            final Definition def = (Definition) element.getContent();
            if (def.getDefinition() instanceof HasContentDefinitionId) {
                final HasContentDefinitionId hasContentDefinitionId = (HasContentDefinitionId) def.getDefinition();
                final String contentId = hasContentDefinitionId.getContentDefinitionId();
                final List<Graph> graphs = getGraphsProvider().getNonGlobalGraphs();
                return graphs.stream().anyMatch(graph -> containsNodeWithContentId(graph, contentId));
            }
        }
        return false;
    }

    boolean containsNodeWithContentId(final Graph graph,
                                      final String contentId) {
        return StreamSupport.stream(graph.nodes().spliterator(), false)
                .anyMatch(n -> Objects.equals(getContentDefinitionId((Node) n), contentId));
    }
}
