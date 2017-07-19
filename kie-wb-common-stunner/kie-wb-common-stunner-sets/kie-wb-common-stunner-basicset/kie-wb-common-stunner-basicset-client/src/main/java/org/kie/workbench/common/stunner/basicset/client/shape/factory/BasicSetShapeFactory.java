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

package org.kie.workbench.common.stunner.basicset.client.shape.factory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.basicset.definition.BasicConnector;
import org.kie.workbench.common.stunner.basicset.definition.BasicSetDefinition;
import org.kie.workbench.common.stunner.basicset.definition.Circle;
import org.kie.workbench.common.stunner.basicset.definition.Polygon;
import org.kie.workbench.common.stunner.basicset.definition.Rectangle;
import org.kie.workbench.common.stunner.basicset.definition.Ring;
import org.kie.workbench.common.stunner.basicset.shape.def.BasicConnectorDefImpl;
import org.kie.workbench.common.stunner.basicset.shape.def.CircleShapeDefImpl;
import org.kie.workbench.common.stunner.basicset.shape.def.PolygonShapeDefImpl;
import org.kie.workbench.common.stunner.basicset.shape.def.RectangleShapeDefImpl;
import org.kie.workbench.common.stunner.basicset.shape.def.RingShapeDefImpl;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;

@Dependent
public class BasicSetShapeFactory implements ShapeFactory<BasicSetDefinition, Shape> {

    private final BasicShapesFactory basicShapesFactory;
    private final DelegateShapeFactory<BasicSetDefinition, Shape> delegateShapeFactory;

    @Inject
    public BasicSetShapeFactory(final BasicShapesFactory basicShapesFactory,
                                final DelegateShapeFactory<BasicSetDefinition, Shape> delegateShapeFactory) {
        this.basicShapesFactory = basicShapesFactory;
        this.delegateShapeFactory = delegateShapeFactory;
    }

    @PostConstruct
    public void init() {
        delegateShapeFactory
                .delegate(Ring.class,
                          new RingShapeDefImpl(),
                          () -> basicShapesFactory)
                .delegate(Circle.class,
                          new CircleShapeDefImpl(),
                          () -> basicShapesFactory)
                .delegate(Rectangle.class,
                          new RectangleShapeDefImpl(),
                          () -> basicShapesFactory)
                .delegate(Polygon.class,
                          new PolygonShapeDefImpl(),
                          () -> basicShapesFactory)
                .delegate(BasicConnector.class,
                          new BasicConnectorDefImpl(),
                          () -> basicShapesFactory);
    }

    @Override
    public Shape newShape(final BasicSetDefinition definition) {
        return delegateShapeFactory.newShape(definition);
    }

    @Override
    public Glyph getGlyph(final String definitionId) {
        return delegateShapeFactory.getGlyph(definitionId);
    }
}
