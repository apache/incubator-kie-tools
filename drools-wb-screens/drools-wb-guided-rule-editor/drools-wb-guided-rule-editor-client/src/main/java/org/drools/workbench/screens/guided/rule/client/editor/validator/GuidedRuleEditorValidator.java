package org.drools.workbench.screens.guided.rule.client.editor.validator;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromEntryPointFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;

public class GuidedRuleEditorValidator {

    private final List<String> errors = new ArrayList<String>();

    private final RuleModel model;
    private final Constants constants;

    public GuidedRuleEditorValidator(final RuleModel model,
            final Constants constants) {
        this.model = PortablePreconditions.checkNotNull("model",
                model);
        this.constants = PortablePreconditions.checkNotNull("constants",
                constants);
    }

    public boolean isValid() {
        if (model.lhs.length == 0) {
            return true;
        } else {
            validateIPatterns(model.lhs);
        }
        return errors.isEmpty();
    }

    private void validateIPatterns(final IPattern[] patterns) {
        if (patterns == null) {
            return;
        }

        for (IPattern iPattern : patterns) {
            validateIPattern(iPattern);
        }
    }

    private void validateIPattern(final IPattern iPattern) {
        if (iPattern instanceof FromEntryPointFactPattern) {
            validateFromEntryPointFactPattern((FromEntryPointFactPattern) iPattern);
        } else if (iPattern instanceof FromAccumulateCompositeFactPattern) {
            validateFromAccumulateCompositeFactPattern((FromAccumulateCompositeFactPattern) iPattern);
        } else if (iPattern instanceof FromCollectCompositeFactPattern) {
            validateFromCollectCompositeFactPattern((FromCollectCompositeFactPattern) iPattern);
        } else if (iPattern instanceof FromCompositeFactPattern) {
            validateFromCompositeFactPattern((FromCompositeFactPattern) iPattern);
        } else if (iPattern instanceof CompositeFactPattern) {
            validateCompositeFactPattern((CompositeFactPattern) iPattern);
        } else if (iPattern instanceof FactPattern) {
            validateFactPattern((FactPattern) iPattern);
        }
    }

    private void validateFromEntryPointFactPattern(final FromEntryPointFactPattern fromEntryPointFactPattern) {
        hasFactPatternSet(fromEntryPointFactPattern);
        hasEntryPoint(fromEntryPointFactPattern);
    }

    private void hasEntryPoint(final FromEntryPointFactPattern fromEntryPointFactPattern) {
        if (isStringNullOrEmpty(fromEntryPointFactPattern.getEntryPointName())) {
            errors.add(constants.PleaseSetTheEntryPoint());
        }
    }

    private void validateCompositeFactPattern(final CompositeFactPattern compositeFactPattern) {
        hasPatterns(compositeFactPattern);
        validateIPatterns(compositeFactPattern.getPatterns());
    }

    private void validateFromCompositeFactPattern(final FromCompositeFactPattern fromCompositeFactPattern) {
        hasFactPatternSet(fromCompositeFactPattern);
        hasExpressionBinding(fromCompositeFactPattern);
        validateFactPattern(fromCompositeFactPattern.getFactPattern());
    }

    private void hasFactPatternSet(final FromCompositeFactPattern fromCompositeFactPattern) {
        if (fromCompositeFactPattern.getFactPattern() == null) {
            reportMandatoryFieldsError();
        }
    }

    private void validateFromAccumulateCompositeFactPattern(final FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern) {
        validateIPattern(fromAccumulateCompositeFactPattern.getSourcePattern());
        validateIPattern(fromAccumulateCompositeFactPattern.getFactPattern());

        if (isExpressionBindingMissing(fromAccumulateCompositeFactPattern)
                && isStringNullOrEmpty(fromAccumulateCompositeFactPattern.getFunction())
                && isAccumulateMissing(fromAccumulateCompositeFactPattern)) {
            reportMissingSource();
        }
    }

    private void validateFromCollectCompositeFactPattern(final FromCollectCompositeFactPattern fromCollectCompositeFactPattern) {

        if (fromCollectCompositeFactPattern.getRightPattern() == null) {
            reportMandatoryFieldsError();
        } else if (fromCollectCompositeFactPattern.getFactPattern() == null){
            reportMandatoryFieldsError();
        } else {
            validateIPattern(fromCollectCompositeFactPattern.getRightPattern());
        }

        validateIPattern(fromCollectCompositeFactPattern.getFactPattern());

    }

    private boolean isAccumulateMissing(final FromAccumulateCompositeFactPattern fromAccumulateCompositeFactPattern) {
        return isStringNullOrEmpty(fromAccumulateCompositeFactPattern.getActionCode())
                && isStringNullOrEmpty(fromAccumulateCompositeFactPattern.getResultCode())
                && isStringNullOrEmpty(fromAccumulateCompositeFactPattern.getResultCode())
                && isStringNullOrEmpty(fromAccumulateCompositeFactPattern.getInitCode());
    }

    private boolean isStringNullOrEmpty(final String actionCode) {
        return actionCode == null || actionCode.isEmpty();
    }

    private void hasExpressionBinding(final FromCompositeFactPattern fromCompositeFactPattern) {
        if (isExpressionBindingMissing(fromCompositeFactPattern)) {
            reportMissingSource();
        }
    }

    private void reportMissingSource() {
        errors.add(constants.WhenUsingFromTheSourceNeedsToBeSet());
    }

    private boolean isExpressionBindingMissing(final FromCompositeFactPattern fromCompositeFactPattern) {
        return fromCompositeFactPattern.getExpression().getBinding() == null
                && fromCompositeFactPattern.getExpression().getParts().isEmpty();
    }

    private void hasPatterns(final CompositeFactPattern iPattern) {
        if (iPattern.getPatterns() == null) {
            reportMandatoryFieldsError();
        } else if (iPattern.getPatterns().length == 0) {
            reportMandatoryFieldsError();
        }
    }

    private void validateFactPattern(final FactPattern factPattern) {
        if (factPattern == null || factPattern.getConstraintList() == null || factPattern.getConstraintList().getConstraints() == null) {
            return;
        }

        for (FieldConstraint constraint : factPattern.getConstraintList().getConstraints()) {
            if (constraint instanceof SingleFieldConstraint) {
                SingleFieldConstraint singleFieldConstraint = (SingleFieldConstraint) constraint;
                if (areOperatorAndValueInvalid(singleFieldConstraint)) {
                    errors.add(constants.FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator(
                            factPattern.getFactType(),
                            singleFieldConstraint.getFieldName()));
                }
            }
        }
    }

    private boolean areOperatorAndValueInvalid(final SingleFieldConstraint singleFieldConstraint) {
        if (singleFieldConstraint.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL) {
            if (DataType.TYPE_STRING.equals(singleFieldConstraint.getFieldType())) {
                return false;
            }
        }
        return singleFieldConstraint.getOperator() != null
                && !singleFieldConstraint.getOperator().equals("== null")
                && !singleFieldConstraint.getOperator().equals("!= null")
                && singleFieldConstraint.getValue() == null
                && singleFieldConstraint.getExpressionValue().isEmpty();
    }

    private void reportMandatoryFieldsError() {
        errors.add(constants.AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving());
    }

    public List<String> getErrors() {
        return errors;
    }
}
