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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.contributors.backend.dataset.ContributorsManager;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.editor.commons.backend.version.VersionRecordService;
import org.uberfire.ext.editor.commons.version.impl.PortableVersionRecord;
import org.uberfire.java.nio.base.version.VersionRecord;

import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ContributorsManagerTest {

    @Mock
    protected DataSetDefRegistry dataSetDefRegistry;

    @Mock
    protected OrganizationalUnitService organizationalUnitService;

    @Mock
    protected KieProjectService projectService;

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
        repositoryHistory1.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("02/01/2017"), "default://3da0441b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("01/01/2017"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("02/01/2017"), "default://3da0443b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/02/2017"), "default://3da0441b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/03/2017"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("02/03/2017"), "default://3da04471@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/04/2017"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/05/2017"), "default://3da0443b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/02/2017"), "default://3da0444b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/05/2017"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/06/2017"), "default://3da0443b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("01/06/2017"), "default://3da0447b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/06/2017"), "default://3da0445b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("01/06/2017"), "default://3da0457b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/06/2017"), "default://3da0441b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/03/2017"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/04/2017"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/05/2017"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/06/2017"), "default://3da0443b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/0/2017"), "default://3da04474@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/08/2016"), "default://3da0445b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/08/2016"), "default://3da0446b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/09/2016"), "default://3da0447b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/10/2016"), "default://3da0445b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/11/2016"), "default://3da0444b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("01/11/2016"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("01/12/2016"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("01/12/2016"), "default://3da0441b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/04/2017"), "default://3da0444b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/05/2017"), "default://3da0444b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/02/2017"), "default://3da0443b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/05/2017"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/06/2017"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("02/01/2017"), "default://3da0441b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("01/01/2017"), "default://3da0442b@myrepo/project1/file"));
        repositoryHistory1.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("02/01/2017"), "default://3da0443b@myrepo/project1/file"));

        List<VersionRecord> repositoryHistory2 = new ArrayList<>();
        repositoryHistory2.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/02/2017"), "default://3da0441b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/03/2017"), "default://3da0442b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("02/03/2017"), "default://3da04471@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/04/2017"), "default://3da0442b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/05/2017"), "default://3da0443b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/02/2017"), "default://3da0444b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/05/2017"), "default://3da0442b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/06/2017"), "default://3da0443b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("01/06/2017"), "default://3da0447b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/06/2017"), "default://3da0445b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("01/06/2017"), "default://3da0457b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/06/2017"), "default://3da0441b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/03/2017"), "default://3da0442b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/04/2017"), "default://3da0442b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/05/2017"), "default://3da0442b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/06/2017"), "default://3da0443b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/0/2017"), "default://3da04474@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/08/2016"), "default://3da0445b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/08/2016"), "default://3da0446b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/09/2016"), "default://3da0447b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/10/2016"), "default://3da0445b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Pere", null, "", sdf.parse("01/11/2016"), "default://3da0444b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("01/11/2016"), "default://3da0442b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("01/12/2016"), "default://3da0442b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("01/12/2016"), "default://3da0441b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "David", null, "", sdf.parse("02/01/2017"), "default://3da0441b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/02/2017"), "default://3da0442b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/03/2017"), "default://3da0443b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("02/03/2017"), "default://3da0444b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/04/2017"), "default://3da0444b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Roger", null, "", sdf.parse("02/05/2017"), "default://3da0444b@myrepo/project2/file"));
        repositoryHistory2.add(new PortableVersionRecord(null, "Mark", null, "", sdf.parse("01/02/2017"), "default://3da0443b@myrepo/project2/file"));

        Repository repo1 = mock(Repository.class);
        when(repo1.getAlias()).thenReturn("test");

        Project project1 = mock(Project.class);
        Project project2 = mock(Project.class);
        when(project1.getProjectName()).thenReturn("project1");
        when(project2.getProjectName()).thenReturn("project2");
        when(project1.getRootPath()).thenReturn(null);
        when(project2.getRootPath()).thenReturn(null);
        Set<Project> projects = new HashSet<>();
        projects.add(project1);
        projects.add(project2);
        when(projectService.getAllProjects(eq(repo1), any())).thenReturn(projects);

        when(recordService.loadVersionRecords(any()))
                .thenReturn(repositoryHistory1)
                .thenReturn(repositoryHistory2);

        OrganizationalUnit org1 = mock(OrganizationalUnit.class);
        OrganizationalUnit org2 = mock(OrganizationalUnit.class);
        when(org1.getName()).thenReturn("test1");
        when(org2.getName()).thenReturn("test2");
        when(org1.getRepositories()).thenReturn(Arrays.asList(repo1));
        when(org2.getRepositories()).thenReturn(Collections.emptyList());
        when(organizationalUnitService.getOrganizationalUnits()).thenReturn(Arrays.asList(org1, org2));
    }

    @Test
    public void testBuildDataSet() throws Exception {
        DataSet dataSet = contributorsManager.buildDataSet(null);
        assertEquals(dataSet.getRowCount(), 68);
        assertEquals(dataSet.getColumns().size(), 6);

        DataColumn column = dataSet.getColumns().get(0);
        assertEquals(column.getColumnType(), ColumnType.LABEL);
        assertEquals(column.getId(), COLUMN_ORG);

        column = dataSet.getColumns().get(1);
        assertEquals(column.getColumnType(), ColumnType.LABEL);
        assertEquals(column.getId(), COLUMN_REPO);

        column = dataSet.getColumns().get(2);
        assertEquals(column.getColumnType(), ColumnType.LABEL);
        assertEquals(column.getId(), COLUMN_PROJECT);

        column = dataSet.getColumns().get(3);
        assertEquals(column.getColumnType(), ColumnType.LABEL);
        assertEquals(column.getId(), COLUMN_AUTHOR);

        column = dataSet.getColumns().get(4);
        assertEquals(column.getColumnType(), ColumnType.TEXT);
        assertEquals(column.getId(), COLUMN_MSG);

        column = dataSet.getColumns().get(5);
        assertEquals(column.getColumnType(), ColumnType.DATE);
        assertEquals(column.getId(), COLUMN_DATE);

        DataSetMetadata metadata = dataSet.getMetadata();
        assertNotNull(metadata);
    }
}