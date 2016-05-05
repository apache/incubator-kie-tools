/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.client.actions;

import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.resources.client.ImageResource;

public class StencilActionBuilder {

    private static final int HEIGHT_BOUNDING = 20;
    private static final int WIDTH_BOUNDING = 20;
    private static final int HEIGHT_PICTURE = 16;
    private static final int WIDTH_PICTURE = 16;

    public ActionShape build( final NodeMouseClickHandler clickHandler,
                              final ImageResource img ) {
        final Rectangle bounding = getBoundingImage( clickHandler );
        final Picture icon = new Picture( img,
                                          WIDTH_PICTURE,
                                          HEIGHT_PICTURE,
                                          false );

        final ActionShape shape = new ActionShape();
        shape.setPicture( icon );
        shape.setBounding( bounding );
        return shape;
    }

    private Rectangle getBoundingImage( final NodeMouseClickHandler clickHandler ) {
        final Rectangle bounding = new Rectangle( WIDTH_BOUNDING,
                                                  HEIGHT_BOUNDING ).setX( 0 ).setY( 0 ).setStrokeColor( ColorName.WHITE.getValue() );
        bounding.addNodeMouseClickHandler( clickHandler );
        return bounding;
    }

}
