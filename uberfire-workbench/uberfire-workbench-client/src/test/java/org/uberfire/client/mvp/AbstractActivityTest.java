package org.uberfire.client.mvp;

import org.junit.Test;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

/**
 * Base class for testing Activity implementations. All Activity unit tests should extend this one; it tests the Activity contract.
 */
public abstract class AbstractActivityTest {

    /** Subclasses should implement this method to return the object they are unit testing. */
    public abstract Activity getActivityUnderTest();

    @Test(expected = IllegalStateException.class)
    public void onOpenShouldFailWhenActivityNotStarted() {
        getActivityUnderTest().onOpen();
    }

    @Test
    public void onOpenShouldSucceedWhenActivityStarted() throws Exception {
        Activity a = getActivityUnderTest();
        a.onStartup( new DefaultPlaceRequest( "testplace" ) );
        a.onOpen();
    }

    @Test(expected = IllegalStateException.class)
    public void onOpenShouldFailWhenActivityAlreadyOpen() {
        Activity a = getActivityUnderTest();
        a.onStartup( new DefaultPlaceRequest( "testplace" ) );
        a.onOpen();
        a.onOpen();
    }

    @Test
    public void onShutdownShouldSucceedWhenActivityNeverOpened() throws Exception {
        Activity a = getActivityUnderTest();
        a.onStartup( new DefaultPlaceRequest( "testplace" ) );
        a.onShutdown();
    }

    @Test
    public void onCloseShouldSucceedWhenActivityOpened() throws Exception {
        Activity a = getActivityUnderTest();
        a.onStartup( new DefaultPlaceRequest( "testplace" ) );
        a.onOpen();
        a.onClose();
    }

    @Test(expected = IllegalStateException.class)
    public void onCloseShouldFailWhenActivityNotStarted() {
        getActivityUnderTest().onClose();
    }

    @Test(expected = IllegalStateException.class)
    public void onCloseShouldFailWhenActivityNotOpen() {
        Activity a = getActivityUnderTest();
        a.onStartup( new DefaultPlaceRequest( "testplace" ) );
        a.onClose();
    }

    @Test(expected = IllegalStateException.class)
    public void onCloseShouldFailWhenActivityAlreadyClosed() {
        Activity a = getActivityUnderTest();
        a.onStartup( new DefaultPlaceRequest( "testplace" ) );
        a.onOpen();
        a.onClose();
        a.onClose();
    }

    @Test(expected = IllegalStateException.class)
    public void onShutdownShouldFailWhenActivityNotStarted() {
        getActivityUnderTest().onShutdown();
    }

    @Test(expected = IllegalStateException.class)
    public void onShutdownShouldFailWhenActivityOpen() {
        Activity a = getActivityUnderTest();
        a.onStartup( new DefaultPlaceRequest( "testplace" ) );
        a.onOpen();
        a.onShutdown();
    }

    @Test
    public void fullLifecycleShouldSucceed() throws Exception {
        Activity a = getActivityUnderTest();
        a.onStartup( new DefaultPlaceRequest( "testplace" ) );
        a.onOpen();
        a.onClose();
        a.onShutdown();
    }

}
