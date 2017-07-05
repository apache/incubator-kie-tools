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
package org.kie.workbench.common.screens.datasource.management.client.explorer.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.DatabaseStructureExplorerScreen;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;

@Dependent
public class DefExplorerContent
        implements IsElement,
                   DefExplorerContentView.Presenter {

    private DefExplorerContentView view;

    private ManagedInstance<DefItem> itemInstance;

    private Map<String, DataSourceDefInfo> dataSourceItemsMap = new HashMap<>();

    private Map<String, DriverDefInfo> driverItemsMap = new HashMap<>();

    private PlaceManager placeManager;

    private DefExplorerContentView.Handler handler;

    @Inject
    public DefExplorerContent(DefExplorerContentView view,
                              ManagedInstance<DefItem> itemInstance,
                              PlaceManager placeManager) {
        this.view = view;
        this.itemInstance = itemInstance;
        this.placeManager = placeManager;

        view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void loadDataSources(Collection<DataSourceDefInfo> dataSourceDefInfos) {
        clearDataSources();
        if (dataSourceDefInfos != null) {
            DefItem item;
            String itemName;
            for (DataSourceDefInfo dataSourceDefInfo : dataSourceDefInfos) {
                itemName = dataSourceDefInfo.getName() + (dataSourceDefInfo.isManaged() ? "" : " (external)");
                item = createItem();
                item.setName(itemName);
                item.addItemHandler(new DefItemView.ItemHandler() {
                    @Override
                    public void onClick(String itemId) {
                        onDataSourceItemClick(dataSourceItemsMap.get(itemId));
                    }
                });
                dataSourceItemsMap.put(item.getId(),
                                       dataSourceDefInfo);
                view.addDataSourceItem(item);
            }
        }
    }

    public void loadDrivers(Collection<DriverDefInfo> driverDefInfos) {
        clearDrivers();
        if (driverDefInfos != null) {
            DefItem item;
            for (DriverDefInfo driverDefInfo : driverDefInfos) {
                item = createItem();
                item.setName(driverDefInfo.getName());
                item.addItemHandler(new DefItemView.ItemHandler() {
                    @Override
                    public void onClick(String itemId) {
                        onDriverItemClick(driverItemsMap.get(itemId));
                    }
                });
                driverItemsMap.put(item.getId(),
                                   driverDefInfo);
                view.addDriverItem(item);
            }
        }
    }

    public void clear() {
        clearDataSources();
        clearDrivers();
    }

    public void clearDataSources() {
        view.clearDataSources();
        dataSourceItemsMap.clear();
    }

    private void clearDrivers() {
        view.clearDrivers();
        driverItemsMap.clear();
    }

    @Override
    public void onAddDataSource() {
        if (handler != null) {
            handler.onAddDataSource();
        }
    }

    @Override
    public void onAddDriver() {
        if (handler != null) {
            handler.onAddDriver();
        }
    }

    public void setHandler(DefExplorerContentView.Handler handler) {
        this.handler = handler;
    }

    protected void onDataSourceItemClick(DataSourceDefInfo dataSourceDefInfo) {
        PlaceRequest placeRequest;
        if (dataSourceDefInfo.isManaged()) {
            placeRequest = view.createEditorPlaceRequest(dataSourceDefInfo.getPath());
        } else {
            placeRequest = view.createScreenPlaceRequest(DatabaseStructureExplorerScreen.SCREEN_ID);
            placeRequest.addParameter(DatabaseStructureExplorerScreen.DATASOURCE_UUID_PARAM,
                                      dataSourceDefInfo.getUuid());
            placeRequest.addParameter(DatabaseStructureExplorerScreen.DATASOURCE_NAME_PARAM,
                                      dataSourceDefInfo.getName());
        }
        placeManager.goTo(placeRequest);
    }

    protected void onDriverItemClick(DriverDefInfo driverDefInfo) {
        placeManager.goTo(view.createEditorPlaceRequest(driverDefInfo.getPath()));
    }

    protected DefItem createItem() {
        return itemInstance.get();
    }
}