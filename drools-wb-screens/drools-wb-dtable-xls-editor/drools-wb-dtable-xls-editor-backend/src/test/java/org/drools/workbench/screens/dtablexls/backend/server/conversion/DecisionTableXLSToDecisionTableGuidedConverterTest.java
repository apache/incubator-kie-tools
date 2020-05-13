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
import java.util.List;
import java.util.Map;

import org.drools.template.parser.DecisionTableParseException;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessage;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
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
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
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

    @Captor
    private ArgumentCaptor<GuidedDecisionTable52> decisionTableArgumentCaptor;

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
        converter = spy(new DecisionTableXLSToDecisionTableGuidedConverter(ioService,
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
                                                                           globalsType));
        when(path.toURI()).thenReturn("file://src/main/resources/p0/source.xls");
        when(path.getFileName()).thenReturn("source.xls");
        when(dataModelService.getDataModel(eq(path))).thenReturn(dmo);

        when(dmo.getPackageName()).thenReturn("org.test");
        when(dmo.getModuleModelFields()).thenReturn(packageModelFields);

        when(moduleService.resolveModule(any(Path.class))).thenReturn(module);
        when(module.getImportsPath()).thenReturn(expectedProjectImportsPath);
        when(expectedProjectImportsPath.toURI()).thenReturn("file://project.imports");
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

    @Test
    public void checkConversionOfXLSXFilesRHDM1159() {
        final InputStream is = this.getClass().getResourceAsStream("RHDM-1159.xls");
        when(ioService.newInputStream(any(org.uberfire.java.nio.file.Path.class))).thenReturn(is);

        //Mock imports to match project attached to JIRA
        final ProjectImports projectImports = new ProjectImports();
        projectImports.getImports().addImport(new Import("com.aia.model.underWriting.Benefit"));
        projectImports.getImports().addImport(new Import("com.aia.model.underWriting.Plans"));
        projectImports.getImports().addImport(new Import("com.aia.model.underWriting.CaseInfo"));
        projectImports.getImports().addImport(new Import("com.aia.model.underWriting.UnderwritingRuleRequest"));
        projectImports.getImports().addImport(new Import("com.aia.model.underWriting.UnderwritingRuleResponse"));
        doReturn(projectImports).when(converter).loadProjectImports(eq(expectedProjectImportsPath));

        final ConversionResult result = converter.convert(path);
        assertNotNull(result);

        final List<ConversionMessage> messages = result.getMessages();
        assertEquals(1,
                     messages.size());
        assertTrue(messages.get(0).getMessage().startsWith("Created Guided Decision Table 'SampleAUWRule"));

        verify(guidedDecisionTableService,
               times(1)).create(any(Path.class),
                                any(String.class),
                                decisionTableArgumentCaptor.capture(),
                                any(String.class));

        final GuidedDecisionTable52 dtable = decisionTableArgumentCaptor.getValue();
        assertNotNull(dtable);

        //Check attributes
        final List<AttributeCol52> attributes = dtable.getAttributeCols();
        assertEquals(1, attributes.size());
        assertEquals(Attribute.RULEFLOW_GROUP.getAttributeName(), attributes.get(0).getAttribute());

        //Check LHS
        final List<CompositeColumn<? extends BaseColumn>> conditions = dtable.getConditions();
        assertEquals(3, conditions.size());

        assertTrue(conditions.get(0) instanceof BRLConditionColumn);
        final BRLConditionColumn condition0 = (BRLConditionColumn) conditions.get(0);
        assertEquals(1, condition0.getDefinition().size());
        assertTrue(condition0.getDefinition().get(0) instanceof FactPattern);
        final FactPattern condition0FactPattern = (FactPattern) condition0.getDefinition().get(0);
        assertEquals("UnderwritingRuleRequest", condition0FactPattern.getFactType());
        assertEquals(1, condition0FactPattern.getNumberOfConstraints());
        final FieldConstraint condition0FactPatternConstraint = condition0FactPattern.getConstraint(0);
        assertTrue(condition0FactPatternConstraint instanceof SingleFieldConstraint);
        final SingleFieldConstraint condition0FactPatternSingleFieldConstraint = (SingleFieldConstraint) condition0FactPattern.getConstraint(0);
        assertEquals("traceId", condition0FactPatternSingleFieldConstraint.getFieldName());
        assertEquals("==", condition0FactPatternSingleFieldConstraint.getOperator());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE, condition0FactPatternSingleFieldConstraint.getConstraintValueType());
        assertEquals("param1", condition0FactPatternSingleFieldConstraint.getValue());

        assertEquals(1, condition0.getChildColumns().size());
        final BRLConditionVariableColumn condition0Variable = condition0.getChildColumns().get(0);
        assertEquals("param1", condition0Variable.getVarName());
        assertEquals("UnderwritingRuleRequest", condition0Variable.getFactType());
        assertEquals("traceId", condition0Variable.getFactField());

        assertTrue(conditions.get(1) instanceof BRLConditionColumn);
        final BRLConditionColumn condition1 = (BRLConditionColumn) conditions.get(1);
        assertEquals(1, condition1.getDefinition().size());
        assertTrue(condition1.getDefinition().get(0) instanceof FromCompositeFactPattern);
        final FromCompositeFactPattern condition1FromCompositeFactPattern = (FromCompositeFactPattern) condition1.getDefinition().get(0);
        final FactPattern condition1FactPattern = condition1FromCompositeFactPattern.getFactPattern();
        assertEquals("CaseInfo", condition1FactPattern.getFactType());
        assertEquals(1, condition1FactPattern.getNumberOfConstraints());
        final FieldConstraint condition1FactPatternConstraint = condition1FactPattern.getConstraint(0);
        assertTrue(condition1FactPatternConstraint instanceof SingleFieldConstraint);
        final SingleFieldConstraint condition1FactPatternSingleFieldConstraint = (SingleFieldConstraint) condition1FactPattern.getConstraint(0);
        assertEquals("channel", condition1FactPatternSingleFieldConstraint.getFieldName());
        assertEquals("in", condition1FactPatternSingleFieldConstraint.getOperator());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE, condition1FactPatternSingleFieldConstraint.getConstraintValueType());
        assertEquals("param2", condition1FactPatternSingleFieldConstraint.getValue());

        assertEquals(1, condition1.getChildColumns().size());
        final BRLConditionVariableColumn condition1Variable = condition1.getChildColumns().get(0);
        assertEquals("param2", condition1Variable.getVarName());
        assertEquals("CaseInfo", condition1Variable.getFactType());
        assertEquals("channel", condition1Variable.getFactField());

        assertTrue(conditions.get(2) instanceof BRLConditionColumn);
        final BRLConditionColumn condition2 = (BRLConditionColumn) conditions.get(2);
        assertEquals(1, condition2.getDefinition().size());
        assertTrue(condition2.getDefinition().get(0) instanceof FreeFormLine);
        final FreeFormLine condition2FreeFormLine = (FreeFormLine) condition2.getDefinition().get(0);
        assertEquals("plan :Plans();bn:Benefit(benefitCode  in (\"@{param3}\")) from plan.benefit;\n", condition2FreeFormLine.getText());

        assertEquals(1, condition2.getChildColumns().size());
        final BRLConditionVariableColumn condition2Variable = condition2.getChildColumns().get(0);
        assertEquals("param3", condition2Variable.getVarName());
        assertEquals("Benefit", condition2Variable.getFactType());
        assertEquals("benefitCode", condition2Variable.getFactField());

        //Check RHS
        final List<ActionCol52> actions = dtable.getActionCols();
        assertEquals(1, actions.size());

        assertTrue(actions.get(0) instanceof BRLActionColumn);
        final BRLActionColumn action0 = (BRLActionColumn) actions.get(0);
        assertTrue(action0.getDefinition().get(0) instanceof FreeFormLine);
        final FreeFormLine action0FreeFormLine = (FreeFormLine) action0.getDefinition().get(0);
        assertEquals("UnderwritingRuleResponse uwr= new UnderwritingRuleResponse();uwr.setAuwStatus(@{param4});", action0FreeFormLine.getText());

        assertEquals(1, action0.getChildColumns().size());
        final BRLActionVariableColumn action0Variable = action0.getChildColumns().get(0);
        assertEquals("param4", action0Variable.getVarName());

        //Check data
        final List<List<DTCellValue52>> data = dtable.getData();
        assertEquals(5, data.size());
        assertRHDM1159RowData(data.get(0), "abc", "AGENT", "RB", "\"FAIL\"");
        assertRHDM1159RowData(data.get(1), "sdf", "AGENT", "RB", "\"FAIL\"");
        assertRHDM1159RowData(data.get(2), "dsds", "AGENT", "RB", "\"FAIL\"");
        assertRHDM1159RowData(data.get(3), "sdddv", "AGENT", "RB", "\"FAIL\"");
        assertRHDM1159RowData(data.get(4), "", "", "", "");
    }

    private void assertRHDM1159RowData(final List<DTCellValue52> rowData,
                                       final String cell1Value,
                                       final String cell2Value,
                                       final String cell4Value,
                                       final String cell5Value) {
        assertEquals(7, rowData.size());
        assertEquals(cell1Value, rowData.get(3).getStringValue());
        assertEquals(cell2Value, rowData.get(4).getStringValue());
        assertEquals(cell4Value, rowData.get(5).getStringValue());
        assertEquals(cell5Value, rowData.get(6).getStringValue());
    }

    @Test(expected = DecisionTableParseException.class)
    public void checkConversionOfXLSWithInvalidContent() {
        final InputStream is = this.getClass().getResourceAsStream("wrong_file.xls");
        when(ioService.newInputStream(any(org.uberfire.java.nio.file.Path.class))).thenReturn(is);
        converter.convert(path);
    }
}
