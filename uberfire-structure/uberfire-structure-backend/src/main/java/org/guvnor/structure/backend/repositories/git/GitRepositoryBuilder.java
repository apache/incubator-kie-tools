/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.structure.backend.repositories.git;

import org.guvnor.structure.backend.repositories.git.hooks.PostCommitNotificationService;
import org.guvnor.structure.repositories.*;
import org.guvnor.structure.repositories.impl.DefaultPublicURI;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.config.SecureConfigItem;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.extensions.FileSystemHooks;
import org.uberfire.java.nio.file.extensions.FileSystemHooksConstants;
import org.uberfire.spaces.SpacesAPI;

import javax.enterprise.event.Event;
import java.net.URI;
import java.util.*;

import static org.uberfire.backend.server.util.Paths.convert;

public class GitRepositoryBuilder {

    private final IOService ioService;
    private final PasswordService secureService;
    private SpacesAPI spacesAPI;
    private Event<RepositoryExternalUpdateEvent> repositoryExternalUpdate;
    private PostCommitNotificationService postCommitNotificationService;
    private GitRepository repo;

    public GitRepositoryBuilder(final IOService ioService,
                                final PasswordService secureService,
                                final SpacesAPI spacesAPI,
                                final Event<RepositoryExternalUpdateEvent> repositoryExternalUpdate,
                                final PostCommitNotificationService postCommitNotificationService) {
        this.ioService = ioService;
        this.secureService = secureService;
        this.spacesAPI = spacesAPI;
        this.repositoryExternalUpdate = repositoryExternalUpdate;
        this.postCommitNotificationService = postCommitNotificationService;
    }

    public Repository build(final ConfigGroup repoConfig) {

        ConfigItem space = repoConfig.getConfigItem(EnvironmentParameters.SPACE);
        if (space == null) {
            throw new IllegalStateException("Repository " + repoConfig.getName() + " space is not valid");
        }
        repo = new GitRepository(repoConfig.getName(), spacesAPI.getSpace(space.getValue().toString()));

        if (!repo.isValid()) {
            throw new IllegalStateException("Repository " + repoConfig.getName() + " not valid");
        } else {

            addEnvironmentParameters(repoConfig.getItems());

            FileSystem fileSystem = createFileSystem(repo);

            setBranches(fileSystem);

            setPublicURIs(fileSystem);

            return repo;
        }
    }

    private void setPublicURIs(final FileSystem fileSystem) {
        final String[] uris = fileSystem.toString().split("\\r?\\n");
        final List<PublicURI> publicURIs = new ArrayList<>(uris.length);

        for (final String s : uris) {
            final int protocolStart = s.indexOf("://");
            publicURIs.add(getPublicURI(s, protocolStart));
        }
        repo.setPublicURIs(publicURIs);
    }

    private PublicURI getPublicURI(final String s,
                                   final int protocolStart) {
        if (protocolStart > 0) {
            return new DefaultPublicURI(s.substring(0, protocolStart), s);
        } else {
            return new DefaultPublicURI(s);
        }
    }

    private void setBranches(final FileSystem fileSystem) {
        final Map<String, Branch> branches = getBranches(fileSystem);

        repo.setBranches(branches);
    }

    private void addEnvironmentParameters(final Collection<ConfigItem> items) {
        for (final ConfigItem item : items) {
            if (item instanceof SecureConfigItem) {
                repo.addEnvironmentParameter(item.getName(),
                        secureService.decrypt(item.getValue().toString()));
            } else {
                repo.addEnvironmentParameter(item.getName(),
                        item.getValue());
            }
        }
    }

    private FileSystem createFileSystem(final GitRepository repo) {
        FileSystem fs;
        URI uri = null;
        try {
            uri = URI.create(repo.getUri());
            fs = newFileSystem(uri);
        } catch (final FileSystemAlreadyExistsException e) {
            fs = ioService.getFileSystem(uri);
            Object replaceIfExists = repo.getEnvironment().get("replaceIfExists");
            if (replaceIfExists != null && Boolean.valueOf(replaceIfExists.toString())) {
                org.uberfire.java.nio.file.Path root = fs.getPath(null);
                ioService.delete(root);
                fs = newFileSystem(uri);
            }
        } catch (final Throwable ex) {
            throw new RuntimeException(ex);
        }
        return fs;
    }

    private FileSystem newFileSystem(URI uri) {
        return ioService.newFileSystem(uri,
                new HashMap<String, Object>(repo.getEnvironment()) {{
                    if (!repo.getEnvironment().containsKey("origin")) {
                        put("init", true);
                    }
                    put(FileSystemHooks.ExternalUpdate.name(), externalUpdatedCallBack());
                    put(FileSystemHooks.PostCommit.name(), postCommitCallback());
                }});
    }

    private FileSystemHooks.FileSystemHook externalUpdatedCallBack() {
        return ctx -> repositoryExternalUpdate.fire(new RepositoryExternalUpdateEvent(repo));
    }

    private FileSystemHooks.FileSystemHook postCommitCallback() {
        return ctx -> postCommitNotificationService.notifyUser(repo, (Integer) ctx.getParamValue(FileSystemHooksConstants.POST_COMMIT_EXIT_CODE));
    }

    /**
     * collect all branches
     * @param fs
     * @return
     */
    private Map<String, Branch> getBranches(final FileSystem fs) {
        final Map<String, Branch> branches = new HashMap<>();
        for (final org.uberfire.java.nio.file.Path path : fs.getRootDirectories()) {
            final String branchName = getBranchName(path);
            branches.put(branchName,
                         new Branch(branchName,
                                    convert(path)));
        }
        return branches;
    }

    protected String getBranchName(final org.uberfire.java.nio.file.Path path) {
        URI uri = path.toUri();
        return GitPathUtil.extractBranch(uri.toString()).get();
    }
}
