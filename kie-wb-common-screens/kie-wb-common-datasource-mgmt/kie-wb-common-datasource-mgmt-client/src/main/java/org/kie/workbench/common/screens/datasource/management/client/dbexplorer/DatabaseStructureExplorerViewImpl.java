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

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.OrderedList;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.datasource.management.client.widgets.BreadcrumbItem;

@Templated
public class DatabaseStructureExplorerViewImpl
        implements DatabaseStructureExplorerView, IsElement {

    private Presenter presenter;

    @Inject
    @DataField( "menu-breadcrumb" )
    private OrderedList breadcrumb;

    @Inject
    @DataField( "content-panel" )
    private Div contentPanel;

    public DatabaseStructureExplorerViewImpl( ) {
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void clearBreadcrumbs() {
        DOMUtil.removeAllChildren( breadcrumb );
    }

    @Override
    public void addBreadcrumbItem( BreadcrumbItem item ) {
        breadcrumb.appendChild( item.getElement() );
    }

    @Override
    public void clearContent( ) {
        DOMUtil.removeAllChildren( contentPanel );
    }

    @Override
    public void setContent( org.jboss.errai.common.client.api.IsElement conent ) {
        contentPanel.appendChild( conent.getElement() );
    }
}