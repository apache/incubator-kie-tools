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

import org.dashbuilder.dataset.engine.index.DataSetFilterIndex;
import org.dashbuilder.dataset.engine.index.DataSetFunctionIndex;
import org.dashbuilder.dataset.engine.index.DataSetGroupIndex;
import org.dashbuilder.dataset.engine.index.DataSetIndex;
import org.dashbuilder.dataset.engine.index.DataSetIndexElement;
import org.dashbuilder.dataset.engine.index.DataSetSortIndex;
import org.dashbuilder.dataset.engine.index.visitor.DataSetIndexVisitor;

/**
 * A DataSetIndex stats
 */
public class DataSetIndexStatsImpl implements DataSetIndexStats, DataSetIndexVisitor {

    private DataSetIndex index;
    private transient long buildTime = 0;
    private transient long reuseTime = 0;
    private transient long indexSize = 0;
    private transient int numberOfGroupOps = 0;
    private transient int numberOfFilterOps = 0;
    private transient int numberOfSortOps = 0;
    private transient int numberOfAggFunctions = 0;
    private transient DataSetIndexElement longestBuild;
    private transient DataSetIndexElement shortestBuild;
    private transient DataSetIndexElement lessReused;
    private transient DataSetIndexElement mostReused;

    public DataSetIndexStatsImpl(DataSetIndex index) {
        this.index = index;
        index.acceptVisitor(this);
    }

    public void visit(DataSetIndexElement element) {
        buildTime += element.getBuildTime();
        reuseTime += element.getReuseTime();
        indexSize += element.getEstimatedSize();

        if (longestBuild == null || element.getBuildTime() > longestBuild.getBuildTime()) {
            longestBuild = element;
        }
        if (shortestBuild == null || element.getBuildTime() > shortestBuild.getBuildTime()) {
            shortestBuild = element;
        }
        if (lessReused == null || element.getReuseHits() > lessReused.getReuseHits()) {
            lessReused = element;
        }
        if (mostReused == null || element.getReuseHits() > mostReused.getReuseHits()) {
            mostReused = element;
        }

        if (element instanceof DataSetGroupIndex) {
            numberOfGroupOps++;
        }
        if (element instanceof DataSetFilterIndex) {
            numberOfFilterOps++;
        }
        if (element instanceof DataSetSortIndex) {
            numberOfSortOps++;
        }
        if (element instanceof DataSetFunctionIndex) {
            numberOfAggFunctions++;
        }
    }

    public double getReuseRate() {
        if (buildTime == 0) return 0;
        return reuseTime/buildTime;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public long getReuseTime() {
        return reuseTime;
    }

    public DataSetIndexElement getLongestBuild() {
        return longestBuild;
    }

    public DataSetIndexElement getShortestBuild() {
        return shortestBuild;
    }

    public DataSetIndexElement getLessReused() {
        return lessReused;
    }

    public DataSetIndexElement getMostReused() {
        return mostReused;
    }

    public long getIndexSize() {
        return indexSize;
    }

    public int getNumberOfGroupOps() {
        return numberOfGroupOps;
    }

    public int getNumberOfFilterOps() {
        return numberOfFilterOps;
    }

    public int getNumberOfSortOps() {
        return numberOfSortOps;
    }

    public int getNumberOfAggFunctions() {
        return numberOfAggFunctions;
    }

    public String toString() {
        return toString(" ");
    }

    public String toString(String sep) {
        StringBuilder out = new StringBuilder();
        out.append("Index size=").append(MemSizeFormatter.formatSize(getIndexSize())).append(sep);
        out.append("Build time=").append(((double) getBuildTime() / 1000000)).append(" (secs)").append(sep);
        out.append("Reuse time=").append(((double) getReuseTime() / 1000000)).append(" (secs)").append(sep);
        out.append("Reuse rate=").append(getReuseRate()).append(sep);
        out.append("#Group ops=").append(getNumberOfGroupOps()).append(sep);
        out.append("#Filter ops=").append(getNumberOfFilterOps()).append(sep);
        out.append("#Sort ops=").append(getNumberOfSortOps()).append(sep);
        out.append("#Agg funcs=").append(getNumberOfAggFunctions()).append(sep);
        return out.toString();
    }
}

