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

package org.kie.workbench.common.forms.data.modeller.client.formModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.client.resources.i18n.DataModellerIntegrationConstants;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DataObjectFormModelCreationPresenterTest {

    private DataObjectFinderService finderService;

    private CallerMock<DataObjectFinderService> finderServiceCallerMock;

    private DataObjectFormModelCreationView view;

    private Path path;

    private DataObjectFormModelCreationPresenter presenter;

    private List<DataObjectFormModel> formModels = new ArrayList<>(  );

    private TranslationService translationService;

    @Before
    public void setup() {

        path = mock( Path.class );

        formModels.add( new DataObjectFormModel( "employee", "org.kie.wb.test.Employee" ) );
        formModels.add( new DataObjectFormModel( "address", "org.kie.wb.test.Address" ) );
        formModels.add( new DataObjectFormModel( "company", "org.kie.wb.test.Company" ) );
        formModels.add( new DataObjectFormModel( "department", "org.kie.wb.test.Department" ) );

        finderService = mock( DataObjectFinderService.class );

        when( finderService.getAvailableDataObjects( path ) ).thenReturn( formModels );

        finderServiceCallerMock = new CallerMock<>( finderService );

        view = mock( DataObjectFormModelCreationView.class );

        translationService = mock( TranslationService.class );

        presenter = new DataObjectFormModelCreationPresenter( finderServiceCallerMock, view, translationService );
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
        translationService.getTranslation( DataModellerIntegrationConstants.DataObject );

        verify( finderService ).getAvailableDataObjects( path );
        verify( view ).setFormModels( formModels );

        presenter.isValid();
        verify( view ).isValid();

        presenter.getFormModel();
        verify( view ).getSelectedFormModel();
    }

}
