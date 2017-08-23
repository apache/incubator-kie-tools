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

package org.kie.workbench.common.services.datamodel.backend.server;

import java.util.Set;

import org.appformer.project.datamodel.commons.oracle.ProjectDataModelOracleImpl;
import org.appformer.project.datamodel.oracle.Annotation;
import org.appformer.project.datamodel.oracle.TypeSource;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.JAXBSmurf;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.RoleSmurf;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.Smurf;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodel.backend.server.ProjectDataModelOracleTestUtils.*;

/**
 * Tests for Fact's annotations
 */
public class ProjectDataModelFactAnnotationsTest {

    @Test
    public void testProjectDMOZeroAnnotationAttributes() throws Exception {
        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( builder,
                                                          Product.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( oracle );

        assertEquals( 1,
                      oracle.getProjectModelFields().size() );
        assertContains( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product",
                        oracle.getProjectModelFields().keySet() );

        final Set<Annotation> annotations = oracle.getProjectTypeAnnotations().get( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product" );
        assertNotNull( annotations );
        assertEquals( 0,
                      annotations.size() );
    }

    @Test
    public void testProjectDMOAnnotationAttributes() throws Exception {
        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( builder,
                                                          Smurf.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( oracle );

        assertEquals( 1,
                      oracle.getProjectModelFields().size() );
        assertContains( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.Smurf",
                        oracle.getProjectModelFields().keySet() );

        final Set<Annotation> annotations = oracle.getProjectTypeAnnotations().get( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.Smurf" );
        assertNotNull( annotations );
        assertEquals( 1,
                      annotations.size() );

        final Annotation annotation = annotations.iterator().next();
        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfDescriptor",
                      annotation.getQualifiedTypeName() );
        assertEquals( "blue",
                      annotation.getParameters().get( "colour" ) );
        assertEquals( "M",
                      annotation.getParameters().get( "gender" ) );
        assertEquals( "Brains",
                      annotation.getParameters().get( "description" ) );
    }

    @Test
    public void testProjectDMOAnnotationAttributes2() throws Exception {
        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( builder,
                                                          RoleSmurf.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( oracle );

        assertEquals( 1,
                      oracle.getProjectModelFields().size() );
        assertContains( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.RoleSmurf",
                        oracle.getProjectModelFields().keySet() );

        final Set<Annotation> annotations = oracle.getProjectTypeAnnotations().get( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.RoleSmurf" );
        assertNotNull( annotations );
        assertEquals( 1,
                      annotations.size() );

        final Annotation annotation = annotations.iterator().next();
        assertEquals( "org.kie.api.definition.type.Role",
                      annotation.getQualifiedTypeName() );
        assertEquals( "EVENT",
                      annotation.getParameters().get( "value" ) );
    }

    @Test
    public void annotationsWithMemberOfTypeClass() throws Exception {
        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( builder,
                                                          JAXBSmurf.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( oracle );

        assertEquals( 1,
                      oracle.getProjectModelFields().size() );
        assertContains( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.JAXBSmurf",
                        oracle.getProjectModelFields().keySet() );

        final Set<Annotation> annotations = oracle.getProjectTypeAnnotations().get( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.JAXBSmurf" );
        assertNotNull( annotations );
        assertEquals( 1,
                      annotations.size() );

        final Annotation annotation = annotations.iterator().next();
        assertEquals( "javax.xml.bind.annotation.XmlType",
                      annotation.getQualifiedTypeName() );
        assertEquals( 5,
                      annotation.getParameters().size() );
        assertEquals( "smurf-namespace",
                      annotation.getParameters().get( "namespace" ) );
        assertEquals( "smurf-xsd",
                      annotation.getParameters().get( "name" ) );
        assertArraysEqual( new String[]{ "name", "colour" },
                           (String[]) annotation.getParameters().get( "propOrder" ) );
        assertTrue( annotation.getParameters().get( "factoryClass" ).toString().contains( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.JAXBSmurfFactory" ) );
    }

    private static void assertArraysEqual( final String[] expected,
                                           final String[] actual ) {
        assertEquals( expected.length,
                      actual.length );
        for ( int i = 0; i < expected.length; i++ ) {
            assertEquals( expected[ i ],
                          actual[ i ] );
        }
    }

}
