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

package org.kie.workbench.common.screens.social.hp.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.social.hp.config.SocialConfigurationService;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class SocialHomePageEntryPointTest {

    @Mock
    private SocialConfigurationService socialConfigurationService;
    private Caller<SocialConfigurationService> socialConfigurationServiceCaller;

    private SocialHomePageEntryPoint entryPoint;

    @Before
    public void setup() {
        socialConfigurationServiceCaller = new CallerMock<>( socialConfigurationService );
        entryPoint = new SocialHomePageEntryPoint( socialConfigurationServiceCaller );
    }

    @Test
    public void registerMessages() {
        Map<String, String> messagesByKey = new HashMap<>();
        messagesByKey.put( "added", "Added" );
        messagesByKey.put( "created", "Created" );
        messagesByKey.put( "edited", "Edited" );

        entryPoint.startApp();

        verify( socialConfigurationService ).registerSocialMessages( messagesByKey );
    }

}
