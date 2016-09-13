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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import java.util.HashSet;

import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.Util.makeDependency;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class EnhancedDependenciesManager_onEchancedDependenciesLoadedTest {

    @Mock
    private DependencyLoader dependencyLoader;

    @InjectMocks
    private EnhancedDependenciesManager enhancedDependenciesManager;

    private Dependencies         originalSetOfDependencies;
    private EnhancedDependencies shownDependencies;
    private Dependency           junit;

    @Before
    public void setUp() throws Exception {
        shownDependencies = null;
        originalSetOfDependencies = new Dependencies();
        junit = makeDependency( "org.junit", "junit", "1.0" );
        originalSetOfDependencies.add( junit );
        originalSetOfDependencies.add( makeDependency( "org.drools", "drools-core", "4.0" ) );

        final POM pom = new POM();
        pom.setDependencies( originalSetOfDependencies );
        enhancedDependenciesManager.init( pom,
                                          new Callback<EnhancedDependencies>() {
                                              @Override
                                              public void callback( final EnhancedDependencies result ) {
                                                  shownDependencies = result;
                                              }
                                          } );
    }

    @Test
    public void testShowEmpty() throws Exception {
        originalSetOfDependencies.clear();
        enhancedDependenciesManager.onEnhancedDependenciesUpdated( new EnhancedDependencies() );
        assertNotNull( shownDependencies );
    }

    @Test
    public void testAdd() throws Exception {
        final Dependency dependency = makeDependency( "artifactId", "groupId", "1.0" );

        enhancedDependenciesManager.addNew( dependency );

        final EnhancedDependencies loadedEnhancedDependencies = new EnhancedDependencies();
        loadedEnhancedDependencies.add( new NormalEnhancedDependency( dependency,
                                                                      new HashSet<String>() ) );
        enhancedDependenciesManager.onEnhancedDependenciesUpdated( loadedEnhancedDependencies );

        assertEquals( 3, originalSetOfDependencies.size() );
        assertTrue( originalSetOfDependencies.contains( dependency ) );

        assertEquals( 1, shownDependencies.size() );
        assertTrue( shownDependencies.asList().get( 0 ).getDependency().isGAVEqual( dependency ) );
    }

    @Test
    public void testEditingEnhancedUpdatesOriginal() throws Exception {

        final EnhancedDependencies loadedEnhancedDependencies = new EnhancedDependencies();
        loadedEnhancedDependencies.add( new NormalEnhancedDependency( makeDependency( "org.junit", "junit", "1.0" ),
                                                                      new HashSet<String>() ) );
        enhancedDependenciesManager.onEnhancedDependenciesUpdated( loadedEnhancedDependencies );

        final EnhancedDependency enhancedDependency = shownDependencies.asList().get( 0 );

        enhancedDependency.getDependency().setArtifactId( "newId" );
        assertEquals( "newId", junit.getArtifactId() );
    }

    @Test
    public void testDelete() throws Exception {
        enhancedDependenciesManager.delete( new NormalEnhancedDependency( makeDependency( "org.junit", "junit", "1.0" ),
                                                                          new HashSet<String>() ) );

        verify( dependencyLoader ).load();

        // Queue is empty so loader returns with nothing.
        enhancedDependenciesManager.onEnhancedDependenciesUpdated( new EnhancedDependencies() );

        assertTrue( shownDependencies.isEmpty() );
        assertFalse( originalSetOfDependencies.contains( junit ) );
    }

}