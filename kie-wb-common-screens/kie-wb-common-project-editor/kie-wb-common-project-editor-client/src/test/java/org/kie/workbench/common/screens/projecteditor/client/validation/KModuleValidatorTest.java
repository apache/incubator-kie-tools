package org.kie.workbench.common.screens.projecteditor.client.validation;

import org.guvnor.common.services.project.model.KBaseModel;
import org.guvnor.common.services.project.model.KModuleModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KModuleValidatorTest {

    private KModuleModel kModule;

    @Before
    public void setUp() throws Exception {
        kModule = new KModuleModel();
    }

    @Test
    public void testValidateEmptyKModule() throws Exception {
        assertTrue(KModuleValidator.isValid(kModule));
    }

    @Test
    public void testValidateWorkingKModule() throws Exception {
        KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setDefault(true);
        kModule.getKBases().put("test", kBaseModel);

        assertTrue(KModuleValidator.isValid(kModule));
    }

    @Test
    public void testValidateFailingKModule() throws Exception {
        KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setDefault(false);
        kModule.getKBases().put("test", kBaseModel);

        assertFalse(KModuleValidator.isValid(kModule));
    }
}
