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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.eclipse.aether.artifact.Artifact;
import org.guvnor.common.services.backend.archive.ZipWriter;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.DependencyDescriptor;
import org.kie.scanner.MavenRepository;
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

    private File junit;
    private File hamcrest;

    @Before
    public void setUp() throws Exception {

        tempFiles = new TempFiles();

        when( mavenRepository.getArtifactDependecies( anyString() ) ).thenAnswer(
                new Answer<Collection<DependencyDescriptor>>() {
                    @Override
                    public Collection<DependencyDescriptor> answer( final InvocationOnMock invocationOnMock )
                            throws Throwable {

                        String gavString = (String) invocationOnMock.getArguments()[0];

                        Collection<DependencyDescriptor> result = new ArrayList<DependencyDescriptor>();
                        if ( gavString.equals( "junit:junit:4.11" ) ) {
                            result.add( makeDependencyDescriptor( "org.hamcrest", "hamcrest-core", "1.3" ) );
                        }
                        return result;
                    }
                } );

        makeFiles();

        TestJarWriter junitWriter = new TestJarWriter( junit, "junit:junit:4.11" );
        junitWriter.addFile( "org/junit/rules/SomeClass", ".class" );
        junitWriter.addFile( "org/junit/matchers/SomeClass", ".class" );
        junitWriter.addFile( "org/junit/doNotAddMe/SomeClass", ".txt" );
        junitWriter.close();

        TestJarWriter hamcrestWriter = new TestJarWriter( hamcrest, "org.hamcrest:hamcrest-core:1.3" );
        hamcrestWriter.addFile( "org/hamcrest/SomeClass", ".class" );
        hamcrestWriter.addFile( "org/hamcrest/core/SomeClass", ".class" );
        hamcrestWriter.close();

        service = new DependencyServiceImpl() {
            @Override protected MavenRepository getMavenRepository() {
                return mavenRepository;
            }
        };
    }

    private void makeFiles() throws IOException {
        junit = tempFiles.createTempFile( "junit.jar" );
        this.hamcrest = tempFiles.createTempFile( "hamcrest.jar" );
    }

    @After
    public void tearDown() throws Exception {
        tempFiles.deleteFiles();
    }

    class TestJarWriter {

        Artifact artifact = mock( Artifact.class );
        ZipWriter zipWriter;

        public TestJarWriter( final File file,
                              final String artifactName ) throws IOException {
            zipWriter = new ZipWriter( new FileOutputStream( file ) );
            when( artifact.getFile() ).thenReturn( file );
            when( mavenRepository.resolveArtifact( artifactName ) ).thenReturn( artifact );
        }

        public void addFile( final String prefix,
                             final String suffix ) throws IOException {
            File tempFile = tempFiles.createTempFile( prefix + suffix );
            zipWriter.addFile( new ZipEntry( prefix + suffix ),
                               new FileInputStream( tempFile ) );
        }

        public void close() throws IOException {
            zipWriter.close();
        }
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
        GAV gav = new GAV( "junit",
                           "junit",
                           "4.11" );
        POM pom = new POM( gav );
        pom.getDependencies().add( new Dependency() );

        Collection<Dependency> dependencies = service.loadDependencies( gav );

        assertEquals( 1, dependencies.size() );
        Dependency dependency = dependencies.iterator().next();
        assertEquals( "org.hamcrest", dependency.getGroupId() );
        assertEquals( "hamcrest-core", dependency.getArtifactId() );
        assertEquals( "1.3", dependency.getVersion() );
    }

    @Test
    public void testListPackages() throws Exception {

        Set<String> junitPackages = service.loadPackageNamesForDependency( new GAV( "junit",
                                                                                    "junit",
                                                                                    "4.11" ) );
        Set<String> hamcrestPackages = service.loadPackageNamesForDependency( new GAV( "org.hamcrest",
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