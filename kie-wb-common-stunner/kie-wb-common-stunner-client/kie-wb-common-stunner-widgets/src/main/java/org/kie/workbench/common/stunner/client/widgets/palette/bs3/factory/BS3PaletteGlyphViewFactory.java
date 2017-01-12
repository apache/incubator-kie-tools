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

package org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.validation.client.impl.Group;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoPanelUtils;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

class BS3PaletteGlyphViewFactory implements BS3PaletteViewFactory {

    private final ShapeManager shapeManager;

    BS3PaletteGlyphViewFactory( final ShapeManager shapeManager ) {
        this.shapeManager = shapeManager;
    }

    @Override
    public boolean accepts( final String id ) {
        return true;
    }

    @Override
    public IsWidget getCategoryView( final String defSetId,
                                     final String categoryId,
                                     final int width,
                                     final int height ) {
        return null;
    }

    @Override
    public IsWidget getDefinitionView( final String defSetId,
                                       final String defId,
                                       final int width,
                                       final int height ) {
        final Glyph<Group> glyph = getGlyph( defSetId,
                                             defId,
                                             width,
                                             height );
        return LienzoPanelUtils.newPanel( glyph,
                                          width,
                                          height );
    }

    @SuppressWarnings( "unchecked" )
    private Glyph<Group> getGlyph( final String defSetId,
                                   final String id,
                                   final int width,
                                   final int height ) {
        return shapeManager.getDefaultShapeSet( defSetId ).getShapeFactory().glyph( id,
                                                                                    width,
                                                                                    height );
    }
}
