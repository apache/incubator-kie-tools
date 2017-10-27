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

import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LRUPomModelCacheTest {

    @Mock
    private KieProjectService projectService;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private KieProject project;

    @Mock
    private KieProject otherProject;

    @Mock
    private Path resourcePath;

    private LRUPomModelCache cache;

    @Before
    public void setup() {
        this.cache = spy(new LRUPomModelCache(projectService));
    }

    @Test
    public void testCacheIsInvalidatedWhenResourceThatMapsToProject() {
        final InvalidateDMOProjectCacheEvent event = new InvalidateDMOProjectCacheEvent(sessionInfo,
                                                                                        project,
                                                                                        resourcePath);
        doReturn(project).when(projectService).resolveProject(resourcePath);

        cache.invalidateProjectCache(event);

        verify(cache).invalidateCache(eq(project));
        verify(cache,
               never()).invalidateCache(eq(otherProject));
    }
}