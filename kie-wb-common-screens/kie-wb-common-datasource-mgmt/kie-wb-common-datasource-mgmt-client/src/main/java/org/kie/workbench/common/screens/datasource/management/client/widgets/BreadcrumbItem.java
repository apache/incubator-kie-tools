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

package org.kie.workbench.common.screens.datasource.management.client.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.mvp.Command;

@Dependent
public class BreadcrumbItem
        implements BreadcrumbItemView.Presenter, IsElement {

    private BreadcrumbItemView view;

    private Command command;

    public BreadcrumbItem( ) {
    }

    @Inject
    public BreadcrumbItem( BreadcrumbItemView view ) {
        this.view = view;
        view.init( this );
    }

    public void setName( String name ) {
        view.setName( name );
    }

    public void setCommand( Command command ) {
        this.command = command;
    }

    public void setActive( boolean active ) {
        view.setActive( active );
    }

    @Override
    public void onClick( ) {
        if ( command != null ) {
            command.execute( );
        }
    }

    @Override
    public HTMLElement getElement( ) {
        return view.getElement( );
    }

}