package org.drools.workbench.screens.guided.rule.client.editor.validator;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromEntryPointFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;

public class GuidedRuleEditorValidator {

    private final List<String> errors = new ArrayList<String>();

    private final RuleModel model;
    private final Constants constants;

    public GuidedRuleEditorValidator(RuleModel model, Constants constants) {
        this.model = model;
        this.constants = constants;
    }

    public boolean isValid() {

        if (model.lhs.length == 0) {
            return true;
        } else {
            validateIPatterns(model.lhs);
        }

        return errors.isEmpty();

    }

    private void validateIPatterns(IPattern[] patterns) {
        if (patterns == null) {
            return;
        }

        for (IPattern iPattern : patterns) {
            validateIPattern(iPattern);
        }
    }

    private void validateIPattern(IPattern iPattern) {
        if (iPattern instanceof FromEntryPointFactPattern) {
            validateFromEntryPointFactPattern((FromEntryPointFactPattern) iPattern);
        } else if (iPattern instanceof FromAccumulateCompositeFactPattern) {
            validateFromAccumulateCompositeFactPattern((FromAccumulateCompositeFactPattern) iPattern);
        } else if (iPattern instanceof FromCompositeFactPattern) {
            validateFromCompositeFactPattern((FromCompositeFactPattern) iPattern);
        } else if (iPattern instanceof CompositeFactPattern) {
            validateCompositeFactPattern((CompositeFactPattern) iPattern);
        } else if (iPattern instanceof FactPattern) {
            validateFactPattern((FactPattern) iPattern);
        }
    }

    private void validateFromEntryPointFactPattern(FromEntryPointFactPattern fromEntryPointFactPattern) {
        hasFactPatternSet(fromEntryPointFactPattern);
        hasEntryPoint(fromEntryPointFactPattern);
    }

    private void hasEntryPoint(FromEntryPointFactPattern fromEntryPointFactPattern) {
        if (isStringNullOrEmpty(fromEntryPointFactPattern.getEntryPointName())) {
            errors.add(constants.PleaseSetTheEntryPoint());
        }
    }

    private void validateCompositeFactPattern(CompositeFactPattern compositeFactPattern) {
        hasPatterns(compositeFactPattern);

        validateIPatterns(compositeFactPattern.getPatterns());
    }

    private void validateFromCompositeFactPattern(FromCompositeFactPattern fromCompositeFactPattern) {
        hasFactPatternSet(fromCompositeFactPattern);
        hasExpressionBinding(fromCompositeFactPattern);
        validateFactPattern(fromCompositeFactPattern.getFactPattern());
    }

    private void hasFactPatternSet(FromCompositeFactPattern fromCompositeFactPattern) {
        if (fromCompositeFactPattern.getFactPattern() == null) {
            reportMandatoryFieldsError();
        }
    }

    private void validateFromAccumulateCompositeFactPattern(FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern) {
        validateIPattern(fromAccumulateCompositeFactPattern.getSourcePattern());
        validateIPattern(fromAccumulateCompositeFactPattern.getFactPattern());

        if (isExpressionBindingMissing(fromAccumulateCompositeFactPattern)
                && isStringNullOrEmpty(fromAccumulateCompositeFactPattern.getFunction())
                && isAccumulateMissing(fromAccumulateCompositeFactPattern)) {
            reportMissingSource();
        }
    }

    private boolean isAccumulateMissing(FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern) {
        return isStringNullOrEmpty(fromAccumulateCompositeFactPattern.getActionCode())
                && isStringNullOrEmpty(fromAccumulateCompositeFactPattern.getResultCode())
                && isStringNullOrEmpty(fromAccumulateCompositeFactPattern.getResultCode())
                && isStringNullOrEmpty(fromAccumulateCompositeFactPattern.getInitCode());
    }

    private boolean isStringNullOrEmpty(String actionCode) {
        return actionCode == null || actionCode.isEmpty();
    }

    private void hasExpressionBinding(FromCompositeFactPattern fromCompositeFactPattern) {
        if (isExpressionBindingMissing(fromCompositeFactPattern)) {
            reportMissingSource();
        }
    }

    private void reportMissingSource() {
        errors.add(
                constants.WhenUsingFromTheSourceNeedsToBeSet());
    }

    private boolean isExpressionBindingMissing(FromCompositeFactPattern fromCompositeFactPattern) {
        return fromCompositeFactPattern.getExpression().getBinding() == null
                && fromCompositeFactPattern.getExpression().getParts().isEmpty();
    }

    private void hasPatterns(CompositeFactPattern iPattern) {

        if (iPattern.getPatterns() == null) {
            reportMandatoryFieldsError();
        } else if (iPattern.getPatterns().length == 0) {
            reportMandatoryFieldsError();
        }
    }

    private void validateFactPattern(FactPattern factPattern) {
        if (factPattern == null || factPattern.getConstraintList() == null || factPattern.getConstraintList().getConstraints() == null) {
            return;
        }

        for (FieldConstraint constraint : factPattern.getConstraintList().getConstraints()) {
            if (constraint instanceof SingleFieldConstraint) {
                SingleFieldConstraint singleFieldConstraint = (SingleFieldConstraint) constraint;
                if (areOperatorAndValueInValid(singleFieldConstraint)) {
                    errors.add(
                            constants.FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator(
                                    factPattern.getFactType(),
                                    singleFieldConstraint.getFieldName()));
                }
            }
        }
    }

    private boolean areOperatorAndValueInValid(SingleFieldConstraint singleFieldConstraint) {
        return singleFieldConstraint.getOperator() != null
                && !singleFieldConstraint.getOperator().equals("== null")
                && !singleFieldConstraint.getOperator().equals("!= null")
                && singleFieldConstraint.getValue() == null;
    }

    private void reportMandatoryFieldsError() {
        errors.add(constants.AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving());
    }

    public List<String> getErrors() {
        return errors;
    }
}
