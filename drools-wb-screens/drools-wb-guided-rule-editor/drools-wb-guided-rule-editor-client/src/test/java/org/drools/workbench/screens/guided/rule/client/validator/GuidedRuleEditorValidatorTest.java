package org.drools.workbench.screens.guided.rule.client.validator;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionVariable;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromEntryPointFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.editor.validator.GuidedRuleEditorValidator;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GuidedRuleEditorValidatorTest {

    private static final String MISSING_FACT_PATTERN = "Missing fact pattern";
    private static final String MISSING_VALUE_WHEN_OPERATOR_IS_SET = "Missing value when operator is set";
    private static final String MISSING_RHS_FROM = "Missing RHS from";
    private static final String MISSING_ENTRY_POINT = "Missing Entry Point";

    private RuleModel model;
    private GuidedRuleEditorValidator validator;
    private Constants constants;

    @Before
    public void setUp() throws Exception {
        model = new RuleModel();
        constants = mock( Constants.class );

        when(
                constants.AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving()
            ).thenReturn(
                MISSING_FACT_PATTERN
                        );
        when(
                constants.FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator( anyString(), anyString() )
            ).thenReturn(
                MISSING_VALUE_WHEN_OPERATOR_IS_SET
                        );
        when(
                constants.WhenUsingFromTheSourceNeedsToBeSet()
            ).thenReturn(
                MISSING_RHS_FROM
                        );
        when(
                constants.PleaseSetTheEntryPoint()
            ).thenReturn(
                MISSING_ENTRY_POINT
                        );

        validator = new GuidedRuleEditorValidator( model,
                                                   constants );
    }

    @Test
    public void testEmpty() throws Exception {
        assertTrue( validator.isValid() );
        assertTrue( validator.getErrors().isEmpty() );
    }

    @Test
    public void testValidateFactPattern() throws Exception {

        model.addLhsItem( new FactPattern() );

        assertTrue( validator.isValid() );
        assertTrue( validator.getErrors().isEmpty() );
    }

    @Test
    public void testValidateCompositeFactPatternFalse() throws Exception {
        CompositeFactPattern not = new CompositeFactPattern();
        not.setType( "not" );

        model.addLhsItem( not );

        assertFalse( validator.isValid() );
        assertTrue( validator.getErrors().get( 0 ).equals( MISSING_FACT_PATTERN ) );
    }

    @Test
    public void testValidateCompositeFactPatternTrue() throws Exception {
        CompositeFactPattern not = new CompositeFactPattern();
        not.setType( "not" );
        not.addFactPattern( new FactPattern() );

        model.addLhsItem( not );

        assertTrue( validator.isValid() );
        assertTrue( validator.getErrors().isEmpty() );
    }

    @Test
    public void testMissingValueWhenOperatorExists() throws Exception {

        FactPattern pattern = new FactPattern( "Person" );

        SingleFieldConstraint constraint = new SingleFieldConstraint( "age" );
        constraint.setOperator( "==" );
        pattern.addConstraint( constraint );
        model.lhs = new IPattern[]{ pattern };

        assertFalse( validator.isValid() );
        assertEquals( 1, validator.getErrors().size() );
        assertEquals( MISSING_VALUE_WHEN_OPERATOR_IS_SET, validator.getErrors().get( 0 ) );

        verify( constants ).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator( "Person",
                                                                                                                 "age" );

    }

    @Test
    public void testMissingValueWhenOperatorExistsInCompositePattern() throws Exception {

        FactPattern pattern = new FactPattern( "Person" );

        SingleFieldConstraint constraint = new SingleFieldConstraint( "age" );
        constraint.setOperator( "==" );
        pattern.addConstraint( constraint );

        CompositeFactPattern not = new CompositeFactPattern();
        not.setType( "not" );
        not.addFactPattern( pattern );

        model.lhs = new IPattern[]{ not };

        assertFalse( validator.isValid() );
        assertEquals( 1, validator.getErrors().size() );
        assertEquals( MISSING_VALUE_WHEN_OPERATOR_IS_SET, validator.getErrors().get( 0 ) );

        verify( constants ).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator( "Person",
                                                                                                                 "age" );
    }

    @Test
    public void testMissingValueWhenOperatorExistsIsNull() throws Exception {

        FactPattern pattern = new FactPattern( "Person" );

        SingleFieldConstraint constraint = new SingleFieldConstraint( "age" );
        constraint.setOperator( "== null" );
        pattern.addConstraint( constraint );

        CompositeFactPattern not = new CompositeFactPattern();
        not.setType( "not" );
        not.addFactPattern( pattern );

        model.lhs = new IPattern[]{ not };

        assertTrue( validator.isValid() );
        assertTrue( validator.getErrors().isEmpty() );
    }

    @Test
    public void testMissingValueWhenOperatorExistsIsNotNull() throws Exception {

        FactPattern pattern = new FactPattern( "Person" );

        SingleFieldConstraint constraint = new SingleFieldConstraint( "age" );
        constraint.setOperator( "!= null" );
        pattern.addConstraint( constraint );

        CompositeFactPattern not = new CompositeFactPattern();
        not.setType( "not" );
        not.addFactPattern( pattern );

        model.lhs = new IPattern[]{ not };

        assertTrue( validator.isValid() );
        assertTrue( validator.getErrors().isEmpty() );
    }

    @Test
    public void testMissingValueInFrom() throws Exception {
        FactPattern boundPattern = new FactPattern( "Person" );
        boundPattern.setBoundName( "person" );
        boundPattern.addConstraint( new SingleFieldConstraint( "addresses" ) );

        FactPattern pattern = new FactPattern( "Address" );

        SingleFieldConstraint constraint = new SingleFieldConstraint( "street" );
        constraint.setOperator( "!=" );
        pattern.addConstraint( constraint );

        FromCompositeFactPattern fromCompositeFactPattern = new FromCompositeFactPattern();
        fromCompositeFactPattern.setFactPattern( pattern );
        ExpressionFormLine expression = new ExpressionFormLine();
        expression.setBinding( "person.addresses" );
        fromCompositeFactPattern.setExpression( expression );

        model.lhs = new IPattern[]{ boundPattern, fromCompositeFactPattern };

        assertFalse( validator.isValid() );
        assertEquals( 1, validator.getErrors().size() );
        assertEquals( MISSING_VALUE_WHEN_OPERATOR_IS_SET, validator.getErrors().get( 0 ) );

        verify( constants ).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator( "Address", "street" );
    }

    @Test
    public void testWorkingFrom() throws Exception {

        FactPattern boundPattern = new FactPattern( "Person" );
        boundPattern.setBoundName( "person" );
        boundPattern.addConstraint( new SingleFieldConstraint( "addresses" ) );

        FactPattern pattern = new FactPattern( "Address" );

        SingleFieldConstraint constraint = new SingleFieldConstraint( "street" );
        pattern.addConstraint( constraint );

        FromCompositeFactPattern fromCompositeFactPattern = new FromCompositeFactPattern();
        fromCompositeFactPattern.setFactPattern( pattern );
        ExpressionFormLine expression = new ExpressionFormLine();
        expression.setBinding( "person.addresses" );
        fromCompositeFactPattern.setExpression( expression );

        model.lhs = new IPattern[]{ boundPattern, fromCompositeFactPattern };

        assertTrue( validator.isValid() );
    }

    @Test
    public void testEmptyFrom() throws Exception {

        model.lhs = new IPattern[]{ new FromCompositeFactPattern() };

        assertFalse( validator.isValid() );

        assertEquals( 2, validator.getErrors().size() );
        assertEquals( MISSING_FACT_PATTERN, validator.getErrors().get( 0 ) );
        assertEquals( MISSING_RHS_FROM, validator.getErrors().get( 1 ) );

        verify( constants ).AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving();
        verify( constants ).WhenUsingFromTheSourceNeedsToBeSet();
    }

    @Test
    public void testValidFromCompositeFactPattern() throws Exception {

        FactPattern factPattern = new FactPattern( "SomeList" );
        factPattern.setBoundName( "list" );

        FromCompositeFactPattern fromCompositeFactPattern = new FromCompositeFactPattern();
        fromCompositeFactPattern.setFactPattern( new FactPattern( "Person" ) );
        ExpressionFormLine expression = new ExpressionFormLine();
        expression.appendPart( new ExpressionVariable( "list", "SomeList", "SomeList" ) );
        fromCompositeFactPattern.setExpression( expression );
        model.lhs = new IPattern[]{ fromCompositeFactPattern };

        assertTrue( validator.isValid() );
    }

    @Test
    public void testMissingRHSPartInFrom() throws Exception {

        FactPattern pattern = new FactPattern( "Address" );

        SingleFieldConstraint constraint = new SingleFieldConstraint( "street" );
        pattern.addConstraint( constraint );

        FromCompositeFactPattern fromCompositeFactPattern = new FromCompositeFactPattern();
        fromCompositeFactPattern.setFactPattern( pattern );
        ExpressionFormLine expression = new ExpressionFormLine();
        fromCompositeFactPattern.setExpression( expression );

        model.lhs = new IPattern[]{ fromCompositeFactPattern };

        assertFalse( validator.isValid() );
        assertEquals( 1, validator.getErrors().size() );
        assertEquals( MISSING_RHS_FROM, validator.getErrors().get( 0 ) );

        verify( constants ).WhenUsingFromTheSourceNeedsToBeSet();

    }

    @Test
    public void testFromAccumulateCompositePattern() throws Exception {
        FactPattern pattern1 = new FactPattern( "Person" );
        SingleFieldConstraint constraint1 = new SingleFieldConstraint( "name" );
        constraint1.setOperator( "==" );
        constraint1.setValue( "Toni" );
        pattern1.addConstraint( constraint1 );

        FactPattern pattern2 = new FactPattern( "Address" );
        SingleFieldConstraint constraint2 = new SingleFieldConstraint( "street" );
        constraint2.setOperator( "!=" );
        constraint2.setValue( "some street" );
        pattern2.addConstraint( constraint2 );

        FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern = new FromAccumulateCompositeFactPattern();
        fromAccumulateCompositeFactPattern.setSourcePattern( pattern1 );
        fromAccumulateCompositeFactPattern.setFactPattern( pattern2 );
        ExpressionFormLine expression = new ExpressionFormLine();
        expression.setBinding( "person.addresses" );
        fromAccumulateCompositeFactPattern.setExpression( expression );

        model.lhs = new IPattern[]{ fromAccumulateCompositeFactPattern };

        assertTrue( validator.isValid() );
    }

    @Test
    public void testFromAccumulateCompositePatternMissingValues() throws Exception {
        FactPattern pattern1 = new FactPattern( "Person" );
        SingleFieldConstraint constraint1 = new SingleFieldConstraint( "name" );
        constraint1.setOperator( "==" );
        pattern1.addConstraint( constraint1 );

        FactPattern pattern2 = new FactPattern( "Address" );
        SingleFieldConstraint constraint2 = new SingleFieldConstraint( "street" );
        constraint2.setOperator( "!=" );
        pattern2.addConstraint( constraint2 );

        FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern = new FromAccumulateCompositeFactPattern();
        fromAccumulateCompositeFactPattern.setSourcePattern( pattern1 );
        fromAccumulateCompositeFactPattern.setFactPattern( pattern2 );
        fromAccumulateCompositeFactPattern.setFunction( "test()" );

        model.lhs = new IPattern[]{ fromAccumulateCompositeFactPattern };

        assertFalse( validator.isValid() );
        assertEquals( 2, validator.getErrors().size() );

        verify( constants, never() ).WhenUsingFromTheSourceNeedsToBeSet();
        verify( constants ).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator( "Person", "name" );
        verify( constants ).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator( "Address", "street" );
    }

    @Test
    public void testFromAccumulateCompositePatternMissingValues2() throws Exception {
        FactPattern pattern1 = new FactPattern( "Person" );

        FactPattern pattern2 = new FactPattern( "Address" );

        FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern = new FromAccumulateCompositeFactPattern();
        fromAccumulateCompositeFactPattern.setSourcePattern( pattern1 );
        fromAccumulateCompositeFactPattern.setFactPattern( pattern2 );
        fromAccumulateCompositeFactPattern.setFunction( "" );
        fromAccumulateCompositeFactPattern.setReverseCode( "" );
        fromAccumulateCompositeFactPattern.setInitCode( "" );
        fromAccumulateCompositeFactPattern.setActionCode( "" );
        fromAccumulateCompositeFactPattern.setResultCode( "" );

        model.lhs = new IPattern[]{ fromAccumulateCompositeFactPattern };

        assertFalse( validator.isValid() );
        assertEquals( 1, validator.getErrors().size() );

        verify( constants ).WhenUsingFromTheSourceNeedsToBeSet();
    }

    @Test
    public void testFromAccumulateCompositePatternMissingValuesWithExistingFrom() throws Exception {
        FactPattern pattern1 = new FactPattern( "Person" );
        SingleFieldConstraint constraint1 = new SingleFieldConstraint( "name" );
        constraint1.setOperator( "==" );
        pattern1.addConstraint( constraint1 );

        FactPattern pattern2 = new FactPattern( "Address" );
        SingleFieldConstraint constraint2 = new SingleFieldConstraint( "street" );
        constraint2.setOperator( "!=" );
        pattern2.addConstraint( constraint2 );

        FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern = new FromAccumulateCompositeFactPattern();
        fromAccumulateCompositeFactPattern.setSourcePattern( pattern1 );
        fromAccumulateCompositeFactPattern.setFactPattern( pattern2 );
        fromAccumulateCompositeFactPattern.setInitCode( "int i = 0" );
        fromAccumulateCompositeFactPattern.setActionCode( " i++;" );
        fromAccumulateCompositeFactPattern.setReverseCode( "i--;" );
        fromAccumulateCompositeFactPattern.setResultCode( "return i" );

        model.lhs = new IPattern[]{ fromAccumulateCompositeFactPattern };

        assertFalse( validator.isValid() );
        assertEquals( 2, validator.getErrors().size() );

        verify( constants, never() ).WhenUsingFromTheSourceNeedsToBeSet();
        verify( constants ).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator( "Person", "name" );
        verify( constants ).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator( "Address", "street" );
    }

    @Test
    public void testEmptyFromEntryPointFactPattern() throws Exception {
        model.lhs = new IPattern[]{ new FromEntryPointFactPattern() };

        assertFalse( validator.isValid() );
        assertEquals( 2, validator.getErrors().size() );
        assertEquals( MISSING_FACT_PATTERN, validator.getErrors().get( 0 ) );
        assertEquals( MISSING_ENTRY_POINT, validator.getErrors().get( 1 ) );

        verify( constants ).AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving();
        verify( constants ).PleaseSetTheEntryPoint();
    }

    @Test
    public void testValidFromEntryPointFactPattern() throws Exception {
        FromEntryPointFactPattern fromEntryPointFactPattern = new FromEntryPointFactPattern();
        fromEntryPointFactPattern.setFactPattern( new FactPattern( "Person" ) );
        fromEntryPointFactPattern.setEntryPointName( "entryPoint" );
        model.lhs = new IPattern[]{ fromEntryPointFactPattern };

        assertTrue( validator.isValid() );
    }

    @Test
    public void testComparingFieldToExpression() throws Exception {
        FactPattern f1 = new FactPattern();
        f1.setBoundName( "f1" );

        FactPattern f2 = new FactPattern();
        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setOperator( "==" );
        constraint.getExpressionValue().appendPart( new ExpressionVariable( "field",
                                                                            "java.lang.Number",
                                                                            "Number" ) );
        f2.addConstraint( constraint );

        model.lhs = new IPattern[]{ f1, f2 };

        assertTrue( validator.isValid() );
    }

    @Test
    public void testEmptyLiteralStringFieldConstraints() throws Exception {
        FactPattern f1 = new FactPattern( "Applicant" );
        f1.setBoundName( "$a" );

        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        constraint.setFieldType( DataType.TYPE_STRING );
        constraint.setFactType( "Applicant" );
        constraint.setOperator( "==" );
        constraint.setFieldName( "name" );
        f1.addConstraint( constraint );

        model.lhs = new IPattern[]{ f1 };

        assertTrue( validator.isValid() );
    }

    @Test
    public void testEmptyLiteralNonStringFieldConstraints() throws Exception {
        FactPattern f1 = new FactPattern( "Applicant" );
        f1.setBoundName( "$a" );

        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        constraint.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        constraint.setFactType( "Applicant" );
        constraint.setOperator( "==" );
        constraint.setFieldName( "age" );
        f1.addConstraint( constraint );

        model.lhs = new IPattern[]{ f1 };

        assertFalse( validator.isValid() );
    }

    @Test
    public void testMissingValueInFromCollect() throws Exception {
        FactPattern pattern = new FactPattern( "Person" );
        pattern.setBoundName("person");

        FromCompositeFactPattern fromCompositeFactPattern = new FromCollectCompositeFactPattern();
        fromCompositeFactPattern.setFactPattern( pattern );
        model.lhs = new IPattern[]{ fromCompositeFactPattern };

        assertFalse( validator.isValid() );
        assertEquals( 1, validator.getErrors().size() );
        assertEquals( MISSING_FACT_PATTERN, validator.getErrors().get( 0 ) );

        verify( constants ).AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving();
    }

    @Test
    public void testMissingFactTypeInFromCollect() throws Exception {

        FromCollectCompositeFactPattern fromCompositeFactPattern = new FromCollectCompositeFactPattern();
        fromCompositeFactPattern.setRightPattern(new FactPattern("Person"));
        model.lhs = new IPattern[]{ fromCompositeFactPattern };

        assertFalse( validator.isValid() );
        assertEquals( 1, validator.getErrors().size() );
        assertEquals( MISSING_FACT_PATTERN, validator.getErrors().get( 0 ) );

        verify( constants ).AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving();
    }
}
