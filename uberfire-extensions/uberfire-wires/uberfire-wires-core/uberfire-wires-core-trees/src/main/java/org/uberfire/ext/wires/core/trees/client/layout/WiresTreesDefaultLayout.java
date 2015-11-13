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
package org.uberfire.ext.wires.core.trees.client.layout;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.api.layout.LayoutManager;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.trees.client.layout.treelayout.AbstractTreeForTreeLayout;
import org.uberfire.ext.wires.core.trees.client.layout.treelayout.Configuration;
import org.uberfire.ext.wires.core.trees.client.layout.treelayout.DefaultConfiguration;
import org.uberfire.ext.wires.core.trees.client.layout.treelayout.NodeExtentProvider;
import org.uberfire.ext.wires.core.trees.client.layout.treelayout.Rectangle2D;
import org.uberfire.ext.wires.core.trees.client.layout.treelayout.TreeLayout;
import org.uberfire.ext.wires.core.trees.client.shapes.WiresBaseTreeNode;

/**
 * Default Tree layout using an adaptation of https://code.google.com/p/treelayout/ for use with GWT
 */
public class WiresTreesDefaultLayout implements LayoutManager {

    @Override
    public Map<WiresBaseShape, Point2D> getLayoutInformation( final WiresBaseShape root ) {
        if ( root == null ) {
            return Collections.emptyMap();
        }
        if ( !( root instanceof WiresBaseTreeNode ) ) {
            return Collections.emptyMap();
        }

        //Layout tree
        final WiresBaseTreeNode treeRootNode = (WiresBaseTreeNode) root;
        final WiresTreeForTreeLayout treeNodesProvider = new WiresTreeForTreeLayout( treeRootNode );
        final WiresTreeNodeExtentProvider treeNodesExtentProvider = new WiresTreeNodeExtentProvider();
        final Configuration<WiresBaseTreeNode> treeNodesLayoutConfiguration = new DefaultConfiguration<WiresBaseTreeNode>( 50,
                                                                                                                           50 );
        final TreeLayout<WiresBaseTreeNode> layout = new TreeLayout<WiresBaseTreeNode>( treeNodesProvider,
                                                                                        treeNodesExtentProvider,
                                                                                        treeNodesLayoutConfiguration );

        //Set absolute positions
        final Map<WiresBaseShape, Point2D> locations = new HashMap<WiresBaseShape, Point2D>();
        for ( Map.Entry<WiresBaseTreeNode, Rectangle2D> e : layout.getNodeBounds().entrySet() ) {
            locations.put( e.getKey(),
                           new Point2D( e.getValue().getX(),
                                        e.getValue().getY() ) );
        }

        //Collapse children into parent if required. By setting the location of "collapsed" nodes to that
        //of their parent we can animate the layout changes required to collapse a node and repositioning
        //of the remaining nodes with a single animation.
        collapseChildren( treeRootNode,
                          locations );

        return locations;
    }

    private void collapseChildren( final WiresBaseTreeNode node,
                                   final Map<WiresBaseShape, Point2D> locations ) {
        if ( node.hasCollapsedChildren() ) {
            final Point2D destination = locations.get( node );
            for ( WiresBaseTreeNode child : node.getChildren() ) {
                collapseChildren( child,
                                  destination,
                                  locations );
            }

        } else {
            for ( WiresBaseTreeNode child : node.getChildren() ) {
                collapseChildren( child,
                                  locations );
            }
        }
    }

    private void collapseChildren( final WiresBaseTreeNode node,
                                   final Point2D destination,
                                   final Map<WiresBaseShape, Point2D> locations ) {
        locations.put( node,
                       destination );
        for ( WiresBaseTreeNode child : node.getChildren() ) {
            collapseChildren( child,
                              destination,
                              locations );
        }
    }

    /**
     * Implementation of AbstractTreeForTreeLayout for Wires that handles collapsed nodes
     */
    private static class WiresTreeForTreeLayout extends AbstractTreeForTreeLayout<WiresBaseTreeNode> {

        public WiresTreeForTreeLayout( final WiresBaseTreeNode root ) {
            super( root );
        }

        @Override
        public WiresBaseTreeNode getParent( final WiresBaseTreeNode node ) {
            return node.getParentNode();
        }

        @Override
        public List<WiresBaseTreeNode> getChildrenList( final WiresBaseTreeNode node ) {
            //If node has collapsed children don't return them so it appears we have a single node
            if ( node.hasCollapsedChildren() ) {
                return Collections.emptyList();
            }
            return node.getChildren();
        }
    }

    /**
     * Implementation of NodeExtentProvider for Wires
     */
    private static class WiresTreeNodeExtentProvider implements
                                                     NodeExtentProvider<WiresBaseTreeNode> {

        @Override
        public double getWidth( final WiresBaseTreeNode node ) {
            return node.getWidth();
        }

        @Override
        public double getHeight( final WiresBaseTreeNode node ) {
            return node.getHeight();
        }
    }

}
