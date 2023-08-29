/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.webapp.kogito.common.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PatternFlyBootstrapper.class})
public class DMNKogitoCommonEntryPointTest {

    private DMNKogitoCommonEntryPoint entryPoint;

    @Before
    public void setup() {
        entryPoint = spy(new DMNKogitoCommonEntryPoint());
        doNothing().when(entryPoint).initializeLienzoCore();
    }

    @Test
    public void testInit() {

        PowerMockito.mockStatic(PatternFlyBootstrapper.class);

        entryPoint.init();

        PowerMockito.verifyStatic(PatternFlyBootstrapper.class);
        PatternFlyBootstrapper.ensureMomentIsAvailable();

        PowerMockito.verifyStatic(PatternFlyBootstrapper.class);
        PatternFlyBootstrapper.ensureMomentTimeZoneIsAvailable();

        // LienzoCore is final, thus it's not possible to verify LienzoCore#setHidpiEnabled.
        verify(entryPoint).initializeLienzoCore();
    }
}
