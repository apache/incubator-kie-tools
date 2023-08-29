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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum Duration {
    MINUTE("minutes", "m"),
    HOUR("hours", "H"),
    DAYS("days", "D"),
    MONTHS("months", "M"),
    YEARS("years", "Y");

    private static final Map<String, Duration> ENUM_MAP;

    static {
        Map<String, Duration> map = new ConcurrentHashMap<>();
        for (Duration instance : Duration.values()) {
            map.put(instance.getAlias(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    private String type;

    private String alias;

    Duration(String type, String alias) {
        this.type = type;
        this.alias = alias;
    }

    public static Duration get(String name) {
        return ENUM_MAP.get(name);
    }

    public String getType() {
        return type;
    }

    public String getAlias() {
        return alias;
    }
}
