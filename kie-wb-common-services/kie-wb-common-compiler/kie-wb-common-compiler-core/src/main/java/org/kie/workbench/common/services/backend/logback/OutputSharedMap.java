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
package org.kie.workbench.common.services.backend.logback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * Map to hold the outputs of the compilations using the UUID key to retrieve and store
 */
public class OutputSharedMap {

    private static Map<String, List<String>> map = new ConcurrentHashMap();

    public static List<String> getLog(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return Collections.emptyList();
        }
    }

    public static void removeLog(String key) {
        map.remove(key);
    }

    public static void addMsgToLog(String key, String msg) {
        if (map.containsKey(key)) {
            map.get(key).add(msg);
        } else {
            map.put(key, new ArrayList<>());
            map.get(key).add(msg);
        }
    }

    public static void purgeAll(){
        map.clear();
    }
}
