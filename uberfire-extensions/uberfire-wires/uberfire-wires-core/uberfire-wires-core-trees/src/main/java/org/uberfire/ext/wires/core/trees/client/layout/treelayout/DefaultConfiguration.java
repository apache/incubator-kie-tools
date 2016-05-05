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

import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Specify a {@link Configuration} through configurable parameters, or falling
 * back to some frequently used defaults.
 * @param <TreeNode> <p/>
 * <p/>
 * Adapted from https://code.google.com/p/treelayout/ to be available to GWT clients
 * <p/>
 * @author Udo Borkowski (ub@abego.org)
 */
public class DefaultConfiguration<TreeNode> implements
                                            Configuration<TreeNode> {

    /**
     * Specifies the constants to be used for this Configuration.
     * @param gapBetweenLevels
     * @param gapBetweenNodes
     * @param location [default: {@link Configuration.Location#Top Top}]
     * @param alignmentInLevel [default: {@link Configuration.AlignmentInLevel#Center Center}]
     */
    public DefaultConfiguration( double gapBetweenLevels,
                                 double gapBetweenNodes,
                                 Location location,
                                 AlignmentInLevel alignmentInLevel ) {
        PortablePreconditions.checkCondition( "gapBetweenLevels must be >= 0",
                                              gapBetweenLevels >= 0 );
        PortablePreconditions.checkCondition( "gapBetweenNodes must be >= 0",
                                              gapBetweenNodes >= 0 );

        this.gapBetweenLevels = gapBetweenLevels;
        this.gapBetweenNodes = gapBetweenNodes;
        this.location = location;
        this.alignmentInLevel = alignmentInLevel;
    }

    /**
     * Convenience constructor, using a default for the alignmentInLevel.
     * <p/>
     * see
     * {@link #DefaultConfiguration(double, double, Configuration.Location, Configuration.AlignmentInLevel)}
     */
    public DefaultConfiguration( double gapBetweenLevels,
                                 double gapBetweenNodes,
                                 Location location ) {
        this( gapBetweenLevels,
              gapBetweenNodes,
              location,
              AlignmentInLevel.Center );
    }

    /**
     * Convenience constructor, using a default for the rootLocation and the
     * alignmentInLevel.
     * <p/>
     * see
     * {@link #DefaultConfiguration(double, double, Configuration.Location, Configuration.AlignmentInLevel)}
     */
    public DefaultConfiguration( double gapBetweenLevels,
                                 double gapBetweenNodes ) {
        this( gapBetweenLevels,
              gapBetweenNodes,
              Location.Top,
              AlignmentInLevel.Center );
    }

    // -----------------------------------------------------------------------
    // gapBetweenLevels

    private final double gapBetweenLevels;

    @Override
    public double getGapBetweenLevels( int nextLevel ) {
        return gapBetweenLevels;
    }

    // -----------------------------------------------------------------------
    // gapBetweenNodes

    private final double gapBetweenNodes;

    @Override
    public double getGapBetweenNodes( TreeNode node1,
                                      TreeNode node2 ) {
        return gapBetweenNodes;
    }

    // -----------------------------------------------------------------------
    // location

    private final Location location;

    @Override
    public Location getRootLocation() {
        return location;
    }

    // -----------------------------------------------------------------------
    // alignmentInLevel

    private AlignmentInLevel alignmentInLevel;

    @Override
    public AlignmentInLevel getAlignmentInLevel() {
        return alignmentInLevel;
    }
}