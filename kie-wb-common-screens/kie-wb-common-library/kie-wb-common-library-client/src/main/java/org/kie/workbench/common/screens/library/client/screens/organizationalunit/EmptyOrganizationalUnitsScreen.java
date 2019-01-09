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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.popup.OrganizationalUnitPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.uberfire.client.mvp.UberElement;

public class EmptyOrganizationalUnitsScreen {

    public interface View extends UberElement<EmptyOrganizationalUnitsScreen> {

    }

    private View view;

    private ManagedInstance<OrganizationalUnitPopUpPresenter> organizationalUnitPopUpPresenters;

    private LibraryPermissions libraryPermissions;

    @Inject
    public EmptyOrganizationalUnitsScreen(final View view,
                                          final ManagedInstance<OrganizationalUnitPopUpPresenter> organizationalUnitPopUpPresenters,
                                          final LibraryPermissions libraryPermissions) {
        this.view = view;
        this.organizationalUnitPopUpPresenters = organizationalUnitPopUpPresenters;
        this.libraryPermissions = libraryPermissions;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void createOrganizationalUnit() {
        if (userCanCreateOrganizationalUnits()) {
            final OrganizationalUnitPopUpPresenter organizationalUnitPopUpPresenter = organizationalUnitPopUpPresenters.get();
            organizationalUnitPopUpPresenter.show();
        }
    }

    boolean userCanCreateOrganizationalUnits() {
        return libraryPermissions.userCanCreateOrganizationalUnit();
    }

    public View getView() {
        return view;
    }
}
