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
 * Provides the extent (width and height) of a tree node.
 * <p/>
 * Also see <a href="package-summary.html">this overview</a>.
 * @param <TreeNode> <p/>
 * <p/>
 * Adapted from https://code.google.com/p/treelayout/ to be available to GWT clients
 * <p/>
 * @author Udo Borkowski (ub@abego.org)
 */
public interface NodeExtentProvider<TreeNode> {

    /**
     * Returns the width of the given treeNode.
     * @param treeNode
     * @return [result >= 0]
     */
    double getWidth( TreeNode treeNode );

    /**
     * Returns the height of the given treeNode.
     * @param treeNode
     * @return [result >= 0]
     */
    double getHeight( TreeNode treeNode );
}
