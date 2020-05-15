/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoadReposOnAppInitTest {

    @Mock
    private ConfiguredRepositories configuredRepositories;

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Before
    public void setUp() throws Exception {
        when(organizationalUnitService.getAllOrganizationalUnits())
                .thenReturn(new ArrayList<OrganizationalUnit>() {{
                    add(new OrganizationalUnitImpl("test", "test"));
                }});
    }

    @Test
    public void testLoadRepositories() {

        final LoadReposOnAppInit loadReposOnAppInit = spy(new LoadReposOnAppInit(configuredRepositories,
                                                                                 organizationalUnitService));

        doReturn(true).when(loadReposOnAppInit).isGitDefaultFileSystem();
        loadReposOnAppInit.execute();
        verify(configuredRepositories, times(1)).getAllConfiguredRepositories(any());
    }

    @Test
    public void testOnlyRunInBCServerType() {
        {
            LoadReposOnAppInit loadReposOnInit = spy(new LoadReposOnAppInit(configuredRepositories,
                                                                            organizationalUnitService));
            doReturn(false).when(loadReposOnInit).isGitDefaultFileSystem();
            loadReposOnInit.execute();
            verify(organizationalUnitService, never()).getAllOrganizationalUnits();
        }
        {
            LoadReposOnAppInit loadReposOnInit = spy(new LoadReposOnAppInit(configuredRepositories,
                                                                            organizationalUnitService));
            doReturn(true).when(loadReposOnInit).isGitDefaultFileSystem();
            loadReposOnInit.execute();
            verify(organizationalUnitService, times(1)).getAllOrganizationalUnits();
        }
    }
}