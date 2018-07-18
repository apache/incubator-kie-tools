/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.backend.server;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableGraphEditorService;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableGraphResourceTypeDefinition;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableResourceTypeDefinition;
import org.guvnor.common.services.backend.file.FileExtensionFilter;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.DeleteHelper;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;

/**
 * DeleteHelper for Guided Decision Tables referenced in Guided Decision Table Graphs
 */
@ApplicationScoped
public class GuidedDecisionTableEditorGraphDeleteHelper implements DeleteHelper {

    private IOService ioService;
    private GuidedDTableResourceTypeDefinition dtableType;
    private GuidedDTableGraphResourceTypeDefinition dtableGraphType;
    private GuidedDecisionTableGraphEditorService dtableGraphService;
    private CommentedOptionFactory commentedOptionFactory;

    public GuidedDecisionTableEditorGraphDeleteHelper() {
        //CDI proxies
    }

    @Inject
    public GuidedDecisionTableEditorGraphDeleteHelper(final @Named("ioStrategy") IOService ioService,
                                                      final GuidedDTableResourceTypeDefinition dtableType,
                                                      final GuidedDTableGraphResourceTypeDefinition dtableGraphType,
                                                      final GuidedDecisionTableGraphEditorService dtableGraphService,
                                                      final CommentedOptionFactory commentedOptionFactory) {
        this.ioService = ioService;
        this.dtableType = dtableType;
        this.dtableGraphType = dtableGraphType;
        this.dtableGraphService = dtableGraphService;
        this.commentedOptionFactory = commentedOptionFactory;
    }

    @Override
    public boolean supports(final Path destination) {
        return (dtableType.accept(destination));
    }

    @Override
    public void postProcess(final Path path) {
        try (final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream =
                     ioService.newDirectoryStream(getParentFolder(path),
                                                  new FileExtensionFilter(dtableGraphType.getSuffix()))) {
            directoryStream.forEach((p) -> updateGraphReferences(path,
                                                                 Paths.convert(p)));
        }
    }

    org.uberfire.java.nio.file.Path getParentFolder(final Path path) {
        org.uberfire.java.nio.file.Path nioFolderPath = Paths.convert(path);
        return Files.isDirectory(nioFolderPath) ? nioFolderPath : nioFolderPath.getParent();
    }

    void updateGraphReferences(final Path path,
                               final Path graphPath) {
        final GuidedDecisionTableEditorGraphModel dtGraphModel = dtableGraphService.load(graphPath);
        final Set<GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry> dtGraphEntries = dtGraphModel.getEntries();
        dtGraphEntries.removeIf((e) -> e.getPathHead().equals(path));
        ioService.write(Paths.convert(graphPath),
                        GuidedDTGraphXMLPersistence.getInstance().marshal(dtGraphModel),
                        commentedOptionFactory.makeCommentedOption("File [" + path.toURI() + "] deleted."));
    }
}
