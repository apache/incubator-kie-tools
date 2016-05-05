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

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;
import static org.uberfire.ext.editor.commons.client.history.Helper.*;

public class VersionMenuDropDownButtonTest {

    private VersionMenuDropDownButtonView view;
    private VersionMenuDropDownButton button;

    @Before
    public void setUp() throws Exception {
        view = mock( VersionMenuDropDownButtonView.class );

        button = new VersionMenuDropDownButton( view );
    }

    @Test
    public void testPresenterSet() throws Exception {
        verify( view ).setPresenter( button );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoVersion() throws Exception {
        button.setItems( new ArrayList<VersionRecord>() );

        button.onMenuOpening();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoVersionRecords() throws Exception {
        button.setVersion( "111" );

        button.onMenuOpening();
    }

    @Test
    public void testVersionChange() throws Exception {
        ArrayList<VersionRecord> versionRecords = new ArrayList<VersionRecord>();

        versionRecords.add( getVersionRecord( "1111" ) );
        versionRecords.add( getVersionRecord( "2222" ) );
        versionRecords.add( getVersionRecord( "3333" ) );

        button.setItems( versionRecords );
        button.setVersion( "3333" );

        button.onMenuOpening();

        verify( view ).addLabel( eq( versionRecords.get( 2 ) ), eq( true ), eq( 3 ) );
        verify( view ).addLabel( eq( versionRecords.get( 1 ) ), eq( false ), eq( 2 ) );
        verify( view ).addLabel( eq( versionRecords.get( 0 ) ), eq( false ), eq( 1 ) );

        button.setVersion( "2222" );

        button.onMenuOpening();

        verify( view ).addLabel( eq( versionRecords.get( 2 ) ), eq( false ), eq( 3 ) );
        verify( view ).addLabel( eq( versionRecords.get( 1 ) ), eq( true ), eq( 2 ) );
        verify( view, times( 2 ) ).addLabel( eq( versionRecords.get( 0 ) ), eq( false ), eq( 1 ) );
    }

    @Test
    public void testItemsCanNotBeSetToLessThanBefore() throws Exception {
        ArrayList<VersionRecord> versions = new ArrayList<VersionRecord>();
        versions.add( getVersionRecord( "1111" ) );
        versions.add( getVersionRecord( "2222" ) );

        button.setItems( versions );
        button.setVersion( "1111" );

        button.onMenuOpening();
        verify( view, times( 2 ) ).addLabel( any( VersionRecord.class ), anyBoolean(), anyInt() );

        ArrayList<VersionRecord> versions2 = new ArrayList<VersionRecord>();
        versions2.add( getVersionRecord( "1111" ) );

        button.setItems( versions2 );

        button.onMenuOpening();
        verify( view, times( 4 ) ).addLabel( any( VersionRecord.class ), anyBoolean(), anyInt() );

    }

    @Test
    public void testNewVersionAdded() throws Exception {
        ArrayList<VersionRecord> versionRecords = new ArrayList<VersionRecord>();

        versionRecords.add( getVersionRecord( "1111" ) );
        versionRecords.add( getVersionRecord( "2222" ) );
        versionRecords.add( getVersionRecord( "3333" ) );

        button.setItems( versionRecords );
        button.setVersion( "3333" );

        button.onMenuOpening();

        verify( view ).addLabel( eq( versionRecords.get( 2 ) ), eq( true ), eq( 3 ) );
        verify( view ).addLabel( eq( versionRecords.get( 1 ) ), eq( false ), eq( 2 ) );
        verify( view ).addLabel( eq( versionRecords.get( 0 ) ), eq( false ), eq( 1 ) );

        ArrayList<VersionRecord> versionRecords2 = new ArrayList<VersionRecord>();

        versionRecords2.add( getVersionRecord( "1111" ) );
        versionRecords2.add( getVersionRecord( "2222" ) );
        versionRecords2.add( getVersionRecord( "3333" ) );
        versionRecords2.add( getVersionRecord( "4444" ) );

        button.setItems( versionRecords2 );
        button.setVersion( "4444" );

        button.onMenuOpening();

        verify( view ).addLabel( eq( versionRecords2.get( 3 ) ), eq( true ), eq( 4 ) );
        verify( view ).addLabel( eq( versionRecords2.get( 2 ) ), eq( false ), eq( 3 ) );
        verify( view ).addLabel( eq( versionRecords2.get( 1 ) ), eq( false ), eq( 2 ) );
        verify( view ).addLabel( eq( versionRecords2.get( 0 ) ), eq( false ), eq( 1 ) );
    }

    @Test
    public void testShowMoreCount() {

        ArrayList<VersionRecord> versionRecords = new ArrayList<VersionRecord>();

        versionRecords.add( getVersionRecord( "1111" ) );
        versionRecords.add( getVersionRecord( "2222" ) );
        versionRecords.add( getVersionRecord( "3333" ) );
        versionRecords.add( getVersionRecord( "4444" ) );
        versionRecords.add( getVersionRecord( "5555" ) );
        versionRecords.add( getVersionRecord( "6666" ) );
        versionRecords.add( getVersionRecord( "7777" ) );
        versionRecords.add( getVersionRecord( "8888" ) );
        versionRecords.add( getVersionRecord( "9999" ) );

        button.setItems( versionRecords );
        button.setVersion( "3333" );

        button.onMenuOpening();

        //the last 7 elements are shown in the UI
        verify( view ).addLabel( eq( versionRecords.get( 8 ) ), eq( false ), eq( 9 ) );
        verify( view ).addLabel( eq( versionRecords.get( 7 ) ), eq( false ), eq( 8 ) );
        verify( view ).addLabel( eq( versionRecords.get( 6 ) ), eq( false ), eq( 7 ) );
        verify( view ).addLabel( eq( versionRecords.get( 5 ) ), eq( false ), eq( 6 ) );
        verify( view ).addLabel( eq( versionRecords.get( 4 ) ), eq( false ), eq( 5 ) );
        verify( view ).addLabel( eq( versionRecords.get( 3 ) ), eq( false ), eq( 4 ) );
        verify( view ).addLabel( eq( versionRecords.get( 2 ) ), eq( true ), eq( 3 ) );


        //so a count of 9 - 7 = 2 should be shown in the show more label
        verify( view ).addViewAllLabel( eq( 2 ), any( Command.class ) );

    }


}
