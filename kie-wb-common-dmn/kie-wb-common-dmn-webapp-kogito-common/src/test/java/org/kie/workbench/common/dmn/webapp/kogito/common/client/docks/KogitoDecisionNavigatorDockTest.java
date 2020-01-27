/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.webapp.kogito.common.client.docks;

import org.appformer.kogito.bridge.client.context.EditorContextProvider;
import org.appformer.kogito.bridge.client.context.KogitoChannel;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class KogitoDecisionNavigatorDockTest extends BaseKogitoDockTest<KogitoDecisionNavigatorDock> {

    @Mock
    private EditorContextProvider context;

    @Before
    @Override
    public void setup() {
        when(context.getChannel()).thenReturn(KogitoChannel.DEFAULT);

        super.setup();
    }

    @Override
    protected KogitoDecisionNavigatorDock makeDock() {
        return new KogitoDecisionNavigatorDock(uberfireDocks,
                                               decisionNavigatorPresenter,
                                               translationService,
                                               context);
    }

    @Override
    protected UberfireDockPosition position() {
        return UberfireDockPosition.WEST;
    }

    @Override
    protected String screen() {
        return DecisionNavigatorPresenter.IDENTIFIER;
    }

    @Test
    public void testDefaultChannelPosition() {
        when(context.getChannel()).thenReturn(KogitoChannel.DEFAULT);

        assertEquals(UberfireDockPosition.WEST, dock.position());
    }

    @Test
    public void testOnlineChannelPosition() {
        when(context.getChannel()).thenReturn(KogitoChannel.ONLINE);

        assertEquals(UberfireDockPosition.WEST, dock.position());
    }

    @Test
    public void testVSCodeChannelPosition() {
        when(context.getChannel()).thenReturn(KogitoChannel.VSCODE);

        assertEquals(UberfireDockPosition.EAST, dock.position());
    }

    @Test
    public void testGitHubChannelPosition() {
        when(context.getChannel()).thenReturn(KogitoChannel.GITHUB);

        assertEquals(UberfireDockPosition.EAST, dock.position());
    }
}
