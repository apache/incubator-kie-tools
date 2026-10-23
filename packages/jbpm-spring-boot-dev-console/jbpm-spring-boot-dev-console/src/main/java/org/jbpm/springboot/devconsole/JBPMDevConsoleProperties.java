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
package org.jbpm.springboot.devconsole;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jbpm.dev-console")
public class JBPMDevConsoleProperties {

    /**
     * Enables the jBPM Dev Console. The console is a development tool and is disabled by default.
     */
    private boolean enabled = false;

    /**
     * Base path where the Dev Console is served.
     */
    private String path = "/jbpm-dev-console";

    /**
     * Users available in the Dev Console user switcher, keyed by user id. E.g. {@code jbpm.dev-console.users.jdoe.groups=admin,HR}.
     */
    private Map<String, UserGroups> users = new LinkedHashMap<>();

    private final Forms forms = new Forms();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, UserGroups> getUsers() {
        return users;
    }

    public void setUsers(Map<String, UserGroups> users) {
        this.users = users;
    }

    public Forms getForms() {
        return forms;
    }

    public static class UserGroups {

        private List<String> groups = new ArrayList<>();

        public List<String> getGroups() {
            return groups;
        }

        public void setGroups(List<String> groups) {
            this.groups = groups;
        }
    }

    public static class Forms {

        /**
         * Filesystem folder where form updates are persisted. Defaults to {@code src/main/resources/custom-forms-dev}
         * of the running project when it can be derived from the classpath.
         */
        private String folder;

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }
    }
}
