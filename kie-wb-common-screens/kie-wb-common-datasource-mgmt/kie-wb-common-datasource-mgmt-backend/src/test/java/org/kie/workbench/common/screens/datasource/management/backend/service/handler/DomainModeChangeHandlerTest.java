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

package org.kie.workbench.common.screens.datasource.management.backend.service.handler;

import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.UnDeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.Def;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DomainModeChangeHandlerTest
        extends AbstractDefChangeHandlerTest {

    @Override
    protected void setupChangeHandler() {
        changeHandler = new DomainModeChangeHandler(runtimeManager,
                                                    serviceHelper,
                                                    ioService,
                                                    moduleService,
                                                    eventHelper);
    }

    @Override
    protected void verifyUnDeployed(Def def) throws Exception {
        // the domain mode implementation don't do deployments.
        verifyNoUnDeployments();
    }

    @Override
    protected void verifyRegisteredAndDeployed(Path path,
                                               Def def) throws Exception {
        // the definition should have been registered
        verify(defRegistry,
               times(1)).setEntry(path,
                                  def);
        // but the domain mode implementation don't do un-deployments.
        verifyNoDeployments();
    }

    protected void verifyNoUnDeployments() throws Exception {
        verify(runtimeManager,
               never()).unDeployDataSource(any(DataSourceDeploymentInfo.class),
                                           any(UnDeploymentOptions.class));
        verify(runtimeManager,
               never()).unDeployDriver(any(DriverDeploymentInfo.class),
                                       any(UnDeploymentOptions.class));
    }
}