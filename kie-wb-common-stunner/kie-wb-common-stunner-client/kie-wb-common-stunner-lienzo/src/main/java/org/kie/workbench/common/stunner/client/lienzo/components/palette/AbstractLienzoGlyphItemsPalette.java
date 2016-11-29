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

package org.kie.workbench.common.stunner.client.lienzo.components.palette;

import com.ait.lienzo.client.core.shape.Group;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.LienzoPaletteView;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.element.LienzoGlyphPaletteItemView;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.element.LienzoGlyphPaletteItemViewImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.element.LienzoPaletteElementView;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphTooltip;
import org.kie.workbench.common.stunner.core.client.components.palette.ClientPaletteUtils;
import org.kie.workbench.common.stunner.core.client.components.palette.model.GlyphPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteGrid;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractLienzoGlyphItemsPalette<I extends HasPaletteItems<? extends GlyphPaletteItem>, V extends LienzoPaletteView>
        extends AbstractLienzoPalette<I, V>
        implements LienzoGlyphItemsPalette<I, V> {

    private static Logger LOGGER = Logger.getLogger( AbstractLienzoGlyphItemsPalette.class.getName() );

    protected DefinitionGlyphTooltip<Group> definitionGlyphTooltip;
    protected GlyphTooltipCallback glyphTooltipCallback;
    protected final List<LienzoPaletteElementView> itemViews = new LinkedList<LienzoPaletteElementView>();
    private ShapeFactory shapeFactory;

    protected AbstractLienzoGlyphItemsPalette() {
        this( null, null, null );
    }

    public AbstractLienzoGlyphItemsPalette( final ShapeManager shapeManager,
                                            final DefinitionGlyphTooltip definitionGlyphTooltip,
                                            final V view ) {
        super( shapeManager, view );
        this.definitionGlyphTooltip = definitionGlyphTooltip;
    }

    @Override
    public LienzoGlyphItemsPalette<I, V> setShapeSetId( final String shapeSetId ) {
        this.shapeFactory = shapeManager.getShapeSet( shapeSetId ).getShapeFactory();
        return this;
    }

    @Override
    public LienzoGlyphItemsPalette<I, V> onShowGlyTooltip( final GlyphTooltipCallback callback ) {
        this.glyphTooltipCallback = callback;
        return this;
    }

    public DefinitionGlyphTooltip getDefinitionGlyphTooltip() {
        return definitionGlyphTooltip;
    }

    @Override
    protected void doClose() {
        super.doClose();
        AbstractLienzoGlyphItemsPalette.this.getView().hide();
        this.definitionGlyphTooltip.hide();
    }

    @Override
    protected void beforeBind() {
        super.beforeBind();
        itemViews.clear();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void doBind() {
        final PaletteGrid grid = getGrid();
        for ( final GlyphPaletteItem item : getItems() ) {
            addGlyphItemIntoView( item, grid );
        }
    }

    @Override
    protected void afterBind() {
        super.afterBind();
        doExpandCollapse();
    }

    @SuppressWarnings( "unchecked" )
    protected void addGlyphItemIntoView( final GlyphPaletteItem item,
                                         final PaletteGrid grid ) {
        final Glyph<Group> glyph = getGlyph( item.getDefinitionId(), grid.getIconSize(), grid.getIconSize() );
        if ( null != glyph ) {
            final LienzoGlyphPaletteItemView paletteItemView = new LienzoGlyphPaletteItemViewImpl( item, getView(), glyph );
            itemViews.add( paletteItemView );
            view.add( paletteItemView );
        } else {
            LOGGER.log( Level.WARNING, "Could not create glyph for [" + item.getDefinitionId() + "]" );
        }
    }

    @SuppressWarnings( "unchecked" )
    protected Glyph<Group> getGlyph( final String id,
                                     final double width,
                                     final double height ) {
        final ShapeFactory shapeFactory = getShapeFactory();
        if ( null != shapeFactory ) {
            return shapeFactory.glyph( id, width, height );
        }
        LOGGER.log( Level.SEVERE, "No shape factory available." );
        return null;
    }

    @SuppressWarnings( "unchecked" )
    public List<GlyphPaletteItem> getItems() {
        final HasPaletteItems<? extends GlyphPaletteItem> paletteItems = paletteDefinition;
        return ( List<GlyphPaletteItem> ) paletteItems.getItems();
    }

    @Override
    protected String getPaletteItemId( final int index ) {
        final List<GlyphPaletteItem> items = getItems();
        if ( null != items && items.size() > index ) {
            return items.get( index ).getId();
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public double[] computePaletteSize() {
        final PaletteGrid grid = getGrid();
        String longestTitle = null;
        if ( expanded ) {
            final List<GlyphPaletteItem> paletteItems = getItems();
            longestTitle = ClientPaletteUtils.getLongestText( paletteItems );
        }
        final int titleLength = null != longestTitle ? longestTitle.length() : 0;
        if ( isHorizontalLayout() ) {
            return ClientPaletteUtils.computeSizeForHorizontalLayout( getItems().size(), grid.getIconSize(), grid.getPadding(), titleLength );
        } else {
            return ClientPaletteUtils.computeSizeForVerticalLayout( getItems().size(), grid.getIconSize(), grid.getPadding(), titleLength );
        }
    }

    @Override
    public boolean onItemHover( final int pos,
                                final double mouseX,
                                final double mouseY,
                                final double itemX,
                                final double itemY ) {
        if ( super.onItemHover( pos, mouseX, mouseY, itemX, itemY ) ) {
            final GlyphPaletteItem item = getItem( pos );
            if ( null != item ) {
                return onItemHover( item, mouseX, mouseY, itemX, itemY );

            }

        }
        return true;
    }

    public GlyphPaletteItem getItem( final int pos ) {
        final LienzoPaletteElementView itemView = itemViews.get( pos );
        if ( itemView instanceof LienzoGlyphPaletteItemView ) {
            final String iid = ( ( LienzoGlyphPaletteItemView ) itemView ).getPaletteItem().getId();
            return getItem( iid );

        }
        return null;
    }

    protected GlyphPaletteItem getItem( final String id ) {
        final List<GlyphPaletteItem> paletteItems = getItems();
        if ( null != paletteItems && !paletteItems.isEmpty() ) {
            for ( final GlyphPaletteItem item : paletteItems ) {
                if ( item.getId().equals( id ) ) {
                    return item;
                }
            }
        }
        return null;
    }

    protected boolean onItemHover( final GlyphPaletteItem item,
                                   final double mouseX,
                                   final double mouseY,
                                   final double itemX,
                                   final double itemY ) {
        if ( null != glyphTooltipCallback ) {
            glyphTooltipCallback.onShowTooltip( definitionGlyphTooltip, item, mouseX, mouseY, itemX, itemY );
        } else if ( !expanded ) {
            final PaletteGrid grid = getGrid();
            definitionGlyphTooltip
                    .showTooltip(
                            item.getDefinitionId(),
                            itemX + ( grid.getIconSize() / 2 ),
                            itemY,
                            GlyphTooltip.Direction.WEST );
        }
        return true;
    }

    @Override
    public boolean onItemOut( final int index ) {
        if ( super.onItemOut( index ) ) {
            definitionGlyphTooltip.hide();
        }
        return true;
    }

    protected ShapeFactory getShapeFactory() {
        return shapeFactory;
    }

    protected void doExpandCollapse() {
        for ( final LienzoPaletteElementView itemView : itemViews ) {
            if ( itemView instanceof LienzoGlyphPaletteItemView ) {
                final LienzoGlyphPaletteItemView glyphPaletteItemView = ( LienzoGlyphPaletteItemView ) itemView;
                if ( expanded ) {
                    glyphPaletteItemView.expand();
                } else {
                    glyphPaletteItemView.collapse();
                }
            }
        }
        getView().draw();
    }

    @Override
    protected void doDestroy() {
        itemViews.clear();
        super.doDestroy();
    }

}
