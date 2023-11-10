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


package org.appformer.kogito.bridge.client.context.impl;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import org.appformer.client.context.Channel;
import org.appformer.client.context.EditorContextProvider;
import org.appformer.client.context.OperatingSystem;
import org.appformer.kogito.bridge.client.context.interop.EditorContextWrapper;
import org.appformer.kogito.bridge.client.interop.WindowRef;

@Alternative
@ApplicationScoped
public class KogitoEditorContextProviderImpl implements EditorContextProvider {

    @Override
    public Channel getChannel() {
        if (WindowRef.isEnvelopeAvailable()) {
            final String channel = EditorContextWrapper.get().getChannel();
            if (channel != null) {
                return Channel.withName(channel);
            }
        }
        return Channel.DEFAULT;
    }

    @Override
    public Optional<OperatingSystem> getOperatingSystem() {
        if (WindowRef.isEnvelopeAvailable()) {
            final String os = EditorContextWrapper.get().getOperatingSystem();
            if (os != null) {
                return Optional.of(OperatingSystem.withName(os));
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isReadOnly() {
        if (WindowRef.isEnvelopeAvailable()) {
            return EditorContextWrapper.get().isReadOnly();
        }
        return false;
    }
}
