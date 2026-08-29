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
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.jbpm.devconsole.commons.model.User;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the Dev Console page. The page is a Thymeleaf view that loads the standalone build of the
 * {@code @kie-tools/runtime-tools-process-dev-ui-webapp} and initializes it with the settings of
 * the running application (data-index URL, application origin and users).
 */
@Controller
public class JBPMDevConsoleController {

    private static final String CONSOLE_VIEW_NAME = "jbpm-dev-console";

    private static final String SERVICE_URL_PROPERTY = "kogito.service.url";
    private static final String DATA_INDEX_URL_PROPERTY = "kogito.data-index.url";

    private final JBPMDevConsoleProperties properties;
    private final Environment environment;

    public JBPMDevConsoleController(JBPMDevConsoleProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @GetMapping("${jbpm.dev-console.path:/jbpm-dev-console}")
    public String redirectToConsole() {
        // Redirect to the trailing-slash URL so that the page's relative resource URLs resolve correctly.
        return "redirect:" + normalizedPath() + "/";
    }

    @GetMapping("${jbpm.dev-console.path:/jbpm-dev-console}/")
    public String console(Model model, HttpServletRequest request) {
        String appOrigin = environment.getProperty(SERVICE_URL_PROPERTY, defaultAppOrigin());
        String dataIndexUrl = environment.getProperty(DATA_INDEX_URL_PROPERTY, appOrigin);

        model.addAttribute("users", buildUsers());
        model.addAttribute("appOrigin", trimTrailingSlash(appOrigin));
        model.addAttribute("appRootPath", request.getContextPath());
        model.addAttribute("dataIndexUrl", trimTrailingSlash(dataIndexUrl) + "/graphql");
        return CONSOLE_VIEW_NAME;
    }

    private List<User> buildUsers() {
        List<User> users = new ArrayList<>();
        properties.getUsers().forEach((id, userGroups) -> users.add(new User(id, userGroups.getGroups())));
        return users;
    }

    private String defaultAppOrigin() {
        return "http://localhost:" + environment.getProperty("server.port", "8080");
    }

    private String normalizedPath() {
        return trimTrailingSlash(properties.getPath());
    }

    private static String trimTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
