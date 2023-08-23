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

package org.kie.workbench.common.dmn.client.editors.types.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

/**
 * Stores the stack hierarchy for each Data Type, indexed by UUID.
 * <p>
 * e.g.
 * * - tPerson
 * *    - name (Text)
 * *    - age (Number)
 * *    - city (tCity)
 * *        - name (Text)
 * *        - mayor (tPerson) <-- The "tPerson" type is already represented at this point.
 */
@ApplicationScoped
public class DataTypeManagerStackStore {

    private final Map<String, List<String>> typeStack = new HashMap<>();

    public List<String> get(final String uuid) {
        return Optional.ofNullable(typeStack.get(uuid)).orElse(new ArrayList<>());
    }

    public void put(final String uuid,
                    final List<String> types) {
        typeStack.put(uuid, types);
    }

    public void clear() {
        typeStack.clear();
    }

    int size() {
        return typeStack.size();
    }
}
