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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.utils.ShortcutHelper;
import org.kie.workbench.common.screens.home.model.HomeShortcutLink;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShortcutSubHeadingLinkPresenterTest {

    @Mock
    private ShortcutSubHeadingLinkPresenter.View view;

    @Mock
    private ShortcutHelper shortcutHelper;

    @Mock
    private HomeShortcutLink link;

    @InjectMocks
    private ShortcutSubHeadingLinkPresenter presenter;

    @Test
    public void setupWithPermissionTest() {
        doReturn(true).when(shortcutHelper).authorize("perspectiveIdentifier");

        presenter.setup(new HomeShortcutLink("label",
                                             "perspectiveIdentifier"));

        verify(view).setLabel("label");
        verify(view,
               never()).disable();
    }

    @Test
    public void setupWithoutPermissionTest() {
        doReturn(false).when(shortcutHelper).authorize("perspectiveIdentifier");

        presenter.setup(new HomeShortcutLink("label",
                                             "perspectiveIdentifier"));

        verify(view).setLabel("label");
        verify(view).disable();
    }

    @Test
    public void goToPerspectiveTest() {
        presenter.setup(new HomeShortcutLink("label",
                                             "perspectiveIdentifier"));

        presenter.goToPerspective();

        verify(shortcutHelper).goTo("perspectiveIdentifier");
    }
}
