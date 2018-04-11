/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.workitem.deploy;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionDeployServicesTest {

    private static final Path PATH1 = PathFactory.newPath("file1", "file://file1");
    private static final Path PATH2 = PathFactory.newPath("file2", "file://file2");
    private static final Metadata METADATA1 =
            new MetadataImpl.MetadataImplBuilder("defSet1")
                    .setTitle("1")
                    .setRoot(PATH1)
                    .build();
    private static final Metadata METADATA2 =
            new MetadataImpl.MetadataImplBuilder("defSet2")
                    .setTitle("2")
                    .setRoot(PATH2)
                    .build();
    @Mock
    private WorkItemDefinitionDeployService service1;

    @Mock
    private WorkItemDefinitionDeployService service2;

    private WorkItemDefinitionDeployServices tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        ArrayList<WorkItemDefinitionDeployService> services = new ArrayList<WorkItemDefinitionDeployService>(2) {{
            add(service1);
            add(service2);
        }};
        tested = new WorkItemDefinitionDeployServices(services,
                                                      new HashMap<>());
    }

    @Test
    public void testDeploy() {
        tested.deploy(METADATA1);
        tested.deploy(METADATA2);
        tested.deploy(METADATA1);
        tested.deploy(METADATA2);
        verify(service1, times(1)).deploy(eq(METADATA1));
        verify(service2, times(1)).deploy(eq(METADATA1));
        verify(service1, times(1)).deploy(eq(METADATA2));
        verify(service2, times(1)).deploy(eq(METADATA2));
    }
}
