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

package org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.BaseMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.CanBeDisabled;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.HasChildren;

public class GroupContextMenuItemPresenter implements BaseMenuItemPresenter,
                                                      HasChildren,
                                                      CanBeDisabled {

    public interface View extends UberElement<GroupContextMenuItemPresenter> {

        void setLabel(String label);

        void addItem(IsElement item);

        void enable();

        void disable();

        void pullRight();
    }

    private View view;

    @Inject
    public GroupContextMenuItemPresenter(final View view) {
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

    @Override
    public void enable() {
        view.enable();
    }

    @Override
    public void disable() {
        view.disable();
    }

    public void pullRight() {
        view.pullRight();
    }
}
