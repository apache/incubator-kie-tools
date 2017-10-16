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

import java.net.URL;
import java.util.HashSet;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodel.backend.server.ProjectDataModelOracleTestUtils.*;

/**
 * Tests for DataModelService
 */
public class ProjectDataModelSuperTypesTest extends AbstractDataModelWeldTest {

    @Test
    public void testProjectSuperTypes() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans(DataModelService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(dataModelServiceBean);
        final DataModelService dataModelService = (DataModelService) beanManager.getReference(dataModelServiceBean,
                                                                                              DataModelService.class,
                                                                                              cc);

        final URL packageUrl = this.getClass().getResource("/DataModelBackendSuperTypesTest1/src/main/java/t2p1");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = paths.convert(nioPackagePath);

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel(packagePath);

        assertNotNull(oracle);

        assertEquals(5,
                     oracle.getProjectModelFields().size());
        assertContains("t2p1.Bean1",
                       oracle.getProjectModelFields().keySet());
        assertContains("t2p1.Bean2",
                       oracle.getProjectModelFields().keySet());
        assertContains("t2p2.Bean3",
                       oracle.getProjectModelFields().keySet());
        assertContains("t2p1.Bean4",
                       oracle.getProjectModelFields().keySet());
        assertContains("java.lang.String",
                       oracle.getProjectModelFields().keySet());

        assertContains("java.lang.Object",
                       new HashSet<String>(oracle.getProjectSuperTypes().get("t2p1.Bean1")));
        assertContains("t2p1.Bean1",
                       new HashSet<String>(oracle.getProjectSuperTypes().get("t2p1.Bean2")));
        assertContains("t2p1.Bean1",
                       new HashSet<String>(oracle.getProjectSuperTypes().get("t2p2.Bean3")));
        assertContains("t2p2.Bean3",
                       new HashSet<String>(oracle.getProjectSuperTypes().get("t2p1.Bean4")));
    }
}
