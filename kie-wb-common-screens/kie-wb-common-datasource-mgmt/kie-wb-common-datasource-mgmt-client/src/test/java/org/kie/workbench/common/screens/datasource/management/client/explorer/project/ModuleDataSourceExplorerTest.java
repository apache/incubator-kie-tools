/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.explorer.project;

import java.util.Optional;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.explorer.common.ExplorerBaseTest;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ModuleDataSourceExplorerTest
        extends ExplorerBaseTest {

    private ModuleDataSourceExplorer projectExplorer;

    @GwtMock
    private ModuleDataSourceExplorerView view;

    @Mock
    private OrganizationalUnit org1;

    @Mock
    private Repository repo1;

    @Mock
    private Module module1;

    @Mock
    private Path path;

    @Before
    public void setup() {
        super.setup();

        projectExplorer = new ModuleDataSourceExplorer(view,
                                                       explorerContent,
                                                       dataSourceDefWizard,
                                                       driverDefWizard,
                                                       queryServiceCaller);
        explorerBase = projectExplorer;

        //emulate the execution of the @PostConstruct init() method.
        projectExplorer.init();

        when(org1.getName()).thenReturn("org1");
        when(repo1.getAlias()).thenReturn("repo1");
        when(repo1.getDefaultBranch()).thenReturn(Optional.of(new Branch("master",
                                                                         mock(Path.class))));
        when(module1.getRootPath()).thenReturn(path);

        //emulate that OU/repository/project are selected.
        projectExplorer.setActiveOrganizationalUnit(org1);
        projectExplorer.setActiveRepository(repo1);
        projectExplorer.setActiveModule(module1);

        //prepare the query result.
        result.getOrganizationalUnits().add(org1);
        result.getRepositories().add(repo1);
        result.getModules().add(module1);
    }
}
