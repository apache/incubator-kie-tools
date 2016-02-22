/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashSet;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.forms.GAVSelectionHandler;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.projecteditor.client.forms.DependencyTestUtils.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class DependencyGridTest {

    @Mock
    private DependencyGridView view;

    @Mock
    private DependencySelectorPopup dependencySelectorPopup;

    @Mock
    private NewDependencyPopup newDependencyPopup;

    @Mock
    private EnhancedDependenciesManager enhancedDependenciesManager;

    private DependencyGrid grid;

    @Before
    public void setUp() throws Exception {

        grid = new DependencyGrid( dependencySelectorPopup,
                                   newDependencyPopup,
                                   enhancedDependenciesManager,
                                   view );
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify( view ).setPresenter( grid );
    }

    @Test
    public void testSetEnhancedDependencies() throws Exception {

        grid.setDependencies( new POM(),
                              new WhiteList() );

        ArgumentCaptor<Callback> callbackArgumentCaptor = ArgumentCaptor.forClass( Callback.class );
        verify( enhancedDependenciesManager ).init( any( POM.class ),
                                                    callbackArgumentCaptor.capture() );

        final EnhancedDependencies enhancedDependencies = new EnhancedDependencies();
        callbackArgumentCaptor.getValue().callback( enhancedDependencies );

        verify( view ).hideBusyIndicator();
        verify( view ).show( enhancedDependencies );
    }

    @Test
    public void testShow() throws Exception {

        grid.show();

        verify( enhancedDependenciesManager ).update();
    }

    @Test
    public void testRemove() throws Exception {

        final NormalEnhancedDependency dependency = new NormalEnhancedDependency( makeDependency( "org.drools", "guvnor", "1.2", "compile" ),
                                                                                  new HashSet<>(  ));

        grid.onRemoveDependency( dependency );

        verify( enhancedDependenciesManager ).delete( dependency );
    }

    @Test
    public void testSetWhiteList() throws Exception {
        final WhiteList whiteList = new WhiteList();

        grid.setDependencies( new POM(),
                              whiteList );

        verify( view ).setWhiteList( whiteList );
    }

    @Test
    public void testShowDeps() throws Exception {
        final POM pom = new POM();

        pom.getDependencies().add( makeDependency( "org.drools", "drools-core", "1.2", "compile" ) );

        grid.setDependencies( pom,
                              new WhiteList() );

        verify( enhancedDependenciesManager ).init( eq( pom ),
                                                    any( Callback.class ) );
    }

    @Test
    public void testAdd() throws Exception {

        grid.onAddDependency();

        final Dependency dependency = userCreatesANewDependencyUsingPopup();

        verify( enhancedDependenciesManager ).addNew( dependency );
    }

    private Dependency userCreatesANewDependencyUsingPopup() {
        ArgumentCaptor<Callback> callbackArgumentCaptor = ArgumentCaptor.forClass( Callback.class );

        verify( newDependencyPopup ).show( callbackArgumentCaptor.capture() );

        final Dependency dependency = new Dependency();

        callbackArgumentCaptor.getValue().callback( dependency );
        return dependency;
    }

    @Test
    public void testAddFromRepositoryPopupOpens() throws Exception {
        grid.onAddDependencyFromRepository();

        verify( dependencySelectorPopup ).show();

    }

    @Test
    public void testAddFromRepository() throws Exception {

        ArgumentCaptor<GAVSelectionHandler> gavSelectionHandlerArgumentCaptor = ArgumentCaptor.forClass( GAVSelectionHandler.class );
        verify( dependencySelectorPopup ).addSelectionHandler( gavSelectionHandlerArgumentCaptor.capture() );
        GAVSelectionHandler gavSelectionHandler = gavSelectionHandlerArgumentCaptor.getValue();

        final GAV gav = new GAV( "junit", "junit", "4.11" );
        gavSelectionHandler.onSelection( gav );


        ArgumentCaptor<Dependency> dependencyArgumentCaptor = ArgumentCaptor.forClass( Dependency.class );
        verify( enhancedDependenciesManager ).addNew( dependencyArgumentCaptor.capture() );


        final Dependency dependency = dependencyArgumentCaptor.getValue();
        assertTrue( dependency.isGAVEqual( gav ) );
        assertEquals( "compile", dependency.getScope() );
    }

    @Test
    public void testUpdateViewAfterWhiteListChange() throws Exception {
        grid.setDependencies( new POM(),
                              new WhiteList() );
        grid.onTogglePackagesToWhiteList( new HashSet<>() );

        verify( view ).redraw();
    }

    @Test
    public void testTogglePackagesToWhiteList() throws Exception {

        final WhiteList whiteList = new WhiteList();

        grid.setDependencies( new POM(),
                              whiteList );

        final HashSet<String> packages = new HashSet<String>();
        packages.add( "org.drools" );
        packages.add( "org.guvnor" );

        assertEquals( 0, whiteList.size() );

        grid.onTogglePackagesToWhiteList( packages );

        assertTrue( whiteList.containsAll( packages ) );
        assertEquals( 2, whiteList.size() );

        grid.onTogglePackagesToWhiteList( packages );

        assertEquals( 0, whiteList.size() );
    }
}
