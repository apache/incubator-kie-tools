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
package org.dashbuilder.backend.services.dataset.provider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderRegistryImpl;
import org.dashbuilder.dataprovider.StaticDataSetProvider;
import org.dashbuilder.dataprovider.csv.CSVDataSetProvider;
import org.dashbuilder.dataprovider.sql.SQLDataSetProvider;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;

@ApplicationScoped
public class RuntimeDataSetProviderRegistry extends DataSetProviderRegistryImpl {

    @Inject
    StaticDataSetProvider staticDataSetProvider;

    @Inject
    RuntimeBeanDataSetProvider beanDataSetProvider;

    @Inject
    CSVDataSetProvider csvDataSetProvider;

    @Inject
    SQLDataSetProvider sqlDataSetProvider;

    @Inject
    Instance<DataSetProvider> providerSet;

    protected DataSetDefJSONMarshaller dataSetDefJSONMarshaller = new DataSetDefJSONMarshaller(this);

    @PostConstruct
    public void init() {
        DataSetCore.get().setDataSetDefJSONMarshaller(dataSetDefJSONMarshaller);

        // Register all the providers available in classpath
        var it = providerSet.iterator();
        while (it.hasNext()) {
            var provider = it.next();
            super.registerDataProvider(provider);
        }

        // Register the core providers
        super.registerDataProvider(staticDataSetProvider);
        super.registerDataProvider(beanDataSetProvider);
        super.registerDataProvider(csvDataSetProvider);
        super.registerDataProvider(sqlDataSetProvider);
    }

    public StaticDataSetProvider getStaticDataSetProvider() {
        return staticDataSetProvider;
    }

    public RuntimeBeanDataSetProvider getBeanDataSetProvider() {
        return beanDataSetProvider;
    }

    public CSVDataSetProvider getCsvDataSetProvider() {
        return csvDataSetProvider;
    }

    public SQLDataSetProvider getSqlDataSetProvider() {
        return sqlDataSetProvider;
    }

    public DataSetDefJSONMarshaller getDataSetDefJSONMarshaller() {
        return dataSetDefJSONMarshaller;
    }
}
