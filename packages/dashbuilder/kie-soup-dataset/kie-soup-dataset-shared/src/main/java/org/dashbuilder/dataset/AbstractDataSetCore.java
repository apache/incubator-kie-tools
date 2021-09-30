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
package org.dashbuilder.dataset;

import org.dashbuilder.dataset.engine.Chronometer;
import org.dashbuilder.dataset.engine.SharedDataSetOpEngine;
import org.dashbuilder.dataset.engine.filter.DataSetFilterAlgorithm;
import org.dashbuilder.dataset.engine.filter.DefaultFilterAlgorithm;
import org.dashbuilder.dataset.engine.function.AggregateFunctionManagerImpl;
import org.dashbuilder.dataset.engine.group.IntervalBuilderDynamicLabel;
import org.dashbuilder.dataset.engine.group.IntervalBuilderFixedDate;
import org.dashbuilder.dataset.engine.group.IntervalBuilderLocator;
import org.dashbuilder.dataset.engine.index.TransientDataSetIndexRegistry;
import org.dashbuilder.dataset.engine.index.spi.DataSetIndexRegistry;
import org.dashbuilder.dataset.engine.sort.CollectionsDataSetSort;
import org.dashbuilder.dataset.engine.sort.DataSetSortAlgorithm;
import org.dashbuilder.dataset.group.AggregateFunctionManager;
import org.dashbuilder.dataset.uuid.UUIDGenerator;

public abstract class AbstractDataSetCore {

    private DataSetManager dataSetManager;
    private SharedDataSetOpEngine sharedDataSetOpEngine;
    private IntervalBuilderLocator intervalBuilderLocator;
    private Chronometer chronometer;
    private UUIDGenerator uuidGenerator;
    private AggregateFunctionManager aggregateFunctionManager;
    private DataSetIndexRegistry indexRegistry;
    private DataSetSortAlgorithm sortAlgorithm;
    private DataSetFilterAlgorithm filterAlgorithm;
    private IntervalBuilderDynamicLabel intervalBuilderDynamicLabel;
    private IntervalBuilderFixedDate intervalBuilderFixedDate;

    protected <T extends Object> T checkNotNull(T obj, String name) {
        if (obj == null) {
            throw new IllegalStateException(name + " is null.");
        } else {
            return obj;
        }
    }

    public DataSetManager getDataSetManager() {
        if (dataSetManager == null) {
            dataSetManager = newDataSetManager();
        }
        return dataSetManager;
    }

    public SharedDataSetOpEngine getSharedDataSetOpEngine() {
        if (sharedDataSetOpEngine == null) {
            sharedDataSetOpEngine = newSharedDataSetOpEngine();
        }
        return sharedDataSetOpEngine;
    }

    public AggregateFunctionManager getAggregateFunctionManager() {
        if (aggregateFunctionManager == null) {
            aggregateFunctionManager = newAggregateFunctionManager();
        }
        return aggregateFunctionManager;
    }

    public DataSetIndexRegistry getIndexRegistry() {
        if (indexRegistry == null) {
            indexRegistry = newIndexRegistry();
        }
        return indexRegistry;
    }

    public DataSetSortAlgorithm getSortAlgorithm() {
        if (sortAlgorithm == null) {
            sortAlgorithm = newSortAlgorithm();
        }
        return sortAlgorithm;
    }

    public DataSetFilterAlgorithm getFilterAlgorithm() {
        if (filterAlgorithm == null) {
            filterAlgorithm = newFilterAlgorithm();
        }
        return filterAlgorithm;
    }

    public IntervalBuilderLocator getIntervalBuilderLocator() {
        if (intervalBuilderLocator == null) {
            intervalBuilderLocator = newIntervalBuilderLocator();
        }
        return intervalBuilderLocator;
    }

    public IntervalBuilderDynamicLabel getIntervalBuilderDynamicLabel() {
        if (intervalBuilderDynamicLabel == null) {
            intervalBuilderDynamicLabel = newIntervalBuilderDynamicLabel();
        }
        return intervalBuilderDynamicLabel;
    }

    public IntervalBuilderFixedDate getIntervalBuilderFixedDate() {
        if (intervalBuilderFixedDate == null) {
            intervalBuilderFixedDate = newIntervalBuilderFixedDate();
        }
        return intervalBuilderFixedDate;
    }

    public Chronometer getChronometer() {
        if (chronometer == null) {
            chronometer = newChronometer();
        }
        return chronometer;
    }

    public UUIDGenerator getUuidGenerator() {
        if (uuidGenerator  == null) {
            uuidGenerator = newUuidGenerator();
        }
        return uuidGenerator;
    }

    // Setters

    public void setDataSetManager(DataSetManager dataSetManager) {
        this.dataSetManager = dataSetManager;
    }

    public void setAggregateFunctionManager(AggregateFunctionManager aggregateFunctionManager) {
        this.aggregateFunctionManager = aggregateFunctionManager;
    }

    public void setIndexRegistry(DataSetIndexRegistry indexRegistry) {
        this.indexRegistry = indexRegistry;
    }

    public void setSortAlgorithm(DataSetSortAlgorithm sortAlgorithm) {
        this.sortAlgorithm = sortAlgorithm;
    }

    public void setFilterAlgorithm(DataSetFilterAlgorithm filterAlgorithm) {
        this.filterAlgorithm = filterAlgorithm;
    }

    public void setIntervalBuilderLocator(IntervalBuilderLocator intervalBuilderLocator) {
        this.intervalBuilderLocator = intervalBuilderLocator;
    }

    public void setIntervalBuilderDynamicLabel(IntervalBuilderDynamicLabel intervalBuilderDynamicLabel) {
        this.intervalBuilderDynamicLabel = intervalBuilderDynamicLabel;
    }

    public void setIntervalBuilderFixedDate(IntervalBuilderFixedDate intervalBuilderFixedDate) {
        this.intervalBuilderFixedDate = intervalBuilderFixedDate;
    }

    public void setChronometer(Chronometer chronometer) {
        this.chronometer = chronometer;
    }

    public void setUuidGenerator(UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    // Factory methods for default known implementations

    protected SharedDataSetOpEngine newSharedDataSetOpEngine() {
        return new SharedDataSetOpEngine(
                checkNotNull(getAggregateFunctionManager(), "AggregateFunctionManager"),
                checkNotNull(getIntervalBuilderLocator(), "IntervalBuilderLocator"),
                checkNotNull(getIndexRegistry(), "DataSetIndexRegistry"),
                checkNotNull(getSortAlgorithm(), "DataSetSortAlgorithm"),
                checkNotNull(getFilterAlgorithm(), "DataSetFilterAlgorithm"),
                checkNotNull(getChronometer(), "Chronometer"));
    }

    protected AggregateFunctionManager newAggregateFunctionManager() {
        return new AggregateFunctionManagerImpl();
    }

    protected DataSetIndexRegistry newIndexRegistry() {
        return new TransientDataSetIndexRegistry(
                checkNotNull(getUuidGenerator(), "UUIDGenerator"));
    }

    protected DataSetSortAlgorithm newSortAlgorithm() {
        return new CollectionsDataSetSort();
    }

    protected DataSetFilterAlgorithm newFilterAlgorithm() {
        return new DefaultFilterAlgorithm();
    }

    protected IntervalBuilderDynamicLabel newIntervalBuilderDynamicLabel() {
        return new IntervalBuilderDynamicLabel();
    }

    protected IntervalBuilderFixedDate newIntervalBuilderFixedDate() {
        return new IntervalBuilderFixedDate();
    }

    // Factory methods to be implemented by subclasses

    protected abstract DataSetManager newDataSetManager();

    protected abstract IntervalBuilderLocator newIntervalBuilderLocator();

    protected abstract Chronometer newChronometer();

    protected abstract UUIDGenerator newUuidGenerator();
}

