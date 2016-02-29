/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Before;
import org.junit.Test;
import org.kie.internal.utils.KieMeta;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

import static org.junit.Assert.*;

public class KieMetaDataPreferencesLoaderTest {

    private KieMetaDataPreferencesLoader loader;

    @Before
    public void setup() {
        loader = new KieMetaDataPreferencesLoader();
    }

    @Test
    public void testLoad() {
        final Map<String, String> preferences = loader.load();
        assertNotNull( preferences );
        assertEquals( KieMeta.isProductized(),
                      Boolean.parseBoolean( preferences.get( ApplicationPreferences.KIE_PRODUCTIZED ) ) );
    }

}
