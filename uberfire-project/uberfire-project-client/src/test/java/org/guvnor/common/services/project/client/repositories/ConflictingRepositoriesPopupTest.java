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

package org.guvnor.common.services.project.client.repositories;

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.shared.security.AppRoles;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConflictingRepositoriesPopupTest {

    @Mock
    ConflictingRepositoriesPopupView view;

    @Mock
    User user;

    private ConflictingRepositoriesPopup presenter;

    @Before
    public void setup() {
        presenter = new ConflictingRepositoriesPopup(user,
                                                     view);
    }

    @Test
    public void testInitialization() {
        verify(view,
               times(1)).init(eq(presenter));
        verify(view,
               never()).addOKButton();
        verify(view,
               never()).addOverrideButton(any(Command.class));
    }

    @Test
    public void testSetContent_Administrator() {
        final GAV gav = mock(GAV.class);
        final Command command = mock(Command.class);

        final Set<MavenRepositoryMetadata> metadata = new HashSet<MavenRepositoryMetadata>();
        final Set<Role> roles = new HashSet<Role>() {{
            add(new RoleImpl(AppRoles.ADMIN.getName()));
        }};
        when(user.getRoles()).thenReturn(roles);

        presenter.setContent(gav,
                             metadata,
                             command);

        verify(view,
               times(1)).clear();
        verify(view,
               times(1)).setContent(eq(gav),
                                    eq(metadata));
        verify(view,
               times(1)).addOKButton();
        verify(view,
               times(1)).addOverrideButton(any(Command.class));
    }

    @Test
    public void testSetContent_NotAdministrator() {
        final GAV gav = mock(GAV.class);
        final Command command = mock(Command.class);

        final Set<MavenRepositoryMetadata> metadata = new HashSet<MavenRepositoryMetadata>();
        final Set<Role> roles = new HashSet<Role>();
        when(user.getRoles()).thenReturn(roles);

        presenter.setContent(gav,
                             metadata,
                             command);

        verify(view,
               times(1)).clear();
        verify(view,
               times(1)).setContent(eq(gav),
                                    eq(metadata));
        verify(view,
               times(1)).addOKButton();
        verify(view,
               never()).addOverrideButton(any(Command.class));
    }

    @Test
    public void testSetContent_Reuse() {
        final GAV gav = mock(GAV.class);
        final Command command = mock(Command.class);

        final Set<MavenRepositoryMetadata> metadata = new HashSet<MavenRepositoryMetadata>();
        final Set<Role> roles = new HashSet<Role>() {{
            add(new RoleImpl(AppRoles.ADMIN.getName()));
        }};
        when(user.getRoles()).thenReturn(roles);

        presenter.setContent(gav,
                             metadata,
                             command);

        verify(view,
               times(1)).clear();
        verify(view,
               times(1)).setContent(eq(gav),
                                    eq(metadata));
        verify(view,
               times(1)).addOKButton();
        verify(view,
               times(1)).addOverrideButton(any(Command.class));

        //Re-use
        presenter.setContent(gav,
                             metadata,
                             command);
        verify(view,
               times(2)).clear();
        verify(view,
               times(2)).setContent(eq(gav),
                                    eq(metadata));
        verify(view,
               times(2)).addOKButton();
        verify(view,
               times(2)).addOverrideButton(any(Command.class));
    }

    @Test
    public void testShow() {
        presenter.show();

        verify(view,
               times(1)).show();
    }

    @Test
    public void testHide() {
        presenter.hide();

        verify(view,
               times(1)).hide();
    }
}
