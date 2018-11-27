/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.file.extensions;

import java.util.HashMap;
import java.util.Map;

/**
 * Execution context for a {@link FileSystemHooks.FileSystemHook}. It contains the relevant information for the Hook execution
 */
public class FileSystemHookExecutionContext {

    private String fsName;

    private Map<String, Object> params = new HashMap<>();

    public FileSystemHookExecutionContext(String fsName) {
        this.fsName = fsName;
    }

    /**
     *  Returns the fileSystem name that this hook executes
     */
    public String getFsName() {
        return fsName;
    }

    /**
     * Adds a param to the context
     * @param name the name of the param
     * @param value
     */
    public void addParam(String name, Object value) {
        params.put(name, value);
    }

    /**
     * Gets a param value
     * @param name the name of the param
     * @return the param value
     */
    public Object getParamValue(String name) {
        return params.get(name);
    }
}
