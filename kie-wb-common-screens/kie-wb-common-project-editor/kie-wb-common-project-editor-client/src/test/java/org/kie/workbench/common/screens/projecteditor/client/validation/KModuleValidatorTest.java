package org.kie.workbench.common.screens.projecteditor.client.validation;

import org.guvnor.common.services.project.model.KBaseModel;
import org.guvnor.common.services.project.model.KModuleModel;
import org.guvnor.common.services.project.model.KSessionModel;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KModuleValidatorTest {

    private KModuleModel kModule;
    private KModuleValidator validator;
    private ProjectEditorConstants constants;

    @Before
    public void setUp() throws Exception {
        constants = mock(ProjectEditorConstants.class);
        validator = new KModuleValidator(constants);
        kModule = new KModuleModel();
    }

    @Test
    public void testValidateEmptyKModule() throws Exception {
        validator.validate(kModule);
        assertFalse(validator.hasErrors());
    }

    @Test
    public void testValidateWorkingKModule() throws Exception {
        KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setDefault(true);
        kModule.getKBases().put("test", kBaseModel);

        validator.validate(kModule);

        assertFalse(validator.hasErrors());
    }

    @Test
    public void testValidateFailingKModule() throws Exception {
        KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setDefault(false);
        kModule.getKBases().put("test", kBaseModel);

        validator.validate(kModule);

        assertTrue(validator.hasErrors());
        verify(constants).AKModuleMustHaveAtLeastOneDefaultKBasePleaseAddOne();
    }

    @Test
    public void testHasOnlyOneDefaultStatelessKSession() throws Exception {
        KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setDefault(true);
        kModule.getKBases().put("tets", kBaseModel);

        KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setDefault(true);
        kBaseModel.getKSessions().add(kSessionModel);

        assertFalse(validator.hasErrors());
    }
}
