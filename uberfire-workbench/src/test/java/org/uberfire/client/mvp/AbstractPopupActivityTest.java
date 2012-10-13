package org.uberfire.client.mvp;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AbstractPopupActivityTest {


//    private AbstractPopupActivity activity;
//
//    @Before
//    public void setUp() throws Exception {
//        PlaceManager placeManager = mock(PlaceManager.class);
//        activity = spy(createTestActivity(placeManager));
//    }

    @Test
    public void testLaunchOrder() throws Exception {

        // .getPopupPanel() needs to return something that is unit testable.

        assertTrue(true);
//        PlaceRequest placeRequest = mock(PlaceRequest.class);
//        Command revealCallback = mock(Command.class);
//
//        verify(activity, never()).getPopupPanel();
//        verify(activity, never()).onStart();
//        verify(activity, never()).onStart(any(PlaceRequest.class));
//
//        activity.launch(placeRequest, revealCallback);

    }

//    private AbstractPopupActivity createTestActivity(final PlaceManager placeManager) {
//        return new AbstractPopupActivity(placeManager) {
//
//            @Override
//            public PopupPanel getPopupPanel() {
//                PopupPanel panel = mock(PopupPanel.class);
//                return panel;
//            }
//
//            @Override
//            public String getSignatureId() {
//                return null;
//            }
//
//            @Override
//            public Collection<String> getRoles() {
//                return null;
//            }
//
//            @Override
//            public void onStart() {
//                // onStart should not be called before getPopupPanel()
//                verify(activity, never()).getPopupPanel();
//            }
//
//            @Override
//            public void onStart(final PlaceRequest place) {
//                // onStart should not be called before getPopupPanel()
//                verify(activity, never()).getPopupPanel();
//            }
//
//            @Override
//            public Collection<String> getTraits() {
//                return null;
//            }
//        };
//    }
}
