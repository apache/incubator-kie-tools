/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.java.nio.file.api.FileSystemUtils;

/**
 * This will boot internal FS infra for all repos making them available to ssh, http and git protocol
 */
@ApplicationScoped
@Startup
public class LoadReposOnAppInit {

    private ConfiguredRepositories configuredRepositories;
    private OrganizationalUnitService organizationalUnitService;

    public LoadReposOnAppInit() {
    }

    @Inject
    public LoadReposOnAppInit(final ConfiguredRepositories configuredRepositories,
                              final OrganizationalUnitService organizationalUnitService) {

        this.configuredRepositories = configuredRepositories;
        this.organizationalUnitService = organizationalUnitService;
    }

    @PostConstruct
    public void execute() {
        if (this.isGitDefaultFileSystem()) {
            organizationalUnitService
                    .getAllOrganizationalUnits()
                    .forEach(ou -> configuredRepositories.getAllConfiguredRepositories(ou.getSpace()));
        }
    }

    protected boolean isGitDefaultFileSystem() {
        return FileSystemUtils.isGitDefaultFileSystem();
    }
}
