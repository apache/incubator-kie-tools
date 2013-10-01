/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.dtablexls.backend.server.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DataListener;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessageType;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.AnalysisCol52;
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
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the conversion of XLS Decision Tables to Guided Decision Tables
 */
public class DecisionTableXLSToDecisionTableGuidedConverterTest {

    @Test
    public void testAttributes() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "Attributes.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 0,
                      result.getMessages().size() );

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull( dtables );
        assertEquals( 1,
                      dtables.size() );

        GuidedDecisionTable52 dtable = dtables.get( 0 );

        assertEquals( "AttributesTest",
                      dtable.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable.getTableFormat() );

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull( columns );
        assertEquals( 13,
                      columns.size() );
        assertTrue( columns.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns.get( 2 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 3 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 4 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 5 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 6 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 7 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 8 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 9 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 10 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 11 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 12 ) instanceof AnalysisCol52 );

        //Check individual attributes
        AttributeCol52 attrCol2 = ( (AttributeCol52) columns.get( 2 ) );
        assertEquals( GuidedDecisionTable52.SALIENCE_ATTR,
                      attrCol2.getAttribute() );
        assertFalse( attrCol2.isUseRowNumber() );
        assertFalse( attrCol2.isReverseOrder() );

        AttributeCol52 attrCol3 = ( (AttributeCol52) columns.get( 3 ) );
        assertEquals( GuidedDecisionTable52.ACTIVATION_GROUP_ATTR,
                      attrCol3.getAttribute() );

        AttributeCol52 attrCol4 = ( (AttributeCol52) columns.get( 4 ) );
        assertEquals( GuidedDecisionTable52.DURATION_ATTR,
                      attrCol4.getAttribute() );

        AttributeCol52 attrCol5 = ( (AttributeCol52) columns.get( 5 ) );
        assertEquals( GuidedDecisionTable52.TIMER_ATTR,
                      attrCol5.getAttribute() );

        AttributeCol52 attrCol6 = ( (AttributeCol52) columns.get( 6 ) );
        assertEquals( GuidedDecisionTable52.CALENDARS_ATTR,
                      attrCol6.getAttribute() );

        AttributeCol52 attrCol7 = ( (AttributeCol52) columns.get( 7 ) );
        assertEquals( GuidedDecisionTable52.NO_LOOP_ATTR,
                      attrCol7.getAttribute() );

        AttributeCol52 attrCol8 = ( (AttributeCol52) columns.get( 8 ) );
        assertEquals( GuidedDecisionTable52.LOCK_ON_ACTIVE_ATTR,
                      attrCol8.getAttribute() );

        AttributeCol52 attrCol9 = ( (AttributeCol52) columns.get( 9 ) );
        assertEquals( GuidedDecisionTable52.AUTO_FOCUS_ATTR,
                      attrCol9.getAttribute() );

        AttributeCol52 attrCol10 = ( (AttributeCol52) columns.get( 10 ) );
        assertEquals( GuidedDecisionTable52.AGENDA_GROUP_ATTR,
                      attrCol10.getAttribute() );

        AttributeCol52 attrCol11 = ( (AttributeCol52) columns.get( 11 ) );
        assertEquals( GuidedDecisionTable52.RULEFLOW_GROUP_ATTR,
                      attrCol11.getAttribute() );

        //Check data
        assertEquals( 2,
                      dtable.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Specific rule 1", "1", "g1", "100", "T1", "CAL1", "TRUE", "TRUE", "TRUE", "AG1", "RFG1" },
                                     dtable.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Specific rule 2", "2", "g2", "200", "T2", "CAL2", "FALSE", "FALSE", "FALSE", "AG2", "RFG2" },
                                     dtable.getData().get( 1 ) ) );
    }

    @Test
    public void testSequentialSalience() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "SequentialSalience.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 0,
                      result.getMessages().size() );

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull( dtables );
        assertEquals( 1,
                      dtables.size() );

        GuidedDecisionTable52 dtable = dtables.get( 0 );

        assertEquals( "SequentialSalienceTest",
                      dtable.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable.getTableFormat() );

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull( columns );
        assertEquals( 4,
                      columns.size() );
        assertTrue( columns.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns.get( 2 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 3 ) instanceof AnalysisCol52 );

        //Check attribute column
        AttributeCol52 attrCol2 = ( (AttributeCol52) columns.get( 2 ) );
        assertEquals( GuidedDecisionTable52.SALIENCE_ATTR,
                      attrCol2.getAttribute() );
        assertTrue( attrCol2.isUseRowNumber() );
        assertTrue( attrCol2.isReverseOrder() );

        //Check data
        assertEquals( 2,
                      dtable.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Created from row 8", "2" },
                                     dtable.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Created from row 9", "1" },
                                     dtable.getData().get( 1 ) ) );
    }

    @Test
    public void testSalienceWarnings() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "SalienceWarnings.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 2,
                      result.getMessages().size() );
        assertEquals( ConversionMessageType.WARNING,
                      result.getMessages().get( 0 ).getMessageType() );
        assertFalse( result.getMessages().get( 0 ).getMessage().indexOf( "Priority is not an integer literal, in cell C7" ) == -1 );
        assertEquals( ConversionMessageType.WARNING,
                      result.getMessages().get( 1 ).getMessageType() );
        assertFalse( result.getMessages().get( 1 ).getMessage().indexOf( "Priority is not an integer literal, in cell C8" ) == -1 );

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull( dtables );
        assertEquals( 1,
                      dtables.size() );

        GuidedDecisionTable52 dtable = dtables.get( 0 );

        assertEquals( "SalienceWarningsTest",
                      dtable.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable.getTableFormat() );

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull( columns );
        assertEquals( 4,
                      columns.size() );
        assertTrue( columns.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns.get( 2 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 3 ) instanceof AnalysisCol52 );

        //Check attribute column
        AttributeCol52 attrCol2 = ( (AttributeCol52) columns.get( 2 ) );
        assertEquals( GuidedDecisionTable52.SALIENCE_ATTR,
                      attrCol2.getAttribute() );
        assertFalse( attrCol2.isUseRowNumber() );
        assertFalse( attrCol2.isReverseOrder() );

        //Check data
        assertEquals( 2,
                      dtable.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Created from row 7", "" },
                                     dtable.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Created from row 8", "" },
                                     dtable.getData().get( 1 ) ) );
    }

    @Test
    public void testDurationWarnings() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "DurationWarnings.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 2,
                      result.getMessages().size() );
        assertEquals( ConversionMessageType.WARNING,
                      result.getMessages().get( 0 ).getMessageType() );
        assertFalse( result.getMessages().get( 0 ).getMessage().indexOf( "Duration is not an long literal, in cell C7" ) == -1 );
        assertEquals( ConversionMessageType.WARNING,
                      result.getMessages().get( 1 ).getMessageType() );
        assertFalse( result.getMessages().get( 1 ).getMessage().indexOf( "Duration is not an long literal, in cell C8" ) == -1 );

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull( dtables );
        assertEquals( 1,
                      dtables.size() );

        GuidedDecisionTable52 dtable = dtables.get( 0 );

        assertEquals( "DurationWarningsTest",
                      dtable.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable.getTableFormat() );

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull( columns );
        assertEquals( 4,
                      columns.size() );
        assertTrue( columns.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns.get( 2 ) instanceof AttributeCol52 );
        assertTrue( columns.get( 3 ) instanceof AnalysisCol52 );

        //Check attribute column
        AttributeCol52 attrCol2 = ( (AttributeCol52) columns.get( 2 ) );
        assertEquals( GuidedDecisionTable52.DURATION_ATTR,
                      attrCol2.getAttribute() );
        assertFalse( attrCol2.isUseRowNumber() );
        assertFalse( attrCol2.isReverseOrder() );

        //Check data
        assertEquals( 2,
                      dtable.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Created from row 7", "" },
                                     dtable.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Created from row 8", "" },
                                     dtable.getData().get( 1 ) ) );
    }

    @Test
    public void testMetadata() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "Metadata.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 0,
                      result.getMessages().size() );

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull( dtables );
        assertEquals( 1,
                      dtables.size() );

        GuidedDecisionTable52 dtable = dtables.get( 0 );

        assertEquals( "MetadataTest",
                      dtable.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable.getTableFormat() );

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull( columns );
        assertEquals( 4,
                      columns.size() );
        assertTrue( columns.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns.get( 2 ) instanceof MetadataCol52 );
        assertTrue( columns.get( 3 ) instanceof AnalysisCol52 );

        //Check metadata column
        MetadataCol52 mdCol2 = ( (MetadataCol52) columns.get( 2 ) );
        assertEquals( "cheese",
                      mdCol2.getMetadata() );

        //Check data
        assertEquals( 2,
                      dtable.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Created from row 7", "cheddar" },
                                     dtable.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Created from row 8", "edam" },
                                     dtable.getData().get( 1 ) ) );
    }

    @Test
    public void testActions() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "Actions.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 0,
                      result.getMessages().size() );

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull( dtables );
        assertEquals( 1,
                      dtables.size() );

        GuidedDecisionTable52 dtable = dtables.get( 0 );

        assertEquals( "ActionsTest",
                      dtable.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable.getTableFormat() );

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull( columns );
        assertEquals( 8,
                      columns.size() );
        assertTrue( columns.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns.get( 2 ) instanceof BRLActionVariableColumn );
        assertTrue( columns.get( 3 ) instanceof BRLActionVariableColumn );
        assertTrue( columns.get( 4 ) instanceof BRLActionVariableColumn );
        assertTrue( columns.get( 5 ) instanceof BRLActionVariableColumn );
        assertTrue( columns.get( 6 ) instanceof BRLActionVariableColumn );
        assertTrue( columns.get( 7 ) instanceof AnalysisCol52 );

        //Check individual action columns
        assertEquals( 4,
                      dtable.getActionCols().size() );
        assertTrue( dtable.getActionCols().get( 0 ) instanceof BRLActionColumn );
        assertTrue( dtable.getActionCols().get( 1 ) instanceof BRLActionColumn );
        assertTrue( dtable.getActionCols().get( 2 ) instanceof BRLActionColumn );
        assertTrue( dtable.getActionCols().get( 3 ) instanceof BRLActionColumn );

        //Column 1
        BRLActionColumn actionCol0 = ( (BRLActionColumn) dtable.getActionCols().get( 0 ) );
        assertEquals( "Multi-parameters",
                      actionCol0.getHeader() );
        assertEquals( 2,
                      actionCol0.getChildColumns().size() );

        List<IAction> actionCol0definition = actionCol0.getDefinition();
        assertEquals( 1,
                      actionCol0definition.size() );
        assertTrue( actionCol0definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine actionCol0ffl = (FreeFormLine) actionCol0definition.get( 0 );
        assertEquals( "policy.setBasePrice(@{param1}, @{param2});",
                      actionCol0ffl.getText() );

        //Column 1 - Variable 1
        BRLActionVariableColumn actionCol0param0 = actionCol0.getChildColumns().get( 0 );
        assertEquals( "param1",
                      actionCol0param0.getVarName() );
        assertEquals( "Multi-parameters",
                      actionCol0param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      actionCol0param0.getFieldType() );
        assertNull( actionCol0param0.getFactType() );
        assertNull( actionCol0param0.getFactField() );

        //Column 1 - Variable 2
        BRLActionVariableColumn actionCol0param1 = actionCol0.getChildColumns().get( 1 );
        assertEquals( "param2",
                      actionCol0param1.getVarName() );
        assertEquals( "Multi-parameters",
                      actionCol0param1.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      actionCol0param1.getFieldType() );
        assertNull( actionCol0param1.getFactType() );
        assertNull( actionCol0param1.getFactField() );

        //Column 2
        BRLActionColumn actionCol1 = ( (BRLActionColumn) dtable.getActionCols().get( 1 ) );
        assertEquals( "Single-parameter",
                      actionCol1.getHeader() );
        assertEquals( 1,
                      actionCol1.getChildColumns().size() );

        List<IAction> actionCol1definition = actionCol1.getDefinition();
        assertEquals( 1,
                      actionCol1definition.size() );
        assertTrue( actionCol1definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine actionCol1ffl = (FreeFormLine) actionCol1definition.get( 0 );
        assertEquals( "policy.setSmurf(@{param3});",
                      actionCol1ffl.getText() );

        //Column 2 - Variable 1
        BRLActionVariableColumn actionCol1param0 = actionCol1.getChildColumns().get( 0 );
        assertEquals( "param3",
                      actionCol1param0.getVarName() );
        assertEquals( "Single-parameter",
                      actionCol1param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      actionCol1param0.getFieldType() );
        assertNull( actionCol1param0.getFactType() );
        assertNull( actionCol1param0.getFactField() );

        //Column 3
        BRLActionColumn actionCol2 = ( (BRLActionColumn) dtable.getActionCols().get( 2 ) );
        assertEquals( "Log-single-parameter",
                      actionCol2.getHeader() );
        assertEquals( 1,
                      actionCol2.getChildColumns().size() );

        List<IAction> actionCol2definition = actionCol2.getDefinition();
        assertEquals( 1,
                      actionCol2definition.size() );
        assertTrue( actionCol2definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine actionCol2ffl = (FreeFormLine) actionCol2definition.get( 0 );
        assertEquals( "System.out.println(\"@{param4}\");",
                      actionCol2ffl.getText() );

        //Column 3 - Variable 1
        BRLActionVariableColumn actionCol2param0 = actionCol2.getChildColumns().get( 0 );
        assertEquals( "param4",
                      actionCol2param0.getVarName() );
        assertEquals( "Log-single-parameter",
                      actionCol2param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      actionCol2param0.getFieldType() );
        assertNull( actionCol2param0.getFactType() );
        assertNull( actionCol2param0.getFactField() );

        //Column 4
        BRLActionColumn actionCol3 = ( (BRLActionColumn) dtable.getActionCols().get( 3 ) );
        assertEquals( "Zero-parameters",
                      actionCol3.getHeader() );
        assertEquals( 1,
                      actionCol3.getChildColumns().size() );

        List<IAction> actionCol3definition = actionCol3.getDefinition();
        assertEquals( 1,
                      actionCol3definition.size() );
        assertTrue( actionCol3definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine actionCol3ffl = (FreeFormLine) actionCol3definition.get( 0 );
        assertEquals( "System.out.println(\"Woot\");",
                      actionCol3ffl.getText() );

        //Column 3 - Variable 1
        BRLActionVariableColumn actionCol3param0 = actionCol3.getChildColumns().get( 0 );
        assertEquals( "",
                      actionCol3param0.getVarName() );
        assertEquals( "Zero-parameters",
                      actionCol3param0.getHeader() );
        assertEquals( DataType.TYPE_BOOLEAN,
                      actionCol3param0.getFieldType() );
        assertNull( actionCol3param0.getFactType() );
        assertNull( actionCol3param0.getFactField() );

        //Check data
        assertEquals( 2,
                      dtable.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Created from row 7", "10", "20", "30", "hello", "TRUE" },
                                     dtable.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Created from row 8", "50", "60", "70", "goodbye", "FALSE" },
                                     dtable.getData().get( 1 ) ) );
    }

    @Test
    public void testConditions() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "Conditions.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 0,
                      result.getMessages().size() );

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull( dtables );
        assertEquals( 1,
                      dtables.size() );

        GuidedDecisionTable52 dtable = dtables.get( 0 );

        assertEquals( "ConditionsTest",
                      dtable.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable.getTableFormat() );

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull( columns );
        assertEquals( 8,
                      columns.size() );
        assertTrue( columns.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns.get( 2 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 3 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 4 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 5 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 6 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 7 ) instanceof AnalysisCol52 );

        //Check individual condition columns
        assertEquals( 2,
                      dtable.getConditions().size() );
        assertTrue( dtable.getConditions().get( 0 ) instanceof BRLConditionColumn );
        assertTrue( dtable.getConditions().get( 1 ) instanceof BRLConditionColumn );

        //Column 1
        BRLConditionColumn conditionCol0 = ( (BRLConditionColumn) dtable.getConditions().get( 0 ) );
        assertEquals( "Converted from cell [C4]",
                      conditionCol0.getHeader() );
        assertEquals( 3,
                      conditionCol0.getChildColumns().size() );

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals( 1,
                      conditionCol0definition.size() );
        assertTrue( conditionCol0definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine conditionCol0ffl = (FreeFormLine) conditionCol0definition.get( 0 );
        assertEquals( "Driver(age > \"@{param1}\", firstName == \"@{param2}\", surname == \"@{param3}\")",
                      conditionCol0ffl.getText() );

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get( 0 );
        assertEquals( "param1",
                      conditionCol0param0.getVarName() );
        assertEquals( "Converted from cell [C4]",
                      conditionCol0param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol0param0.getFieldType() );
        assertNull( conditionCol0param0.getFactType() );
        assertNull( conditionCol0param0.getFactField() );

        //Column 1 - Variable 2
        BRLConditionVariableColumn conditionCol0param1 = conditionCol0.getChildColumns().get( 1 );
        assertEquals( "param2",
                      conditionCol0param1.getVarName() );
        assertEquals( "Converted from cell [C4]",
                      conditionCol0param1.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol0param1.getFieldType() );
        assertNull( conditionCol0param1.getFactType() );
        assertNull( conditionCol0param1.getFactField() );

        //Column 1 - Variable 3
        BRLConditionVariableColumn conditionCol0param2 = conditionCol0.getChildColumns().get( 2 );
        assertEquals( "param3",
                      conditionCol0param2.getVarName() );
        assertEquals( "Converted from cell [C4]",
                      conditionCol0param2.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol0param2.getFieldType() );
        assertNull( conditionCol0param2.getFactType() );
        assertNull( conditionCol0param2.getFactField() );

        //Column 2
        BRLConditionColumn conditionCol1 = ( (BRLConditionColumn) dtable.getConditions().get( 1 ) );
        assertEquals( "something",
                      conditionCol1.getHeader() );
        assertEquals( 2,
                      conditionCol1.getChildColumns().size() );

        List<IPattern> conditionCol1definition = conditionCol1.getDefinition();
        assertEquals( 1,
                      conditionCol1definition.size() );
        assertTrue( conditionCol1definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine conditionCol1ffl = (FreeFormLine) conditionCol1definition.get( 0 );
        assertEquals( "Vehicle(make == \"@{param4}\", model == \"@{param5}\")",
                      conditionCol1ffl.getText() );

        //Column 2 - Variable 1
        BRLConditionVariableColumn conditionCol1param0 = conditionCol1.getChildColumns().get( 0 );
        assertEquals( "param4",
                      conditionCol1param0.getVarName() );
        assertEquals( "something",
                      conditionCol1param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol1param0.getFieldType() );
        assertNull( conditionCol1param0.getFactType() );
        assertNull( conditionCol1param0.getFactField() );

        //Column 2 - Variable 2
        BRLConditionVariableColumn conditionCol1param1 = conditionCol1.getChildColumns().get( 1 );
        assertEquals( "param5",
                      conditionCol1param1.getVarName() );
        assertEquals( "something",
                      conditionCol1param1.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol1param1.getFieldType() );
        assertNull( conditionCol1param1.getFactType() );
        assertNull( conditionCol1param1.getFactField() );

        //Check data
        assertEquals( 2,
                      dtable.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Created from row 7", "20", "Mike", "Brown", "BMW", "M3" },
                                     dtable.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Created from row 8", "30", "Jason", "Grey", "Audi", "S4" },
                                     dtable.getData().get( 1 ) ) );
    }

    @Test
    public void testConditionsIndexedParameters() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "Conditions-indexedParameters.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 0,
                      result.getMessages().size() );

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull( dtables );
        assertEquals( 1,
                      dtables.size() );

        GuidedDecisionTable52 dtable = dtables.get( 0 );

        assertEquals( "ConditionsTest",
                      dtable.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable.getTableFormat() );

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull( columns );
        assertEquals( 8,
                      columns.size() );
        assertTrue( columns.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns.get( 2 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 3 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 4 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 5 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 6 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 7 ) instanceof AnalysisCol52 );

        //Check individual condition columns
        assertEquals( 2,
                      dtable.getConditions().size() );
        assertTrue( dtable.getConditions().get( 0 ) instanceof BRLConditionColumn );
        assertTrue( dtable.getConditions().get( 1 ) instanceof BRLConditionColumn );

        //Column 1
        BRLConditionColumn conditionCol0 = ( (BRLConditionColumn) dtable.getConditions().get( 0 ) );
        assertEquals( "Converted from cell [C4]",
                      conditionCol0.getHeader() );
        assertEquals( 3,
                      conditionCol0.getChildColumns().size() );

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals( 1,
                      conditionCol0definition.size() );
        assertTrue( conditionCol0definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine conditionCol0ffl = (FreeFormLine) conditionCol0definition.get( 0 );
        assertEquals( "Driver(age > \"@{param1}\", firstName == \"@{param2}\", surname == \"@{param3}\")",
                      conditionCol0ffl.getText() );

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get( 0 );
        assertEquals( "param1",
                      conditionCol0param0.getVarName() );
        assertEquals( "Converted from cell [C4]",
                      conditionCol0param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol0param0.getFieldType() );
        assertNull( conditionCol0param0.getFactType() );
        assertNull( conditionCol0param0.getFactField() );

        //Column 1 - Variable 2
        BRLConditionVariableColumn conditionCol0param1 = conditionCol0.getChildColumns().get( 1 );
        assertEquals( "param2",
                      conditionCol0param1.getVarName() );
        assertEquals( "Converted from cell [C4]",
                      conditionCol0param1.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol0param1.getFieldType() );
        assertNull( conditionCol0param1.getFactType() );
        assertNull( conditionCol0param1.getFactField() );

        //Column 1 - Variable 3
        BRLConditionVariableColumn conditionCol0param2 = conditionCol0.getChildColumns().get( 2 );
        assertEquals( "param3",
                      conditionCol0param2.getVarName() );
        assertEquals( "Converted from cell [C4]",
                      conditionCol0param2.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol0param2.getFieldType() );
        assertNull( conditionCol0param2.getFactType() );
        assertNull( conditionCol0param2.getFactField() );

        //Column 2
        BRLConditionColumn conditionCol1 = ( (BRLConditionColumn) dtable.getConditions().get( 1 ) );
        assertEquals( "something",
                      conditionCol1.getHeader() );
        assertEquals( 2,
                      conditionCol1.getChildColumns().size() );

        List<IPattern> conditionCol1definition = conditionCol1.getDefinition();
        assertEquals( 1,
                      conditionCol1definition.size() );
        assertTrue( conditionCol1definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine conditionCol1ffl = (FreeFormLine) conditionCol1definition.get( 0 );
        assertEquals( "Vehicle(make == \"@{param4}\", model == \"@{param5}\")",
                      conditionCol1ffl.getText() );

        //Column 2 - Variable 1
        BRLConditionVariableColumn conditionCol1param0 = conditionCol1.getChildColumns().get( 0 );
        assertEquals( "param4",
                      conditionCol1param0.getVarName() );
        assertEquals( "something",
                      conditionCol1param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol1param0.getFieldType() );
        assertNull( conditionCol1param0.getFactType() );
        assertNull( conditionCol1param0.getFactField() );

        //Column 2 - Variable 2
        BRLConditionVariableColumn conditionCol1param1 = conditionCol1.getChildColumns().get( 1 );
        assertEquals( "param5",
                      conditionCol1param1.getVarName() );
        assertEquals( "something",
                      conditionCol1param1.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol1param1.getFieldType() );
        assertNull( conditionCol1param1.getFactType() );
        assertNull( conditionCol1param1.getFactField() );

        //Check data
        assertEquals( 2,
                      dtable.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Created from row 7", "20", "Mike", "Brown", "BMW", "M3" },
                                     dtable.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Created from row 8", "30", "Jason", "Grey", "", "" },
                                     dtable.getData().get( 1 ) ) );
    }

    @Test
    public void testMultipleRuleTables() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "MultipleRuleTables.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 0,
                      result.getMessages().size() );

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull( dtables );
        assertEquals( 2,
                      dtables.size() );

        GuidedDecisionTable52 dtable0 = dtables.get( 0 );
        assertEquals( "Table1",
                      dtable0.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable0.getTableFormat() );

        GuidedDecisionTable52 dtable1 = dtables.get( 1 );
        assertEquals( "Table2",
                      dtable1.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable1.getTableFormat() );

        //Check expanded columns
        List<BaseColumn> columns0 = dtable0.getExpandedColumns();
        assertNotNull( columns0 );
        assertEquals( 6,
                      columns0.size() );
        assertTrue( columns0.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns0.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns0.get( 2 ) instanceof AttributeCol52 );
        assertTrue( columns0.get( 3 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns0.get( 4 ) instanceof BRLActionVariableColumn );
        assertTrue( columns0.get( 5 ) instanceof AnalysisCol52 );

        AttributeCol52 attrCol0_2 = ( (AttributeCol52) columns0.get( 2 ) );
        assertEquals( GuidedDecisionTable52.AGENDA_GROUP_ATTR,
                      attrCol0_2.getAttribute() );

        //Check individual condition columns
        assertEquals( 1,
                      dtable0.getConditions().size() );
        assertTrue( dtable0.getConditions().get( 0 ) instanceof BRLConditionColumn );

        //Column 1
        BRLConditionColumn conditionCol0_0 = ( (BRLConditionColumn) dtable0.getConditions().get( 0 ) );
        assertEquals( "Person's name",
                      conditionCol0_0.getHeader() );
        assertEquals( 1,
                      conditionCol0_0.getChildColumns().size() );

        List<IPattern> conditionCol0_0definition = conditionCol0_0.getDefinition();
        assertEquals( 1,
                      conditionCol0_0definition.size() );
        assertTrue( conditionCol0_0definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine conditionCol0_0ffl = (FreeFormLine) conditionCol0_0definition.get( 0 );
        assertEquals( "Person(name == \"@{param1}\")",
                      conditionCol0_0ffl.getText() );

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0_0param0 = conditionCol0_0.getChildColumns().get( 0 );
        assertEquals( "param1",
                      conditionCol0_0param0.getVarName() );
        assertEquals( "Person's name",
                      conditionCol0_0param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol0_0param0.getFieldType() );
        assertNull( conditionCol0_0param0.getFactType() );
        assertNull( conditionCol0_0param0.getFactField() );

        //Column 2
        BRLActionColumn actionCol0_0 = ( (BRLActionColumn) dtable0.getActionCols().get( 0 ) );
        assertEquals( "Salutation",
                      actionCol0_0.getHeader() );
        assertEquals( 1,
                      actionCol0_0.getChildColumns().size() );

        List<IAction> actionCol0_0definition = actionCol0_0.getDefinition();
        assertEquals( 1,
                      actionCol0_0definition.size() );
        assertTrue( actionCol0_0definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine actionCol0_0ffl = (FreeFormLine) actionCol0_0definition.get( 0 );
        assertEquals( "System.out.println(\"@{param2}\");",
                      actionCol0_0ffl.getText() );

        //Column 1 - Variable 1
        BRLActionVariableColumn actionCol0_0param0 = actionCol0_0.getChildColumns().get( 0 );
        assertEquals( "param2",
                      actionCol0_0param0.getVarName() );
        assertEquals( "Salutation",
                      actionCol0_0param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      actionCol0_0param0.getFieldType() );
        assertNull( actionCol0_0param0.getFactType() );
        assertNull( actionCol0_0param0.getFactField() );

        //Check data
        assertEquals( 2,
                      dtable0.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Created from row 7", "AG1", "John", "Hello Sir" },
                                     dtable0.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Created from row 8", "AG2", "Jane", "Hello Madam" },
                                     dtable0.getData().get( 1 ) ) );

        //Check expanded columns
        List<BaseColumn> columns1 = dtable1.getExpandedColumns();
        assertNotNull( columns1 );
        assertEquals( 5,
                      columns1.size() );
        assertTrue( columns1.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns1.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns1.get( 2 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns1.get( 3 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns1.get( 4 ) instanceof AnalysisCol52 );

        //Check individual condition columns
        assertEquals( 1,
                      dtable0.getConditions().size() );
        assertTrue( dtable0.getConditions().get( 0 ) instanceof BRLConditionColumn );

        //Column 1
        BRLConditionColumn conditionCol1_0 = ( (BRLConditionColumn) dtable1.getConditions().get( 0 ) );
        assertEquals( "Converted from cell [C12]",
                      conditionCol1_0.getHeader() );
        assertEquals( 2,
                      conditionCol1_0.getChildColumns().size() );

        List<IPattern> conditionCol1_0definition = conditionCol1_0.getDefinition();
        assertEquals( 1,
                      conditionCol1_0definition.size() );
        assertTrue( conditionCol1_0definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine conditionCol1_0ffl = (FreeFormLine) conditionCol1_0definition.get( 0 );
        assertEquals( "Person(name == \"@{param1}\", age == \"@{param2}\")",
                      conditionCol1_0ffl.getText() );

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol1_0param0 = conditionCol1_0.getChildColumns().get( 0 );
        assertEquals( "param1",
                      conditionCol1_0param0.getVarName() );
        assertEquals( "Converted from cell [C12]",
                      conditionCol1_0param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol1_0param0.getFieldType() );
        assertNull( conditionCol1_0param0.getFactType() );
        assertNull( conditionCol1_0param0.getFactField() );

        //Column 1 - Variable 2
        BRLConditionVariableColumn conditionCol1_0param1 = conditionCol1_0.getChildColumns().get( 1 );
        assertEquals( "param2",
                      conditionCol1_0param1.getVarName() );
        assertEquals( "Converted from cell [C12]",
                      conditionCol1_0param1.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol1_0param1.getFieldType() );
        assertNull( conditionCol1_0param1.getFactType() );
        assertNull( conditionCol1_0param1.getFactField() );

        //Check data
        assertEquals( 2,
                      dtable1.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Created from row 15", "John", "25" },
                                     dtable1.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Created from row 16", "Jane", "29" },
                                     dtable1.getData().get( 1 ) ) );

    }

    @Test
    public void testMultipleSingleParameters() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "MultipleSingleParameters.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 0,
                      result.getMessages().size() );

        //Check basics
        List<GuidedDecisionTable52> dtables = listener.getGuidedDecisionTables();

        assertNotNull( dtables );
        assertEquals( 1,
                      dtables.size() );

        GuidedDecisionTable52 dtable = dtables.get( 0 );

        assertEquals( "MultipleSingleParameters",
                      dtable.getTableName() );
        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      dtable.getTableFormat() );

        //Check expanded columns
        List<BaseColumn> columns = dtable.getExpandedColumns();
        assertNotNull( columns );
        assertEquals( 4,
                      columns.size() );
        assertTrue( columns.get( 0 ) instanceof RowNumberCol52 );
        assertTrue( columns.get( 1 ) instanceof DescriptionCol52 );
        assertTrue( columns.get( 2 ) instanceof BRLConditionVariableColumn );
        assertTrue( columns.get( 3 ) instanceof AnalysisCol52 );

        //Check individual condition columns
        assertEquals( 1,
                      dtable.getConditions().size() );
        assertTrue( dtable.getConditions().get( 0 ) instanceof BRLConditionColumn );

        //Column 1
        BRLConditionColumn conditionCol0 = ( (BRLConditionColumn) dtable.getConditions().get( 0 ) );
        assertEquals( "Re-using single parameter",
                      conditionCol0.getHeader() );
        assertEquals( 1,
                      conditionCol0.getChildColumns().size() );

        List<IPattern> conditionCol0definition = conditionCol0.getDefinition();
        assertEquals( 1,
                      conditionCol0definition.size() );
        assertTrue( conditionCol0definition.get( 0 ) instanceof FreeFormLine );

        FreeFormLine conditionCol0ffl = (FreeFormLine) conditionCol0definition.get( 0 );
        assertEquals( "Driver(@{param1} != null, @{param1} == true)",
                      conditionCol0ffl.getText() );

        //Column 1 - Variable 1
        BRLConditionVariableColumn conditionCol0param0 = conditionCol0.getChildColumns().get( 0 );
        assertEquals( "param1",
                      conditionCol0param0.getVarName() );
        assertEquals( "Re-using single parameter",
                      conditionCol0param0.getHeader() );
        assertEquals( DataType.TYPE_OBJECT,
                      conditionCol0param0.getFieldType() );
        assertNull( conditionCol0param0.getFactType() );
        assertNull( conditionCol0param0.getFactField() );

        //Check data
        assertEquals( 2,
                      dtable.getData().size() );
        assertTrue( isRowEquivalent( new String[]{ "1", "Created from row 7", "isQualified" },
                                     dtable.getData().get( 0 ) ) );
        assertTrue( isRowEquivalent( new String[]{ "2", "Created from row 8", "isLicensed" },
                                     dtable.getData().get( 1 ) ) );
    }

    @Test
    public void testProperties() {

        final ConversionResult result = new ConversionResult();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        //Convert
        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream is = this.getClass().getResourceAsStream( "Properties.xls" );

        try {
            parser.parseFile( is );
        } finally {
            try {
                is.close();
            } catch ( IOException ioe ) {
                fail( ioe.getMessage() );
            }
        }

        //Check conversion results
        assertEquals( 0,
                      result.getMessages().size() );

        //Check properties
        List<String> functions = listener.getFunctions();
        assertNotNull( functions );
        assertEquals( 1,
                      functions.size() );
        assertEquals( "function a() { }",
                      functions.get( 0 ) );

        List<Global> globals = listener.getGlobals();
        assertNotNull( globals );
        assertEquals( 1,
                      globals.size() );
        assertEquals( "java.util.List",
                      globals.get( 0 ).getClassName() );
        assertEquals( "list",
                      globals.get( 0 ).getIdentifier() );

        List<Import> imports = listener.getImports();
        assertNotNull( imports );
        assertEquals( 2,
                      imports.size() );
        assertEquals( "org.yourco.model.*",
                      imports.get( 0 ).getClassName() );
        assertEquals( "java.util.Date",
                      imports.get( 1 ).getClassName() );

        List<String> queries = listener.getQueries();
        assertNotNull( queries );
        assertEquals( 1,
                      queries.size() );
        assertEquals( "A query",
                      queries.get( 0 ) );

        List<String> types = listener.getTypeDeclarations();
        assertNotNull( types );
        assertEquals( 1,
                      types.size() );
        assertEquals( "declare Smurf name : String end",
                      types.get( 0 ) );

    }

    private boolean isRowEquivalent( String[] expected,
                                     List<DTCellValue52> actual ) {
        //Sizes should match
        if ( expected.length != actual.size() ) {
            return false;
        }

        //Column values
        for ( int i = 0; i < expected.length; i++ ) {
            DTCellValue52 dcv = actual.get( i );
            switch ( dcv.getDataType() ) {
                case NUMERIC:
                    final BigDecimal numeric = (BigDecimal) dcv.getNumericValue();
                    if ( !expected[ i ].equals( numeric.toPlainString() ) ) {
                        return false;
                    }
                    break;
                case NUMERIC_BIGDECIMAL:
                    final BigDecimal numericBigDecimal = (BigDecimal) dcv.getNumericValue();
                    if ( !expected[ i ].equals( numericBigDecimal.toPlainString() ) ) {
                        return false;
                    }
                    break;
                case NUMERIC_BIGINTEGER:
                    final BigInteger numericBigInteger = (BigInteger) dcv.getNumericValue();
                    if ( !expected[ i ].equals( numericBigInteger.toString() ) ) {
                        return false;
                    }
                    break;
                case NUMERIC_BYTE:
                    final Byte numericByte = (Byte) dcv.getNumericValue();
                    if ( !expected[ i ].equals( numericByte.toString() ) ) {
                        return false;
                    }
                    break;
                case NUMERIC_DOUBLE:
                    final Double numericDouble = (Double) dcv.getNumericValue();
                    if ( !expected[ i ].equals( numericDouble.toString() ) ) {
                        return false;
                    }
                    break;
                case NUMERIC_FLOAT:
                    final Float numericFloat = (Float) dcv.getNumericValue();
                    if ( !expected[ i ].equals( numericFloat.toString() ) ) {
                        return false;
                    }
                    break;
                case NUMERIC_INTEGER:
                    final Integer numericInteger = (Integer) dcv.getNumericValue();
                    if ( !expected[ i ].equals( numericInteger.toString() ) ) {
                        return false;
                    }
                    break;
                case NUMERIC_LONG:
                    final Long numericLong = (Long) dcv.getNumericValue();
                    if ( !expected[ i ].equals( numericLong.toString() ) ) {
                        return false;
                    }
                    break;
                case NUMERIC_SHORT:
                    final Short numericShort = (Short) dcv.getNumericValue();
                    if ( !expected[ i ].equals( numericShort.toString() ) ) {
                        return false;
                    }
                    break;
                case BOOLEAN:
                    if ( Boolean.parseBoolean( expected[ i ] ) != dcv.getBooleanValue() ) {
                        return false;
                    }
                    break;
                default:
                    if ( !expected[ i ].equals( dcv.getStringValue() ) ) {
                        return false;
                    }
            }
        }
        return true;
    }

}
