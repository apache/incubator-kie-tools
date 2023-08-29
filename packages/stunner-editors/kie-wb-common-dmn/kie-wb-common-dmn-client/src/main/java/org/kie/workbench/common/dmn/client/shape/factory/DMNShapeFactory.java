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
package org.kie.workbench.common.dmn.client.shape.factory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.client.shape.def.DMNConnectorShapeDefImpl;
import org.kie.workbench.common.dmn.client.shape.def.DMNDecisionServiceSVGShapeDefImpl;
import org.kie.workbench.common.dmn.client.shape.def.DMNSVGShapeDefImpl;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;

@Dependent
public class DMNShapeFactory implements ShapeFactory<DMNDefinition, Shape> {

    private final SVGShapeFactory svgShapeFactory;
    private final DMNConnectorShapeFactory dmnConnectorShapeFactory;
    private final DMNDecisionServiceShapeFactory dmnDecisionServiceShapeFactory;
    private final DelegateShapeFactory<DMNDefinition, Shape> delegateShapeFactory;

    @Inject
    public DMNShapeFactory(final SVGShapeFactory svgShapeFactory,
                           final DMNConnectorShapeFactory dmnConnectorShapeFactory,
                           final DMNDecisionServiceShapeFactory dmnDecisionServiceShapeFactory,
                           final DelegateShapeFactory<DMNDefinition, Shape> delegateShapeFactory) {
        this.svgShapeFactory = svgShapeFactory;
        this.dmnConnectorShapeFactory = dmnConnectorShapeFactory;
        this.dmnDecisionServiceShapeFactory = dmnDecisionServiceShapeFactory;
        this.delegateShapeFactory = delegateShapeFactory;
    }

    @PostConstruct
    public void init() {
        delegateShapeFactory
                .delegate(DMNDiagram.class,
                          new DMNSVGShapeDefImpl(),
                          () -> svgShapeFactory)
                .delegate(InputData.class,
                          new DMNSVGShapeDefImpl(),
                          () -> svgShapeFactory)
                .delegate(KnowledgeSource.class,
                          new DMNSVGShapeDefImpl(),
                          () -> svgShapeFactory)
                .delegate(BusinessKnowledgeModel.class,
                          new DMNSVGShapeDefImpl(),
                          () -> svgShapeFactory)
                .delegate(Decision.class,
                          new DMNSVGShapeDefImpl(),
                          () -> svgShapeFactory)
                .delegate(TextAnnotation.class,
                          new DMNSVGShapeDefImpl(),
                          () -> svgShapeFactory)
                .delegate(Association.class,
                          new DMNConnectorShapeDefImpl(),
                          () -> dmnConnectorShapeFactory)
                .delegate(AuthorityRequirement.class,
                          new DMNConnectorShapeDefImpl(),
                          () -> dmnConnectorShapeFactory)
                .delegate(InformationRequirement.class,
                          new DMNConnectorShapeDefImpl(),
                          () -> dmnConnectorShapeFactory)
                .delegate(KnowledgeRequirement.class,
                          new DMNConnectorShapeDefImpl(),
                          () -> dmnConnectorShapeFactory)
                .delegate(DecisionService.class,
                          new DMNDecisionServiceSVGShapeDefImpl(),
                          () -> dmnDecisionServiceShapeFactory);
    }

    @Override
    public Shape newShape(final DMNDefinition definition) {
        return delegateShapeFactory.newShape(definition);
    }

    @Override
    public Glyph getGlyph(final String definitionId) {
        return delegateShapeFactory.getGlyph(definitionId);
    }

    @Override
    public Glyph getGlyph(final String definitionId,
                          final Class<? extends GlyphConsumer> consumer) {
        return delegateShapeFactory.getGlyph(definitionId, consumer);
    }
}
