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

package org.drools.workbench.screens.workitems.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.workitems.PortableBooleanParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableFloatParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableIntegerParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableObjectParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableStringParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.screens.workitems.model.WorkItemDefinitionElements;
import org.drools.workbench.screens.workitems.model.WorkItemsModelContent;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.drools.workbench.screens.workitems.type.WorkItemsTypeDefinition;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.backend.file.FileExtensionsFilter;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.WorkDefinition;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.BooleanDataType;
import org.jbpm.process.core.datatype.impl.type.FloatDataType;
import org.jbpm.process.core.datatype.impl.type.IntegerDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.kie.workbench.common.services.backend.service.KieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class WorkItemsEditorServiceImpl
        extends KieService<WorkItemsModelContent>
        implements WorkItemsEditorService {

    private static final Logger log = LoggerFactory.getLogger(WorkItemsEditorServiceImpl.class);

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
    private ConfigurationService configurationService;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Inject
    private ResourceWorkDefinitionsLoader resourceWorkDefinitionsLoader;

    @Inject
    private ConfigWorkDefinitionsLoader configWorkDefinitionsLoader;

    @Inject
    private WorkItemsTypeDefinition resourceTypeDefinition;

    @Inject
    private SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService;

    @Inject
    private CommentedOptionFactory commentedOptionFactory;

    private WorkItemDefinitionElements workItemDefinitionElements;
    private FileExtensionsFilter imageFilter = new FileExtensionsFilter(new String[]{"png", "gif", "jpg"});
    private SafeSessionInfo safeSessionInfo;

    public WorkItemsEditorServiceImpl() {
    }

    @Inject
    public WorkItemsEditorServiceImpl(final SessionInfo sessionInfo) {
        safeSessionInfo = new SafeSessionInfo(sessionInfo);
    }

    @PostConstruct
    public void setupWorkItemDefinitionElements() {
        workItemDefinitionElements = new WorkItemDefinitionElements(loadWorkItemDefinitionElements());
        saveAndRenameService.init(this);
    }

    Map<String, String> loadWorkItemDefinitionElements() {
        final Map<String, String> workItemDefinitionElements = new HashMap<String, String>();
        final List<ConfigGroup> editorConfigGroups = configurationService.getConfiguration(ConfigType.EDITOR);
        for (ConfigGroup editorConfigGroup : editorConfigGroups) {
            if (WORK_ITEMS_EDITOR_SETTINGS.equals(editorConfigGroup.getName())) {
                for (ConfigItem item : editorConfigGroup.getItems()) {
                    final String itemName = item.getName();
                    final String itemValue = editorConfigGroup.getConfigItemValue(itemName);
                    workItemDefinitionElements.put(itemName,
                                                   itemValue);
                }
            }
        }
        return workItemDefinitionElements;
    }

    @Override
    public Path create(final Path context,
                       final String fileName,
                       final String content,
                       final String comment) {
        try {
            //Get the template for new Work Item Definitions, stored as a configuration item
            String defaultDefinition = workItemDefinitionElements.getDefinitionElements().get(WORK_ITEMS_EDITOR_SETTINGS_DEFINITION);
            if (defaultDefinition == null) {
                defaultDefinition = "";
            }
            defaultDefinition = defaultDefinition.replaceAll("\\|",
                                                             "");

            //Write file to VFS
            final org.uberfire.java.nio.file.Path nioPath = Paths.convert(context).resolve(fileName);
            final Path newPath = Paths.convert(nioPath);

            if (ioService.exists(nioPath)) {
                throw new FileAlreadyExistsException(nioPath.toString());
            }

            ioService.write(nioPath,
                            defaultDefinition,
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
    public WorkItemsModelContent loadContent(final Path path) {
        return super.loadContent(path);
    }

    @Override
    protected WorkItemsModelContent constructContent(Path path, Overview overview) {
        final String definition = load(path);
        final List<String> workItemImages = loadWorkItemImages(path);

        //Signal opening to interested parties
        resourceOpenedEvent.fire(new ResourceOpenedEvent(path,
                                                         safeSessionInfo));

        return new WorkItemsModelContent(definition,
                                         overview,
                                         workItemImages);
    }

    private List<String> loadWorkItemImages(final Path resourcePath) {
        final Path projectRoot = moduleService.resolveModule(resourcePath).getRootPath();
        final org.uberfire.java.nio.file.Path nioProjectPath = Paths.convert(projectRoot);
        final org.uberfire.java.nio.file.Path nioResourceParent = Paths.convert(resourcePath).getParent();

        final Collection<org.uberfire.java.nio.file.Path> imagePaths = fileDiscoveryService.discoverFiles(nioProjectPath,
                                                                                                          imageFilter,
                                                                                                          true);
        final List<String> images = new ArrayList<String>();
        for (org.uberfire.java.nio.file.Path imagePath : imagePaths) {
            final org.uberfire.java.nio.file.Path relativePath = nioResourceParent.relativize(imagePath);
            images.add(relativePath.toString());
        }
        return images;
    }

    @Override
    public WorkItemDefinitionElements loadDefinitionElements() {
        return workItemDefinitionElements;
    }

    @Override
    public Path save(final Path resource,
                     final String content,
                     final Metadata metadata,
                     final String comment) {
        try {
            ioService.write(Paths.convert(resource),
                            content,
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
    public boolean accepts(final Path path) {
        return resourceTypeDefinition.accept(path);
    }

    @Override
    public List<ValidationMessage> validate(final Path path) {
        try {
            final String content = ioService.readAllString(Paths.convert(path));
            return validate(path,
                            content);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public List<ValidationMessage> validate(final Path path,
                                            final String content) {
        return doValidation(path,
                            content);
    }

    private List<ValidationMessage> doValidation(final Path path,
                                                 final String content) {
        final List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        final List<String> workItemDefinitions = new ArrayList<String>();
        workItemDefinitions.add(content);
        try {
            WorkDefinitionsParser.parse(workItemDefinitions);
        } catch (Exception e) {
            final ValidationMessage msg = new ValidationMessage();
            msg.setPath(path);
            msg.setLevel(Level.ERROR);
            msg.setText(e.getMessage());
            validationMessages.add(msg);
        }
        return validationMessages;
    }

    @Override
    public Set<PortableWorkDefinition> loadWorkItemDefinitions(final Path path) {
        final Map<String, WorkDefinition> workDefinitions = new HashMap<String, WorkDefinition>();

        try {
            //Load WorkItemDefinitions from VFS
            final Path projectRoot = moduleService.resolveModule(path).getRootPath();
            workDefinitions.putAll(resourceWorkDefinitionsLoader.loadWorkDefinitions(projectRoot));

            //Load WorkItemDefinitions from ConfigurationService
            workDefinitions.putAll(configWorkDefinitionsLoader.loadWorkDefinitions());

            //Copy the Work Items into Structures suitable for GWT
            final Set<PortableWorkDefinition> workItems = new HashSet<PortableWorkDefinition>();
            for (Map.Entry<String, WorkDefinition> entry : workDefinitions.entrySet()) {
                final PortableWorkDefinition wid = new PortableWorkDefinition();
                final WorkDefinitionImpl wd = (WorkDefinitionImpl) entry.getValue();
                wid.setName(wd.getName());
                wid.setDisplayName(wd.getDisplayName());
                wid.setParameters(convertWorkItemParameters(entry.getValue().getParameters()));
                wid.setResults(convertWorkItemParameters(entry.getValue().getResults()));
                workItems.add(wid);
            }
            return workItems;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private Set<PortableParameterDefinition> convertWorkItemParameters(final Set<ParameterDefinition> parameters) {
        final Set<PortableParameterDefinition> pps = new HashSet<PortableParameterDefinition>();
        for (ParameterDefinition pd : parameters) {
            final DataType pdt = pd.getType();
            PortableParameterDefinition ppd = null;
            if (pdt instanceof BooleanDataType) {
                ppd = new PortableBooleanParameterDefinition();
            } else if (pdt instanceof FloatDataType) {
                ppd = new PortableFloatParameterDefinition();
            } else if (pdt instanceof IntegerDataType) {
                ppd = new PortableIntegerParameterDefinition();
            } else if (pdt instanceof ObjectDataType) {
                ppd = new PortableObjectParameterDefinition();
                final PortableObjectParameterDefinition oppd = (PortableObjectParameterDefinition) ppd;
                final ObjectDataType odt = (ObjectDataType) pdt;
                oppd.setClassName(odt.getClassName());
            } else if (pd.getType() instanceof StringDataType) {
                ppd = new PortableStringParameterDefinition();
            }
            if (ppd != null) {
                ppd.setName(pd.getName());
                pps.add(ppd);
            }
        }
        return pps;
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
