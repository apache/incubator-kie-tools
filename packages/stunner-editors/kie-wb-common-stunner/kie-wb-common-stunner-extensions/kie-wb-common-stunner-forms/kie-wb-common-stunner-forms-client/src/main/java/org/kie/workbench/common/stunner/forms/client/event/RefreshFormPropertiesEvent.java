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

package org.kie.workbench.common.stunner.forms.client.event;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.uberfire.workbench.events.UberFireEvent;

public final class RefreshFormPropertiesEvent implements UberFireEvent {

    private final ClientSession session;
    private final String uuid;

    public RefreshFormPropertiesEvent(final ClientSession session) {
        this(session, null);
    }

    public RefreshFormPropertiesEvent(final ClientSession session,
                                      final String uuid) {
        this.session = session;
        this.uuid = uuid;
    }

    public boolean hasUuid() {
        return null != uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public ClientSession getSession() {
        return session;
    }
}
