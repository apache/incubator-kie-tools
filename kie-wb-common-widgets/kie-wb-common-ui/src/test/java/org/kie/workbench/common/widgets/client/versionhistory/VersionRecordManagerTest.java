/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.widgets.client.versionhistory;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.versionhistory.event.VersionSelectedEvent;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class VersionRecordManagerTest {

    private VersionRecordManager manager;
    private ArrayList<VersionRecord> versions = new ArrayList<VersionRecord>();
    private RestorePopup restorePopup;
    private RestoreUtil util;
    private VersionMenuDropDownButton dropDownButton;
    private ObservablePath pathTo111;
    private ObservablePath pathTo222;
    private ObservablePath pathTo333;

    @Before
    public void setUp() throws Exception {
        dropDownButton = mock(VersionMenuDropDownButton.class);
        restorePopup = mock(RestorePopup.class);

        setUpUtil();
        setUpVersions();

        manager = new VersionRecordManager(
                dropDownButton,
                restorePopup,
                util,
                new VersionServiceCallerMock(versions));

        manager.init(
                null,
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
        when(util.createObservablePath(pathTo333, "hehe//111")).thenReturn(pathTo111);
        when(util.createObservablePath(pathTo333, "hehe//222")).thenReturn(pathTo222);
        when(util.createObservablePath(pathTo333, "hehe//333")).thenReturn(pathTo333);
    }

    @Test
    public void testSimple() throws Exception {

        verify(dropDownButton).addLabel(eq(versions.get(0)), eq(1), eq(false), any(Command.class));
        verify(dropDownButton).addLabel(eq(versions.get(1)), eq(2), eq(false), any(Command.class));
        verify(dropDownButton).addLabel(eq(versions.get(2)), eq(3), eq(true), any(Command.class));

        assertEquals(pathTo333, manager.getCurrentPath());
        assertEquals(pathTo333, manager.getPathToLatest());
        assertEquals("333", manager.getVersion());

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

        manager.onVersionSelectedEvent(new VersionSelectedEvent(pathTo333, getVersionRecord("111")));

        assertEquals(pathTo111, manager.getCurrentPath());
        assertEquals("111", manager.getVersion());
    }

    @Test
    public void testReset() throws Exception {
        manager.onVersionSelectedEvent(new VersionSelectedEvent(pathTo333, getVersionRecord("111")));

        manager.restoreToCurrentVersion();

        verify(restorePopup).show(pathTo111, "hehe//111");
    }

    @Test
    public void testReload() throws Exception {

        versions.add(getVersionRecord("444"));

        ObservablePath pathTo444 = mock(ObservablePath.class);

        when(pathTo444.toURI()).thenReturn("hehe//444");
        when(util.createObservablePath(pathTo444, "hehe//444")).thenReturn(pathTo444);

        manager.reloadVersions(pathTo444);

        assertEquals(pathTo444, manager.getPathToLatest());
        assertEquals(pathTo444, manager.getCurrentPath());
        assertEquals("444", manager.getVersion());
    }

    // init with null path

    private VersionRecord getVersionRecord(String version) {
        VersionRecord versionRecord = mock(VersionRecord.class);
        when(versionRecord.id()).thenReturn(version);
        when(versionRecord.uri()).thenReturn("hehe//" + version);
        return versionRecord;
    }
}
