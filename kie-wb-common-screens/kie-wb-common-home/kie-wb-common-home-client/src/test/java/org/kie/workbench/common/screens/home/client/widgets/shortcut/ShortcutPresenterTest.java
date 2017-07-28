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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShortcutPresenterTest {

    @Mock
    private ShortcutPresenter.View view;

    private ShortcutPresenter presenter;

    @Before
    public void setup() {
        presenter = new ShortcutPresenter(view);
    }

    @Test
    public void setupTest() {
        final HomeShortcut shortcut = ModelUtils.makeShortcut("iconCss1",
                                                              "heading1",
                                                              "subHeading1",
                                                              mock(Command.class));

        presenter.setup(shortcut);

        verify(view).setIcon(shortcut.getIconCss());
        verify(view).setHeading(shortcut.getHeading());
        verify(view).setSubHeading(shortcut.getSubHeading());
        verify(view).setAction(shortcut.getOnClickCommand());
    }
}
