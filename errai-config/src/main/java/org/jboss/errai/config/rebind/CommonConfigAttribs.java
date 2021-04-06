/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.config.rebind;

import java.util.Map;

/**
 * @author Mike Brock
 */
public enum CommonConfigAttribs {
    LAZY_LOAD_BUILTIN_MARSHALLERS("errai.marshalling.lazy_load_builtin_marshallers", "true"),
    MAKE_DEFAULT_ARRAY_MARSHALLERS("errai.marshalling.make_default_array_marshallers", "false");

    protected final String attributeName;
    protected final String defaultValue;

    CommonConfigAttribs(final String attributeName, final String defaultValue) {
        this.attributeName = attributeName;
        this.defaultValue = defaultValue;
    }

    public boolean getBoolean() {
        setDefaultValue();
        return Boolean.parseBoolean(getConfigMap().get(getAttributeName()));
    }

    public Integer getInt() {
        setDefaultValue();
        return Integer.parseInt(getConfigMap().get(getAttributeName()));
    }

    public String get() {
        setDefaultValue();
        return getConfigMap().get(getAttributeName());
    }

    public void set(final String value) {
        getConfigMap().put(getAttributeName(), value);
    }

    private void setDefaultValue() {
        final Map<String, String> map = getConfigMap();
        if (defaultValue != null && !map.containsKey(getAttributeName())) {
            map.put(getAttributeName(), defaultValue);
        }
    }

    private static Map<String, String> getConfigMap() {
        return EnvUtil.getEnvironmentConfig().getFrameworkProperties();
    }

    public String getAttributeName() {
        return attributeName;
    }
}