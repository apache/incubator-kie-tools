/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.kie.workbench.common.services.backend.whitelist;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.builder.core.NoBuilderFoundException;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PackageNameSearchProviderTest {

    @Mock
    private DependencyService dependencyService;

    private PackageNameSearchProvider packageNameSearchProvider;

    @Before
    public void setUp() throws Exception {
        packageNameSearchProvider = new PackageNameSearchProvider( dependencyService );
    }

    private HashMap<Dependency, Set<String>> setUpDependencyService( POM pom ) throws NoBuilderFoundException {
        final HashMap<Dependency, Set<String>> map = new HashMap<Dependency, Set<String>>();

        when( dependencyService.loadDependencies( pom.getGav() ) ).thenAnswer( new Answer<Collection>() {
            @Override public Collection answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return map.keySet();
            }
        } );

        when( dependencyService.loadPackageNames( any( GAV.class ) ) ).thenAnswer( new Answer<Collection>() {
            @Override public Collection answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return map.get( invocationOnMock.getArguments()[0] );
            }
        } );

        return map;
    }

    @Test
    public void testLoadTopLevelDependencies() throws Exception {
        POM pom = new POM( new GAV( "artifactID",
                                    "groupID",
                                    "version" ) );
        pom.getDependencies().add( getDependency( "drools-core", "org.drools", "6.3.0" ) );
        pom.getDependencies().add( getDependency( "junit", "org.junit", "4.11" ) );

        HashMap<Dependency, Set<String>> map = setUpDependencyService( pom );

        map.put( getGAV( "drools-core", "org.drools", "6.3.0" ), toSet( "org.drools.a",
                                                                        "org.drools.b",
                                                                        "org.drools.c" ) );
        map.put( getGAV( "junit", "org.junit", "4.11" ), toSet( "junit.a",
                                                                "junit.b" ) );

        Set<String> packageNames = packageNameSearchProvider.newTopLevelPackageNamesSearch( pom ).search();

        assertEquals( 5, packageNames.size() );
        assertTrue( packageNames.contains( "org.drools.a" ) );
        assertTrue( packageNames.contains( "org.drools.b" ) );
        assertTrue( packageNames.contains( "org.drools.c" ) );
        assertTrue( packageNames.contains( "junit.a" ) );
        assertTrue( packageNames.contains( "junit.b" ) );
    }

    @Test
    public void testLoadTopLevelDependenciesWhenIncompleteDependenciesInPOM() throws Exception {
        POM pom = new POM( new GAV( "artifactID",
                                    "groupID",
                                    "version" ) );
        pom.getDependencies().add( getDependency( "drools-core", "org.drools", null ) );
        pom.getDependencies().add( getDependency( null, null, null ) );

        HashMap<Dependency, Set<String>> map = setUpDependencyService( pom );
        map.put( getGAV( "drools-core", "org.drools", "6.3.0" ), toSet( "org.drools.a",
                                                                        "org.drools.b",
                                                                        "org.drools.c" ) );

        Set<String> packageNames = packageNameSearchProvider.newTopLevelPackageNamesSearch( pom ).search();

        assertEquals( 3, packageNames.size() );
        assertTrue( packageNames.contains( "org.drools.a" ) );
        assertTrue( packageNames.contains( "org.drools.b" ) );
        assertTrue( packageNames.contains( "org.drools.c" ) );
    }

    private HashSet<String> toSet( String... items ) {
        return new HashSet<String>( Arrays.asList( items ) );
    }

    private Dependency getGAV( final String artifactID,
                               final String groupID,
                               final String version ) {
        return new Dependency( new GAV( groupID,
                                        artifactID,
                                        version ) );
    }
    
    private Dependency getDependency( final String artifactID, final String groupID, final String version ) {
        return new Dependency( new GAV( groupID, artifactID, version ) );
    }

}