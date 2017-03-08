/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.gwttest;

import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.main.DefaultScreenActivity;
import org.uberfire.wbtest.client.panels.docking.NestingScreen;
import org.uberfire.wbtest.client.perspective.MultiPanelPerspective;

import static org.uberfire.wbtest.testutil.TestingPredicates.*;

public class ScreenLifecycleTest extends AbstractUberFireGwtTest {

    public void testScreenActivityOnlyCreatedOneTimeOnPerspectiveLoad() throws Exception {
        final PlaceManager placeManager = IOC.getBeanManager().lookupBean(PlaceManager.class).getInstance();

        DefaultScreenActivity.instanceCount = 0;

        pollWhile(DEFAULT_SCREEN_NOT_LOADED)
                .thenDo(new Runnable() {
                    @Override
                    public void run() {
                        assertEquals(1,
                                     DefaultScreenActivity.instanceCount);
                        placeManager.goTo(new DefaultPlaceRequest(MultiPanelPerspective.class.getName()));
                    }
                })
                .thenPollWhile(NESTING_SCREEN_NOT_LOADED)
                .thenDo(new Runnable() {
                    @Override
                    public void run() {
                        assertEquals(1,
                                     NestingScreen.instanceCount);
                    }
                });
    }
}
