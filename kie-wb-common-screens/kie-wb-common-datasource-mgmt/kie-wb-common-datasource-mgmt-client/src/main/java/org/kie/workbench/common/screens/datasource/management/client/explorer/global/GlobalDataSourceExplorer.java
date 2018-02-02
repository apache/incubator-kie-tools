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

package org.kie.workbench.common.screens.datasource.management.client.explorer.global;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.screens.datasource.management.client.explorer.common.DefExplorerBase;
import org.kie.workbench.common.screens.datasource.management.client.explorer.common.DefExplorerContent;
import org.kie.workbench.common.screens.datasource.management.client.explorer.project.ModuleDataSourceExplorerView;
import org.kie.workbench.common.screens.datasource.management.client.wizard.datasource.NewDataSourceDefWizard;
import org.kie.workbench.common.screens.datasource.management.client.wizard.driver.NewDriverDefWizard;
import org.kie.workbench.common.screens.datasource.management.events.BaseDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.BaseDriverEvent;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQuery;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQueryResult;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQueryService;

@Dependent
public class GlobalDataSourceExplorer
        extends DefExplorerBase
        implements ModuleDataSourceExplorerView.Presenter,
                   IsElement {

    private GlobalDataSourceExplorerView view;

    @Inject
    public GlobalDataSourceExplorer(final GlobalDataSourceExplorerView view,
                                    final DefExplorerContent defExplorerContent,
                                    final NewDataSourceDefWizard newDataSourceDefWizard,
                                    final NewDriverDefWizard newDriverDefWizard,
                                    final Caller<DefExplorerQueryService> explorerService) {
        super(defExplorerContent,
              newDataSourceDefWizard,
              newDriverDefWizard,
              explorerService);
        this.view = view;
    }

    @PostConstruct
    protected void init() {
        super.init();
        view.setDataSourceDefExplorer(defExplorerContent);
    }

    @Override
    public void onAddDriver() {
        newDriverDefWizard.setGlobal();
        newDriverDefWizard.start();
    }

    @Override
    public void onAddDataSource() {
        newDataSourceDefWizard.setGlobal();
        newDataSourceDefWizard.start();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    protected DefExplorerQuery createRefreshQuery() {
        return new DefExplorerQuery(true);
    }

    @Override
    protected void loadContent(final DefExplorerQueryResult content) {
        defExplorerContent.loadDataSources(content.getDataSourceDefs());
        defExplorerContent.loadDrivers(content.getDriverDefs());
    }

    @Override
    protected boolean refreshOnDataSourceEvent(BaseDataSourceEvent event) {
        return event.isGlobal();
    }

    @Override
    protected boolean refreshOnDriverEvent(BaseDriverEvent event) {
        return event.isGlobal();
    }
}