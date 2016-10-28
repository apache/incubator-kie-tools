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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class DefEditorActionsPanelViewImpl
        implements DefEditorActionsPanelView {

    @Inject
    @DataField( "save-button" )
    private Button saveButton;

    @Inject
    @DataField( "cancel-button" )
    private Button cancelButton;

    @Inject
    @DataField( "delete-button" )
    private Button deleteButton;

    private Presenter presenter;

    public DefEditorActionsPanelViewImpl( ) {
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @EventHandler( "save-button" )
    private void onSaveButtonClick( ClickEvent event ) {
        presenter.onSave( );
    }

    @EventHandler( "cancel-button" )
    private void onCancelButtonClick( ClickEvent event ) {
        presenter.onCancel( );
    }

    @EventHandler( "delete-button" )
    private void onDeleteButtonClick( ClickEvent event ) {
        presenter.onDelete( );
    }
}