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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.config.RolePermissions;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;

@Dependent
public class RoleItemPresenter extends ListItemPresenter<RolePermissions, BranchManagementPresenter, RoleItemPresenter.View> {

    public interface View extends ListItemView<RoleItemPresenter>,
                                  IsElement {

        void setRoleName(String name);

        boolean canRead();

        void setCanRead(boolean canRead);

        boolean canWrite();

        void setCanWrite(boolean canWrite);

        boolean canDelete();

        void setCanDelete(boolean canDelete);

        boolean canDeploy();

        void setCanDeploy(boolean canDeploy);

        void disableCanRead();
    }

    RolePermissions rolePermissions;

    BranchManagementPresenter parentPresenter;

    @Inject
    public RoleItemPresenter(final View view) {
        super(view);
    }

    @Override
    public RoleItemPresenter setup(final RolePermissions rolePermissions,
                                   final BranchManagementPresenter parentPresenter) {
        view.init(this);

        this.rolePermissions = rolePermissions;
        this.parentPresenter = parentPresenter;

        view.setRoleName(rolePermissions.getRoleName());
        view.setCanRead(rolePermissions.canRead());
        view.setCanWrite(rolePermissions.canWrite());
        view.setCanDelete(rolePermissions.canDelete());
        view.setCanDeploy(rolePermissions.canDeploy());

        if ("master".equals(parentPresenter.selectedBranch)) {
            view.disableCanRead();
        }

        return this;
    }

    public void setCanRead(boolean canRead) {
        this.rolePermissions.setCanRead(canRead);
        if (!canRead) {
            setForceCanWrite(false);
            setForceCanDelete(false);
            setForceCanDeploy(false);
        }

        parentPresenter.fireChangeEvent();
    }

    public void setCanWrite(boolean canWrite) {
        this.rolePermissions.setCanWrite(canWrite);
        if (!canWrite) {
            setForceCanDelete(false);
            setForceCanDeploy(false);
        } else {
            setForceCanRead(true);
        }

        parentPresenter.fireChangeEvent();
    }

    public void setCanDelete(boolean canDelete) {
        this.rolePermissions.setCanDelete(canDelete);
        if (canDelete) {
            setForceCanRead(true);
        }

        parentPresenter.fireChangeEvent();
    }

    public void setCanDeploy(boolean canDeploy) {
        this.rolePermissions.setCanDeploy(canDeploy);
        if (canDeploy) {
            setForceCanRead(true);
            setForceCanWrite(true);
        }

        parentPresenter.fireChangeEvent();
    }

    private void setForceCanRead(final boolean forceCanRead) {
        this.rolePermissions.setCanRead(forceCanRead);
        view.setCanRead(forceCanRead);
    }

    private void setForceCanWrite(final boolean forceCanWrite) {
        this.rolePermissions.setCanWrite(forceCanWrite);
        view.setCanWrite(forceCanWrite);
    }

    private void setForceCanDelete(final boolean forceCanDelete) {
        this.rolePermissions.setCanDelete(forceCanDelete);
        view.setCanDelete(forceCanDelete);
    }

    private void setForceCanDeploy(final boolean forceCanDeploy) {
        this.rolePermissions.setCanDeploy(forceCanDeploy);
        view.setCanDeploy(forceCanDeploy);
    }

    @Override
    public RolePermissions getObject() {
        return rolePermissions;
    }
}
