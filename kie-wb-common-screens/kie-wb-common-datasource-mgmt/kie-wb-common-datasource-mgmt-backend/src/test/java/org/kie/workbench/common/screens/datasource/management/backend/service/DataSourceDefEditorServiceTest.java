/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.core.UnDeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.Def;
import org.kie.workbench.common.screens.datasource.management.model.DefEditorContent;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.kie.workbench.common.screens.datasource.management.service.DriverDefEditorService;
import org.kie.workbench.common.screens.datasource.management.util.DataSourceDefSerializer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSourceDefEditorServiceTest
        extends DefEditorServiceBaseTest {

    @Mock
    private DataSourceDefQueryService dataSourceDefQueryService;

    @Mock
    private DriverDefEditorService driverDefService;

    @Mock
    private EventSourceMock<NewDataSourceEvent> newDataSourceEvent;

    @Mock
    private EventSourceMock<UpdateDataSourceEvent> updateDataSourceEvent;

    @Mock
    private Event<DeleteDataSourceEvent> deleteDataSourceEvent;

    private DataSourceDefEditorContent dataSourceDefEditorContent;

    private DataSourceDef dataSourceDef;

    private DataSourceDef originalDataSourceDef;

    @Before
    public void setup() {
        super.setup();

        editorService = new DataSourceDefEditorServiceImpl(runtimeManager,
                                                           serviceHelper,
                                                           ioService,
                                                           moduleService,
                                                           optionsFactory,
                                                           pathNamingService,
                                                           artifactResolver,
                                                           dataSourceDefQueryService,
                                                           driverDefService,
                                                           newDataSourceEvent,
                                                           updateDataSourceEvent,
                                                           deleteDataSourceEvent);

        dataSourceDef = new DataSourceDef();
        dataSourceDef.setUuid("uuid");
        dataSourceDef.setName("dataSourceName");
        dataSourceDef.setConnectionURL("connectionURL");
        dataSourceDef.setUser("user");
        dataSourceDef.setPassword("password");

        dataSourceDefEditorContent = new DataSourceDefEditorContent();
        dataSourceDefEditorContent.setDef(dataSourceDef);
        dataSourceDefEditorContent.setModule(module);

        originalDataSourceDef = new DataSourceDef();
        originalDataSourceDef.setUuid("uuid");
        originalDataSourceDef.setName("dataSourceNameOriginal");
        originalDataSourceDef.setConnectionURL("connectionURLOriginal");
        originalDataSourceDef.setUser("userOriginal");
        originalDataSourceDef.setPassword("passwordOriginal");
    }

    @Override
    protected DefEditorContent getExpectedContent() {
        return dataSourceDefEditorContent;
    }

    @Override
    protected String getExpectedDefString() {
        return DataSourceDefSerializer.serialize(dataSourceDef);
    }

    @Override
    protected String getExpectedFileName() {
        return dataSourceDef.getName() + ".datasource";
    }

    @Override
    protected Def getExpectedDef() {
        return dataSourceDef;
    }

    @Override
    protected Def getOriginalDef() {
        return originalDataSourceDef;
    }

    @Override
    protected String getOriginalDefString() {
        return DataSourceDefSerializer.serialize(originalDataSourceDef);
    }

    @Override
    protected void verifyCreateConditions(boolean global) {
        //we wants that:
        try {
            // 1) the definition was deployed
            verify(runtimeManager,
                   times(1)).deployDataSource(dataSourceDef,
                                              DeploymentOptions.create());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // 2) the notification was fired.
        NewDataSourceEvent expectedEvent;
        if (global) {
            expectedEvent = new NewDataSourceEvent(dataSourceDef,
                                                   SESSION_ID,
                                                   IDENTITY);
        } else {
            expectedEvent = new NewDataSourceEvent(dataSourceDef,
                                                   module,
                                                   SESSION_ID,
                                                   IDENTITY);
        }
        verify(newDataSourceEvent,
               times(1)).fire(expectedEvent);
    }

    @Override
    protected void verifySaveConditions() {
        //we wants that
        try {
            // 1) the definition was deployed
            verify(runtimeManager,
                   times(1)).deployDataSource(dataSourceDef,
                                              DeploymentOptions.create());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // 2) the update notification was fired.
        verify(updateDataSourceEvent,
               times(1)).fire(new UpdateDataSourceEvent(dataSourceDef,
                                                        module,
                                                        SESSION_ID,
                                                        IDENTITY,
                                                        originalDataSourceDef));
    }

    @Override
    protected void verifyDeleteConditions() {
        //we wants that
        try {
            // 1) the definition was un-deployed.
            verify(runtimeManager,
                   times(1)).unDeployDataSource(dataSourceDeploymentInfo,
                                                UnDeploymentOptions.forcedUnDeployment());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // 2) the delete notification was fired.
        verify(deleteDataSourceEvent,
               times(1)).fire(new DeleteDataSourceEvent(dataSourceDef,
                                                        module,
                                                        SESSION_ID,
                                                        IDENTITY));
    }
}