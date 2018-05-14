/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.preferences;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.preferences.StunnerDiagramEditorPreferences;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CaseManagementPreferencesRegistryTest {

    private CaseManagementPreferencesRegistry tested;

    @Before
    public void setUp() throws Exception {
        tested = new CaseManagementPreferencesRegistry();
    }

    @Test
    public void testSetTheRightSettings() {
        final StunnerPreferences preferences = mock(StunnerPreferences.class);
        final StunnerDiagramEditorPreferences editorPreferences = mock(StunnerDiagramEditorPreferences.class);
        when(preferences.getDiagramEditorPreferences()).thenReturn(editorPreferences);
        tested.set(preferences);
        assertEquals(preferences, tested.get());
        verify(editorPreferences, times(1)).setAutoHidePalettePanel(eq(true));
    }
}
