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
package org.dashbuilder.dataprovider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class DataSetProviderRegistryCDI extends DataSetProviderRegistryImpl {

    @Inject
    private StaticDataSetProviderCDI staticDataSetProviderCDI;

    @Inject
    private BeanDataSetProviderCDI beanDataSetProviderCDI;

    @Inject
    private CSVDataSetProviderCDI csvDataSetProviderCDI;

    @Inject
    private SQLDataSetProviderCDI sqlDataSetProviderCDI;

    @Inject
    private PrometheusDataSetProviderCDI prometheusDataSetProviderCDI;
    
    @Inject
    private KafkaDataSetProviderCDI kafkaDataSetProviderCDI;

    @Inject
    private Instance<DataSetProvider> providerSet;


    @PostConstruct
    public void init() {
        // Register all the providers available in classpath
        var it = providerSet.iterator();
        while (it.hasNext()) {
            var provider = it.next();
            super.registerDataProvider(provider);
        }

        // Register the core providers
        super.registerDataProvider(staticDataSetProviderCDI);
        super.registerDataProvider(beanDataSetProviderCDI);
        super.registerDataProvider(csvDataSetProviderCDI);
        super.registerDataProvider(sqlDataSetProviderCDI);
        super.registerDataProvider(prometheusDataSetProviderCDI);
        super.registerDataProvider(kafkaDataSetProviderCDI);
    }

    public StaticDataSetProviderCDI getStaticDataSetProviderCDI() {
        return staticDataSetProviderCDI;
    }

    public BeanDataSetProviderCDI getBeanDataSetProviderCDI() {
        return beanDataSetProviderCDI;
    }

    public CSVDataSetProviderCDI getCsvDataSetProviderCDI() {
        return csvDataSetProviderCDI;
    }

    public SQLDataSetProviderCDI getSqlDataSetProviderCDI() {
        return sqlDataSetProviderCDI;
    }
    
    public PrometheusDataSetProviderCDI getPrometheusDataSetProviderCDI() {
        return prometheusDataSetProviderCDI;
    }
    
    public KafkaDataSetProviderCDI getKafkaDataSetProviderCDI() {
        return kafkaDataSetProviderCDI;
    }

}
