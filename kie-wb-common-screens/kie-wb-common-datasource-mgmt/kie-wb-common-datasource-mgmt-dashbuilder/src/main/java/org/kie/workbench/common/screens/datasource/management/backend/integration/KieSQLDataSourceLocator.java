/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.backend.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.dashbuilder.dataprovider.SQLDataSourceLocatorCDI;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.def.SQLDataSourceDef;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.impl.AbstractDataSource;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;

@ApplicationScoped
@Specializes
public class KieSQLDataSourceLocator extends SQLDataSourceLocatorCDI {

    private DataSourceDefQueryService queryService;

    private DataSourceRuntimeManager runtimeManager;

    @Inject
    public KieSQLDataSourceLocator( DataSourceDefQueryService queryService,
                                    DataSourceRuntimeManager runtimeManager ) {
        this.queryService = queryService;
        this.runtimeManager = runtimeManager;
    }

    @Override
    public DataSource lookup( SQLDataSetDef def ) throws Exception {
        return ((AbstractDataSource) runtimeManager.lookupDataSource( def.getDataSource() ) ).getInternalDataSource();
    }

    @Override
    public List< SQLDataSourceDef > list( ) {
        Collection<DataSourceDefInfo> dataSourceDefInfos = queryService.findGlobalDataSources( true );
        List<SQLDataSourceDef> result = new ArrayList<>( );
        for ( DataSourceDefInfo dataSourceDefInfo : dataSourceDefInfos ) {
            result.add( new SQLDataSourceDef( dataSourceDefInfo.getUuid(), dataSourceDefInfo.getName() ) );
        }
        return result;
    }
}