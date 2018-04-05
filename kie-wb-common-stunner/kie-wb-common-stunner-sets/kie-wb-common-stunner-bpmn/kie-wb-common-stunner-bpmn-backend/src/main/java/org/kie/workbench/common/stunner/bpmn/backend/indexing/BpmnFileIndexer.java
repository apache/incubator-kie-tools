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
package org.kie.workbench.common.stunner.bpmn.backend.indexing;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.io.impl.ReaderResource;
import org.drools.core.xml.SemanticModules;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.process.core.validation.ProcessValidator;
import org.jbpm.process.core.validation.ProcessValidatorRegistry;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.IndexBuilder;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.stunner.bpmn.resource.BPMNDefinitionSetResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class BpmnFileIndexer extends AbstractFileIndexer {

    private static final Logger logger = LoggerFactory.getLogger(BpmnFileIndexer.class);

    private static final SemanticModules modules = new SemanticModules();

    static {
        modules.addSemanticModule(new BPMNSemanticModule());
        modules.addSemanticModule(new BPMNDISemanticModule());
        modules.addSemanticModule(new BPMNExtensionsSemanticModule());
    }

    @Inject
    protected BPMNDefinitionSetResourceType bpmnTypeDefinition;

    @Inject
    protected ModuleClassLoaderHelper classLoaderHelper;

    @Override
    public boolean supportsPath(Path path) {
        return bpmnTypeDefinition.accept(Paths.convert(path));
    }

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer#fillIndexBuilder(org.uberfire.java.nio.file.Path)
     */
    @Override
    protected IndexBuilder fillIndexBuilder(Path path) throws Exception {
        final KieModule module = moduleService.resolveModule(Paths.convert(path));
        if (module == null) {
            logger.error("Unable to index " + path.toUri().toString() + ": module could not be resolved.");
            return null;
        }

        // responsible for basic index info: module name, branch, etc
        final DefaultIndexBuilder builder = getIndexBuilder(path,
                                                            module);
        String bpmnStr = ioService.readAllString(path);
        ClassLoader moduleClassLoader = getModuleClassLoader(module);

        try {
            List<BpmnProcessDataEventListener> processDataList = buildProcessDefinition(bpmnStr,
                                                                                        moduleClassLoader);
            if (processDataList != null) {
                for (BpmnProcessDataEventListener processData : processDataList) {
                    addReferencedResourcesToIndexBuilder(builder,
                                                         processData);
                    builder.setPackageName(processData.getProcess().getPackageName());
                }
            }
        } catch (Exception e) {
            // log and ignore
            logger.info("Indexing hampered because BPMN2 compilation failed [" + path.toString() + "]: " + e.getMessage());
        }

        /**
         * IMPORTANT: sometimes the build of the BPMN2 might fail for minor reasons, including things like
         * a bad script in a script task
         *
         * When this happens, we (re)parse the process definition, but do not completely "build" it
         * (as in, what org.jbpm.compiler.ProcessBuilderImpl.buildProcess(Process, Resource) does).
         *
         *
         * It *would* be more efficient to basically copy/paste the
         * jbpm-flow-builder org.jbpm.compiler.ProcessBuilderImpl.addProcessFromXml(Resource) logic here,
         * so that we do something like:
         *
         * 1. Use the XmlProcessReader to create a process
         * 2. *try* to build the rest of the process (and fail safely if not)
         * 3. do XmlProcessReader.getProcessBuildData().onBuildComplete(process)
         *    to complete collecting the information
         *
         * But... that's a high maintenance cost for this piece of software
         *
         * So until we can refactor the ProcessBuilderImpl logic (using functional logic for conditional handling?)
         * to be used here, let's keep it simple (as in, parsing the BPMN2 a second time when the build fails..)
         */

        // parse process definitions
        XmlProcessReader processReader = new XmlProcessReader(modules,
                                                              moduleClassLoader);
        List<Process> processes = Collections.emptyList();
        try {
            processes = processReader.read(new StringReader(bpmnStr));
        } catch (Exception e) {
            logger.info("Unable to index because BPMN2 parsing failed [" + path.toString() + "]: " + e.getMessage());
        }

        // complete process definition processing
        if (processes != null) {
            for (Process process : processes) {
                Resource resource = new ReaderResource(new StringReader(bpmnStr));
                ProcessValidationError[] errors;

                ProcessValidator validator = ProcessValidatorRegistry.getInstance().getValidator(process,
                                                                                                 resource);
                errors = validator.validateProcess(process);
                if (errors.length > 0) {
                    logger.error("Trying to finish indexing process '" + process.getId() + "/" + process.getName() + "' despite " + errors.length + " validation errors.");
                }
                processReader.getProcessBuildData().onBuildComplete(process);

                BpmnProcessDataEventListener helper = (BpmnProcessDataEventListener) process.getMetaData().get(BpmnProcessDataEventListener.NAME);
                addReferencedResourcesToIndexBuilder(builder,
                                                     helper);
            }
        } else {
            logger.warn("No process was found in file: " + path.toUri());
        }

        return builder;
    }

    // Protected method for testing
    protected ClassLoader getModuleClassLoader(final KieModule module) {
        return classLoaderHelper.getModuleClassLoader(module);
    }

    private List<BpmnProcessDataEventListener> buildProcessDefinition(String bpmn2Content,
                                                                      ClassLoader moduleClassLoader) throws IllegalArgumentException {
        if (StringUtils.isEmpty(bpmn2Content)) {
            return Collections.<BpmnProcessDataEventListener>emptyList();
        }

        // Set class loader
        KnowledgeBuilder kbuilder = null;
        if (moduleClassLoader != null) {
            KnowledgeBuilderConfigurationImpl pconf = new KnowledgeBuilderConfigurationImpl(moduleClassLoader);
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pconf);
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        }

        // Build
        kbuilder.add(new ByteArrayResource(bpmn2Content.getBytes()),
                     ResourceType.BPMN2);
        if (kbuilder.hasErrors()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                logger.error("Error: {}",
                             error.getMessage());
            }
            logger.debug("Process Cannot be Parsed! \n {} \n",
                         bpmn2Content);
            return Collections.<BpmnProcessDataEventListener>emptyList();
        }

        // Retrieve ProcessInfoHolder
        List<BpmnProcessDataEventListener> processDataList = new ArrayList<>();
        kbuilder.getKnowledgePackages().forEach(
                pkg ->
                        pkg.getProcesses().forEach(
                                p -> {
                                    BpmnProcessDataEventListener processData
                                            = (BpmnProcessDataEventListener) p.getMetaData().get(BpmnProcessDataEventListener.NAME);
                                    processDataList.add(processData);
                                })
        );
        return processDataList;
    }

    protected DefaultIndexBuilder getIndexBuilder(Path path,
                                                  Module module) {
        final Package pkg = moduleService.resolvePackage(Paths.convert(path));
        if (pkg == null) {
            logger.error("Unable to index " + path.toUri().toString() + ": package could not be resolved.");
            return null;
        }

        // responsible for basic index info: module name, branch, etc
        return new DefaultIndexBuilder(Paths.convert(path).getFileName(),
                                       module,
                                       pkg);
    }
}
