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

package org.kie.workbench.common.stunner.shapes.def;

import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;

public interface BasicShapeWithTitleDef<W>
        extends BasicShapeDef<W> {

    String getNamePropertyValue( final W element );

    String getFontFamily( final W element );

    String getFontColor( final W element );

    double getFontSize( final W element );

    double getFontBorderSize( final W element );

    HasTitle.Position getFontPosition( final W element );

    /**
     * The rotation value in degree units.
     */
    double getFontRotation( final W element );
}
