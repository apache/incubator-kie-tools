/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.client.admin.item;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.preferences.client.admin.page.AdminTool;
import org.uberfire.ext.preferences.client.event.PreferencesCentralActionsConfigurationEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminPageItemPresenterTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private EventSourceMock<PreferencesCentralActionsConfigurationEvent> adminPageConfigurationEvent;

    @Mock
    private AdminPageItemPresenter.View view;

    private AdminPageItemPresenter presenter;

    @Before
    public void setup() {
        presenter = new AdminPageItemPresenter(view,
                                               placeManager,
                                               adminPageConfigurationEvent);
    }

    @Test
    public void enterTest() {
        final Command command = spy(new Command() {
            @Override
            public void execute() {
            }
        });

        AdminTool adminTool = new AdminTool("title1",
                                            Collections.singleton("iconCss1"),
                                            "category1",
                                            command);

        presenter.setup(adminTool,
                        "screen",
                        null);
        presenter.enter();

        verify(view).init(presenter);
        verify(command).execute();
        verify(adminPageConfigurationEvent).fire(any(PreferencesCentralActionsConfigurationEvent.class));
    }
}
