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
package org.kie.workbench.common.screens.explorer.client.widgets;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewWidget;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class BaseViewPresenterBranchManagementTest {

    @GwtMock
    CommonConstants commonConstants;

    @Mock
    private BusinessViewWidget view;

    @Mock
    private ActiveContextItems activeContextItems;

    @Mock
    private ActiveContextManager activeContextManager;

    @Mock
    private Explorer explorer;

    @InjectMocks
    private BaseViewPresenter presenter = new BaseViewPresenter( view ) {
        @Override
        protected boolean isViewVisible() {

            return true;
        }
    };

    @Mock
    private OrganizationalUnit organizationalUnit;
    @Mock
    private Repository         repository;

    @Before
    public void setup() {
        when( activeContextItems.getActiveOrganizationalUnit() ).thenReturn( organizationalUnit );
        when( activeContextItems.getActiveRepository() ).thenReturn( repository );
        when( view.getExplorer() ).thenReturn( explorer );
    }

    @Test
    public void testChangeBranch() throws Exception {

        when( activeContextItems.getActiveBranch() ).thenReturn( "master" );

        presenter.onBranchSelected( "dev" );

        verify( explorer ).clear();
        verify( activeContextManager ).initActiveContext( organizationalUnit,
                                                          repository,
                                                          "dev" );
    }

    @Test
    public void testStillInSameBranch() throws Exception {

        when( activeContextItems.getActiveBranch() ).thenReturn( "master" );

        presenter.onBranchSelected( "master" );

        verify( explorer, never() ).clear();
        verify( activeContextManager, never() ).initActiveContext( any( OrganizationalUnit.class ),
                                                                   any( Repository.class ),
                                                                   anyString() );
    }
}
