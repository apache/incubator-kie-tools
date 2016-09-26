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

package org.drools.workbench.screens.guided.dtable.client.widget.table.utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.enums.EnumDropdownService;
import org.kie.workbench.common.widgets.client.util.ConstraintValueHelper;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.Command;

@Dependent
public class EnumLoaderUtilities {

    private final Caller<EnumDropdownService> enumDropdownService;

    private final Map<String, Map<String, String>> enumCache = new HashMap<>();

    @Inject
    public EnumLoaderUtilities( final Caller<EnumDropdownService> enumDropdownService ) {
        this.enumDropdownService = enumDropdownService;
    }

    public void getEnums( final DropDownData enumDefinition,
                          final Callback<Map<String, String>> callback,
                          final GuidedDecisionTablePresenter presenter,
                          final Command onFetchCommand,
                          final Command onFetchCompleteCommand ) {
        if ( enumDefinition == null ) {
            callback.callback( Collections.<String, String>emptyMap() );
            return;
        }

        //Lookup data from definition if the list of enumerations comes from the definition itself
        if ( enumDefinition.getFixedList() != null ) {
            getEnumsFromFixedList( enumDefinition,
                                   callback );
            return;
        }

        //Lookup data from server if the list of enumerations comes from an external query
        if ( enumDefinition.getQueryExpression() != null ) {
            getEnumsFromServer( enumDefinition,
                                callback,
                                presenter,
                                onFetchCommand,
                                onFetchCompleteCommand );
            return;
        }

        //Fallback. No enum definition, so return an empty collection
        callback.callback( Collections.<String, String>emptyMap() );
    }

    private void getEnumsFromFixedList( final DropDownData enumDefinition,
                                        final Callback<Map<String, String>> callback ) {
        final String key = buildKey( enumDefinition );
        if ( enumCache.containsKey( key ) ) {
            callback.callback( enumCache.get( key ) );
            return;
        }
        final Map<String, String> convertedDropDownData = convertDropDownData( enumDefinition.getFixedList() );
        enumCache.put( key,
                       convertedDropDownData );
        callback.callback( convertedDropDownData );
    }

    private void getEnumsFromServer( final DropDownData enumDefinition,
                                     final Callback<Map<String, String>> callback,
                                     final GuidedDecisionTablePresenter presenter,
                                     final Command onFetchCommand,
                                     final Command onFetchCompleteCommand ) {
        final String key = buildKey( enumDefinition );
        if ( enumCache.containsKey( key ) ) {
            callback.callback( enumCache.get( key ) );
            return;
        }

        //Cache empty value to prevent recurrent calls to the server for the same data.
        //The View will be redrawn once by the batch() command in the success callback.
        enumCache.put( key,
                       Collections.emptyMap() );

        final GuidedDecisionTableView view = presenter.getView();
        final ObservablePath currentPath = presenter.getCurrentPath();

        onFetchCommand.execute();

        enumDropdownService.call( new RemoteCallback<String[]>() {
                                      @Override
                                      public void callback( final String[] items ) {
                                          onFetchCompleteCommand.execute();
                                          final Map<String, String> convertedDropDownData = convertDropDownData( items );
                                          enumCache.put( key,
                                                         convertedDropDownData );
                                          callback.callback( convertedDropDownData );
                                          view.batch();
                                      }
                                  },
                                  new HasBusyIndicatorDefaultErrorCallback( view ) ).loadDropDownExpression( currentPath,
                                                                                                             enumDefinition.getValuePairs(),
                                                                                                             enumDefinition.getQueryExpression() );

    }

    private String buildKey( final DropDownData enumDefinition ) {
        if ( enumDefinition.getFixedList() != null ) {
            return buildFixedListKey( enumDefinition.getFixedList() );
        } else {
            return buildQueryExpressionKey( enumDefinition.getQueryExpression(),
                                            enumDefinition.getValuePairs() );
        }
    }

    private String buildFixedListKey( final String[] items ) {
        final StringBuilder sb = new StringBuilder();
        for ( String item : items ) {
            sb.append( item ).append( "#" );
        }
        final String key = sb.toString();
        return key;
    }

    private String buildQueryExpressionKey( final String queryExpression,
                                            final String[] items ) {
        final StringBuilder sb = new StringBuilder();
        sb.append( queryExpression ).append( "#" );
        sb.append( buildFixedListKey( items ) );
        final String key = sb.toString();
        return key;
    }

    public Map<String, String> convertDropDownData( final String[] dropDownItems ) {
        final Map<String, String> convertedDropDownData = new TreeMap<String, String>();
        if ( dropDownItems.length == 0 ) {
            return convertedDropDownData;
        }
        for ( int i = 0; i < dropDownItems.length; i++ ) {
            final String dropDownItem = dropDownItems[ i ];
            String key = dropDownItem;
            String display = dropDownItem;
            if ( dropDownItem.indexOf( '=' ) > 0 ) {
                final String[] split = ConstraintValueHelper.splitValue( dropDownItem );
                key = split[ 0 ];
                display = split[ 1 ];
            }
            convertedDropDownData.put( key.trim(),
                                       display.trim() );
        }
        return convertedDropDownData;
    }

}
