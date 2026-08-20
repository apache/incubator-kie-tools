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

import org.jbpm.springboot.devconsole.forms.FormsRestController;
import org.jbpm.devconsole.commons.forms.FormsStorage;
import org.jbpm.devconsole.commons.forms.impl.FormsStorageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Auto-configuration for the jBPM Dev Console.
 *
 * The console is a development-only tool and must be explicitly enabled with
 * {@code jbpm.dev-console.enabled=true}. It serves the process Dev UI webapp (the same one used by the
 * jBPM Quarkus Dev UI extension) and a small REST API to list and edit custom forms of the
 * running project.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "jbpm.dev-console", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(JBPMDevConsoleProperties.class)
public class JBPMDevConsoleAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(JBPMDevConsoleAutoConfiguration.class);

    public JBPMDevConsoleAutoConfiguration(JBPMDevConsoleProperties properties) {
        LOGGER.info("jBPM Dev Console is enabled and available at '{}/'. It is a development tool and should not be enabled in production.", properties.getPath());
    }

    @Bean
    public JBPMDevConsoleController jbpmDevConsoleController(JBPMDevConsoleProperties properties, Environment environment) {
        return new JBPMDevConsoleController(properties, environment);
    }

    @Bean
    public FormsStorage jbpmDevConsoleFormsStorage(JBPMDevConsoleProperties properties) {
        return new FormsStorageImpl(properties.getForms().getFolder());
    }

    @Bean
    public FormsRestController jbpmDevConsoleFormsRestController(FormsStorage formsStorage) {
        return new FormsRestController(formsStorage);
    }

    @Bean
    public WebMvcConfigurer jbpmDevConsoleResourcesConfigurer(JBPMDevConsoleProperties properties) {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                String path = properties.getPath().endsWith("/")
                        ? properties.getPath().substring(0, properties.getPath().length() - 1)
                        : properties.getPath();
                registry.addResourceHandler(path + "/resources/**").addResourceLocations("classpath:/dev-static/");
            }
        };
    }
}
