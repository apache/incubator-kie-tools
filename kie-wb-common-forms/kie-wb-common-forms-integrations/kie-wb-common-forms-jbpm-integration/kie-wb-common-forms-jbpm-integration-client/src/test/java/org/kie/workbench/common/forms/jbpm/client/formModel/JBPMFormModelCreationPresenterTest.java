/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.client.formModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.jbpm.client.resources.i18n.Constants;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinesProcessVariable;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskVariable;
import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class JBPMFormModelCreationPresenterTest {

    private BPMFinderService finderService;

    private CallerMock<BPMFinderService> finderServiceCallerMock;

    private JBPMFormModelCreationView view;

    private Path path;

    private JBPMFormModelCreationPresenter presenter;

    private List<JBPMProcessModel> formModels = new ArrayList<>(  );

    private TranslationService translationService;

    @Before
    public void setup() {

        initFormModels();

        path = mock( Path.class );

        finderService = mock( BPMFinderService.class );

        when( finderService.getAvailableProcessModels( path ) ).thenReturn( formModels );

        finderServiceCallerMock = new CallerMock<>( finderService );

        view = mock( JBPMFormModelCreationView.class );

        translationService = mock( TranslationService.class );

        presenter = new JBPMFormModelCreationPresenter( finderServiceCallerMock, view, translationService );
    }

    @Test
    public void testGeneralFunctionallity() {
        presenter.getPriority();

        presenter.reset();
        verify( view ).reset();

        presenter.asWidget();

        verify( view ).asWidget();

        presenter.init( path );

        presenter.getLabel();
        translationService.getTranslation( Constants.Process );

        verify( finderService ).getAvailableProcessModels( path );
        verify( view ).setProcessModels( formModels );

        presenter.isValid();
        verify( view ).isValid();

        presenter.getFormModel();
        verify( view ).getSelectedFormModel();
    }

    protected void initFormModels() {
        List<BusinesProcessVariable> processVariables = new ArrayList<>();

        BusinesProcessVariable variable = new BusinesProcessVariable( "name", String.class.getName() );
        processVariables.add( variable );
        variable = new BusinesProcessVariable( "age", Integer.class.getName() );
        processVariables.add( variable );
        variable = new BusinesProcessVariable( "twitter", String.class.getName() );
        processVariables.add( variable );
        variable = new BusinesProcessVariable( "offering", Integer.class.getName() );
        processVariables.add( variable );
        variable = new BusinesProcessVariable( "skills", String.class.getName() );
        processVariables.add( variable );
        variable = new BusinesProcessVariable( "mail", String.class.getName() );
        processVariables.add( variable );
        variable = new BusinesProcessVariable( "hr_score", Integer.class.getName() );
        processVariables.add( variable );
        variable = new BusinesProcessVariable( "tech_score", Integer.class.getName() );
        processVariables.add( variable );
        variable = new BusinesProcessVariable( "signed", Boolean.class.getName() );
        processVariables.add( variable );

        BusinessProcessFormModel processFormModel = new BusinessProcessFormModel( "hiring", "hiring", processVariables );

        TaskFormModel taskFormModel;
        TaskVariable taskVariable;
        List<TaskFormModel> processTasks = new ArrayList<>();
        List<TaskVariable> taskVariables = new ArrayList<>();

        taskVariable = new TaskVariable( "name", String.class.getName(), "in_name", null );
        taskVariables.add( taskVariable );
        taskVariable = new TaskVariable( "age", Integer.class.getName(), null, "out_age" );
        taskVariables.add( taskVariable );
        taskVariable = new TaskVariable( "mail", String.class.getName(), null, "out_mail" );
        taskVariables.add( taskVariable );
        taskVariable = new TaskVariable( "hr_score", Integer.class.getName(), null, "out_score" );
        taskVariables.add( taskVariable );

        taskFormModel = new TaskFormModel( "hiring", "task", "HR Interview", taskVariables );
        processTasks.add( taskFormModel );


        taskVariables = new ArrayList<>();
        taskVariable = new TaskVariable( "name", String.class.getName(), "in_name", null );
        taskVariables.add( taskVariable );
        taskVariable = new TaskVariable( "age", Integer.class.getName(), "in_age", null );
        taskVariables.add( taskVariable );
        taskVariable = new TaskVariable( "mail", String.class.getName(), "in_mail", null );
        taskVariables.add( taskVariable );
        taskVariable = new TaskVariable( "skills", String.class.getName(), null, "out_skills" );
        taskVariables.add( taskVariable );
        taskVariable = new TaskVariable( "tech_score", Integer.class.getName(), null, "out_score" );
        taskVariables.add( taskVariable );
        taskVariable = new TaskVariable( "twitter", Integer.class.getName(), null, "out_twitter" );
        taskVariables.add( taskVariable );

        taskFormModel = new TaskFormModel( "hiring", "task", "Tech Interview", taskVariables );
        processTasks.add( taskFormModel );

        JBPMProcessModel model = new JBPMProcessModel( processFormModel, processTasks );

        formModels.add( model );
    }

}
