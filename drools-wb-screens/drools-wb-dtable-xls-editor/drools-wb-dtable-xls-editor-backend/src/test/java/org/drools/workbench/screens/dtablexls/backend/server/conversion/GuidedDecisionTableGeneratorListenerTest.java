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
package org.drools.workbench.screens.dtablexls.backend.server.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DataListener;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessageType;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the conversion of XLS Decision Tables to Guided Decision Tables
 */
@RunWith(MockitoJUnitRunner.class)
public class GuidedDecisionTableGeneratorListenerTest {

    private PackageDataModelOracle dmo;
    private Map<String, ModelField[]> packageModelFields = new HashMap<String, ModelField[]>();

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
    public void setupMocks() throws Exception {
        dmo = mock(PackageDataModelOracle.class);
        when(dmo.getPackageName()).thenReturn("org.test");
        when(dmo.getModuleModelFields()).thenReturn(packageModelFields);
    }

    @After
    public void cleanUp() throws Exception {
        packageModelFields.clear();
    }

    @Test
    public void testAttributes() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("Attributes.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("AttributesTest",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(12,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof AttributeCol52);
        assertTrue(columns.get(3) instanceof AttributeCol52);
        assertTrue(columns.get(4) instanceof AttributeCol52);
        assertTrue(columns.get(5) instanceof AttributeCol52);
        assertTrue(columns.get(6) instanceof AttributeCol52);
        assertTrue(columns.get(7) instanceof AttributeCol52);
        assertTrue(columns.get(8) instanceof AttributeCol52);
        assertTrue(columns.get(9) instanceof AttributeCol52);
        assertTrue(columns.get(10) instanceof AttributeCol52);
        assertTrue(columns.get(11) instanceof AttributeCol52);

        //Check individual attributes
        AttributeCol52 attrCol2 = ((AttributeCol52) columns.get(2));
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     attrCol2.getAttribute());
        assertFalse(attrCol2.isUseRowNumber());
        assertFalse(attrCol2.isReverseOrder());

        AttributeCol52 attrCol3 = ((AttributeCol52) columns.get(3));
        assertEquals(Attribute.ACTIVATION_GROUP.getAttributeName(),
                     attrCol3.getAttribute());

        AttributeCol52 attrCol4 = ((AttributeCol52) columns.get(4));
        assertEquals(Attribute.DURATION.getAttributeName(),
                     attrCol4.getAttribute());

        AttributeCol52 attrCol5 = ((AttributeCol52) columns.get(5));
        assertEquals(Attribute.TIMER.getAttributeName(),
                     attrCol5.getAttribute());

        AttributeCol52 attrCol6 = ((AttributeCol52) columns.get(6));
        assertEquals(Attribute.CALENDARS.getAttributeName(),
                     attrCol6.getAttribute());

        AttributeCol52 attrCol7 = ((AttributeCol52) columns.get(7));
        assertEquals(Attribute.NO_LOOP.getAttributeName(),
                     attrCol7.getAttribute());

        AttributeCol52 attrCol8 = ((AttributeCol52) columns.get(8));
        assertEquals(Attribute.LOCK_ON_ACTIVE.getAttributeName(),
                     attrCol8.getAttribute());

        AttributeCol52 attrCol9 = ((AttributeCol52) columns.get(9));
        assertEquals(Attribute.AUTO_FOCUS.getAttributeName(),
                     attrCol9.getAttribute());

        AttributeCol52 attrCol10 = ((AttributeCol52) columns.get(10));
        assertEquals(Attribute.AGENDA_GROUP.getAttributeName(),
                     attrCol10.getAttribute());

        AttributeCol52 attrCol11 = ((AttributeCol52) columns.get(11));
        assertEquals(Attribute.RULEFLOW_GROUP.getAttributeName(),
                     attrCol11.getAttribute());

        //Check data
        assertEquals(2,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Specific rule 1", 1, "g1", 100l, "T1", "CAL1", true, true, true, "AG1", "RFG1"},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Specific rule 2", 2, "g2", 200l, "T2", "CAL2", false, false, false, "AG2", "RFG2"},
                                   dtable.getData().get(1)));
    }

    @Test
    public void testSequentialSalience() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("SequentialSalience.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("SequentialSalienceTest",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(3,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof AttributeCol52);

        //Check attribute column
        AttributeCol52 attrCol2 = ((AttributeCol52) columns.get(2));
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     attrCol2.getAttribute());
        assertTrue(attrCol2.isUseRowNumber());
        assertTrue(attrCol2.isReverseOrder());

        //Check data
        assertEquals(2,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Rule 1", 2},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Rule 2", 1},
                                   dtable.getData().get(1)));
    }

    @Test
    public void testSalienceWarnings() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("SalienceWarnings.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(2,
                     result.getMessages().size());
        assertEquals(ConversionMessageType.WARNING,
                     result.getMessages().get(0).getMessageType());
        assertFalse(result.getMessages().get(0).getMessage().indexOf("Priority is not an integer literal, in cell C7") == -1);
        assertEquals(ConversionMessageType.WARNING,
                     result.getMessages().get(1).getMessageType());
        assertFalse(result.getMessages().get(1).getMessage().indexOf("Priority is not an integer literal, in cell C8") == -1);

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("SalienceWarningsTest",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(3,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof AttributeCol52);

        //Check attribute column
        AttributeCol52 attrCol2 = ((AttributeCol52) columns.get(2));
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     attrCol2.getAttribute());
        assertFalse(attrCol2.isUseRowNumber());
        assertFalse(attrCol2.isReverseOrder());

        //Check data
        assertEquals(2,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Rule 1", 0},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Rule 2", 0},
                                   dtable.getData().get(1)));
    }

    @Test
    public void testDurationWarnings() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("DurationWarnings.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(2,
                     result.getMessages().size());
        assertEquals(ConversionMessageType.WARNING,
                     result.getMessages().get(0).getMessageType());
        assertFalse(result.getMessages().get(0).getMessage().indexOf("Duration is not an long literal, in cell C7") == -1);
        assertEquals(ConversionMessageType.WARNING,
                     result.getMessages().get(1).getMessageType());
        assertFalse(result.getMessages().get(1).getMessage().indexOf("Duration is not an long literal, in cell C8") == -1);

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("DurationWarningsTest",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(3,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof AttributeCol52);

        //Check attribute column
        AttributeCol52 attrCol2 = ((AttributeCol52) columns.get(2));
        assertEquals(Attribute.DURATION.getAttributeName(),
                     attrCol2.getAttribute());
        assertFalse(attrCol2.isUseRowNumber());
        assertFalse(attrCol2.isReverseOrder());

        //Check data
        assertEquals(2,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Rule 1", 0},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Rule 2", 0},
                                   dtable.getData().get(1)));
    }

    @Test
    public void testMetadata() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("Metadata.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("MetadataTest",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(3,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof MetadataCol52);

        //Check metadata column
        MetadataCol52 mdCol2 = ((MetadataCol52) columns.get(2));
        assertEquals("cheese",
                     mdCol2.getMetadata());

        //Check data
        assertEquals(2,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Rule 1", "cheddar"},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Rule 2", "edam"},
                                   dtable.getData().get(1)));
    }

    @Test
    //https://bugzilla.redhat.com/show_bug.cgi?id=1310208
    public void testGlobalsConversion() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("BZ1310208.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        final List<Global> globals = listener.getGlobals();
        assertNotNull(globals);
        assertEquals(1,
                     globals.size());

        final Global global = globals.get(0);
        assertEquals("list",
                     global.getIdentifier());
        assertEquals("List",
                     global.getClassName());
    }

    @Test
    public void testActions() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("Actions.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("ActionsTest",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(7,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(3) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(4) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(5) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(6) instanceof BRLActionVariableColumn);

        //Check individual action columns
        assertEquals(4,
                     dtable.getActionCols().size());
        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);
        assertTrue(dtable.getActionCols().get(1) instanceof BRLActionColumn);
        assertTrue(dtable.getActionCols().get(2) instanceof BRLActionColumn);
        assertTrue(dtable.getActionCols().get(3) instanceof BRLActionColumn);

        //Column 1
        BRLActionColumn actionCol0 = ((BRLActionColumn) dtable.getActionCols().get(0));
        assertEquals("Converted from ['Multi-parameters', 'Multi-parameters']",
                     actionCol0.getHeader());
        assertEquals(2,
                     actionCol0.getChildColumns().size());

        List<IAction> actionCol0definition = actionCol0.getDefinition();
        assertEquals(1,
                     actionCol0definition.size());
        assertTrue(actionCol0definition.get(0) instanceof FreeFormLine);

        FreeFormLine actionCol0ffl = (FreeFormLine) actionCol0definition.get(0);
        assertEquals("policy.setBasePrice(@{param1}, @{param2});",
                     actionCol0ffl.getText());

        //Column 1 - Variable 1
        BRLActionVariableColumn actionCol0param0 = actionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     actionCol0param0.getVarName());
        assertEquals("Multi-parameters",
                     actionCol0param0.getHeader());
        assertEquals(DataType.TYPE_OBJECT,
                     actionCol0param0.getFieldType());
        assertNull(actionCol0param0.getFactType());
        assertNull(actionCol0param0.getFactField());

        //Column 1 - Variable 2
        BRLActionVariableColumn actionCol0param1 = actionCol0.getChildColumns().get(1);
        assertEquals("param2",
                     actionCol0param1.getVarName());
        assertEquals("Multi-parameters",
                     actionCol0param1.getHeader());
        assertEquals(DataType.TYPE_OBJECT,
                     actionCol0param1.getFieldType());
        assertNull(actionCol0param1.getFactType());
        assertNull(actionCol0param1.getFactField());

        //Column 2
        BRLActionColumn actionCol1 = ((BRLActionColumn) dtable.getActionCols().get(1));
        assertEquals("Converted from ['Single-parameter']",
                     actionCol1.getHeader());
        assertEquals(1,
                     actionCol1.getChildColumns().size());

        List<IAction> actionCol1definition = actionCol1.getDefinition();
        assertEquals(1,
                     actionCol1definition.size());
        assertTrue(actionCol1definition.get(0) instanceof FreeFormLine);

        FreeFormLine actionCol1ffl = (FreeFormLine) actionCol1definition.get(0);
        assertEquals("policy.setSmurf(@{param3});",
                     actionCol1ffl.getText());

        //Column 2 - Variable 1
        BRLActionVariableColumn actionCol1param0 = actionCol1.getChildColumns().get(0);
        assertEquals("param3",
                     actionCol1param0.getVarName());
        assertEquals("Single-parameter",
                     actionCol1param0.getHeader());
        assertEquals(DataType.TYPE_OBJECT,
                     actionCol1param0.getFieldType());
        assertNull(actionCol1param0.getFactType());
        assertNull(actionCol1param0.getFactField());

        //Column 3
        BRLActionColumn actionCol2 = ((BRLActionColumn) dtable.getActionCols().get(2));
        assertEquals("Converted from ['Log-single-parameter']",
                     actionCol2.getHeader());
        assertEquals(1,
                     actionCol2.getChildColumns().size());

        List<IAction> actionCol2definition = actionCol2.getDefinition();
        assertEquals(1,
                     actionCol2definition.size());
        assertTrue(actionCol2definition.get(0) instanceof FreeFormLine);

        FreeFormLine actionCol2ffl = (FreeFormLine) actionCol2definition.get(0);
        assertEquals("System.out.println(\"@{param4}\");",
                     actionCol2ffl.getText());

        //Column 3 - Variable 1
        BRLActionVariableColumn actionCol2param0 = actionCol2.getChildColumns().get(0);
        assertEquals("param4",
                     actionCol2param0.getVarName());
        assertEquals("Log-single-parameter",
                     actionCol2param0.getHeader());
        assertEquals(DataType.TYPE_OBJECT,
                     actionCol2param0.getFieldType());
        assertNull(actionCol2param0.getFactType());
        assertNull(actionCol2param0.getFactField());

        //Column 4
        BRLActionColumn actionCol3 = ((BRLActionColumn) dtable.getActionCols().get(3));
        assertEquals("Converted from ['Zero-parameters']",
                     actionCol3.getHeader());
        assertEquals(1,
                     actionCol3.getChildColumns().size());

        List<IAction> actionCol3definition = actionCol3.getDefinition();
        assertEquals(1,
                     actionCol3definition.size());
        assertTrue(actionCol3definition.get(0) instanceof FreeFormLine);

        FreeFormLine actionCol3ffl = (FreeFormLine) actionCol3definition.get(0);
        assertEquals("System.out.println(\"Woot\");",
                     actionCol3ffl.getText());

        //Column 3 - Variable 1
        BRLActionVariableColumn actionCol3param0 = actionCol3.getChildColumns().get(0);
        assertEquals("",
                     actionCol3param0.getVarName());
        assertEquals("Zero-parameters",
                     actionCol3param0.getHeader());
        assertEquals(DataType.TYPE_BOOLEAN,
                     actionCol3param0.getFieldType());
        assertNull(actionCol3param0.getFactType());
        assertNull(actionCol3param0.getFactField());

        //Check data
        assertEquals(2,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Row 1", "10", "20", "30", "hello", true},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Row 2", "50", "60", "70", "goodbye", false},
                                   dtable.getData().get(1)));
    }

    @Test
    public void testConditions() {
        final List<Object[]> expectedRows = new ArrayList<Object[]>(2);
        expectedRows.add(new Object[]{1, "Row 1", 20, "Mike", "Brown", "BMW", "M3"});
        expectedRows.add(new Object[]{2, "Row 2", 30, "Jason", "Grey", "Audi", "S4"});
        conditionsTest("Conditions.xls",
                       expectedRows);
    }

    @Test
    public void testConditionsIndexedParameters() {
        final List<Object[]> expectedRows = new ArrayList<Object[]>(2);
        expectedRows.add(new Object[]{1, "Row 1", 20, "Mike", "Brown", "BMW", "M3"});
        expectedRows.add(new Object[]{2, "Row 2", 30, "Jason", "Grey", "", ""});
        conditionsTest("Conditions-indexedParameters.xls",
                       expectedRows);
    }

    private void conditionsTest(final String xlsFileName,
                                final List<Object[]> expectedRows) {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();

        addModelField("org.test.Driver",
                      "this",
                      "org.test.Driver",
                      DataType.TYPE_THIS);
        addModelField("org.test.Driver",
                      "age",
                      Integer.class.getName(),
                      DataType.TYPE_NUMERIC_INTEGER);
        addModelField("org.test.Driver",
                      "firstName",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Driver",
                      "surname",
                      String.class.getName(),
                      DataType.TYPE_STRING);

        addModelField("org.test.Vehicle",
                      "this",
                      "org.test.Vehicle",
                      DataType.TYPE_THIS);
        addModelField("org.test.Vehicle",
                      "make",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Vehicle",
                      "model",
                      String.class.getName(),
                      DataType.TYPE_STRING);

        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream(xlsFileName);

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("ConditionsTest",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(7,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(3) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(4) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(5) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(6) instanceof BRLConditionVariableColumn);

        //Check individual condition columns
        assertEquals(2,
                     dtable.getConditions().size());
        assertTrue(dtable.getConditions().get(0) instanceof BRLConditionColumn);
        assertTrue(dtable.getConditions().get(1) instanceof BRLConditionColumn);

        //Column 1
        BRLConditionColumn conditionCol0 = ((BRLConditionColumn) dtable.getConditions().get(0));
        assertEquals("Converted from ['Age of driver', 'First name', 'Surname']",
                     conditionCol0.getHeader());
        assertEquals(3,
                     conditionCol0.getChildColumns().size());

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals(1,
                     conditionCol0definition.size());
        assertTrue(conditionCol0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol0fp = (FactPattern) conditionCol0definition.get(0);
        assertEquals("Driver",
                     conditionCol0fp.getFactType());
        assertEquals(3,
                     conditionCol0fp.getNumberOfConstraints());
        assertTrue(conditionCol0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc0 = (SingleFieldConstraint) conditionCol0fp.getConstraint(0);
        assertEquals("age",
                     conditionCol0fpsfc0.getFieldName());
        assertEquals(">",
                     conditionCol0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0fpsfc0.getFieldType());

        assertTrue(conditionCol0fp.getConstraint(1) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc1 = (SingleFieldConstraint) conditionCol0fp.getConstraint(1);
        assertEquals("firstName",
                     conditionCol0fpsfc1.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc1.getOperator());
        assertEquals("param2",
                     conditionCol0fpsfc1.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc1.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0fpsfc1.getFieldType());

        assertTrue(conditionCol0fp.getConstraint(2) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc2 = (SingleFieldConstraint) conditionCol0fp.getConstraint(2);
        assertEquals("surname",
                     conditionCol0fpsfc2.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc2.getOperator());
        assertEquals("param3",
                     conditionCol0fpsfc2.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc2.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0fpsfc2.getFieldType());

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0param0.getVarName());
        assertEquals("Age of driver",
                     conditionCol0param0.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0param0.getFieldType());
        assertEquals("Driver",
                     conditionCol0param0.getFactType());
        assertEquals("age",
                     conditionCol0param0.getFactField());

        //Column 1 - Variable 2
        BRLConditionVariableColumn conditionCol0param1 = conditionCol0.getChildColumns().get(1);
        assertEquals("param2",
                     conditionCol0param1.getVarName());
        assertEquals("First name",
                     conditionCol0param1.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param1.getFieldType());
        assertEquals("Driver",
                     conditionCol0param1.getFactType());
        assertEquals("firstName",
                     conditionCol0param1.getFactField());

        //Column 1 - Variable 3
        BRLConditionVariableColumn conditionCol0param2 = conditionCol0.getChildColumns().get(2);
        assertEquals("param3",
                     conditionCol0param2.getVarName());
        assertEquals("Surname",
                     conditionCol0param2.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param2.getFieldType());
        assertEquals("Driver",
                     conditionCol0param2.getFactType());
        assertEquals("surname",
                     conditionCol0param2.getFactField());

        //Column 2
        BRLConditionColumn conditionCol1 = ((BRLConditionColumn) dtable.getConditions().get(1));
        assertEquals("Converted from ['something', 'something']",
                     conditionCol1.getHeader());
        assertEquals(2,
                     conditionCol1.getChildColumns().size());

        List<IPattern> conditionCol1definition = conditionCol1.getDefinition();
        assertEquals(1,
                     conditionCol1definition.size());
        assertTrue(conditionCol1definition.get(0) instanceof FactPattern);

        FactPattern conditionCol1fp = (FactPattern) conditionCol1definition.get(0);
        assertEquals("Vehicle",
                     conditionCol1fp.getFactType());
        assertEquals(2,
                     conditionCol1fp.getNumberOfConstraints());
        assertTrue(conditionCol1fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol1fpsfc0 = (SingleFieldConstraint) conditionCol1fp.getConstraint(0);
        assertEquals("make",
                     conditionCol1fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol1fpsfc0.getOperator());
        assertEquals("param4",
                     conditionCol1fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol1fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol1fpsfc0.getFieldType());

        assertTrue(conditionCol1fp.getConstraint(1) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol1fpsfc1 = (SingleFieldConstraint) conditionCol1fp.getConstraint(1);
        assertEquals("model",
                     conditionCol1fpsfc1.getFieldName());
        assertEquals("==",
                     conditionCol1fpsfc1.getOperator());
        assertEquals("param5",
                     conditionCol1fpsfc1.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol1fpsfc1.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol1fpsfc1.getFieldType());

        //Column 2 - Variable 1
        BRLConditionVariableColumn conditionCol1param0 = conditionCol1.getChildColumns().get(0);
        assertEquals("param4",
                     conditionCol1param0.getVarName());
        assertEquals("something",
                     conditionCol1param0.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol1param0.getFieldType());
        assertEquals("Vehicle",
                     conditionCol1param0.getFactType());
        assertEquals("make",
                     conditionCol1param0.getFactField());

        //Column 2 - Variable 2
        BRLConditionVariableColumn conditionCol1param1 = conditionCol1.getChildColumns().get(1);
        assertEquals("param5",
                     conditionCol1param1.getVarName());
        assertEquals("something",
                     conditionCol1param1.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol1param1.getFieldType());
        assertEquals("Vehicle",
                     conditionCol1param1.getFactType());
        assertEquals("model",
                     conditionCol1param1.getFactField());

        //Check data
        assertEquals(2,
                     dtable.getData().size());

        assertNotNull(expectedRows);
        assertTrue(expectedRows.size() == 2);

        for (int i = 0; i < 2; i++) {
            assertTrue(isRowEquivalent(expectedRows.get(i),
                                       dtable.getData().get(i)));
        }
    }

    @Test
    public void testMultipleRuleTables() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("MultipleRuleTables.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull(dtables);
        assertEquals(2,
                     dtables.size());

        GuidedDecisionTable52 dtable0 = dtables.get(0);
        assertEquals("Table1",
                     dtable0.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable0.getTableFormat());

        GuidedDecisionTable52 dtable1 = dtables.get(1);
        assertEquals("Table2",
                     dtable1.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable1.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns0 = dtable0.getExpandedColumns();
        assertNotNull(columns0);
        assertEquals(5,
                     columns0.size());
        assertTrue(columns0.get(0) instanceof RowNumberCol52);
        assertTrue(columns0.get(1) instanceof DescriptionCol52);
        assertTrue(columns0.get(2) instanceof AttributeCol52);
        assertTrue(columns0.get(3) instanceof BRLConditionVariableColumn);
        assertTrue(columns0.get(4) instanceof BRLActionVariableColumn);

        AttributeCol52 attrCol0_2 = ((AttributeCol52) columns0.get(2));
        assertEquals(Attribute.AGENDA_GROUP.getAttributeName(),
                     attrCol0_2.getAttribute());

        //Check individual condition columns
        assertEquals(1,
                     dtable0.getConditions().size());
        assertTrue(dtable0.getConditions().get(0) instanceof BRLConditionColumn);

        //Column 1
        BRLConditionColumn conditionCol0_0 = ((BRLConditionColumn) dtable0.getConditions().get(0));
        assertEquals("Converted from ['Person's name']",
                     conditionCol0_0.getHeader());
        assertEquals(1,
                     conditionCol0_0.getChildColumns().size());

        List<IPattern> conditionCol0_0definition = conditionCol0_0.getDefinition();
        assertEquals(1,
                     conditionCol0_0definition.size());
        assertTrue(conditionCol0_0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol0_0fp = (FactPattern) conditionCol0_0definition.get(0);
        assertEquals("Person",
                     conditionCol0_0fp.getFactType());
        assertEquals(1,
                     conditionCol0_0fp.getNumberOfConstraints());
        assertTrue(conditionCol0_0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0_0fpsfc0 = (SingleFieldConstraint) conditionCol0_0fp.getConstraint(0);
        assertEquals("name",
                     conditionCol0_0fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol0_0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol0_0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0_0fpsfc0.getConstraintValueType());

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0_0param0 = conditionCol0_0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0_0param0.getVarName());
        assertEquals("Person's name",
                     conditionCol0_0param0.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0_0param0.getFieldType());
        assertEquals("Person",
                     conditionCol0_0param0.getFactType());
        assertEquals("name",
                     conditionCol0_0param0.getFactField());

        //Column 2
        BRLActionColumn actionCol0_0 = ((BRLActionColumn) dtable0.getActionCols().get(0));
        assertEquals("Converted from ['Salutation']",
                     actionCol0_0.getHeader());
        assertEquals(1,
                     actionCol0_0.getChildColumns().size());

        List<IAction> actionCol0_0definition = actionCol0_0.getDefinition();
        assertEquals(1,
                     actionCol0_0definition.size());
        assertTrue(actionCol0_0definition.get(0) instanceof FreeFormLine);

        FreeFormLine actionCol0_0ffl = (FreeFormLine) actionCol0_0definition.get(0);
        assertEquals("System.out.println(\"@{param2}\");",
                     actionCol0_0ffl.getText());

        //Column 1 - Variable 1
        BRLActionVariableColumn actionCol0_0param0 = actionCol0_0.getChildColumns().get(0);
        assertEquals("param2",
                     actionCol0_0param0.getVarName());
        assertEquals("Salutation",
                     actionCol0_0param0.getHeader());
        assertEquals(DataType.TYPE_OBJECT,
                     actionCol0_0param0.getFieldType());
        assertNull(actionCol0_0param0.getFactType());
        assertNull(actionCol0_0param0.getFactField());

        //Check data
        assertEquals(2,
                     dtable0.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Created from row 7", "AG1", "John", "Hello Sir"},
                                   dtable0.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Row 2", "AG2", "Jane", "Hello Madam"},
                                   dtable0.getData().get(1)));

        //Check expanded columns
        List<BaseColumn> columns1 = dtable1.getExpandedColumns();
        assertNotNull(columns1);
        assertEquals(4,
                     columns1.size());
        assertTrue(columns1.get(0) instanceof RowNumberCol52);
        assertTrue(columns1.get(1) instanceof DescriptionCol52);
        assertTrue(columns1.get(2) instanceof BRLConditionVariableColumn);
        assertTrue(columns1.get(3) instanceof BRLConditionVariableColumn);

        //Check individual condition columns
        assertEquals(1,
                     dtable0.getConditions().size());
        assertTrue(dtable0.getConditions().get(0) instanceof BRLConditionColumn);

        //Column 1
        BRLConditionColumn conditionCol1_0 = ((BRLConditionColumn) dtable1.getConditions().get(0));
        assertEquals("Converted from ['Person’s name', 'Person’s age']",
                     conditionCol1_0.getHeader());
        assertEquals(2,
                     conditionCol1_0.getChildColumns().size());

        List<IPattern> conditionCol1_0definition = conditionCol1_0.getDefinition();
        assertEquals(1,
                     conditionCol1_0definition.size());
        assertTrue(conditionCol1_0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol1_0fp = (FactPattern) conditionCol1_0definition.get(0);
        assertEquals("Person",
                     conditionCol1_0fp.getFactType());
        assertEquals(2,
                     conditionCol1_0fp.getNumberOfConstraints());
        assertTrue(conditionCol1_0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol1_0fpsfc0 = (SingleFieldConstraint) conditionCol1_0fp.getConstraint(0);
        assertEquals("name",
                     conditionCol1_0fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol1_0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol1_0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol1_0fpsfc0.getConstraintValueType());
        assertTrue(conditionCol1_0fp.getConstraint(1) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol1_0fpsfc1 = (SingleFieldConstraint) conditionCol1_0fp.getConstraint(1);
        assertEquals("age",
                     conditionCol1_0fpsfc1.getFieldName());
        assertEquals("==",
                     conditionCol1_0fpsfc1.getOperator());
        assertEquals("param2",
                     conditionCol1_0fpsfc1.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol1_0fpsfc1.getConstraintValueType());

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol1_0param0 = conditionCol1_0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol1_0param0.getVarName());
        assertEquals("Person’s name",
                     conditionCol1_0param0.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol1_0param0.getFieldType());
        assertEquals("Person",
                     conditionCol1_0param0.getFactType());
        assertEquals("name",
                     conditionCol1_0param0.getFactField());

        //Column 1 - Variable 2
        BRLConditionVariableColumn conditionCol1_0param1 = conditionCol1_0.getChildColumns().get(1);
        assertEquals("param2",
                     conditionCol1_0param1.getVarName());
        assertEquals("Person’s age",
                     conditionCol1_0param1.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol1_0param1.getFieldType());
        assertEquals("Person",
                     conditionCol1_0param1.getFactType());
        assertEquals("age",
                     conditionCol1_0param1.getFactField());

        //Check data
        assertEquals(2,
                     dtable1.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Row 1", "John", "25"},
                                   dtable1.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Created from row 16", "Jane", "29"},
                                   dtable1.getData().get(1)));
    }

    @Test
    public void testMultipleSingleParameters() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("MultipleSingleParameters.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("MultipleSingleParameters",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(3,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLConditionVariableColumn);

        //Check individual condition columns
        assertEquals(1,
                     dtable.getConditions().size());
        assertTrue(dtable.getConditions().get(0) instanceof BRLConditionColumn);

        //Column 1
        BRLConditionColumn conditionCol0 = ((BRLConditionColumn) dtable.getConditions().get(0));
        assertEquals("Converted from ['Re-using single parameter']",
                     conditionCol0.getHeader());
        assertEquals(1,
                     conditionCol0.getChildColumns().size());

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals(1,
                     conditionCol0definition.size());
        assertTrue(conditionCol0definition.get(0) instanceof FreeFormLine);

        FreeFormLine conditionCol0ffl = (FreeFormLine) conditionCol0definition.get(0);
        assertEquals("Driver(@{param1} != null, @{param1} == true)",
                     conditionCol0ffl.getText());

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0param0.getVarName());
        assertEquals("Re-using single parameter",
                     conditionCol0param0.getHeader());
        assertEquals(DataType.TYPE_OBJECT,
                     conditionCol0param0.getFieldType());
        assertNull(conditionCol0param0.getFactType());
        assertNull(conditionCol0param0.getFactField());

        //Check data
        assertEquals(2,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Row 1", "isQualified"},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Row 2", "isLicensed"},
                                   dtable.getData().get(1)));
    }

    @Test
    public void testProperties() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("Properties.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check properties
        List<String> functions = listener.getFunctions();
        assertNotNull(functions);
        assertEquals(1,
                     functions.size());
        assertEquals("function a() { }",
                     functions.get(0));

        List<Global> globals = listener.getGlobals();
        assertNotNull(globals);
        assertEquals(1,
                     globals.size());
        assertEquals("java.util.List",
                     globals.get(0).getClassName());
        assertEquals("list",
                     globals.get(0).getIdentifier());

        List<Import> imports = listener.getImports();
        assertNotNull(imports);
        assertEquals(2,
                     imports.size());
        assertEquals("org.yourco.model.*",
                     imports.get(0).getClassName());
        assertEquals("java.util.Date",
                     imports.get(1).getClassName());

        List<String> queries = listener.getQueries();
        assertNotNull(queries);
        assertEquals(1,
                     queries.size());
        assertEquals("A query",
                     queries.get(0));

        List<String> types = listener.getTypeDeclarations();
        assertNotNull(types);
        assertEquals(1,
                     types.size());
        assertEquals("declare Smurf name : String end",
                     types.get(0));
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2188
    public void testTestNonExistentCellsFromPOI() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("GUVNOR-2188.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        final List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();
        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        final GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("IdentifyMetadataRules",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(6,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(3) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(4) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(5) instanceof BRLActionVariableColumn);

        //Check individual condition columns
        assertEquals(1,
                     dtable.getConditions().size());
        assertTrue(dtable.getConditions().get(0) instanceof BRLConditionColumn);

        //Column 1
        BRLConditionColumn conditionCol0 = ((BRLConditionColumn) dtable.getConditions().get(0));
        assertEquals("Converted from ['Ingest Path', 'Court Id', 'Artifact Metadata Identified']",
                     conditionCol0.getHeader());
        assertEquals(3,
                     conditionCol0.getChildColumns().size());

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals(1,
                     conditionCol0definition.size());
        assertTrue(conditionCol0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol0fp = (FactPattern) conditionCol0definition.get(0);
        assertEquals("IdentifyMetadataArtifact",
                     conditionCol0fp.getFactType());
        assertEquals(3,
                     conditionCol0fp.getNumberOfConstraints());
        assertTrue(conditionCol0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc0 = (SingleFieldConstraint) conditionCol0fp.getConstraint(0);
        assertEquals("ingestPath",
                     conditionCol0fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0fpsfc0.getFieldType());

        assertTrue(conditionCol0fp.getConstraint(1) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc1 = (SingleFieldConstraint) conditionCol0fp.getConstraint(1);
        assertEquals("courtId",
                     conditionCol0fpsfc1.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc1.getOperator());
        assertEquals("param2",
                     conditionCol0fpsfc1.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc1.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0fpsfc1.getFieldType());

        assertTrue(conditionCol0fp.getConstraint(2) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc2 = (SingleFieldConstraint) conditionCol0fp.getConstraint(2);
        assertEquals("artifactMetadataIdentified",
                     conditionCol0fpsfc2.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc2.getOperator());
        assertEquals("param3",
                     conditionCol0fpsfc2.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc2.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0fpsfc2.getFieldType());

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0param0.getVarName());
        assertEquals("Ingest Path",
                     conditionCol0param0.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param0.getFieldType());
        assertEquals("IdentifyMetadataArtifact",
                     conditionCol0param0.getFactType());
        assertEquals("ingestPath",
                     conditionCol0param0.getFactField());

        //Column 1 - Variable 2
        BRLConditionVariableColumn conditionCol0param1 = conditionCol0.getChildColumns().get(1);
        assertEquals("param2",
                     conditionCol0param1.getVarName());
        assertEquals("Court Id",
                     conditionCol0param1.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param1.getFieldType());
        assertEquals("IdentifyMetadataArtifact",
                     conditionCol0param1.getFactType());
        assertEquals("courtId",
                     conditionCol0param1.getFactField());

        //Column 1 - Variable 3
        BRLConditionVariableColumn conditionCol0param2 = conditionCol0.getChildColumns().get(2);
        assertEquals("param3",
                     conditionCol0param2.getVarName());
        assertEquals("Artifact Metadata Identified",
                     conditionCol0param2.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param2.getFieldType());
        assertEquals("IdentifyMetadataArtifact",
                     conditionCol0param2.getFactType());
        assertEquals("artifactMetadataIdentified",
                     conditionCol0param2.getFactField());

        //Check individual action columns
        assertEquals(1,
                     dtable.getActionCols().size());
        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);

        //Column 2
        BRLActionColumn actionCol0 = ((BRLActionColumn) dtable.getActionCols().get(0));
        assertEquals("Converted from ['Identify Metadata Required']",
                     actionCol0.getHeader());
        assertEquals(1,
                     actionCol0.getChildColumns().size());

        List<IAction> actionCol0definition = actionCol0.getDefinition();
        assertEquals(1,
                     actionCol0definition.size());
        assertTrue(actionCol0definition.get(0) instanceof ActionSetField);

        ActionSetField actionCol0asf = (ActionSetField) actionCol0definition.get(0);
        assertEquals("fact1",
                     actionCol0asf.getVariable());
        assertEquals(1,
                     actionCol0asf.getFieldValues().length);

        ActionFieldValue actionCol0asf_afv0 = actionCol0asf.getFieldValues()[0];
        assertEquals("identifiedMetadataRequired",
                     actionCol0asf_afv0.getField());
        assertEquals("param4",
                     actionCol0asf_afv0.getValue());
        assertEquals(FieldNatureType.TYPE_TEMPLATE,
                     actionCol0asf_afv0.getNature());
        assertEquals(DataType.TYPE_STRING,
                     actionCol0asf_afv0.getType());
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2030
    public void testMissingTemplateKeyValues_StringFields() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("GUVNOR-2030.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        final List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();
        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("asd",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(6,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(3) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(4) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(5) instanceof BRLActionVariableColumn);

        //Check individual condition columns
        assertEquals(1,
                     dtable.getConditions().size());
        assertTrue(dtable.getConditions().get(0) instanceof BRLConditionColumn);

        //Column 1
        BRLConditionColumn conditionCol0 = ((BRLConditionColumn) dtable.getConditions().get(0));
        assertEquals("Converted from ['Rule disabler', 'CovenanteeId', 'blah']",
                     conditionCol0.getHeader());
        assertEquals(3,
                     conditionCol0.getChildColumns().size());

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals(1,
                     conditionCol0definition.size());
        assertTrue(conditionCol0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol0fp = (FactPattern) conditionCol0definition.get(0);
        assertEquals("Transaction",
                     conditionCol0fp.getFactType());
        assertEquals(3,
                     conditionCol0fp.getNumberOfConstraints());
        assertTrue(conditionCol0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc0 = (SingleFieldConstraint) conditionCol0fp.getConstraint(0);
        assertEquals("isDisabled(\"asd\")",
                     conditionCol0fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc0.getConstraintValueType());
        assertEquals("java.lang.String",
                     conditionCol0fpsfc0.getFieldType());

        assertTrue(conditionCol0fp.getConstraint(1) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc1 = (SingleFieldConstraint) conditionCol0fp.getConstraint(1);
        assertEquals("getClientCovenanteeId()",
                     conditionCol0fpsfc1.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc1.getOperator());
        assertEquals("param2",
                     conditionCol0fpsfc1.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc1.getConstraintValueType());
        assertEquals("java.lang.String",
                     conditionCol0fpsfc1.getFieldType());

        assertTrue(conditionCol0fp.getConstraint(2) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc2 = (SingleFieldConstraint) conditionCol0fp.getConstraint(2);
        assertEquals("isBlacklisted(\"asd\")",
                     conditionCol0fpsfc2.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc2.getOperator());
        assertEquals("param3",
                     conditionCol0fpsfc2.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc2.getConstraintValueType());
        assertEquals("java.lang.String",
                     conditionCol0fpsfc2.getFieldType());

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0param0.getVarName());
        assertEquals("Rule disabler",
                     conditionCol0param0.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param0.getFieldType());
        assertEquals("java.lang.String",
                     conditionCol0param0.getFactType());
        assertEquals("isDisabled(\"asd\")",
                     conditionCol0param0.getFactField());

        //Column 1 - Variable 2
        BRLConditionVariableColumn conditionCol0param1 = conditionCol0.getChildColumns().get(1);
        assertEquals("param2",
                     conditionCol0param1.getVarName());
        assertEquals("CovenanteeId",
                     conditionCol0param1.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param1.getFieldType());
        assertEquals("java.lang.String",
                     conditionCol0param1.getFactType());
        assertEquals("getClientCovenanteeId()",
                     conditionCol0param1.getFactField());

        //Column 1 - Variable 3
        BRLConditionVariableColumn conditionCol0param2 = conditionCol0.getChildColumns().get(2);
        assertEquals("param3",
                     conditionCol0param2.getVarName());
        assertEquals("blah",
                     conditionCol0param2.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param2.getFieldType());
        assertEquals("java.lang.String",
                     conditionCol0param2.getFactType());
        assertEquals("isBlacklisted(\"asd\")",
                     conditionCol0param2.getFactField());

        //Check individual action columns
        assertEquals(1,
                     dtable.getActionCols().size());
        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);

        //Column 2
        BRLActionColumn actionCol0 = ((BRLActionColumn) dtable.getActionCols().get(0));
        assertEquals("Converted from ['Risk level']",
                     actionCol0.getHeader());
        assertEquals(1,
                     actionCol0.getChildColumns().size());

        List<IAction> actionCol0definition = actionCol0.getDefinition();
        assertEquals(1,
                     actionCol0definition.size());
        assertTrue(actionCol0definition.get(0) instanceof FreeFormLine);

        FreeFormLine actionCol0ffl = (FreeFormLine) actionCol0definition.get(0);
        assertEquals("riskIndex.setRisk(\"asd\", @{param4});",
                     actionCol0ffl.getText());

        //Check data
        assertEquals(1,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "asd", "false", "", "true", "0"},
                                   dtable.getData().get(0)));
    }

    @Test
    public void testRowDescriptions() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("MultipleRuleTables.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        final List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();
        assertNotNull(dtables);
        assertEquals(2,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);
        assertEquals(2,
                     dtable.getData().size());
        assertEquals("Created from row 7",
                     dtable.getData().get(0).get(1).getStringValue());
        assertEquals("Row 2",
                     dtable.getData().get(1).get(1).getStringValue());

        dtable = dtables.get(1);
        assertEquals(2,
                     dtable.getData().size());
        assertEquals("Row 1",
                     dtable.getData().get(0).get(1).getStringValue());
        assertEquals("Created from row 16",
                     dtable.getData().get(1).get(1).getStringValue());
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2030
    public void testMissingTemplateKeyValues_NonStringFields() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();

        addModelField("org.test.Client",
                      "this",
                      "org.test.Client",
                      DataType.TYPE_THIS);
        addModelField("org.test.Client",
                      "monthlyTransactions",
                      Integer.class.getName(),
                      DataType.TYPE_NUMERIC_INTEGER);

        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("GUVNOR-2030 (DecisionTable).xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        final List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();
        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("Steps",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(5,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(3) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(4) instanceof BRLActionVariableColumn);

        //Check individual condition columns
        assertEquals(1,
                     dtable.getConditions().size());
        assertTrue(dtable.getConditions().get(0) instanceof BRLConditionColumn);

        //Column 1
        BRLConditionColumn conditionCol0 = ((BRLConditionColumn) dtable.getConditions().get(0));
        assertEquals("Converted from ['From', 'To']",
                     conditionCol0.getHeader());
        assertEquals(2,
                     conditionCol0.getChildColumns().size());

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals(1,
                     conditionCol0definition.size());
        assertTrue(conditionCol0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol0fp = (FactPattern) conditionCol0definition.get(0);
        assertEquals("Client",
                     conditionCol0fp.getFactType());
        assertEquals(2,
                     conditionCol0fp.getNumberOfConstraints());
        assertTrue(conditionCol0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc0 = (SingleFieldConstraint) conditionCol0fp.getConstraint(0);
        assertEquals("monthlyTransactions",
                     conditionCol0fpsfc0.getFieldName());
        assertEquals(">=",
                     conditionCol0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0fpsfc0.getFieldType());

        assertTrue(conditionCol0fp.getConstraint(1) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc1 = (SingleFieldConstraint) conditionCol0fp.getConstraint(1);
        assertEquals("monthlyTransactions",
                     conditionCol0fpsfc1.getFieldName());
        assertEquals("<=",
                     conditionCol0fpsfc1.getOperator());
        assertEquals("param2",
                     conditionCol0fpsfc1.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc1.getConstraintValueType());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0fpsfc1.getFieldType());

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0param0.getVarName());
        assertEquals("From",
                     conditionCol0param0.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0param0.getFieldType());
        assertEquals("Client",
                     conditionCol0param0.getFactType());
        assertEquals("monthlyTransactions",
                     conditionCol0param0.getFactField());

        //Column 1 - Variable 2
        BRLConditionVariableColumn conditionCol0param1 = conditionCol0.getChildColumns().get(1);
        assertEquals("param2",
                     conditionCol0param1.getVarName());
        assertEquals("To",
                     conditionCol0param1.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0param1.getFieldType());
        assertEquals("Client",
                     conditionCol0param1.getFactType());
        assertEquals("monthlyTransactions",
                     conditionCol0param1.getFactField());

        //Check individual action columns
        assertEquals(1,
                     dtable.getActionCols().size());
        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);

        //Column 2
        BRLActionColumn actionCol0 = ((BRLActionColumn) dtable.getActionCols().get(0));
        assertEquals("Converted from ['Step']",
                     actionCol0.getHeader());
        assertEquals(1,
                     actionCol0.getChildColumns().size());

        List<IAction> actionCol0definition = actionCol0.getDefinition();
        assertEquals(1,
                     actionCol0definition.size());
        assertTrue(actionCol0definition.get(0) instanceof FreeFormLine);

        FreeFormLine actionCol0ffl = (FreeFormLine) actionCol0definition.get(0);
        assertEquals("if (!$c.isPricingStepCustom() && ($c.getPricingStep() == null || $c.getPricingStep().compareTo(\"@{param3}\") < 0)) { modify($c) { setPricingStep(\"@{param3}\"); } };",
                     actionCol0ffl.getText());

        //Check data
        assertEquals(1,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Created from row 12", 0, 100, "A1"},
                                   dtable.getData().get(0)));
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2030
    public void testMissingTemplateKeyValues_RHSInsertThenUpdate() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();

        addModelField("org.test.Transaction",
                      "this",
                      "org.test.Transaction",
                      DataType.TYPE_THIS);
        addModelField("org.test.Transaction",
                      "enabled",
                      Boolean.class.getName(),
                      DataType.TYPE_BOOLEAN);

        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("GUVNOR-2030 (RHS insert then update).xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        final List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();
        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("asd",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(6,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(3) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(4) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(5) instanceof BRLActionVariableColumn);

        //Check individual condition columns
        assertEquals(1,
                     dtable.getConditions().size());
        assertTrue(dtable.getConditions().get(0) instanceof BRLConditionColumn);

        //Column 1
        BRLConditionColumn conditionCol0 = ((BRLConditionColumn) dtable.getConditions().get(0));
        assertEquals("Converted from ['Disabled']",
                     conditionCol0.getHeader());
        assertEquals(1,
                     conditionCol0.getChildColumns().size());

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals(1,
                     conditionCol0definition.size());
        assertTrue(conditionCol0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol0fp = (FactPattern) conditionCol0definition.get(0);
        assertEquals("Transaction",
                     conditionCol0fp.getFactType());
        assertEquals(1,
                     conditionCol0fp.getNumberOfConstraints());
        assertTrue(conditionCol0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc0 = (SingleFieldConstraint) conditionCol0fp.getConstraint(0);
        assertEquals("enabled",
                     conditionCol0fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_BOOLEAN,
                     conditionCol0fpsfc0.getFieldType());

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0param0.getVarName());
        assertEquals("Disabled",
                     conditionCol0param0.getHeader());
        assertEquals(DataType.TYPE_BOOLEAN,
                     conditionCol0param0.getFieldType());
        assertEquals("Transaction",
                     conditionCol0param0.getFactType());
        assertEquals("enabled",
                     conditionCol0param0.getFactField());

        //Check individual action columns
        assertEquals(3,
                     dtable.getActionCols().size());
        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);

        //Column 2
        BRLActionColumn actionCol0 = ((BRLActionColumn) dtable.getActionCols().get(0));
        assertEquals("Converted from ['Enable']",
                     actionCol0.getHeader());
        assertEquals(1,
                     actionCol0.getChildColumns().size());

        List<IAction> actionCol0definition = actionCol0.getDefinition();
        assertEquals(1,
                     actionCol0definition.size());
        assertTrue(actionCol0definition.get(0) instanceof ActionSetField);

        ActionSetField actionCol0asf0 = (ActionSetField) actionCol0definition.get(0);
        assertEquals("$t",
                     actionCol0asf0.getVariable());
        ActionFieldValue[] actionCol0asf0afvs = actionCol0asf0.getFieldValues();
        assertEquals(1,
                     actionCol0asf0afvs.length);
        ActionFieldValue actionCol0asf0afv0 = actionCol0asf0afvs[0];
        assertEquals("enabled",
                     actionCol0asf0afv0.getField());
        assertEquals("param2",
                     actionCol0asf0afv0.getValue());
        assertEquals(FieldNatureType.TYPE_TEMPLATE,
                     actionCol0asf0afv0.getNature());
        assertEquals(DataType.TYPE_BOOLEAN,
                     actionCol0asf0afv0.getType());

        //Column 3
        BRLActionColumn actionCol1 = ((BRLActionColumn) dtable.getActionCols().get(1));
        assertEquals("Converted from ['New']",
                     actionCol1.getHeader());
        assertEquals(1,
                     actionCol1.getChildColumns().size());

        List<IAction> actionCol1definition = actionCol1.getDefinition();
        assertEquals(1,
                     actionCol1definition.size());
        assertTrue(actionCol1definition.get(0) instanceof ActionInsertFact);

        ActionInsertFact actionCol1aif0 = (ActionInsertFact) actionCol1definition.get(0);
        assertEquals("$t2",
                     actionCol1aif0.getBoundName());
        assertEquals("Transaction",
                     actionCol1aif0.getFactType());
        ActionFieldValue[] actionCol1aif0afvs = actionCol1aif0.getFieldValues();
        assertEquals(0,
                     actionCol1aif0afvs.length);

        //Column 4
        BRLActionColumn actionCol2 = ((BRLActionColumn) dtable.getActionCols().get(2));
        assertEquals("Converted from ['Disable']",
                     actionCol2.getHeader());
        assertEquals(1,
                     actionCol2.getChildColumns().size());

        List<IAction> actionCol2definition = actionCol2.getDefinition();
        assertEquals(1,
                     actionCol2definition.size());
        assertTrue(actionCol2definition.get(0) instanceof ActionSetField);

        ActionSetField actionCol2asf0 = (ActionSetField) actionCol2definition.get(0);
        assertEquals("$t2",
                     actionCol2asf0.getVariable());
        ActionFieldValue[] actionCol2asf0afvs = actionCol2asf0.getFieldValues();
        assertEquals(1,
                     actionCol2asf0afvs.length);
        ActionFieldValue actionCol2asf0afv0 = actionCol2asf0afvs[0];
        assertEquals("enabled",
                     actionCol2asf0afv0.getField());
        assertEquals("param3",
                     actionCol2asf0afv0.getValue());
        assertEquals(FieldNatureType.TYPE_TEMPLATE,
                     actionCol2asf0afv0.getNature());
        assertEquals(DataType.TYPE_BOOLEAN,
                     actionCol2asf0afv0.getType());

        //Check data
        assertEquals(1,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "asd", false, true, false, false},
                                   dtable.getData().get(0)));
    }

    @Test
    //https://bugzilla.redhat.com/show_bug.cgi?id=1256623
    public void testEmptyCells() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("EmptySpreadsheetCells.xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();
        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        final GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("ExceptionPrivateCar",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(20,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof AttributeCol52);
        assertTrue(columns.get(3) instanceof AttributeCol52);
        assertTrue(columns.get(4) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(5) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(6) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(7) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(8) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(9) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(10) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(11) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(12) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(13) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(14) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(15) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(16) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(17) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(18) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(19) instanceof BRLActionVariableColumn);
    }

    @Test
    //https://issues.jboss.org/browse/RHBPMS-856
    public void correctMergedConditionColumnHeaders() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<>();

        addModelField("org.test.Vehicle",
                      "this",
                      "org.test.Vehicle",
                      DataType.TYPE_THIS);
        addModelField("org.test.Vehicle",
                      "subRTO",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Vehicle",
                      "manufacturer",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Vehicle",
                      "model",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Vehicle",
                      "vehicleSegment",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Vehicle",
                      "vehicleAge",
                      Integer.class.getName(),
                      DataType.TYPE_NUMERIC_INTEGER);
        addModelField("org.test.Vehicle",
                      "discount",
                      Double.class.getName(),
                      DataType.TYPE_NUMERIC_DOUBLE);

        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("RHBPMS-856 (Merged condition columns).xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        final List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();
        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("VehiclePremiumDiscount",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(9,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(3) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(4) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(5) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(6) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(7) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(8) instanceof BRLActionVariableColumn);

        //Check individual condition columns
        assertEquals(1,
                     dtable.getConditions().size());
        assertTrue(dtable.getConditions().get(0) instanceof BRLConditionColumn);

        BRLConditionColumn conditionCol0 = ((BRLConditionColumn) dtable.getConditions().get(0));
        assertEquals("Converted from ['RTO', 'Manufacturer', 'Model', 'Segment', 'Age Min', 'Age Max']",
                     conditionCol0.getHeader());
        assertEquals(6,
                     conditionCol0.getChildColumns().size());

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals(1,
                     conditionCol0definition.size());
        assertTrue(conditionCol0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol0fp = (FactPattern) conditionCol0definition.get(0);
        assertEquals("Vehicle",
                     conditionCol0fp.getFactType());
        assertEquals(6,
                     conditionCol0fp.getNumberOfConstraints());

        //Field Constraint 1
        assertTrue(conditionCol0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc0 = (SingleFieldConstraint) conditionCol0fp.getConstraint(0);
        assertEquals("subRTO",
                     conditionCol0fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0fpsfc0.getFieldType());

        //Field Constraint 2
        assertTrue(conditionCol0fp.getConstraint(1) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc1 = (SingleFieldConstraint) conditionCol0fp.getConstraint(1);
        assertEquals("manufacturer",
                     conditionCol0fpsfc1.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc1.getOperator());
        assertEquals("param2",
                     conditionCol0fpsfc1.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc1.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0fpsfc1.getFieldType());

        //Field Constraint 3
        assertTrue(conditionCol0fp.getConstraint(2) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc2 = (SingleFieldConstraint) conditionCol0fp.getConstraint(2);
        assertEquals("model",
                     conditionCol0fpsfc2.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc2.getOperator());
        assertEquals("param3",
                     conditionCol0fpsfc2.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc2.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0fpsfc2.getFieldType());

        //Field Constraint 4
        assertTrue(conditionCol0fp.getConstraint(3) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc3 = (SingleFieldConstraint) conditionCol0fp.getConstraint(3);
        assertEquals("vehicleSegment",
                     conditionCol0fpsfc3.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc3.getOperator());
        assertEquals("param4",
                     conditionCol0fpsfc3.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc3.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0fpsfc3.getFieldType());

        //Field Constraint 5
        assertTrue(conditionCol0fp.getConstraint(4) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc4 = (SingleFieldConstraint) conditionCol0fp.getConstraint(4);
        assertEquals("vehicleAge",
                     conditionCol0fpsfc4.getFieldName());
        assertEquals("<=",
                     conditionCol0fpsfc4.getOperator());
        assertEquals("param5",
                     conditionCol0fpsfc4.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc4.getConstraintValueType());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0fpsfc4.getFieldType());

        //Field Constraint 6
        assertTrue(conditionCol0fp.getConstraint(5) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc5 = (SingleFieldConstraint) conditionCol0fp.getConstraint(5);
        assertEquals("vehicleAge",
                     conditionCol0fpsfc5.getFieldName());
        assertEquals(">",
                     conditionCol0fpsfc5.getOperator());
        assertEquals("param6",
                     conditionCol0fpsfc5.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc5.getConstraintValueType());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0fpsfc5.getFieldType());

        //Field Constraint 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0param0.getVarName());
        assertEquals("RTO",
                     conditionCol0param0.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param0.getFieldType());
        assertEquals("Vehicle",
                     conditionCol0param0.getFactType());
        assertEquals("subRTO",
                     conditionCol0param0.getFactField());

        //Field Constraint 2 - Variable 2
        BRLConditionVariableColumn conditionCol0param1 = conditionCol0.getChildColumns().get(1);
        assertEquals("param2",
                     conditionCol0param1.getVarName());
        assertEquals("Manufacturer",
                     conditionCol0param1.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param1.getFieldType());
        assertEquals("Vehicle",
                     conditionCol0param1.getFactType());
        assertEquals("manufacturer",
                     conditionCol0param1.getFactField());

        //Field Constraint 3 - Variable 3
        BRLConditionVariableColumn conditionCol0param2 = conditionCol0.getChildColumns().get(2);
        assertEquals("param3",
                     conditionCol0param2.getVarName());
        assertEquals("Model",
                     conditionCol0param2.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param2.getFieldType());
        assertEquals("Vehicle",
                     conditionCol0param2.getFactType());
        assertEquals("model",
                     conditionCol0param2.getFactField());

        //Field Constraint 4 - Variable 4
        BRLConditionVariableColumn conditionCol0param3 = conditionCol0.getChildColumns().get(3);
        assertEquals("param4",
                     conditionCol0param3.getVarName());
        assertEquals("Segment",
                     conditionCol0param3.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param3.getFieldType());
        assertEquals("Vehicle",
                     conditionCol0param3.getFactType());
        assertEquals("vehicleSegment",
                     conditionCol0param3.getFactField());

        //Field Constraint 5 - Variable 5
        BRLConditionVariableColumn conditionCol0param4 = conditionCol0.getChildColumns().get(4);
        assertEquals("param5",
                     conditionCol0param4.getVarName());
        assertEquals("Age Min",
                     conditionCol0param4.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0param4.getFieldType());
        assertEquals("Vehicle",
                     conditionCol0param4.getFactType());
        assertEquals("vehicleAge",
                     conditionCol0param4.getFactField());

        //Field Constraint 6 - Variable 6
        BRLConditionVariableColumn conditionCol0param5 = conditionCol0.getChildColumns().get(5);
        assertEquals("param6",
                     conditionCol0param5.getVarName());
        assertEquals("Age Max",
                     conditionCol0param5.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0param5.getFieldType());
        assertEquals("Vehicle",
                     conditionCol0param5.getFactType());
        assertEquals("vehicleAge",
                     conditionCol0param5.getFactField());

        //Check individual action columns
        assertEquals(1,
                     dtable.getActionCols().size());
        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);

        BRLActionColumn actionCol0 = ((BRLActionColumn) dtable.getActionCols().get(0));
        assertEquals("Converted from ['Discount(%)']",
                     actionCol0.getHeader());
        assertEquals(1,
                     actionCol0.getChildColumns().size());

        List<IAction> actionCol0definition = actionCol0.getDefinition();
        assertEquals(1,
                     actionCol0definition.size());
        assertTrue(actionCol0definition.get(0) instanceof ActionSetField);

        ActionSetField actionCol0asf0 = (ActionSetField) actionCol0definition.get(0);
        assertEquals("v",
                     actionCol0asf0.getVariable());
        ActionFieldValue[] actionCol0asf0afvs = actionCol0asf0.getFieldValues();
        assertEquals(1,
                     actionCol0asf0afvs.length);
        ActionFieldValue actionCol0asf0afv0 = actionCol0asf0afvs[0];
        assertEquals("discount",
                     actionCol0asf0afv0.getField());
        assertEquals("param7",
                     actionCol0asf0afv0.getValue());
        assertEquals(FieldNatureType.TYPE_TEMPLATE,
                     actionCol0asf0afv0.getNature());
        assertEquals(DataType.TYPE_NUMERIC_DOUBLE,
                     actionCol0asf0afv0.getType());

        //Action 1 - Variable 1
        BRLActionVariableColumn actionCol0param0 = actionCol0.getChildColumns().get(0);
        assertEquals("param7",
                     actionCol0param0.getVarName());
        assertEquals("Discount(%)",
                     actionCol0param0.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_DOUBLE,
                     actionCol0param0.getFieldType());
        assertEquals("Vehicle",
                     actionCol0param0.getFactType());
        assertEquals("discount",
                     actionCol0param0.getFactField());

        //Check data
        assertEquals(4,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "1", "ALL", "XYZ", "EL", "HighEnd", 1, 0, 1.75d},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "2", "ALL", "XYZ", "EL", "HighEnd", 5, 1, 2.00d},
                                   dtable.getData().get(1)));
        assertTrue(isRowEquivalent(new Object[]{3, "3", "ALL", "XYZ", "EL", "HighEnd", 7, 5, 2.00d},
                                   dtable.getData().get(2)));
        assertTrue(isRowEquivalent(new Object[]{4, "4", "ALL", "XYZ", "EL", "HighEnd", 10, 7, 1.00d},
                                   dtable.getData().get(3)));
    }

    @Test
    //https://issues.jboss.org/browse/RHBRMS-2055
    public void conversionWithEnumerationsInCells() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<>();

        addModelField("org.test.Message",
                      "this",
                      "org.test.Message",
                      DataType.TYPE_THIS);
        addModelField("org.test.Message",
                      "status",
                      Integer.class.getName(),
                      DataType.TYPE_NUMERIC_INTEGER);
        addModelField("org.test.Message",
                      "message",
                      String.class.getName(),
                      DataType.TYPE_STRING);

        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("RHBRMS-2055 (Decision table with enums).xls");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(3,
                     result.getMessages().size());
        assertTrue(result.getMessages().get(0).getMessage().contains("C11"));
        assertTrue(result.getMessages().get(1).getMessage().contains("C12"));
        assertTrue(result.getMessages().get(2).getMessage().contains("F11"));

        //Check basics
        final List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();
        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("HelloWorld1",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(6,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(3) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(4) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(5) instanceof BRLActionVariableColumn);

        //Check individual condition columns
        assertEquals(1,
                     dtable.getConditions().size());
        assertTrue(dtable.getConditions().get(0) instanceof BRLConditionColumn);

        BRLConditionColumn conditionCol0 = ((BRLConditionColumn) dtable.getConditions().get(0));
        assertEquals("Converted from ['Status']",
                     conditionCol0.getHeader());
        assertEquals(1,
                     conditionCol0.getChildColumns().size());

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals(1,
                     conditionCol0definition.size());
        assertTrue(conditionCol0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol0fp = (FactPattern) conditionCol0definition.get(0);
        assertEquals("Message",
                     conditionCol0fp.getFactType());
        assertEquals(1,
                     conditionCol0fp.getNumberOfConstraints());

        //Field Constraint 1
        assertTrue(conditionCol0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc0 = (SingleFieldConstraint) conditionCol0fp.getConstraint(0);
        assertEquals("status",
                     conditionCol0fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0fpsfc0.getFieldType());

        //Field Constraint 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0param0.getVarName());
        assertEquals("Status",
                     conditionCol0param0.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0param0.getFieldType());
        assertEquals("Message",
                     conditionCol0param0.getFactType());
        assertEquals("status",
                     conditionCol0param0.getFactField());

        //Check individual action columns
        assertEquals(2,
                     dtable.getActionCols().size());

        //Action 1
        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);

        BRLActionColumn actionCol0 = ((BRLActionColumn) dtable.getActionCols().get(0));
        assertEquals("Converted from ['Print out message?']",
                     actionCol0.getHeader());
        assertEquals(1,
                     actionCol0.getChildColumns().size());

        List<IAction> actionCol0definition = actionCol0.getDefinition();
        assertEquals(1,
                     actionCol0definition.size());
        assertTrue(actionCol0definition.get(0) instanceof FreeFormLine);

        FreeFormLine actionCol0ffl0 = (FreeFormLine) actionCol0definition.get(0);
        assertEquals("System.out.println(m.getMessage());",
                     actionCol0ffl0.getText());

        //Action 2
        BRLActionColumn actionCol1 = ((BRLActionColumn) dtable.getActionCols().get(1));
        assertEquals("Converted from ['Set message', 'Set status']",
                     actionCol1.getHeader());
        assertEquals(2,
                     actionCol1.getChildColumns().size());

        List<IAction> actionCol1definition = actionCol1.getDefinition();
        assertEquals(1,
                     actionCol1definition.size());
        assertTrue(actionCol1definition.get(0) instanceof ActionSetField);

        ActionSetField actionCol1asf0 = (ActionSetField) actionCol1definition.get(0);
        assertEquals("m",
                     actionCol1asf0.getVariable());
        ActionFieldValue[] actionCol1asf0afvs = actionCol1asf0.getFieldValues();
        assertEquals(2,
                     actionCol1asf0afvs.length);
        ActionFieldValue actionCol1asf0afv0 = actionCol1asf0afvs[0];
        assertEquals("message",
                     actionCol1asf0afv0.getField());
        assertEquals("param2",
                     actionCol1asf0afv0.getValue());
        assertEquals(FieldNatureType.TYPE_TEMPLATE,
                     actionCol1asf0afv0.getNature());
        assertEquals(DataType.TYPE_STRING,
                     actionCol1asf0afv0.getType());
        ActionFieldValue actionCol1asf0afv1 = actionCol1asf0afvs[1];
        assertEquals("status",
                     actionCol1asf0afv1.getField());
        assertEquals("param3",
                     actionCol1asf0afv1.getValue());
        assertEquals(FieldNatureType.TYPE_TEMPLATE,
                     actionCol1asf0afv1.getNature());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     actionCol1asf0afv1.getType());

        //Action 1 - Variable 1
        BRLActionVariableColumn actionCol0param0 = actionCol0.getChildColumns().get(0);
        assertEquals("",
                     actionCol0param0.getVarName());
        assertEquals("Print out message?",
                     actionCol0param0.getHeader());
        assertEquals(DataType.TYPE_BOOLEAN,
                     actionCol0param0.getFieldType());
        assertNull(actionCol0param0.getFactType());
        assertNull(actionCol0param0.getFactField());

        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);

        //Action 2 - Variable 1
        BRLActionVariableColumn actionCol1param0 = actionCol1.getChildColumns().get(0);
        assertEquals("param2",
                     actionCol1param0.getVarName());
        assertEquals("Set message",
                     actionCol1param0.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     actionCol1param0.getFieldType());
        assertEquals("Message",
                     actionCol1param0.getFactType());
        assertEquals("message",
                     actionCol1param0.getFactField());

        //Action 2 - Variable 2
        BRLActionVariableColumn actionCol1param1 = actionCol1.getChildColumns().get(1);
        assertEquals("param3",
                     actionCol1param1.getVarName());
        assertEquals("Set status",
                     actionCol1param1.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     actionCol1param1.getFieldType());
        assertEquals("Message",
                     actionCol1param1.getFactType());
        assertEquals("status",
                     actionCol1param1.getFactField());

        //Check data
        assertEquals(2,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Hello World", null, false, "Goodbye cruel world", null},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Goodbye", null, false, "", null},
                                   dtable.getData().get(1)));
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2888
    public void conversionWithRetract() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<>();

        addModelField("org.test.Employee",
                      "this",
                      "org.test.Employee",
                      DataType.TYPE_THIS);
        addModelField("org.test.Employee",
                      "yearsOfService",
                      Integer.class.getName(),
                      DataType.TYPE_NUMERIC_INTEGER);
        addModelField("org.test.Employee",
                      "name",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Employee",
                      "bigAbsence",
                      Boolean.class.getName(),
                      DataType.TYPE_BOOLEAN);

        addModelField("org.test.Bonus",
                      "this",
                      "org.test.Bonus",
                      DataType.TYPE_THIS);
        addModelField("org.test.Bonus",
                      "amount",
                      Double.class.getName(),
                      DataType.TYPE_NUMERIC_DOUBLE);
        addModelField("org.test.Bonus",
                      "employee",
                      "org.test.Employee",
                      DataType.TYPE_OBJECT);

        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("GUVNOR-2888.xlsx");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        //Check conversion results
        assertEquals(1,
                     result.getMessages().size());
        assertTrue(result.getMessages().get(0).getMessage().contains("I13"));

        //Check basics
        final List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();
        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("bonus_program",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(11,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(3) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(4) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(5) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(6) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(7) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(8) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(9) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(10) instanceof BRLActionVariableColumn);

        //Check individual condition columns
        assertEquals(3,
                     dtable.getConditions().size());

        //Pattern 0
        assertTrue(dtable.getConditions().get(0) instanceof BRLConditionColumn);

        BRLConditionColumn conditionCol0 = ((BRLConditionColumn) dtable.getConditions().get(0));
        assertEquals("Converted from ['Served between', 'Served between', 'Served more', 'absence']",
                     conditionCol0.getHeader());
        assertEquals(4,
                     conditionCol0.getChildColumns().size());

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals(1,
                     conditionCol0definition.size());
        assertTrue(conditionCol0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol0fp = (FactPattern) conditionCol0definition.get(0);
        assertEquals("Employee",
                     conditionCol0fp.getFactType());
        assertEquals(4,
                     conditionCol0fp.getNumberOfConstraints());

        //Pattern 0 - Field Constraint 0
        assertTrue(conditionCol0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc0 = (SingleFieldConstraint) conditionCol0fp.getConstraint(0);
        assertEquals("yearsOfService",
                     conditionCol0fpsfc0.getFieldName());
        assertEquals(">=",
                     conditionCol0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0fpsfc0.getFieldType());

        //Pattern 0 - Field Constraint 0 - Variable 0
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0param0.getVarName());
        assertEquals("Served between",
                     conditionCol0param0.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0param0.getFieldType());
        assertEquals("Employee",
                     conditionCol0param0.getFactType());
        assertEquals("yearsOfService",
                     conditionCol0param0.getFactField());

        //Pattern 0 - Field Constraint 1
        assertTrue(conditionCol0fp.getConstraint(1) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc1 = (SingleFieldConstraint) conditionCol0fp.getConstraint(1);
        assertEquals("yearsOfService",
                     conditionCol0fpsfc1.getFieldName());
        assertEquals("<",
                     conditionCol0fpsfc1.getOperator());
        assertEquals("param2",
                     conditionCol0fpsfc1.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc1.getConstraintValueType());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0fpsfc1.getFieldType());

        //Pattern 0 - Field Constraint 1 - Variable 0
        BRLConditionVariableColumn conditionCol0param1 = conditionCol0.getChildColumns().get(1);
        assertEquals("param2",
                     conditionCol0param1.getVarName());
        assertEquals("Served between",
                     conditionCol0param1.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0param1.getFieldType());
        assertEquals("Employee",
                     conditionCol0param1.getFactType());
        assertEquals("yearsOfService",
                     conditionCol0param1.getFactField());

        //Pattern 0 - Field Constraint 2
        assertTrue(conditionCol0fp.getConstraint(2) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc2 = (SingleFieldConstraint) conditionCol0fp.getConstraint(2);
        assertEquals("yearsOfService",
                     conditionCol0fpsfc2.getFieldName());
        assertEquals(">",
                     conditionCol0fpsfc2.getOperator());
        assertEquals("param3",
                     conditionCol0fpsfc2.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc2.getConstraintValueType());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0fpsfc2.getFieldType());

        //Pattern 0 - Field Constraint 2 - Variable 0
        BRLConditionVariableColumn conditionCol0param2 = conditionCol0.getChildColumns().get(2);
        assertEquals("param3",
                     conditionCol0param2.getVarName());
        assertEquals("Served more",
                     conditionCol0param2.getHeader());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     conditionCol0param2.getFieldType());
        assertEquals("Employee",
                     conditionCol0param2.getFactType());
        assertEquals("yearsOfService",
                     conditionCol0param2.getFactField());

        //Pattern 0 - Field Constraint 3
        assertTrue(conditionCol0fp.getConstraint(3) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc3 = (SingleFieldConstraint) conditionCol0fp.getConstraint(3);
        assertEquals("bigAbsence",
                     conditionCol0fpsfc3.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc3.getOperator());
        assertEquals("param4",
                     conditionCol0fpsfc3.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc3.getConstraintValueType());
        assertEquals(DataType.TYPE_BOOLEAN,
                     conditionCol0fpsfc3.getFieldType());

        //Pattern 0 - Field Constraint 3 - Variable 0
        BRLConditionVariableColumn conditionCol0param3 = conditionCol0.getChildColumns().get(3);
        assertEquals("param4",
                     conditionCol0param3.getVarName());
        assertEquals("absence",
                     conditionCol0param3.getHeader());
        assertEquals(DataType.TYPE_BOOLEAN,
                     conditionCol0param3.getFieldType());
        assertEquals("Employee",
                     conditionCol0param3.getFactType());
        assertEquals("bigAbsence",
                     conditionCol0param3.getFactField());

        //Pattern 1
        assertTrue(dtable.getConditions().get(1) instanceof BRLConditionColumn);

        BRLConditionColumn conditionCol1 = ((BRLConditionColumn) dtable.getConditions().get(1));
        assertEquals("Converted from ['bonus for employee']",
                     conditionCol1.getHeader());
        assertEquals(1,
                     conditionCol1.getChildColumns().size());

        List<IPattern> conditionCol1definition = conditionCol1.getDefinition();
        assertEquals(1,
                     conditionCol1definition.size());
        assertTrue(conditionCol1definition.get(0) instanceof FactPattern);

        FactPattern conditionCol1fp = (FactPattern) conditionCol1definition.get(0);
        assertEquals("Bonus",
                     conditionCol1fp.getFactType());
        assertEquals(1,
                     conditionCol1fp.getNumberOfConstraints());

        //Pattern 1 - Field Constraint 0
        assertTrue(conditionCol1fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol1fpsfc0 = (SingleFieldConstraint) conditionCol1fp.getConstraint(0);
        assertEquals("employee",
                     conditionCol1fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol1fpsfc0.getOperator());
        assertEquals("param5",
                     conditionCol1fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol1fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_OBJECT,
                     conditionCol1fpsfc0.getFieldType());

        //Pattern 1 - Field Constraint 0 - Variable 0
        BRLConditionVariableColumn conditionCol1param0 = conditionCol1.getChildColumns().get(0);
        assertEquals("param5",
                     conditionCol1param0.getVarName());
        assertEquals("bonus for employee",
                     conditionCol1param0.getHeader());
        assertEquals(DataType.TYPE_OBJECT,
                     conditionCol1param0.getFieldType());
        assertEquals("Bonus",
                     conditionCol1param0.getFactType());
        assertEquals("employee",
                     conditionCol1param0.getFactField());

        //Pattern 2
        assertTrue(dtable.getConditions().get(2) instanceof BRLConditionColumn);

        BRLConditionColumn conditionCol2 = ((BRLConditionColumn) dtable.getConditions().get(2));
        assertEquals("Converted from ['no bonus for employee']",
                     conditionCol2.getHeader());
        assertEquals(1,
                     conditionCol2.getChildColumns().size());

        List<IPattern> conditionCol2definition = conditionCol2.getDefinition();
        assertEquals(1,
                     conditionCol2definition.size());
        assertTrue(conditionCol2definition.get(0) instanceof CompositeFactPattern);

        CompositeFactPattern conditionCol2cfp = (CompositeFactPattern) conditionCol2definition.get(0);
        assertEquals(CompositeFactPattern.COMPOSITE_TYPE_NOT,
                     conditionCol2cfp.getType());
        assertEquals(1,
                     conditionCol2cfp.getPatterns().length);
        assertTrue(conditionCol2cfp.getPatterns()[0] instanceof FactPattern);
        FactPattern conditionCol2fp = (FactPattern) conditionCol2cfp.getPatterns()[0];
        assertEquals("Bonus",
                     conditionCol2fp.getFactType());
        assertEquals(1,
                     conditionCol2fp.getNumberOfConstraints());

        //Pattern 2 - Field Constraint 0
        assertTrue(conditionCol2fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol2fpsfc0 = (SingleFieldConstraint) conditionCol2fp.getConstraint(0);
        assertEquals("employee",
                     conditionCol2fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol2fpsfc0.getOperator());
        assertEquals("param6",
                     conditionCol2fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol2fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_OBJECT,
                     conditionCol2fpsfc0.getFieldType());

        //Pattern 2 - Field Constraint 0 - Variable 0
        BRLConditionVariableColumn conditionCol2param0 = conditionCol2.getChildColumns().get(0);
        assertEquals("param6",
                     conditionCol2param0.getVarName());
        assertEquals("no bonus for employee",
                     conditionCol2param0.getHeader());
        assertEquals(DataType.TYPE_OBJECT,
                     conditionCol2param0.getFieldType());
        assertEquals("Bonus",
                     conditionCol2param0.getFactType());
        assertEquals("employee",
                     conditionCol2param0.getFactField());

        //Check individual action columns
        assertEquals(3,
                     dtable.getActionCols().size());

        //Action 0
        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);

        BRLActionColumn actionCol0 = ((BRLActionColumn) dtable.getActionCols().get(0));
        assertEquals("Converted from ['Inser bonus of amount']",
                     actionCol0.getHeader());
        assertEquals(1,
                     actionCol0.getChildColumns().size());

        List<IAction> actionCol0definition = actionCol0.getDefinition();
        assertEquals(1,
                     actionCol0definition.size());
        assertTrue(actionCol0definition.get(0) instanceof FreeFormLine);

        FreeFormLine actionCol0ffl0 = (FreeFormLine) actionCol0definition.get(0);
        assertEquals("Bonus $b = new Bonus(); $b.setAmount((double)@{param7}); $b.setEmployee($e); insert($b);",
                     actionCol0ffl0.getText());

        //Action 0 - Variable 0
        BRLActionVariableColumn actionCol0param0 = actionCol0.getChildColumns().get(0);
        assertEquals("param7",
                     actionCol0param0.getVarName());
        assertEquals("Inser bonus of amount",
                     actionCol0param0.getHeader());
        assertEquals(DataType.TYPE_OBJECT,
                     actionCol0param0.getFieldType());

        //Action 1
        BRLActionColumn actionCol1 = ((BRLActionColumn) dtable.getActionCols().get(1));
        assertEquals("Converted from ['retract']",
                     actionCol1.getHeader());
        assertEquals(1,
                     actionCol1.getChildColumns().size());

        List<IAction> actionCol1definition = actionCol1.getDefinition();
        assertEquals(1,
                     actionCol1definition.size());
        assertTrue(actionCol1definition.get(0) instanceof ActionRetractFact);
        ActionRetractFact actionCol1arf = (ActionRetractFact) actionCol1definition.get(0);
        assertEquals("$bonus",
                     actionCol1arf.getVariableName());

        assertTrue(dtable.getActionCols().get(1) instanceof BRLActionColumn);

        //Action 1 - Variable 0
        BRLActionVariableColumn actionCol1param0 = actionCol1.getChildColumns().get(0);
        assertEquals("param8",
                     actionCol1param0.getVarName());
        assertEquals("retract",
                     actionCol1param0.getHeader());
        assertEquals(DataType.TYPE_BOOLEAN,
                     actionCol1param0.getFieldType());

        //Action 2
        BRLActionColumn actionCol2 = ((BRLActionColumn) dtable.getActionCols().get(2));
        assertEquals("Converted from ['retract2']",
                     actionCol2.getHeader());
        assertEquals(1,
                     actionCol2.getChildColumns().size());

        List<IAction> actionCol2definition = actionCol2.getDefinition();
        assertEquals(1,
                     actionCol2definition.size());
        assertTrue(actionCol2definition.get(0) instanceof ActionRetractFact);
        ActionRetractFact actionCol2arf = (ActionRetractFact) actionCol2definition.get(0);
        assertEquals("$b",
                     actionCol2arf.getVariableName());

        assertTrue(dtable.getActionCols().get(2) instanceof BRLActionColumn);

        //Action 2 - Variable 1
        BRLActionVariableColumn actionCol2param0 = actionCol2.getChildColumns().get(0);
        assertEquals("",
                     actionCol2param0.getVarName());
        assertEquals("retract2",
                     actionCol2param0.getHeader());
        assertEquals(DataType.TYPE_BOOLEAN,
                     actionCol2param0.getFieldType());

        //Check data
        assertEquals(4,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Lowest bonus", 0, 2, null, false, "", "$e", "400", false, false},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Middle bonus", 2, 6, null, false, "", "$e", "800", false, false},
                                   dtable.getData().get(1)));
        assertTrue(isRowEquivalent(new Object[]{3, "High bonus", null, null, 6, false, "", "", "1000", false, false},
                                   dtable.getData().get(2)));
        assertTrue(isRowEquivalent(new Object[]{4, "Big absence", null, null, null, true, "$e", "", "", true, true},
                                   dtable.getData().get(3)));
    }

    @Test
    //https://issues.jboss.org/browse/RHBPMS-4737
    public void conversionWithBigDecimals() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<>();

        addModelField("org.test.Message",
                      "this",
                      "org.test.Message",
                      DataType.TYPE_THIS);
        addModelField("org.test.Message",
                      "status",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Message",
                      "message",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Message",
                      "rate",
                      BigDecimal.class.getName(),
                      DataType.TYPE_NUMERIC_BIGDECIMAL);

        addModelField("org.test.Reply",
                      "this",
                      "org.test.Reply",
                      DataType.TYPE_THIS);
        addModelField("org.test.Reply",
                      "message",
                      String.class.getName(),
                      DataType.TYPE_STRING);

        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("RHBPMS-4737 (BigDecimal).xlsx");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        checkConversionWithBigMaths(result,
                                    listener,
                                    () -> DataType.TYPE_NUMERIC_BIGDECIMAL,
                                    () -> new BigDecimal("10.00"),
                                    () -> new BigDecimal("20.00"));
    }

    @Test
    //https://issues.jboss.org/browse/RHBPMS-4737
    public void conversionWithBigDecimalsWithoutDMO() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<>();

        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("RHBPMS-4737 (No DMO).xlsx");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        checkConversionWithBigMaths(result,
                                    listener,
                                    () -> DataType.TYPE_NUMERIC,
                                    () -> new BigDecimal("10.00"),
                                    () -> new BigDecimal("20.00"));
    }

    @Test
    //https://issues.jboss.org/browse/RHBPMS-4737
    public void conversionWithBigIntegers() {
        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<>();

        addModelField("org.test.Message",
                      "this",
                      "org.test.Message",
                      DataType.TYPE_THIS);
        addModelField("org.test.Message",
                      "status",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Message",
                      "message",
                      String.class.getName(),
                      DataType.TYPE_STRING);
        addModelField("org.test.Message",
                      "rate",
                      BigDecimal.class.getName(),
                      DataType.TYPE_NUMERIC_BIGINTEGER);

        addModelField("org.test.Reply",
                      "this",
                      "org.test.Reply",
                      DataType.TYPE_THIS);
        addModelField("org.test.Reply",
                      "message",
                      String.class.getName(),
                      DataType.TYPE_STRING);

        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        //Convert
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("RHBPMS-4737 (BigInteger).xlsx");

        try {
            parser.parseFile(is);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                fail(ioe.getMessage());
            }
        }

        checkConversionWithBigMaths(result,
                                    listener,
                                    () -> DataType.TYPE_NUMERIC_BIGINTEGER,
                                    () -> new BigInteger("10"),
                                    () -> new BigInteger("20"));
    }

    private void checkConversionWithBigMaths(final ConversionResult result,
                                             final GuidedDecisionTableGeneratorListener listener,
                                             final Supplier<String> expectedDataType,
                                             final Supplier<Object> expectedDataValueRow0,
                                             final Supplier<Object> expectedDataValueRow1) {

        //Check conversion results
        assertEquals(0,
                     result.getMessages().size());

        //Check basics
        final List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();
        assertNotNull(dtables);
        assertEquals(1,
                     dtables.size());

        GuidedDecisionTable52 dtable = dtables.get(0);

        assertEquals("HelloWorld1",
                     dtable.getTableName());
        assertEquals(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                     dtable.getTableFormat());

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull(columns);
        assertEquals(8,
                     columns.size());
        assertTrue(columns.get(0) instanceof RowNumberCol52);
        assertTrue(columns.get(1) instanceof DescriptionCol52);
        assertTrue(columns.get(2) instanceof BRLConditionVariableColumn);
        assertTrue(columns.get(3) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(4) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(5) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(6) instanceof BRLActionVariableColumn);
        assertTrue(columns.get(7) instanceof BRLActionVariableColumn);

        //Check individual condition columns
        assertEquals(1,
                     dtable.getConditions().size());
        assertTrue(dtable.getConditions().get(0) instanceof BRLConditionColumn);

        BRLConditionColumn conditionCol0 = ((BRLConditionColumn) dtable.getConditions().get(0));
        assertEquals("Converted from ['Status']",
                     conditionCol0.getHeader());
        assertEquals(1,
                     conditionCol0.getChildColumns().size());

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals(1,
                     conditionCol0definition.size());
        assertTrue(conditionCol0definition.get(0) instanceof FactPattern);

        FactPattern conditionCol0fp = (FactPattern) conditionCol0definition.get(0);
        assertEquals("Message",
                     conditionCol0fp.getFactType());
        assertEquals(1,
                     conditionCol0fp.getNumberOfConstraints());

        //Field Constraint 1
        assertTrue(conditionCol0fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint conditionCol0fpsfc0 = (SingleFieldConstraint) conditionCol0fp.getConstraint(0);
        assertEquals("status",
                     conditionCol0fpsfc0.getFieldName());
        assertEquals("==",
                     conditionCol0fpsfc0.getOperator());
        assertEquals("param1",
                     conditionCol0fpsfc0.getValue());
        assertEquals(SingleFieldConstraint.TYPE_TEMPLATE,
                     conditionCol0fpsfc0.getConstraintValueType());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0fpsfc0.getFieldType());

        //Field Constraint 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get(0);
        assertEquals("param1",
                     conditionCol0param0.getVarName());
        assertEquals("Status",
                     conditionCol0param0.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     conditionCol0param0.getFieldType());
        assertEquals("Message",
                     conditionCol0param0.getFactType());
        assertEquals("status",
                     conditionCol0param0.getFactField());

        //Check individual action columns
        assertEquals(3,
                     dtable.getActionCols().size());

        //Action 1
        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);

        BRLActionColumn actionCol0 = ((BRLActionColumn) dtable.getActionCols().get(0));
        assertEquals("Converted from ['Print out message?']",
                     actionCol0.getHeader());
        assertEquals(1,
                     actionCol0.getChildColumns().size());

        List<IAction> actionCol0definition = actionCol0.getDefinition();
        assertEquals(1,
                     actionCol0definition.size());
        assertTrue(actionCol0definition.get(0) instanceof FreeFormLine);

        FreeFormLine actionCol0ffl0 = (FreeFormLine) actionCol0definition.get(0);
        assertEquals("System.out.println(m.getMessage());",
                     actionCol0ffl0.getText());

        //Action 2
        BRLActionColumn actionCol1 = ((BRLActionColumn) dtable.getActionCols().get(1));
        assertEquals("Converted from ['Set message', 'Set status', 'Set rate']",
                     actionCol1.getHeader());
        assertEquals(3,
                     actionCol1.getChildColumns().size());

        List<IAction> actionCol1definition = actionCol1.getDefinition();
        assertEquals(1,
                     actionCol1definition.size());
        assertTrue(actionCol1definition.get(0) instanceof ActionSetField);

        ActionSetField actionCol1asf0 = (ActionSetField) actionCol1definition.get(0);
        assertEquals("m",
                     actionCol1asf0.getVariable());
        ActionFieldValue[] actionCol1asf0afvs = actionCol1asf0.getFieldValues();
        assertEquals(3,
                     actionCol1asf0afvs.length);
        ActionFieldValue actionCol1asf0afv0 = actionCol1asf0afvs[0];
        assertEquals("message",
                     actionCol1asf0afv0.getField());
        assertEquals("param2",
                     actionCol1asf0afv0.getValue());
        assertEquals(FieldNatureType.TYPE_TEMPLATE,
                     actionCol1asf0afv0.getNature());
        assertEquals(DataType.TYPE_STRING,
                     actionCol1asf0afv0.getType());
        ActionFieldValue actionCol1asf0afv1 = actionCol1asf0afvs[1];
        assertEquals("status",
                     actionCol1asf0afv1.getField());
        assertEquals("param3",
                     actionCol1asf0afv1.getValue());
        assertEquals(FieldNatureType.TYPE_TEMPLATE,
                     actionCol1asf0afv1.getNature());
        assertEquals(DataType.TYPE_STRING,
                     actionCol1asf0afv1.getType());

        //Action 1 - Variable 1
        BRLActionVariableColumn actionCol0param0 = actionCol0.getChildColumns().get(0);
        assertEquals("",
                     actionCol0param0.getVarName());
        assertEquals("Print out message?",
                     actionCol0param0.getHeader());
        assertEquals(DataType.TYPE_BOOLEAN,
                     actionCol0param0.getFieldType());
        assertNull(actionCol0param0.getFactType());
        assertNull(actionCol0param0.getFactField());

        assertTrue(dtable.getActionCols().get(0) instanceof BRLActionColumn);

        //Action 2 - Variable 1
        BRLActionVariableColumn actionCol1param0 = actionCol1.getChildColumns().get(0);
        assertEquals("param2",
                     actionCol1param0.getVarName());
        assertEquals("Set message",
                     actionCol1param0.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     actionCol1param0.getFieldType());
        assertEquals("Message",
                     actionCol1param0.getFactType());
        assertEquals("message",
                     actionCol1param0.getFactField());

        //Action 2 - Variable 2
        BRLActionVariableColumn actionCol1param1 = actionCol1.getChildColumns().get(1);
        assertEquals("param3",
                     actionCol1param1.getVarName());
        assertEquals("Set status",
                     actionCol1param1.getHeader());
        assertEquals(DataType.TYPE_STRING,
                     actionCol1param1.getFieldType());
        assertEquals("Message",
                     actionCol1param1.getFactType());
        assertEquals("status",
                     actionCol1param1.getFactField());

        //Action 2 - Variable 3
        BRLActionVariableColumn actionCol1param2 = actionCol1.getChildColumns().get(2);
        assertEquals("param4",
                     actionCol1param2.getVarName());
        assertEquals("Set rate",
                     actionCol1param2.getHeader());
        assertEquals(expectedDataType.get(),
                     actionCol1param2.getFieldType());
        assertEquals("Message",
                     actionCol1param2.getFactType());
        assertEquals("rate",
                     actionCol1param2.getFactField());

        //Check data
        assertEquals(2,
                     dtable.getData().size());
        assertTrue(isRowEquivalent(new Object[]{1, "Hello World", "Message.HELLO", false, "Goodbye cruel world", "Message.GOODBYE", expectedDataValueRow0.get(), "m"},
                                   dtable.getData().get(0)));
        assertTrue(isRowEquivalent(new Object[]{2, "Goodbye", "Message.GOODBYE", false, "", "", expectedDataValueRow1.get(), "m"},
                                   dtable.getData().get(1)));
    }

    private boolean isRowEquivalent(Object[] expected,
                                    List<DTCellValue52> actual) {
        //Sizes should match
        if (expected.length != actual.size()) {
            return false;
        }

        //Column values
        for (int i = 0; i < expected.length; i++) {
            DTCellValue52 dcv = actual.get(i);
            switch (dcv.getDataType()) {
                case NUMERIC:
                    final BigDecimal numeric = (BigDecimal) dcv.getNumericValue();
                    if (!assertValues(expected[i],
                                      numeric)) {
                        return false;
                    }
                    break;
                case NUMERIC_BIGDECIMAL:
                    final BigDecimal numericBigDecimal = (BigDecimal) dcv.getNumericValue();
                    if (!assertValues(expected[i],
                                      numericBigDecimal)) {
                        return false;
                    }
                    break;
                case NUMERIC_BIGINTEGER:
                    final BigInteger numericBigInteger = (BigInteger) dcv.getNumericValue();
                    if (!assertValues(expected[i],
                                      numericBigInteger)) {
                        return false;
                    }
                    break;
                case NUMERIC_BYTE:
                    final Byte numericByte = (Byte) dcv.getNumericValue();
                    if (!assertValues(expected[i],
                                      numericByte)) {
                        return false;
                    }
                    break;
                case NUMERIC_DOUBLE:
                    final Double numericDouble = (Double) dcv.getNumericValue();
                    if (!assertValues(expected[i],
                                      numericDouble)) {
                        return false;
                    }
                    break;
                case NUMERIC_FLOAT:
                    final Float numericFloat = (Float) dcv.getNumericValue();
                    if (!assertValues(expected[i],
                                      numericFloat)) {
                        return false;
                    }
                    break;
                case NUMERIC_INTEGER:
                    final Integer numericInteger = (Integer) dcv.getNumericValue();
                    if (!assertValues(expected[i],
                                      numericInteger)) {
                        return false;
                    }
                    break;
                case NUMERIC_LONG:
                    final Long numericLong = (Long) dcv.getNumericValue();
                    if (!assertValues(expected[i],
                                      numericLong)) {
                        return false;
                    }
                    break;
                case NUMERIC_SHORT:
                    final Short numericShort = (Short) dcv.getNumericValue();
                    if (!assertValues(expected[i],
                                      numericShort)) {
                        return false;
                    }
                    break;
                case BOOLEAN:
                    if (!assertValues(expected[i],
                                      dcv.getBooleanValue())) {
                        return false;
                    }
                    break;
                default:
                    if (!assertValues(expected[i],
                                      dcv.getStringValue())) {
                        return false;
                    }
            }
        }
        return true;
    }

    private boolean assertValues(final Object expected,
                                 final Object actual) {
        if (expected == null) {
            return actual == null;
        }
        return expected.equals(actual);
    }

    private void addModelField(final String factName,
                               final String fieldName,
                               final String clazz,
                               final String type) {
        ModelField[] modelFields = new ModelField[1];
        modelFields[0] = new ModelField(fieldName,
                                        clazz,
                                        ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                        ModelField.FIELD_ORIGIN.DECLARED,
                                        FieldAccessorsAndMutators.BOTH,
                                        type);
        if (packageModelFields.containsKey(factName)) {
            final List<ModelField> existingModelFields = new ArrayList<ModelField>(Arrays.asList(packageModelFields.get(factName)));
            existingModelFields.add(modelFields[0]);
            modelFields = existingModelFields.toArray(modelFields);
        }
        packageModelFields.put(factName,
                               modelFields);
    }
}
