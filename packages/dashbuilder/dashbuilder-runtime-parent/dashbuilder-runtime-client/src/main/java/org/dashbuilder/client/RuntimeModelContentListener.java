/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client;

import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.MessageEvent;
import jsinterop.base.Js;

@ApplicationScoped
public class RuntimeModelContentListener {

    private static final String READY = "ready";
    
    @Inject
    RuntimeCommunication runtimeCommunication;

    public void start(Consumer<String> contentConsumer) {
        DomGlobal.window.addEventListener("message", evt -> {
            MessageEvent<String> message = Js.cast(evt);
            try {
                if (!READY.equals(message.data)) {
                    contentConsumer.accept(message.data);
                    runtimeCommunication.showSuccess("Dashboard Updated");
                }
            } catch (Exception e) {
                runtimeCommunication.showWarning("Error loading content: " + e.getMessage(), e);
            }
        });
        if (DomGlobal.window.parent != null) {
            DomGlobal.window.parent.postMessage(READY, null);
        }
    }
}
