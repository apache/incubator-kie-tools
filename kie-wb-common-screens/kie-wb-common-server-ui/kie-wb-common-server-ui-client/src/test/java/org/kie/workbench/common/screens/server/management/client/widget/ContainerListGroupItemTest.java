/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ LinkedGroupItem.class })
public class ContainerListGroupItemTest {

    public List<String> text = new ArrayList<String>();
    public ClickHandler clickHandler = null;
    public ParameterizedCommand<String> command = mock( ParameterizedCommand.class );

    ContainerListGroupItem customGroupItem;
    private Widget child = null;
    private int beforeIndex = -1;

    @Before
    public void setup() throws Exception {
        text.clear();
        child = null;
        beforeIndex = -1;
        customGroupItem = new ContainerListGroupItem( "item", command ) {

            public void setText( final String value ) {
                text.add( value );
            }

            public String getText() {
                return text.get( 0 );
            }

            public void insert( final Widget _child,
                                final int _beforeIndex ) {
                child = _child;
                beforeIndex = _beforeIndex;
            }

            public HandlerRegistration addClickHandler( final ClickHandler handler ) {
                clickHandler = handler;
                return new HandlerRegistration() {
                    @Override
                    public void removeHandler() {

                    }
                };
            }
        };
    }

    @Test
    public void test() {
        assertTrue( customGroupItem.getText().equals( "item" ) );
        assertTrue( beforeIndex == 0 );
        assertNotNull( child );
        assertNotNull( clickHandler );
    }

    @Test
    public void testClick() {
        clickHandler.onClick( mock( ClickEvent.class ) );
        verify( command ).execute( "item" );
    }

}