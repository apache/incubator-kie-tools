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

package org.guvnor.common.services.project.preferences.scope;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.PreferenceScopeFactoryImpl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserProjectPreferenceScopeTest {

    private PreferenceScopeTypes scopeTypes;

    private PreferenceScopeFactory scopeFactory;

    private ProjectContext projectContext;

    private UserProjectPreferenceScope userProjectScope;

    @Before
    public void setup() {
        scopeTypes = mock(PreferenceScopeTypes.class);
        doReturn("admin").when(scopeTypes).getDefaultKeyFor("user");
        doReturn(false).when(scopeTypes).typeRequiresKey("user");
        doReturn(true).when(scopeTypes).typeRequiresKey("project");

        scopeFactory = new PreferenceScopeFactoryImpl(scopeTypes);

        projectContext = mock(ProjectContext.class);

        userProjectScope = new UserProjectPreferenceScope(scopeFactory,
                                                          projectContext);
    }

    @Test
    public void userProjectScopeIsResolvedWithCustomProjectCorrectlyTest() {
        final Project project = mock(Project.class);
        doReturn("identifier").when(project).getEncodedIdentifier();
        userProjectScope.forProject(project);

        final PreferenceScope resolvedUserProjectScope = userProjectScope.resolve();

        assertEquals("user",
                     resolvedUserProjectScope.type());
        assertEquals("admin",
                     resolvedUserProjectScope.key());
        assertNotNull(resolvedUserProjectScope.childScope());
        assertEquals("project",
                     resolvedUserProjectScope.childScope().type());
        assertEquals("identifier",
                     resolvedUserProjectScope.childScope().key());
        assertNull(resolvedUserProjectScope.childScope().childScope());
    }

    @Test
    public void userProjectScopeIsResolvedWithActiveProjectCorrectlyTest() {
        final Project project = mock(Project.class);
        doReturn("identifier").when(project).getEncodedIdentifier();
        doReturn(project).when(projectContext).getActiveProject();

        final PreferenceScope resolvedUserProjectScope = userProjectScope.resolve();

        assertEquals("user",
                     resolvedUserProjectScope.type());
        assertEquals("admin",
                     resolvedUserProjectScope.key());
        assertNotNull(resolvedUserProjectScope.childScope());
        assertEquals("project",
                     resolvedUserProjectScope.childScope().type());
        assertEquals("identifier",
                     resolvedUserProjectScope.childScope().key());
        assertNull(resolvedUserProjectScope.childScope().childScope());
    }

    @Test(expected = RuntimeException.class)
    public void userProjectScopeIsNotResolvedWhenProjectIsNotPassedTest() {
        userProjectScope.resolve();
    }
}
