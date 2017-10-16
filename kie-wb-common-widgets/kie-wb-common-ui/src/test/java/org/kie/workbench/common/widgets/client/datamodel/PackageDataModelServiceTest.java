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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.guvnor.test.WeldJUnitRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.ModelField;
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
public class PackageDataModelServiceTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    @Inject
    private BeanManager beanManager;

    @Inject
    private Paths paths;

    @Inject
    private Instance<DynamicValidator> validatorInstance;

    @Before
    public void setUp() throws Exception {
        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }

    @Test
    public void testPackageDataModelOracle() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans(DataModelService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(dataModelServiceBean);
        final DataModelService dataModelService = (DataModelService) beanManager.getReference(dataModelServiceBean,
                                                                                              DataModelService.class,
                                                                                              cc);

        final URL packageUrl = this.getClass().getResource("/DataModelBackendTest1/src/main/java/t3p1");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = paths.convert(nioPackagePath);

        final PackageDataModelOracle packageLoader = dataModelService.getDataModel(packagePath);

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName("t3p1");
        dataModel.setModelFields(packageLoader.getProjectModelFields());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        assertNotNull(oracle);
        assertEquals(3,
                     oracle.getAllFactTypes().length);
        assertContains("t3p1.Bean1",
                       oracle.getAllFactTypes());
        assertContains("t3p2.Bean2",
                       oracle.getAllFactTypes());
        assertContains("java.lang.String",
                       oracle.getAllFactTypes());

        assertEquals(1,
                     oracle.getFactTypes().length);
        assertContains("Bean1",
                       oracle.getFactTypes());

        oracle.getFieldCompletions("Bean1",
                                   new Callback<ModelField[]>() {
                                       @Override
                                       public void callback(final ModelField[] fields) {
                                           assertEquals(3,
                                                        fields.length);
                                           assertContains("this",
                                                          fields);
                                           assertContains("field1",
                                                          fields);
                                           assertContains("field2",
                                                          fields);
                                       }
                                   });

        assertEquals(2,
                     oracle.getExternalFactTypes().length);
        assertContains("t3p2.Bean2",
                       oracle.getExternalFactTypes());
        assertContains("java.lang.String",
                       oracle.getExternalFactTypes());
    }

    @Test
    public void testProjectDataModelOracle() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans(DataModelService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(dataModelServiceBean);
        final DataModelService dataModelService = (DataModelService) beanManager.getReference(dataModelServiceBean,
                                                                                              DataModelService.class,
                                                                                              cc);

        final URL packageUrl = this.getClass().getResource("/DataModelBackendTest1/src/main/java/t3p1");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = paths.convert(nioPackagePath);

        final PackageDataModelOracle packageLoader = dataModelService.getDataModel(packagePath);
        final ProjectDataModelOracle projectLoader = dataModelService.getProjectDataModel(packagePath);

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getProjectModelFields());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        assertNotNull(oracle);

        assertEquals(3,
                     oracle.getAllFactTypes().length);
        assertContains("t3p1.Bean1",
                       oracle.getAllFactTypes());
        assertContains("t3p2.Bean2",
                       oracle.getAllFactTypes());
        assertContains("java.lang.String",
                       oracle.getAllFactTypes());

        oracle.getFieldCompletions("t3p1.Bean1",
                                   new Callback<ModelField[]>() {
                                       @Override
                                       public void callback(final ModelField[] fields) {
                                           assertEquals(3,
                                                        fields.length);
                                           assertContains("this",
                                                          fields);
                                           assertContains("field1",
                                                          fields);
                                           assertContains("field2",
                                                          fields);
                                       }
                                   });

        oracle.getFieldCompletions("t3p2.Bean2",
                                   new Callback<ModelField[]>() {
                                       @Override
                                       public void callback(final ModelField[] fields) {
                                           assertEquals(2,
                                                        fields.length);
                                           assertContains("this",
                                                          fields);
                                           assertContains("field1",
                                                          fields);
                                       }
                                   });
    }
}

