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

package org.kie.workbench.common.dmn.client.property.dmn;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class DefaultValueUtilities {

    public static void updateNewNodeName(final Graph<?, Node> graph,
                                         final DMNModelInstrumentedBase dmnModel) {
        if (dmnModel instanceof NamedElement) {
            if (isNameAlreadySet((NamedElement) dmnModel)) {
                return;
            }
        }

        if (dmnModel instanceof BusinessKnowledgeModel) {
            updateBusinessKnowledgeModelDefaultName(graph, (BusinessKnowledgeModel) dmnModel);
        } else if (dmnModel instanceof Decision) {
            updateDecisionDefaultName(graph, (Decision) dmnModel);
        } else if (dmnModel instanceof InputData) {
            updateInputDataDefaultName(graph, (InputData) dmnModel);
        } else if (dmnModel instanceof KnowledgeSource) {
            updateKnowledgeSourceDefaultName(graph, (KnowledgeSource) dmnModel);
        } else if (dmnModel instanceof TextAnnotation) {
            updateTextAnnotationDefaultText(graph, (TextAnnotation) dmnModel);
        } else if (dmnModel instanceof DecisionService) {
            updateDecisionServiceDefaultName(graph, (DecisionService) dmnModel);
        } else {
            throw new IllegalArgumentException("Default Name for '" + dmnModel.getClass().getSimpleName() + "' is not supported.");
        }
    }

    public static Optional<Integer> extractIndex(final String text,
                                                 final String prefix) {
        if (text == null) {
            return Optional.empty();
        }

        if (!text.startsWith(prefix)) {
            return Optional.empty();
        }

        final String suffix = text.substring(prefix.length());
        try {
            return Optional.of(Integer.parseInt(suffix));
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }

    public static int getMaxUnusedIndex(final Collection<String> values,
                                        final String prefix) {
        int maxIndex = 0;
        for (final String value : values) {
            final Optional<Integer> index = extractIndex(value, prefix);
            if (index.isPresent()) {
                maxIndex = Math.max(maxIndex, index.get());
            }
        }
        return maxIndex + 1;
    }

    private static void updateBusinessKnowledgeModelDefaultName(final Graph<?, Node> graph,
                                                                final BusinessKnowledgeModel bkm) {
        final String prefix = bkm.getClass().getSimpleName() + "-";
        bkm.getName().setValue(prefix + getMaxUnusedIndex(getExistingNodeNames(graph,
                                                                               bkm,
                                                                               (n) -> n.getName().getValue()),
                                                          prefix));
    }

    private static void updateDecisionDefaultName(final Graph<?, Node> graph,
                                                  final Decision decision) {
        final String prefix = decision.getClass().getSimpleName() + "-";
        decision.getName().setValue(prefix + getMaxUnusedIndex(getExistingNodeNames(graph,
                                                                                    decision,
                                                                                    (n) -> n.getName().getValue()),
                                                               prefix));
    }

    private static void updateInputDataDefaultName(final Graph<?, Node> graph,
                                                   final InputData inputData) {
        final String prefix = inputData.getClass().getSimpleName() + "-";
        inputData.getName().setValue(prefix + getMaxUnusedIndex(getExistingNodeNames(graph,
                                                                                     inputData,
                                                                                     (n) -> n.getName().getValue()),
                                                                prefix));
    }

    private static void updateKnowledgeSourceDefaultName(final Graph<?, Node> graph,
                                                         final KnowledgeSource knowledgeSource) {
        final String prefix = knowledgeSource.getClass().getSimpleName() + "-";
        knowledgeSource.getName().setValue(prefix + getMaxUnusedIndex(getExistingNodeNames(graph,
                                                                                           knowledgeSource,
                                                                                           (n) -> n.getName().getValue()),
                                                                      prefix));
    }

    private static void updateTextAnnotationDefaultText(final Graph<?, Node> graph,
                                                        final TextAnnotation textAnnotation) {
        final String prefix = textAnnotation.getClass().getSimpleName() + "-";
        textAnnotation.getText().setValue(prefix + getMaxUnusedIndex(getExistingNodeNames(graph,
                                                                                          textAnnotation,
                                                                                          (n) -> n.getText().getValue()),
                                                                     prefix));
    }

    private static void updateDecisionServiceDefaultName(final Graph<?, Node> graph,
                                                         final DecisionService decisionService) {
        final String prefix = decisionService.getClass().getSimpleName() + "-";
        decisionService.getName().setValue(prefix + getMaxUnusedIndex(getExistingNodeNames(graph,
                                                                                           decisionService,
                                                                                           (n) -> n.getName().getValue()),
                                                                      prefix));
    }

    @SuppressWarnings("unchecked")
    private static <T extends DMNModelInstrumentedBase> List<String> getExistingNodeNames(final Graph<?, Node> graph,
                                                                                          final T dmnModel,
                                                                                          final Function<T, String> nameExtractor) {
        return StreamSupport
                .stream(graph.nodes().spliterator(), false)
                .filter(node -> node.getContent() instanceof View)
                .map(node -> (View) node.getContent())
                .filter(view -> view.getDefinition() instanceof DMNModelInstrumentedBase)
                .map(view -> (T) view.getDefinition())
                .filter(dmn -> dmn.getClass().equals(dmnModel.getClass()))
                .map(nameExtractor::apply)
                .collect(Collectors.toList());
    }

    private static boolean isNameAlreadySet(final NamedElement element) {
        return element.getName() != null
                && !StringUtils.isEmpty(element.getName().getValue());
    }
}
