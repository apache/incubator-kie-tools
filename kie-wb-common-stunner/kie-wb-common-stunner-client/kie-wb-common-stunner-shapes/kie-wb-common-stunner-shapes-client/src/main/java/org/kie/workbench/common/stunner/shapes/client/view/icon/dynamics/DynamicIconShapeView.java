/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.shapes.client.view.icon.dynamics;

import com.ait.lienzo.client.core.shape.MultiPath;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.shapes.client.view.BasicShapeView;
import org.kie.workbench.common.stunner.shapes.client.view.BasicShapesSupportedEvents;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.Icons;

public class DynamicIconShapeView<T extends DynamicIconShapeView>
        extends BasicShapeView<T>
        implements HasSize<T> {

    private final static String BLACK = "#000000";
    private final static double STROKE_WIDTH = 0;

    private Icons icon;
    private double width;
    private double height;

    public DynamicIconShapeView( final Icons icon,
                                 final double width,
                                 final double height ) {
        super( BasicShapesSupportedEvents.ALL_DESKTOP_EVENT_TYPES,
                buildIcon( new MultiPath(),
                        icon,
                        width,
                        height,
                        BLACK,
                        BLACK,
                        STROKE_WIDTH ) );
        this.icon = icon;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        icon = null;
        width = 0;
        height = 0;
    }

    @SuppressWarnings( "unchecked" )
    public T setIcon( final Icons icon ) {
        this.icon = icon;
        updateIcon();
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setSize( final double width,
                      final double height ) {
        this.width = width;
        this.height = height;
        updateIcon();
        return ( T ) this;
    }

    private void updateIcon() {
        buildIcon( getPath(),
                icon,
                width,
                height,
                getFillColor(),
                getStrokeColor(),
                getStrokeWidth() );

    }

    private static MultiPath buildIcon( final MultiPath path,
                                        final Icons icon,
                                        final double w,
                                        final double h,
                                        final String fillColor,
                                        final String strokeColor,
                                        final double strokeWidth ) {
        return DynamicIconsBuilder.build( path, icon, w, h )
                .setFillColor( fillColor )
                .setStrokeColor( strokeColor )
                .setStrokeWidth( strokeWidth );
    }

}
