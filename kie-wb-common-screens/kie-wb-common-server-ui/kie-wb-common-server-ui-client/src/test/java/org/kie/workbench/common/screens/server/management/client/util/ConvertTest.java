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

package org.kie.workbench.common.screens.server.management.client.util;

import java.util.ArrayList;

import org.junit.Test;
import org.kie.server.api.model.Message;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;

import static org.junit.Assert.*;

public class ConvertTest {

    @Test
    public void testToKey() {
        final String serverTemplateId = "serverTemplateId";
        final String serverName = "serverName";
        final String serverInstanceId = "serverInstanceId";
        final String url = "url";

        ServerInstance serverInstance = new ServerInstance( serverTemplateId, serverName, serverInstanceId, url, "version", new ArrayList<Message>(), new ArrayList<Container>() );

        ServerInstanceKey key = Convert.toKey( serverInstance );

        assertEquals( serverTemplateId, key.getServerTemplateId() );
        assertEquals( serverName, key.getServerName() );
        assertEquals( serverInstanceId, key.getServerInstanceId() );
        assertEquals( url, key.getUrl() );
    }
}
