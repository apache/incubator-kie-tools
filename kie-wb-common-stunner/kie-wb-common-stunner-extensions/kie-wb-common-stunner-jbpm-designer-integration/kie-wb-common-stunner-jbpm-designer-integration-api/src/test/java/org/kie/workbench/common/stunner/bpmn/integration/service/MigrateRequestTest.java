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

package org.kie.workbench.common.stunner.bpmn.integration.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.util.EqualsAndHashCodeTestUtils;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

@RunWith(MockitoJUnitRunner.class)
public class MigrateRequestTest {

    @Mock
    private Path path1;

    @Mock
    private Path path2;

    @Mock
    private ProjectDiagram diagram1;

    @Mock
    private ProjectDiagram diagram2;

    @Test
    public void testEqualsAndHashCode() {
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(MigrateRequest.newFromJBPMDesignerToStunner(path1, "name", "extension", "message", diagram1),
                             MigrateRequest.newFromJBPMDesignerToStunner(path1, "name", "extension", "message", diagram1))
                .addFalseCase(MigrateRequest.newFromJBPMDesignerToStunner(path1, "name", "extension", "message", diagram1),
                              MigrateRequest.newFromJBPMDesignerToStunner(path2, "name", "extension", "message", diagram1))
                .addFalseCase(MigrateRequest.newFromJBPMDesignerToStunner(path1, "name", "extension", "message", diagram1),
                              MigrateRequest.newFromJBPMDesignerToStunner(path1, "name1", "extension", "message", diagram1))
                .addFalseCase(MigrateRequest.newFromJBPMDesignerToStunner(path1, "name", "extension", "message", diagram1),
                              MigrateRequest.newFromJBPMDesignerToStunner(path1, "name", "extension1", "message", diagram1))
                .addFalseCase(MigrateRequest.newFromJBPMDesignerToStunner(path1, "name", "extension", "message", diagram1),
                              MigrateRequest.newFromJBPMDesignerToStunner(path1, "name", "extension", "message1", diagram1))
                .addFalseCase(MigrateRequest.newFromJBPMDesignerToStunner(path1, "name", "extension", "message", diagram1),
                              MigrateRequest.newFromJBPMDesignerToStunner(path1, "name", "extension", "message1", diagram2))
                .addTrueCase(MigrateRequest.newFromStunnerToJBPMDesigner(path1, "name", "extension", "message"),
                             MigrateRequest.newFromStunnerToJBPMDesigner(path1, "name", "extension", "message"))
                .addFalseCase(MigrateRequest.newFromStunnerToJBPMDesigner(path1, "name", "extension", "message"),
                              MigrateRequest.newFromStunnerToJBPMDesigner(path2, "name", "extension", "message"))
                .addFalseCase(MigrateRequest.newFromStunnerToJBPMDesigner(path1, "name", "extension", "message"),
                              MigrateRequest.newFromStunnerToJBPMDesigner(path1, "name1", "extension", "message"))
                .addFalseCase(MigrateRequest.newFromStunnerToJBPMDesigner(path1, "name", "extension", "message"),
                              MigrateRequest.newFromStunnerToJBPMDesigner(path1, "name", "extension1", "message"))
                .addFalseCase(MigrateRequest.newFromStunnerToJBPMDesigner(path1, "name", "extension", "message"),
                              MigrateRequest.newFromStunnerToJBPMDesigner(path1, "name", "extension", "message1"))
                .test();
    }
}
