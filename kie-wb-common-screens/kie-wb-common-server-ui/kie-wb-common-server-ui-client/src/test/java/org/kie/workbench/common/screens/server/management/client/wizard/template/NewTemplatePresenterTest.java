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

package org.kie.workbench.common.screens.server.management.client.wizard.template;

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.util.ContentChangeHandler;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewTemplatePresenterTest {

    Caller<SpecManagementService> specManagementServiceCaller;

    @Mock
    SpecManagementService specManagementService;

    @Spy
    Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent = new EventSourceMock<WizardPageStatusChangeEvent>();

    @Mock
    NewTemplatePresenter.View view;

    NewTemplatePresenter presenter;

    @Before
    public void init() {
        doNothing().when( wizardPageStatusChangeEvent ).fire( any( WizardPageStatusChangeEvent.class ) );
        specManagementServiceCaller = new CallerMock<SpecManagementService>( specManagementService );
        presenter = spy( new NewTemplatePresenter( view, specManagementServiceCaller, wizardPageStatusChangeEvent ) );
    }

    @Test
    public void testInit() {
        presenter.init();

        verify( view ).init( presenter );
        assertEquals( view.asWidget(), presenter.asWidget() );
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testIsTemplateNameValid() {
        when( view.getTemplateName() )
                .thenReturn( null )
                .thenReturn( "" )
                .thenReturn( "test" );

        assertFalse( presenter.isTemplateNameValid() );
        assertFalse( presenter.isTemplateNameValid() );
        assertTrue( presenter.isTemplateNameValid() );
    }

    @Test
    public void testIsCapabilityValid() {
        when( view.isPlanningCapabilityChecked() ).thenReturn( true, false, true, false );
        when( view.isRuleCapabilityChecked() ).thenReturn( true, false, false );
        when( view.isProcessCapabilityChecked() ).thenReturn( true, true, false );

        assertTrue( presenter.isCapabilityValid() );

        assertTrue( presenter.isCapabilityValid() );

        assertTrue( presenter.isCapabilityValid() );

        assertFalse( presenter.isCapabilityValid() );
    }

    @Test
    public void testIsValid() {
        when( view.getTemplateName() ).thenReturn( "templateName", "", "templateName" );
        when( view.isRuleCapabilityChecked() ).thenReturn( true, false );
        when( view.isProcessCapabilityChecked() ).thenReturn( true, false );
        when( view.isPlanningCapabilityChecked() ).thenReturn( false );

        assertTrue( presenter.isValid() );

        verify( view ).noErrorOnTemplateName();
        verify( view ).noErrorOnCapability();

        assertFalse( presenter.isValid() );
        verify( view ).errorOnTemplateName();
        verify( view, times( 2 ) ).noErrorOnCapability();

        assertFalse( presenter.isValid() );
        verify( view, times( 2 ) ).noErrorOnTemplateName();
        verify( view ).errorCapability();
    }

    @Test
    public void testIsComplete() {
        final Callback callback = mock( Callback.class );
        final String templateName = "templateName";
        when( view.getTemplateName() ).thenReturn( templateName ).thenReturn( templateName, templateName, templateName, "" );
        when( view.isRuleCapabilityChecked() ).thenReturn( true );
        when( view.isProcessCapabilityChecked() ).thenReturn( true );
        when( specManagementService.isNewServerTemplateIdValid( templateName ) ).thenReturn( true, false );

        presenter.isComplete( callback );

        verify( specManagementService ).isNewServerTemplateIdValid( templateName );
        verify( callback ).callback( true );

        presenter.isComplete( callback );

        verify( specManagementService, times( 2 ) ).isNewServerTemplateIdValid( templateName );
        verify( callback ).callback( false );

        presenter.isComplete( callback );

        verify( callback, times( 2 ) ).callback( false );
        verify( view ).errorOnTemplateName();
    }

    @Test
    public void testClear() {
        presenter.clear();

        verify( view ).clear();
    }

    @Test
    public void testTitle() {
        final String title = "title";
        when( view.getTitle() ).thenReturn( title );

        assertEquals( title, presenter.getTitle() );
        verify( view ).getTitle();
    }

    @Test
    public void testAddContentChangeHandler() {
        doAnswer( new Answer() {
            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable {
                final ContentChangeHandler handler = (ContentChangeHandler) invocation.getArguments()[ 0 ];
                if ( handler != null ) {
                    handler.onContentChange();
                }
                return null;
            }
        } ).when( view ).addContentChangeHandler( any( ContentChangeHandler.class ) );

        presenter.addContentChangeHandler( mock( ContentChangeHandler.class ) );

        final ArgumentCaptor<WizardPageStatusChangeEvent> eventCaptor = ArgumentCaptor.forClass( WizardPageStatusChangeEvent.class );
        verify( wizardPageStatusChangeEvent ).fire( eventCaptor.capture() );
        assertEquals( presenter, eventCaptor.getValue().getPage() );
    }

    @Test
    public void testIsRuleCapabilityChecked() {
        when( view.isRuleCapabilityChecked() ).thenReturn( true ).thenReturn( false );

        assertTrue( presenter.isRuleCapabilityChecked() );
        assertFalse( presenter.isRuleCapabilityChecked() );
    }

    @Test
    public void testIsProcessCapabilityChecked() {
        when( view.isProcessCapabilityChecked() ).thenReturn( true ).thenReturn( false );

        assertTrue( presenter.isProcessCapabilityChecked() );
        assertFalse( presenter.isProcessCapabilityChecked() );
    }

    @Test
    public void testIsPlanningCapabilityChecked() {
        when( view.isPlanningCapabilityChecked() ).thenReturn( true ).thenReturn( false );

        assertTrue( presenter.isPlanningCapabilityChecked() );
        assertFalse( presenter.isPlanningCapabilityChecked() );
    }

    @Test
    public void testHasProcessCapability() {
        when( view.getProcessCapabilityCheck() ).thenReturn( true, false );

        assertTrue( presenter.hasProcessCapability() );
        assertFalse( presenter.hasProcessCapability() );
        verify( view, times( 2 ) ).getProcessCapabilityCheck();
    }

    @Test
    public void testTemplateName() {
        final String templateName = "templateName";
        when( view.getTemplateName() ).thenReturn( templateName );

        assertEquals( templateName, presenter.getTemplateName() );
        verify( view ).getTemplateName();
    }

}