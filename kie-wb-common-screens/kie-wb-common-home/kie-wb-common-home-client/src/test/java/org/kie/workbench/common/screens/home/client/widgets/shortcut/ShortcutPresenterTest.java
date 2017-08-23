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

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.subheading.ShortcutSubHeadingLinkPresenter;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.subheading.ShortcutSubHeadingTextPresenter;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.utils.ShortcutHelper;
import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.kie.workbench.common.screens.home.model.HomeShortcutLink;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShortcutPresenterTest {

    @Mock
    private ShortcutPresenter.View view;

    @Mock
    private ShortcutHelper shortcutHelper;

    @Mock
    private ManagedInstance<ShortcutSubHeadingLinkPresenter> linkPresenters;

    @Mock
    private ManagedInstance<ShortcutSubHeadingTextPresenter> textPresenters;

    private ShortcutPresenter presenter;

    @Mock
    private ShortcutSubHeadingTextPresenter textPresenter;

    @Mock
    private ShortcutSubHeadingLinkPresenter linkPresenter;

    @Mock
    private ShortcutSubHeadingTextPresenter.View textView;

    @Mock
    private ShortcutSubHeadingLinkPresenter.View linkView;

    @Before
    public void setup() {
        doReturn(true).when(shortcutHelper).authorize(any(HomeShortcut.class));

        doReturn(textView).when(textPresenter).getView();
        doReturn(linkView).when(linkPresenter).getView();

        doReturn(textPresenter).when(textPresenters).get();
        doReturn(linkPresenter).when(linkPresenters).get();

        presenter = new ShortcutPresenter(view,
                                          shortcutHelper,
                                          linkPresenters,
                                          textPresenters);
    }

    @Test
    public void setupTest() {
        final HomeShortcut shortcut = ModelUtils.makeShortcut("iconCss iconCss2",
                                                              "heading",
                                                              "subHeadingPrefix{0}subHeadingSuffix",
                                                              mock(Command.class));
        final HomeShortcutLink link = new HomeShortcutLink("label",
                                                           "perspectiveIdentifier");
        shortcut.addLink(link);

        presenter.setup(shortcut);

        verify(view).addIconClass("iconCss");
        verify(view).addIconClass("iconCss2");
        verify(view).setHeading(shortcut.getHeading());
        verify(view).setAction(shortcut.getOnClickCommand());

        verify(textPresenter).setup(shortcut.getSubHeading(),
                                    1);
        verify(textPresenter).setup(shortcut.getSubHeading(),
                                    2);
        verify(view,
               times(2)).addSubHeadingChild(textPresenter.getView());

        verify(linkPresenter).setup(link);
        verify(view).addSubHeadingChild(linkPresenter.getView());
    }

    @Test
    public void setupWithNoActionPermissionTest() {
        doReturn(false).when(shortcutHelper).authorize(any(HomeShortcut.class));

        final HomeShortcut shortcut = ModelUtils.makeShortcut("iconCss",
                                                              "heading",
                                                              "subHeadingPrefix",
                                                              mock(Command.class));

        presenter.setup(shortcut);

        verify(view,
               never()).setAction(shortcut.getOnClickCommand());
    }
}
