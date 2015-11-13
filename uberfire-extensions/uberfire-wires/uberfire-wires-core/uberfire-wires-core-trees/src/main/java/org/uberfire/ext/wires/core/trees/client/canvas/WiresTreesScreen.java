/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.core.trees.client.canvas;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.wires.core.api.events.ClearEvent;
import org.uberfire.ext.wires.core.api.events.ShapeAddedEvent;
import org.uberfire.ext.wires.core.api.events.ShapeDeletedEvent;
import org.uberfire.ext.wires.core.api.events.ShapeDragCompleteEvent;
import org.uberfire.ext.wires.core.api.events.ShapeDragPreviewEvent;
import org.uberfire.ext.wires.core.api.events.ShapeSelectedEvent;
import org.uberfire.ext.wires.core.api.layout.LayoutManager;
import org.uberfire.ext.wires.core.api.layout.RequiresLayoutManager;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.canvas.WiresCanvas;
import org.uberfire.ext.wires.core.trees.client.layout.WiresLayoutUtilities;
import org.uberfire.ext.wires.core.trees.client.layout.treelayout.Rectangle2D;
import org.uberfire.ext.wires.core.trees.client.shapes.WiresBaseTreeNode;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "WiresTreesScreen")
public class WiresTreesScreen extends WiresCanvas {

    private static final int MAX_PROXIMITY = 200;

    private static final int ANIMATION_DURATION = 250;

    @Inject
    private Event<ClearEvent> clearEvent;

    @Inject
    private Event<ShapeSelectedEvent> shapeSelectedEvent;

    @Inject
    private Event<ShapeAddedEvent> shapeAddedEvent;

    @Inject
    private Event<ShapeDeletedEvent> shapeDeletedEvent;

    @Inject
    private LayoutManager layoutManager;

    private Menus menus;

    private WiresTreeNodeDropContext dropContext = new WiresTreeNodeDropContext();

    private WiresTreeNodeConnector connector = null;

    private WiresBaseTreeNode root;

    @PostConstruct
    public void setup() {
        this.menus = MenuFactory
                .newTopLevelMenu( "Clear grid" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        clear();
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Delete selected" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        if ( isShapeSelected() ) {
                            deleteShape( getSelectedShape() );
                        }
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Clear selection" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        if ( isShapeSelected() ) {
                            clearSelection();
                            menus.getItems().get( 1 ).setEnabled( false );
                            menus.getItems().get( 2 ).setEnabled( false );
                            menus.getItems().get( 3 ).setEnabled( false );
                            menus.getItems().get( 4 ).setEnabled( false );
                        }
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Collapse node" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        collapseNode();
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Expand node" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        expandNode();
                    }
                } )
                .endMenu()
                .build();
        menus.getItems().get( 0 ).setEnabled( false );
        menus.getItems().get( 1 ).setEnabled( false );
        menus.getItems().get( 2 ).setEnabled( false );
        menus.getItems().get( 3 ).setEnabled( false );
        menus.getItems().get( 4 ).setEnabled( false );
    }

    @WorkbenchPartTitle
    @Override
    public String getTitle() {
        return "Canvas";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @Override
    public void selectShape( final WiresBaseShape shape ) {
        shapeSelectedEvent.fire( new ShapeSelectedEvent( shape ) );
    }

    public void onShapeSelected( @Observes ShapeSelectedEvent event ) {
        final WiresBaseShape shape = event.getShape();
        super.selectShape( shape );
        menus.getItems().get( 1 ).setEnabled( isShapeSelected() );
        menus.getItems().get( 2 ).setEnabled( isShapeSelected() );
        menus.getItems().get( 3 ).setEnabled( nodeHasChildren( shape ) && !nodeHasCollapsedChildren( shape ) );
        menus.getItems().get( 4 ).setEnabled( nodeHasCollapsedChildren( shape ) );
    }

    @Override
    public void deselectShape( final WiresBaseShape shape ) {
        super.deselectShape( shape );
        menus.getItems().get( 1 ).setEnabled( isShapeSelected() );
        menus.getItems().get( 2 ).setEnabled( isShapeSelected() );
        menus.getItems().get( 3 ).setEnabled( isShapeSelected() );
        menus.getItems().get( 4 ).setEnabled( isShapeSelected() );
    }

    public void onDragPreviewHandler( @Observes ShapeDragPreviewEvent shapeDragPreviewEvent ) {
        //We can only connect WiresTreeNodes to each other
        if ( !( shapeDragPreviewEvent.getShape() instanceof WiresBaseTreeNode ) ) {
            dropContext.setContext( null );
            return;
        }

        //Find a Parent Node to attach the Shape to
        final double cx = getX( shapeDragPreviewEvent.getX() );
        final double cy = getY( shapeDragPreviewEvent.getY() );
        final WiresBaseTreeNode child = (WiresBaseTreeNode) shapeDragPreviewEvent.getShape();
        final WiresBaseTreeNode prospectiveParent = getParentNode( child,
                                                                   cx,
                                                                   cy );

        //If there is a prospective parent show the line between child and parent
        if ( prospectiveParent != null ) {
            if ( connector == null ) {
                connector = new WiresTreeNodeConnector();
                canvasLayer.add( connector );
                connector.moveToBottom();
            }
            connector.getPoints().get( 0 ).set( prospectiveParent.getLocation() );
            connector.getPoints().get( 1 ).set( new Point2D( cx,
                                                                  cy ) );
        } else if ( connector != null ) {
            canvasLayer.remove( connector );
            connector = null;
        }

        dropContext.setContext( prospectiveParent );
        canvasLayer.batch();
    }

    public void onDragCompleteHandler( @Observes ShapeDragCompleteEvent shapeDragCompleteEvent ) {
        final WiresBaseShape wiresShape = shapeDragCompleteEvent.getShape();

        //Hide the temporary connector
        if ( connector != null ) {
            canvasLayer.remove( connector );
            canvasLayer.batch();
            connector = null;
        }

        //If there's no Shape to add then exit
        if ( wiresShape == null ) {
            dropContext.setContext( null );
            return;
        }

        //Get Shape's co-ordinates relative to the Canvas
        final double cx = getX( shapeDragCompleteEvent.getX() );
        final double cy = getY( shapeDragCompleteEvent.getY() );

        //If the Shape was dropped outside the bounds of the Canvas then exit
        if ( cx < 0 || cy < 0 ) {
            dropContext.setContext( null );
            return;
        }

        final int scrollWidth = getElement().getScrollWidth();
        final int scrollHeight = getElement().getScrollHeight();
        if ( cx > scrollWidth || cy > scrollHeight ) {
            dropContext.setContext( null );
            return;
        }

        //Add the new Node to it's parent (unless this is the first node)
        final WiresBaseTreeNode parent = dropContext.getContext();
        boolean addShape = getShapesInCanvas().size() == 0 || getShapesInCanvas().size() > 0 && parent != null;
        boolean addChildToParent = parent != null;

        if ( addShape ) {
            wiresShape.setX( cx );
            wiresShape.setY( cy );

            if ( addChildToParent ) {
                parent.addChildNode( (WiresBaseTreeNode) wiresShape );
            } else if ( wiresShape instanceof WiresBaseTreeNode ) {
                root = (WiresBaseTreeNode) wiresShape;
            }

            addShape( wiresShape );
            layout();

            //Enable clearing of Canvas now a Shape has been added
            menus.getItems().get( 0 ).setEnabled( true );

            //Notify other Panels of a Shape being added
            shapeAddedEvent.fire( new ShapeAddedEvent( wiresShape ) );
        }
    }

    private double getX( double xShapeEvent ) {
        return xShapeEvent - getAbsoluteLeft();
    }

    private double getY( double yShapeEvent ) {
        return yShapeEvent - getAbsoluteTop();
    }

    @Override
    public void clear() {
        if ( Window.confirm( "Are you sure to clean the canvas?" ) ) {
            super.clear();
            clearEvent.fire( new ClearEvent() );
            root = null;
        }
    }

    @Override
    public void deleteShape( final WiresBaseShape shape ) {
        if ( Window.confirm( "Are you sure to remove the selected shape?" ) ) {
            shapeDeletedEvent.fire( new ShapeDeletedEvent( shape ) );
            layout();
        }
    }

    @Override
    public void forceDeleteShape( final WiresBaseShape shape ) {
        shapeDeletedEvent.fire( new ShapeDeletedEvent( shape ) );
    }

    public void onShapeDeleted( @Observes ShapeDeletedEvent event ) {
        if ( root != null && root.equals( event.getShape() ) ) {
            root = null;
        }
        super.deleteShape( event.getShape() );
        menus.getItems().get( 0 ).setEnabled( getShapesInCanvas().size() > 0 );
        menus.getItems().get( 1 ).setEnabled( isShapeSelected() );
        menus.getItems().get( 2 ).setEnabled( isShapeSelected() );
        menus.getItems().get( 3 ).setEnabled( isShapeSelected() );
        menus.getItems().get( 4 ).setEnabled( isShapeSelected() );
    }

    @Override
    public void addShape( final WiresBaseShape shape ) {
        super.addShape( shape );

        //Attach relevant handlers
        if ( shape instanceof RequiresLayoutManager ) {
            ( (RequiresLayoutManager) shape ).setLayoutManager( layoutManager );
        }
    }

    protected WiresBaseTreeNode getParentNode( final WiresBaseTreeNode dragShape,
                                               final double cx,
                                               final double cy ) {
        WiresBaseTreeNode prospectiveParent = null;
        double finalDistance = Double.MAX_VALUE;
        for ( WiresBaseShape ws : getShapesInCanvas() ) {
            if ( ws.isVisible() ) {
                if ( ws instanceof WiresBaseTreeNode ) {
                    final WiresBaseTreeNode node = (WiresBaseTreeNode) ws;
                    if ( node.acceptChildNode( dragShape ) && !node.hasCollapsedChildren() ) {
                        double deltaX = cx - node.getX();
                        double deltaY = cy - node.getY();
                        double distance = Math.sqrt( Math.pow( deltaX, 2 ) + Math.pow( deltaY, 2 ) );

                        if ( finalDistance > distance ) {
                            finalDistance = distance;
                            prospectiveParent = node;
                        }
                    }
                }
            }
        }

        //If we're too far away from a parent we might as well not have a parent
        if ( finalDistance > MAX_PROXIMITY ) {
            prospectiveParent = null;
        }
        return prospectiveParent;
    }

    private void collapseNode() {
        if ( !isShapeSelected() ) {
            return;
        }
        final WiresBaseShape shape = getSelectedShape();
        if ( !( shape instanceof WiresBaseTreeNode ) ) {
            return;
        }
        final WiresBaseTreeNode node = (WiresBaseTreeNode) shape;
        node.collapse( new Command() {
            @Override
            public void execute() {
                menus.getItems().get( 3 ).setEnabled( false );
                menus.getItems().get( 4 ).setEnabled( true );
            }
        } );
    }

    private void expandNode() {
        if ( !isShapeSelected() ) {
            return;
        }
        final WiresBaseShape shape = getSelectedShape();
        if ( !( shape instanceof WiresBaseTreeNode ) ) {
            return;
        }
        final WiresBaseTreeNode node = (WiresBaseTreeNode) shape;
        node.expand( new Command() {
            @Override
            public void execute() {
                menus.getItems().get( 3 ).setEnabled( true );
                menus.getItems().get( 4 ).setEnabled( false );
            }
        } );
    }

    private boolean nodeHasChildren( final WiresBaseShape shape ) {
        if ( !( shape instanceof WiresBaseTreeNode ) ) {
            return false;
        }
        final WiresBaseTreeNode node = (WiresBaseTreeNode) shape;
        return node.hasChildren();
    }

    private boolean nodeHasCollapsedChildren( final WiresBaseShape shape ) {
        if ( !( shape instanceof WiresBaseTreeNode ) ) {
            return false;
        }
        final WiresBaseTreeNode node = (WiresBaseTreeNode) shape;
        return node.hasCollapsedChildren();
    }

    private void layout() {
        //Get layout information
        final Map<WiresBaseShape, Point2D> layout = layoutManager.getLayoutInformation( root );
        final Rectangle2D canvasBounds = WiresLayoutUtilities.alignLayoutInCanvas( layout );

        //Run an animation to move WiresBaseTreeNodes from their current position to the target position
        root.animate( AnimationTweener.EASE_OUT,
                      new AnimationProperties(),
                      ANIMATION_DURATION,
                      new IAnimationCallback() {

                          private final Map<WiresBaseShape, Pair<Point2D, Point2D>> transformations = new HashMap<WiresBaseShape, Pair<Point2D, Point2D>>();

                          @Override
                          public void onStart( final IAnimation iAnimation,
                                               final IAnimationHandle iAnimationHandle ) {
                              //Reposition nodes. First we store the WiresBaseTreeNode together with its current position and target position
                              transformations.clear();
                              for ( Map.Entry<WiresBaseShape, Point2D> e : layout.entrySet() ) {
                                  final Point2D origin = e.getKey().getLocation();
                                  final Point2D destination = e.getValue();
                                  transformations.put( e.getKey(),
                                                       new Pair<Point2D, Point2D>( origin,
                                                                                   destination ) );
                              }
                              WiresLayoutUtilities.resizeViewPort( canvasBounds,
                                                                   canvasLayer.getViewport() );
                          }

                          @Override
                          public void onFrame( final IAnimation iAnimation,
                                               final IAnimationHandle iAnimationHandle ) {
                              //Lienzo's IAnimation.getPercent() passes values > 1.0
                              final double pct = iAnimation.getPercent() > 1.0 ? 1.0 : iAnimation.getPercent();

                              //Move each descendant along the line between its origin and the target destination
                              for ( Map.Entry<WiresBaseShape, Pair<Point2D, Point2D>> e : transformations.entrySet() ) {
                                  final Point2D descendantOrigin = e.getValue().getK1();
                                  final Point2D descendantTarget = e.getValue().getK2();
                                  final double dx = ( descendantTarget.getX() - descendantOrigin.getX() ) * pct;
                                  final double dy = ( descendantTarget.getY() - descendantOrigin.getY() ) * pct;
                                  e.getKey().setX( descendantOrigin.getX() + dx );
                                  e.getKey().setY( descendantOrigin.getY() + dy );
                              }

                              //Without this call Lienzo doesn't update the Canvas for sub-classes of WiresBaseTreeNode
                              root.getLayer().batch();
                          }

                          @Override
                          public void onClose( final IAnimation iAnimation,
                                               final IAnimationHandle iAnimationHandle ) {
                              //Nothing to do
                          }
                      } );

        canvasLayer.batch();
    }

}
