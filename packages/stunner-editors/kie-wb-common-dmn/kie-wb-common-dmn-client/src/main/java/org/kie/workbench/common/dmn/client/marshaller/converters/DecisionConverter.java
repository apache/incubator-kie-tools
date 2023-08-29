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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntry;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAuthorityRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getRawId;
import static org.kie.workbench.common.dmn.client.marshaller.converters.HrefBuilder.getHref;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class DecisionConverter implements NodeConverter<JSITDecision, Decision> {

    private FactoryManager factoryManager;

    public DecisionConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<Decision>, ?> nodeFromDMN(final NodeEntry nodeEntry) {

        final JSITDecision dmn = Js.uncheckedCast(nodeEntry.getDmnElement());

        @SuppressWarnings("unchecked")
        final Node<View<Decision>, ?> node = (Node<View<Decision>, ?>) factoryManager.newElement(nodeEntry.getId(),
                                                                                                 getDefinitionId(Decision.class)).asNode();
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Name name = new Name(dmn.getName());
        final InformationItemPrimary informationItem = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn.getVariable(), dmn);

        Expression expression = null;
        final JSITExpression jsiWrapped = dmn.getExpression();
        if (Objects.nonNull(jsiWrapped)) {
            final JSITExpression jsiExpression = Js.uncheckedCast(JsUtils.getUnwrappedElement(jsiWrapped));
            expression = ExpressionPropertyConverter.wbFromDMN(jsiExpression,
                                                               Js.uncheckedCast(dmn),
                                                               nodeEntry.getComponentWidthsConsumer());
        }

        final Decision decision = new Decision(id,
                                               description,
                                               name,
                                               new Question(),
                                               new AllowedAnswers(),
                                               informationItem,
                                               expression,
                                               new StylingSet(),
                                               new GeneralRectangleDimensionsSet());
        decision.setDiagramId(nodeEntry.getDiagramId());
        decision.setQuestion(QuestionPropertyConverter.wbFromDMN(dmn.getQuestion()));
        decision.setAllowedAnswers(AllowedAnswersPropertyConverter.wbFromDMN(dmn.getAllowedAnswers()));
        node.getContent().setDefinition(decision);

        if (Objects.nonNull(informationItem)) {
            informationItem.setParent(decision);
        }
        if (Objects.nonNull(expression)) {
            expression.setParent(decision);
        }

        DMNExternalLinksToExtensionElements.loadExternalLinksFromExtensionElements(dmn, decision);
        return node;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSITDecision dmnFromNode(final Node<View<Decision>, ?> node,
                                    final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final Decision source = (Decision) DefinitionUtils.getElementDefinition(node);
        final JSITDecision d = JSITDecision.newInstance();
        d.setId(source.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        description.ifPresent(d::setDescription);
        d.setName(source.getName().getValue());
        final JSITInformationItem variable = InformationItemPrimaryPropertyConverter.dmnFromWB(source.getVariable(), source);
        d.setVariable(variable);
        final JSITExpression expression = ExpressionPropertyConverter.dmnFromWB(source.getExpression(), componentWidthsConsumer);
        d.setExpression(expression);

        final String question = QuestionPropertyConverter.dmnFromWB(source.getQuestion());
        if (!StringUtils.isEmpty(question)) {
            d.setQuestion(question);
        }
        final String allowedAnswers = AllowedAnswersPropertyConverter.dmnFromWB(source.getAllowedAnswers());
        if (!StringUtils.isEmpty(allowedAnswers)) {
            d.setAllowedAnswers(allowedAnswers);
        }
        // Add because it is present in the original JSON when unmarshalling
        if (Objects.isNull(d.getInformationRequirement())) {
            d.setInformationRequirement(new ArrayList<>());
        }
        // Add because it is present in the original JSON when unmarshalling
        if (Objects.isNull(d.getKnowledgeRequirement())) {
            d.setKnowledgeRequirement(new ArrayList<>());
        }
        // Add because it is present in the original JSON when unmarshalling
        if (Objects.isNull(d.getAuthorityRequirement())) {
            d.setAuthorityRequirement(new ArrayList<>());
        }

        // DMN spec table 2: Requirements connection rules
        final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            final Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (drgElement instanceof Decision) {
                        final JSITInformationRequirement iReq = JSITInformationRequirement.newInstance();
                        iReq.setId(getRawId(e.getUUID()));
                        final JSITDMNElementReference ri = JSITDMNElementReference.newInstance();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredDecision(ri);
                        d.addInformationRequirement(iReq);
                    } else if (drgElement instanceof BusinessKnowledgeModel) {
                        final JSITKnowledgeRequirement iReq = JSITKnowledgeRequirement.newInstance();
                        iReq.setId(getRawId(e.getUUID()));
                        final JSITDMNElementReference ri = JSITDMNElementReference.newInstance();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredKnowledge(ri);
                        d.addKnowledgeRequirement(iReq);
                    } else if (drgElement instanceof KnowledgeSource) {
                        final JSITAuthorityRequirement iReq = JSITAuthorityRequirement.newInstance();
                        iReq.setId(getRawId(e.getUUID()));
                        final JSITDMNElementReference ri = JSITDMNElementReference.newInstance();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredAuthority(ri);
                        d.addAuthorityRequirement(iReq);
                    } else if (drgElement instanceof InputData) {
                        final JSITInformationRequirement iReq = JSITInformationRequirement.newInstance();
                        iReq.setId(getRawId(e.getUUID()));
                        final JSITDMNElementReference ri = JSITDMNElementReference.newInstance();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredInput(ri);
                        d.addInformationRequirement(iReq);
                    } else if (drgElement instanceof DecisionService) {
                        if (e.getContent() instanceof Child) {
                            // Stunner relationship of this Decision be encapsulated by the DecisionService, not managed here.
                        } else if (e.getContent() instanceof View && ((View) e.getContent()).getDefinition() instanceof KnowledgeRequirement) {
                            final JSITKnowledgeRequirement iReq = JSITKnowledgeRequirement.newInstance();
                            iReq.setId(getRawId(e.getUUID()));
                            final JSITDMNElementReference ri = JSITDMNElementReference.newInstance();
                            ri.setHref(getHref(drgElement));
                            iReq.setRequiredKnowledge(ri);
                            d.addKnowledgeRequirement(iReq);
                        } else {
                            throw new UnsupportedOperationException("wrong model definition.");
                        }
                    } else {
                        throw new UnsupportedOperationException("wrong model definition.");
                    }
                }
            }
        }

        DMNExternalLinksToExtensionElements.loadExternalLinksIntoExtensionElements(source, d);

        return d;
    }
}
