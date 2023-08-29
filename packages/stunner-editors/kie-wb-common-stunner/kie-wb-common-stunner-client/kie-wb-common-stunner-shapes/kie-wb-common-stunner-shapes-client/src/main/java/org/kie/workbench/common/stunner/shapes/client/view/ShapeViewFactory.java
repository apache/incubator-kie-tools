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


package org.kie.workbench.common.stunner.shapes.client.view;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.shapes.client.factory.PictureProvidersManager;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

@Dependent
public class ShapeViewFactory {

    private final PictureProvidersManager pictureProvidersManager;

    protected ShapeViewFactory() {
        this(null);
    }

    @Inject
    public ShapeViewFactory(PictureProvidersManager pictureProvidersManager) {
        this.pictureProvidersManager = pictureProvidersManager;
    }

    public RectangleView rectangle(final double width,
                                   final double height,
                                   final double corner_radius) {
        return new RectangleView(width,
                                 height,
                                 corner_radius);
    }

    public PictureShapeView pictureFromUri(final SafeUri uri,
                                           final double width,
                                           final double height) {
        checkNotNull("uri", uri);
        return new PictureShapeView(uri.asString(),
                                    width,
                                    height);
    }

    public PictureShapeView picture(final Object source,
                                    final double width,
                                    final double height) {
        checkNotNull("source", source);
        final SafeUri uri = pictureProvidersManager.getUri(source);
        return new PictureShapeView(uri.asString(),
                                    width,
                                    height);
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public CircleView circle(final double radius) {
        return new CircleView(radius);
    }

    public RingView ring(final double outer) {
        return new RingView(outer);
    }

    public PolygonView polygon(final double radius) {
        return new PolygonView(radius);
    }

    public AbstractConnectorView connector(final ConnectorShapeDef.Direction direction, final double... points) {
        return new OrthogonalPolyLineConnectorView(direction, points);
    }
}
