/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring;

import java.io.IOException;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.junit.Before;
import org.kie.workbench.common.forms.commons.shared.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.commons.shared.layout.impl.StaticFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
import org.kie.workbench.common.forms.editor.service.shared.model.impl.FormModelSynchronizationUtilImpl;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.AbstractBPMNFormGeneratorServiceTest;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.test.TestFormModelHandlerManager;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BPMNVFSFormDefinitionGeneratorServiceTest extends AbstractBPMNFormGeneratorServiceTest<BPMNVFSFormDefinitionGeneratorService, Path> {

    @Mock
    protected VFSFormFinderService formFinderService;

    @Mock
    protected IOService ioService;

    protected FieldManager fieldManager = new TestFieldManager();
    protected FormLayoutTemplateGenerator templateGenerator = new StaticFormLayoutTemplateGenerator();

    @Mock
    protected DataObjectFinderService dataObjectFinderService;
    protected FormModelHandlerManager formModelHandlerManager;
    protected FormDefinitionSerializer formSerializer = new FormDefinitionSerializerImpl(new FieldSerializer(),
            new FormModelSerializer(),
            new TestMetaDataEntryManager());
    protected SimpleFileSystemProvider simpleFileSystemProvider = null;
    @Mock
    protected CommentedOptionFactory commentedOptionFactory;

    @Mock
    private KieModuleService projectService;

    @Mock
    private KieModule module;

    @Mock
    private ModuleClassLoaderHelper projectClassLoaderHelper;

    @Before
    public void setup() throws IOException {

        when(projectService.resolveModule(any())).thenReturn(module);
        when(projectClassLoaderHelper.getModuleClassLoader(module)).thenReturn(this.getClass().getClassLoader());

        source = mock(Path.class);

        simpleFileSystemProvider = new SimpleFileSystemProvider();
        simpleFileSystemProvider.forceAsDefault();

        when(source.toURI()).thenReturn("default:///src/main/resources/test.frm");

        when(commentedOptionFactory.makeCommentedOption(anyString())).then(invocationOnMock -> new CommentedOption("1",
                invocationOnMock.getArguments()[0].toString()));

        formModelHandlerManager = new TestFormModelHandlerManager(projectService,
                projectClassLoaderHelper,
                fieldManager,
                dataObjectFinderService);

        service = new BPMNVFSFormDefinitionGeneratorService(fieldManager,
                formModelHandlerManager,
                formFinderService,
                formSerializer,
                ioService,
                commentedOptionFactory,
                new FormModelSynchronizationUtilImpl(fieldManager,
                        templateGenerator));
    }
}
