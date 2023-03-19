/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.external.impl;

import java.util.Map;

/**
 * Server side component functions contract.
 *
 * @param <T>
 * The function return type.
 */
public interface BackendComponentFunction<T> {

    default String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 
     * The function execution. Must return an object that can be used in browser windows communication. 
     * @param params
     * Params set by user when configuring the component.
     * @return
     * The result
     * 
     */
    T exec(Map<String, Object> params);

}