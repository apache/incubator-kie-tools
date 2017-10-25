/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.widget.artifact;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import org.guvnor.m2repo.client.widgets.ArtifactListView;
import org.guvnor.m2repo.model.JarListPageRow;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ArtifactSelectorView_SelectColumn;
import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;

@Dependent
@Templated
public class ArtifactSelectorView
        implements IsElement,
                   ArtifactSelectorPresenter.View {

    @Inject
    @DataField("artifact-list-container")
    private Row artifactListContainer;

    @Inject
    @DataField("filter")
    private TextInput filter;

    @Inject
    @DataField("search-button")
    private Button search;

    @Inject
    private TranslationService translationService;

    private ArtifactSelectorPresenter presenter;

    @Override
    public void init(final ArtifactSelectorPresenter presenter) {
        this.presenter = presenter;

        final ArtifactListView artifactListView = presenter.getArtifactListView();

        artifactListView.addColumn(buildSelectColumn(),
                                   getSelectColumnLabel());

        artifactListView.setContentHeight("200px");

        final Style style = artifactListView.asWidget().getElement().getStyle();
        style.setMarginLeft(0,
                            Style.Unit.PX);
        style.setMarginRight(0,
                             Style.Unit.PX);

        artifactListContainer.add(artifactListView);
    }

    private Column<JarListPageRow, String> buildSelectColumn() {
        return new Column<JarListPageRow, String>(new ButtonCell(ButtonSize.EXTRA_SMALL)) {
            public String getValue(final JarListPageRow row) {
                return getSelectColumnLabel();
            }

            {
                setFieldUpdater(new FieldUpdater<JarListPageRow, String>() {
                    public void update(final int index,
                                       final JarListPageRow row,
                                       final String value) {
                        presenter.onArtifactSelected(row.getPath());
                    }
                });
            }
        };
    }

    private String getSelectColumnLabel() {
        return translationService.getTranslation(ArtifactSelectorView_SelectColumn);
    }

    @Override
    public String getFilter() {
        return filter.getValue();
    }

    @Override
    public void clear() {
        filter.setValue(EMPTY_STRING);
    }

    @EventHandler("search-button")
    private void onSearch(@ForEvent("click") final Event event) {
        presenter.onSearch();
    }
}
