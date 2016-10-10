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

import com.ait.lienzo.client.core.shape.Layer;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.*;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.LienzoHoverPaletteView;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphTooltip;
import org.kie.workbench.common.stunner.core.client.components.palette.ClientPaletteUtils;
import org.kie.workbench.common.stunner.core.client.components.palette.model.GlyphPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteGrid;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

@Dependent
public class LienzoDefinitionSetPaletteImpl
        extends AbstractLienzoGlyphItemsPalette<DefinitionSetPalette, LienzoHoverPaletteView>
        implements LienzoDefinitionSetPalette {

    LienzoGlyphsHoverPalette glyphsFloatingPalette;

    private final List<GlyphPaletteItem> items = new LinkedList<>();

    protected LienzoDefinitionSetPaletteImpl() {
        this( null, null, null, null );
    }

    @Inject
    public LienzoDefinitionSetPaletteImpl( final ShapeManager shapeManager,
                                           final LienzoHoverPaletteView view,
                                           final DefinitionGlyphTooltip definitionGlyphTooltip,
                                           final LienzoGlyphsHoverPalette glyphsFloatingPalette ) {
        super( shapeManager, definitionGlyphTooltip, view );
        this.glyphsFloatingPalette = glyphsFloatingPalette;
    }

    @PostConstruct
    public void init() {
        super.doInit();
        onShowGlyTooltip( ( glyphTooltip, item, mouseX, mouseY, itemX, itemY ) -> {
            if ( !hasPaletteItems( item ) ) {
                glyphTooltip.show( getGlyphTooltipText( item ), mouseX, mouseY, GlyphTooltip.Direction.WEST );

            }
            return false;
        } );
        glyphsFloatingPalette.setExpandable( false );
        glyphsFloatingPalette.setLayout( LienzoPalette.Layout.HORIZONTAL );
        glyphsFloatingPalette.onClose( floatingPaletteCloseCallback );
        glyphsFloatingPalette.onShowGlyTooltip( floatingPaletteGlyphTooltipCallback );

    }

    @Override
    protected void doItemHover( final String id,
                                final double mouseX,
                                final double mouseY,
                                final double itemX,
                                final double itemY ) {
        super.doItemHover( id, mouseX, mouseY, itemX, itemY );
        showFloatingPalette( id, mouseX, mouseY, itemX, itemY );
    }

    private final CloseCallback floatingPaletteCloseCallback = () -> {
        clearFloatingPalette();
        return true;
    };

    private final LienzoGlyphItemsPalette.GlyphTooltipCallback floatingPaletteGlyphTooltipCallback =
            ( glyphTooltip, item, mouseX, mouseY, itemX, itemY ) -> {
                // final int[] mainPaletteSize = getMainPaletteSize();
                // final double px = getView().getX() + mainPaletteSize[0] + itemX;
                // final double py = getView().getY() + itemY + ( getIconSize() / 2 ) - getPadding();
                glyphTooltip.show( getGlyphTooltipText( item ), mouseX, mouseY + getIconSize() + getPadding(), GlyphTooltip.Direction.NORTH );
                return false;

            };

    private String getGlyphTooltipText( final GlyphPaletteItem item ) {
        return "Create a new " + item.getTitle();
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

                }

            }

        }

    }

    @Override
    public List<GlyphPaletteItem> getItems() {
        return items;
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();

    }

    @SuppressWarnings( "unchecked" )
    private boolean showFloatingPalette( final String id,
                                         final double x,
                                         final double y,
                                         final double itemX,
                                         final double itemY ) {
        final GlyphPaletteItem item = getItem( id );
        if ( hasPaletteItems( item ) ) {
            final HasPaletteItems<GlyphPaletteItem> hasPaletteItems =
                    ( HasPaletteItems<GlyphPaletteItem> ) item;
            glyphsFloatingPalette.setIconSize( getIconSize() );
            glyphsFloatingPalette.setPadding( getPadding() );
            glyphsFloatingPalette.bind( hasPaletteItems );
            final int[] mainPaletteSize = getMainPaletteSize();
            final double pX = getView().getX() + mainPaletteSize[ 0 ] - ( getPadding() * 1.5 ) - getIconSize();
            final double pY = getView().getY() + itemY - ( getPadding() * 3 ) + ( getIconSize() / 2 );
            final Layer paletteLayer = getView().getLayer();
            glyphsFloatingPalette
                    .getView()
                    .attach( paletteLayer )
                    .setX( pX )
                    .setY( pY );
            glyphsFloatingPalette.onItemHover( ( id12, mouseX, mouseY, itemX12, itemY12 ) -> {
                getView().clearTimeOut();
                return true;
            } );
            glyphsFloatingPalette.onItemOut( id13 -> {
                getView().startTimeOut();
                return false;
            } );
            glyphsFloatingPalette.onItemClick( new ItemClickCallback() {
                @Override
                public boolean onItemClick( String id14, double mouseX, double mouseY, double itemX, double itemY ) {
                    final GlyphPaletteItem item1 = getPaletteItem( id14, hasPaletteItems.getItems() );
                    if ( !hasPaletteItems( item1 ) ) {
                        // Fire the main palette's callback.
                        LienzoDefinitionSetPaletteImpl.this.onItemClick( id14, mouseX, mouseY, getView().getX(), getView().getY() );

                    }
                    clearFloatingPalette();
                    return true;

                }
            } );
            glyphsFloatingPalette.getView().show();
            return false;

        }
        return true;

    }

    @Override
    protected String getPaletteItemId( final int index ) {
        final GlyphPaletteItem item = getMainPaletteItem( index );
        return null != item ? item.getId() : null;
    }

    private void clearFloatingPalette() {
        glyphsFloatingPalette.getView().clear();
    }

    private void hideFloatingPalette() {
        glyphsFloatingPalette.getView().hide();
    }

    private int[] getMainPaletteSize() {
        double width = 0;
        double height = 0;
        if ( null != paletteDefinition ) {
            final List<? extends GlyphPaletteItem> items = getItems();
            final int itemsSize = null != items ? items.size() : 0;
            final double[] mainPaletteSize =
                    ClientPaletteUtils.computeSizeForVerticalLayout( itemsSize, getIconSize(), getPadding(), 0 );
            width = mainPaletteSize[ 0 ];
            height = mainPaletteSize[ 1 ];
        }
        return new int[]{ ( int ) width, ( int ) height };
    }

    private boolean hasPaletteItems( final GlyphPaletteItem item ) {
        return item instanceof HasPaletteItems;
    }

    private GlyphPaletteItem getMainPaletteItem( final int index ) {
        return getItems().get( index );
    }

    private GlyphPaletteItem getPaletteItem( final String id, final List<GlyphPaletteItem> items ) {
        if ( null != items && !items.isEmpty() ) {
            for ( final GlyphPaletteItem item : items ) {
                if ( item.getId().equals( id ) ) {
                    return item;

                }

            }

        }
        return null;
    }

    private int getPadding() {
        return getGrid().getPadding();
    }

    private int getIconSize() {
        return getGrid().getIconSize();
    }

}
