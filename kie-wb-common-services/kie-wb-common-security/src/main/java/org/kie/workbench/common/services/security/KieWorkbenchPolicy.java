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
package org.kie.workbench.common.services.security;

import java.util.HashMap;

public class KieWorkbenchPolicy extends HashMap<String,String> {

    public KieWorkbenchPolicy() {
        super();
    }

    public KieWorkbenchPolicy(String policy) {
        this();
        build(policy);
    }

    public void build(String policy) {

        String[] lines = policy.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("#") || line.length() < 4) continue;

            String[] row = line.split("=");
            put(row[0].trim(), row[1].trim());
        }
    }
}