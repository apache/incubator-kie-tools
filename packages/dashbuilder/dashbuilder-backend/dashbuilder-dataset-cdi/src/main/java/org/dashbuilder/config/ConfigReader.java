/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Producer class for primitive fields annotated with @Config
 */
@ApplicationScoped
public class ConfigReader {

    private static Logger log = LoggerFactory.getLogger(ConfigReader.class);
    private volatile Properties globalProperties;
    private volatile Map<String, Properties> beanPropertyMap;
    public static final String beansConfigFile ="META-INF/beans.config";

    @PostConstruct
    public void init() {
        try {
            globalProperties = new Properties();
            beanPropertyMap = new HashMap<String, Properties>();
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(beansConfigFile);
            if (is != null) globalProperties.load(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public @Produces @Config String readConfig(InjectionPoint p) {

        // Read from specific bean
        String beanKey = p.getMember().getDeclaringClass().getName();
        Properties beanProperties = beanPropertyMap.get(beanKey);
        if (beanProperties == null) {
            beanPropertyMap.put(beanKey, beanProperties = new Properties());
            try {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/" + beanKey + ".config");
                if (is != null)  beanProperties.load(is);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        // Read from the bean config
        String configKey = p.getMember().getName();
        String configValue = beanProperties.getProperty(configKey);
        if (configValue != null) return configValue;

        // Read from global - by the fully qualified class name and field name
        for (Type type : p.getBean().getTypes()) {
            configKey = ((Class)type).getName() + "." + p.getMember().getName();
            configValue = globalProperties.getProperty(configKey);
            if (configValue != null) return configValue;

            // Try class name from System.properties
            configValue = System.getProperty(configKey);
            if (configValue != null) {
                log.info(String.format("System property: %s=%s", configKey, configValue));
                return configValue;
            }
            // Try class simple name from System.properties
            configKey = ((Class)type).getSimpleName() + "." + p.getMember().getName();
            configValue = System.getProperty(configKey);
            if (configValue != null) {
                log.info(String.format("System property: %s=%s", configKey, configValue));
                return configValue;
            }
        }

        // Read from global - only by the field name
        configKey = p.getMember().getName();
        configValue = globalProperties.getProperty(configKey);
        if (configValue != null) return configValue;

        // Return the default value if any.
        Annotated annotated = p.getAnnotated();
        Config config = annotated.getAnnotation(Config.class);
        if (config != null) return config.value();
        return null;
    }

    public @Produces @Config byte readPrimitiveByte(InjectionPoint p) {
        String val= readConfig(p);
        return Byte.parseByte(val);
    }

    public @Produces @Config short readPrimitiveShort(InjectionPoint p) {
        String val= readConfig(p);
        return Short.parseShort(val);
    }

    public @Produces @Config int readPrimitiveInt(InjectionPoint p) {
        String val= readConfig(p);
        return Integer.parseInt(val);
    }

    public @Produces @Config long readPrimitiveLong(InjectionPoint p) {
        String val= readConfig(p);
        return Long.parseLong(val);
    }

    public @Produces @Config boolean readPrimitiveBoolean(InjectionPoint p) {
        String val= readConfig(p);
        return Boolean.parseBoolean(val);
    }

    public @Produces @Config float readPrimitiveFloat(InjectionPoint p) {
        String val= readConfig(p);
        return Float.parseFloat(val);
    }

    public @Produces @Config double readPrimitiveDouble(InjectionPoint p) {
        String val= readConfig(p);
        return Double.parseDouble(val);
    }

    public @Produces @Config  String[] readStringArray(InjectionPoint p) {
        String val = readConfig(p);
        String[] result = StringUtils.split(val, ",");
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].trim();
        }
        return result;
    }

    public @Produces @Config java.util.List<String> readStringList(InjectionPoint p) {
        String val = readConfig(p);
        String[] array = StringUtils.split(val, ",");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i].trim());
        }
        return list;
    }

    public @Produces @Config Map<String,String> readStringMap(InjectionPoint p) {
        String val = readConfig(p);
        Map<String,String> results = new HashMap<String, String>();
        String[] rows = StringUtils.split(val, ",");
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i].trim();
            final String[] rowSplit = row.split("=");
            if (rowSplit.length != 2) throw new IllegalArgumentException("Illegal map entry '" + row + "'");
            results.put(rowSplit[0].trim(), rowSplit[1].trim());
        }
        return results;
    }

    public @Produces @Config Properties readProperties(InjectionPoint p) {
        String val = readConfig(p);
        Properties results = new Properties();
        String[] rows = StringUtils.split(val, ",");
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i].trim();
            final String[] rowSplit = row.split("=");
            if (rowSplit.length != 2) throw new IllegalArgumentException("Illegal property entry '" + row + "'");
            results.put(rowSplit[0].trim(), rowSplit[1].trim());
        }
        return results;
    }
}
