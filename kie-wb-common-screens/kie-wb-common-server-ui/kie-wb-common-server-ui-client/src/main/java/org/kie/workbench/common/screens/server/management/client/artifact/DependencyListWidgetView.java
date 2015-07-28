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

package org.kie.workbench.common.screens.server.management.client.artifact;

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
import org.guvnor.m2repo.client.widgets.ArtifactListView;
import org.guvnor.m2repo.model.JarListPageRow;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;

@Dependent
public class DependencyListWidgetView
        extends Composite implements DependencyListWidgetPresenter.View {

    interface Binder
            extends
            UiBinder<Widget, DependencyListWidgetView> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    FlowPanel panel;

    @UiField
    TextBox filter;

    @UiField
    Button search;

    private DependencyListWidgetPresenter presenter;

    public DependencyListWidgetView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final DependencyListWidgetPresenter presenter ) {
        this.presenter = presenter;

        search.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.search( filter.getText() );
            }
        } );

        final ArtifactListView artifactListView = presenter.getArtifactListPresenter().getView();

        artifactListView.addColumn( buildSelectColumn(), "Select" );

        artifactListView.setContentHeight( "200px" );

        final Style style = artifactListView.asWidget().getElement().getStyle();
        style.setMarginLeft( 0, Style.Unit.PX );
        style.setMarginRight( 0, Style.Unit.PX );

        panel.add( artifactListView );
    }

    private Column<JarListPageRow, String> buildSelectColumn() {
        return new Column<JarListPageRow, String>( new ButtonCell( ButtonSize.EXTRA_SMALL ) ) {
            public String getValue( final JarListPageRow row ) {
                return "Select";
            }

            {
                setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
                    public void update( final int index,
                                        final JarListPageRow row,
                                        final String value ) {
                        presenter.onSelect( row.getPath() );
                    }
                } );
            }
        };
    }
}
