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

package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.HashMap;
import java.util.HashSet;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class ActiveContextItemsTest {

    @Mock
    ExplorerService explorerService;
    private ActiveContextItems activeContextItems;

    @Before
    public void setUp() throws Exception {
        activeContextItems = new ActiveContextItems( new EventSourceMock<ProjectContextChangeEvent>(),
                                                     new CallerMock<>( explorerService ) );
    }

    @Test
    public void testSetupActiveBranch() throws Exception {

        assertTrue( activeContextItems.setupActiveBranch( getProjectContext( "master" ) ) );

        assertFalse( activeContextItems.setupActiveBranch( getProjectContext( "master" ) ) );

        assertTrue( activeContextItems.setupActiveBranch( getProjectContext( "hahaaNotTheSame" ) ) );
    }

    private ProjectExplorerContent getProjectContext( final String branch ) {
        return new ProjectExplorerContent( new HashSet<>(),
                                           mock( OrganizationalUnit.class ),
                                           new HashSet<>(),
                                           mock( Repository.class ),
                                           branch,
                                           new HashSet<>(),
                                           mock( Project.class ),
                                           mock( FolderListing.class ),
                                           new HashMap<>() );
    }
}