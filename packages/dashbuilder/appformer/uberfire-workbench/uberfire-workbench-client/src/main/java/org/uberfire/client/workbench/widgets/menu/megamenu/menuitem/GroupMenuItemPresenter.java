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

package org.uberfire.client.workbench.widgets.menu.megamenu.menuitem;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.BaseMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.HasChildren;

public class GroupMenuItemPresenter implements BaseMenuItemPresenter,
                                               HasChildren {

    public interface View extends UberElement<GroupMenuItemPresenter> {

        void setLabel(String label);

        void addItem(IsElement item);
    }

    private View view;

    @Inject
    public GroupMenuItemPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final String label) {
        view.setLabel(label);
    }

    @Override
    public void addChild(final IsElement item) {
        view.addItem(item);
    }

    @Override
    public View getView() {
        return view;
    }
}
