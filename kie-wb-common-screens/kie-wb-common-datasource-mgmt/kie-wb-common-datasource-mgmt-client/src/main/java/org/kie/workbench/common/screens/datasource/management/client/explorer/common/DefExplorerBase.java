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

import javax.enterprise.event.Observes;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datasource.management.client.wizard.datasource.NewDataSourceDefWizard;
import org.kie.workbench.common.screens.datasource.management.client.wizard.driver.NewDriverDefWizard;
import org.kie.workbench.common.screens.datasource.management.events.BaseDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.BaseDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDriverEvent;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQuery;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQueryResult;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQueryService;

public abstract class DefExplorerBase {

    protected DefExplorerContent defExplorerContent;

    protected NewDataSourceDefWizard newDataSourceDefWizard;

    protected NewDriverDefWizard newDriverDefWizard;

    protected Caller<DefExplorerQueryService> explorerService;

    protected DefExplorerBase(final DefExplorerContent defExplorerContent,
                              final NewDataSourceDefWizard newDataSourceDefWizard,
                              final NewDriverDefWizard newDriverDefWizard,
                              final Caller<DefExplorerQueryService> explorerService) {
        this.defExplorerContent = defExplorerContent;
        this.newDataSourceDefWizard = newDataSourceDefWizard;
        this.newDriverDefWizard = newDriverDefWizard;
        this.explorerService = explorerService;
    }

    protected void init() {
        defExplorerContent.setHandler(new DefExplorerContentView.Handler() {
            @Override
            public void onAddDataSource() {
                DefExplorerBase.this.onAddDataSource();
            }

            @Override
            public void onAddDriver() {
                DefExplorerBase.this.onAddDriver();
            }
        });
    }

    protected abstract void onAddDataSource();

    protected abstract void onAddDriver();

    public void refresh() {
        explorerService.call(getRefreshCallback()).executeQuery(createRefreshQuery());
    }

    protected abstract DefExplorerQuery createRefreshQuery();

    protected RemoteCallback<?> getRefreshCallback() {
        return new RemoteCallback<DefExplorerQueryResult>() {
            @Override
            public void callback(DefExplorerQueryResult content) {
                loadContent(content);
            }
        };
    }

    protected abstract void loadContent(DefExplorerQueryResult content);

    protected abstract boolean refreshOnDataSourceEvent(BaseDataSourceEvent event);

    protected abstract boolean refreshOnDriverEvent(BaseDriverEvent event);

    protected void onDataSourceCreated(@Observes NewDataSourceEvent event) {
        if (refreshOnDataSourceEvent(event)) {
            refresh();
        }
    }

    protected void onDataSourceDeleted(@Observes DeleteDataSourceEvent event) {
        if (refreshOnDataSourceEvent(event)) {
            refresh();
        }
    }

    protected void onDataSourceUpdated(@Observes UpdateDataSourceEvent event) {
        if (refreshOnDataSourceEvent(event)) {
            refresh();
        }
    }

    protected void onDriverCreated(@Observes NewDriverEvent event) {
        if (refreshOnDriverEvent(event)) {
            refresh();
        }
    }

    protected void onDriverUpdated(@Observes UpdateDriverEvent event) {
        if (refreshOnDriverEvent(event)) {
            refresh();
        }
    }

    protected void onDriverDeleted(@Observes DeleteDriverEvent event) {
        if (refreshOnDriverEvent(event)) {
            refresh();
        }
    }
}