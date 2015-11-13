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

/**
 * Represents a tree to be used by the {@link TreeLayout}.
 * <p/>
 * The TreeForTreeLayout interface is designed to best match the implemented
 * layout algorithm and to ensure the algorithm's time complexity promises in
 * all possible cases. However in most situation a client must not deal with all
 * details of this interface and can directly use the
 * {@link AbstractTreeForTreeLayout} to implement this
 * interface.
 * <p/>
 * Also see <a href="package-summary.html">this overview</a>.
 * @param <TreeNode> <p/>
 * <p/>
 * Adapted from https://code.google.com/p/treelayout/ to be available to GWT clients
 * <p/>
 * @author Udo Borkowski (ub@abego.org)
 */
public interface TreeForTreeLayout<TreeNode> {

    /**
     * Returns the the root of the tree.
     * <p/>
     * Time Complexity: O(1)
     * @return the root of the tree
     */
    TreeNode getRoot();

    /**
     * Tells if a node is a leaf in the tree.
     * <p/>
     * Time Complexity: O(1)
     * @param node
     * @return true iff node is a leaf in the tree, i.e. has no children.
     */
    boolean isLeaf( TreeNode node );

    /**
     * Tells if a node is a child of a given parentNode.
     * <p/>
     * Time Complexity: O(1)
     * @param node
     * @param parentNode
     * @return true iff the node is a child of the given parentNode
     */
    boolean isChildOfParent( TreeNode node,
                             TreeNode parentNode );

    /**
     * Returns the children of a parent node.
     * <p/>
     * Time Complexity: O(1)
     * @param parentNode [!isLeaf(parentNode)]
     * @return the children of the given parentNode, from first to last
     */
    Iterable<TreeNode> getChildren( TreeNode parentNode );

    /**
     * Returns the children of a parent node, in reverse order.
     * <p/>
     * Time Complexity: O(1)
     * @param parentNode [!isLeaf(parentNode)]
     * @return the children of given parentNode, from last to first
     */
    Iterable<TreeNode> getChildrenReverse( TreeNode parentNode );

    /**
     * Returns the first child of a parent node.
     * <p/>
     * Time Complexity: O(1)
     * @param parentNode [!isLeaf(parentNode)]
     * @return the first child of the parentNode
     */
    TreeNode getFirstChild( TreeNode parentNode );

    /**
     * Returns the last child of a parent node.
     * <p/>
     * <p/>
     * Time Complexity: O(1)
     * @param parentNode [!isLeaf(parentNode)]
     * @return the last child of the parentNode
     */
    TreeNode getLastChild( TreeNode parentNode );

}
