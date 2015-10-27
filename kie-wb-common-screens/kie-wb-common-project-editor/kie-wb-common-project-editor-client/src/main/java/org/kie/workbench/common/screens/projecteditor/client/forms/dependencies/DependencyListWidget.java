/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.guvnor.m2repo.client.widgets.ArtifactListView;
import org.guvnor.m2repo.model.JarListPageRow;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class DependencyListWidget
        extends Composite {

    interface Binder
            extends
            UiBinder<Widget, DependencyListWidget> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    FlowPanel panel;

    @UiField
    TextBox filter;

    @UiField
    Button search;

    private ArtifactListPresenter dependencyPagedJarTable;
    private ParameterizedCommand<String> onPathSelect;

    public DependencyListWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
        search.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                dependencyPagedJarTable.search( filter.getText() );
            }
        } );
    }

    @AfterInitialization
    public void init() {
        dependencyPagedJarTable = IOC.getBeanManager().lookupBean( ArtifactListPresenter.class ).getInstance();

        final Column<JarListPageRow, String> selectColumn = new Column<JarListPageRow, String>( new ButtonCell( ButtonSize.EXTRA_SMALL ) ) {
            public String getValue( JarListPageRow row ) {
                return "Select";
            }
        };
        selectColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
            public void update( final int index,
                                final JarListPageRow row,
                                final String value ) {
                onPathSelect.execute( row.getPath() );
            }
        } );
        final ArtifactListView artifactListView = dependencyPagedJarTable.getView();

        artifactListView.addColumn( selectColumn, "Select" );
        artifactListView.setContentHeight( "200px" );
        artifactListView.asWidget().getElement().getStyle().setMarginLeft( 0, Style.Unit.PX );
        artifactListView.asWidget().getElement().getStyle().setMarginRight( 0, Style.Unit.PX );

        panel.add( artifactListView );
    }

    public void addOnSelect( final ParameterizedCommand<String> onPathSelect ) {
        this.onPathSelect = onPathSelect;
    }

    public void refresh() {
        dependencyPagedJarTable.refresh();
    }

}
