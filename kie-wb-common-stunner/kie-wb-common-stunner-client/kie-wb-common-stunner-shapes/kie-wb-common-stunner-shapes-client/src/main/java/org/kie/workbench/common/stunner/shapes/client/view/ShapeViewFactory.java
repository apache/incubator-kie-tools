/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.shapes.client.view;

import org.kie.workbench.common.stunner.shapes.client.view.icon.dynamics.DynamicIconShapeView;
import org.kie.workbench.common.stunner.shapes.client.view.icon.statics.StaticIconShapeView;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.Icons;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ShapeViewFactory {

    public RectangleView rectangle( final double width,
                                    final double height ) {
        return new RectangleView( width, height );

    }

    public DynamicIconShapeView dynamicIcon( final Icons icon,
                                             final double width,
                                             final double height ) {
        return new DynamicIconShapeView( icon, width, height );

    }

    public StaticIconShapeView staticIcon( final org.kie.workbench.common.stunner.shapes.def.icon.statics.Icons icon ) {
        return new StaticIconShapeView( icon );

    }

    public CircleView circle( final double radius ) {
        return new CircleView( radius );

    }

    public RingView ring( final double outer ) {
        return new RingView( outer );

    }

    public PolygonView polygon( final double radius,
                                final String fillColor ) {
        return new PolygonView( radius, fillColor );

    }

    public ConnectorView connector( final double... points ) {
        return new ConnectorView( points );

    }

}
