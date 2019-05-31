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

package org.kie.workbench.common.stunner.project.diagram.impl;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.util.EqualsAndHashCodeTestUtils;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ProjectMetadataImplTest {

    private static final String TITLE1 = "TITLE1";

    private static final String TITLE2 = "TITLE2";

    private static final String DEF_SET1 = "DEF_SET1";

    private static final String DEF_SET2 = "DEF_SET2";

    private static final String MODULE_NAME1 = "MODULE_NAME1";

    private static final String MODULE_NAME2 = "MODULE_NAME2";

    private static final String PROJECT_TYPE1 = "PROJECT_TYPE1";

    private static final String PROJECT_TYPE2 = "PROJECT_TYPE2";

    @Mock
    private Package projectPkg1;

    @Mock
    private Package projectPkg2;

    @Mock
    private Overview overview1;

    @Mock
    private Overview overview2;

    private ProjectMetadata.SVGGenerator diagramSVGGenerator1 = ProjectMetadata.SVGGenerator.JBPM_DESIGNER;

    private ProjectMetadata.SVGGenerator diagramSVGGenerator2 = ProjectMetadata.SVGGenerator.STUNNER;

    @Mock
    private Path path1;

    @Mock
    private Path path2;

    @Mock
    private Path diagramSVGPath1;

    @Mock
    private Path diagramSVGPath2;

    @Test
    public void testMetadataBuilder() {
        ProjectMetadata metadata = new ProjectMetadataImpl.ProjectMetadataBuilder()
                .forDefinitionSetId(DEF_SET1)
                .forTitle(TITLE1)
                .forModuleName(MODULE_NAME1)
                .forProjectPackage(projectPkg1)
                .forOverview(overview1)
                .forPath(path1)
                .forProjectType(PROJECT_TYPE1)
                .forDiagramSVGGenerator(diagramSVGGenerator1)
                .forDiagramSVGPAth(diagramSVGPath1).build();

        assertEquals(DEF_SET1, metadata.getDefinitionSetId());
        assertEquals(TITLE1, metadata.getTitle());
        assertEquals(MODULE_NAME1, metadata.getModuleName());
        assertEquals(projectPkg1, metadata.getProjectPackage());
        assertEquals(overview1, metadata.getOverview());
        assertEquals(path1, metadata.getPath());
        assertEquals(PROJECT_TYPE1, metadata.getProjectType());
        assertEquals(diagramSVGGenerator1, metadata.getDiagramSVGGenerator());
        assertEquals(diagramSVGPath1, metadata.getDiagramSVGPath());
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new ProjectMetadataImpl.ProjectMetadataBuilder()
                                     .forDefinitionSetId(DEF_SET1)
                                     .forTitle(TITLE1)
                                     .forModuleName(MODULE_NAME1)
                                     .forProjectPackage(projectPkg1)
                                     .forOverview(overview1)
                                     .forPath(path1)
                                     .forProjectType(PROJECT_TYPE1)
                                     .forDiagramSVGGenerator(diagramSVGGenerator1)
                                     .forDiagramSVGPAth(diagramSVGPath1).build(),
                             new ProjectMetadataImpl.ProjectMetadataBuilder()
                                     .forDefinitionSetId(DEF_SET1)
                                     .forTitle(TITLE1)
                                     .forModuleName(MODULE_NAME1)
                                     .forProjectPackage(projectPkg1)
                                     .forOverview(overview1)
                                     .forPath(path1)
                                     .forProjectType(PROJECT_TYPE1)
                                     .forDiagramSVGGenerator(diagramSVGGenerator1)
                                     .forDiagramSVGPAth(diagramSVGPath1).build())
                .addFalseCase(new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build(),
                              new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET2)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build())
                .addFalseCase(new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build(),
                              new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE2)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build())
                .addFalseCase(new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build(),
                              new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME2)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build())
                .addFalseCase(new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build(),
                              new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg2)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build())
                .addFalseCase(new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build(),
                              new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview2)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build())
                .addFalseCase(new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build(),
                              new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path2)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build())
                .addFalseCase(new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build(),
                              new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE2)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build())
                .addFalseCase(new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build(),
                              new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator2)
                                      .forDiagramSVGPAth(diagramSVGPath1).build())
                .addFalseCase(new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath1).build(),
                              new ProjectMetadataImpl.ProjectMetadataBuilder()
                                      .forDefinitionSetId(DEF_SET1)
                                      .forTitle(TITLE1)
                                      .forModuleName(MODULE_NAME1)
                                      .forProjectPackage(projectPkg1)
                                      .forOverview(overview1)
                                      .forPath(path1)
                                      .forProjectType(PROJECT_TYPE1)
                                      .forDiagramSVGGenerator(diagramSVGGenerator1)
                                      .forDiagramSVGPAth(diagramSVGPath2).build())
                .test();
    }
}
