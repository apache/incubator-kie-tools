/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.refactoring.backend.server.query.assetUsages;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllChangeImpactQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourcePartReferencesQuery;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetsUsageServiceImplTest extends BaseIndexingTest<TestJavaResourceTypeDefinition> {

    public static final String REFERENCED = "org.kie.workbench.common.services.refactoring.backend.server.query.assetUsages.Referenced";
    private static final String WHEEL = "org.kie.workbench.common.services.refactoring.backend.server.query.assetUsages.Wheel";
    private static final String CAR = "org.kie.workbench.common.services.refactoring.backend.server.query.assetUsages.Car";
    private static final String BIKE = "org.kie.workbench.common.services.refactoring.backend.server.query.assetUsages.Bike";
    private static final String BIKE_FRONT = "front";
    private static final String BIKE_BACK = "back";

    private static final int WHEEL_USAGES = 2;
    private static final int BIKE_USAGES = 1;
    private static final int OTHER_USAGES = 0;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private Package aPackage;

    private Path path;

    @Mock
    private KieModule project;

    private AssetsUsageServiceImpl assetsUsageService;

    @Before
    public void init() throws IOException, InterruptedException {
        path = Paths.convert(basePath);
        when(moduleService.resolveModule(any(Path.class))).thenReturn(project);
        when(project.getRootPath()).thenReturn(path);
        when(moduleService.resolvePackage(any())).thenReturn(aPackage);

        assetsUsageService = new AssetsUsageServiceImpl(moduleService,
                                                        service);

        indexResource("Wheel.java");
        indexResource("Bike.java");
        indexResource("Car.java");
    }

    protected void indexResource(String resource) throws IOException, InterruptedException {
        final org.uberfire.java.nio.file.Path path = basePath.resolve(resource);
        final String content = loadText(resource);
        ioService().write(path,
                          content);
        Thread.sleep(5000);
    }

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add(new FindResourcePartReferencesQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder(ioService());
                }
            });
            add(new FindAllChangeImpactQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder(ioService());
                }
            });
        }};
    }

    @Test
    public void testFindUsages() {
        // Testing asset usages search
        List<Path> usages = assetsUsageService.getAssetUsages(WHEEL,
                                                              ResourceType.JAVA,
                                                              path);

        assertNotNull(usages);
        assertEquals(WHEEL_USAGES,
                     usages.size());

        usages = assetsUsageService.getAssetUsages(CAR,
                                                   ResourceType.JAVA,
                                                   path);

        assertNotNull(usages);
        assertEquals(OTHER_USAGES,
                     usages.size());

        usages = assetsUsageService.getAssetUsages(BIKE,
                                                   ResourceType.JAVA,
                                                   path);

        assertNotNull(usages);
        assertEquals(BIKE_USAGES,
                     usages.size());

        // Testing asset part usages search
        usages = assetsUsageService.getAssetPartUsages(BIKE,
                                                       BIKE_FRONT,
                                                       PartType.FIELD,
                                                       path);

        assertNotNull(usages);
        assertEquals(BIKE_USAGES,
                     usages.size());

        usages = assetsUsageService.getAssetPartUsages(BIKE,
                                                       BIKE_BACK,
                                                       PartType.FIELD,
                                                       path);

        assertNotNull(usages);
        assertEquals(OTHER_USAGES,
                     usages.size());
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestJavaIndexer(moduleService);
    }

    @Override
    protected TestJavaResourceTypeDefinition getResourceTypeDefinition() {
        return new TestJavaResourceTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }
}
