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

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.kie.workbench.common.forms.services.backend.util.VFSScanner;
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

    @Inject
    public BPMFinderServiceImpl(@Named("ioStrategy") IOService ioService,
                                KieModuleService moduleService,
                                BPMNFormModelGenerator bpmnFormModelGenerator) {
        this.ioService = ioService;
        this.moduleService = moduleService;
        this.bpmnFormModelGenerator = bpmnFormModelGenerator;
    }

    @Override
    public List<JBPMProcessModel> getAvailableProcessModels(final Path path) {

        return findModels(path, definitions -> true)
                .collect(Collectors.toList());
    }

    @Override
    public JBPMProcessModel getModelForProcess(final String processId, final Path path) {

        return findModels(path, definitions -> matchProcessId(definitions, processId))
                .findFirst()
                .orElse(null);
    }

    private boolean matchProcessId(final Definitions definitions, final String processId) {
        if (definitions == null) {
            return false;
        }

        Process process = bpmnFormModelGenerator.getProcess(definitions);

        if (process == null) {
            return false;
        }

        return process.getId().equals(processId);
    }

    private Stream<JBPMProcessModel> findModels(final Path path, final Predicate<Definitions> filter) {

        Path rootPath = moduleService.resolveModule(path).getRootPath();

        return VFSScanner.scan(ioService, Paths.convert(rootPath), ResourceType.getResourceType("BPMN2").getAllExtensions(), this::toDefinitions, filter)
                .stream()
                .filter(Objects::nonNull)
                .map(VFSScanner.ScanResult::getResource)
                .map(definitions -> parseToModel(definitions, path));
    }

    private JBPMProcessModel parseToModel(final Definitions definitions, final Path path) {

        BusinessProcessFormModel processFormModel = bpmnFormModelGenerator.generateProcessFormModel(definitions, path);
        List<TaskFormModel> taskModels = bpmnFormModelGenerator.generateTaskFormModels(definitions, path);
        return new JBPMProcessModel(processFormModel, taskModels);
    }

    private Definitions toDefinitions(final InputStream in) {
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
            outResource.load(in, options);

            DocumentRoot root = (DocumentRoot) outResource.getContents().get(0);

            return root.getDefinitions();
        } catch (Exception ex) {
            logger.warn("Cannot parse definitions due to", ex);
        }

        return null;
    }
}
