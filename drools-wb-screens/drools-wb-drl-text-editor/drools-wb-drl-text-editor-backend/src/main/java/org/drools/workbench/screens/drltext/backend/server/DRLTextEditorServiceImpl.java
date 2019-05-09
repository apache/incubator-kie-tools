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

package org.drools.workbench.screens.drltext.backend.server;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.oracle.DSLActionSentence;
import org.drools.workbench.models.datamodel.oracle.DSLConditionSentence;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.screens.drltext.model.DrlModelContent;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.drools.workbench.screens.drltext.type.DRLResourceTypeDefinition;
import org.drools.workbench.screens.drltext.type.DSLRResourceTypeDefinition;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.project.datamodel.commons.packages.PackageNameParser;
import org.kie.soup.project.datamodel.commons.packages.PackageNameWriter;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.packages.HasPackageName;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class DRLTextEditorServiceImpl
        extends KieService<DrlModelContent>
        implements DRLTextEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private CopyService copyService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private GenericValidator genericValidator;

    @Inject
    private DRLResourceTypeDefinition drlResourceType;

    @Inject
    private DSLRResourceTypeDefinition dslrResourceType;

    @Inject
    private SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService;

    @Inject
    private CommentedOptionFactory commentedOptionFactory;
    private SafeSessionInfo safeSessionInfo;

    public DRLTextEditorServiceImpl() {
    }

    @Inject
    public DRLTextEditorServiceImpl(final SessionInfo sessionInfo) {
        safeSessionInfo = new SafeSessionInfo(sessionInfo);
    }

    @PostConstruct
    public void init() {
        saveAndRenameService.init(this);
    }

    @Override
    public Path create(final Path context,
                       final String fileName,
                       final String content,
                       final String comment) {
        try {
            final String drl = assertPackageName(content,
                                                 context);

            final org.uberfire.java.nio.file.Path nioPath = Paths.convert(context).resolve(fileName);
            final Path newPath = Paths.convert(nioPath);

            if (ioService.exists(nioPath)) {
                throw new FileAlreadyExistsException(nioPath.toString());
            }

            ioService.write(nioPath,
                            drl,
                            commentedOptionFactory.makeCommentedOption(comment));

            return newPath;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public String load(final Path path) {
        try {
            final String content = ioService.readAllString(Paths.convert(path));

            return content;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public DrlModelContent loadContent(final Path path) {
        return super.loadContent(path);
    }

    @Override
    protected DrlModelContent constructContent(Path path,
                                               Overview overview) {
        final PackageDataModelOracle oracle = dataModelService.getDataModel(path);
        final String[] fullyQualifiedClassNames = DataModelOracleUtilities.getFactTypes(oracle);
        final List<DSLSentence> dslConditions = oracle.getExtensions(DSLConditionSentence.INSTANCE);
        final List<DSLSentence> dslActions = oracle.getExtensions(DSLActionSentence.INSTANCE);

        //Signal opening to interested parties
        resourceOpenedEvent.fire(new ResourceOpenedEvent(path,
                                                         safeSessionInfo));

        return new DrlModelContent(load(path),
                                   overview,
                                   Arrays.asList(fullyQualifiedClassNames),
                                   dslConditions,
                                   dslActions);
    }

    @Override
    public List<String> loadClassFields(final Path path,
                                        final String fullyQualifiedClassName) {
        try {
            final PackageDataModelOracle oracle = dataModelService.getDataModel(path);
            final String[] fieldNames = DataModelOracleUtilities.getFieldNames(oracle,
                                                                               fullyQualifiedClassName);
            return Arrays.asList(fieldNames);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path save(final Path resource,
                     final String content,
                     final Metadata metadata,
                     final String comment) {
        try {
            final String drl = assertPackageName(content,
                                                 resource);
            ioService.write(Paths.convert(resource),
                            drl,
                            metadataService.setUpAttributes(resource,
                                                            metadata),
                            commentedOptionFactory.makeCommentedOption(comment));

            return resource;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
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
    public List<ValidationMessage> validate(final Path path,
                                            final String content) {
        try {
            return genericValidator.validate(path,
                                             content);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    //Check if the DRL contains a Package declaration, appending one if it does not exist
    @Override
    public String assertPackageName(final String drl,
                                    final Path resource) {
        try {
            final String existingPackageName = PackageNameParser.parsePackageName(drl);
            if (!"".equals(existingPackageName)) {
                return drl;
            }

            final Package pkg = moduleService.resolvePackage(resource);
            final String requiredPackageName = (pkg == null ? null : pkg.getPackageName());
            final HasPackageName mockHasPackageName = new HasPackageName() {

                @Override
                public String getPackageName() {
                    return requiredPackageName;
                }

                @Override
                public void setPackageName(final String packageName) {
                    //Nothing to do here
                }
            };
            final StringBuilder sb = new StringBuilder();
            PackageNameWriter.write(sb,
                                    mockHasPackageName);
            sb.append(drl);
            return sb.toString();
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final Metadata metadata,
                              final String content,
                              final String comment) {
        return saveAndRenameService.saveAndRename(path, newFileName, metadata, content, comment);
    }
}
