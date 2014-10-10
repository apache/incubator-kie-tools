/*
 * Copyright 2014 JBoss Inc
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
package org.kie.uberfire.wires.core.trees.client.shapes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emitrom.lienzo.client.core.animation.AnimationProperties;
import com.emitrom.lienzo.client.core.animation.AnimationTweener;
import com.emitrom.lienzo.client.core.animation.IAnimation;
import com.emitrom.lienzo.client.core.animation.IAnimationCallback;
import com.emitrom.lienzo.client.core.animation.IAnimationHandle;
import com.emitrom.lienzo.client.core.event.NodeDragMoveEvent;
import com.emitrom.lienzo.client.core.event.NodeDragMoveHandler;
import com.emitrom.lienzo.client.core.shape.Group;
import com.emitrom.lienzo.client.core.types.Point2D;
import org.kie.uberfire.wires.core.api.layout.LayoutManager;
import org.kie.uberfire.wires.core.api.layout.RequiresLayoutManager;
import org.kie.uberfire.wires.core.api.shapes.RequiresShapesManager;
import org.kie.uberfire.wires.core.api.shapes.ShapesManager;
import org.kie.uberfire.wires.core.api.shapes.WiresBaseShape;
import org.kie.uberfire.wires.core.trees.client.canvas.WiresTreeNodeConnector;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.Command;

public abstract class WiresBaseTreeNode extends WiresBaseShape implements RequiresShapesManager,
                                                                          RequiresLayoutManager {

    private static final int ANIMATION_DURATION = 250;

    private WiresBaseTreeNode parent;
    private List<WiresBaseTreeNode> children = new ArrayList<WiresBaseTreeNode>();
    private List<WiresTreeNodeConnector> connectors = new ArrayList<WiresTreeNodeConnector>();

    private IAnimationHandle animationHandle;

    private int collapsed = 0;

    protected ShapesManager shapesManager;
    protected LayoutManager layoutManager;

    public WiresBaseTreeNode() {
        //Update connectors when this Node moves
        addNodeDragMoveHandler( new NodeDragMoveHandler() {

            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                for ( WiresTreeNodeConnector connector : connectors ) {
                    connector.getPoints().getPoint( 0 ).set( getLocation() );
                }
                getLayer().draw();
            }
        } );
    }

    @Override
    public void setShapesManager( final ShapesManager shapesManager ) {
        this.shapesManager = shapesManager;
    }

    @Override
    public void setLayoutManager( final LayoutManager layoutManager ) {
        this.layoutManager = layoutManager;
    }

    @Override
    public boolean contains( final double cx,
                             final double cy ) {
        return false;
    }

    @Override
    public void destroy() {
        //Remove children
        final List<WiresBaseTreeNode> cloneChildren = new ArrayList<WiresBaseTreeNode>( children );
        for ( WiresBaseTreeNode child : cloneChildren ) {
            shapesManager.forceDeleteShape( child );
        }
        children.clear();

        //Remove connectors to children
        final List<WiresTreeNodeConnector> cloneConnectors = new ArrayList<WiresTreeNodeConnector>( connectors );
        for ( WiresTreeNodeConnector connector : cloneConnectors ) {
            getLayer().remove( connector );
        }
        connectors.clear();

        //Remove from its parent
        if ( parent != null ) {
            parent.removeChildNode( this );
        }
        super.destroy();
    }

    public WiresBaseTreeNode getParentNode() {
        return this.parent;
    }

    public void setParentNode( final WiresBaseTreeNode parent ) {
        this.parent = parent;
    }

    /**
     * TreeNodes can decide to accept child TreeNodes when being dragged from the Palette to a prospective parent
     * @param child TreeNode that will be added to this TreeNode as a child
     * @return true if the child can be added to this TreeNode
     */
    public boolean acceptChildNode( final WiresBaseTreeNode child ) {
        //Accept all types of WiresBaseTreeNode by default
        return true;
    }

    /**
     * Add a TreeNode as a child to this TreeNode. A connector is automatically created and maintained for the child.
     * Connectors are "outgoing" from the parent to a child.
     * @param child
     */
    public void addChildNode( final WiresBaseTreeNode child ) {
        final WiresTreeNodeConnector connector = new WiresTreeNodeConnector();
        connector.getPoints().getPoint( 0 ).set( getLocation() );
        connector.getPoints().getPoint( 1 ).set( child.getLocation() );
        getLayer().add( connector );
        connector.moveToBottom();

        final int index = getChildIndex( connector );
        children.add( index,
                      child );
        connectors.add( index,
                        connector );
        child.setParentNode( this );

        //Update connectors when child Node moves
        child.addNodeDragMoveHandler( new NodeDragMoveHandler() {
            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                connector.getPoints().getPoint( 1 ).set( child.getLocation() );
            }
        } );
    }

    //Get the index of the new child connector by determining the angle of existing connectors it lays in between
    private int getChildIndex( final WiresTreeNodeConnector newConnector ) {
        final double newConnectorTheta = getConnectorAngle( newConnector );
        for ( int index = 0; index < connectors.size(); index++ ) {
            final WiresTreeNodeConnector existingConnector = connectors.get( index );
            final double existingConnectorTheta = getConnectorAngle( existingConnector );
            if ( newConnectorTheta > existingConnectorTheta ) {
                return index;
            }
        }
        return connectors.size();
    }

    private double getConnectorAngle( final WiresTreeNodeConnector connector ) {
        final double cdx = connector.getPoints().getPoint( 1 ).getX() - connector.getPoints().getPoint( 0 ).getX();
        final double cdy = connector.getPoints().getPoint( 1 ).getY() - connector.getPoints().getPoint( 0 ).getY();
        final double theta = Math.atan2( cdy,
                                         cdx ) + Math.PI / 2;
        return ( theta < 0 ? theta + ( 2 * Math.PI ) : theta );
    }

    /**
     * Remove a child TreeNode from this TreeNode. Connectors are automatically cleared up.
     * @param child
     */
    public void removeChildNode( final WiresBaseTreeNode child ) {
        child.setParentNode( null );
        final int index = children.indexOf( child );
        final WiresTreeNodeConnector connector = connectors.get( index );
        children.remove( child );
        connectors.remove( connector );
        getLayer().remove( connector );
    }

    public List<WiresBaseTreeNode> getChildren() {
        return this.children;
    }

    public abstract double getWidth();

    public abstract double getHeight();

    private void childMoved( final WiresBaseTreeNode child,
                             final double nx,
                             final double ny ) {
        final int index = children.indexOf( child );
        final WiresTreeNodeConnector connector = connectors.get( index );
        connector.getPoints().getPoint( 1 ).setX( nx );
        connector.getPoints().getPoint( 1 ).setY( ny );
    }

    /**
     * Collapse this TreeNode and all descendants.
     * @param callback The callback is invoked when the animation completes.
     */
    public void collapse( final Command callback ) {
        //This TreeNode is already collapsed
        if ( !hasChildren() || hasCollapsedChildren() ) {
            return;
        }
        if ( animationHandle != null ) {
            animationHandle.stop();
        }
        animationHandle = animate( AnimationTweener.EASE_OUT,
                                   new AnimationProperties(),
                                   ANIMATION_DURATION,
                                   new IAnimationCallback() {

                                       private List<WiresBaseTreeNode> descendants;
                                       private Map<WiresBaseShape, Pair<Point2D, Point2D>> transformations = new HashMap<WiresBaseShape, Pair<Point2D, Point2D>>();

                                       @Override
                                       public void onStart( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           //Mark all descendants as collapsed, which affects the layout information
                                           descendants = getDescendants( WiresBaseTreeNode.this );
                                           for ( WiresBaseTreeNode descendant : descendants ) {
                                               descendant.collapsed++;
                                           }

                                           //Get new layout information
                                           final Map<WiresBaseShape, Point2D> layout = layoutManager.getLayoutInformation( getTreeRoot(),
                                                                                                                           WiresBaseTreeNode.this.getLayer() );

                                           //Store required transformations: Shape, Current location, Target location
                                           transformations.clear();
                                           for ( Map.Entry<WiresBaseShape, Point2D> e : layout.entrySet() ) {
                                               transformations.put( e.getKey(),
                                                                    new Pair<Point2D, Point2D>( e.getKey().getLocation(),
                                                                                                e.getValue() ) );
                                           }

                                           //Allow subclasses to change their appearance
                                           onCollapseStart();
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

                                           for ( WiresBaseTreeNode descendant : descendants ) {
                                               descendant.setAlpha( 1.0 - pct );
                                           }

                                           //Allow subclasses to change their appearance
                                           onCollapseProgress( pct );

                                           //Without this call Lienzo doesn't update the Canvas for sub-classes of WiresBaseTreeNode
                                           WiresBaseTreeNode.this.getLayer().draw();
                                       }

                                       @Override
                                       public void onClose( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           //Hide connectors, descendants and descendants' connectors when complete
                                           for ( WiresTreeNodeConnector connector : connectors ) {
                                               connector.setVisible( false );
                                           }
                                           for ( WiresBaseTreeNode descendant : descendants ) {
                                               descendant.setVisible( false );
                                               for ( WiresTreeNodeConnector connector : descendant.connectors ) {
                                                   connector.setVisible( false );
                                               }
                                           }

                                           //Allow subclasses to change their appearance
                                           onCollapseEnd();

                                           //Invoke callback if one was provided
                                           if ( callback != null ) {
                                               callback.execute();
                                           }
                                       }
                                   } );

        getLayer().draw();
    }

    /**
     * Called when the TreeNode is about to be collapsed. Default implementation does nothing.
     */
    public void onCollapseStart() {
        //Do nothing by default
    }

    /**
     * Called while the TreeNode is being collapsed. Default implementation does nothing.
     * @param pct 0.0 to 1.0 where 1.0 is collapsed
     */
    public void onCollapseProgress( final double pct ) {
        //Do nothing by default
    }

    /**
     * Called when the TreeNode has been collapsed. Default implementation does nothing.
     */
    public void onCollapseEnd() {
        //Do nothing by default
    }

    /**
     * Expand this TreeNode and all descendants. Nested collapsed TreeNodes are not expanded.
     * @param callback The callback is invoked when the animation completes.
     */
    public void expand( final Command callback ) {
        //This TreeNode is already expanded
        if ( !hasCollapsedChildren() ) {
            return;
        }
        if ( animationHandle != null ) {
            animationHandle.stop();
        }
        animationHandle = animate( AnimationTweener.EASE_OUT,
                                   new AnimationProperties(),
                                   ANIMATION_DURATION,
                                   new IAnimationCallback() {

                                       private List<WiresBaseTreeNode> descendants;
                                       private Map<WiresBaseShape, Pair<Point2D, Point2D>> transformations = new HashMap<WiresBaseShape, Pair<Point2D, Point2D>>();

                                       @Override
                                       public void onStart( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           //Show connectors to this node's immediate children
                                           for ( WiresTreeNodeConnector connector : connectors ) {
                                               connector.setVisible( true );
                                           }

                                           //Show child nodes and connectors if they are not still collapsed
                                           descendants = getDescendants( WiresBaseTreeNode.this );
                                           for ( WiresBaseTreeNode descendant : descendants ) {
                                               descendant.collapsed--;
                                               if ( descendant.collapsed == 0 ) {
                                                   descendant.setVisible( true );
                                               }
                                           }
                                           for ( WiresBaseTreeNode descendant : descendants ) {
                                               for ( WiresTreeNodeConnector connector : descendant.connectors ) {
                                                   connector.setVisible( !descendant.hasCollapsedChildren() );
                                               }
                                           }

                                           //Get new layout information
                                           final Map<WiresBaseShape, Point2D> layout = layoutManager.getLayoutInformation( getTreeRoot(),
                                                                                                                           WiresBaseTreeNode.this.getLayer() );

                                           //Store required transformations: Shape, Current location, Target location
                                           transformations.clear();
                                           for ( Map.Entry<WiresBaseShape, Point2D> e : layout.entrySet() ) {
                                               transformations.put( e.getKey(),
                                                                    new Pair<Point2D, Point2D>( e.getKey().getLocation(),
                                                                                                e.getValue() ) );
                                           }

                                           //Allow subclasses to change their appearance
                                           onExpandStart();
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

                                           for ( WiresBaseTreeNode descendant : descendants ) {
                                               descendant.setAlpha( pct );
                                           }

                                           //Allow subclasses to change their appearance
                                           onExpandProgress( pct );

                                           //Without this call Lienzo doesn't update the Canvas for sub-classes of WiresBaseTreeNode
                                           WiresBaseTreeNode.this.getLayer().draw();
                                       }

                                       @Override
                                       public void onClose( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           //Allow subclasses to change their appearance
                                           onExpandEnd();

                                           //Invoke callback if one was provided
                                           if ( callback != null ) {
                                               callback.execute();
                                           }
                                       }
                                   } );

        getLayer().draw();
    }

    /**
     * Get the root node for the tree in which this node exists
     * @return The root
     */
    private WiresBaseTreeNode getTreeRoot() {
        WiresBaseTreeNode root = this;
        while ( root.parent != null ) {
            root = root.parent;
        }
        return root;
    }

    /**
     * Called when the TreeNode is about to be expanded. Default implementation does nothing.
     */
    public void onExpandStart() {
        //Do nothing by default
    }

    /**
     * Called while the TreeNode is being expanded. Default implementation does nothing.
     * @param pct 0.0 to 1.0 where 1.0 is expanded
     */
    public void onExpandProgress( final double pct ) {
        //Do nothing by default
    }

    /**
     * Called when the TreeNode has been expanded. Default implementation does nothing.
     */
    public void onExpandEnd() {
        //Do nothing by default
    }

    protected List<WiresBaseTreeNode> getDescendants( final WiresBaseTreeNode node ) {
        final List<WiresBaseTreeNode> descendants = new ArrayList<WiresBaseTreeNode>();
        descendants.addAll( node.children );
        for ( WiresBaseTreeNode child : node.children ) {
            descendants.addAll( getDescendants( child ) );
        }
        return descendants;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public boolean hasCollapsedChildren() {
        for ( WiresBaseTreeNode child : children ) {
            if ( child.collapsed > 0 ) {
                return true;
            }
        }
        return false;
    }

    //Move the Connector end-points to match where the descendant has been moved
    private void updateConnectorsEndPoints() {
        if ( connectors == null ) {
            return;
        }
        for ( WiresTreeNodeConnector connector : connectors ) {
            connector.getPoints().getPoint( 0 ).setX( getX() );
            connector.getPoints().getPoint( 0 ).setY( getY() );
        }
        if ( parent != null ) {
            parent.childMoved( this,
                               getX(),
                               getY() );
        }
    }

    @Override
    public Group setX( final double x ) {
        final Group g = super.setX( x );
        updateConnectorsEndPoints();
        return g;
    }

    @Override
    public Group setY( final double y ) {
        final Group g = super.setY( y );
        updateConnectorsEndPoints();
        return g;
    }

    @Override
    public Group setLocation( final Point2D p ) {
        final Group g = super.setLocation( p );
        updateConnectorsEndPoints();
        return g;
    }

}
