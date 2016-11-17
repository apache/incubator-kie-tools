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
package org.kie.workbench.common.screens.library.client.screens;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class NewProjectScreenTest {

    @Mock
    NewProjectScreen.View view;

    @InjectMocks
    NewProjectScreen newProjectScreen;

    @Mock
    LibraryService libraryService;

    @Mock
    private OrganizationalUnit ou1;

    @Mock
    private OrganizationalUnit ou2;

    CallerMock<LibraryService> libraryServiceCaller;
    private String ouAlias;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>( libraryService );
        newProjectScreen.libraryService = libraryServiceCaller;
    }


    @Test
    public void loadTest() throws Exception {

        when( libraryService.getDefaultLibraryInfo() ).thenReturn( getDefaultLibraryMock() );

        newProjectScreen.load();

        verify( view ).setOUAlias( ouAlias );
        verify( view, times( 2 ) ).addOrganizationUnit( any() );
        verify( view ).setOrganizationUnitSelected( ou2.getIdentifier() );
        assertEquals( ou2.getIdentifier(), newProjectScreen.selectOu );

    }

    private LibraryInfo getDefaultLibraryMock() {
        OrganizationalUnit defaultOrganizationUnit = ou1;
        OrganizationalUnit selectedOrganizationUnit = ou2;

        Set<Project> projects = new HashSet<>();
        projects.add( mock( Project.class ) );
        projects.add( mock( Project.class ) );
        projects.add( mock( Project.class ) );
        Collection<OrganizationalUnit> organizationUnits = Arrays.asList( ou1, ou2 );
        ouAlias = "alias";

        LibraryInfo libraryInfo = new LibraryInfo( defaultOrganizationUnit, selectedOrganizationUnit, projects,
                                                   organizationUnits, ouAlias );
        return libraryInfo;
    }
}