/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.explorer.client.widgets.branches;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveContextItems;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class BranchSelectorTest {

    @Mock
    private BranchSelectorView view;

    @GwtMock
    private Widget widget;

    private BranchSelector branchSelector;
    private ActiveContextItems activeContextItems;

    @Before
    public void setUp() throws Exception {
        activeContextItems = new ActiveContextItems();
        branchSelector = new BranchSelector( view,
                                             activeContextItems );
    }

    @Test
    public void testSetNullRepository() throws Exception {
        branchSelector.setRepository( null );

        verify( view ).clear();

        verify( view ).hide();
    }

    @Test
    public void testSetEmptyRepository() throws Exception {
        branchSelector.setRepository( mock( Repository.class ) );

        verify( view ).clear();

        verify( view ).hide();
    }

    @Test
    public void testSetRepository() throws Exception {
        Repository repository = mock( Repository.class );
        ArrayList<String> branches = new ArrayList<String>();
        branches.add( "master" );

        when( repository.getBranches() ).thenReturn( branches );
        activeContextItems.setActiveBranch( "master" );

        branchSelector.setRepository( mock( Repository.class ) );

        verify( view ).clear();
        verify( view, never() ).addBranch( anyString() );
        verify( view ).hide();
    }

    @Test
    public void testShowSelector() throws Exception {
        Repository repository = mock( Repository.class );
        ArrayList<String> branches = new ArrayList<String>();
        branches.add( "master" );
        branches.add( "feature1" );

        when( repository.getBranches() ).thenReturn( branches );
        activeContextItems.setActiveBranch( "master" );

        branchSelector.setRepository( repository );

        verify( view ).clear();

        verify( view ).setCurrentBranch( "master" );
        verify( view, never() ).addBranch( "master" );
        verify( view ).addBranch( "feature1" );

        verify( view ).show();
    }

    @Test
    public void testBranchSelectedNoHandler() throws Exception {
        verify( view ).setPresenter( branchSelector );
        verifyNoMoreInteractions( view );
        branchSelector.onBranchSelected( "something" );
    }

    @Test
    public void testChangeBranch() throws Exception {
        BranchChangeHandler branchChangeHandler = mock( BranchChangeHandler.class );
        branchSelector.addBranchChangeHandler( branchChangeHandler );

        branchSelector.onBranchSelected( "some branch" );

        verify( branchChangeHandler ).onBranchSelected( "some branch" );
    }

    @Test
    public void testGetView() throws Exception {
        when( view.asWidget() ).thenReturn( widget );

        assertEquals( widget, branchSelector.asWidget() );

    }
}