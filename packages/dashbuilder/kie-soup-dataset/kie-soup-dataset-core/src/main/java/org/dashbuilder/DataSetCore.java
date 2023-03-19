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
import org.dashbuilder.dataprovider.StaticDataSetProvider;
import org.dashbuilder.dataset.AbstractDataSetCore;
import org.dashbuilder.dataset.DataSetDefDeployer;
import org.dashbuilder.dataset.IntervalBuilderDynamicDate;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.scheduler.Scheduler;

/**
 * Interface that initializes and provides access to all services and components of the data set core subsystem
 *
 * TODO: Convert to a pure interface after upgrading to Java 8 (static methods in interfaces)
 */
public abstract class DataSetCore extends AbstractDataSetCore {

    private static DataSetCore _instance = null;

    public static DataSetCore get() {
        if (_instance == null) {
            _instance = new DataSetCoreImpl();
        }
        return _instance;
    }

    // For testing mocking purposes mainly
    public static void set(DataSetCore dataSetCore) {
        _instance = dataSetCore;
    }

    public abstract boolean isDataSetPushEnabled();

    public abstract int getDataSetPushMaxSize();

    public abstract Scheduler getScheduler();

    public abstract DataSetDefRegistry getDataSetDefRegistry();

    public abstract DataSetProviderRegistry getDataSetProviderRegistry();

    public abstract DataSetDefDeployer getDataSetDefDeployer();

    public abstract BeanDataSetProvider getBeanDataSetProvider();

    public abstract StaticDataSetProvider getStaticDataSetProvider();

    public abstract IntervalBuilderDynamicDate getIntervalBuilderDynamicDate();

    public abstract DataSetDefJSONMarshaller getDataSetDefJSONMarshaller();

    public abstract void setDataSetPushEnabled(boolean dataSetPushEnabled);

    public abstract void setDataSetPushMaxSize(int dataSetPushMaxSize);

    public abstract void setScheduler(Scheduler scheduler);

    public abstract void setDataSetDefRegistry(DataSetDefRegistry dataSetDefRegistry);

    public abstract void setDataSetProviderRegistry(DataSetProviderRegistry dataSetProviderRegistry);

    public abstract void setDataSetDefDeployer(DataSetDefDeployer dataSetDefDeployer);

    public abstract void setBeanDataSetProvider(BeanDataSetProvider beanDataSetProvider);

    public abstract void setStaticDataSetProvider(StaticDataSetProvider staticDataSetProvider);

    public abstract void setIntervalBuilderDynamicDate(IntervalBuilderDynamicDate intervalBuilderDynamicDate);

    public abstract void setDataSetDefJSONMarshaller(DataSetDefJSONMarshaller dataSetDefJSONMarshaller);
}


