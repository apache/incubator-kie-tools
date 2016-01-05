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

package org.uberfire.backend.server.security.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.security.auth.Subject;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.authz.adapter.GroupsAdapter;

@ApplicationScoped
public class JettyGroupsAdapter implements GroupsAdapter {

    private static final String GROUPS_DEFINITION_FILE = "/jetty-groups.properties";
    private static final Logger logger = LoggerFactory.getLogger(JettyGroupsAdapter.class);

    private Map<String, List<Group>> groupsByUser = null;

    public JettyGroupsAdapter() {
        InputStream input = this.getClass().getResourceAsStream(GROUPS_DEFINITION_FILE);
        if (input != null) {

            try {
                Properties properties = new Properties();
                properties.load(input);

                groupsByUser = new HashMap<String, List<Group>>();

                Set<String> userNames = properties.stringPropertyNames();
                for (String userName : userNames) {
                    String groupsStr = properties.getProperty(userName);
                    List<Group> userGroups = new ArrayList<Group>();
                    if (groupsStr != null) {

                        String[] groups = groupsStr.split(",");

                        for (String group : groups) {
                            userGroups.add( new GroupImpl( group ) );
                        }
                    }

                    groupsByUser.put(userName, userGroups);
                }
            } catch (IOException e) {
                logger.warn("Unable to load jetty-groups.properties file due to {}", e.getMessage());
            }
        }
    }

    @Override
    public List<Group> getGroups(String principalName, final Object subject) {
        if (groupsByUser == null || !groupsByUser.containsKey(principalName)) {
            return Collections.emptyList();
        }

        return groupsByUser.get(principalName);
    }
}
