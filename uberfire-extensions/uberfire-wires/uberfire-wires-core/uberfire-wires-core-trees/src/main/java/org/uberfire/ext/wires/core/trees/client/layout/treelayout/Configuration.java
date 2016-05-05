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
 * Used to configure the tree layout algorithm.
 * <p/>
 * Also see <a href="package-summary.html">this overview</a>.
 * @param <TreeNode> <p/>
 * <p/>
 * Adapted from https://code.google.com/p/treelayout/ to be available to GWT clients
 * <p/>
 * @author Udo Borkowski (ub@abego.org)
 */
public interface Configuration<TreeNode> {

    // ------------------------------------------------------------------------
    // rootLocation

    /**
     * Identifies the sides of a rectangle (top, left, ...)
     */
    public enum Location {
        Top, Left, Bottom, Right
    }

    // ------------------------------------------------------------------------
    // alignmentInLevel

    /**
     * Returns the position of the root node in the diagram.
     * <p/>
     * By default the root of the tree is located at the top of the diagram.
     * However one may also put it at the left, right or bottom of the diagram.
     * <p/>
     * <table border="1">
     * <tr>
     * <th>Top (Default)</th>
     * <th>Left</th>
     * <th>Right</th>
     * <th>Bottom</th>
     * </tr>
     * <tr>
     * <td style="padding:10px;"><img src="doc-files/TreeGraphView-Top.png"></td>
     * <td style="padding:10px;"><img src="doc-files/TreeGraphView-Left.png"></td>
     * <td style="padding:10px;"><img src="doc-files/TreeGraphView-Right.png"></td>
     * <td style="padding:10px;"><img src="doc-files/TreeGraphView-Bottom.png"></td>
     * </tr>
     * </table>
     * @return the position of the root node in the diagram
     */
    Location getRootLocation();

    /**
     * Possible alignments of a node within a level (centered, towards or away
     * from root)
     */
    public enum AlignmentInLevel {
        Center, TowardsRoot, AwayFromRoot
    }

    /**
     * Returns the alignment of "smaller" nodes within a level.
     * <p>
     * By default all nodes of one level are centered in the level. However one
     * may also align them "towards the root" or "away from the root". When the
     * root is located at the top this means the nodes are aligned "to the top
     * of the level" or "to the bottom of the level".
     * <p>
     * <table border="1">
     * <tr>
     * <th>Center (Default)</th>
     * <th>TowardsRoot ("top of level")</th>
     * <th>AwayFromRoot ("bottom of level")</th>
     * </tr>
     * <tr>
     * <td style="padding:10px;"><img src="doc-files/TreeGraphView-Center.png"></td>
     * <td style="padding:10px;"><img
     * src="doc-files/TreeGraphView-TowardsRoot.png"></td>
     * <td style="padding:10px;"><img
     * src="doc-files/TreeGraphView-AwayFromRoot.png"></td>
     * </tr>
     * </table>
     * <p>
     * Alignment in level when root is at the left:
     * </p>
     * <table border="1">
     * <tr>
     * <th>Center (Default)</th>
     * <th>TowardsRoot ("left of level")</th>
     * <th>AwayFromRoot<br>
     * ("right of level")</th>
     * </tr>
     * <tr>
     * <td style="padding:10px;"><img
     * src="doc-files/TreeGraphView-Center-RootLeft.png"></td>
     * <td style="padding:10px;"><img
     * src="doc-files/TreeGraphView-TowardsRoot-RootLeft.png"></td>
     * <td style="padding:10px;"><img
     * src="doc-files/TreeGraphView-AwayFromRoot-RootLeft.png"></td>
     * </tr>
     * </table>
     * <p/>
     * <p>
     * Of cause the alignment also works when the root is at the bottom or at
     * the right side.
     * </p>
     * @return the alignment of "smaller" nodes within a level
     */
    AlignmentInLevel getAlignmentInLevel();

    // ------------------------------------------------------------------------
    // gapBetweenLevels/Nodes

    /**
     * Returns the size of the gap between subsequent levels.
     * <p/>
     * <img src="doc-files/gapBetweenLevels.png">
     * @param nextLevel [nextLevel > 0]
     * @return the size of the gap between level (nextLevel-1) and nextLevel
     *         [result >= 0]
     */
    double getGapBetweenLevels( int nextLevel );

    /**
     * Returns the size of the minimal gap of nodes within a level.
     * <p/>
     * In the layout there will be a gap of at least the returned size between
     * both given nodes.
     * <p/>
     * <img src="doc-files/gapBetweenNodes.png">
     * <p/>
     * node1 and node2 are at the same level and are placed next to each other.
     * @param node1
     * @param node2
     * @return the minimal size of the gap between node1 and node2 [result >= 0]
     */
    double getGapBetweenNodes( TreeNode node1,
                               TreeNode node2 );
}
