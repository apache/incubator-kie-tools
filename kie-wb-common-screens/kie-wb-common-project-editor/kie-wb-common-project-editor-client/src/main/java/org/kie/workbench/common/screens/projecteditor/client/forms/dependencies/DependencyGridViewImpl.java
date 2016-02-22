/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;
import org.uberfire.mvp.Command;

public class DependencyGridViewImpl
        extends Composite
        implements DependencyGridView {

    @UiField( provided = true )
    SimpleTable<EnhancedDependency> dataGrid = new SimpleTable<EnhancedDependency>();

    interface Binder
            extends
            UiBinder<Widget, DependencyGridViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private DependencyGrid presenter;
    private WhiteListColumn whiteListColumn = new WhiteListColumn();

    @UiField
    Button addDependencyButton;

    @UiField
    Button addFromRepositoryDependencyButton;

    public DependencyGridViewImpl() {
        dataGrid.setEmptyTableCaption( ProjectEditorResources.CONSTANTS.NoDependencies() );

        addGroupIdColumn();
        addArtifactIdColumn();
        addVersionColumn();
        addActionColumn();
        addRemoveRowColumn();

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    private void addGroupIdColumn() {
        dataGrid.addColumn( new GroupIdColumn( new RedrawCommand() ),
                            ProjectEditorResources.CONSTANTS.GroupID() );
    }

    private void addArtifactIdColumn() {
        dataGrid.addColumn( new ArtifactIdColumn( new RedrawCommand() ),
                            ProjectEditorResources.CONSTANTS.ArtifactID() );
    }

    private void addVersionColumn() {
        dataGrid.addColumn( new VersionColumn( new RedrawCommand() ),
                            ProjectEditorResources.CONSTANTS.Version() );
    }

    private void addRemoveRowColumn() {
        RemoveColumn column = new RemoveColumn();

        column.setFieldUpdater( new FieldUpdater<EnhancedDependency, String>() {
            @Override
            public void update( final int index,
                                final EnhancedDependency dependency,
                                final String value ) {
                presenter.onRemoveDependency( dependency );
            }
        } );

        dataGrid.addColumn( column,
                            CommonConstants.INSTANCE.Delete() );
    }

    private void addActionColumn() {
        dataGrid.addColumn( whiteListColumn,
                            ProjectEditorResources.CONSTANTS.WhiteList() );
    }

    @Override
    public void setPresenter( DependencyGrid presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setReadOnly() {
        addDependencyButton.setEnabled( false );
        addFromRepositoryDependencyButton.setEnabled( false );
    }

    @UiHandler("addDependencyButton")
    void onAddDependency( ClickEvent event ) {
        presenter.onAddDependency();
    }

    @UiHandler("addFromRepositoryDependencyButton")
    void onAddDependencyFromRepository( ClickEvent event ) {
        presenter.onAddDependencyFromRepository();
    }

    @Override
    public void show( final EnhancedDependencies dependencies ) {
        dataGrid.setRowData( dependencies.asList() );
        dataGrid.redraw();
    }

    @Override
    public void setWhiteList( final WhiteList whiteList ) {
        whiteListColumn.init( presenter,
                              whiteList );
    }

    @Override
    public void redraw() {
        dataGrid.redraw();
    }

    @Override
    public void showLoading() {
        BusyPopup.showMessage( CommonConstants.INSTANCE.Loading() );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    class RedrawCommand
            implements Command {

        @Override public void execute() {
            dataGrid.redraw();
        }
    }
}
