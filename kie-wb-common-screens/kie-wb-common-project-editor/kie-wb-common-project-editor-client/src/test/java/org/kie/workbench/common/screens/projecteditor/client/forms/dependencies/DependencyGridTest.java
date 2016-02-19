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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.forms.GAVSelectionHandler;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.CallerMock;

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
    private DependencyService dependencyService;

    private GAVSelectionHandler gavSelectionHandler;

    private POM pom;

    private DependencyGrid grid;
    private WhiteList whiteList;

    @Before
    public void setUp() throws Exception {

        pom = makePOM();

        whiteList = new WhiteList();

        when( dependencyService.loadDependenciesWithPackageNames( anyList() ) ).thenAnswer( new Answer<List<Dependency>>() {
            @Override
            public List<Dependency> answer( final InvocationOnMock invocationOnMock ) throws Throwable {
                final List<Dependency> dependencies = ( List<Dependency> ) invocationOnMock.getArguments()[0];

                for ( Dependency dependency : dependencies ) {
                    if ( dependency.toString().equals( "junit:junit:4.11" ) ) {
                        dependency.getPackages().add( "org.junit" );
                    } else if ( dependency.toString().equals( "org.hamcrest:hamcrest-core:1.3" ) ) {
                        dependency.getPackages().add( "org.hamcrest" );
                    } else if ( dependency.toString().equals( "org.drools:drools-core:1.2" ) ) {
                        dependency.getPackages().add( "org.drools.core" );
                    } else if ( dependency.toString().equals( "org.drools:guvnor:1.2" ) ) {
                        dependency.getPackages().add( "org.guvnor" );
                    }
                }

                return dependencies;
            }
        } );

        when( dependencyService.loadDependencies( anyCollection() ) ).thenAnswer( new Answer<Collection<Dependency>>() {
            @Override
            public Collection<Dependency> answer( final InvocationOnMock invocationOnMock )
                    throws Throwable {
                final Collection<GAV> gavs = ( Collection<GAV> ) invocationOnMock.getArguments()[0];

                final ArrayList<Dependency> dependencies = new ArrayList<Dependency>();

                if ( gavs.contains( new GAV( "org.drools", "guvnor", "1.2" ) ) ) {
                    dependencies.add( makeDependency( "org.drools", "drools-core", "1.2", null ) );
                }
                if ( gavs.contains( new GAV( "junit", "junit", "4.11" ) ) ) {
                    dependencies.add( makeDependency( "org.hamcrest", "hamcrest-core", "1.3", null ) );
                }
                return dependencies;
            }
        } );

        grid = new DependencyGrid( dependencySelectorPopup,
                                   view,
                                   new CallerMock<DependencyService>( dependencyService ) );
        ArgumentCaptor<GAVSelectionHandler> gavSelectionHandlerArgumentCaptor = ArgumentCaptor.forClass( GAVSelectionHandler.class );
        verify( dependencySelectorPopup ).addSelectionHandler( gavSelectionHandlerArgumentCaptor.capture() );
        gavSelectionHandler = gavSelectionHandlerArgumentCaptor.getValue();

    }

    private POM makePOM() {
        POM pom = new POM();
        pom.getDependencies().add( makeDependency( "org.drools", "guvnor", "1.2", "compile" ) );
        pom.getDependencies().add( makeDependency( "junit", "junit", "4.11", "test" ) );
        return pom;
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify( view ).setPresenter( grid );
    }

    @Test
    public void testFillListWithTransientDependencies() throws Exception {

        grid.setDependencies( pom,
                              whiteList );

        grid.show();

        verify( view ).setWhiteList( whiteList );

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass( List.class );
        verify( view ).show( listArgumentCaptor.capture() );
        assertEquals( 3, listArgumentCaptor.getValue().size() );

        final Dependency guvnor = assertContains( listArgumentCaptor.getValue(), "org.drools", "guvnor", "1.2", "compile" );
        assertEquals( 1, guvnor.getPackages().size() );
        assertTrue( guvnor.getPackages().contains( "org.guvnor" ) );

        final Dependency droolsCore = assertContains( listArgumentCaptor.getValue(), "org.drools", "drools-core", "1.2", "transitive" );
        assertTrue( droolsCore.getPackages().contains( "org.drools.core" ) );
        final Dependency junit = assertContains( listArgumentCaptor.getValue(), "junit", "junit", "4.11", "test" );
        assertTrue( junit.getPackages().contains( "org.junit" ) );
    }

    @Test
    public void testLoadingDependenciesFails() throws Exception {

        when( dependencyService.loadDependencies( anyCollection() ) ).thenThrow( new NullPointerException() );

        grid.setDependencies( pom, whiteList );
        grid.show();

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass( List.class );
        verify( view ).show( listArgumentCaptor.capture() );
        assertEquals( 2, listArgumentCaptor.getValue().size() );
        assertContains( listArgumentCaptor.getValue(), "org.drools", "guvnor", "1.2", "compile" );
        assertContains( listArgumentCaptor.getValue(), "junit", "junit", "4.11", "test" );
    }

    @Test
    public void testRemove() throws Exception {

        grid.setDependencies( pom, whiteList );
        grid.show();

        reset( view );

        grid.onRemoveDependency( makeDependency( "org.drools", "guvnor", "1.2", "compile" ) );

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass( List.class );
        verify( view ).show( listArgumentCaptor.capture() );
        assertEquals( 1, listArgumentCaptor.getValue().size() );
        assertContains( listArgumentCaptor.getValue(), "junit", "junit", "4.11", "test" );
        //Verify loadDependencies only called once during loading initial dependencies
        verify( dependencyService ).loadDependencies( anyCollection() );
    }

    @Test
    public void testTransitiveDependencyIsAlsoCompileScoped() throws Exception {

        pom.getDependencies().add( makeDependency( "org.drools", "drools-core", "1.2", "compile" ) );

        grid.setDependencies( pom, whiteList );
        grid.show();

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass( List.class );
        verify( view ).show( listArgumentCaptor.capture() );
        assertEquals( 3, listArgumentCaptor.getValue().size() );
        assertContains( listArgumentCaptor.getValue(), "org.drools", "guvnor", "1.2", "compile" );
        assertContains( listArgumentCaptor.getValue(), "org.drools", "drools-core", "1.2", "compile" );
        assertContains( listArgumentCaptor.getValue(), "junit", "junit", "4.11", "test" );

    }

    @Test
    public void testAdd() throws Exception {

        grid.setDependencies( pom, whiteList );
        grid.show();

        reset( view );

        grid.onAddDependency();

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass( List.class );
        verify( view ).show( listArgumentCaptor.capture() );
        assertEquals( 3, listArgumentCaptor.getValue().size() );
        assertContains( listArgumentCaptor.getValue(), "org.drools", "guvnor", "1.2", "compile" );
        assertContains( listArgumentCaptor.getValue(), "junit", "junit", "4.11", "test" );
        assertContains( listArgumentCaptor.getValue(), null, null, null );
        //Verify loadDependencies only called once during loading initial dependencies
        verify( dependencyService ).loadDependencies( anyCollection() );
    }

    @Test
    public void testAddFromRepository() throws Exception {

        GAV gav = new GAV();
        POM pom = new POM( gav );

        grid.setDependencies( pom, whiteList );

        grid.onAddDependencyFromRepository();

        verify( dependencySelectorPopup ).show();

        gavSelectionHandler.onSelection( new GAV( "junit", "junit", "4.11" ) );

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass( List.class );
        verify( view ).show( listArgumentCaptor.capture() );
        final List dependencies = listArgumentCaptor.getValue();
        assertEquals( 2, dependencies.size() );
        assertEquals( 1, pom.getDependencies().size() );
        assertContains( dependencies, "junit", "junit", "4.11", "compile" );
        assertContains( dependencies, "org.hamcrest", "hamcrest-core", "1.3", "transitive" );
    }

    @Test
    public void testTogglePackagesToWhiteList() throws Exception {

        grid.setDependencies( pom,
                              whiteList );

        grid.show();

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
