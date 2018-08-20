/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.offprocess.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;

/***
 * Map to hold the Response of the compilations using the UUID key to retrieve and store
 */
public class ResponseSharedMap {

    private Map<String, CompilationResponse> map;

    public ResponseSharedMap() {
        map = new ConcurrentHashMap<>();
    }

    public CompilationResponse getResponse(String key) {
        return map.getOrDefault(key, null);
    }

    public void removeResponse(String key) {
        map.remove(key);
    }

    public void addResponse(String key, CompilationResponse res) {
        if (!map.containsKey(key)) {
            map.put(key, res);
        }
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public void purgeAll() {
        map.clear();
    }
}
