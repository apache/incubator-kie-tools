/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dtablexls.backend.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.template.parser.DecisionTableParseException;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSContent;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSConversionService;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSService;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.source.SourceGenerationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.StandardOpenOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
// Implementation needs to implement both interfaces even though one extends the other
// otherwise the implementation discovery mechanism for the @Service annotation fails.
public class DecisionTableXLSServiceImpl
        extends KieService<DecisionTableXLSContent>
        implements DecisionTableXLSService,
                   ExtendedDecisionTableXLSService {

    private static final Logger log = LoggerFactory.getLogger(DecisionTableXLSServiceImpl.class);

    private IOService ioService;
    private CopyService copyService;
    private DeleteService deleteService;
    private RenameService renameService;
    private Event<ResourceOpenedEvent> resourceOpenedEvent;
    private DecisionTableXLSConversionService conversionService;
    private GenericValidator genericValidator;
    private CommentedOptionFactory commentedOptionFactory;
    private AuthenticationService authenticationService;

    public DecisionTableXLSServiceImpl() {
    }

    @Inject
    public DecisionTableXLSServiceImpl(@Named("ioStrategy") final IOService ioService,
                                       final CopyService copyService,
                                       final DeleteService deleteService,
                                       final RenameService renameService,
                                       final Event<ResourceOpenedEvent> resourceOpenedEvent,
                                       final DecisionTableXLSConversionService conversionService,
                                       final GenericValidator genericValidator,
                                       final CommentedOptionFactory commentedOptionFactory,
                                       final AuthenticationService authenticationService) {
        this.ioService = ioService;
        this.copyService = copyService;
        this.deleteService = deleteService;
        this.renameService = renameService;
        this.resourceOpenedEvent = resourceOpenedEvent;
        this.conversionService = conversionService;
        this.genericValidator = genericValidator;
        this.commentedOptionFactory = commentedOptionFactory;
        this.authenticationService = authenticationService;
    }

    @Override
    public DecisionTableXLSContent loadContent(final Path path) {
        return super.loadContent(path);
    }

    @Override
    protected DecisionTableXLSContent constructContent(Path path,
                                                       Overview overview) {
        final DecisionTableXLSContent content = new DecisionTableXLSContent();
        content.setOverview(overview);
        return content;
    }

    @Override
    public InputStream load(final Path path,
                            final String sessionId) {
        try {
            final InputStream inputStream = ioService.newInputStream(Paths.convert(path),
                                                                     StandardOpenOption.READ);

            //Signal opening to interested parties
            resourceOpenedEvent.fire(new ResourceOpenedEvent(path,
                                                             getSessionInfo(sessionId)));

            return inputStream;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path create(final Path resource,
                       final InputStream content,
                       final String sessionId,
                       final String comment) {
        return writeToFile(resource,
                           content,
                           sessionId,
                           comment,
                           true);
    }

    @Override
    public Path save(final Path resource,
                     final InputStream content,
                     final String sessionId,
                     final String comment) {
        return writeToFile(resource,
                           content,
                           sessionId,
                           comment,
                           false);
    }

    private Path writeToFile(final Path resource,
                             final InputStream content,
                             final String sessionId,
                             final String comment,
                             boolean create) {
        final SessionInfo sessionInfo = getSessionInfo(sessionId);
        String userAction = "UPDATING";
        if (create) {
            userAction = "CREATING";
        }
        log.info("USER:" + sessionInfo.getIdentity().getIdentifier() + " " + userAction + " asset [" + resource.getFileName() + "]");

        try {

            final File tempFile = File.createTempFile("testxls", null);
            try (FileOutputStream tempFOS = new FileOutputStream(tempFile)) {

                IOUtils.copy(content, tempFOS);
                tempFOS.flush();
            }

            //Validate the xls
            validate(tempFile);

            final org.uberfire.java.nio.file.Path nioPath = Paths.convert(resource);

            ioService.startBatch(nioPath.getFileSystem());
            try (FileInputStream tempFIS = new FileInputStream(tempFile)) {

                if (create) {

                    ioService.write(nioPath,
                                    IOUtils.toByteArray(tempFIS),
                                    commentedOptionFactory.makeCommentedOption(comment,
                                                                               sessionInfo.getIdentity(),
                                                                               sessionInfo));
                } else {

                    try (OutputStream outputStream = ioService.newOutputStream(nioPath,
                                                                               commentedOptionFactory.makeCommentedOption(comment,
                                                                                                                          sessionInfo.getIdentity(),
                                                                                                                          sessionInfo))) {

                        //InputStream 'content' has been fully read to write to the temp file; so we need to use a new InputStream
                        //An alternative is to check for content.markSupported() and reset() however since we have a temporary
                        //file we may as well use it!
                        IOUtils.copy(tempFIS,
                                     outputStream);
                        outputStream.flush();
                    }
                }
            }

            //Read Path to ensure attributes have been set
            final Path newPath = Paths.convert(nioPath);

            return newPath;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            ioService.endBatch();
            try {
                content.close();
            } catch (IOException e) {
                throw ExceptionUtilities.handleException(e);
            }
        }
    }

    void validate(final File tempFile) {
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(tempFile);
        } catch (IOException e) {
            throw new DecisionTableParseException("DecisionTableParseException: Failed to open Excel stream, " + "please check that the content is xls97 format.",
                                                  e);
        } catch (Throwable e) {
            throw new DecisionTableParseException("DecisionTableParseException: " + e.getMessage(),
                                                  e);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    throw ExceptionUtilities.handleException(e);
                }
            }
        }
    }

    @Override
    public String getSource(final Path path) {
        InputStream inputStream = null;
        try {
            final SpreadsheetCompiler compiler = new SpreadsheetCompiler();
            inputStream = ioService.newInputStream(Paths.convert(path),
                                                   StandardOpenOption.READ);
            final String drl = compiler.compile(inputStream,
                                                InputType.XLS);
            return drl;
        } catch (Exception e) {
            throw new SourceGenerationFailedException(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioe) {
                    throw ExceptionUtilities.handleException(ioe);
                }
            }
        }
    }

    @Override
    public void delete(final Path path,
                       final String comment) {
        try {
            deleteService.delete(path,
                                 comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path rename(final Path path,
                       final String newName,
                       final String comment) {
        try {
            return renameService.rename(path,
                                        newName,
                                        comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path copy(final Path path,
                     final String newName,
                     final String comment) {
        try {
            return copyService.copy(path,
                                    newName,
                                    comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path copy(final Path path,
                     final String newName,
                     final Path targetDirectory,
                     final String comment) {
        try {
            return copyService.copy(path,
                                    newName,
                                    targetDirectory,
                                    comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public ConversionResult convert(final Path path) {
        try {
            return conversionService.convert(path);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public List<ValidationMessage> validate(final Path path,
                                            final Path resource) {
        try {
            return genericValidator.validate(path);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private SessionInfo getSessionInfo(final String sessionId) {
        return new SafeSessionInfo(new SessionInfoImpl(sessionId,
                                                       authenticationService.getUser()));
    }
}
