/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.uberfire.backend.server.security;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.WebAppListener;
import org.uberfire.backend.server.WebAppSettings;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

/**
 * Startup class that read the roles declared in the webapp's web.xml and register them into the {@link RoleRegistry}.
 */
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class RoleLoader {

    Logger logger = LoggerFactory.getLogger(RoleLoader.class);

    @PostConstruct
    public void init() {
        WebAppListener.registerOnStartupCommand(this::registerRolesFromwWebXml);
    }

    public void registerRolesFromwWebXml() {
        try {
            Set<String> roles = loadRolesFromwWebXml();
            for (String role : roles) {
                RoleRegistry.get().registerRole(role);
            }
            if (!roles.isEmpty()) {
                logger.info("Roles registered from web.xml \"" + StringUtils.join(roles.toArray(),
                                                                                  ",") + "\"");
            }
        } catch (Exception e) {
            logger.error("Error reading roles from web.xml",
                         e);
        }
    }

    protected Set<String> loadRolesFromwWebXml() throws Exception {
        Path webXml = WebAppSettings.get().getAbsolutePath("WEB-INF",
                                                           "web.xml");

        Set<String> result = new HashSet<>();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(webXml.toFile());
        Element root = doc.getRootElement();

        // Look for <security-role> declarations.
        List bundleNodes = root.getChildren("security-role");
        if (bundleNodes.isEmpty()) {
            bundleNodes = root.getChildren("security-role",
                                           null);
        }
        for (Iterator iterator = bundleNodes.iterator(); iterator.hasNext(); ) {
            Element el_role = (Element) iterator.next();
            List ch_role = el_role.getChildren();
            for (int i = 0; i < ch_role.size(); i++) {
                Element el_child = (Element) ch_role.get(i);
                if (el_child.getName().equals("role-name")) {
                    String name = el_child.getValue().trim();
                    if (!StringUtils.isBlank(name)) {
                        result.add(name);
                    }
                }
            }
        }
        return result;
    }
}
