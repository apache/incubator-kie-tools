/*
* Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtree.client.widget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.user.client.Window;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionRetractNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionUpdateNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.screens.guided.dtree.client.editor.GuidedDecisionTreeEditorPresenter;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionInsertNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionRetractNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionUpdateNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ConstraintNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.TypeNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.shapes.BaseGuidedDecisionTreeShape;
import org.drools.workbench.screens.guided.dtree.client.widget.shapes.TypeShape;
import org.uberfire.client.mvp.UberView;
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
import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;
import org.uberfire.ext.wires.core.trees.client.canvas.WiresTreeNodeConnector;
import org.uberfire.ext.wires.core.trees.client.layout.WiresLayoutUtilities;
import org.uberfire.ext.wires.core.trees.client.layout.treelayout.Rectangle2D;
import org.uberfire.ext.wires.core.trees.client.shapes.WiresBaseTreeNode;

public class GuidedDecisionTreeWidget extends WiresCanvas implements UberView<GuidedDecisionTreeEditorPresenter> {

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

    @Inject
    private TypeNodeFactory typeNodeFactory;

    @Inject
    private ConstraintNodeFactory constraintNodeFactory;

    @Inject
    private ActionInsertNodeFactory actionInsertNodeFactory;

    @Inject
    private ActionUpdateNodeFactory actionUpdateNodeFactory;

    @Inject
    private ActionRetractNodeFactory actionRetractNodeFactory;

    private GuidedDecisionTreeDropContext dropContext = new GuidedDecisionTreeDropContext();

    private WiresTreeNodeConnector connector = null;

    private WiresBaseTreeNode uiRoot;
    private GuidedDecisionTree model;

    private GuidedDecisionTreeEditorPresenter presenter;

    private Group hint = null;
    private boolean isGettingStartedHintVisible = false;

    @Override
    public void init( final GuidedDecisionTreeEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void selectShape( final WiresBaseShape shape ) {
        shapeSelectedEvent.fire( new ShapeSelectedEvent( shape ) );
    }

    public void onShapeSelected( @Observes ShapeSelectedEvent event ) {
        final WiresBaseShape shape = event.getShape();
        super.selectShape( shape );
    }

    @Override
    public void deselectShape( final WiresBaseShape shape ) {
        super.deselectShape( shape );
    }

    public void onDragPreviewHandler( @Observes ShapeDragPreviewEvent shapeDragPreviewEvent ) {
        //We can only connect WiresTreeNodes to each other
        if ( !( shapeDragPreviewEvent.getShape() instanceof BaseGuidedDecisionTreeShape ) ) {
            dropContext.setContext( null );
            return;
        }

        //Find a Parent Node to attach the Shape to
        final double cx = getX( shapeDragPreviewEvent.getX() );
        final double cy = getY( shapeDragPreviewEvent.getY() );
        final BaseGuidedDecisionTreeShape uiChild = (BaseGuidedDecisionTreeShape) shapeDragPreviewEvent.getShape();
        final BaseGuidedDecisionTreeShape uiProspectiveParent = getParentNode( uiChild,
                                                                               cx,
                                                                               cy );

        //If there is a prospective parent show the line between child and parent
        if ( uiProspectiveParent != null ) {
            if ( connector == null ) {
                connector = new WiresTreeNodeConnector();
                canvasLayer.add( connector );
                connector.moveToBottom();
            }
            connector.getPoints().get( 0 ).set( uiProspectiveParent.getLocation() );
            connector.getPoints().get( 1 ).set( new Point2D( cx,
                                                             cy ) );
        } else if ( connector != null ) {
            canvasLayer.remove( connector );
            connector = null;
        }

        dropContext.setContext( uiProspectiveParent );
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

        //If the Shape is not intended for the Guided Decision Tree widget then exit
        if ( !( wiresShape instanceof BaseGuidedDecisionTreeShape ) ) {
            dropContext.setContext( null );
            return;
        }
        final BaseGuidedDecisionTreeShape uiChild = (BaseGuidedDecisionTreeShape) wiresShape;

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
        final BaseGuidedDecisionTreeShape uiParent = dropContext.getContext();
        boolean addShape = ( ( getShapesInCanvas().size() == 0 && ( uiChild instanceof TypeShape ) ) || ( getShapesInCanvas().size() > 0 && uiParent != null ) );
        boolean addChildToParent = uiParent != null;

        if ( addShape ) {
            uiChild.setX( cx );
            uiChild.setY( cy );

            if ( addChildToParent ) {
                uiParent.addChildNode( uiChild );
                uiParent.getModelNode().addChild( uiChild.getModelNode() );

            } else if ( uiChild instanceof TypeShape ) {
                uiRoot = uiChild;
                model.setRoot( ( (TypeShape) uiChild ).getModelNode() );
            }

            addShape( uiChild );

            //Notify other Panels of a Shape being added
            shapeAddedEvent.fire( new ShapeAddedEvent( uiChild ) );
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
        if ( Window.confirm( GuidedDecisionTreeConstants.INSTANCE.confirmDeleteDecisionTree() ) ) {
            super.clear();
            clearEvent.fire( new ClearEvent() );
            uiRoot = null;
        }
    }

    @Override
    public void deleteShape( final WiresBaseShape shape ) {
        if ( Window.confirm( GuidedDecisionTreeConstants.INSTANCE.confirmDeleteDecisionTreeNode() ) ) {

            if ( uiRoot != null && uiRoot.equals( shape ) ) {
                uiRoot = null;
                model.setRoot( null );

            } else if ( shape instanceof BaseGuidedDecisionTreeShape ) {
                final BaseGuidedDecisionTreeShape uiChild = (BaseGuidedDecisionTreeShape) shape;
                if ( uiChild.getParentNode() instanceof BaseGuidedDecisionTreeShape ) {
                    final BaseGuidedDecisionTreeShape uiParent = (BaseGuidedDecisionTreeShape) uiChild.getParentNode();
                    uiParent.getModelNode().removeChild( uiChild.getModelNode() );
                }
            }

            shapeDeletedEvent.fire( new ShapeDeletedEvent( shape ) );

            layout();
        }
    }

    @Override
    public void forceDeleteShape( final WiresBaseShape shape ) {
        shapeDeletedEvent.fire( new ShapeDeletedEvent( shape ) );
    }

    public void onShapeDeleted( @Observes ShapeDeletedEvent event ) {
        super.deleteShape( event.getShape() );
        if ( getShapesInCanvas().isEmpty() ) {
            showGettingStartedHint();
        }
    }

    @Override
    public void addShape( final WiresBaseShape shape ) {
        super.addShape( shape );

        //Attach relevant handlers
        if ( shape instanceof RequiresLayoutManager ) {
            ( (RequiresLayoutManager) shape ).setLayoutManager( layoutManager );
        }
        if ( shape instanceof BaseGuidedDecisionTreeShape ) {
            ( (BaseGuidedDecisionTreeShape) shape ).setPresenter( presenter );
        }

        if ( !getShapesInCanvas().isEmpty() ) {
            hideGettingStartedHint();
        }

        layout();
    }

    public void setModel( final GuidedDecisionTree model,
                          final boolean isReadOnly ) {
        this.uiRoot = null;
        this.model = model;

        //Clear existing state
        super.clear();
        clearEvent.fire( new ClearEvent() );

        //Walk model creating UIModel
        final TypeNode root = model.getRoot();
        if ( root != null ) {
            final WiresBaseTreeNode uiRoot = typeNodeFactory.getShape( root,
                                                                       isReadOnly );
            this.uiRoot = uiRoot;

            processChildren( root,
                             uiRoot,
                             isReadOnly );

            final Map<WiresBaseShape, Point2D> layout = layoutManager.getLayoutInformation( uiRoot );
            final Rectangle2D canvasBounds = WiresLayoutUtilities.alignLayoutInCanvas( layout );
            for ( Map.Entry<WiresBaseShape, Point2D> e : layout.entrySet() ) {
                final Point2D destination = new Point2D( e.getValue().getX(),
                                                         e.getValue().getY() );

                e.getKey().setLocation( destination );
            }

            WiresLayoutUtilities.resizeViewPort( canvasBounds,
                                                 canvasLayer.getViewport() );
        }

        if ( shapesInCanvas.isEmpty() ) {
            showGettingStartedHint();
        }

        canvasLayer.batch();
    }

    private void processChildren( final Node node,
                                  final WiresBaseTreeNode uiNode,
                                  final boolean isReadOnly ) {
        uiNode.setSelectionManager( this );
        uiNode.setShapesManager( this );
        uiNode.setLayoutManager( layoutManager );
        if ( uiNode instanceof BaseGuidedDecisionTreeShape ) {
            ( (BaseGuidedDecisionTreeShape) uiNode ).setPresenter( presenter );
        }
        canvasLayer.add( uiNode );
        shapesInCanvas.add( uiNode );

        final Iterator<Node> itr = node.iterator();
        while ( itr.hasNext() ) {
            final Node child = itr.next();
            WiresBaseTreeNode uiChildNode = null;
            if ( child instanceof TypeNode ) {
                uiChildNode = typeNodeFactory.getShape( (TypeNode) child,
                                                        isReadOnly );

            } else if ( child instanceof ConstraintNode ) {
                uiChildNode = constraintNodeFactory.getShape( (ConstraintNode) child,
                                                              isReadOnly );

            } else if ( child instanceof ActionInsertNode ) {
                uiChildNode = actionInsertNodeFactory.getShape( (ActionInsertNode) child,
                                                                isReadOnly );

            } else if ( child instanceof ActionUpdateNode ) {
                uiChildNode = actionUpdateNodeFactory.getShape( (ActionUpdateNode) child,
                                                                isReadOnly );

            } else if ( child instanceof ActionRetractNode ) {
                uiChildNode = actionRetractNodeFactory.getShape( (ActionRetractNode) child,
                                                                 isReadOnly );
            }

            if ( uiChildNode != null ) {
                uiNode.addChildNode( uiChildNode );
                processChildren( child,
                                 uiChildNode,
                                 isReadOnly );
            }
        }
    }

    protected BaseGuidedDecisionTreeShape getParentNode( final BaseGuidedDecisionTreeShape uiChild,
                                                         final double cx,
                                                         final double cy ) {
        BaseGuidedDecisionTreeShape uiProspectiveParent = null;
        double finalDistance = Double.MAX_VALUE;
        for ( WiresBaseShape ws : getShapesInCanvas() ) {
            if ( ws.isVisible() ) {
                if ( ws instanceof BaseGuidedDecisionTreeShape ) {
                    final BaseGuidedDecisionTreeShape uiNode = (BaseGuidedDecisionTreeShape) ws;
                    if ( uiNode.acceptChildNode( uiChild ) && !uiNode.hasCollapsedChildren() ) {
                        double deltaX = cx - uiNode.getX();
                        double deltaY = cy - uiNode.getY();
                        double distance = Math.sqrt( Math.pow( deltaX, 2 ) + Math.pow( deltaY, 2 ) );

                        if ( finalDistance > distance ) {
                            finalDistance = distance;
                            uiProspectiveParent = uiNode;
                        }
                    }
                }
            }
        }

        //If we're too far away from a parent we might as well not have a parent
        if ( finalDistance > MAX_PROXIMITY ) {
            uiProspectiveParent = null;
        }
        return uiProspectiveParent;
    }

    private void layout() {
        //Get layout information
        final Map<WiresBaseShape, Point2D> layout = layoutManager.getLayoutInformation( uiRoot );
        final Rectangle2D canvasBounds = WiresLayoutUtilities.alignLayoutInCanvas( layout );

        //Run an animation to move WiresBaseTreeNodes from their current position to the target position
        uiRoot.animate( AnimationTweener.EASE_OUT,
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
                                    final Point2D destination = new Point2D( e.getValue().getX(),
                                                                             e.getValue().getY() );
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
                                uiRoot.getLayer().batch();
                            }

                            @Override
                            public void onClose( final IAnimation iAnimation,
                                                 final IAnimationHandle iAnimationHandle ) {
                                //Nothing to do
                            }
                        } );

        canvasLayer.batch();
    }

    private void showGettingStartedHint() {
        if ( isGettingStartedHintVisible ) {
            return;
        }
        if ( hint == null ) {
            hint = new Group();
            final Rectangle hintRectangle = new Rectangle( 600,
                                                           225,
                                                           15 );
            hintRectangle.setStrokeWidth( 2.0 );
            hintRectangle.setStrokeColor( "#6495ED" );
            hintRectangle.setFillColor( "#AFEEEE" );
            hintRectangle.setAlpha( 0.75 );

            final Text hintText = new Text( GuidedDecisionTreeConstants.INSTANCE.gettingStartedHint(),
                                            ShapeFactoryUtil.FONT_FAMILY_DESCRIPTION,
                                            18 );
            hintText.setTextAlign( TextAlign.CENTER );
            hintText.setTextBaseLine( TextBaseLine.MIDDLE );
            hintText.setFillColor( "#6495ED" );
            hintText.setX( hintRectangle.getWidth() / 2 );
            hintText.setY( hintRectangle.getHeight() / 2 );

            hint.setX( ( canvasLayer.getWidth() - hintRectangle.getWidth() ) / 2 );
            hint.setY( ( canvasLayer.getHeight() / 3 ) - ( hintRectangle.getHeight() / 2 ) );
            hint.add( hintRectangle );
            hint.add( hintText );
        }

        hint.animate( AnimationTweener.LINEAR,
                      new AnimationProperties(),
                      ANIMATION_DURATION,
                      new IAnimationCallback() {

                          @Override
                          public void onStart( final IAnimation iAnimation,
                                               final IAnimationHandle iAnimationHandle ) {
                              hint.setAlpha( 0.0 );
                              canvasLayer.add( hint );
                              isGettingStartedHintVisible = true;
                          }

                          @Override
                          public void onFrame( final IAnimation iAnimation,
                                               final IAnimationHandle iAnimationHandle ) {
                              //Lienzo's IAnimation.getPercent() passes values > 1.0
                              final double pct = iAnimation.getPercent() > 1.0 ? 1.0 : iAnimation.getPercent();
                              hint.setAlpha( pct );
                              hint.getLayer().batch();
                          }

                          @Override
                          public void onClose( final IAnimation iAnimation,
                                               final IAnimationHandle iAnimationHandle ) {
                              //Nothing to do
                          }
                      } );
    }

    private void hideGettingStartedHint() {
        if ( !isGettingStartedHintVisible ) {
            return;
        }
        hint.animate( AnimationTweener.LINEAR,
                      new AnimationProperties(),
                      ANIMATION_DURATION,
                      new IAnimationCallback() {

                          @Override
                          public void onStart( final IAnimation iAnimation,
                                               final IAnimationHandle iAnimationHandle ) {
                              //Nothing to do
                          }

                          @Override
                          public void onFrame( final IAnimation iAnimation,
                                               final IAnimationHandle iAnimationHandle ) {
                              //Lienzo's IAnimation.getPercent() passes values > 1.0
                              final double pct = iAnimation.getPercent() > 1.0 ? 1.0 : iAnimation.getPercent();
                              hint.setAlpha( 1 - pct );
                              hint.getLayer().batch();
                          }

                          @Override
                          public void onClose( final IAnimation iAnimation,
                                               final IAnimationHandle iAnimationHandle ) {
                              canvasLayer.remove( hint );
                              isGettingStartedHintVisible = false;
                          }
                      } );

    }

}
