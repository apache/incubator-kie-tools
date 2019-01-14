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

package org.drools.workbench.screens.dtablexls.backend.server.conversion;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.template.parser.DecisionTableParseException;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.drools.workbench.screens.drltext.type.DRLResourceTypeDefinition;
import org.drools.workbench.screens.dtablexls.type.DecisionTableXLSResourceTypeDefinition;
import org.drools.workbench.screens.dtablexls.type.DecisionTableXLSXResourceTypeDefinition;
import org.drools.workbench.screens.globals.model.GlobalsModel;
import org.drools.workbench.screens.globals.service.GlobalsEditorService;
import org.drools.workbench.screens.globals.type.GlobalResourceTypeDefinition;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DecisionTableXLSToDecisionTableGuidedConverterTest {

    @Mock
    private IOService ioService;

    @Mock
    private DRLTextEditorService drlService;

    @Mock
    private GuidedDecisionTableEditorService guidedDecisionTableService;

    @Mock
    private GlobalsEditorService globalsService;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private ProjectImportsService importsService;

    @Mock
    private MetadataService metadataService;

    @Mock
    private DataModelerService modellerService;

    @Mock
    private DataModelService dataModelService;

    @Mock
    private AppConfigService appConfigService;

    @Mock
    private Path path;

    @Mock
    private PackageDataModelOracle dmo;
    private Map<String, ModelField[]> packageModelFields = new HashMap<String, ModelField[]>();

    @Mock
    private KieModule module;

    @Mock
    private Path expectedProjectImportsPath;

    private DecisionTableXLSResourceTypeDefinition xlsDTableType = new DecisionTableXLSResourceTypeDefinition(new Decision());
    private DecisionTableXLSXResourceTypeDefinition xlsxDTableType = new DecisionTableXLSXResourceTypeDefinition(new Decision());
    private GuidedDTableResourceTypeDefinition guidedDTableType = new GuidedDTableResourceTypeDefinition(new Decision());
    private DRLResourceTypeDefinition drlType = new DRLResourceTypeDefinition(new Decision());
    private GlobalResourceTypeDefinition globalsType = new GlobalResourceTypeDefinition(new Decision());

    private DecisionTableXLSToDecisionTableGuidedConverter converter;

    @BeforeClass
    public static void setup() {
        setupPreferences();
        setupSystemProperties();
    }

    private static void setupPreferences() {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd-MM-yyyy");
        }};
        ApplicationPreferences.setUp(preferences);
    }

    private static void setupSystemProperties() {
        System.setProperty("org.uberfire.nio.git.daemon.enabled",
                           "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled",
                           "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled",
                           "true");
    }

    @Before
    public void setupMocks() {
        converter = new DecisionTableXLSToDecisionTableGuidedConverter(ioService,
                                                                       drlService,
                                                                       guidedDecisionTableService,
                                                                       globalsService,
                                                                       moduleService,
                                                                       importsService,
                                                                       metadataService,
                                                                       modellerService,
                                                                       dataModelService,
                                                                       appConfigService,
                                                                       xlsDTableType,
                                                                       xlsxDTableType,
                                                                       guidedDTableType,
                                                                       drlType,
                                                                       globalsType);
        when(path.toURI()).thenReturn("default://src/main/resources/p0/source.xls");
        when(path.getFileName()).thenReturn("source.xls");
        when(dataModelService.getDataModel(eq(path))).thenReturn(dmo);

        when(dmo.getPackageName()).thenReturn("org.test");
        when(dmo.getModuleModelFields()).thenReturn(packageModelFields);

        when(moduleService.resolveModule(any(Path.class))).thenReturn(module);
        when(module.getImportsPath()).thenReturn(expectedProjectImportsPath);
        when(expectedProjectImportsPath.toURI()).thenReturn("default://project.imports");
    }

    @Test
    //https://bugzilla.redhat.com/show_bug.cgi?id=1310208
    public void testGlobalGeneration() {
        final InputStream is = this.getClass().getResourceAsStream("BZ1310208.xls");
        when(ioService.newInputStream(any(org.uberfire.java.nio.file.Path.class))).thenReturn(is);
        final ConversionResult result = converter.convert(path);
        assertNotNull(result);

        final ArgumentCaptor<GlobalsModel> globalsModelArgumentCaptor = ArgumentCaptor.forClass(GlobalsModel.class);
        verify(globalsService,
               times(1)).create(any(Path.class),
                                any(String.class),
                                globalsModelArgumentCaptor.capture(),
                                any(String.class));
        assertNotNull(globalsModelArgumentCaptor.getValue());
        final GlobalsModel globalsModel = globalsModelArgumentCaptor.getValue();
        assertEquals(1,
                     globalsModel.getGlobals().size());
        assertEquals("list",
                     globalsModel.getGlobals().get(0).getAlias());
        assertEquals("java.util.List",
                     globalsModel.getGlobals().get(0).getClassName());

        verify(guidedDecisionTableService,
               times(1)).create(any(Path.class),
                                any(String.class),
                                any(GuidedDecisionTable52.class),
                                any(String.class));

        verify(drlService,
               never()).create(any(Path.class),
                               any(String.class),
                               any(String.class),
                               any(String.class));
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2478
    public void testImportGeneration() {

        final InputStream is = this.getClass().getResourceAsStream("GUVNOR-2478.xls");
        when(ioService.newInputStream(any(org.uberfire.java.nio.file.Path.class))).thenReturn(is);
        final ConversionResult result = converter.convert(path);
        assertNotNull(result);

        final ArgumentCaptor<Path> projectImportsPathArgumentCaptor = ArgumentCaptor.forClass(Path.class);
        final ArgumentCaptor<ProjectImports> projectImportsArgumentCaptor = ArgumentCaptor.forClass(ProjectImports.class);
        verify(importsService,
               times(1)).save(projectImportsPathArgumentCaptor.capture(),
                              projectImportsArgumentCaptor.capture(),
                              any(Metadata.class),
                              any(String.class));
        assertNotNull(projectImportsPathArgumentCaptor.getValue());
        final Path actualProjectImportsPath = projectImportsPathArgumentCaptor.getValue();
        assertEquals(expectedProjectImportsPath.toURI(),
                     actualProjectImportsPath.toURI());

        assertNotNull(projectImportsArgumentCaptor.getValue());
        final ProjectImports projectImports = projectImportsArgumentCaptor.getValue();
        assertEquals(1,
                     projectImports.getImports().getImports().size());
        assertEquals("java.util.List",
                     projectImports.getImports().getImports().get(0).getType());

        verify(guidedDecisionTableService,
               times(1)).create(any(Path.class),
                                any(String.class),
                                any(GuidedDecisionTable52.class),
                                any(String.class));
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2696
    public void checkConversionOfXLSXFiles() {
        final InputStream is = this.getClass().getResourceAsStream("GUVNOR-2696.xlsx");
        when(ioService.newInputStream(any(org.uberfire.java.nio.file.Path.class))).thenReturn(is);
        final ConversionResult result = converter.convert(path);
        assertNotNull(result);
        assertEquals(1,
                     result.getMessages().size());
        assertTrue(result.getMessages().get(0).getMessage().startsWith("Created Guided Decision Table 'Weather"));

        verify(guidedDecisionTableService,
               times(1)).create(any(Path.class),
                                any(String.class),
                                any(GuidedDecisionTable52.class),
                                any(String.class));
    }

    @Test(expected = DecisionTableParseException.class)
    public void checkConversionOfXLSWithInvalidContent() {
        final InputStream is = this.getClass().getResourceAsStream("wrong_file.xls");
        when(ioService.newInputStream(any(org.uberfire.java.nio.file.Path.class))).thenReturn(is);
        converter.convert(path);
    }
}
