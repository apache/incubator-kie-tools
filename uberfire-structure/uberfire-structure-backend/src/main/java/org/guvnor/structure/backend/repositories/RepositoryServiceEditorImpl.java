/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryServiceEditor;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.uberfire.backend.server.util.Paths.convert;
import static org.uberfire.java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@ApplicationScoped
public class RepositoryServiceEditorImpl implements RepositoryServiceEditor {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private SpacesAPI spaces;

    @Override
    public List<VersionRecord> revertHistory(final String alias,
                                             final Path path,
                                             final String _comment,
                                             final VersionRecord record) {
        final org.uberfire.java.nio.file.Path history = ioService.get(URI.create(record.uri()));
        Space space = spaces.resolveSpace(path.toURI()).orElseThrow(() -> new IllegalArgumentException("Cannot resolve space from path: " + path));

        final String comment;
        if (_comment == null || _comment.trim().isEmpty()) {
            comment = "revert history from commit {" + record.comment() + "}";
        } else {
            comment = _comment;
        }

        ioService.move(history,
                       convert(path),
                       REPLACE_EXISTING,
                       new CommentedOption(sessionInfo.getId(),
                                           sessionInfo.getIdentity().getIdentifier(),
                                           null,
                                           comment));


        return new ArrayList<>(repositoryService.getRepositoryInfo(space, alias).getInitialVersionList());
    }
}
