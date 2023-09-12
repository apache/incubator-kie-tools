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


package org.appformer.kogito.bridge.client.resource.impl;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentEditorServiceWrapper;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentOptions;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;

/**
 *  A {@link ResourceContentService} implementation used when envelope API is available
 *
 */
public class EnvelopeResourceContentService implements ResourceContentService {

    @Override
    public Promise<String> get(String uri) {
        return ResourceContentEditorServiceWrapper.get().get(uri);
    }

    @Override
    public Promise<String[]> list(String pattern) {
        return ResourceContentEditorServiceWrapper.get().list(pattern);
    }

    @Override
    public Promise<String> get(String uri, ResourceContentOptions options) {
        return ResourceContentEditorServiceWrapper.get().get(uri, options);
    }

    @Override
    public Promise<String[]> list(String pattern, ResourceListOptions options) {
        return ResourceContentEditorServiceWrapper.get().list(pattern, options);
    }
}
