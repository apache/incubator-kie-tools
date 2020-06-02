/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.backend.services.dataset;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.backend.services.dataset.provider.RuntimeDataSetProviderRegistry;
import org.dashbuilder.backend.services.dataset.provider.RuntimeSQLDataSourceLocator;
import org.dashbuilder.dataprovider.StaticDataSetProvider;
import org.dashbuilder.dataprovider.backend.elasticsearch.ElasticSearchDataSetProvider;
import org.dashbuilder.dataprovider.csv.CSVDataSetProvider;
import org.dashbuilder.dataprovider.csv.CSVFileStorage;
import org.dashbuilder.dataprovider.sql.SQLDataSetProvider;
import org.dashbuilder.dataset.DataSetDefRegistryImpl;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.DataSetManagerImpl;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.scheduler.Scheduler;

/**
 * Produces types related to DataSet
 *
 */
public class DataSetServicesProducer {

    @Produces
    @ApplicationScoped
    public CSVDataSetProvider produceCSVProvider(StaticDataSetProvider staticDataSetProvider,
                                                 CSVFileStorage csvStorage) {
        return new CSVDataSetProvider(staticDataSetProvider, csvStorage);
    }

    @Produces
    @ApplicationScoped
    public StaticDataSetProvider produceStaticDataSetProviderCDI() {
        return new StaticDataSetProvider(DataSetCore.get().getSharedDataSetOpEngine());
    }

    @Produces
    @ApplicationScoped
    public SQLDataSetProvider produceSQLDataSetProvider(StaticDataSetProvider staticDataSetProvider,
                                                        RuntimeSQLDataSourceLocator sqlDataSourceLocator) {

        SQLDataSetProvider provider = new SQLDataSetProvider(staticDataSetProvider,
                                                             DataSetCore.get().getIntervalBuilderLocator(),
                                                             DataSetCore.get().getIntervalBuilderDynamicDate(),
                                                             DataSetCore.get().getSharedDataSetOpEngine());

        provider.setDataSourceLocator(sqlDataSourceLocator);
        return provider;
    }

    @Produces
    @ApplicationScoped
    public ElasticSearchDataSetProvider produceElasticSearchDataSetProvider(StaticDataSetProvider staticDataSetProvider) {
        return new ElasticSearchDataSetProvider(staticDataSetProvider,
                                                DataSetCore.get().getIntervalBuilderLocator(),
                                                DataSetCore.get().getIntervalBuilderDynamicDate());
    }

    @Produces
    @ApplicationScoped
    public DataSetDefRegistry produceDataSetDefRegistry(RuntimeDataSetProviderRegistry providerRegistry) {
        Scheduler scheduler = new Scheduler();
        scheduler.init(10);
        return new DataSetDefRegistryImpl(providerRegistry, scheduler);
    }

    @Produces
    @ApplicationScoped
    public DataSetManager produceDataSetManager(StaticDataSetProvider staticDataSetProvider,
                                                DataSetDefRegistry dataSetDefRegistry,
                                                RuntimeDataSetProviderRegistry dataSetProviderRegistry) {
        return new DataSetManagerImpl(dataSetDefRegistry,
                                      dataSetProviderRegistry,
                                      staticDataSetProvider,
                                      true,
                                      1024);
    }

}
