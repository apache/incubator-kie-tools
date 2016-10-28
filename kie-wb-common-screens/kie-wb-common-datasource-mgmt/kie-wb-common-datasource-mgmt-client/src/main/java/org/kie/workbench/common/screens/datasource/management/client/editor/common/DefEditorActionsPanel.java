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

package org.kie.workbench.common.screens.datasource.management.client.editor.common;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.mvp.Command;

@Dependent
public class DefEditorActionsPanel
        implements DefEditorActionsPanelView.Presenter, IsElement {

    private DefEditorActionsPanelView view;

    private Command saveCommand;

    private Command cancelCommand;

    private Command deleteCommand;

    public DefEditorActionsPanel( ) {
    }

    @Inject
    public DefEditorActionsPanel( DefEditorActionsPanelView view ) {
        this.view = view;
        view.init( this );
    }

    public void setSaveCommand( Command saveCommand ) {
        this.saveCommand = saveCommand;
    }

    public void setCancelCommand( Command cancelCommand ) {
        this.cancelCommand = cancelCommand;
    }

    public void setDeleteCommand( Command deleteCommand ) {
        this.deleteCommand = deleteCommand;
    }

    @Override
    public HTMLElement getElement( ) {
        return view.getElement( );
    }

    @Override
    public void onSave( ) {
        if ( saveCommand != null ) {
            saveCommand.execute( );
        }
    }

    @Override
    public void onCancel( ) {
        if ( cancelCommand != null ) {
            cancelCommand.execute( );
        }
    }

    @Override
    public void onDelete( ) {
        if ( deleteCommand != null ) {
            deleteCommand.execute( );
        }
    }
}