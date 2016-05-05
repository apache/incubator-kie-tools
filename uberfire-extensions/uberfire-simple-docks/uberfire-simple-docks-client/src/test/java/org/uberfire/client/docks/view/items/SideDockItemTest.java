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

package org.uberfire.client.docks.view.items;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SideDockItemTest {

    @Mock
    private ImageResource imageResource, imageResourceFocused;

    @Mock
    private PlaceRequest placeRequest;

    private ParameterizedCommand<String> emptyCommand;

    private UberfireDock dockWithFontIcon, dockWithImageIcon;

    private SideDockItem sideDockWithFontIcon, sideDockWithImageIcon;


    @Before
    public void setup() {
        dockWithFontIcon = new UberfireDock( UberfireDockPosition.EAST, "BRIEFCASE", placeRequest, "" ).withSize( 450 ).withLabel( "dock" );
        dockWithImageIcon = new UberfireDock( UberfireDockPosition.EAST, imageResource, imageResourceFocused, placeRequest, "" ).withSize( 450 ).withLabel( "dock" );

        sideDockWithFontIcon = spy( new SideDockItem( dockWithFontIcon, emptyCommand, emptyCommand ) );
        sideDockWithImageIcon = spy( new SideDockItem( dockWithImageIcon, emptyCommand, emptyCommand ) );

        emptyCommand = new ParameterizedCommand<String>() {
            @Override
            public void execute( final String parameter ) {

            }
        };
    }

    @Test
    public void createSideDockItemWithFontIconTest() {
        sideDockWithFontIcon.createButton();

        verify( sideDockWithFontIcon ).configureIcon( any( Button.class ), eq( (ImageResource) null ) );
        verify( sideDockWithFontIcon, never() ).configureImageIcon( any( Button.class ), any( ImageResource.class ) );
    }

    @Test
    public void createSideDockItemFocusedWithFontIconTest() {
        sideDockWithFontIcon.getPopup().createButton( sideDockWithFontIcon );

        verify( sideDockWithFontIcon ).configureIcon( any( Button.class ), eq( (ImageResource) null ) );
        verify( sideDockWithFontIcon, never() ).configureImageIcon( any( Button.class ), any( ImageResource.class ) );
    }

    @Test
    public void selectSideDockItemWithFontIconTest() {
        sideDockWithFontIcon.select();

        verify( sideDockWithFontIcon, never() ).configureImageIcon( any( Button.class ), any( ImageResource.class ) );
    }

    @Test
    public void deselectSideDockItemWithFontIconTest() {
        sideDockWithFontIcon.deselect();

        verify( sideDockWithFontIcon, never() ).configureImageIcon( any( Button.class ), any( ImageResource.class ) );
    }

    @Test
    public void createSideDockItemWithImageIconTest() {
        sideDockWithImageIcon.createButton();

        verify( sideDockWithImageIcon ).configureIcon( any( Button.class ), eq( imageResource ) );
        verify( sideDockWithImageIcon ).configureImageIcon( any( Button.class ), eq( imageResource ) );
    }

    @Test
    public void createSideDockItemFocusedWithImageIconTest() {
        sideDockWithImageIcon.getPopup().createButton( sideDockWithImageIcon );

        verify( sideDockWithImageIcon ).configureIcon( any( Button.class ), eq( imageResourceFocused ) );
        verify( sideDockWithImageIcon ).configureImageIcon( any( Button.class ), eq( imageResourceFocused ) );
    }

    @Test
    public void selectSouthDockItemWithImageIconTest() {
        sideDockWithImageIcon.select();

        verify( sideDockWithImageIcon ).configureImageIcon( any( Button.class ), eq( imageResourceFocused ) );
    }

    @Test
    public void deselectSouthDockItemWithImageIconTest() {
        sideDockWithImageIcon.deselect();

        verify( sideDockWithImageIcon ).configureImageIcon( any( Button.class ), eq( imageResource ) );
    }
}
