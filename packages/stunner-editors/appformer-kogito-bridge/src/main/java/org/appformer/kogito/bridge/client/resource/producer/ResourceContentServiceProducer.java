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


package org.appformer.kogito.bridge.client.resource.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import elemental2.dom.DomGlobal;
import org.appformer.kogito.bridge.client.interop.WindowRef;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.impl.EnvelopeResourceContentService;
import org.appformer.kogito.bridge.client.resource.impl.NoOpResourceContentService;

/**
 * Produces {@link ResourceContentService} beans according to whether the envelope API is available or not
 *
 */
public class ResourceContentServiceProducer {

    @Produces
    @ApplicationScoped
    public ResourceContentService produce() {
        if (WindowRef.isEnvelopeAvailable()) {
            return new EnvelopeResourceContentService();
        }
        DomGlobal.console.info("[ResourceContentServiceProducer] Envelope API is not available. Producing NoOpResourceContentService");
        return new NoOpResourceContentService();
    }

}
