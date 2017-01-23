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

package org.uberfire.preferences.backend;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;

import static org.mockito.Mockito.*;

public class PreferenceBeanDefaultValueRecorderTest {

    private PreferenceBeanStore store;

    private PreferenceBeanDefaultValueRecorder defaultValueRecorder;

    @Before
    public void setup() {
        store = mock( PreferenceBeanStore.class );
        defaultValueRecorder = spy( PreferenceBeanDefaultValueRecorder.class );
    }

    @Test
    public void initializePreferenceValuesTest() {
        final MyPreferenceBeanGeneratedImpl myPreference = spy( new MyPreferenceBeanGeneratedImpl( store ) );
        final MyInnerPreferenceBeanGeneratedImpl myInnerPreference = spy( new MyInnerPreferenceBeanGeneratedImpl( store ) );

        final List<BasePreference<? extends BasePreference<? extends BasePreference<?>>>> preferences = Arrays.asList( myPreference, myInnerPreference );
        doReturn( preferences ).when( defaultValueRecorder ).getPreferences();

        defaultValueRecorder.initializePreferenceValues();

        verify( store, times( 1 ) ).saveDefaultValue( anyObject(), anyObject(), anyObject() );

        InOrder ordenatedVerification = inOrder( store, myPreference, myInnerPreference );
        ordenatedVerification.verify( myPreference ).saveDefaultValue();
        ordenatedVerification.verify( store ).saveDefaultValue( anyObject(), anyObject(), anyObject() );
        ordenatedVerification.verify( myInnerPreference ).saveDefaultValue();
    }

    @Test(expected = RuntimeException.class)
    public void initializePreferenceValuesWithInvalidDefaultValueInstanceTest() {
        final InvalidDefaultPreferenceBeanGeneratedImpl invalidDefaultPreference = spy( new InvalidDefaultPreferenceBeanGeneratedImpl( store ) );

        final List<BasePreference<? extends BasePreference<? extends BasePreference<?>>>> preferences = Arrays.asList( invalidDefaultPreference );
        doReturn( preferences ).when( defaultValueRecorder ).getPreferences();

        defaultValueRecorder.initializePreferenceValues();
    }
}
