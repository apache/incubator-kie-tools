package org.drools.workbench.screens.guided.rule.client.editor.validator;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;

public class GuidedRuleEditorValidator {

    private final List<String> errors = new ArrayList<String>();
    private boolean isValid = true;

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

        return isValid;

    }

    private void validateIPatterns(IPattern[] patterns) {
        if (patterns == null) {
            return;
        }

        for (IPattern iPattern : patterns) {
            if (iPattern instanceof FromAccumulateCompositeFactPattern) {
                validateFromAccumulateCompositeFactPattern((FromAccumulateCompositeFactPattern) iPattern);
            } else if (iPattern instanceof FromCompositeFactPattern) {
                validateFromCompositeFactPattern((FromCompositeFactPattern) iPattern);
            } else if (iPattern instanceof CompositeFactPattern) {
                validateCompositeFactPattern((CompositeFactPattern) iPattern);
            } else if (iPattern instanceof FactPattern) {
                validateFactPattern((FactPattern) iPattern);
            }
        }
    }

    private void validateCompositeFactPattern(CompositeFactPattern compositeFactPattern) {
        hasPatterns(compositeFactPattern);

        validateIPatterns(compositeFactPattern.getPatterns());
    }

    private void validateFromCompositeFactPattern(FromCompositeFactPattern fromCompositeFactPattern) {
        hasExpressionBinding(fromCompositeFactPattern);

        validateFactPattern(fromCompositeFactPattern.getFactPattern());
    }

    private void validateFromAccumulateCompositeFactPattern(FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern) {
        IPattern sourcePattern = fromAccumulateCompositeFactPattern.getSourcePattern();
        if (sourcePattern instanceof FactPattern) {
            validateFactPattern((FactPattern) sourcePattern);
        }
    }

    private void hasExpressionBinding(FromCompositeFactPattern fromCompositeFactPattern) {
        if (fromCompositeFactPattern.getExpression().getBinding() == null) {
            errors.add(
                    constants.WhenUsingFromTheSourceNeedsToBeSet());
            isValid = false;
        }
    }

    private void hasPatterns(CompositeFactPattern iPattern) {

        if (iPattern.getPatterns() == null) {
            setMandatoryFieldsError();
        } else if (iPattern.getPatterns().length == 0) {
            setMandatoryFieldsError();
        }
    }

    private void validateFactPattern(FactPattern factPattern) {
        if (factPattern.getConstraintList() == null || factPattern.getConstraintList().getConstraints() == null) {
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
                    isValid = false;
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

    private void setMandatoryFieldsError() {
        errors.add(constants.AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving());
        isValid = false;
    }

    public List<String> getErrors() {
        return errors;
    }
}
