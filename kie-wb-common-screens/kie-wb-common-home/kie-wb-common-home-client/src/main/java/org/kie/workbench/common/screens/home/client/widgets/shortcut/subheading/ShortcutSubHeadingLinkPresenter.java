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

package org.kie.workbench.common.screens.home.client.widgets.shortcut.subheading;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.workbench.common.screens.home.client.widgets.shortcut.utils.ShortcutHelper;
import org.kie.workbench.common.screens.home.model.HomeShortcutLink;
import org.uberfire.client.mvp.UberElement;

public class ShortcutSubHeadingLinkPresenter {

    public interface View extends UberElement<ShortcutSubHeadingLinkPresenter> {

        void setLabel(String label);

        void disable();
    }

    private View view;

    private ShortcutHelper shortcutHelper;

    private HomeShortcutLink link;

    @Inject
    public ShortcutSubHeadingLinkPresenter(final View view,
                                           final ShortcutHelper shortcutHelper) {
        this.view = view;
        this.shortcutHelper = shortcutHelper;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final HomeShortcutLink link) {
        this.link = link;
        view.setLabel(link.getLabel());
        if (!shortcutHelper.authorize(link.getPerspectiveIdentifier())) {
            view.disable();
        }
    }

    void goToPerspective() {
        shortcutHelper.goTo(link.getPerspectiveIdentifier());
    }

    public View getView() {
        return view;
    }
}
