/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.uberfire.ext.metadata.io.infinispan.suite;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class InfinispanTestProperties {

    public static final String VERSION = "version";
    public static final String IMAGE = "image";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String ISPN_PROPERTIES = "ispn.properties";
    private final Properties props;

    private static class LazyHolder {

        static final InfinispanTestProperties INSTANCE = new InfinispanTestProperties();
    }

    public static InfinispanTestProperties getInstance() {
        return LazyHolder.INSTANCE;
    }

    InfinispanTestProperties() {

        InputStream resource = this.getClass().getClassLoader().getResourceAsStream(ISPN_PROPERTIES);
        props = new Properties();
        try {
            props.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getProp(String key,
                           Properties props) {
        Properties systemProps = System.getProperties();
        String prop = systemProps.getProperty(key);
        if (StringUtils.isEmpty(prop)) {
            prop = props.getProperty(key);
        }
        return prop;
    }

    public String getVersion() {
        return this.getProp(VERSION,
                            props);
    }

    public String getImage() {
        return this.getProp(IMAGE,
                            props);
    }

    public String getUser() {
        return this.getProp(USER,
                            props);
    }

    public String getPassword() {
        return this.getProp(PASSWORD,
                            props);
    }
}
