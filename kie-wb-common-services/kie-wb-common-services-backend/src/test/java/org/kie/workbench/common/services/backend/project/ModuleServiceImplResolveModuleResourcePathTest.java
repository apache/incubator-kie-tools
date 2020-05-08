/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(WeldJUnitRunner.class)
public class ModuleServiceImplResolveModuleResourcePathTest
        extends ModuleTestBase {

    private KieModuleService moduleService;

    private Package pkg;

    private Path packageMainResourcesPath;

    private Path packageMainSrcPath;

    private List<String> resourceTypes = new ArrayList<>();

    @Before
    public void setUp() {
        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                    KieModuleService.class,
                                                                    cc);
        assertNotNull(moduleService);

        final Set<Bean<?>> beans = beanManager.getBeans(ResourceTypeDefinition.class);
        assertNotNull(beans);
        assertFalse(beans.isEmpty());
        resourceTypes = beans.stream()
                .map(bean -> ((ResourceTypeDefinition) beanManager.getReference(bean,
                                                                                bean.getBeanClass(),
                                                                                cc)).getSuffix())
                .collect(Collectors.toList());

        pkg = mock(Package.class);
        packageMainResourcesPath = mock(Path.class);
        packageMainSrcPath = mock(Path.class);
        when(pkg.getPackageMainResourcesPath()).thenReturn(packageMainResourcesPath);
        when(pkg.getPackageMainSrcPath()).thenReturn(packageMainSrcPath);
    }

    @Test
    public void testResolveInstanciableResourcesDefaultPath() {
        resourceTypes.forEach(resourceType -> {
            Path targetPath = moduleService.resolveDefaultPath(pkg,
                                                               resourceType);
            if ("java".equals(resourceType)) {
                //by now only java files are stored in the src location
                assertEquals(packageMainSrcPath,
                             targetPath);
            } else {
                //all other resources are stored in the resources location.
                assertEquals(packageMainResourcesPath,
                             targetPath);
            }
        });
    }

    @Test
    public void testResolveJavaResourceDefaultPath() {
        assertEquals(packageMainSrcPath,
                     moduleService.resolveDefaultPath(pkg,
                                                      "java"));
    }
}