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


package org.kie.workbench.common.stunner.core.client.preferences;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;

@ApplicationScoped
@Default
public class DefaultPreferencesRegistry
        extends StunnerPreferencesRegistryHolder {

    private StunnerPreferences preferences;

    private Map<Class<?>, Object> preferencesMap = new HashMap<>();

    @Override
    public <T> T get(Class<T> preferenceType) {
        return (T) preferencesMap.get(preferenceType);
    }

    @Override
    public <T> void set(final T preferences, Class<T> preferenceType) {
        preferencesMap.put(preferenceType, preferences);
    }
}
