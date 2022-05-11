/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.setup;

import java.util.Arrays;

public enum RuntimeClientMode {

    /**
     * When Dashbuilder is used only as a editor 
     */
    EDITOR,
    /**
     * When Dashbuilder is used on client side with YML/JSON definitions
     */
    CLIENT,
    /**
     * Legacy mode with a backend
     */
    APP;

    public static RuntimeClientMode getOrDefault(String value) {
        if (value == null || value.trim().isEmpty()) {
            return APP;
        }
        return Arrays.stream(RuntimeClientMode.values())
                .filter(n -> n.name().equalsIgnoreCase(value))
                .findFirst().orElse(APP);
    }
}
