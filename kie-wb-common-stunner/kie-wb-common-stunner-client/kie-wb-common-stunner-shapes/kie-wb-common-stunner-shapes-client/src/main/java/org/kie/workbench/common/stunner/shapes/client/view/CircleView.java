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

import com.ait.lienzo.client.core.shape.MultiPath;
import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;

public class CircleView extends BasicShapeView<CircleView>
        implements HasRadius<CircleView> {

    public CircleView( final double radius ) {
        super( create( new MultiPath(), radius ) );
    }

    @Override
    public CircleView setRadius( final double radius ) {
        create( getPath().clear(), radius );
        updateFillGradient( radius * 2, radius * 2 );
        refresh();
        return this;

    }

    private static MultiPath create( final MultiPath path,
                                     final double radius ) {
        return path.M( radius, 0 ).circle( radius );
    }

}
