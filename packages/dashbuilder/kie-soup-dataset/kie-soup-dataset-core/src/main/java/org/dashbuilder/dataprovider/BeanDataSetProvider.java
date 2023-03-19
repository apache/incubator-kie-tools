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
package org.dashbuilder.dataprovider;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetGenerator;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.BeanDataSetDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanDataSetProvider implements DataSetProvider, DataSetDefRegistryListener {

    protected Logger log = LoggerFactory.getLogger(BeanDataSetProvider.class);
    protected StaticDataSetProvider staticDataSetProvider;

    public BeanDataSetProvider() {
    }

    public BeanDataSetProvider(StaticDataSetProvider staticDataSetProvider) {
        this.staticDataSetProvider = staticDataSetProvider;
    }

    public DataSetProviderType getType() {
        return DataSetProviderType.BEAN;
    }

    public StaticDataSetProvider getStaticDataSetProvider() {
        return staticDataSetProvider;
    }

    public DataSetMetadata getDataSetMetadata(DataSetDef def) throws Exception {
        DataSet dataSet = lookupDataSet(def, null);
        if (dataSet == null) {
            return null;
        }
        return dataSet.getMetadata();
    }

    public DataSetGenerator lookupGenerator(DataSetDef def) {
        BeanDataSetDef beanDef = (BeanDataSetDef) def;
        String beanName = beanDef.getGeneratorClass();
        try {
            return (DataSetGenerator) Class.forName(beanName).newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Data set generator can not be instantiated: " + beanName, e);
        }
    }

    public DataSet lookupDataSet(DataSetDef def, DataSetLookup lookup) throws Exception {
        // Look first into the static data set provider since BEAN data sets are statically registered once loaded.
        DataSet dataSet = staticDataSetProvider.lookupDataSet(def.getUUID(), null);

        // If test mode or not exists then invoke the BEAN generator class
        if ((lookup != null && lookup.testMode()) || dataSet == null) {
            BeanDataSetDef beanDef = (BeanDataSetDef) def;
            DataSetGenerator dataSetGenerator = lookupGenerator(def);
            dataSet = dataSetGenerator.buildDataSet(beanDef.getParamaterMap());
            dataSet.setUUID(def.getUUID());
            dataSet.setDefinition(def);

            // Remove non declared columns
            if (!def.isAllColumnsEnabled()) {
                for (DataColumn column : dataSet.getColumns()) {
                    if (def.getColumnById(column.getId()) == null) {
                        dataSet.removeColumn(column.getId());
                    }
                }
            }
            // Register the data set before return
            staticDataSetProvider.registerDataSet(dataSet);
        }
        try {
            // Always do the lookup over the static data set registry.
            dataSet = staticDataSetProvider.lookupDataSet(def, lookup);
        }
        finally {
            // In test mode remove the data set from cache
            if (lookup != null && lookup.testMode()) {
                staticDataSetProvider.removeDataSet(def.getUUID());
            }
        }
        // Return the lookup results
        return dataSet;
    }

    public boolean isDataSetOutdated(DataSetDef def) {
        return false;
    }

    // Listen to changes on the data set definition registry

    @Override
    public void onDataSetDefStale(DataSetDef def) {
        if (DataSetProviderType.BEAN.equals(def.getProvider())) {
            staticDataSetProvider.removeDataSet(def.getUUID());
        }
    }

    @Override
    public void onDataSetDefModified(DataSetDef olDef, DataSetDef newDef) {
        if (DataSetProviderType.BEAN.equals(olDef.getProvider())) {
            staticDataSetProvider.removeDataSet(olDef.getUUID());
        }
    }

    @Override
    public void onDataSetDefRemoved(DataSetDef oldDef) {
        if (DataSetProviderType.BEAN.equals(oldDef.getProvider())) {
            staticDataSetProvider.removeDataSet(oldDef.getUUID());
        }
    }

    @Override
    public void onDataSetDefRegistered(DataSetDef newDef) {

    }
}
