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

package org.drools.workbench.screens.testscenario.client;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.widget.KSessionSelector;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.*;

public class ScenarioKSessionSelectorTest {

    private ScenarioKSessionSelector selector;
    private Path                     path;
    private KSessionSelector         innerSelector;

    @Before
    public void setUp() throws Exception {

        path = mock( Path.class );

        innerSelector = mock( KSessionSelector.class );

        selector = new ScenarioKSessionSelector( innerSelector );
    }

    @Test
    public void testNoSessionSet() throws Exception {
        selector.init( path, new Scenario() );

        verify( innerSelector ).init( path,
                                      null );
    }

    @Test
    public void testSetKBaseAndKSession() throws Exception {
        Scenario scenario = new Scenario();
        scenario.getKSessions().add( "ksession2" );
        selector.init( path, scenario );

        verify( innerSelector ).init( path,
                                      "ksession2" );
    }

}