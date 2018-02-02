/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.screens.contributors.backend.dataset;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.event.Event;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.events.DataSetStaleEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.contributors.backend.dataset.ContributorsManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.version.VersionRecordService;
import org.uberfire.ext.editor.commons.version.impl.PortableVersionRecord;
import org.uberfire.java.nio.base.version.VersionRecord;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_AUTHOR;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_DATE;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_MSG;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_ORG;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_PROJECT;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_REPO;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContributorsManagerTest {

    @Mock
    protected DataSetDefRegistry dataSetDefRegistry;

    @Mock
    protected OrganizationalUnitService organizationalUnitService;

    @Mock
    protected WorkspaceProjectService projectService;

    @Mock
    protected VersionRecordService recordService;

    @Mock
    protected Event<DataSetStaleEvent> dataSetStaleEvent;

    @InjectMocks
    ContributorsManager contributorsManager;

    @Before
    public void setUp() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<VersionRecord> repositoryHistory1 = new ArrayList<>();
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("02/01/2017"),
                                                         "default://3da0441b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("01/01/2017"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("02/01/2017"),
                                                         "default://3da0443b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/02/2017"),
                                                         "default://3da0441b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/03/2017"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("02/03/2017"),
                                                         "default://3da04471@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/04/2017"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/05/2017"),
                                                         "default://3da0443b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/02/2017"),
                                                         "default://3da0444b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/05/2017"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0443b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0447b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0445b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0457b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0441b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/03/2017"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/04/2017"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/05/2017"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0443b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/0/2017"),
                                                         "default://3da04474@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/08/2016"),
                                                         "default://3da0445b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/08/2016"),
                                                         "default://3da0446b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/09/2016"),
                                                         "default://3da0447b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/10/2016"),
                                                         "default://3da0445b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/11/2016"),
                                                         "default://3da0444b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("01/11/2016"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("01/12/2016"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("01/12/2016"),
                                                         "default://3da0441b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/04/2017"),
                                                         "default://3da0444b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/05/2017"),
                                                         "default://3da0444b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/02/2017"),
                                                         "default://3da0443b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/05/2017"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("02/01/2017"),
                                                         "default://3da0441b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("01/01/2017"),
                                                         "default://3da0442b@project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("02/01/2017"),
                                                         "default://3da0443b@project1/file"));

        List<VersionRecord> repositoryHistory2 = new ArrayList<>();
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/02/2017"),
                                                         "default://3da0441b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/03/2017"),
                                                         "default://3da0442b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("02/03/2017"),
                                                         "default://3da04471@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/04/2017"),
                                                         "default://3da0442b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/05/2017"),
                                                         "default://3da0443b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/02/2017"),
                                                         "default://3da0444b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/05/2017"),
                                                         "default://3da0442b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0443b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0447b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0445b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0457b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0441b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/03/2017"),
                                                         "default://3da0442b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/04/2017"),
                                                         "default://3da0442b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/05/2017"),
                                                         "default://3da0442b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/06/2017"),
                                                         "default://3da0443b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/0/2017"),
                                                         "default://3da04474@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/08/2016"),
                                                         "default://3da0445b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/08/2016"),
                                                         "default://3da0446b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/09/2016"),
                                                         "default://3da0447b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/10/2016"),
                                                         "default://3da0445b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Pere",
                                                         null,
                                                         "",
                                                         sdf.parse("01/11/2016"),
                                                         "default://3da0444b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("01/11/2016"),
                                                         "default://3da0442b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("01/12/2016"),
                                                         "default://3da0442b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("01/12/2016"),
                                                         "default://3da0441b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "David",
                                                         null,
                                                         "",
                                                         sdf.parse("02/01/2017"),
                                                         "default://3da0441b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/02/2017"),
                                                         "default://3da0442b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/03/2017"),
                                                         "default://3da0443b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("02/03/2017"),
                                                         "default://3da0444b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/04/2017"),
                                                         "default://3da0444b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Roger",
                                                         null,
                                                         "",
                                                         sdf.parse("02/05/2017"),
                                                         "default://3da0444b@project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null,
                                                         "Mark",
                                                         null,
                                                         "",
                                                         sdf.parse("01/02/2017"),
                                                         "default://3da0443b@project2/file"));

        System.out.println("SUM = " + (repositoryHistory1.size() + repositoryHistory2.size()));

        final Repository repo1 = makeRepository("testRepo1");
        final Repository repo2 = makeRepository("testRepo2");

        final OrganizationalUnit org1 = makeOrganizationalUnit("test1",
                                                               Arrays.asList(repo1, repo2));

        when(organizationalUnitService.getOrganizationalUnits()).thenReturn(Arrays.asList(org1));

        final Set<WorkspaceProject> projects = new HashSet<>();
        projects.add(makeProject(repo1,
                                 org1,
                                 "project1"));
        projects.add(makeProject(repo2,
                                 org1,
                                 "project2"));

        when(projectService.getAllWorkspaceProjects(eq(org1))).thenReturn(projects);

        when(recordService.loadVersionRecords(any()))
                .thenReturn(repositoryHistory1)
                .thenReturn(repositoryHistory2);
    }

    private Repository makeRepository(final String repositoryAlias) {
        final Repository repository = mock(Repository.class);
        when(repository.getAlias()).thenReturn(repositoryAlias);
        return repository;
    }

    private OrganizationalUnit makeOrganizationalUnit(final String name,
                                                      final List<Repository> repositories) {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(organizationalUnit.getName()).thenReturn(name);
        when(organizationalUnit.getRepositories()).thenReturn(repositories);
        return organizationalUnit;
    }

    private WorkspaceProject makeProject(final Repository repository,
                                         final OrganizationalUnit organizationalUnit,
                                         final String moduleName) throws IOException {
        final Module module = mock(Module.class);
        when(module.getModuleName()).thenReturn(moduleName);

        return new WorkspaceProject(organizationalUnit,
                                    repository,
                                    new Branch("master",
                                               PathFactory.newPath("testFile",
                                                                   "file:///" + moduleName)),
                                    module);
    }

    @Test
    public void testBuildDataSet() throws Exception {
        DataSet dataSet = contributorsManager.buildDataSet(null);
        assertEquals(68,
                     dataSet.getRowCount());
        assertEquals(6,
                     dataSet.getColumns().size());

        DataColumn column = dataSet.getColumns().get(0);
        assertEquals(ColumnType.LABEL,
                     column.getColumnType());
        assertEquals(COLUMN_ORG,
                     column.getId());

        column = dataSet.getColumns().get(1);
        assertEquals(ColumnType.LABEL,
                     column.getColumnType());
        assertEquals(COLUMN_REPO,
                     column.getId());

        column = dataSet.getColumns().get(2);
        assertEquals(ColumnType.LABEL,
                     column.getColumnType());
        assertEquals(COLUMN_PROJECT,
                     column.getId());

        column = dataSet.getColumns().get(3);
        assertEquals(ColumnType.LABEL,
                     column.getColumnType());
        assertEquals(COLUMN_AUTHOR,
                     column.getId());

        column = dataSet.getColumns().get(4);
        assertEquals(ColumnType.TEXT,
                     column.getColumnType());
        assertEquals(COLUMN_MSG,
                     column.getId());

        column = dataSet.getColumns().get(5);
        assertEquals(ColumnType.DATE,
                     column.getColumnType());
        assertEquals(COLUMN_DATE,
                     column.getId());

        DataSetMetadata metadata = dataSet.getMetadata();
        assertNotNull(metadata);
    }
}