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

package org.kie.workbench.common.screens.datasource.management.client.editor.datasource.selector;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.datasourceselector.DataSourceInfo;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.PopupsUtil;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class DataSourceSelector
        implements DataSourceSelectorView.Presenter,
                   org.kie.workbench.common.screens.datamodeller.client.widgets.datasourceselector.DataSourceSelector {

    private DataSourceSelectorView view;

    private PopupsUtil popupsUtil;

    private TranslationService translationService;

    private ListDataProvider<DataSourceSelectorPageRow> dataProvider = new ListDataProvider<>();

    private ParameterizedCommand<DataSourceInfo> onSelectCommand;

    private Module module;

    private Command onCloseCommand;

    private Caller<DataSourceDefQueryService> queryService;

    public DataSourceSelector() {
    }

    @Inject
    public DataSourceSelector(DataSourceSelectorView view,
                              PopupsUtil popupsUtil,
                              TranslationService translationService,
                              Caller<DataSourceDefQueryService> queryService) {
        this.view = view;
        this.queryService = queryService;
        this.popupsUtil = popupsUtil;
        this.translationService = translationService;
        view.init(this);
        dataProvider.addDataDisplay(view.getDisplay());
    }

    @Override
    public void setModuleSelection(final Module module) {
        this.module = module;
    }

    @Override
    public void setGlobalSelection() {
        this.module = null;
    }

    public boolean isGlobalSelection() {
        return module == null;
    }

    public void show(ParameterizedCommand<DataSourceInfo> onSelectCommand,
                     Command onCloseCommand) {
        this.onSelectCommand = onSelectCommand;
        this.onCloseCommand = onCloseCommand;

        if (isGlobalSelection()) {
            queryService.call(getLoadSuccessCallback(),
                              getLoadErrorCallback()).findGlobalDataSources(true);
        } else {
            queryService.call(getLoadSuccessCallback(),
                              getLoadErrorCallback()).findModuleDataSources(module);
        }
    }

    @Override
    public void onClose() {
        dataProvider.getList().clear();
        if (onCloseCommand != null) {
            onCloseCommand.execute();
        }
    }

    @Override
    public void onSelect() {
        if (onSelectCommand != null) {
            onSelectCommand.execute(new DataSourceInfo() {
                @Override
                public boolean isDeployed() {
                    return view.getSelectedRow().getDataSourceDefInfo().isDeployed();
                }

                @Override
                public String getJndi() {
                    if (view.getSelectedRow().getDataSourceDefInfo().getDeploymentInfo() != null) {
                        return view.getSelectedRow().getDataSourceDefInfo().getDeploymentInfo().getJndi();
                    }
                    return null;
                }

                @Override
                public String getUuid() {
                    return view.getSelectedRow().getDataSourceDefInfo().getUuid();
                }
            });
        }
    }

    private RemoteCallback<Collection<DataSourceDefInfo>> getLoadSuccessCallback() {
        return new RemoteCallback<Collection<DataSourceDefInfo>>() {
            @Override
            public void callback(Collection<DataSourceDefInfo> dataSourceDefInfos) {
                loadDataSources(dataSourceDefInfos);
            }
        };
    }

    private ErrorCallback<?> getLoadErrorCallback() {
        return (Message message, Throwable throwable) -> {
                popupsUtil.showErrorPopup(
                        translationService.format(
                                DataSourceManagementConstants.DataSourceSelector_DataSourcesLoadError));
                onClose();
                return false;
        };
    }

    private void loadDataSources(Collection<DataSourceDefInfo> dataSourceDefInfos) {
        dataProvider.getList().clear();
        for (DataSourceDefInfo defInfo : dataSourceDefInfos) {
            dataProvider.getList().add(new DataSourceSelectorPageRow(defInfo));
        }
        dataProvider.flush();
        view.show();
    }
}