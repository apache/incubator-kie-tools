/*
 * Copyright 2013 JBoss Inc
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

import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Dependency;
import org.gwtbootstrap3.client.ui.Button;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;
import org.uberfire.mvp.Command;

public class DependencyGridViewImpl
        extends Composite
        implements DependencyGridView {

    interface Binder
            extends
            UiBinder<Widget, DependencyGridViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private DependencyGrid presenter;

    @UiField(provided = true)
    SimpleTable<Dependency> dataGrid = new SimpleTable<Dependency>();

    @UiField
    Button addDependencyButton;

    @UiField
    Button addFromRepositoryDependencyButton;

    public DependencyGridViewImpl() {
        dataGrid.setEmptyTableCaption( ProjectEditorResources.CONSTANTS.NoDependencies() );

        addGroupIdColumn();
        addArtifactIdColumn();
        addVersionColumn();
        addScopeColumn();
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

    private void addScopeColumn() {
        dataGrid.addColumn( new ScopeColumn(),
                            ProjectEditorResources.CONSTANTS.Scope() );
    }

    private void addRemoveRowColumn() {
        RemoveColumn column = new RemoveColumn();

        column.setFieldUpdater( new FieldUpdater<Dependency, String>() {
            @Override
            public void update( int index,
                                Dependency dependency,
                                String value ) {
                presenter.onRemoveDependency( dependency );
            }
        } );

        dataGrid.addColumn( column,
                            CommonConstants.INSTANCE.Delete() );
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
        presenter.onAddDependencyButton();
    }

    @UiHandler("addFromRepositoryDependencyButton")
    void onAddDependencyFromRepository( ClickEvent event ) {
        presenter.onAddDependencyFromRepositoryButton();
    }

    @Override
    public void show( final List<Dependency> dependencies ) {
        dataGrid.setRowData( dependencies );
        dataGrid.redraw();
    }

    class RedrawCommand
            implements Command {

        @Override public void execute() {
            dataGrid.redraw();
        }
    }
}
