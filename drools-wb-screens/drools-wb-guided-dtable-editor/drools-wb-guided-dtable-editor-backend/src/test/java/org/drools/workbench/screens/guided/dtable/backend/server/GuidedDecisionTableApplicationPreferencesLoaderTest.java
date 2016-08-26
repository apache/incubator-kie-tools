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

package org.drools.workbench.screens.guided.dtable.backend.server;

import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class GuidedDecisionTableApplicationPreferencesLoaderTest {

    @After
    public void tearDown() throws Exception {
        System.clearProperty( GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED );
    }

    @Test
    public void notSet() throws Exception {

        assertEquals( "false", new GuidedDecisionTableApplicationPreferencesLoader().load().get( GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED ) );
    }

    @Test
    public void setTrue() throws Exception {
        System.setProperty( GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED, "true" );

        assertEquals( "true", new GuidedDecisionTableApplicationPreferencesLoader().load().get( GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED ) );
    }

    @Test
    public void setFalse() throws Exception {
        System.setProperty( GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED, "false" );

        assertEquals( "false", new GuidedDecisionTableApplicationPreferencesLoader().load().get( GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED ) );
    }
}