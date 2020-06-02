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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.dashbuilder.dataprovider.BeanDataSetProvider;
import org.dashbuilder.dataprovider.StaticDataSetProvider;
import org.dashbuilder.dataset.DataSetGenerator;
import org.dashbuilder.dataset.def.BeanDataSetDef;
import org.dashbuilder.dataset.def.DataSetDef;

@ApplicationScoped
public class RuntimeBeanDataSetProvider extends BeanDataSetProvider {

    protected BeanManager beanManager;
    protected Map<String, DataSetGenerator> generatorMap = new HashMap<>();
    
    public RuntimeBeanDataSetProvider() {
        super();
    }

    @Inject
    public RuntimeBeanDataSetProvider(StaticDataSetProvider staticDataSetProvider,
                                      BeanManager beanManager) {

        super(staticDataSetProvider);
        this.beanManager = beanManager;
    }

    @PostConstruct
    protected void init() {
        Set<Bean<?>> beans = beanManager.getBeans(DataSetGenerator.class);
        for (Bean<?> bean : beans) {
            CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
            DataSetGenerator generator = (DataSetGenerator) beanManager.getReference(bean, DataSetGenerator.class, ctx);
            generatorMap.put(bean.getBeanClass().getName(), generator);
        }
    }

    @Override
    public DataSetGenerator lookupGenerator(DataSetDef def) {
        if (def instanceof BeanDataSetDef) {
            return loadDataSetGenerator((BeanDataSetDef) def);
        }

        throw new IllegalArgumentException("Not a BeanDataSetDef instance");

    }

    private DataSetGenerator loadDataSetGenerator(BeanDataSetDef beanDef) {
        String beanName = beanDef.getGeneratorClass();
        DataSetGenerator generator = generatorMap.get(beanName);
        if (generator != null) {
            return generator;
        } else {
            throw new IllegalArgumentException("Data set generator class not found: " + beanName);
        }
    }

}
