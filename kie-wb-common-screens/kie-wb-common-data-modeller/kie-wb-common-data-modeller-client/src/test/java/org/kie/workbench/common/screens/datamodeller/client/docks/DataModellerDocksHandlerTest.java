/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.docks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContextChangeEvent;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchFocusEvent;
import org.mockito.Mock;
import org.uberfire.mvp.Command;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.jgroups.util.Util.assertEquals;
import static org.jgroups.util.Util.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataModellerDocksHandlerTest {

    public static final String AUTHORING_PERSPECTIVE = "authoringPerspective";

    public static final int MIN_DOCKS = 3;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private DataModelerWorkbenchContext dataModelerWorkbenchContext;

    @Mock
    private Command command;

    private DataModelerContext originalContext = new DataModelerContext();

    private DataModellerDocksHandler handler;

    @Before
    public void init() {
        when(dataModelerWorkbenchContext.getActiveContext()).thenReturn(originalContext);

        handler = new DataModellerDocksHandler(sessionInfo,
                                               authorizationManager,
                                               dataModelerWorkbenchContext);

        handler.init(command);
    }

    @Test
    public void testSetDataModelerFocusEventWithPlanner() {
        testSetDataModelerFocusEvent(true,
                                     true);
    }

    @Test
    public void testSetDataModelerFocusEventWithoutPlanner() {
        testSetDataModelerFocusEvent(false,
                                     true);
    }

    @Test
    public void testLoseDataModelerFocusEventWithPlanner() {
        testSetDataModelerFocusEvent(true,
                                     false);
    }

    @Test
    public void testLoseDataModelerFocusEventWithoutPlanner() {
        testSetDataModelerFocusEvent(false,
                                     false);
    }

    protected void testSetDataModelerFocusEvent(final boolean withPlanner,
                                                final boolean setFocus) {
        when(authorizationManager.authorize(anyString(),
                                            any())).thenReturn(withPlanner);

        DataModelerWorkbenchFocusEvent event = new DataModelerWorkbenchFocusEvent();

        if (!setFocus) {
            event = event.lostFocus();
        }

        handler.onDataModelerWorkbenchFocusEvent(event);

        int maxDox = MIN_DOCKS;

        if (withPlanner) {
            maxDox++;
        }

        assertEquals(maxDox,
                     handler.provideDocks(AUTHORING_PERSPECTIVE).size());

        assertTrue(handler.shouldRefreshDocks());

        assertEquals(!setFocus,
                     handler.shouldDisableDocks());

        verify(command).execute();
    }

    @Test
    public void testChangeDataModelerContextSameContextSourceMode() {
        testSetDataModelerFocusEventWithPlanner();

        originalContext.setEditionMode(DataModelerContext.EditionMode.SOURCE_MODE);

        handler.onContextChange(new DataModelerWorkbenchContextChangeEvent());

        assertTrue(handler.shouldRefreshDocks());

        assertTrue(handler.shouldDisableDocks());

        verify(command,
               times(2)).execute();
    }

    @Test
    public void testChangeDataModelerContextDifferentContextSourceMode() {
        DataModelerContext newContext = new DataModelerContext();

        newContext.setEditionMode(DataModelerContext.EditionMode.SOURCE_MODE);

        testChangeDataModelerEvent(true,
                                   true,
                                   newContext);
    }

    @Test
    public void testChangeDataModelerContextDifferentContextGraphicMode() {
        testChangeDataModelerEvent(true,
                                   false,
                                   new DataModelerContext());
    }

    protected void testChangeDataModelerEvent(boolean shouldRefresh,
                                              boolean shouldDisable,
                                              DataModelerContext eventContext) {
        testSetDataModelerFocusEventWithPlanner();

        when(dataModelerWorkbenchContext.getActiveContext()).thenReturn(eventContext);

        handler.onContextChange(new DataModelerWorkbenchContextChangeEvent());

        assertEquals(shouldRefresh,
                     handler.shouldRefreshDocks());

        assertEquals(shouldDisable,
                     handler.shouldDisableDocks());

        verify(command,
               times(2)).execute();
    }
}
