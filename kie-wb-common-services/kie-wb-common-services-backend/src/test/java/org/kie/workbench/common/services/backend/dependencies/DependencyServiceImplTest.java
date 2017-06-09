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
package org.kie.workbench.common.services.backend.dependencies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.appformer.maven.integration.DependencyDescriptor;
import org.appformer.maven.integration.MavenRepository;
import org.eclipse.aether.artifact.Artifact;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.test.TempFiles;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.ReleaseId;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DependencyServiceImplTest {

    @Mock
    MavenRepository mavenRepository;

    private TempFiles tempFiles;

    private DependencyServiceImpl service;

    @Before
    public void setUp() throws Exception {

        tempFiles = new TempFiles();

        when( mavenRepository.getArtifactDependecies( anyString() ) ).thenAnswer(
                new Answer<Collection<DependencyDescriptor>>() {
                    @Override
                    public Collection<DependencyDescriptor> answer( final InvocationOnMock invocationOnMock )
                            throws Throwable {

                        String gavString = ( String ) invocationOnMock.getArguments()[0];

                        Collection<DependencyDescriptor> result = new ArrayList<DependencyDescriptor>();
                        if ( gavString.equals( "junit:junit:4.11" ) ) {
                            result.add( makeDependencyDescriptor( "org.hamcrest", "hamcrest-core", "1.3" ) );
                        } else if ( gavString.equals( "org.guvnor:guvnor-web-app:5.0" ) ) {
                            result.add( makeDependencyDescriptor( "org.drools", "drools-core", "5.0" ) );
                        }
                        return result;
                    }
                } );

        getJarWriter( "junit:junit:4.11" )
                .addFile( "org/junit/rules/SomeClass.class" )
                .addFile( "org/junit/matchers/SomeClass.class" )
                .addFile( "org/junit/doNotAddMe/SomeClass.txt" )
                .close();

        getJarWriter( "org.hamcrest:hamcrest-core:1.3" )
                .addFile( "org/hamcrest/SomeClass.class" )
                .addFile( "org/hamcrest/core/SomeClass.class" )
                .close();

        service = new DependencyServiceImpl() {
            @Override protected MavenRepository getMavenRepository() {
                return mavenRepository;
            }
        };
    }

    private TestJarWriter getJarWriter( final String artifactName ) throws IOException {
        final Artifact artifact = mock( Artifact.class );
        final File file = tempFiles.createTempFile( artifactName + ".jar" );

        when( artifact.getFile() ).thenReturn( file );
        when( this.mavenRepository.resolveArtifact( artifactName ) ).thenReturn( artifact );

        return new TestJarWriter( file,
                                  tempFiles );
    }

    @After
    public void tearDown() throws Exception {
        tempFiles.deleteFiles();
    }

    @Test
    public void testNoDependencies() throws Exception {

        GAV gav = new GAV( "artifactID",
                           "groupID",
                           "version" );

        Collection<Dependency> dependencies = service.loadDependencies( gav );

        assertTrue( dependencies.isEmpty() );
    }

    @Test
    public void testDependencies() throws Exception {
        Collection<Dependency> dependencies = service.loadDependencies( new GAV( "junit",
                                                                                 "junit",
                                                                                 "4.11" ) );

        assertEquals( 1, dependencies.size() );
        Dependency dependency = dependencies.iterator().next();
        assertEquals( "org.hamcrest", dependency.getGroupId() );
        assertEquals( "hamcrest-core", dependency.getArtifactId() );
        assertEquals( "1.3", dependency.getVersion() );
    }

    @Test
    public void testDependenciesForGAVs() throws Exception {

        final Collection<GAV> gavs = new ArrayList<GAV>();
        gavs.add( new GAV( "junit",
                           "junit",
                           "4.11" ) );
        gavs.add( new GAV( "org.guvnor",
                           "guvnor-web-app",
                           "5.0" ) );
        Collection<Dependency> dependencies = service.loadDependencies( gavs );

        assertEquals( 2, dependencies.size() );

        final Iterator<Dependency> iterator = dependencies.iterator();

        Dependency dependency = iterator.next();
        assertEquals( "org.hamcrest", dependency.getGroupId() );
        assertEquals( "hamcrest-core", dependency.getArtifactId() );
        assertEquals( "1.3", dependency.getVersion() );

        dependency = iterator.next();
        assertEquals( "org.drools", dependency.getGroupId() );
        assertEquals( "drools-core", dependency.getArtifactId() );
        assertEquals( "5.0", dependency.getVersion() );
    }

    @Test
    public void testListPackages() throws Exception {

        Set<String> junitPackages = service.loadPackageNames( new GAV( "junit",
                                                                       "junit",
                                                                       "4.11" ) );
        Set<String> hamcrestPackages = service.loadPackageNames( new GAV( "org.hamcrest",
                                                                          "hamcrest-core",
                                                                          "1.3" ) );

        assertTrue( junitPackages.contains( "org.junit.rules" ) );
        assertTrue( junitPackages.contains( "org.junit.matchers" ) );

        assertFalse( junitPackages.contains( "org.hamcrest" ) );
        assertFalse( junitPackages.contains( "org.hamcrest.core" ) );

        assertTrue( hamcrestPackages.contains( "org.hamcrest" ) );
        assertTrue( hamcrestPackages.contains( "org.hamcrest.core" ) );

        assertFalse( hamcrestPackages.contains( "org.junit.rules" ) );
        assertFalse( hamcrestPackages.contains( "org.junit.matchers" ) );

    }

    @Test
    public void testFillDependenciesWithPackageNames() throws Exception {
        final Set<String> packageNames = service.loadPackageNames( new GAV( "junit",
                                                                            "junit",
                                                                            "4.11" ) );

        assertEquals( 2, packageNames.size() );
        assertTrue( packageNames.contains( "org.junit.rules" ) );
        assertTrue( packageNames.contains( "org.junit.matchers" ) );

    }

    private DependencyDescriptor makeDependencyDescriptor( final String groupId,
                                                           final String artifactId,
                                                           final String version ) {
        return new DependencyDescriptor( new ReleaseId() {
            @Override public String getGroupId() {
                return groupId;
            }

            @Override public String getArtifactId() {
                return artifactId;
            }

            @Override public String getVersion() {
                return version;
            }

            @Override public String toExternalForm() {
                return null;
            }

            @Override public boolean isSnapshot() {
                return false;
            }
        } );
    }
}