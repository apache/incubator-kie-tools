/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.widget.artifact;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import org.guvnor.m2repo.client.widgets.ArtifactListView;
import org.guvnor.m2repo.model.JarListPageRow;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;

@Dependent
@Templated
public class ArtifactListWidgetView
        extends Composite implements ArtifactListWidgetPresenter.View {

    private ArtifactListWidgetPresenter presenter;

    private TranslationService translationService;

    @Inject
    @DataField("row-place-list")
    Row panel;

    @Inject
    @DataField
    TextBox filter;

    @Inject
    @DataField
    Button search;

    @Inject
    public ArtifactListWidgetView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final ArtifactListWidgetPresenter presenter ) {
        this.presenter = presenter;

        search.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.search( filter.getText() );
            }
        } );

        final ArtifactListView artifactListView = presenter.getArtifactListView();

        artifactListView.addColumn( buildSelectColumn(), getSelectColumnLabel() );

        artifactListView.setContentHeight( "200px" );

        final Style style = artifactListView.asWidget().getElement().getStyle();
        style.setMarginLeft( 0, Style.Unit.PX );
        style.setMarginRight( 0, Style.Unit.PX );

        panel.add( artifactListView );
    }

    private Column<JarListPageRow, String> buildSelectColumn() {
        return new Column<JarListPageRow, String>( new ButtonCell( ButtonSize.EXTRA_SMALL ) ) {
            public String getValue( final JarListPageRow row ) {
                return getSelectColumnLabel();
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

    private String getSelectColumnLabel() {
        return translationService.format( Constants.ArtifactListWidgetView_SelectColumnLabel );
    }
}
