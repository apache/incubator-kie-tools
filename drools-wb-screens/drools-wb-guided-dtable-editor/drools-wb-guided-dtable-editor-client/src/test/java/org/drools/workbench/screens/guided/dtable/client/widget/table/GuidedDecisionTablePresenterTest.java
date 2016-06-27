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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTablePresenterTest extends BaseGuidedDecisionTablePresenterTest {

    @Test
    public void testOnUpdatedLockStatusEvent_LockedByCurrentUser() {
        final UpdatedLockStatusEvent event = mock( UpdatedLockStatusEvent.class );
        when( event.getFile() ).thenReturn( dtPath );
        when( event.isLockedByCurrentUser() ).thenReturn( true );
        when( event.isLocked() ).thenReturn( true );

        dtPresenter.onUpdatedLockStatusEvent( event );

        verify( modellerPresenter,
                times( 1 ) ).onLockStatusUpdated( eq( dtPresenter ) );
        assertEquals( CURRENT_USER,
                      dtPresenter.getAccess().getLock() );
    }

    @Test
    public void testOnUpdatedLockStatusEvent_LockedByOtherUser() {
        final UpdatedLockStatusEvent event = mock( UpdatedLockStatusEvent.class );
        when( event.getFile() ).thenReturn( dtPath );
        when( event.isLockedByCurrentUser() ).thenReturn( false );
        when( event.isLocked() ).thenReturn( true );

        dtPresenter.onUpdatedLockStatusEvent( event );

        verify( modellerPresenter,
                times( 1 ) ).onLockStatusUpdated( eq( dtPresenter ) );
        assertEquals( OTHER_USER,
                      dtPresenter.getAccess().getLock() );
    }

    @Test
    public void testOnUpdatedLockStatusEvent_NotLocked() {
        final UpdatedLockStatusEvent event = mock( UpdatedLockStatusEvent.class );
        when( event.getFile() ).thenReturn( dtPath );
        dtPresenter.onUpdatedLockStatusEvent( event );

        verify( modellerPresenter,
                times( 1 ) ).onLockStatusUpdated( eq( dtPresenter ) );
        assertEquals( NOBODY,
                      dtPresenter.getAccess().getLock() );
    }

    @Test
    public void testOnUpdatedLockStatusEvent_NullFile() {
        final UpdatedLockStatusEvent event = mock( UpdatedLockStatusEvent.class );
        dtPresenter.onUpdatedLockStatusEvent( event );
    }

    @Test
    public void testActivate() {
        dtPresenter.activate();
        verify( lockManager,
                times( 1 ) ).fireChangeTitleEvent();
    }

}
