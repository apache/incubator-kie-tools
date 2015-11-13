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
package org.uberfire.ext.wires.core.api.shapes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.wires.core.api.selection.RequiresSelectionManager;
import org.uberfire.ext.wires.core.api.selection.SelectionManager;

/**
 * A Fixed Shape that cannot be re-sized or have connectors attached
 */
public abstract class WiresBaseShape extends Group implements WiresShape,
                                                              RequiresSelectionManager {

    protected String id;
    protected SelectionManager selectionManager;

    protected List<Group> controls = new ArrayList<Group>();
    protected boolean isControlsVisible = false;

    private static final int ANIMATION_DURATION = 250;
    private static final int DEFAULT_CONTROL_SPACING = 30;
    private static final int DEFAULT_CONTROL_POSITION_X_OFFSET = 100;
    private static final int DEFAULT_CONTROL_POSITION_Y_OFFSET = 0;

    private IAnimationHandle animationHandle;

    public WiresBaseShape() {
        id = UUID.uuid();
        setDraggable( true );

        //Clicking the Group selects the Shape
        addNodeMouseClickHandler( new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick( final NodeMouseClickEvent nodeMouseClickEvent ) {
                selectionManager.selectShape( WiresBaseShape.this );
            }
        } );

        //Update Control positions when Shape is dragged
        addNodeDragMoveHandler( new NodeDragMoveHandler() {
            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                updateControlLocations();
            }
        } );
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setSelectionManager( final SelectionManager manager ) {
        this.selectionManager = manager;
    }

    @Override
    public void showControls() {
        if ( controls == null || controls.isEmpty() ) {
            return;
        }
        if ( isControlsVisible ) {
            return;
        }
        if ( animationHandle != null ) {
            animationHandle.stop();
        }
        isControlsVisible = true;
        animationHandle = animate( AnimationTweener.EASE_OUT,
                                   new AnimationProperties(),
                                   ANIMATION_DURATION,
                                   new IAnimationCallback() {

                                       private final AnimationTweener tweener = AnimationTweener.TweenerBuilder.MAKE_ELASTIC( 1 );
                                       private final Map<Group, Pair<Point2D, Point2D>> transformations = new HashMap<Group, Pair<Point2D, Point2D>>();

                                       @Override
                                       public void onStart( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           //Store required transformations: Control, Current location, Target location
                                           transformations.clear();
                                           for ( int index = 0; index < controls.size(); index++ ) {
                                               final Group ctrl = controls.get( index );
                                               final Point2D origin = new Point2D( 0,
                                                                                   0 );
                                               final Point2D target = getControlTarget( ctrl );
                                               transformations.put( ctrl,
                                                                    new Pair<Point2D, Point2D>( origin,
                                                                                                target ) );
                                               WiresBaseShape.this.getLayer().add( ctrl );
                                               ctrl.setLocation( origin );
                                               ctrl.setAlpha( 0.0 );
                                           }
                                       }

                                       @Override
                                       public void onFrame( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           final double pct = tweener.apply( iAnimation.getPercent() > 1.0 ? 1.0 : iAnimation.getPercent() );

                                           //Move each Control along the line between its origin and the target destination
                                           for ( Map.Entry<Group, Pair<Point2D, Point2D>> e : transformations.entrySet() ) {
                                               final Point2D origin = e.getValue().getK1();
                                               final Point2D target = e.getValue().getK2();
                                               final double dx = ( target.getX() - origin.getX() ) * pct;
                                               final double dy = ( target.getY() - origin.getY() ) * pct;
                                               e.getKey().setLocation( new Point2D( origin.getX() + dx,
                                                                                    origin.getY() + dy ).add( WiresBaseShape.this.getLocation() ) );
                                           }

                                           for ( Group ctrl : controls ) {
                                               ctrl.setAlpha( iAnimation.getPercent() );
                                           }

                                           //Without this call Lienzo doesn't update the Canvas for sub-classes of WiresBaseTreeNode
                                           WiresBaseShape.this.getLayer().batch();
                                       }

                                       @Override
                                       public void onClose( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           //Do nothing
                                       }
                                   } );
    }

    @Override
    public void hideControls() {
        if ( controls == null || controls.isEmpty() ) {
            return;
        }
        if ( !isControlsVisible ) {
            return;
        }
        if ( animationHandle != null ) {
            animationHandle.stop();
        }
        isControlsVisible = false;
        animationHandle = animate( AnimationTweener.EASE_OUT,
                                   new AnimationProperties(),
                                   ANIMATION_DURATION,
                                   new IAnimationCallback() {

                                       private final AnimationTweener tweener = AnimationTweener.TweenerBuilder.MAKE_EASE_IN( 3.0 );
                                       private final Map<Group, Pair<Point2D, Point2D>> transformations = new HashMap<Group, Pair<Point2D, Point2D>>();

                                       @Override
                                       public void onStart( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           //Store required transformations: Control, Current location, Target location
                                           transformations.clear();
                                           for ( int index = 0; index < controls.size(); index++ ) {
                                               final Group ctrl = controls.get( index );
                                               final Point2D origin = ctrl.getLocation();
                                               origin.minus( WiresBaseShape.this.getLocation() );
                                               final Point2D target = new Point2D( 0,
                                                                                   0 );
                                               transformations.put( ctrl,
                                                                    new Pair<Point2D, Point2D>( origin,
                                                                                                target ) );
                                           }
                                       }

                                       @Override
                                       public void onFrame( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           final double pct = tweener.apply( iAnimation.getPercent() > 1.0 ? 1.0 : iAnimation.getPercent() );

                                           //Move each Control along the line between its origin and the target destination
                                           for ( Map.Entry<Group, Pair<Point2D, Point2D>> e : transformations.entrySet() ) {
                                               final Point2D origin = e.getValue().getK1();
                                               final Point2D target = e.getValue().getK2();
                                               final double dx = ( target.getX() - origin.getX() ) * pct;
                                               final double dy = ( target.getY() - origin.getY() ) * pct;
                                               e.getKey().setLocation( new Point2D( origin.getX() + dx,
                                                                                    origin.getY() + dy ).add( WiresBaseShape.this.getLocation() ) );
                                           }

                                           for ( Group ctrl : controls ) {
                                               ctrl.setAlpha( 1.0 - iAnimation.getPercent() );
                                           }

                                           //Without this call Lienzo doesn't update the Canvas for sub-classes of WiresBaseTreeNode
                                           WiresBaseShape.this.getLayer().batch();
                                       }

                                       @Override
                                       public void onClose( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           for ( Group ctrl : controls ) {
                                               WiresBaseShape.this.getLayer().remove( ctrl );
                                           }
                                       }
                                   } );
    }

    @Override
    public void addControl( final Group ctrlToAdd ) {
        if ( !isControlsVisible ) {
            controls.add( ctrlToAdd );
            return;
        }
        final List<Group> newControls = new ArrayList<Group>( controls );
        newControls.add( ctrlToAdd );
        setControls( newControls );
    }

    @Override
    public void removeControl( final Group ctrlToRemove ) {
        if ( !isControlsVisible ) {
            controls.remove( ctrlToRemove );
            return;
        }
        final List<Group> newControls = new ArrayList<Group>( controls );
        newControls.remove( ctrlToRemove );
        setControls( newControls );
    }

    @Override
    public void setControls( final List<Group> newControls ) {
        if ( !isControlsVisible ) {
            controls.clear();
            controls.addAll( newControls );
            return;
        }
        if ( animationHandle != null ) {
            animationHandle.stop();
        }
        animationHandle = animate( AnimationTweener.EASE_OUT,
                                   new AnimationProperties(),
                                   ANIMATION_DURATION,
                                   new IAnimationCallback() {

                                       private final List<Group> controlsToAdd = new ArrayList<Group>();
                                       private final List<Group> controlsToRemove = new ArrayList<Group>();
                                       private final List<Group> controlsToRemain = new ArrayList<Group>();
                                       private final AnimationTweener tweener = AnimationTweener.TweenerBuilder.MAKE_ELASTIC( 1 );
                                       private final Map<Group, Pair<Point2D, Point2D>> transformations = new HashMap<Group, Pair<Point2D, Point2D>>();

                                       @Override
                                       public void onStart( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           //Initialise new Controls being added
                                           controlsToAdd.clear();
                                           controlsToAdd.addAll( newControls );
                                           controlsToAdd.removeAll( controls );
                                           for ( Group ctrl : controlsToAdd ) {
                                               ctrl.setLocation( new Point2D( 0,
                                                                              0 ) );
                                               ctrl.setAlpha( 0.0 );
                                               WiresBaseShape.this.getLayer().add( ctrl );
                                           }

                                           //Initialise new Controls being removed
                                           controlsToRemove.clear();
                                           controlsToRemove.addAll( controls );
                                           controlsToRemove.removeAll( newControls );

                                           //Initialise remaining Controls
                                           controlsToRemain.clear();
                                           controlsToRemain.addAll( controls );
                                           controlsToRemain.removeAll( controlsToAdd );
                                           controlsToRemain.removeAll( controlsToRemove );

                                           //Store required transformations: Control, Current location, Target location
                                           controls.clear();
                                           controls.addAll( newControls );
                                           transformations.clear();
                                           for ( Group ctrl : controlsToAdd ) {
                                               final Point2D origin = new Point2D( 0,
                                                                                   0 );
                                               final Point2D target = getControlTarget( ctrl );
                                               transformations.put( ctrl,
                                                                    new Pair<Point2D, Point2D>( origin,
                                                                                                target ) );
                                           }
                                           for ( Group ctrl : controlsToRemove ) {
                                               final Point2D origin = ctrl.getLocation();
                                               origin.minus( WiresBaseShape.this.getLocation() );
                                               final Point2D target = new Point2D( 0,
                                                                                   0 );
                                               transformations.put( ctrl,
                                                                    new Pair<Point2D, Point2D>( origin,
                                                                                                target ) );
                                           }
                                           for ( Group ctrl : controlsToRemain ) {
                                               final Point2D origin = ctrl.getLocation();
                                               origin.minus( WiresBaseShape.this.getLocation() );
                                               final Point2D target = getControlTarget( ctrl );
                                               transformations.put( ctrl,
                                                                    new Pair<Point2D, Point2D>( origin,
                                                                                                target ) );
                                           }
                                       }

                                       @Override
                                       public void onFrame( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           final double pct = tweener.apply( iAnimation.getPercent() > 1.0 ? 1.0 : iAnimation.getPercent() );

                                           //Move each Control along the line between its origin and the target destination
                                           for ( Map.Entry<Group, Pair<Point2D, Point2D>> e : transformations.entrySet() ) {
                                               final Point2D origin = e.getValue().getK1();
                                               final Point2D target = e.getValue().getK2();
                                               final double dx = ( target.getX() - origin.getX() ) * pct;
                                               final double dy = ( target.getY() - origin.getY() ) * pct;
                                               e.getKey().setLocation( new Point2D( origin.getX() + dx,
                                                                                    origin.getY() + dy ).add( WiresBaseShape.this.getLocation() ) );
                                           }

                                           for ( Group ctrl : controlsToAdd ) {
                                               ctrl.setAlpha( pct );
                                           }
                                           for ( Group ctrl : controlsToRemove ) {
                                               ctrl.setAlpha( 1.0 - pct );
                                           }

                                           //Without this call Lienzo doesn't update the Canvas for sub-classes of WiresBaseTreeNode
                                           WiresBaseShape.this.getLayer().batch();
                                       }

                                       @Override
                                       public void onClose( final IAnimation iAnimation,
                                                            final IAnimationHandle iAnimationHandle ) {
                                           isControlsVisible = !controls.isEmpty();
                                           for ( Group ctrl : controlsToRemove ) {
                                               WiresBaseShape.this.getLayer().remove( ctrl );
                                           }
                                       }
                                   } );
    }

    @Override
    public boolean isControlsVisible() {
        return this.isControlsVisible;
    }

    /**
     * Returns a Point (relative to the WiresShape) where a Control should be placed when Controls
     * are shown. This default implementation places all Controls at WiresShape.getX()+100 and evenly
     * spaces the Controls vertically with centres spaced 30px
     * @param ctrl The Control to position
     * @return The position of the Control
     */
    protected Point2D getControlTarget( final Group ctrl ) {
        final int offsetY = -( ( controls.size() - 1 ) * DEFAULT_CONTROL_SPACING ) / 2;
        final Point2D target = new Point2D( DEFAULT_CONTROL_POSITION_X_OFFSET,
                                            DEFAULT_CONTROL_POSITION_Y_OFFSET + offsetY + ( controls.indexOf( ctrl ) * DEFAULT_CONTROL_SPACING ) );
        return target;
    }

    @Override
    public void destroy() {
        if ( isControlsVisible ) {
            for ( Group ctrl : controls ) {
                getLayer().remove( ctrl );
            }
            isControlsVisible = false;
        }
        Layer layer = getLayer();
        layer.remove( this );
        layer.batch();
    }

    //Move the Controls to match where the descendant has been moved
    private void updateControlLocations() {
        if ( controls == null ) {
            return;
        }
        if ( !isControlsVisible ) {
            return;
        }
        for ( Group ctrl : controls ) {
            final Point2D target = getControlTarget( ctrl ).add( WiresBaseShape.this.getLocation() );
            ctrl.setLocation( target );
        }
    }

    //Move the Controls to match where the descendant has been moved
    private void updateControlLocations( final double dx,
                                         final double dy ) {
        if ( controls == null ) {
            return;
        }
        if ( !isControlsVisible ) {
            return;
        }
        for ( Group ctrl : controls ) {
            ctrl.setLocation( ctrl.getLocation().add( new Point2D( dx,
                                                                   dy ) ) );
        }
    }

    @Override
    public Group setX( final double x ) {
        final double dx = x - super.getX();
        final Group g = super.setX( x );
        updateControlLocations( dx,
                                0 );
        return g;
    }

    @Override
    public Group setY( final double y ) {
        final double dy = y - super.getY();
        final Group g = super.setY( y );
        updateControlLocations( 0,
                                dy );
        return g;
    }

}
