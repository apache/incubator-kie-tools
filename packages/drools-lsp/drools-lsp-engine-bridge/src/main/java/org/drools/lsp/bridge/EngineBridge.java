/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.lsp.bridge;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;

/**
 * Compiles DRL sources with whatever Drools engine this class's own class loader
 * can see and reports the engine's messages as plain JDK values, so a caller
 * that holds this class through a different class loader can read the result
 * without sharing any engine type with it.
 */
public final class EngineBridge {

    public static final String PATH = "path";
    public static final String LEVEL = "level";
    public static final String LINE = "line";
    public static final String COLUMN = "column";
    public static final String TEXT = "text";

    private EngineBridge() {
    }

    public static List<Map<String, Object>> build(Map<String, String> drlByPath) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        for (Map.Entry<String, String> entry : drlByPath.entrySet()) {
            kfs.write(entry.getKey(), ks.getResources()
                    .newByteArrayResource(entry.getValue().getBytes(StandardCharsets.UTF_8))
                    .setResourceType(ResourceType.DRL));
        }
        KieBuilder builder = ks.newKieBuilder(kfs, EngineBridge.class.getClassLoader());
        builder.buildAll();

        List<Map<String, Object>> out = new ArrayList<>();
        Results results = builder.getResults();
        if (results == null || results.getMessages() == null) {
            return out;
        }
        for (Message message : results.getMessages()) {
            if (message == null) {
                continue;
            }
            Map<String, Object> m = new LinkedHashMap<>();
            m.put(PATH, message.getPath());
            m.put(LEVEL, message.getLevel() == null ? "WARNING" : message.getLevel().name());
            m.put(LINE, message.getLine());
            m.put(COLUMN, message.getColumn());
            m.put(TEXT, message.getText());
            out.add(m);
        }
        return out;
    }
}
