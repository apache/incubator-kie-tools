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

package org.uberfire.ext.plugin.client.editor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.plugin.client.validation.RuleValidator;
import org.uberfire.ext.plugin.model.DynamicMenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DynamicMenuEditorPresenterTest {

    private DynamicMenuEditorPresenter presenter;
    private DynamicMenuEditorPresenter.View view;

    private DynamicMenuItem existingMenuItem;

    @Before
    public void setup() {
        view = mock( DynamicMenuEditorPresenter.View.class );
        presenter = createDynamicMenuEditorPresenter();

        when( view.emptyActivityID() ).thenReturn( "e1" );
        when( view.invalidActivityID() ).thenReturn( "e2" );
        when( view.emptyMenuLabel() ).thenReturn( "e3" );
        when( view.invalidMenuLabel() ).thenReturn( "e4" );
        when( view.duplicatedMenuLabel() ).thenReturn( "e5" );

        existingMenuItem = new DynamicMenuItem( "existingActivityId", "existingMenuLabel" );
    }

    @Test
    public void validateMenuItemActivityId() {
        RuleValidator activityIdValidator = presenter.getMenuItemActivityIdValidator();

        assertFalse( activityIdValidator.isValid( null ) );
        assertFalse( activityIdValidator.isValid( "" ) );
        assertTrue( activityIdValidator.isValid( "existingActivityId" ) );
        assertTrue( activityIdValidator.isValid( "newActivityId" ) );
    }

    @Test
    public void validateNewMenuItemLabel() {
        RuleValidator labelValidator;

        labelValidator = presenter.getMenuItemLabelValidator( new DynamicMenuItem( null, null ), null );
        assertFalse( labelValidator.isValid( null ) );

        labelValidator = presenter.getMenuItemLabelValidator( new DynamicMenuItem( "", "" ), null );
        assertFalse( labelValidator.isValid( "" ) );

        labelValidator = presenter.getMenuItemLabelValidator( new DynamicMenuItem( "existingActivityId", "existingMenuLabel" ), null );
        assertFalse( labelValidator.isValid( "existingMenuLabel" ) );

        labelValidator = presenter.getMenuItemLabelValidator( new DynamicMenuItem( "newActivityId", "newMenuLabel" ), null );
        assertTrue( labelValidator.isValid( "newMenuLabel" ) );
    }

    @Test
    public void validateEditedMenuItemLabel() {
        RuleValidator labelValidator = presenter.getMenuItemLabelValidator( new DynamicMenuItem( "existingActivityId", "existingMenuLabel" ), existingMenuItem );
        assertTrue( labelValidator.isValid( "newMenuLabel" ) );
        assertTrue( labelValidator.isValid( "existingMenuLabel" ) );
    }

    private DynamicMenuEditorPresenter createDynamicMenuEditorPresenter() {
        return new DynamicMenuEditorPresenter( view ) {
            @Override
            public List<DynamicMenuItem> getDynamicMenuItems() {
                List<DynamicMenuItem> dynamicMenuItems = new ArrayList<DynamicMenuItem>();
                dynamicMenuItems.add( existingMenuItem );

                return dynamicMenuItems;
            }
        };
    }
}
