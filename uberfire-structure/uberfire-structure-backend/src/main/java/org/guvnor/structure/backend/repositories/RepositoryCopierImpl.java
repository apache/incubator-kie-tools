/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.structure.backend.repositories;

import java.util.Optional;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryCopier;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.FileVisitor;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.StandardCopyOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;

public class RepositoryCopierImpl
        implements RepositoryCopier {

    private IOService ioService;
    private Event<NewBranchEvent> newBranchEventEvent;
    private ConfiguredRepositories configuredRepositories;
    private RepositoryService repositoryService;
    private SessionInfo sessionInfo;

    public RepositoryCopierImpl() {
    }

    @Inject
    public RepositoryCopierImpl(final @Named("ioStrategy") IOService ioService,
                                final Event<NewBranchEvent> newBranchEventEvent,
                                final ConfiguredRepositories configuredRepositories,
                                final RepositoryService repositoryService,
                                final SessionInfo sessionInfo) {
        this.ioService = ioService;
        this.newBranchEventEvent = newBranchEventEvent;
        this.configuredRepositories = configuredRepositories;
        this.repositoryService = repositoryService;
        this.sessionInfo = sessionInfo;
    }

    @Override
    public Repository copy(final OrganizationalUnit targetOU,
                           final String newRepositoryName,
                           final Path originRoot) {

        final Repository targetRepository = getRepository(targetOU,
                                                          newRepositoryName);

        if (targetRepository.getDefaultBranch().isPresent()) {
            copy(originRoot,
                 targetRepository.getDefaultBranch().get().getPath());
        }

        return targetRepository;
    }

    private Repository getRepository(final OrganizationalUnit targetOU,
                                     final String newRepositoryName) {
        return repositoryService.createRepository(targetOU,
                                                  GitRepository.SCHEME.toString(),
                                                  makeSafeRepositoryName(newRepositoryName),
                                                  new RepositoryEnvironmentConfigurations());
    }

    @Override
    public void copy(final Path originRoot,
                     final Path targetRoot) {

        final boolean branchExisted = (repositoryService.getRepository(targetRoot) != null);

        final org.uberfire.java.nio.file.Path nioTargetRepositoryRoot = Paths.convert(targetRoot);
        final org.uberfire.java.nio.file.Path originRepositoryRoot = Paths.convert(originRoot);

        ioService.startBatch(nioTargetRepositoryRoot.getFileSystem());
        try {
            copyFolders(nioTargetRepositoryRoot,
                        originRepositoryRoot);
            copyRootFiles(targetRoot,
                          originRepositoryRoot);
        } finally {
            ioService.endBatch();
        }

        if (!branchExisted) {
            fireNewBranchEvent(targetRoot,
                               nioTargetRepositoryRoot,
                               originRepositoryRoot);
        }
    }

    @Override
    public void copy(Space space,
                     Path originRoot,
                     Path targetRoot) {

        final boolean branchExisted = (repositoryService.getRepository(space,
                                                                       targetRoot) != null);

        final org.uberfire.java.nio.file.Path nioTargetRepositoryRoot = Paths.convert(targetRoot);
        final org.uberfire.java.nio.file.Path originRepositoryRoot = Paths.convert(originRoot);

        ioService.startBatch(nioTargetRepositoryRoot.getFileSystem());
        try {
            copyFolders(nioTargetRepositoryRoot,
                        originRepositoryRoot);
            copyRootFiles(targetRoot,
                          originRepositoryRoot);
        } finally {
            ioService.endBatch();
        }

        if (!branchExisted) {
            fireNewBranchEvent(space,
                               targetRoot,
                               nioTargetRepositoryRoot,
                               originRepositoryRoot);
        }
    }

    public void fireNewBranchEvent(final Path targetRoot,
                                   final org.uberfire.java.nio.file.Path nioTargetRepositoryRoot,
                                   final org.uberfire.java.nio.file.Path originRepositoryRoot) {

        final Repository repository = repositoryService.getRepository(targetRoot);

        final Optional<Branch> branch = repository.getBranch(Paths.convert(nioTargetRepositoryRoot.getRoot()));

        final Optional<Branch> origBranch = repository.getBranch(Paths.convert(originRepositoryRoot.getRoot()));

        if (branch.isPresent()) {
            newBranchEventEvent.fire(new NewBranchEvent(repository,
                                                        branch.get().getName(),
                                                        origBranch.get().getName(),
                                                        sessionInfo.getIdentity().getIdentifier()));
        } else {
            throw new IllegalStateException("Could not find a branch that was just created. The Path used was " + nioTargetRepositoryRoot.getRoot());
        }
    }

    public void fireNewBranchEvent(final Space space,
                                   final Path targetRoot,
                                   final org.uberfire.java.nio.file.Path nioTargetRepositoryRoot,
                                   final org.uberfire.java.nio.file.Path originRepositoryRoot) {

        final Repository repository = repositoryService.getRepository(space,
                                                                      targetRoot);

        final Optional<Branch> branch = repository.getBranch(Paths.convert(nioTargetRepositoryRoot.getRoot()));

        final Optional<Branch> origBranch = repository.getBranch(Paths.convert(originRepositoryRoot.getRoot()));

        if (branch.isPresent()) {
            newBranchEventEvent.fire(new NewBranchEvent(repository,
                                                        branch.get().getName(),
                                                        origBranch.get().getName(),
                                                        sessionInfo.getIdentity().getIdentifier()));
        } else {
            throw new IllegalStateException("Could not find a branch that was just created. The Path used was " + nioTargetRepositoryRoot.getRoot());
        }
    }

    private void copyFolders(final org.uberfire.java.nio.file.Path nioTargetRepositoryRoot,
                             final org.uberfire.java.nio.file.Path originRepositoryRoot) {
        final RecursiveCopier copier = new RecursiveCopier(originRepositoryRoot,
                                                           nioTargetRepositoryRoot);
        Files.walkFileTree(originRepositoryRoot,
                           copier);
    }

    private void copyRootFiles(final Path targetRoot,
                               final org.uberfire.java.nio.file.Path originRepositoryRoot) {
        for (org.uberfire.java.nio.file.Path path : Files.newDirectoryStream(originRepositoryRoot)) {

            if (!Files.isDirectory(path)) {
                try {
                    org.uberfire.java.nio.file.Path fileName = path.getFileName();
                    org.uberfire.java.nio.file.Path resolve = Paths.convert(targetRoot).resolve(fileName);
                    Files.copy(path,
                               resolve,
                               StandardCopyOption.REPLACE_EXISTING);
                } catch (FileAlreadyExistsException x) {
                    //Swallow
                    x.printStackTrace();
                }
            }
        }
    }

    @Override
    public String makeSafeRepositoryName(final String oldName) {
        return oldName.replace(' ',
                               '-');
    }

    static class RecursiveCopier implements FileVisitor<org.uberfire.java.nio.file.Path> {

        private final org.uberfire.java.nio.file.Path source;
        private final org.uberfire.java.nio.file.Path target;

        RecursiveCopier(final org.uberfire.java.nio.file.Path source,
                        final org.uberfire.java.nio.file.Path target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public FileVisitResult preVisitDirectory(final org.uberfire.java.nio.file.Path src,
                                                 final BasicFileAttributes attrs) {
            final org.uberfire.java.nio.file.Path tgt = target.resolve(source.relativize(src));
            try {
                Files.copy(src,
                           tgt,
                           StandardCopyOption.REPLACE_EXISTING);
//            } catch (FileAlreadyExistsException x) {
            } catch (Exception x) {
                x.printStackTrace();
                //Swallow
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final org.uberfire.java.nio.file.Path file,
                                         final BasicFileAttributes attrs) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final org.uberfire.java.nio.file.Path dir,
                                                  final IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final org.uberfire.java.nio.file.Path file,
                                               final IOException exc) {
            return FileVisitResult.CONTINUE;
        }
    }
}
