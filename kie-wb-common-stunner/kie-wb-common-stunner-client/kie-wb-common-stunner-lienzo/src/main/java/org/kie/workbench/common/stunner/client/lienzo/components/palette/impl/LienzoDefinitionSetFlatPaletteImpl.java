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

package org.kie.workbench.common.stunner.client.lienzo.components.palette.impl;

import org.kie.workbench.common.stunner.client.lienzo.components.palette.AbstractLienzoGlyphItemsPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.LienzoDefinitionSetFlatPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.LienzoPaletteViewImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.element.*;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.components.palette.model.GlyphPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteGrid;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

@Dependent
public class LienzoDefinitionSetFlatPaletteImpl
        extends AbstractLienzoGlyphItemsPalette<HasPaletteItems<? extends GlyphPaletteItem>, LienzoPaletteViewImpl>
        implements LienzoDefinitionSetFlatPalette {

    private String lastCategory = null;
    private final List<GlyphPaletteItem> items = new LinkedList<>();

    protected LienzoDefinitionSetFlatPaletteImpl() {
        this( null, null, null );
    }

    @Inject
    public LienzoDefinitionSetFlatPaletteImpl( final ShapeManager shapeManager,
                                               final LienzoPaletteViewImpl view,
                                               final DefinitionGlyphTooltip definitionGlyphTooltip ) {
        super( shapeManager, definitionGlyphTooltip, view );
    }

    @PostConstruct
    public void init() {
        super.doInit();
    }

    @Override
    protected void doBind() {
        items.clear();
        final DefinitionSetPalette definitionSetPalette = ( DefinitionSetPalette ) paletteDefinition;
        final List<DefinitionPaletteCategory> categories = definitionSetPalette.getItems();
        if ( null != categories && !categories.isEmpty() ) {
            final PaletteGrid grid = getGrid();
            for ( final DefinitionPaletteCategory category : categories ) {
                final List<DefinitionPaletteItem> categoryItems = category.getItems();
                if ( null != categoryItems && !categoryItems.isEmpty() ) {
                    for ( final GlyphPaletteItem item : categoryItems ) {
                        addGlyphItemIntoView( item, grid );
                        items.add( item );

                    }
                    // TODO: addSeparatorIntoView( grid ); - Too much height as using static grid size.
                }

            }

        }

    }

    @Override
    protected ShapeFactory getShapeFactory() {
        final ShapeFactory f = super.getShapeFactory();
        if ( null == f ) {
            final DefinitionSetPalette definitionSetPalette = ( DefinitionSetPalette ) paletteDefinition;
            return shapeManager.getDefaultShapeSet( definitionSetPalette.getDefinitionSetId() ).getShapeFactory();
        }
        return f;
    }

    @Override
    public List<GlyphPaletteItem> getItems() {
        return items;
    }

    protected void addTextIntoView( final String text,
                                    final PaletteGrid grid ) {
        final LienzoTextPaletteElementView separatorPaletteTextView =
                new LienzoTextPaletteElementViewImpl( text, "Verdana", 10 );
        addElementIntoView( separatorPaletteTextView );
    }

    protected void addSeparatorIntoView( final PaletteGrid grid ) {
        final LienzoSeparatorPaletteElementView separatorPaletteElementView =
                new LienzoSeparatorPaletteElementViewImpl( grid.getIconSize(), grid.getIconSize() );
        addElementIntoView( separatorPaletteElementView );
    }

    protected void addElementIntoView( final LienzoPaletteElementView paletteElementView ) {
        itemViews.add( paletteElementView );
        view.add( paletteElementView );

    }

}
