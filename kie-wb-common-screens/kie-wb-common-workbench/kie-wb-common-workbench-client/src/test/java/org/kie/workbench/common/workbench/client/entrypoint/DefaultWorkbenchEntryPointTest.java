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

package org.kie.workbench.common.workbench.client.entrypoint;

import java.util.Map;

import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwtmockito.GwtMockitoTestRunner;
import junit.framework.Assert;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultWorkbenchEntryPointTest {

    private AppConfigService appConfigService;
    private CallerMock<AppConfigService> appConfigServiceCallerMock;

    private ActivityBeansCache activityBeansCache;

    private DefaultWorkbenchEntryPoint entryPoint;

    @Mock
    private Callback<String> callback1;

    @Mock
    private Callback<String> callback2;

    @Mock
    private DefaultWorkbenchErrorCallback defaultWorkbenchErrorCallback;

    @Before
    public void setup() {
        mockAppConfigService();
        mockActivityBeansCache();

        entryPoint = spy(new DefaultWorkbenchEntryPoint(appConfigServiceCallerMock,
                                                        activityBeansCache,
                                                        defaultWorkbenchErrorCallback) {
            @Override
            protected void setupMenu() {
            }
        });
        doNothing().when(entryPoint).hideLoadingPopup();
    }

    @Test
    public void startDefaultWorkbenchTest() {
        entryPoint.startDefaultWorkbench();

        verify(entryPoint).loadPreferences();
        verify(entryPoint).loadStyles();
    }

    @Test
    public void loadPreferencesTest() {
        entryPoint.loadPreferences();

        verify(entryPoint).setupMenu();
        verify(entryPoint).setupAdminPage();

        Assert.assertEquals("value",
                            ApplicationPreferences.getStringPref("key"));
    }

    private void mockActivityBeansCache() {
        activityBeansCache = mock(ActivityBeansCache.class);
    }

    private void mockAppConfigService() {
        appConfigService = mock(AppConfigService.class);
        Map<String, String> preferencesMap = new HashMap<>();
        preferencesMap.put("key",
                           "value");
        doReturn(preferencesMap).when(appConfigService).loadPreferences();
        appConfigServiceCallerMock = new CallerMock<>(appConfigService);
    }
}
