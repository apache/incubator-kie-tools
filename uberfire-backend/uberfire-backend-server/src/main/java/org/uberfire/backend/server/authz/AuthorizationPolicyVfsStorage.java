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

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.authz.AuthorizationPolicyStorage;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.AuthorizationPolicyBuilder;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.Files.walkFileTree;

/**
 * An implementation that stores the authorization policy in property files.
 */
@ApplicationScoped
public class AuthorizationPolicyVfsStorage implements AuthorizationPolicyStorage {

    private Logger logger = LoggerFactory.getLogger(AuthorizationPolicyVfsStorage.class);

    private PermissionManager permissionManager;
    private IOService ioService;
    private FileSystem fileSystem;
    private Path root;

    public AuthorizationPolicyVfsStorage() {
    }

    @Inject
    public AuthorizationPolicyVfsStorage(@Named("configIO") IOService ioService,
                                         PermissionManager permissionManager) {
        this.ioService = ioService;
        this.permissionManager = permissionManager;
    }

    @PostConstruct
    private synchronized void init() {
        initFileSystem();
    }

    @Override
    public synchronized AuthorizationPolicy loadPolicy() {
        return loadPolicyFromVfs();
    }

    @Override
    public synchronized void savePolicy(AuthorizationPolicy policy) {
        savePolicyIntoVfs(policy,
                          "system",
                          "Save policy");
    }

    // VFS operations

    public void initFileSystem() {
        try {
            fileSystem = ioService.newFileSystem(URI.create("default://security"),
                                                 new HashMap<String, Object>() {{
                                                     put("init",
                                                         Boolean.TRUE);
                                                     put("internal",
                                                         Boolean.TRUE);
                                                 }});
        } catch (FileSystemAlreadyExistsException e) {
            fileSystem = ioService.getFileSystem(URI.create("default://security"));
        }
        this.root = fileSystem.getRootDirectories().iterator().next();
    }

    public AuthorizationPolicy loadPolicyFromVfs() {
        Path authzPath = getAuthzPath();
        if (!ioService.exists(authzPath)) {
            return null;
        }
        AuthorizationPolicyBuilder builder = permissionManager.newAuthorizationPolicy();
        AuthorizationPolicyMarshaller marshaller = new AuthorizationPolicyMarshaller();

        walkFileTree(authzPath,
                     new SimpleFileVisitor<Path>() {
                         @Override
                         public FileVisitResult visitFile(final Path file,
                                                          final BasicFileAttributes attrs) throws IOException {
                             try {
                                 checkNotNull("file",
                                              file);
                                 checkNotNull("attrs",
                                              attrs);

                                 if (isPolicyFile(file)) {
                                     String content = ioService.readAllString(file);
                                     NonEscapedProperties props = new NonEscapedProperties();
                                     props.load(new StringReader(content));
                                     marshaller.read(builder,
                                                     props);
                                 }
                             } catch (final Exception e) {
                                 logger.error("Authz policy file VFS read error: " + file.getFileName(),
                                              e);
                                 return FileVisitResult.TERMINATE;
                             }
                             return FileVisitResult.CONTINUE;
                         }
                     });
        return builder.build();
    }

    public boolean isPolicyFile(Path p) {
        String fileName = p.getName(p.getNameCount() - 1).toString();
        return fileName.equals("security-policy.properties") || fileName.startsWith("security-module-");
    }

    public void savePolicyIntoVfs(AuthorizationPolicy policy,
                                  String subjectId,
                                  String message) {

        if (subjectId == null || message == null) {
            ioService.startBatch(fileSystem);
        } else {
            ioService.startBatch(fileSystem,
                                 new CommentedOption(subjectId,
                                                     message));
        }

        try {
            // Dump the entire authz policy into a properties map
            AuthorizationPolicyMarshaller marshaller = new AuthorizationPolicyMarshaller();
            NonEscapedProperties entries = new NonEscapedProperties();
            marshaller.write(policy,
                             entries);

            // Store the entries into a properties file
            StringWriter sw = new StringWriter();
            entries.store(sw,
                          "Authorization Policy",
                          "Last update: " + new Date().toString());
            String policyContent = sw.toString();
            Path policyPath = getAuthzPath().resolve("security-policy.properties");
            ioService.write(policyPath,
                            policyContent);
        } catch (Exception e) {
            logger.error("Authz policy write error.",
                         e);
        } finally {
            ioService.endBatch();
        }
    }

    public Path getAuthzPath() {
        checkNotNull("root",
                     root);
        return root.resolve("authz");
    }
}
