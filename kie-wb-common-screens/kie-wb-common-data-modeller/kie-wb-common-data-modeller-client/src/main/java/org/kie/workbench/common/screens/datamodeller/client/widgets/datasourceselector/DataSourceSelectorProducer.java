/*
 * Copyright 2016 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.datasourceselector;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.uberfire.annotations.Customizable;
import org.uberfire.annotations.FallbackImplementation;

//@Startup(value = StartupType.BOOTSTRAP, priority = -1)
@ApplicationScoped
public class DataSourceSelectorProducer {

    @Inject
    private Instance<DataSourceSelector> dataSourceSelector;

    @Inject
    @FallbackImplementation
    private DataSourceSelector defaultDataSourceSelector;

    @Produces
    @Customizable
    public DataSourceSelector dataSourceSelectorProducer() {
        if ( this.dataSourceSelector.isUnsatisfied() ) {
            return defaultDataSourceSelector;
        }
        return this.dataSourceSelector.get();
    }
}
