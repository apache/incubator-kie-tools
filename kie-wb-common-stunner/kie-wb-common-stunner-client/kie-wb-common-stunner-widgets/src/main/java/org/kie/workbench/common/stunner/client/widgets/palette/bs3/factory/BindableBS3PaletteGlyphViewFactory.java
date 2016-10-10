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

package org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.client.ShapeManager;

public abstract class BindableBS3PaletteGlyphViewFactory<V extends IsWidget> extends BindableBS3PaletteViewFactory<V> {

    private final BS3PaletteGlyphViewFactory glyphViewFactory;

    public BindableBS3PaletteGlyphViewFactory( final ShapeManager shapeManager ) {
        this.glyphViewFactory = new BS3PaletteGlyphViewFactory( shapeManager );
    }

    @Override
    public IsWidget getDefinitionView( final String defId,
                                       final int width,
                                       final int height ) {
        final IsWidget view = super.getDefinitionView( defId, width, height );
        if ( null != view ) {
            return view;

        } else {
            return glyphViewFactory.getDefinitionView( defId, width, height );
        }

    }

}
