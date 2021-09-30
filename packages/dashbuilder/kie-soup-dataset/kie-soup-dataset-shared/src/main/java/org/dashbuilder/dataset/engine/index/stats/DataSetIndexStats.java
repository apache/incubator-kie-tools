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
package org.dashbuilder.dataset.engine.index.stats;

import org.dashbuilder.dataset.engine.index.DataSetIndexElement;

/**
 * A DataSetIndex stats
 */
public interface DataSetIndexStats {

    /**
     * Time required to "build" (load, create, filter, ...) the data set.
     * @return Time in nanoseconds
     */
    long getBuildTime();

    /**
     * The real usage time this element would have if was not indexed.
     * @return Time in nanoseconds
     */
    long getReuseTime();

    /**
     * The ratio between the reuse time and the build time.
     */
    double getReuseRate();

    /**
     * Total number of group operations over the data set
     */
    int getNumberOfGroupOps();

    /**
     * Total number of filter operations executed over the data set
     */
    int getNumberOfFilterOps();

    /**
     * Total number of sort operations executed over the data set
     */
    int getNumberOfSortOps();

    /**
     * Total number of aggregate function calculations executed over the data set
     */
    int getNumberOfAggFunctions();

    /**
     * An index reference to the element that takes more time to get instantiated.
     */
    DataSetIndexElement getLongestBuild();

    /**
     * An index reference to the element that takes less time to get instantiated.
     */
    DataSetIndexElement getShortestBuild();

    /**
     * An index reference to the less reused element.
     */
    DataSetIndexElement getLessReused();

    /**
     * An index reference to the most reused element.
     */
    DataSetIndexElement getMostReused();

    /**
     * Return the estimated memory (in bytes) the data set index is consuming.
     * @return The number of bytes
     */
    long getIndexSize();

    /**
     * Prints a stats summary.
     * @param sep The separator string to insert between every stat.
     */
    String toString(String sep);
}
