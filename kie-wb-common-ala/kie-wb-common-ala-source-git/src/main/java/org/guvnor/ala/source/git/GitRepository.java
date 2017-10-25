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

package org.guvnor.ala.source.git;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.guvnor.ala.exceptions.SourcingException;
import org.guvnor.ala.source.Host;
import org.guvnor.ala.source.Repository;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.model.GitSource;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystems;
import org.uberfire.java.nio.file.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class GitRepository implements Repository {

    private final Host host;
    private final String id;
    private final String name;
    private final URI uri;
    private final GitCredentials credentials;
    private final String bareRepoDir;
    private final Map<String, String> env = new HashMap<>();
    private FileSystem fileSystem = null;

    public GitRepository(final Host host,
                         final String id,
                         final String name,
                         final URI uri,
                         final GitCredentials credentials,
                         final Map<String, String> env,
                         final ConfigProperties config) {
        this.host = checkNotNull("host",
                                 host);
        this.id = checkNotEmpty("id",
                                id);
        this.name = checkNotEmpty("name",
                                  name);
        this.uri = checkNotNull("uri",
                                uri);
        this.credentials = checkNotNull("credentials",
                                        credentials);
        if (env != null && !env.isEmpty()) {
            this.env.putAll(env);
        }
        checkNotNull("config",
                     config);
        final ConfigProperties.ConfigProperty currentDirectory = config.get("user.dir",
                                                                            null);
        this.bareRepoDir = config.get("org.uberfire.provisioning.git.dir",
                                      currentDirectory.getValue()).getValue();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Source getSource() {
        return getSource("master");
    }

    @Override
    public Source getSource(final String _root,
                            final String... _path) throws SourcingException {
        if (fileSystem == null) {
            final URI fsURI = URI.create("git://" + name);
            try {
                fileSystem = FileSystems.newFileSystem(fsURI,
                                                       new HashMap<String, Object>(env) {
                                                           {
                                                               putIfAbsent("origin",
                                                                           uri.toString());
                                                               putIfAbsent("out-dir",
                                                                           bareRepoDir);
                                                               if (credentials.getUser() != null) {
                                                                   putIfAbsent("username",
                                                                               credentials.getUser());
                                                                   putIfAbsent("password",
                                                                               credentials.getPassw());
                                                               }
                                                           }
                                                       });
            } catch (FileSystemAlreadyExistsException fsae) {
                try {
                    fileSystem = FileSystems.getFileSystem(fsURI);
                } catch (final Exception ex) {
                    throw new SourcingException("Error Getting Source",
                                                ex);
                }
            }
        }

        final String root;
        if (_root == null || _root.isEmpty()) {
            root = "master";
        } else {
            root = _root;
        }
        final String[] path;
        if (_path == null || _path.length == 0) {
            path = new String[]{"/"};
        } else {
            path = _path;
        }

        final Path result = fileSystem.getPath(root,
                                               path);

        return new GitSource(this,
                             result);
    }
}
