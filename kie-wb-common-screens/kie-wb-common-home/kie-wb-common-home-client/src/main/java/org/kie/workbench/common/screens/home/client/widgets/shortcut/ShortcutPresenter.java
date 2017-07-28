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

package org.kie.workbench.common.screens.home.client.widgets.shortcut;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public class ShortcutPresenter {

    public interface View extends UberElement<ShortcutPresenter> {

        void setIcon(String icon);

        void setHeading(String icon);

        void setSubHeading(String icon);

        void setAction(Command action);
    }

    private View view;

    @Inject
    public ShortcutPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final HomeShortcut shortcut) {
        view.setIcon(shortcut.getIconCss());
        view.setHeading(shortcut.getHeading());
        view.setSubHeading(shortcut.getSubHeading());
        view.setAction(shortcut.getOnClickCommand());
    }

    public View getView() {
        return view;
    }
}
