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

package org.kie.workbench.common.dmn.client.editors.included.imports.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.IsLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.util.StringUtils;

@Dependent
public class PMMLIncludedModelHandler implements DRGElementHandler {

    private final DMNGraphUtils dmnGraphUtils;

    private final ExpressionGridCache expressionGridCache;

    @Inject
    public PMMLIncludedModelHandler(final DMNGraphUtils dmnGraphUtils,
                                    final SessionManager sessionManager) {
        this.dmnGraphUtils = dmnGraphUtils;
        this.expressionGridCache = ((DMNSession) sessionManager.getCurrentSession()).getExpressionGridCache();
    }

    @Override
    public void update(final String oldModelName,
                       final String newModelName) {
        final List<Decision> decisions = getDecisions();
        final List<BusinessKnowledgeModel> businessKnowledgeModels = getBusinessKnowledgeModels();
        final List<FunctionDefinition> functions = getPMMLFunctionDefinitions(decisions, businessKnowledgeModels);
        final Collection<Context> contexts = getPMMLContexts(functions).values();

        //The values in the DMN model are stored with quotes
        final String quotedOldModelName = StringUtils.createQuotedString(oldModelName);
        final String quotedNewModelName = StringUtils.createQuotedString(newModelName);

        contexts.stream()
                .map(Context::getContextEntry)
                .flatMap(Collection::stream)
                .filter(ce -> Objects.equals(LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT, ce.getVariable().getName().getValue()))
                .filter(ce -> ce.getExpression() instanceof IsLiteralExpression)
                .map(ce -> (IsLiteralExpression) ce.getExpression())
                .filter(ile -> Objects.nonNull(ile.getText()))
                .map(IsLiteralExpression::getText)
                .filter(text -> Objects.equals(quotedOldModelName, text.getValue()))
                .forEach(text -> text.setValue(quotedNewModelName));

        //Refresh cached grids from the DMN model
        refreshCachedExpressionGrids(decisions, businessKnowledgeModels);
    }

    @Override
    public void destroy(final String oldModelName) {
        final List<Decision> decisions = getDecisions();
        final List<BusinessKnowledgeModel> businessKnowledgeModels = getBusinessKnowledgeModels();
        final List<FunctionDefinition> functions = getPMMLFunctionDefinitions(decisions, businessKnowledgeModels);
        final Map<FunctionDefinition, Context> contexts = getPMMLContexts(functions);

        //The values in the DMN model are stored with quotes
        final String quotedOldModelName = StringUtils.createQuotedString(oldModelName);

        for (final Map.Entry<FunctionDefinition, Context> entry : contexts.entrySet()) {
            final Context context = entry.getValue();
            for (final ContextEntry contextEntry : context.getContextEntry()) {
                if (Objects.equals(LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT, contextEntry.getVariable().getName().getValue())) {
                    final Expression expression = contextEntry.getExpression();
                    if (expression instanceof IsLiteralExpression) {
                        final IsLiteralExpression ile = (IsLiteralExpression) expression;
                        if (Objects.nonNull(ile.getText())) {
                            final Text text = ile.getText();
                            if (Objects.equals(quotedOldModelName, text.getValue())) {
                                clearContextValues(context);
                                entry.getKey().getFormalParameter().clear();
                            }
                        }
                    }
                }
            }
        }

        //Refresh cached grids from the DMN model
        refreshCachedExpressionGrids(decisions, businessKnowledgeModels);
    }

    private List<Decision> getDecisions() {
        return dmnGraphUtils
                .getModelDRGElements()
                .stream()
                .filter(e -> e instanceof Decision)
                .map(e -> (Decision) e)
                .collect(Collectors.toList());
    }

    private List<BusinessKnowledgeModel> getBusinessKnowledgeModels() {
        return dmnGraphUtils
                .getModelDRGElements()
                .stream()
                .filter(e -> e instanceof BusinessKnowledgeModel)
                .map(e -> (BusinessKnowledgeModel) e)
                .collect(Collectors.toList());
    }

    private List<FunctionDefinition> getPMMLFunctionDefinitions(final List<Decision> decisions,
                                                                final List<BusinessKnowledgeModel> businessKnowledgeModels) {
        final List<FunctionDefinition> functions = new ArrayList<>();
        functions.addAll(decisions.stream()
                                 .map(d -> extractPMMLFunctionsFromExpression(d.getExpression()))
                                 .flatMap(Collection::stream)
                                 .collect(Collectors.toList()));
        functions.addAll(businessKnowledgeModels.stream()
                                 .map(bkm -> extractPMMLFunctionsFromExpression(bkm.getEncapsulatedLogic()))
                                 .flatMap(Collection::stream)
                                 .collect(Collectors.toList()));
        return functions;
    }

    private List<FunctionDefinition> extractPMMLFunctionsFromExpression(final Expression expression) {
        if (expression instanceof FunctionDefinition) {
            final FunctionDefinition function = (FunctionDefinition) expression;
            if (Objects.equals(function.getKind(), FunctionDefinition.Kind.PMML)) {
                return Collections.singletonList(function);
            } else {
                return extractPMMLFunctionsFromExpression(function.getExpression());
            }
        } else if (expression instanceof Context) {
            final Context context = (Context) expression;
            return context.getContextEntry().stream()
                    .map(ce -> extractPMMLFunctionsFromExpression(ce.getExpression()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Map<FunctionDefinition, Context> getPMMLContexts(final List<FunctionDefinition> functions) {
        return functions.stream()
                .filter(f -> f.getExpression() instanceof Context)
                .collect(Collectors.toMap(f -> f, f -> (Context) f.getExpression()));
    }

    private void clearContextValues(final Context context) {
        context.getContextEntry()
                .stream()
                .filter(ce -> ce.getExpression() instanceof IsLiteralExpression)
                .map(ce -> (IsLiteralExpression) ce.getExpression())
                .filter(ile -> Objects.nonNull(ile.getText()))
                .map(IsLiteralExpression::getText)
                .forEach(text -> text.setValue(""));
    }

    private void refreshCachedExpressionGrids(final List<Decision> decisions,
                                              final List<BusinessKnowledgeModel> businessKnowledgeModels) {
        final List<String> nodeUUIDs = new ArrayList<>();
        nodeUUIDs.addAll(decisions.stream().map(d -> d.getId().getValue()).collect(Collectors.toList()));
        nodeUUIDs.addAll(businessKnowledgeModels.stream().map(d -> d.getId().getValue()).collect(Collectors.toList()));
        nodeUUIDs.stream()
                .map(expressionGridCache::getExpressionGrid)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(BaseExpressionGrid::initialiseUiCells);
    }
}
