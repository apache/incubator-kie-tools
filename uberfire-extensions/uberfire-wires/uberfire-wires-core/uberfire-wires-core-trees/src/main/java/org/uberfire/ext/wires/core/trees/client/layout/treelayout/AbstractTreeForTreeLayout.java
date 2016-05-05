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

import java.util.List;

import org.uberfire.ext.wires.core.trees.client.layout.treelayout.util.IterableUtil;
import org.uberfire.ext.wires.core.trees.client.layout.treelayout.util.ListUtil;

/**
 * Provides an easy way to implement the {@link TreeForTreeLayout} interface by
 * defining just two simple methods and a constructor.
 * <p/>
 * To use this class the underlying tree must provide the children as a list
 * (see {@link #getChildrenList(Object)} and give direct access to the parent of
 * a node (see {@link #getParent(Object)}).
 * <p/>
 * @param <TreeNode> <p/>
 * <p/>
 * Adapted from https://code.google.com/p/treelayout/ to be available to GWT clients
 * <p/>
 * @author Udo Borkowski (ub@abego.org)
 */
public abstract class AbstractTreeForTreeLayout<TreeNode> implements
                                                          TreeForTreeLayout<TreeNode> {

    /**
     * Returns the parent of a node, if it has one.
     * <p/>
     * Time Complexity: O(1)
     * @param node
     * @return [nullable] the parent of the node, or null when the node is a
     *         root.
     */
    abstract public TreeNode getParent( TreeNode node );

    /**
     * Return the children of a node as a {@link List}.
     * <p/>
     * Time Complexity: O(1)
     * <p/>
     * Also the access to an item of the list must have time complexity O(1).
     * <p/>
     * A client must not modify the returned list.
     * @param node
     * @return the children of the given node. When node is a leaf the list is
     *         empty.
     */
    abstract public List<TreeNode> getChildrenList( TreeNode node );

    private final TreeNode root;

    public AbstractTreeForTreeLayout( TreeNode root ) {
        this.root = root;
    }

    @Override
    public TreeNode getRoot() {
        return root;
    }

    @Override
    public boolean isLeaf( TreeNode node ) {
        return getChildrenList( node ).isEmpty();
    }

    @Override
    public boolean isChildOfParent( TreeNode node,
                                    TreeNode parentNode ) {
        return getParent( node ) == parentNode;
    }

    @Override
    public Iterable<TreeNode> getChildren( TreeNode node ) {
        return getChildrenList( node );
    }

    @Override
    public Iterable<TreeNode> getChildrenReverse( TreeNode node ) {
        return IterableUtil.createReverseIterable( getChildrenList( node ) );
    }

    @Override
    public TreeNode getFirstChild( TreeNode parentNode ) {
        return getChildrenList( parentNode ).get( 0 );
    }

    @Override
    public TreeNode getLastChild( TreeNode parentNode ) {
        return ListUtil.getLast( getChildrenList( parentNode ) );
    }
}

