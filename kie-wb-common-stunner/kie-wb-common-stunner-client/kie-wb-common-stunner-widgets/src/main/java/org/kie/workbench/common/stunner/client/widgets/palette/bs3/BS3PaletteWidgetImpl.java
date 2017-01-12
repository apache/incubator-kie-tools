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

package org.kie.workbench.common.stunner.client.widgets.palette.bs3;

import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import com.google.gwt.user.client.Timer;
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
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class BS3PaletteWidgetImpl extends AbstractPaletteWidget<DefinitionSetPalette, BS3PaletteWidgetView>
        implements BS3PaletteWidget,
                   IsWidget {

    private static Logger LOGGER = Logger.getLogger( BS3PaletteWidgetImpl.class.getName() );
    private static final int MOUSE_OVER_TIMER_DURATION = 300;

    public static final String BG_COLOR = "#D3D3D3";
    public static final String HOVER_BG_COLOR = "#f2f2f2";

    private static final int CATEGORIES_ICON_WIDTH = 30;
    private static final int CATEGORIES_ICON_HEIGHT = 20;
    private static final int GLYPH_ICON_SIZE = 30;
    private static final int CATEGORY_VIEW_WIDTH = 200;
    private static final int CATEGORY_VIEW_HEIGHT = 400;
    private static final int PADDING = 10;
    private static final int FLOATING_VIEW_TIMEOUT = 1500;

    private final BS3PaletteCategories paletteCategories;
    private final BS3PaletteCategory paletteCategory;
    private final FloatingView<IsWidget> floatingView;
    private final ShapeGlyphDragHandler<Group> shapeGlyphDragHandler;

    private BS3PaletteViewFactory viewFactory;
    private Timer itemMouseDverTimer;
    private boolean dragging;

    @Inject
    public BS3PaletteWidgetImpl( final ShapeManager shapeManager,
                                 final ClientFactoryService clientFactoryServices,
                                 final BS3PaletteWidgetView view,
                                 final BS3PaletteCategories paletteCategories,
                                 final BS3PaletteCategory paletteCategory,
                                 final FloatingView<IsWidget> floatingView,
                                 final ShapeGlyphDragHandler<Group> shapeGlyphDragHandler ) {
        super( shapeManager,
               clientFactoryServices,
               view );
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
        paletteCategories.setIconWidth( CATEGORIES_ICON_WIDTH );
        paletteCategories.setIconHeight( CATEGORIES_ICON_HEIGHT );
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
    public void unbind() {
        super.unbind();
        clearItemMouseOverTimer();
        paletteCategories.clear();
        paletteCategory.clear();
        floatingView.hide();
    }

    @Override
    protected ShapeFactory getShapeFactory() {
        final DefinitionSetPalette palette = paletteDefinition;
        return shapeManager.getDefaultShapeSet( palette.getDefinitionSetId() ).getShapeFactory();
    }

    @Override
    public double getIconSize() {
        return GLYPH_ICON_SIZE;
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
            view.show( paletteCategories.getView(),
                       mainPaletteSize[ 0 ],
                       mainPaletteSize[ 1 ] );
            paletteCategories.onItemMouseDown( ( id1,
                                                 mouseX,
                                                 mouseY,
                                                 itemX1,
                                                 itemY1 ) -> {
                hideFloatingPalette();
                clearItemMouseOverTimer();
                final String catDefId = getDefinitionIdForCategory( id1 );
                if ( null != catDefId ) {
                    BS3PaletteWidgetImpl.this.onPaletteItemMouseDown( catDefId,
                                                                      mouseX,
                                                                      mouseY );
                    return true;
                }
                return false;
            } );
            paletteCategories.onItemHover( ( id,
                                             x,
                                             y,
                                             itemX,
                                             itemY ) -> {
                if ( !dragging ) {
                    final String theId = id;
                    final double theX = x;
                    final double theY = y;
                    final double theitemX = itemX;
                    final double theitemY = itemY;
                    BS3PaletteWidgetImpl.this.itemMouseDverTimer = new Timer() {
                        @Override
                        public void run() {
                            // 1.- Ensure floating view is not still visible ( call hide )
                            // 2.- While over this category navigation item, do never hide the floating view ( due to its timeout ).
                            floatingView.hide().setTimeOut( -1 );
                            // Show the floating view that containes the palette widget for the category.
                            BS3PaletteWidgetImpl.this.showFloatingPalette( theId,
                                                                           theX,
                                                                           theY,
                                                                           theitemX,
                                                                           theitemY );
                        }
                    };
                    BS3PaletteWidgetImpl.this.itemMouseDverTimer.schedule( MOUSE_OVER_TIMER_DURATION );
                }
                return true;
            } );
            paletteCategories.onItemOut( id -> {
                clearItemMouseOverTimer();
                if ( !dragging ) {
                    // Set the floating visibility timeout once not hover the concrete navigation item.
                    floatingView.setTimeOut( FLOATING_VIEW_TIMEOUT );
                }
                return true;
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
            paletteCategory.onItemMouseDown( ( id1,
                                               mouseX,
                                               mouseY,
                                               itemX1,
                                               itemY1 ) -> {
                BS3PaletteWidgetImpl.this.onPaletteItemMouseDown( id1,
                                                                  mouseX,
                                                                  mouseY );
                return false;
            } );
            floatingView.show();
            return false;
        }
        return true;
    }

    @Override
    public void onDragProxyComplete( final String definitionId ) {
        super.onDragProxyComplete( definitionId );
        dragProxyComplete();
    }

    @Override
    public void onDragProxyComplete( final String definitionId,
                                     final double x,
                                     final double y ) {
        super.onDragProxyComplete( definitionId,
                                   x,
                                   y );
        dragProxyComplete();
    }

    private String getDefinitionIdForCategory( final String id ) {
        final DefinitionPaletteCategory categoryItem = getMainPaletteItem( id );
        return null != categoryItem ? categoryItem.getDefinitionId() : null;
    }

    private void onPaletteItemMouseDown( final String id,
                                         final double x,
                                         final double y ) {
        showDragProxy( id,
                       x,
                       y );
    }

    private void onPaletteItemClick( final String id ) {
        // Add the element into canvas, no target coordinates specified, let the builder control determine those.
        BS3PaletteWidgetImpl.this.onDragProxyComplete( id );
        floatingView.hide();
    }

    private void hideFloatingPalette() {
        clearItemMouseOverTimer();
        paletteCategory.clear();
        floatingView.hide();
    }

    private void showDragProxy( final String id,
                                final double x,
                                final double y ) {
        // Show the drag proxy for the element at x, y.
        dragging = true;
        view.showDragProxy( id,
                            x,
                            y );
        floatingView.hide();
    }

    private void dragProxyComplete() {
        this.dragging = false;
    }

    private int[] getMainPaletteSize() {
        double width = 0;
        double height = 0;
        if ( null != paletteDefinition ) {
            final List<? extends GlyphPaletteItem> items = getMainPaletteItems();
            final int itemsSize = null != items ? items.size() : 0;
            final double[] mainPaletteSize = ClientPaletteUtils.computeSizeForVerticalLayout( itemsSize,
                                                                                              ( int ) getIconSize(),
                                                                                              getPadding(),
                                                                                              0 );
            // TODO: The current impl is not considering padding, as bootstrap navitems with padding are not well aligned.
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

    void onCanvasFocusedEvent( final @Observes CanvasFocusedEvent canvasFocusedEvent ) {
        checkNotNull( "canvasFocusedEvent",
                      canvasFocusedEvent );
        hideFloatingPalette();
    }

    void onCanvasElementSelectedEvent( final @Observes CanvasElementSelectedEvent canvasElementSelectedEvent ) {
        checkNotNull( "canvasElementSelectedEvent",
                      canvasElementSelectedEvent );
        hideFloatingPalette();
    }

    IsWidget getCategoryView( final String id ) {
        return viewFactory.getCategoryView( paletteDefinition.getDefinitionSetId(),
                                            id,
                                            CATEGORIES_ICON_WIDTH,
                                            CATEGORIES_ICON_HEIGHT );
    }

    IsWidget getDefinitionView( final String id ) {
        return viewFactory.getDefinitionView( paletteDefinition.getDefinitionSetId(),
                                              id,
                                              ( int ) getIconSize(),
                                              ( int ) getIconSize() );
    }

    @Override
    protected void doDestroy() {
        clearItemMouseOverTimer();
        paletteCategories.destroy();
        paletteCategory.destroy();
        floatingView.destroy();
        super.doDestroy();
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

    private void clearItemMouseOverTimer() {
        if ( null != this.itemMouseDverTimer ) {
            if ( this.itemMouseDverTimer.isRunning() ) {
                this.itemMouseDverTimer.cancel();
            }
            this.itemMouseDverTimer = null;
        }
    }
}
