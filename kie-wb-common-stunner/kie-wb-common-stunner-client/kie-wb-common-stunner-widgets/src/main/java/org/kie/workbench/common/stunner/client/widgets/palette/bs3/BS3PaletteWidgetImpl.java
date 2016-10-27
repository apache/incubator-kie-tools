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

package org.kie.workbench.common.stunner.client.widgets.palette.bs3;

import com.ait.lienzo.client.core.shape.Group;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.widgets.palette.AbstractPaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory.BS3PaletteViewFactory;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.ClientPaletteUtils;
import org.kie.workbench.common.stunner.core.client.components.palette.model.GlyphPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingWidgetView;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class BS3PaletteWidgetImpl extends AbstractPaletteWidget<DefinitionSetPalette, BS3PaletteWidgetView>
        implements BS3PaletteWidget, IsWidget {

    private static Logger LOGGER = Logger.getLogger( BS3PaletteWidgetImpl.class.getName() );

    public static final String BG_COLOR = "#D3D3D3";
    public static final String HOVER_BG_COLOR = "#f2f2f2";

    private static final int GLYPH_ICON_SIZE = 30;
    private static final int CATEGORY_ICON_SIZE = 100;
    private static final int CATEGORY_VIEW_WIDTH = 300;
    private static final int CATEGORY_VIEW_HEIGHT = 600;
    private static final int PADDING = 30;
    private static final int FLOATING_VIEW_TIMEOUT = 1500;

    BS3PaletteCategories paletteCategories;
    BS3PaletteCategory paletteCategory;
    FloatingWidgetView floatingView;
    ShapeGlyphDragHandler<Group> shapeGlyphDragHandler;

    private BS3PaletteViewFactory viewFactory;

    @Inject
    public BS3PaletteWidgetImpl( final ShapeManager shapeManager,
                                 final ClientFactoryService clientFactoryServices,
                                 final BS3PaletteWidgetView view,
                                 final BS3PaletteCategories paletteCategories,
                                 final BS3PaletteCategory paletteCategory,
                                 final FloatingWidgetView floatingView,
                                 final ShapeGlyphDragHandler<Group> shapeGlyphDragHandler ) {
        super( shapeManager, clientFactoryServices, view );
        this.paletteCategories = paletteCategories;
        this.paletteCategory = paletteCategory;
        this.floatingView = floatingView;
        this.shapeGlyphDragHandler = shapeGlyphDragHandler;
    }

    public static int getDefaultWidth() {
        return GLYPH_ICON_SIZE + PADDING;
    }

    @PostConstruct
    public void init() {
        view.init( this );
        view.shapeGlyphDragHandler = shapeGlyphDragHandler;
        paletteCategories.bs3PaletteWidget = this;
        paletteCategory.bs3PaletteWidget = this;
        paletteCategories.setPadding( getPadding() );
        paletteCategories.setIconSize( ( int ) getIconSize() );
        paletteCategory.setWidth( CATEGORY_VIEW_WIDTH );
        paletteCategory.setHeight( CATEGORY_VIEW_HEIGHT );
        floatingView.setTimeOut( FLOATING_VIEW_TIMEOUT );
        floatingView.add( paletteCategory.getView() );
        view.setBackgroundColor( BG_COLOR );
        paletteCategories.setBackgroundColor( BG_COLOR );
        paletteCategory.setBackgroundColor( HOVER_BG_COLOR );
        view.showEmptyView( true );

    }

    @Override
    public BS3PaletteWidget setViewFactory( final BS3PaletteViewFactory viewFactory ) {
        this.viewFactory = viewFactory;
        return this;
    }

    @Override
    public FloatingView<IsWidget> getFloatingView() {
        return floatingView;
    }

    @Override
    protected void beforeBind() {
        super.beforeBind();
        hideFloatingPalette();

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected AbstractPalette<DefinitionSetPalette> bind() {
        final DefinitionSetPalette palette = paletteDefinition;
        if ( null != palette ) {
            paletteCategories.bind( palette );
            final int[] mainPaletteSize = getMainPaletteSize();
            view.show( paletteCategories.getView(), mainPaletteSize[ 0 ], mainPaletteSize[ 1 ] );
            paletteCategories.onItemHover( ( id, x, y, itemX, itemY ) -> {
                // 1.- Ensure floating view is not still visible ( call hide )
                // 2.- While over this category navigation item, do never hide the floating view ( due to its timeout ).
                floatingView.hide().setTimeOut( -1 );
                // Show the floating view that containes the palette widget for the category.
                return BS3PaletteWidgetImpl.this.showFloatingPalette( id, x, y, itemX, itemY );

            } );
            paletteCategories.onItemOut( id -> {
                // Set the floating visibility timeout once not hover the concrete navigation item.
                floatingView.setTimeOut( FLOATING_VIEW_TIMEOUT ).show();
                return false;
            } );

        }
        return this;
    }

    @SuppressWarnings( "unchecked" )
    private boolean showFloatingPalette( final String id,
                                         final double x,
                                         final double y,
                                         final double itemX,
                                         final double itemY ) {
        paletteCategory.clear();
        final DefinitionPaletteCategory item = getMainPaletteItem( id );
        if ( hasPaletteItems( item ) ) {
            paletteCategory.bind( item );
            final int[] mainPaletteSize = getMainPaletteSize();
            final double pX = mainPaletteSize[ 0 ];
            floatingView
                    .setOffsetX( getViewAbsoluteLeft() )
                    .setOffsetY( getViewAbsoluteTop() )
                    .setX( pX )
                    .setY( 0 );
            paletteCategory.onItemMouseDown( ( id1, mouseX, mouseY, itemX1, itemY1 ) -> {
                view.showDragProxy( id1, mouseX, mouseY );
                floatingView.hide();
                return false;

            } );
            paletteCategory.onItemClick( ( id12, mouseX, mouseY, itemX12, itemY12 ) -> {
                // Add the element into canvas, no target coordinates specified, let the builder control determine those.
                BS3PaletteWidgetImpl.this.onDragProxyComplete( id12 );
                floatingView.hide();
                return false;

            } );
            floatingView.show();
            return false;

        }
        return true;

    }

    private void hideFloatingPalette() {
        floatingView.hide();
        paletteCategory.clear();

    }

    private int[] getMainPaletteSize() {
        double width = 0;
        double height = 0;
        if ( null != paletteDefinition ) {
            final List<? extends GlyphPaletteItem> items = getMainPaletteItems();
            final int itemsSize = null != items ? items.size() : 0;
            final double[] mainPaletteSize =
                    ClientPaletteUtils.computeSizeForVerticalLayout( itemsSize, ( int ) getIconSize(), getPadding(), 0 );
            // TODO: The current impl is not considering padding, as boostrap navitems with padding are not well aligned.
            width = mainPaletteSize[ 0 ] - getPadding();
            height = mainPaletteSize[ 1 ] - ( getPadding() * ( itemsSize + 1 ) );
        }
        return new int[]{ ( int ) width, ( int ) height };
    }

    private List<DefinitionPaletteCategory> getMainPaletteItems() {
        return paletteCategories.getDefinition().getItems();
    }

    private boolean hasPaletteItems( final DefinitionPaletteCategory item ) {
        return null != item.getItems() && !item.getItems().isEmpty();
    }

    private DefinitionPaletteCategory getMainPaletteItem( final int index ) {
        return getMainPaletteItems().get( index );
    }

    private DefinitionPaletteCategory getMainPaletteItem( final String id ) {
        final List<DefinitionPaletteCategory> categories = getMainPaletteItems();
        if ( null != categories && !categories.isEmpty() ) {
            for ( final DefinitionPaletteCategory category : categories ) {
                if ( category.getId().equals( id ) ) {
                    return category;

                }

            }

        }
        return null;
    }

    private double getViewAbsoluteTop() {
        return view.getAbsoluteTop();
    }

    private double getViewAbsoluteLeft() {
        return view.getAbsoluteLeft();
    }

    @Override
    public double getIconSize() {
        return GLYPH_ICON_SIZE;
    }

    @Override
    public void unbind() {
        super.unbind();
        floatingView.hide();
        paletteCategories.clear();
        paletteCategory.clear();

    }

    void onCanvasFocusedEvent( @Observes CanvasFocusedEvent canvasFocusedEvent ) {
        checkNotNull( "canvasFocusedEvent", canvasFocusedEvent );
        hideFloatingPalette();
    }

    void onCanvasElementSelectedEvent( @Observes CanvasElementSelectedEvent canvasElementSelectedEvent ) {
        checkNotNull( "canvasElementSelectedEvent", canvasElementSelectedEvent );
        hideFloatingPalette();
    }

    IsWidget getCategoryView( final String id ) {
        return viewFactory.getCategoryView( id, CATEGORY_ICON_SIZE, CATEGORY_ICON_SIZE );
    }

    IsWidget getDefinitionView( final String id ) {
        return viewFactory.getDefinitionView( id, ( int ) getIconSize(), ( int ) getIconSize() );
    }

    @Override
    protected void doDestroy() {
        paletteCategories.destroy();
        paletteCategory.destroy();
        floatingView.destroy();
        super.doDestroy();
        this.paletteCategories = null;
        this.paletteCategory = null;
        this.floatingView = null;

    }

    @Override
    protected String getPaletteItemId( final int index ) {
        final DefinitionPaletteCategory item = getMainPaletteItem( index );
        return null != item ? item.getId() : null;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    private int getPadding() {
        return PADDING;
    }

}
