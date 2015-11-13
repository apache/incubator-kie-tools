/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.workbench.panels.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MultiListWorkbenchPanelViewTest extends AbstractDockingWorkbenchPanelViewTest {

    @Mock ListBarWidget listBar;
    @Mock MaximizeToggleButtonPresenter maximizeButton;
    @Mock MultiListWorkbenchPanelPresenter presenter;

    @InjectMocks MultiListWorkbenchPanelView view;

    @Override
    protected AbstractDockingWorkbenchPanelView<?> getViewToTest() {
        return view;
    }

    @Before
    public void setup() {
        when( listBar.getMaximizeButton() ).thenReturn( maximizeButton );

        final AtomicLong parts = new AtomicLong();
        doAnswer( new Answer() {
            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable {
                parts.incrementAndGet();
                return null;
            }
        } ).when( listBar ).addPart( any( WorkbenchPartPresenter.View.class ) );

        when( listBar.getPartsSize() ).thenAnswer( new Answer<Integer>() {
            @Override
            public Integer answer( InvocationOnMock invocation ) throws Throwable {
                return parts.intValue();
            }
        } );
    }

    @Test
    public void setupWidget() {
        view.setupWidget();

        verify( listBar ).addSelectionHandler( any(SelectionHandler.class) );
        verify( listBar ).addOnFocusHandler( any(Command.class) );

        verify( maximizeButton ).setVisible( true );
        verify( maximizeButton ).setMaximizeCommand( any( Command.class ) );
        verify( maximizeButton ).setUnmaximizeCommand( any( Command.class ) );
    }

    @Test
    public void shouldPropagateOnResize() {
        view.onResize();
        RequiresResize viewChild = (RequiresResize) view.getWidget();
        verify( viewChild, times( 1 ) ).onResize();
    }

    @Test
    public void shouldAddMultipleParts() {
        assertEquals( 0, listBar.getPartsSize() );
        verify( listBar, never() ).disableClosePart();

        //Add multiple parts
        view.addPart( mock( WorkbenchPartPresenter.View.class ) );
        verify( listBar ).addPart( any( WorkbenchPartPresenter.View.class ) );

        view.addPart( mock( WorkbenchPartPresenter.View.class ) );
        verify( listBar, times( 2 ) ).addPart( any( WorkbenchPartPresenter.View.class ) );

        assertEquals( 2, listBar.getPartsSize() );
    }
}