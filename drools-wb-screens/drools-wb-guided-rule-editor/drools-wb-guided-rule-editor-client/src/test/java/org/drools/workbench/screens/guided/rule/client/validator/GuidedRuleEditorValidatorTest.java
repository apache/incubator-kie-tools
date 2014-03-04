package org.drools.workbench.screens.guided.rule.client.validator;

import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
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

    private RuleModel model;
    private GuidedRuleEditorValidator validator;
    private Constants constants;

    @Before
    public void setUp() throws Exception {
        model = new RuleModel();
        constants = mock(Constants.class);

        when(
                constants.AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving()
        ).thenReturn(
                MISSING_FACT_PATTERN
        );
        when(
                constants.FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator(anyString(), anyString())
        ).thenReturn(
                MISSING_VALUE_WHEN_OPERATOR_IS_SET
        );
        when(
                constants.WhenUsingFromTheSourceNeedsToBeSet()
        ).thenReturn(
                MISSING_RHS_FROM
        );

        validator = new GuidedRuleEditorValidator(model, constants);
    }

    @Test
    public void testEmpty() throws Exception {
        assertTrue(validator.isValid());
        assertTrue(validator.getErrors().isEmpty());
    }

    @Test
    public void testValidateFactPattern() throws Exception {

        model.addLhsItem(new FactPattern());

        assertTrue(validator.isValid());
        assertTrue(validator.getErrors().isEmpty());
    }

    @Test
    public void testValidateCompositeFactPatternFalse() throws Exception {
        CompositeFactPattern not = new CompositeFactPattern();
        not.setType("not");

        model.addLhsItem(not);

        assertFalse(validator.isValid());
        assertTrue(validator.getErrors().get(0).equals(MISSING_FACT_PATTERN));
    }

    @Test
    public void testValidateCompositeFactPatternTrue() throws Exception {
        CompositeFactPattern not = new CompositeFactPattern();
        not.setType("not");
        not.addFactPattern(new FactPattern());

        model.addLhsItem(not);

        assertTrue(validator.isValid());
        assertTrue(validator.getErrors().isEmpty());
    }

    @Test
    public void testMissingValueWhenOperatorExists() throws Exception {

        FactPattern pattern = new FactPattern("Person");

        SingleFieldConstraint constraint = new SingleFieldConstraint("age");
        constraint.setOperator("==");
        pattern.addConstraint(constraint);
        model.lhs = new IPattern[]{pattern};

        assertFalse(validator.isValid());
        assertEquals(1, validator.getErrors().size());
        assertEquals(MISSING_VALUE_WHEN_OPERATOR_IS_SET, validator.getErrors().get(0));

        verify(constants).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator("Person", "age");

    }

    @Test
    public void testMissingValueWhenOperatorExistsInCompositePattern() throws Exception {

        FactPattern pattern = new FactPattern("Person");

        SingleFieldConstraint constraint = new SingleFieldConstraint("age");
        constraint.setOperator("==");
        pattern.addConstraint(constraint);

        CompositeFactPattern not = new CompositeFactPattern();
        not.setType("not");
        not.addFactPattern(pattern);

        model.lhs = new IPattern[]{not};

        assertFalse(validator.isValid());
        assertEquals(1, validator.getErrors().size());
        assertEquals(MISSING_VALUE_WHEN_OPERATOR_IS_SET, validator.getErrors().get(0));

        verify(constants).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator("Person", "age");
    }

    @Test
    public void testMissingValueWhenOperatorExistsIsNull() throws Exception {

        FactPattern pattern = new FactPattern("Person");

        SingleFieldConstraint constraint = new SingleFieldConstraint("age");
        constraint.setOperator("== null");
        pattern.addConstraint(constraint);

        CompositeFactPattern not = new CompositeFactPattern();
        not.setType("not");
        not.addFactPattern(pattern);

        model.lhs = new IPattern[]{not};

        assertTrue(validator.isValid());
        assertTrue(validator.getErrors().isEmpty());
    }

    @Test
    public void testMissingValueWhenOperatorExistsIsNotNull() throws Exception {

        FactPattern pattern = new FactPattern("Person");

        SingleFieldConstraint constraint = new SingleFieldConstraint("age");
        constraint.setOperator("!= null");
        pattern.addConstraint(constraint);

        CompositeFactPattern not = new CompositeFactPattern();
        not.setType("not");
        not.addFactPattern(pattern);

        model.lhs = new IPattern[]{not};

        assertTrue(validator.isValid());
        assertTrue(validator.getErrors().isEmpty());
    }

    @Test
    public void testMissingValueInFrom() throws Exception {
        FactPattern boundPattern = new FactPattern("Person");
        boundPattern.setBoundName("person");
        boundPattern.addConstraint(new SingleFieldConstraint("addresses"));

        FactPattern pattern = new FactPattern("Address");

        SingleFieldConstraint constraint = new SingleFieldConstraint("street");
        constraint.setOperator("!=");
        pattern.addConstraint(constraint);

        FromCompositeFactPattern fromCompositeFactPattern = new FromCompositeFactPattern();
        fromCompositeFactPattern.setFactPattern(pattern);
        ExpressionFormLine expression = new ExpressionFormLine();
        expression.setBinding("person.addresses");
        fromCompositeFactPattern.setExpression(expression);

        model.lhs = new IPattern[]{boundPattern, fromCompositeFactPattern};

        assertFalse(validator.isValid());
        assertEquals(1, validator.getErrors().size());
        assertEquals(MISSING_VALUE_WHEN_OPERATOR_IS_SET, validator.getErrors().get(0));

        verify(constants).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator("Address", "street");
    }

    @Test
    public void testWorkingFrom() throws Exception {

        FactPattern boundPattern = new FactPattern("Person");
        boundPattern.setBoundName("person");
        boundPattern.addConstraint(new SingleFieldConstraint("addresses"));

        FactPattern pattern = new FactPattern("Address");

        SingleFieldConstraint constraint = new SingleFieldConstraint("street");
        pattern.addConstraint(constraint);

        FromCompositeFactPattern fromCompositeFactPattern = new FromCompositeFactPattern();
        fromCompositeFactPattern.setFactPattern(pattern);
        ExpressionFormLine expression = new ExpressionFormLine();
        expression.setBinding("person.addresses");
        fromCompositeFactPattern.setExpression(expression);

        model.lhs = new IPattern[]{boundPattern, fromCompositeFactPattern};

        assertTrue(validator.isValid());
    }

    @Test
    public void testMissingRHSPartInFrom() throws Exception {

        FactPattern pattern = new FactPattern("Address");

        SingleFieldConstraint constraint = new SingleFieldConstraint("street");
        pattern.addConstraint(constraint);

        FromCompositeFactPattern fromCompositeFactPattern = new FromCompositeFactPattern();
        fromCompositeFactPattern.setFactPattern(pattern);
        ExpressionFormLine expression = new ExpressionFormLine();
        fromCompositeFactPattern.setExpression(expression);

        model.lhs = new IPattern[]{fromCompositeFactPattern};

        assertFalse(validator.isValid());
        assertEquals(1, validator.getErrors().size());
        assertEquals(MISSING_RHS_FROM, validator.getErrors().get(0));

        verify(constants).WhenUsingFromTheSourceNeedsToBeSet();

    }

    @Test
    public void testFromAccumulateCompositePattern() throws Exception {
        FactPattern pattern1 = new FactPattern("Person");
        SingleFieldConstraint constraint1 = new SingleFieldConstraint("name");
        constraint1.setOperator("==");
        constraint1.setValue("Toni");
        pattern1.addConstraint(constraint1);

        FactPattern pattern2 = new FactPattern("Address");
        SingleFieldConstraint constraint2 = new SingleFieldConstraint("street");
        constraint2.setOperator("!=");
        constraint2.setValue("some street");
        pattern2.addConstraint(constraint2);

        FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern = new FromAccumulateCompositeFactPattern();
        fromAccumulateCompositeFactPattern.setSourcePattern(pattern1);
        fromAccumulateCompositeFactPattern.setFactPattern(pattern2);
        ExpressionFormLine expression = new ExpressionFormLine();
        expression.setBinding("person.addresses");
        fromAccumulateCompositeFactPattern.setExpression(expression);


        model.lhs = new IPattern[]{fromAccumulateCompositeFactPattern};

        assertTrue(validator.isValid());
    }

    @Test
    public void testFromAccumulateCompositePatternMissingValues() throws Exception {
        FactPattern pattern1 = new FactPattern("Person");
        SingleFieldConstraint constraint1 = new SingleFieldConstraint("name");
        constraint1.setOperator("==");
        pattern1.addConstraint(constraint1);

        FactPattern pattern2 = new FactPattern("Address");
        SingleFieldConstraint constraint2 = new SingleFieldConstraint("street");
        constraint2.setOperator("!=");
        pattern2.addConstraint(constraint2);

        FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern = new FromAccumulateCompositeFactPattern();
        fromAccumulateCompositeFactPattern.setSourcePattern(pattern1);
        fromAccumulateCompositeFactPattern.setFactPattern(pattern2);
        fromAccumulateCompositeFactPattern.setFunction("test()");

        model.lhs = new IPattern[]{fromAccumulateCompositeFactPattern};

        assertFalse(validator.isValid());
        assertEquals(3, validator.getErrors().size());
        
        verify(constants).WhenUsingFromTheSourceNeedsToBeSet();
        verify(constants).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator("Person", "name");
        verify(constants).FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator("Address", "street");
    }
}
