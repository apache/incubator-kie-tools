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

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.transfer.DataTransferAssets;
import org.dashbuilder.transfer.DataTransferExportModel;
import org.dashbuilder.transfer.ExportModelValidationService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class ExportSummaryWizardPage implements WizardPage {

    ContentManagerConstants i18n = ContentManagerConstants.INSTANCE;

    @Inject
    View view;

    @Inject
    Caller<ExportModelValidationService> exportModelValidationService;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    DataTransferAssets assets;

    private Supplier<DataTransferExportModel> exportModelSupplier;
    private ParameterizedCommand<DataTransferExportModel> dataTransferExportModelCallback;
    private DataTransferExportModel exportModel;

    private Command goToDataSetsCommand = () -> {
    };

    private Command goToPagesCommand = () -> {
    };

    public interface View extends UberElemental<ExportSummaryWizardPage> {

        void success(DataTransferExportModel dataTransferExportModel);

        void validationErrors(DataTransferExportModel dataTransferExportModel,
                              Map<String, List<String>> pageDependencies);

        void exportError(DataTransferExportModel dataTransferExportModel, String message);

        void emptyState();

        void validationError(Throwable error);

    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    @Override
    public String getTitle() {
        return i18n.exportWizardTitle();
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        callback.callback(true);
    }

    @Override
    public void initialise() {
        view.init(this);
    }

    @Override
    public void prepareView() {
        validateAndUpdateView();
    }

    public void setGoToDataSetsCommand(Command goToDatasets) {
        this.goToDataSetsCommand = goToDatasets;
    }

    public void setGoToPagesCommand(Command goToPages) {
        this.goToPagesCommand = goToPages;
    }

    public void setExportSummary(Supplier<DataTransferExportModel> exportModelSupplier) {
        this.exportModelSupplier = exportModelSupplier;
    }

    public void setCallback(ParameterizedCommand<DataTransferExportModel> dataTransferExportModelCallback) {
        this.dataTransferExportModelCallback = dataTransferExportModelCallback;
    }

    void confirmDownload() {
        dataTransferExportModelCallback.execute(exportModel);
    }

    public void goToDataSetsPage() {
        goToDataSetsCommand.execute();
    }

    public void goToPagesPage() {
        goToPagesCommand.execute();
    }

    private void validateAndUpdateView() {
        exportModel = exportModelSupplier.get();

        if (exportModel.getPages().isEmpty() && exportModel.getDatasetDefinitions().isEmpty()) {
            view.exportError(exportModel, i18n.nothingToExport());
            return;
        }
        if (exportModel.getPages().isEmpty()) {
            view.exportError(exportModel, i18n.noPagesExported());
            return;
        }

        view.emptyState();
        busyIndicatorView.showBusyIndicator(i18n.validatingExport());
        exportModelValidationService.call((Map<String, List<String>> validation) -> {
            busyIndicatorView.hideBusyIndicator();
            if (validation.isEmpty()) {
                view.success(exportModel);
            } else {
                remapMissingDependencies(validation);
                view.validationErrors(exportModel, validation);
            }
        }, (message, error) -> {
            busyIndicatorView.hideBusyIndicator();
            view.validationError(error);
            return false;
        }).checkMissingDatasets(exportModel);
    }

    void remapMissingDependencies(Map<String, List<String>> validation) {
        if (assets == null) {
            return;
        }
        List<DataSetDef> datasets = assets.getDatasetsDefinitions();
        validation.replaceAll((page, deps) -> {
            return deps.stream()
                       .map(uuid -> datasets.stream()
                                            .filter(ds -> ds.getUUID().equals(uuid))
                                            .map(ds -> ds.getName()).findAny().orElse(uuid))
                       .collect(toList());
        });
    }

    public void setAssets(DataTransferAssets assets) {
        this.assets = assets;
    }

}