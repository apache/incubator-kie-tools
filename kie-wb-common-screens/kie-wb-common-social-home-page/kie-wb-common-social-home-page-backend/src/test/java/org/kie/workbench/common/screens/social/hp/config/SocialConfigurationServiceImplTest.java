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

package org.kie.workbench.common.screens.social.hp.config;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.uberfire.social.activities.server.SocialConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jgroups.util.Util.*;

@RunWith( MockitoJUnitRunner.class )
public class SocialConfigurationServiceImplTest {

    @Mock
    private SocialConfiguration socialConfiguration;

    @InjectMocks
    private SocialConfigurationServiceImpl service;

    @Test
    public void registerMessages() {
        Map<String, String> messagesByKey = new HashMap<>();
        messagesByKey.put( "added", "message-added" );
        messagesByKey.put( "created", "message-created" );
        messagesByKey.put( "edited", "message-edited" );

        service.registerSocialMessages( messagesByKey );

        final Map<String, String> registeredMessagesByKey = service.getSocialMessages();

        assertEquals( messagesByKey, registeredMessagesByKey );
    }
}
