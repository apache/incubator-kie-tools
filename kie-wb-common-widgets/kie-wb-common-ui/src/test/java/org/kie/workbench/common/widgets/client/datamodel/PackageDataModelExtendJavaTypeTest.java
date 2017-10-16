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

import java.net.URL;
import java.util.HashMap;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.guvnor.test.WeldJUnitRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;
import static org.kie.workbench.common.widgets.client.datamodel.PackageDataModelOracleTestUtils.*;
import static org.mockito.Mockito.*;

@RunWith(WeldJUnitRunner.class)
public class PackageDataModelExtendJavaTypeTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    @Inject
    private BeanManager beanManager;

    @Inject
    private Paths paths;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private Instance<DynamicValidator> validatorInstance;

    @Before
    public void setUp() throws Exception {
        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }

    @Test
    public void testPackageExtendJavaTypeWithQualifiedDRLBeanName() throws Exception {
        final URL packageUrl = this.getClass().getResource("/DataModelBackendExtendJavaTypeTest1/src/main/java/t4p1");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = paths.convert(nioPackagePath);

        final PackageDataModelOracle projectLoader = dataModelService.getDataModel(packagePath);

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(projectLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName("t4p1");
        dataModel.setModelFields(projectLoader.getProjectModelFields());
        dataModel.setEventTypes(new HashMap<String, Boolean>() {
            {
                put("t4p1.Bean1", true);
            }
        });
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        assertNotNull(oracle);

        assertEquals(1,
                     oracle.getFactTypes().length);
        assertContains("Bean1",
                       oracle.getFactTypes());

        oracle.isFactTypeAnEvent("Bean1",
                                 new Callback<Boolean>() {
                                     @Override
                                     public void callback(final Boolean result) {
                                         assertTrue(result);
                                     }
                                 });
    }

    @Test
    public void testProjectExtendJavaTypeWithQualifiedDRLBeanName() throws Exception {
        final URL packageUrl = this.getClass().getResource("/DataModelBackendExtendJavaTypeTest1/src/main/java/t4p1");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = paths.convert(nioPackagePath);

        final PackageDataModelOracle packageLoader = dataModelService.getDataModel(packagePath);
        final ProjectDataModelOracle projectLoader = dataModelService.getProjectDataModel(packagePath);

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName("t4p1");
        dataModel.setModelFields(projectLoader.getProjectModelFields());
        dataModel.setEventTypes(new HashMap<String, Boolean>() {
            {
                put("t4p1.Bean1", true);
            }
        });
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        assertNotNull(oracle);

        assertEquals(1,
                     oracle.getFactTypes().length);
        assertContains("Bean1",
                       oracle.getFactTypes());

        oracle.isFactTypeAnEvent("Bean1",
                                 new Callback<Boolean>() {
                                     @Override
                                     public void callback(final Boolean result) {
                                         assertTrue(result);
                                     }
                                 });
    }

    @Test
    public void testPackageExtendJavaTypeWithImport() throws Exception {
        final URL packageUrl = this.getClass().getResource("/DataModelBackendExtendJavaTypeTest2/src/main/java/t5p1");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = paths.convert(nioPackagePath);

        final PackageDataModelOracle projectLoader = dataModelService.getDataModel(packagePath);

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(projectLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName("t5p1");
        dataModel.setModelFields(projectLoader.getProjectModelFields());
        dataModel.setEventTypes(new HashMap<String, Boolean>() {
            {
                put("t5p1.Bean1", true);
            }
        });
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);
        assertNotNull(oracle);

        assertEquals(1,
                     oracle.getFactTypes().length);
        assertContains("Bean1",
                       oracle.getFactTypes());

        oracle.isFactTypeAnEvent("Bean1",
                                 new Callback<Boolean>() {
                                     @Override
                                     public void callback(final Boolean result) {
                                         assertTrue(result);
                                     }
                                 });
    }

    @Test
    public void testProjectExtendJavaTypeWithImport() throws Exception {
        final URL packageUrl = this.getClass().getResource("/DataModelBackendExtendJavaTypeTest2/src/main/java/t5p1");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = paths.convert(nioPackagePath);

        final PackageDataModelOracle packageLoader = dataModelService.getDataModel(packagePath);
        final ProjectDataModelOracle projectLoader = dataModelService.getProjectDataModel(packagePath);

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName("t5p1");
        dataModel.setModelFields(projectLoader.getProjectModelFields());
        dataModel.setEventTypes(new HashMap<String, Boolean>() {
            {
                put("t5p1.Bean1", true);
            }
        });
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);
        assertNotNull(oracle);

        assertEquals(1,
                     oracle.getFactTypes().length);
        assertContains("Bean1",
                       oracle.getFactTypes());

        oracle.isFactTypeAnEvent("Bean1",
                                 new Callback<Boolean>() {
                                     @Override
                                     public void callback(final Boolean result) {
                                         assertTrue(result);
                                     }
                                 });
    }
}

