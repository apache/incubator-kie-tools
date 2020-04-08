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

package org.uberfire.ext.editor.commons.client.file.popups;

import com.google.gwtmockito.WithClassesToStub;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.file.RestoreUtil;
import org.uberfire.ext.editor.commons.version.CurrentBranch;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@WithClassesToStub({CommonConstants.class, ObservablePath.class})
public class RestorePopUpPresenterTest {

    @Mock
    BusyIndicatorView busyIndicatorView;

    @Mock
    Caller<VersionService> versionService;

    @Mock
    EventSourceMock<RestoreEvent> restoreEvent;

    @Mock
    RestoreUtil restoreUtil;

    @Mock
    RestorePopUpPresenter.View view;

    @Mock
    ObservablePath path;

    @Mock
    ParameterizedCommand<String> commandMock;

    @Mock
    CurrentBranch currentBranch;

    RestorePopUpPresenter presenter;

    @Before
    public void init() throws Exception {
        presenter = new RestorePopUpPresenter(view,
                                              busyIndicatorView,
                                              versionService,
                                              restoreEvent,
                                              restoreUtil) {
        };
    }

    @Test
    public void testSetup() throws Exception {
        presenter.setup();

        verify(view).init(presenter);
    }

    @Test
    public void testRestore() throws Exception {
        when(view.getComment()).thenReturn("test");
        presenter.command = commandMock;

        presenter.restore();

        verify(commandMock).execute("test");
        verify(view).hide();
    }

    @Test
    public void testShow() throws Exception {
        presenter = spy(presenter);

        presenter.show(path,
                       "uri",
                       currentBranch.getName());

        verify(view).show();
        verify(presenter).restoreCommand(path,
                                         "uri",
                                         currentBranch.getName());
    }

    @Test
    public void testCancel() throws Exception {
        presenter.cancel();

        verify(view).hide();
    }
}