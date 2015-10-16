/*
 *
 *  * Copyright 2015 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.workbench.panels.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.workbench.model.PartDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractSimpleWorkbenchPanelViewTest extends AbstractDockingWorkbenchPanelViewTest {

    @Mock
    ListBarWidget listBar;

    @Mock
    Element listBarElement;

    @Mock
    Style listBarElementStyle;

    @Mock
    MaximizeToggleButtonPresenter maximizeButton;

    @Before
    public void setup() {
        Widget listBarWidget = mock( Widget.class );
        when( listBar.asWidget() ).thenReturn( listBarWidget );
        when( listBarWidget.getElement() ).thenReturn( listBarElement );
        when( listBarElement.getStyle() ).thenReturn( listBarElementStyle );
        when( listBar.getMaximizeButton() ).thenReturn( maximizeButton );

        final AtomicLong parts = new AtomicLong();
        doAnswer( new Answer() {
            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable {
                parts.incrementAndGet();
                return null;
            }
        } ).when( listBar ).addPart( any( WorkbenchPartPresenter.View.class ) );

        doAnswer( new Answer() {
            @Override
            public Boolean answer( InvocationOnMock invocation ) throws Throwable {
                parts.decrementAndGet();
                return true;
            }
        } ).when( listBar ).remove( any( PartDefinition.class ) );

        when( listBar.getPartsSize() ).thenAnswer( new Answer<Integer>() {
            @Override
            public Integer answer( InvocationOnMock invocation ) throws Throwable {
                return parts.intValue();
            }
        } );

    }

    @Test
    public void shouldOnlyHaveOnePart() {
        assertEquals( 0, listBar.getPartsSize() );

        //Add first part
        getViewToTest().addPart( mock( WorkbenchPartPresenter.View.class ) );
        verify( listBar ).addPart( any( WorkbenchPartPresenter.View.class ) );
        assertEquals( 1, listBar.getPartsSize() );

        //Second part will be ignored
        getViewToTest().addPart( mock( WorkbenchPartPresenter.View.class ) );
        verify( listBar ).addPart( any( WorkbenchPartPresenter.View.class ) );
        assertEquals( 1, listBar.getPartsSize() );

        //Remove part
        getViewToTest().removePart( mock( PartDefinition.class ) );
        assertEquals( 0, listBar.getPartsSize() );

        //Add part again
        getViewToTest().addPart( mock( WorkbenchPartPresenter.View.class ) );
        verify( listBar, times( 2 ) ).addPart( any( WorkbenchPartPresenter.View.class ) );
        assertEquals( 1, listBar.getPartsSize() );
    }

}