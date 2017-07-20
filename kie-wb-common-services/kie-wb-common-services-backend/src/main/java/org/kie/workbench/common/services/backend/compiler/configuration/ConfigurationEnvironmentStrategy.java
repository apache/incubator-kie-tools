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

package org.kie.workbench.common.services.backend.compiler.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.services.backend.compiler.Order;
import org.slf4j.LoggerFactory;

public class ConfigurationEnvironmentStrategy implements ConfigurationStrategy,
                                                         Order {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigurationEnvironmentStrategy.class);

    protected Map<ConfigurationKey, String> conf;

    private Boolean valid = Boolean.TRUE;

    public ConfigurationEnvironmentStrategy() {
        conf = new HashMap<>();
        Map<String, String> env = System.getenv();
        ConfigurationKey[] keys = ConfigurationKey.values();
        for (ConfigurationKey key : keys) {
            String value = env.get(key.name());
            if (value == null) {
                logger.info("Key {} not present in the Environment, skip to the next ConfigurationStrategy. \n",
                            key.name());
                valid = Boolean.FALSE;
                break;
            } else {
                conf.put(key,
                         value);
            }
        }
    }

    @Override
    public Integer getOrder() {
        return Integer.valueOf(0);
    }

    @Override
    public Boolean isValid() {
        return valid;
    }

    @Override
    public Map<ConfigurationKey, String> loadConfiguration() {
        return Collections.unmodifiableMap(conf);
    }
}
