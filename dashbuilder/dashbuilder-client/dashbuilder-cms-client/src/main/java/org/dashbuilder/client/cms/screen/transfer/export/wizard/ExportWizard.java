/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.client.cms.screen.transfer.export.wizard;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.dashbuilder.transfer.DataTransferAssets;
import org.dashbuilder.transfer.DataTransferExportModel;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageSelectedEvent;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class ExportWizard extends AbstractWizard {
    
    ContentManagerConstants i18n = ContentManagerConstants.INSTANCE;

    @Inject
    DataSetsWizardPage dataSetsWizardPage;

    @Inject
    PagesWizardPage pagesWizardPage;

    @Inject
    ExportSummaryWizardPage exportSummaryWizardPage;

    @Inject
    Event<WizardPageSelectedEvent> wizardPageSelectedEvent;

    private List<WizardPage> wizardPages;

    private boolean canConclude;

    @PostConstruct
    void init() {
        wizardPages = new ArrayList<>();
        wizardPages.add(dataSetsWizardPage);
        wizardPages.add(pagesWizardPage);
        wizardPages.add(exportSummaryWizardPage);

        exportSummaryWizardPage.setExportSummary(this::getDataTransferExportModel);
        exportSummaryWizardPage.setGoToDataSetsCommand(() -> goTo(dataSetsWizardPage));
        exportSummaryWizardPage.setGoToPagesCommand(() -> goTo(pagesWizardPage));
    }

    public void start(DataTransferAssets assets) {
        dataSetsWizardPage.setDataSets(assets.getDatasetsDefinitions());
        pagesWizardPage.setPages(assets.getPages());
        exportSummaryWizardPage.setAssets(assets);
        this.start();
    }

    @Override
    public List<WizardPage> getPages() {
        return wizardPages;
    }

    @Override
    public Widget getPageWidget(int pageNumber) {
        return wizardPages.get(pageNumber).asWidget();
    }

    @Override
    public String getTitle() {
        return i18n.exportWizardTitle();
    }

    @Override
    public int getPreferredWidth() {
        return 900;
    }

    @Override
    public int getPreferredHeight() {
        return 600;
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        callback.callback(canConclude);
    }

    private void goTo(WizardPage page) {
        wizardPageSelectedEvent.fire(new WizardPageSelectedEvent(page));
    }

    public DataTransferExportModel getDataTransferExportModel() {
        return new DataTransferExportModel(dataSetsWizardPage.getSelectedDataSetDefs(),
                                           pagesWizardPage.getSelectedPages(),
                                           true);
    }

    public void setCallback(ParameterizedCommand<DataTransferExportModel> dataTransferExportModelCallback) {
        exportSummaryWizardPage.setCallback(dataTransferExportModelCallback);
    }
    
    @Override
    public void pageSelected(int pageNumber) {
        super.pageSelected(pageNumber);
        WizardPage page = getPages().get(pageNumber);
        page.prepareView();
        this.canConclude = page == exportSummaryWizardPage;
        checkPagesState();
    }

}