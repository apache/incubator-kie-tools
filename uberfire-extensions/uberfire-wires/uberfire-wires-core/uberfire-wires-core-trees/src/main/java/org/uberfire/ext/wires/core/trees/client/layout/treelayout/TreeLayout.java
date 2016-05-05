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
package org.uberfire.ext.wires.core.trees.client.layout.treelayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Implements the actual tree layout algorithm.
 * <p/>
 * The nodes with their final layout can be retrieved through
 * {@link #getNodeBounds()}.
 * <p/>
 * @param <TreeNode> <p/>
 * <p/>
 * Adapted from https://code.google.com/p/treelayout/ to be available to GWT clients
 * <p/>
 * @author Udo Borkowski (ub@abego.org)
 */
public class TreeLayout<TreeNode> {
    /*
     * Differences between this implementation and original algorithm
	 * --------------------------------------------------------------
	 *
	 * For easier reference the same names (or at least similar names) as in the
	 * paper of Buchheim, J&uuml;nger, and Leipert are used in this
	 * implementation. However in the external interface "first" and "last" are
	 * used instead of "left most" and "right most". The implementation also
	 * supports tree layouts with the root at the left (or right) side. In that
	 * case using "left most" would refer to the "top" child, i.e. using "first"
	 * is less confusing.
	 *
	 * Also the y coordinate is not the level but directly refers the y
	 * coordinate of a level, taking node's height and gapBetweenLevels into
	 * account. When the root is at the left or right side the y coordinate
	 * actually becomes an x coordinate.
	 *
	 * Instead of just using a constant "distance" to calculate the position to
	 * the next node we refer to the "size" (width or height) of the node and a
	 * "gapBetweenNodes".
	 */

    // ------------------------------------------------------------------------
    // tree

    private final TreeForTreeLayout<TreeNode> tree;

    /**
     * Returns the Tree the layout is created for.
     */
    public TreeForTreeLayout<TreeNode> getTree() {
        return tree;
    }

    // ------------------------------------------------------------------------
    // nodeExtentProvider

    private final NodeExtentProvider<TreeNode> nodeExtentProvider;

    /**
     * Returns the {@link NodeExtentProvider} used by this {@link TreeLayout}.
     */
    public NodeExtentProvider<TreeNode> getNodeExtentProvider() {
        return nodeExtentProvider;
    }

    private double getNodeHeight( TreeNode node ) {
        return nodeExtentProvider.getHeight( node );
    }

    private double getNodeWidth( TreeNode node ) {
        return nodeExtentProvider.getWidth( node );
    }

    private double getWidthOrHeightOfNode( TreeNode treeNode,
                                           boolean returnWidth ) {
        return returnWidth ? getNodeWidth( treeNode ) : getNodeHeight( treeNode );
    }

    /**
     * When the level changes in Y-axis (i.e. root location Top or Bottom) the
     * height of a node is its thickness, otherwise the node's width is its
     * thickness.
     * <p/>
     * The thickness of a node is used when calculating the locations of the
     * levels.
     * @param treeNode
     * @return
     */
    private double getNodeThickness( TreeNode treeNode ) {
        return getWidthOrHeightOfNode( treeNode,
                                       !isLevelChangeInYAxis() );
    }

    /**
     * When the level changes in Y-axis (i.e. root location Top or Bottom) the
     * width of a node is its size, otherwise the node's height is its size.
     * <p/>
     * The size of a node is used when calculating the distance between two
     * nodes.
     * @param treeNode
     * @return
     */
    private double getNodeSize( TreeNode treeNode ) {
        return getWidthOrHeightOfNode( treeNode,
                                       isLevelChangeInYAxis() );
    }

    // ------------------------------------------------------------------------
    // configuration

    private final Configuration<TreeNode> configuration;

    /**
     * Returns the Configuration used by this {@link TreeLayout}.
     */
    public Configuration<TreeNode> getConfiguration() {
        return configuration;
    }

    private boolean isLevelChangeInYAxis() {
        Configuration.Location rootLocation = configuration.getRootLocation();
        return rootLocation == Configuration.Location.Top || rootLocation == Configuration.Location.Bottom;
    }

    private int getLevelChangeSign() {
        Configuration.Location rootLocation = configuration.getRootLocation();
        return rootLocation == Configuration.Location.Bottom
                || rootLocation == Configuration.Location.Right ? -1 : 1;
    }

    // ------------------------------------------------------------------------
    // bounds

    private double boundsLeft = Double.MAX_VALUE;
    private double boundsRight = Double.MIN_VALUE;
    private double boundsTop = Double.MAX_VALUE;
    private double boundsBottom = Double.MIN_VALUE;

    private void updateBounds( TreeNode node,
                               double centerX,
                               double centerY ) {
        double width = getNodeWidth( node );
        double height = getNodeHeight( node );
        double left = centerX - width / 2;
        double right = centerX + width / 2;
        double top = centerY - height / 2;
        double bottom = centerY + height / 2;
        if ( boundsLeft > left ) {
            boundsLeft = left;
        }
        if ( boundsRight < right ) {
            boundsRight = right;
        }
        if ( boundsTop > top ) {
            boundsTop = top;
        }
        if ( boundsBottom < bottom ) {
            boundsBottom = bottom;
        }
    }

    /**
     * Returns the bounds of the tree layout.
     * <p/>
     * The bounds of a TreeLayout is the smallest rectangle containing the
     * bounds of all nodes in the layout. It always starts at (0,0).
     * @return the bounds of the tree layout
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D( 0,
                                0,
                                boundsRight - boundsLeft,
                                boundsBottom - boundsTop );
    }

    // ------------------------------------------------------------------------
    // size of level

    private final List<Double> sizeOfLevel = new ArrayList<Double>();

    private void calcSizeOfLevels( TreeNode node,
                                   int level ) {
        double oldSize;
        if ( sizeOfLevel.size() <= level ) {
            sizeOfLevel.add( Double.valueOf( 0 ) );
            oldSize = 0;
        } else {
            oldSize = sizeOfLevel.get( level );
        }

        double size = getNodeThickness( node );
        // size = nodeExtentProvider.getHeight(node);
        if ( oldSize < size ) {
            sizeOfLevel.set( level,
                             size );
        }

        if ( !tree.isLeaf( node ) ) {
            for ( TreeNode child : tree.getChildren( node ) ) {
                calcSizeOfLevels( child,
                                  level + 1 );
            }
        }
    }

    /**
     * Returns the number of levels of the tree.
     * @return [level > 0]
     */
    public int getLevelCount() {
        return sizeOfLevel.size();
    }

    /**
     * Returns the size of a level.
     * <p/>
     * When the root is located at the top or bottom the size of a level is the
     * maximal height of the nodes of that level. When the root is located at
     * the left or right the size of a level is the maximal width of the nodes
     * of that level.
     * @param level
     * @return the size of the level [level >= 0 && level < levelCount]
     */
    public double getSizeOfLevel( int level ) {
        PortablePreconditions.checkCondition( "level must be >= 0",
                                              level >= 0 );
        PortablePreconditions.checkCondition( "level must be < levelCount",
                                              level < getLevelCount() );

        return sizeOfLevel.get( level );
    }

    // ------------------------------------------------------------------------
    // NormalizedPosition

    /**
     * The algorithm calculates the position starting with the root at 0. I.e.
     * the left children will get negative positions. However we want the result
     * to be normalized to (0,0).
     * <p/>
     * {@link NormalizedPosition} will normalize the position (given relative to
     * the root position), taking the current bounds into account. This way the
     * left most node bounds will start at x = 0, the top most node bounds at y
     * = 0.
     */
    private class NormalizedPosition extends Point2D {

        private double x_relativeToRoot;
        private double y_relativeToRoot;

        public NormalizedPosition( double x_relativeToRoot,
                                   double y_relativeToRoot ) {
            setLocation( x_relativeToRoot,
                         y_relativeToRoot );
        }

        @Override
        public double getX() {
            return x_relativeToRoot - boundsLeft;
        }

        @Override
        public double getY() {
            return y_relativeToRoot - boundsTop;
        }

        @Override
        // never called from outside
        public void setLocation( double x_relativeToRoot,
                                 double y_relativeToRoot ) {
            this.x_relativeToRoot = x_relativeToRoot;
            this.y_relativeToRoot = y_relativeToRoot;
        }
    }

    // ------------------------------------------------------------------------
    // The Algorithm

    private final boolean useIdentity;

    private final Map<TreeNode, Double> mod;
    private final Map<TreeNode, TreeNode> thread;
    private final Map<TreeNode, Double> prelim;
    private final Map<TreeNode, Double> change;
    private final Map<TreeNode, Double> shift;
    private final Map<TreeNode, TreeNode> ancestor;
    private final Map<TreeNode, Integer> number;
    private final Map<TreeNode, Point2D> positions;

    private double getMod( TreeNode node ) {
        Double d = mod.get( node );
        return d != null ? d.doubleValue() : 0;
    }

    private void setMod( TreeNode node,
                         double d ) {
        mod.put( node, d );
    }

    private TreeNode getThread( TreeNode node ) {
        TreeNode n = thread.get( node );
        return n != null ? n : null;
    }

    private void setThread( TreeNode node,
                            TreeNode thread ) {
        this.thread.put( node,
                         thread );
    }

    private TreeNode getAncestor( TreeNode node ) {
        TreeNode n = ancestor.get( node );
        return n != null ? n : node;
    }

    private void setAncestor( TreeNode node,
                              TreeNode ancestor ) {
        this.ancestor.put( node,
                           ancestor );
    }

    private double getPrelim( TreeNode node ) {
        Double d = prelim.get( node );
        return d != null ? d.doubleValue() : 0;
    }

    private void setPrelim( TreeNode node,
                            double d ) {
        prelim.put( node, d );
    }

    private double getChange( TreeNode node ) {
        Double d = change.get( node );
        return d != null ? d.doubleValue() : 0;
    }

    private void setChange( TreeNode node,
                            double d ) {
        change.put( node, d );
    }

    private double getShift( TreeNode node ) {
        Double d = shift.get( node );
        return d != null ? d.doubleValue() : 0;
    }

    private void setShift( TreeNode node,
                           double d ) {
        shift.put( node, d );
    }

    /**
     * The distance of two nodes is the distance of the centers of both noded.
     * <p/>
     * I.e. the distance includes the gap between the nodes and half of the
     * sizes of the nodes.
     * @param v
     * @param w
     * @return the distance between node v and w
     */
    private double getDistance( TreeNode v,
                                TreeNode w ) {
        double sizeOfNodes = getNodeSize( v ) + getNodeSize( w );

        double distance = sizeOfNodes / 2 + configuration.getGapBetweenNodes( v, w );
        return distance;
    }

    private TreeNode nextLeft( TreeNode v ) {
        return tree.isLeaf( v ) ? getThread( v ) : tree.getFirstChild( v );
    }

    private TreeNode nextRight( TreeNode v ) {
        return tree.isLeaf( v ) ? getThread( v ) : tree.getLastChild( v );
    }

    /**
     * @param node [tree.isChildOfParent(node, parentNode)]
     * @param parentNode parent of node
     * @return
     */
    private int getNumber( TreeNode node,
                           TreeNode parentNode ) {
        Integer n = number.get( node );
        if ( n == null ) {
            int i = 1;
            for ( TreeNode child : tree.getChildren( parentNode ) ) {
                number.put( child, i++ );
            }
            n = number.get( node );
        }

        return n.intValue();
    }

    /**
     * @param vIMinus
     * @param v
     * @param parentOfV
     * @param defaultAncestor
     * @return the greatest distinct ancestor of vIMinus and its right neighbor
     *         v
     */
    private TreeNode ancestor( TreeNode vIMinus,
                               TreeNode v,
                               TreeNode parentOfV,
                               TreeNode defaultAncestor ) {
        TreeNode ancestor = getAncestor( vIMinus );

        // when the ancestor of vIMinus is a sibling of v (i.e. has the same
        // parent as v) it is also the greatest distinct ancestor vIMinus and
        // v. Otherwise it is the defaultAncestor

        return tree.isChildOfParent( ancestor, parentOfV ) ? ancestor : defaultAncestor;
    }

    private void moveSubtree( TreeNode wMinus,
                              TreeNode wPlus,
                              TreeNode parent,
                              double shift ) {

        int subtrees = getNumber( wPlus,
                                  parent ) - getNumber( wMinus,
                                                        parent );
        setChange( wPlus,
                   getChange( wPlus ) - shift / subtrees );
        setShift( wPlus,
                  getShift( wPlus ) + shift );
        setChange( wMinus,
                   getChange( wMinus ) + shift / subtrees );
        setPrelim( wPlus,
                   getPrelim( wPlus ) + shift );
        setMod( wPlus,
                getMod( wPlus ) + shift );
    }

    /**
     * In difference to the original algorithm we also pass in the leftSibling
     * and the parent of v.
     * <p/>
     * <b>Why adding the parameter 'parent of v' (parentOfV) ?</b>
     * <p/>
     * In this method we need access to the parent of v. Not every tree
     * implementation may support efficient (i.e. constant time) access to it.
     * On the other hand the (only) caller of this method can provide this
     * information with only constant extra time.
     * <p/>
     * Also we need access to the "left most sibling" of v. Not every tree
     * implementation may support efficient (i.e. constant time) access to it.
     * On the other hand the "left most sibling" of v is also the "first child"
     * of the parent of v. The first child of a parent node we can get in
     * constant time. As we got the parent of v we can so also get the
     * "left most sibling" of v in constant time.
     * <p/>
     * <b>Why adding the parameter 'leftSibling' ?</b>
     * <p/>
     * In this method we need access to the "left sibling" of v. Not every tree
     * implementation may support efficient (i.e. constant time) access to it.
     * However it is easy for the caller of this method to provide this
     * information with only constant extra time.
     * <p/>
     * <p/>
     * <p/>
     * In addition these extra parameters avoid the need for
     * {@link TreeForTreeLayout} to include extra methods "getParent",
     * "getLeftSibling", or "getLeftMostSibling". This keeps the interface
     * {@link TreeForTreeLayout} small and avoids redundant implementations.
     * @param v
     * @param defaultAncestor
     * @param leftSibling [nullable] the left sibling v, if there is any
     * @param parentOfV the parent of v
     * @return the (possibly changes) defaultAncestor
     */
    private TreeNode apportion( TreeNode v,
                                TreeNode defaultAncestor,
                                TreeNode leftSibling,
                                TreeNode parentOfV ) {
        TreeNode w = leftSibling;
        if ( w == null ) {
            // v has no left sibling
            return defaultAncestor;
        }
        // v has left sibling w

        // The following variables "v..." are used to traverse the contours to
        // the subtrees. "Minus" refers to the left, "Plus" to the right
        // subtree. "I" refers to the "inside" and "O" to the outside contour.
        TreeNode vOPlus = v;
        TreeNode vIPlus = v;
        TreeNode vIMinus = w;
        // get leftmost sibling of vIPlus, i.e. get the leftmost sibling of
        // v, i.e. the leftmost child of the parent of v (which is passed
        // in)
        TreeNode vOMinus = tree.getFirstChild( parentOfV );

        Double sIPlus = getMod( vIPlus );
        Double sOPlus = getMod( vOPlus );
        Double sIMinus = getMod( vIMinus );
        Double sOMinus = getMod( vOMinus );

        TreeNode nextRightVIMinus = nextRight( vIMinus );
        TreeNode nextLeftVIPlus = nextLeft( vIPlus );

        while ( nextRightVIMinus != null && nextLeftVIPlus != null ) {
            vIMinus = nextRightVIMinus;
            vIPlus = nextLeftVIPlus;
            vOMinus = nextLeft( vOMinus );
            vOPlus = nextRight( vOPlus );
            setAncestor( vOPlus, v );
            double shift = ( getPrelim( vIMinus ) + sIMinus )
                    - ( getPrelim( vIPlus ) + sIPlus )
                    + getDistance( vIMinus, vIPlus );

            if ( shift > 0 ) {
                moveSubtree( ancestor( vIMinus, v,
                                       parentOfV,
                                       defaultAncestor ),
                             v,
                             parentOfV,
                             shift );
                sIPlus = sIPlus + shift;
                sOPlus = sOPlus + shift;
            }
            sIMinus = sIMinus + getMod( vIMinus );
            sIPlus = sIPlus + getMod( vIPlus );
            sOMinus = sOMinus + getMod( vOMinus );
            sOPlus = sOPlus + getMod( vOPlus );

            nextRightVIMinus = nextRight( vIMinus );
            nextLeftVIPlus = nextLeft( vIPlus );
        }

        if ( nextRightVIMinus != null && nextRight( vOPlus ) == null ) {
            setThread( vOPlus, nextRightVIMinus );
            setMod( vOPlus, getMod( vOPlus ) + sIMinus - sOPlus );
        }

        if ( nextLeftVIPlus != null && nextLeft( vOMinus ) == null ) {
            setThread( vOMinus,
                       nextLeftVIPlus );
            setMod( vOMinus,
                    getMod( vOMinus ) + sIPlus - sOMinus );
            defaultAncestor = v;
        }
        return defaultAncestor;
    }

    /**
     * @param v [!tree.isLeaf(v)]
     */
    private void executeShifts( TreeNode v ) {
        double shift = 0;
        double change = 0;
        for ( TreeNode w : tree.getChildrenReverse( v ) ) {
            change = change + getChange( w );
            setPrelim( w,
                       getPrelim( w ) + shift );
            setMod( w,
                    getMod( w ) + shift );
            shift = shift + getShift( w ) + change;
        }
    }

    /**
     * In difference to the original algorithm we also pass in the leftSibling
     * (see {@link #apportion(Object, Object, Object, Object)} for a
     * motivation).
     * @param v
     * @param leftSibling [nullable] the left sibling v, if there is any
     */
    private void firstWalk( TreeNode v,
                            TreeNode leftSibling ) {
        if ( tree.isLeaf( v ) ) {
            // No need to set prelim(v) to 0 as the getter takes care of this.

            TreeNode w = leftSibling;
            if ( w != null ) {
                // v has left sibling

                setPrelim( v,
                           getPrelim( w ) + getDistance( v,
                                                         w ) );
            }

        } else {
            // v is not a leaf

            TreeNode defaultAncestor = tree.getFirstChild( v );
            TreeNode previousChild = null;
            for ( TreeNode w : tree.getChildren( v ) ) {
                firstWalk( w, previousChild );
                defaultAncestor = apportion( w,
                                             defaultAncestor,
                                             previousChild,
                                             v );
                previousChild = w;
            }
            executeShifts( v );
            double midpoint = ( getPrelim( tree.getFirstChild( v ) ) + getPrelim( tree.getLastChild( v ) ) ) / 2.0;
            TreeNode w = leftSibling;
            if ( w != null ) {
                // v has left sibling

                setPrelim( v,
                           getPrelim( w ) + getDistance( v,
                                                         w ) );
                setMod( v,
                        getPrelim( v ) - midpoint );

            } else {
                // v has no left sibling

                setPrelim( v,
                           midpoint );
            }
        }
    }

    /**
     * In difference to the original algorithm we also pass in extra level
     * information.
     * @param v
     * @param m
     * @param level
     * @param levelStart
     */
    private void secondWalk( TreeNode v,
                             double m,
                             int level,
                             double levelStart ) {
        // construct the position from the prelim and the level information

        // The rootLocation affects the way how x and y are changed and in what
        // direction.
        double levelChangeSign = getLevelChangeSign();
        boolean levelChangeOnYAxis = isLevelChangeInYAxis();
        double levelSize = getSizeOfLevel( level );

        double x = getPrelim( v ) + m;

        double y;
        Configuration.AlignmentInLevel alignment = configuration.getAlignmentInLevel();
        if ( alignment == Configuration.AlignmentInLevel.Center ) {
            y = levelStart + levelChangeSign * ( levelSize / 2 );
        } else if ( alignment == Configuration.AlignmentInLevel.TowardsRoot ) {
            y = levelStart + levelChangeSign * ( getNodeThickness( v ) / 2 );
        } else {
            y = levelStart + levelSize - levelChangeSign * ( getNodeThickness( v ) / 2 );
        }

        if ( !levelChangeOnYAxis ) {
            double t = x;
            x = y;
            y = t;
        }

        positions.put( v,
                       new NormalizedPosition( x,
                                               y ) );

        // update the bounds
        updateBounds( v,
                      x,
                      y );

        // recurse
        if ( !tree.isLeaf( v ) ) {
            double nextLevelStart = levelStart
                    + ( levelSize + configuration.getGapBetweenLevels( level + 1 ) )
                    * levelChangeSign;
            for ( TreeNode w : tree.getChildren( v ) ) {
                secondWalk( w,
                            m + getMod( v ),
                            level + 1,
                            nextLevelStart );
            }
        }
    }

    // ------------------------------------------------------------------------
    // nodeBounds

    private Map<TreeNode, Rectangle2D> nodeBounds;

    /**
     * Returns the layout of the tree nodes by mapping each node of the tree to
     * its bounds (position and size).
     * <p/>
     * For each rectangle x and y will be >= 0. At least one rectangle will have
     * an x == 0 and at least one rectangle will have an y == 0.
     * @return maps each node of the tree to its bounds (position and size).
     */
    public Map<TreeNode, Rectangle2D> getNodeBounds() {
        if ( nodeBounds == null ) {
            nodeBounds = this.useIdentity ? new IdentityHashMap<TreeNode, Rectangle2D>()
                    : new HashMap<TreeNode, Rectangle2D>();
            for ( Map.Entry<TreeNode, Point2D> entry : positions.entrySet() ) {
                TreeNode node = entry.getKey();
                Point2D pos = entry.getValue();
                double w = getNodeWidth( node );
                double h = getNodeHeight( node );
                double x = pos.getX() - w / 2;
                double y = pos.getY() - h / 2;
                nodeBounds.put( node, new Rectangle2D( x,
                                                       y,
                                                       w,
                                                       h ) );
            }
        }
        return nodeBounds;
    }

    // ------------------------------------------------------------------------
    // constructor

    /**
     * Creates a TreeLayout for a given tree.
     * <p/>
     * In addition to the tree the {@link NodeExtentProvider} and the
     * {@link Configuration} must be given.
     * @param useIdentity [default: false] when true, identity ("==") is used instead of
     * equality ("equals(...)") when checking nodes. Within a tree
     * each node must only be once (using this check).
     */
    public TreeLayout( TreeForTreeLayout<TreeNode> tree,
                       NodeExtentProvider<TreeNode> nodeExtentProvider,
                       Configuration<TreeNode> configuration,
                       boolean useIdentity ) {
        this.tree = tree;
        this.nodeExtentProvider = nodeExtentProvider;
        this.configuration = configuration;
        this.useIdentity = useIdentity;

        if ( this.useIdentity ) {
            this.mod = new IdentityHashMap<TreeNode, Double>();
            this.thread = new IdentityHashMap<TreeNode, TreeNode>();
            this.prelim = new IdentityHashMap<TreeNode, Double>();
            this.change = new IdentityHashMap<TreeNode, Double>();
            this.shift = new IdentityHashMap<TreeNode, Double>();
            this.ancestor = new IdentityHashMap<TreeNode, TreeNode>();
            this.number = new IdentityHashMap<TreeNode, Integer>();
            this.positions = new IdentityHashMap<TreeNode, Point2D>();
        } else {
            this.mod = new HashMap<TreeNode, Double>();
            this.thread = new HashMap<TreeNode, TreeNode>();
            this.prelim = new HashMap<TreeNode, Double>();
            this.change = new HashMap<TreeNode, Double>();
            this.shift = new HashMap<TreeNode, Double>();
            this.ancestor = new HashMap<TreeNode, TreeNode>();
            this.number = new HashMap<TreeNode, Integer>();
            this.positions = new HashMap<TreeNode, Point2D>();
        }

        // No need to explicitly set mod, thread and ancestor as their getters
        // are taking care of the initial values. This avoids a full tree walk
        // through and saves some memory as no entries are added for
        // "initial values".

        TreeNode r = tree.getRoot();
        firstWalk( r,
                   null );
        calcSizeOfLevels( r,
                          0 );
        secondWalk( r,
                    -getPrelim( r ),
                    0,
                    0 );
    }

    public TreeLayout( TreeForTreeLayout<TreeNode> tree,
                       NodeExtentProvider<TreeNode> nodeExtentProvider,
                       Configuration<TreeNode> configuration ) {
        this( tree, nodeExtentProvider, configuration, false );
    }

    // ------------------------------------------------------------------------
    // checkTree

    private void addUniqueNodes( Map<TreeNode, TreeNode> nodes,
                                 TreeNode newNode ) {
        if ( nodes.put( newNode,
                        newNode ) != null ) {
            throw new RuntimeException( "Node used more than once in tree: " + newNode );
        }
        for ( TreeNode n : tree.getChildren( newNode ) ) {
            addUniqueNodes( nodes,
                            n );
        }
    }

    /**
     * Check if the tree is a "valid" tree.
     * <p/>
     * Typically you will use this method during development when you get an
     * unexpected layout from your trees.
     * <p/>
     * The following checks are performed:
     * <ul>
     * <li>Each node must only occur once in the tree.</li>
     * </ul>
     */
    public void checkTree() {
        Map<TreeNode, TreeNode> nodes = this.useIdentity ? new IdentityHashMap<TreeNode, TreeNode>()
                : new HashMap<TreeNode, TreeNode>();

        // Traverse the tree and check if each node is only used once.
        addUniqueNodes( nodes,
                        tree.getRoot() );
    }

}
