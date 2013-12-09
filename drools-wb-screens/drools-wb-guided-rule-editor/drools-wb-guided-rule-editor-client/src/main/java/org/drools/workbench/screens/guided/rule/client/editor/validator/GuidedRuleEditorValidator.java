package org.drools.workbench.screens.guided.rule.client.editor.validator;

import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;

public class GuidedRuleEditorValidator {

    private final RuleModel model;
    private final Constants constants;
    private String errorMessage = "";

    public GuidedRuleEditorValidator(RuleModel model, Constants constants) {
        this.model = model;
        this.constants = constants;
    }

    public boolean isValid() {

        if (model.lhs.length == 0) {
            return true;
        } else {
            return validateCompositeFactPatterns();
        }

    }

    private boolean validateCompositeFactPatterns() {
        for (int i = 0; i < model.lhs.length; i++) {
            IPattern iPattern = model.lhs[i];
            if (iPattern instanceof CompositeFactPattern) {
                if (!hasPatterns((CompositeFactPattern) iPattern)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasPatterns(CompositeFactPattern iPattern) {

        if (iPattern.getPatterns() == null) {
            setMandatoryFieldsError();
            return false;
        } else if (iPattern.getPatterns().length == 0) {
            setMandatoryFieldsError();
            return false;
        } else {
            return true;
        }
    }

    private void setMandatoryFieldsError() {
        errorMessage = constants.AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving();
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
