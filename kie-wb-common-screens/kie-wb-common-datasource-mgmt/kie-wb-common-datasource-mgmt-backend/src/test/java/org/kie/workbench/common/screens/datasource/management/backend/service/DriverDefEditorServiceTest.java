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

import java.net.URI;
import java.net.URL;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.core.UnDeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDriverEvent;
import org.kie.workbench.common.screens.datasource.management.model.Def;
import org.kie.workbench.common.screens.datasource.management.model.DefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.util.DriverDefSerializer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DriverDefEditorServiceTest
        extends DefEditorServiceBaseTest {

    @Mock
    private EventSourceMock<NewDriverEvent> newDriverEvent;

    @Mock
    private EventSourceMock<UpdateDriverEvent> updateDriverEvent;

    @Mock
    private EventSourceMock<DeleteDriverEvent> deleteDriverEvent;

    private DriverDefEditorContent driverDefEditorContent;

    private DriverDef driverDef;

    private DriverDef originalDriverDef;

    private URI uri;

    @Before
    public void setup() {
        super.setup();

        editorService = new DriverDefEditorServiceImpl(runtimeManager,
                                                       serviceHelper,
                                                       ioService,
                                                       moduleService,
                                                       optionsFactory,
                                                       pathNamingService,
                                                       artifactResolver,
                                                       newDriverEvent,
                                                       updateDriverEvent,
                                                       deleteDriverEvent);

        driverDef = new DriverDef();
        driverDef.setUuid("uuid");
        driverDef.setName("driverName");
        driverDef.setDriverClass(TestDriver.class.getName());
        driverDef.setGroupId("groupId");
        driverDef.setArtifactId("artifactId");
        driverDef.setVersion("version");

        driverDefEditorContent = new DriverDefEditorContent();
        driverDefEditorContent.setDef(driverDef);
        driverDefEditorContent.setModule(module);

        originalDriverDef = new DriverDef();
        originalDriverDef.setUuid("uuid");
        originalDriverDef.setName("driverNameOriginal");
        originalDriverDef.setDriverClass(TestDriver.class.getName());
        originalDriverDef.setGroupId("groupIdOriginal");
        originalDriverDef.setArtifactId("artifactIdOriginal");
        originalDriverDef.setVersion("versionOriginal");

        try {
            URL resource = getClass().getClassLoader().getResource("DataSourceFiles");
            uri = resource.toURI();
            when(artifactResolver.resolve(driverDef.getGroupId(),
                                          driverDef.getArtifactId(),
                                          driverDef.getVersion())).thenReturn(uri);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected DefEditorContent getExpectedContent() {
        return driverDefEditorContent;
    }

    @Override
    protected String getExpectedDefString() {
        return DriverDefSerializer.serialize(driverDef);
    }

    @Override
    protected String getExpectedFileName() {
        return driverDef.getName() + ".driver";
    }

    @Override
    protected Def getExpectedDef() {
        return driverDef;
    }

    @Override
    protected Def getOriginalDef() {
        return originalDriverDef;
    }

    @Override
    protected String getOriginalDefString() {
        return DriverDefSerializer.serialize(originalDriverDef);
    }

    @Override
    protected void verifyCreateConditions(boolean global) {
        //we wants that:
        try {
            // 1) the definition was deployed.
            verify(runtimeManager,
                   times(1)).deployDriver(driverDef,
                                          DeploymentOptions.create());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // 2) the notification was fired.
        NewDriverEvent expectedEvent;
        if (global) {
            expectedEvent = new NewDriverEvent(driverDef,
                                               SESSION_ID,
                                               IDENTITY);
        } else {
            expectedEvent = new NewDriverEvent(driverDef,
                                               module,
                                               SESSION_ID,
                                               IDENTITY);
        }
        verify(newDriverEvent,
               times(1)).fire(expectedEvent);
    }

    @Override
    protected void verifySaveConditions() {
        //we wants that
        try {
            // 1) the definition was deployed
            verify(runtimeManager,
                   times(1)).deployDriver(driverDef,
                                          DeploymentOptions.create());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // 2) the update notification was fired.
        verify(updateDriverEvent,
               times(1)).fire(new UpdateDriverEvent(driverDef,
                                                    module,
                                                    SESSION_ID,
                                                    IDENTITY,
                                                    originalDriverDef));
    }

    @Override
    protected void verifyDeleteConditions() {
        //we wants that
        try {
            // 1) the definition was un-deployed.
            verify(runtimeManager,
                   times(1)).unDeployDriver(driverDeploymentInfo,
                                            UnDeploymentOptions.forcedUnDeployment());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // 2) the delete notification was fired.
        verify(deleteDriverEvent,
               times(1)).fire(new DeleteDriverEvent(driverDef,
                                                    module,
                                                    SESSION_ID,
                                                    IDENTITY));
    }
}
