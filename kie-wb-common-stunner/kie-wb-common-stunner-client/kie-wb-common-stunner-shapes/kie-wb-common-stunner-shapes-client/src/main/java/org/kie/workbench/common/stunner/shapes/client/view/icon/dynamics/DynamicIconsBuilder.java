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
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.Icons;

public class DynamicIconsBuilder {

    public static MultiPath build( final MultiPath path,
                                   final Icons icon,
                                   final double w,
                                   final double h ) {
        return build( path,
                      icon,
                      0,
                      0,
                      w,
                      h );
    }

    public static MultiPath build( final MultiPath path,
                                   final Icons icon,
                                   final double x,
                                   final double y,
                                   final double w,
                                   final double h ) {
        path.clear();
        switch ( icon ) {
            case PLUS:
                path
                        .M( x,
                            y + ( h / 2 ) )
                        .L( x + w,
                            y + ( h / 2 ) )
                        .M( x + ( w / 2 ),
                            y )
                        .L( x + ( w / 2 ),
                            y + h );
                break;
            case MINUS:
                path
                        .M( x,
                            y + ( h / 2 ) )
                        .L( x + w,
                            y + ( h / 2 ) );
                break;
            case XOR:
                path
                        .M( x,
                            y )
                        .L( x + w,
                            y + h )
                        .M( x + w,
                            y )
                        .L( x,
                            y + h );
                break;
        }
        return path
                .Z();
    }
}
