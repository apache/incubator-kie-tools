/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.formatter;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ValueFormatterRegistry {

    Map<String,Map<String,ValueFormatter>> formatterMap = new HashMap<String, Map<String,ValueFormatter>>();
    public static final String _UNASSIGNED = "_unassigned";

    public void register(String columnId, ValueFormatter formatter) {
        Map<String,ValueFormatter> m = formatterMap.get(_UNASSIGNED);
        if (m == null) {
            m = new HashMap<String,ValueFormatter>();
            formatterMap.put(_UNASSIGNED, m);
        }
        m.put(columnId, formatter);
    }

    public void register(String displayerUuid, String columnId, ValueFormatter formatter) {
        Map<String,ValueFormatter> m = formatterMap.get(displayerUuid);
        if (m == null) {
            m = new HashMap<String,ValueFormatter>();
            formatterMap.put(displayerUuid, m);
        }
        m.put(columnId, formatter);
    }

    public Map<String,ValueFormatter> get(String displayerUuid) {
        Map<String,ValueFormatter> results = new HashMap<String,ValueFormatter>();

        Map m = formatterMap.get(_UNASSIGNED);
        if (m != null) {
            results.putAll(m);
        }
        m = formatterMap.get(displayerUuid);
        if (m != null) {
            results.putAll(m);
        }
        return results;
    }
}
