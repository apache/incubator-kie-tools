package org.kie.workbench.common.screens.projecteditor.client.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.project.model.KBaseModel;
import org.guvnor.common.services.project.model.KModuleModel;
import org.guvnor.common.services.project.model.KSessionModel;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;

public class KModuleValidator {

    private final ProjectEditorConstants constants;

    private Set<String> errors = new HashSet<String>();

    public KModuleValidator(ProjectEditorConstants constants) {
        this.constants = constants;
    }

    public void validate(KModuleModel kModule) {

        if (kModule.getKBases().isEmpty()) {
            return;
        }

        hasDefaultKBase(kModule.getKBases());

        for (KBaseModel kBase : kModule.getKBases().values()) {
            hasOnlyOneOfEachDefaultKSession(kBase.getKSessions());
        }
    }

    private void hasOnlyOneOfEachDefaultKSession(List<KSessionModel> kSessions) {
        //TODO: -Rikkola-
    }

    private void hasDefaultKBase(Map<String, KBaseModel> kBases) {
        boolean hasDefault = false;
        for (KBaseModel kbase : kBases.values()) {
            if (kbase.isDefault()) {
                hasDefault = true;
                break;
            }
        }

        if (!hasDefault) {
            errors.add(constants.AKModuleMustHaveAtLeastOneDefaultKBasePleaseAddOne());
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public String getErrorsString() {
        String list = "";
        for (String line : errors) {
            list += line + "\n";
        }
        return list;
    }
}
