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

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.guvnor.structure.contributors.ContributorType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

@Templated("#root")
public class RoleItemView implements RoleItemPresenter.View {

    @Inject
    private TranslationService ts;

    @Inject
    @Named("span")
    @DataField("role-name")
    private HTMLElement roleName;

    @Inject
    @DataField("read")
    private HTMLInputElement canRead;

    @Inject
    @DataField("write")
    private HTMLInputElement canWrite;

    @Inject
    @DataField("delete")
    private HTMLInputElement canDelete;

    @Inject
    @DataField("deploy")
    private HTMLInputElement canDeploy;

    private RoleItemPresenter presenter;

    @Override
    public void init(final RoleItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setRoleName(final String name) {
        if (ContributorType.OWNER.name().equals(name)) {
            roleName.innerHTML = ts.format(LibraryConstants.ContributorTypeOwner);
        } else if (ContributorType.ADMIN.name().equals(name)) {
            roleName.innerHTML = ts.format(LibraryConstants.ContributorTypeAdmin);
        } else if (ContributorType.CONTRIBUTOR.name().equals(name)) {
            roleName.innerHTML = ts.format(LibraryConstants.ContributorTypeContributor);
        } else {
            roleName.innerHTML = name;
        }
    }

    @Override
    public boolean canRead() {
        return canRead.checked;
    }

    @Override
    public void setCanRead(boolean canRead) {
        this.canRead.checked = canRead;
    }

    @Override
    public boolean canWrite() {
        return canWrite.checked;
    }

    @Override
    public void setCanWrite(boolean canWrite) {
        this.canWrite.checked = canWrite;
    }

    @Override
    public boolean canDelete() {
        return canDelete.checked;
    }

    @Override
    public void setCanDelete(boolean canDelete) {
        this.canDelete.checked = canDelete;
    }

    @Override
    public boolean canDeploy() {
        return canDeploy.checked;
    }

    @Override
    public void setCanDeploy(boolean canDeploy) {
        this.canDeploy.checked = canDeploy;
    }

    @Override
    public void disableCanRead() {
        canRead.disabled = true;
    }

    @EventHandler("read")
    public void onCanReadChanged(final ChangeEvent ignore) {
        presenter.setCanRead(canRead.checked);
    }

    @EventHandler("write")
    public void onCanWriteChanged(final ChangeEvent ignore) {
        presenter.setCanWrite(canWrite.checked);
    }

    @EventHandler("delete")
    public void onCanDeleteChanged(final ChangeEvent ignore) {
        presenter.setCanDelete(canDelete.checked);
    }

    @EventHandler("deploy")
    public void onCanDeployChanged(final ChangeEvent ignore) {
        presenter.setCanDeploy(canDeploy.checked);
    }
}
