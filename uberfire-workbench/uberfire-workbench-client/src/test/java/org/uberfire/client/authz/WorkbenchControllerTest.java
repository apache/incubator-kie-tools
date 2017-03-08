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

package org.uberfire.client.authz;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.AuthorizationManager;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchControllerTest {

    @Mock
    User user;

    @Mock
    Command onGranted;

    @Mock
    Command onDenied;

    @Mock
    AuthorizationManager authorizationManager;

    @Mock
    PerspectiveActivity perspectiveActivity;

    @InjectMocks
    DefaultWorkbenchController workbenchController;

    @Before
    public void setUp() {
    }

    @Test
    public void testPerspectiveUpdateAction() {
        workbenchController.perspective(perspectiveActivity).update()
                .granted(onGranted)
                .denied(onDenied);

        verify(authorizationManager).authorize(perspectiveActivity,
                                               PerspectiveAction.UPDATE,
                                               user);
    }

    @Test
    public void testPerspectiveDeleteAction() {
        workbenchController.perspective(perspectiveActivity).delete()
                .granted(onGranted)
                .denied(onDenied);

        verify(authorizationManager).authorize(perspectiveActivity,
                                               PerspectiveAction.DELETE,
                                               user);
    }
}