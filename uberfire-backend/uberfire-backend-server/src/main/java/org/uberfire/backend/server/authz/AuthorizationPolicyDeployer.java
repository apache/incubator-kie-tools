/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.backend.server.authz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.authz.AuthorizationPolicyStorage;
import org.uberfire.backend.events.AuthorizationPolicyDeployedEvent;
import org.uberfire.backend.server.WebAppListener;
import org.uberfire.backend.server.WebAppSettings;
import org.uberfire.backend.server.security.RoleRegistry;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.AuthorizationPolicyBuilder;

/**
 * An Uberfire's startup bean that scans the classpath looking for an authorization policy to deploy (a file named
 * <i>security-policy.properties</i>).</p>
 * <p>
 * <p>If located, the policy file is loaded and passed along the {@link AuthorizationPolicyStorage}. The deployment
 * process is only executed once, so if a policy instance has been already stored then the deployment is left out.
 * The {@link AuthorizationPolicyMarshaller} class is used to read and convert the entries defined at
 * the <i>security-policy.properties</i> file into an {@link AuthorizationPolicy} instance.</p>
 * <p>
 * <p>It is also possible to split the policy into multiple files. The
 * <i>security-policy.properties</i> file is always mandatory as it serves as a marker file. Alongside that file,
 * several <i>security-module-?.properties</i> files can be created. The split mechanism allows either for the
 * provision of just a single full standalone policy file or multiple module files each of them containing different
 * entries. The way those files are defined is always up to the application developer.</p>
 */
@Startup
@ApplicationScoped
public class AuthorizationPolicyDeployer {

    private Logger logger = LoggerFactory.getLogger(AuthorizationPolicyDeployer.class);

    private AuthorizationPolicyStorage authzPolicyStorage;
    private PermissionManager permissionManager;
    private Event<AuthorizationPolicyDeployedEvent> deployedEvent;

    public AuthorizationPolicyDeployer() {
    }

    @Inject
    public AuthorizationPolicyDeployer(AuthorizationPolicyStorage authzPolicyStorage,
                                       PermissionManager permissionManager,
                                       Event<AuthorizationPolicyDeployedEvent> deployedEvent) {
        this.authzPolicyStorage = authzPolicyStorage;
        this.permissionManager = permissionManager;
        this.deployedEvent = deployedEvent;
    }

    @PostConstruct
    public void init() {
        WebAppListener.registerOnStartupCommand(this::deployPolicy);
    }

    public void deployPolicy() {
        Path policyDir = getPolicyDir();
        deployPolicy(policyDir);
    }

    public Path getPolicyDir() {
        String rootDir = WebAppSettings.get().getRootDir();
        return Paths.get(rootDir,
                         "WEB-INF",
                         "classes");
    }

    public void deployPolicy(Path policyDir) {
        if (policyDir != null) {
            AuthorizationPolicy policy = authzPolicyStorage.loadPolicy();
            if (policy == null) {
                policy = loadPolicy(policyDir);
                authzPolicyStorage.savePolicy(policy);
                logger.info("Security policy deployed");

                // Ensure any role defined is available in the role registry
                for (Role role : policy.getRoles()) {
                    RoleRegistry.get().registerRole(role.getName());
                }
                // Notify the interested parties
                deployedEvent.fire(new AuthorizationPolicyDeployedEvent(policy));
            } else {
                logger.info("Security policy active");
            }
            // Set the active policy
            permissionManager.setAuthorizationPolicy(policy);
        } else {
            logger.info("Security policy not defined");
        }
    }

    public AuthorizationPolicy loadPolicy(Path policyDir) {
        AuthorizationPolicyBuilder builder = permissionManager.newAuthorizationPolicy();
        AuthorizationPolicyMarshaller marshaller = new AuthorizationPolicyMarshaller();
        if (policyDir != null) {
            try {
                NonEscapedProperties properties = readPolicyProperties(policyDir);
                marshaller.read(builder,
                                properties);
            } catch (IOException e) {
                logger.warn("Error loading security policy files",
                            e);
            }
        }
        return builder.build();
    }

    /**
     * Put all the policy files together into a single properties instance.
     * @param policyDir The source directory where to read the policy files from.
     * @return An {@link NonEscapedProperties} instance containing all the properties read from the policy files found
     * @throws IOException When an IO error occurs reading any of the policy files
     */
    public NonEscapedProperties readPolicyProperties(Path policyDir) throws IOException {
        NonEscapedProperties properties = new NonEscapedProperties();
        Files.list(policyDir)
                .filter(this::isPolicyFile)
                .forEach(path -> loadPolicyFile(properties,
                                                path));

        return properties;
    }

    public boolean isPolicyFile(Path p) {
        String fileName = p.getName(p.getNameCount() - 1).toString();
        return fileName.equals("security-policy.properties") || fileName.startsWith("security-module-");
    }

    public void loadPolicyFile(NonEscapedProperties properties,
                               Path path) {
        try {
            properties.load(path);
        } catch (IOException e) {
            logger.error("Security policy file load error: " + path,
                         e);
        }
    }
}
