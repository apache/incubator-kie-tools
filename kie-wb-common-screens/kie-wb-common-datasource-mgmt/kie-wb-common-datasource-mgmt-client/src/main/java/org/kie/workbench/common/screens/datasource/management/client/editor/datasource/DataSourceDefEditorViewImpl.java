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

package org.kie.workbench.common.screens.datasource.management.client.editor.datasource;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.datasource.management.client.editor.common.DefEditorBaseViewImpl;

@Dependent
@Templated
public class DataSourceDefEditorViewImpl
        extends DefEditorBaseViewImpl
        implements DataSourceDefEditorView {

    @Inject
    @DataField( "header-panel" )
    private Div headerPanel;

    @Inject
    @DataField( "datasource-name-label" )
    private Label dataSourceNameLabel;

    @Inject
    @DataField( "browse-content-button" )
    private Button browserContentButton;

    @Inject
    @DataField( "content-panel-column" )
    private Div contentPanelColumn;

    @Inject
    @DataField( "content-panel" )
    private FlowPanel contentPanel;

    private Presenter presenter;

    public DataSourceDefEditorViewImpl( ) {
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
        super.init( presenter );
    }

    @Override
    public void setDataSourceName( String dataSourceName ) {
        dataSourceNameLabel.setTextContent( dataSourceName );
    }

    @Override
    public void clearContent() {
        contentPanel.clear();
    }

    @Override
    public void setContent( IsWidget content ) {
        contentPanel.add( content );
    }

    @Override
    public void setContentWidth( String width ) {
        contentPanelColumn.setClassName( width );
    }

    @Override
    public void showHeaderPanel( boolean show ) {
        headerPanel.setHidden( !show );
    }

    @Override
    public void showActionsPanel( boolean show ) {
        actionsPanel.getElement().setHidden( !show );
    }

    @EventHandler( "browse-content-button" )
    private void onBrowseButtonClick( ClickEvent event ) {
        presenter.onShowContent( );
    }

}