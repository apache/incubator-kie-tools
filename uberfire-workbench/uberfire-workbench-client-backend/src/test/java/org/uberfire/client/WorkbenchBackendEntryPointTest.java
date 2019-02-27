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

package org.uberfire.client;

import java.util.stream.IntStream;

import org.jboss.errai.bus.client.api.BusLifecycleEvent;
import org.jboss.errai.bus.client.api.BusLifecycleListener;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.api.TransportError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchBackendEntryPointTest {

    @Mock
    private Logger logger;

    @Mock
    private ClientMessageBus bus;

    @Mock
    private WorkbenchServicesProxy workbenchServices;

    @Mock
    private ErrorPopupPresenter errorPopupPresenter;

    @Test
    public void testErrorDisplay() {
        final WorkbenchBackendEntryPoint workbenchBackendEntryPoint = spy(new WorkbenchBackendEntryPoint(logger,
                                                                                                         bus,
                                                                                                         workbenchServices,
                                                                                                         errorPopupPresenter));

        doAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            final Command afterShow = (Command) args[1];
            afterShow.execute();
            return null;
        }).when(errorPopupPresenter).showMessage(anyString(),
                                                 any(),
                                                 any());

        doAnswer((Answer<Void>) invocation -> {
            final Object[] args = invocation.getArguments();
            final ParameterizedCommand<Boolean> obj = (ParameterizedCommand<Boolean>) args[0];
            obj.execute(false);
            return null;
        }).when(workbenchServices).isWorkbenchOnCluster(any(ParameterizedCommand.class));

        workbenchBackendEntryPoint.init();

        final ArgumentCaptor<BusLifecycleListener> captor = ArgumentCaptor.forClass(BusLifecycleListener.class);
        verify(bus).addLifecycleListener(captor.capture());

        final BusLifecycleListener listener = captor.getValue();
        final TransportError error = mock(TransportError.class);
        final BusLifecycleEvent event = new BusLifecycleEvent(bus,
                                                              error);

        assertTrue(workbenchBackendEntryPoint.hasMoreRetries());

        IntStream.range(0,
                        5).forEach(i -> listener.busOffline(event));

        verify(logger,
               times(5)).error((anyString()));
        verify(errorPopupPresenter,
               never()).showMessage(anyString());
        assertFalse(workbenchBackendEntryPoint.hasMoreRetries());

        assertFalse(workbenchBackendEntryPoint.isOpen());

        listener.busOffline(event);

        verify(logger,
               times(6)).error((anyString()));
        assertFalse(workbenchBackendEntryPoint.hasMoreRetries());
        verify(errorPopupPresenter,
               times(1)).showMessage(anyString(),
                                     any(),
                                     any());

        assertTrue(workbenchBackendEntryPoint.isOpen());

        listener.busOnline(event);

        verify(logger,
               times(1)).info((anyString()));
        verify(errorPopupPresenter,
               times(1)).showMessage(anyString(),
                                     any(),
                                     any());

        listener.busOffline(event);
        listener.busOffline(event);

        verify(logger,
               times(8)).error((anyString()));
        verify(errorPopupPresenter,
               times(1)).showMessage(anyString(),
                                     any(),
                                     any());

        IntStream.range(0,
                        5).forEach(i -> listener.busOffline(event));

        verify(errorPopupPresenter,
               times(1)).showMessage(anyString(),
                                     any(),
                                     any());
    }

    @Test
    public void testNoErrorDisplay() {
        final WorkbenchBackendEntryPoint workbenchBackendEntryPoint = new WorkbenchBackendEntryPoint(logger,
                                                                                                     bus,
                                                                                                     workbenchServices,
                                                                                                     errorPopupPresenter);

        doAnswer((Answer<Void>) invocation -> {
            final Object[] args = invocation.getArguments();
            final ParameterizedCommand<Boolean> obj = (ParameterizedCommand<Boolean>) args[0];
            obj.execute(true);
            return null;
        }).when(workbenchServices).isWorkbenchOnCluster(any(ParameterizedCommand.class));

        workbenchBackendEntryPoint.init();

        final ArgumentCaptor<BusLifecycleListener> captor = ArgumentCaptor.forClass(BusLifecycleListener.class);
        verify(bus).addLifecycleListener(captor.capture());

        final BusLifecycleListener listener = captor.getValue();
        final TransportError error = mock(TransportError.class);
        final BusLifecycleEvent event = new BusLifecycleEvent(bus,
                                                              error);
        listener.busOffline(event);

        verify(logger,
               times(1)).error((anyString()));
        verify(errorPopupPresenter,
               times(0)).showMessage(anyString());

        listener.busOffline(event);

        verify(logger,
               times(2)).error((anyString()));
        verify(errorPopupPresenter,
               times(0)).showMessage(anyString());

        listener.busOnline(event);

        verify(logger,
               times(1)).info((anyString()));
        verify(errorPopupPresenter,
               times(0)).showMessage(anyString());

        listener.busOffline(event);
        listener.busOffline(event);

        verify(logger,
               times(4)).error((anyString()));
        verify(errorPopupPresenter,
               times(0)).showMessage(anyString());
    }
}
