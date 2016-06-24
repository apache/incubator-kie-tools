/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;

@Dependent
@Templated
public class RadarMenuViewImpl extends Composite implements RadarMenuView {

    private static final int RADAR_WIDTH = 200;
    private static final int RADAR_HEIGHT = 200;

    private RadarMenuBuilder presenter;

    private TranslationService translationService;

    @DataField("radarMenuDropdown")
    ButtonElement radarMenuDropdown = Document.get().createPushButtonElement();

    @DataField("radarMenu")
    LIElement radarMenu = Document.get().createLIElement();

    @DataField("radarCanvas")
    LienzoPanel radarCanvas = new LienzoPanel( RADAR_WIDTH,
                                               RADAR_HEIGHT );
    Layer radarLayer = new Layer();

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private final Rectangle visibleBounds = new Rectangle( 0, 0 ) {

        @Override
        public DragConstraintEnforcer getDragConstraints() {
            return new DragConstraintEnforcer() {

                private Point2D start;

                @Override
                public void startDrag( final DragContext dragContext ) {
                    start = new Point2D( visibleBounds.getLocation() );
                }

                @Override
                public boolean adjust( final Point2D dxy ) {
                    boolean adjusted = false;
                    final Point2D newPoint = new Point2D( start ).add( dxy );

                    if ( newPoint.getX() < minX ) {
                        dxy.setX( minX - start.getX() );
                        adjusted = true;
                    }
                    if ( newPoint.getX() + visibleBounds.getWidth() > maxX ) {
                        dxy.setX( maxX - start.getX() - visibleBounds.getWidth() );
                        adjusted = true;
                    }

                    if ( newPoint.getY() < minY ) {
                        dxy.setY( minY - start.getY() );
                        adjusted = true;
                    }
                    if ( newPoint.getY() + visibleBounds.getHeight() > maxY ) {
                        dxy.setY( maxY - start.getY() - visibleBounds.getHeight() );
                        adjusted = true;
                    }

                    return adjusted;
                }
            };
        }
    };

    @Inject
    public RadarMenuViewImpl( final TranslationService translationService ) {
        this.translationService = translationService;
    }

    @PostConstruct
    public void setup() {
        radarCanvas.add( radarLayer );
        radarMenuDropdown.setTitle( translationService.getTranslation( GuidedDecisionTableErraiConstants.RowContextMenuViewImpl_Title ) );

        visibleBounds.setDraggable( true );
        visibleBounds.addNodeDragMoveHandler( new NodeDragMoveHandler() {
            @Override
            public void onNodeDragMove( final NodeDragMoveEvent event ) {
                presenter.onDragVisibleBounds( visibleBounds.getX(),
                                               visibleBounds.getY() );
            }
        } );
        visibleBounds.setFillColor( ColorName.GRAY );
        visibleBounds.setAlpha( 0.25 );
        visibleBounds.setLocation( new Point2D( ( RADAR_WIDTH - visibleBounds.getWidth() ) / 2,
                                                ( RADAR_HEIGHT - visibleBounds.getHeight() ) / 2 ) );
        radarLayer.add( visibleBounds );

        radarCanvas.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( eventTargetsPopup( event.getNativeEvent(),
                                        radarMenu ) ) {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }

            private boolean eventTargetsPopup( final NativeEvent event,
                                               final Element element ) {
                final EventTarget target = event.getEventTarget();
                if ( Element.is( target ) ) {
                    return element.isOrHasChild( Element.as( target ) );
                }
                return false;
            }

        } );
    }

    @Override
    public void init( final RadarMenuBuilder presenter ) {
        this.presenter = presenter;
    }

    @Override
    public boolean isEnabled() {
        return !radarMenuDropdown.isDisabled();
    }

    @Override
    public void setEnabled( final boolean enabled ) {
        radarMenuDropdown.setDisabled( !enabled );
    }

    @Override
    public void reset() {
        radarLayer.removeAll();
    }

    @Override
    public void setModellerBounds( final Bounds bounds ) {
        this.minX = bounds.getX();
        this.maxX = minX + bounds.getWidth();
        this.minY = bounds.getY();
        this.maxY = minY + bounds.getHeight();
        this.radarLayer.getViewport().setTransform( getTransform( bounds ) );
    }

    @Override
    public void setAvailableDecisionTables( final Set<GuidedDecisionTableView.Presenter> dtPresenters ) {
        for ( GuidedDecisionTableView.Presenter dtPresenter : dtPresenters ) {
            final GuidedDecisionTableView view = dtPresenter.getView();
            radarLayer.add( makeDecisionTableGlyph( view ) );
        }
        radarLayer.batch();
    }

    private Transform getTransform( final Bounds bounds ) {
        final Transform t = new Transform();
        t.scale( (double) RADAR_WIDTH / bounds.getWidth(),
                 (double) RADAR_HEIGHT / bounds.getHeight() ).translate( -bounds.getX(),
                                                                         -bounds.getY() );
        return t;
    }

    private Group makeDecisionTableGlyph( final GuidedDecisionTableView view ) {
        final Group g = new Group();
        final Rectangle r = new Rectangle( view.getWidth(),
                                           view.getHeight() );
        r.setFillColor( ColorName.LIGHTGRAY );
        r.setLocation( view.getLocation() );
        g.setListening( false );
        g.add( r );
        return g;
    }

    @Override
    public void setVisibleBounds( final Bounds bounds ) {
        radarLayer.remove( visibleBounds );
        visibleBounds.setLocation( new Point2D( bounds.getX(),
                                                bounds.getY() ) );
        visibleBounds.setHeight( bounds.getHeight() );
        visibleBounds.setWidth( bounds.getWidth() );
        radarLayer.add( visibleBounds );
        visibleBounds.moveToTop();
        radarLayer.batch();
    }

    @Override
    public void enableDrag( final boolean enabled ) {
        visibleBounds.setDraggable( enabled );
    }

    @SuppressWarnings("unused")
    @EventHandler("radarMenuDropdown")
    public void onClickRadarMenuDropdown( final ClickEvent e ) {
        presenter.onClick();
    }

}
