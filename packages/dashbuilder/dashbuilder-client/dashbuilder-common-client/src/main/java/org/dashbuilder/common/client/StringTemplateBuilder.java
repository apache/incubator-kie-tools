/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.common.client;

import java.util.HashSet;
import java.util.Set;

public class StringTemplateBuilder {

    protected String keyPrefix;
    protected String keySufix;
    protected Set<String> keySet = new HashSet<>();
    protected String sourceCode;

    public StringTemplateBuilder() {
        this("${", "}");
    }

    public StringTemplateBuilder(String keyPrefix, String keySufix) {
        this.keyPrefix = keyPrefix;
        this.keySufix = keySufix;
    }

    public void setTemplate(String template) {
        this.sourceCode = template;
        this.extractKeys();
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getKeySufix() {
        return keySufix;
    }

    public void setKeySufix(String keySufix) {
        this.keySufix = keySufix;
    }

    public String build() {
        return sourceCode;
    }

    public Set<String> keys() {
        return keySet;
    }

    public String asVar(String key) {
        return getKeyPrefix() + key + getKeySufix();
    }

    public StringTemplateBuilder replace(String key, String value) {
        sourceCode = sourceCode.replace(keyPrefix + key + keySufix, value);
        return this;
    }

    protected void extractKeys() {
        this.keySet.clear();

        if (sourceCode != null) {
            int idx = 0;
            int end = 0;
            while (idx != -1 && end != -1) {
                idx = sourceCode.indexOf(keyPrefix, end);
                end = sourceCode.indexOf(keySufix, idx+keyPrefix.length());
                if (idx != -1 && end != -1) {
                    String key = sourceCode.substring(idx + 2, end);
                    keySet.add(key);
                }
            }
        }
    }
}
