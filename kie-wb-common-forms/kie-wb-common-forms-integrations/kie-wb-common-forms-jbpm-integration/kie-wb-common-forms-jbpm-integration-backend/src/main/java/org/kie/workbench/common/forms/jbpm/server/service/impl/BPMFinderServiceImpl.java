/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.guvnor.common.services.project.model.Project;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.util.DroolsResourceFactoryImpl;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.BPMNFormModelGenerator;
import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Service
@Dependent
public class BPMFinderServiceImpl implements BPMFinderService {

    private IOService ioService;

    private KieProjectService projectService;

    private BPMNFormModelGenerator bpmnFormModelGenerator;

    @Inject
    public BPMFinderServiceImpl( @Named( "ioStrategy" ) IOService ioService,
                                 KieProjectService projectService,
                                 BPMNFormModelGenerator bpmnFormModelGenerator ) {
        this.ioService = ioService;
        this.projectService = projectService;
        this.bpmnFormModelGenerator = bpmnFormModelGenerator;
    }

    @Override
    public List<JBPMProcessModel> getAvailableProcessModels( Path path ) {

        Project project = projectService.resolveProject( path );

        FileUtils utils = FileUtils.getInstance();

        List<org.uberfire.java.nio.file.Path> nioPaths = new ArrayList<>();

        nioPaths.add( Paths.convert( project.getRootPath() ) );

        Collection<FileUtils.ScanResult> processes = utils.scan( ioService, nioPaths, "bpmn2", true );

        List<JBPMProcessModel> result = new ArrayList<>();

        for ( FileUtils.ScanResult process : processes ) {
            org.uberfire.java.nio.file.Path formPath = process.getFile();

            try {
                ResourceSet resourceSet = new ResourceSetImpl();

                resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put
                        ( Resource.Factory.Registry.DEFAULT_EXTENSION,
                          new DroolsResourceFactoryImpl() );
                resourceSet.getPackageRegistry().put
                        ( DroolsPackage.eNS_URI,
                          DroolsPackage.eINSTANCE );
                resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                        .put( Resource.Factory.Registry.DEFAULT_EXTENSION, new Bpmn2ResourceFactoryImpl() );
                resourceSet.getPackageRegistry().put( "http://www.omg.org/spec/BPMN/20100524/MODEL",
                                                      Bpmn2Package.eINSTANCE );

                XMLResource outResource = (XMLResource) resourceSet.createResource( URI.createURI(
                        "inputStream://dummyUriWithValidSuffix.xml" ) );
                outResource.getDefaultLoadOptions().put( XMLResource.OPTION_ENCODING, "UTF-8" );
                outResource.setEncoding( "UTF-8" );

                Map<String, Object> options = new HashMap<String, Object>();
                options.put( XMLResource.OPTION_ENCODING, "UTF-8" );
                outResource.load( ioService.newInputStream( formPath ), options );

                DocumentRoot root = (DocumentRoot) outResource.getContents().get( 0 );

                Definitions definitions = root.getDefinitions();

                BusinessProcessFormModel processFormModel = bpmnFormModelGenerator.generateProcessFormModel( definitions );
                List<TaskFormModel> taskModels = bpmnFormModelGenerator.generateTaskFormModels( definitions );

                result.add( new JBPMProcessModel( processFormModel, taskModels ) );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
