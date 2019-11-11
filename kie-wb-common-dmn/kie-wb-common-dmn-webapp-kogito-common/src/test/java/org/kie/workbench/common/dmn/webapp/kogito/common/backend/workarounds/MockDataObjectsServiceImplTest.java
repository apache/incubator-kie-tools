/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.common.backend.workarounds;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MockDataObjectsServiceImplTest {

    @Mock
    private WorkspaceProject workspaceProject;

    private MockDataObjectsServiceImpl dataObjectsService;

    @Before
    public void setup() {
        this.dataObjectsService = new MockDataObjectsServiceImpl();
    }

    @Test
    public void testLoadDataObjectsWithNullWorkspaceProject() {
        assertThat(dataObjectsService.loadDataObjects(null)).isEmpty();
    }

    @Test
    public void testLoadDataObjectsWithNonNullWorkspaceProject() {
        assertThat(dataObjectsService.loadDataObjects(workspaceProject)).isEmpty();
    }
}
