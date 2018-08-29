/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.preferences;

import java.util.Map;
import java.util.TimeZone;

import org.gwtbootstrap3.client.ui.Modal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.KIE_TIMEZONE_OFFSET;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({KieTimeZonePreferencesLoader.class, TimeZone.class})
@RunWith(PowerMockRunner.class)
public class KieTimeZonePreferencesLoaderTest {

    private KieTimeZonePreferencesLoader loader;

    @Before
    public void setup() {
        loader = spy(new KieTimeZonePreferencesLoader());
    }

    @Test
    public void testGetTimeZoneWhenTimeZoneIsHST() {
        doReturn("HST").when(loader).getSystemPropertyTimeZone();

        assertEquals("-36000000", getLoaderOffset());
    }

    @Test
    public void testGetTimeZoneWhenTimeZoneIsAmericaSaoPaulo() {
        doReturn("America/Sao_Paulo").when(loader).getSystemPropertyTimeZone();

        assertEquals("-10800000", getLoaderOffset());
    }

    @Test
    public void testGetTimeZoneWhenTimeZoneIsNotSet() {
        doReturn("").when(loader).getSystemPropertyTimeZone();

        final TimeZone timeZone = mock(TimeZone.class);
        final int expectedOffset = 36000000;

        mockStatic(TimeZone.class);
        when(TimeZone.getDefault()).thenReturn(timeZone);
        when(timeZone.getOffset(anyInt())).thenReturn(expectedOffset);

        assertEquals(String.valueOf(expectedOffset), getLoaderOffset());
    }

    private String getLoaderOffset() {
        final Map<String, String> preferences = loader.load();
        return preferences.get(KIE_TIMEZONE_OFFSET);
    }
}
