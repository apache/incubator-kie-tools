package org.drools.workbench.screens.guided.rule.client.editor.validator;

import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.junit.Before;
import org.junit.Test;

import static org.jgroups.util.Util.*;
import static org.mockito.Mockito.*;

public class GuidedRuleEditorValidatorTest {

    private static final String MISSING_FACT_PATTERN = "Missing fact pattern";

    private RuleModel model;
    private GuidedRuleEditorValidator validator;

    @Before
    public void setUp() throws Exception {
        model = new RuleModel();
        Constants constants = mock(Constants.class);
        when(constants.AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving()).thenReturn(MISSING_FACT_PATTERN);
        validator = new GuidedRuleEditorValidator(model, constants);
    }

    @Test
    public void testEmpty() throws Exception {
        assertTrue(validator.isValid());
    }

    @Test
    public void testValidateFactPattern() throws Exception {

        model.addLhsItem(new FactPattern());

        assertTrue(validator.isValid());
        assertTrue(validator.getErrorMessage().equals(""));
    }

    @Test
    public void testValidateCompositeFactPatternFalse() throws Exception {
        CompositeFactPattern not = new CompositeFactPattern();
        not.setType("not");

        model.addLhsItem(not);

        assertFalse(validator.isValid());
        assertTrue(validator.getErrorMessage().equals(MISSING_FACT_PATTERN));
    }

    @Test
    public void testValidateCompositeFactPatternTrue() throws Exception {
        CompositeFactPattern not = new CompositeFactPattern();
        not.setType("not");
        not.addFactPattern(new FactPattern());

        model.addLhsItem(not);

        assertTrue(validator.isValid());
        assertTrue(validator.getErrorMessage().equals(""));
    }
}
