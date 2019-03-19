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

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.Annotation;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfHouse;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfMajorHouse;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodel.backend.server.ModuleDataModelOracleTestUtils.assertContains;

/**
 * Tests for Fact's annotations
 */
public class ModuleDataModelFactFieldsAnnotationsTest {

    @Test
    public void testModuleDMOZeroAnnotationAttributes() throws Exception {
        final ModuleDataModelOracleBuilder builder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator());
        final ModuleDataModelOracleImpl oracle = new ModuleDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder(builder,
                                                         Product.class,
                                                         false,
                                                         type -> TypeSource.JAVA_PROJECT);
        cb.build(oracle);

        assertEquals(1,
                     oracle.getModuleModelFields().size());
        assertContains("org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product",
                       oracle.getModuleModelFields().keySet());

        final Map<String, Set<Annotation>> fieldAnnotations = oracle.getModuleTypeFieldsAnnotations().get("org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product");
        assertNotNull(fieldAnnotations);
        assertEquals(0,
                     fieldAnnotations.size());
    }

    @Test
    public void testModuleDMOAnnotationAttributes() throws Exception {
        final ModuleDataModelOracleBuilder builder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator());
        final ModuleDataModelOracleImpl oracle = new ModuleDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder(builder,
                                                         SmurfHouse.class,
                                                         false,
                                                         type -> TypeSource.JAVA_PROJECT);
        cb.build(oracle);

        assertEquals(1,
                     oracle.getModuleModelFields().size());
        assertContains("org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfHouse",
                       oracle.getModuleModelFields().keySet());

        final Map<String, Set<Annotation>> fieldsAnnotations = oracle.getModuleTypeFieldsAnnotations().get("org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfHouse");
        assertNotNull(fieldsAnnotations);
        assertEquals(2,
                     fieldsAnnotations.size());

        testBaseAnnotations(fieldsAnnotations);
    }

    protected void testBaseAnnotations(final Map<String, Set<Annotation>> fieldsAnnotations) {
        assertTrue(fieldsAnnotations.containsKey("occupant"));
        final Set<Annotation> occupantAnnotations = fieldsAnnotations.get("occupant");
        assertNotNull(occupantAnnotations);
        assertEquals(1,
                     occupantAnnotations.size());

        final Annotation occupantAnnotation = occupantAnnotations.iterator().next();
        assertEquals("org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfFieldDescriptor",
                     occupantAnnotation.getQualifiedTypeName());
        assertEquals("blue",
                     occupantAnnotation.getParameters().get("colour"));
        assertEquals("M",
                     occupantAnnotation.getParameters().get("gender"));
        assertEquals("Brains",
                     occupantAnnotation.getParameters().get("description"));

        assertTrue(fieldsAnnotations.containsKey("positionedOccupant"));
        final Set<Annotation> posOccupantAnnotations = fieldsAnnotations.get("positionedOccupant");
        assertNotNull(posOccupantAnnotations);
        assertEquals(1,
                     posOccupantAnnotations.size());

        final Annotation annotation2 = posOccupantAnnotations.iterator().next();
        assertEquals("org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfFieldPositionDescriptor",
                     annotation2.getQualifiedTypeName());
        assertEquals(1,
                     annotation2.getParameters().get("value"));
    }

    @Test
    public void testModuleDMOInheritedAnnotationAttributes() throws Exception {
        final ModuleDataModelOracleBuilder builder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator());
        final ModuleDataModelOracleImpl oracle = new ModuleDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder(builder,
                                                         SmurfMajorHouse.class,
                                                         false,
                                                         type -> TypeSource.JAVA_PROJECT);
        cb.build(oracle);

        assertEquals(1,
                     oracle.getModuleModelFields().size());
        assertContains("org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfMajorHouse",
                       oracle.getModuleModelFields().keySet());

        final Map<String, Set<Annotation>> fieldsAnnotations = oracle.getModuleTypeFieldsAnnotations().get("org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfMajorHouse");
        assertNotNull(fieldsAnnotations);
        assertEquals(3,
                     fieldsAnnotations.size());

        assertTrue(fieldsAnnotations.containsKey("major"));
        final Set<Annotation> majorAnnotations = fieldsAnnotations.get("major");
        assertNotNull(majorAnnotations);
        assertEquals(1,
                     majorAnnotations.size());

        final Annotation majorAnnotation = majorAnnotations.iterator().next();
        assertEquals("org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfFieldDescriptor",
                     majorAnnotation.getQualifiedTypeName());
        assertEquals("red",
                     majorAnnotation.getParameters().get("colour"));
        assertEquals("M",
                     majorAnnotation.getParameters().get("gender"));
        assertEquals("Papa Smurf",
                     majorAnnotation.getParameters().get("description"));

        testBaseAnnotations(fieldsAnnotations);
    }
}
