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

package org.kie.workbench.common.screens.datamodeller.backend.server.indexing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaSourceVisitorTest {

    private static JavaClassSource javaClassSource;

    private static JavaClassSource javaClassSource2;

    private static JavaInterfaceSource javaInterfaceSource;

    private static JavaEnumSource javaEnumSource;

    private JavaSourceVisitor visitor;

    private Resource resourceParts;

    @BeforeClass
    public static void setUpJavaSources() throws URISyntaxException, IOException {

        javaClassSource = (JavaClassSource) Roaster.parse( JavaSourceVisitorTest.class.getResource( "/org/kie/workbench/common/screens/datamodeller/backend/server/indexing/Pojo2.java" ) );
        javaClassSource2 = (JavaClassSource) Roaster.parse(JavaSourceVisitorTest.class.getResource("/org/kie/workbench/common/screens/datamodeller/backend/server/indexing/Pojo3.java"));
        javaInterfaceSource = (JavaInterfaceSource) Roaster.parse( JavaSourceVisitorTest.class.getResource( "/org/kie/workbench/common/screens/datamodeller/backend/server/indexing/Interface2.java" ) );
        javaEnumSource = (JavaEnumSource) Roaster.parse( JavaSourceVisitorTest.class.getResource( "/org/kie/workbench/common/screens/datamodeller/backend/server/indexing/Enum2.java" ) );
    }

    @Before
    public void setUp() {
        this.resourceParts = new Resource( "org.kie.workbench.common.screens.datamodeller.backend.server.indexing.Pojo2.java", ResourceType.JAVA );
        this.visitor = new JavaSourceVisitor( javaClassSource, JavaSourceVisitorTest.class.getClassLoader(), resourceParts );
    }

    @Test
    public void visitAnnotationSources() {
        for ( AnnotationSource annotationSource : javaClassSource.getAnnotations() ) {
            visitor.visit( annotationSource );
        }

        checkVisitor( Arrays.asList( "ref:java => org.kie.workbench.common.screens.datamodeller.backend.server.indexing.Annotation1" ) );
    }

    @Test
    public void visitImports() {
        for ( Import importSource : javaClassSource.getImports() ) {
            visitor.visit( importSource );
        }

        checkVisitor( Arrays.asList( "ref:java => java.math.BigDecimal", "ref:java => java.io.Serializable" ) );
    }

    @Test
    public void visitFields() {
        for ( FieldSource fieldSource : javaClassSource.getFields() ) {
            visitor.visit( fieldSource );
        }

        checkVisitor( Arrays.asList( "ref:java => int", "ref:java => java.math.BigDecimal" ) );
    }

    @Test
    public void visitMethods() {
        for ( MethodSource methodSource : javaClassSource.getMethods() ) {
            visitor.visit( methodSource );
        }

        checkVisitor( Arrays.asList( "ref:java => int", "ref:java => java.math.BigDecimal", "ref:java => org.kie.workbench.common.screens.datamodeller.backend.server.indexing.Pojo1" ) );
    }

    @Test
    public void visitInterface() {
        visitor.visit( javaInterfaceSource );

        checkVisitor( Arrays.asList( "ref:java => int", "ref:java => java.lang.String", "ref:java => org.kie.workbench.common.screens.datamodeller.backend.server.indexing.Annotation1" ) );
    }

    @Test
    public void visitEnum() {
        visitor.visit( javaEnumSource );

        checkVisitor( Arrays.asList( "ref:java => int" ) );
    }

    @Test
    public void visitJavaClassImplmentingInterfaceWithGenerics() {
        visitor.visit(javaClassSource2);

        checkVisitor(Arrays.asList("ref:java => org.kie.workbench.common.screens.datamodeller.backend.server.indexing.Pojo3", "ref:java => java.lang.Object", "ref:java => java.lang.Comparable", "ref:java => int"));
    }

    private void checkVisitor( Collection<String> referenceDefinitions ) {
        Collection<ResourceReference> resourceReferences = visitor.getResourceReferences();

        assertEquals(referenceDefinitions.size(), resourceReferences.size());

        List<String> resourceReferenceList = resourceReferences.stream().map( resourceReference -> resourceReference.toString() ).collect( Collectors.toList() );
        assertTrue( resourceReferenceList.containsAll( referenceDefinitions ) );
    }

}
