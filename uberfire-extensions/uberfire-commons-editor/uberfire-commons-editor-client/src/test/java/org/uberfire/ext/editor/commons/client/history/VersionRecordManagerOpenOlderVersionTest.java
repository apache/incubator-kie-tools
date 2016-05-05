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

import java.lang.Exception;import java.lang.IllegalArgumentException;import java.lang.Override;import java.lang.String;import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.editor.commons.client.file.RestorePopup;
import org.uberfire.ext.editor.commons.client.file.RestoreUtil;
import org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent;
import org.uberfire.java.nio.base.version.VersionRecord;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This happens when an editor is opened and the path has the version parameter pointing to an older version.
 * This should open and older version of the file.
 */
public class VersionRecordManagerOpenOlderVersionTest {

    private VersionRecordManager manager;
    private ArrayList<VersionRecord> versions = new ArrayList<VersionRecord>();
    private RestorePopup restorePopup;
    private RestoreUtil util;
    private VersionMenuDropDownButton dropDownButton;
    private SaveButton saveButton;
    private ObservablePath pathTo111;
    private ObservablePath pathTo222;
    private ObservablePath pathTo333;

    @Before
    public void setUp() throws Exception {
        dropDownButton = mock( VersionMenuDropDownButton.class );
        saveButton = mock( SaveButton.class );
        restorePopup = mock( RestorePopup.class );

        setUpUtil();
        setUpVersions();

        manager = new VersionRecordManager(
                dropDownButton,
                saveButton,
                restorePopup,
                util,
                new VersionSelectedEventMock(
                        new Callback<VersionSelectedEvent>() {
                            @Override
                            public void callback( VersionSelectedEvent result ) {
                                manager.onVersionSelectedEvent( result );
                            }
                        } ),
                new VersionServiceCallerMock( versions ) );

    }

    private void setUpVersions() {
        versions.add( getVersionRecord( "111" ) );
        versions.add( getVersionRecord( "222" ) );
        versions.add( getVersionRecord( "333" ) );
    }

    private void setUpUtil() {
        util = mock( RestoreUtil.class );
        pathTo111 = mock( ObservablePath.class );
        pathTo222 = mock( ObservablePath.class );
        pathTo333 = mock( ObservablePath.class );
        when( pathTo111.toURI() ).thenReturn( "hehe//111" );
        when( pathTo222.toURI() ).thenReturn( "hehe//222" );
        when( pathTo333.toURI() ).thenReturn( "hehe//333" );
        when( util.createObservablePath( pathTo333, "hehe//111" ) ).thenReturn( pathTo111 );
        when( util.createObservablePath( pathTo333, "hehe//222" ) ).thenReturn( pathTo222 );
        when( util.createObservablePath( pathTo333, "hehe//333" ) ).thenReturn( pathTo333 );
    }

    @Test
    public void testOpenOlder() throws Exception {

        manager.init(
                "222",
                pathTo333,
                new Callback<VersionRecord>() {
                    @Override
                    public void callback( VersionRecord result ) {
                        manager.setVersion( result.id() );
                    }
                } );

        assertEquals( pathTo222, manager.getCurrentPath() );
        assertEquals( pathTo333, manager.getPathToLatest() );
        assertEquals( "222", manager.getVersion() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOpenUnknownVersion() throws Exception {

        manager.init(
                "xxx",
                pathTo333,
                new Callback<VersionRecord>() {
                    @Override
                    public void callback( VersionRecord result ) {
                        manager.setVersion( result.id() );
                    }
                } );
    }

    private VersionRecord getVersionRecord( String version ) {
        VersionRecord versionRecord = mock( VersionRecord.class );
        when( versionRecord.id() ).thenReturn( version );
        when( versionRecord.uri() ).thenReturn( "hehe//" + version );
        return versionRecord;
    }

}
