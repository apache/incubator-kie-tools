/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.builder.core;

import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LRUPomModelCacheTest {

    @Mock
    private KieModuleService moduleService;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private KieModule module;

    @Mock
    private KieModule otherModule;

    @Mock
    private Path resourcePath;

    private LRUPomModelCache cache;

    @Before
    public void setup() {
        this.cache = spy(new LRUPomModelCache(moduleService));
    }

    @Test
    public void testCacheIsInvalidatedWhenResourceThatMapsToProject() {
        final InvalidateDMOModuleCacheEvent event = new InvalidateDMOModuleCacheEvent(sessionInfo,
                                                                                      module,
                                                                                      resourcePath);
        doReturn(module).when(moduleService).resolveModule(resourcePath);

        cache.invalidateProjectCache(event);

        verify(cache).invalidateCache(eq(module));
        verify(cache,
               never()).invalidateCache(eq(otherModule));
    }
}