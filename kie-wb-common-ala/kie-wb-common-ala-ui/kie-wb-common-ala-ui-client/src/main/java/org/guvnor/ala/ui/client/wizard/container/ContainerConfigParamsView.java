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
package org.guvnor.ala.ui.client.wizard.container;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.HasData;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigParamsView_ArtifactIdColumn;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigParamsView_ContainerNameColumn;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigParamsView_ContainersEmptyTableCaption;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigParamsView_Delete;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigParamsView_GroupIdColumn;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigParamsView_Title;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigParamsView_VersionColumn;

@Dependent
@Templated
public class ContainerConfigParamsView
        implements IsElement,
                   ContainerConfigParamsPresenter.View {

    @Inject
    @DataField("add-container-button")
    private Button addContainerButton;

    @DataField("containers-table")
    private SimpleTable<ContainerConfig> dataGrid = new SimpleTable<>();

    @Inject
    private TranslationService translationService;

    private ContainerConfigParamsPresenter presenter;

    @Override
    public void init(final ContainerConfigParamsPresenter presenter) {
        this.presenter = presenter;
        dataGrid.setColumnPickerButtonVisible(false);
        dataGrid.setToolBarVisible(false);
        dataGrid.setEmptyTableCaption(translationService.getTranslation(ContainerConfigParamsView_ContainersEmptyTableCaption));
        addNameColumn();
        addGroupIdColumn();
        addArtifactIdColumn();
        addVersionColumn();
        addRemoveRowColumn();
    }

    @Override
    public String getWizardTitle() {
        return translationService.getTranslation(ContainerConfigParamsView_Title);
    }

    @Override
    public HasData<ContainerConfig> getDisplay() {
        return dataGrid;
    }

    private void addNameColumn() {
        Column<ContainerConfig, String> column = new Column<ContainerConfig, String>(new TextCell()) {
            @Override
            public String getValue(ContainerConfig containerConfig) {
                return containerConfig.getName();
            }
        };

        dataGrid.addColumn(column,
                           translationService.getTranslation(ContainerConfigParamsView_ContainerNameColumn));
    }

    private void addGroupIdColumn() {
        Column<ContainerConfig, String> column = new Column<ContainerConfig, String>(new TextCell()) {
            @Override
            public String getValue(ContainerConfig containerConfig) {
                return containerConfig.getGroupId();
            }
        };

        dataGrid.addColumn(column,
                           translationService.getTranslation(ContainerConfigParamsView_GroupIdColumn));
    }

    private void addArtifactIdColumn() {
        Column<ContainerConfig, String> column = new Column<ContainerConfig, String>(new TextCell()) {
            @Override
            public String getValue(ContainerConfig containerConfig) {
                return containerConfig.getArtifactId();
            }
        };

        dataGrid.addColumn(column,
                           translationService.getTranslation(ContainerConfigParamsView_ArtifactIdColumn));
    }

    private void addVersionColumn() {
        Column<ContainerConfig, String> column = new Column<ContainerConfig, String>(new TextCell()) {
            @Override
            public String getValue(ContainerConfig containerConfig) {
                return containerConfig.getVersion();
            }
        };

        dataGrid.addColumn(column,
                           translationService.getTranslation(ContainerConfigParamsView_VersionColumn));
    }

    private void addRemoveRowColumn() {
        ButtonCell buttonCell = new ButtonCell(IconType.TRASH,
                                               ButtonType.DANGER,
                                               ButtonSize.SMALL);
        Column<ContainerConfig, String> column = new Column<ContainerConfig, String>(buttonCell) {
            @Override
            public String getValue(ContainerConfig containerConfig) {
                return translationService.getTranslation(ContainerConfigParamsView_Delete);
            }
        };

        column.setFieldUpdater((index, containerConfig, value) -> presenter.onDeleteContainer(containerConfig));

        dataGrid.addColumn(column,
                           "");
        dataGrid.setColumnWidth(column,
                                90,
                                Style.Unit.PX);
    }

    @EventHandler("add-container-button")
    private void onAddContainer(@ForEvent("click") final Event event) {
        presenter.onAddContainer();
    }
}
