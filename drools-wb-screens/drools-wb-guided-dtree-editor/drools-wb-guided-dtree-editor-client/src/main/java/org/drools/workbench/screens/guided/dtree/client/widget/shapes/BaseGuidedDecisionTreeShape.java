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
package org.drools.workbench.screens.guided.dtree.client.widget.shapes;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.image.PictureLoadedHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.resources.client.ImageResource;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.screens.guided.dtree.client.editor.GuidedDecisionTreeEditorPresenter;
import org.drools.workbench.screens.guided.dtree.client.resources.GuidedDecisionTreeResources;
import org.uberfire.ext.wires.core.trees.client.shapes.WiresBaseTreeNode;
import org.uberfire.mvp.Command;

public abstract class BaseGuidedDecisionTreeShape<T extends Node> extends WiresBaseTreeNode {

    private static final int BOUNDARY_SIZE = 10;

    private final Circle circle;
    private final Circle bounding;

    protected final Text plus = new Text( "+",
                                          "normal",
                                          50 );
    protected NodeLabel nodeLabel = new NodeLabel();

    private Group ctrlGroupDeleteIcon;
    private Group ctrlGroupEditIcon;
    private Group ctrlGroupCollapseIcon;
    private Group ctrlGroupExpandIcon;

    protected final T node;
    protected boolean isReadOnly;

    private GuidedDecisionTreeEditorPresenter presenter;

    public BaseGuidedDecisionTreeShape( final Circle shape,
                                        final T node,
                                        final boolean isReadOnly ) {
        this.circle = shape;
        this.node = node;
        this.isReadOnly = isReadOnly;

        bounding = new Circle( circle.getRadius() + ( BOUNDARY_SIZE / 2 ) );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( 0.1 );

        plus.setTextAlign( TextAlign.CENTER );
        plus.setTextBaseLine( TextBaseLine.MIDDLE );
        plus.setStrokeWidth( 2 );

        add( circle );
        add( nodeLabel );

        nodeLabel.addNodeMouseClickHandler( new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick( final NodeMouseClickEvent nodeMouseClickEvent ) {
                selectionManager.selectShape( BaseGuidedDecisionTreeShape.this );
            }
        } );

        if ( !isReadOnly ) {
            setupControls();
        }
    }

    public void setPresenter( final GuidedDecisionTreeEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    protected void setupControls() {
        ctrlGroupDeleteIcon = setupControl( GuidedDecisionTreeResources.INSTANCE.images().ctrlDelete(),
                                            new Command() {
                                                @Override
                                                public void execute() {
                                                    shapesManager.deleteShape( BaseGuidedDecisionTreeShape.this );
                                                }
                                            } );
        ctrlGroupEditIcon = setupControl( GuidedDecisionTreeResources.INSTANCE.images().ctrlEdit(),
                                          new Command() {
                                              @Override
                                              public void execute() {
                                                  presenter.editModelNode( BaseGuidedDecisionTreeShape.this.getModelNode(),
                                                                           new Command() {
                                                                               @Override
                                                                               public void execute() {
                                                                                   updateLabels( BaseGuidedDecisionTreeShape.this );
                                                                                   BaseGuidedDecisionTreeShape.this.getLayer().batch();
                                                                               }

                                                                               private void updateLabels( final BaseGuidedDecisionTreeShape parent ) {
                                                                                   parent.setNodeLabel( parent.getNodeLabel() );
                                                                                   for ( WiresBaseTreeNode child : parent.getChildren() ) {
                                                                                       if ( child instanceof BaseGuidedDecisionTreeShape ) {
                                                                                           final BaseGuidedDecisionTreeShape cs = (BaseGuidedDecisionTreeShape) child;
                                                                                           cs.setNodeLabel( cs.getNodeLabel() );
                                                                                           updateLabels( cs );
                                                                                       }
                                                                                   }
                                                                               }

                                                                           } );
                                              }
                                          } );

        ctrlGroupCollapseIcon = setupControl( GuidedDecisionTreeResources.INSTANCE.images().ctrlCollapse(),
                                              new Command() {
                                                  @Override
                                                  public void execute() {
                                                      BaseGuidedDecisionTreeShape.this.collapse( new Command() {
                                                          @Override
                                                          public void execute() {
                                                              //Nothing to do when the animation completes
                                                          }
                                                      } );
                                                      final List<Group> controls = new ArrayList<Group>() {{
                                                          add( ctrlGroupDeleteIcon );
                                                          add( ctrlGroupExpandIcon );
                                                      }};
                                                      BaseGuidedDecisionTreeShape.this.setControls( controls );
                                                  }
                                              } );

        ctrlGroupExpandIcon = setupControl( GuidedDecisionTreeResources.INSTANCE.images().ctrlExpand(),
                                            new Command() {
                                                @Override
                                                public void execute() {
                                                    BaseGuidedDecisionTreeShape.this.expand( new Command() {
                                                        @Override
                                                        public void execute() {
                                                            //Nothing to do when the animation completes
                                                        }
                                                    } );
                                                    final List<Group> controls = new ArrayList<Group>() {{
                                                        add( ctrlGroupDeleteIcon );
                                                        add( ctrlGroupEditIcon );
                                                        add( ctrlGroupCollapseIcon );
                                                    }};
                                                    BaseGuidedDecisionTreeShape.this.setControls( controls );
                                                }
                                            } );

        controls.add( ctrlGroupDeleteIcon );
        controls.add( ctrlGroupEditIcon );
    }

    protected Group setupControl( final ImageResource resource,
                                  final Command command ) {
        final Group controlGroup = new Group();
        final Picture picture = new Picture( resource,
                                             false );
        picture.onLoaded( new PictureLoadedHandler() {

            @Override
            public void onPictureLoaded( Picture picture ) {
                //This is a hack for Lienzo 1.2 (and possibly 2.x?). There is a bug in Picture when
                //we want to add it to Lienzo's SelectionLayer. We work around it here by adding
                //the Picture to a Group containing a "near invisible" Rectangle that we use to
                //capture the NodeMouseClickEvents.
                final double offsetX = -picture.getImageData().getWidth() / 2;
                final double offsetY = -picture.getImageData().getHeight() / 2;
                final Rectangle selector = new Rectangle( picture.getImageData().getWidth(),
                                                          picture.getImageData().getHeight() );
                selector.setFillColor( Color.rgbToBrowserHexColor( 255,
                                                                   255,
                                                                   255 ) );
                selector.setAlpha( 0.01 );
                selector.setLocation( new Point2D( offsetX,
                                                   offsetY ) );
                picture.setLocation( new Point2D( offsetX,
                                                  offsetY ) );
                controlGroup.add( picture );
                controlGroup.add( selector );
            }
        } );

        controlGroup.addNodeMouseClickHandler( new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick( final NodeMouseClickEvent nodeMouseClickEvent ) {
                command.execute();
            }
        } );
        return controlGroup;
    }

    /**
     * Set the label shown for this Node.
     * @param label
     */
    protected void setNodeLabel( final String label ) {
        nodeLabel.setLabel( label );
    }

    /**
     * Get a String for the Node
     * @return
     */
    protected abstract String getNodeLabel();

    @Override
    public void setSelected( final boolean isSelected ) {
        if ( isSelected ) {
            add( bounding );
            if ( !isReadOnly ) {
                showControls();
            }

        } else {
            remove( bounding );
            if ( !isReadOnly ) {
                hideControls();
            }
        }
    }

    @Override
    public void addChildNode( final WiresBaseTreeNode child ) {
        final boolean hasChildren = hasChildren();
        super.addChildNode( child );
        if ( !hasChildren && hasChildren() ) {
            addControl( ctrlGroupCollapseIcon );
        }
    }

    @Override
    protected Point2D getControlTarget( final Group ctrl ) {
        final Point2D target = super.getControlTarget( ctrl );
        target.setX( circle.getRadius() + 25 );
        return target;
    }

    @Override
    public void onCollapseStart() {
        add( plus );
        nodeLabel.moveToTop();
        plus.setAlpha( 0.0 );
    }

    @Override
    public void onCollapseProgress( final double pct ) {
        plus.setAlpha( pct );
    }

    @Override
    public void onExpandProgress( double pct ) {
        plus.setAlpha( 1.0 - pct );
    }

    @Override
    public void onExpandEnd() {
        remove( plus );
    }

    @Override
    public double getWidth() {
        return circle.getRadius() * 2;
    }

    @Override
    public double getHeight() {
        return circle.getRadius() * 2;
    }

    public T getModelNode() {
        return node;
    }

}
