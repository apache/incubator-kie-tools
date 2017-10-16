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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.mockito.Mock;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for DataModelService
 */
public class PackageDataModelDeclaredTypesTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private BeanManager beanManager;
    private Paths paths;

    @Mock
    private Instance<DynamicValidator> validatorInstance;

    @Before
    public void setUp() throws Exception {
        //Bootstrap WELD container
        StartMain startMain = new StartMain(new String[0]);
        beanManager = startMain.go().getBeanManager();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = (Bean) beanManager.getBeans(Paths.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(pathsBean);
        paths = (Paths) beanManager.getReference(pathsBean,
                                                 Paths.class,
                                                 cc);

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }

    @Test
    public void testPackageDeclaredTypes() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans(DataModelService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(dataModelServiceBean);
        final DataModelService dataModelService = (DataModelService) beanManager.getReference(dataModelServiceBean,
                                                                                              DataModelService.class,
                                                                                              cc);

        final URL packageUrl = this.getClass().getResource("/DataModelBackendDeclaredTypesTest1/src/main/java/t1p1");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = paths.convert(nioPackagePath);

        final PackageDataModelOracle packageLoader = dataModelService.getDataModel(packagePath);

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName("t1p1");
        dataModel.setModelFields(packageLoader.getProjectModelFields());
        dataModel.setTypeSources(new HashMap<String, TypeSource>() {
            {
                put("t1p1.Bean1", TypeSource.JAVA_PROJECT);
                put("t1p1.DRLBean", TypeSource.DECLARED);
                put("t1p2.Bean2", TypeSource.JAVA_PROJECT);
                put("java.lang.String", TypeSource.JAVA_PROJECT);
                put("int", TypeSource.JAVA_PROJECT);
            }
        });
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        assertNotNull(dataModel);

        assertEquals(2,
                     oracle.getFactTypes().length);
        PackageDataModelOracleTestUtils.assertContains("Bean1",
                                                       oracle.getFactTypes());
        PackageDataModelOracleTestUtils.assertContains("DRLBean",
                                                       oracle.getFactTypes());

        assertEquals(2,
                     oracle.getExternalFactTypes().length);
        PackageDataModelOracleTestUtils.assertContains("t1p2.Bean2",
                                                       oracle.getExternalFactTypes());
        PackageDataModelOracleTestUtils.assertContains("java.lang.String",
                                                       oracle.getExternalFactTypes());

        oracle.getTypeSource("Bean1",
                             new Callback<TypeSource>() {
                                 @Override
                                 public void callback(final TypeSource result) {
                                     assertEquals(TypeSource.JAVA_PROJECT,
                                                  result);
                                 }
                             });
        oracle.getTypeSource("DRLBean",
                             new Callback<TypeSource>() {
                                 @Override
                                 public void callback(final TypeSource result) {
                                     assertEquals(TypeSource.DECLARED,
                                                  result);
                                 }
                             });
    }
}

