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

package org.kie.workbench.common.stunner.shapes.client.util;

import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoUtils;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;

public class BasicShapesUtils {

    public static void scalePicture( final Picture picture,
                                     final double width,
                                     final double height ) {
        final BoundingBox bb = picture.getBoundingBox();
        final double[] scale = LienzoUtils.getScaleFactor( bb.getWidth(),
                                                           bb.getHeight(),
                                                           width,
                                                           height );
        picture.setScale( scale[ 0 ],
                          scale[ 1 ] );
    }

    public static WiresLayoutContainer.Layout getWiresLayout( final HasChildren.Layout layout ) {
        switch ( layout ) {
            case CENTER:
                return WiresLayoutContainer.Layout.CENTER;
            case LEFT:
                return WiresLayoutContainer.Layout.LEFT;
            case RIGHT:
                return WiresLayoutContainer.Layout.RIGHT;
            case TOP:
                return WiresLayoutContainer.Layout.TOP;
            case BOTTOM:
                return WiresLayoutContainer.Layout.BOTTOM;
        }
        throw new UnsupportedOperationException( "Unsupported layout [" + layout.name() + "]" );
    }
}
