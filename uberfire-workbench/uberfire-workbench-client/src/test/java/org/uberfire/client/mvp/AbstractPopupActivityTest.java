/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.mvp;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.widgets.popup.PopupView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractPopupActivityTest extends AbstractActivityTest {

    @Mock IsWidget popupWidget;
    @Mock PlaceManager placeManager;
    @Mock PopupView popupView;

    TestingPopupActivity popupActivity;

    /**
     * When popupActivity.open() is called, the CloseHandler that it registers on the mock PopupView will be captured
     * here.
     */
    CloseHandler<PopupView> registeredCloseHandler;

    /**
     * The HandlerRegistration that the mock PopupView returned to the Activity.
     */
    HandlerRegistration closeHandlerRegistration;

    /**
     * The place given to popupActivity when it was started by the setup method.
     */
    private PlaceRequest popupPlace;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when( popupView.addCloseHandler( any( CloseHandler.class ) ) ).thenAnswer( new Answer<HandlerRegistration>() {
            @Override
            public HandlerRegistration answer( InvocationOnMock invocation ) throws Throwable {
                registeredCloseHandler = (CloseHandler<PopupView>) invocation.getArguments()[0];
                closeHandlerRegistration = mock( HandlerRegistration.class );
                return closeHandlerRegistration;
            }
        } );
        popupActivity = new TestingPopupActivity( placeManager, popupView );
        popupPlace = new DefaultPlaceRequest( "PopupPlace" );
    }

    @Override
    public Activity getActivityUnderTest() {
        return popupActivity;
    }

    @Test
    public void shouldShowViewInOnOpen() throws Exception {
        popupActivity.onStartup( popupPlace );
        popupActivity.onOpen();
        verify( popupView, times( 1 ) ).show();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUsePlaceManagerToCloseSelfOnViewClosedCallback() throws Exception {
        popupActivity.onStartup( popupPlace );
        popupActivity.onOpen();
        registeredCloseHandler.onClose( mock( CloseEvent.class ) );
        verify( placeManager, times( 1 ) ).closePlace( popupPlace );
    }

    @Test
    public void shouldCloseViewInOnClose() throws Exception {
        popupActivity.onStartup( popupPlace );
        popupActivity.onOpen();
        popupActivity.onClose();
        verify( popupView, times( 1 ) ).hide();
    }

    /**
     * Test to ensure we don't start an infinite loop when someone clicks the "X" in the popup header.
     * See also the complementary test, {@link #shouldNotCallCloseOnPlaceManagerWhenCloseOperationTriggeredByPlaceManager()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotCallHideOnViewWhenCloseOperationTriggeredByView() throws Exception {
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                // simulate is what PlaceManager will do in response to the close hook
                popupActivity.onClose();
                return null;
            }
        } ).when( placeManager ).closePlace( popupPlace );

        popupActivity.onStartup( popupPlace );
        popupActivity.onOpen();
        registeredCloseHandler.onClose( mock( CloseEvent.class ) );

        verify( placeManager, times( 1 ) ).closePlace( popupPlace );

        // we shouldn't call hide() on the popup, because this sequence of events
        // was triggered by the fact that it was already hiding itself
        verify( popupView, never() ).hide();
    }

    /**
     * Test to ensure we don't start an infinite loop when the PlaceManager closes the activity.
     * See also the complementary case, {@link #shouldNotCallHideOnViewWhenCloseOperationTriggeredByView()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotCallCloseOnPlaceManagerWhenCloseOperationTriggeredByPlaceManager() throws Exception {
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                // simulate is what PopupView will do in response to hide()
                registeredCloseHandler.onClose( mock( CloseEvent.class ) );
                return null;
            }
        } ).when( popupView ).hide();

        popupActivity.onStartup( popupPlace );
        popupActivity.onOpen();
        popupActivity.onClose();

        verify( popupView ).hide();

        // PlaceManager initiated this operation, so we mustn't call back into it
        verify( placeManager, never() ).closePlace( popupPlace );
    }

    private final class TestingPopupActivity extends AbstractPopupActivity {

        public TestingPopupActivity( PlaceManager placeManager,
                                     PopupView popupView ) {
            super( placeManager, popupView );
        }

        @Override
        public Collection<String> getTraits() {
            return Collections.emptyList();
        }

        @Override
        public String getSignatureId() {
            return "fake.popup.Activity";
        }

        @Override
        public Collection<String> getRoles() {
            return Collections.emptyList();
        }

        @Override
        public IsWidget getWidget() {
            return popupWidget;
        }

        @Override
        public String getTitle() {
            return "Testing Popup Activity";
        }

        @Override
        public String getIdentifier() {
            return "fake.popup.Activity";
        }
    }

}
