/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.server.annotations.Service;

@ApplicationScoped @Service
public class IsDirtyService implements MessageCallback {

    private static final Map<String, Boolean> DIRTY = new ConcurrentHashMap<String, Boolean>();

    @Override
    public void callback(final Message message) {
        System.out.println("IsDirtyService::callback::OK");
        DIRTY.put(message.get(String.class, "id"), message.get(Boolean.class, "is_dirty"));
    }

    public Boolean isDirty(final String id) {
        return DIRTY.get(id);
    }

}
