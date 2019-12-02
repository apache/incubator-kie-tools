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

package org.kie.workbench.common.screens.server.management.client.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ Anchor.class })
public class CustomGroupItemTest {

    public Set<String> styles = new HashSet<String>();
    public List<String> text = new ArrayList<String>();
    public List<IconType> icon = new ArrayList<IconType>();
    public ClickHandler clickHandler = null;
    final Command command = mock( Command.class );

    CustomGroupItem customGroupItem;

    @Before
    public void setup() throws Exception {
        styles.clear();
        text.clear();
        clickHandler = null;
        customGroupItem = new CustomGroupItem( "item", IconType.ADJUST, command ) {

            public void addStyleName( final String style ) {
                styles.add( style );
            }

            public void removeStyleName( final String style ) {
                styles.remove( style );
            }

            public String getStyleName() {
                return styles.toString();
            }

            public void setText( final String value ) {
                text.add( value );
            }

            public String getText() {
                return text.get( 0 );
            }

            public IconType getIcon() {
                return icon.get( 0 );
            }

            public void setIcon(IconType icontype) {
                icon.add( icontype );
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
        assertTrue( customGroupItem.getStyleName().contains( Styles.LIST_GROUP_ITEM ) );
        assertTrue( customGroupItem.getText().equals( "item" ) );
        assertTrue( customGroupItem.getIcon().equals( IconType.ADJUST ) );
        assertNotNull( clickHandler );
    }

    @Test
    public void testActive() {
        customGroupItem.setActive( true );
        assertTrue( customGroupItem.getStyleName().contains( "active" ) );

        customGroupItem.setActive( false );
        assertTrue( !customGroupItem.getStyleName().contains( "active" ) );
    }

    @Test
    public void testClick() {
        clickHandler.onClick( mock( ClickEvent.class ) );
        verify( command ).execute();
    }

}