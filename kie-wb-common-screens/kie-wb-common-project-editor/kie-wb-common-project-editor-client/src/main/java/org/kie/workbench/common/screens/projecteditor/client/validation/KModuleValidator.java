package org.kie.workbench.common.screens.projecteditor.client.validation;

import org.guvnor.common.services.project.model.KBaseModel;
import org.guvnor.common.services.project.model.KModuleModel;

import java.util.Map;

public class KModuleValidator {

    public static boolean isValid(KModuleModel kModule) {

        if (!hasDefaultKBase(kModule.getKBases())) return false;

        return true;
    }

    private static boolean hasDefaultKBase(Map<String, KBaseModel> kBases) {
        if (kBases.isEmpty()) return true;

        boolean hasDefault = false;
        for (KBaseModel kbase : kBases.values()) {
            if (kbase.isDefault()) {
                hasDefault = true;
                break;
            }
        }

        return hasDefault;
    }

}
