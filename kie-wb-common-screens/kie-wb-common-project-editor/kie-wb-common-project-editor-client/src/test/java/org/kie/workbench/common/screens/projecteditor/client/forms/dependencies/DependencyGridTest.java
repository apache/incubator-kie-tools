/*
 * Copyright 2013 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import java.util.List;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.forms.GAVSelectionHandler;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DependencyGridTest {

    @Mock
    private DependencyGridView view;

    @Mock
    private DependencySelectorPopup dependencySelectorPopup;

    private GAVSelectionHandler gavSelectionHandler;

    private DependencyGrid grid;

    @Before
    public void setUp() throws Exception {
        grid = new DependencyGrid( dependencySelectorPopup,
                                   view );
        ArgumentCaptor<GAVSelectionHandler> gavSelectionHandlerArgumentCaptor = ArgumentCaptor.forClass( GAVSelectionHandler.class );
        verify( dependencySelectorPopup ).addSelectionHandler( gavSelectionHandlerArgumentCaptor.capture() );
        gavSelectionHandler = gavSelectionHandlerArgumentCaptor.getValue();
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify( view ).setPresenter( grid );
    }

    @Test
    public void testFillList() throws Exception {

        Dependency dependency = new Dependency();

        POM pom = new POM();
        pom.getDependencies().add( dependency );
        grid.setDependencies( pom );

        grid.show();

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass( List.class );
        verify( view ).show( listArgumentCaptor.capture() );
        assertTrue( listArgumentCaptor.getValue().contains( dependency ) );
    }

    @Test
    public void testAddFromRepository() throws Exception {

        GAV gav = new GAV();
        POM pom = new POM( gav );

        grid.setDependencies( pom );

        grid.onAddDependencyFromRepositoryButton();

        verify( dependencySelectorPopup ).show();

        gavSelectionHandler.onSelection( new GAV( "myGroupID", "myArtifactID", "myVersion" ) );

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass( List.class );
        verify( view ).show( listArgumentCaptor.capture() );
        assertEquals( 1, listArgumentCaptor.getValue().size() );
        assertEquals( 1, pom.getDependencies().size() );
        assertEquals( "myGroupID", ((Dependency) listArgumentCaptor.getValue().get( 0 )).getGroupId() );
        assertEquals( "myArtifactID", ((Dependency) listArgumentCaptor.getValue().get( 0 )).getArtifactId() );
        assertEquals( "myVersion", ((Dependency) listArgumentCaptor.getValue().get( 0 )).getVersion() );
    }

}
