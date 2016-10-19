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

package org.uberfire.ext.preferences.client.admin.page;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdminPageImplTest {

    private AdminPageImpl adminPage;

    @Before
    public void setup() {
        adminPage = new AdminPageImpl();
    }

    @Test
    public void addItemTest() {
        adminPage.addTool( "title1", "iconCss1", "category1", () -> {
        } );
        adminPage.addTool( "title2", "iconCss2", "category1", () -> {
        } );
        adminPage.addTool( "title3", "iconCss3", "category2", () -> {
        } );

        final Map<String, List<AdminTool>> toolsByCategory = adminPage.getToolsByCategory();

        assertNotNull( toolsByCategory );
        assertEquals( 2, toolsByCategory.size() );

        final List<AdminTool> category1Shortcuts = toolsByCategory.get( "category1" );
        assertEquals( 2, category1Shortcuts.size() );
        assertEquals( "title1", category1Shortcuts.get( 0 ).getTitle() );
        assertEquals( "iconCss1", category1Shortcuts.get( 0 ).getIconCss() );
        assertEquals( "title2", category1Shortcuts.get( 1 ).getTitle() );
        assertEquals( "iconCss2", category1Shortcuts.get( 1 ).getIconCss() );

        final List<AdminTool> category2Shortcuts = toolsByCategory.get( "category2" );
        assertEquals( 1, category2Shortcuts.size() );
        assertEquals( "title3", category2Shortcuts.get( 0 ).getTitle() );
        assertEquals( "iconCss3", category2Shortcuts.get( 0 ).getIconCss() );
    }

    @Test(expected = RuntimeException.class)
    public void addItemWithNullCategoryTest() {
        adminPage.addTool( "title", "iconCss", null, () -> {
        } );
    }
}
