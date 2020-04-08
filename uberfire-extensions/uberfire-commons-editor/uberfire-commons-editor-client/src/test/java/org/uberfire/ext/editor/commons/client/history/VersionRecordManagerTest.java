/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.history;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.editor.commons.client.file.RestoreUtil;
import org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent;
import org.uberfire.ext.editor.commons.version.CurrentBranch;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.uberfire.ext.editor.commons.client.history.Helper.getVersionRecord;

public class VersionRecordManagerTest {

    private VersionRecordManager manager;
    private ArrayList<VersionRecord> versions = new ArrayList<VersionRecord>();
    private RestorePopUpPresenter restorePopup;
    private RestoreUtil util;
    private VersionMenuDropDownButton dropDownButton;
    private SaveButton saveButton;
    private ObservablePath pathTo111;
    private ObservablePath pathTo222;
    private ObservablePath pathTo333;
    private VersionSelectedEventMock versionSelectedEvent;
    private CurrentBranch currentBranch;

    @Before
    public void setUp() throws Exception {
        dropDownButton = mock(VersionMenuDropDownButton.class);
        saveButton = mock(SaveButton.class);
        restorePopup = mock(RestorePopUpPresenter.class);
        currentBranch = mock(CurrentBranch.class);

        setUpUtil();
        setUpVersions();

        versionSelectedEvent = spy(new VersionSelectedEventMock(new Callback<VersionSelectedEvent>() {
            @Override
            public void callback(VersionSelectedEvent result) {
                manager.onVersionSelectedEvent(result);
            }
        }));
        manager = spy(new VersionRecordManager(dropDownButton,
                                               saveButton,
                                               restorePopup,
                                               util,
                                               versionSelectedEvent,
                                               new VersionServiceCallerMock(versions),
                                               currentBranch));

        manager.init(null,
                     pathTo333,
                     new Callback<VersionRecord>() {
                         @Override
                         public void callback(VersionRecord result) {
                             manager.setVersion(result.id());
                         }
                     });
    }

    private void setUpVersions() {
        versions.add(getVersionRecord("111"));
        versions.add(getVersionRecord("222"));
        versions.add(getVersionRecord("333"));
    }

    private void setUpUtil() {
        util = mock(RestoreUtil.class);
        pathTo111 = mock(ObservablePath.class);
        pathTo222 = mock(ObservablePath.class);
        pathTo333 = mock(ObservablePath.class);
        when(pathTo111.toURI()).thenReturn("hehe//111");
        when(pathTo222.toURI()).thenReturn("hehe//222");
        when(pathTo333.toURI()).thenReturn("hehe//333");
        when(util.createObservablePath(pathTo333,
                                       "hehe//111")).thenReturn(pathTo111);
        when(util.createObservablePath(pathTo333,
                                       "hehe//222")).thenReturn(pathTo222);
        when(util.createObservablePath(pathTo333,
                                       "hehe//333")).thenReturn(pathTo333);
    }

    @Test
    public void testSimple() throws Exception {
        verify(dropDownButton).setItems(versions);
        assertEquals(pathTo333,
                     manager.getCurrentPath());
        assertEquals(pathTo333,
                     manager.getPathToLatest());
        assertEquals("333",
                     manager.getVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVersionToNull() throws Exception {
        manager.setVersion(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVersionsNull() throws Exception {
        manager.setVersions(null);
    }

    @Test
    public void testVersionChange() throws Exception {
        manager.onVersionSelectedEvent(new VersionSelectedEvent(pathTo333,
                                                                getVersionRecord("111")));

        assertEquals(pathTo111,
                     manager.getCurrentPath());
        assertEquals("111",
                     manager.getVersion());
    }

    @Test
    public void testReset() throws Exception {
        manager.onVersionSelectedEvent(new VersionSelectedEvent(pathTo333,
                                                                getVersionRecord("111")));

        manager.restoreToCurrentVersion(true);

        verify(restorePopup).show(pathTo111,
                                  "hehe//111",
                                  currentBranch.getName());
    }

    @Test
    public void testRestoreToCurrentVersionWithNoComment() {
        manager.onVersionSelectedEvent(new VersionSelectedEvent(pathTo333,
                                                                getVersionRecord("111")));
        when(restorePopup.restoreCommand(pathTo111,
                                         "hehe//111",
                                         currentBranch.getName())).thenReturn(mock(ParameterizedCommand.class));
        manager.restoreToCurrentVersion(false);
        verify(restorePopup).restoreCommand(pathTo111,
                                            "hehe//111",
                                            currentBranch.getName());
    }

    @Test
    public void testReload() throws Exception {
        versions.add(getVersionRecord("444"));

        ObservablePath pathTo444 = mock(ObservablePath.class);

        when(pathTo444.toURI()).thenReturn("hehe//444");
        when(util.createObservablePath(pathTo444,
                                       "hehe//444")).thenReturn(pathTo444);

        manager.reloadVersions(pathTo444);

        assertEquals(pathTo444,
                     manager.getPathToLatest());
        assertEquals(pathTo444,
                     manager.getCurrentPath());
        assertEquals("444",
                     manager.getVersion());
    }

    @Test
    public void saveButtonLabelChangeTest() throws Exception {
        //This is called by setUp()'s call to manager.init(..)
        verify(saveButton,
               times(1)).setTextToSave();

        //when an older version is selected the label should be "Restore"
        manager.onVersionSelectedEvent(new VersionSelectedEvent(pathTo333,
                                                                getVersionRecord("111")));
        verify(saveButton,
               times(1)).setTextToRestore();

        //if last version is selected the label should be "Save"
        manager.onVersionSelectedEvent(new VersionSelectedEvent(pathTo333,
                                                                getVersionRecord("333")));
        verify(saveButton,
               times(2)).setTextToSave();

        //if we go back to an older version again the label should be "Restore" again
        manager.onVersionSelectedEvent(new VersionSelectedEvent(pathTo333,
                                                                getVersionRecord("222")));
        verify(saveButton,
               times(2)).setTextToRestore();
    }

    @Test
    public void testInitNeedsToClearTheState() throws Exception {
        // clear the state before to init. This will cover the cases where the init method is invoked multiple
        // times. for example if KieEditor.init(...) method is invoked multiple times, or KieMultipleDocumentEditor
        // re-initialises VersionRecordManager for different document.

        verify(manager).clear();
        verify(dropDownButton).resetVersions();
    }

    @Test
    public void testReinitialise_WithLatestVersion() {
        //Reset Mocks so we can check the *real* interactions expected by this test, excluding those in setUp()
        reset(saveButton);
        reset(dropDownButton);

        //Emulate reinitialisation with history consisting of a single entry
        versions.clear();
        versions.add(getVersionRecord("111"));

        manager.init(null,
                     pathTo111,
                     (VersionRecord result) -> manager.setVersion(result.id()));

        verify(dropDownButton).setItems(versions);
        verify(saveButton,
               times(1)).setTextToSave();

        assertEquals(pathTo111,
                     manager.getCurrentPath());
        assertEquals(pathTo111,
                     manager.getPathToLatest());
        assertEquals("111",
                     manager.getVersion());
    }

    @Test
    public void testReinitialise_WithOlderVersion() {
        //This is called by setUp()'s call to manager.init(..)
        verify(saveButton,
               times(1)).setTextToSave();

        //Reset Mocks so we can check the *real* interactions expected by this test, excluding those in setUp()
        reset(saveButton);
        reset(dropDownButton);

        //Emulate reinitialisation with history consisting of a single entry
        versions.clear();
        versions.add(getVersionRecord("333"));
        versions.add(getVersionRecord("111"));

        manager.init("333",
                     pathTo333,
                     (VersionRecord result) -> manager.setVersion(result.id()));

        verify(dropDownButton).setItems(versions);
        verify(saveButton,
               times(1)).setTextToRestore();

        assertEquals(pathTo333,
                     manager.getCurrentPath());
        assertEquals(pathTo333,
                     manager.getPathToLatest());
        assertEquals("333",
                     manager.getVersion());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void zeroVersionRecordsForDeletedFile() {
        reset(saveButton);
        reset(dropDownButton);

        versions.clear();

        manager.init(null,
                     pathTo111,
                     (VersionRecord result) -> manager.setVersion(result.id()));

        verify(dropDownButton,
               never()).setItems(any(List.class));
        verify(dropDownButton,
               never()).setVersion(any(String.class));

        assertEquals(pathTo111,
                     manager.getCurrentPath());
        assertEquals(pathTo111,
                     manager.getPathToLatest());
        assertNull(manager.getVersion());
    }
}
