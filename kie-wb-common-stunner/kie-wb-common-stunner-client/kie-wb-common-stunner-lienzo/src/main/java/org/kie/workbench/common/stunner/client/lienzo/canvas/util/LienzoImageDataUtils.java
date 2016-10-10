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

package org.kie.workbench.common.stunner.client.lienzo.canvas.util;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.widget.LienzoPanel;

public class LienzoImageDataUtils {

    public static String toImageData( final Layer layer,
                                      final int x,
                                      final int y,
                                      final int width,
                                      final int height ) {
        final ImageData imageData = layer.getContext().getImageData( x, y, width, height );
        if ( null != imageData ) {
            final LienzoPanel p = new LienzoPanel( width, height );
            final Layer l = new Layer().setTransformable( true );
            p.add( l );
            l.getContext().putImageData( imageData, 0, 0 );
            return l.toDataURL();

        }
        return null;

    }

}
