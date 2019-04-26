/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.branchmanagement;

import org.guvnor.structure.organizationalunit.config.RolePermissions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoleItemPresenterTest {

    @Mock
    private RoleItemPresenter.View view;

    private RoleItemPresenter presenter;

    private RolePermissions rolePermissions;

    private BranchManagementPresenter branchManagementPresenter;

    @Before
    public void setup() {
        presenter = new RoleItemPresenter(view);
        rolePermissions = new RolePermissions("myRole", true, true, true, true);
        branchManagementPresenter = mock(BranchManagementPresenter.class);

        presenter.setup(rolePermissions,
                        branchManagementPresenter);
    }

    @Test
    public void setupTest() {
        verify(view).init(presenter);
        assertSame(rolePermissions, presenter.rolePermissions);
        assertSame(branchManagementPresenter, presenter.parentPresenter);
        verify(view).setRoleName("myRole");
        verify(view).setCanRead(true);
        verify(view).setCanWrite(true);
        verify(view).setCanDelete(true);
        verify(view).setCanDeploy(true);
    }

    @Test
    public void setCanReadTest() {
        presenter.setCanRead(false);
        assertFalse(rolePermissions.canRead());
        verify(branchManagementPresenter).fireChangeEvent();
    }

    @Test
    public void setCanWriteTest() {
        presenter.setCanWrite(false);
        assertFalse(rolePermissions.canWrite());
        verify(branchManagementPresenter).fireChangeEvent();
    }

    @Test
    public void setCanDeleteTest() {
        presenter.setCanDelete(false);
        assertFalse(rolePermissions.canDelete());
        verify(branchManagementPresenter).fireChangeEvent();
    }

    @Test
    public void setCanBuildTest() {
        presenter.setCanDeploy(false);
        assertFalse(rolePermissions.canDeploy());
        verify(branchManagementPresenter).fireChangeEvent();
    }
}
