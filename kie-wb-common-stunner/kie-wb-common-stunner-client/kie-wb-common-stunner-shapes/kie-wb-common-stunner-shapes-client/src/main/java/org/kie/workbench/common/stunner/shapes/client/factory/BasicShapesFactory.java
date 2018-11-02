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

package org.kie.workbench.common.stunner.shapes.client.factory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.shapes.client.BasicConnectorShape;
import org.kie.workbench.common.stunner.shapes.client.BasicContainerShape;
import org.kie.workbench.common.stunner.shapes.client.PictureShape;
import org.kie.workbench.common.stunner.shapes.client.RingShape;
import org.kie.workbench.common.stunner.shapes.client.view.AbstractConnectorView;
import org.kie.workbench.common.stunner.shapes.client.view.CircleView;
import org.kie.workbench.common.stunner.shapes.client.view.PictureShapeView;
import org.kie.workbench.common.stunner.shapes.client.view.PolygonView;
import org.kie.workbench.common.stunner.shapes.client.view.RectangleView;
import org.kie.workbench.common.stunner.shapes.client.view.RingView;
import org.kie.workbench.common.stunner.shapes.client.view.ShapeViewFactory;
import org.kie.workbench.common.stunner.shapes.def.BasicShapeDef;
import org.kie.workbench.common.stunner.shapes.def.CircleShapeDef;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;
import org.kie.workbench.common.stunner.shapes.def.PolygonShapeDef;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;
import org.kie.workbench.common.stunner.shapes.def.RingShapeDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureShapeDef;

@Dependent
public class BasicShapesFactory
        implements ShapeDefFactory<Object, BasicShapeDef<Object>, Shape<ShapeView>> {

    private final ShapeDefFunctionalFactory<Object, BasicShapeDef, Shape> functionalFactory;
    private final ShapeViewFactory shapeViewFactory;

    @Inject
    public BasicShapesFactory(ShapeDefFunctionalFactory<Object, BasicShapeDef, Shape> functionalFactory,
                              final ShapeViewFactory shapeViewFactory) {
        this.functionalFactory = functionalFactory;
        this.shapeViewFactory = shapeViewFactory;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        // Set shape builder functions for each shape definition supported.
        functionalFactory
                .set(CircleShapeDef.class,
                     this::newCircle)
                .set(RingShapeDef.class,
                     this::newRing)
                .set(RectangleShapeDef.class,
                     this::newRectangle)
                .set(PolygonShapeDef.class,
                     this::newPolygon)
                .set(ConnectorShapeDef.class,
                     this::newConnector)
                .set(PictureShapeDef.class,
                     this::newPicture);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Shape<ShapeView> newShape(final Object instance,
                                     final BasicShapeDef<Object> shapeDef) {
        return functionalFactory.newShape(instance,
                                          shapeDef);
    }

    @SuppressWarnings("unchecked")
    private Shape<ShapeView> newCircle(final Object instance,
                                       final BasicShapeDef shapeDef) {
        final CircleShapeDef circleShapeDef = (CircleShapeDef) shapeDef;
        final double radius = circleShapeDef.getRadius(instance);
        final CircleView view = shapeViewFactory.circle(radius);
        return new BasicContainerShape(circleShapeDef,
                                       view);
    }

    @SuppressWarnings("unchecked")
    private Shape<ShapeView> newRing(final Object instance,
                                     final BasicShapeDef shapeDef) {
        final RingShapeDef ringShapeDef = (RingShapeDef) shapeDef;
        final double oRadius = ringShapeDef.getOuterRadius(instance);
        final RingView view = shapeViewFactory.ring(oRadius);
        return new RingShape(ringShapeDef,
                             view);
    }

    @SuppressWarnings("unchecked")
    private Shape<ShapeView> newRectangle(final Object instance,
                                          final BasicShapeDef shapeDef) {
        final RectangleShapeDef rectShapeDef = (RectangleShapeDef) shapeDef;
        final double width = rectShapeDef.getWidth(instance);
        final double height = rectShapeDef.getHeight(instance);
        final double cr = rectShapeDef.getCornerRadius(instance);
        final RectangleView view = shapeViewFactory.rectangle(width,
                                                              height,
                                                              cr);
        return new BasicContainerShape(rectShapeDef,
                                       view);
    }

    @SuppressWarnings("unchecked")
    private Shape<ShapeView> newPolygon(final Object instance,
                                        final BasicShapeDef shapeDef) {
        final PolygonShapeDef polygonShapeDef = (PolygonShapeDef) shapeDef;
        final double radius = polygonShapeDef.getRadius(instance);
        final PolygonView view = shapeViewFactory.polygon(radius);
        return new BasicContainerShape(polygonShapeDef,
                                       view);
    }

    @SuppressWarnings("unchecked")
    private Shape<ShapeView> newConnector(final Object instance,
                                          final BasicShapeDef shapeDef) {
        final ConnectorShapeDef cShapeDef = (ConnectorShapeDef) shapeDef;
        final AbstractConnectorView view = shapeViewFactory.connector(0,
                                                                      0,
                                                                      100,
                                                                      100);
        view.setDashArray(cShapeDef.getDashArray(instance));
        return new BasicConnectorShape(cShapeDef,
                                       view);
    }

    @SuppressWarnings("unchecked")
    private Shape newPicture(final Object instance,
                             final BasicShapeDef shapeDef) {
        final PictureShapeDef pictShapeDef = (PictureShapeDef) shapeDef;
        final Object pictureSource = pictShapeDef.getPictureSource(instance);
        final double width = pictShapeDef.getWidth(instance);
        final double height = pictShapeDef.getHeight(instance);
        final PictureShapeView view = shapeViewFactory.picture(pictureSource,
                                                               width,
                                                               height);
        return new PictureShape(view);
    }
}
