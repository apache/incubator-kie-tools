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

package org.kie.workbench.common.stunner.client.lienzo.components.palette.view.element;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;

// TODO: Review alphas workaround.
public final class LienzoSeparatorPaletteElementViewImpl
        implements LienzoSeparatorPaletteElementView {

    private final Rectangle view;

    public LienzoSeparatorPaletteElementViewImpl( final double height,
                                                  final double width ) {
        this.view = new Rectangle( width, height ).setAlpha( 0.1d ).setStrokeAlpha( 0 - 1d );
    }

    @Override
    public IPrimitive<?> getView() {
        return view;
    }

}
