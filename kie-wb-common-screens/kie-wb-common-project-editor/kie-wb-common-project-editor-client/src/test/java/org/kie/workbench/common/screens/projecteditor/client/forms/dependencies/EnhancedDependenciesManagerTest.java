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
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class EnhancedDependenciesManagerTest {

    @Mock
    private DependencyLoader dependencyLoader;

    @InjectMocks
    private EnhancedDependenciesManager enhancedDependenciesManager;

    @Mock
    private Callback<EnhancedDependencies> callback;

    private Dependencies originalSetOfDependencies;

    @Before
    public void setUp() throws Exception {
        originalSetOfDependencies = new Dependencies();

        final POM pom = new POM();
        pom.setDependencies( originalSetOfDependencies );
        enhancedDependenciesManager.init( pom,
                                          callback );
    }

    @Test
    public void testUpload() throws Exception {
        enhancedDependenciesManager.update();

        verify( dependencyLoader ).load();
    }

    @Test
    public void testAdd() throws Exception {
        final Dependency dependency = makeDependency( "artifactId", "groupId", "1.0" );

        enhancedDependenciesManager.addNew( dependency );

        verify( dependencyLoader ).load();
        assertEquals( 1, originalSetOfDependencies.size() );
    }

    @Test
    public void testDelete() throws Exception {
        enhancedDependenciesManager.addNew( makeDependency( "artifactId", "groupId", "1.0" ) );

        enhancedDependenciesManager.delete( new NormalEnhancedDependency( makeDependency( "artifactId", "groupId", "1.0" ),
                                                                          new HashSet<String>() ) );

        assertTrue( originalSetOfDependencies.isEmpty() );
    }

    private Dependency makeDependency( final String artifactId,
                                       final String groupId,
                                       final String version ) {
        final Dependency dependency = new Dependency();
        dependency.setArtifactId( artifactId );
        dependency.setGroupId( groupId );
        dependency.setVersion( version );
        return dependency;
    }
}