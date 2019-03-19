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

package org.kie.workbench.common.widgets.client.datamodel;

import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.Instance;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.Annotation;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.Product;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations.SmurfHouse;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Fact's annotations
 */
public class PackageDataModelFactFieldsAnnotationsTest {

    @Mock
    private Instance<DynamicValidator> validatorInstance;

    @Test
    public void testCorrectPackageDMOZeroAnnotationAttributes() throws Exception {
        //Build ModuleDMO
        final ModuleDataModelOracleBuilder moduleBuilder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator());
        final ModuleDataModelOracleImpl moduleLoader = new ModuleDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder(moduleBuilder,
                                                         Product.class,
                                                         false,
                                                         type -> TypeSource.JAVA_PROJECT);
        cb.build(moduleLoader);

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator(),
                                                                                                                   "org.kie.workbench.common.widgets.client.datamodel.testclasses");
        packageBuilder.setModuleOracle(moduleLoader);
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName(packageLoader.getPackageName());
        dataModel.setModelFields(packageLoader.getModuleModelFields());
        dataModel.setTypeAnnotations(packageLoader.getModuleTypeAnnotations());
        dataModel.setTypeFieldsAnnotations(packageLoader.getModuleTypeFieldsAnnotations());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        assertEquals(1,
                     oracle.getFactTypes().length);
        assertEquals("Product",
                     oracle.getFactTypes()[0]);

        oracle.getTypeFieldsAnnotations("Product",
                                        new Callback<Map<String, Set<Annotation>>>() {
                                            @Override
                                            public void callback(final Map<String, Set<Annotation>> fieldsAnnotations) {
                                                assertNotNull(fieldsAnnotations);
                                                assertEquals(0,
                                                             fieldsAnnotations.size());
                                            }
                                        });
    }

    @Test
    public void testCorrectPackageDMOAnnotationAttributes() throws Exception {
        //Build ModuleDMO
        final ModuleDataModelOracleBuilder moduleBuilder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator());
        final ModuleDataModelOracleImpl moduleLoader = new ModuleDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder(moduleBuilder,
                                                         SmurfHouse.class,
                                                         false,
                                                         type -> TypeSource.JAVA_PROJECT);
        cb.build(moduleLoader);

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator(),
                                                                                                                   "org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations");
        packageBuilder.setModuleOracle(moduleLoader);
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName(packageLoader.getPackageName());
        dataModel.setModelFields(packageLoader.getModuleModelFields());
        dataModel.setTypeAnnotations(packageLoader.getModuleTypeAnnotations());
        dataModel.setTypeFieldsAnnotations(packageLoader.getModuleTypeFieldsAnnotations());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        assertEquals(1,
                     oracle.getFactTypes().length);
        assertEquals("SmurfHouse",
                     oracle.getFactTypes()[0]);

        oracle.getTypeFieldsAnnotations("SmurfHouse",
                                        new Callback<Map<String, Set<Annotation>>>() {
                                            @Override
                                            public void callback(final Map<String, Set<Annotation>> fieldsAnnotations) {
                                                assertNotNull(fieldsAnnotations);
                                                assertEquals(2,
                                                             fieldsAnnotations.size());

                                                assertTrue(fieldsAnnotations.containsKey("occupant"));
                                                final Set<Annotation> occupantAnnotations = fieldsAnnotations.get("occupant");
                                                assertNotNull(occupantAnnotations);
                                                assertEquals(1,
                                                             occupantAnnotations.size());
                                                final Annotation annotation = occupantAnnotations.iterator().next();
                                                assertEquals("org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations.SmurfFieldDescriptor",
                                                             annotation.getQualifiedTypeName());
                                                assertEquals("blue",
                                                             annotation.getParameters().get("colour"));
                                                assertEquals("M",
                                                             annotation.getParameters().get("gender"));
                                                assertEquals("Brains",
                                                             annotation.getParameters().get("description"));

                                                assertTrue(fieldsAnnotations.containsKey("positionedOccupant"));
                                                final Set<Annotation> posOccupantAnnotations = fieldsAnnotations.get("positionedOccupant");
                                                assertNotNull(posOccupantAnnotations);
                                                assertEquals(1,
                                                             posOccupantAnnotations.size());

                                                final Annotation annotation2 = posOccupantAnnotations.iterator().next();
                                                assertEquals("org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations.SmurfFieldPositionDescriptor",
                                                             annotation2.getQualifiedTypeName());
                                                assertEquals(1,
                                                             annotation2.getParameters().get("value"));
                                            }
                                        });
    }

    @Test
    public void testIncorrectPackageDMOZeroAnnotationAttributes() throws Exception {
        //Build ModuleDMO
        final ModuleDataModelOracleBuilder moduleBuilder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator());
        final ModuleDataModelOracleImpl moduleLoader = new ModuleDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder(moduleBuilder,
                                                         Product.class,
                                                         false,
                                                         type -> TypeSource.JAVA_PROJECT);
        cb.build(moduleLoader);

        //Build PackageDMO. Defaults to defaultpkg
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator());
        packageBuilder.setModuleOracle(moduleLoader);
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName(packageLoader.getPackageName());
        dataModel.setModelFields(packageLoader.getModuleModelFields());
        dataModel.setTypeAnnotations(packageLoader.getModuleTypeAnnotations());
        dataModel.setTypeFieldsAnnotations(packageLoader.getModuleTypeFieldsAnnotations());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        assertEquals(0,
                     oracle.getFactTypes().length);

        oracle.getTypeFieldsAnnotations("Product",
                                        new Callback<Map<String, Set<Annotation>>>() {
                                            @Override
                                            public void callback(final Map<String, Set<Annotation>> fieldsAnnotations) {
                                                assertNotNull(fieldsAnnotations);
                                                assertEquals(0,
                                                             fieldsAnnotations.size());
                                            }
                                        });
    }

    @Test
    public void testIncorrectPackageDMOAnnotationAttributes() throws Exception {
        //Build ModuleDMO
        final ModuleDataModelOracleBuilder moduleBuilder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator());
        final ModuleDataModelOracleImpl moduleLoader = new ModuleDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder(moduleBuilder,
                                                         SmurfHouse.class,
                                                         false,
                                                         type -> TypeSource.JAVA_PROJECT);
        cb.build(moduleLoader);

        //Build PackageDMO. Defaults to defaultpkg
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator());
        packageBuilder.setModuleOracle(moduleLoader);
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName(packageLoader.getPackageName());
        dataModel.setModelFields(packageLoader.getModuleModelFields());
        dataModel.setTypeAnnotations(packageLoader.getModuleTypeAnnotations());
        dataModel.setTypeFieldsAnnotations(packageLoader.getModuleTypeFieldsAnnotations());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        assertEquals(0,
                     oracle.getFactTypes().length);

        oracle.getTypeFieldsAnnotations("SmurfHouse",
                                        new Callback<Map<String, Set<Annotation>>>() {
                                            @Override
                                            public void callback(final Map<String, Set<Annotation>> fieldAnnotations) {
                                                assertNotNull(fieldAnnotations);
                                                assertEquals(0,
                                                             fieldAnnotations.size());
                                            }
                                        });
    }
}
