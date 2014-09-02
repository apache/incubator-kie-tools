/*
 * Copyright 2014 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.contributors.client;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSetLookupService;
import org.dashbuilder.dataset.client.DataSetLookupClient;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.RendererLibLocator;
import org.dashbuilder.renderer.google.client.GoogleRenderer;
import org.dashbuilder.renderer.selector.client.SelectorRenderer;
import org.dashbuilder.renderer.table.client.TableRenderer;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;

/**
 * GWT's Entry-point for the Contributors module
 */
@EntryPoint
public class ContributorsEntryPoint {

    @Inject
    private RendererLibLocator rendererLibLocator;

    @Inject
    private DataSetLookupClient dataSetLookupClient;

    @Inject
    private Caller<DataSetLookupService> dataSetLookupService;

    @AfterInitialization
    private void init() {
        // Enable the data set lookup backend service so that the DataSetLookupClient is able to send requests
        // not only to the ClientDataSetManager but also to the remote DataSetLookupService.
        dataSetLookupClient.setLookupService(dataSetLookupService);

        // Set the default renderer lib for each displayer type.
        rendererLibLocator.setDefaultRenderer( DisplayerType.BARCHART, GoogleRenderer.UUID);
        rendererLibLocator.setDefaultRenderer( DisplayerType.PIECHART, GoogleRenderer.UUID);
        rendererLibLocator.setDefaultRenderer( DisplayerType.AREACHART, GoogleRenderer.UUID);
        rendererLibLocator.setDefaultRenderer( DisplayerType.LINECHART, GoogleRenderer.UUID);
        rendererLibLocator.setDefaultRenderer( DisplayerType.BUBBLECHART, GoogleRenderer.UUID);
        rendererLibLocator.setDefaultRenderer( DisplayerType.METERCHART, GoogleRenderer.UUID);
        rendererLibLocator.setDefaultRenderer( DisplayerType.MAP, GoogleRenderer.UUID);
        rendererLibLocator.setDefaultRenderer( DisplayerType.TABLE, TableRenderer.UUID);
        rendererLibLocator.setDefaultRenderer( DisplayerType.SELECTOR, SelectorRenderer.UUID);

        // Disable dataset push as the contributors datasets are constantly changing on the server.
        dataSetLookupClient.setPushRemoteDataSetEnabled(false);
    }
}