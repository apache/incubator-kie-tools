/*
 * Copyright 2010 JBoss Inc
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

package org.uberfire.ext.widgets.common.client.tables;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserDataGridPreferencesService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class FilterSelectorDropdown<T> {

    private GridPreferencesStore gridPreferenceStore;
    private final List<DataGridFilter<T>> dataGridFilterList = new ArrayList<DataGridFilter<T>>();
    private String selectedFilterKey = "NONE";

    @Inject
    private Caller<UserDataGridPreferencesService> preferencesService;

    public FilterSelectorDropdown( GridPreferencesStore gridPreferences ) {
        this.gridPreferenceStore = gridPreferences;
    }


    public void setGridPreferencesStore( GridPreferencesStore gridPreferences ) {
        this.gridPreferenceStore = gridPreferences;
    }

    public void setPreferencesService( Caller<UserDataGridPreferencesService> preferencesService ) {
        this.preferencesService = preferencesService;
    }

    public void createDropdownButton( final ListBox listbox ) {
        listbox.clear();
        for ( final DataGridFilter<T> dataGridFilter : dataGridFilterList ) {
            listbox.addItem( dataGridFilter.getFilterName(), dataGridFilter.getKey() );
        }
        if ( gridPreferenceStore != null && gridPreferenceStore.getSelectedFilterKey() != null && gridPreferenceStore.getSelectedFilterKey().trim().length() > 0 ) {
            listbox.setSelectedValue( gridPreferenceStore.getSelectedFilterKey() );
            DataGridFilter filter = getFilterByKey( listbox.getValue() );
            if ( filter != null ) {
                filter.getFilterCommand().execute();
            }
        }
        listbox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                DataGridFilter filter = getFilterByKey( listbox.getValue() );
                storeFilterKey( listbox.getValue() );
                if ( filter != null ) {
                    filter.getFilterCommand().execute();
                }
            }
        } );

    }

    public void addFilter( DataGridFilter dataGridFilter ) {
        dataGridFilterList.add( dataGridFilter );
    }

    public void clearFilters( ) {
        dataGridFilterList.clear();
    }

    private DataGridFilter getFilterByKey( String key ) {
        for ( final DataGridFilter<T> dataGridFilter : dataGridFilterList ) {
            if ( dataGridFilter.getKey().equals( key ) ) return dataGridFilter;
        }
        return null;

    }

    public void storeFilterKey( String filterkey ) {
        if ( gridPreferenceStore != null && preferencesService != null ) {
            gridPreferenceStore.setSelectedFilterKey( filterkey );
            preferencesService.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void response ) {
                }
            } ).saveGridPreferences( gridPreferenceStore );
        }
    }


}
