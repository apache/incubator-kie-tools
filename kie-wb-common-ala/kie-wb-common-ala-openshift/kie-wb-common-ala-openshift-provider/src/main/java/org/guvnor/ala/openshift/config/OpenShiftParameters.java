/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.config;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * Compound parameterization for use in a single guvnor ala input string.
 */
@JsonIgnoreType
public class OpenShiftParameters extends LinkedHashMap<String, String> implements Map<String, String> {

    public static final String DEFAULT_PARAM_DELIMITER = ",";
    public static final String DEFAULT_PARAM_ASSIGNER = "=";

    private final String paramDelimiter;
    private final String paramAssigner;

    public OpenShiftParameters() {
        this(DEFAULT_PARAM_DELIMITER);
    }

    public OpenShiftParameters(String paramDelimiter) {
        this(paramDelimiter, DEFAULT_PARAM_ASSIGNER);
    }

    public OpenShiftParameters(String paramDelimiter, String paramAssigner) {
        this.paramDelimiter = paramDelimiter;
        this.paramAssigner = paramAssigner;
    }

    public String getParamDelimiter() {
        return paramDelimiter;
    }

    public String getParamAssigner() {
        return paramAssigner;
    }

    public OpenShiftParameters param(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException(String.format("param key [%s] and/or value [%s] is null", key, value));
        }
        put(key, value);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> iter = entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            sb.append(entry.getKey());
            sb.append(paramAssigner);
            sb.append(entry.getValue());
            if (iter.hasNext()) {
                sb.append(paramDelimiter);
            }
        }
        return sb.toString();
    }

    public static OpenShiftParameters fromString(String str) {
        return fromString(str, DEFAULT_PARAM_DELIMITER);
    }

    public static OpenShiftParameters fromString(String str, String paramDelimiter) {
        return fromString(str, paramDelimiter, DEFAULT_PARAM_ASSIGNER);
    }

    public static OpenShiftParameters fromString(String str, String paramDelimiter, String paramAssigner) {
        OpenShiftParameters params = new OpenShiftParameters(paramDelimiter, paramAssigner);
        if (str != null) {
            String[] splitAll = str.split(paramDelimiter);
            for (int i = 0; i < splitAll.length; i++) {
                int pos = splitAll[i].indexOf(paramAssigner);
                if (pos > 0) {
                    String key = splitAll[i].substring(0, pos).trim();
                    if (!key.isEmpty()) {
                        String value = splitAll[i].substring(pos + 1, splitAll[i].length());
                        params.put(key, value);
                    }
                }
            }
        }
        return params;
    }

    public static OpenShiftParameters fromRuntimeConfig(OpenShiftRuntimeConfig runtimeConfig) {
        String paramDelim = runtimeConfig.getResourceTemplateParamDelimiter();
        if (paramDelim == null || paramDelim.isEmpty()) {
            paramDelim = DEFAULT_PARAM_DELIMITER;
        }
        String paramAssign = runtimeConfig.getResourceTemplateParamAssigner();
        if (paramAssign == null || paramAssign.isEmpty()) {
            paramAssign = DEFAULT_PARAM_ASSIGNER;
        }
        String paramVals = runtimeConfig.getResourceTemplateParamValues();
        if (paramVals != null) {
            return fromString(paramVals, paramDelim, paramAssign);
        } else {
            return new OpenShiftParameters(paramDelim, paramAssign);
        }
    }

}
