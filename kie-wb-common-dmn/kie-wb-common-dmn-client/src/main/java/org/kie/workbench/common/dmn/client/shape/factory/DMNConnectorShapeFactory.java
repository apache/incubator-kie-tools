/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.shape.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.client.shape.DMNConnectorShape;
import org.kie.workbench.common.dmn.client.shape.def.DMNConnectorShapeDef;
import org.kie.workbench.common.dmn.client.shape.def.DMNShapeDef;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresConnectorViewExt;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;

@ApplicationScoped
public class DMNConnectorShapeFactory implements ShapeDefFactory<DMNDefinition, DMNShapeDef, Shape> {

    private final Map<Class<? extends DMNDefinition>, Function<Double[], WiresConnectorViewExt>> VIEW_FACTORIES =
            new HashMap<Class<? extends DMNDefinition>, Function<Double[], WiresConnectorViewExt>>() {{
                put(Association.class,
                    points -> getDMNConnectorShapeViewFactory()
                            .association(points[0],
                                         points[1],
                                         points[2],
                                         points[3]));
                put(InformationRequirement.class,
                    points -> getDMNConnectorShapeViewFactory()
                            .informationRequirement(points[0],
                                                    points[1],
                                                    points[2],
                                                    points[3]));
                put(KnowledgeRequirement.class,
                    points -> getDMNConnectorShapeViewFactory()
                            .knowledgeRequirement(points[0],
                                                  points[1],
                                                  points[2],
                                                  points[3]));
                put(AuthorityRequirement.class,
                    points -> getDMNConnectorShapeViewFactory()
                            .authorityRequirement(points[0],
                                                  points[1],
                                                  points[2],
                                                  points[3]));
            }};

    private final DMNConnectorShapeViewFactory dmnConnectorShapeViewFactory;

    protected DMNConnectorShapeFactory() {
        this(null);
    }

    @Inject
    public DMNConnectorShapeFactory(final DMNConnectorShapeViewFactory dmnConnectorShapeViewFactory) {
        this.dmnConnectorShapeViewFactory = dmnConnectorShapeViewFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Shape newShape(final DMNDefinition instance,
                          final DMNShapeDef shapeDef) {
        final DMNConnectorShapeDef dmnShapeDef = (DMNConnectorShapeDef) shapeDef;
        final WiresConnectorViewExt view = VIEW_FACTORIES.get(instance.getClass()).apply(new Double[]{0d, 0d, 100d, 100d});
        return new DMNConnectorShape(dmnShapeDef, view);
    }

    public DMNConnectorShapeViewFactory getDMNConnectorShapeViewFactory() {
        return dmnConnectorShapeViewFactory;
    }
}
