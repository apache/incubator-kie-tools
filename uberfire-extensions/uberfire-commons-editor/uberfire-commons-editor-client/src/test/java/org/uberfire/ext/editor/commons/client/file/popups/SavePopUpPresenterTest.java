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

import java.util.HashMap;
import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.SaveInProgressEvent;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SavePopUpPresenterTest {

    @Mock
    Event<SaveInProgressEvent> saveInProgressEvent;

    @Mock
    SavePopUpPresenter.View view;

    @Mock
    ParameterizedCommand<String> command;

    @Mock
    Path path;

    SavePopUpPresenter presenter;

    @Before
    public void init() throws Exception {
        presenter = new SavePopUpPresenter(view, saveInProgressEvent);
    }

    @Test
    public void testSetup() throws Exception {
        presenter.setup();
        verify(view).init(presenter);
    }

    @Test
    public void testShowWithoutPath() throws Exception {
        presenter.show(command);

        verify(view).show();
        assertEquals(command,
                     presenter.getCommand());
    }

    @Test
    public void testShowWithAPathThatIsVersioned() throws Exception {
        final Path versionedPath = getVersionedPath();

        presenter.show(versionedPath,
                       command);

        verify(view).show();
        verifyNoMoreInteractions(saveInProgressEvent);
    }

    @Test
    public void testShowWithAPathThatIsNotVersioned() throws Exception {
        presenter.show(path,
                       command);

        verifyNoMoreInteractions(view);
        verify(command).execute("");
        verify(saveInProgressEvent).fire(new SaveInProgressEvent(any(Path.class)));
    }

    @Test
    public void testSaveWithCommand() throws Exception {
        when(view.getComment()).thenReturn("test");

        presenter.show(command);
        presenter.save();

        verify(command).execute("test");
        verify(view).hide();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveWithoutCommand() throws Exception {
        presenter.show(null);
        presenter.save();
    }

    private Path getVersionedPath() {
        return PathFactory.newPath("fileName",
                                   "uri",
                                   new HashMap<String, Object>() {
                                       {
                                           put(PathFactory.VERSION_PROPERTY,
                                               true);
                                       }
                                   });
    }

    @Test
    public void cancel() throws Exception {
        presenter.cancel();

        verify(view).hide();
    }
}
