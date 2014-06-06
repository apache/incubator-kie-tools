/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.mvp;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Event;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.workbench.events.PanelFocusEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.client.workbench.pmgr.nswe.NSWEExtendedBeanFactory;
import org.uberfire.client.workbench.pmgr.nswe.NSWEPanelManager;
import org.uberfire.client.workbench.widgets.statusbar.WorkbenchStatusBarPresenter;

/**
 * Base class for tests requiring a dummy PanelManager
 */
public abstract class BasePanelManagerTest {

    @Mock protected PlaceHistoryHandler placeHistoryHandler;
    @Mock protected ActivityManager activityManager;
    @Spy protected PlaceManagerImpl placeManager = new PlaceManagerImpl();
    @Spy protected NSWEExtendedBeanFactory factory = new MockBeanFactory();
    @Mock protected StubPlaceGainFocusEvent placeGainFocusEvent;
    @Mock protected StubPlaceLostFocusEvent placeLostFocusEvent;
    @Mock protected StubSelectPlaceEvent selectPlaceEvent;
    @Mock protected StubPanelFocusEvent panelFocusEvent;
    @Mock protected WorkbenchStatusBarPresenter statusBar;
    @Mock protected SimpleWorkbenchPanelPresenter workbenchPanelPresenter;

    @InjectMocks protected NSWEPanelManager panelManager;

    /**
     * Mockito fails to produce a valid mock for a raw {@code Event<Anything>} due to classloader issues. Trivial
     * subclasses of this class provide Mockito something that it can mock successfully and inject into our
     * {@code @InjectMocks} object.
     */
    static class StubEventSource<T> implements Event<T> {

        @Override
        public void fire( T event ) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public Event<T> select( Annotation... qualifiers ) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public <U extends T> Event<U> select( Class<U> subtype, Annotation... qualifiers ) {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }

    static class StubPlaceGainFocusEvent extends StubEventSource<PlaceGainFocusEvent> {}
    static class StubPlaceLostFocusEvent extends StubEventSource<PlaceLostFocusEvent> {}
    static class StubSelectPlaceEvent extends StubEventSource<SelectPlaceEvent> {}
    static class StubPanelFocusEvent extends StubEventSource<PanelFocusEvent> {}

}
