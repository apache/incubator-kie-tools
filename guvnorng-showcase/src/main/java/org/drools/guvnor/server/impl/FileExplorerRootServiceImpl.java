/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystems;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.impl.PathImpl;
import org.uberfire.shared.mvp.PlaceRequest;

import static java.util.Collections.*;

@Service
@ApplicationScoped
public class FileExplorerRootServiceImpl implements FileExplorerRootService {

    protected final Set<Root> roots = new HashSet<Root>();

    @PostConstruct
    protected void init() {
        setupGitRepos();
    }

    private void setupGitRepos() {
        final String gitURL = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final String userName = "guvnorngtestuser1";
        final String password = "test1234";
        final URI fsURI = URI.create("jgit:///guvnorng-playground");

        final Map<String, Object> env = new HashMap<String, Object>();
        env.put("username", userName);
        env.put("password", password);
        env.put("giturl", gitURL);

        final FileSystem fs;
        try {
            fs = FileSystems.newFileSystem(fsURI, env);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Root root = new Root(new PathImpl("guvnorng-playground", fsURI.toString()),
                new PlaceRequest("RepositoryEditor"));

        roots.add(root);
    }

    @Override
    public Collection<Root> listRoots() {
        return unmodifiableSet(roots);
    }

    @Override
    public void addRoot(final Root root) {
        roots.add(root);
    }

    @Override
    public void removeRoot(final Root root) {
        roots.remove(root);
    }

    @Override
    public void clear() {
        roots.clear();
    }

}
