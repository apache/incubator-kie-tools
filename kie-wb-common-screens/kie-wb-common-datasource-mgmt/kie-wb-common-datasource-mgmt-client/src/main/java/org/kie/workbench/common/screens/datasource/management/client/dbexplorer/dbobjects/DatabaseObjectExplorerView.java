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

package org.kie.workbench.common.screens.datasource.management.client.dbexplorer.dbobjects;

import java.util.List;

import com.google.gwt.view.client.AsyncDataProvider;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface DatabaseObjectExplorerView
        extends UberElement< DatabaseObjectExplorerView.Presenter >, HasBusyIndicator {


    interface Presenter {

        void onSearch( );

        void onOpen( DatabaseObjectRow row );
    }

    interface Handler {

        void onOpen( String schemaName, String objectName );
    }

    String getSchema( );

    String getObjectType( );

    String getFilterTerm( );

    void loadSchemaOptions( final List< Pair< String, String > > options, final String selectedOption );

    void loadDatabaseObjectTypeOptions( final List< Pair< String, String > > options );

    void setDataProvider( AsyncDataProvider< DatabaseObjectRow > dataProvider );

    void showHeaderPanel( boolean show );

    void showSchemaSelector( boolean show );

    void showObjectTypeFilter( boolean show );

    void showObjectNameFilter( boolean show );

    void showFilterButton( boolean show );

    void redraw( );
}