/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.shape.factory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.client.shape.AssociationShape;
import org.kie.workbench.common.dmn.client.shape.AuthorityRequirementShape;
import org.kie.workbench.common.dmn.client.shape.InformationRequirementShape;
import org.kie.workbench.common.dmn.client.shape.KnowledgeRequirementShape;
import org.kie.workbench.common.dmn.client.shape.def.AssociationShapeDef;
import org.kie.workbench.common.dmn.client.shape.def.AuthorityRequirementShapeDef;
import org.kie.workbench.common.dmn.client.shape.def.DMNShapeDef;
import org.kie.workbench.common.dmn.client.shape.def.InformationRequirementShapeDef;
import org.kie.workbench.common.dmn.client.shape.def.KnowledgeRequirementShapeDef;
import org.kie.workbench.common.dmn.client.shape.view.AssociationView;
import org.kie.workbench.common.dmn.client.shape.view.AuthorityRequirementView;
import org.kie.workbench.common.dmn.client.shape.view.InformationRequirementView;
import org.kie.workbench.common.dmn.client.shape.view.KnowledgeRequirementView;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

@ApplicationScoped
public class DMNConnectorShapeFactory implements ShapeDefFactory<DMNDefinition, DMNShapeDef, Shape> {

    private final DMNConnectorShapeViewFactory dmnConnectorShapeViewFactory;
    private final ShapeDefFunctionalFactory<DMNDefinition, DMNShapeDef, Shape> functionalFactory;

    protected DMNConnectorShapeFactory() {
        this(null,
             null);
    }

    @Inject
    public DMNConnectorShapeFactory(final DMNConnectorShapeViewFactory dmnConnectorShapeViewFactory,
                                    final ShapeDefFunctionalFactory<DMNDefinition, DMNShapeDef, Shape> functionalFactory) {
        this.dmnConnectorShapeViewFactory = dmnConnectorShapeViewFactory;
        this.functionalFactory = functionalFactory;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        // Register shape instance builders.
        functionalFactory
                .set(AssociationShapeDef.class,
                     this::newAssociationShape)
                .set(InformationRequirementShapeDef.class,
                     this::newInformationRequirementShape)
                .set(KnowledgeRequirementShapeDef.class,
                     this::newKnowledgeRequirementShape)
                .set(AuthorityRequirementShapeDef.class,
                     this::newAuthorityRequirementShape);
    }

    private Shape newAssociationShape(final Object instance,
                                      final ShapeDef shapeDef) {
        final AssociationShapeDef dmnShapeDef = (AssociationShapeDef) shapeDef;
        final AssociationView view = dmnConnectorShapeViewFactory.association(0,
                                                                              0,
                                                                              100,
                                                                              100);
        return new AssociationShape(dmnShapeDef,
                                    view);
    }

    private Shape newInformationRequirementShape(final Object instance,
                                                 final ShapeDef shapeDef) {
        final InformationRequirementShapeDef dmnShapeDef = (InformationRequirementShapeDef) shapeDef;
        final InformationRequirementView view = dmnConnectorShapeViewFactory.informationRequirement(0,
                                                                                                    0,
                                                                                                    100,
                                                                                                    100);
        return new InformationRequirementShape(dmnShapeDef,
                                               view);
    }

    private Shape newKnowledgeRequirementShape(final Object instance,
                                               final ShapeDef shapeDef) {
        final KnowledgeRequirementShapeDef dmnShapeDef = (KnowledgeRequirementShapeDef) shapeDef;
        final KnowledgeRequirementView view = dmnConnectorShapeViewFactory.knowledgeRequirement(0,
                                                                                                0,
                                                                                                100,
                                                                                                100);
        return new KnowledgeRequirementShape(dmnShapeDef,
                                             view);
    }

    private Shape newAuthorityRequirementShape(final Object instance,
                                               final ShapeDef shapeDef) {
        final AuthorityRequirementShapeDef dmnShapeDef = (AuthorityRequirementShapeDef) shapeDef;
        final AuthorityRequirementView view = dmnConnectorShapeViewFactory.authorityRequirement(0,
                                                                                                0,
                                                                                                100,
                                                                                                100);
        return new AuthorityRequirementShape(dmnShapeDef,
                                             view);
    }

    @Override
    public Shape newShape(final DMNDefinition instance,
                          final DMNShapeDef shapeDef) {
        return functionalFactory.newShape(instance,
                                          shapeDef);
    }
}
