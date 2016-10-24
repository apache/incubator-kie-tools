/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.driver.impl;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.services.datamodeller.driver.FilterHolder;
import org.kie.workbench.common.services.datamodeller.driver.MethodFilter;
import org.kie.workbench.common.services.datamodeller.driver.NestedClassFilter;
import org.kie.workbench.common.services.datamodeller.driver.SourceFilter;

@ApplicationScoped
public class FilterHolderImpl implements FilterHolder {

    @Inject
    @Any
    private Instance<SourceFilter> sourceFiltersInstance;

    @Inject
    @Any
    private Instance<NestedClassFilter> nestedClassFiltersInstance;

    @Inject
    @Any
    private Instance<MethodFilter> methodFiltersInstance;

    private Collection<SourceFilter> sourceFilters;

    private Collection<NestedClassFilter> nestedClassFilters;

    private Collection<MethodFilter> methodFilters;

    @PostConstruct
    private void init() {
        sourceFilters = StreamSupport.stream( sourceFiltersInstance.spliterator(), false ).collect( Collectors.toList() );
        nestedClassFilters = StreamSupport.stream( nestedClassFiltersInstance.spliterator(), false ).collect( Collectors.toList() );
        methodFilters = StreamSupport.stream( methodFiltersInstance.spliterator(), false ).collect( Collectors.toList() );
    }

    @PreDestroy
    private void tearDown() {
        sourceFilters.forEach( filter -> sourceFiltersInstance.destroy( filter ) );
        sourceFilters.clear();

        nestedClassFilters.forEach( filter -> nestedClassFiltersInstance.destroy( filter ) );
        nestedClassFilters.clear();

        methodFilters.forEach( filter -> methodFiltersInstance.destroy( filter ) );
        methodFilters.clear();
    }

    @Override
    public Collection<SourceFilter> getSourceFilters() {
        return sourceFilters;
    }

    @Override
    public Collection<NestedClassFilter> getNestedClassFilters() {
        return nestedClassFilters;
    }

    @Override
    public Collection<MethodFilter> getMethodFilters() {
        return methodFilters;
    }

}
