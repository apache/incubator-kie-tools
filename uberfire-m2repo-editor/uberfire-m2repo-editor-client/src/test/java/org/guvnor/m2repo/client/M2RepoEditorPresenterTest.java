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

package org.guvnor.m2repo.client;

import javax.enterprise.event.Event;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.m2repo.client.event.M2RepoRefreshEvent;
import org.guvnor.m2repo.client.upload.UploadFormPresenter;
import org.gwtbootstrap3.client.ui.Button;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuItemCommand;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class M2RepoEditorPresenterTest {

    @Mock
    UploadFormPresenter uploadFormPresenter;

    @Spy
    Event<M2RepoRefreshEvent> refreshEvents = new EventSourceMock<M2RepoRefreshEvent>();

    @GwtMock
    Button menuRefreshButton;

    ClickHandler clickHandler;

    @InjectMocks
    M2RepoEditorPresenter presenter;

    @Before
    public void setup() {
        doNothing().when(refreshEvents).fire(any(M2RepoRefreshEvent.class));
    }

    @Test
    public void testUpload() {
        presenter.getMenus(menus -> {
            final MenuItemCommand upload = (MenuItemCommand) menus.getItems().get(0);
            upload.getCommand().execute();

            verify(uploadFormPresenter).showView();
        });
    }

    @Test
    public void testRefresh() {
        when(menuRefreshButton.addClickHandler(any(ClickHandler.class))).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock aInvocation) throws Throwable {
                clickHandler = (ClickHandler) aInvocation.getArguments()[0];
                return null;
            }
        });

        presenter.getMenus(menus -> {
            final MenuCustom refresh = (MenuCustom) menus.getItems().get(1);
            refresh.build();

            clickHandler.onClick(new ClickEvent() {
            });

            verify(refreshEvents).fire(any(M2RepoRefreshEvent.class));
        });
    }
}
