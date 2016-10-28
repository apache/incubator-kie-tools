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

package org.kie.workbench.common.screens.datasource.management.util;


import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.SQLDataSetDefBuilder;

/**
 * Builds a by sql data set definition for a given table.
 */
public class DataSetDefBuilder {

    private SQLDataSetDefBuilder defBuilder;

    private boolean _public = true;

    private DataSetDefBuilder () {
        defBuilder = DataSetDefFactory.newSQLDataSetDef( );
    }

    public DataSetDefBuilder dataSetUuid( String dataSetUuid ) {
        defBuilder.uuid( dataSetUuid );
        return this;
    }

    public DataSetDefBuilder dataSetName( String dataSetName ) {
        defBuilder.name( dataSetName );
        return this;
    }

    public DataSetDefBuilder dataSourceUuid( String dataSourceUuid ) {
        defBuilder.dataSource( dataSourceUuid );
        return this;
    }

    public DataSetDefBuilder schema( String schema ) {
        defBuilder.dbSchema( schema );
        return this;
    }

    public DataSetDefBuilder table( String table ) {
        defBuilder.dbTable( table, true );
        return this;
    }

    public DataSetDefBuilder isPublic( boolean isPublic ) {
        this._public = isPublic;
        return this;
    }

    public DataSetDef build( ) {
        DataSetDef def = defBuilder.buildDef();
        def.setPublic( _public );
        return def;
    }

    public static DataSetDefBuilder newBuilder( ) {
        return new DataSetDefBuilder( );
    }
}