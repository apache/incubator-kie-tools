/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder;

import org.dashbuilder.dataprovider.BeanDataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderRegistryImpl;
import org.dashbuilder.dataprovider.StaticDataSetProvider;
import org.dashbuilder.dataset.ChronometerImpl;
import org.dashbuilder.dataset.DataSetDefDeployer;
import org.dashbuilder.dataset.DataSetDefRegistryImpl;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.DataSetManagerImpl;
import org.dashbuilder.dataset.IntervalBuilderDynamicDate;
import org.dashbuilder.dataset.IntervalBuilderLocatorImpl;
import org.dashbuilder.dataset.UUIDGeneratorImpl;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.engine.Chronometer;
import org.dashbuilder.dataset.engine.group.IntervalBuilderLocator;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.dataset.uuid.UUIDGenerator;
import org.dashbuilder.scheduler.Scheduler;

public class DataSetCoreImpl extends DataSetCore {

    private static final String STATIC_DATA_SET_PROVIDER = "StaticDataSetProvider";
    private static final String DATA_SET_DEF_REGISTRY = "DataSetDefRegistry";
    private boolean dataSetPushEnabled = false;
    private int dataSetPushMaxSize = 1024;
    private Scheduler scheduler;
    private DataSetDefRegistry dataSetDefRegistry;
    private DataSetProviderRegistry dataSetProviderRegistry;
    private DataSetDefDeployer dataSetDefDeployer;
    private DataSetManagerImpl dataSetManagerImpl;
    private BeanDataSetProvider beanDataSetProvider;
    private StaticDataSetProvider staticDataSetProvider;
    private IntervalBuilderLocatorImpl intervalBuilderLocator;
    private IntervalBuilderDynamicDate intervalBuilderDynamicDate;
    private ChronometerImpl chronometerImpl;
    private UUIDGeneratorImpl uuidGeneratorImpl;
    private DataSetDefJSONMarshaller dataSetDefJSONMarshaller;

    // Factory methods

    @Override
    public DataSetManager newDataSetManager() {
        return getDataSetManagerImpl();
    }

    @Override
    public IntervalBuilderLocator newIntervalBuilderLocator() {
        return getIntervalBuilderLocatorImpl();
    }

    @Override
    public Chronometer newChronometer() {
        return getChronometerImpl();
    }

    @Override
    public UUIDGenerator newUuidGenerator() {
        return getUUIDGeneratorImpl();
    }

    // Getters

    public boolean isDataSetPushEnabled() {
        return dataSetPushEnabled;
    }

    public int getDataSetPushMaxSize() {
        return dataSetPushMaxSize;
    }

    public DataSetManagerImpl getDataSetManagerImpl() {
        if (dataSetManagerImpl == null) {
            dataSetManagerImpl = new DataSetManagerImpl(
                    checkNotNull(getDataSetDefRegistry(), DATA_SET_DEF_REGISTRY),
                    checkNotNull(getDataSetProviderRegistry(), "DataSetProviderRegistry"),
                    checkNotNull(getStaticDataSetProvider(), STATIC_DATA_SET_PROVIDER),
                    dataSetPushEnabled, dataSetPushMaxSize);

        }
        return dataSetManagerImpl;
    }

    public DataSetDefRegistry getDataSetDefRegistry() {
        if (dataSetDefRegistry == null) {
            dataSetDefRegistry = new DataSetDefRegistryImpl(
                    checkNotNull(getDataSetProviderRegistry(), "DataSetProviderRegistry"),
                    checkNotNull(getScheduler(), "Scheduler"));
        }
        return dataSetDefRegistry;
    }

    public DataSetProviderRegistry getDataSetProviderRegistry() {
        if (dataSetProviderRegistry == null) {
            dataSetProviderRegistry = new DataSetProviderRegistryImpl();
            dataSetProviderRegistry.registerDataProvider(checkNotNull(getStaticDataSetProvider(), STATIC_DATA_SET_PROVIDER));
            dataSetProviderRegistry.registerDataProvider(checkNotNull(getBeanDataSetProvider(), "BeanDataSetProvider"));
        }
        return dataSetProviderRegistry;
    }

    public DataSetDefDeployer getDataSetDefDeployer() {
        if (dataSetDefDeployer == null) {
            dataSetDefDeployer = new DataSetDefDeployer(
                    checkNotNull(getDataSetDefJSONMarshaller(), "DataSetDefJSONMarshaller"),
                    checkNotNull(getDataSetDefRegistry(), DATA_SET_DEF_REGISTRY));
        }
        return dataSetDefDeployer;
    }

    public Scheduler getScheduler() {
        if (scheduler == null) {
            scheduler = new Scheduler();
        }
        return scheduler;
    }

    public StaticDataSetProvider getStaticDataSetProvider() {
        if (staticDataSetProvider == null) {
            staticDataSetProvider = new StaticDataSetProvider(
                    checkNotNull(getSharedDataSetOpEngine(), "SharedDataSetOpEngine"));
        }
        return staticDataSetProvider;
    }

    public BeanDataSetProvider getBeanDataSetProvider() {
        if (beanDataSetProvider == null) {
            beanDataSetProvider = new BeanDataSetProvider(checkNotNull(getStaticDataSetProvider(), STATIC_DATA_SET_PROVIDER));
            getDataSetDefRegistry().addListener(beanDataSetProvider);
        }
        return beanDataSetProvider;
    }

    public IntervalBuilderLocatorImpl getIntervalBuilderLocatorImpl() {
        if (intervalBuilderLocator == null) {
            intervalBuilderLocator = new IntervalBuilderLocatorImpl(
                    checkNotNull(getIntervalBuilderDynamicLabel(), "IntervalBuilderDynamicLabel"),
                    checkNotNull(getIntervalBuilderDynamicDate(), "IntervalBuilderDynamicDateImpl"),
                    checkNotNull(getIntervalBuilderFixedDate(), "IntervalBuilderFixedDate"));
        }
        return intervalBuilderLocator;
    }

    public IntervalBuilderDynamicDate getIntervalBuilderDynamicDate() {
        if (intervalBuilderDynamicDate == null) {
            intervalBuilderDynamicDate = new IntervalBuilderDynamicDate();
        }
        return intervalBuilderDynamicDate;
    }

    public ChronometerImpl getChronometerImpl() {
        if (chronometerImpl == null) {
            chronometerImpl = new ChronometerImpl();
        }
        return chronometerImpl;
    }

    public UUIDGeneratorImpl getUUIDGeneratorImpl() {
        if (uuidGeneratorImpl == null) {
            uuidGeneratorImpl = new UUIDGeneratorImpl();
        }
        return uuidGeneratorImpl;
    }

    @Override
    public DataSetDefJSONMarshaller getDataSetDefJSONMarshaller() {
        if (dataSetDefJSONMarshaller == null) {
            dataSetDefJSONMarshaller = new DataSetDefJSONMarshaller(
                    checkNotNull(getDataSetProviderRegistry(), "DataSetProviderRegistry"));
        }
        return dataSetDefJSONMarshaller;
    }

    // Setters

    public void setDataSetPushEnabled(boolean dataSetPushEnabled) {
        this.dataSetPushEnabled = dataSetPushEnabled;
    }

    public void setDataSetPushMaxSize(int dataSetPushMaxSize) {
        this.dataSetPushMaxSize = dataSetPushMaxSize;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setDataSetDefDeployer(DataSetDefDeployer dataSetDefDeployer) {
        this.dataSetDefDeployer = dataSetDefDeployer;
    }

    public void setBeanDataSetProvider(BeanDataSetProvider beanDataSetProvider) {
        this.beanDataSetProvider = beanDataSetProvider;
    }

    public void setStaticDataSetProvider(StaticDataSetProvider staticDataSetProvider) {
        this.staticDataSetProvider = staticDataSetProvider;
    }

    public void setDataSetDefRegistry(DataSetDefRegistry dataSetDefRegistry) {
        this.dataSetDefRegistry = dataSetDefRegistry;
    }

    public void setDataSetProviderRegistry(DataSetProviderRegistry dataSetProviderRegistry) {
        this.dataSetProviderRegistry = dataSetProviderRegistry;
    }

    public void setIntervalBuilderDynamicDate(IntervalBuilderDynamicDate intervalBuilderDynamicDate) {
        this.intervalBuilderDynamicDate = intervalBuilderDynamicDate;
    }

    @Override
    public void setDataSetDefJSONMarshaller(DataSetDefJSONMarshaller dataSetDefJSONMarshaller) {
        this.dataSetDefJSONMarshaller = dataSetDefJSONMarshaller;
    }
}


