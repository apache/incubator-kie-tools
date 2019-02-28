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

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.util.DroolsResourceFactoryImpl;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.api.io.ResourceType;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.BPMNFormModelGenerator;
import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Service
@Dependent
public class BPMFinderServiceImpl implements BPMFinderService {

    private static final Logger logger = LoggerFactory.getLogger(BPMFinderServiceImpl.class);

    private IOService ioService;

    private KieModuleService moduleService;

    private BPMNFormModelGenerator bpmnFormModelGenerator;
    private FileUtils fileUtils;

    @Inject
    public BPMFinderServiceImpl(@Named("ioStrategy") IOService ioService,
                                KieModuleService moduleService,
                                BPMNFormModelGenerator bpmnFormModelGenerator) {
        this.ioService = ioService;
        this.moduleService = moduleService;
        this.bpmnFormModelGenerator = bpmnFormModelGenerator;
    }

    @PostConstruct
    public void init() {
        fileUtils = FileUtils.getInstance();
    }

    @Override
    public List<JBPMProcessModel> getAvailableProcessModels(final Path path) {

        final GenerationConfig<List<JBPMProcessModel>> operations = new GenerationConfig<>(new ArrayList<>());

        operations.setPredicate(definitions -> definitions.isPresent());

        operations.setConsumer(processModel -> operations.getValue().add(processModel));

        Path rootPath = moduleService.resolveModule(path).getRootPath();

        ResourceType.getResourceType("BPMN2").getAllExtensions().stream().forEach(ext -> scannProcessesForType(rootPath, ext, operations));

        return operations.getValue();
    }

    @Override
    public JBPMProcessModel getModelForProcess(final String processId,
                                               final Path path) {

        GenerationConfig<Optional<JBPMProcessModel>> operations = new GenerationConfig<>(Optional.empty());

        operations.setPredicate(definitions -> {
            if (definitions.isPresent()) {
                if (!operations.getValue().isPresent()) {
                    Optional<Process> optional = Optional.of(bpmnFormModelGenerator.getProcess(definitions.get()));
                    return optional.isPresent() && optional.get().getId().equals(processId);
                }
            }
            return false;
        });

        operations.setConsumer(processModel -> operations.setValue(Optional.ofNullable(processModel)));

        Path rootPath = moduleService.resolveModule(path).getRootPath();

        ResourceType.getResourceType("BPMN2").getAllExtensions().stream()
                .filter(ext -> {
                    scannProcessesForType(rootPath, ext, operations);
                    return operations.getValue().isPresent();
                }).findAny();

        return operations.getValue().orElse(null);
    }

    protected void scannProcessesForType(final Path path,
                                         final String extension,
                                         final GenerationConfig generationConfig) {
        List<org.uberfire.java.nio.file.Path> nioPaths = new ArrayList<>();

        nioPaths.add(Paths.convert(path));

        Collection<FileUtils.ScanResult> processes = fileUtils.scan(ioService,
                                                                    nioPaths,
                                                                    extension,
                                                                    true);

        processes.stream().map(scanResult -> parse(scanResult)).filter(definitions -> definitions != null && generationConfig.getPredicate().test(definitions)).forEach(definitions -> {
            BusinessProcessFormModel processFormModel = bpmnFormModelGenerator.generateProcessFormModel(definitions.get(),
                                                                                                        path);
            List<TaskFormModel> taskModels = bpmnFormModelGenerator.generateTaskFormModels(definitions.get(),
                                                                                           path);
            generationConfig.getConsumer().accept(new JBPMProcessModel(processFormModel,
                                                                       taskModels));
        });
    }

    protected Optional<Definitions> parse(FileUtils.ScanResult process) {
        org.uberfire.java.nio.file.Path formPath = process.getFile();

        try {
            ResourceSet resourceSet = new ResourceSetImpl();

            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put
                    (Resource.Factory.Registry.DEFAULT_EXTENSION,
                     new DroolsResourceFactoryImpl());
            resourceSet.getPackageRegistry().put
                    (DroolsPackage.eNS_URI,
                     DroolsPackage.eINSTANCE);
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                    .put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                         new Bpmn2ResourceFactoryImpl());
            resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL",
                                                 Bpmn2Package.eINSTANCE);

            XMLResource outResource = (XMLResource) resourceSet.createResource(URI.createURI(
                    "inputStream://dummyUriWithValidSuffix.xml"));
            outResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING,
                                                    "UTF-8");
            outResource.setEncoding("UTF-8");

            Map<String, Object> options = new HashMap<String, Object>();
            options.put(XMLResource.OPTION_ENCODING,
                        "UTF-8");
            outResource.load(ioService.newInputStream(formPath),
                             options);

            DocumentRoot root = (DocumentRoot) outResource.getContents().get(0);

            return Optional.of(root.getDefinitions());
        } catch (Exception ex) {
            logger.warn("Error reading process '" + process.getFile().getFileName(),
                        ex);
        }
        return Optional.empty();
    }

    class GenerationConfig<T> {

        private Predicate<Optional<Definitions>> predicate;
        private Consumer<JBPMProcessModel> consumer;

        private T value;

        public GenerationConfig(T value) {
            this.value = value;
        }

        public Predicate<Optional<Definitions>> getPredicate() {
            return predicate;
        }

        public void setPredicate(Predicate<Optional<Definitions>> predicate) {
            this.predicate = predicate;
        }

        public Consumer<JBPMProcessModel> getConsumer() {
            return consumer;
        }

        public void setConsumer(Consumer<JBPMProcessModel> consumer) {
            this.consumer = consumer;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }
}
