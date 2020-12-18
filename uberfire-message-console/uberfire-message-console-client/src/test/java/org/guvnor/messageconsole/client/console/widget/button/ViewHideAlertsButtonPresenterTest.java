/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.messageconsole.client.console.widget.button;

import org.guvnor.messageconsole.client.console.MessageConsoleScreen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ViewHideAlertsButtonPresenterTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ViewHideAlertsButtonPresenter.View view;

    @InjectMocks
    private ViewHideAlertsButtonPresenter presenter;

    @Test
    public void initTest() {
        presenter.init();

        verify(view).init(presenter);
        verify(view).setAlertsActive(anyBoolean());
        verify(placeManager).registerOnOpenCallback(eq(new DefaultPlaceRequest(MessageConsoleScreen.ALERTS)),
                                                    any());
        verify(placeManager).registerOnCloseCallback(eq(new DefaultPlaceRequest(MessageConsoleScreen.ALERTS)),
                                                     any());
    }

    @Test
    public void addCssClassToButtonsTest() {
        presenter.addCssClassToButtons("my-class");

        verify(view).addCssClassToButtons("my-class");
    }

    @Test
    public void viewAlertsTest() {
        presenter.viewAlerts();

        verify(placeManager).goTo(MessageConsoleScreen.ALERTS);
    }

    @Test
    public void hideAlertsTest() {
        presenter.hideAlerts();

        verify(placeManager).closePlace(MessageConsoleScreen.ALERTS);
    }
}
