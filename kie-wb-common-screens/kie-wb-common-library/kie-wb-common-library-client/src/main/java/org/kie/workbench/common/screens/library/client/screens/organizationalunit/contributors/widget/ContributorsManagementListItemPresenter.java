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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.widget;

import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.uberfire.client.mvp.UberElement;

public class ContributorsManagementListItemPresenter {

    public interface View extends UberElement<ContributorsManagementListItemPresenter> {

        void setUserName(String userName);

        void setSelected(boolean selected);

        boolean isSelected();

        void setEnabled(boolean enabled);
    }

    private View view;

    @Inject
    public ContributorsManagementListItemPresenter(final View view) {
        this.view = view;
    }

    public void setup(final SocialUser user) {
        setup(user,
              false);
    }

    public void setup(final SocialUser user,
                      final boolean selected) {
        view.init(this);

        if (user.getName() != null && !user.getName().isEmpty()) {
            view.setUserName(user.getName());
        } else {
            view.setUserName(user.getUserName());
        }

        if (selected) {
            view.setSelected(selected);
        }
    }

    public void setSelected(final boolean selected) {
        view.setSelected(selected);
    }

    public boolean isSelected() {
        return view.isSelected();
    }

    public void setEnabled(boolean enabled) {
        view.setEnabled(enabled);
    }

    public View getView() {
        return view;
    }
}
