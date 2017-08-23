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

import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.subheading.ShortcutSubHeadingLinkPresenter;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.subheading.ShortcutSubHeadingTextPresenter;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.utils.ShortcutHelper;
import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.kie.workbench.common.screens.home.model.HomeShortcutLink;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public class ShortcutPresenter {

    public interface View extends UberElement<ShortcutPresenter> {

        void addIconClass(String iconClass);

        void setHeading(String heading);

        void setAction(Command action);

        void addSubHeadingChild(IsElement child);
    }

    private View view;

    private ShortcutHelper shortcutHelper;

    private ManagedInstance<ShortcutSubHeadingLinkPresenter> linkPresenters;

    private ManagedInstance<ShortcutSubHeadingTextPresenter> textPresenters;

    @Inject
    public ShortcutPresenter(final View view,
                             final ShortcutHelper shortcutHelper,
                             final ManagedInstance<ShortcutSubHeadingLinkPresenter> linkPresenters,
                             final ManagedInstance<ShortcutSubHeadingTextPresenter> textPresenters) {
        this.view = view;
        this.shortcutHelper = shortcutHelper;
        this.linkPresenters = linkPresenters;
        this.textPresenters = textPresenters;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final HomeShortcut shortcut) {
        setupIcon(shortcut);
        view.setHeading(shortcut.getHeading());
        setupAction(shortcut);
        setupSubHeading(shortcut);
    }

    private void setupIcon(final HomeShortcut shortcut) {
        final String iconClass = shortcut.getIconCss();
        if (iconClass != null && !iconClass.isEmpty()) {
            Stream.of(iconClass.split(" ")).forEach(clazz -> view.addIconClass(clazz));
        }
    }

    private void setupSubHeading(final HomeShortcut shortcut) {
        int part = 1;
        addText(shortcut.getSubHeading(),
                part);
        for (HomeShortcutLink link : shortcut.getLinks()) {
            addLink(link);
            addText(shortcut.getSubHeading(),
                    ++part);
        }
    }

    private void setupAction(final HomeShortcut shortcut) {
        if (shortcutHelper.authorize(shortcut)) {
            view.setAction(shortcut.getOnClickCommand());
        }
    }

    private void addText(final String subHeading,
                         final int part) {
        final ShortcutSubHeadingTextPresenter textPresenter = textPresenters.get();
        textPresenter.setup(subHeading,
                            part);
        view.addSubHeadingChild(textPresenter.getView());
    }

    private void addLink(final HomeShortcutLink link) {
        final ShortcutSubHeadingLinkPresenter linkPresenter = linkPresenters.get();
        linkPresenter.setup(link);
        view.addSubHeadingChild(linkPresenter.getView());
    }

    public View getView() {
        return view;
    }
}
