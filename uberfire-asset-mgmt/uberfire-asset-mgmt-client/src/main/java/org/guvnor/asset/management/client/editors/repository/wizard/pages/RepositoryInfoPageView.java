/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.client.editors.repository.wizard.pages;

import java.util.List;

import org.uberfire.client.mvp.UberView;
import org.uberfire.commons.data.Pair;

public interface RepositoryInfoPageView
        extends
        UberView<RepositoryInfoPageView.Presenter> {

    String NOT_SELECTED = "NOT_SELECTED";

    interface Presenter {

        void onNameChange();

        void onOUChange();

        void onManagedRepositoryChange();
    }

    String getName();

    void setName(String name);

    void setNameErrorMessage(String message);

    void clearNameErrorMessage();

    void initOrganizationalUnits(List<Pair<String, String>> organizationalUnits);

    String getOrganizationalUnitName();

    void setVisibleOU(boolean visible);

    void setValidOU(boolean ouValid);

    boolean isManagedRepository();

    void enabledManagedRepositoryCreation(boolean enabled);

    void alert(String message);
}
