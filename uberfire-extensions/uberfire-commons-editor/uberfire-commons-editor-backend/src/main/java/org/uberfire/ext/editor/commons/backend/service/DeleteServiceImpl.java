/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.backend.service;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.DeleteHelper;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.restriction.PathOperationRestriction;
import org.uberfire.ext.editor.commons.service.restrictor.DeleteRestrictor;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.rpc.SessionInfo;

@Service
@ApplicationScoped
public class DeleteServiceImpl implements DeleteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteServiceImpl.class);

    private IOService ioService;
    private User identity;
    private SessionInfo sessionInfo;
    private Instance<DeleteHelper> helpers;
    private Instance<DeleteRestrictor> deleteRestrictorBeans;

    public DeleteServiceImpl() {
        //CDI proxies
    }

    @Inject
    public DeleteServiceImpl(final @Named("ioStrategy") IOService ioService,
                             final User identity,
                             final SessionInfo sessionInfo,
                             final Instance<DeleteHelper> helpers,
                             final Instance<DeleteRestrictor> deleteRestrictorBeans) {
        this.ioService = ioService;
        this.identity = identity;
        this.sessionInfo = sessionInfo;
        this.helpers = helpers;
        this.deleteRestrictorBeans = deleteRestrictorBeans;
    }

    @Override
    public void delete(final Path path,
                       final String comment) {

        LOGGER.info("User:" + identity.getIdentifier() + " deleting file [" + path.getFileName() + "]");

        checkRestrictions(path);

        try {
            deletePath(path,
                       comment);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteIfExists(final Collection<Path> paths,
                               final String comment) {
        try {
            startBatch(paths);

            for (final Path path : paths) {
                LOGGER.info("User:" + identity.getIdentifier() + " deleting file (if exists) [" + path.getFileName() + "]");

                checkRestrictions(path);
                deletePathIfExists(path,
                                   comment);
            }
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            endBatch(paths);
        }
    }

    @Override
    public boolean hasRestriction(final Path path) {
        for (DeleteRestrictor deleteRestrictor : getDeleteRestrictors()) {
            final PathOperationRestriction deleteRestriction = deleteRestrictor.hasRestriction(path);
            if (deleteRestriction != null) {
                return true;
            }
        }

        return false;
    }

    private void checkRestrictions(final Path path) {
        for (DeleteRestrictor deleteRestrictor : getDeleteRestrictors()) {
            final PathOperationRestriction deleteRestriction = deleteRestrictor.hasRestriction(path);
            if (deleteRestriction != null) {
                throw new RuntimeException(deleteRestriction.getMessage(path));
            }
        }
    }

    void deletePath(final Path path,
                    final String comment) {
        final org.uberfire.java.nio.file.Path _path = Paths.convert(path);

        try {
            ioService.startBatch(_path.getFileSystem());

            // Delegate additional changes required for a deletion to applicable Helpers. Helpers are invoked before
            // the deletion as Helpers may depend on the presence of the file; in particular when it is necessary to
            // resolve a Package from a file name.
            notifyDeleteHelpers(path);

            ioService.delete(Paths.convert(path),
                             new CommentedOption(sessionInfo != null ? sessionInfo.getId() : "--",
                                                 identity.getIdentifier(),
                                                 null,
                                                 comment));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            ioService.endBatch();
        }
    }

    void deletePathIfExists(final Path path,
                            final String comment) {
        // Delegate additional changes required for a deletion to applicable Helpers. Helpers are invoked before
        // the deletion as Helpers may depend on the presence of the file; in particular when it is necessary to
        // resolve a Package from a file name.
        notifyDeleteHelpers(path);

        ioService.deleteIfExists(Paths.convert(path),
                                 new CommentedOption(sessionInfo.getId(),
                                                     identity.getIdentifier(),
                                                     null,
                                                     comment),
                                 StandardDeleteOption.NON_EMPTY_DIRECTORIES
        );
    }

    void notifyDeleteHelpers(final Path path) {
        final Iterable<DeleteHelper> helpers = getDeleteHelpers();
        if (helpers != null) {
            for (DeleteHelper helper : helpers) {
                if (helper.supports(path)) {
                    helper.postProcess(path);
                }
            }
        }
    }

    void startBatch(final Collection<Path> paths) {
        if (paths.size() > 1) {
            ioService.startBatch(Paths.convert(paths.iterator().next()).getFileSystem());
        }
    }

    void endBatch(final Collection<Path> paths) {
        if (paths.size() > 1) {
            ioService.endBatch();
        }
    }

    Iterable<DeleteRestrictor> getDeleteRestrictors() {
        return deleteRestrictorBeans;
    }

    Iterable<DeleteHelper> getDeleteHelpers() {
        return helpers;
    }
}
