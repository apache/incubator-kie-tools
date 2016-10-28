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

package org.kie.workbench.common.screens.datasource.management.client.dbexplorer.schemas;

import com.google.gwt.view.client.AsyncDataProvider;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface DatabaseSchemaExplorerView
        extends UberElement< DatabaseSchemaExplorerView.Presenter >, HasBusyIndicator {

    interface Presenter {

        void onOpen( DatabaseSchemaRow row );
    }

    interface Handler {

        void onOpen( String schemaName );
    }

    void setDataProvider( AsyncDataProvider< DatabaseSchemaRow > dataProvider );

    void redraw( );
}