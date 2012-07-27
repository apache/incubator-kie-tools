/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server.cache;

import org.drools.RuleBase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RuleBaseCache {

    // TODO: This class should be injected with DI Singleton (when we have working DI)
    /**
     * Used for a simple cache of binary packages to avoid serialization from
     * the database - for test scenarios.
     */
    private final Map<String, RuleBase> ruleBaseCache = Collections
            .synchronizedMap(new HashMap<String, RuleBase>());
    private static final RuleBaseCache instance = new RuleBaseCache();

    private RuleBaseCache() {
    }

    public static RuleBaseCache getInstance() {
        return instance;
    }

    public void put(final String uuid,
                    final RuleBase ruleBase) {
        this.ruleBaseCache.put(uuid,
                ruleBase);
    }

    public void remove(final String uuid) {
        this.ruleBaseCache.remove(uuid);
    }

    public void clearCache() {
        this.ruleBaseCache.clear();
    }

    public RuleBase get(final String uuid) {
        return this.ruleBaseCache.get(uuid);
    }

    public boolean contains(final String uuid) {
        return this.ruleBaseCache.containsKey(uuid);
    }
}
