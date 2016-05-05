/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Row;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;

import javax.enterprise.context.Dependent;

/**
 * <p>View implementation for exploring the assigned groups or roles for a given user.</p>
 * <p>This explorer is implemented using a <code>org.gwtbootstrap3.client.ui.LinkedGroup</code> widget.</p>
 *           
 * @since 0.8.0
 */
@Dependent
public class UserGroupsExplorerView extends Composite
        implements
        UserGroupsExplorer.View {

    interface UserGroupsExplorerViewBinder
            extends
            UiBinder<Row, UserGroupsExplorerView> {

    }

    private static UserGroupsExplorerViewBinder uiBinder = GWT.create(UserGroupsExplorerViewBinder.class);

    private UserGroupsExplorer presenter;
    
    interface UserGroupsExplorerViewStyle extends CssResource {
    }

    @UiField
    UserGroupsExplorerViewStyle style;
    
    @UiField(provided = true)
    EntitiesList.View entitiesListView;

    @Override
    public void init(UserGroupsExplorer presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public UserGroupsExplorer.View configure(final EntitiesList.View entitiesList) {
        this.entitiesListView = entitiesList;
        initWidget( uiBinder.createAndBindUi( this ) );
        return this;
    }

    @Override
    public UserGroupsExplorer.View clear() {
        entitiesListView.clear();
        return this;
    }

}