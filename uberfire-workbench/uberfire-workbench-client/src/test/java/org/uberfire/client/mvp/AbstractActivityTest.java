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
