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

package org.kie.workbench.common.screens.datasource.management.client.dbexplorer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen( identifier = DatabaseStructureExplorerScreen.SCREEN_ID )
public class DatabaseStructureExplorerScreen
        implements DatabaseStructureExplorerScreenView.Presenter {

    public static final String SCREEN_ID = "DatabaseStructureExplorerScreen";

    public static final String DATASOURCE_UUID_PARAM = "dataSourceUuid";

    public static final String DATASOURCE_NAME_PARAM = "dataSourceName";

    private DatabaseStructureExplorerScreenView view;

    private TranslationService translationService;

    private PlaceRequest placeRequest;

    private String dataSourceName;

    public DatabaseStructureExplorerScreen( ) {
    }

    @Inject
    public DatabaseStructureExplorerScreen( DatabaseStructureExplorerScreenView view,
                                            TranslationService translationService ) {
        this.view = view;
        view.init( this );
        this.translationService = translationService;
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
        String dataSourceUuid = placeRequest.getParameter( DATASOURCE_UUID_PARAM, null );
        dataSourceName = placeRequest.getParameter( DATASOURCE_NAME_PARAM, "" );
        view.initialize( new DatabaseStructureExplorer.Settings( )
                .dataSourceUuid( dataSourceUuid )
                .dataSourceName( dataSourceName ) );
    }

    @WorkbenchPartView
    public IsWidget getView( ) {
        return ElementWrapperWidget.getWidget( view.getElement( ) );
    }

    @WorkbenchPartTitle
    public String getTitle( ) {
        return dataSourceName + " - " +
                translationService.getTranslation( DataSourceManagementConstants.DatabaseStructureExplorerScreen_title );
    }
}