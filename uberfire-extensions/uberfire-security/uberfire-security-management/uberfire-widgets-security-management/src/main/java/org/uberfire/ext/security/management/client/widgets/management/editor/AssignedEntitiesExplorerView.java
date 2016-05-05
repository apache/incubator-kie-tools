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

package org.uberfire.ext.security.management.client.widgets.management.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Heading;
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
public class AssignedEntitiesExplorerView extends Composite
        implements
        AssignedEntitiesExplorer {

    interface AssignedEntitiesExplorerViewBinder
            extends
            UiBinder<Row, AssignedEntitiesExplorerView> {

    }

    private static AssignedEntitiesExplorerViewBinder uiBinder = GWT.create(AssignedEntitiesExplorerViewBinder.class);

    @UiField
    Row headerRow;
            
    @UiField
    Heading headerText;
    
    @UiField(provided = true)
    EntitiesList.View entitiesListView;

    @Override
    public AssignedEntitiesExplorer configure(final String header, final EntitiesList.View entitiesList) {
        this.entitiesListView = entitiesList;
        initWidget( uiBinder.createAndBindUi( this ) );
        
        if ( null != header && header.trim().length() > 0 ) {
            headerText.setText(header);
            headerRow.setVisible(true);
        } else {
            headerText.setText("");
            headerRow.setVisible(false);
        }

        return this;
    }

    @Override
    public AssignedEntitiesExplorer clear() {
        return this;
    }

}